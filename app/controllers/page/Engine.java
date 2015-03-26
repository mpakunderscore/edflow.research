package controllers.page;

import com.avaje.ebean.Ebean;
import controllers.Watcher;
import controllers.page.utils.ValueComparator;
import models.Category;
import models.Token;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.Logger;

import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static play.libs.Json.fromJson;
import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 20/03/15.
 */
public class Engine {

    private static final String tokenUrl = ".wikipedia.org/wiki/";
    private static final String categoryUrl = ".wikipedia.org/wiki/Category:";

    private final static Pattern wordPattern = Pattern.compile("[^\\s+\"\\d+(){}, –'\\-=_@:$;#%!<>&\\|\\*\\?\\[\\]\\.\\/\\+\\\\]{2,}");

    private final static boolean bigrams = false;

    public static Token getToken(String word) {

        Token token;

        token = Ebean.find(Token.class).where().idEq(word).findUnique();

        if (token != null)
            return token;

        Document doc = null;

        try {

            String lang = LangDetect.detect(word);

            Logger.debug("[token new] " + word + " [" + lang + "]");

            String connectUrl = "http://" + lang + tokenUrl

                    + (!lang.equals("en") ? URLEncoder.encode(word, "UTF-8") : word);

            Connection connection = Jsoup.connect(connectUrl);

            doc = connection.userAgent(Watcher.USER_AGENT).followRedirects(true).get();

        } catch (Exception exception) { //TODO

            token = new Token(word, null, null, false);
            token.save();

            return token;
        }

        String redirect_name = null;

        String name = doc.body().getElementById("firstHeading").text().toLowerCase();

        if (!name.equals(word)) {

            redirect_name = name;
        }

        Elements links = doc.body().select("#mw-normal-catlinks ul a");

        List<String> categories = new ArrayList<>();

        for (Element link : links) {

            String category = link.text().toLowerCase();
            categories.add(category);
        }

        token = new Token(word, redirect_name, String.valueOf(toJson(categories)), true);
        token.save();

        return token;
    }

    /**
     *
     * Load any text, get map of sorted unique words (with bigrams)
     *
     * @param text - any text
     * @return Map of sorted words with weight
     */

    public static Map<String, Integer> getWords(String text) {

        Map<String, Integer> resultWords = new HashMap<>();
        Map<String, Integer> words = new HashMap<>();
        List<String> wordsList = new ArrayList<>();

        ValueComparator bvc =  new ValueComparator(resultWords);
        Map<String, Integer> sortedWords  = new TreeMap<>(bvc);

        Matcher matcher = wordPattern.matcher(text);
        while (matcher.find()) {

            String word = matcher.group().toLowerCase();

            if (words.containsKey(word)) words.put(word, words.get(word) + 1);
            else words.put(word, 1);

            wordsList.add(word);
        }

        if (bigrams)
            setBigrams(wordsList, words);

        for (Map.Entry<String, Integer> word : words.entrySet()) {

            if (word.getValue() > 2) //TODO
                resultWords.put(word.getKey(), word.getValue());
        }

        sortedWords.putAll(resultWords);

        return sortedWords;
    }

    /**
     *
     * Catch bigrams (and n-grams maybe) like "project management", unsorted
     *
     * @param wordsList - ["put", "your", "text", "like", "a", "list", "of", "words"]
     * @return - Map of bigrams (unsorted)
     */

    private static void setBigrams(List<String> wordsList, Map<String, Integer> words) {

        Map<String, Integer> bigrams = new HashMap<String, Integer>();

        for (int i = 0; i < wordsList.size() - 1; i++) {

            String bigram = wordsList.get(i) + " " + wordsList.get(i + 1);

            if (bigrams.containsKey(bigram)) bigrams.put(bigram, bigrams.get(bigram) + 1);
            else bigrams.put(bigram, 1);
        }

        words.putAll(bigrams);
    }

    public static Map<String, Integer> getTokensMap(Map<String, Integer> wordsMap) {

        Map<String, Integer> tags = new HashMap<>();

        for (Map.Entry<String, Integer> word : wordsMap.entrySet()) {

            Token token = getToken(word.getKey());

            if (token.isMark()) {

                Logger.debug("[token ok] " + word.getKey() + ": " + word.getValue() + " " + token.getCategories() + (token.getRedirect() == null ? "" : " " + token.getRedirect()));

                if (token.getRedirect() != null) {

                    Token redirect = getToken(token.getRedirect());

                    if (redirect != null && !redirect.isMark())
                        continue;

                    if (tags.containsKey(token.getRedirect())) {

                        int old = tags.get(token.getRedirect());
                        tags.put(token.getRedirect(), old + word.getValue());

                    } else
                        tags.put(token.getRedirect(), word.getValue());

                } else
                    tags.put(word.getKey(), word.getValue());

            } else
                Logger.debug("[token not mark] " + word.getKey() + ": " + word.getValue());
        }

        if (bigrams)
            fromWordsToBigrams(tags);

        return tags;
    }

    private static void fromWordsToBigrams(Map<String, Integer> tokens) {

        for (Map.Entry<String, Integer> tag : tokens.entrySet()) {

            if (tag.getKey().contains(" ")) {

                // "first second" - bigram
                String first = tag.getKey().split(" ")[0];
                String second = tag.getKey().split(" ")[0];

                if (tokens.keySet().contains(first))
                    tokens.put(first, tokens.get(first) - tag.getValue());

                if (tokens.keySet().contains(second))
                    tokens.put(second, tokens.get(second) - tag.getValue());
            }
        }
    }

    public static Map<String, Integer> getCategoriesMap(Map<String, Integer> tokensMap) {

        Map<String, Integer> categories  = new HashMap<>();
        ValueComparator bvc =  new ValueComparator(categories);
        Map<String, Integer> sortedCategories  = new TreeMap<>(bvc);

        for (Map.Entry<String, Integer> token : tokensMap.entrySet()) {

            Token tokens = Ebean.find(Token.class).where().idEq(token.getKey()).findUnique();
            List<String> tokenCategories = fromJson(tokens.getCategories(), ArrayList.class);

            for (String tokenCategory : tokenCategories) {

                Category category = getCategory(token.getKey(), tokenCategory);
                List<String> categoryCategories = fromJson(category.getCategories(), ArrayList.class);

                if (category != null)
                    Logger.debug("[category ok] " + tokenCategory);

                if (categories.containsKey(tokenCategory)) {

                    int old = categories.get(tokenCategory);
                    categories.put(tokenCategory, old + token.getValue());

                } else
                    categories.put(tokenCategory, token.getValue());
            }
        }

        sortedCategories.putAll(categories);
        return sortedCategories;
    }

    public static Category getCategory(String tokenName, String categoryName) {

        Category category;

        category = Ebean.find(Category.class).where().idEq(categoryName).findUnique();

        if (category != null)
            return category;

        Document doc;

        try {

            String lang = LangDetect.detect(categoryName);

            Logger.debug("[category new] " + categoryName + " [" + lang + "]");

            String connectUrl = "http://" + lang + categoryUrl

                    + (!lang.equals("en") ? URLEncoder.encode(categoryName.replace(" ", "_"), "UTF-8") : categoryName);

            Connection connection = Jsoup.connect(connectUrl);

            doc = connection.userAgent(Watcher.USER_AGENT).followRedirects(true).get();

        } catch (Exception exception) { //TODO

            Logger.error("[category null] " + categoryName + " [from token] " + tokenName);
            return null;
        }

        Elements links = doc.body().select("#mw-normal-catlinks ul a");

        List<String> categories = new ArrayList<>();

        for (Element link : links) {

            String c = link.text().toLowerCase();
            categories.add(c);
        }

        category = new Category(categoryName, String.valueOf(toJson(categories)));
        category.save();

        return category;
    }
}

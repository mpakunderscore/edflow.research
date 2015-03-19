package controllers.page;

import com.fasterxml.jackson.databind.JsonNode;
import models.Tag;
import play.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pavelkuzmin on 09/04/14.
 */
public class TagParser {

    private final static Pattern wordPattern = Pattern.compile("[^\\s+\"\\d+(){}, –'\\-=_@:$;#%!<>&\\|\\*\\?\\[\\]\\.\\/\\+\\\\]{2,}");

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

//        setBigrams(wordsList, words);

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

    /**
     *
     * This method will return $defaultTagsCount tags in Map. Any tag taken as Wikipedia page (getTagPage).
     *
     * @param wordsMap - Map of sorted words
     * @return - this method will return $defaultTagsCount tags in Map. if tag is allowed and
     */

    public static Map<String, Integer> getTags(Map<String, Integer> wordsMap) {

        Map<String, Integer> tags = new HashMap<>();

        int i = 0;
        for (Map.Entry<String, Integer> word : wordsMap.entrySet()) {

            Tag tag = TagFactory.getTagPage(word.getKey());

            if (tag == null) {

                Logger.error("[tag null] " + word.getKey());
                break;

            } else if (tag.isMark()) {

                Logger.debug("[tag] " + word.getKey() + ": " + word.getValue() + " " + tag.getCategories() + (tag.getRedirect() == null ? "" : " " + tag.getRedirect()));

                if (tag.getRedirect() != null) {

                    Tag redirect = TagFactory.getTagPage(tag.getRedirect());

                    if (redirect != null && !redirect.isMark()) continue;

                    if (tags.containsKey(tag.getRedirect())) {

                        int old = tags.get(tag.getRedirect());
                        tags.put(tag.getRedirect(), old + word.getValue());

                    } else tags.put(tag.getRedirect(), word.getValue());

                } else tags.put(word.getKey(), word.getValue());

                i++;

            } else {

                Logger.debug("[tag not mark] " + word.getKey() + ": " + word.getValue());
            }
        }

//        for (Map.Entry<String, Integer> tag : tags.entrySet()) {
//
//            if (tag.getKey().contains(" ")) {
//
//                // "first second" - bigram
//                String first = tag.getKey().split(" ")[0];
//                String second = tag.getKey().split(" ")[0];
//
//                if (tags.keySet().contains(first))
//                    tags.put(first, tags.get(first) - tag.getValue());
//
//                if (tags.keySet().contains(second))
//                    tags.put(second, tags.get(second) - tag.getValue());
//            }
//        }

        return tags;
    }
}

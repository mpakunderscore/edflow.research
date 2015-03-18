package controllers.page;

import com.avaje.ebean.Ebean;
import controllers.parsers.Watcher;
import models.Tag;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.Logger;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 08/04/14.
 */

// wiki api == old crap

public class TagFactory {

    private static final List<String> unmarkedCategories = new ArrayList<String>() {{


        add("disambiguation pages");
        add("english grammar");
        add("grammar");
        add("parts of speech");
        add("months");
        add("integers");
        add("english modal and auxiliary verbs");

        add("units of time");
        add("identifiers");

        add("многозначные термины"); //TODO
    }};

    private static final String url = ".wikipedia.org/wiki/";

    private static final String simpleWordsUS = "http://simple.wikipedia.org/wiki/Wikipedia:List_of_1000_basic_words";
    private static final String simpleWordsRU = "http://en.wiktionary.org/wiki/Appendix:Frequency_dictionary_of_the_modern_Russian_language_(the_Russian_National_Corpus)";

    public static Tag getTagPage(String word) {

        Tag tag = null;

        tag = Ebean.find(Tag.class).where().idEq(word).findUnique();

        if (tag != null) return tag;

        Document doc = null;

        try {

            String lang = LangDetect.detect(word);

            Logger.debug("[new tag] " + word + " [" + lang + "]");

            String connectUrl = "http://" + lang + url

                    + (!lang.equals("en") ? URLEncoder.encode(word, "UTF-8") : word);

            Connection connection = Jsoup.connect(connectUrl);

            doc = connection.userAgent(Watcher.USER_AGENT).followRedirects(true).get();

        } catch (Exception exception) { //TODO

            tag = new Tag(word, null, null, false);
            tag.save();

            return tag;
        }

        String redirect_name = null;

        String name = doc.body().getElementById("firstHeading").text().toLowerCase();

        if (!name.equals(word)) {

            redirect_name = name;
        }

        Elements links = doc.body().select("#mw-normal-catlinks ul a");

        List<String> categories = new ArrayList<String>();

        boolean mark = true;

        for (Element link : links) {

            String category = link.text().toLowerCase();

            if (unmarkedCategories.contains(category))
                mark = false;

            categories.add(category);
        }

        tag = new Tag(word, redirect_name, String.valueOf(toJson(categories)), mark);

        tag.save();

        return tag;
    }

    public static void loadSimpleWordsEN() {

        Document doc = null;
        Connection connection = Jsoup.connect(simpleWordsUS);

        try {

            doc = connection.userAgent(Watcher.USER_AGENT).followRedirects(true).get();

        } catch (IOException exception) {

            exception.printStackTrace();
            return;
        }

        Elements words = doc.body().select("dd a");

        for (Element word : words) {

            String wordText = word.text().toLowerCase();

            try {
                new Tag(wordText, null, "[\"simple words\"]", false).save();
            }catch (Exception e) {
                continue;
            }
        }
    }

    public static void loadSimpleWordsRU() {

        Document doc = null;
        Connection connection = Jsoup.connect(simpleWordsRU);

        try {

            doc = connection.userAgent(Watcher.USER_AGENT).followRedirects(true).get();

        } catch (IOException exception) {

            exception.printStackTrace();
            return;
        }

        Elements words = doc.body().select("div[id=mw-content-text] ol li a");

        int i = 0;
        for (Element word : words) {

            String wordText = word.text().toLowerCase();

            try {
                new Tag(wordText, null, "[\"simple words\"]", false).save();
            } catch (Exception e) {
                continue;
            }

            i++;

            if (i > 2000)
                break;
        }
    }
}

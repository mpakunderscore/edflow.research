package controllers.page.types;

import controllers.parsers.FavIcon;
import controllers.parsers.JSONTag;
import controllers.parsers.TagParser;
import controllers.parsers.Watcher;
import models.Domain;
import models.WebData;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import play.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 08/04/14.
 */
public class Page implements Types {

    public static Page get(String url) {

        Long time = System.currentTimeMillis();

        String title;
        Document doc;

        Connection connection = Jsoup.connect(url);

        try {

            doc = connection.userAgent(Watcher.USER_AGENT).followRedirects(true).timeout(5000).get();

        } catch (IOException exception) { //TODO

//            Logger.error("[webData is NULL]  " + url);
            return null;
        }

        String text = doc.body().text();

        title = doc.title();

        if (title.length() == 0) return null; //TODO

        Map<String, Integer> words = TagParser.getWords(text);
        Logger.debug("[words] " + words.size());
        Map<String, Integer> textTags = TagParser.getTags(words);
        List<JSONTag> tagsList = TagParser.getTagsList(textTags);

        int uniqueWordsCount = words.size();

        int wordsCount = 0;
        for (int value : words.values()) {
            wordsCount += value;
        }

        Logger.debug("[time for web data] " + (System.currentTimeMillis() - time) / 1000);
        return new WebData(url, title, String.valueOf(toJson(tagsList)), wordsCount, uniqueWordsCount);
    }

    public static Domain requestDomain(String url) {

        Long time = System.currentTimeMillis();

        String domainString = WebData.getDomainString(url);

        Logger.debug("[get domain] " + domainString);

        String title = "";

        Map<String, Integer> tagsMap = new HashMap<String, Integer>();

        Document doc = null;
        Connection connection = Jsoup.connect("http://" + domainString);

        try {

            doc = connection.userAgent(Watcher.USER_AGENT).followRedirects(true).get();

        } catch (IOException exception) { //TODO

            Logger.error("[can't get domain from web] " + domainString);
//            exception.printStackTrace();

            return null;
        }

        String text = doc.body().text();

        title = doc.title();

        if (title.length() == 0)
            return null; //TODO

        Map<String, Integer> words = TagParser.getWords(text);
        Map<String, Integer> textTags = TagParser.getTags(words);
        List<JSONTag> tagsList = TagParser.getTagsList(textTags);

        Logger.debug("[time for domain] " + (System.currentTimeMillis() - time) / 1000);
        time = System.currentTimeMillis();

        String favIconFormat = null;
        try {
            favIconFormat = FavIcon.produce(url, doc);
        } catch (Exception e) {
        }

        Logger.debug("[time for favicon] " + (System.currentTimeMillis() - time) / 1000);

        return new Domain(domainString, title, String.valueOf(toJson(tagsList)), Domain.UNCHECKED, favIconFormat);
    }
}

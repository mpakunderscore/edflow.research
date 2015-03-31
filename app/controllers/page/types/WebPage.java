package controllers.page.types;

import controllers.Watcher;
import controllers.page.Engine;
import controllers.page.utils.Node;
import models.Page;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import play.Logger;

import java.io.IOException;
import java.util.Map;

import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 08/04/14.
 */
public class WebPage extends Type {

    public static Page get(String url) {

        Long time = System.currentTimeMillis();

        String title;
        Document doc;

        Connection connection = Jsoup.connect(url);

        try {

            doc = connection.userAgent(Watcher.USER_AGENT).followRedirects(true).timeout(5000).get();

        } catch (IOException exception) { //TODO
            return null;
        }

        String text = doc.body().text();

        title = doc.title();

        if (title.length() == 0)
            return null; //TODO

        Map<String, Integer> words = Engine.getWordsMap(text);
        Map<String, Integer> textTags = Engine.getTokensMap(words);
        Map<String, Integer> categories = Engine.getCategoriesMap(textTags);

//        System.out.println(textTags);
//        System.out.println(categories);

        int uniqueWordsCount = words.size();

        int wordsCount = 0;
        for (int value : words.values()) {
            wordsCount += value;
        }

        Logger.debug("[time for url] " + (System.currentTimeMillis() - time) / 1000);
        return new Page(url, title, String.valueOf(toJson(Node.getNodeList(categories))));
    }
}

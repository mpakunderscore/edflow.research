package controllers.engine.types;

import controllers.Watcher;
import controllers.engine.Engine;
import controllers.engine.utils.Log;
import controllers.engine.utils.Node;
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

        final Long time = Log.getTime();

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
        Map<String, Integer> tokens = Engine.getTokensMap(words);
        Map<String, Integer> categories = Engine.getCategoriesMap(tokens);

//        System.out.println(textTags);
//        System.out.println(categories);

        int uniqueWordsCount = words.size();

        int wordsCount = 0;
        for (int value : words.values()) {
            wordsCount += value;
        }

        Log.time("[url] ", time);
        return new Page(url, title, tokens, categories);
    }
}

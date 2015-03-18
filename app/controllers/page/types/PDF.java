package controllers.page.types;

import controllers.parsers.JSONTag;
import controllers.parsers.TagParser;
import models.WebData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 08/04/14.
 */
public class PDF {

    public static WebData requestWebData(String url) {

        Map<String, Integer> words = new HashMap<String, Integer>();
        Map<String, Integer> textTags = new HashMap<String, Integer>();

        textTags.put("pdf", 0);
        List<JSONTag> tagsList = TagParser.getTagsList(textTags);

        String title = url;

        int uniqueWordsCount = words.size();

        int wordsCount = 0;
        for (int value : words.values()) {
            wordsCount += value;
        }

        return new WebData(url, title, String.valueOf(toJson(tagsList)), wordsCount, uniqueWordsCount);
    }
}

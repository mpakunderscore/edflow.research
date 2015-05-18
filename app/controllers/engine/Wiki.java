package controllers.engine;

import com.avaje.ebean.Ebean;
import controllers.Watcher;
import controllers.engine.utils.LangDetect;
import controllers.engine.utils.Log;
import models.Category;
import models.Page;
import models.Token;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.Logger;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 01/04/15.
 */
public class Wiki {

    private static final String tokenUrl = ".wikipedia.org/wiki/";
    private static final String categoryUrl = ".wikipedia.org/wiki/Category:";

    private static final String protocol = "https://";

    private static final String randomUrl = protocol + "en.wikipedia.org/wiki/Special:Random";


    public static Page getRandom() throws Exception {

        Connection.Response response;

        try {

            response = Jsoup.connect(randomUrl).userAgent(Watcher.USER_AGENT).followRedirects(true).execute();

        } catch (Exception exception) { //TODO

            return null;
        }

        return Watcher.getPage(response.url().toString());
    }

    public static Token getToken(String word) {

        Token token;

        token = Ebean.find(Token.class).where().idEq(word).findUnique();

        if (token != null)
            return token;

        Document doc = null;

        try {

            String lang = LangDetect.detect(word);

            Log.out(Log.State.Tokens, "[new] " + word + " [" + lang + "]");

            String connectUrl = protocol + lang + tokenUrl

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

        token = new Token(word, redirect_name, categories, true);
        token.save();

        return token;
    }

    public static Category getCategory(String tokenName, String categoryName) {

        Category category = Ebean.find(Category.class).where().idEq(categoryName).findUnique();

        if (category != null)
            return category;

        Document doc;

        try {

            String lang = LangDetect.detect(categoryName);

            Log.out(Log.State.Categories, "[new] " + categoryName + " [" + lang + "]");

            String connectUrl = protocol + lang + categoryUrl

                    + (!lang.equals("en") ? URLEncoder.encode(categoryName.replace(" ", "_"), "UTF-8") : categoryName);

            Connection connection = Jsoup.connect(connectUrl);

            doc = connection.userAgent(Watcher.USER_AGENT).followRedirects(true).get();

        } catch (Exception exception) { //TODO

            Log.out(Log.State.Categories, "[null] " + categoryName + " [from token] " + tokenName);

            category = new Category(categoryName, String.valueOf(toJson(new ArrayList<>())));
            category.save();

            return category;
        }

        //TODO check
        // This category contains only the following page. This list may not reflect recent changes (learn more).

        Elements links = doc.body().select("#mw-normal-catlinks ul a");

        List<String> subCategories = new ArrayList<>();

        for (Element link : links) {
            String subCategory = link.text().toLowerCase();
            subCategories.add(subCategory);
        }

        if (subCategories.size() == 0)
            Log.out(Log.State.Categories, "[top] " + categoryName);

        category = new Category(categoryName, String.valueOf(toJson(subCategories)));
        category.save();

        return category;
    }
}

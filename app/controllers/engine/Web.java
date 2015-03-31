package controllers.engine;

import com.avaje.ebean.Ebean;
import controllers.Watcher;
import models.Category;
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
public class Web {

    private static final String tokenUrl = ".wikipedia.org/wiki/";
    private static final String categoryUrl = ".wikipedia.org/wiki/Category:";

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

    public static Category getCategory(String tokenName, String categoryName) {

        Category category = Ebean.find(Category.class).where().idEq(categoryName).findUnique();

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

        //TODO check
        // This category contains only the following page. This list may not reflect recent changes (learn more).

        Elements links = doc.body().select("#mw-normal-catlinks ul a");

        List<String> subCategories = new ArrayList<>();

        for (Element link : links) {
            String subCategory = link.text().toLowerCase();
            subCategories.add(subCategory);
        }

        if (subCategories.size() == 0)
            Logger.debug("[category top] " + categoryName);

        category = new Category(categoryName, String.valueOf(toJson(subCategories)));
        category.save();

        return category;
    }
}

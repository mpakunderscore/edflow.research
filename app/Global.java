import com.avaje.ebean.Ebean;
import com.cybozu.labs.langdetect.LangDetectException;
import controllers.page.LangDetect;
import models.Category;
import models.Page;
import models.Token;
import play.Application;
import play.GlobalSettings;
import play.Logger;

import java.util.List;

/**
 * Created by pavelkuzmin on 19/05/14.
 */
public class Global extends GlobalSettings {

    public void onStart(Application app) {

        try {

            LangDetect.init("lib/profiles");

        } catch (LangDetectException e) {
//            e.printStackTrace();
        }

        List<Token> tokens = Ebean.find(Token.class).findList();
        Logger.info("Tokens: " + tokens.size());

        List<Category> categories = Ebean.find(Category.class).findList();
        Logger.info("Categories: " + categories.size());

        List<Page> pages = Ebean.find(Page.class).findList();
        Logger.info("Pages: " + pages.size());

        Logger.info("Application has started");
    }

    public void onStop(Application app) {

        Logger.info("Application shutdown...");
    }
}

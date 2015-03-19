import com.avaje.ebean.Ebean;
import com.cybozu.labs.langdetect.LangDetectException;
import controllers.page.LangDetect;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pavelkuzmin on 19/05/14.
 */
public class Global extends GlobalSettings {

    public void onStart(Application app) {

        try {

            LangDetect.init("lib/profiles");

        } catch (LangDetectException e) {
            e.printStackTrace();
        }

        Logger.info("Application has started");
    }

    public void onStop(Application app) {

        Logger.info("Application shutdown...");
    }
}

package controllers;

import com.avaje.ebean.Ebean;
import controllers.page.types.PDF;
import controllers.page.types.WebPage;
import models.Page;
import play.Logger;

/**
 * Created by pavelkuzmin on 16/04/14.
 */
public class Watcher {

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36"; //TODO

    private static final String pdf = ".pdf";

    private static Page requestPage(String url) {

        Logger.debug("[url request] " + url);

        if (url.endsWith(pdf))
            return PDF.get(url);

        else
            return WebPage.get(url);
    }

    public static Page getPage(String url) throws Exception {

        Page page = Ebean.find(Page.class).where().eq("url", url).findUnique();
        if (page == null) {

            page = Watcher.requestPage(url);

            if (page == null) {
                Logger.error("[url not responding] " + url);
                return null;
            }

            page.save();
        }

        return page;
    }
}

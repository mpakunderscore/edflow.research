package controllers;

import controllers.page.types.PDF;
import controllers.page.types.WebPage;
import models.Page;
import play.Logger;

/**
 * Created by pavelkuzmin on 16/04/14.
 */
public class Watcher {

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36"; //TODO

    public static Page request(String url) {

        //TODO types of page (html, pdf, fb2, txt) move type checker into Interface
        if (url.endsWith(".pdf")) {
            Logger.debug("[pdf] " + url);
            return PDF.get(url);

        } else {
            Logger.debug("[web page] " + url);
            return WebPage.get(url);
        }
    }
}

package controllers;

import com.avaje.ebean.Ebean;
import controllers.parsers.types.PDF;
import controllers.parsers.types.Page;
import models.Bundle;
import models.Domain;
import models.WebData;
import play.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 16/04/14.
 */
public final class Watcher {

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36"; //TODO

    public static Page request(String url) {

        Logger.debug("[get web data from web] " + url);

        //TODO types of page (html, pdf, fb2, txt) move type checker into Interface
        if (url.endsWith(".pdf")) {

            Logger.debug("[pdf] " + url);
            return PDF.requestWebData(url);

        } else { // if html page

            return Page.requestWebData(url);
        }
    }

    public static Page getPage(String url) throws Exception {

        Page page = Ebean.find(Page.class).where().eq("url", url).findUnique();
        if (webData == null) {

            Logger.debug("[can't find web data in database]  " + url);
            webData = Watcher.requestPage(url);

            if (webData == null) {
                Logger.error("[web data not responding]  " + url);
                return null; //TODO
            }

            String domainString = WebData.getDomainString(webData.getUrl());

            Domain domain = Ebean.find(Domain.class).where().idEq(domainString).findUnique();
            if (domain == null) {

                Logger.debug("[can't find domain in database]  " + domainString);
                domain = Page.requestDomain(url);

                if (domain == null)
                    return null;

                domain.save();
            }

            webData.setFavIconFormat(domain.getFavIconFormat());

            webData.save();
        }

        return webData;
    }

    public static void fillBundle(final Bundle bundle, final String urls) {

        String title = bundle.getTitle();

        List<WebData> webDataList = new ArrayList<WebData>();
        List<Long> webDataIds = new ArrayList<Long>();

        String[] urlsList = urls.split("\r\n");

        Logger.debug("[fill bundle] '" + bundle.getTitle() + "' [" + urlsList.length + "]");
        Long time = System.currentTimeMillis();

        int i = 0;
        for (String urlString : urlsList) {

            Logger.debug("[check url] " + urlString + " (" + ++i + "/" + urlsList.length + ") ");

            try {
                new URL(urlString);
            } catch (MalformedURLException e) {
                Logger.error("[invalid url] " + urlString);
                continue;
            }

            WebData webData;
            try {
                webData = Watcher.getWebData(urlString);
            } catch (Exception e) {
                continue;
            }

            if (webData == null) {
                continue;
            }

            webDataList.add(webData);
            webDataIds.add(webData.getId());

            bundle.setWebDataIds(String.valueOf(toJson(webDataIds)));

            bundle.setTags(String.valueOf(toJson(TagParser.getTagsForBundle(webDataList))));

            bundle.setTitle(title + " " + i + "/" + urlsList.length);

            bundle.update();
        }

        bundle.setTitle(title);

        bundle.update();

        Logger.debug("[time for bundle] " + (System.currentTimeMillis() - time) / 1000);
    }
}

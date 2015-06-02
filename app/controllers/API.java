package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.engine.Classifier;
import controllers.engine.Wiki;
import controllers.engine.utils.Log;
import controllers.engine.utils.Node;
import controllers.engine.utils.ValueComparator;
import models.Category;
import models.Page;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.*;

import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 16/03/15.
 */
public class API extends Controller {

    public static Result page(String url) throws Exception {

        return ok(toJson(Watcher.getPage(url)));
    }

    public static Result pageProcessed(String url) throws Exception {

        final Long time = Log.getTime();

        List<Page> pagesList = Ebean.find(Page.class).findList();

        Page page = Watcher.getPage(url);

        Classifier.processPage(pagesList, page);

        Log.time("pageProcessed()", time);

        return ok(toJson(page));
    }

    public static Result pages() throws Exception {

        final Long time = Log.getTime();

        Map<String, List> out = new HashMap<>();

        int limit = 200;
        List<Page> pagesList = Ebean.find(Page.class).order().desc("id").setMaxRows(limit).findList();
//        List<Page> pagesList = Ebean.find(Page.class).order().desc("id").findList();

        out.put("pages", pagesList);

        Map<String, Integer> categories = Classifier.getCategories(pagesList);

        out.put("categories", Node.getNodeList(categories));

        Log.time("pages(" + pagesList.size() + ")", time);

        return ok(toJson(out));
    }

    public static Result tokens() throws Exception {

        List<Page> pagesList = Ebean.find(Page.class).findList();

        return ok(toJson(Classifier.getTokens(pagesList)));
    }

    public static Result categories() throws Exception {

        List<Page> pagesList = Ebean.find(Page.class).findList();

        Map<String, Integer> categories = Classifier.getCategories(pagesList);

//        fillAsGraph(categories, 0);

        return ok(toJson(categories));
    }

    public static Result generate() throws Exception {

        List<Page> pages = new ArrayList<>();

        int limit = 100;

        for (int i = 0; i < limit; i++) {

            Page page = Wiki.getRandom();
            pages.add(page);
        }

        return ok(toJson(pages));
    }
}

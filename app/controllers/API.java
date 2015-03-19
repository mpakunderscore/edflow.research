package controllers;

import com.avaje.ebean.Ebean;
import models.Page;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 16/03/15.
 */
public class API extends Controller {

    public static Result page(String url) throws Exception {

        return ok(toJson(Watcher.getPage(url)));
    }

    public static Result pages() throws Exception {

        int limit = 200;
        List<Page> pageList = Ebean.find(Page.class).order().desc("id").setMaxRows(limit).findList();
        return ok(toJson(pageList));
    }

    public static Result categories() throws Exception {

        return ok();
    }

}

package controllers;

import com.avaje.ebean.Ebean;
import models.Page;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 16/03/15.
 */
public class API extends Controller {

    public static Result page(String url) throws Exception {

        return ok(toJson(Watcher.getPage(url)));
    }

    public static Result pages() throws Exception {

        return ok();
    }

    public static Result categories() throws Exception {

        return ok();
    }

}

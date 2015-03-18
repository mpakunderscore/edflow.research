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

    public static Result add(String url) throws Exception {

        Page page = Ebean.find(Page.class).where().eq("url", url).findUnique();

        return ok();
    }

    public static Result links() throws Exception {

        return ok();
    }

    public static Result categories() throws Exception {

        return ok();
    }

}

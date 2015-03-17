package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by pavelkuzmin on 16/03/15.
 */
public class Application extends Controller {

    public static Result index() {

        return ok(views.html.index.render());
    }
}

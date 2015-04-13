package controllers.engine.types;

import models.Page;

/**
 * Created by pavelkuzmin on 08/04/14.
 */
public class PDF extends Type {

    public static Page get(String url) {

        String title = url;

        return new Page(url, title, null, null);
    }
}

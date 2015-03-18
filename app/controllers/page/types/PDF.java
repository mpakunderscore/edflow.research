package controllers.page.types;

import models.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pavelkuzmin on 08/04/14.
 */
public class PDF extends Type {

    public static Page get(String url) {

        String title = url;

        return new Page(url, title);
    }
}

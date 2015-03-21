package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.page.utils.ValueComparator;
import models.Page;
import play.mvc.Controller;
import play.mvc.Result;

import javax.swing.tree.TreeNode;
import java.util.*;

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

        Map<String, Integer> categories  = new HashMap<>();
        ValueComparator bvc =  new ValueComparator(categories);
        Map<String, Integer> sortedCategories  = new TreeMap<>(bvc);

        List<Page> pageList = Ebean.find(Page.class).findList();

        for (Page page : pageList) {

            JsonNode pageCategories = page.getCategories();
            for (JsonNode category : pageCategories) {

                String name = category.get("name").asText();
                if (categories.containsKey(name)) {
                    categories.put(name, categories.get(name) + 1);
                } else {
                    categories.put(name, 1);
                }
            }
        }

        sortedCategories.putAll(categories);
        return ok(toJson(sortedCategories));
    }
}

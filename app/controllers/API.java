package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.engine.Engine;
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

                if (categories.containsKey(name))
                    categories.put(name, categories.get(name) + 1);

                else
                    categories.put(name, 1);
            }
        }

//        fillAsGraph(categories, 0);

        sortedCategories.putAll(categories);
        return ok(toJson(sortedCategories));
    }

    private static void fillAsGraph(Map<String, Integer> categories, int i) {

        Logger.debug("[fill graph] " + i + " [wave size] " + categories.size());
        i++;

        Map<String, Integer> subCategories = new HashMap<>();

        for (String categoryName : categories.keySet()) {

            Category category = Engine.getCategory(null, categoryName);

            if (category == null)
                continue;

            JsonNode categorySubCategories = category.getCategories();

            for (JsonNode subCategory : categorySubCategories) {

                String name = subCategory.asText();

                if (subCategories.containsKey(name))
                    subCategories.put(name, subCategories.get(name) + 1);

                else
                    subCategories.put(name, 1);
            }
        }

        if (subCategories.size() > 0)
            fillAsGraph(subCategories, i);

        for (String subCategoryName : subCategories.keySet()) {

            if (categories.containsKey(subCategoryName))
                categories.put(subCategoryName, subCategories.get(subCategoryName) + 1);

            else
                categories.put(subCategoryName, 1);
        }
    }
}

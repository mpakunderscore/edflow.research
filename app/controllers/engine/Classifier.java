package controllers.engine;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.engine.utils.ValueComparator;
import models.Category;
import models.Page;
import play.Logger;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 15/04/15.
 */
public class Classifier {

    public static Map<String, Integer> getTokens(List<Page> pagesList) throws Exception {

        Map<String, Integer> tokens  = new HashMap<>();
        ValueComparator bvc =  new ValueComparator(tokens);
        Map<String, Integer> sortedTokens  = new TreeMap<>(bvc);

        int pagesCount = pagesList.size();

        Map<String, Integer> count = process(pagesList, true);
        Map<String, Integer> mass = process(pagesList, false);

        for (String token : mass.keySet()) {

            int tMass = mass.get(token);
            int tCount = count.get(token);

            double average = tMass / tCount;
            double idf = Math.log((double) pagesCount / (double) tCount);

            tokens.put(token, (int) (average * idf));

//            System.out.println(token + " " + average + " " + idf);
//            System.out.format("%32s %10s %16s %16s\n", token, average, idf, average * idf);
        }

        sortedTokens.putAll(tokens);
        return sortedTokens;
    }

    private static Map<String, Integer> process(List<Page> pagesList, boolean w) {

        Map<String, Integer> tokens  = new HashMap<>();

        for (Page page : pagesList) {

            for (Map.Entry<String, Integer> token : page.getTokensMap().entrySet()) {

                String name = token.getKey();
                int weight = (w ? 1 : token.getValue());

                if (tokens.containsKey(name))
                    tokens.put(name, tokens.get(name) + weight);

                else
                    tokens.put(name, weight);
            }
        }

        return tokens;
    }

    public static Map<String, Integer> getCategories(List<Page> pagesList) {

        Map<String, Integer> categories  = new HashMap<>();
        ValueComparator bvc =  new ValueComparator(categories);
        Map<String, Integer> sortedCategories  = new TreeMap<>(bvc);

        for (Page page : pagesList) {

            for (Map.Entry<String, Integer> category : page.getCategoriesMap().entrySet()) {

                String name = category.getKey();
                int weight = category.getValue();

                if (categories.containsKey(name))
                    categories.put(name, categories.get(name) + weight);

                else
                    categories.put(name, weight);
            }
        }

        sortedCategories.putAll(categories);
        return sortedCategories;
    }

    private static void fillAsGraph(Map<String, Integer> categories, int i) {

        Logger.debug("[fill graph] " + i + " [wave size] " + categories.size());
        i++;

        Map<String, Integer> subCategories = new HashMap<>();

        for (String categoryName : categories.keySet()) {

            Category category = Wiki.getCategory(null, categoryName);

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

    public static void processPage(List<Page> pagesList, Page target) {

        target.setTokens(getPageTokens(pagesList, target));
        target.setCategories(getPageCategories(pagesList, target));
    }

    private static Map<String, Integer> getPageTokens(List<Page> pagesList, Page target) {

        Map<String, Integer> tokens  = new HashMap<>();
        ValueComparator bvc =  new ValueComparator(tokens);
        Map<String, Integer> sortedTokens  = new TreeMap<>(bvc);

        int pagesCount = pagesList.size();

        Map<String, Integer> count = process(pagesList, true);

        for (Map.Entry<String, Integer> token : target.getTokensMap().entrySet()) {

            String name = token.getKey();
            int weight = token.getValue();

            int tCount = count.get(name);

            double idf = Math.log((double) pagesCount / (double) tCount);

            tokens.put(name, (int) (weight * idf)); //TODO not weight, weight/tokensWeight
        }

        sortedTokens.putAll(tokens);
        return sortedTokens;
    }

    public static Map<String, Integer> getPageCategories(List<Page> pagesList, Page target) {

        Map<String, Integer> categories  = new HashMap<>();
        ValueComparator bvc =  new ValueComparator(categories);
        Map<String, Integer> sortedCategories  = new TreeMap<>(bvc);


        sortedCategories.putAll(categories);
        return sortedCategories;
    }
}

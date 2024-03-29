package controllers.engine;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.engine.utils.Log;
import controllers.engine.utils.ValueComparator;
import models.Category;
import models.Page;
import play.Logger;
import play.cache.Cache;

import java.util.*;

/**
 * Created by pavelkuzmin on 15/04/15.
 */
public class Classifier {

    public static Map<String, Integer> getTokens(List<Page> pagesList) throws Exception {

        Map<String, Integer> tokens  = new HashMap<>();
        ValueComparator bvc =  new ValueComparator(tokens);
        Map<String, Integer> sortedTokens  = new TreeMap<>(bvc);

        int pagesCount = pagesList.size();

        Map<String, Integer> count = processTokens(pagesList, true);
        Map<String, Integer> mass = processTokens(pagesList, false);

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

    private static Map<String, Integer> processTokens(List<Page> pagesList, boolean w) {

        Map<String, Integer> tokens  = new HashMap<>();

        for (Page page : pagesList) {

            for (Map.Entry<String, Integer> token : page.tokensMap().entrySet()) {

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

    private static Map<String, Integer> processCategories(List<Page> pagesList, boolean w) {

        Map<String, Integer> categories  = new HashMap<>();

        for (Page page : pagesList) {

            for (Map.Entry<String, Integer> category : page.categoriesMap().entrySet()) {

                String name = category.getKey();
                int weight = (w ? 1 : category.getValue());

                if (categories.containsKey(name))
                    categories.put(name, categories.get(name) + weight);

                else
                    categories.put(name, weight);
            }
        }

        return categories;
    }

    public static Map<String, Integer> processCategories(List<Page> pagesList) {

        List oldPagesList = new ArrayList<>(pagesList);

        int i = 0;

        for (Page page : pagesList) {

            final Long time = Log.getTime();

            processPage(oldPagesList, page);

            Log.time("processPage(" + ++i + "/" + pagesList.size() + ")", time);
        }

        return getCategories(pagesList);
    }

    public static Map<String, Integer> getCategories(List<Page> pagesList) {

        Map<String, Integer> categories  = new HashMap<>();
        ValueComparator bvc =  new ValueComparator(categories);
        Map<String, Integer> sortedCategories  = new TreeMap<>(bvc);

        //first
        for (Page page : pagesList) {

            for (Map.Entry<String, Integer> category : page.categoriesMap().entrySet()) {

                String name = category.getKey();
                int weight = category.getValue();
//                int weight = 1;

                if (categories.containsKey(name))
                    categories.put(name, categories.get(name) + weight);

                else
                    categories.put(name, weight);
            }
        }

        Map<String, Integer> subCategories = new HashMap<>();

        //second
        boolean second = false;
        if (second) {

            for (Map.Entry<String, Integer> category : categories.entrySet()) {

                Category categoryObject = Wiki.getCategory(null, category.getKey());

//            String name = category.getKey();
                int weight = category.getValue();
//                int weight = 1;

                if (categoryObject == null)
                    continue;

                JsonNode categorySubCategories = categoryObject.getCategories();

                for (JsonNode subCategory : categorySubCategories) {

                    String name = subCategory.asText();

                    if (subCategories.containsKey(name))
                        subCategories.put(name, subCategories.get(name) + weight);

                    else
                        subCategories.put(name, weight);
                }
            }

            for (String subCategoryName : subCategories.keySet()) {

                int weight = subCategories.get(subCategoryName);

                if (categories.containsKey(subCategoryName))
                    categories.put(subCategoryName, subCategories.get(subCategoryName) + weight);

                else
                    categories.put(subCategoryName, weight);
            }
        }


        sortedCategories.putAll(categories);
        return sortedCategories;
    }

    private static void fillAsGraph(Map<String, Integer> categories, int i) {

        Log.out(Log.State.Research, "[fill graph] " + i + " [wave size] " + categories.size());
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

        Page saved = (Page) Cache.get(target.getUrl());

//        if (saved != null)
//            return;

        target.setTokens(getPageTokens(pagesList, target));

        target.setCategories(Engine.getCategoriesMap(target.tokensMap()));

        target.setCategories(getPageCategories(pagesList, target));

        Cache.set(target.getUrl(), target);
    }

    private static Map<String, Integer> getPageTokens(List<Page> pagesList, Page target) {

        Map<String, Integer> tokens  = new HashMap<>();
        ValueComparator bvc =  new ValueComparator(tokens);
        Map<String, Integer> sortedTokens  = new TreeMap<>(bvc);

        int pagesCount = pagesList.size();

        Map<String, Integer> count = processTokens(pagesList, true);

        for (Map.Entry<String, Integer> token : target.tokensMap().entrySet()) {

            String name = token.getKey();
            int weight = token.getValue();

            int tCount = (count.containsKey(name) ? count.get(name) : 1);

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

        int pagesCount = pagesList.size();

        Map<String, Integer> count = processCategories(pagesList, true);

        for (Map.Entry<String, Integer> category : target.categoriesMap().entrySet()) {

            String name = category.getKey();
            int weight = category.getValue();

            int tCount = (count.containsKey(name) ? count.get(name) : 1);

            double idf = Math.log((double) pagesCount / (double) tCount);

            categories.put(name, (int) (weight * idf)); //TODO not weight, weight/tokensWeight
        }

        sortedCategories.putAll(categories);
        return sortedCategories;
    }
}

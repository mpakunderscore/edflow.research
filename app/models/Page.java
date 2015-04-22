package models;

import java.util.*;
import javax.persistence.*;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.engine.utils.Node;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.libs.Json;

import static play.libs.Json.fromJson;
import static play.libs.Json.toJson;

/**
 * Created by pavelkuzmin on 19/03/15.
 */

@Entity
@Table(name="pages")
public class Page extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Constraints.Required
    String url;

    String title;

    @Column(columnDefinition = "TEXT")
    String tokens;

    @Column(columnDefinition = "TEXT")
    String categories;

    public Page(String url, String title, Map<String, Integer> tokens, Map<String, Integer> categories) {
        this.url = url;
        this.title = title;
        this.tokens = String.valueOf(toJson(Node.getNodeList(tokens)));
        this.categories = String.valueOf(toJson(Node.getNodeList(categories)));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JsonNode getTokens() {
        return Json.parse(tokens);
    }

    public Map<String, Integer> tokensMap() {
        return Node.getItemsMap(tokens);
    }

    public void setTokens(Map<String, Integer> tokens) {
        this.tokens = String.valueOf(toJson(Node.getNodeList(tokens)));
    }

    public JsonNode getCategories() {
        return Json.parse(categories);
    }

    public Map<String, Integer> categoriesMap() {
        return Node.getItemsMap(categories);
    }

    public void setCategories(Map<String, Integer> categories) {
        this.categories = String.valueOf(toJson(Node.getNodeList(categories)));
    }

    public static String getDomainString(String url) {

        String domain = null;

        try {

            domain = url.split("://")[1].split("/")[0].replace("www.", "");

        } catch (Exception e) {
            return null;
        }

        return domain;
    }
}

package models;

import java.util.*;
import javax.persistence.*;

import com.fasterxml.jackson.databind.JsonNode;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.libs.Json;

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

    public Page(String url, String title, String tokens, String categories) {
        this.url = url;
        this.title = title;
        this.tokens = tokens;
        this.categories = categories;
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

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public JsonNode getCategories() {
        return Json.parse(categories);
    }

    public void setCategories(String categories) {
        this.categories = categories;
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

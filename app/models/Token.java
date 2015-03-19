package models;

import com.fasterxml.jackson.databind.JsonNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by pavelkuzmin on 19/03/14.
 */

@Entity
@Table(name="tokens")
public class Token extends Model {

    @Id
    public String name;

    String redirect;

    @Column(columnDefinition = "TEXT")
    String categories;

    boolean mark = false;

    public Token(String name, String redirect, String categories, boolean mark) {
        this.name = name;
        this.redirect = redirect;
        this.categories = categories;
        this.mark = mark;
    }

    public boolean isMark() {
        return mark;
    }

    public JsonNode getCategories() {
        return Json.parse(categories);
    }

    public String getRedirect() {
        return redirect;
    }
}

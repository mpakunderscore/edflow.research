package models;

import com.fasterxml.jackson.databind.JsonNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by pavelkuzmin on 20/03/15.
 */
@Entity
@Table(name="categories")
public class Category extends Model {

    @Id
    public String name;

    @Column(columnDefinition = "TEXT")
    String categories;

    public Category(String name, String categories) {
        this.name = name;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public JsonNode getCategories() {
        return Json.parse(categories);
    }
}
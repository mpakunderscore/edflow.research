package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

/**
 * Created by pavelkuzmin on 19/03/15.
 */

@Entity
@Table(name="users")
public class Page extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @Constraints.Required
    String url;

    String title;

    public Page(String url, String title) {
        this.url = url;
        this.title = title;
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

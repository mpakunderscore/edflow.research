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
}

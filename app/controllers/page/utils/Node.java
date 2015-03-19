package controllers.page.utils;

import java.util.List;

/**
 * Created by pavelkuzmin on 08/06/14.
 */
public class Node {

    public String name;
    public int weight;

    List<String> nodes;

    public Node(String name, int weight, List<String> nodes) {
        this.name = name;
        this.weight = weight;
        this.nodes = nodes;
    }

    public Node() {
    }
}

package controllers.engine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static List<Node> getNodeList(Map<String, Integer> map) {

        List<Node> nodes = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {

            Node node = new Node();
            node.name = entry.getKey();
            node.weight = entry.getValue();

            nodes.add(node);
        }

        return nodes;
    }
}

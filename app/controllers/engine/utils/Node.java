package controllers.engine.utils;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    public static Map<String, Integer> getItemsMap(String items) {

        Map<String, Integer> itemsMap = new TreeMap<>();

        for (JsonNode item : Json.parse(items)) {

            String name = item.get("name").asText();
            int weight = item.get("weight").asInt();

            itemsMap.put(name, weight);
        }

        return itemsMap;
    }
}

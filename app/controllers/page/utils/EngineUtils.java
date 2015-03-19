package controllers.page.utils;

import java.util.*;

/**
 * Created by pavelkuzmin on 20/03/15.
 */
public class EngineUtils {

    public static List<Node> getList(Map<String, Integer> map) {

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

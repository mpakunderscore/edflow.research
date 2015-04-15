import controllers.engine.utils.ValueComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by pavelkuzmin on 14/04/15.
 */
public class Main {

    public static void main(String[] args) {

        int a1 = 5;
        int a2 = 3;

        double a = (double) a1 / (double) a2;
        double idf = Math.log(a);
        System.out.println(idf);

        Map<String, Integer> tokens  = new HashMap<>();
        ValueComparator bvc =  new ValueComparator(tokens);
        Map<String, Integer> sortedTokens  = new TreeMap<>(bvc);

        tokens.put("one", 1);
        tokens.put("two", 2);

        for (String t : tokens.keySet()) {
            System.out.println(tokens.containsKey(t));
        }

        sortedTokens.putAll(tokens);

        for (String t : sortedTokens.keySet()) {
            System.out.println(sortedTokens.containsKey(t));
        }
    }
}

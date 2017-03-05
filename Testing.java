
import algorithm.ShortestPaths;
import graph.*;
import graphgui.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Locale;

public class Testing {

    public static void main(String[] args) throws IOException {
        String source = "A B\nA B -2\nB A -5";
        String source2 = "A B C D E\nA B 5\nA C 10\nB D 1\nD E 3\nB E 7\nA D 40\nB C 20";
        Graph graph = GraphFactory.createListGraph(GraphIO.read(new StringReader(source2)));

        Locale.setDefault(Locale.US);
        System.out.println(GraphFactory.stringify(graph));
        // new GraphFrame(graph);

        System.out.println(Arrays.toString(ShortestPaths.BellmanFord(graph, 0)));
    }
}

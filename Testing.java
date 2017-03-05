
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
        String clothing = "undershorts pants belt shirt tie jacket shoes socks watch\n" +
                "undershorts shoes\n" +
                "undershorts pants\n" +
                "socks shoes\n" +
                "pants shoes\n" +
                "pants belt\n" +
                "shirt belt\n" +
                "belt jacket\n" +
                "shirt tie\n" +
                "tie jacket";
        GraphIterable<?> itr = GraphIO.read(new StringReader(source2));
        Graph graph = GraphFactory.createListGraph(itr);
        Graph graphm = GraphFactory.createMatrixGraph(itr);
        Graph c = GraphFactory.createMatrixGraph(GraphIO.read(new StringReader(clothing)));

        Locale.setDefault(Locale.US);
        System.out.println(GraphFactory.stringify(graph));
        System.out.println(GraphFactory.stringify(c));
        new GraphFrame(c);

        System.out.println(Arrays.toString(ShortestPaths.BellmanFord(graph, 0)));
        System.out.println(Arrays.toString(ShortestPaths.BellmanFord(graphm, 0)));
    }
}

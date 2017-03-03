
import graph.*;
import java.io.IOException;
import java.io.StringReader;

public class Testing {

    public static void main(String[] args) throws IOException {
        String source = "A B\nA B -2\nB A -5";
        for (WeightedEdge e : GraphFactory.createMatrixGraph(new GraphReader(new StringReader(source)))) {
            System.out.println(e);
        }
    }
}

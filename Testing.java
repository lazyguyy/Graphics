
import graph.*;
import graphgui.*;
import java.io.IOException;
import java.io.StringReader;

public class Testing {

    public static void main(String[] args) throws IOException {
        String source = "A B\nA B -2\nB A -5";
        System.out.println(GraphFactory.createMatrixGraph(GraphIO.read(new StringReader(source))));
        GraphFrame g = new GraphFrame(GraphFactory.createListGraph(GraphIO.read(new StringReader(source))));
    }
}

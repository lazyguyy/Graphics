
package graph;

public abstract class GraphFactory {

    public static Graph createMatrixGraph(GraphIterable itr) {
        return new MatrixGraph(itr);
    }
    public static Graph createListGraph(GraphIterable itr) {
        return new ListGraph(itr);
    }
}


package graph;

import java.util.List;

public abstract class GraphFactory {

    protected List<String> vertices;
    protected int vertexCount;

    public static Graph createMatrixGraph(GraphIterable itr) {

    }
    public static Graph createListGraph(GraphIterable itr) {

    }
    public abstract void addVertex(String label);
    public abstract void addVertices(int count);
    public abstract void addEdge(int from, int to);
    public abstract void addEdge(int from, int to, int weight);
    public abstract Graph create();
}

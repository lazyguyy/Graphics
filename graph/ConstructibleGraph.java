
package graph;

public interface ConstructibleGraph extends Graph {
    public abstract void relabelVertex(int vertex, String label);
    public abstract void addEdge(int from, int to);
    public abstract void addEdge(int from, int to, int weight);
    public abstract Graph create();
}

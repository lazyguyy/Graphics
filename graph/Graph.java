
package graph;

public interface Graph extends GraphIterable {
    public boolean hasEdge(int from, int to);
    public WeightedEdge edge(int from, int to);
    public Iterable<WeightedEdge> adjacentEdges(int from);
}

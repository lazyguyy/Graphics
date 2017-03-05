
package graph;

public interface Graph<I> extends GraphIterable<I> {
    public boolean hasEdge(int from, int to);
    public WeightedEdge edge(int from, int to);

    public Iterable<WeightedEdge> adjacency(int from);
}

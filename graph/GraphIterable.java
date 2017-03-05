
package graph;

public interface GraphIterable<I> extends Iterable<WeightedEdge> {
    public I info();
    public int vertexCount();
}

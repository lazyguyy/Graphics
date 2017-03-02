
package graph;

public interface GraphIterable extends Iterable<WeightedEdge> {
    public String[] vertices();
    public int vertexCount();
}

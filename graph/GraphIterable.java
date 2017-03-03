
package graph;

public interface GraphIterable extends Iterable<WeightedEdge> {
    public String[] vertexNames();
    public int vertexCount();
}

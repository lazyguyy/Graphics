
package graph;

public class WeightedEdge {
    public final int from;
    public final int to;
    public final double weight;

    public WeightedEdge(int from, int to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d) -> %f", from, to, weight);
    }
}
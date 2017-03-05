
package graph;

import java.util.Iterator;
import java.util.stream.IntStream;

class MatrixGraph<I> extends AbstractGraph<I> {

    private final double[] graph;
    private final int n;

    public MatrixGraph(GraphIterable<I> itr) {
        super(itr);
        this.n = itr.vertexCount();
        this.graph = new double[n * n];

        for (int i = 0; i < n; i++)
            for (int j = i * n; j < i * n + n; j++)
                graph[j] = Double.POSITIVE_INFINITY;

        for (WeightedEdge e : itr) {
            graph[e.from * n + e.to] = e.weight;
        }
    }

    @Override
    public boolean hasEdge(int from, int to) {
        return Double.isFinite(graph[from * n + to]);
    }

    @Override
    public WeightedEdge edge(int from, int to) {
        return new WeightedEdge(from, to, graph[from * n + to]);
    }

    @Override
    protected Iterator<WeightedEdge> adjacencyIterator(int from) {
        return IntStream.range(0, vertexCount())
                        .filter(to -> hasEdge(from, to))
                        .mapToObj(to -> new WeightedEdge(from, to, graph[from * n + to]))
                        .iterator();
    }
}

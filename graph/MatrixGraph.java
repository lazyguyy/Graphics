
package graph;

import java.util.Iterator;
import java.util.NoSuchElementException;

class MatrixGraph extends AbstractGraph {

    private final double[][] graph;

    public MatrixGraph(GraphIterable itr) {
        super(itr);
        int n = itr.vertexCount();
        graph = new double[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                graph[i][j] = Double.POSITIVE_INFINITY;

        for (WeightedEdge e : itr) {
            graph[e.from][e.to] = e.weight;
        }
    }

    @Override
    public boolean hasEdge(int from, int to) {
        return Double.isFinite(graph[from][to]);
    }

    @Override
    public WeightedEdge edge(int from, int to) {
        return new WeightedEdge(from, to, graph[from][to]);
    }

    @Override
    protected Iterator<WeightedEdge> adjacencyIterator(int from) {
        return new AdjacencyIterator(from);
    }

    private class AdjacencyIterator implements Iterator<WeightedEdge> {

        private final int from;
        private int to;

        public AdjacencyIterator(int from) {
            this.from = from;
            this.to = 0;
            while (to < vertexCount() && !hasEdge(from, to)) to++;
        }

        @Override
        public boolean hasNext() {
            return to < vertexCount();
        }

        @Override
        public WeightedEdge next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements");
            WeightedEdge edge = new WeightedEdge(from, to, graph[from][to]);
            to++;
            while (to < vertexCount() && !hasEdge(from, to)) to++;
            return edge;
        }
    }
}


package graph;

import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class AbstractGraph implements Graph {

    private final String[] vertexNames;

    public AbstractGraph(GraphIterable itr) {
        vertexNames = itr.vertexNames();
    }

    @Override
    public String[] vertexNames() {
        return vertexNames;
    }

    @Override
    public int vertexCount() {
        return vertexNames.length;
    }

    protected abstract Iterator<WeightedEdge> adjacencyIterator(int from);

    @Override
    public Iterable<WeightedEdge> adjacency(int from) {
        return new Adjacency(from);
    }

    private class Adjacency implements Iterable<WeightedEdge> {

        private final int from;

        public Adjacency(int from) {
            this.from = from;
        }

        @Override
        public Iterator<WeightedEdge> iterator() {
            return adjacencyIterator(from);
        }
    }

    @Override
    public Iterator<WeightedEdge> iterator() {
        return new GraphIterator();
    }

    private class GraphIterator implements Iterator<WeightedEdge> {

        private Iterator<WeightedEdge> itr;
        private int from;

        public GraphIterator() {
            this.from = 0;
            this.itr = adjacency(from).iterator();
            advance();
        }

        private void advance() {
            while (from < vertexCount() - 1 && !itr.hasNext())
                itr = adjacency(++from).iterator();
        }

        @Override
        public boolean hasNext() {
            return itr.hasNext();
        }

        @Override
        public WeightedEdge next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements");
            WeightedEdge edge = itr.next();
            advance();
            return edge;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("     ");
        int n = vertexCount();
        for (int i = 0; i < n; i++) {
            sb.append(String.format("%8s ", vertexNames()[i]));
        }
        sb.append("\n");
        for (int i = 0; i < n; i++) {
            sb.append(String.format("%4s ", vertexNames()[i]));

            String[] edges = new String[n];
            for (WeightedEdge e : adjacency(i)) {
                edges[e.to] = String.format("%8s ", e.weight);
            }
            for (String s : edges) {
                sb.append(s == null ? "       - " : s);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

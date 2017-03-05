
package graph;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class AbstractGraph<I> implements Graph<I>, Serializable {

    private final I info;
    private final int vertexCount;

    public AbstractGraph(GraphIterable<I> itr) {
        info = itr.info();
        vertexCount = itr.vertexCount();
    }

    @Override
    public I info() {
        return info;
    }

    @Override
    public int vertexCount() {
        return vertexCount;
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
}

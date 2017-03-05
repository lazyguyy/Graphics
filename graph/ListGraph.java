
package graph;

import java.util.Iterator;
import java.util.NoSuchElementException;

class ListGraph<I> extends AbstractGraph<I> {

    private final Node[] graph;

    public ListGraph(GraphIterable<I> itr) {
        super(itr);
        int n = itr.vertexCount();
        graph = new Node[n];

        for (WeightedEdge e : itr) {
            graph[e.from] = new Node(graph[e.from], e.to, e.weight);
        }
    }

    private Node find(int from, int to) {
        Node itr = graph[from];
        while (itr != null) {
            if (itr.to == to)
                return itr;
            itr = itr.next;
        }
        return null;
    }

    @Override
    public boolean hasEdge(int from, int to) {
        return find(from, to) != null;
    }

    @Override
    public WeightedEdge edge(int from, int to) {
        Node n = find(from, to);
        return new WeightedEdge(from, to, n == null ? Double.POSITIVE_INFINITY : n.weight);
    }

    @Override
    protected Iterator<WeightedEdge> adjacencyIterator(int from) {
        return new ListGraphIterator(from);
    }

    private class ListGraphIterator implements Iterator<WeightedEdge> {

        private Node itr;
        private final int from;

        public ListGraphIterator(int from) {
            this.from = from;
            this.itr = graph[from];
        }

        @Override
        public boolean hasNext() {
            return itr != null;
        }

        @Override
        public WeightedEdge next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements");
            WeightedEdge edge = new WeightedEdge(from, itr.to, itr.weight);
            itr = itr.next;
            return edge;
        }
    }

    public static class Node {
        public final Node next;
        public final int to;
        public final double weight;

        public Node(Node next, int to, double weight) {
            this.next = next;
            this.to = to;
            this.weight = weight;
        }
    }
}

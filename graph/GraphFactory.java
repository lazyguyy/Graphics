
package graph;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract class GraphFactory {

    public static Graph createMatrixGraph(GraphIterable itr) {
        return ConstructibleGraph.fromIterable(itr, MatrixGraph::new);
    }
    public static Graph createListGraph(GraphIterable itr) {
        return ConstructibleGraph.fromIterable(itr, ListGraph::new);
    }

    // todo rename dis
    private static abstract class ConstructibleGraph implements Graph {

        // "ist gut nein?" - Uter from "The Simpsons"
        public static <G extends ConstructibleGraph> ConstructibleGraph
                    fromIterable(GraphIterable itr, Function<GraphIterable, G> constructor) {
            return constructor.apply(itr).addEdges(itr);
        }
        public void addEdge(WeightedEdge edge) {
            addEdge(edge.from, edge.to, edge.weight);
        }
        public abstract void addEdge(int from, int to, double weight);
        public ConstructibleGraph addEdges(GraphIterable itr) {
            for (WeightedEdge edge : itr)
                addEdge(edge);
            return this;
        }

        @Override
        public Iterator<WeightedEdge> iterator() {
            return new GraphIterator();
        }
        
        // TODO fix dis shit immediately
        private class GraphIterator implements Iterator<WeightedEdge> {

            private Iterator<WeightedEdge> itr;
            private int from;

            public GraphIterator() {
                from = 0;
                itr = adjacentEdges(from).iterator();
                while (from < vertexCount() - 1 && !itr.hasNext()) {
                    itr = adjacentEdges(++from).iterator();
                }
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
                while (from < vertexCount() - 1 && !itr.hasNext()) {
                    itr = adjacentEdges(++from).iterator();
                }
                return edge;
            }
        }
    }

    private static class ListGraph extends ConstructibleGraph {

        // TODO move vertexnames to ConstructibleGraph
        private final String[] vertexNames;
        private final Node[] graph;

        private ListGraph(GraphIterable itr) {
            int n = itr.vertexCount();
            vertexNames = itr.vertexNames();
            graph = new Node[n];
        }

        @Override
        public void addEdge(int from, int to, double weight) {
            graph[from] = new Node(graph[from], to, weight);
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
        public Iterable<WeightedEdge> adjacentEdges(int from) {
            return new AdjacencyIterator(from);
        }

        // TODO move this to ConstructibleGraph using delegates for hasNext and next
        private class AdjacencyIterator implements Iterable<WeightedEdge>, Iterator<WeightedEdge> {

            private Node itr;
            private final int from;

            public AdjacencyIterator(int from) {
                this.from = from;
                this.itr = graph[from];
            }

            @Override
            public Iterator<WeightedEdge> iterator() {
                return this;
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

        @Override
        public String[] vertexNames() {
            return vertexNames;
        }

        @Override
        public int vertexCount() {
            return vertexNames.length;
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

    private static class MatrixGraph extends ConstructibleGraph {

        private final double[][] graph;
        private final String[] vertexNames;

        private MatrixGraph(GraphIterable itr) {
            int n = itr.vertexCount();
            vertexNames = itr.vertexNames();
            graph = new double[n][n];

            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    graph[i][j] = Double.POSITIVE_INFINITY;
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
        public Iterable<WeightedEdge> adjacentEdges(int from) {
            return new AdjacencyIterator(from);
        }

        private class AdjacencyIterator implements Iterable<WeightedEdge>, Iterator<WeightedEdge> {

            private final int from;
            private int to;

            public AdjacencyIterator(int from) {
                this.from = from;
                to = 0;
                while (to < vertexCount() && !hasEdge(from, to)) to++;
            }

            @Override
            public Iterator<WeightedEdge> iterator() {
                return this;
            }

            @Override
            public boolean hasNext() {
                return to != vertexCount();
            }

            @Override
            public WeightedEdge next() {
                if (!hasNext())
                    throw new NoSuchElementException("No more elements");
                WeightedEdge edge = new WeightedEdge(from, to, graph[from][to]);
                while (to < vertexCount() && !hasEdge(from, to)) to++;
                return edge;
            }
        }

        @Override
        public String[] vertexNames() {
            return vertexNames;
        }

        @Override
        public int vertexCount() {
            return vertexNames.length;
        }

        @Override
        public void addEdge(int from, int to, double weight) {
            graph[from][to] = weight;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("     ");
            int n = vertexCount();
            for (int i = 0; i < n; i++) {
                sb.append(String.format("%8s ", vertexNames[i]));
            }
            sb.append("\n");
            for (int i = 0; i < n; i++) {
                sb.append(String.format("%4s ", vertexNames[i]));
                for (int j = 0; j < n; j++) {
                    sb.append(hasEdge(i, j) ? String.format("%8s ", graph[i][j]) : "       - ");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }
}

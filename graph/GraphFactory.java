
package graph;

public abstract class GraphFactory {

    public static <I> Graph createMatrixGraph(GraphIterable<I> itr) {
        return new MatrixGraph(itr);
    }
    public static <I> Graph createListGraph(GraphIterable<I> itr) {
        return new ListGraph(itr);
    }

    public static <I extends VertexNameInfo> String stringify(GraphIterable<I> itr) {
        StringBuilder sb = new StringBuilder("         ");
        String[] vertexNames = itr.info().vertexNames();
        int n = itr.vertexCount();
        double[] edges = new double[n * n];

        for (int i = 0; i < n * n; i++) {
            edges[i] = Double.POSITIVE_INFINITY;
        }

        for (WeightedEdge e : itr) {
            edges[e.from * n + e.to] = e.weight;
        }

        for (int i = 0; i < n; i++) {
            sb.append(String.format("%8.8s ", vertexNames[i]));
        }
        sb.append("\n");
        for (int i = 0; i < n; i++) {
            sb.append(String.format("%8.8s ", vertexNames[i]));

            for (int j = i * n; j < i * n + n; j++) {
                sb.append(Double.isFinite(edges[j]) ? String.format("%8.8s ", edges[j]) : "       - ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

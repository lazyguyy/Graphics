
package algorithm;

import graph.Graph;
import graph.WeightedEdge;

public class ShortestPaths {

    public static double[] BellmanFord(Graph<?> graph, int source) {

        int n = graph.vertexCount();
        double[] distances = new double[n];

        for (int i = 0; i < n; i++)
            distances[i] = i == source ? 0 : Double.POSITIVE_INFINITY;

        for (int i = 0; i < n - 1; i++)
            for (WeightedEdge e : graph)
                distances[e.to] = Math.min(distances[e.to], distances[e.from] + e.weight);

        for (WeightedEdge e : graph)
            if (distances[e.to] > distances[e.from] + e.weight)
                return null;

        return distances;
    }

}

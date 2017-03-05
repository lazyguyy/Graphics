
package graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphIO {

    public static GraphIterable<VertexNameInfo> read(Reader input) throws IOException {

        int vertexCount;
        String[] vertices;
        ArrayList<WeightedEdge> edges;

        try (BufferedReader reader = new BufferedReader(input)) {

            String line = reader.readLine();
            while (line != null && line.isEmpty())
                line = reader.readLine();

            if (line == null)
                throw new IOException("Empty file");

            if (line.startsWith("#")) {
                vertexCount = Integer.parseInt(line.substring(1).trim());
                vertices = IntStream.range(0, vertexCount).mapToObj(Integer::toString).toArray(String[]::new);
            } else {
                vertices = line.trim().split("\\s+");
                vertexCount = vertices.length;
            }

            Map<String, Integer> vertexNames = IntStream.range(0, vertexCount).mapToObj(i -> i)
                                   .collect(Collectors.toMap(i -> vertices[i], i -> i));

            edges = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                String[] info = line.trim().split("\\s+");
                if (info.length < 2) {
                    throw new IOException("Invalid input format");
                }

                double weight = info.length > 2 ? Double.parseDouble(info[2]) : 1;
                edges.add(new WeightedEdge(vertexNames.get(info[0]), vertexNames.get(info[1]), weight));
            }
        }

        return new NamedVertexEdgeList(vertexCount, edges, vertices);
    }

    private static void write(Writer output, GraphIterable<? extends VertexNameInfo> itr) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(output)) {
            for (String v : itr.info().vertexNames())
                writer.append(v + " ");
            writer.append("\n");
            for (WeightedEdge e : itr)
                writer.append(String.format("%s %s %s\n", e.from, e.to, e.weight));
        }
    }

    private static class NamedVertexEdgeList implements GraphIterable<VertexNameInfo> {

        private final Iterable<WeightedEdge> edges;
        private final int vertexCount;
        private String[] vertexNames;

        public NamedVertexEdgeList(int vertexCount, Iterable<WeightedEdge> edges, String[] vertexNames) {
            this.edges = edges;
            this.vertexCount = vertexCount;
            this.vertexNames = vertexNames;
        }

        @Override
        public VertexNameInfo info() {
            return () -> vertexNames;
        }

        @Override
        public int vertexCount() {
            return vertexCount;
        }

        @Override
        public Iterator<WeightedEdge> iterator() {
            return edges.iterator();
        }
    }
}

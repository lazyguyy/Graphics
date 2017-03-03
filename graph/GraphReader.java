
package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphReader implements GraphIterable, Iterator<WeightedEdge> {

    private final int vertexCount;
    private final String[] vertices;
    private final BufferedReader reader;
    private final Map<String, Integer> vertexNames;
    private String nextLine;

    public GraphReader(Reader inputStream) throws IOException {
        reader = new BufferedReader(inputStream);
        String firstLine = reader.readLine();
        if (firstLine.startsWith("#")) {
            vertexCount = Integer.parseInt(firstLine.substring(1).trim());
            vertices = IntStream.range(0, vertexCount).mapToObj(Integer::toString).toArray(String[]::new);
        } else {
            vertices = firstLine.split("\\s+");
            vertexCount = vertices.length;
        }
        vertexNames = IntStream.range(0, vertexCount).mapToObj(i -> i)
                               .collect(Collectors.toMap(i -> vertices[i], i -> i));
        nextLine = read();
    }

    private String read() throws IOException {
        nextLine = reader.readLine();
        while (nextLine != null && nextLine.isEmpty())
            nextLine = reader.readLine();
        return nextLine;
    }

    private void closeStream() {
        try {
            reader.close();
        } catch (IOException ex) {}
    }

    @Override
    public boolean hasNext() {
        return nextLine != null;
    }

    @Override
    public WeightedEdge next() {
        if (!hasNext())
            throw new NoSuchElementException("No more elements");

        String[] info = nextLine.split("\\s+");
        if (info.length < 2) {
            closeStream();
            throw new NoSuchElementException("Invalid input format");
        }

        try {
            nextLine = read();
        } catch (IOException ex) {
            nextLine = null;
        }
        if (nextLine == null)
            closeStream();

        double weight = info.length > 2 ? Double.parseDouble(info[2]) : 1;
        return new WeightedEdge(vertexNames.get(info[0]), vertexNames.get(info[1]), weight);
    }

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public String[] vertexNames() {
        return vertices;
    }

    @Override
    public Iterator<WeightedEdge> iterator() {
        return this;
    }
}

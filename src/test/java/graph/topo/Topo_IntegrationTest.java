package graph.topo;

import graph.model.Graph;
import graph.metrics.Metrics;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Topo_IntegrationTest {

    private Graph loadGraph(String file) throws Exception {
        InputStream is = getClass().getResourceAsStream("/data/" + file);
        assertNotNull(is, "File not found: " + file);
        JSONObject obj = new JSONObject(new JSONTokener(is));

        int n = obj.getInt("n");
        JSONArray edges = obj.getJSONArray("edges");
        Graph g = new Graph(n, true); // ✅ Directed = true
        for (int i = 0; i < edges.length(); i++) {
            JSONObject e = edges.getJSONObject(i);
            g.addEdge(e.getInt("u"), e.getInt("v"), e.getInt("w"));
        }
        return g;
    }

    @Test
    public void testTopologicalSortOnDAGs() throws Exception {
        String[] files = {"small1.json", "medium1.json", "large1.json"};
        for (String file : files) {
            Graph g = loadGraph(file);
            Metrics metrics = new Metrics();

            // ✅ вызываем статический метод
            List<Integer> order = TopologicalSort.kahn(g, metrics);

            assertNotNull(order);
            assertEquals(g.n(), order.size(), file + " → invalid topo order size");

            System.out.println(file + " → Topological order: " + order);
            System.out.println("Pushes: " + metrics.pushes + ", Pops: " + metrics.pops +
                    ", Time (ms): " + metrics.timeNanos / 1_000_000.0);
        }
    }
}
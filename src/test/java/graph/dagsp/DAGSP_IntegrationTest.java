package graph.dagsp;

import graph.model.Graph;
import graph.metrics.Metrics;
import graph.topo.TopologicalSort;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DAGSP_IntegrationTest {

    private Graph loadGraph(String file) throws Exception {
        InputStream is = getClass().getResourceAsStream("/data/" + file);
        assertNotNull(is, "File not found: " + file);
        JSONObject obj = new JSONObject(new JSONTokener(is));

        int n = obj.getInt("n");
        JSONArray edges = obj.getJSONArray("edges");
        Graph g = new Graph(n, true); // Directed = true
        for (int i = 0; i < edges.length(); i++) {
            JSONObject e = edges.getJSONObject(i);
            g.addEdge(e.getInt("u"), e.getInt("v"), e.getInt("w"));
        }
        return g;
    }

    @Test
    public void testDAGShortestPaths() throws Exception {
        String[] files = {"small1.json", "medium1.json", "large1.json"};
        for (String file : files) {
            InputStream is = getClass().getResourceAsStream("/data/" + file);
            JSONObject obj = new JSONObject(new JSONTokener(is));
            int src = obj.getInt("source");

            Graph g = loadGraph(file);
            Metrics metricsTopo = new Metrics();
            Metrics metricsSP = new Metrics();

            // 🔹 Получаем топологический порядок
            List<Integer> topoOrder = TopologicalSort.kahn(g, metricsTopo);

            // 🔹 Вызываем метод run
            DAGShortestPath.Result res = DAGShortestPath.run(g, topoOrder, src, metricsSP);

            // 🔹 Проверяем
            assertNotNull(res);
            assertEquals(g.n(), res.dist.length, file + " → invalid distance array size");

            System.out.println("File: " + file);
            System.out.println("Topo order: " + topoOrder);
            System.out.println("Source: " + src);
            System.out.println("Shortest distances: ");
            for (int i = 0; i < res.dist.length; i++) {
                System.out.println("  to " + i + " = " + (res.dist[i] == Long.MAX_VALUE ? "∞" : res.dist[i]));
            }

            System.out.println("Relaxations: " + metricsSP.relaxations +
                    ", Time (ms): " + metricsSP.timeNanos / 1_000_000.0);
            System.out.println("-----------------------------------------");
        }
    }
}
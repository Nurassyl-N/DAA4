package graph.scc;

import graph.model.Graph;
import graph.metrics.Metrics;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class SCC_IntegrationTest {

    /** Загружает граф из JSON */
    private Graph loadGraph(String file) throws Exception {
        InputStream is = getClass().getResourceAsStream("/data/" + file);
        assertNotNull(is, "File not found: " + file);
        JSONObject obj = new JSONObject(new JSONTokener(is));

        int n = obj.getInt("n");
        JSONArray edges = obj.getJSONArray("edges");
        Graph g = new Graph(n, true); // directed
        for (int i = 0; i < edges.length(); i++) {
            JSONObject e = edges.getJSONObject(i);
            g.addEdge(e.getInt("u"), e.getInt("v"), e.getInt("w"));
        }
        return g;
    }

    /** Проверка корректности Kosaraju SCC на всех датасетах */
    @Test
    public void testSCCOnAllDatasets() throws Exception {
        String[] files = {
                "small1.json", "small2.json", "small3.json",
                "medium1.json", "medium2.json", "medium3.json",
                "large1.json", "large2.json", "large3.json"
        };

        for (String file : files) {
            Graph g = loadGraph(file);
            Metrics metrics = new Metrics();

            // ✅ Вызываем статический метод run
            KosarajuSCC.Result result = KosarajuSCC.run(g, metrics);

            // Проверки
            assertNotNull(result, "Result should not be null");
            assertTrue(result.compCount > 0, "At least one component expected in " + file);
            assertEquals(result.comps.size(), result.compCount, "Mismatch between compCount and comps.size()");
            for (int cid = 0; cid < result.comps.size(); cid++) {
                assertFalse(result.comps.get(cid).isEmpty(), "Empty component found");
            }

            // Вывод информации
            System.out.println("📘 File: " + file);
            System.out.println("SCC count: " + result.compCount);
            for (int i = 0; i < result.comps.size(); i++) {
                System.out.println("  Component " + (i + 1) + ": " + result.comps.get(i));
            }
            System.out.println("DFS visits: " + metrics.dfsVisits + ", DFS edges: " + metrics.dfsEdges +
                    ", Time (ms): " + metrics.timeNanos / 1_000_000.0);
            System.out.println("───────────────────────────────────────────────");
        }
    }
}
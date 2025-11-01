package app;

import graph.model.Graph;
import graph.model.Edge;
import graph.metrics.Metrics;
import graph.scc.KosarajuSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;
import graph.dagsp.DAGLongestPath;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Run:
 *  java -cp target/classes:~/.m2/repository/org/json/json/20231013/json-20231013.jar app.Main data/small1.json out_small1.json
 *
 * If no args: uses data/input.json and out.json
 */
public class Main {

    public static void main(String[] args) {
        String inPath  = args.length >= 1 ? args[0] : "data/input.json";
        String outPath = args.length >= 2 ? args[1] : "out.json";

        try (FileInputStream fis = new FileInputStream("src/main/resources/data/input.json")) {
            JSONObject root = new JSONObject(new JSONTokener(fis));

            boolean directed = root.optBoolean("directed", true);
            int n = root.getInt("n");
            JSONArray E = root.getJSONArray("edges");
            int source = root.optInt("source", 0);
            String weightModel = root.optString("weight_model", "edge"); // we use "edge"

            Graph g = new Graph(n, directed);
            for (int i = 0; i < E.length(); i++) {
                JSONObject e = E.getJSONObject(i);
                g.addEdge(e.getInt("u"), e.getInt("v"), e.getInt("w"));
            }

            // 1) SCC (Kosaraju)
            Metrics sccM = new Metrics();
            KosarajuSCC.Result sccRes = KosarajuSCC.run(g, sccM);

            // 2) Condensation DAG
            Graph dag = Graph.condensation(g, sccRes.compId, sccRes.compCount);

            // 3) Topological order on condensation DAG
            Metrics topoM = new Metrics();
            List<Integer> topo = TopologicalSort.kahn(dag, topoM);

            // 4) DAG shortest & longest from source's component
            int sourceComp = sccRes.compId[source];
            Metrics spM = new Metrics(), lpM = new Metrics();

            DAGShortestPath.Result sp = DAGShortestPath.run(dag, topo, sourceComp, spM);
            DAGLongestPath.Result  lp = DAGLongestPath.run(dag, topo, sourceComp, lpM);

            // Build output JSON
            JSONObject out = new JSONObject();

            // SCC section
            JSONObject sccObj = new JSONObject();
            sccObj.put("components_count", sccRes.compCount);
            JSONArray compsArr = new JSONArray();
            for (List<Integer> comp : sccRes.comps) {
                JSONArray arr = new JSONArray();
                for (int v : comp) arr.put(v);
                compsArr.put(arr);
            }
            sccObj.put("components", compsArr);
            sccObj.put("metrics", metricsJson(sccM));

            // Condensation DAG stats
            JSONObject dagObj = new JSONObject();
            dagObj.put("nodes", dag.n());
            dagObj.put("edges", dag.edges().size());

            // Topo section
            JSONObject topoObj = new JSONObject();
            JSONArray topoArr = new JSONArray();
            for (int v : topo) topoArr.put(v);
            topoObj.put("order", topoArr);
            topoObj.put("metrics", metricsJson(topoM));

            // Shortest paths
            JSONObject spObj = new JSONObject();
            spObj.put("source_component", sourceComp);
            spObj.put("dist", toArray(sp.dist));
            spObj.put("metrics", metricsJson(spM));

            // Longest paths (critical path lengths)
            JSONObject lpObj = new JSONObject();
            lpObj.put("source_component", sourceComp);
            lpObj.put("best", toArray(lp.best));
            lpObj.put("metrics", metricsJson(lpM));

            // Example: reconstruct one shortest and one longest path to every node
            JSONArray paths = new JSONArray();
            for (int t = 0; t < dag.n(); t++) {
                JSONObject p = new JSONObject();
                p.put("target_component", t);
                p.put("shortest_path", toArray(sp.buildPathTo(t)));
                p.put("longest_path",  toArray(lp.buildPathTo(t)));
                paths.put(p);
            }

            out.put("input_meta", new JSONObject()
                    .put("directed", directed)
                    .put("n", n)
                    .put("weight_model", weightModel)
                    .put("source", source));
            out.put("scc", sccObj);
            out.put("condensation_dag", dagObj);
            out.put("topological_sort", topoObj);
            out.put("dag_shortest_paths", spObj);
            out.put("dag_longest_paths", lpObj);
            out.put("paths_examples", paths);

            // write file
            Files.writeString(Path.of(outPath), out.toString(2));
            System.out.println("Done. Output saved to " + outPath);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static JSONArray toArray(long[] a) {
        JSONArray arr = new JSONArray();
        for (long v : a) arr.put(v);
        return arr;
    }
    private static JSONArray toArray(List<Integer> list) {
        JSONArray arr = new JSONArray();
        for (int v : list) arr.put(v);
        return arr;
    }
    private static JSONObject metricsJson(graph.metrics.Metrics m) {
        return new JSONObject()
                .put("dfsVisits", m.dfsVisits)
                .put("dfsEdges", m.dfsEdges)
                .put("pushes", m.pushes)
                .put("pops", m.pops)
                .put("relaxations", m.relaxations)
                .put("time_ms", m.timeNanos / 1_000_000.0);
    }
}
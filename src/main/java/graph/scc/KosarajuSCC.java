package graph.scc;

import graph.model.Graph;
import graph.model.Edge;
import graph.metrics.Metrics;

import java.util.*;

public class KosarajuSCC {

    /** Result holder */
    public static class Result {
        public final int compCount;
        public final int[] compId;             // compId[v] in [0..compCount-1]
        public final List<List<Integer>> comps; // vertices per component

        public Result(int compCount, int[] compId, List<List<Integer>> comps) {
            this.compCount = compCount;
            this.compId = compId;
            this.comps = comps;
        }
    }

    /** Computes SCC using Kosaraju (two DFS passes with stack). Counts metrics.dfsVisits/dfsEdges. */
    public static Result run(Graph g, Metrics metrics) {
        long t0 = System.nanoTime();

        int n = g.n();
        List<List<Edge>> adj = g.adj();

        boolean[] vis = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();

        // 1) DFS on original graph, push vertices to stack after exploring
        for (int v = 0; v < n; v++) {
            if (!vis[v]) dfs1(v, adj, vis, stack, metrics);
        }

        // 2) DFS on reversed graph in stack order -> components
        Graph gr = g.reverse();
        Arrays.fill(vis, false);
        int[] compId = new int[n];
        Arrays.fill(compId, -1);
        List<List<Integer>> comps = new ArrayList<>();
        int cid = 0;

        while (!stack.isEmpty()) {
            int v = stack.pop();
            if (!vis[v]) {
                List<Integer> comp = new ArrayList<>();
                dfs2(v, gr.adj(), vis, comp, metrics);
                for (int u : comp) compId[u] = cid;
                comps.add(comp);
                cid++;
            }
        }

        long t1 = System.nanoTime();
        metrics.timeNanos += (t1 - t0);
        return new Result(cid, compId, comps);
    }

    private static void dfs1(int v, List<List<Edge>> adj, boolean[] vis, Deque<Integer> stack, Metrics m) {
        vis[v] = true;
        m.dfsVisits++;
        for (Edge e : adj.get(v)) {
            m.dfsEdges++;
            if (!vis[e.v]) dfs1(e.v, adj, vis, stack, m);
        }
        stack.push(v);
    }

    private static void dfs2(int v, List<List<Edge>> adj, boolean[] vis, List<Integer> comp, Metrics m) {
        vis[v] = true;
        comp.add(v);
        m.dfsVisits++;
        for (Edge e : adj.get(v)) {
            m.dfsEdges++;
            if (!vis[e.v]) dfs2(e.v, adj, vis, comp, m);
        }
    }
}
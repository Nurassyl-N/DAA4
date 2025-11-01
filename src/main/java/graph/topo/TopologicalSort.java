package graph.topo;

import graph.model.Graph;
import graph.model.Edge;
import graph.metrics.Metrics;

import java.util.*;

public class TopologicalSort {

    /** Kahn's algorithm: returns topological order of DAG; counts pushes/pops. */
    public static List<Integer> kahn(Graph dag, Metrics metrics) {
        long t0 = System.nanoTime();

        int n = dag.n();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++)
            for (Edge e : dag.adj().get(u))
                indeg[e.v]++;

        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indeg[i] == 0) { q.add(i); metrics.pushes++; }
        }

        List<Integer> order = new ArrayList<>(n);
        while (!q.isEmpty()) {
            int u = q.remove(); metrics.pops++;
            order.add(u);
            for (Edge e : dag.adj().get(u)) {
                if (--indeg[e.v] == 0) { q.add(e.v); metrics.pushes++; }
            }
        }

        long t1 = System.nanoTime();
        metrics.timeNanos += (t1 - t0);
        if (order.size() != n) throw new IllegalStateException("Graph is not a DAG (cycle detected in condensation).");
        return order;
    }
}
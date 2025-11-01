package graph.dagsp;

import graph.model.Graph;
import graph.model.Edge;
import graph.metrics.Metrics;

import java.util.*;

public class DAGShortestPath {

    public static class Result {
        public final int sourceComp;
        public final long[] dist;
        public final int[] parent; // parent comp in shortest path tree

        public Result(int sourceComp, long[] dist, int[] parent) {
            this.sourceComp = sourceComp;
            this.dist = dist;
            this.parent = parent;
        }

        public List<Integer> buildPathTo(int targetComp) {
            if (dist[targetComp] == Long.MAX_VALUE) return Collections.emptyList();
            List<Integer> path = new ArrayList<>();
            for (int cur = targetComp; cur != -1; cur = parent[cur]) path.add(cur);
            Collections.reverse(path);
            return path;
        }
    }

    /** Single-source shortest paths on DAG using topological order; counts relaxations. */
    public static Result run(Graph dag, List<Integer> topoOrder, int sourceComp, Metrics metrics) {
        long t0 = System.nanoTime();

        int n = dag.n();
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        Arrays.fill(parent, -1);

        dist[sourceComp] = 0;

        for (int u : topoOrder) {
            if (dist[u] == Long.MAX_VALUE) continue;
            for (Edge e : dag.adj().get(u)) {
                long nd = dist[u] + e.w;
                metrics.relaxations++;
                if (nd < dist[e.v]) {
                    dist[e.v] = nd;
                    parent[e.v] = u;
                }
            }
        }

        long t1 = System.nanoTime();
        metrics.timeNanos += (t1 - t0);
        return new Result(sourceComp, dist, parent);
    }
}
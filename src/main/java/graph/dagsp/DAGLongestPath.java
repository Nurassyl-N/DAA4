package graph.dagsp;

import graph.model.Graph;
import graph.model.Edge;
import graph.metrics.Metrics;

import java.util.*;

public class DAGLongestPath {

    public static class Result {
        public final int sourceComp;
        public final long[] best;
        public final int[] parent;

        public Result(int sourceComp, long[] best, int[] parent) {
            this.sourceComp = sourceComp;
            this.best = best;
            this.parent = parent;
        }

        public List<Integer> buildPathTo(int targetComp) {
            if (best[targetComp] == Long.MIN_VALUE/4) return Collections.emptyList();
            List<Integer> path = new ArrayList<>();
            for (int cur = targetComp; cur != -1; cur = parent[cur]) path.add(cur);
            Collections.reverse(path);
            return path;
        }
    }

    /** Longest path on DAG via max-DP along topo order; counts relaxations. */
    public static Result run(Graph dag, List<Integer> topoOrder, int sourceComp, Metrics metrics) {
        long t0 = System.nanoTime();

        int n = dag.n();
        long NEG_INF = Long.MIN_VALUE / 4;
        long[] best = new long[n];
        int[] parent = new int[n];
        Arrays.fill(best, NEG_INF);
        Arrays.fill(parent, -1);

        best[sourceComp] = 0;

        for (int u : topoOrder) {
            if (best[u] == NEG_INF) continue;
            for (Edge e : dag.adj().get(u)) {
                long nd = best[u] + e.w;
                metrics.relaxations++;
                if (nd > best[e.v]) {
                    best[e.v] = nd;
                    parent[e.v] = u;
                }
            }
        }

        long t1 = System.nanoTime();
        metrics.timeNanos += (t1 - t0);
        return new Result(sourceComp, best, parent);
    }
}
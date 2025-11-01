package graph.model;

import java.util.*;

public class Graph {
    private final int n;                 // number of nodes [0..n-1]
    private final boolean directed;      // directed flag
    private final List<Edge> edges;      // edge list
    private final List<List<Edge>> adj;  // adjacency list (outgoing)

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.edges = new ArrayList<>();
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    public void addEdge(int u, int v, int w) {
        Edge e = new Edge(u, v, w);
        edges.add(e);
        adj.get(u).add(e);
        if (!directed) { // not used here, but kept for completeness
            Edge back = new Edge(v, u, w);
            edges.add(back);
            adj.get(v).add(back);
        }
    }

    public int n() { return n; }
    public boolean directed() { return directed; }
    public List<Edge> edges() { return edges; }
    public List<List<Edge>> adj() { return adj; }

    /** Build reverse graph (transpose) for directed graphs */
    public Graph reverse() {
        Graph g = new Graph(n, directed);
        for (Edge e : edges) g.addEdge(e.v, e.u, e.w);
        return g;
    }

    /** Build condensation DAG from component ids [0..compCount-1] */
    public static Graph condensation(Graph g, int[] compId, int compCount) {
        Graph dag = new Graph(compCount, true);
        // to avoid parallel duplicate edges, track seen pairs
        boolean[][] seen = new boolean[compCount][compCount];
        for (Edge e : g.edges()) {
            int a = compId[e.u], b = compId[e.v];
            if (a != b && !seen[a][b]) {
                dag.addEdge(a, b, e.w); // weight optional for DAG topo, but keep it
                seen[a][b] = true;
            }
        }
        return dag;
    }
}
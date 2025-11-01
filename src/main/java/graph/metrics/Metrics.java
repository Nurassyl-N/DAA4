package graph.metrics;

public class Metrics {
    // SCC (DFS)
    public long dfsVisits = 0;
    public long dfsEdges  = 0;

    // Topo (Kahn)
    public long pushes = 0;
    public long pops   = 0;

    // DAG shortest/longest
    public long relaxations = 0;

    // Timing
    public long timeNanos = 0;

    public void reset() {
        dfsVisits = dfsEdges = pushes = pops = relaxations = 0;
        timeNanos = 0;
    }
}
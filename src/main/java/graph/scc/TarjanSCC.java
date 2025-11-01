package graph.scc;

import model.InputGraph;
import util.Metrics;

import java.util.*;

public class TarjanSCC {
    private final int n;
    private final List<InputGraph.Edge> edges;
    private final List<List<Integer>> adj;
    private final Metrics metrics;

    private int time = 0;
    private int[] disc;
    private int[] low;
    private boolean[] inStack;
    private Deque<Integer> stack;
    private List<List<Integer>> sccs;

    public TarjanSCC(int n, List<InputGraph.Edge> edges, Metrics metrics) {
        this.n = n;
        this.edges = edges;
        this.metrics = metrics;
        adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (InputGraph.Edge e : edges) {
            adj.get(e.u).add(e.v);
        }
        disc = new int[n];
        Arrays.fill(disc, -1);
        low = new int[n];
        inStack = new boolean[n];
        stack = new ArrayDeque<>();
        sccs = new ArrayList<>();
        runTarjan();
    }

    private void runTarjan() {
        long t0 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) dfs(i);
        }
        metrics.addTime(System.nanoTime() - t0);
    }

    private void dfs(int u) {
        metrics.incrementDfsVisits();
        disc[u] = low[u] = time++;
        stack.push(u);
        inStack[u] = true;

        for (int v : adj.get(u)) {
            metrics.incrementDfsEdges();
            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (inStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> comp = new ArrayList<>();
            while (!stack.isEmpty()) {
                int w = stack.pop();
                inStack[w] = false;
                comp.add(w);
                if (w == u) break;
            }
            sccs.add(comp);
        }
    }

    public List<List<Integer>> getSCCs() {
        return sccs;
    }
}

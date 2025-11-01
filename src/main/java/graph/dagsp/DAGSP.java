package graph.dagsp;

import app.CondensationBuilder;
import util.Metrics;

import java.util.*;

public class DAGSP {
    private final int n;
    private final CondensationBuilder cb;
    private final Metrics metrics;
    private long lastComputeTime;
    private final long INF = Long.MAX_VALUE / 4;
    private long[] dist;
    private int[] parent;

    private long[] maxDist;
    private int[] parentMax;

    public DAGSP(int n, CondensationBuilder cb, Metrics metrics) {
        this.n = n;
        this.cb = cb;
        this.metrics = metrics;
        dist = new long[n];
        parent = new int[n];
        maxDist = new long[n];
        parentMax = new int[n];
    }

    public void computeShortestFrom(int src) {
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);
        dist[src] = 0;
        // topo
        TopoOrder topo = new TopoOrder(cb, n);
        List<Integer> order = topo.order();
        long t0 = System.nanoTime();
        for (int u : order) {
            if (dist[u] == INF) continue;
            for (int v : cb.adj(u)) {
                metrics.incrementRelaxations();
                long w = cb.edgeWeight(u, v);
                if (dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    parent[v] = u;
                }
            }
        }
        lastComputeTime = System.nanoTime() - t0;
        metrics.addTime(lastComputeTime);
    }

    public void computeLongestFrom(int src) {
        Arrays.fill(maxDist, Long.MIN_VALUE/4);
        Arrays.fill(parentMax, -1);
        maxDist[src] = 0;
        TopoOrder topo = new TopoOrder(cb, n);
        List<Integer> order = topo.order();
        long t0 = System.nanoTime();
        for (int u : order) {
            if (maxDist[u] < Long.MIN_VALUE/10) continue;
            for (int v : cb.adj(u)) {
                long w = cb.edgeWeight(u, v);
                if (maxDist[v] < maxDist[u] + w) {
                    maxDist[v] = maxDist[u] + w;
                    parentMax[v] = u;
                }
            }
        }
        lastComputeTime = System.nanoTime() - t0;
        metrics.addTime(lastComputeTime);
    }

    public long dist(int comp) { return dist[comp]==INF?Long.MAX_VALUE:dist[comp]; }
    public long maxDist(int comp) { return maxDist[comp]; }

    public List<Integer> reconstructPath(int to) {
        if (dist[to]==INF) return Collections.emptyList();
        LinkedList<Integer> path = new LinkedList<>();
        int cur = to;
        while (cur != -1) { path.addFirst(cur); cur = parent[cur]; }
        return path;
    }

    public List<Integer> reconstructPathMax(int to) {
        if (maxDist[to] < Long.MIN_VALUE/10) return Collections.emptyList();
        LinkedList<Integer> path = new LinkedList<>();
        int cur = to;
        while (cur != -1) { path.addFirst(cur); cur = parentMax[cur]; }
        return path;
    }

    public int anyFarthest() {
        int arg = -1;
        long best = Long.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            if (dist[i] != INF && dist[i] > best) { best = dist[i]; arg = i; }
        }
        return arg;
    }

    public int anyFarthestMax() {
        int arg = -1;
        long best = Long.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            if (maxDist[i] > best) { best = maxDist[i]; arg = i; }
        }
        return arg;
    }

    // helper topo order inside DAGSP
    static class TopoOrder {
        private final CondensationBuilder cb;
        private final int n;
        TopoOrder(CondensationBuilder cb, int n) { this.cb = cb; this.n = n; }
        List<Integer> order() {
            int[] indeg = new int[n];
            for (int u = 0; u < n; u++) for (int v : cb.adj(u)) indeg[v]++;
            Deque<Integer> q = new ArrayDeque<>();
            for (int i = 0; i < n; i++) if (indeg[i] == 0) q.addLast(i);
            List<Integer> ord = new ArrayList<>();
            while (!q.isEmpty()) {
                int u = q.removeFirst();
                ord.add(u);
                for (int v : cb.adj(u)) {
                    indeg[v]--;
                    if (indeg[v] == 0) q.addLast(v);
                }
            }
            return ord;
        }
    }
}

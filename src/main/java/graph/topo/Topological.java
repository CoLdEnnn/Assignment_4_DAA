package graph.topo;

import app.CondensationBuilder;
import util.Metrics;

import java.util.*;

public class Topological {
    private final int n;
    private final CondensationBuilder cb;

    public Topological(int n, CondensationBuilder cb) {
        this.n = n;
        this.cb = cb;
    }

    public List<Integer> kahn(Metrics metrics) {
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : cb.adj(u)) indeg[v]++;
        }
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) q.addLast(i);

        List<Integer> order = new ArrayList<>();
        long t0 = System.nanoTime();
        while (!q.isEmpty()) {
            int u = q.removeFirst();
            metrics.incrementKahnPops();
            order.add(u);
            for (int v : cb.adj(u)) {
                indeg[v]--;
                metrics.incrementKahnPushes();
                if (indeg[v] == 0) q.addLast(v);
            }
        }
        metrics.addTime(System.nanoTime() - t0);
        return order;
    }
}

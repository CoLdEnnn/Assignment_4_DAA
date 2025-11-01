package app;

import model.InputGraph;

import java.util.*;

public class CondensationBuilder {
    private final int n;
    private final List<InputGraph.Edge> edges;
    private final List<List<Integer>> sccs;
    private final int[] compOf;
    private final int compCount;
    private final Map<Integer, Map<Integer, Integer>> compEdges; // compU -> (compV -> weight)

    public CondensationBuilder(int n, List<InputGraph.Edge> edges, List<List<Integer>> sccs) {
        this.n = n;
        this.edges = edges;
        this.sccs = sccs;
        compOf = new int[n];
        Arrays.fill(compOf, -1);
        for (int i = 0; i < sccs.size(); i++) {
            for (int v : sccs.get(i)) compOf[v] = i;
        }
        compCount = sccs.size();
        compEdges = new HashMap<>();
        build();
    }

    private void build() {
        for (InputGraph.Edge e : edges) {
            int cu = compOf[e.u], cv = compOf[e.v];
            if (cu != cv) {
                compEdges.computeIfAbsent(cu, k -> new HashMap<>());
                Map<Integer,Integer> map = compEdges.get(cu);
                map.put(cv, Math.min(map.getOrDefault(cv, Integer.MAX_VALUE), e.w));
            }
        }
    }

    public int compCount() { return compCount; }
    public int componentOf(int originalNode) { return compOf[originalNode]; }
    public List<Integer> adj(int comp) {
        Map<Integer,Integer> map = compEdges.getOrDefault(comp, Collections.emptyMap());
        return new ArrayList<>(map.keySet());
    }
    public int edgeWeight(int cu, int cv) {
        return compEdges.getOrDefault(cu, Collections.emptyMap()).getOrDefault(cv, Integer.MAX_VALUE);
    }
    public int edgeCount() {
        int c = 0;
        for (var m : compEdges.values()) c += m.size();
        return c;
    }
    public Map<Integer, Map<Integer,Integer>> rawMap() {
        return compEdges;
    }
}

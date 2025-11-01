package app;

import com.google.gson.Gson;
import model.InputGraph;
import graph.scc.TarjanSCC;
import graph.topo.Topological;
import graph.dagsp.DAGSP;
import util.Metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String path = "data/tasks.json";
        String json = Files.readString(Path.of(path));
        Gson gson = new Gson();
        InputGraph in = gson.fromJson(json, InputGraph.class);

        Metrics metrics = new Metrics();

        System.out.println("Input graph: n=" + in.n + ", edges=" + in.edges.size() + ", weight_model=" + in.weight_model);
        System.out.println("Source: " + in.source);

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < in.n; i++) adj.add(new ArrayList<>());
        for (InputGraph.Edge e : in.edges) {
            adj.get(e.u).add(e.v);
        }

        TarjanSCC tarjan = new TarjanSCC(in.n, in.edges, metrics);
        List<List<Integer>> sccs = tarjan.getSCCs();
        System.out.println("\nSCCs (count=" + sccs.size() + "):");
        for (int i = 0; i < sccs.size(); i++) {
            System.out.println("  comp " + i + ": " + sccs.get(i) + " (size=" + sccs.get(i).size() + ")");
        }

        CondensationBuilder cb = new CondensationBuilder(in.n, in.edges, sccs);
        int compCount = cb.compCount();
        System.out.println("\nCondensation graph: components=" + compCount + ", edges=" + cb.edgeCount());
        for (int u = 0; u < compCount; u++) {
            System.out.println(" comp " + u + " -> " + cb.adj(u));
        }

        Topological topo = new Topological(compCount, cb);
        List<Integer> compOrder = topo.kahn(metrics);
        System.out.println("\nTopological order of components: " + compOrder);

        List<Integer> derived = new ArrayList<>();
        for (int comp : compOrder) {
            List<Integer> nodes = sccs.get(comp);
            derived.addAll(nodes);
        }
        System.out.println("Derived task order (by components, each component as listed): " + derived);

        DAGSP dagsp = new DAGSP(compCount, cb, metrics);
        int sourceComp = cb.componentOf(in.source);
        dagsp.computeShortestFrom(sourceComp);
        System.out.println("\nShortest distances from component " + sourceComp + ":");
        for (int i = 0; i < compCount; i++) {
            System.out.println(" comp " + i + " dist=" + dagsp.dist(i));
        }

        int targetComp = dagsp.anyFarthest();
        System.out.println("One shortest path (components) from " + sourceComp + " to " + targetComp + ": " + dagsp.reconstructPath(targetComp));
        List<Integer> pathComps = dagsp.reconstructPath(targetComp);
        List<Integer> repPath = new ArrayList<>();
        for (int c : pathComps) {
            repPath.add(sccs.get(c).get(0));
        }
        System.out.println("Representative original-node path: " + repPath);

        dagsp.computeLongestFrom(sourceComp);
        int farMax = dagsp.anyFarthestMax();
        System.out.println("\nLongest path (components) from " + sourceComp + " to " + farMax + " length=" + dagsp.maxDist(farMax));
        System.out.println("Path: " + dagsp.reconstructPathMax(farMax));
        List<Integer> repMaxPath = new ArrayList<>();
        for (int c : dagsp.reconstructPathMax(farMax)) repMaxPath.add(sccs.get(c).get(0));
        System.out.println("Representative original-node critical path: " + repMaxPath);

        System.out.println("\nMetrics: " + metrics);
    }
}

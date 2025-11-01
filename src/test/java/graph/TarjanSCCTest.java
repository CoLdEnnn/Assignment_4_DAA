package graph.scc;

import model.InputGraph;
import org.junit.jupiter.api.Test;
import util.Metrics;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCCTest {
    @Test
    public void simpleCycle() {
        InputGraph.Edge e1 = new InputGraph.Edge(); e1.u=0; e1.v=1; e1.w=1;
        InputGraph.Edge e2 = new InputGraph.Edge(); e2.u=1; e2.v=2; e2.w=1;
        InputGraph.Edge e3 = new InputGraph.Edge(); e3.u=2; e3.v=0; e3.w=1;
        InputGraph.Edge e4 = new InputGraph.Edge(); e4.u=2; e4.v=3; e4.w=1;
        var edges = java.util.List.of(e1,e2,e3,e4);
        TarjanSCC t = new TarjanSCC(4, edges, new Metrics());
        List<List<Integer>> sccs = t.getSCCs();
        assertTrue(sccs.stream().anyMatch(c -> c.size()==3));
    }
}

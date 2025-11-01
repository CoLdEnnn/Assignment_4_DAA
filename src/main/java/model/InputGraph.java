package model;

import java.util.List;

public class InputGraph {
    public boolean directed;
    public int n;
    public List<Edge> edges;
    public int source;
    public String weight_model;

    public static class Edge {
        public int u;
        public int v;
        public int w;
    }
}

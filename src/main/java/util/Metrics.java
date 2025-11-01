package util;

public class Metrics {
    private long timeNano = 0;
    private long dfsVisits = 0;
    private long dfsEdges = 0;
    private long kahnPushes = 0;
    private long kahnPops = 0;
    private long relaxations = 0;

    public void addTime(long t) { timeNano += t; }
    public void incrementDfsVisits() { dfsVisits++; }
    public void incrementDfsEdges() { dfsEdges++; }
    public void incrementKahnPushes() { kahnPushes++; }
    public void incrementKahnPops() { kahnPops++; }
    public void incrementRelaxations() { relaxations++; }

    @Override
    public String toString() {
        return "Metrics{" +
                "timeNano=" + timeNano +
                ", dfsVisits=" + dfsVisits +
                ", dfsEdges=" + dfsEdges +
                ", kahnPushes=" + kahnPushes +
                ", kahnPops=" + kahnPops +
                ", relaxations=" + relaxations +
                '}';
    }
}

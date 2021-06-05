package Reinforce.MAB;


public class State {
    private final double [] features;
    private final String stateName;
    private final int stateId;
    private int occurrence = 0;
    private double occurrenceProb = 0;
    public static int numState = 0;

    public State(String name, double ... features){
        this.stateId = numState;
        this.stateName = name;
        this.features = new double[features.length];
        System.arraycopy(features, 0, this.features, 0, this.features.length);
        numState ++;
    }

    public void toThisState(int totalNumAction) {
        this.occurrence ++;
        this.occurrenceProb = this.occurrence*1.0/(1 + totalNumAction);
    }

    public double getOccurrenceProb() {
        return occurrenceProb;
    }

    public int getStateId() {
        return stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public double[] getFeatures() {
        return features;
    }

    @Override
    public String toString() {
        return stateName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        State s = (State)o;
        for (int i = 0; i < this.features.length; i ++) {
            if (this.features[i] != s.features[i]) {
                return false;
            }
        }
        return true;
    }
}

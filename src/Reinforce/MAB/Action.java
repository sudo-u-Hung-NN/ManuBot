package Reinforce.MAB;

public class Action implements Doable{
    private final int actionID;
    private final String actionName;
    private double value;
    private int numTaken;
    private double probTaken;
    private final double learning_rate = 0.1;
    public static int totalTaken = 0;

    public final double networkValue;

    public Action(int actionID, String actionName, double initValue, double networkValue) {
        this.actionID = actionID;
        this.value = initValue;
        this.actionName = actionName;
        this.networkValue = networkValue;
    }

    @Override
    public double takeAction() {
        this.numTaken ++;
        Action.totalTaken ++;
        this.probTaken = this.numTaken*1.0/Action.totalTaken;
        return this.networkValue;
    }

    @Override
    public void updateValue(double newValue) {
        this.value = (1 - learning_rate) * this.value + learning_rate * newValue;
    }

    @Override
    public String toString() {
        return "Action{" +
                "actionID=" + actionID +
                ", actionName='" + actionName + '\'' +
                ", value=" + value +
                ", probTaken=" + probTaken +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        Action a = (Action)o;
        return a.getActionID() == this.getActionID();
    }

    public double getValue() {
        return this.value;
    }

    public double getProbTaken() {
        return probTaken;
    }

    public int getActionID() {
        return actionID;
    }
}

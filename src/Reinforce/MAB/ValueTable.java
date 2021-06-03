package Reinforce.MAB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ValueTable {
    public HashMap<ArrayList<Double>,ArrayList<Action>> tableSpace;
    private int numAction = 0;
    private final double eInit;
    private final double eThrh;
    private final double Ptb;
    private ArrayList<Double> lastState;
    private int lastActionIndex;

    public ValueTable(double eInit, double eThrh, double Ptb) {
        this.eInit = eInit;
        this.eThrh = eThrh;
        this.Ptb = Ptb;
        tableSpace = new HashMap<ArrayList<Double>, ArrayList<Action>>();
    }

    private double encodeEtb(double Etb) {
        if (Etb < 0.5 * eInit) {
            return -1;
        }
        else if (Etb < 0.75 * eInit) {
            return 0;
        }
        else return 1;
    }

    private void addAction(double netVal, double initValue, ArrayList<Action> actions) {
        Action action = new Action(this.numAction, "", initValue, netVal);
        actions.add(action);
        this.numAction ++;
    }

    private ArrayList<Action> addState(double k, double Etb) {
        ArrayList<Double> newSate = new ArrayList<>();
        newSate.add(k);
        newSate.add(encodeEtb(Etb));
        ArrayList<Action> actionList = new ArrayList<>();
        for (double i : new double[] {0.5, 0.75, 1.0}) {
            double initValue = 1.0/(1 + numAction) * Math.sqrt((eInit - eThrh)/Ptb);
            addAction(i, initValue, actionList);
        }
        tableSpace.put(newSate, actionList);
        lastState = newSate;
        return actionList;
    }

    private double getAction(ArrayList<Action> actions) {
        int index = Policy.epsilonGreedy(actions);
        return actions.get(index).networkValue;
    }

    public double takeAction(double k, double Etb) {
        for (Map.Entry<ArrayList<Double>, ArrayList<Action>> entry : this.tableSpace.entrySet()) {
            ArrayList<Double> state = entry.getKey();
            ArrayList<Action> actions = entry.getValue();
            if (state.get(0) == k && state.get(1) == Etb) {
                this.lastState = state;
                lastActionIndex = (int)getAction(actions);
                return lastActionIndex;
            }
        }
        ArrayList<Action> actions = addState(k, Etb);
        lastActionIndex = (int)getAction(actions);
        return lastActionIndex;
    }

    public static void main(String[] args) {
        ValueTable TB = new ValueTable(100, 25, 2);
        System.out.println(TB.encodeEtb(20));
        System.out.println(TB.encodeEtb(30));
        System.out.println(TB.encodeEtb(40));
        System.out.println(TB.encodeEtb(50));
        System.out.println(TB.encodeEtb(60));
        System.out.println(TB.encodeEtb(70));
        System.out.println(TB.encodeEtb(80));
        System.out.println(TB.encodeEtb(90));
    }
}

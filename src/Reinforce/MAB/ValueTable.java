package Reinforce.MAB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ValueTable {
    public HashMap<State,ArrayList<Action>> tableSpace;
    private int numAction = 0;
    private final double eInit;
    private final double eThrh;
    private final double Ptb;
    private State lastState;
    private Action lastAction;

    public ValueTable(double eInit, double eThrh, double Ptb) {
        this.eInit = eInit;
        this.eThrh = eThrh;
        this.Ptb = Ptb;
        tableSpace = new HashMap<State, ArrayList<Action>>();
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
        Action action = new Action(this.numAction, "charge " + netVal + "%", initValue, netVal);
        actions.add(action);
        this.numAction ++;
    }

    private ArrayList<Action> addState(double k, double Etb) {
        State newSate = new State("", k, encodeEtb(Etb));
        ArrayList<Action> actionList = new ArrayList<>();
        for (double i : new double[] {1.0, 0.75, 0.5}) {
            double initValue = 1.0/(1 + numAction) * Math.sqrt((eInit - eThrh)/Ptb);
            addAction(i, initValue, actionList);
        }
        tableSpace.put(newSate, actionList);
        lastState = newSate;
        return actionList;
    }

    private double getAction(ArrayList<Action> actions) {
        int index = Policy.epsilonGreedy(actions);
        lastAction = actions.get(index);
        return actions.get(index).networkValue;
    }

    public double takeAction(double k, double Etb) {
        for (Map.Entry<State, ArrayList<Action>> entry : this.tableSpace.entrySet()) {
            State state = entry.getKey();
            ArrayList<Action> actions = entry.getValue();
            if (state.equals(new State("", k, encodeEtb(Etb)))) {
                this.lastState = state;
                return getAction(actions);
            }
        }
        ArrayList<Action> actions = addState(k, Etb);
        return getAction(actions);
    }

    public void updateValue(double reward) {
        if (lastAction == null || lastState == null) return;
        ArrayList<Action> actions = this.tableSpace.get(lastState);
        for (Action action : actions) {
            if (action != lastAction)
                action.updateValue(reward/actions.size());
            else
                action.updateValue(reward);
        }
    }

    public void printTable() {
        for (Map.Entry<State, ArrayList<Action>> entry : this.tableSpace.entrySet()) {
            State state = entry.getKey();
            ArrayList<Action> actions = entry.getValue();
            System.out.print(String.format(
                    "State (%.0f, %.0f):\n ",
                    state.getFeatures()[0], state.getFeatures()[1]
            ));
            for (Action action : actions) {
                System.out.println(action);
            }
        }
    }
}

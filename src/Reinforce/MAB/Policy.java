package Reinforce.MAB;

import java.util.ArrayList;
import java.util.Random;

public class Policy {
    public static double epsilon = 0.1;

    public static int epsilonGreedy(ArrayList<Action> actions) {
        Random random = new Random();
        int index = 0;
        if (random.nextDouble() < epsilon) {
            index = random.nextInt(actions.size());
        }
        else {
            double max = -10e3;
            for(int i = 0; i < actions.size(); i ++) {
                if (actions.get(i).getValue() >= max) {
                    max = actions.get(i).getValue();
                    index = i;
                }
            }
        }
        return index;
    }
}

package Reinforce.MAB;

public interface Doable {
    double takeAction();
    void updateValue(double value);
}

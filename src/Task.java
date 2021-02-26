import java.util.Random;

public class Task {
    private int ID;
    private double activateTime;
    private String rangeRandom = Config.getInstance().getAsString("Task_active_range");
    private String[] bound = rangeRandom.split(";");
    private double lowerBound = Double.parseDouble(bound[0]);
    private double ranging = Double.parseDouble(bound[1]);

    public double getLowerBound() {
        return lowerBound;
    }

    public double getRanging() {
        return ranging;
    }

    public void setActivateTime(double now) {
        Random rand = new Random();
        double deltaT = rand.nextGaussian()*getRanging()+getLowerBound(); // this give a number ranging from 300 to 400
        this.activateTime = now + deltaT;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    // Constructor
    public Task(int ID, double timeNow){
        setID(ID);
        setActivateTime(timeNow);
    }
}

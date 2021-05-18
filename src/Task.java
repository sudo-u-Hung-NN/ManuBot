import java.util.Random;

public class Task {
    private int ID;
    private double activateTime;
    private int gateOutID;
    private String rangeRandom = Config.getInstance().getAsString("Task_active_range");
    private String[] bound = rangeRandom.split(";");
    private double lowerBound = Double.parseDouble(bound[0]);
    private double ranging = Double.parseDouble(bound[1]);
    private point nextStop;
    private point shelfLocation;
    public boolean isActive = false;
    public boolean announced = false;

    public point getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(point shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public point getNextStop(){
        return nextStop;
    }

    public void setNextStop(point nextStop) {
        this.nextStop = nextStop;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getRanging() {
        return ranging;
    }

    public int getGateOut() {
        return this.gateOutID;
    }

    public void setGateOut(int numGateOut) {
        System.out.println("numGateOut = " + numGateOut);
        Random rand = new Random();
        this.gateOutID = rand.nextInt(numGateOut);
    }

    public void setActivateTime(double now) {
        Random rand = new Random();
        double deltaT = rand.nextDouble()*getRanging()+getLowerBound(); // this give a number ranging from 300 to 400
        this.activateTime = now + deltaT;
    }

    public double getActivateTime() {
        return activateTime;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public boolean isActive(double timeNow){
        if (timeNow >= getActivateTime()) {
            isActive = true;
        }
        return isActive;
    }

    // Constructor
    public Task(int ID, double timeNow, int numGateOut){
        setID(ID);
        setActivateTime(timeNow);
        setGateOut(numGateOut);
    }
}

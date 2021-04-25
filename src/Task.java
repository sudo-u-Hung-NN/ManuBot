import java.util.Random;

public class Task {
    private int ID;
    private double activateTime;
    private int gateOutID;
    private point locationNow;
    private String rangeRandom = Config.getInstance().getAsString("Task_active_range");
    private String[] bound = rangeRandom.split(";");
    private double lowerBound = Double.parseDouble(bound[0]);
    private double ranging = Double.parseDouble(bound[1]);
    private point shelfLocation = null;

    public point getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(point shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public void setLocationNow(point locationNow) {
        this.locationNow = locationNow;
    }

    public point getLocationNow() {
        return locationNow;
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
        Random rand = new Random();
        this.gateOutID = rand.nextInt(numGateOut);
    }

    public void setGateOutIDCopy(int GateOutId){
        this.gateOutID = GateOutId;
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
        return timeNow >= getActivateTime();
    }

    // Constructor
    public Task(int ID, double timeNow, int numGateOut){
        setID(ID);
        setActivateTime(timeNow);
        setGateOut(numGateOut);
    }
}

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
    public point shelfLocation = null;

    public point getShelfLocation() {
        return shelfLocation;
    }

//    public void setShelfLocation(Network net) {
//        Random rand = new Random();
//        int shelfID = rand.nextInt(net.getNumShelf());
//        this.shelfLocation = net.getTaskShelfId(shelfID).getLocation();
//    }

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
        this.gateOutID = 1 + rand.nextInt(numGateOut);
    }

    public void setActivateTime(double now) {
        Random rand = new Random();
        double deltaT = rand.nextGaussian()*getRanging()+getLowerBound(); // this give a number ranging from 300 to 400
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

    public void Activate(Network net, double timeNow){
        if (timeNow > getActivateTime()){
            net.insertActiveTaskQueue(this);
        }
    }

    // Constructor
    public Task(int ID, double timeNow, int numGateOut){
        setID(ID);
        setActivateTime(timeNow);
        setGateOut(numGateOut);
    }
}

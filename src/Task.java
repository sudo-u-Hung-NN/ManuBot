import java.util.Random;

public class Task extends ManuObject implements Timed{
    private double activateTime;
    private String rangeRandom = Config.getInstance().getAsString("Task_active_range");
    private String[] bound = rangeRandom.split(";");
    private double lowerBound = Double.parseDouble(bound[0]);
    private double ranging = Double.parseDouble(bound[1]);

    private int gateOutID;
    private point nextStop;
    private point shelfLocation;
    public boolean withAutoBot = false;

    public static int numTask = 0;

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

    public int getGateOut() {
        return this.gateOutID;
    }

    public void setGateOut(int numGateOut) {
        System.out.println("numGateOut = " + numGateOut);
        Random rand = new Random();
        this.gateOutID = rand.nextInt(numGateOut);
    }

    @Override
    public void setActivateTime(double now) {
        Random rand = new Random();
        double deltaT = rand.nextDouble()*ranging + lowerBound; // this give a number ranging from 300 to 400
        this.activateTime = now + deltaT;
    }

    @Override
    public boolean activate(double timeNow) {
        return  timeNow >= activateTime;
    }

    // Constructor
    public Task(double timeNow, int numGateOut){
        Task.numTask ++;
        setID(Task.numTask);
        setActivateTime(timeNow);
        setGateOut(numGateOut);
    }
}

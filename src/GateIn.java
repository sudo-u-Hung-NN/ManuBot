import java.util.Random;

public class GateIn extends TaskHolder implements Timed {

    private final String rangeRandom = Config.getInstance().getAsString("Gate_task_range");
    private final String[] bound = rangeRandom.split(";");

    private double makeTaskTime = 0;
    private final double lowerBound = Double.parseDouble(bound[0]);
    private final double ranging = Double.parseDouble(bound[1]);

    // This function setup default range of time intervals between each time the task arrives
    @Override
    public void setActivateTime(double timeNow) {
        Random rand = new Random();
        double deltaT = rand.nextDouble()*ranging + lowerBound;  //deltaT from 300 to 400 (second)
        this.makeTaskTime += deltaT;
    }

    // Constructor
    public GateIn(int ID, point X){
        setID(ID);
        setLocation(X);
        this.makeTaskTime = 0;
    }

    @Override
    public boolean activate(double timeNow) {
        return timeNow >= makeTaskTime;
    }

    // Methods
    public void Running(Network net, double timeNow, int numGateOut){
        if (activate(timeNow)){
            Task nt = new Task(timeNow, numGateOut); // nt: new task
            nt.setNextStop(this.location);
            net.insertArrivalTaskQueue(nt);
            setActivateTime(timeNow);
            System.out.println("Gate_in id{" + this.getID() + "} created package id{"
                    + nt.getID() +"} at " + timeNow);
        }
    }
}

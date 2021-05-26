import java.time.LocalTime;
import java.util.Random;

public class GateIn {
    private point location;
    private double makeTaskTime = 0;
    private String rangeRandom = Config.getInstance().getAsString("Gate_task_range");
    private String[] bound = rangeRandom.split(";");
    private double lowerBound = Double.parseDouble(bound[0]);
    private double ranging = Double.parseDouble(bound[1]);
    private int gateID = 0;

    public void setGateID(int gateID) {
        this.gateID = gateID;
    }

    public int getGateID() {
        return gateID;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getRanging() {
        return ranging;
    }

    public void setLocation(point location){
        this.location = location;
    }

    public point getLocation() {
        return location;
    }

    public double getMakeTaskTime() {
        return makeTaskTime;
    }

    // This function setup default range of time intervals between each time the task arrives
    public void setTaskInterval() {
        try {
            Random rand = new Random();
            double deltaT = rand.nextDouble()*getRanging()+getLowerBound();  //deltaT from 300 to 400 (second)
            this.makeTaskTime += deltaT;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // Constructor
    public GateIn(int ID, point X){
        setGateID(ID);
        setLocation(X);
        this.makeTaskTime = 0;
    }

    // Nếu mà gói tin sinh ra trước mà chưa được lấy thì gói hàng sinh ra sau cũng không được lấy.
    // Giả sử gói hàng A được chỉ định cho autoBot a, gói hàng B được chỉ định cho autoBot b.
    // Giả sử gói A được sinh ra trước gói B thì kể cả khi b đến nhận hàng trước a thì cũng không được lấy hàng.

    // Methods
    public void Running(Network net, double timeNow, int numGateOut){
            if (timeNow >= getMakeTaskTime()){
                Task nt = new Task(timeNow, numGateOut); // nt: new task
                nt.setNextStop(this.location);
                net.insertArrivalTaskQueue(nt);
                setTaskInterval();
                System.out.println("Gate_in id{" + this.getGateID() + "} created package id{"
                        + nt.getID() +"} at " + timeNow);
            }
    }
}

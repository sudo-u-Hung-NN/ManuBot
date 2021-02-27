import java.util.Random;

public class Gate {
    private point location;
    private String type;
    private double makeTaskTime = 0;
    private String rangeRandom = Config.getInstance().getAsString("Gate_task_range");
    private String[] bound = rangeRandom.split(";");
    private double lowerBound = Double.parseDouble(bound[0]);
    private double ranging = Double.parseDouble(bound[1]);

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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
            double deltaT = rand.nextGaussian()*getRanging()+getLowerBound();  //deltaT from 300 to 400 (second)
            this.makeTaskTime += deltaT;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // Constructor
    public Gate(point X, String type){
        setType(type);
        setLocation(X);
        this.makeTaskTime = 0;
    }

    // Nếu mà gói tin sinh ra trước mà chưa được lấy thì gói hàng sinh ra sau cũng không được lấy.
    // Giả sử gói hàng A được chỉ định cho autoBot a, gói hàng B được chỉ định cho autoBot b.
    // Giả sử gói A được sinh ra trước gói B thì kể cả khi b đến nhận hàng trước a thì cũng không được lấy hàng.


    // Methods
    public void Running(Network net, double timeNow, int numGateOut){
        if (this.getType().equals("In")){
            if (timeNow > getMakeTaskTime()){
                Task nt = new Task(net.getTaskID(), timeNow, numGateOut); // nt: new task
                net.insertArrivalTaskQueue(nt);
                setTaskInterval();
            }
        }
        if (this.getType().equals("Out")){
            // Count task out;
            // Print to file: TimeNow - ID
        }
    }
}

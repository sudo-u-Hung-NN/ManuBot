import java.util.LinkedList;
import java.util.List;

public class ManuBot { // Manufacture robot
    private int ID;
    private double speed = Config.getInstance().getAsDouble("speed");
    private point locationStart = new point(0.0,0.0);
    private point locationNow;
    private double InitEnergy = Config.getInstance().getAsDouble("init_energy");      // Energy at the start   (J)
    private Double ResEnergy;       // Energy at the moment  (J)
    private double ThshEnergy = Config.getInstance().getAsDouble("energy_threshold");      // Below this value is considered at dangerous stage  (J)
    private double ERperSec = Config.getInstance().getAsDouble("resting_energy");        // Energy to operate per Second at Westing stage  (J/s)
    private double EWperSec = Config.getInstance().getAsDouble("working_energy");        // Energy to operate per Second at Working state  (J/s)
    public List<Task> workList = new LinkedList<Task>(); // List of works
    public List<point> pathPointList = new LinkedList<point>(); // List of points indicate path

    // ************************************************************************************
    // Robot methods SECTION

    public int getId(){
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getSpeed() {return this.speed;}

    public point getLocationNow(){return this.locationNow;}

    // return TRUE if the robot is assigned for work
    public boolean isOccupied() {
        return this.workList.isEmpty();
    }

    // Return : True - The robot is at dangerous stage
    public boolean isDanger() {
        return this.ResEnergy > this.ThshEnergy;
    }

    // Return: True - The robot is not dead
    public boolean isFunctional() {
        return ResEnergy > 0;
    }


    public void simulateMovingTime(){
        point lastPoint = this.pathPointList.get(0);
        double sumTime = 0;
        for (point T: this.pathPointList){
            double lenPath = lastPoint.getLength(T);
            sumTime += lenPath/getSpeed();
            lastPoint = T;
        }

        this.pathPointList.clear();
    }

    // This need to be precise, add points to pathPointList
    public void getPath(){

    }

    public void GoCharge(Network net){
        point charger_location = getChargingPoint(net);
        simulateMovingTime();

        double time_charging = getChargeTime();

    }


    public void Running(Network net, double cycleTime){
        while (this.isFunctional()){
            if (this.isDanger()){
                GoCharge(net);
            }
            else{
                for (Task ts: this.workList){
                    //update vi tri Sx = Sx + vx*t
                    //Update vi tri Sy = Sy + vy*t
                }
            }
        }
    }

    // Constructor
    public ManuBot(int ID){
        setID(ID);
    }


    // Interface ends here, start modifying code under this line
    //***********************************************************
    // Methods to choose the charge point

    // Return the point of the charger
    public point getChargingPoint(Network net) {
        return null;
    }

    // Return the time to get charging
    public double getChargeTime(){
        return 0;
    }


}

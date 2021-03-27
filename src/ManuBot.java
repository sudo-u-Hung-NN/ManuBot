import java.util.LinkedList;
import java.util.List;

public class ManuBot { // Manufacture robot
    private int ID;
    private double speed = Config.getInstance().getAsDouble("speed");
    private point locationNow;
    private double InitEnergy = Config.getInstance().getAsDouble("init_energy");      // Energy at the start   (J)
    private Double ResEnergy;       // Energy at the moment  (J)
    private double ThshEnergy = Config.getInstance().getAsDouble("energy_threshold");      // Below this value is considered at dangerous stage  (J)
    private double ERperSec = Config.getInstance().getAsDouble("resting_energy");        // Energy to operate per Second at Westing stage  (J/s)
    private double EWperSec = Config.getInstance().getAsDouble("working_energy");        // Energy to operate per Second at Working state  (J/s)
    private boolean isAdaptive = Config.getInstance().getAsBoolean("adaptive_charging");         // False if manual determining the percentage charging
    private double ChargeLevel = Config.getInstance().getAsDouble("fixed_energy_charge_level");
    public List<Task> workList = new LinkedList<Task>(); // List of works
    public List<point> pathPointList = new LinkedList<point>(); // List of points indicate path trajectories

    // ************************************************************************************
    // Robot methods SECTION

    public int getId(){
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getSpeed() {
    	return this.speed;
    }

    public void setLocationNow(point LocationNow){
        this.locationNow = LocationNow;
    }

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
        return this.ResEnergy > 0;
    }

    // Update energy status
    public void setResEnergy(double resEnergy){
        this.ResEnergy = resEnergy;
    }

    public Double getResEnergy() {
        return ResEnergy;
    }

    //
    /** @author Nguyen Nang Hung
     * @using to move AutoBot for 'durationTime' seconds
     * @param durationTime is the time given
     * @rule AutoBots always goes either vertically or horizontally
     * For a given time: durationT, a given next destination:
        => compute time needed to travel: denote as deltaT (s)
        2 possibilities:
         First: if deltaT < durationT:
                Meaning: the AutoBot get to the destination and there's spare time
                => get the next destination then recurrently recall the function(durationT - deltaT)
         Second: if deltaT >= durationT:
                Meaning: the AutoBot has just enough time or not enough time to reach destination
                and the AutoBot move towards the destination for durationT (s)
                => update the current location after the formula of velocity
     */
    public void moving(double durationTime){
        point nextJoint = this.pathPointList.get(0);
        point locationNow = getLocationNow();
        double timeRequire = locationNow.getLength(nextJoint)/getSpeed();
        if (timeRequire < durationTime){
            setLocationNow(nextJoint);
            this.pathPointList.remove(0);
            moving(durationTime - timeRequire);
        }
        else {
            if (locationNow.getX() == nextJoint.getX()){ // moving vertically
                double newX = locationNow.getX();
                double directionVector = nextJoint.getY() - locationNow.getY();
                double newY = locationNow.getY() + durationTime/timeRequire * directionVector;
                point locationNew = new point(newX, newY);
                setLocationNow(locationNew);
            }
            else{ // moving horizontally
                double newY = locationNow.getY();
                double directionVector = nextJoint.getX() - locationNow.getX();
                double newX = locationNow.getX() + durationTime/timeRequire * directionVector;
                point locationNew = new point(newX, newY);
                setLocationNow(locationNew);
            }
        }
    }

    private void energySimulation(double cylceTime){
        if (this.isOccupied()){
            setResEnergy(getResEnergy() - cylceTime*this.ERperSec);
        }
        setResEnergy(getResEnergy() - cylceTime*this.EWperSec);
    }

    public void Running(Network net, double cycleTime){
        while (this.isFunctional()){
            if (this.isDanger()){
                GoCharge(net);
            }
            else{
                for (Task ts: this.workList){
                    moving(cycleTime);

                }
            }
            energySimulation(cycleTime);
        }
    }

    // Constructor
    public ManuBot(int ID, point Location){
        setID(ID);
        setLocationNow(Location);
    }

    // Interface ends here, start modifying code under this line
    //***********************************************************
    // Methods to choose the charge point
    /** @author Nguyen Nang Hung
     * @param net
     * network provide locations of charging points and their status
     * @return the charging point
     */
    public point getChargingPoint(Network net) {
        return null;
    }

    /** @author Nguyen Nang Hung
     * The AutoBot charges itself for an amount of time, this time is determined
     * as the output of this function
     * if not adaptive, charge to a constant amount of energy
     * @return the duration for which the AutoBot stops at the charger
     */
    public double getChargeTime(Charger chgr){
        if (this.isAdaptive){
            return 0;
        }
        else{
            return this.InitEnergy * this.ChargeLevel / chgr.getECperSec();
        }
    }

    public void getReCharge(double duration, double timeNow){

    }

    public void GoCharge(Network net){
        point charger_location = getChargingPoint(net);
        //double time_charging = getChargeTime();

    }

    /** @author Nguyen Nang Hung
     * @using to determine the next joint point leading to the destination, add this point to pathPointList
     * @rule path MUST BE lines that are either vertically or horizontally aligned
       and the path must not over-crossing Shelves
     *
     */
    public void getNextPath(){

    }

}

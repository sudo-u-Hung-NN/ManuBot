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
    private boolean isCharging = false;
    public int isTransporting = -1; // 1 if carring task and -1 if not

    // ************************************************************************************
    // Robot methods SECTION

    public int getId(){
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getSpeed() {return this.speed;}

    public void setLocationNow(point LocationNow){
        this.locationNow = LocationNow;
    }

    public point getLocationNow(){return this.locationNow;}

    // return TRUE if the robot is assigned for work
    public boolean isRelax() {
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
    public void moving(double durationTime, Network net){ // duration input is cycleTime GS = 1s
        point nextJoint = this.pathPointList.get(0);
        point locationNow = getLocationNow();
        double timeRequire = locationNow.getLength(nextJoint)/getSpeed(); // 0.2 => 0.8s
        if (timeRequire < durationTime){
            setLocationNow(nextJoint);
            for (Charger chgr: net.ChargerList){
                if (nextJoint.getX() == chgr.getLocation().getX() && nextJoint.getY() == chgr.getLocation().getY()){
                    this.isTransporting *= (-1);
                    break;
                }
            }
            this.isTransporting *= (-1);
            this.pathPointList.remove(0);
            moving(durationTime - timeRequire, net);
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
        if (this.isRelax()){
            setResEnergy(getResEnergy() - cylceTime*this.ERperSec);
        }
        else {
            setResEnergy(getResEnergy() - cylceTime*this.EWperSec);
        }
    }

    public void Running(Network net, double cycleTime){
        if (this.isFunctional()){
            if (!this.workList.isEmpty()){
                this.pathPointList.add(this.workList.get(0).getLocationNow());
            }
            moving(cycleTime, net);
            if (this.isDanger()){

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
     * @return the charger
     */
    public Charger getCharger(Network net) {
        double minDistance = 10000;
        Charger output = null;
        for (Charger chgr: net.ChargerList){
            double dist = chgr.getLocation().getLength(getLocationNow());
            if (dist > minDistance){
                minDistance = dist;
                output = chgr;
            }
        }
        return output;
    }

    /** @author Nguyen Nang Hung
     * The AutoBot charges itself for an amount of time, this time is determined
     * as the output of this function
     * if not adaptive, charge to a constant amount of energy
     * @using after determining the charger (run getCharger)
     * @return the duration for which the AutoBot stops at the charger
     */
    public double getChargeTime(Charger chgr){
        if (this.isAdaptive){
            return 0;
        }
        else{
            return (this.InitEnergy * this.ChargeLevel - this.ResEnergy) / chgr.getECperSec();
        }
    }

    /** @author Nguyen Nang Hung
     * 2 possibilities:
     *      If duration > timeCycle: then update the residual energy
     *      then waits for the next timeCycle
     *      If duration <= timeCycle: then update the residual energy
     *      and get back to work
     * @param duration
     * @param timeCycle
     */
    public void getReCharge(Charger chgr ,double duration, double timeCycle){
        double time_charging = getChargeTime(chgr);
        this.isCharging = true;
        if (duration > timeCycle){

        }
        else{
            setResEnergy(this.getResEnergy() + chgr.getECperSec()*(duration-timeCycle));
            this.isCharging = false;
        }
    }

    public void GoCharge(Network net, double timeNow){
        Charger chgr = getCharger(net);


    }

    /** @author Nguyen Nang Hung
     * @using to determine the next joint point leading to the destination, add this point to pathPointList
     * @rule path MUST BE lines that are either vertically or horizontally aligned
       and the path must not over-crossing Shelves
     *
     */
    public void getNextPoint(){

    }

}

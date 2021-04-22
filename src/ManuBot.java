import java.util.ArrayList;
import java.util.List;

public class ManuBot { // Manufacture robot
    private int ID;
    private double speed = Config.getInstance().getAsDouble("speed");
    private point locationNow;
    private point destination;
    private double InitEnergy = Config.getInstance().getAsDouble("init_energy");      // Energy at the start   (J)
    private double ResEnergy;       // Energy at the moment  (J)
    private double ThshEnergy = Config.getInstance().getAsDouble("energy_threshold");      // Below this value is considered at dangerous stage  (J)
    private double ERperSec = Config.getInstance().getAsDouble("resting_energy");        // Energy to operate per Second at Westing stage  (J/s)
    private double EWperSec = Config.getInstance().getAsDouble("working_energy");        // Energy to operate per Second at Working state  (J/s)
    private boolean isAdaptive = Config.getInstance().getAsBoolean("adaptive_charging");         // False if manual determining the percentage charging
    private double ChargeLevel = Config.getInstance().getAsDouble("fixed_energy_charge_level");
    public List<Task> workList = new ArrayList<>(); // List of works
    public List<point> pathPointList = new ArrayList<>(); // List of points indicate path trajectories
    private double chargingTimeLeft = 0;
    public int isTransporting = -1; // 1 if carring task and -1 if not
    private final List<point> switchStatePoints;

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

    public double getEWperSec() {
        return EWperSec;
    }

    public double getERperSec() {
        return ERperSec;
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

    public double getChargingTimeLeft() {
        return chargingTimeLeft;
    }

    public void moving()
    {
    	if (this.getChargingTimeLeft() > 0){
    	    return;
        }
    	if (this.switchStatePoints.contains(locationNow))
    	{
    		this.isTransporting *= -1;
    		return;
    	}
    	else {
    		this.locationNow = this.pathPointList.get(0);
    		pathPointList.remove(0);
    	}
    }
    
    private void energySimulation(double cylceTime){
        if (this.isTransporting == -1){
            setResEnergy(getResEnergy() - cylceTime*this.ERperSec);
        }
        else {
            setResEnergy(getResEnergy() - cylceTime*this.EWperSec);
        }
    }

    public Double ECperSec = 0.0;
    public void Running(Network net, Map map, double cycleTime) {
        if (this.isFunctional()) {
            if (this.chargingTimeLeft == 0) {
                if (!this.workList.isEmpty()) {
                    Task firstTask = this.workList.get(0);
                    List<point> toDest = getPath(this.getLocationNow(), firstTask.getLocationNow(), map);
                    this.pathPointList.addAll(toDest);
                    moving();
                }
                if (this.isDanger()) {
                    ECperSec = GoCharge(net, map); // set charging time > 0, return energy charging per second
                }
            } else {
                getReCharge(ECperSec, cycleTime);
            }
//            System.out.println("AutoBot id{" + this.getId() + "}Doing task id{" + firstTask.getID() +"}");
            energySimulation(cycleTime);
            this.chargingTimeLeft = Math.max(this.chargingTimeLeft - cycleTime, 0);
            assert chargingTimeLeft >= 0 :
                    String.format("chargingTimeLeft is set false, value = %f", this.chargingTimeLeft);
        }
    }

    // Constructor
    public ManuBot(int ID, point Location, Network network){
        setID(ID);
        setLocationNow(Location);
        setResEnergy(InitEnergy);
        this.switchStatePoints = new ArrayList<>();
        for (Gate gt: network.getGateInList()){
            this.switchStatePoints.add(gt.getLocation());
        }
        for (Gate gt: network.getGateOutList()){
            this.switchStatePoints.add(gt.getLocation());
        }
        for (TaskShelf tsh: network.getShelfList()){
            this.switchStatePoints.add(tsh.getLocation());
        }
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
     *      If chargingTimeLeft > timeCycle: then update the residual energy
     *      then waits for the next timeCycle, chargingTimeLeft get reduced
     *      If chargingTimeLeft <= timeCycle: then update the residual energy
     *      and set chargingTimeLeft to zero
     * @param timeCycle
     */
    public void getReCharge(double ECperSec, double timeCycle){
        if (this.chargingTimeLeft > timeCycle){
            // stand still
            this.chargingTimeLeft -= timeCycle;
        }
        else{
            setResEnergy(this.getResEnergy() + ECperSec*(this.chargingTimeLeft-timeCycle));
            this.chargingTimeLeft = 0;
        }
    }

    /** @author Nguyen Nang Hung
     * This function determine charging point, then insert path to that charger
     * @param net
     * @return charging energy per second of the chosen charger
     */
    public double GoCharge(Network net,Map map){
        // choose charger location
        Charger chgr = getCharger(net);
        // Find path to charging point
        List<point> toCharger = getPath(this.getLocationNow(), chgr.getLocation(), map);
        // Insert the path to the head of the path point list
        this.pathPointList.addAll(0, toCharger);
        // Determine charging time
        this.chargingTimeLeft = getChargeTime(chgr);
        return chgr.getECperSec();
    }

    public List<point> getPath(point startPoint, point endPoint, Map map){
        List<point> toDest = new ArrayList<>();
        // Determine points then insert into toDest list
		Node newNode ;
		map.setStartPoint(startPoint);
		map.setEndPoint(endPoint);
		do
		{
			newNode = map.FindPath(toDest);
			map.setStartPoint(newNode.getLocation());
		} while (!newNode.equals(map.getEndPoint()));
		System.out.println("Find path completed");
        // Return toDest
        return toDest;
    }
}

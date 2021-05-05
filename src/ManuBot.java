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
    public List<Node> pathNodeList = new ArrayList<>(); // List of point indicate path trajectories
    private double chargingTimeLeft = 0;
    public int isTransporting = -1; // 1 if carring task and -1 if not
    private final List<Node> switchStateNodes;

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

    public void setLocationNow(Node LocationNow){
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

    public void moving(point nextNode, Network network, Map map)
    {
        this.locationNow = nextNode;
        System.out.println("AutoBot id{" + this.ID + "} is at location (x, y) = (" + this.locationNow.getX() + ", " + this.locationNow.getY() +")");
    	// If at gate in ...
//        if (network.amIatGateIn(this.getLocationNow())){
//            network.getArrivalTask(this.workList.get(0));
//            assert this.workList.get(0) != null: "Invalid remove task";
//            this.isTransporting *= -1;
//        }
        // If at shelf ...
//        TaskShelf checkShelf = network.amIatShelf(getLocationNow());
//        if (checkShelf != null){
//            // Case1: come to get task
//            if (network.isActive(this.workList.get(0))){
//                network.deleteActiveTask(this.workList.get(0));
//                checkShelf.getTaskList().remove(this.workList.get(0));
//                this.workList.get(0).setNextStop(
//                        network.getGateOutList().get(
//                                this.workList.get(0).getGateOut()
//                        ).getLocation()
//                );
//            }
//            else {
//                // Case2: come to store task
//                checkShelf.getTaskList().add(this.workList.get(0));
//                this.workList.remove(0);
//            }
//            this.isTransporting *= -1;
//        }
        // If at gate out ...
//        GateOut checkGateOut = network.amIatGateOut(getLocationNow());
//        if (checkGateOut != null){
//            checkGateOut.recieveTask(this.workList.get(0));
//            this.workList.remove(0);
//            this.isTransporting *= -1;
//        }

        switch (map.point2node(this.locationNow).getType()){
            case GATE_IN:
                network.getArrivalTask(this.workList.get(0));
                assert this.workList.get(0) != null: "Invalid remove task";
                this.isTransporting *= -1;
                break;
            case GATE_OUT:
                GateOut checkGateOut = network.amIatGateOut(getLocationNow());
                assert checkGateOut != null : "Gate out null pointer";
                checkGateOut.recieveTask(this.workList.get(0));
                this.workList.remove(0);
                this.isTransporting *= -1;
                break;
            case SHELF:
                TaskShelf checkShelf = network.amIatShelf(getLocationNow());
                if (checkShelf != null){
                    // Case1: come to get task
                    if (network.isActive(this.workList.get(0))){
                        network.deleteActiveTask(this.workList.get(0));
                        checkShelf.getTaskList().remove(this.workList.get(0));
                        this.workList.get(0).setNextStop(
                                network.getGateOutList().get(
                                        this.workList.get(0).getGateOut()
                                ).getLocation()
                        );
                    }
                    else {
                        // Case2: come to store task
                        checkShelf.getTaskList().add(this.workList.get(0));
                        this.workList.remove(0);
                    }
                    this.isTransporting *= -1;
                }
                break;
            default:
                break;
        }
    }
    
    private void energySimulation(double cycleTime){
        if (this.isTransporting == -1){
            setResEnergy(getResEnergy() - cycleTime*this.ERperSec);
        }
        else {
            setResEnergy(getResEnergy() - cycleTime*this.EWperSec);
        }
    }

    public Double ECperSec = 0.0;
    public void Running(Network net, Map map, double cycleTime) {
        if (this.isFunctional()) {
            //boolean t = net.amIatCharger(this.getLocationNow()) == null;
            boolean t = map.point2node(this.getLocationNow()).isAtCharger();
            if (this.chargingTimeLeft > 0 && t) {
                assert ECperSec == 0: "Invalid charging energy called";
                this.setResEnergy(this.getResEnergy() + cycleTime * ECperSec);
                this.chargingTimeLeft = Math.max(this.chargingTimeLeft - cycleTime, 0);
            }
            else if (this.chargingTimeLeft == 0 && t) {
                getPath(map.point2node(locationNow), map.point2node(this.workList.get(0).getNextStop()), map);
                moving(this.pathNodeList.get(0), net, map);
                System.out.println("AutoBot id{" + this.getId() + "} Doing task id{" + this.workList.get(0).getID() +"}");
            }
            else {
                if (this.pathNodeList.isEmpty()){
                    if (this.workList.isEmpty()){
                        // ???
                    }
                }
                moving(this.pathNodeList.get(0), net, map);
                System.out.println("AutoBot id{" + this.getId() + "} Doing task id{" + this.workList.get(0).getID() +"}");
            }
            if (this.isDanger()){
                // Đặt lại chargingTime > 0, đồng thời, thay đổi đường đi của AutoBot. Trả lại năng lượng sạc mỗi giây
                ECperSec = this.GoCharge(net, map);
            }

            energySimulation(cycleTime);
            assert chargingTimeLeft < 0 :
                    String.format("chargingTimeLeft is set false, value = %f", this.chargingTimeLeft);
            System.out.println("AutoBot id{" + this.getId() +"} is at (x, y) = (" + this.locationNow.getX() +", " + this.locationNow.getY()+ ")");
        }
    }

    // Constructor
    public ManuBot(int ID, Node Location, List<Node> switchStateNodes){
        setID(ID);
        setLocationNow(Location);
        setResEnergy(InitEnergy);
        this.switchStateNodes = switchStateNodes;
    }

    // Interface ends here, start modifying code under this line
    //**********************************************************
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
     * This function determine charging point, then insert path to that charger
     * @param net
     * @return charging energy per second of the chosen charger
     */
    public double GoCharge(Network net,Map map){
        this.pathNodeList.clear();
        // choose charger location
        Charger chgr = getCharger(net);
        // Find path to charging point
        getPath(map.point2node(this.getLocationNow()), map.point2node(chgr.getLocation()) , map);
        // Determine charging time
        this.chargingTimeLeft = getChargeTime(chgr);
        return chgr.getECperSec();
    }

    public void getPath(Node startPoint, Node endPoint, Map map){
        List<Node> toDest = new ArrayList<>();
        // Determine points then insert into toDest list
		Node newNode ;
		map.setStartPoint(startPoint);
		map.setEndPoint(endPoint);
		do
		{
			newNode = map.FindPath(toDest);
			map.setStartPoint(newNode);
		} while (!newNode.equals(map.getEndPoint()));
		System.out.println("Find path completed");
        // Return toDest
        this.pathNodeList.addAll(toDest);
    }

    public Node getNextNode(){
        Node tmp = this.pathNodeList.get(0);
        this.pathNodeList.remove(0);
        return tmp;
    }
}

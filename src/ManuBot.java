import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManuBot extends ManuObject { // Manufacture robot
    private final double speed = Config.getInstance().getAsDouble("speed");
    private final double InitEnergy = Config.getInstance().getAsDouble("init_energy");      // Energy at the start   (J)
    private final double ThshEnergy = Config.getInstance().getAsDouble("energy_threshold");      // Below this value is considered at dangerous stage  (J)
    private final double ERperSec = Config.getInstance().getAsDouble("resting_energy");        // Energy to operate per Second at Westing stage  (J/s)
    private final double EWperSec = Config.getInstance().getAsDouble("working_energy");        // Energy to operate per Second at Working state  (J/s)
    private final boolean isAdaptive = Config.getInstance().getAsBoolean("adaptive_charging");         // False if manual determining the percentage charging
    private final double ChargeLevel = Config.getInstance().getAsDouble("fixed_energy_charge_level");
    public List<Task> workList = new ArrayList<>(); // List of works
    public List<Node> pathNodeList = new ArrayList<>(); // List of point indicate path trajectories
    private double chargingTimeLeft = 0;
    public int isTransporting = -1; // 1 if carring task and -1 if not
    private Charger charger;
    private double ResEnergy;       // Energy at the momemomentnt  (J)
    private Node location;

    public Node getLocation() {
        return location;
    }

    public void setLocation(Node location) {
        this.location = location;
    }
    // ************************************************************************************
    // Robot methods SECTION

    public double getSpeed() {
    	return this.speed;
    }

    public double getEWperSec() {
        return EWperSec;
    }

    public double getERperSec() {
        return ERperSec;
    }

    // Return : True - The robot is at dangerous stage
    public boolean isDanger() {
        return this.ResEnergy < this.ThshEnergy;
    }

    // Return: True - The robot is not dead
    public boolean isFunctional() {
        return this.ResEnergy > 0;
    }

    // Update energy status
    public void setResEnergy(double resEnergy){
        this.ResEnergy = Math.min(resEnergy, InitEnergy);
    }

    public Double getResEnergy() {
        return ResEnergy;
    }

    public double getChargingTimeLeft() {
        return chargingTimeLeft;
    }

    private void validate(Map map) {
        if (!this.workList.isEmpty()) {
            switch (map.point2node(this.workList.get(0).getNextStop()).getType()) {
                case GATE_IN:
                    assert this.isTransporting == -1 : "Invalid moving purpose to gate in";
                    break;
                case GATE_OUT:
                    assert this.isTransporting == 1 : "Invalid moving purpose to gate out";
                    break;
                case SHELF:
                    if (this.workList.get(0).withAutoBot) {
                        assert this.isTransporting == 1 : "Invalid moving purpose to shelf 1";
                    }
                    else {
                        assert this.isTransporting == -1 : "Invalid moving purpose to shelf 2";
                    }
                    break;
                case NONE:
                case CHARGER:
                    break;
            }
        }
    }

    public void moving(Node nextNode, Network network, Map map, double timeNow) throws IOException {
        if (nextNode == null) {
            System.out.println("Here in moving, wrong");
        }
        if (this.location.equals(nextNode)) {
            System.out.println("Already at the destination");
        }

        this.location = nextNode;

        switch (this.location.getType()){
            case GATE_IN:
                network.getArrivalTask(this.workList.get(0));
                assert this.workList.get(0) != null: "Invalid remove task";
                this.isTransporting = 1;
                this.workList.get(0).withAutoBot = true;
                this.workList.get(0).setNextStop(this.workList.get(0).getShelfLocation());
                System.out.println("AutoBot id{" + this.objectId + "} at gate in, now goes to a shelf.");
                map.cleanPath(this);
                break;
            case GATE_OUT:
                GateOut checkGateOut = network.amIatGateOut(location, map);
                assert checkGateOut != null : "Gate out null pointer";
                assert !this.workList.isEmpty() : "Error, Work list is empty";
                if(this.isTransporting == 1) {
                    checkGateOut.recieveTask(this.workList.get(0), this, Ultilis.gateOutWriter, timeNow);
                    this.workList.remove(0);
                    this.isTransporting = -1;
                }
                map.cleanPath(this);
                break;
            case SHELF:
                TaskShelf checkShelf = network.amIatShelf(location, map);
                System.out.println("AutoBot is at shelf");
                if (checkShelf != null){
                    // Case1: come to get task
                    if (this.workList.get(0).activate(timeNow) && checkShelf.isContain(this.workList.get(0))) {
                        this.isTransporting = 1;
                        System.out.println("AutoBot come to get task");
                        network.deleteActiveTask(this.workList.get(0));
                        checkShelf.takeFromShelf(this, Ultilis.shelvesWriter, timeNow, this.workList.get(0));
                        this.workList.get(0).setNextStop(network.getGateOutList().get(this.workList.get(0).getGateOut()).getLocation());
                    }
                    else  {
                        // Case2: come to store task
                        System.out.println("AutoBot come to store task");
                        checkShelf.store2Shelf(this, Ultilis.shelvesWriter, timeNow, this.workList.get(0));
                        this.workList.remove(0);
                        this.isTransporting = -1;
                    }
                }
                map.cleanPath(this);
                break;
            case CHARGER:
                System.out.println("This is charger!");
                map.cleanPath(this);
                break;
            case NONE:
                break;
            default:
                break;
        }
        if (!this.pathNodeList.isEmpty()) {
            this.pathNodeList.remove(0);
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
    public void Running(Network net, Map map, double cycleTime, double timeNow, ComputingCenter CC) throws IOException{
        if (this.isFunctional()) {
            validate(map);
            System.out.println("AutoBot id{" + this.objectId +"} is at (x, y) = (" + this.location.getX() +", " + this.location.getY() +
                    "), current node type: " + this.location.getType());

            boolean t = this.location.isAtCharger();
            if (this.chargingTimeLeft > 0 && t) {
                assert ECperSec == 0: "Invalid charging energy called";
                Ultilis.chargerPrintFile(this, charger, timeNow);
                this.setResEnergy(this.getResEnergy() + cycleTime * ECperSec);
                this.chargingTimeLeft = Math.max(this.chargingTimeLeft - cycleTime, 0);
                if (this.getResEnergy() == this.InitEnergy) {
                    this.chargingTimeLeft = 0;
                }
                System.out.println("IV. AutoBot id{" + this.objectId + "}, Energy: " + this.getResEnergy() + ", going location x = " +this.charger.getLocation().getX() +
                        ", y = " + charger.getLocation().getY() + "), node type = " + map.point2node(charger.getLocation()).getType());
            }
            else if (this.chargingTimeLeft == 0 && t) {
                if (!this.workList.isEmpty()) {
                    Task currtask = this.workList.get(0);
                    System.out.println("I. AutoBot id{" + this.objectId + "}, Energy: " + this.getResEnergy() + " doing task id{" + currtask.objectId + "}, location: x = " + currtask.getNextStop().getX() + ", y = " +currtask.getNextStop().getY() +
                            ", Task next stop node type: " + map.point2node(currtask.getNextStop()).getType());
                    Node destination = map.point2node(this.workList.get(0).getNextStop());
                    getPath(location, destination, map);
                    moving(this.pathNodeList.get(0), net, map, timeNow);
                }
                else {
                    this.setResEnergy(this.getResEnergy() + cycleTime * ECperSec);
                }
            }
            else if (this.chargingTimeLeft > 0 && !t) {
                Node destination = map.point2node(this.charger.getLocation());
                getPath(location, destination, map);
                System.out.println("III. AutoBot id{" + this.objectId + "}, Energy: " + this.getResEnergy() + ", going location x = " +this.charger.getLocation().getX() +
                        ", y = " + charger.getLocation().getY() + "), node type = " + map.point2node(charger.getLocation()).getType());
                moving(this.pathNodeList.get(0), net, map, timeNow);
            }
            else {
                if (this.workList.isEmpty()){
                    System.out.println("AutoBot id{" + this.objectId + "} Doing nothing this cycle");
                }
                else {
                    Task currtask = this.workList.get(0);
                    System.out.println("II. AutoBot id{" + this.objectId + "}, Energy: " + this.getResEnergy() + " doing task id{" + currtask.objectId +
                            "}, location: x = " + currtask.getNextStop().getX() + ", y = " +currtask.getNextStop().getY() +
                            ", Task next stop node type: " + map.point2node(currtask.getNextStop()).getType());
                    Node destination = map.point2node(this.workList.get(0).getNextStop());
                    if (location.equals(destination)){
                        moving(location, net, map, timeNow);
                    }
                    if (!this.workList.isEmpty()) {
                        destination = map.point2node(this.workList.get(0).getNextStop());
                        getPath(location, destination, map);
                        moving(this.pathNodeList.get(0), net, map, timeNow);
                    }
                }
            }
            if (this.isDanger() && this.chargingTimeLeft <= 0){
                // Đặt lại chargingTime > 0, đồng thời, thay đổi đường đi của AutoBot. Trả lại năng lượng sạc mỗi giây
                ECperSec = this.GoCharge(net, map, cycleTime, CC);
            }
            energySimulation(cycleTime);
            assert chargingTimeLeft < 0 :
                    String.format("chargingTimeLeft is set false, value = %f", this.chargingTimeLeft);
        }
    }

    // Constructor
    public ManuBot(int ID, Node Location, List<Node> switchStateNodes){
        setID(ID);
        setLocation(Location);
        setResEnergy(InitEnergy);
//        this.switchStateNodes = switchStateNodes;
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
            double dist = chgr.getLocation().getLength(location);
            if (dist < minDistance){
                minDistance = dist;
                output = chgr;
            }
        }
        assert output != null : "Can not find Charger!";
        return output;
    }

    /** @author Nguyen Nang Hung
     * The AutoBot charges itself for an amount of time, this time is determined
     * as the output of this function
     * if not adaptive, charge to a constant amount of energy
     * @using after determining the charger (run getCharger)
     * @return the duration for which the AutoBot stops at the charger
     */
    public double getChargeTime(Charger chgr, Network net, double cycleTime, ComputingCenter center){
        if (this.isAdaptive){
            return center.getChargingTime(net, this, cycleTime);
        }
        else{
            return (this.InitEnergy * this.ChargeLevel - this.ResEnergy) / (chgr.getECperSec() - this.EWperSec);
        }
    }

    /** @author Nguyen Nang Hung
     * This function determine charging point, then insert path to that charger
     * @param net
     * @return charging energy per second of the chosen charger
     */
    public double GoCharge(Network net, Map map, double cycleTime, ComputingCenter computingCenter){
        System.out.println("Clear pathNodeList in Gocharge");
        this.pathNodeList.clear();
        // choose charger location
        this.charger = getCharger(net);
        // Clean path, Find path to charging point
        map.cleanPath(this);
        getPath(location, map.point2node(this.charger.getLocation()) , map);
        // Determine charging time
        this.chargingTimeLeft = getChargeTime(this.charger, net, cycleTime, computingCenter);
        return this.charger.getECperSec();
    }

    public void getPath(Node startPoint, Node endPoint, Map map){
        if (startPoint.getX() != location.getX() || startPoint.getY() != location.getY()) {
            System.out.println("Error here getPath");
            System.exit(150);
        }
        this.pathNodeList.add(map.FindPath(startPoint, endPoint, this));
    }
}

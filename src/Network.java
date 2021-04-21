import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Network {
    private int numTaskTotal = 0;
    private int numManubot = Config.getInstance().getAsInteger("ManuBot_quantity");
    private int factorySize = Config.getInstance().getAsInteger("Factory_size");
    private String Gate_xcord_in = Config.getInstance().getAsString("Gate_xcord_in");
    private String Gate_ycord_in = Config.getInstance().getAsString("Gate_ycord_in");
    private String Gate_xcord_out = Config.getInstance().getAsString("Gate_xcord_out");
    private String Gate_ycord_out = Config.getInstance().getAsString("Gate_ycord_out");
    private String Charger_xcord = Config.getInstance().getAsString("Charger_xcord");
    private String Charger_ycord = Config.getInstance().getAsString("Charger_ycord");
    private String Shelf_xcord = Config.getInstance().getAsString("Shelf_xcord");
    private String Shelf_ycord = Config.getInstance().getAsString("Shelf_ycord");
    public static double Sim_time = Config.getInstance().getAsDouble("Simulation_time");
    public static double Cyc_time = Config.getInstance().getAsDouble("Cycle_time");

    // Objects section
    private List<TaskShelf> ShelfList = new ArrayList<>();
    private List<ManuBot> ManuList = new ArrayList<>();
    private List<Gate> GateInList = new ArrayList<>();
    private List<Gate> GateOutList = new ArrayList<>();
    public List<Charger> ChargerList = new ArrayList<>();

    // Get list
    public List<ManuBot> getChargingList(){
        List<ManuBot> chargeList = new ArrayList<>();
        for (ManuBot mb: this.ManuList){
            if (mb.getChargingTimeLeft() > 0){
                chargeList.add(mb);
            }
        }
        return chargeList;
    }

    public List<Gate> getGateInList() {
        return GateInList;
    }

    public List<Gate> getGateOutList() {
        return GateOutList;
    }

    public List<TaskShelf> getShelfList() {
        return ShelfList;
    }

    public List<ManuBot> getManuList() {
        return ManuList;
    }

    //  Queues section
    private List<Task> ArrivalTaskQueue = new ArrayList<>(); // Queue Sinh
    private List<Task> ActiveTaskQueue = new ArrayList<>();  // Queue Yeu cau

    // Objects interact section
    public void insertChargerList(Charger chgr) {this.ChargerList.add(chgr);}

    public void insertShelfList(TaskShelf ts){
        this.ShelfList.add(ts);
    }

    public void insertManuList(ManuBot mb){
        this.ManuList.add(mb);
    }

    public void insertGateInList(Gate gt){
        this.GateInList.add(gt);
    }

    public void insertGateOutList(Gate gt){ this.GateOutList.add(gt); }

    // Queues interact section
    public void insertArrivalTaskQueue(Task nt){
        this.ArrivalTaskQueue.add(nt);
    }

    public void insertActiveTaskQueue(Task nt){
        this.ActiveTaskQueue.add(nt);
    }

    public boolean isNewTask(){
        return this.ArrivalTaskQueue.isEmpty();
    }

    public boolean isRequesting(){
        return this.ActiveTaskQueue.isEmpty();
    }

    public int getNumShelf() {
        return this.ShelfList.size();
    }

    // Get task that is active by insert its ID
    public Task getActiveTask(int ID){
        for (Task i: this.ActiveTaskQueue){
            if (i.getID() == ID){
                return i;
            }
        }
        return null;
    }

    // Get task that is active by insert its ID
    public Task getArrivalTask(int ID){
        for (Task i: this.ArrivalTaskQueue){
            if (i.getID() == ID){
                return i;
            }
        }
        return null;
    }

    // ult
    public int getFactorySize() {
        return factorySize;
    }

    public int getTaskID() {
        this.numTaskTotal += 1;
        return this.numTaskTotal;
    }

    /**
     * @author Le Minh An
     * function choose the autoBot to get the new occured task
     * @param PointX: location of the packet
     * @param PointY: destination of the packet
     * @return id of the chosen autoBot
     */
    public int getAutoBotFromXTY(point PointX, point PointY){
        int chooseID1 = -1;             // The uncharged manubot can go to gate
        int chooseID2 = -1;             // The charging manubot can complete task
        int chooseID3 = -1;             // The charging manubot can go to gate
        double minEstimateTime1 = 100;
        double minEstimateTime2 = 100;
        double minEstimateTime3 = 100;
        for(ManuBot mb: this.ManuList) {
            if (mb.isTransporting == 1)
                continue;
            double lengthTX = mb.getLocationNow().getLength(PointX)*1.302;          // length from current manubot's location to Gate
            double lengthXTY = PointX.getLength(PointY)*1.302;                  // length from gate to shelf
            double estimateTimeX = lengthTX / mb.getSpeed();                             // estimate time to Gate
            double estimateTimeY = lengthXTY / mb.getSpeed();
            if (mb.getResEnergy() >= (estimateTimeX + estimateTimeY) * mb.getERperSec()) {           // Manubot can take task and complete mission
                if (mb.getChargingTimeLeft() == 0){
                    return mb.getId();
                }
                if (minEstimateTime2 > estimateTimeX + estimateTimeY) {
                    chooseID2 = mb.getId();
                    minEstimateTime2 = estimateTimeX + estimateTimeY;
                }
            }
            if (mb.getResEnergy() >= (estimateTimeX)*mb.getERperSec()){
                if (mb.getChargingTimeLeft() == 0){
                    if (minEstimateTime1 < estimateTimeX){
                        chooseID1 = mb.getId();
                        minEstimateTime1 = estimateTimeX;
                    }
                }
                else if (minEstimateTime3 > estimateTimeX) {
                    chooseID3 = mb.getId();
                    minEstimateTime3 = estimateTimeX;
                }
            }
        }
        if (chooseID1 != -1)
            return chooseID1;
        if (chooseID2 != -1)
            return chooseID2;
        if (chooseID3 != -1)
            return chooseID3;
        return -2;
    }


    // Constructor
    public Network(){
        // Initialize gates
        System.out.println("Initializing Gates...");
        String[] GateInX = Gate_xcord_in.split(";");
        String[] GateInY = Gate_ycord_in.split(";");
        String[] GateOutX = Gate_xcord_out.split(";");
        String[] GateOutY = Gate_ycord_out.split(";");
        for (int i = 0; i < GateInX.length; i++){
            point X = new point(Double.parseDouble(GateInX[i]), Double.parseDouble(GateInY[i]));
            Gate gt = new Gate(i, X, "In");
            insertGateInList(gt);
            System.out.println("Gate_in id{" + i + "} at location (" + X.getX() + "," + X.getY() + ") initialized");
        }
        for (int i =0; i < GateOutX.length; i++){
            point X = new point(Double.parseDouble(GateOutX[i]), Double.parseDouble(GateOutY[i]));
            Gate gt = new Gate(i, X, "Out");
            insertGateOutList(gt);
            System.out.println("Gate_out id{" + i + "} at location (" + X.getX() + "," + X.getY() + ") initialized");
        }

        // Initialize Charger
        System.out.println("Initializing Chargers...");
        String[] ChargerX = Charger_xcord.split(";");
        String[] ChargerY = Charger_ycord.split(";");
        for (int i=0; i < ChargerX.length; i++){
            point X = new point(Double.parseDouble(ChargerX[i]), Double.parseDouble(ChargerY[i]));
            Charger chgr = new Charger(i, X);
            insertChargerList(chgr);
            System.out.println("Charger id{" + i + "} at location (" + X.getX() + "," + X.getY() + ") initialized");
        }

        // Initialize shelves and setting ids
        System.out.println("Initializing Shelves...");
        String[] ShelfX = Shelf_xcord.split(";");
        String[] ShelfY = Shelf_ycord.split(";");
        for (int i = 0; i < ShelfX.length; i ++){
            point X = new point(Double.parseDouble(ShelfX[i]), Double.parseDouble(ShelfY[i]));
            TaskShelf ts = new TaskShelf(i, X);
            insertShelfList(ts);
            System.out.println("Shelf id{" + i + "} at location (" + X.getX() + "," + X.getY() + ") initialized");
        }

        // Initialize autoBots and setting ids
        System.out.println("Initializing AutoBots...");
        for (int i = 0; i < this.numManubot; i++){
            point X = new point(0,0);
            ManuBot mb = new ManuBot(i, X);
            insertManuList(mb);
            System.out.println("AutoBot id{" + i + "} at location (" + X.getX() + "," + X.getY() + ") initialized");
        }

    }

    public static void main(String[] args) {
        Network net = new Network();
        double timeNow = 0;
        System.out.println(LocalDate.now() + "; " + LocalTime.now());
        System.out.println("Starting Simulation...");
        while (timeNow < Sim_time){
            // Run gate
//            System.out.println("Time now: " + timeNow);
            for( Gate gts: net.GateInList ){
                gts.Running(net, timeNow, net.GateOutList.size());
            }
//            // For each task in Queue sinh, assign to autobots
//            for ( Task tks: this.ArrivalTaskQueue ){
//                for (TaskShelf tsh: this.ShelfList){
//                    if(!tsh.isFull()){
//                        tks.shelfLocation = tsh.getLocation();
//                        break;
//                    }
//                }
//                int AutoBotID = getAutoBotFromXTY(tks.getLocationNow(), tks.getShelfLocation());
//                ManuBot mb = this.ManuList.get(AutoBotID);
//                mb.workList.add(tks);
//            }
//            // Running autoBot in amount of time equals cycle time
//            for ( ManuBot mb: this.ManuList ) {
//                mb.Running(net, Cyc_time);
//            }
//
//            // Activate task shelves
//            for ( TaskShelf tsh: this.ShelfList){
//                for (ManuBot mb: this.ManuList){
//                    if (mb.getLocationNow() == tsh.getLocation()){
//                        if (mb.isTransporting == 1) {
//                            tsh.insertShelf(mb.workList.get(0));
//                            mb.workList.remove(0);
//                        }
//                        if (mb.isTransporting == -1) {
//                            mb.workList.add(0, tsh.getTask());
//                        }
//                    }
//                }
//            }
//
//            // For each task in Queue yeu cau, assign to autobots
//            for ( Task tks: this.ActiveTaskQueue ){
//                point gateOutpoint = net.GateOutList.get(tks.getGateOut()).getLocation();
//                int AutoBotID = getAutoBotFromXTY(tks.getShelfLocation(), gateOutpoint);
//                ManuBot mb = this.ManuList.get(AutoBotID);
//                mb.pathPointList.add(tks.getLocationNow());
//                mb.pathPointList.add(gateOutpoint);
//            }
//            this.ActiveTaskQueue.clear();
//            this.ArrivalTaskQueue.clear();
            timeNow += Cyc_time;
        }
    }

}


import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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
    public static double Cyc_time;

    // Objects list section
    private List<TaskShelf> ShelfList = new ArrayList<>();
    private List<ManuBot> ManuList = new ArrayList<>();
    private List<GateIn> GateInList = new ArrayList<>();
    private List<GateOut> GateOutList = new ArrayList<>();
    public List<Charger> ChargerList = new ArrayList<>();
    public List<Task> TaskList = new ArrayList<>();

    // Get list
    public List<ManuBot> getChargingList() {
        List<ManuBot> chargeList = new ArrayList<>();
        for (ManuBot mb : this.ManuList) {
            if (mb.getChargingTimeLeft() > 0) {
                chargeList.add(mb);
            }
        }
        return chargeList;
    }

    public List<GateIn> getGateInList() {
        return GateInList;
    }

    public List<GateOut> getGateOutList() {
        return GateOutList;
    }

    public List<TaskShelf> getShelfList() {
        return ShelfList;
    }

    public List<ManuBot> getManuList() {
        return ManuList;
    }

    public List<Charger> getChargerList() {
        return ChargerList;
    }

    //  Queues section
    private List<Task> ArrivalTaskQueue; // Queue Sinh
    private List<Task> ActiveTaskQueue;  // Queue Yeu cau

    public List<Task> getActiveTaskQueue() {
        return ActiveTaskQueue;
    }

    // Objects interact section
    public void insertChargerList(Charger chgr) {
        this.ChargerList.add(chgr);
    }

    public void insertShelfList(TaskShelf ts) {
        this.ShelfList.add(ts);
    }

    public void insertManuList(ManuBot mb) {
        this.ManuList.add(mb);
    }

    public void insertGateInList(GateIn gt) {
        this.GateInList.add(gt);
    }

    public void insertGateOutList(GateOut gt) {
        this.GateOutList.add(gt);
    }

    // Queues interact section
    public void insertArrivalTaskQueue(Task nt) {
        this.ArrivalTaskQueue.add(nt);
        this.TaskList.add(nt);
    }

    public int getArrivalListSize() {
        return this.ArrivalTaskQueue.size();
    }

    public int getActiveListSize() {
        return this.ActiveTaskQueue.size();
    }

    public int getNumShelf() {
        return this.ShelfList.size();
    }

    // Get task that is active by insert its ID
    public void getArrivalTask(Task tsk) {
        tsk.withAutoBot = true;
        this.ArrivalTaskQueue.remove(tsk);
    }

    // ult
    public int getFactorySize() {
        return factorySize;
    }

    public boolean isAllShelvesFull() {
        for (TaskShelf tsh : this.ShelfList) {
            if (!tsh.isFull()) {
                return false;
            }
        }
        return true;
    }

    public GateIn amIatGateIn(point location) {
        for (GateIn gti : this.GateInList) {
            if (gti.getLocation().equals(location))
                return gti;
        }
        return null;
    }

    public GateOut amIatGateOut(point location) {
        for (GateOut gto : this.GateOutList) {
            if (gto.getLocation().equals(location))
                return gto;
        }
        return null;
    }

    public TaskShelf amIatShelf(point location) {
        for (TaskShelf shf : this.ShelfList) {
            if (shf.getLocation().equals(location)) {
                System.out.println("Here at shelf");
                return shf;
            }
        }
        System.out.println(String.format("Location input (%.2f,%.2f) found no shelf", location.getX(), location.getY()));
        return null;
    }

    public void deleteActiveTask(Task tsk) {
        this.ActiveTaskQueue.remove(tsk);
    }

    // Constructor
    public Network() {
        // Initialize Queues
        this.ArrivalTaskQueue = new ArrayList<>();
        this.ActiveTaskQueue = new ArrayList<>();

        // Initialize gates
        System.out.println("Initializing Gates...");
        String[] GateInX = Gate_xcord_in.split(";");
        String[] GateInY = Gate_ycord_in.split(";");
        String[] GateOutX = Gate_xcord_out.split(";");
        String[] GateOutY = Gate_ycord_out.split(";");
        for (int i = 0; i < GateInX.length; i++) {
            point X = new point(Double.parseDouble(GateInX[i]), Double.parseDouble(GateInY[i]));
            GateIn gt = new GateIn(i, X);
            insertGateInList(gt);
            System.out.println("Gate_in id{" + i + "} at location (" + X.getX() + "," + X.getY() + ") initialized");
        }
        for (int i = 0; i < GateOutX.length; i++) {
            point X = new point(Double.parseDouble(GateOutX[i]), Double.parseDouble(GateOutY[i]));
            GateOut gt = new GateOut(i, X);
            insertGateOutList(gt);
            System.out.println("Gate_out id{" + i + "} at location (" + X.getX() + "," + X.getY() + ") initialized");
        }

        // Initialize Charger
        System.out.println("Initializing Chargers...");
        String[] ChargerX = Charger_xcord.split(";");
        String[] ChargerY = Charger_ycord.split(";");
        for (int i = 0; i < ChargerX.length; i++) {
            point X = new point(Double.parseDouble(ChargerX[i]), Double.parseDouble(ChargerY[i]));
            Charger chgr = new Charger(i, X);
            insertChargerList(chgr);
            System.out.println("Charger id{" + i + "} at location (" + X.getX() + "," + X.getY() + ") initialized");
        }

        // Initialize shelves and setting ids
        System.out.println("Initializing Shelves...");
        String[] ShelfX = Shelf_xcord.split(";");
        String[] ShelfY = Shelf_ycord.split(";");
        for (int i = 0; i < ShelfX.length; i++) {
            point X = new point(Double.parseDouble(ShelfX[i]), Double.parseDouble(ShelfY[i]));
            TaskShelf ts = new TaskShelf(i, X);
            insertShelfList(ts);
            System.out.println("Shelf id{" + i + "} at location (" + X.getX() + "," + X.getY() + ") initialized");
        }

    }

    public static void main(String[] args) {
        Network net = new Network();
        Map map = new Map(net);
        // Initialize autoBots and setting ids
        System.out.println("Initializing AutoBots...");
        for (int i = 0; i < net.numManubot; i++) {
            point X = new point(0, 0);
            ManuBot mb = new ManuBot(i, map.point2node(X), map.getSwitchStateNodes());
            net.insertManuList(mb);
            System.out.println("AutoBot id{" + i + "} at location (" + X.getX() + "," + X.getY() + ") initialized");
        }
        // Initial closeList for all manubots
        map.map4bot(net.ManuList);
        // Initial computational center
        ComputingCenter brain = new ComputingCenter(net);
        // Initial files
        Ultilis.initFiles(net);

        // Run simulator
        double timeNow = 0;
        System.out.println(LocalDate.now() + "; " + LocalTime.now());
        System.out.println("Starting Simulation...");

        List<Integer> GiveAwayList = new LinkedList<>();

        try {
            int check = 1;
            while (timeNow < Sim_time && check == 1){
                System.out.println("===========================================");
                System.out.println("Time step: " + Math.round(timeNow/Cyc_time));

                // For all tasks, check if any are activated
                Iterator<Task> iter = net.TaskList.listIterator();
                while (iter.hasNext()) {
                    Task tks = iter.next();
                    if (tks.activate(timeNow)) {
                        System.out.println("Active: task id {" + tks.getID() + "} at time:" + timeNow);
                        net.ActiveTaskQueue.add(tks);
                        net.ArrivalTaskQueue.remove(tks);
                        iter.remove();
                    }
                }

                // For all tasks in ActiveQueue, find them an autoBot
                iter = net.ActiveTaskQueue.listIterator();
                while (iter.hasNext()) {
                    Task tks = iter.next();
                    GateOut gateOut = net.GateOutList.get(tks.getGateOut());
                    int AutoBotID = brain.getAutoBotFromXTY(net, tks.getNextStop(), gateOut.getLocation());
                    if (AutoBotID >= 0) {
                        ManuBot mb = net.ManuList.get(AutoBotID);
                        mb.workList.add(tks);
                        if (!GiveAwayList.contains(tks.getID())) {
                            GiveAwayList.add(tks.getID());
                        }
                        else {
                            System.out.println("Already active once");
                            System.exit(100);
                        }
                        Ultilis.test_getAutoBot.write(String.format("%.2f\t%d\t%d\tACTIVE\n",
                                timeNow, tks.getID(), mb.getID()));
                        iter.remove();
                    }
                }


                // Run gate
                for( GateIn gts: net.GateInList ){
                    gts.Running(net, timeNow, net.GateOutList.size());
                }

                // For each task in Queue sinh, assign to autobots

                iter = net.ArrivalTaskQueue.listIterator();
                while (iter.hasNext()) {
                    Task tks = iter.next();
                    if (!net.isAllShelvesFull()) {
                        int shelfID = -1;
                        int count = 0;
                        do {
                            Random rand = new Random();
                            shelfID = rand.nextInt(net.getNumShelf());
                            count ++;
                        }while (net.getShelfList().get(shelfID).isFull() && count < 10);
                        if (count < 10) {
                            int AutoBotID = brain.getAutoBotFromXTY(net, tks.getNextStop(), net.getShelfList().get(shelfID).getLocation());
                            tks.setShelfLocation(net.getShelfList().get(shelfID).getLocation());
                            if (AutoBotID >= 0){
                                ManuBot mb = net.ManuList.get(AutoBotID);
                                mb.workList.add(tks);
                                System.out.println("Assigned task id{" + tks.getID() +"} to AutoBot id {" + mb.getID() + "} to Shelf id{" + shelfID + "}");
                                Ultilis.test_getAutoBot.write(String.format("%.2f\t%d\t%d\tARRIVE\n",
                                        timeNow, tks.getID(), mb.getID()));
                                iter.remove();
                            }
                        }
                    }
                }

                // Running autoBot in amount of time equals cycle time
                int botLive = 0;
                for (ManuBot mb : net.ManuList) {
                    if (mb.isFunctional()) {
                        mb.Running(net, map, Cyc_time, timeNow, brain);
                        Ultilis.manuPrintFile(mb, map, timeNow);
                        botLive ++;
                    }
                }

                if (botLive == 0) {
                    System.out.println("All bot is non-functional");
                    check = 0;
                }

                timeNow += Cyc_time;
            }

            Ultilis.dumpFinal(net);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Ultilis.closeFile();
        }

    }

}

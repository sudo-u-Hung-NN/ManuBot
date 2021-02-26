import java.util.ArrayList;
import java.util.List;

public class Network {
    private int numTaskTotal = 0;
    private int numShelf = Config.getInstance().getAsInteger("Shelf_quantity");
    private int numManubot = Config.getInstance().getAsInteger("ManuBot_quantity");
    private int numCharger = Config.getInstance().getAsInteger("Charger_quantity");
    private int factorySize = Config.getInstance().getAsInteger("Factory_size");
    private String Gate_xcord_in = Config.getInstance().getAsString("Gate_xcord_in");
    private String Gate_ycord_in = Config.getInstance().getAsString("Gate_ycord_in");
    private String Gate_xcord_out = Config.getInstance().getAsString("Gate_xcord_out");
    private String Gate_ycord_out = Config.getInstance().getAsString("Gate_ycord_out");

    // Objects section
    private List<TaskShelf> ShelfList = new ArrayList<>();
    private List<ManuBot> ManuList = new ArrayList<>();
    private List<Gate> GateInList = new ArrayList<>();
    private List<Gate> GateOutList = new ArrayList<>();

    //  Queues section
    private List<Task> ArrivalTaskQueue = new ArrayList<>();
    private List<Task> ActiveTaskQueue = new ArrayList<>();

    // Objects interact section
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

    public int getTaskID(){
        this.numTaskTotal += 1;
        return this.numTaskTotal;
    }

    // Constructor
    public Network(){
        // Initialize gates
        String[] GateInX = Gate_xcord_in.split(";");
        String[] GateInY = Gate_ycord_in.split(";");
        String[] GateOutX = Gate_xcord_out.split(";");
        String[] GateOutY = Gate_ycord_out.split(";");
        for (int i = 0; i < GateInX.length; i++){
            point X = new point(Double.parseDouble(GateInX[i]), Double.parseDouble(GateInY[i]));
            Gate gt = new Gate(X, "In");
            insertGateInList(gt);
        }
        for (int i =0; i < GateOutX.length; i++){
            point X = new point(Double.parseDouble(GateOutX[i]), Double.parseDouble(GateOutY[i]));
            Gate gt = new Gate(X, "Out");
            insertGateOutList(gt);
        }

        // Initialize shelves and setting ids
        for (int i = 0; i < this.numShelf; i ++){
            TaskShelf ts = new TaskShelf(i);
            insertShelfList(ts);
        }

        // Initialize autoBots and setting ids
        for (int j = 0; j < this.numManubot; j++){
            ManuBot mb = new ManuBot(j);
            insertManuList(mb);
        }

        // Setting locations to shelves

    }


}

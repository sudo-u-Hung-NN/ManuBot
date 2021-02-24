import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Network {
    private int numShelf = Config.getInstance().getAsInteger("Shelf_quantity");
    private int numManubot = Config.getInstance().getAsInteger("ManuBot_quantity");
    private int numCharger = Config.getInstance().getAsInteger("Charger_quantity");
    private int factorySize = Config.getInstance().getAsInteger("Factory_size");
    private String Door_xcord = Config.getInstance().getAsString("Door_xcord");
    private String Door_ycord = Config.getInstance().getAsString("Door_ycord");

    private List<TaskShelf> ShelfList = new ArrayList<>();
    private List<ManuBot> ManuList = new ArrayList<>();
    private List<point> DoorList = new ArrayList<>();

    public void insertShelfList(TaskShelf ts){
        this.ShelfList.add(ts);
    }

    public void insertManuList(ManuBot mb){
        this.ManuList.add(mb);
    }

    public void insertDoorList(point door){
        this.DoorList.add(door);
    }

    public int getFactorySize() {
        return factorySize;
    }

    public Network(){
        // Initialize doors
        String[] doorX = Door_xcord.split(";");
        String[] doorY = Door_ycord.split(";");
        for (int i = 0; i < 2; i++){
            double valueX = Double.parseDouble(doorX[i]);
            for (int j = 0; j < 2; j++){
                double valueY = Double.parseDouble(doorY[i]);
                point newdoor = new point(valueX, valueY);
                insertDoorList(newdoor);
            }
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

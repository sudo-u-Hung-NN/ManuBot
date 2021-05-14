import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TaskShelf {
    private int ID;
    private point location;
    private int capacityMax = Config.getInstance().getAsInteger("Shelf_capacity");
    private ArrayList<Task> TaskList = new ArrayList<Task>();

    // Methods
    public int getID() {return ID; }

    public void setID(int ID) {this.ID = ID; }

    public point getLocation() {return location; }

    public void setLocation(point location) {this.location = location; }

    public ArrayList<Task> getTaskList() {
        return TaskList;
    }

    public Task getTask(){ //
        Task t = this.TaskList.get(0);
        this.TaskList.remove(0);
        return t;
    }

    public boolean isEmpty(){
        return TaskList.isEmpty();
    }

    public boolean isFull(){return TaskList.size() == capacityMax;}

    public void store2Shelf(ManuBot mb, FileWriter file, double timeNow, Task tsk) throws IOException {
        this.TaskList.add(tsk);
        file.write(String.format("%.2f\t%d\t%d\t%d\t%d\t%s\n",
                timeNow, this.ID, this.TaskList.size(), tsk.getID(), mb.getId(), "RECV")
        );

    }

    public void takeFromShelf(ManuBot mb, FileWriter file, double timeNow, Task tsk) throws IOException {
    	this.TaskList.remove(tsk);
        file.write(String.format("%.2f\t%d\t%d\t%d\t%d\t%s\n",
                timeNow, this.ID, this.TaskList.size(), tsk.getID(), mb.getId(), "GIV")
        );
    }

    public void Running(){

    }

    // Constructor
    public TaskShelf(int ID,point location){
        setID(ID);
        setLocation(location);
    }
}

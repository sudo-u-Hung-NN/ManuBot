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

<<<<<<< HEAD
    public void insertShelf(Task t){ 
=======
    public void insertShelf(Task t){
        t.setLocationNow(this.getLocation());
>>>>>>> db190c46b7d4d8827516e742681020d8b74eb11b
        this.TaskList.add(t);
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

    public void requestTaking(Network net){
    	
    }

    public void Running(){

    }

    // Constructor
<<<<<<< HEAD
    public TaskShelf(int ID,point location){
        setID(ID);
        setLocation(location)
=======
    public TaskShelf(int ID, point location){
        setID(ID);
        this.location = location;
>>>>>>> db190c46b7d4d8827516e742681020d8b74eb11b
    }

}

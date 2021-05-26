import java.io.FileWriter;
import java.io.IOException;

public class TaskShelf extends TaskHolder{
    // Methods

    public void store2Shelf(ManuBot mb, FileWriter file, double timeNow, Task tsk) throws IOException {
        this.TaskList.add(tsk);
        tsk.withAutoBot = false;
        file.write(String.format("%.2f\t%d\t%d\t%d\t%d\t%s\n",
                timeNow, this.getID(), this.TaskList.size(), tsk.getID(), mb.getID(), "RECV")
        );
    }

    public void takeFromShelf(ManuBot mb, FileWriter file, double timeNow, Task tsk) throws IOException {
    	this.TaskList.remove(tsk);
    	tsk.withAutoBot = true;
        file.write(String.format("%.2f\t%d\t%d\t%d\t%d\t%s\n",
                timeNow, this.getID(), this.TaskList.size(), tsk.getID(), mb.getID(), "GIV")
        );
    }

    // Constructor
    public TaskShelf(int ID,point location){
        setID(ID);
        setLocation(location);
    }
}

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskHolder extends ManuObject{
    protected List<Task> TaskList = new ArrayList<>(); /* Storing tasks */
    protected final int MAX_CAPACITY = Config.getInstance().getAsInteger("Shelf_capacity");

    protected void insertTaskList(Task tk){
        if (TaskList.size() == MAX_CAPACITY){
            System.out.println("Object id{" + this.objectId + "} is full");
        }
        this.TaskList.add(tk);
    }

    protected List<Task> getTaskList() {
        return this.TaskList;
    }

    protected boolean isEmpty(){
        return this.TaskList.size() == 0;
    }

    protected boolean isFull(){
        return this.TaskList.size() == MAX_CAPACITY;
    }

    protected boolean isContain(Task tsk) {
        return this.TaskList.contains(tsk);
    }

    protected Task popTask(int TaskID){
        Iterator<Task> tsk = this.TaskList.iterator();
        Task found = null;
        while (tsk.hasNext()){
            found = tsk.next();
            if (found.getID() == TaskID){
                break;
            }
        }
        this.TaskList.remove(found);
        return found;
    }
}

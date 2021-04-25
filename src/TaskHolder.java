import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskHolder extends ManuObject{
    protected List<Task> TaskList = new ArrayList<>(); /* Storing tasks */
    protected final int MAX_CAPACITY = 100;

    protected void insertTaskList(Task tk){
        if (TaskList.size() == MAX_CAPACITY){
            System.out.println("Object id{" + this.objectId + "} is full");
        }
        this.TaskList.add(tk);
    }

    protected boolean isEmpty(){
        return this.TaskList.size() == 0;
    }

    protected boolean isFull(){
        return this.TaskList.size() == MAX_CAPACITY;
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

//    public static void main(String[] args) {
//        TaskHolder th = new TaskHolder();
//        th.insertTaskList(new Task(3, 0, 6));
//        th.insertTaskList(new Task(5, 0, 6));
//        th.insertTaskList(new Task(2, 0, 6));
//        th.insertTaskList(new Task(9, 0, 6));
//        Task x = th.popTask(5);
//        System.out.println(th.TaskList.size());
//        System.out.println(x.getGateOut());
//    }
}

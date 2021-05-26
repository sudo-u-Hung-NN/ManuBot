import java.io.FileWriter;
import java.io.IOException;

public class GateOut extends ManuObject {
    private static int count = 0;

    public void recieveTask(Task tsk, ManuBot mb, FileWriter file, double timeNow) throws IOException {
        count ++;
        System.out.println("Receive task id {" + tsk.getID() + "}");
        file.write(String.format("%.2f\t%d\t%d\t%d\t%d\n",
                timeNow, this.getID(), mb.getID(), tsk.getID(), GateOut.count));
    }

    // Constructor
    public GateOut(int ID, point X){
        setID(ID);
        setLocation(X);
    }
}

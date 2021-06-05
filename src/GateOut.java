import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GateOut extends ManuObject {
    public static int count = -1;
    public static double [] timeRecords = new double[10000];

    public void recieveTask(Task tsk, ManuBot mb, FileWriter file, double timeNow) throws IOException {
        count ++;
        timeRecords[count] = timeNow;
        System.out.println("Receive task id {" + tsk.getID() + "}");
        file.write(String.format("%.2f\t%d\t%d\t%d\t%d\n",
                timeNow, this.getID(), mb.getID(), tsk.getID(), GateOut.count + 1));
    }

    // Constructor
    public GateOut(int ID, point X){
        setID(ID);
        setLocation(X);
    }
}

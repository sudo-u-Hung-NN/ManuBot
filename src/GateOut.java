import java.io.FileWriter;
import java.io.IOException;

public class GateOut {
    private point location;
    private int gateID = 0;
    private static int count = 0;

    public void setGateID(int gateID) {
        this.gateID = gateID;
    }

    public int getGateID() {
        return gateID;
    }

    public void setLocation(point location){
        this.location = location;
    }

    public point getLocation() {
        return location;
    }

    public void recieveTask(Task tsk, ManuBot mb, FileWriter file, double timeNow) throws IOException {
        count ++;
        System.out.println("Receive task id {" + tsk.getID() + "}");
        file.write(String.format("%.2f\t%d\t%d\t%d\t%d\n",
                timeNow, this.gateID, mb.getId(), tsk.getID(), GateOut.count));
    }
    //hello
    // Constructor
    public GateOut(int ID, point X){
        setGateID(ID);
        setLocation(X);
    }
}

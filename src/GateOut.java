import java.util.Random;

public class GateOut {
    private point location;
    private int gateID = 0;

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

    public void recieveTask(Task tsk){
        System.out.println("Receive task id...");
    }

    // Constructor
    public GateOut(int ID, point X){
        setGateID(ID);
        setLocation(X);
    }
}

public class Charger {
    private int ID;
    private point location;
    private double ECperSec = Config.getInstance().getAsDouble("ECperSec");
    public boolean isOccupied = false;

    public int getID() {
        return ID;
    }

    public point getLocation() {
        return location;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setLocation(point location) {
        this.location = location;
    }

    public double getECperSec() {
        return ECperSec;
    }

    // Construction
    public Charger(int ID, point location){
        setID(ID);
        setLocation(location);
    }
}

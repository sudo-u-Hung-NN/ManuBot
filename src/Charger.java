public class Charger extends ManuObject{
    private final double ECperSec = Config.getInstance().getAsDouble("ECperSec");
    public boolean isOccupied = false;

    public double getECperSec() {
        return ECperSec;
    }

    // Construction
    public Charger(int ID, point location){
        setID(ID);
        setLocation(location);
    }
}

public class ManuObject implements Active{
    protected int objectId;
    protected point location;

    public int getID() {
        return objectId;
    }

    public void setID(int objectId) {
        this.objectId = objectId;
    }

    public point getLocation() {
        return location;
    }

    public void setLocation(point location) {
        this.location = location;
    }

    @Override
    public void Running() {}
}

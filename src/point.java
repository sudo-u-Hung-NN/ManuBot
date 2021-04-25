import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class point {
    private double x;
    private double y;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getLength(){
        return sqrt(x*x + y*y);
    }

    public double getLength(point X){
        return sqrt( pow(this.getX() - X.getX(), 2) + pow(this.getY() - X.getY(), 2));
    }

    // Constructor: khởi tạo tọa độ 
    public point(double x, double y){
        setX(x);
        setY(y);
    }

    public point (){

    }
}

import java.util.Objects;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class point {
    protected double x;
    protected double y;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        point point = (point) o;
        return point.x == x && point.y == y;
    }

    // Constructor: khởi tạo tọa độ
    public point(double x, double y){
        setX(x);
        setY(y);
    }

    public point (){

    }
}

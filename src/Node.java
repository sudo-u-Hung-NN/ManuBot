import java.util.ArrayList;
import java.util.List;

public class Node {
	private point location;
	private boolean isWalkable = true;
	private boolean isObstace = false;
	private List<Node> next = new ArrayList<>();
	private double H;
	private double G;
	private double F;
	private Node cameFrom;
	public Node(point location) {
		this.location = location;
	}
	
	public Node() {
		point t = new point(0,0);
		this.location = t;
	}

	public point getLocation() {
		return location;
	}
	public void setLocation(point location) {
		this.location = location;
	}
	public void setLocation(int X,int Y) {
		double doubleX = X;
		double doubleY = Y;
		this.location.setX(doubleX);
		this.location.setY(doubleY);
	}
	
	public boolean isWalkable() {
		return isWalkable;
	}
	public void setWalkable(boolean isWalkable) {
		this.isWalkable = isWalkable;
	}
	public List<Node> getNext() {
		return this.next;
	}
	public void setNext(Node next) {
		this.next.add(next);
	}
	public void setNext(List<Node> next) {
		this.next = next;
	}
	public double getH() {
		return H;
	}
	public void setH(double h) {
		H = h;
	}
	public double getG() {
		return G;
	}
	public void setG(double g) {
		G = g;
	}
	public double getF() {
		return F;
	}
	public void setF(double f) {
		F = f;
	}
	public Node getCameFrom() {
		return cameFrom;
	}
	public void setCameFrom(Node cameFrom) {
		this.cameFrom = cameFrom;
		
	}

	public boolean isObstace() {
		return isObstace;
	}

	public void setObstace(boolean isObstace) {
		this.isObstace = isObstace;
	}
	
	
	
}

import java.util.ArrayList;
import java.util.List;

public class Node extends point{
	private boolean isWalkable = true;
	private boolean isObstacle = false;
	private List<Node> next = new ArrayList<>();
	private double H;
	private double G;
	private double F;

	public Node() {
		super();
	}

	public Node(double x, double y) {
		super(x,y);
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

	public boolean isObstacle() {
		return isObstacle;
	}

	public void setObstacle(boolean isObstacle) {
		this.isObstacle = isObstacle;
	}
	
	
	
}

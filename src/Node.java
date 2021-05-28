import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Node extends point{
	private boolean isWalkable = true;
	private boolean isObstacle = false;
	private List<Node> next = new ArrayList<>();
	private manuType type;
	public int id;

	public void setType(manuType type) {
		this.type = type;
	}

	public manuType getType() {
		return type;
	}

	public boolean isAtShelf() {
		return this.type == manuType.SHELF;
	}

	public boolean isAtGateIn() {
		return this.type == manuType.GATE_IN;
	}

	public boolean isAtGateOut() {
		return this.type == manuType.GATE_OUT;
	}

	public boolean isAtCharger() {
		return this.type == manuType.CHARGER;
	}

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

	public boolean isObstacle() {
		return isObstacle;
	}

	public void setObstacle(boolean isObstacle) {
		this.isObstacle = isObstacle;
	}
}

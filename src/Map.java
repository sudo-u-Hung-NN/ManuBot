import java.util.ArrayList;
import java.util.*;


public class Map {
	
	public static final int mapSize = Config.getInstance().getAsInteger("Map_size");
	private Node map[][] = new Node[mapSize][mapSize];
	private Node startPoint;
	private Node endPoint;
	private List<Node> closeList =  new ArrayList<>();;
	private String Obstacle_xcord = Config.getInstance().getAsString("Shelf_xcord");
    private String Obstacle_ycord = Config.getInstance().getAsString("Shelf_ycord");
	private int minDistance = 0;
	private static final int factorySize = Config.getInstance().getAsInteger("Factory_size");
	private static final double distance = (double) factorySize / mapSize;
	
	public Map() {
	
		for (int i = 0 ; i < mapSize ; i++)
			for (int j = 0; j < mapSize; j++)
			{
				point newPoint = new point(distance * i , distance * j);
				map[i][j] = new Node(newPoint);

			}
		
		String [] ObstacleX = Obstacle_xcord.split(";");
        String [] ObstacleY = Obstacle_ycord.split(";");
        for (int i = 0; i < ObstacleX.length; i ++)
        	for (int j = 0; j < ObstacleY.length; j++)
        	{
        		Double xcord = Double.parseDouble(ObstacleX[i]);
        		Double ycord = Double.parseDouble(ObstacleY[j]);
        		
        		int nodeX = (int) (xcord.doubleValue() / distance) ;
        		int nodeY = (int) (ycord.doubleValue() / distance);
        		
        		map[nodeX][nodeY].setWalkable(false);
        		map[nodeX][nodeY].setObstace(true);
        	}
		
		for (int i = 0; i < mapSize; i++)
			for (int j = 0 ; j < mapSize; j++)
			{
				if (i - 1 >= 0)
					map[i][j].setNext(map[i-1][j]);
				if (j - 1 >= 0 )
					map[i][j].setNext(map[i][j-1]);
				if (i + 1 < mapSize )
				{
					map[i][j].setNext(map[i+1][j]);
				}
				if (j + 1 < mapSize)
					map[i][j].getNext().add(map[i][j+1]);
			}
	}

	public void printMapInformation(){
		System.out.println(String.format("Map size: %d", mapSize));
		System.out.println(String.format("Factory size: %d", factorySize));
		System.out.println(String.format("Number of nodes: %d", mapSize*mapSize));
//		System.out.println(String.format("Number of obstacles: %d"));
	}
	

	public Node FindPath(List<point> outList)
	{
		if (startPoint.getLocation().equals(endPoint.getLocation()))
		{
			outList = new ArrayList<>();
            return endPoint;
		}
		
		// Set startPoint and endPoint is walkable 
		startPoint.setWalkable(true);
		endPoint.setWalkable(true);
		
		closeList.add(startPoint);
		Node nowPoint = startPoint;
		Node nextNode = new Node();
		
		double minF = 1000 * Math.sqrt(2);
		for (Node near: nowPoint.getNext()) 
			if (!closeList.contains(near) && near.isWalkable())
				{
//					System.out.println(near.isWalkable());
					point temp1 = near.getLocation();
					point temp2 = nowPoint.getLocation();
					near.setG(nowPoint.getG() + temp1.getLength(temp2));
					
					point temp3 = endPoint.getLocation();
					near.setH(temp1.getLength(temp3));
			
					near.setF(near.getG() + near.getH());
					
					if (near.getF() < minF)
					{
						minF = near.getF();
						nextNode.setLocation(near.getLocation());
						nextNode.setNext(near.getNext());
						nextNode.setF(near.getF());
						nextNode.setH(near.getH());
						nextNode.setG(near.getG());
					}
				}
		closeList.add(nextNode);
		minDistance += nextNode.getG();
		outList.add(nextNode.getLocation());
		if (nextNode.getLocation().equals(endPoint.getLocation()))
		{
			if (startPoint.isObstace())
				startPoint.setWalkable(false);
			if (endPoint.isObstace())
				endPoint.setWalkable(false);
			closeList.clear();
		}
		return nextNode;
	}
	
	public int getMinDistance()
	{
		return this.minDistance;
	}

	public Node getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(point startPoint) {
		this.startPoint = this.pointToNode(startPoint);
		this.startPoint.setG(0);
		
	}

	public Node getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(point endPoint) {
		this.endPoint = this.pointToNode(endPoint);
		this.endPoint.setH(0);
	}

	public Node[][] getMap() {
		return map;
	}

	public void setMap(Node[][] map) {
		this.map = map;
	}


	public double getDistance() {
		return distance;
	}


	public Node pointToNode(point Point) {
		double x = Point.getX();
		double y = Point.getY();
		int nodeX = (int) (x / distance);
		int nodeY = (int) (y / distance);
		return map[nodeX][nodeY];
	}
	

}
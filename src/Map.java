import java.util.ArrayList;
import java.util.*;


public class Map {
	
	public static final int mapSize = Config.getInstance().getAsInteger("Map_size");
	private Node map[][] = new Node[mapSize+1][mapSize+1];
	private Node startPoint;
	private Node endPoint;
	private List<Node> closeList =  new ArrayList<>();
//	private String Obstacle_xcord = Config.getInstance().getAsString("Shelf_xcord");
//    private String Obstacle_ycord = Config.getInstance().getAsString("Shelf_ycord");
	private int minDistance = 0;
	private static final int factorySize = Config.getInstance().getAsInteger("Factory_size");
	private static final double distance = (double) factorySize / mapSize;
	private List<Node> switchStateNodes = new ArrayList<>();

	public Node point2node(point input){
		int nodeX = (int) (input.getX()/distance);
		int nodeY = (int) (input.getY()/distance);
		return this.map[nodeX][nodeY];
	}
	
	public Map(Network network) {
	
		for (int i = 0 ; i < mapSize+1; i++)
			for (int j = 0; j < mapSize+1; j++) {
				map[i][j] = new Node(distance * i, distance * j );
			}
		
//		String [] ObstacleX = Obstacle_xcord.split(";");
//        String [] ObstacleY = Obstacle_ycord.split(";");
//        for (int i = 0; i < ObstacleX.length; i ++)
//        	for (int j = 0; j < ObstacleY.length; j++)
//        	{
//        		Double xcord = Double.parseDouble(ObstacleX[i]);
//        		Double ycord = Double.parseDouble(ObstacleY[j]);
//
//        		int nodeX = (int) (xcord.doubleValue() / distance) ;
//        		int nodeY = (int) (ycord.doubleValue() / distance);
//
//        		map[nodeX][nodeY].setWalkable(false);
//        		map[nodeX][nodeY].setObstacle(true);
//        	}

        for (TaskShelf tsh : network.getShelfList()){
        	int nodeX = (int)(tsh.getLocation().getX()/distance);
        	int nodeY = (int)(tsh.getLocation().getY()/distance);

        	map[nodeX][nodeY].setType(manuType.SHELF);
			map[nodeX][nodeY].setWalkable(false);
			map[nodeX][nodeY].setObstacle(true);

			switchStateNodes.add(map[nodeX][nodeY]);
		}

        for (GateIn gti : network.getGateInList()){
			int nodeX = (int)(gti.getLocation().getX()/distance);
			int nodeY = (int)(gti.getLocation().getY()/distance);

			if (map[nodeX][nodeY] == null){
				System.out.println("Error here x = " + nodeX + ", y = " + nodeY);
				System.exit(-5);
			}

			map[nodeX][nodeY].setType(manuType.GATE_IN);
			switchStateNodes.add(map[nodeX][nodeY]);
		}

		for (GateOut gto : network.getGateOutList()){
			int nodeX = (int)(gto.getLocation().getX()/distance);
			int nodeY = (int)(gto.getLocation().getY()/distance);

			map[nodeX][nodeY].setType(manuType.GATE_OUT);
			switchStateNodes.add(map[nodeX][nodeY]);
		}

		for (Charger chr : network.getChargerList()){
			int nodeX = (int)(chr.getLocation().getX()/distance);
			int nodeY = (int)(chr.getLocation().getY()/distance);

			map[nodeX][nodeY].setType(manuType.CHARGER);
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

	public List<Node> getSwitchStateNodes(){
		return this.switchStateNodes;
	}

	public void printMapInformation(){
		System.out.println(String.format("Map size: %d", mapSize));
		System.out.println(String.format("Factory size: %d", factorySize));
		System.out.println(String.format("Number of nodes: %d", mapSize*mapSize));
//		System.out.println(String.format("Number of obstacles: %d"));
	}
	

	public Node FindPath(List<Node> outList)
	{
		if (startPoint.equals(endPoint))
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
					near.setG(nowPoint.getG() + near.getLength(nowPoint));

					near.setH(near.getLength(endPoint));
			
					near.setF(near.getG() + near.getH());
					
					if (near.getF() < minF)
					{
						minF = near.getF();
						nextNode = near;
					}
				}
		closeList.add(nextNode);
		minDistance += nextNode.getG();
		outList.add(nextNode);
		if (nextNode.equals(endPoint))
		{
			if (startPoint.isObstacle())
				startPoint.setWalkable(false);
			if (endPoint.isObstacle())
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
		this.startPoint = this.point2node(startPoint);
		this.startPoint.setG(0);
		
	}

	public Node getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(point endPoint) {
		this.endPoint = this.point2node(endPoint);
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
}
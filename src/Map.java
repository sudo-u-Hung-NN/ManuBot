import java.util.ArrayList;
import java.util.*;


public class Map {
	
	public static final int maxRow = 1001;
	public static final int maxColumn = 1001;
	private Node map[][] = new Node[maxRow][maxColumn];
	private Node startPoint;
	private Node endPoint;
	private List<Node> closeList =  new ArrayList<>();;
	private String Obstacle_xcord = Config.getInstance().getAsString("Shelf_xcord");
    private String Obstacle_ycord = Config.getInstance().getAsString("Shelf_ycord");
	private int minDistance = 0;
	
	public Map() {
		
		for (int i = 0 ; i < maxRow ; i++)
			for (int j = 0; j < maxColumn; j++)
			{
				point newPoint = new point(i,j);
				map[i][j] = new Node(newPoint);
			}
		
		String [] ObstacleX = Obstacle_xcord.split(";");
        String [] ObstacleY = Obstacle_ycord.split(";");
        for (int i = 0; i < ObstacleX.length; i ++)
        	for (int j = 0; j < ObstacleY.length; j++)
        	{
        		int xcord = Integer.parseInt(ObstacleX[i]);
        		int ycord = Integer.parseInt(ObstacleY[j]);
        		map[xcord][ycord].setWalkable(false);
        		map[xcord][ycord].setObstace(true);
        	}
		
		for (int i = 0; i < maxRow; i++)
			for (int j = 0 ; j < maxColumn; j++)
			{
				if (i - 1 >= 0)
					map[i][j].setNext(map[i-1][j]);
				if (j - 1 >= 0 )
					map[i][j].setNext(map[i][j-1]);
				if (i + 1 < maxRow )
				{
					map[i][j].setNext(map[i+1][j]);
				}
				if (j + 1 < maxColumn)
					map[i][j].getNext().add(map[i][j+1]);
			}
		
	}
	

	public void FindPath(List<point> outList, Node start, Node end)
	{
		if (start.getLocation().equals(end.getLocation()))
		{
			outList = new ArrayList<>();
            return;
		}
		start.setWalkable(true);
		end.setWalkable(true);
		this.closeList.add(start);
		Node nowPoint = start;
		//System.out.println("Near: " + nowPoint.getNext().size());
		Node nextNode = new Node();
		
		double minF = 1000 * Math.sqrt(2);
		for (Node near: nowPoint.getNext()) 
			if (!closeList.contains(near) && near.isWalkable())
				{
					
					near.setCameFrom(nowPoint);
					
					point temp1 = near.getLocation();
					point temp2 = nowPoint.getLocation();
					near.setG(nowPoint.getG() + temp1.getLength(temp2));
					
					point temp3 = end.getLocation();
					near.setH(temp1.getLength(temp3));
			
					near.setF(near.getG() + near.getH());
//					System.out.println("Near:"+ near.getLocation().getX() + " " + near.getLocation().getY()+ "  "
//					+near.getG() + " " +near.getH() + " " +near.getF());
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
		this.closeList.add(nextNode);
		this.minDistance += nextNode.getG();
		outList.add(nextNode.getLocation());
		//System.out.println(nextNode.getLocation().getX() +" " +  nextNode.getLocation().getY());
		if (!nextNode.getLocation().equals(end.getLocation()))
		{
//			Scanner sc = new Scanner(System.in);
//			String strName = sc.nextLine();
//			if (strName.equals(" "))
				FindPath(outList,nextNode,end);
		}
		else 
		{
			if (this.startPoint.isObstace())
				this.startPoint.setWalkable(false);
			if (this.endPoint.isObstace())
				this.endPoint.setWalkable(false);
			return;
		}
	}
	
	public int getMinDistance()
	{
		return this.minDistance;
	}

	public Node getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Node startPoint) {
		double i = startPoint.getLocation().getX();
		double j = startPoint.getLocation().getY();

		this.startPoint = map[(int)((double)i)][(int)((double)j)];
		this.startPoint.setG(0);
		
	}

	public Node getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Node endPoint) {
		this.endPoint = endPoint;
		this.endPoint.setH(0);
	}

	public Node[][] getMap() {
		return map;
	}

	public void setMap(Node[][] map) {
		this.map = map;
	}
	
}
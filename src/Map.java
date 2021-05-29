import java.io.FileWriter;
import java.util.ArrayList;
import java.util.*;


public class Map {
	public static final int mapSize = Config.getInstance().getAsInteger("Map_size");
	private final Node[][] map = new Node[mapSize+1][mapSize+1];
	private static final int factorySize = Config.getInstance().getAsInteger("Factory_size");
	private static final int speed = Config.getInstance().getAsInteger("speed");
	private static final double simTime = Config.getInstance().getAsInteger("Simulation_time");
	private static final double distance = (double) factorySize / mapSize;
	private final List<Node> switchStateNodes = new ArrayList<>();

	private HashMap<Integer, List<Node>> closeListDictionary = new HashMap<>();

	public Node point2node(point input){
		if (input == null) {
			System.out.println("Null input point2node function\n");
			System.exit(-1);
		}
		int nodeX = (int) (input.getX()/distance) ;
		int nodeY = (int) (input.getY()/distance) ;
		if (map[nodeX][nodeY] == null) {
			System.out.println("Null return in point2node function: (nodeX, nodeY) = (" + nodeX + ", " + nodeY + ")");
			System.exit(-1);
		}
		return this.map[nodeX][nodeY];
	}
	
	public Map(Network network) {
	
		for (int i = 0 ; i < mapSize+1; i++)
			for (int j = 0; j < mapSize+1; j++) {
				map[i][j] = new Node(distance * i, distance * j );
				map[i][j].setType(manuType.NONE);
				map[i][j].id = i * (mapSize + 1) + j;
			}

        for (TaskShelf tsh : network.getShelfList()) {
			int nodeX = (int)(tsh.getLocation().getX() / distance);
			int nodeY = (int)(tsh.getLocation().getY() / distance);

			map[nodeX][nodeY].setType(manuType.SHELF);
			map[nodeX][nodeY].setWalkable(false);
			map[nodeX][nodeY].setObstacle(true);

			switchStateNodes.add(map[nodeX][nodeY]);
			System.out.println(String.format("Shelf at node (%d, %d)", nodeX, nodeY));
		}

		for (Charger chr : network.getChargerList()){
			int nodeX = (int)(chr.getLocation().getX()/distance);
			int nodeY = (int)(chr.getLocation().getY()/distance);

			map[nodeX][nodeY].setType(manuType.CHARGER);
			map[nodeX][nodeY].setWalkable(false);
			map[nodeX][nodeY].setObstacle(true);
			System.out.println(String.format("Charger at node (%d, %d)", nodeX, nodeY));
		}

		for (GateIn gti : network.getGateInList()){
			int nodeX = (int)(gti.getLocation().getX()/distance);
			int nodeY = (int)(gti.getLocation().getY()/distance);

			map[nodeX][nodeY].setType(manuType.GATE_IN);
			switchStateNodes.add(map[nodeX][nodeY]);
			map[nodeX][nodeY].setWalkable(false);
			map[nodeX][nodeY].setObstacle(true);
			System.out.println(String.format("Gate In at node (%d, %d)", nodeX, nodeY));
		}

		for (GateOut gto : network.getGateOutList()){
			int nodeX = (int)(gto.getLocation().getX()/distance);
			int nodeY = (int)(gto.getLocation().getY()/distance);

			map[nodeX][nodeY].setType(manuType.GATE_OUT);
			switchStateNodes.add(map[nodeX][nodeY]);
			map[nodeX][nodeY].setWalkable(false);
			map[nodeX][nodeY].setObstacle(true);
			System.out.println(String.format("Gate Out at node (%d, %d)", nodeX, nodeY));
		}

		for (int i = 0; i < mapSize+1; i++)
			for (int j = 0 ; j < mapSize+1; j++)
			{
				if (i - 1 >= 0)
					map[i][j].setNext(map[i-1][j]);
				if (j - 1 >= 0 )
					map[i][j].setNext(map[i][j-1]);
				if (i + 1 < mapSize+1 )
					map[i][j].getNext().add(map[i+1][j]);
				if (j + 1 < mapSize+1)
					map[i][j].getNext().add(map[i][j+1]);
			}

		Network.Cyc_time = distance * 1.0/speed;
		printMapInformation();
	}

	public List<Node> getSwitchStateNodes(){
		return this.switchStateNodes;
	}

	public void printMapInformation(){
		try {
			String outputFile = "Results/MapInformation.txt";
			FileWriter fileout = new FileWriter(outputFile, false);
			fileout.write("Map size: " + mapSize + "\n");
			fileout.write("Factory size: " + factorySize + "\n");
			fileout.write("Number of nodes: " + (mapSize + 1)*(mapSize + 1) + "\n");
			fileout.write("Virtual map size: " + mapSize * distance + "\n");
			fileout.write("Number of time step total: " + simTime * speed * 1.0/distance + "\n");
			fileout.write("Map detail\n");
			for (int i = 0; i < mapSize + 1; i ++) {
				int check = 0;
				for (int j = 0; j < mapSize + 1; j ++) {
					if (map[i][j].getType() != manuType.NONE) {
						check = 1;
						fileout.write(String.format("[%4d, %4d]. %s\t", i, j, map[i][j].getType()));
					}
				}
				if (check == 1) {
					fileout.write("\n");
				}
			}
			fileout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done print map!!!");
	}

	public void map4bot(List<ManuBot> ManuList) {
		for (ManuBot mb : ManuList) {
			List<Node> closeList = new ArrayList<>();
			this.closeListDictionary.put(mb.getID(), closeList);
		}
	}
	/**
	 * Find the neighbor node whose distance to the destination is the shortest
	 * and MUST BE shorter than that of current node to the destination
	 * @param mb: current manubot
	 * @param currentNode
	 * @param destination
	 * @return next node
	 */
	public Node FindPath(Node currentNode, Node destination, ManuBot mb)
	{
		// Get CloseList of the manubot
		List<Node> closeList = this.closeListDictionary.get(mb.getID());

		System.out.println(String.format("Here in Findpath, current location: (%.2f, %.2f)", currentNode.getX(), currentNode.getY()));

		Node nextNode = null;
		System.out.print("Neighbor (x, y) = ");
		for (Node near: currentNode.getNext()) {
			System.out.print(String.format("\t(%.2f, %.2f)", near.getX(), near.getY()));
		}
		double currentDist = 1000;
		System.out.print("\nConsidering: ");
		for (Node near: currentNode.getNext()) {
			System.out.print(String.format("\t(%.2f, %.2f)", near.getX(), near.getY()));
			if ((Math.abs(near.getX() - destination.getX()) < 0.001 && Math.abs(near.getY() - destination.getY()) < 0.001) || near.id == destination.id) {
				System.out.println("\nReach destination");
				closeList.clear();
				return near;
			}
			//
			else if (!closeList.contains(near) && near.isWalkable()) {
				double neighborDist = near.getLength(destination);
				System.out.print(String.format("\t%.5f V", neighborDist));
				if (Double.compare(currentDist, neighborDist) == 1) {
					currentDist = neighborDist;
					nextNode = near;
				}
			}
			else {
				System.out.print("\tX");
				if(near.isObstacle()){
					System.out.print("Z");
				}
			}
		}
		if (nextNode == null) {
			System.out.println("\nReturn null in FindPath");
			System.exit(-1);
		}
		System.out.println(String.format("\nFindPath: next go to this location: (%.2f, %.2f)\n", nextNode.getX(), nextNode.getY()));
		closeList.add(currentNode);
		return nextNode;
	}

	public void cleanPath(ManuBot mb) {
		List<Node> closeList = this.closeListDictionary.get(mb.getID());
		closeList.clear();
	}
}
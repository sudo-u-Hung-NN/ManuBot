//import java.util.LinkedList;
//import java.util.List;
//
//public class Test {
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		Map newMap = new Map();
//
//		point point1 = new point(99, 100);
//		point point2 = new point(101, 100);
//		Node newNode ;
//
//		newMap.setStartPoint(point1);
//		newMap.setEndPoint(point2);
//		List<point> toDest = new LinkedList<>();
//		do
//		{
//			newNode = newMap.FindPath(toDest);
//			newMap.setStartPoint(newNode.getLocation());
//		} while (!newNode.equals(newMap.getEndPoint()));
//		System.out.println("New map completed");
//		System.out.println(toDest.size());
//		for (point p : toDest)
//		{
//			System.out.println(p.getX()+ " - " +p.getY());
//
//		}
//	}
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		Map newMap = new Map();
//		Node startPoint = newMap.getMap()[300][300];
//
//		Node endPoint = newMap.getMap()[100][100];
//
//		newMap.setStartPoint(startPoint);
//		newMap.setEndPoint(endPoint);
//		List<point> toDest = new LinkedList<>();
//		newMap.FindPath(outList, startPoint,endPoint);
//		System.out.println(outList.size());
//		for (int i = 1; i < outList.size(); i = i+10)
//			System.out.println(outList.get(i).getLocation().getX() + " " + outList.get(i).getLocation().getY());
////		System.out.println(nowPoint.getNext().get(0).getLocation().getX() + " " + nowPoint.getNext().get(0).getLocation().getY());
//
//
//	}
//
//}

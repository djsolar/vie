package split;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


public class PolygonHandle {
	//测试
	public static List<List<Integer>> compute(List<Point> list,Graphics g){
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		DecompPoly relation = null;
		PointList pts = new PointList(list);//赋值
		if (!pts.isEmpty()) {
//			if (!pts.ccw()) {//逆时针方向调整
//				pts.reverse();
//			}
			relation = compute(pts);//计算
		}
		if(relation != null){
			relation.draw(g);//整理关系
			Node[] nodes = relation.getRelations();
			//测试关系
//			for (Node node : nodes) {
//				System.out.println(node.index);
//				for (Node node1 : node.nodes) {
//					System.out.print(node1.index+"    ");
//				}
//				System.out.println();
//				System.out.println("-----------------------");
//			}
			//整理关系
			for(int index = 0; index < nodes.length; ){
				Node startNode = nodes[index];
				if(startNode.nodes.size() > 0){
//					Edge edge = new Edge();
//					edge.points.add(list.get(startNode.index));
					int nextNodeIndex = startNode.getNextNode(startNode).index;
					int startNodeIndex = index;
					System.out.println("========="+startNodeIndex+"====="+nextNodeIndex+"===");
					List<Integer> polygon = new ArrayList<Integer>();
					polygon.add(startNodeIndex);
					polygon.add(nextNodeIndex);
					while(index != nextNodeIndex){
						int newNodeindexId = nodes[nextNodeIndex].getNextNode(nodes[startNodeIndex]).index;
						startNodeIndex = nextNodeIndex;
						nextNodeIndex = newNodeindexId;
						System.out.println(newNodeindexId);
						polygon.add(newNodeindexId);
					}
					result.add(polygon);
				}
				index++;
			}
		}
		return result;
	}
	public static List<List<Point>> convex(List<Point> list){
		DecompPoly result = null;
		PointList pts = new PointList(list);//赋值
		if (!pts.isEmpty()) {
			if (!pts.ccw()) {//逆时针方向调整
				pts.reverse();
			}
			result = compute(pts);//计算
		}
		if(result != null){
			result.draw(null);//整理关系
		}
		return null;
	}
	/** Assumes that the point list contains at least three points */
	public static DecompPoly compute(PointList pl) {
		int i, k, n = pl.number();
		DecompPoly dp = new DecompPoly(pl);
		dp.init();

		for (int l = 3; l < n; l++) {
			for (i = dp.reflexIter(); i + l < n; i = dp.reflexNext(i))
				if (dp.visible(i, k = i + l)) {
					dp.initPairs(i, k);
					if (dp.reflex(k))
						for (int j = i + 1; j < k; j++)
							dp.typeA(i, j, k);
					else {
						for (int j = dp.reflexIter(i + 1); j < k - 1; j = dp
								.reflexNext(j))
							dp.typeA(i, j, k);
						dp.typeA(i, k - 1, k); // do this, reflex or not.
					}
				}

			for (k = dp.reflexIter(l); k < n; k = dp.reflexNext(k))
				if ((!dp.reflex(i = k - l)) && dp.visible(i, k)) {
					dp.initPairs(i, k);
					dp.typeB(i, i + 1, k); // do this, reflex or not.
					for (int j = dp.reflexIter(i + 2); j < k; j = dp
							.reflexNext(j))
						dp.typeB(i, j, k);
				}
		}
		dp.guard = 3 * n;
		dp.recoverSolution(0, n - 1);
		return dp;
	}
}

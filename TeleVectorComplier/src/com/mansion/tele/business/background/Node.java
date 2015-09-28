package com.mansion.tele.business.background;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Node {
	int index;
	Point point;
	List<Node> nodes = new ArrayList<Node>();
	public Node(int index) {
		this.index = index;
	}
	
	private static Comparator<Node> comparator = new Comparator<Node>() {
		@Override
		public int compare(Node o1, Node o2) {
			return o1.index - o2.index;
		}
	};
	
	/**
	 * 下一节点获取方法,进入节点为基准最左侧路径所指向节点
	 * @return
	 */
	//TODO 方法过程优化
	public Node getNextNode(Node fromNode){
		if(nodes.size() == 0){
			//System.out.println("Error!!!");
			return null;
		}
		if(nodes.size() == 1){//只有一条路可选
			return nodes.remove(0);
		}
		boolean exist = nodes.remove(fromNode);
		Collections.sort(nodes, comparator);
		if(this.equals(fromNode)){//起始点
			return this.nodes.remove(0);
		}
		//找出最左Node
		Node nodeB = nodes.get(nodes.size()-1);
		Node nodeA = nodes.get(0);
		Node leftNode = leftNode(fromNode, this, nodeA, nodeB);
		
//		Node leftNode = nodes.get(0);
//		for (int i = 1; i< nodes.size(); i++) {
//			Node currentNode = nodes.get(i);
//			leftNode = leftNode(fromNode, this, leftNode, currentNode);
//		}
//		//
		if(exist){
			nodes.add(fromNode);
		}
		this.nodes.remove(leftNode);
		return leftNode;
		
	}
	public static Node leftNode(Node fromNode,Node junctionNode,Node nodeA,Node nodeB){
		double angleA = LEFT(fromNode.point,junctionNode.point,nodeA.point);
		double angleB = LEFT(fromNode.point,junctionNode.point,nodeB.point);
		if(angleA*angleB < 0){//不同侧
			if(angleA>0){
				return nodeA;
			}else{
				return nodeB;
			}
		}else{//同侧
			double angleAB = LEFT(junctionNode.point,nodeA.point,nodeB.point);
			if(angleAB > 0){
				return nodeB;
			}else{
				return nodeA;
			}
		}
	}
	/**
	 * 向量ab
	 * 向量ap
	 * ab*ap=|ab|*|ap|sin@
	 * 
	 *若 ab*ap>0
	 *则 sin@>0
	 *p在ab左侧
	 * @param a
	 * @param b
	 * @param p
	 * @return
	 */
	public static double LEFT(Point a,Point b,Point p){
		return (b.x-a.x)*(p.y-a.y)-(b.y-a.y)*(p.x-a.x);
	}
	final public boolean rightMax(Point min, Point current,Point max) {
		return (max.y-current.y)*(min.x-current.x) < (max.x-current.x)*(min.y - current.y); //斜率
	}
	@Override
	public boolean equals(Object obj) {
		Node node = (Node)obj;
		return node.index == this.index;
	}
}

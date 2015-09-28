package com.mansion.tele.business.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.Task;
import com.mansion.tele.business.network.RoadNew.defineDTF;
import com.mansion.tele.business.network.RoadNew.defineFOW;
import com.mansion.tele.db.bean.elemnet.Node;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

/**
 * Basic object ,use for build NetWork;
 * 
 * join roads
 * 
 * @author zhangj
 * 
 */
public class NodeNew implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7363176839162600262L;

	// Node基本属性
	public String nodeId; // node点id//

	byte isDummyNode; // dummy类型，

	public ShpPoint point;// node形状
	
	boolean delFlag;//true:删除
	
	public IntersectionNew intersection;
	
	public int mmnodeNum = -1;
	
	// 建立拓扑关系属性
	public List<RoadNew> roads = new ArrayList<RoadNew>();
	
	// mm偏移值
	public int offset = -1;
	
	//是否是block边界
	public boolean mmBlockBorderNode;
	
	public boolean meshBorderNode;

	// from Hibernate Object to NetWork Object
	public void convert(Node node) {
		this.nodeId = node.getStrid();
		this.isDummyNode = node.getByisdummynode();
		this.point = node.getStGeom().getCoordinate();
		if(node.getByistaskborder() == 5){
			this.meshBorderNode = true;
		}
	}
	/**
	 * 判断是否为复杂交叉路口Node
	 * @return
	 */
	public boolean isIntersectionNode(){
		if(this.intersection == null){
			return false;
		}
		for (RoadNew road : this.roads) {
			//Node中存在虚拟路则认为是复杂路口Node
			if(road.fow == defineFOW.JunctionLink){
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断是否为边界Node
	 * @param network
	 * @return
	 */
	public boolean isTaskBorderNode(Network network){
		Task task = network.taskData.task;
		if(point == null || task == null){
			System.out.println("sdsdsd");
			return true;
		}
		if(point.x == task.getLeft() || point.x == task.getRight()){
			return true;
		}
		if(point.y == task.getTop() || point.y == task.getBottom()){
			return true;
		}
		if(meshBorderNode){
			return true;
		}
		return false;
	}
	
	/**
	 * 传入的Node设置删除标记 把相关信息保存在this Node 中 保持拓扑关系不变
	 * @param node
	 * @return 可以合并返回 true 否则 false
	 */
	public boolean mergerBorderNode(NodeNew node) {
			// 合并Node
			for (RoadNew road : node.roads) {
				if (road.startNode.equals(node)) {
					road.startNode = this;
					this.roads.add(road);
				} else if (road.endNode.equals(node)){
					road.endNode = this;
					this.roads.add(road);
				}else{
					return false;
				}
			}
			return true;
	}
	/**
	 * 判断二叉路是否可以合并，当前Node必须只连接两条道路
	 * @param road
	 * @return true：可以合并
	 */
	public boolean canMerge(){
		RoadNew roadFirst = this.roads.get(0);
		RoadNew roadSecond = this.roads.get(1);
		//判断两条道路是否可以合并
		//1，不能成为环形
		if(roadFirst.startNode.equals(roadSecond.startNode) && roadFirst.endNode.equals(roadSecond.endNode)
		||
		roadFirst.endNode.equals(roadSecond.startNode) && roadFirst.startNode.equals(roadSecond.endNode)){
			return false;
		}
		//2.属性一致
		//虚拟道路属性不考虑，直接合并
		if(roadSecond.fow == defineFOW.JunctionLink || roadFirst.fow == defineFOW.JunctionLink){
			return false;
		}
		if(!roadSecond.roadName.equals(roadFirst.roadName)){
			return false;
		}
		if(!roadSecond.routeNo.toString().equals(roadFirst.routeNo.toString())){
			return false;
		}
		if(roadSecond.styleColor != roadFirst.styleColor){
			return false;
		}
		if(roadSecond.styleMode != roadFirst.styleMode){
			return false;
		}
		if(roadSecond.fow != roadFirst.fow){
			return false;
		}
		if(roadSecond.frc != roadFirst.frc){
			return false;
		}
		if(roadSecond.dtf != roadFirst.dtf){
			return false;
		}
		if(roadSecond.grade != roadFirst.grade){
			return false;
		}
		if(roadSecond.roadConstructType != roadFirst.roadConstructType){
			return false;
		}
		if(roadSecond.cs != roadFirst.cs){
			return false;
		}
		//可以合并 为一个方向
//		if(!roadFirst.endNode.getOutRoads().contains(roadSecond)){
//			return false;
//		}
//		if(!roadFirst.startNode.getInRoads().contains(roadSecond)){
//			return false;
//		}
		return true; 
	}
	/**
	 * 返回所有拖出路
	 * @return 
	 */
	public List<RoadNew> getOutRoads(){
		List<RoadNew> outWays = new ArrayList<RoadNew>();
		for(RoadNew road:this.roads){
			if((road.dtf == defineDTF.PosPass && this.equals(road.startNode) == true)//单向通行且从复杂路口拖出
					|| road.dtf == defineDTF.TwoWayPass ||road.dtf == defineDTF.NoPass){
				outWays.add(road);
			}
		}
		return outWays;
	}
	/**
	 * 返回所有进入路
	 * @return
	 */
	public List<RoadNew> getInRoads(){
		List<RoadNew> inWays = new ArrayList<RoadNew>();
		for(RoadNew road:this.roads){
			if((road.dtf == defineDTF.PosPass && this.equals(road.endNode) == true)//单向通行且从复杂路口拖出
					|| road.dtf == defineDTF.TwoWayPass
					|| road.dtf == defineDTF.NoPass){
				inWays.add(road);
			}
		}
		return inWays;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (delFlag ? 1231 : 1237);
		result = prime * result
				+ ((intersection == null) ? 0 : intersection.hashCode());
		result = prime * result + isDummyNode;
		result = prime * result + mmnodeNum;
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		result = prime * result + ((roads == null) ? 0 : roads.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeNew other = (NodeNew) obj;
		if (delFlag != other.delFlag)
			return false;
		if (intersection == null) {
			if (other.intersection != null)
				return false;
		} else if (!intersection.equals(other.intersection))
			return false;
		if (isDummyNode != other.isDummyNode)
			return false;
		if (mmnodeNum != other.mmnodeNum)
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		if (roads == null) {
			if (other.roads != null)
				return false;
		} else if (!roads.equals(other.roads))
			return false;
		return true;
	}
	
}

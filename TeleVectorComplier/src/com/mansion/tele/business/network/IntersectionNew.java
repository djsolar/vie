package com.mansion.tele.business.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.mapping.Array;

import com.mansion.tele.business.network.RoadNew.defineDTF;
import com.mansion.tele.business.network.RoadNew.defineFOW;
import com.mansion.tele.db.bean.elemnet.Intersection_O;
import com.mansion.tele.db.bean.elemnet.Node;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

//复杂交叉口
public class IntersectionNew implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1511460466262259463L;
	public String id;
	//复杂交叉路口内的所有Node
	public List<NodeNew> allNodes = new ArrayList<NodeNew>();
	
	boolean delFlag;//true:删除
	//交叉口node的id
	public void convert(Network network,Intersection_O intersection_O){
		List<String> astNodeIdList = intersection_O.getAstNodeIDList();
		for (NodeNew node : network.nodeList) {
			if(astNodeIdList.contains(node.nodeId)){
				node.intersection = this;//建立拓扑关系
				allNodes.add(node);
			}
		}
		id = intersection_O.getStrid();
	}
	/**
	 * 复杂交叉路口合并
	 * @return
	 */
	public void mergerIntersection(IntersectionNew intersection){
		if(!intersection.equals(this)){
			for (NodeNew node : intersection.allNodes) {
				node.intersection = this;
			}
			this.allNodes.addAll(intersection.allNodes);
			intersection.delFlag = true;
		}
	}
	/**
	 * 返回复杂路口中的所有拖出路
	 * @return
	 */
	public List<RoadNew> getOutRoads(){
		List<RoadNew> outWays = new ArrayList<RoadNew>();
		for (NodeNew node : this.allNodes) {
			for(RoadNew road:node.roads){
				if(road.fow != defineFOW.JunctionLink){// 交差点内リンク
					if((road.dtf == defineDTF.PosPass && node.equals(road.startNode) == true)//单向通行且从复杂路口拖出
							|| road.dtf == defineDTF.TwoWayPass){//双方向通行//TODO 疑问 双方向的道路是否可以使用两次
						outWays.add(road);
					}
				}
			}
		}
		return outWays;
	}
	/**
	 * 返回复杂路口中的所有进入路
	 * @return
	 */
	public List<RoadNew> getInRoads(){
		List<RoadNew> inWays = new ArrayList<RoadNew>();
		for (NodeNew node : this.allNodes) {
			for(RoadNew road:node.roads){
				if(road.fow != defineFOW.JunctionLink){// 交差点内リンク
					if((road.dtf == defineDTF.PosPass && node.equals(road.endNode) == true)
							|| road.dtf == defineDTF.TwoWayPass){
						inWays.add(road);
					}
				}
			}
		}
		return inWays;
	}
	/**
	 * 获得交叉点内link 
	 * @return
	 */
	public List<RoadNew> getIntersectInnerRoads(){
		List<RoadNew> roads = new ArrayList<RoadNew>();
		for (NodeNew node : this.allNodes) {
			for(RoadNew road:node.roads){
				if(road.fow == defineFOW.JunctionLink && !roads.contains(road)){// 交差点内リンク
					roads.add(road);
				}
			}
		}
		return roads;
	}
}

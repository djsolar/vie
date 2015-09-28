package com.mansion.tele.business.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.DataManager.LevelInfo;
import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.TaskData.VectoryStyle;
import com.mansion.tele.business.network.RoadNew.defineFOW;
import com.mansion.tele.business.network.RoadNew.defineNRC;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.db.bean.elemnet.Intersection_O;
import com.mansion.tele.db.bean.elemnet.Node;
import com.mansion.tele.db.bean.elemnet.Node.NodeType;
import com.mansion.tele.db.bean.elemnet.Road;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.db.daoImpl.TeleDao;
import com.mansion.tele.db.factory.TeleHbSessionFactory;
import com.mansion.tele.util.PolygonUtil;

/**
 * 保存路网，提供路网相关的操作 save road net,server operation of relations
 * 
 * @author zhangj
 */
public class Network implements Serializable {

	
	/**
	 * 边界点集合
	 */
	static Map<ShpPoint, List<NodeNew>>  taskNodebord = new HashMap<ShpPoint, List<NodeNew>>(100000);
	
	private static int mmLinkID = 0;
	/**
	 * 获得MMLinkID
	 * @return
	 */
	public static synchronized int getMMLinkID(){
		int currnum = mmLinkID;
		mmLinkID++;
		return currnum;
	}
	/**
	 * 存放任务边界点
	 * @param nodelist
	 */
	public synchronized static void putTaskBorderNode(List<NodeNew> nodelist){
		for (NodeNew nodeNew : nodelist) {
			List<NodeNew> tasknodelist = taskNodebord.get(nodeNew.point);
			if(tasknodelist != null){
				tasknodelist.add(nodeNew);
			}else{
				tasknodelist = new ArrayList<NodeNew>();
				tasknodelist.add(nodeNew);
				taskNodebord.put(nodeNew.point, tasknodelist);
			}
		}
	}
	
	public synchronized static List<NodeNew> getTaskBorderNode(NodeNew nodenew){
		return Network.taskNodebord.get(nodenew.point);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3486534301786107926L;

	TaskData taskData;

	public List<RoadNew> roadList = new ArrayList<RoadNew>(5000);

	public List<NodeNew> nodeList = new ArrayList<NodeNew>(2000);

	public List<IntersectionNew> intersectionList = new ArrayList<IntersectionNew>(200);
	
	
	public Network(TaskData taskData) {
		this.taskData = taskData;
	}

	/**
	 * 制作mesh边界的拓扑关系
	 * 
	 * @param bordNodeList
	 *            ：边界Node列表
	 */
	public void makeBorderTopo(List<NodeNew> bordNodeList) {
		if (bordNodeList.size() == 0) {
			return;
		}
		Collections.sort(bordNodeList, new Comparator<NodeNew>() {
			@Override
			public int compare(NodeNew o1, NodeNew o2) {
				if (o1.point.x > o2.point.x) {
					return 1;
				}
				if (o1.point.x< o2.point.x) {
					return -1;
				}
				if(o1.point.y > o2.point.y){
					return 1;
				}
				if(o1.point.y < o2.point.y){
					return -1;
				}
				return 0;
			}
		});
		// 合并相同边界Node
		NodeNew bordNode = bordNodeList.get(0);
		for (int i = 1; i < bordNodeList.size(); i++) {
			NodeNew node = bordNodeList.get(i);
//			if("97A062A001F0001382".equals(bordNode.nodeId)){
//				System.out.println("test");
//			}
			// 判断合并条件 && // 合并边界点
			if (node.point.equals(bordNode.point) && bordNode.mergerBorderNode(node)) {
				node.delFlag = true;
				//合并复杂路口对象
				//if（复杂路口）
				if(node.isIntersectionNode() && bordNode.isIntersectionNode()){
					//在复杂路口内遍历，找到包含bordNode node
					bordNode.intersection.mergerIntersection(node.intersection);
				}
			} else {
				bordNode = node;
			}
		}
		clearRoadList();
		clearNodeList();	
		clearIntersectionList();

	}

	// 二叉路合并
	public void mergerBinaryRoad() {
		for (NodeNew node : nodeList) {
			// node只连接两条路
			if (node.roads.size() == 2) {
				RoadNew roadFirst = node.roads.get(0);
				RoadNew roadSecond = node.roads.get(1);
				if (node.canMerge() == true) {// 是否可以合并
					RoadNew roadMergedFrom = roadSecond;//被合并掉的道路
					RoadNew roadMergedTo = roadFirst;//合并后的新道路
					//头&头 
					if (roadFirst.startNode.equals(roadSecond.startNode)) {
						// 把点少的道路反转方向
						if (roadFirst.coordinate.size() >= roadSecond.coordinate.size()) {
							Collections.reverse(roadSecond.coordinate);
							
							roadSecond.coordinate.remove(roadSecond.coordinate.size()-1);
							roadSecond.coordinate.addAll(roadFirst.coordinate);
							roadFirst.coordinate = roadSecond.coordinate;
							
							roadFirst.startNode = roadSecond.endNode;
							roadFirst.startNode.roads.remove(roadSecond);
							roadSecond.delFlag = true;
							roadFirst.startNode.roads.add(roadFirst);
							roadFirst.startAngle30m = roadSecond.endAngle30m;
							roadFirst.startAngle60m = roadSecond.endAngle60m;
						} else {
							Collections.reverse(roadFirst.coordinate);

							roadFirst.coordinate.remove(roadFirst.coordinate.size()-1);
							roadFirst.coordinate.addAll(roadSecond.coordinate);
							roadSecond.coordinate = roadFirst.coordinate;
							
							roadSecond.startNode = roadFirst.endNode;
							roadSecond.startNode.roads.remove(roadFirst);
							roadFirst.delFlag = true;
							roadSecond.startNode.roads.add(roadSecond);
							roadSecond.startAngle30m = roadFirst.endAngle30m;
							roadSecond.startAngle60m = roadFirst.endAngle60m;
							roadMergedFrom = roadFirst;
							roadMergedTo = roadSecond;
						}
					//尾&尾 
					}else if(roadFirst.endNode.equals(roadSecond.endNode)){
						// 把点少的道路反转方向
						if (roadFirst.coordinate.size() >= roadSecond.coordinate.size()) {
							Collections.reverse(roadSecond.coordinate);
							
							roadFirst.coordinate.remove(roadFirst.coordinate.size()-1);
							roadFirst.coordinate.addAll(roadSecond.coordinate);
							
							roadFirst.endNode = roadSecond.startNode;
							roadFirst.endNode.roads.remove(roadSecond);
							roadSecond.delFlag = true;
							roadFirst.endNode.roads.add(roadFirst);
							
							roadFirst.endAngle30m = roadSecond.startAngle30m;
							roadFirst.endAngle60m = roadSecond.startAngle60m;
							
						} else {
							Collections.reverse(roadFirst.coordinate);
							
							roadSecond.coordinate.remove(roadSecond.coordinate.size()-1);
							roadSecond.coordinate.addAll(roadFirst.coordinate);
							
							roadSecond.endNode = roadFirst.startNode;
							roadSecond.endNode.roads.remove(roadFirst);
							roadFirst.delFlag = true;
							roadSecond.endNode.roads.add(roadSecond);
							
							roadSecond.endAngle30m = roadFirst.startAngle30m;
							roadSecond.endAngle60m = roadFirst.startAngle60m;
							roadMergedFrom = roadFirst;
							roadMergedTo = roadSecond;
						}
						//头&尾
					}else if(roadFirst.startNode.equals(roadSecond.endNode)){
						roadFirst.startNode = roadSecond.startNode;
						roadFirst.startNode.roads.remove(roadSecond);
						roadSecond.delFlag = true;
						roadFirst.startNode.roads.add(roadFirst);
						
						roadSecond.coordinate.remove(roadSecond.coordinate.size()-1);
						roadSecond.coordinate.addAll(roadFirst.coordinate);
						roadFirst.coordinate = roadSecond.coordinate;
						
						roadFirst.startAngle30m = roadSecond.startAngle30m;
						roadFirst.startAngle60m = roadSecond.startAngle60m;
					}else if(roadFirst.endNode.equals(roadSecond.startNode)){
						roadFirst.endNode = roadSecond.endNode;
						roadFirst.endNode.roads.remove(roadSecond);
						roadSecond.delFlag = true;
						roadFirst.endNode.roads.add(roadFirst);
						
						roadFirst.coordinate.remove(roadFirst.coordinate.size()-1);
						roadFirst.coordinate.addAll(roadSecond.coordinate);
						
						roadFirst.endAngle30m = roadSecond.endAngle30m;
						roadFirst.endAngle60m = roadSecond.endAngle60m;
					}else{
						System.err.println("二叉路合并时发生错误!");
					}
					// 如果roadMergedTo是虚拟道理，要把roadMergedFrom的属性（长度、形状、拓扑关系除外）克隆过来
					if (roadMergedTo.fow == defineFOW.JunctionLink) {
						roadMergedTo.copy(roadMergedFrom);
					}
					// 属性赋值
					roadMergedTo.length += roadMergedFrom.length;
					roadMergedTo.lb.x = Math.min(roadMergedTo.lb.x, roadMergedFrom.lb.x);
					roadMergedTo.lb.y = Math.min(roadMergedTo.lb.y, roadMergedFrom.lb.y);
					roadMergedTo.rt.x = Math.max(roadMergedTo.rt.x, roadMergedFrom.rt.x);
					roadMergedTo.rt.y = Math.max(roadMergedTo.rt.y, roadMergedFrom.rt.y);

					// 添加删除标记
					node.delFlag = true;
				}
			}
		}
		// System.out.println("--------------");
		clearRoadList();
		clearNodeList();
		clearIntersectionList();
		
	}

	// 删除标记为无用的node
	void clearNodeList() {
		Collections.sort(this.nodeList, new Comparator<NodeNew>() {
			@Override
			public int compare(NodeNew o1, NodeNew o2) {
				if (o1.delFlag == o2.delFlag) {
					return 0;
				} else {
					if (o1.delFlag) {
						return 1;
					}
					return -1;
				}
			}
		});
		// System.out.println("node "+this.nodeList.size());
		for (int index = this.nodeList.size() - 1; index >= 0; index--) {
			NodeNew node = this.nodeList.get(index);
			if (node.delFlag) {
				// System.out.println(index +" : "+this.nodeList.size());
				nodeList.remove(index);
			} else {
				break;
			}
		}
		// System.out.println("node "+this.nodeList.size());
	}

	// 删除标记为无用的road
	void clearRoadList() {
		Collections.sort(this.roadList, new Comparator<RoadNew>() {
			@Override
			public int compare(RoadNew o1, RoadNew o2) {
				if (o1.delFlag == o2.delFlag) {
					return 0;
				} else {
					if (o1.delFlag) {
						return 1;
					}
					return -1;
				}
			}
		});
		// System.out.println("road "+this.roadList.size());
		for (int index = this.roadList.size() - 1; index >= 0; index--) {
			RoadNew road = this.roadList.get(index);
			if (road.delFlag) {
				road.startNode.roads.remove(road);
				road.endNode.roads.remove(road);
				roadList.remove(index);
			} else {
				break;
			}
		}
		// System.out.println("road "+this.roadList.size());
	}
	// 删除复杂交叉路口,删除复杂交叉路口中标记为无用的Node
	void clearIntersectionList(){
		Collections.sort(this.intersectionList, new Comparator<IntersectionNew>() {
			@Override
			public int compare(IntersectionNew o1, IntersectionNew o2) {
				if (o1.delFlag == o2.delFlag) {
					return 0;
				} else {
					if (o1.delFlag) {
						return 1;
					}
					return -1;
				}
			}
		});
		// 
		for (int i = this.intersectionList.size() -1 ;i >= 0;i--) {
			IntersectionNew intersection = this.intersectionList.get(i);
			if (intersection.delFlag) {// 删除复杂交叉路口
				intersectionList.remove(i);
			} else {// 删除复杂交叉路口中标记为无用的Node
				for (int j = intersection.allNodes.size() - 1; j >= 0; j--) {
					NodeNew node = intersection.allNodes.get(j);
					if (node.delFlag) {
						intersection.allNodes.remove(j);
					}
				}
			}
			//TODO 删除复杂路口本身 当 复杂路口中的Node size ==0 时 
			//打开一下代码会出现 问题 
//			java.lang.NullPointerException
//			at com.mansion.tele.common.BlockNo.equals(BlockNo.java:142)
//			at com.mansion.tele.business.viewdata.ViewData.createBlockData(ViewData.java:135)
			
//			if(!intersection.allNodes.isEmpty()){
//				this.intersectionList.remove(i);
//			}
		}
	}

	public static Comparator<NodeNew> comparableNodeId = new Comparator<NodeNew>() {
		@Override
		public int compare(NodeNew o1, NodeNew o2) {
			return o1.nodeId.compareTo(o2.nodeId);
		}
	};

	/**
	 * 由母库数据建立0层路网
	 * 
	 * @param task
	 * @param data
	 * @throws Exception
	 */
	public void fromDB() throws Exception {
		List<String> meshNos = DataManager.getMeshNos(this.taskData.task);
		TeleDao teleDao = new TeleDao();
		Session session = TeleHbSessionFactory.getOrgHbSession(this.taskData.task.getLevel()).getSession();
		List<NodeNew> meshBorderNodeList = new ArrayList<NodeNew>();
		for (String meshNo : meshNos) {
			List<Node> node_oList = teleDao.getNodeList(meshNo, session);
			List<NodeNew> nodeList = new ArrayList<NodeNew>(node_oList.size());
			// 读取关系节点
			for (Node node_o : node_oList) {
				NodeNew nodeNew = new NodeNew();
				nodeNew.convert(node_o);
				if (node_o.getByistaskborder() == NodeType.meshBorder) {
					meshBorderNodeList.add(nodeNew);
				}
				nodeList.add(nodeNew);
			}

			this.nodeList.addAll(nodeList);
			Collections.sort(nodeList, comparableNodeId);
			// 读取道路
			List<Road> roadList = teleDao.getRoadList(meshNo, session);
			for (Road road_O : roadList) {
				RoadNew road = new RoadNew();
				// 属性赋值，正规化计算
				road.convert(this, road_O);
				this.roadList.add(road);
				// 建立拓扑关系
				// add node to road
				int indexStart = Collections.binarySearch(nodeList, road.startNode, comparableNodeId);
				road.startNode = nodeList.get(indexStart);
				int indexEnd = Collections.binarySearch(nodeList, road.endNode, comparableNodeId);
				road.endNode = nodeList.get(indexEnd);
				// add road to node
				road.startNode.roads.add(road);
				road.endNode.roads.add(road);
			}
			// 读取复杂路口
			List<Intersection_O> intersectionList = teleDao.getIntersectionList(meshNo, session);
			for (Intersection_O intersection_O : intersectionList) {
				IntersectionNew intersection = new IntersectionNew();
				intersection.convert(this, intersection_O);
				this.intersectionList.add(intersection);
			}
		}
		// 建立mesh边界的拓扑关系
		this.makeBorderTopo(meshBorderNodeList);
		//
	}

	/**
	 * 按Block块分割Road
	 */
	public void splitRoadByBlock() {
		// X方向切割
		splitRoadByBlockX();
		// Y方向切割
		splitRoadByBlockY();
		//点数过滤
		splitRoadByPointCount();
		// 对分割后的道路进行长度计算
		for (RoadNew road : this.roadList) {
			road.calcRoadLength();	
		}
		this.clearRoadList();
		this.clearNodeList();
	}
	//该方法是为了保证单个道路的形状点个数少于256 ，从而在格式输出的时候没有问题
	void splitRoadByPointCount(){
		for (int i = this.roadList.size() - 1;i >=0;i--) {
			RoadNew road = this.roadList.get(i);
			for(int count = 0;count < 100;count++){
				if(road.coordinate.size() > 255){
					NodeNew node = new NodeNew();
					node.point = ShpPoint.valueOf(road.coordinate.get(254));
					RoadNew splitRoad = new RoadNew();
					splitRoad.copy(road);// 长度不需要计算 在分割完成后统一重新计算
					splitRoad.coordinate.addAll(road.coordinate.subList(0, 254));
					splitRoad.coordinate.add(ShpPoint.valueOf(road.coordinate.get(254)));
					road.coordinate = new ArrayList<ShpPoint>(road.coordinate.subList(255, road.coordinate.size()));
					// 第一个开始点有价值
					splitRoad.startAngle30m = road.startAngle30m;
					splitRoad.startAngle60m = road.startAngle60m;
					// 在合并Rout时需要判断角度
					splitRoad.endAngle30m = -90;
					splitRoad.endAngle60m = -90;
					road.startAngle30m = 90;
					road.startAngle60m = 90;
					//维护拓扑关系
					splitRoad.startNode = road.startNode;
					splitRoad.endNode = node;
					road.startNode = node;
					
					splitRoad.startNode.roads.remove(road);
					splitRoad.startNode.roads.add(splitRoad);
					
					node.roads.add(splitRoad);
					node.roads.add(road);
					
					this.nodeList.add(node);
					this.roadList.add(splitRoad);
				}else if(road.coordinate.size() < 2 || (road.coordinate.size() == 2 && road.coordinate.get(0).equals(road.coordinate.get(1)))){
					road.delFlag = true;
				}else{
					break;
				}
			}
		}
	}
	//X方向上的Block分割
	void splitRoadByBlockX() {
		List<RoadNew> spiltRoadList = new ArrayList<RoadNew>();
		for (int i = 0; i < this.roadList.size(); i++) {
			RoadNew road = this.roadList.get(i);
			List<ShpPoint> points = new ArrayList<ShpPoint>(road.coordinate);
			ShpPoint startPoint = points.get(0);
			ShpPoint headerSplitPoint = null;
			int startBlockNo_x = taskData.calcBlockNo(startPoint).iX;
			
			int splitIndexFrom = 0;
			int splitIndexTo = 0;
			ShpPoint splitPoint = null;
			
			for (int index = 1; index < points.size();) {
				ShpPoint currentPoint = points.get(index);
				int currentBlockNo_x = taskData.calcBlockNo(currentPoint).iX;

				// 道路跨X方向边界
				if (currentBlockNo_x != startBlockNo_x) {
					boolean ascX = currentBlockNo_x > startBlockNo_x;
					//分割线x值
					int blockPoint_x = (startBlockNo_x + (ascX ? 1 : 0)) * taskData.blockInfo.iBlockWidth+DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN;
				
					// 根据相对位置计算Y值
					long blockPoint_y = startPoint.y;//long 类型为了避免溢出发生
					if (currentPoint.y != startPoint.y) {
						blockPoint_y = (long)(blockPoint_x - startPoint.x) * (currentPoint.y - startPoint.y) / (currentPoint.x - startPoint.x) + startPoint.y;
					}
					//切割点
					if(ascX){
						splitPoint = new ShpPoint(blockPoint_x-1, (int)blockPoint_y);
					}else{
						splitPoint = new ShpPoint(blockPoint_x, (int)blockPoint_y);
					}
					//切割位置
					splitIndexTo = index;
					
					//计算BlockNo
					road.blockNo = taskData.calcBlockNo(splitPoint);
					// 产生新的Block边界Node,插入点
					
					RoadNew splitRoad = createSplitRoad(road, splitIndexFrom,splitIndexTo, points,headerSplitPoint,splitPoint,new ShpPoint(blockPoint_x, (int)blockPoint_y));
					splitIndexFrom = splitIndexTo;
					if(ascX){
						headerSplitPoint = new ShpPoint(blockPoint_x, (int)blockPoint_y);
					}else{
						headerSplitPoint = new ShpPoint(blockPoint_x-1, (int)blockPoint_y);
					}
					// 收集新道路
					spiltRoadList.add(splitRoad);
					startPoint = new ShpPoint(blockPoint_x, (int)blockPoint_y); ;
					startBlockNo_x = startBlockNo_x + (ascX ? 1 : -1);
				} else {
					startPoint = currentPoint;
					index++;
				}
				
			}
			// 原始道路（最后一段道路） 添加Block值
			ShpPoint endPoint = points.get(points.size() - 1);
			road.blockNo = taskData.calcBlockNo(endPoint);
			if(headerSplitPoint != null){
				road.coordinate = new ArrayList<ShpPoint>(points.subList(splitIndexFrom, points.size()));
				road.coordinate.add(0, headerSplitPoint);
			}
		}
		// 添加新结果
		this.roadList.addAll(spiltRoadList);
	} 
	//Y方向上的分割
	void splitRoadByBlockY() {
		List<RoadNew> spiltRoadList = new ArrayList<RoadNew>();
		for (RoadNew road : this.roadList) {
			List<ShpPoint> points = new ArrayList<ShpPoint>(road.coordinate);
			ShpPoint startPoint = points.get(0);
			ShpPoint headerSplitPoint = null;
			int startBlockNo_y = taskData.calcBlockNo(startPoint).iY;
			
			int splitIndexFrom = 0;
			int splitIndexTo = 0;
			ShpPoint splitPoint = null;
			for (int index = 1; index < points.size();) {
				ShpPoint currentPoint = points.get(index);
				int currentBlockNo_y = taskData.calcBlockNo(currentPoint).iY;

				// 道路跨Y方向边界
				if (currentBlockNo_y != startBlockNo_y) {
					boolean ascY = (currentBlockNo_y > startBlockNo_y);
					//分割时y值
					int blockPoint_y = (startBlockNo_y + ( ascY? 1 : 0)) * taskData.blockInfo.iBlockHight;
					// 根据相对位置计算X值
					long blockPoint_x = startPoint.x;//long 类型为了避免溢出发生
					if (currentPoint.x != startPoint.x) {
						blockPoint_x = (long)(currentPoint.x - startPoint.x) * (blockPoint_y - startPoint.y) / (currentPoint.y - startPoint.y) + startPoint.x;
					}
					//切割位置
					if(ascY){
						splitPoint = new ShpPoint((int)blockPoint_x, blockPoint_y-1);
					}else{
						splitPoint = new ShpPoint((int)blockPoint_x, blockPoint_y);
					}
					
					splitIndexTo = index;
					//计算BlockNo
					road.blockNo = taskData.calcBlockNo(splitPoint);
					// 产生新的Block边界Node,插入点
					RoadNew splitRoad = createSplitRoad(road, splitIndexFrom,splitIndexTo, points,headerSplitPoint,splitPoint,new ShpPoint((int)blockPoint_x, blockPoint_y));
					splitIndexFrom = splitIndexTo;
					if(ascY){
						headerSplitPoint = new ShpPoint((int)blockPoint_x, blockPoint_y);
					}else{
						headerSplitPoint = new ShpPoint((int)blockPoint_x, blockPoint_y-1);
					}
					
					// 收集新道路
					spiltRoadList.add(splitRoad);
					startPoint = new ShpPoint((int)blockPoint_x, blockPoint_y);;
					startBlockNo_y = startBlockNo_y + (ascY ? 1 : -1);
				} else {
					startPoint = currentPoint;
					index++;
				}
			}
			// 原始道路（最后一段道路） 添加Block值
			ShpPoint endPoint = points.get(points.size() - 1);
			
			road.blockNo = taskData.calcBlockNo(endPoint);
			if(headerSplitPoint != null){
				road.coordinate = new ArrayList<ShpPoint>(points.subList(splitIndexFrom, points.size()));
				road.coordinate.add(0, headerSplitPoint);
			}
		}
		// 添加新结果
		this.roadList.addAll(spiltRoadList);
	}

	/**
	 * 根据新产生的Block边界Node分割道路，维护原来路网关系 新产生道路splitRoad
	 * 
	 * @param road
	 *            原道路
	 * @param index
	 *            Node产生所在原道路中的位置
	 * @param splitPoint
	 *            Node具体位置坐标点
	 */
	private RoadNew createSplitRoad(RoadNew road, int Indexfrom, int indexTo,List<ShpPoint> points,ShpPoint headerSplitPoint,ShpPoint splitPoint,ShpPoint nodePoint) {
		// 新建Block边界Node
		NodeNew blockNode = new NodeNew();
		blockNode.point = nodePoint;// 原始坐标

		// 创建新的道路形状点
		List<ShpPoint> splitPoints = new ArrayList<ShpPoint>(points.subList(Indexfrom, indexTo));// 避免ConcurrentModificationException
		if(headerSplitPoint != null){
			splitPoints.add(0,ShpPoint.valueOf(headerSplitPoint));
		}
		
		splitPoints.add(ShpPoint.valueOf(splitPoint));
		RoadNew splitRoad = new RoadNew();

		splitRoad.copy(road);
		splitRoad.coordinate = splitPoints;

		// 第一个开始点有价值
		splitRoad.startAngle30m = road.startAngle30m;
		splitRoad.startAngle60m = road.startAngle60m;
		// 在合并Rout时需要判断角度
		splitRoad.endAngle30m = -90;
		splitRoad.endAngle60m = -90;
		road.startAngle30m = 90;
		road.startAngle60m = 90;
		// 建立,维护拓扑关系
		splitRoad.startNode = road.startNode;
		road.startNode.roads.add(splitRoad);
		road.startNode.roads.remove(road);

		blockNode.roads.add(splitRoad);
		blockNode.roads.add(road);
		// road.startNode = splitRoad.endNode = blockNode;
		road.startNode = blockNode;
		splitRoad.endNode = blockNode;
		
		if(road.fow == defineFOW.JunctionLink){
				road.endNode.intersection.allNodes.add(blockNode);
				blockNode.intersection = road.endNode.intersection;
		}

		this.nodeList.add(blockNode);
		return splitRoad;
	}

	/**
	 * 计算道路两端的角度。单位：度，北偏东
	 */
	public void calcAngel() {
		for (RoadNew road : this.roadList) {

			// System.out.println(road.length + " : "+road.coordinate);
			// 驶入方向，距离30m夹角
			road.startAngle30m = road.calcAngle(true, 30.0);
			// 驶入方向，距离60m夹角
			road.startAngle60m = road.calcAngle(true, 60.0);
			// 驶出方向，距离30m夹角
			road.endAngle30m = road.calcAngle(false, 30.0);
			// 驶出方向，距离60m夹角
			road.endAngle60m = road.calcAngle(false, 60.0);
		}
	}

	/**
	 * 
	 * @param bgFrc
	 *            升层后成为背景道路
	 * @param roadList
	 *            下一层道路
	 * @param nodeList
	 *            下一层node
	 * 
	 * 道路frc <= bgFrc 的升层为上一层道路 升层的道路bgFrc > frc > raiseFrc 的为背景道路
	 */
	private void raiseRoad(byte threshold, List<RoadNew> roadList) {
		byte level = this.taskData.task.getLevel();
		// 方案一：从道路出发进行整理
		for (RoadNew road : roadList) {
			if (road.frc <= threshold) {
				this.roadList.add(road);
				// ------
				road.convertStyle(level);
			} else {
				road.startNode.roads.remove(road);
				road.endNode.roads.remove(road);
			}
		}
	}
	/**
	 * 路网升层，根据所要升层的层号level，提取符合升层要求的Road 升层条件式样表
	 */
	public List<NodeNew> addTaskData(Network lowerNetwork) {
		List<NodeNew> taskBorderNodes = new ArrayList<NodeNew>();
		byte level = this.taskData.task.getLevel();

		// 式样转换
		// this.taskData.network.intersectionList.addAll(taskData.network.intersectionList);
		byte threshold = 127; // 升层FRC 最大值
		switch (level) {
		case 1:
			break;
		case 2:
			threshold = 6;
			break;
		case 3:
			threshold = 4;
			break;
		case 4:
			threshold = 3;
			break;
		case 5:
			threshold = 2;
			break;
		case 6:
		case 7:
			return taskBorderNodes;
		default:
			System.err.println("升层过程中层号出现错误：" + level);
			break;
		}

		// 道路升层
		this.raiseRoad(threshold, lowerNetwork.roadList);
		// 连接点升层
		for (NodeNew node : lowerNetwork.nodeList) {
			if (node.roads.size() != 0) {
				this.nodeList.add(node);

				// 保存任务边界连接点
				if (node.isTaskBorderNode(lowerNetwork)) {
					taskBorderNodes.add(node);
				}
			}
		}
		// 复杂路口升层
		for (IntersectionNew intersection : lowerNetwork.intersectionList) {
			for (int i = intersection.allNodes.size() - 1; i >= 0; i--) {
				NodeNew node = intersection.allNodes.get(i);
				if (node.roads.isEmpty()) {
					intersection.allNodes.remove(i);
				}
			}
			if(!intersection.allNodes.isEmpty()){
				this.intersectionList.add(intersection);
			}
		}

		return taskBorderNodes;
	}
	/**
	 * 删除没有显示效果的道路
	 */
	public void deleNoUseRoad() {
		if(this.taskData.task.getLevel() !=2 ){
			return;
		}
		
		RouteSingleLineHandle handler = new RouteSingleLineHandle(this);
		// 单线化处理
		if(this.taskData.task.getLevel() == 2){
			handler.singleLine();
		}
		byte level = taskData.task.getLevel();
		LevelInfo levelAndScale = DataManager.getLevelInfo(level);
		
		int levelSize = levelAndScale.minScale;
		for (int i = roadList.size()-1;i>=0;i--) {
			RoadNew road = roadList.get(i);
			//删除 ramp，(端点距离，每个端点的距离)
			if(road.fow == defineFOW.RampSlipRoad){
				double distance = PolygonUtil.twoPointDistance(road.coordinate.get(0), road.coordinate.get(road.coordinate.size()-1));
				boolean startwithRamp = false;
				for (RoadNew joinRoad: road.startNode.roads) {
					if(joinRoad.equals(road)){
						continue;
					}
					if(joinRoad.fow == defineFOW.RampSlipRoad){
						startwithRamp = true;
					}
				}
				boolean endwithRamp = false;
				for (RoadNew joinRoad: road.endNode.roads) {
					if(joinRoad.equals(road)){
						continue;
					}
					if(joinRoad.fow == defineFOW.RampSlipRoad){
						endwithRamp = true;
					}
				}
				if(!(startwithRamp && endwithRamp) && distance <levelSize*.2){
					road.delFlag = true;
				}
				
			}
			//左右转道，回转道，（）
			if(road.fow == defineFOW.Uturn || road.fow == defineFOW.LeftRightTurn || road.fow == defineFOW.LeftTurn || road.fow == defineFOW.RightTurn 
					){
				double distance = PolygonUtil.twoPointDistance(road.coordinate.get(0), road.coordinate.get(road.coordinate.size()-1));
				//TODO 每层的毫米数
//				if(distance < levelSize*3){
					road.delFlag = true;
//				}
			}
			//环岛
//			if(road.fow == defineFOW.RoundAbout){
//				if(road.length < levelSize*0.5){
////					road.delFlag = true;
//				}
//			}
			//辅路
			if(road.fow == defineFOW.ByPath){
				//TODO 每层的毫米数，=====
//				if(road.length < levelSize*0.4){
					road.delFlag = true;
//				}
			}
			if(road.fow == defineFOW.MainAssistExchange){
				road.delFlag = true;
			}
			//，端头路，背景道路
			if((road.startNode.roads.size()==1 || road.endNode.roads.size() ==1) && road.styleColor == VectoryStyle.BackGroudRoad){
				//1，2层有效果
				if(road.length < levelSize*1){
					road.delFlag = true;
				}
			}
		}
		this.clearRoadList();
		this.clearNodeList();
		for (int i = roadList.size() - 1; i >= 0; i--) {
			RoadNew road = roadList.get(i);
			if (road.fow == defineFOW.RampSlipRoad) {
				if (road.startNode.roads.isEmpty() || road.endNode.roads.isEmpty()) {
					road.delFlag = true;
				}
			}
		}
		this.clearRoadList();
		this.clearNodeList();
	}

	// code below is for test
	// 1 道路只存在NodeList 中 而不在 RoadList 中
	public boolean roadExistTest() {
		for (NodeNew node : this.nodeList) {
			for (RoadNew roadCheck : node.roads) {
				if (!this.roadList.contains(roadCheck)) {
					System.out.println(roadCheck.roadId);
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * mm的block分割
	 */
	public void spliteRoadByMMBlock(){
		if(this.roadList == null || this.roadList.size() == 0){
			return;
		}
		this.taskData.blockInfo = new LevelInfo();
		this.taskData.blockInfo.iLevel = 88;
		this.taskData.blockInfo.iBlockWidth = 72000;
		this.taskData.blockInfo.iBlockHight = 48000;
		this.taskData.blockInfo.unitWidth = 9216000;
		this.taskData.blockInfo.unitHeight = 6144000;
		this.taskData.blockInfo.minScale = 10;
		this.taskData.blockInfo.maxScale = 70;
		this.taskData.blockInfo.scales = new int[]{10,20,50};
		this.splitRoadByBlock();
		//Map的应用过度啊
//		Map<BlockNo, Integer> blockNoCountNum = new HashMap<BlockNo, Integer>();
//		for (RoadNew roadNew : this.roadList) {
//			if(blockNoCountNum.containsKey(roadNew.blockNo)){
//				int num = blockNoCountNum.get(roadNew.blockNo);
//				roadNew.blockmmNo = num + 1;
//				blockNoCountNum.put(roadNew.blockNo, num + 1);
//			}else{
//				roadNew.blockmmNo = 0;
//				blockNoCountNum.put(roadNew.blockNo, 0);
//			}
//		}
		Collections.sort(this.roadList, new Comparator<RoadNew>() {

			@Override
			public int compare(RoadNew route1, RoadNew route2) {
				BlockNo blockNo1 = route1.blockNo;
				BlockNo blockNo2 = route2.blockNo;
				if(blockNo1.iY == blockNo2.iY){
					return blockNo1.iX - blockNo2.iX;
				}else{
					return blockNo1.iY - blockNo2.iY;
				}
			}
		});
		BlockNo blockNo = this.roadList.get(0).blockNo;
		int num = 0;
		for (int i = 0; i < this.roadList.size(); i++) {
			RoadNew roadNew = this.roadList.get(i);
			if(blockNo.equals(roadNew.blockNo)){
				roadNew.blockmmNo = num;
				roadNew.mmLineID = getMMLinkID();
				num = num + 1;
			}else{
				blockNo = roadNew.blockNo;
				num = 0;
				roadNew.blockmmNo = num;
				roadNew.mmLineID = getMMLinkID();
				num = num + 1;
			}
		}
		List<NodeNew> taskbord = new ArrayList<NodeNew>();
		for (NodeNew node : this.nodeList) {
			if(node.isTaskBorderNode(this)){
				taskbord.add(node);
			}
			this.judgeMMblockBordNode(node);
		}
		Network.putTaskBorderNode(taskbord);
	}
	/**
	 * 判断是否是block边界
	 * @param node
	 */
	public void judgeMMblockBordNode(NodeNew node) {
			ShpPoint regpoint = taskData.calcRegular(node.point);
			if (regpoint.x == 0 || regpoint.x == 255 || regpoint.y == 0
					|| regpoint.y == 255) {
				node.mmBlockBorderNode = true;
			}
	}

	/**
	 * ramp道处理
	 */
	public void rampRoadStyleHandle() {

		if(taskData.task.getLevel() != 1){
			return;
		}
		List<RouteForRampRoad> allRampRoute = new ArrayList<RouteForRampRoad>();
		for (int i = 0; i < taskData.network.roadList.size(); i++) {
			RoadNew routeRoad = taskData.network.roadList.get(i);
			if (routeRoad.fow == defineFOW.RampSlipRoad
					&& routeRoad.nrc == defineNRC.CityFreeWay) {
				if(routeRoad.routed){
					continue;
				}
				routeRoad.routed = true;
				RouteForRampRoad route = new RouteForRampRoad(taskData);
				DirRoad nextDirRoad = new DirRoad(routeRoad, true);
				route.roads.add(nextDirRoad);
				// 反序链接
				for (int j = 0; j < 500; j++) {
					if (nextDirRoad.dir == true) {
						nextDirRoad = route.nextRoadReverse();
					} else {
						nextDirRoad = route.nextRoadForward();
					}
					if (nextDirRoad == null) {
						break;
					}
					route.roads.add(nextDirRoad);

					nextDirRoad.road.routed = true;
				}

				// 调转顺序
				Collections.reverse(route.roads);

				nextDirRoad = route.roads.get(route.roads.size() - 1);
				// 正序链接
				for (int j = 0; j < 500; j++) {
					if (nextDirRoad.dir == true) {
						nextDirRoad = route.nextRoadForward();
					} else {
						nextDirRoad = route.nextRoadReverse();
					}
					if (nextDirRoad == null) {
						break;
					}
					route.roads.add(nextDirRoad);
					nextDirRoad.road.routed = true;
				}
				route.init();
				route.roadStyleTransform();
				allRampRoute.add(route);
			} else {
				continue;
			}
		}

		for (int i = 0; i < allRampRoute.size(); i++) {
			RouteForRampRoad route = allRampRoute.get(i);
			for (int j = 0; j < route.roads.size(); j++) {
				RoadNew roadNew = route.roads.get(j).road;
				roadNew.routed = false;
				roadNew.styleColor = route.layerNo;
				roadNew.nrc = route.nrc;
			}
		}
	}
	
}

package com.mansion.tele.business.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mansion.tele.business.TaskData.VectoryStyle;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.db.bean.elemnet.BaseName;
import com.mansion.tele.db.bean.elemnet.Road;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.PolygonUtil;
/**
 * Basic object ,use for build NetWork;
 * 路网中的道路实体
 * @author zhangj
 *
 */
public class RoadNew implements Serializable{
//	private final static double EARTH_RADIUS = 6378137;
	private static final long serialVersionUID = 7485892815952044601L;
	// 道路id，
	public String roadId;
	// 长度
	public int length;
	// 名称全称<正式名>
//	List<String> astRoadNames = new ArrayList<String>();
	String roadName="";
	// 描画颜色式样,矢量式样
	byte styleColor; 
	// 描画线型式样
	byte styleMode = 127; 
	// 管理等级
	public byte nrc; 
	// 道路形态
	public byte fow; 
	// 功能等级
	byte frc; 
//	byte bySRT; // 匝道类型
	// 道路通行方向
	public byte dtf;
	 // 跨压层级
	byte grade;
	// 道路番号;
	List<String> routeNo = new ArrayList<String>();
//	RouteNo routeNo2; 
	// 构筑物类型，
	public byte roadConstructType; 
	// 建筑状态
	byte cs; 
	//左下点-正规化后
	ShpPoint lb;
	//右上点-正规化后
	ShpPoint rt;
	// 起始连接点
	public NodeNew startNode = new NodeNew();
	//末点连接点
	public NodeNew endNode = new NodeNew();
	//末点角度,以Node为原点。两条路夹角180,Pi 表示顺畅
//	double startAngle;
//	double endAngle;
	double startAngle30m = defineValue.angle;
	double startAngle60m = defineValue.angle;
	double endAngle30m = defineValue.angle;
	double endAngle60m = defineValue.angle;
	//背景道路
	boolean bgRoad;
	
	// 道路形状
	public List<ShpPoint> coordinate = new ArrayList<ShpPoint>();
	
	//以上为道路的固有属性
	//删除标志
	public boolean delFlag;//true:删除
	//Route遍历标志，true：该道路已被加入到route
	public boolean routed;//ture：已链接进一条路线
	//所在Block块
	public BlockNo blockNo;
	//mm中block内道路编号
	public int blockmmNo;
	
	public int mmLineID;
	
	//from Hibernate Object to NetWork Object
	public void convert(Network network,Road road){
		this.roadId = road.getStrid();
		convertDBStyle(road.getBynrc(),road.getByfow());
		convertFow(road.getByfow(),road.getBysrt(),road.getBynrc());
		this.frc = road.getByfrc();
		this.dtf = road.getBydtf();
		this.grade = road.getBygrade();
		convertRouteNo(road);
		this.lb = road.getStGeom().getLbCoordinate();
		this.rt = road.getStGeom().getRtCoordinate();
		this.roadConstructType = road.getByroadconstructtype();
		this.cs = road.getBycs();
		for(ShpPoint point : road.getStGeom().getCoordinate()){
			if(coordinate.isEmpty()){
				coordinate.add(point);
				continue;
			}
			if (coordinate.get(coordinate.size() - 1).equals(point)) {
				continue;
			}
			if (coordinate.size() >= 2
					&& ((coordinate.get(coordinate.size() - 1).x == point.x && point.x == coordinate.get(coordinate.size() - 2).x) || ((coordinate
							.get(coordinate.size() - 1).y == point.y && point.y == coordinate.get(coordinate.size() - 2).y)))) {
				coordinate.set(coordinate.size() - 1, point);
				continue;
			}
			coordinate.add(point);
		}
//		regular(network);
		convertAstRoadNames(road);
		this.length = road.getIroadelelength();
//		convertAngle();
		this.startNode.nodeId = road.getStrStartNodeID();
		this.endNode.nodeId = road.getStrEndNodeID();
	}
	/**
	 * 计算与道路起末点的距离为distance点与起末点之间的北偏东夹角
	 * asc true 驶入方向，与起点夹角
	 * asc false 驶出方向，与末点之间夹角
	 * @param asc 正序 true 逆序 false 
	 * @param distance 与道路端点距离 
	 * @return 北偏东角度
	 */
	public double calcAngle(boolean asc,double distance){
		double angle = 0.0;
		if (length >= 0) {//原来为（length > distance），暂时屏蔽else代码，//TODO
			if(asc){
				angle = calcAngleByDistance(coordinate, distance);
			}else{
				angle = calcAngleByDistance(reverseCoordinate(), distance);
			}
		} else {//道路本身长度小于distance米，与相邻道路进行合并后再进行计算
			//TODO 有错误的代码
			List<ShpPoint> points = null;
			int totalLength = this.length;
			if(asc){//正向合并
				points = new ArrayList<ShpPoint>(coordinate);
				ShpPoint startPoint = coordinate.get(0);//简化内存存储
				RoadNew nextRoad = this.nextRoadForward();
				
				while (nextRoad != null) {
					points.clear();
					points.add(startPoint);
					points.addAll(nextRoad.coordinate);
					totalLength += nextRoad.length;
					if (totalLength >= distance) {
						break;
					}
					nextRoad = nextRoad.nextRoadForward();
				}
			}else{//逆向合并
				points = new ArrayList<ShpPoint>(coordinate);
				ShpPoint startPoint = coordinate.get(coordinate.size()-1);//简化内存存储
				RoadNew nextRoad = this.nextRoadReverse();
				while (nextRoad != null) {
					points.clear();
					points.add(startPoint);
					points.addAll(nextRoad.reverseCoordinate());
					totalLength += nextRoad.length;
					if (totalLength >= distance) {
						break;
					}
					nextRoad = nextRoad.nextRoadReverse();
				}
			}
			angle = calcAngleByDistance(points, distance);
		}
		return angle;
	}
	/**
	 * 
	 * @param points道路中的形状位置点
	 * @param distance与道路端点的距离
	 * @return 计算所得的角度
	 */
	private double calcAngleByDistance(List<ShpPoint> points,double distance){
		ShpPoint startPoint = points.get(0);
		ShpPoint distanceStartPoint = PolygonUtil.getPointByDistance(points, distance);
		return PolygonUtil.calcAngle(startPoint, distanceStartPoint);
	}
	/**
	 * 正向遍历路网 
	 * @return 当前道路的下一个道路
	 */
	public RoadNew nextRoadForward(){
		if(this.endNode.roads.size() == 1){
			return null;
		}
		else if(this.endNode.roads.size() == 2){
			RoadNew roadNext = this.endNode.roads.get(0);
			if(!this.equals(roadNext) && roadNext.startNode.equals(this.endNode)){
				return roadNext;
			}
			roadNext = this.endNode.roads.get(1);
			if(!this.equals(roadNext) && roadNext.startNode.equals(this.endNode)){
				return roadNext;
			}
		}
		for(RoadNew roadNext : this.endNode.roads){
			if(this.equals(roadNext)){
				continue;
			}
			if(this.endNode.equals(roadNext.startNode) && this.roadName.equals(roadNext.roadName)){
				return roadNext;
			}
		}
		return null;
	}
	/**
	 * 反向扩展路线 
	 * @return 非null：下一个可以连接到路线的道路，null：不能继续连接
	 */
	public RoadNew nextRoadReverse(){
		if(this.startNode.roads.size() == 1){
			return null;
		}
		else if(this.startNode.roads.size() == 2){
			RoadNew roadNext = this.startNode.roads.get(0);
			if(!this.equals(roadNext) && roadNext.endNode.equals(this.startNode)){
				return roadNext;
			}
			roadNext = this.startNode.roads.get(1);
			if(!this.equals(roadNext) && roadNext.endNode.equals(this.startNode)){
				return roadNext;
			}
		}
		for(RoadNew roadNext : this.startNode.roads){
			if(this.equals(roadNext)){
				continue;
			}
			if(roadNext.endNode.equals(this.startNode) &&this.roadName.equals(roadNext.roadName)){
				return roadNext;
			}
		}
		return null;
	}

	/**
	 * 反转道路方向，修改拓扑关系和形状
	 */
	public void reverse(){
//		Collections.reverse(regularCoordinate);
		Collections.reverse(coordinate);
	}
	
	public List<ShpPoint> reverseCoordinate(){
		List<ShpPoint> points = new ArrayList<ShpPoint>(coordinate.size());
		for (int i = coordinate.size()-1; i >= 0; i--) {
			points.add(coordinate.get(i));
		}
		return points;
	}
	private void convertRouteNo(Road road){
		if(road.getRouteNo1() != null){
			routeNo.add(road.getRouteNo1().getPrefix()+road.getRouteNo1().getNumber());
		}
		if(road.getRouteNo2() != null){
			routeNo.add(road.getRouteNo2().getPrefix()+road.getRouteNo2().getNumber());
		}
	}
	private void convertAstRoadNames(Road road){
		List<BaseName> listBaseName = road.getAstRoadNames();
		if (listBaseName != null
				&& listBaseName.size() != 0) {
			for (BaseName baseName : listBaseName) {
				if(baseName.getBynametype() == 1 && baseName.getBylantype() == 1){
					this.roadName = baseName.getStrnametext();
					break;
				}
			}
		}
	}
	/**
	 * 转换描画式样
	 * 	  
	 * @param bynrc
	 *            byte
	 * @param byFOW
	 *            byte
	 * @return byte
	 */
	public void convertStyle(byte level) {
		
		switch (level) {
	//便于以后扩展
		case 2://5级 ，6级
			if((frc == 5 || frc == 6) && this.nrc >= 6){
//				this.nrc = defineStyleColor.Background;
				this.styleColor = VectoryStyle.BackGroudRoad;
			}
			break;
		default:
			break;
		}
		if(level > 2){
			this.grade = 0;
		}
	}
	/**
	 * 将传入Road值赋给当前 Road,长度、形状、拓扑关系除外
	 */
	public void copy(RoadNew road){
		this.routeNo = road.routeNo;
		this.styleColor = road.styleColor;
		this.styleMode = road.styleMode;
		this.fow = road.fow;
		this.frc = road.frc;
		this.dtf = road.dtf;
		this.grade = road.grade;
		this.roadConstructType = road.roadConstructType;
		this.cs = road.cs;
		this.blockNo = road.blockNo;
		this.nrc = road.nrc;
	}

	/**
	 * 计算道路长度
	 */
	public void calcRoadLength() {
		double length = 0.0;
		for (int i = 0; i < coordinate.size() - 1; i++) {
			length += PolygonUtil.twoPointDistance(coordinate.get(i),
					coordinate.get(i + 1));
		}
		this.length = (int) Math.round(length);
	}

	/**
	 * 判断两条道路是否可以连接
	 * 
	 * @param road2
	 * @return true：可以连接，但不一定是最优的
	 */
	public static boolean isStyleEqual(RoadNew road1, RoadNew road2) {
		if (road1.styleColor != road2.styleColor) {
			return false;
		}
		if (road1.fow != road2.fow) {
			return false;
		}
		return true;
	}
	/**
	 * 判断两条道路是否可以连接
	 * 
	 * @param road2
	 * @return true：可以连接，但不一定是最优的
	 */
	public static boolean isEqualSingleLine(RoadNew road1, RoadNew road2) {
		if (road1.nrc != road2.nrc) {
			return false;
		}
		if (road1.fow != road2.fow) {
			return false;
		}
		if(!road1.roadName.equals(road2.roadName)){
			return false;
		}
		return true;
	}
	/**
	 * 转换描画式样
	 * 	styleColor矢量式样
	 * 	nrc mm式样
	 * @param bynrc
	 *            byte
	 * @param byFOW
	 *            byte
	 * @return byte
	 */
	
	private void convertDBStyle(byte nrc,byte fow) {
		switch (nrc) {
			case 10:
				if(fow == 9){
					this.styleColor = VectoryStyle.MinorRoad1;
					this.nrc = defineNRC.MinorRoad1;
				}else{
					this.styleColor = VectoryStyle.MinorRoad2;
					this.nrc = defineNRC.MinorRoad2;
				}
				break;
			case 9:
			case 8:
			case 7:
				this.styleColor = VectoryStyle.NormalRoad;
				this.nrc = defineNRC.NormalRoad;
				break;
			case 6:
				this.styleColor = VectoryStyle.CityMinorRoad;
				this.nrc = defineNRC.CityMinorRoad;
				break;
			case 5:
				this.styleColor = VectoryStyle.CityMajorRoad;
				this.nrc = defineNRC.CityMajorRoad;
				break;
			case 4:
				this.styleColor = VectoryStyle.ProcinceRoad;
				this.nrc = defineNRC.ProcinceRoad;
				break;
			case 3:
				this.styleColor = VectoryStyle.CityFreeWay;
				this.nrc = defineNRC.CityFreeWay;
				break;
			case 2:
				this.styleColor = VectoryStyle.NationalRoad;
				this.nrc = defineNRC.NationalRoad;
				break;
			case 1:
				this.styleColor = VectoryStyle.HightWay;
				this.nrc = defineNRC.HightWay;
				break;
			default:
				System.err.println("nrc from db is out of range "+nrc);
		}
	}
	private void convertFow(byte fow, byte srt, byte nrc){
		switch (fow) {
		case 1:
		case 2:
			this.fow = defineFOW.TwoWay;
			break;
		case 3:
		case 5:
		case 9:
			if(nrc == 1){
				this.fow = defineFOW.Sapa;
				break;
			}
		case 11:
			this.fow = defineFOW.OneWay;
			break;
		case 4:
			this.fow = defineFOW.RoundAbout;
			break;
		case 6://虚拟道路
				switch (srt) {
				case 2:
					this.fow = defineFOW.RampSlipRoad;
					break;
				case 3:
					this.fow = defineFOW.JctSlipRoad;
					break;
				case 4:
				case 5:
					this.fow = defineFOW.RightTurn;
					break;
				case 6:
				case 7:
					this.fow = defineFOW.LeftTurn;
					break;
				case 8:
					this.fow = defineFOW.LeftRightTurn;
					break;
				case 9:
					this.fow = defineFOW.Uturn;
					break;
				case 11:
					this.fow = defineFOW.JunctionLink;
					break;
				case 12:
					this.fow = defineFOW.MainAssistExchange;
					break;
				default:
					break;
				}
				break;
		case 7:
			this.fow = defineFOW.ByPath;
			break;
		case 8:
			this.nrc = defineNRC.Ferry;
			this.styleColor = VectoryStyle.Ferry;
			break;
		case 10:
			this.fow = defineFOW.WalkingRoad;
			break;
		default:
			break;
		}
	}
	
	// 道路形态
	public static final class defineFOW
//	1：快速路的一部分  //TwoWay
//	2：非快速路的多车道道路的一部分//TwoWay
//	3：单车道道路的一部分//OneWay
//	4：环岛的一部分//RoundAbout
//	5：封闭交通区域的一部分//OneWay
//	6：匝道的一部分
//	7：辅路的一部分（ByPath 侧道）
//	8：车渡联络线的一部分//nrc Freey
//	9：服务出入道路的一部分//OneWay
//	a：步行街的一部分//WalkingRoad
//	b：特殊交通形态的一部分//OneWay
	
//	"1：平行匝道
//	2：Ramp道
//	3：JCT
//	4：提前右转道
//	5：右转道
//	6：提前左转道
//	7：左转道
//	8：左右转道
//	9：回转道
//	a：其他
//	b：虚拟道路
//	c：主辅路出入路"

	{
		public static final byte OneWay = 1; // 本線(上下線非分離)リンク
		public static final byte TwoWay = 2;// 本線(上下線分離)リンク
		public static final byte JctSlipRoad = 3;// JCT
		public static final byte JunctionLink = 4;// 交差点内リンク
		public static final byte RampSlipRoad = 5;// ramp道
		public static final byte ByPath = 6;// 侧道
		public static final byte Sapa = 7;// 服务区停车区
		public static final byte RoundAbout = 8;//环岛
		public static final byte Uturn = 9;// 回转道
		public static final byte LeftTurn = 10;// 左转车道
		public static final byte RightTurn = 11;// 右转车道
		public static final byte LeftRightTurn = 14;// 左右转车道
		public static final byte MainAssistExchange = 17;// 主路与辅路之间的既出且入口
		public static final byte WalkingRoad = 20;// 步行街道，人行过街天桥或地道
	}

	// 管理等级 mm
	public static final class defineNRC
	{
		public static final byte HightWay = 0;// 高速道路
		public static final byte NationalRoad = 1;// 国道
		public static final byte CityFreeWay = 2;// 城市快速道路，城市间快速道路
		public static final byte ProcinceRoad = 3;// 省道
		public static final byte CityMajorRoad = 4;// 城市主干道
		public static final byte CityMinorRoad = 5;// 城市次干道
		public static final byte NormalRoad = 6;// 一般道
		public static final byte MinorRoad1 = 7;// 細道路（細道路1）
		public static final byte MinorRoad2 = 8;// 細道路（細道路2）
		public static final byte Reserved = 9;// 保留不用的(Reserved)
		public static final byte Ferry = 10;// Ferry link（航道） //：车渡联络线的一部分
	}
	
	// 颜色描画式样 vector
	public static final class defineStyleColor
	{
		public static final byte HightWay = 59;// 高速道路
		public static final byte NationalRoad = 58;// 国道
		public static final byte CityFreeWay = 2;// 城市快速道路，城市间快速道路
		public static final byte ProcinceRoad = 3;// 省道
		public static final byte CityMajorRoad = 4;// 城市主干道
		public static final byte CityMinorRoad = 5;// 城市次干道
		public static final byte NormalRoad = 6;// 一般道
		public static final byte MinorRoad1 = 7;// 細道路（細道路1）
		public static final byte MinorRoad2 = 8;// 細道路（細道路2）
		public static final byte Freey = 9;// Ferry link（航道）
		public static final byte Background = 10;// 背景道路
	}

	//线型描画式样
	public static final class defineStyleMode
	{
		public static final byte Tunnel = 0;// 隧道
		public static final byte Freey = 9;// 航道
	}
	
	// 交通流方向
	public static final class defineDTF
	{
		public static final byte TwoWayPass = 1;// 双向通行

		public static final byte NegPass = 2;// 正向禁行且反向通行

		public static final byte PosPass = 3;// 反向禁行且正向通行

		public static final byte NoPass = 4;// 双向禁行
	}

	// 道路构造物
	public static final class defineCONSTRUCT {
		public static final byte Bridge= 1;// 桥梁
		public static final byte Tunnel = 2;// 隧道
		public static final byte OverPass = 3;// 立交桥
		public static final byte Deep = 4;// 深槽路
		public static final byte OverHead = 5;// 高架路
	}
	// 收费站
	public static final class defineTOLL{
		public static final byte NoToll = 1;// 不是收费站
		public static final byte IsToll = 2;// 是收费站
	}
	// 默认值
	public static final class defineValue{
		public static final double angle = -1000;// 角度
	}
	//是否为虚拟道路
	public boolean isJunctionLink(){
		return (fow == defineFOW.JunctionLink);
	}
	//code below is for test
	public boolean testAngle(){
		if(this.startAngle30m == defineValue.angle || this.endAngle30m == defineValue.angle || this.startAngle60m == defineValue.angle || this.endAngle60m ==defineValue.angle){
			System.out.println(this.startAngle30m+" , "+this.endAngle30m +" , "+this.startAngle60m+" , "+this.endAngle60m);
			return false;
		}return true;
	}
}

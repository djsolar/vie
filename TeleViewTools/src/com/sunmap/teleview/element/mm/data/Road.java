package com.sunmap.teleview.element.mm.data;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.sunmap.teleview.element.mm.Device;
import com.sunmap.teleview.util.ToolsUnit;

public class Road {
	
	// 交通流方向
	public static final class defineDTF{
		public static final byte TwoWayPass = 1;// 双向通行

		public static final byte NegPass = 2;// 正向禁行且反向通行

		public static final byte PosPass = 3;// 反向禁行且正向通行

		public static final byte NoPass = 4;// 双向禁行
	}
	//当前道路是虚拟道路时候，blockid和脱出道路blockid一致
	public MMBlockID blockid;
	//道路ID
	public int roadID;
	
	//Link属性
	public int linkAttr;
	//Link属性2
	public byte linkAttr2;
	public List<Point> points = new ArrayList<Point>();
	
	/**
	 * 道路顺方向脱出是否有规制。true：有规制
	 */
	public boolean NegRuleFlag;
	/**
	 * 道路逆方向脱出是否有规制。true：有规制
	 */
	public boolean PassRuleFlag;

	//道路形状,block内正规化坐标（x,y校正后，x、y单位长度相同）
	public float pointxsf[];
	public float pointysf[];
	/**
	 * 形状的正北为0的顺时针角度。单位：角度[0,360)。
	 */
	public short angles[]; //形状的角度，个数比形状点少一；

	/**
	 * 形状点间的长度。单位：米
	 */
	public short distance[];//形状的距离
	
	/**
	 * 道路长度。单位：米
	 */
	public int length; 
	public Node startNode;
	public Node endNode;
	public boolean isDummyFlag = false;	//true:是虚拟道路；false:不是虚拟道路
	
	// 道路形态
	public static final class defineFOW
	{
		public static final byte OneWay = 1; // 本线(上下线非分离)
		public static final byte TwoWay = 2;// 本线(上下线分离)
		public static final byte JctSlipRoad = 3;// 连接路(JCT)
		public static final byte JunctionLink = 4;// 交差点内
		public static final byte RampSlipRoad = 5;//连接路(出入口)
		public static final byte ByPath = 6;// 侧道
		public static final byte Sapa = 7;// SA等侧线
		public static final byte RoundAbout = 8;//环岛
		public static final byte Uturn = 9;// 回转道
		public static final byte LeftTurn = 10;// 左转车道
		public static final byte RightTurn = 11;// 右转车道
		public static final byte BeforeLeftTurn = 12;// 提前左转车道
		public static final byte BeforeRightTurn = 13;// 提前右转车道
		public static final byte LeftRightTurn = 14;// 左右转车道
		public static final byte SlowLane = 15;// 慢车道
		public static final byte RESERVED = 16;// RESERVED
		public static final byte MainToAssist = 17;//主路通辅路出口
		public static final byte AssistToMain = 18;//辅路通主路入口
		public static final byte MainAssistExchange = 19;//主路与辅路之间的既出且入口
		public static final byte WalkingRoad = 20;//步行街道，人行过街天桥或地道
	}
	// 管理等级
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
		public static final byte Reserved = 9;//保留不用的(Reserved)
		public static final byte Freey = 10;// Ferry link（航道）
	}
		

	public double calcDis(Point2D.Float point){
		List<Point2D> listPoint = new ArrayList<Point2D>();
		for (int i = 0; i < points.size(); i++) {
			Point2D point2d = Device.geoToPix(new Point(points.get(i).x, points.get(i).y));
			listPoint.add(point2d);
		}
		double apeak = ToolsUnit.getApeak(listPoint, point,true);
		return apeak;
	}
	
	/**
	 * 获取道路形态
	 * @return
	 */
	public int getFow(){
		if(this.isDummyFlag){
			return defineFOW.JunctionLink;
		}
		return ToolsUnit.getBits(linkAttr,25,29);
	}
	
	
	
	public int getNRC(){
		if(this.isDummyFlag){
			return defineNRC.NormalRoad;
		}
		return ToolsUnit.getBits(linkAttr2, 4, 7);
	}

	public byte getDTF(){
		byte dtf = (byte) ToolsUnit.getBits(linkAttr, 30, 31);
		if(dtf == 0){
			dtf = defineDTF.TwoWayPass;
		}else if(dtf == 1){
			dtf = defineDTF.PosPass;
		}else if(dtf == 2){
			dtf = defineDTF.NegPass;
		}else if(dtf == 3){
			dtf = defineDTF.NoPass;
		}
		return dtf;
	}	

	

	
	@Override
	public boolean equals(Object o) {
		boolean isSame = false;
		Road compareRoad = (Road)o;
		if(this.isDummyFlag == false && compareRoad.isDummyFlag == false){
			if(this.roadID == compareRoad.roadID){
				isSame = true;
			}
		}else if(this.isDummyFlag && compareRoad.isDummyFlag){
			if(this.roadID == compareRoad.roadID
					&& this.length == compareRoad.length
					&& this.angles[0] == compareRoad.angles[0]){
				isSame = true;
			}
		}
		return isSame;
	}
	
	@Override
	public int hashCode(){
		int ret = (int) (this.roadID >> 32);
		ret += this.roadID << 32;
		return ret;
	}
	
	
	
	/**
	 * 获取表头
	 * @return 表头 String[]
	 */
	public String[]  getHeader() {
		String[] params = {"图页号","道路ID","道路长度","管理等级","道路形态","交通流","道路顺方向规制","道路逆方向规制","startNode连接道路","endNode连接道路"};
		String[] disTitles = new String[pointxsf.length];
		for(int i = 0; i < pointxsf.length; i++){
			disTitles[i] = "第" + (i + 1) + "点";
		}
		String[] alTit = new String[angles.length+1];
		for(int i = 0; i < angles.length; i++){
			alTit[i] =  "第" +(i+1)+"与"+ (i + 2) + "点";
		}
		String[] header = new String[params.length + disTitles.length + alTit.length];
		
		//合并数组
		System.arraycopy(params, 0,header, 0, params.length);
		System.arraycopy(disTitles, 0, header, params.length, disTitles.length);
		System.arraycopy(alTit, 0, header,params.length + disTitles.length, alTit.length);
		
		return header;
	}
	
	/**
	 * 获取表内容
	 * @return String[] 表内容
	 */
	public String[]  getValues() {
		String blockId = this.blockid.getKey();
		String roadID = String.valueOf(this.roadID);
		String length =  String.valueOf(this.length);
		String NRC =  String.valueOf(getNRC())+ " , "+ getMMRoadNRC(getNRC());
		String FOW = String.valueOf(getFow())+" , "+getRoadFOW(getFow());
		String DTF = String.valueOf(getDTF())+" , "+getRoadDTF(getDTF());
		String NegRuleFlag = String.valueOf(this.NegRuleFlag);
		String PassRuleFlag = String.valueOf(this.PassRuleFlag);
		String startNode = this.startNode.roadIndex.length + ": ";
		for (int i = 0; i < this.startNode.roadIndex.length; i++) {
			startNode += this.startNode.roadIndex[i] + ","
					+ this.startNode.blockIndexX[i] + ","
					+ this.startNode.blockIndexY[i] + " | ";
		}
		String endNode =String.valueOf( this.endNode.roadIndex.length) + ": ";
		for (int i = 0; i < this.endNode.roadIndex.length; i++) {
			endNode += this.endNode.roadIndex[i] + ","
					+ this.endNode.blockIndexX[i] + ","
					+ this.endNode.blockIndexY[i] + " | ";
		}
		String []paremValues = {blockId,roadID,length,NRC,FOW,DTF,NegRuleFlag,PassRuleFlag,startNode,endNode};
		String[] disValues = new String[pointxsf.length];
		for(int i = 0; i < pointxsf.length; i++){
			disValues[i] =   "经纬度：" + + points.get(i).x+" , "+points.get(i).y
					+" | 经纬度(正规划)：" + pointxsf[i]+" , "+pointysf[i];
		}
		String[] alValue = new String[angles.length];
		for(int i = 0; i < angles.length; i++){
			alValue[i] ="角度"+angles[i]+" | 点间长度"+distance[i]+"米";
		}
		String[] values = new String[paremValues.length + disValues.length + alValue.length];
		
		//合并数组
		System.arraycopy(paremValues, 0, values, 0, paremValues.length);
		System.arraycopy(disValues, 0, values, paremValues.length, disValues.length);
		System.arraycopy(alValue, 0, values, paremValues.length + disValues.length, alValue.length);
		return values;
	}
	/**
	 * 获取列表信息
	 * @return
	 */
	public String getStrList() {
		return "MM道路       roadID：" + roadID ;
	}
	
	private String getMMRoadNRC(int nrc) {
		String strNRCName = "";
		switch (nrc) {
		case 0:
			strNRCName = "高速道路";
			break;
		case 1:
			strNRCName = "国道";
			break;
		case 2:
			strNRCName = "城市快速道路";
			break;
		case 3:
			strNRCName = "省道";
			break;
		case 4:
			strNRCName = "城市主干道";
			break;
		case 5:
			strNRCName = "城市次干道";
			break;
		case 6:
			strNRCName = "一般道";
			break;
		case 7:
			strNRCName = "細道路1";
			break;
		case 8:
			strNRCName = "細道路2";
			break;
		case 9:
			strNRCName = "保留不用的道路";
			break;
		case 10:
			strNRCName = "航道";
			break;
		default:
			strNRCName = "其他";
			break;
		}
		return strNRCName;
	}
	private String getRoadFOW(int fow) {
		String strFOWName = "";
		switch(fow){
			case 1:
				strFOWName = "本线(上下线非分离)";
				break;
			case 2:
				strFOWName = "本线(上下线分离)";
				break;
			case 3:
				strFOWName = "连接路(JCT)";
				break;
			case 4:
				strFOWName = "虚拟道路";
				break;
			case 5:
				strFOWName = "连接路(ramp)";
				break;
			case 6:
				strFOWName = "侧道";
				break;
			case 7:
				strFOWName = "SA等侧线";
			case 8:
				strFOWName = "环岛";
				break;
			case 9:
				strFOWName = "回转道";
				break;
			case 10:
				strFOWName = "左转车道";
			case 11:
				strFOWName = "右转车道";
				break;
			case 12:
				strFOWName = "提前左转车道";
				break;
			case 13:
				strFOWName = "提前右转车道";
				break;
			case 14:
				strFOWName = "左右转车道";
				break;
			case 15:
				strFOWName = "慢车道";
				break;
			case 16:
				strFOWName = "RESERVED";
				break;
			case 17:
				strFOWName = "主路通辅路出口";
				break;
			case 18:
				strFOWName = "辅路通主路入口";
				break;
			case 19:
				strFOWName = "主路与辅路之间的既出且入口";
				break;
			case 20:
				strFOWName = "步行街道";
				break;
			default:
				strFOWName = "其他";
				break;
		}
		return strFOWName;
	}
	private String getRoadDTF(byte dtf) {
		String strDTFName = "";
		switch(dtf){
			case 1:
				strDTFName = "双向通行";
				break;
			case 2:
				strDTFName = "正向禁行且反向通行";
				break;
			case 3:
				strDTFName = "反向禁行且正向通行";
				break;
			case 4:
				strDTFName = "双向禁行";
				break;
			default:
				strDTFName = "其他";	
				break;
		}
		return strDTFName;
	}

}

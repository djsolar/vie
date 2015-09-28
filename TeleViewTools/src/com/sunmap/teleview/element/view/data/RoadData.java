package com.sunmap.teleview.element.view.data;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.sunmap.teleview.element.view.Device;
import com.sunmap.teleview.util.ToolsUnit;

public class RoadData {
	public float[] pointxs;
	public float[] pointys;
	public short attr;//线的属性，比如是否是桥梁、计划道路等，和layerNo配合使用
	public short priority;//道路的跨压关系
	public short layerNO;
	public List<Point> points = new ArrayList<Point>();
	public String blockID;
	
	
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
	 * 获取表头
	 * @return 表头
	 */
	public String[] getHeader() {
		String[] params = {"Block位置","道路属性","类型","是否为计划道路","是否为隧道","是否为桥梁","始点夸压","尾点夸压","跨压关系","点个数"};
		String[] disTitles = new String[pointxs.length+1];
		for(int i = 0; i < pointxs.length; i++){
			disTitles[i] = "第" + (i + 1) + "点";
		}
		String[]  header= new String[params.length + disTitles.length];
		//合并数组
		System.arraycopy(params, 0, header, 0, params.length);
		System.arraycopy(disTitles, 0, header, params.length, disTitles.length);
		return header;
	}
	/**
	 * 获取表内容
	 * @return String[] 表内容
	 */
	public String[]  getValues(){
		String blockID = this.blockID;
		String attr = String.valueOf(this.attr);
		String layerNO = String.valueOf(this.layerNO)+" ： "+getRoadNRC(this.layerNO);
		String isRoad =  String.valueOf(ToolsUnit.getBits(this.attr, 15, 16));
		String isTunnel =  String.valueOf(ToolsUnit.getBits(this.attr, 14, 15));
		String isBridge =  String.valueOf(ToolsUnit.getBits(this.attr, 13, 14));
		String isStart= String.valueOf(ToolsUnit.getBits(this.attr, 12, 13));
		String isEnd= String.valueOf(ToolsUnit.getBits(this.attr, 11, 12));
		String over= String.valueOf(this.priority);
		String count= String.valueOf(pointxs.length);
		String []paremValues = {blockID,attr,layerNO, isRoad,isTunnel,isBridge,isStart,isEnd,over,count};
		String[] disValues = new String[pointxs.length+1];
		for(int i = 0; i < pointxs.length; i++){
			disValues[i] =  "经纬度：" + + points.get(i).x+" , "+points.get(i).y
					+" | 经纬度(正规划)：" + pointxs[i]+" , "+pointys[i];
		}
		String[] values = new String[paremValues.length + disValues.length];
		//合并数组
		System.arraycopy(paremValues, 0, values, 0, paremValues.length);
		System.arraycopy(disValues, 0, values, paremValues.length, disValues.length);
		return values;
		
	}
	
	/**
	 * 获取列表信息
	 * @return
	 */
	public String getStrList() {
		return "道路    管理等级： " + layerNO +"   线属性 ："+attr+"    点个数： "+pointxs.length;
	}
	
	private String getRoadNRC(int nrc) {
		String strNRCName = "";
		switch(nrc){
			case 59:
				strNRCName = "高速道路";
				break;
			case 58:
				strNRCName = "国道";
				break;
			case 57:
				strNRCName = "城市快速道路";
				break;
			case 56:
				strNRCName = "省道";
				break;
			case 55:
				strNRCName = "城市主干道";
				break;
			case 54:
				strNRCName = "城市次干道";
				break;
			case 53:
				strNRCName = "一般道";
				break;
			case 52:
				strNRCName = "細道路1";
				break;
			case 51:
				strNRCName = "細道路2";
				break;
			case 50:
				strNRCName = "航道";
				break;
			case 40:
				strNRCName = "保留不用的道路";
				break;
			case 41:	
				strNRCName = "铁路";
				break;
			case 13:	
				strNRCName = "行政界（县）";
				break;
			case 12:	
				strNRCName = "行政界（市）";
				break;
			case 11:	
				strNRCName = "特殊边界（当前指港澳区界）";
				break;
			case 10:	
				strNRCName = "行政界（省）";
				break;
			case 9:	
				strNRCName = "行政界（未定国界）";
				break;
			case 8:	
				strNRCName = "行政界（国界）";
				break;
			case 7:	
				strNRCName = "水系";
				break;
			case 6:	
				strNRCName = "绿地、公园";
				break;
			case 5:	
				strNRCName = "海岸线";
				break;
			case 4:	
				strNRCName = "海疆线";
				break;
			case 110:	
				strNRCName = "磁悬浮";
				break;
			case 70:
			case 71:
			case 72:
			case 73:
			case 74:
			case 75:
			case 76:
			case 77:
			case 78:
			case 79:
			case 80:
			case 81:
			case 82:
			case 83:
				strNRCName = "地铁";
				break;	
			case 90:
			case 91:
			case 92:
			case 93:
				strNRCName = "轻轨";
				break;
			default:
				strNRCName = "其他";
				break;
		}
		return strNRCName;
	}
}

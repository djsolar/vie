package com.sunmap.teleview.element.view.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BackGroundData {
	public short[] pointxs;
	public short[] pointys;
	public short layerNo;
	public String blockidString;
	public List<Point> points = new ArrayList<Point>();
	
	@Override
	public boolean equals(Object o){
		BackGroundData other = (BackGroundData)o;
		if(other.blockidString.equalsIgnoreCase(blockidString) == false){
			return false;
		}
		if(other.layerNo != layerNo || other.pointxs.length != pointxs.length){
			return false;
		}
		for(int i = 0; i < pointxs.length; i++){
			if(pointxs[i] != other.pointxs[i] || pointys[i] != other.pointys[i]){
				return false;
			}
		}
			
		return true;
	}
	
	/**
	 * 获取表头
	 * @return 表头
	 */
	public String[] getHeader() {
		String[] params = {"管理等级","点个数"};
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
		String layerNO = String.valueOf(this.layerNo)+" ： "+ getRoadNRC(this.layerNo);
		String count= String.valueOf(pointxs.length);
		String []paremValues = {layerNO,count};
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
		return "背景      管理等级： " + layerNo ;
	}
	
	public String getRoadNRC(int nrc) {
		String strNRCName = "";
		switch(nrc){
			case 1:
				strNRCName = "海域";
				break;
			case 2:
				strNRCName = "省级行政面";
				break;
			case 3:
				strNRCName = "岛";
				break;
			case 4:
				strNRCName = "海疆线";
				break;
			case 5:
				strNRCName = "海岸线";
				break;
			case 6:
				strNRCName = "绿地、公园";
				break;
			case 7:
				strNRCName = "水系";
				break;
			case 8:
				strNRCName = "行政界（国界）";
				break;
			case 9:
				strNRCName = "行政界（未定国界）";
				break;
			case 10:
				strNRCName = "行政界（省）";
				break;
			case 11:
				strNRCName = "特殊边界（当前指港澳区界）";
				break;
			case 12:
				strNRCName = "行政界（市）";
				break;
			case 13:
				strNRCName = "行政界（县）";
				break;
			default:
				strNRCName = "其他";
				break;
		}
		return strNRCName;
	}


}

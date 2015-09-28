package com.sunmap.teleview.element.view.data;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.sunmap.teleview.element.view.Device;
import com.sunmap.teleview.util.ToolsUnit;

public class RoadTextLineData {
	
	public short pointxs[];//基准点坐标X
	public short pointys[];
	public String blockIDString;
	public List<Point> points = new ArrayList<Point>();
	
	public String getStrList() {
		return "文字线    " +"    点个数： "+pointxs.length;
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
	 * 获取表头
	 * @return 表头
	 */
	public String[] getHeader() {
		String[] params = {"Block位置","点个数"};
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
		String blockID = this.blockIDString;
		String count= String.valueOf(pointxs.length);
		String []paremValues = {blockID,count};
		String[] disValues = new String[pointxs.length+1];
		for(int i = 0; i < pointxs.length; i++){
			disValues[i] =  "经纬度：" +  points.get(i) .x+" , "+points.get(i).y
					+" | 经纬度(正规划)：" + pointxs[i]+" , "+pointys[i];
		}
		String[] values = new String[paremValues.length + disValues.length];
		//合并数组
		System.arraycopy(paremValues, 0, values, 0, paremValues.length);
		System.arraycopy(disValues, 0, values, paremValues.length, disValues.length);
		return values;
		
	}

}

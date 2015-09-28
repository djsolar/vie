package com.sunmap.teleview.element.view.data;

import java.awt.Point;
import java.awt.geom.Point2D;

import com.sunmap.teleview.element.view.Device;
import com.sunmap.teleview.element.view.IconSearcher;
import com.sunmap.teleview.util.ToolsUnit;

public class TeleViewText {

	public String str;
	public int x;//2560分之一秒
	public int y;//2560分之一秒
	public short type;//类型 -1为道路名称
	public byte positionInfo;//文字配置情报
	public int number;//在Block序号内的顺序
	public TeleViewText() {
		
	}
	
	public TeleViewText(String str,int x,int y){
		this.str = str;
		this.x = x;
		this.y = y;
	}

	public double calcDis(Point2D point){
		Point2D pixPoint = Device.geoToPix(new Point(x, y));
		double disPix = ToolsUnit.calcTwoPointDistance(point, pixPoint);
		return disPix;
	}
	
	/**
	 * 获取文字配置情报矩形框Point
	 * @return
	 */
	public Point getPositionInfoPoint(byte b,int fontsize){
		String[] name = str.split("\\\r\\\n");
		int row = name.length;
		int count = name[0].length();
		Point2D.Double pointFloat = Device.geoToPix(new Point(x, y));
		int x = (int)( pointFloat.x + 0.5);
		int y = (int)( pointFloat.y + 0.5);
		switch (b) {
		case 1://文字在图标的右方
			x = x+fontsize;
			y = y-(row - 2) * fontsize/2; 
			break;
		case 2://文字在图标的左方
			x = x - fontsize * count - fontsize;
			y = y-(row - 2) * fontsize/2; 
			break;
		case 3://文字在图标的正上方
			x = x - fontsize * count / 2;
			y = y - fontsize * (row - 1) - fontsize;
			break;
		case 4://文字在图标的正下方
			x = x - fontsize * count / 2;
			y = y + fontsize + fontsize;
			break;
		case 0://文字在图标的中心
			x = x - fontsize * count / 2;
			y = y-(row - 2) * fontsize/2;
			break;

		}
		return new Point(x, y);
		
	}
	/**
	 * 获取表头
	 * @return 表头
	 */
	public String[] getHeader() {
		String[] params = {"名称","位置","类型","序号","文字配置情报","总行数,总占个数"};
		String[]  header= new String[params.length+1];
		//合并数组
		System.arraycopy(params, 0, header, 0, params.length);
		return header;
	}
	/**
	 * 获取表内容
	 * @return String[] 表内容
	 */
	public String[]  getValues(){
		String name = this.str;
		String location = "经纬度："+ String.valueOf(x)+" , "+String.valueOf(y)+" | ";
        String type = String.format("0x%04X", this.type)+"       "+IconSearcher.getTypeName(this.type);  //以十六进制显示，格式化成十六进制的数据
		String number = String.valueOf(this.number);
		String pos = String.valueOf(this.positionInfo);
		String[] txtName = str.split("\\\r\\\n");
		int row = txtName.length;
		int sunmap = 0;		
		for (String string : txtName) {
			sunmap += string.length();
		}
		String len = String.valueOf(row)+ "      " + String.valueOf(sunmap);
		String []paremValues = {name,location,type, number,pos,len};
		String[] values = new String[paremValues.length ];
		//合并数组
		System.arraycopy(paremValues, 0, values, 0, paremValues.length);
		return values;
	}
	
	/**
	 * 获取列表信息
	 * @return
	 */
	public String getStrList() {
		String type = String.format("0x%04X", this.type);
		return "POI    类型： " + type +"   所在序号 ："+this.number;
	}	
	
	
}

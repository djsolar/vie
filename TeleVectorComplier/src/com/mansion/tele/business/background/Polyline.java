package com.mansion.tele.business.background;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.background.Background.Type;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
/**
 * Basic object,use for build background.
 * @author zhangj
 *
 */
public class Polyline implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Type type;
//	Attr attr;//属性。
	List<Edge> edges = new ArrayList<Edge>(2);
	String name;
	String id;
	
	public BlockNo blockNo;
	
	boolean delFlag;//二叉合并删除时设置为true，排序后同一删除 
	/**
	 * 简单属性赋值, 除坐标点以外
	 * @param line
	 */
	void copy(Polyline line){
		this.type = line.type;
//		this.attr = line.attr;
		this.name = line.name;
		this.blockNo = line.blockNo;
	}
	public ShpPoint getTailPoint(){
		return edges.get(edges.size()-1).coordinate.get(edges.get(edges.size()-1).coordinate.size()-1);
	}
	public ShpPoint getHeadPoint(){
		return edges.get(0).coordinate.get(0);
	}
//	public static enum Attr{
//		NationBorder,//国界
//		ProviceBorder,//省级区界
//		CityBorder,//市级区界
//		CountryBorder,//区县级区界
//		UnNationBorder,//未定国界
//		SeaBankBorder,//海岸线
//		SpecialBorder,//特殊边界（当前指港澳区界）
//		Canal,//运河（线）
//		River,//河流（线）
//	}
}

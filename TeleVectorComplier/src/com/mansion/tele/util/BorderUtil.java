package com.mansion.tele.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mansion.tele.common.ConstantValue;
import com.mansion.tele.db.bean.elemnet.BaseBorder;
import com.mansion.tele.db.bean.elemnet.ShpPoint;


/**
 * 面要素的边界处理工具
 * @author hefeng
 *
 */
public class BorderUtil {
	
	/**
	 * 边界比较器
	 */
	private static Comparator<BaseBorder> comparator = 
		new Comparator<BaseBorder>() {

		@Override
		public int compare(BaseBorder o1, BaseBorder o2) {
			String stra = o1.getStrid();
			String strb = o2.getStrid();
			return stra.compareTo(strb);
		}
	};
	
	/**
	 * 默认构造器
	 */
	private BorderUtil() {
		super();
	}

	
	
	/**
	 * 由边围成面要素形状
	 * @param <T> 继承baseborder
	 * @param baseBorders 边界list
	 * @return List<ShpPoint>面形状点集合
	 */
	public static <T extends BaseBorder> List<ShpPoint> getPolygon(List<T> baseBorders){
		List<ShpPoint> polygonShp = new ArrayList<ShpPoint>();
		if (baseBorders != null && baseBorders.size() > 0) {
			Collections.sort(baseBorders, BorderUtil.comparator);
			BaseBorder curBorder = (BaseBorder) baseBorders.get(0);
			String startID = curBorder.getStrid();
			do {
				List<ShpPoint> shpPoints = curBorder.getStGeom().getCoordinate();
				if (shpPoints == null || shpPoints.size() < 2) {
					break;
				}
				if (curBorder.getByisdisplay() == ConstantValue.BORDER_BYISDISPLAY_NO_2) {
					shpPoints.get(0).z = 2;
					shpPoints.get(shpPoints.size() - 1).z = 3;
				}
				BorderUtil.add(shpPoints, polygonShp);
				BaseBorder nextBorder = new BaseBorder();
				nextBorder.setStrid(curBorder.getStrNextBorderID());
				int index = Collections.binarySearch(baseBorders, nextBorder, BorderUtil.comparator);
				if (index >= 0) {
					curBorder = baseBorders.get(index);
				}else {
					break;
				}
				
			} while (!startID.equals(curBorder.getStrid()));
			
			
		}
		return polygonShp;
	}



	/**
	 * 将border的形状点添加到面形状点集合中
	 * @param shpPoints border形状点
	 * @param polygonShp 面形状点
	 */
	private static void add(List<ShpPoint> shpPoints, List<ShpPoint> polygonShp) {
		for (int i = 0; i < shpPoints.size(); i++) {
			if (shpPoints.get(i).z == 2) {
				if (polygonShp.size() > 0) {
					polygonShp.get(polygonShp.size()-1).z = 2;
				}else {
					polygonShp.add(shpPoints.get(i));
				}
			}else {
				if (!polygonShp.contains(shpPoints.get(i))) {
					polygonShp.add(shpPoints.get(i));
				}else if (i != 0 && 
						polygonShp.get(0).equals(shpPoints.get(i))) {
					polygonShp.add(shpPoints.get(i));
				}
			}
		}
	}

}

package com.mansion.tele.util;

import java.util.List;

import com.mansion.tele.common.GeoLocation;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.db.bean.elemnet.PolygonShp;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

/**
 * 形状处理工具
 * @author hefeng
 *
 */
public class GeoUtil {
	
	/** 东沙群岛矩形范围的左下坐标点的x */
	public static final int DONGSA_LB_X = 1061827764;
	/** 东沙群岛矩形范围的左下坐标点的y */
	public static final int DONGSA_LB_Y = 179645741;
	
	/** 东沙群岛矩形范围的右上坐标点的x */
	public static final int DONGSA_RT_X = 1092764611;
	/** 东沙群岛矩形范围的右上坐标点的y */
	public static final int DONGSA_RT_Y = 200270305;
	
	/** 西沙群岛矩形范围的左下坐标点的x */
	public static final int NANSA_LB_X = 1005736658;
	/** 西沙群岛矩形范围的左下坐标点的y */
	public static final int NANSA_LB_Y = 28141929;
	
	/** 西沙群岛矩形范围的右上坐标点的x */
	public static final int NANSA_RT_X = 1116473223;
	/** 西沙群岛矩形范围的右上坐标点的y */
	public static final int NANSA_RT_Y = 162876235;
	
	/** 钓鱼岛矩形范围的左下坐标点的x */
	public static final int DIAOYU_LB_X = 1132953600;
	/** 钓鱼岛矩形范围的左下坐标点的y */
	public static final int DIAOYU_LB_Y = 230339764;
	
	/** 钓鱼岛矩形范围的右上坐标点的x */
	public static final int DIAOYU_RT_X = 1156373082;
	/** 钓鱼岛矩形范围的右上坐标点的y */
	public static final int DIAOYU_RT_Y = 242290447;
	
	/**
	 * 东沙群岛的位置
	 */
	private static GeoRect specialIslandRect_dongsa = GeoRect.
	                      valueOf(GeoLocation.valueOf(GeoUtil.DONGSA_LB_X, GeoUtil.DONGSA_LB_Y), 
	                    		  GeoLocation.valueOf(GeoUtil.DONGSA_RT_X, GeoUtil.DONGSA_RT_Y));
	/**
	 * 南沙群岛的位置
	 */
	private static GeoRect specialIslandRect_nansa = GeoRect.
	                      valueOf(GeoLocation.valueOf(GeoUtil.NANSA_LB_X, GeoUtil.NANSA_LB_Y), 
	                    		  GeoLocation.valueOf(GeoUtil.NANSA_RT_X, GeoUtil.NANSA_RT_Y));
	/**
	 * 钓鱼岛的位置
	 */
	private static GeoRect specialIslandRect_diaoyu = GeoRect.
	                      valueOf(GeoLocation.valueOf(GeoUtil.DIAOYU_LB_X, GeoUtil.DIAOYU_LB_X), 
	                    		  GeoLocation.valueOf(GeoUtil.DIAOYU_RT_X, GeoUtil.DIAOYU_RT_Y));
	
	/**
	 * 取得多边形的外接矩形
	 * @param rect 外接矩形
	 * @param shpList 多边形的形状点
	 * @return  标识 -1 错误图形， 0 获取成功
	 */
	public static int getRectByPoints(GeoRect rect, List<ShpPoint> shpList){
		
		if (2 > shpList.size()) {
			
			return -1;
		}
		rect.left = shpList.get(0).x;
		rect.bottom = shpList.get(0).y;
		rect.right = shpList.get(0).x;
		rect.top = shpList.get(0).y;
		for (int i = 0; i < shpList.size(); ++i) {
			if (shpList.get(i).x < rect.left) {
				rect.left = shpList.get(i).x;
			}
			if (shpList.get(i).y < rect.bottom) {
				rect.bottom = shpList.get(i).y;
			}
			if (shpList.get(i).x > rect.right) {
				rect.right = shpList.get(i).x;
			}
			if (shpList.get(i).y > rect.top) {
				rect.top = shpList.get(i).y;
			}
			
		}
		return 0;
	}
	
	/**
	 * 获得多边形的矩形范围
	 * @param shp 多边形
	 * @return  矩形范围
	 */
	public static GeoRect getRectByPoints(List<ShpPoint> shp){
		GeoRect rect = new GeoRect();
		int i = GeoUtil.getRectByPoints(rect, shp);
		if (i != -1) {
			return rect;
		}
		return null;
		
	}

	/**
	 * 提供给敏感岛屿，判断形状是否在指定矩形范围内
	 * @param stGeom 源形状
	 * @return  是否在指定矩形范围内
	 */
	public static boolean isInRect(PolygonShp stGeom) {
		boolean flag = false;
		
		if (GeoUtil.rectCompare(stGeom, GeoUtil.specialIslandRect_nansa)) {
			flag = true;
		}
		if (GeoUtil.rectCompare(stGeom, GeoUtil.specialIslandRect_dongsa)) {
			flag = true;
		}
		if (GeoUtil.rectCompare(stGeom, GeoUtil.specialIslandRect_diaoyu)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 矩形范围比较
	 * @param stGeom 源形状
	 * @param rect 矩形范围
	 * @return boolean 源形状是否在指定矩形范围内
	 */
	public static boolean rectCompare(PolygonShp stGeom, GeoRect rect){
		ShpPoint lb = stGeom.getLbCoordinate();
		ShpPoint rt = stGeom.getRtCoordinate();
			
		if (lb.x > rect.left && lb.y > rect.bottom && rt.x < rect.right	&& rt.y < rect.top) {
			return true;
		}
		return false;
	}
}

package com.sunmap.teleview.element.mm.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.sunmap.teleview.util.GeoRect;

public class MMBaseInfo {
	public static int curMapScale = 50;
	public static Point centerPoint = new Point();
	public static String mmFilePath;
	
	public static final int XStart = 184320000;// 0.0Block开始位置的经纬度 单位1/2560秒
	public static final int YStart = 0;
	public static final int LonBlockWidth = 72000;// 每个Block经纬度跨度 单位1/2560秒
	public static final int LatBlockHeight = 48000;
	public static final float xyUnitRatio = 0.846f;// y方向和x方向上一个正规化坐标单位的长度比，即如果x方向上一个正规化坐标长a米，y方向上一个正规化坐标长就为0.846*a米
	public static final float MaxUnitX = 255; // 正规化坐标与经纬度的转换关系
	public static final float MaxUnitY = 255 * xyUnitRatio;
	public static double meterPerUnit;//一个正规化坐标代表的米数
	
	@SuppressWarnings("static-access")
	public void setMMBaseInfo(Point centerPoint, int scale) {
		this.curMapScale = scale;
		this.centerPoint.x = centerPoint.x;
		this.centerPoint.y = centerPoint.y;
	}
	
	/**
	 * 计算范围内包含的BlockID
	 * 
	 * @param rect
	 *            矩形框
	 */
	public List<MMBlockID> calcMMBlockIDs(GeoRect rect) {
		List<MMBlockID> screenBlockIDs = new ArrayList<MMBlockID>();
		int x1 = rect.pointLB.longitude - XStart;
		int y1 = rect.pointLB.latitude - YStart;
		int x2 = rect.pointRT.longitude - XStart;
		int y2 = rect.pointRT.latitude - YStart;
		int xStartIndex = (int) Math.floor((float) x1 / LonBlockWidth);
		int xEndIndex = (int) Math.floor((float) x2 / LonBlockWidth);
		int yStartIndex = (int) Math.floor((float) y1 / LatBlockHeight);
		int yEndIndex = (int) Math.floor((float) y2 / LatBlockHeight);

		short mapCountX = (short) (xEndIndex - xStartIndex + 1);
		short mapCountY = (short) (yEndIndex - yStartIndex + 1);

		for (int i = 0; i < mapCountX; i++) {
			for (int j = 0; j < mapCountY; j++) {
				MMBlockID blockId = new MMBlockID(xStartIndex + i, yStartIndex + j);
				screenBlockIDs.add(blockId);
			}
		}
		return screenBlockIDs;
	}
}

package com.mansion.tele.business.landmark;

import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.DataManager.LevelInfo;
import com.mansion.tele.business.Task;
import com.mansion.tele.business.network.Network;
import com.mansion.tele.business.network.RoadNew;
import com.mansion.tele.business.network.RoadNew.defineNRC;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.PolygonUtil;

/**
 * 
 * @author zhangj 
 * 建立空间索引 
 * 建立空间索引的涵义
 * 空间索引是，未来提高排版效率所制定的功能，将数据以某一范围进行分散，这样就可以使用同一范围内的数据进行排版比较，而无需全部排版
 * 
 * 空间索引公式=倍数 * 比例尺 * 一个字的毫米数 *最多显示的数据 /10）
 * 
 * ps：此公式是计算一个排版框范围的计算公式，排版框范围，应为x方向一个值，y方向一个值，通过此公式计算，每个比例尺，x，y的长度
 * 
 * 词义解析：1：倍数：在x/y方向文字以最大长度/高度显示的倍数（这个倍数关系到排版所设置的矩形大小即排版密度，以及排版比较的效率）
 * 
 * 当前给定的倍数为x方向10，y方向5
 * 
 * 每个字的宽度为2mm
 */
public class CreateIndexImpl {
	private int down;
	private int up;
	private int left;
	private int right;
	private double unit_X;// x轴坐标最小单位的经度
	private double unit_Y;// x轴坐标最小单位的经度
	private int numx;
	private int numy;
	private List<Index> indexList = new ArrayList<Index>();
	//建立地标空间索引
	public void indexLandMarkType(List<MarkPoint> landMarkList) {
		for (MarkPoint landMarkType : landMarkList) {
			if(Style.DisPlay.lineShow.equals(landMarkType.display)){
				continue;
			}
			ShpPoint shpPoint = landMarkType.getBasicPoint();
			int index_X = (int) ((shpPoint.x - left) / unit_X);
			int index_Y = (int) ((shpPoint.y - down) / unit_Y);
			int indexNo = index_Y * (numx + 1) + index_X;
			Index index = indexList.get(indexNo);
			index.landMarkList.add(landMarkType);
//			if (!indexList.contains(index)) {
//				index.add(landMarkType);
//				indexList.add(index);
//			} else {
//				indexList.get(indexList.indexOf(index)).add(landMarkType);
//			}
			//地标中加入索引
			landMarkType.setIndex_X(index_X);
			landMarkType.setIndex_Y(index_Y);
		}
	}
	
	/**
	 * 建立道路名空间索引
	 * @param roadNameList
	 */
	public void indexRoadNameType(List<MarkPoint> roadNameList) {
		for (MarkPoint roadNameType : roadNameList) {
			ShpPoint shpPoint = roadNameType.getBasicPoint();
			int index_X = (int) ((shpPoint.x - left) / unit_X);
			int index_Y = (int) ((shpPoint.y - down) / unit_Y);
			int indexNo = index_Y * (numx + 1) + index_X;
			Index index = indexList.get(indexNo);
			index.roadNameList.add(roadNameType);
			//道路名中加入索引
			roadNameType.setIndex_X(index_X);
			roadNameType.setIndex_Y(index_Y);
		}
	}
	
	//任务块初始化
	public CreateIndexImpl(Task task) {
		LevelInfo levelInfos = DataManager.getLevelInfo(task.getLevel());
		int scale = levelInfos.scales[levelInfos.scales.length - 1];
		down = task.getBottom();
		up = task.getTop();
		left = task.getLeft();
		right = task.getRight();
		int xRectPer = 20 * scale * 2 * 6 / 10;// x轴坐标最小单位的距离
		int yRectPer = 10 * scale * 2 * 2 / 10;// y轴坐标最小单位的距离
		unit_Y = (up - down) / (PolygonUtil.twoPointDistance(new ShpPoint(left, up), new ShpPoint(left, down)) / yRectPer);// x轴坐标最小单位的经度
		unit_X = (right - left) / (PolygonUtil.twoPointDistance(new ShpPoint(right, down), new ShpPoint(left, down)) / xRectPer);// x轴坐标最小单位的经度
		numx = (int) ((right - left) / unit_X);
		if((right - left) % unit_X > 0){
			numx = numx + 1;
		}
		numy = (int) ((up - down) / unit_Y);
		if((up - down) % unit_Y > 0){
			numy = numy + 1;
		}
		for (int i = 0; i <= numy; i++) {
			for (int j = 0; j <= numx; j++) {
				Index index = new Index(j, i);
				this.indexList.add(index);
			}
		}
		// 统计块数
		// int yCount = (int) (twoPointDistance(new ShpPoint(left, up), new
		// ShpPoint(left,
		// down)) / yRectPer);
		// int xCount = (int) (twoPointDistance(new ShpPoint(right, down), new
		// ShpPoint(
		// left, down)) / xRectPer);
		// System.out.println(task.getiTaskID()
		// +" Level: "+task.getbLevel()+" scale: "+scaleKey.getiScale());
		// System.out.println("xCount : "+xCount +" 每块X:"+xRectPer);
		// System.out.println("yCount : "+yCount+"每块Y："+yRectPer);
		// System.out.println("任务 yL : "+(twoPointDistance(new ShpPoint(left,
		// up), new ShpPoint(left,
		// down))));
		// System.out.println("任务 xL : "+(twoPointDistance(new ShpPoint(right,
		// down), new ShpPoint(
		// left, down))));
	}

//  道路建立索引

	//获得任务下的所有索引
	public List<Index> getIndexList() {
		return indexList;
	}
	//空间索引类
	public static class Index{
		//索引 x
		private int index_X;
		//索引 y
		private int index_Y;
		//该索引包含的所有地标
		private List<MarkPoint> landMarkList = new ArrayList<MarkPoint>();
		//该索引包含的所有道路名
		private List<MarkPoint> roadNameList = new ArrayList<MarkPoint>();
		public List<MarkPoint> getRoadNameList() {
			return roadNameList;
		}
		
		public int compareIndex(Index index){
			if(this.index_Y == index.index_Y){
				return this.index_X - index.index_X;
			}else{
				return this.index_Y - index.index_Y;
			}
		}
		
		public List<MarkPoint> getLandMarkList() {
			return landMarkList;
		}
//		public List<LandMarkType> cloneData(){
//			List<LandMarkType> cloneLand = new ArrayList<LandMarkType>();
//			for (int i = 0; i < landMarkList.size(); i++) {
//				try {
//					cloneLand.add((LandMarkType) landMarkList.get(i).clone());
//				} catch (CloneNotSupportedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			return cloneLand;
//		}
		public int[] getIndexXY(){
			return new int[]{index_X,index_Y};
		}
//		public List<ShpPoint[]> getSegmentList() {
//			return segmentList;
//		}
		//该索引包含的所有道路段
		private List<ShpPoint[]> segmentList = new ArrayList<ShpPoint[]>();
		public Index(int index_X,int index_Y) {
			this.index_X = index_X;
			this.index_Y = index_Y;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + index_X;
			result = prime * result + index_Y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Index other = (Index) obj;
			if (index_X != other.index_X)
				return false;
			if (index_Y != other.index_Y)
				return false;
			return true;
		}
		/**
		 * 判断index范围内，rect矩形是否与范围内的道路坐标点相交
		 * @param rect
		 * @return
		 */
		public boolean isCrossLineToRect(GeoRect rect){
			int minX = rect.left;
			int minY = rect.bottom;
			int maxX = rect.right;
			int maxY = rect.top;
			for (ShpPoint[] shpPoints : this.segmentList) {// 与道路进行比较压盖关系
				ShpPoint firstPoint = shpPoints[0];
				ShpPoint secondPoint = shpPoints[1];
				boolean cross = GeoRect.isLineCrossRect(firstPoint.x,
						firstPoint.y, secondPoint.x, secondPoint.y, minX, minY,
						maxX, maxY);
				if (cross) {
					// iRect为被压盖类型
					return true;
				}
			}
			return false;
		}
		
		//添加地标到索引中
		public void add(MarkPoint landMarkType){
			landMarkList.add(landMarkType);
		}
		//添加道路段到索引中
		public void add(ShpPoint[] segment){
			segmentList.add(segment);
		}
		
		public int compareByX(Index index){
			return this.index_X - index.index_X;
		}
		
		public int compareByY(Index index){
			return this.index_Y - index.index_Y;
		}
	}
}

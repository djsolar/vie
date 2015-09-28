package com.mansion.tele.business.network;

import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.Task;
import com.mansion.tele.business.DataManager.LevelInfo;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.PolygonUtil;

public class NetworkIndex {
	private int down;
	private int up;
	private int left;
	private int right;
	private double unit_X;// x轴坐标最小单位的经度
	private double unit_Y;// x轴坐标最小单位的经度
	private int numx;
	private int numy;
	private List<RoadIndex> indexList = new ArrayList<RoadIndex>();
	
	private List<ShpPoint[]> comLinkPoint;
	
	//任务块初始化
	public NetworkIndex(Task task) {
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
				RoadIndex index = new RoadIndex(j, i);
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
	public List<RoadIndex> getIndexList() {
		return indexList;
	}
	//空间索引类
	public class RoadIndex{
		//索引 x
		private int index_X;
		//索引 y
		private int index_Y;
		
		public int compareIndex(RoadIndex index){
			if(this.index_Y == index.index_Y){
				return this.index_X - index.index_X;
			}else{
				return this.index_Y - index.index_Y;
			}
		}
		
		public int[] getIndexXY(){
			return new int[]{index_X,index_Y};
		}
		//该索引包含的所有道路段
		private List<ShpPoint[]> segmentList = new ArrayList<ShpPoint[]>();
		public RoadIndex(int index_X,int index_Y) {
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
			RoadIndex other = (RoadIndex) obj;
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
		
		//添加道路段到索引中
		public void add(ShpPoint[] segment){
			segmentList.add(segment);
		}
		
		public int compareByX(RoadIndex index){
			return this.index_X - index.index_X;
		}
		
		public int compareByY(RoadIndex index){
			return this.index_Y - index.index_Y;
		}
	}
	/**
	 * 
	 * @param unit_X
	 * @param unit_Y
	 * @param numx
	 * @param numy
	 * @param roads
	 */
	public void indexRoadSegment(List<RoadNew> roads){
		for (RoadNew road : roads) {
			if ((road.nrc == RoadNew.defineNRC.CityFreeWay
				|| road.nrc == RoadNew.defineNRC.CityMajorRoad
				|| road.nrc == RoadNew.defineNRC.CityMinorRoad)
				&& (road.fow == RoadNew.defineFOW.JunctionLink
				|| road.fow == RoadNew.defineFOW.TwoWay
				|| road.fow == RoadNew.defineFOW.OneWay)) {
				List<ShpPoint> shpPointList = road.coordinate;
				for (int i = 0; i < shpPointList.size() - 1;) {
					ShpPoint pointA = shpPointList.get(i);
					ShpPoint pointB = shpPointList.get(++i);
					addSegmentToIndex(pointA, pointB, unit_X, unit_Y, numx, numy);
				}
			}
		}
	}
	//添加道路段
	private void addSegmentToIndex(ShpPoint pointA,ShpPoint pointB, double unit_X, double unit_Y, int numx, int numy){
		ShpPoint segment[] = {pointA,pointB};
		int index_X_A = (int) ((pointA.x - left) / unit_X);
		int index_Y_A = (int) ((pointA.y - down) / unit_Y);
		
		int index_X_B = (int) ((pointB.x - left) / unit_X);
		int index_Y_B = (int) ((pointB.y - down) / unit_Y);
//		addSegmentToIndex(new Index(index_X_A,index_Y_A),segment);
//		addSegmentToIndex(new Index(index_X_B,index_Y_B),segment);
		int from_X = index_X_A>index_X_B?index_X_B:index_X_A;
		int from_Y = index_Y_A>index_Y_B?index_Y_B:index_Y_A;
		int to_X = index_X_A<index_X_B?index_X_B:index_X_A;
		int to_Y = index_Y_A<index_Y_B?index_Y_B:index_Y_A;
		
		for (int i = from_X; i <= to_X; i++) {
			for (int j = from_Y; j <= to_Y; j++) {
				int minX = (int) (left + i * unit_X);
				int maxX = (int) (left + (i + 1) * unit_X);
				int minY = (int) (down +  j * unit_Y );
				int maxY = (int) (down + (j + 1) * unit_Y);
				if (GeoRect.isLineCrossRect(pointA.x, pointA.y, pointB.x,
						pointB.y, minX, minY, maxX, maxY))
				{
					addSegmentToIndex(i,j,segment, numx, numy);
				}
			}
		}
	}
	//添加道路段到目标空间索引中
	private void addSegmentToIndex(int indexx,int indexy,ShpPoint[] segment, int numx, int numy){
		int indexNo = indexy * (numx + 1) + indexx;
		RoadIndex index = this.indexList.get(indexNo);
		index.add(segment);
	}
	
	public void initLinkComRect(int index_x, int index_y){
		this.comLinkPoint = new ArrayList<ShpPoint[]>();
		for (int i = index_y - 1; i <= index_y + 1; i++) {
			for (int j = index_x - 1; j <= index_x + 1; j++) {
				int indexNo = i * (numx + 1) + j;
				if(indexNo < 0 || indexNo > this.indexList.size() - 1){
					continue;
				}
				RoadIndex index = this.indexList.get(indexNo);
				this.comLinkPoint.addAll(index.segmentList);
			}
		}
	}
	
	public boolean isCrossRectGlandLink(GeoRect rect){
		int minX = rect.left;
		int minY = rect.bottom;
		int maxX = rect.right;
		int maxY = rect.top;
		for (ShpPoint[] shpPoints : this.comLinkPoint) {// 与道路进行比较压盖关系
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
	
	
}

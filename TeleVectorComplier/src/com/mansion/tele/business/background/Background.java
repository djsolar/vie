package com.mansion.tele.business.background;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.Session;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.DataManager.LevelInfo;
import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.network.SmoothStyle;
import com.mansion.tele.common.ConstantValue;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.db.bean.elemnet.AdmArea_M;
import com.mansion.tele.db.bean.elemnet.AdmArea_O;
import com.mansion.tele.db.bean.elemnet.CRailwayLine;
import com.mansion.tele.db.bean.elemnet.LandUse;
import com.mansion.tele.db.bean.elemnet.LandUse_M;
import com.mansion.tele.db.bean.elemnet.LandUse_O;
import com.mansion.tele.db.bean.elemnet.RailWay_M;
import com.mansion.tele.db.bean.elemnet.RailWay_O;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.db.bean.elemnet.Water;
import com.mansion.tele.db.bean.elemnet.Water_M;
import com.mansion.tele.db.bean.elemnet.Water_O;
import com.mansion.tele.db.daoImpl.TeleDao;
import com.mansion.tele.db.factory.TeleHbSessionFactory;
import com.mansion.tele.util.GeoUtil;
import com.mansion.tele.util.PolygonUtil;

/**
 * 保存背景，提供背景相关的操作
 * 
 */
public class Background {
	/**
	 * 
	 */
	// private static final long serialVersionUID = 4715464328027095259L;
	TaskData taskData;
	public List<Edge> edges = new ArrayList<Edge>();
	public List<Polyline> polylines = new ArrayList<Polyline>();
	public List<Polygon> polygongs = new ArrayList<Polygon>();

	public Background(TaskData taskData) {
		this.taskData = taskData;
	}

	private static int right = 0;// 分割正确
	private static int wrong = 0;// 分割错误
	private static int ok = 0;// 简引分割有误原始坐标进行分割OK

	public void printLog() {
		System.out
				.println("wrong" + wrong + " : right " + right + " ok: " + ok);
	}

	// 面的凹变凸
	public void converToConvex() {
		// 存储变换后的凹面图形
		List<Polygon> newPolygongs = new ArrayList<Polygon>();
		for (Polygon polygon : polygongs) {
			Polygon simplePolygon = this.smoothPolygon(polygon);
			List<Point> points = PolygonHandle.getPolygonPoints(simplePolygon);
			if (points.size() < 3) {// 错误图形----
				continue;
			}
			if (points.size() == 3) {// 不用分割
				newPolygongs.add(simplePolygon);
				right++;
				continue;
			}

			List<List<Integer>> dividList = null;
			try {
				PointList pts = new PointList(points);// 赋值
				if (!pts.ccw()) {// 逆时针方向调整
					Collections.reverse(points);
					Collections.reverse(polygon.edges.get(0).coordinate);
					pts = new PointList(points);// 赋值
				}
				// 原图形本身为凸多边形
				boolean convex = PolygonHandle.isConvex(points);
				if (convex) {// 不需要分割直接保存
					newPolygongs.add(polygon);
					right++;
					continue;
				}
				// 调用分割函数进行凹凸分割

				dividList = PolygonHandle.converToConvex(pts, null);
			} catch (Exception e) {
				// System.out.println("重新分割....");
				points = PolygonHandle.getPolygonPoints(polygon);
				if (points.size() < 3) {// 错误图形----
					continue;
				}
				if (points.size() == 3) {// 不用分割
					newPolygongs.add(polygon);
					continue;
				}
				try {
					PointList pts = new PointList(points);// 赋值
					if (!pts.ccw()) {// 逆时针方向调整
						Collections.reverse(points);
						Collections.reverse(polygon.edges.get(0).coordinate);
						pts = new PointList(points);// 赋值
					}
					// 原图形本身为凸多边形
					boolean convex = PolygonHandle.isConvex(points);
					if (convex) {// 不需要分割直接保存
						newPolygongs.add(polygon);
						continue;
					}
					// 调用分割函数进行凹凸分割
					dividList = PolygonHandle.converToConvex(pts, null);
					ok++;
				} catch (Exception e1) {
					// 分割失败 将原图形存储
					System.out.println("错误图形原始经纬度坐标  id:" + polygon.id
							+ " type: " + polygon.type);
					// for(int i = 0;i < points.size();i++){
					// System.out.println((int)points.get(0).x()
					// +"\t"+(int)points.get(0).y());
					// }
					// newPolygongs.add(polygon);
					wrong++;
					continue;
				}
			}
			right++;
			// 整理存储分割后的新凹面图形
			for (List<Integer> list : dividList) {
				Polygon polygonConvex = new Polygon();
				Edge edge = new Edge();
				for (Integer i : list) {
					Point p = points.get(i.intValue());
					if (edge.coordinate.isEmpty()
							|| !edge.coordinate.get(edge.coordinate.size() - 1)
									.equals(p)) {
						edge.coordinate.add(new ShpPoint(p.x(), p.y()));
					}
				}
				if (edge.coordinate.size() >= 3) {
					polygonConvex.edges.add(edge);
//					newPolygongs.add(polygonConvex);
					polygonConvex.type = polygon.type;
					newPolygongs.addAll(this.getCheckedPolygongs(polygonConvex,0));
//					newPolygongs.add(this.getConvexPolygon(polygonConvex));
				}
			}
		}
		polygongs = newPolygongs;
		printLog();
	}
	
	
	public List<Polygon> getCheckedPolygongs(Polygon polygon, int count) {
		count++;
//		if (count >= 2) {
//			System.out.println("多次分割");
//		}
		List<Polygon> newPolygongs = new ArrayList<Polygon>();
		List<Point> points = PolygonHandle.getPolygonPoints(polygon);
		if (points.size() < 3 || count >= 10) {// 错误图形或已达到最大分割次数
			return newPolygongs;
		}
		if (points.size() == 3) {// 不用分割
			newPolygongs.add(polygon);
			return newPolygongs;
		}
		List<List<Integer>> dividList = null;
		try {
			PointList pts = new PointList(points);// 赋值
			if (!pts.ccw()) {// 逆时针方向调整
				Collections.reverse(points);
				Collections.reverse(polygon.edges.get(0).coordinate);
				pts = new PointList(points);// 赋值
			}
			// 原图形本身为凸多边形
			boolean convex = PolygonHandle.isConvex(points);
			if (convex) {// 不需要分割直接保存
				newPolygongs.add(polygon);
				return newPolygongs;
			}
			// 调用分割函数进行凹凸分割
			dividList = PolygonHandle.converToConvex(pts, null);
		} catch (Exception e) {
			System.err.println("多次分割时有错误图形 "
					+ polygon.edges.get(0).coordinate.get(0).toString());
			return newPolygongs;
		}
		for (List<Integer> list : dividList) {
			Polygon polygonConvex = new Polygon();
			Edge edge = new Edge();
			for (Integer i : list) {
				Point p = points.get(i.intValue());
				if (edge.coordinate.isEmpty()
						|| !edge.coordinate.get(edge.coordinate.size() - 1)
								.equals(p)) {
					edge.coordinate.add(new ShpPoint(p.x(), p.y()));
				}
			}
			if (edge.coordinate.size() >= 3) {
				polygonConvex.edges.add(edge);
				polygonConvex.type = polygon.type;
				newPolygongs.addAll(this.getCheckedPolygongs(polygonConvex,
						count));
			}
		}

		return newPolygongs;
	}

	/**
	 * 合并背景线
	 */
	public void mergerBinaryLine() {
		Collections.sort(this.polylines, new Comparator<Polyline>() {

			@Override
			public int compare(Polyline o1, Polyline o2) {
				// 类型
				return o1.type.compareTo(o2.type);
			}
		});
		List<Polyline> result = new ArrayList<Polyline>();
		MergeLine merge = new MergeLine();
		List<Polyline> polylineList = new ArrayList<Polyline>();
		for (Polyline polyline : this.polylines) {
			if (polylineList.isEmpty()
					|| polylineList.get(polylineList.size() - 1).type
							.equals(polyline.type)) {
				polylineList.add(polyline);
			} else {
				result.addAll(merge.combire(polylineList));
				polylineList = new ArrayList<Polyline>();
				polylineList.add(polyline);
			}
		}
		if (!polylineList.isEmpty()) {
			result.addAll(merge.combire(polylineList));
		}
		this.polylines = result;
	}

	public void fromDB() {
		List<String> meshNos = DataManager.getMeshNos(this.taskData.task);
		// TODO添加背景过滤条件为了测试
		// if(!meshNos.isEmpty() && this.taskData.task.getbLevel() == 6){
		if (!meshNos.isEmpty()) {
			Session session = TeleHbSessionFactory.getOrgHbSession(
					this.taskData.task.getLevel()).getSession();
			loadAdmArea(session, meshNos);
			loadWaterArea(session, meshNos);
			loadLanduse(session, meshNos);
			loadRailway(session, meshNos);
			session.close();
			loadSubway();
		}
	}

	void loadAdmArea(Session session, List<String> meshNos) {
		// 六层7层有数据
		if (this.taskData.task.getLevel() < 6) {
			return;
		}
		List<AdmArea_O> elements = TeleDao.getEleDataFromOrg(AdmArea_O.class,
				meshNos, session, "strid");
		for (int i = 0; i < elements.size(); i++) {
			AdmArea_O element = elements.get(i);
			// 过滤出省级行政区划面
			// TODO 应该在查询语句中加入该过滤条件
			Integer iMinLevel = LayerCollection.AdmArea.minlevelMap.get(element
					.getByadmareatype());
			Integer iMaxLevel = LayerCollection.AdmArea.maxlevelMap.get(element
					.getByadmareatype());
			if (iMinLevel != null && iMaxLevel != null) {
				if (this.taskData.task.getLevel() >= iMinLevel
						&& this.taskData.task.getLevel() <= iMaxLevel) {
					AdmArea_M adminArea = new AdmArea_M();
					adminArea.convert(element);
					if (element.getByadmareatype() == LayerCollection.AdmArea.ProvinceAdmArea.iid
							&& adminArea.getStGeom() != null) {
						Edge edge = new Edge();
						edge.coordinate.addAll(adminArea.getStGeom()
								.getCoordinate());
						this.edges.add(edge);
						Polygon polygon = new Polygon();
						polygon.type = Type.ProvinceAdmArea;
						polygon.edges.add(edge);
						this.polygongs.add(polygon);
						polygon.id = element.getStrid();
					}
				}
			}
		}

	}

	void loadWaterArea(Session session, List<String> meshNos) {
		List<Water_O> elements = TeleDao.getEleDataFromOrg(Water_O.class,
				meshNos, session, "strid");
		for (int i = 0; i < elements.size(); i++) {
			Water_O water = elements.get(i);
			// TODO 数据库已经分层，是否还需要下面的判断条件？
			Integer iMinLevel = LayerCollection.Water.minlevelMap.get(water
					.getIwatertype());
			Integer iMaxLevel = LayerCollection.Water.maxlevelMap.get(water
					.getIwatertype());
			if (iMinLevel != null && iMaxLevel != null) {
				if (this.taskData.task.getLevel() >= iMinLevel
						&& this.taskData.task.getLevel() <= iMaxLevel) {
					if (water.getIwatertype() == ConstantValue.BYTE_INVALID
							|| this.filter(water,
									this.taskData.task.getLevel())) {
						continue;
					}
					Water_M element = new Water_M();
					element.convert(water);
					Edge edge = new Edge();
					ShpPoint lb = element.getStGeom().getLbCoordinate();
					ShpPoint rt = element.getStGeom().getRtCoordinate();
					
//					if(((lb.x > 1065435737 && lb.y > 206767274) && (rt.x < 1071438413 && rt.y < 208937965))
//							){
////							|| ((lb.x < 1073247612 && lb.y < 209093705) && (rt.x > 1071438413 && rt.y > 208937965))){
//						System.out.println("tteetteett");
//					}
					
					edge.coordinate.addAll(element.getStGeom().getCoordinate());
					this.edges.add(edge);
					Polygon polygon = new Polygon();
					if (LayerCollection.Water.WaterSeaArea.iid == element
							.getIwatertype()) {
						polygon.type = Type.SeaArea;
					} else {
						polygon.type = Type.Water;
					}
					polygon.edges.add(edge);
					this.polygongs.add(polygon);
					polygon.id = water.getStrid();
				}
			}
		}
	}

	/**
	 * 根据不同层，筛选小面积的水体
	 * 
	 * @param water
	 *            水体
	 * @param bLevel
	 *            层号
	 * @return true: 过滤掉 false:不过滤掉
	 */
	private boolean filter(Water water, byte bLevel) {
		boolean flag = false;
		if (water.getIwatertype() == LayerCollection.Water.Lake.iid) {
			if (water.getlArea() < AreaFilter.WATER[LayerCollection.Water.Lake
					.ordinal()][bLevel]) {
				flag = true;
			}
		}
		if (water.getIwatertype() == LayerCollection.Water.Pond.iid) {
			if (water.getlArea() < AreaFilter.WATER[LayerCollection.Water.Pond
					.ordinal()][bLevel]) {
				flag = true;
			}
		}
		return flag;
	}

	void loadLanduse(Session session, List<String> meshNos) {
		List<LandUse_O> elements = TeleDao.getEleDataFromOrg(LandUse_O.class,
				meshNos, session, "strid");
		for (int i = 0; i < elements.size(); i++) {
			LandUse_O landuse = elements.get(i);
			Integer iMinLevel = LayerCollection.LandUse.minlevelMap.get(landuse
					.getIlandusetype());
			Integer iMaxLevel = LayerCollection.LandUse.maxlevelMap.get(landuse
					.getIlandusetype());
			if (iMinLevel != null && iMaxLevel != null) {
				if (this.taskData.task.getLevel() >= iMinLevel
						&& this.taskData.task.getLevel() <= iMaxLevel) {

					LandUse_M element = new LandUse_M();
					element.convert(landuse);
					if (this.filter(element, this.taskData.task.getLevel())) {
						Edge edge = new Edge();
						edge.coordinate.addAll(element.getStGeom()
								.getCoordinate());
						this.edges.add(edge);
						Polygon polygon = new Polygon();
						if (landuse.getIlandusetype() == LayerCollection.LandUse.Park.iid
								|| landuse.getIlandusetype() == LayerCollection.LandUse.GreenStreet.iid
								|| landuse.getIlandusetype() == LayerCollection.LandUse.Forest.iid) {
							polygon.type = Type.Land;// 绿地
						} else if (landuse.getIlandusetype() == LayerCollection.LandUse.Lawn.iid
								|| landuse.getIlandusetype() == LayerCollection.LandUse.Island.iid) {
							if (this.taskData.task.getLevel() < 6) {
								continue;// 6层以下有水域勾勒陆地和岛屿
							}
							polygon.type = Type.Island;// 岛屿
						}

						polygon.edges.add(edge);
						this.polygongs.add(polygon);
						polygon.id = landuse.getStrid();

					}
				}
			}

		}
	}

	void loadRailway(Session session, List<String> meshNos) {
		List<RailWay_O> elements = TeleDao.getEleDataFromOrg(RailWay_O.class,
				meshNos, session, "strid");
		for (int i = 0; i < elements.size(); i++) {
			RailWay_O railway = elements.get(i);
			 if(railway.getIrailwaytype() ==
			 LayerCollection.RailWay.Subway.iid) {
			 //地铁数据从公交中读取
			 continue;
			 }
			Integer iMinLevel = LayerCollection.RailWay.minlevelMap.get(railway
					.getIrailwaytype());
			Integer iMaxLevel = LayerCollection.RailWay.maxlevelMap.get(railway
					.getIrailwaytype());
			if (iMinLevel != null && iMaxLevel != null) {
				if (this.taskData.task.getLevel() >= iMinLevel
						&& this.taskData.task.getLevel() <= iMaxLevel) {
					RailWay_M element = new RailWay_M();
					element.convert(railway);
					// TODO TaskStep.T3应该是没用参数。
					Edge edge = new Edge();
					edge.coordinate.addAll(element.getStGeom().getCoordinate());
					this.edges.add(edge);
					Polyline polyline = new Polyline();
					// 地铁
					if (railway.getIrailwaytype() == LayerCollection.RailWay.Subway.iid) {
						polyline.type = this.getLineType(railway
								.getAstRailWayNames().get(0).getStrnametext());
						// polyline.type = Type.Subway;
					} else if (element.getIrailwaytype() == LayerCollection.RailWay.Rail.iid
							|| element.getIrailwaytype() == LayerCollection.RailWay.LightRail.iid
							|| element.getIrailwaytype() == LayerCollection.RailWay.Suspension.iid
							|| element.getIrailwaytype() == LayerCollection.RailWay.LightRailTwo.iid
							|| element.getIrailwaytype() == LayerCollection.RailWay.LightRailThree.iid
							|| element.getIrailwaytype() == LayerCollection.RailWay.SuspensionOne.iid) {
						polyline.type = Type.RailWay;
					} else if (element.getIrailwaytype() == LayerCollection.RailWay.LightRailOne.iid
							|| element.getIrailwaytype() == LayerCollection.RailWay.LightRail.iid) {
						polyline.type = Type.LightRailOne;
					} else if (element.getIrailwaytype() == LayerCollection.RailWay.LightRailTwo.iid) {
						polyline.type = Type.LightRailTwo;
					} else if (element.getIrailwaytype() == LayerCollection.RailWay.LightRailThree.iid) {
						polyline.type = Type.LightRailThree;
					} else if (element.getIrailwaytype() == LayerCollection.RailWay.River.iid
							|| element.getIrailwaytype() == LayerCollection.RailWay.Canal.iid) {
						polyline.type = Type.WaterLine;
					} else if (element.getIrailwaytype() == LayerCollection.RailWay.ProviceBorder.iid) {
						polyline.type = Type.ProviceBorder;// 省线
					} else if (element.getIrailwaytype() == LayerCollection.RailWay.NationBorder.iid) {
						polyline.type = Type.AdmaBorderNation;// 国线
					} else if (element.getIrailwaytype() == LayerCollection.RailWay.SpecialBorder.iid) {
						polyline.type = Type.SpecialBorder;// 特殊边界
					} else if (element.getIrailwaytype() == LayerCollection.RailWay.UnNationBorder.iid) {
						polyline.type = Type.UnNationBorder;// 行政界（未定国界）
					} else if (element.getIrailwaytype() == LayerCollection.RailWay.UnProviceBorder.iid) {
						polyline.type = Type.UnProviceBorder;// 行政界（未定国界）
					} else {
						System.err.println("遗失背景线类型"
								+ element.getIrailwaytype());
						continue;
					}
					polyline.edges.add(edge);
					this.polylines.add(polyline);
					polyline.id = railway.getStrid();
				}
			}
		}
	}
	
	/**
	 * 加载地铁数据
	 */
	@SuppressWarnings("unchecked")
	void loadSubway(){
		List<CRailwayLine> subways = (List<CRailwayLine>) TeleDao.getAllPlusDataObject(CRailwayLine.class,
				TeleHbSessionFactory.getOrgOtherSession().getSession());
		subways = this.delSubway(subways);
		for(int i=0;i<subways.size();i++){
			CRailwayLine subway = subways.get(i);
			Integer iMinLevel = LayerCollection.RailWay.minlevelMap.get(LayerCollection.RailWay.Subway.iid);
			Integer iMaxLevel = LayerCollection.RailWay.maxlevelMap.get(LayerCollection.RailWay.Subway.iid);
			if (iMinLevel != null && iMaxLevel != null) {
				if (this.taskData.task.getLevel() >= iMinLevel
						&& this.taskData.task.getLevel() <= iMaxLevel) {
					this.divSubwayByUnit(subway);
				}
			}
		}
	}
	
	/**
	 * 以Unit为单位分割地铁线路
	 * @param subway
	 */
	void divSubwayByUnit(CRailwayLine subway){
		List<ShpPoint> points = subway.getGeomList();
		Edge edge = new Edge();
		Polyline polyline = new Polyline();
		ShpPoint last = null;
		ShpPoint current = null;
		int lastStation = 0;
		int station = 0;
		// TODO
		for(int i=0;i<points.size();i++){
			current = points.get(i);
			station = GeoRect.pointTaskRelation(current, taskData.task);
			if(station == 0){ // 在任务块内
				if(i == 0){ // 首点
					edge.coordinate.add(current);
				}
				else if(lastStation == 0){ // 前一个也在任务块内
					edge.coordinate.add(current);
				}
				else if(lastStation == 1){ // 前一个点在任务块左边，从左边穿入任务块
					int insertY = GeoRect.getIntersectionY(last, current, taskData.task.getLeft());
					ShpPoint insert = new ShpPoint(taskData.task.getLeft(),insertY);
					edge.coordinate.add(insert);
					edge.coordinate.add(current);
				}
				else if(lastStation == 2){ // 前一个点在任务块右边，从右边穿入任务块
					int insertY = GeoRect.getIntersectionY(last, current, taskData.task.getRight());
					ShpPoint insert = new ShpPoint(taskData.task.getRight(),insertY);
					edge.coordinate.add(insert);
					edge.coordinate.add(current);
				}
				else if(lastStation == 3){ // 前一个点在任务块下边，从下边穿入任务块
					int insertX = GeoRect.getIntersectionX(last, current, taskData.task.getBottom());
					ShpPoint insert = new ShpPoint(insertX,taskData.task.getBottom());
					edge.coordinate.add(insert);
					edge.coordinate.add(current);
				}
				else if(lastStation == 4){ // 前一个点在任务块上边，从上边穿入任务块
					int insertX = GeoRect.getIntersectionX(last, current, taskData.task.getTop());
					ShpPoint insert = new ShpPoint(insertX,taskData.task.getTop());
					edge.coordinate.add(insert);
					edge.coordinate.add(current);
				}
			}
			else if(station == 1 && lastStation == 0 && last != null){ 
				int insertY = GeoRect.getIntersectionY(last, current, taskData.task.getLeft());
				ShpPoint insert = new ShpPoint(taskData.task.getLeft(),insertY);
				edge.coordinate.add(insert);
				polyline.edges.add(edge);
				polyline.type = this.getLineType(subway.getNamec());
				this.polylines.add(polyline);
				edge = new Edge();
				polyline = new Polyline();
			}
			else if(station == 2 && lastStation == 0 && last != null){
				int insertY = GeoRect.getIntersectionY(last, current, taskData.task.getRight());
				ShpPoint insert = new ShpPoint(taskData.task.getRight(),insertY);
				edge.coordinate.add(insert);
				polyline.edges.add(edge);
				polyline.type = this.getLineType(subway.getNamec());
				this.polylines.add(polyline);
				edge = new Edge();
				polyline = new Polyline();
			}
			else if(station == 3 && lastStation == 0 && last != null){
				int insertX = GeoRect.getIntersectionX(last, current, taskData.task.getBottom());
				ShpPoint insert = new ShpPoint(insertX,taskData.task.getBottom());
				edge.coordinate.add(insert);
				polyline.edges.add(edge);
				polyline.type = this.getLineType(subway.getNamec());
				this.polylines.add(polyline);
				edge = new Edge();
				polyline = new Polyline();
			}
			else if(station == 4 && lastStation == 0 && last != null){
				int insertX = GeoRect.getIntersectionX(last, current, taskData.task.getTop());
				ShpPoint insert = new ShpPoint(insertX,taskData.task.getTop());
				edge.coordinate.add(insert);
				polyline.edges.add(edge);
				polyline.type = this.getLineType(subway.getNamec());
				this.polylines.add(polyline);
				edge = new Edge();
				polyline = new Polyline();
			}
			
			last = current;
			lastStation = station;
		}
		if(!edge.coordinate.isEmpty()){
			polyline.edges.add(edge);
			polyline.type = this.getLineType(subway.getNamec());
			polyline.name = subway.getNamec();
			this.polylines.add(polyline);
		}
	}
	
	/**
	 * 往返的相同线路保留一个
	 * @param subways
	 * @return
	 */
	private List<CRailwayLine> delSubway(List<CRailwayLine> subways){
		List<CRailwayLine> newSubways = new ArrayList<CRailwayLine>();
		boolean same = false;
		newSubways.add(subways.get(0));
		for(int i=1;i<subways.size();i++){
			CRailwayLine subway = subways.get(i);
			for(int j=0;j<newSubways.size();j++){
				CRailwayLine newSubway = newSubways.get(j);
				if(subway.equals(newSubway)){
					same = true;
				}
			}
			if(!same){
				newSubways.add(subway);
			}
			same = false;
		}
		return newSubways;
	}

	public Type getLineType(String name) {
		// System.out.println(name);
		if (name.contains("地铁1号线") || name.contains("地铁一号线")) {
			return Type.SubwayOne;
		}
		if (name.contains("地铁2号线") || name.contains("地铁二号线")) {
			return Type.SubwayTwo;
		}
		if (name.contains("地铁3号线") || name.contains("地铁三号线")) {
			return Type.SubwayThree;
		}
		if (name.contains("地铁4号线") || name.contains("地铁四号线")) {
			return Type.SubwayFour;
		}
		if (name.contains("地铁5号线") || name.contains("地铁五号线")) {
			return Type.SubwayFive;
		}
		if (name.contains("地铁6号线") || name.contains("地铁六号线")) {
			return Type.SubwaySix;
		}
		if (name.contains("地铁7号线") || name.contains("地铁七号线")) {
			return Type.SubwaySeven;
		}
		if (name.contains("地铁8号线") || name.contains("地铁八号线")) {
			return Type.SubwayEight;
		}
		if (name.contains("地铁9号线") || name.contains("地铁九号线")) {
			return Type.SubwayNine;
		}
		if (name.contains("地铁10号线") || name.contains("地铁十号线")) {
			return Type.SubwayTen;
		}
		if (name.contains("地铁11号线")) {
			return Type.SubwayEleven;
		}
		if (name.contains("地铁12号线")) {
			return Type.SubwayTwelve;
		}
		if (name.contains("地铁13号线")) {
			return Type.SubwayThirteen;
		}
		if (name.contains("地铁14号线")) {
			return Type.SubwayFourteen;
		}
		if (name.contains("地铁15号线")) {
			return Type.SubwayFourteen;// 没有14号地铁
		}
		return Type.SubwayTwelve;// 没有12号地铁
	}

	/**
	 * 根据不同层，筛选小面积的土地
	 * 
	 * @param landUse
	 *            土地
	 * @param bLevel
	 *            层号
	 * @return true: 过滤掉 false:不过滤掉
	 */
	// TODO 1,为什么不筛选水系呢？ 2,筛选的面积应该按照比例尺折算成像素
	private boolean filter(LandUse landUse, byte bLevel) {
		boolean flag = true;
		if (landUse.getIlandusetype() == LayerCollection.LandUse.Park.iid) {
			if (bLevel == 3
					&& landUse.getlArea() < AreaFilter.LANDUSE_PARK_3) {
				flag = false;
			}
		}
		if (landUse.getIlandusetype() == LayerCollection.LandUse.GreenStreet.iid) {
			if (bLevel == 3
					&& landUse.getlArea() < AreaFilter.LANDUSE_PARK1_3) {
				flag = false;
			}
		}
		if (landUse.getIlandusetype() == LayerCollection.LandUse.Island.iid) {
			flag = this.islandFilter(landUse, bLevel);
		}

		return flag;
	}

	/**
	 * 岛屿的面积过滤 特殊位置的岛屿不参加过滤
	 * 
	 * @param landUse
	 *            岛屿
	 * @param bLevel
	 *            层号
	 * @return true 过滤掉 false 不过滤
	 */
	private boolean islandFilter(LandUse landUse, byte bLevel) {
		boolean flag = true;
		if (GeoUtil.isInRect(landUse.getStGeom())) {
			flag = true;
		} else {
			if (bLevel == 3
					&& landUse.getlArea() < AreaFilter.LANDUSE_ISLAND_3) {
				flag = false;
			} else if (bLevel == 4
					&& landUse.getlArea() < AreaFilter.LANDUSE_ISLAND_4) {
				flag = false;
			} else if (bLevel == 5
					&& landUse.getlArea() < AreaFilter.LANDUSE_ISLAND_5) {
				flag = false;
			} else if (bLevel == 6
					&& landUse.getlArea() < AreaFilter.LANDUSE_ISLAND_6) {
				flag = false;
			}
		}
		return flag;
	}

	// 线图形的Blcok分割
	public void splitPolylineByBlock() {
		splitPolylineByBlockX();
		splitPolylineByBlockY();
		splitLinePoint();
	}

	void splitPolylineByBlockX() {
		List<Polyline> splitLinelist = new ArrayList<Polyline>();
		for (Polyline ployline : this.polylines) {
			List<ShpPoint> points = new ArrayList<ShpPoint>();
			for (Edge edge : ployline.edges) {
				points.addAll(edge.coordinate);
			}
			ShpPoint startPoint = points.get(0);
			int startBlockNo_x = taskData.calcBlockNo(startPoint).iX;
			ShpPoint headPoint = null;
			ShpPoint tailPoint = null;
			int splitIndexFrom = 0;
			int splitIndexTo = 0;
			for (int index = 1; index < points.size();) {
				ShpPoint currentPoint = points.get(index);
				int currentBlockNo_x = taskData.calcBlockNo(currentPoint).iX;
				if (startBlockNo_x != currentBlockNo_x) {
					boolean ascX = currentBlockNo_x > startBlockNo_x;
					// 分割线x值
					int blockPoint_x = (startBlockNo_x + (ascX ? 1 : 0))
							* taskData.blockInfo.iBlockWidth
							+ DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN;
					// 根据相对位置计算Y值
					long blockPoint_y = startPoint.y;// long 类型为了避免溢出发生
					if (currentPoint.y != startPoint.y) {
						blockPoint_y = (long) (blockPoint_x - startPoint.x)
								* (currentPoint.y - startPoint.y)
								/ (currentPoint.x - startPoint.x)
								+ startPoint.y;
					}
					// 切割点
					if (ascX) {
						tailPoint = new ShpPoint(blockPoint_x - 1,
								(int) blockPoint_y);
					} else {
						tailPoint = new ShpPoint(blockPoint_x,
								(int) blockPoint_y);
					}
					// 切割位置
					splitIndexTo = index;
					// 计算BlockNo
					ployline.blockNo = taskData.calcBlockNo(tailPoint);

					Polyline splitLine = createSplitPolyLine(points,
							splitIndexFrom, splitIndexTo, headPoint, tailPoint);
					splitLinelist.add(splitLine);
					splitLine.copy(ployline);
					splitIndexFrom = splitIndexTo;

					if (ascX) {
						headPoint = new ShpPoint(blockPoint_x,
								(int) blockPoint_y);
					} else {
						headPoint = new ShpPoint(blockPoint_x - 1,
								(int) blockPoint_y);
					}
					startBlockNo_x = startBlockNo_x + (ascX ? 1 : -1);
				} else {
					startPoint = currentPoint;
					index++;
				}
			}
			// 原始道路（最后一段道路） 添加Block值
			ShpPoint endPoint = points.get(points.size() - 1);
			ployline.blockNo = taskData.calcBlockNo(endPoint);
			if (headPoint != null) {
				Edge edge = new Edge();
				edge.coordinate = new ArrayList<ShpPoint>(points.subList(
						splitIndexFrom, points.size()));
				edge.coordinate.add(0, ShpPoint.valueOf(headPoint));
				ployline.edges.clear();
				ployline.edges.add(edge);
			}
		}
		this.polylines.addAll(splitLinelist);
	}

	void splitPolylineByBlockY() {
		List<Polyline> splitLinelist = new ArrayList<Polyline>();
		for (Polyline ployline : this.polylines) {
			List<ShpPoint> points = new ArrayList<ShpPoint>();
			for (Edge edge : ployline.edges) {
				points.addAll(edge.coordinate);
			}
			ShpPoint startPoint = points.get(0);
			int startBlockNo_y = taskData.calcBlockNo(startPoint).iY;
			;
			ShpPoint headPoint = null;
			ShpPoint tailPoint = null;
			int splitIndexFrom = 0;
			int splitIndexTo = 0;
			for (int index = 1; index < points.size();) {
				ShpPoint currentPoint = points.get(index);
				int currentBlockNo_y = taskData.calcBlockNo(currentPoint).iY;
				if (startBlockNo_y != currentBlockNo_y) {
					boolean ascY = currentBlockNo_y > startBlockNo_y;
					// 分割线Y值
					int blockPoint_y = (startBlockNo_y + (ascY ? 1 : 0))
							* taskData.blockInfo.iBlockHight;
					// 根据相对位置计算X值
					long blockPoint_x = startPoint.x;// long 类型为了避免溢出发生
					if (currentPoint.x != startPoint.x) {
						blockPoint_x = (long) (currentPoint.x - startPoint.x)
								* (blockPoint_y - startPoint.y)
								/ (currentPoint.y - startPoint.y)
								+ startPoint.x;
					}
					// 切割位置
					if (ascY) {
						tailPoint = new ShpPoint((int) blockPoint_x,
								blockPoint_y - 1);
					} else {
						tailPoint = new ShpPoint((int) blockPoint_x,
								blockPoint_y);
					}
					// 切割位置
					splitIndexTo = index;
					// 计算BlockNo

					ployline.blockNo = taskData.calcBlockNo(tailPoint);

					Polyline splitLine = createSplitPolyLine(points,
							splitIndexFrom, splitIndexTo, headPoint, tailPoint);
					splitLinelist.add(splitLine);
					splitLine.copy(ployline);
					splitIndexFrom = splitIndexTo;
					if (ascY) {
						headPoint = new ShpPoint((int) blockPoint_x,
								blockPoint_y);
					} else {
						headPoint = new ShpPoint((int) blockPoint_x,
								blockPoint_y - 1);
					}
					startBlockNo_y = startBlockNo_y + (ascY ? 1 : -1);
				} else {
					startPoint = currentPoint;
					index++;
				}
			}
			// 原始道路（最后一段道路） 添加Block值
			ShpPoint endPoint = points.get(points.size() - 1);
			ployline.blockNo = taskData.calcBlockNo(endPoint);
			if (headPoint != null) {
				Edge edge = new Edge();
				edge.coordinate = new ArrayList<ShpPoint>(points.subList(
						splitIndexFrom, points.size()));
				edge.coordinate.add(0, ShpPoint.valueOf(headPoint));
				ployline.edges.clear();
				ployline.edges.add(edge);
			}
		}
		this.polylines.addAll(splitLinelist);
	}

	void splitLinePoint() {
		// 单个形状超过255个，进行中处理。
		for (int j = this.polylines.size() - 1; j >= 0; j--) {
			Polyline polyline = polylines.get(j);
			int pointCount = 0;
			for (Edge edge : polyline.edges) {
				pointCount += edge.coordinate.size();
				if (pointCount > 255) {
					// 进行处理 只考虑一个线类型只有一条边
					for (int i = 0; i < 100; i++) {
						if (edge.coordinate.size() > 255) {
							Polyline _line = new Polyline();
							Edge _edge = new Edge();
							_line.copy(polyline);
							_line.edges.add(_edge);
							_edge.coordinate.addAll(edge.coordinate.subList(0,
									254));
							_edge.coordinate.add(ShpPoint
									.valueOf(edge.coordinate.get(254)));
							edge.coordinate = new ArrayList<ShpPoint>(
									edge.coordinate.subList(254,
											edge.coordinate.size()));
							polylines.add(_line);
						} else {
							break;
						}
					}
				}
			}
		}
	}

	private Polyline createSplitPolyLine(List<ShpPoint> points, int indexFrom,
			int indexTo, ShpPoint headPoint, ShpPoint tailPoint) {
		Polyline splitLine = new Polyline();
		Edge edge = new Edge();
		edge.coordinate = new ArrayList<ShpPoint>(points.subList(indexFrom,
				indexTo));
		if (headPoint != null) {
			edge.coordinate.add(0, ShpPoint.valueOf(headPoint));
		}
		edge.coordinate.add(ShpPoint.valueOf(tailPoint));
		splitLine.edges.add(edge);
		return splitLine;
	}

	/**
	 * 面图形Block分割
	 */
	public void splitPolygonByBlock() {
		splitPolygonByBlockX();
		splitPolygonByBlockY();
	}

	void splitPolygonByBlockX() {
		List<Polygon> splitPolygonList = new ArrayList<Polygon>();
		for (int k = 0; k < polygongs.size(); k++) {
			Polygon polygon = polygongs.get(k);
			List<ShpPoint> points = new ArrayList<ShpPoint>();
			for (Edge edge : polygon.edges) {
				points.addAll(edge.coordinate);
			}

			// X方向 最大 最小
			int indexMin = 0, indexMax = 0;
			ShpPoint minPoint = points.get(indexMin);
			ShpPoint maxPoint = points.get(indexMax);
			polygon.blockNo = taskData.calcBlockNo(points.get(0));
			for (int i = 1; i < points.size(); i++) {
				ShpPoint point = points.get(i);
				if (minPoint.x > point.x) {
					minPoint = point;
					indexMin = i;
				}
				if (maxPoint.x < point.x) {
					maxPoint = point;
					indexMax = i;
				}
			}
			// 面的范围
			int startBlockNo_x = taskData.calcBlockNo(minPoint).iX;
			int endBlockBlockNo_x = taskData.calcBlockNo(maxPoint).iX;
			if (startBlockNo_x == endBlockBlockNo_x) {
				splitPolygonList.add(polygon);
				continue;
			}
			// 造两个List A B
			List<ShpPoint> listA = new ArrayList<ShpPoint>();
			List<ShpPoint> listB = new ArrayList<ShpPoint>();
			for (int index = indexMin;; index++) {
				index = index % points.size();
				listA.add(ShpPoint.valueOf(points.get(index)));
				if (index == indexMax) {
					break;
				}
			}
			for (int index = indexMin;; index--) {
				index = index % points.size();
				if (index < 0) {
					index += index + points.size() + 1;
				}
				listB.add(ShpPoint.valueOf(points.get(index)));
				if (index == indexMax) {
					break;
				}
			}
//			boolean check = true;
//			// test 验证 ListA ListB
//			for (int i = 0; i < listA.size() - 1; i++) {
//				if (listA.get(i + 1).x < listA.get(i).x) {
//					// System.err.println("a");
//					check = false;
//				}
//			}
//			for (int i = 0; i < listB.size() - 1; i++) {
//				if (listB.get(i + 1).x < listB.get(i).x) {
//					// System.err.println("b");
//					check = false;
//				}
//			}
//			if (!check) {
//				System.err.println("分割后依然存在凹面"+ "task:" + taskData.task.toString());
//				continue;
//			}
			// 分割
			for (int splitX = (startBlockNo_x + 1)
					* taskData.blockInfo.iBlockWidth
					+ DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN; splitX <= endBlockBlockNo_x
					* taskData.blockInfo.iBlockWidth
					+ DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN; splitX += taskData.blockInfo.iBlockWidth) {
				int indexA = 1;
				int indexB = 1;
				ShpPoint splitPointA = null;
				ShpPoint splitPointB = null;
				for (; indexA < listA.size(); indexA++) {
					if (splitX == listA.get(indexA).x) {
						splitPointA = listA.get(indexA);
						// indexA++;
						break;
					}
					if (splitX < listA.get(indexA).x) {
						long blockPoint_y = (long) (splitX - listA
								.get(indexA - 1).x)
								* (listA.get(indexA).y - listA.get(indexA - 1).y)
								/ (listA.get(indexA).x - listA.get(indexA - 1).x)
								+ listA.get(indexA - 1).y;
						splitPointA = new ShpPoint(splitX, (int) blockPoint_y);
						break;
					}
				}
				for (; indexB < listB.size(); indexB++) {
					if (splitX == listB.get(indexB).x) {
						splitPointB = listB.get(indexB);
						// indexB++;
						break;
					}
					if (splitX < listB.get(indexB).x) {
						long blockPoint_y = (long) (splitX - listB
								.get(indexB - 1).x)
								* (listB.get(indexB).y - listB.get(indexB - 1).y)
								/ (listB.get(indexB).x - listB.get(indexB - 1).x)
								+ listB.get(indexB - 1).y;
						splitPointB = new ShpPoint(splitX, (int) blockPoint_y);
						break;
					}
				}
				// 创建一个面
				Edge edgeBlock = new Edge();
				Polygon polygonBlock = new Polygon();
				polygonBlock.edges.add(edgeBlock);
				splitPolygonList.add(polygonBlock);
				for (int i = 0; i < indexA; i++) {
					edgeBlock.coordinate.add(listA.get(i));
				}
				edgeBlock.coordinate.add(new ShpPoint(splitPointA.x - 1,
						splitPointA.y));
				edgeBlock.coordinate.add(new ShpPoint(splitPointB.x - 1,
						splitPointB.y));
				for (int i = indexB - 1; i >= 0; i--) {
					edgeBlock.coordinate.add(listB.get(i));
				}
				listA = new ArrayList<ShpPoint>(listA.subList(indexA,
						listA.size()));
				listB = new ArrayList<ShpPoint>(listB.subList(indexB,
						listB.size()));
				listA.add(0, ShpPoint.valueOf(splitPointA));
				listB.add(0, ShpPoint.valueOf(splitPointB));

				polygonBlock.blockNo = taskData
						.calcBlockNo(edgeBlock.coordinate.get(0));
				polygonBlock.type = polygon.type;
			}
			//
			{
				// 创建一个面
				Edge edgeBlock = new Edge();
				Polygon polygonBlock = new Polygon();
				polygonBlock.edges.add(edgeBlock);
				splitPolygonList.add(polygonBlock);
				edgeBlock.coordinate.addAll(listA);
				Collections.reverse(listB);
				edgeBlock.coordinate.addAll(listB);

				polygonBlock.blockNo = taskData.calcBlockNo(listB.get(0));
				polygonBlock.type = polygon.type;
			}
		}
		this.polygongs = splitPolygonList;
	}

	void splitPolygonByBlockY() {
		List<Polygon> splitPolygon = new ArrayList<Polygon>();
		for (Polygon polygon : polygongs) { 
			List<ShpPoint> points = new ArrayList<ShpPoint>();
			for (Edge edge : polygon.edges) {
				points.addAll(edge.coordinate);
			}
			// X方向 最大 最小
			int indexMin = 0, indexMax = 0;
			ShpPoint minPoint = points.get(indexMin);
			ShpPoint maxPoint = points.get(indexMax);

			polygon.blockNo = taskData.calcBlockNo(points.get(0));
			for (int i = 1; i < points.size(); i++) {
				ShpPoint point = points.get(i);
				if (minPoint.y > point.y) {
					minPoint = point;
					indexMin = i;
				}
				if (maxPoint.y < point.y) {
					maxPoint = point;
					indexMax = i;
				}
			}
			// 面的范围
			int startBlockNo_y = taskData.calcBlockNo(minPoint).iY;
			int endBlockBlockNo_y = taskData.calcBlockNo(maxPoint).iY;
			if (startBlockNo_y == endBlockBlockNo_y) {
				splitPolygon.add(polygon);
				continue;
			}
			// 造两个List A B
			List<ShpPoint> listA = new ArrayList<ShpPoint>();
			List<ShpPoint> listB = new ArrayList<ShpPoint>();
			for (int index = indexMin;; index++) {
				index = index % points.size();
				listA.add(ShpPoint.valueOf(points.get(index)));
				if (index == indexMax) {
					break;
				}
			}
			for (int index = indexMin;; index--) {
				index = index % points.size();
				if (index < 0) {
					index += index + points.size() + 1;
				}
				listB.add(ShpPoint.valueOf(points.get(index)));
				if (index == indexMax) {
					break;
				}
			}
//			boolean check = true;
//			// test 验证 ListA ListB
//			for (int i = 0; i < listA.size() - 1; i++) {
//				if (listA.get(i + 1).y < listA.get(i).y) {
//					// System.err.println("a y");
//					check = false;
//				}
//			}
//			for (int i = 0; i < listB.size() - 1; i++) {
//				if (listB.get(i + 1).y < listB.get(i).y) {
//					// System.err.println("b y");
//					check = false;
//				}
//			}
//			if (!check) {
//				System.err.println("分割后依然存在凹面 "+ "task:" + taskData.task.toString());
//				continue;
//			}
			// 分割
			for (int splitY = (startBlockNo_y + 1)
					* taskData.blockInfo.iBlockHight; splitY <= (endBlockBlockNo_y)
					* taskData.blockInfo.iBlockHight; splitY += taskData.blockInfo.iBlockHight) {
				int indexA = 1;
				int indexB = 1;
				ShpPoint splitPointA = null;
				ShpPoint splitPointB = null;
				for (; indexA < listA.size(); indexA++) {
					if (splitY == listA.get(indexA).y) {
						splitPointA = listA.get(indexA);
						// indexA++;
						break;
					}
					if (splitY < listA.get(indexA).y) {
						long blockPoint_x = (long) (splitY - listA
								.get(indexA - 1).y)
								* (listA.get(indexA).x - listA.get(indexA - 1).x)
								/ (listA.get(indexA).y - listA.get(indexA - 1).y)
								+ listA.get(indexA - 1).x;
						splitPointA = new ShpPoint((int) blockPoint_x, splitY);
						break;
					}
				}
				for (; indexB < listB.size(); indexB++) {
					if (splitY == listB.get(indexB).y) {
						splitPointB = listB.get(indexB);
						// indexB++;
						break;
					}
					if (splitY < listB.get(indexB).y) {
						long blockPoint_x = (long) (splitY - listB
								.get(indexB - 1).y)
								* (listB.get(indexB).x - listB.get(indexB - 1).x)
								/ (listB.get(indexB).y - listB.get(indexB - 1).y)
								+ listB.get(indexB - 1).x;
						splitPointB = new ShpPoint((int) blockPoint_x, splitY);
						break;
					}
				}
				// 创建一个面
				Edge edgeBlock = new Edge();
				Polygon polygonBlock = new Polygon();
				polygonBlock.edges.add(edgeBlock);
				splitPolygon.add(polygonBlock);
				for (int i = 0; i < indexA; i++) {
					edgeBlock.coordinate.add(listA.get(i));
				}
				edgeBlock.coordinate.add(new ShpPoint(splitPointA.x,
						splitPointA.y - 1));
				edgeBlock.coordinate.add(new ShpPoint(splitPointB.x,
						splitPointB.y - 1));
				for (int i = indexB - 1; i >= 0; i--) {
					edgeBlock.coordinate.add(listB.get(i));
				}
				listA = new ArrayList<ShpPoint>(listA.subList(indexA,
						listA.size()));
				listB = new ArrayList<ShpPoint>(listB.subList(indexB,
						listB.size()));
				listA.add(0, ShpPoint.valueOf(splitPointA));
				listB.add(0, ShpPoint.valueOf(splitPointB));

				polygonBlock.blockNo = taskData
						.calcBlockNo(edgeBlock.coordinate.get(0));
				polygonBlock.type = polygon.type;
			}
			{
				// 创建一个面
				Edge edgeBlock = new Edge();
				Polygon polygonBlock = new Polygon();
				polygonBlock.edges.add(edgeBlock);
				splitPolygon.add(polygonBlock);
				edgeBlock.coordinate.addAll(listA);
				Collections.reverse(listB);
				edgeBlock.coordinate.addAll(listB);
				polygonBlock.blockNo = taskData.calcBlockNo(listB.get(0));
				polygonBlock.type = polygon.type;
			}
		}
		this.polygongs = splitPolygon;
	}

	public static enum Type {
		SeaArea, // 海洋
		Land, // 绿地
		Island, // 岛屿
		Water, // 水系
		ProvinceAdmArea, // 行政面
		WaterLine, // 水系
		AdmaBorderNation, // 国界线
		ProviceBorder, // 省界线
		SpecialBorder, // 特殊边界
		UnNationBorder, // 行政界（未定国界）
		UnProviceBorder, // 未定省界
		RailWay, // 铁路
		Subway, // 地铁
		Suspension, // 磁悬浮
		// 轻轨
		LightRail, LightRailOne, LightRailTwo, LightRailThree,
		// 地铁
		SubwayOne, SubwayTwo, SubwayThree, SubwayFour, SubwayFive, SubwaySix, SubwaySeven, SubwayEight, SubwayNine, SubwayTen, SubwayEleven, SubwayTwelve, SubwayThirteen, SubwayFourteen,
	}

	// 以下为测试代码 =========
	public static void main(String[] args) {
		List<Point> list = readerPoints();
		PointList pts = new PointList(list);// 赋值
		if (!pts.ccw()) {// 逆时针方向调整
			Collections.reverse(list);
			pts = new PointList(list);// 赋值
		}
		// List<List<Integer>> dividList = PolygonHandle.converToConvex(pts,
		// null);
	}

	public static List<Point> readerPoints() {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				Background.class.getResourceAsStream("points.txt")));
		List<Point> points = new ArrayList<Point>();
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] pintXY = line.split(":");
				System.out.println(Double.parseDouble(pintXY[0]) + " : "
						+ Double.parseDouble(pintXY[1]));
				Point point = new Point(Double.parseDouble(pintXY[0]),
						Double.parseDouble(pintXY[1]));
				if (points.isEmpty()) {
					points.add(point);
					continue;
				} else {
					// 相邻两点相同||X共线||Y共线
					if (points.get(points.size() - 1).equals(point)) {
						continue;
					}
					// 共线
					if (points.size() >= 2
							&& ((points.get(points.size() - 1).x() == point.x() && point
									.x() == points.get(points.size() - 2).x()) || ((points
									.get(points.size() - 1).y() == point.y() && point
									.y() == points.get(points.size() - 2).y())))) {
						points.set(points.size() - 1, point);
						continue;
					}
				}
				points.add(point);
			}// 首位点相同
			if (points.get(0).equals(points.get(points.size() - 1))) {
				points.remove(points.size() - 1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return points;
	}

	/**
	 * 背景面的平滑处理
	 */
	public void smoothPolygon() {
		int level = this.taskData.task.getLevel();
		LevelInfo levelAndScale = DataManager.getLevelInfo(level);

		double disAngleThreshold = levelAndScale.minScale
				* SmoothStyle.length_1;
		double disThreshold = levelAndScale.minScale * SmoothStyle.length_2;

		for (Polygon polygon : this.polygongs) {
			if (polygon.type != Type.ProvinceAdmArea
					&& polygon.type != Type.SeaArea) {
				continue;
			}
			for (Edge edge : polygon.edges) {
				for (int i = edge.coordinate.size() - 2; i > 0; i--) {
					ShpPoint headerPoint = edge.coordinate.get(i - 1);
					ShpPoint midPoint = edge.coordinate.get(i);
					ShpPoint tailPoint = edge.coordinate.get(i + 1);
					// 根据不同的纬度设置不同的阀值。
					double disThresholdWithLon = disThreshold
							* Math.cos(midPoint.y / 2560 / 2400 * Math.PI / 180);
					double disAngleThresholdWithLon = disAngleThreshold
							* Math.cos(midPoint.y / 2560 / 2400 * Math.PI / 180);
					// 邻边1
					double disA = PolygonUtil.twoPointDistance(headerPoint,
							midPoint);
					// 邻边2
					double disB = PolygonUtil.twoPointDistance(midPoint,
							tailPoint);

					double angle = PolygonUtil.sphereAngle(headerPoint,
							midPoint, tailPoint);

					// 删除道路点条件,1角度接近直线 并且 距离相离的两点不是很远
					if (angle > 175
							&& (disA < disAngleThresholdWithLon || disB < disAngleThresholdWithLon)) {
						edge.coordinate.remove(i);
					} else if (disB < disThresholdWithLon
							&& disA < disThresholdWithLon) {
						edge.coordinate.remove(i);
					}
				}
			}
		}
	}

	/**
	 * 背景面的平滑处理
	 */
	public Polygon smoothPolygon(Polygon polygon) {
		Polygon _polygon = new Polygon();
		_polygon.type = polygon.type;
		_polygon.id = polygon.id;
		_polygon.name = polygon.name;
		int level = this.taskData.task.getLevel();
		LevelInfo levelAndScale = DataManager.getLevelInfo(level);

		double disAngleThreshold = levelAndScale.minScale
				* SmoothStyle.length_1;
		double disThreshold = levelAndScale.minScale * SmoothStyle.length_2;
		for (Edge edge : polygon.edges) {
			Edge _edge = new Edge();
			_edge.coordinate = new ArrayList<ShpPoint>(edge.coordinate);
			for (int i = _edge.coordinate.size() - 2; i > 0; i--) {
				ShpPoint headerPoint = _edge.coordinate.get(i - 1);
				ShpPoint midPoint = _edge.coordinate.get(i);
				ShpPoint tailPoint = _edge.coordinate.get(i + 1);
				// 根据不同的纬度设置不同的阀值。
				double disThresholdWithLon = disThreshold
						* Math.cos(midPoint.y / 2560 / 2400 * Math.PI / 180);
				double disAngleThresholdWithLon = disAngleThreshold
						* Math.cos(midPoint.y / 2560 / 2400 * Math.PI / 180);
				// 邻边1
				double disA = PolygonUtil.twoPointDistance(headerPoint,
						midPoint);
				// 邻边2
				double disB = PolygonUtil.twoPointDistance(midPoint, tailPoint);

				double angle = PolygonUtil.sphereAngle(headerPoint, midPoint,
						tailPoint);

				// 删除道路点条件,1角度接近直线 并且 距离相离的两点不是很远
				if (angle > 175
						&& (disA < disAngleThresholdWithLon || disB < disAngleThresholdWithLon)) {
					_edge.coordinate.remove(i);
				} else if (this.taskData.task.getLevel() >= 6
						&& disB < disThresholdWithLon
						&& disA < disThresholdWithLon) {
					_edge.coordinate.remove(i);
				}
			}
			_polygon.edges.add(_edge);
		}
		return _polygon;
	}

	// 背景线的处理
	public void smoothLine() {
		for (Polyline polyline : this.polylines) {
			for (Edge edge : polyline.edges) {
				taskData.deletePoint(edge.coordinate);
			}
		}
	}
}

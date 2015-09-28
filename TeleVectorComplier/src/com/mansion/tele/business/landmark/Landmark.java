package com.mansion.tele.business.landmark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.Task;
import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.landmark.CreateIndexImpl.Index;
import com.mansion.tele.business.landmark.Style.DisPlay;
import com.mansion.tele.business.network.NetworkIndex;
import com.mansion.tele.business.network.NetworkIndex.RoadIndex;
import com.mansion.tele.business.network.RouteForRoadName;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.PolygonUtil;

/**
 * 保存地标，提供地标相关的操作
 * 
 */
public class Landmark implements Serializable {
	// staitc 式样；母库-》处理-》格式 道路名称的code
	public static Style style;
	// static 特殊处理
	public static SpecialLandMark specialLandMark;
	//一纬度等于多少米
	public int oneLatToMeter;
	//一经度等于多少米
	public int oneLonToMeter;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3948019501045419538L;
	// 标记数据集合
	public List<MarkPoint> LandMarkType_list = new ArrayList<MarkPoint>();
	//下层不需要的标记
	public List<MarkPoint> mapsings = new ArrayList<MarkPoint>();
	public transient  List<MarkPoint> RoadName = new ArrayList<MarkPoint>();
	// 任务数据管理
	public TaskData taskData;

	
	
	/**
	 * 式样信息初始化
	 * @return
	 */
	public static boolean init() {
		style = new Style();
		style.init();
		specialLandMark = new SpecialLandMark();
		specialLandMark.initSpectial();
		style.getRoadStyle();
		return true;
	}

	/**
	 * 式样处理
	 */
	public void build() {
		// 获取LandMarkType数据
		// TestConServlet.taskManager.getData_mid_mapT3()
		// .get(task).getLandMarkType_list();
		if (LandMarkType_list == null || LandMarkType_list.size() == 0) {
			return;
		}
		//不排版的数据
		List<MarkPoint> notTypeSetData = new ArrayList<MarkPoint>();
		//获取道路名
		this.getRoadNameMark(style.roadStyleList);
		//获得式样
		this.getStyle(notTypeSetData);
		//处理名称
		this.makeMarkName();
		// LandMarkType优先级处理
		this.makeMarkPri();
		// 获得此层的比例尺
		int[] landScale = DataManager.getLevelInfo(taskData.task.getLevel()).scales;
		// 建立空间索引
		List<Index> indexs = this.createMarkIndex();
		List<RoadIndex> roadIndexs = this.createRoadIndex();
		for (int i = landScale.length - 1; i >= 0 ; i--) {
			// 创建排版矩形框
			this.createTypeRect(i);
			// 标记与道路进行比较
			if (taskData.task.getLevel() <= style.typeSetRoadLevel) {
				this.composeSignLandMarkVsRoad(indexs, roadIndexs);
			}
			// 排版
			this.typeSetting(indexs, i);// TODO 去掉比例尺意外的参数
		}
		this.delTypeSetMark(notTypeSetData);
	}

	/**
	 * 排序比较器
	 */
	static Comparator<MarkPoint> comparator = new Comparator<MarkPoint>() {

		@Override
		public int compare(MarkPoint o1, MarkPoint o2) {
			int re = o1.comparePri(o2);
			if (re == 0) {
				return o1.compareName(o2);
			}
			return re;
		}
	};
	public static Comparator<Index> indexComparator = new Comparator<CreateIndexImpl.Index>() {

		@Override
		public int compare(Index arg0, Index arg1) {
//			int comValue = arg0.compareByX(arg1);
//			if (comValue == 0) {
//				comValue = arg0.compareByY(arg1);
//			}
//			return comValue;
			return arg0.compareIndex(arg1);
		}
	};
	

	/**
	 * 地标优先级处理
	 * 
	 * @param landMarkList
	 *            标记集合
	 * @param task
	 *            任务号
	 * @return 返回处理之后的集合
	 */
	public void makeMarkPri() {
		// 根据父子关系优先级合理化
		for (int i = 0; i < LandMarkType_list.size(); i++) {
			boolean del = LandMarkType_list.get(i).getFather(LandMarkType_list);
			if(del){
				i--;
			}
		}
	}

	/**
	 * 删除排版压盖的数据
	 */
	private void delTypeSetMark(List<MarkPoint> notTypeSet) {
		int[] landScale = DataManager.getLevelInfo(taskData.task.getLevel()).scales;
		for (int i = 0; i < this.LandMarkType_list.size(); i++) {
			MarkPoint landMarkType = this.LandMarkType_list.get(i);
			if(taskData.task.getLevel() == 2 && (landMarkType.telecode == 2010105 || 
					landMarkType.telecode == 2010106)){
				landMarkType.isSpecial = true;
			}
			if(landMarkType.bvs[0] == false && landMarkType.bvs[1] == false 
					&& landMarkType.bvs[2] == false && !landMarkType.isSpecial){
//				if(style.isShowLevel(landMarkType.telecode, taskData.task.getLevel())){
//					this.LandMarkType_list.remove(i);
//					i--;
//				}
				this.LandMarkType_list.remove(i);
				i--;
			}
			// 最后一个比例尺不显示的不升层
			if(!landMarkType.bvs[landScale.length-1] && !landMarkType.isSpecial){
				mapsings.add(landMarkType);
			}
		}
		this.LandMarkType_list.addAll(notTypeSet);
		for(int j= 0 ;j < this.RoadName.size() ; j++){
			MarkPoint roadName = this.RoadName.get(j);
			if(roadName.bvs[0] == false && roadName.bvs[1] == false && roadName.bvs[2] == false){
				this.RoadName.remove(j);
				j--;
			}
		}
	}

	/**
	 * 地标名称处理
	 * 
	 * @param landMarkList
	 *            标记集合
	 * @param task
	 *            任务号
	 * @return 返回地标名称处理之后集合
	 */
	public void makeMarkName() {
		// 地标名称处理
		for (int i = 0; i < LandMarkType_list.size(); i++) {
			LandMarkType_list.get(i).getName(LandMarkType_list.get(i),
					taskData.task);
		}
		// 道路名处理
		for(int j=0;j<RoadName.size();j++){
			RoadName.get(j).getName(RoadName.get(j), taskData.task);
		}
	}

	/**
	 * 删除去重复地标
	 */
	public void delRepeatMark() {
		Collections.sort(this.LandMarkType_list, compareLandMarkTypeName);
		LandMarkType_list = this.deleteMultSignLandMark(LandMarkType_list,  -1);
//		List<LandMarkType> bvs0 = new ArrayList<LandMarkType>();
//		List<LandMarkType> bvs1 = new ArrayList<LandMarkType>();
//		List<LandMarkType> bvs2 = new ArrayList<LandMarkType>();
//		for (int i = 0; i < this.RoadName.size(); i++) {
//			LandMarkType roadnamemark = RoadName.get(i);
//			if(roadnamemark.bvs[0]){
//				bvs0.add(roadnamemark);
//			}else if(roadnamemark.bvs[1]){
//				bvs1.add(roadnamemark);
//			}else if(roadnamemark.bvs[2]){
//				bvs2.add(roadnamemark);
//			}
//		}
//		Collections.sort(bvs0, compareLandMarkTypeName);
//		Collections.sort(bvs1, compareLandMarkTypeName);
//		Collections.sort(bvs2, compareLandMarkTypeName);
//		this.RoadName = new ArrayList<LandMarkType>();
//		this.RoadName.addAll(this.deleteMultSignLandMark(bvs0,0));
//		this.RoadName.addAll(this.deleteMultSignLandMark(bvs1,1));
//		this.RoadName.addAll(this.deleteMultSignLandMark(bvs2,2));
	}

	/**
	 * 建立空间索引
	 * 
	 * @return
	 */
	public List<Index> createMarkIndex() {
		CreateIndexImpl createIndexImpl = new CreateIndexImpl(taskData.task);
		createIndexImpl.indexLandMarkType(LandMarkType_list);
		createIndexImpl.indexRoadNameType(RoadName);
		List<Index> indexs = createIndexImpl.getIndexList();
		Collections.sort(indexs, indexComparator);
		return indexs;
	}
	
	public List<RoadIndex> createRoadIndex(){
		NetworkIndex networkIndex = new NetworkIndex(taskData.task);
		networkIndex.indexRoadSegment(taskData.network.roadList);
		return networkIndex.getIndexList();
	}
	/**
	 * 
	 * @return
	 */
	public void getRoadNameMark(List<ShowStyle> roadStyleList){
		RouteForRoadName roadName = new RouteForRoadName();
		this.RoadName.addAll(roadName.getRoadName(taskData, roadStyleList));
	}

	/**
	 * 建立rect
	 * 
	 * @param iScaleNo
	 */
	public void createTypeRect(int iScaleNo) {
		for (int i = 0; i < LandMarkType_list.size(); i++) {
			MarkPoint landMarkType = LandMarkType_list.get(i);
//			if(iScaleNo - 1 != -1 && !landMarkType.bvs[iScaleNo - 1] 
//					&& ((style.isShowByLevelAndScale(landMarkType.telecode, taskData.task.getLevel(), iScaleNo))
//						|| landMarkType.isLandMark)){
//				if(iScaleNo - 1 == 0){
//					this.LandMarkType_list.remove(i);
//					i--;
//				}else{
//					landMarkType.indexivs[iScaleNo] = true;
//				}
//			}
			if (landMarkType.display.equals(Style.DisPlay.lineShow)) {
				landMarkType.getEyeoneRectlist();
			} else if (landMarkType.display.equals(Style.DisPlay.isTxtLandMark)) {
				landMarkType.createRectBySignByStation(this.taskData.task.getLevel(), iScaleNo, 0, this.oneLonToMeter, this.oneLatToMeter);
				specialLandMark.handleStation(landMarkType, taskData.task.getLevel());
				specialLandMark.judgeThisMarkIsShow(landMarkType, this.taskData.task.getLevel(), iScaleNo);
			} else {
				landMarkType.createRectBySign(this.taskData.task.getLevel(), iScaleNo, this.oneLonToMeter, this.oneLatToMeter);
				specialLandMark.handleStation(landMarkType, taskData.task.getLevel());
				specialLandMark.handleTypeSetRect(landMarkType, taskData.task.getLevel(), iScaleNo, this.oneLonToMeter, this.oneLatToMeter);
				specialLandMark.judgeThisMarkIsShow(landMarkType, this.taskData.task.getLevel(), iScaleNo);
			}
		}
	}

	/**
	 * 分块排版
	 * 
	 * @param landlist
	 * @param indexs
	 * @param task
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public void typeSetting(List<Index> indexs, int iScaleNo) {
		for (int i = 0; i < indexs.size(); i++) {
			List<MarkPoint> landMarks = new ArrayList<MarkPoint>();
			Index index = indexs.get(i);
//			typeSetInit(index, iScaleNo);
			landMarks.addAll(index.getLandMarkList());
			landMarks.addAll(this.getSurroundLandMark(indexs, index));
			// 不需要道路排版时关闭
			landMarks.addAll(index.getRoadNameList());
			landMarks.addAll(this.getSurroundRoadName(indexs, index));
			this.TypeSetting(landMarks, iScaleNo);
		}
	}

	/**
	 * 初始化功能
	 * @param index
	 * @param iScaleNo
	 */
	public void typeSetInit(Index index, int iScaleNo){
		for (int i = 0; i < index.getLandMarkList().size(); i++) {
			MarkPoint landMarkType = index.getLandMarkList().get(i);
			if(!DisPlay.lineShow.equals(landMarkType.display)){
				landMarkType.bvs[iScaleNo] = false;
			}
		}
	}
	
	/**
	 * 获得块周围块的数据
	 * 
	 * @param indexs
	 * @param index
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public List<MarkPoint> getSurroundLandMark(List<Index> indexs,
			Index index) {
		List<MarkPoint> landMarks = new ArrayList<MarkPoint>();
		int[] xy = index.getIndexXY();
		for (int i = xy[0] - 1; i <= xy[0] + 1; i++) {
			for (int j = xy[1] - 1; j <= xy[1] + 1; j++) {
				if (i == xy[0] && j == xy[1]) {
					continue;
				}
				int indexstation = Collections.binarySearch(indexs,
						new Index(i, j), indexComparator);
				if (indexstation < 0) {
					continue;
				}
				landMarks.addAll(indexs.get(indexstation).getLandMarkList());
			}
		}
		return landMarks;

	}
	
	/**
	 * 获得块周围块的道路名
	 * 
	 * @param indexs
	 * @param index
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public List<MarkPoint> getSurroundRoadName(List<Index> indexs,
			Index index) {
		List<MarkPoint> landMarks = new ArrayList<MarkPoint>();
		int[] xy = index.getIndexXY();
		for (int i = xy[0] - 1; i <= xy[0] + 1; i++) {
			for (int j = xy[1] - 1; j <= xy[1] + 1; j++) {
				if (i == xy[0] && j == xy[1]) {
					continue;
				}
				int indexstation = Collections.binarySearch(indexs,
						new Index(i, j), indexComparator);
				if (indexstation < 0) {
					continue;
				}
				landMarks.addAll(indexs.get(indexstation).getRoadNameList());
			}
		}
		return landMarks;

	}

	/**
	 * 排版 1，只是初级排版，删除一定不需要显示的地标，不保证压盖。 2，要考虑在旋转以后，任何角度可以存在的地标，都要保留。 3，//TODO
	 * 将来移植到栅格时，要重写此方法，保证不能压盖
	 * 道路名称不参与排版，bvs都设置成true
	 * @param lmlist
	 * @return
	 */
	public void TypeSetting(List<MarkPoint> lmlist, int scaleNo) {
		List<MarkPoint> typeIsShow = new ArrayList<MarkPoint>();
		List<MarkPoint> isNotShow = new ArrayList<MarkPoint>();
		List<MarkPoint> thisScaleNotShow = new ArrayList<MarkPoint>();
		List<MarkPoint> lastScaleShow = new ArrayList<MarkPoint>();
		// 低层显示但升不上来
		List<MarkPoint> lowLevelShow = new ArrayList<MarkPoint>();
		for (int i = 0; i < lmlist.size(); i++) {
			MarkPoint landMarkType = lmlist.get(i);
			if(landMarkType.nextScaleShow[scaleNo]){
				lastScaleShow.add(landMarkType);
			}
			else if(!style.isShowByLevelAndScale(landMarkType.telecode, taskData.task.getLevel(), scaleNo)
					&& !landMarkType.isLandMark){
				if(style.isFillLevel(landMarkType.telecode, taskData.task.getLevel())){
					lowLevelShow.add(landMarkType);
				}
				else{
					thisScaleNotShow.add(landMarkType);
				}
			}
			else if(landMarkType.bvs[scaleNo]){
				typeIsShow.add(landMarkType);
			}
			else if(landMarkType.indexivs[scaleNo] || landMarkType.isRoadName){
				thisScaleNotShow.add(landMarkType);
			}
			else{
				isNotShow.add(landMarkType);
			}
		}
		//按优先级排序
//		Collections.sort(isNotShow, comparator);
		List<MarkPoint> displaiedMarks = new ArrayList<MarkPoint>();
		List<MarkPoint> typesetMarks = new ArrayList<MarkPoint>();
		typeIsShow.addAll(isNotShow);
		Collections.sort(lastScaleShow, comparator);
		Collections.sort(typeIsShow, comparator);
		Collections.sort(lowLevelShow, comparator);
		typesetMarks.addAll(lastScaleShow);
		typesetMarks.addAll(typeIsShow);
		typesetMarks.addAll(lowLevelShow);
		for (int i = 0; i < typesetMarks.size(); i++) {
			MarkPoint landMarkType = typesetMarks.get(i);
			boolean typeOk = true;
//			if(landMarkType.display.equals(DisPlay.lineShow)){
//				continue;
//			}
			for (int j2 = 0; j2 < landMarkType.ableStations.size(); j2++) {
				for (int j = 0; j < displaiedMarks.size(); j++) {
					// 判断和已有的mark是否压盖，有多个矩形时把压盖的矩形去掉
					boolean flag = displaiedMarks.get(j).typeSetting(
							landMarkType, scaleNo);
					landMarkType.indexivs[scaleNo] = true;
					if (flag) {
						typeOk = false;
						if(landMarkType.ableStations.size() > 1 && // 路名不能remove
								!landMarkType.display.equals(Style.DisPlay.lineShow)){
							landMarkType.ableStations.remove(j2);
							j2--;
						}
						break;
					}
					else{
						typeOk = true;
					}
				}
				if (typeOk == true) {
					if (landMarkType.display.equals(Style.DisPlay.centerShow)
							|| landMarkType.display.equals(Style.DisPlay.otherShow)
							|| landMarkType.display.equals(Style.DisPlay.notShowTxt)) {
						MarkPointRect setRect = landMarkType.ableStations.get(0);
						landMarkType.ableStations = new ArrayList<MarkPointRect>();
						landMarkType.ableStations.add(setRect);
					}
					landMarkType.bvs[scaleNo] = true;
				}else{
					landMarkType.bvs[scaleNo] = false;
					// 如果为路名，压盖了一个矩形框就认为是压盖不需要再比较
					if(landMarkType.display.equals(Style.DisPlay.lineShow)){
						break;
					}
				}
			}
			if(typeOk == true){
				displaiedMarks.add(landMarkType);
				if(!landMarkType.isRoadName && scaleNo > 0){
					landMarkType.nextScaleShow[scaleNo-1] = true;
				}
			}
			else{
				if(!landMarkType.isRoadName && scaleNo > 0){
					landMarkType.nextScaleShow[scaleNo-1] = false;
				}
			}
		}
	}

	/**
	 * poi与路比较
	 * 
	 * @param indexList
	 */
	public void composeSignLandMarkVsRoad(List<Index> indexList, List<RoadIndex> roadindexList) {
		for (int z = 0 ; z < indexList.size() ; z++) {
			Index index = indexList.get(z);
			List<MarkPoint> landMarkType = index.getLandMarkList();
			for (int i = 0; i < landMarkType.size(); i++) {
				if (landMarkType.get(i).display.equals(DisPlay.centerShow)
						|| landMarkType.get(i).display.equals(DisPlay.otherShow)) {
					MarkPoint signLandMark = landMarkType.get(i);
					
					List<MarkPointRect> rects = new ArrayList<MarkPointRect>();
					for (int j = 0; j < signLandMark.ableStations.size(); j++) {
						RoadIndex roadindex = roadindexList.get(z);
//						System.out.println("pointindex" + index.getIndexXY()[0] + "," + index.getIndexXY()[1]);
//						System.out.println("roadindex" + roadindex.getIndexXY()[0] + "," + roadindex.getIndexXY()[1]);
						int oneFontSize = 0;
						GeoRect typeRect = signLandMark.ableStations.get(j).geoRects;
						if(signLandMark.name.length() > 6){
							oneFontSize = (typeRect.top - typeRect.bottom) / 3;
						}else{
							oneFontSize = (typeRect.top - typeRect.bottom) / 2;
						}
						boolean textCross =roadindex.isCrossLineToRect(GeoRect.valueOf(
								new ShpPoint(typeRect.left + oneFontSize, typeRect.bottom + oneFontSize)
								, new ShpPoint(typeRect.right - oneFontSize, typeRect.top - oneFontSize)));
						boolean iconCross = false;
						if( signLandMark.iconRect != null){
							GeoRect iconRect = signLandMark.iconRect.geoRects;
							iconCross = roadindex.isCrossLineToRect(GeoRect.valueOf(
									new ShpPoint(iconRect.left + oneFontSize, iconRect.bottom + oneFontSize)
									, new ShpPoint(iconRect.right - oneFontSize, iconRect.top - oneFontSize)));
						}
						if(!textCross && !iconCross){
							rects.add(signLandMark.ableStations.get(j));
						}
					}
					if(rects.size() != 0){
						signLandMark.ableStations = rects;
					}
				}
			}
		}
	}

	/**
	 * 读取数据
	 */
	public List<MarkPoint> fromDB() {// 注释 zhangjin for test
		LandMarkLoader landMarkLoad = new LandMarkLoader();
		List<String> meshNos = DataManager.getMeshNos(this.taskData.task);
		List<MarkPoint> busstops = landMarkLoad.loadBusAndTransform(
				this.taskData.task, meshNos);
		List<MarkPoint> marks = landMarkLoad.loadLevel0Mark(
				this.taskData.task, meshNos);
		List<MarkPoint> subrailstops = landMarkLoad.loadRailWayStopInfor(
				this.taskData.task, meshNos);
		List<MarkPoint> subEntrances = landMarkLoad.loadSubwayEntranceInfor(
				this.taskData.task, meshNos);
		List<MarkPoint> mapsigns = landMarkLoad.loadmapSign(
				this.taskData.task, meshNos);
		LandMarkType_list.addAll(busstops);
		LandMarkType_list.addAll(marks);
		LandMarkType_list.addAll(subrailstops);
		LandMarkType_list.addAll(subEntrances);
		LandMarkType_list.addAll(mapsigns);
		return mapsigns;
	}
	
	/**
	 * 标记升层
	 * 
	 * @param task
	 *            当前任务
	 * @return 返回升层后的当前层数据
	 */
	public List<MarkPoint> upliftedMarkLevel(
			List<MarkPoint> downLevelAllData) {
		Task task = this.taskData.task;
		List<String> meshNos = DataManager.getMeshNos(task);
		LandMarkLoader landMarkLoad = new LandMarkLoader();
		List<MarkPoint> mapsigns = landMarkLoad.loadmapSign(task, meshNos);
		downLevelAllData.addAll(mapsigns);
		List<MarkPoint> landMarkType = new ArrayList<MarkPoint>();
		MarkPoint markPoint = new MarkPoint();
		// 对下层数据进行升层处理
		for (MarkPoint landMark : downLevelAllData) {
			landMarkType = markPoint.upLevel(landMark, task, landMarkType);
		}
		this.LandMarkType_list = landMarkType;
		return mapsigns;
	}

	/**
	 * 获得式样
	 */
	public void getStyle(List<MarkPoint> notTypeSet) {
		if (LandMarkType_list != null) {
			for (int i = 0; i < this.LandMarkType_list.size(); i++) {
				MarkPoint landMarkType = this.LandMarkType_list.get(i);
				Style.SignStyle signStyle = null;
				if(style.isFillLevel(landMarkType.telecode, taskData.task.getLevel())){
					signStyle = style.getStyle(landMarkType.telecode, taskData.task.getLevel()-1, 0);
					if(signStyle == null){
						signStyle = style.getStyle(landMarkType.telecode, 0, 0);
					}
				}
				else{
					signStyle = style.getStyle(landMarkType.telecode, taskData.task.getLevel(), 0);
				}
				if(landMarkType.isLandMark){
					signStyle = style.getStyle(landMarkType.telecode, 0, 0);
					if(signStyle != null){
						signStyle.pri = specialLandMark.LANDMARKPRI;
					}else{
						System.out.println("strid:" + landMarkType.strid + ",名称为:" + landMarkType.name);
					}
				}
				if (signStyle != null) {
					landMarkType.display = signStyle.display;
					landMarkType.font = signStyle.font;
					landMarkType.xSpace = signStyle.xSpace;
					landMarkType.ySpace = signStyle.ySpace;
					landMarkType.pri = signStyle.pri;
					landMarkType.letterSpacing = signStyle.letterSpacing;
				}else{
					//名称中带有街道，并且是乡镇类型不显示
					if(specialLandMark.isShowByTxtAndCode(landMarkType)){
						this.LandMarkType_list.remove(i);
						i--;
						continue;
					}
					notTypeSet.add(landMarkType);
					this.LandMarkType_list.remove(i);
					i--;
				}
			}
		}
		
		if (RoadName != null) {
			for (int i = 0; i < this.RoadName.size(); i++) {
				MarkPoint landMarkType = this.RoadName.get(i);
				Style.SignStyle signStyle = style.getStyle(
						landMarkType.telecode, taskData.task.getLevel(), 0);
				if (signStyle != null) {
					landMarkType.display = signStyle.display;
					landMarkType.font = signStyle.font;
					landMarkType.xSpace = signStyle.xSpace;
					landMarkType.ySpace = signStyle.ySpace;
					landMarkType.pri = signStyle.pri;
					landMarkType.letterSpacing = signStyle.letterSpacing;
				}else{
					notTypeSet.add(landMarkType);
					this.RoadName.remove(i);
					i--;
				}
			}
		}
	}

	// /**
	// * 合并不同比例尺的相同数据
	// * @param list
	// * @return
	// */
	// public List<LandMarkType> mergeLandMarkByScale(List<LandMarkType> list) {
	// List<LandMarkType> elementSecond = new ArrayList<LandMarkType>();
	// List<LandMarkType> elements = new ArrayList<LandMarkType>();
	// // 提取IVS需要合并的数据
	// for (LandMarkType landMarkType : list) {
	// if (landMarkType.judge()) {
	// elements.add(landMarkType);
	// } else {
	// elementSecond.add(landMarkType);
	// }
	// }
	// // 对需要合并的数据进行排序
	// Collections.sort(elementSecond, new Comparator<LandMarkType>() {
	// public int compare(LandMarkType p1, LandMarkType p2) {
	// return p1.compare(p2);
	// }
	// });
	// // 对需要合并的数据进行合并
	// LandMarkType landMark = elementSecond.get(0);
	// for (int i = 1; i < elementSecond.size(); i++) {
	// if (landMark.compare(elementSecond.get(i)) == 0) {
	// landMark.sumIvs(elementSecond.get(i));
	// } else {
	// elements.add(landMark);
	// landMark = elementSecond.get(i);
	// }
	// }
	// elements.add(landMark);
	// return elements;
	// }

	/**
	 * 获取标记的block信息
	 * 
	 * @param signLis
	 *            标记集合
	 * @param task
	 *            任务号
	 * @return 返回添加了block信息的标记集合
	 */
	public void makeMarkInfor() {
		List<MarkPoint> signLis = this.LandMarkType_list;
		// 通过Level取得当前Block在宽和高
		for (int i = 0; i < signLis.size(); i++) {
			MarkPoint info = signLis.get(i);
			if(info.bvs[0] == false && info.bvs[1] == false && info.bvs[2] == false){
				signLis.remove(i);
				i--;
			}
			info.coordinateRegular(taskData);
		}
		
		for (int i = 0; i < this.RoadName.size(); i++) {
			MarkPoint info = this.RoadName.get(i);
			info.coordinateRegular(taskData);
			if(info.display.equals(DisPlay.lineShow) && (info.line == null || info.line.size() == 0)){
				this.RoadName.remove(i);
				i--;
			}
		}
	}
	
	// sort by poi name
		private static Comparator<MarkPoint> compareLandMarkTypeName = new Comparator<MarkPoint>() {
			@Override
			public int compare(MarkPoint o1, MarkPoint o2) {
				if(o1.telecode - o2.telecode == 0){
					return o1.name.compareTo(o2.name);
				}else{
					return o1.telecode - o2.telecode;
				}
			}
		};
		// sort by orgCode
//		private static Comparator<LandMarkType> compareLandMarkTypeOrg = new Comparator<LandMarkType>() {
//			@Override
//			public int compare(LandMarkType o1, LandMarkType o2) {
//				return o1.compareByOrg(o2);
//			}
//		};

		// delete duplicate name
		public List<MarkPoint> deleteMultSignLandMark(List<MarkPoint> landMarkList, int scaleNo) {
			//存放相同的点
			List<MarkPoint> homonymicList = new ArrayList<MarkPoint>();
			//存放结果集
			List<MarkPoint> sampleList = new ArrayList<MarkPoint>();
			
			//=====开始遍历去重
//			List<LandMarkType> homonymicList = new ArrayList<LandMarkType>();
			for (int index = 0; index < landMarkList.size();index++) {
//				if (landMarkList.get(index).display.equals(DisPlay.lineShow)
//						|| landMarkList.get(index).display.equals(DisPlay.isTxtLandMark)) {
//					sampleList.add(landMarkList.get(index));
//					continue;
//				}
				MarkPoint signLandMarkBehind =  landMarkList
						.get(index);
//				System.out.println(signLandMarkBehind.getName()+" : "+signLandMarkBehind.getOrgcode());
				if (homonymicList.isEmpty() || (signLandMarkBehind.name.equals(homonymicList.get(0).name)
						&& (signLandMarkBehind.getOrgcode() == homonymicList.get(0)
								.getOrgcode()))) {// 收集名称相同的点
					homonymicList.add(signLandMarkBehind);
				} else {
					//删除一个屏幕内的点
					sampleList.addAll(deleteSameNameInScreem(homonymicList, scaleNo));
					homonymicList.clear();
					homonymicList.add(signLandMarkBehind);
				}
			}
			sampleList.addAll(deleteSameNameInScreem(homonymicList,scaleNo));
			return sampleList;
		}
		//删除同屏幕内的点
		private List<MarkPoint> deleteSameNameInScreem(List<MarkPoint> homonymicList, int scaleNo){
			double standDistance = 0;
			int[] scales = DataManager.getLevelInfo(taskData.task.getLevel()).scales;
			if(scaleNo == -1){
				standDistance = scales[1]*7;
			}else if(scaleNo == 0){
				standDistance = scales[0]*7;
			}else if(scaleNo == 1){
				standDistance = scales[1]*7;
			}else if(scaleNo == 2 && scales.length == scaleNo + 1){
				standDistance = scales[2]*7;
			}else{
				standDistance = scales[1]*Math.sqrt(3.5*3.5 + 6.5*6.5);
			}
			
			if (homonymicList.size() <= 1) {
				return homonymicList;
			}
			int length = homonymicList.size();
			for (int i = 0; i < length;) {
				MarkPoint headLandMark = homonymicList.get(i);
				for (int j = i + 1; j < length; j++) {
					MarkPoint landMark = homonymicList.get(j);
					double distance = PolygonUtil.twoPointDistance(landMark.getBasicPoint(),headLandMark.getBasicPoint());
					if (standDistance > distance) {
						homonymicList.remove(i);
						break;
					}
				}
				if(length == homonymicList.size()){
					i++;
				}
				length = homonymicList.size();
			}
			return homonymicList;
		}

	/**
	 * 构造方法	
	 * @param taskData
	 */
	public Landmark(TaskData taskData) {
		// TODO Auto-generated constructor stub
		this.taskData = taskData;
		this.oneLatToMeter = (int) PolygonUtil.twoPointDistance(
				new ShpPoint(taskData.task.getLeft(), taskData.task.getBottom())
				, new ShpPoint(taskData.task.getLeft(), taskData.task.getBottom() + 3600*2560));
		this.oneLonToMeter = (int) PolygonUtil.twoPointDistance(
				new ShpPoint(taskData.task.getLeft(), taskData.task.getBottom())
				, new ShpPoint(taskData.task.getLeft() + 3600*2560, taskData.task.getBottom()));
	}
	

}

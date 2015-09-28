package com.mansion.tele.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;

import com.mansion.tele.action.distriManage.TestConServlet;
import com.mansion.tele.business.DataManager.LevelInfo;
import com.mansion.tele.business.background.Background;
import com.mansion.tele.business.landmark.MarkPoint;
import com.mansion.tele.business.landmark.Landmark;
import com.mansion.tele.business.network.Network;
import com.mansion.tele.business.network.NodeNew;
import com.mansion.tele.business.network.SmoothHandle;
import com.mansion.tele.business.network.SmoothStyle;
import com.mansion.tele.business.viewdata.TeleUnit;
import com.mansion.tele.business.viewdata.TeleView;
import com.mansion.tele.business.viewdata.ViewData;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.PolygonUtil;
import com.mansion.tele.util.Util;

/**
 * 一个任务的数据
 * 
 */
public class TaskData implements Serializable {
	private static final long serialVersionUID = -2896506016424244441L;
	public static final byte mmlevel = 88;

	public transient LevelInfo blockInfo = null;
	transient double ratioX;
	transient double ratioY;

	public Task task = null;
	// 路网存储 add by zhangjin
	public Network network = null;

	// 背景
	public transient Background background = null;

	// 地标
	public Landmark landmark = null;

	public TaskData(Task task) {
		this.task = task;
		network = new Network(this);
		landmark = new Landmark(this);
		background = new Background(this);

		blockInfo = DataManager.getLevelInfo(task.level);
		this.ratioX = blockInfo.iBlockWidth / 255.0;
		this.ratioY = blockInfo.iBlockHight / 255.0;
	}

	/**
	 * 根据point计算图页号，，边界上的点属于右上图页。
	 * 
	 * @param point
	 * @return
	 */
	public BlockNo calcBlockNo(ShpPoint point) {
		int blockX = (point.x - DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN)
				/ blockInfo.iBlockWidth;
		int blockY = (point.y - DataManager.MAP_GEO_LOCATION_LATITUDE_MIN)
				/ blockInfo.iBlockHight;
		return new BlockNo(blockX, blockY);
	}

	/**
	 * 根据point计算正规化坐标，边界上的点属于右上图页。
	 * 
	 * @param point
	 * @return 返回值域范围[0,255]
	 */
	public ShpPoint calcRegular(ShpPoint point) {
		int a = (point.x - DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN)
				% blockInfo.iBlockWidth;
		int x = (int) Math.round(a * 255.0 / blockInfo.iBlockWidth);

		int b = (point.y - DataManager.MAP_GEO_LOCATION_LATITUDE_MIN)
				% blockInfo.iBlockHight;
		int y = (int) Math.round(b * 255.0 / blockInfo.iBlockHight);

		return new ShpPoint(x, y);
	}

	/**
	 * 计算point在指定block內的正规化坐标。主要用于计算道路的名称的线坐标
	 * 
	 * @param block
	 *            ,point
	 * @return 返回值域范围[-32768,32767]
	 */
	public ShpPoint calcRegular(BlockNo block, ShpPoint point) {
		int x = (int) Math.round((point.x - block
				.getLeft(blockInfo.iBlockWidth))
				* 255.0
				/ blockInfo.iBlockWidth);
		int y = (int) Math.round((point.y - block
				.getBottom(blockInfo.iBlockHight))
				* 255.0
				/ blockInfo.iBlockHight);

		return new ShpPoint(x, y);
	}

	/**
	 * 根据不同的层号设置不同的阀,不同道路Nrc阀值不同 1.角度判断 高速，国道角度单独处理 //
	 */
	public void deletePoint(List<ShpPoint> coordinate) {
		byte level = task.level;
		if (level == 0) {
			return;
		}
		LevelInfo levelAndScale = DataManager.getLevelInfo(level);

		double disAngleThreshold = levelAndScale.minScale
				* SmoothStyle.length_1;
		double disThreshold = levelAndScale.minScale * SmoothStyle.length_2;

		// 计算中间点的角度,以及到两个相邻的的距离
		for (int i = coordinate.size() - 2; i > 0; i--) {
			ShpPoint headerPoint = coordinate.get(i - 1);
			ShpPoint midPoint = coordinate.get(i);
			ShpPoint tailPoint = coordinate.get(i + 1);
			// 根据不同的纬度设置不同的阀值。
			double disThresholdWithLon = disThreshold
					* Math.cos(midPoint.y / 2560 / 2400 * Math.PI / 180);
			double disAngleThresholdWithLon = disAngleThreshold
					* Math.cos(midPoint.y / 2560 / 2400 * Math.PI / 180);
			// 邻边1
			double disA = PolygonUtil.twoPointDistance(headerPoint, midPoint);
			// 邻边2
			double disB = PolygonUtil.twoPointDistance(midPoint, tailPoint);

			double angle = PolygonUtil.sphereAngle(headerPoint, midPoint,
					tailPoint);

			// 删除道路点条件,1角度接近直线 并且 距离相离的两点不是很远
			if (angle > SmoothStyle.angle
					&& (disA < disAngleThresholdWithLon || disB < disAngleThresholdWithLon)) {
				coordinate.remove(i);
			} else if (disB < disThresholdWithLon && disA < disThresholdWithLon) {
				coordinate.remove(i);
			}
		}
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	/**
	 * 输出成view格式
	 * 
	 * @param filePath
	 * @throws IOException 
	 */
	public void formatData(String filePath) throws IOException {
		ViewData viewData = new ViewData(this);
		viewData.createViewData();
		TeleUnit impl = new TeleView();
		// System.out.println(viewData.blockDatas.size());
		try {
			impl.makeUnit(filePath, this.task, viewData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从数据库加载任务数据
	 * 
	 * @throws Exception
	 */
	public void loadDataFromDB() throws Exception {
		// 背景
		if (TestConServlet.MakeBackground == true) {
			this.background.fromDB();
		}
		// 道路
		if (TestConServlet.MakeRoad == true) {
			this.network.fromDB();
			this.network.calcAngel();
		}
		// 做标记
		if (TestConServlet.MakeLandmark == true) {
			this.landmark.mapsings = this.landmark.fromDB();
		}
	}

	/**
	 * 从底层数据抽取任务数据
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public void loadDataFromLow(String filePath) throws Exception {
		// 背景
		if (TestConServlet.MakeBackground == true) {
			this.background.fromDB();
		}
		// 道路
		if (TestConServlet.MakeRoad == true) {
			List<Task> taskList = Task.getLowerTaskNos(this.getTask());
			List<NodeNew> taskBorderNodeList = new ArrayList<NodeNew>();
			for (Task lowTask : taskList) {
				String readFilePath = filePath + File.separator
						+ lowTask.getFilePath();
				if (new File(readFilePath).exists()) {
					TaskData lowLevelTaskData = (TaskData) Util
							.readObjectFromFile(readFilePath);
					taskBorderNodeList.addAll(this.network
							.addTaskData(lowLevelTaskData.network));
				}
			}
			// 处理边界节点
			this.network.makeBorderTopo(taskBorderNodeList);

		}
		// 做标记
		if (TestConServlet.MakeLandmark == true) {
			List<Task> downLevelTasks = Task.getLowerTaskNos(task);
			List<MarkPoint> downAllLand = new ArrayList<MarkPoint>();
			for (Task lowTask : downLevelTasks) {
				String readFilePath = filePath + File.separator
						+ lowTask.getFilePath();
				if (new File(readFilePath).exists()) {
					TaskData lowLevelTaskData = (TaskData) Util.readObjectFromFile(readFilePath);
					if (lowLevelTaskData.landmark.mapsings != null
							&& lowLevelTaskData.landmark.mapsings.size() != 0) {
						lowLevelTaskData.landmark.LandMarkType_list
								.removeAll(lowLevelTaskData.landmark.mapsings);
					}
					downAllLand.addAll(lowLevelTaskData.landmark.LandMarkType_list);
				}
			}
			this.landmark.mapsings = this.landmark.upliftedMarkLevel(downAllLand);
		}
	}

	/**
	 * 各种要素的式样处理
	 */
	public void buildData() {
//		// 二叉路合并
//		this.network.mergerBinaryRoad();
		// 删除没有显示效果的道路
		this.network.deleNoUseRoad();
		//修改ramp显示式样
		this.network.rampRoadStyleHandle();
		// 调整道路形状显示效果
		SmoothHandle sooothRoad = new SmoothHandle(this);
		sooothRoad.deletRoadPoint();
		// this.background.smoothPolygon();
		this.background.converToConvex();
		this.background.mergerBinaryLine();
		this.background.smoothLine();
		// 排版
		// 地标处理
		this.landmark.build();
	}

	/**
	 * 序列化数据对象，升层用
	 * 
	 * @param filePath
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void saveData(String filePath) throws FileNotFoundException,
			IOException {
		String saveFile = filePath + File.separator + this.task.getFilePath();
		Util.saveObjectToFile(saveFile, this);
	}

	public void saveMMData(String filePath) throws FileNotFoundException,
			IOException {
		this.task.level = mmlevel;
		this.saveData(filePath);
	}

	public void buildMMData(String filePath) throws ZipException, IOException,
			ClassNotFoundException {
		String readFilePath = filePath + File.separator
				+ this.task.getFilePath();
		this.network = ((TaskData) Util.readObjectFromFile(readFilePath)).network;
		this.network.spliteRoadByMMBlock();
	}

	// 式样描述
	public static final class VectoryStyle {

		public static final byte SeaArea = 1;// 海域,
		public static final byte ProvinceAdmArea = 2;// 省级行政面
		public static final byte Island = 3;// 岛
		public static final byte SeaFrontier = 4;// 海疆线
		public static final byte SeaBank = 5;// 海岸线
		public static final byte Land = 6;// 绿地、公园
		public static final byte Water = 7;// 水系
		public static final int WaterLine = 99;// 线性水系
		public static final byte AdmaBorderNation = 8;// 行政界（国界）
		public static final byte AdmaBorderUnNation = 9;// 行政界（未定国界）
		public static final byte AdmaBorderProvince = 10;// 行政界（省）
		public static final byte AdmaBorderSpecial = 11;// 特殊边界（当前指港澳区界）
		public static final byte AdmaBorderCity = 12;// 行政界（市）
		public static final byte AdmaBorderCountry = 13;// 行政界（县）
		public static final byte BackGroudRoad = 40;// 背景路
		public static final byte RailWay = 41;// 铁路
		public static final byte Ferry = 50;// 航道
		public static final byte MinorRoad1 = 51;// 细道路1
		public static final byte MinorRoad2 = 52;// 细道路2de
		public static final byte NormalRoad = 53;// 一般道路
		public static final byte CityMinorRoad = 54;// 城市次干路
		public static final byte CityMajorRoad = 55;// 城市主干路
		public static final byte ProcinceRoad = 56;// 省道
		public static final byte CityFreeWay = 57;// 城市快速路
		public static final byte NationalRoad = 58;// 国道
		public static final byte HightWay = 59;// 高速道路
		public static final byte SubwayOne = 70;// 地铁1
		public static final byte SubwayTwo = 71;// 地铁2
		public static final byte SubwayThree = 72;// 地铁3
		public static final byte SubwayFour = 73;// 地铁4
		public static final byte SubwayFive = 74;// 地铁5
		public static final byte SubwaySix = 75;// 地铁6
		public static final byte SubwaySeven = 76;// 地铁7
		public static final byte SubwayEight = 77;// 地铁8
		public static final byte SubwayNine = 78;// 地铁9
		public static final byte SubwayTen = 79;// 地铁10
		public static final byte SubwayEleven = 80;// 地铁11
		public static final byte SubwayTwelve = 81;// 地铁12
		public static final byte SubwayThirteen = 82;// 地铁13
		public static final byte SubwayFourteen = 83;// 地铁14
		public static final byte LightRailOne = 90;// 轻轨1
		public static final byte LightRailTwo = 91;// 轻轨2
		public static final byte LightRailThree = 92;// 轻轨3
		public static final byte Suspension = 110;// 磁悬浮
		// public static final byte Mark = (byte) 200;//记号 //TODO
		public static final int Mark = 200;// 记号

	}
}

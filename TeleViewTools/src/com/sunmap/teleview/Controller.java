package com.sunmap.teleview;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JFileChooser;

import com.sunmap.teleview.element.InformationDataManage;
import com.sunmap.teleview.element.mm.MMManager;
import com.sunmap.teleview.element.mm.data.MMBlockID;
import com.sunmap.teleview.element.mm.data.Road;
import com.sunmap.teleview.element.view.TeleViewManager;
import com.sunmap.teleview.element.view.UnitID;
import com.sunmap.teleview.element.view.data.BlockID;
import com.sunmap.teleview.element.view.data.TeleViewText;
import com.sunmap.teleview.parser.ParserFactory;
import com.sunmap.teleview.thread.DrawThread;
import com.sunmap.teleview.thread.DrawThread.DrawType;
import com.sunmap.teleview.util.AdminCode;
import com.sunmap.teleview.util.AdminCodes;
import com.sunmap.teleview.util.GeoPoint;
import com.sunmap.teleview.util.ToolsUnit;
import com.sunmap.teleview.view.DrawParams;
import com.sunmap.teleview.view.mainfram.Console;
import com.sunmap.teleview.view.mainfram.DrawEleType;
import com.sunmap.teleview.view.mainfram.ImageInfo;
import com.sunmap.teleview.view.mainfram.TabbedPane.TabbedMode;
import com.sunmap.teleview.view.mainfram.TeleToolsFrame;

/**
 * @author lijingru
 *
 */
public class Controller {
	//UI
	public static TeleToolsFrame UI;
	//数据
	public static DrawParams drawParams = new DrawParams();
	public static TeleViewManager telemanage = new TeleViewManager();
	public static MMManager mmManage = new MMManager();
	public static InformationDataManage informationDataManage = new InformationDataManage();
	//支持线程
	public static DrawThread draw;

	public static void main(String[] args) {
		init();
	}

	/**
	 * 初始化
	 */
	private static void init() {
		filePathfromFile();
		drawParams.init();
		telemanage.init();
		mmManage.init();
		UI = new TeleToolsFrame();
		loadUIStatus("./prefer.properties");
		displayScale(drawParams.currMapScale);
		draw  = new DrawThread();
		draw.start();
		draw.putDrawQueue(DrawType.ALL);
	}
	
	/**
	 * 读取文件路径
	 */
	private static void filePathfromFile(){
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream("./app.properties");
			System.getProperties().load(fileInputStream);
			String linuxFile = "linux.teledata.path";
			String windosFile = "windows.teledata.path";
			drawParams.WindowsTeleFile = String.valueOf(System.getProperty(windosFile));//缺省值
			drawParams.LinuxTeleFile = String.valueOf(System.getProperty(linuxFile));//缺省值
			String sysname = System.getProperty("os.name");
			if(sysname.equalsIgnoreCase("linux")){
				telemanage.setViewPath(drawParams.LinuxTeleFile);
				mmManage.setMMPath(drawParams.LinuxTeleFile);
			}else {
				telemanage.setViewPath(drawParams.WindowsTeleFile);
				mmManage.setMMPath(drawParams.WindowsTeleFile);
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		} catch (IOException e1) {
			System.out.println("没有找到app文件");
		}
	}
	
	/**
	
	/**
	 * 保存UI状态到文件中
	 * @param pathString
	 */
	public static void saveUIStatus(String pathString){
		String UIStatue = UI.getCurUIStatus();
		String[] UIStatues = UIStatue.split("\\|");
		String[] uIStrings = UIStatues[0].split(",");
		for (int i = 0; i < uIStrings.length; i++) {
			String[] statusStrings = uIStrings[i].split("=");
			if (statusStrings[0].equals("com.sunmap.teleview.tools.toolsbar")) {
				toFile("com.sunmap.teleview.tools.toolsbar", statusStrings[1], pathString);
			}
			if (statusStrings[0].equals("com.sunmap.teleview.tools.startbar")) {
				toFile("com.sunmap.teleview.tools.startbar", statusStrings[1], pathString);
			}
			if (statusStrings[0].equals("com.sunmap.teleview.tools.info")) {
				toFile("com.sunmap.teleview.tools.info", statusStrings[1], pathString);
			}
			if (statusStrings[0].equals("com.sunmap.teleview.tools.console")) {
				toFile("com.sunmap.teleview.tools.console", statusStrings[1], pathString);
			}
		}
		
		String[] drawEleTypes = UIStatues[1].split(",");
		for (int i = 0; i < drawEleTypes.length; i++) {
			String[] drawEle = drawEleTypes[i].split("=");
			if (drawEle[0].equals("com.sunmap.teleview.look.road")) {
				toFile("com.sunmap.teleview.look.road", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.bg")) {
				toFile("com.sunmap.teleview.look.bg", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.roadname")) {
				toFile("com.sunmap.teleview.look.roadname", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.poi")) {
				toFile("com.sunmap.teleview.look.poi", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.regionline")) {
				toFile("com.sunmap.teleview.look.regionline", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.block")) {
				toFile("com.sunmap.teleview.look.block", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.regionline")) {
				toFile("com.sunmap.teleview.pick.regionline", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.road")) {
				toFile("com.sunmap.teleview.pick.road", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.bg")) {
				toFile("com.sunmap.teleview.pick.bg", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.poi")) {
				toFile("com.sunmap.teleview.pick.poi", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.roadname")) {
				toFile("com.sunmap.teleview.pick.roadname", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.MM")) {
				toFile("com.sunmap.teleview.look.MM", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.MMblock")) {
				toFile("com.sunmap.teleview.look.MMblock", drawEle[1], pathString);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.MM")) {
				toFile("com.sunmap.teleview.pick.MM", drawEle[1], pathString);
			}
		}
		
		String drawPar = drawParams.getCurDrawParams();
		String[] CurDrawParam = drawPar.split(",");
		for (int i = 0; i < CurDrawParam.length; i++) {
			String[] drawParam = CurDrawParam[i].split("=");
			if (drawParam[0].equals("com.sunmap.teleview.map.lon")) {
				toFile("com.sunmap.teleview.map.lon", drawParam[1], pathString);
			}
			if (drawParam[0].equals("com.sunmap.teleview.map.lat")) {
				toFile("com.sunmap.teleview.map.lat", drawParam[1], pathString);
			}
			if (drawParam[0].equals("com.sunmap.teleview.scaleNO")) {
				toFile("com.sunmap.teleview.scaleNO", drawParam[1], pathString);
			}
			if (drawParam[0].equals("windows.teledata.path")) {
				toFile("windows.teledata.path", drawParam[1], pathString);
			}
			if (drawParam[0].equals("linux.teledata.path")) {
				toFile("linux.teledata.path", drawParam[1], pathString);
			}
		}
	}
	
	/**
	 * 设置首选项内容
	 * 
	 * @param key
	 * @param value
	 */
	public static void toFile(String key, String value, String filePath){
		File file = null;
		Properties pro = new Properties();
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			file = new File(filePath);
			file.createNewFile();
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			pro.load(bis);
			FileOutputStream fos = new FileOutputStream(file);
			pro.setProperty(key, value);
			pro.store(fos, null);
			if (fos != null ) {
				fos.close();
			}
			if (bis !=null) {
				bis.close();
			}
			if (fis != null ) {
				fis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从文件中读取UI状态
	 * @param pathString
	 */
	@SuppressWarnings("static-access")
	public static void loadUIStatus(String pathString){
		FileInputStream fileInputStream = null;
		// 获取properties文件中内容
		try {
			fileInputStream = new FileInputStream(pathString);
			System.getProperties().load(fileInputStream);
			String toolsbar= "com.sunmap.teleview.tools.toolsbar";
			String startbar= "com.sunmap.teleview.tools.startbar";
			String info = "com.sunmap.teleview.tools.info";
			String console = "com.sunmap.teleview.tools.console";
			String road_l = "com.sunmap.teleview.look.road";
			String bg_l = "com.sunmap.teleview.look.bg";
			String roadname_l = "com.sunmap.teleview.look.roadname";
			String poi_l = "com.sunmap.teleview.look.poi";
			String region_l = "com.sunmap.teleview.look.regionline";
			String block_l = "com.sunmap.teleview.look.block";
			String region_p = "com.sunmap.teleview.pick.regionline";
			String road_p = "com.sunmap.teleview.pick.road";
			String bg_p = "com.sunmap.teleview.pick.bg";
			String poi_p = "com.sunmap.teleview.pick.poi";
			String roadname_p = "com.sunmap.teleview.pick.roadname";
			String mm_l = "com.sunmap.teleview.look.MM";
			String mmblock_l = "com.sunmap.teleview.look.MMblock";
			String mm_p = "com.sunmap.teleview.pick.MM";
			
			StringBuffer UIStatus = new StringBuffer();
			UIStatus.append(toolsbar).append("=").append(System.getProperty(toolsbar)).append(",");
			UIStatus.append(startbar).append("=").append(System.getProperty(startbar)).append(",");
			UIStatus.append(info).append("=").append(System.getProperty(info)).append(",");
			UIStatus.append(console).append("=").append(System.getProperty(console));
			UIStatus.append("|");
			UIStatus.append(road_l).append("=").append(System.getProperty(road_l)).append(",");
			UIStatus.append(bg_l).append("=").append(System.getProperty(bg_l)).append(",");
			UIStatus.append(roadname_l).append("=").append(System.getProperty(roadname_l)).append(",");
			UIStatus.append(poi_l).append("=").append(System.getProperty(poi_l)).append(",");
			UIStatus.append(region_l).append("=").append(System.getProperty(region_l)).append(",");
			UIStatus.append(block_l).append("=").append(System.getProperty(block_l)).append(",");
			UIStatus.append(region_p).append("=").append(System.getProperty(region_p)).append(",");
			UIStatus.append(road_p).append("=").append(System.getProperty(road_p)).append(",");
			UIStatus.append(bg_p).append("=").append(System.getProperty(bg_p)).append(",");
			UIStatus.append(poi_p).append("=").append(System.getProperty(poi_p)).append(",");
			UIStatus.append(roadname_p).append("=").append(System.getProperty(roadname_p)).append(",");
			UIStatus.append(mm_l).append("=").append(System.getProperty(mm_l)).append(",");
			UIStatus.append(mmblock_l).append("=").append(System.getProperty(mmblock_l)).append(",");
			UIStatus.append(mm_p).append("=").append(System.getProperty(mm_p));
			UI.setCurUIStatus(UIStatus.toString());
			com.sunmap.teleview.element.view.DrawEleType drawViewEleType = new com.sunmap.teleview.element.view.DrawEleType();
			drawViewEleType.isDrawRoad = Boolean.parseBoolean(System.getProperty(road_l));
			drawViewEleType.isDrawBg = Boolean.parseBoolean(System.getProperty(bg_l));
			drawViewEleType.isDrawRoadTextLine = Boolean.parseBoolean(System.getProperty(roadname_l));
			drawViewEleType.isDrawLandMark = Boolean.parseBoolean(System.getProperty(poi_l));
			drawViewEleType.isDrawBlockEdg = Boolean.parseBoolean(System.getProperty(block_l));
			drawViewEleType.isDrawRegionLine = Boolean.parseBoolean(System.getProperty(region_l));
			drawViewEleType.pickRegionLine = Boolean.parseBoolean(System.getProperty(region_p));
			drawViewEleType.pickRoad = Boolean.parseBoolean(System.getProperty(road_p));
			drawViewEleType.pickBg = Boolean.parseBoolean(System.getProperty(bg_p));
			drawViewEleType.pickLandMark = Boolean.parseBoolean(System.getProperty(poi_p));
			drawViewEleType.pickRoadTextLine = Boolean.parseBoolean(System.getProperty(roadname_p));
			com.sunmap.teleview.element.mm.DrawEleType drawMMEleType = new com.sunmap.teleview.element.mm.DrawEleType();
			drawMMEleType.drawMM = Boolean.parseBoolean(System.getProperty(mm_l));
			drawMMEleType.drawMMBlock = Boolean.parseBoolean(System.getProperty(mmblock_l));
			drawMMEleType.pickMM = Boolean.parseBoolean(System.getProperty(mm_p));
			mmManage.setDrawEleType(drawMMEleType);
			telemanage.setDrawEleType(drawViewEleType);
			
			String linuxFile = "linux.teledata.path";
			String windosFile = "windows.teledata.path";
			String lon = "com.sunmap.teleview.map.lon";
			String lat = "com.sunmap.teleview.map.lat";
			String scaleNO = "com.sunmap.teleview.scaleNO";
			StringBuffer drawParam = new StringBuffer();
			drawParam.append(lon).append("=").append(System.getProperty(lon)).append(",");
			drawParam.append(lat).append("=").append(System.getProperty(lat)).append(",");
			drawParam.append(scaleNO).append("=").append(System.getProperty(scaleNO)).append(",");
			drawParam.append(windosFile).append("=").append(System.getProperty(windosFile)).append(",");
			drawParam.append(linuxFile).append("=").append(System.getProperty(linuxFile)).append(",");
			drawParams.setCurDrawParams(drawParam.toString());
			String sysname = System.getProperty("os.name");
			if(sysname.equalsIgnoreCase("linux")){
				telemanage.setViewPath(System.getProperty(linuxFile));
				mmManage.setMMPath(System.getProperty(linuxFile));
			}else {
				telemanage.setViewPath(System.getProperty(windosFile));
				mmManage.setMMPath(System.getProperty(windosFile));
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		} catch (IOException e) {
			System.out.println("没有找到文件、采用缺省值");
		}
	}
	
	public static void combinImage(ImageInfo imageInfo){
		UI.viewPanel.combinImage(imageInfo);
		if(imageInfo != null){
			showStatusInfo(imageInfo.imagePoint);
		}
	}
	
	/**
	 * 移动地图描画
	 * @param point
	 */
	public static void moveMap(Point point){
		drawParams.imageCentre.x = point.x;
		drawParams.imageCentre.y = point.y;
		drawParams.panelCentre.x = point.x;
		drawParams.panelCentre.y = point.y;
		drawParams.updataMapData();//更新描画参数
		saveUIStatus("./prefer.properties");
//		loadUIStatus("./prefer.properties");
		draw.putDrawQueue(DrawType.ALL);
	}
	
	/**
	 * 改变比例尺
	 * @param parame
	 * @param mousePoint
	 */
	public static void changeScale(int parame, Point mousePoint){
		if (parame > 0) {
			scaleDown(mousePoint);
		}else {
			scaleUp(mousePoint);
		}
		saveUIStatus("./prefer.properties");
	}
	
	/**
	 * 加比例尺   显示高层的
	 */
	private static void scaleUp(Point point) {
		
		Point centerPoint = calcUpCenterPoint(point);
		drawParams.setCenterGeo(centerPoint);//设置中心点
		drawParams.panelCentre.x = centerPoint.x;
		drawParams.panelCentre.y = centerPoint.y;
		drawParams.updataMapData();//更新描画参数
		//更新状态栏和工具栏等信息 
		displayScale(drawParams.currMapScale);
		draw.putDrawQueue(DrawType.ALL);
	}
	
	/**
	 * 计算滚轮后（加比例尺）的屏幕中心点
	 * @param point
	 * @return
	 */
	private static Point calcUpCenterPoint(Point point){
		int preScale = drawParams.currMapScale;
		drawParams.scaleUp();//设置比例尺
		int curScale = drawParams.currMapScale;
		float ratioScale = (float) preScale / curScale;
		float x = point.x+(drawParams.imageCentre.x - point.x)/ratioScale;
		float y = point.y+(drawParams.imageCentre.y - point.y)/ratioScale;
		Point centerPoint = new Point((int)x, (int)y);
		return centerPoint;
	}
	
	/**
	 * 计算滚轮后（减比例尺）的屏幕中心点
	 * @param point
	 * @return
	 */
	private static Point calcDownCenterPoint(Point point){
		int preScale = drawParams.currMapScale;
		drawParams.scaleDown();//设置比例尺
		int curScale = drawParams.currMapScale;
		float ratioScale = (float)  curScale/ preScale;
		float x = point.x+(drawParams.imageCentre.x - point.x)*ratioScale;
		float y = point.y+(drawParams.imageCentre.y - point.y)*ratioScale;
		Point centerPoint = new Point((int)x, (int)y);
		return centerPoint;
	}

	/**
	 * 减小比例尺  显示底层的
	 * @param point 
	 */
	public static void scaleDown(Point point) {
		Point centerPoint = calcDownCenterPoint(point);
		drawParams.setCenterGeo(centerPoint);//设置中心点
		drawParams.panelCentre.x = centerPoint.x;
		drawParams.panelCentre.y = centerPoint.y;
		drawParams.updataMapData();//更新描画参数
		//更新状态栏和工具栏等信息 
		displayScale(drawParams.currMapScale);
		draw.putDrawQueue(DrawType.ALL);
	}
	
	/**
	 * 锁层
	 * @param selected
	 */
	public static void lockLayer(boolean selected) {
		if (selected == true) {
			UI.drawEleType.drawMM = false;
			UI.drawEleType.drawMMBlock = false;
			UI.drawEleType.pickMM = false;
		}else {
			UI.drawEleType.drawMM = true;
			UI.drawEleType.drawMMBlock = true;
			UI.drawEleType.pickMM = true;
		}
		saveUIStatus("./prefer.properties");
		loadUIStatus("./prefer.properties");
		telemanage.setLockLevel(selected);
		ParserFactory.getInstance().againParser();
		draw.putDrawQueue(DrawType.ALL);
	}
	
	/**
	 * 设置比例尺的显示
	 * @param scale
	 */
	public static void displayScale(int scale){
		UI.setScale(scale);
	}
	
	
	/**
	 * 位置检索
	 * @param locationX
	 * @param locationY
	 * @return
	 */
	public static boolean locationSearch(int locationX, int locationY){
		moveMap(new Point(locationX, locationY));
		return true;
	}
	
	/**
	 * ViewBlock检索
	 * @param blockLevel
	 * @param X
	 * @param Y
	 * @param flag
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static boolean viewBlockSearch(byte blockLevel, int X, int Y, String flag){
		int datalevel = telemanage.getViewBaseInfo().curDataLevel;
		int x = drawParams.imageCentre.x;
		int y = drawParams.imageCentre.y;
		int level = blockLevel;
		if (flag.equals("block")) {
			BlockID blockID =new BlockID();
			blockID.level =  blockLevel;
			blockID.xIndex =  X;
			blockID.yIndex =  Y;
			x = (int) blockID.getLeft();
			y = (int) blockID.getBottom();
		}else {
			UnitID unitId = new UnitID();
			unitId.level = blockLevel;
			unitId.xIndex = X;
			unitId.yIndex = Y;
			x = (int) unitId.getLeft();
			y = (int) unitId.getBottom();
		}
		if (ToolsUnit.isInChina(x, y) == false) {
			return false;
		}
		if (level != datalevel) {
			if (x != drawParams.imageCentre.x && y != drawParams.imageCentre.y) {
				drawParams.scaleUINO = 15 - telemanage.getViewBaseInfo().levelManage.getZoomindexByDataLevel((byte) level)[0];
				moveMap(new Point(x, y));
			}
		}else{
			if (x != drawParams.imageCentre.x && y != drawParams.imageCentre.y) {
				moveMap(new Point(x, y));
			}
		}
		return true;
	}
	
	/**
	 * 图层改变
	 * @param drawEleType
	 */
	public static void layerChanger(DrawEleType drawEleType){
		UI.drawEleType.setValue(drawEleType);
		saveUIStatus("./prefer.properties");
		loadUIStatus("./prefer.properties");
		ParserFactory.getInstance().againParser();
		draw.putDrawQueue(DrawType.ALL);
	}
	
	/**
	 * MMBlock检索
	 * @param blockLevel
	 * @param blockX
	 * @param blockY
	 * @return
	 */
	public static boolean MMBlockSearch(int blockX, int blockY){
		MMBlockID blockID =new MMBlockID();
		blockID.xIndex =  blockX;
		blockID.yIndex =  blockY;
		int x = (int) blockID.getLeft();
		int y = (int) blockID.getBottom();
		if (ToolsUnit.isInChina(x, y) == false) {
			return false;
		}
		if (x != drawParams.imageCentre.x && y != drawParams.imageCentre.y) {
			drawParams.scaleUINO = 3;
			moveMap(new Point(x, y));
		}
		return true;
	}
	
	/**
	 * landMark检索
	 * @param landMarkName
	 * @param provinceName
	 * @param cityName
	 * @return
	 * @throws SQLException
	 */
	public static boolean landMarkSearch(String landMarkName, String provinceName, String cityName) throws SQLException{
		List<TeleViewText> landMarks = telemanage.selectLandMark(landMarkName, provinceName, cityName);
		if (landMarks.size() == 0) {
			return false;
		}
		informationDataManage.clearPickDraw();
		informationDataManage.clearPick();
		//查询到道路存放到TeleToolsManage
		for (int i = 0; i < landMarks.size(); i++) {
			informationDataManage.listInInfor.add(landMarks.get(i));
		}
		
		if (informationDataManage.listInInfor.size() > 0 ) {
			informationDataManage.makeSelectList();
			setInfoVisible(true);
		}else{
			setInfoVisible(false);
		}
		saveUIStatus("./prefer.properties");
		loadUIStatus("./prefer.properties");
		return true;
	}
	
	/**
	 * 获取当前位置信息
	 * @return
	 */
	public static List<String> getLocation(){
		List<String> locationStrings = new ArrayList<String>();
		locationStrings.add(String.valueOf(drawParams.imageCentre.x));
		locationStrings.add(String.valueOf(drawParams.imageCentre.y));
		return locationStrings;
	}
	
	/**
	 * 获取当前Viewblock信息
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static List<String> getViewBlock(){
		List<String> viewBlockStrings = new ArrayList<String>();
		int level = telemanage.getViewBaseInfo().curDataLevel;
		int x = drawParams.imageCentre.x;
		int y = drawParams.imageCentre.y;
		viewBlockStrings.add(String.valueOf(level));
		BlockID blockID = BlockID.getBlockIDbyGeoPoint((byte) level, new GeoPoint(y, x));
		viewBlockStrings.add(String.valueOf(blockID.xIndex));
		viewBlockStrings.add(String.valueOf(blockID.yIndex));
		UnitID unitid = new UnitID();
		unitid = unitid.getUnitIDbyGeoPoint(new GeoPoint(y, x));
		viewBlockStrings.add(String.valueOf(unitid.xIndex));
		viewBlockStrings.add(String.valueOf(unitid.yIndex));
		return viewBlockStrings;
	}
	
	/**
	 * 获取当前MMBlock信息
	 * @return
	 */
	public static List<String> getMMBlock(){
		List<String> MMBlockStrings = new ArrayList<String>();
		int x = drawParams.imageCentre.x;
		int y = drawParams.imageCentre.y;
		MMBlockID mmBlockID = MMBlockID.getMMBlockIDbyGeoPoint(new GeoPoint(y, x));
		MMBlockStrings.add(String.valueOf(mmBlockID.xIndex));
		MMBlockStrings.add(String.valueOf(mmBlockID.yIndex));
		return MMBlockStrings;
	}
	
	/**
	 * 获取当前城市信息
	 * @return
	 * @throws SQLException 
	 */
	public static List<String> getCity() throws SQLException{
		List<String> cityStrings = new ArrayList<String>();
		//不在中国范围
		if(ToolsUnit.isInChina(drawParams.imageCentre.x, drawParams.imageCentre.y) == false){
			return cityStrings;
		}
		//通过坐标取得当前城市
		AdminCode adminCode = AdminCodes.getCityByCode(drawParams.imageCentre.x, drawParams.imageCentre.y);
		if (adminCode.getProvince() == null) {
			return cityStrings;
		}
		cityStrings.add(adminCode.getProvince());
		if (adminCode.getCity()!= null) {
			cityStrings.add(adminCode.getCity());
		}
		return cityStrings;
		
	}
	
	/**
	 * 计算拾取信息
	 * @param point
	 */
	public static void calcPickUpInfo(Point point){
		informationDataManage.clearPickDraw();
		informationDataManage.clearPick();
		int dis = 5;
		Map<List<Object>, Double> viewPickUpInfo = telemanage.calcPickUpInfo(point, dis);
		Map<List<Object>, Double> MMPickUpInfo = mmManage.calcPickUpInfo(point, dis);
		List<Object> pickUpInfos = getSortPickInfo(viewPickUpInfo, MMPickUpInfo);
		informationDataManage.listInInfor.addAll(pickUpInfos);
		if (informationDataManage.listInInfor.size() > 0 ) {
			informationDataManage.makeList();
			setInfoVisible(true);
		}else{
			setInfoVisible(false);
		}
		draw.putDrawQueue(DrawType.Special);
	}
	
	private static List<Object> getSortPickInfo( Map<List<Object>, Double> viewPickUpInfo, Map<List<Object>, Double> mMPickUpInfo) {
		Map<List<Object>, Double> pickresultMap = new HashMap<List<Object>, Double>();
		pickresultMap.putAll(viewPickUpInfo);
		pickresultMap.putAll(mMPickUpInfo);
		List<Object> pickUpInfos = new ArrayList<Object>();
		if (pickresultMap.size() == 0) {
			return pickUpInfos;
		}
		List<Map.Entry<List<Object>, Double>> resultList = new ArrayList<Map.Entry<List<Object>,Double>>(pickresultMap.entrySet());
		Collections.sort(resultList, new Comparator<Map.Entry<List<Object>, Double>>() {
			@Override
			public int compare(Entry<List<Object>, Double> left,
					Entry<List<Object>, Double> right) {
				if (left.getValue() - right.getValue() > 0) {
					return 1;
				}
				if (left.getValue() - right.getValue() < 0) {
					return -1;
				}
				return 0;
			}
		});
		for (int i = 0; i < resultList.size(); i++) {
			pickUpInfos.addAll(resultList.get(i).getKey());
		}
		return pickUpInfos;
	}

	/**
	 * 拾取MM后node的链接道路
	 * @param titleString 
	 */
	public static void pickMMNodeLinkRoad(String valueString) {
		List<Road> roads = new ArrayList<Road>();
		valueString = valueString.trim().split(":")[1];
		String[] nodeInfo = valueString.trim().split("\\|");
		for (int i = 0; i < nodeInfo.length; i++) {
			String[] roadInfo = nodeInfo[i].split(",");
			int roadIndex = Integer.parseInt(roadInfo[0]);
			int blockIndexX = Integer.parseInt(roadInfo[1]);
			int blockIndexY = Integer.parseInt(roadInfo[2]);
			Road road = mmManage.getNodeLinkRoad(roadIndex, blockIndexX, blockIndexY);
			if (road != null) {
				roads.add(road);
			}
		}
		informationDataManage.setDrawNodeLinkRoad(roads);
		draw.putDrawQueue(DrawType.Special);
	}
	
	/**
	 * 打开tele数据位置
	 */
	public static void openFile() {
		String file = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		//Tele文件这个过滤器。  
		fileChooser.setAcceptAllFileFilterUsed(false);  
		ViewFileFilter fileFilter = new ViewFileFilter();
		fileChooser.setFileFilter(fileFilter);
		
		if (fileChooser.showOpenDialog(UI) == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile().getParent();
		}
		//保存路径
		if (file != null) {
			String sysname = System.getProperty("os.name");
			if(sysname.equalsIgnoreCase("linux")){
				drawParams.LinuxTeleFile = file;
				telemanage.setViewPath(file);
				toFile("linux.teledata.path", file, "./prefer.properties");
			}else {
				drawParams.WindowsTeleFile = file;
				mmManage.setMMPath(file);
				toFile("windows.teledata.path", file, "./prefer.properties");
			}
			clearData();
			ParserFactory.getInstance().againParser();
			Console.getInstance().setTxt("打开路径为" + file + "的View文件成功！");
			//触发描画
			draw.putDrawQueue(DrawType.ALL);
		}
		
	}
	
	private static class ViewFileFilter extends javax.swing.filechooser.FileFilter{

		public boolean accept(File file) {
			if (file.isDirectory()){
				return true;
			}
			if (file.getName().equals("view") || file.getName().equals("mm")) {
				return true;
			}
			return false;
		}

		public String getDescription() {
			return "Tele文件";
		}
	}
	
	/**
	 * 清理数据
	 */
	private static void clearData() {
		telemanage.clearViewData();
		mmManage.clearMMData();
	}
	
	/**
	 * 显示鼠标位置和层号
	 * @param point
	 */
	@SuppressWarnings("static-access")
	public static void showStatusInfo(Point point){
		//修改信息内容
		DecimalFormat df = new DecimalFormat("#.00000000");// 格式化显示
		String s = "经纬度： "  + df.format(point.x / 2560.0 / 3600.0) + "     "
				+ df.format(point.y / 2560.0 / 3600.0) + "           1/2560经纬度： " + point.x + "     " + point.y;
		UI.stateBar.setTxt_centerPoint(s);
		int level = telemanage.getViewBaseInfo().curDataLevel;
		int curscale = telemanage.getViewBaseInfo().curDataScale;
		String blockId = BlockID.getBlockIDbyGeoPoint((byte) level, new GeoPoint(point.y,point.x)).getURL();
		UnitID unitID = new UnitID();
		String unitId   = unitID.getUnitIDbyGeoPoint(new GeoPoint( point.y,point.x)).getKey();
		String str  = " 信息：" +" view level :  "+level+"  scale :" + curscale
				+"     blockId:"+blockId  +"     unitId:"+unitId ;
		UI.stateBar.setTxt_Info(str);
	}
	
	public static void setInfoVisible(boolean selected){
		UI.menuBar.setTabbedPaneShow(selected,TabbedMode.Info);
		UI.tabbedPane.setTabbedVisible(selected,TabbedMode.Info);
		displayScale(drawParams.currMapScale);
		saveUIStatus("./prefer.properties");
		loadUIStatus("./prefer.properties");
		combinImage(null);
	}
	
	public static void setConsoleVisible(boolean selected){
		UI.menuBar.setTabbedPaneShow(selected,TabbedMode.Console);
		UI.tabbedPane.setTabbedVisible(selected,TabbedMode.Console);	
		displayScale(drawParams.currMapScale);
		saveUIStatus("./prefer.properties");
		loadUIStatus("./prefer.properties");
		combinImage(null);
	}
	

}

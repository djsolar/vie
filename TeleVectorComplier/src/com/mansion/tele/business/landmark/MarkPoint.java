package com.mansion.tele.business.landmark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.Task;
import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.landmark.Style.Brand;
import com.mansion.tele.business.landmark.Style.DisPlay;
import com.mansion.tele.business.viewdata.Landmark;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.ConstantValue;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.NumberUtil;
import com.mansion.tele.util.PolygonUtil;
import com.mansion.tele.util.Util;

public class MarkPoint implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8354260682992012785L;

	// telecode
	int telecode;
	// 品牌
	Brand brand;
	// 名称
	public String name;
	// 库中ID标识
	String strid;
	// 中心点位置x
	int x;
	// 中心点位置y
	int y;
	// 原始emg数据id
	String strshpid;
	// 是地标类型数据
	boolean isLandMark;
	// 显示样式 0:不显示文字；1：只显示文字；2：文字在icon中心显示；3：文字在icon外面显示；4：线性文字
	transient DisPlay display;
	// 优先级
	transient int pri;
	// 文字大小
	transient double font;
	// 文字横向空白
	transient double xSpace;
	// 文字纵向空白
	transient double ySpace;
	// BlockX编号
	transient int iBlockX;
	// BlockY编号
	transient int iBlockY;
	// 父对象
	MarkPoint landMarkType;
	// 空间索引坐标
	transient private int index_X;
	// 空间索引坐标
	transient private int index_Y;
	// 文字排版框
	transient List<MarkPointRect> ableStations;
	// icon排版框
	transient MarkPointRect iconRect;
	// 线形状坐标点集合
	transient List<ShpPoint> line;
	// 线性坐标矩形框
	transient List<GeoRect> eyeoneRectlist;
	// 因为有高层显示底层不显示的地标，所以都为false时不存储到格式中
	public transient boolean bvs[] = { false, false, false };
	// 表示该文字是否被排版过
	public transient boolean[] indexivs = {false,false,false};
	// 是否是道路名,用来在排版时判断
	public transient boolean isRoadName = false;
	//文字间距
	transient double letterSpacing;
	// 下个比例尺是否显示
	public transient boolean[] nextScaleShow = {false,false,false};
	// 特殊处理标识
	public transient boolean isSpecial = false;
//	/**
//	 * 记号类型文字矩形框样式
//	 * 
//	 * @author wxc
//	 * 
//	 */
//	class Station {
//		SignTypeSetRect laststion;
//		List<SignTypeSetRect> ableStations;
//	}

	public MarkPoint() {

	}

	/**
	 * 构造地标数据
	 * 
	 * @param telecode
	 * @param name
	 * @param strid
	 * @param x
	 * @param y
	 */
	public MarkPoint(int telecode, String name, String strid, int x, int y, String strshpid) {
		this.telecode = telecode;
		this.name = name;
		this.strid = strid;
		this.x = x;
		this.y = y;
		this.strshpid = strshpid;
	}
	
	/**
	 * 构造地标数据带有矩形框
	 * 
	 * @param telecode
	 * @param name
	 * @param strid
	 * @param x
	 * @param y
	 */
	public MarkPoint(int telecode, String name, String strid, int x, int y, String strshpid,
			List<MarkPointRect> rects) {
		this.telecode = telecode;
		this.name = name;
		this.strid = strid;
		this.x = x;
		this.y = y;
		this.strshpid = strshpid;
		this.ableStations = rects;
	}

	/**
	 * 构造地标数据带有品牌
	 * 
	 * @param telecode
	 * @param name
	 * @param strid
	 * @param x
	 * @param y
	 */
	public MarkPoint(int telecode, String name, String strid, int x, int y,
			Brand brand, String strshpid) {
		this.telecode = telecode;
		this.name = name;
		this.strid = strid;
		this.x = x;
		this.y = y;
		this.brand = brand;
		this.strshpid = strshpid;
	}

	/**
	 * 构造地标数据并且带有父类
	 * 
	 * @param telecode
	 * @param name
	 * @param strid
	 * @param x
	 * @param y
	 * @param landMarkType
	 */
	public MarkPoint(int telecode, String name, String strid, int x, int y,
			MarkPoint landMarkType, String strshpid) {
		this.telecode = telecode;
		this.name = name;
		this.strid = strid;
		this.x = x;
		this.y = y;
		this.landMarkType = landMarkType;
		this.strshpid = strshpid;
	}

	/**
	 * 构造地标数据并且带有父类以及品牌
	 * 
	 * @param telecode
	 * @param name
	 * @param strid
	 * @param x
	 * @param y
	 * @param landMarkType
	 */
	public MarkPoint(int telecode, String name, String strid, int x, int y,
			MarkPoint landMarkType, Brand brand, String strshpid) {
		this.telecode = telecode;
		this.name = name;
		this.strid = strid;
		this.x = x;
		this.y = y;
		this.landMarkType = landMarkType;
		this.brand = brand;
		this.strshpid = strshpid;
	}

	/**
	 * 构造线型地标数据
	 * 
	 * @param telecode
	 * @param name
	 * @param strid
	 * @param x
	 * @param y
	 */
	public MarkPoint(int telecode, String name, String strid, int x, int y,
			List<ShpPoint> line, List<MarkPointRect> rects) {
		this.telecode = telecode;
		this.name = name;
		this.strid = strid;
		this.x = x;
		this.y = y;
		this.line = line;
		this.ableStations = rects;
	}

	/**
	 * 得到icon大小
	 * 
	 * @param task
	 * @param iScaleNo
	 * @return
	 */
	private int[] getIconSize(int level, int iScaleNo, int oneLonToMe, int oneLatToMe) {
		double width = this.font;
		// 默认值
		if(width == 0){
			width = 1.875;
		}
		long scaleValue = DataManager.getLevelInfo(level).scales[iScaleNo];
		double realwidth = scaleValue * width / 10;
		int coordinatesW = (int) (realwidth * ConstantValue.DEGREETOSECOND
				/ oneLonToMe + 1);
		int coordinatesH = (int) (realwidth * ConstantValue.DEGREETOSECOND
				/ oneLatToMe + 1);
		return new int[]{coordinatesW, coordinatesH};
	}

	/**
	 * 获得icon最大的矩形框
	 * 
	 * @return
	 */
//	public GeoRect getMaxGeoRectBySign() {
//		int maxRectminlon = signRect.left;
//		int maxRectminlat = signRect.bottom;
//		int maxRectmaxlon = signRect.right;
//		int maxRectmaxlat = signRect.top;
//		if (this.station != null && this.station.ableStations != null
//				&& this.station.ableStations.size() != 0) {
//			for (int i = 0; i < this.station.ableStations.size(); i++) {
//				GeoLocation lb = GeoLocation.valueOf(
//						this.station.ableStations.get(i).geoRects.left,
//						this.station.ableStations.get(i).geoRects.bottom);
//				GeoLocation rt = GeoLocation.valueOf(
//						this.station.ableStations.get(i).geoRects.right,
//						this.station.ableStations.get(i).geoRects.top);
//				if (lb.getiLongitude() < maxRectminlon) {
//					maxRectminlon = lb.getiLongitude();
//				}
//				if (lb.getiLatitude() < maxRectminlat) {
//					maxRectminlat = lb.getiLatitude();
//				}
//				if (rt.getiLongitude() > maxRectmaxlon) {
//					maxRectmaxlon = rt.getiLongitude();
//				}
//				if (rt.getiLatitude() > maxRectmaxlat) {
//					maxRectmaxlat = rt.getiLatitude();
//				}
//			}
//			return GeoRect.valueOf(new ShpPoint(maxRectminlon, maxRectminlat),
//					new ShpPoint(maxRectmaxlon, maxRectmaxlat));
//		} else {
//			return signRect;
//		}
//	}

	/**
	 * 创建地标排版矩形框
	 */
	public void createRectBySign(int level, int iScale, int lonToMe, int latToMe) {
		int[] iconSize = getIconSize(level, iScale, lonToMe, latToMe);
		int[] letterSpacing = calcLetterSpacing(level, iScale, lonToMe, latToMe);
		int[] whsize = calcTxtWithAndHigh(level, iScale, lonToMe, latToMe, letterSpacing);
		int[] spaceSize = calcSpaceValue(level, iScale, lonToMe, latToMe);
		int width = whsize[0];
		int hight = whsize[1];
		if ((width == 0 && hight == 0) || name == null) {
			this.ableStations = new ArrayList<MarkPointRect>();
			MarkPointRect[] laststation = makeTypeSettingRectByStation(iconSize, width, hight, 5, spaceSize);
			this.ableStations.add(laststation[0]);
			this.iconRect = laststation[1];
			return;
		}
		if (this.ableStations != null && this.ableStations.size() > 0
				&& (bvs[0] || bvs[1] || bvs[2])) {
			MarkPointRect[] laststation = makeTypeSettingRectByStation(iconSize, width, hight,this.ableStations.get(0).stations,spaceSize);
			this.ableStations = new ArrayList<MarkPointRect>();
			this.ableStations.add(laststation[0]);
			this.iconRect = laststation[1];
		} else {
			this.ableStations = new ArrayList<MarkPointRect>();
			if (Style.DisPlay.centerShow.equals(this.display)) {
				MarkPointRect[] laststation = makeTypeSettingRectByStation(iconSize, width, hight, 5, spaceSize);
				this.ableStations.add(laststation[0]);
				this.iconRect = laststation[1];
			} else {
				for (int i = 1; i < 5; i++) {
					MarkPointRect[] laststation = makeTypeSettingRectByStation(iconSize, width, hight, i, spaceSize);
					this.ableStations.add(laststation[0]);	
					this.iconRect = laststation[1];
				}
			}
		}
	}

	/**
	 * 计算文字宽高
	 * @param iScale
	 * @param letterSpacing 
	 * @param task
	 * @return
	 */
	public int[] calcTxtWithAndHigh(int level, int iScale, int lonToMe, int latToMe, int[] letterSpacing){
		int[] oneFont = this.getOneFontSize(level, iScale, lonToMe, latToMe);
		int width = oneFont[0];
		int hight = oneFont[1];
		int xLetSpace = letterSpacing[0];
		int yLetSpace = letterSpacing[1];
		if(name == null){
			return new int[]{0,0};
		}
		if (name.length() > 6) {
			if(name.length() % 2 != 0){
				width = (name.length() / 2  + 1) * width + (name.length() / 2) * xLetSpace;
			}else{
				width = name.length() / 2 * width + (name.length() / 2) * xLetSpace;
			}
			hight = (int) (2 * hight + yLetSpace);
		} else {
			width = name.length() * width + (name.length() - 1) * xLetSpace;
		}
		return new int[]{width, hight};
	}
	/**
	 * 通过显示位置制作矩形框
	 * @param iconSize
	 * @param width
	 * @param hight
	 * @param station
	 * @return
	 */
	private MarkPointRect[] makeTypeSettingRectByStation(int[] iconSize, int width,
			int hight, int station, int[] space) {
		// 文字框
		MarkPointRect textRect = null;
		// icon框
		MarkPointRect iconRect = null;
		MarkPointRect[] result = new MarkPointRect[2];  
		int left =0;
		int down = 0;
		int right = 0;
		int top = 0;
		int xspace = space[0];
		int yspace = space[1];
		switch (station) {
		// 不显示icon，只显示文字
		case 0:
			left = (int) (x - width / 2.0);
			down = (int) (y - hight / 2.0);
			right = (int) (x + width / 2.0);
			top = (int) (y + hight / 2.0);
			textRect = new MarkPointRect(GeoRect.valueOf(
					new ShpPoint(left - xspace, down - yspace), new ShpPoint(
							right + xspace, top + yspace)), (byte)0);
			break;
		// 文字在icon右侧
		case 1:
			left = (int) (x - iconSize[0] / 2.0);
			down = (int) (y - hight / 2.0);
			right = (int) (x + iconSize[0] / 2.0 + width);
			top = (int) (y + hight / 2.0);
			textRect = new MarkPointRect(GeoRect.valueOf(
					new ShpPoint(left + iconSize[0], down - yspace), new ShpPoint(
							right + xspace, top + yspace)), (byte) 1);
			iconRect = new MarkPointRect(GeoRect.valueOf(
					new ShpPoint((int)(x - iconSize[0]/2.0 - xspace), (int)(y - iconSize[1]/2.0 - yspace)),
					new ShpPoint((int)(x + iconSize[0]/2.0 + xspace), (int)(y + iconSize[1]/2.0 + yspace))));
			break;
		// 文字在icon左侧	
		case 2:
			left = (int) (x - iconSize[0] / 2.0 - width);
			down = (int) (y - hight / 2.0);
			right = (int) (x + iconSize[0] / 2.0);
			top = (int) (y + hight / 2.0);
			textRect = new MarkPointRect(GeoRect.valueOf(
					new ShpPoint(left - xspace,  down - yspace),
					new ShpPoint(right - iconSize[0], top + yspace)), (byte) 2);
			iconRect = new MarkPointRect(GeoRect.valueOf(
					new ShpPoint((int)(x - iconSize[0]/2.0 - xspace), (int)(y - iconSize[1]/2.0 - yspace)),
					new ShpPoint((int)(x + iconSize[0]/2.0 + xspace), (int)(y + iconSize[1]/2.0 + yspace))));
			break;
		// 文字在icon上	
		case 3:
			left = (int) (x - width / 2.0);
			down = (int) (y - iconSize[1] / 2.0);
			right = (int) (x + width / 2.0);
			top = (int) (y + hight + iconSize[1] / 2.0);
			textRect = new MarkPointRect(GeoRect.valueOf(
					new ShpPoint(left - xspace,  down + iconSize[1]),
					new ShpPoint(right + xspace, top + yspace)),(byte) 3);
			iconRect = new MarkPointRect(GeoRect.valueOf(
					new ShpPoint((int)(x - iconSize[0]/2.0 - xspace), (int)(y - iconSize[1]/2.0 - yspace)),
					new ShpPoint((int)(x + iconSize[0]/2.0 + xspace), (int)(y + iconSize[1]/2.0 + yspace))));
			break;
		// 文字在icon下	
		case 4:
			left = (int) (x - width / 2.0);
			down = (int) (y - iconSize[1] / 2.0 - hight);
			right = (int) (x + width / 2.0);
			top = (int) (y + iconSize[1] / 2.0);
			textRect = new MarkPointRect(GeoRect.valueOf(
					new ShpPoint(left - xspace,  down - yspace),
					new ShpPoint(right + xspace, top - iconSize[1])),(byte) 4);
			iconRect = new MarkPointRect(GeoRect.valueOf(
					new ShpPoint((int)(x - iconSize[0]/2.0 - xspace), (int)(y - iconSize[1]/2.0 - yspace)),
					new ShpPoint((int)(x + iconSize[0]/2.0 + xspace), (int)(y + iconSize[1]/2.0 + yspace))));
			break;
		// 文字在icon中	
		case 5:
			textRect = new MarkPointRect(GeoRect.valueOf(
					new ShpPoint(x - iconSize[0] - xspace, y - iconSize[1] - yspace),
					new ShpPoint(x + iconSize[0] + xspace, y + iconSize[1] + yspace)),
					(byte) 5);
			break;
		default:
			break;
		}
		result[0] = textRect;
		result[1] = iconRect; 
		return result;
	}

	/**
	 * 记号类型创建矩形框通过给定的位置
	 */
	public void createRectBySignByStation(int level, int iScale, int state, int lonToMe, int latToMe) {
		int[] iconSize = getIconSize(level, iScale, lonToMe, latToMe);
		int[] letterSpacing = calcLetterSpacing(level, iScale, lonToMe, latToMe);
		int[] whsize = calcTxtWithAndHigh(level, iScale, lonToMe, latToMe, letterSpacing);
		int[] spaceSize = calcSpaceValue(level, iScale, lonToMe, latToMe);
		int width = whsize[0];
		int hight = whsize[1];
		if ((width == 0 && hight == 0) || name == null) {
			MarkPointRect[] laststation = makeTypeSettingRectByStation(iconSize, width, hight, 5, spaceSize);
			this.ableStations = new ArrayList<MarkPointRect>();
			this.ableStations.add(laststation[0]);
			this.iconRect = laststation[1];
			return;
		}
		this.ableStations = new ArrayList<MarkPointRect>();
		if (Style.DisPlay.centerShow.equals(this.display)) {
			MarkPointRect[] laststation = makeTypeSettingRectByStation(iconSize, width, hight, 5, spaceSize);
			this.ableStations.add(laststation[0]);
			this.iconRect = laststation[1];
		} else {
			MarkPointRect[] laststation = makeTypeSettingRectByStation(iconSize, width, hight, state, spaceSize);
			this.ableStations.add(laststation[0]);
			this.iconRect = laststation[1];
		}
	}

	private int[] calcSpaceValue(int level, int iScale, int lonToMe, int latToMe) {
		if (Style.DisPlay.notShowTxt.equals(this.display)) {
			return new int[] { 0, 0 };
		}
		double width = this.xSpace;
		double high = this.ySpace;
		long scaleValue = DataManager.getLevelInfo(level).scales[iScale];
		double realwidth = scaleValue * width / 10;
		double realhigh = scaleValue * high / 10;
		int coordinatesW = 0;
		int coordinatesY = 0;
		if (realwidth != 0 && realhigh != 0) {
			coordinatesW = (int) (realwidth * ConstantValue.DEGREETOSECOND
					/ lonToMe + 1);
			coordinatesY = (int) (realhigh * ConstantValue.DEGREETOSECOND
					/ latToMe + 1);
		}
		return new int[] { coordinatesW, coordinatesY };
	}

	/**
	 * 与道路比较
	 * 
	 * @param segment
	 */
	public void composeRoadSegment(List<ShpPoint[]> segment) {
		// 压盖则删除
		int length = this.ableStations.size();
		List<MarkPointRect> removeRects = new ArrayList<MarkPointRect>();
		for (int i = 0; i < length;) {// 遍历mark矩形块
			MarkPointRect rect = this.ableStations.get(i);
			int minX = rect.geoRects.left;
			int minY = rect.geoRects.bottom;
			int maxX = rect.geoRects.right;
			int maxY = rect.geoRects.top;
			for (ShpPoint[] shpPoints : segment) {// 与道路进行比较压盖关系
				ShpPoint firstPoint = shpPoints[0];
				ShpPoint secondPoint = shpPoints[1];
				boolean cross = GeoRect.isLineCrossRect(firstPoint.x,
						firstPoint.y, secondPoint.x, secondPoint.y, minX, minY,
						maxX, maxY);
				if (cross) {
					// iRect为被压盖类型
					removeRects.add(this.ableStations.remove(i));
					break;
				}
			}
			if (length == this.ableStations.size()) {
				i++;
			}
			length = this.ableStations.size();
		}
		if (this.ableStations != null
				&& this.ableStations.size() == 0
				&& removeRects.size() != 0) {
			this.ableStations = removeRects;
		}
	}

	/**
	 * 转换格式
	 * 
	 * @return
	 */
	public Landmark changeToFormBySign() {
		int ivs = 0;
		for (int i = 0; i < bvs.length; i++) {
			if (bvs[i]) {
				ivs = ivs + NumberUtil.IVS[i];
			}
		}
		Landmark iconmark = new Landmark();
		iconmark.code = com.mansion.tele.business.landmark.Landmark.style
				.getUserCode(this.telecode);
		iconmark.iPri = this.pri;
		iconmark.ivs = ivs;
		if (this.display.equals(Style.DisPlay.notShowTxt)) {
			iconmark.markTxtStation = 0;
		} else {
			iconmark.markTxtStation = this.ableStations.get(0).stations;
		}
		iconmark.station = this.getBasicPoint();
		iconmark.txtname = this.name;
		iconmark.blockNo = this.getBlock();
		iconmark.txtoricon = false;
		return iconmark;
	}

	/**
	 * 地标名称处理
	 */
	public MarkPoint getName(MarkPoint landMarkType, Task task) {
		if (landMarkType.name == null) {
			return landMarkType;
		}
//		// 公交名称全角转半角
//		if (landMarkType.telecode == NumberUtil.busOrgCode) {
//			landMarkType.name = Util.stringToBSBC(Util
//					.getNoBrackets(landMarkType.name));
//		}
		// 地铁出入口名称去掉“出口”“入口”“出入口”，名称半角转全角
		else if (this.telecode == NumberUtil.subwayEntranceOrgCode) {
			landMarkType.name = Util.getSubwayEntranceName(landMarkType.name);
			landMarkType.name = Util.stringToSBC(landMarkType.name);
		}
		// 地铁站名称半角转全角
		else if (landMarkType.telecode == NumberUtil.SubwayStopOrgCode) {
			landMarkType.name = Util.stringToSBC(landMarkType.name);
		}
		// 道路阿拉伯数据转汉字
		else if (landMarkType.telecode <= 10) {
			landMarkType.name = Util.convertRoadName(landMarkType.name);
		}
		// 五层以上仅有省会名，将名称"中市"去除
		else if (this.display.equals(Style.DisPlay.otherShow)
				&& 5 < task.getLevel()
				&& (ConstantValue.ORGCODEOFPROVINCIALCAPITAL == this.telecode
				|| ConstantValue.ORGCODEOFCAPITAL == this.telecode || ConstantValue.ORGCODEOFDIRECTCITY == this.telecode)) {
			landMarkType.name = Util.FiveCityDelete(landMarkType.name,
					landMarkType.telecode, task);
		}
		// 其他地标名称使用简称
		else {
			landMarkType.name = Util.predigestMarkName(landMarkType.name);
		}
		return landMarkType;
	}
//
//	/**
//	 * 文字创建矩形框
//	 * 
//	 * @param task
//	 * @param iScaleNo
//	 */
//	public void createRectByTxt(Task task, int iScaleNo) {
//
//		int[] wh = this.getOneFontSize(task.getLevel(), iScaleNo);
//		int width = wh[0];
//		int hight = wh[1];
//		if (name.length() > 6) {
//			width = 7 * width;
//			hight = 3 * hight;
//		} else {
//			width = (name.length() + 1) * width;
//		}
//		this.maxRect = GeoRect.valueOf(new ShpPoint(x - width / 2, y - hight
//				/ 2), new ShpPoint(x + width / 2, y + hight / 2));
//	}

	/**
	 * 文字类型格式转换
	 * 
	 * @return
	 */
	public Landmark changeToFormByTxt() {
		int ivs = 0;
		for (int i = 0; i < bvs.length; i++) {
			if (bvs[i]) {
				ivs = ivs + NumberUtil.IVS[i];
			}
		}
		Landmark txtmark = new Landmark();
		txtmark.code = com.mansion.tele.business.landmark.Landmark.style
				.getUserCode(this.telecode);
		txtmark.iPri = this.pri;
		txtmark.ivs = ivs;
		txtmark.station = this.getBasicPoint();
		txtmark.txtname = this.name;
		txtmark.blockNo = this.getBlock();
		txtmark.txtoricon = true;
		return txtmark;
	}

	/**
	 * 获得道路名称所在的矩形范围
	 * 
	 * @return
	 */
	public GeoRect getLineGeoRect() {
			if (line != null && line.size() > 0) {
				ShpPoint lb = new ShpPoint();
				ShpPoint rt = new ShpPoint();
				lb.convert(line.get(0));
				rt.convert(line.get(0));
				for (int i = 0; i < line.size(); i++) {
					ShpPoint shpPoint = line.get(i);
					if (shpPoint.x < lb.x) {
						lb.x = shpPoint.x;
					}
					if (shpPoint.y < lb.y) {
						lb.y = shpPoint.y;
					}
					if (shpPoint.x > rt.x) {
						rt.x = shpPoint.x;
					}
					if (shpPoint.y > rt.y) {
						rt.y = shpPoint.y;
					}
				}
				return  GeoRect.valueOf(lb, rt);
			}
			return null;
	}

	/**
	 * 获得线路类型的矩形框
	 * 
	 * @return
	 */
	public List<GeoRect> getEyeoneRectlist() {
		if (eyeoneRectlist == null || eyeoneRectlist.isEmpty()) {
			eyeoneRectlist = new ArrayList<GeoRect>();
			List<ShpPoint> textPointList = new ArrayList<ShpPoint>();
			double length = 0;
			for (int i = 0; i < line.size() - 1; i++) {
				length += PolygonUtil.twoPointDistance(line.get(i),
						line.get(i + 1));
			}
			double rectWidth = length / name.length() / 2;
			double distance = 0;
			ShpPoint currentPoint = line.get(0);
			textPointList.add(currentPoint);
			for (int i = 1; i < line.size();) {
				double dx = PolygonUtil.twoPointDistance(currentPoint,
						line.get(i));
				distance += dx;
				if (distance > rectWidth) {// 截点
					currentPoint = creatInsertPoint(dx, distance - rectWidth,
							currentPoint, line.get(i));
					textPointList.add(currentPoint);
					distance = 0;
				} else {
					currentPoint = line.get(i);
					i++;
				}
			}
			for (int i = 0; i < textPointList.size() - 1; i++) {
				int minX = Math.min(textPointList.get(i).x,
						textPointList.get(i + 1).x);
				int minY = Math.min(textPointList.get(i).y,
						textPointList.get(i + 1).y);
				int maxX = Math.max(textPointList.get(i).x,
						textPointList.get(i + 1).x);
				int maxY = Math.max(textPointList.get(i).y,
						textPointList.get(i + 1).y);
				GeoRect geoRect = GeoRect.valueOf(new ShpPoint(minX, minY),
						new ShpPoint(maxX, maxY));
				eyeoneRectlist.add(geoRect);
			}
		}
		return eyeoneRectlist;
	}

	/**
	 * 创建插入点
	 * 
	 * @param twoPointDistance
	 * @param distance
	 * @param left
	 * @param right
	 * @return
	 */
	private ShpPoint creatInsertPoint(double twoPointDistance, double distance,
			ShpPoint left, ShpPoint right) {
		if (twoPointDistance == 0) {
			return right;
		}
		int x = (int) (distance * (left.x - right.x) / twoPointDistance + right.x);
		int y = (int) (distance * (left.y - right.y) / twoPointDistance + right.y);
		return new ShpPoint(x, y);
	}

	/**
	 * 路线格式转换
	 * 
	 * @return
	 */
	public Landmark changeToFormByLine() {
		Landmark txtmark = new Landmark();
		int ivs = 0;
		for (int i = 0; i < bvs.length; i++) {
			if (bvs[i]) {
				ivs = ivs + NumberUtil.IVS[i];
			}
		}
		txtmark.ivs = ivs;
		txtmark.code = com.mansion.tele.business.landmark.Landmark.style
				.getUserCode(this.telecode);
		txtmark.iPri = this.pri;
		txtmark.station = this.getBasicPoint();
		txtmark.txtname = this.name;
		txtmark.iCoorcount = this.line.size() + 1;
		txtmark.list = this.line;
		txtmark.blockNo = this.getBlock();
		txtmark.txtoricon = true;
		return txtmark;
	}

	/**
	 * 地标升层处理
	 */
	public List<MarkPoint> upLevel(MarkPoint landMark, Task task,
			List<MarkPoint> landMarkType) {
		if (com.mansion.tele.business.landmark.Landmark.style.isShowByLevel(
				landMark.telecode, task.getLevel())) {
				landMark.bvs = new boolean[] { false, false, false };
				landMark.indexivs = new boolean[] { false, false, false };
				landMark.nextScaleShow = new boolean[] {false,false,false};
//				MarkPoint landMarkType2 = (MarkPoint) landMark.clone();
				landMarkType.add(landMark);
		}
		else if(landMark.isLandMark && task.getLevel() < 4){
				landMark.bvs = new boolean[] { false, false, false };
				landMark.indexivs = new boolean[] { false, false, false };
				landMark.nextScaleShow = new boolean[] {false,false,false};
//				MarkPoint landMarkType2 = (MarkPoint) landMark.clone();
				landMarkType.add(landMark);
		}
		else if(com.mansion.tele.business.landmark.Landmark.style.isFillLevel(
				landMark.telecode, task.getLevel()) || landMark.isSpecial){
			landMark.bvs = new boolean[] { false, false, false };
			landMark.indexivs = new boolean[] { false, false, false };
			landMark.nextScaleShow = new boolean[] {false,false,false};
//			MarkPoint landMarkType2 = (MarkPoint) landMark.clone();
			landMarkType.add(landMark);
		}
		return landMarkType;
	}

	/**
	 * 排版方法，由于路名的排版框
	 * 有多个，需要单独考虑
	 * @param landMarkType
	 * @param scaleNo
	 */
	public boolean typeSetting(MarkPoint landMarkType, int scaleNo) {
		// 路名和路名
		if (Style.DisPlay.lineShow.equals(this.display)
				&& Style.DisPlay.lineShow.equals(landMarkType.display)) {
			
//			return TypeSettinger.lineCompareLine(this, landMarkType, scaleNo);
			return false;
		}
		// 路名和其他地标
		else if(Style.DisPlay.lineShow.equals(this.display)
				&& !Style.DisPlay.lineShow.equals(landMarkType.display)){
			
			return TypeSettinger.lineCompareOther(this, landMarkType);
//			return false;
		}
		// 其他地标和路名
		else if(!Style.DisPlay.lineShow.equals(this.display)
				&& Style.DisPlay.lineShow.equals(landMarkType.display)){
			
			return TypeSettinger.lineCompareOther(landMarkType, this);
		}
		// 其他地标和其他地标
		else{
			
			return TypeSettinger.LandMarkCompareLandMark(this, landMarkType, scaleNo);
		}
	}

	/**
	 * 格式转换
	 * 
	 * @return
	 */
	public Landmark changeToForm(byte blevel) {
		// TODO 设置特殊处理标记，升层但不存到view数据中
		if (com.mansion.tele.business.landmark.Landmark.style.isShowLevel(this.telecode, blevel) 
				|| this.isLandMark || com.mansion.tele.business.landmark.Landmark.style.
				isFillLevel(this.telecode, blevel) && !this.isSpecial) {
			if (this.display == null) {
				System.out.println("错误的code" + this.telecode);
				return null;
			}
			if (this.display.equals(Style.DisPlay.isTxtLandMark)) {
				return this.changeToFormByTxt();
			} else if (this.display.equals(Style.DisPlay.lineShow)) {
				return this.changeToFormByLine();
			} else {
				return this.changeToFormBySign();
			}
		} else {
			return null;
		}
	}

	/**
	 * 获得block
	 * 
	 * @return
	 */
	private BlockNo getBlock() {
		BlockNo blockNo = new BlockNo(iBlockX, iBlockY);
		return blockNo;
	}

	/**
	 * 正规化
	 * 
	 * @param blockInfo
	 */
	public void coordinateRegular(TaskData taskData) {
		ShpPoint point = taskData.calcRegular(new ShpPoint(this.x,this.y));
		BlockNo block = taskData.calcBlockNo(new ShpPoint(this.x,this.y));
		this.iBlockX = block.iX;
		this.iBlockY = block.iY;
		this.x = point.x;
		this.y = point.y;

		if (Style.DisPlay.lineShow.equals(this.display)) {
			ShpPoint lastPoint = new ShpPoint(x, y);
			List<ShpPoint> regular = new ArrayList<ShpPoint>();
			for (int i = 0; i < line.size(); i++) {
				point = taskData.calcRegular(block, line.get(i));
				if (!lastPoint.equals(point)) {
					regular.add(point);
				}
				lastPoint = point;
			}
			line = regular;
		}
	}

	/**
	 * LinePri获取
	 * 
	 * @return
	 */
	public MarkPoint linePri() {
		return this;
	}

	/**
	 * signPri获取
	 */
	public MarkPoint signPri() {
		return this;
	}

	/**
	 * txtPri获取
	 */
	public MarkPoint txtPri() {
		return this;

	}

	/**
	 * 父子优先级处理
	 * @param LandMarkType_list
	 */
	public boolean getFather(List<MarkPoint> LandMarkType_list) {
		boolean del = false;
		if (this.landMarkType != null) {
			if(this.landMarkType.telecode == this.telecode && 
				(this.landMarkType.name.equals(this.name) || 
					this.landMarkType.name.contains(this.name))){
				LandMarkType_list.remove(this);
				del = true;
			}
			else if(this.landMarkType.pri >= this.pri){
				this.landMarkType.pri = this.pri - 1;
			}
		}
		return del;
	}

	/**
	 * 通过优先级进行比较排序
	 * 
	 * @param landMarkType
	 * @return
	 */
	public int comparePri(MarkPoint landMarkType) {
		return this.pri - landMarkType.pri;
	}

	public int compareShow(MarkPoint landMarkType, int scaleNo) {
		return Boolean.valueOf(bvs[scaleNo]).compareTo(
				landMarkType.bvs[scaleNo]);
	}

	/**
	 * 通过名称进行比较排序
	 * 
	 * @param landMarkType
	 * @return
	 */
	public int compareName(MarkPoint landMarkType) {
		if (name == null && landMarkType.name == null) {
			return 0;
		} else if (name == null || landMarkType.name == null) {
			return -1;
		}
		return this.name.compareTo(landMarkType.name);
	}

	/**
	 * 设置code
	 * 
	 * @param orgcode
	 */
	public void setOrgcode(int orgcode) {
		this.telecode = orgcode;
	}

	/**
	 * 判断
	 * 
	 * @return
	 */
	public boolean judge() {
		return this.strid == null;
	}

	/**
	 * 比较排版方法
	 * 
	 * @param landMark
	 * @return
	 */
	public boolean compareStrid(MarkPoint landMark) {
		return (this.strid.equals(landMark.strid));
	}

	/**
	 * 
	 * @param landMarkType
	 * @return
	 */
	public int compare(MarkPoint landMarkType) {
		return this.strid.compareTo(landMarkType.strid);
	}

	/**
	 * 通过母库CODE进行比较排序
	 * 
	 * @param landMarkType
	 * @return
	 */
	public int compareByOrg(MarkPoint landMarkType) {
		return telecode - landMarkType.getOrgcode();
	}

	public int getIndex_X() {
		return index_X;
	}

	public void setIndex_X(int index_X) {
		this.index_X = index_X;
	}

	public int getIndex_Y() {
		return index_Y;
	}

	public void setIndex_Y(int index_Y) {
		this.index_Y = index_Y;
	}

	public int getOrgcode() {
		return telecode;
	}

	// private byte getbCoorCnt(LandMarkType landMarkType) {
	// byte bCoorCnt = 1;
	// if (this.display == Style.DisPlay.lineShow.ordinal()) {
	// bCoorCnt = (byte) this.line.size();
	// }
	// return bCoorCnt;
	// }

	// TODO 获取不同比例尺下文字尺寸
	// 每个文字的经纬度/2560
	// protected int getFontSize(byte level, int scale) {
	// double stand = 111315.25198057058;
	// long scaleValue = TeleConfig.get().getScalePaCount(level, scale);
	// return (int) (font * scaleValue * 256 / stand);
	// }

	/**
	 * 克隆
	 */
//	@Override
//	public LandMarkType clone() throws CloneNotSupportedException {
//		LandMarkType signLandMark = (LandMarkType) super.clone();
//		boolean[] clonebvs = new boolean[3];
//		for (int i = 0; i < bvs.length; i++) {
//			clonebvs[i] = bvs[i];
//		}
//		signLandMark.bvs = clonebvs;
//		signLandMark.station = new Station();
//		signLandMark.station.ableStations = new ArrayList<SignTypeSetRect>();
//		if (this.station != null && this.station.ableStations != null) {
//			for (int i = 0; i < this.station.ableStations.size(); i++) {
//				signLandMark.station.ableStations.add(this.station.ableStations
//						.get(i));
//			}
//		}
//		return signLandMark;
//	}

	public ShpPoint getBasicPoint() {
		return new ShpPoint(x, y);
	}

	protected int[] getOneFontSize(int level, int iScaleNo, int lonToMe, int latToMe) {
		if (Style.DisPlay.notShowTxt.equals(this.display)) {
			return new int[] { 0, 0 };
		}
		double width = this.font;
		double high = this.font;
		long scaleValue = DataManager.getLevelInfo(level).scales[iScaleNo];
		double realwidth = scaleValue * width / 10;
		double realhigh = scaleValue * high / 10;
		int coordinatesW = 0;
		int coordinatesY = 0;
		if (realwidth != 0 && realhigh != 0) {
			coordinatesW = (int) (realwidth * ConstantValue.DEGREETOSECOND
					/ lonToMe + 1);
			coordinatesY = (int) (realhigh * ConstantValue.DEGREETOSECOND
					/ latToMe + 1);
		}
		return new int[] { coordinatesW, coordinatesY };
	}
	
	/**
	 * 获取文字间距，单位为1/2560秒
	 * @param level
	 * @param iScaleNo
	 * @param lonToMe
	 * @param latToMe
	 * @return
	 */
	protected int[] calcLetterSpacing(int level, int iScaleNo, int lonToMe, int latToMe){
		int spaceX = 0;
		int spaceY = 0;
		if (Style.DisPlay.notShowTxt.equals(this.display)) {
			return new int[]{spaceX,spaceY};
		}
		long scaleValue = DataManager.getLevelInfo(level).scales[iScaleNo];
		double realSpacing = scaleValue * this.letterSpacing / 10;
		if(realSpacing != 0){
			spaceX = (int) (realSpacing *  ConstantValue.DEGREETOSECOND / lonToMe + 1);
			spaceY = (int) (realSpacing *  ConstantValue.DEGREETOSECOND / latToMe + 1);		
		}
		return new int[]{spaceX,spaceY};
	}


//	public boolean comPareMaxGeo(LandMarkType signLandMark) {
//		if (GeoRect.rectInRect(this.maxRect, signLandMark.maxRect)) {
//			return true;
//		}
//		return false;
//	}
}

package com.sunmap.teleview.element.view.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sunmap.teleview.element.view.Device;
import com.sunmap.teleview.element.view.IconSearcher;
import com.sunmap.teleview.element.view.LayerManage;
import com.sunmap.teleview.util.ToolsUnit;

public class BlockData {

	public BlockID blockID;
	public TxtMessage txtMessage;
	public List<LayerManage> layerDatas = new ArrayList<LayerManage>();

	public BlockData(BlockID blockID) {
		this.blockID = new BlockID();
		this.blockID.dataSize = blockID.dataSize;
		this.blockID.level = blockID.level;
		this.blockID.xIndex = blockID.xIndex;
		this.blockID.yIndex = blockID.yIndex;
	}

	public boolean equals(Object other1) {
		BlockData o = (BlockData) other1;
		return this.blockID.equals(o.blockID);
	}

	private void parse(DataInputStream dis) {
		try {
			List<LayerManageMessage> layerManageMessages = parseLayerManage(dis);

			txtMessage = new TxtMessage();
			txtMessage.parse(dis);

			for (int i = 0; i < layerManageMessages.size(); i++) {
				LayerManage layerData = new LayerManage();
				layerData.priority = layerManageMessages.get(i).priority;
				if(blockID.xIndex == 88 && blockID.yIndex == 36){
					System.out.println("test");
				}
				layerData.parse(dis, txtMessage);
				if (layerData.elementDatas.size() > 0) {
					layerDatas.add(layerData);
				}
			}
			dis.readByte();
			dis.readByte();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析LayerManage和TxtMessage
	 * 
	 * @param buffer
	 * @throws IOException
	 */
	public void parse(byte[] buffer) throws IOException {
		if (buffer != null) {
			ByteArrayInputStream bis = null;
			DataInputStream dis = null;
			if (buffer.length > 0) {
				bis = new ByteArrayInputStream(buffer);
				dis = new DataInputStream(bis);
				parse(dis);
				if (dis != null) {
					dis.close();
				}
			}
		}
	}

	/**
	 * 解析Layer管理信息表
	 * 
	 * @param dis
	 *            数据流
	 * @return Layer管理信息表
	 */
	private List<LayerManageMessage> parseLayerManage(DataInputStream dis) {
		List<LayerManageMessage> layerManageMessages = new ArrayList<LayerManageMessage>();
		try {
			short layerDataCount = (short) dis.readUnsignedByte();

			for (int i = 0; i < layerDataCount; i++) {
				LayerManageMessage message = new LayerManageMessage();
				message.parse(dis);
				layerManageMessages.add(message);
			}

			int byteSize = 1 + layerManageMessages.size() * 5;
			if (byteSize % 2 != 0) {
				dis.readByte();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return layerManageMessages;
	}

	/**
	 * Layer管理信息表
	 */
	public class LayerManageMessage implements Serializable {
		private static final long serialVersionUID = 1L;
		int offset;
		int size;
		short priority;

		public void parse(DataInputStream dis) {
			try {
				offset = dis.readUnsignedShort() * 2;
				size = dis.readUnsignedShort() * 2;
				priority = dis.readByte();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static public Comparator<BlockData> compare = new Comparator<BlockData>() {

		@Override
		public int compare(BlockData left, BlockData right) {
			if (left.blockID.level > right.blockID.level) {
				return 1;
			}
			if (left.blockID.level < right.blockID.level) {
				return -1;
			}
			if (left.blockID.xIndex > right.blockID.xIndex) {
				return 1;
			}
			if (left.blockID.xIndex < right.blockID.xIndex) {
				return -1;
			}
			if (left.blockID.yIndex > right.blockID.yIndex) {
				return 1;
			}
			if (left.blockID.yIndex < right.blockID.yIndex) {
				return -1;
			}
			return 0;
		}
	};

	/**
	 * 描画背景
	 * 
	 * @param g
	 */
	public void drawBG(Graphics2D g) {
		Map<Short, GeneralPath> mapBg = new HashMap<Short, GeneralPath>(); // 背景
		for (int i = 0; i < layerDatas.size(); i++) {
			LayerManage layerman = layerDatas.get(i);
			layerman.makeBGDrawData(mapBg, blockID);
		}
		Set<Short> layerBgs = mapBg.keySet();
		for (Short layerNO : layerBgs) {
			GeneralPath path = mapBg.get(layerNO);
			g.setColor(selectColor(layerNO));
			g.setStroke(new BasicStroke(2.0f));
			// g.draw(path);
			g.fill(path);
		}
	}

	/**
	 * 描画区域线
	 * 
	 * @param g
	 */
	public void drawRegionLine(Graphics2D g) {
		Map<Short, GeneralPath> regionRoad = new HashMap<Short, GeneralPath>(); // 底层道路
		for (int i = 0; i < layerDatas.size(); i++) {
			LayerManage layerman = layerDatas.get(i);
			layerman.makeRegionDrawData(regionRoad, blockID);
		}
		Set<Short> layerRoads = regionRoad.keySet();
		for (Short layerNO : layerRoads) {
			if (layerNO < 14) {
				GeneralPath path = regionRoad.get(layerNO);
				g.setColor(selectColor(layerNO));
				g.setStroke(selectStroke(layerNO));
				g.draw(path);
			}

		}
	}

	/**
	 * 描画道路
	 * 
	 * @param g
	 */
	public void drawRoad(Graphics2D g) {
		Map<Short, GeneralPath> mapRoad = new HashMap<Short, GeneralPath>(); // 底层道路
		for (int i = 0; i < layerDatas.size(); i++) {
			LayerManage layerman = layerDatas.get(i);
			layerman.makeRoadDrawData(mapRoad, blockID);
		}
		Set<Short> layerRoads = mapRoad.keySet();
		for (Short layerNO : layerRoads) {
			if (layerNO > 39) {
				GeneralPath path = mapRoad.get(layerNO);
				g.setColor(selectColor(layerNO));
				g.setStroke(selectStroke(layerNO));
				g.draw(path);
			}
		}
	}
	/**
	 *  描画道路文字线
	 */
	public void drawRoadTextLine(Graphics2D g) {
		Map<Short, GeneralPath> mapRoadTextLine = new HashMap<Short, GeneralPath>(); // 底层道路
		for (int i = 0; i < layerDatas.size(); i++) {
			LayerManage layerman = layerDatas.get(i);
			layerman.makeRoadTextLineData(mapRoadTextLine, blockID);
		}
		//描画道路文字线
		Set<Short> layerTextLines =  mapRoadTextLine.keySet();
		for (Short layerNO : layerTextLines) {
			GeneralPath path = mapRoadTextLine.get(layerNO);
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke(1.0f));
			g.draw(path);	
		}
	}
	
	/**
	 * 描画BLOCK边框
	 * @param g
	 */
	public void drawBlockEdging(Graphics2D g) {
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(1.5f));
		//正规化坐标变成1/2560秒
		Point  geoPointA = blockID.unitToGeo(255, 0);
		Point  geoPointB = blockID.unitToGeo(0, 0);
		Point  geoPointC = blockID.unitToGeo(0, 255);
		Point  geoPointD = blockID.unitToGeo(255, 255);
		Point2D pointA = Device.geoToPix(geoPointA);
		Point2D pointB = Device.geoToPix(geoPointB);
		Point2D pointC = Device.geoToPix(geoPointC);
		Point2D pointD = Device.geoToPix(geoPointD);
		int[] xPoints = new int[]{(int) (pointA.getX() + 0.5),(int) (pointB.getX() + 0.5),(int) (pointC.getX() + 0.5),(int) (pointD.getX() + 0.5)};
		int[] yPoints = new int[]{(int) (pointA.getY() + 0.5),(int) (pointB.getY() + 0.5),(int) (pointC.getY() + 0.5),(int) (pointD.getY() + 0.5)};
		Shape s = new Polygon(xPoints, yPoints, 4);
		g.draw(s);
	}
	/**
	 * 描画地标
	 * 
	 * @param g
	 */
	public void drawLandMark(Graphics2D g) {
		List<TeleViewText> texts = new ArrayList<TeleViewText>();
		for (int i = 0; i < layerDatas.size(); i++) {
			LayerManage layerman = layerDatas.get(i);
			layerman.makeLandMarkDrawData(texts, txtMessage, blockID);
		}
		int fontsize = 12;
		g.setFont(new Font("宋体", Font.CENTER_BASELINE, fontsize));
		List<TeleViewText> subways = new ArrayList<TeleViewText>();
		//描画POI
		g.setColor(Color.DARK_GRAY);
		for (int i = 0; i < texts.size(); i++) {
			TeleViewText text = texts.get(i);
			if (text.type == (short)	0x4900) {
				subways.add(text);//地铁的POI因设备部一样稍后一起重新描画
				continue;
			}
			byte b = text.positionInfo;
			Point point = text.getPositionInfoPoint(b, fontsize);
			String[] name = text.str.split("\\\r\\\n");
			//描画ICON
			Point2D.Double pixPoint = Device.geoToPix(new Point(text.x, text.y));
			Image image = IconSearcher.getInstance().getMapIcon(text.type);
			if (image!=null) {
				g.drawImage(image,(int)(pixPoint.x-5), (int)(pixPoint.y-5), fontsize, fontsize, null);
			}else if (b>0) {
				g.fillOval((int)(pixPoint.x), (int)(pixPoint.y), 3, 3);
			}
			//描画文字
			if (text.type == (short)	0xb880) {//公共厕所不描画文字
				continue;
			}
			for (int m = 0; m < name.length; m++) {
				g.drawString(name[m], point.x, point.y + m * fontsize);
			}
		}
		//描画地铁出口
		g.setColor(Color.WHITE);
		for (int i = 0; i < subways.size(); i++) {
			TeleViewText text = subways.get(i);
			byte b = text.positionInfo;
			Point point = text.getPositionInfoPoint(b, fontsize);
			String[] name = text.str.split("\\\r\\\n");
			//描画ICON
			Point2D.Double pixPoint = Device.geoToPix(new Point(text.x, text.y));
			Image image = IconSearcher.getInstance().getMapIcon(text.type);
			g.drawImage(image,(int)(pixPoint.x-4), (int)(pixPoint.y-4), fontsize, fontsize, null);
			//描画文字
			for (int m = 0; m < name.length; m++) {
				g.drawString(name[m], point.x, point.y + m * fontsize);
			}
		}
	}

	/**
	 * 颜色选择
	 * 
	 * @param inRoad
	 *            道路
	 * @return
	 */
	public static Color selectColor(int type) {
		Color color;
		switch (type) {
		case defineLayerNo.HightWay:// 高速道路
			color = defineLayerNoColor.HightWay;
			break;
		case defineLayerNo.NationalRoad:// 国道
			color = defineLayerNoColor.NationalRoad;
			break;
		case defineLayerNo.CityFreeWay:// 城市快速道路，城市间快速道路
			color = defineLayerNoColor.CityFreeWay;
			break;
		case defineLayerNo.ProcinceRoad:// 省道
			color = defineLayerNoColor.ProcinceRoad;
			break;
		case defineLayerNo.CityMajorRoad:// 城市主干道
			color = defineLayerNoColor.CityMajorRoad;
			break;
		case defineLayerNo.CityMinorRoad:// 城市次干道
			color = defineLayerNoColor.CityMinorRoad;
			break;
		case defineLayerNo.NormalRoad:// 一般道
			color = defineLayerNoColor.NormalRoad;
			break;
		case defineLayerNo.MinorRoad1:// 細道路（細道路1）
			color = defineLayerNoColor.MinorRoad1;
			break;
		case defineLayerNo.MinorRoad2:// 細道路（細道路2）
			color = defineLayerNoColor.MinorRoad2;
			break;
		case defineLayerNo.Freey:
			color = defineLayerNoColor.Freey;// Ferry link（航道）
			break;
		case defineLayerNo.Sea:
			color = defineLayerNoColor.Sea;
			break;
		case defineLayerNo.Province:
			color = defineLayerNoColor.Province;
			break;
		case defineLayerNo.Island:
			color = defineLayerNoColor.Island;
			break;
		case defineLayerNo.CoastalLine:
			color = defineLayerNoColor.CoastalLine;
			break;
		case defineLayerNo.SeaLine:
			color = defineLayerNoColor.SeaLine;
			break;
		case defineLayerNo.Park:
			color = defineLayerNoColor.Park;
			break;
		case defineLayerNo.Water:
			color = defineLayerNoColor.Water;
			break;
		case defineLayerNo.CountryLine:
			color = defineLayerNoColor.CountryLine;
			break;
		case defineLayerNo.ICLine:
			color = defineLayerNoColor.ICLine;
			break;
		case defineLayerNo.ProvinceLine:
			color = defineLayerNoColor.ProvinceLine;
			break;
		case defineLayerNo.HKLine:
			color = defineLayerNoColor.HKLine;
			break;
		case defineLayerNo.CityLine:
			color = defineLayerNoColor.CityLine;
			break;
		case defineLayerNo.TownLine:
			color = defineLayerNoColor.TownLine;
			break;
		case defineLayerNo.BGRoad:
			color = defineLayerNoColor.BGRoad;
			break;
		case defineLayerNo.Railway:
			color = defineLayerNoColor.Railway;
			break;
		default:
			color = defineLayerNoColor.Subway1;
			break;
		}
		return color;
	}

	/**
	 * 线式样选择
	 * 
	 * @param layerNo2
	 * @return
	 */
	public static Stroke selectStroke(short layerNo2) {
		Stroke outerStroke = null;
		switch (layerNo2) {
		case defineLayerNo.HightWay:
			outerStroke = defineLayerNoStroke.roadline;
			break;
		case defineLayerNo.NationalRoad:
			outerStroke = defineLayerNoStroke.roadline;
			break;
		case defineLayerNo.CityFreeWay:
			outerStroke = defineLayerNoStroke.roadline;
			break;
		case defineLayerNo.ProcinceRoad:
			outerStroke = defineLayerNoStroke.roadline;
			break;
		case defineLayerNo.CityMajorRoad:
			outerStroke = defineLayerNoStroke.roadline;
			break;
		case defineLayerNo.CityMinorRoad:
			outerStroke = defineLayerNoStroke.roadline;
			break;
		case defineLayerNo.NormalRoad:
			outerStroke = defineLayerNoStroke.roadline;
			break;
		case defineLayerNo.CountryLine:
			outerStroke = defineLayerNoStroke.CountryLine;
			break;
		case defineLayerNo.ICLine:
			outerStroke = defineLayerNoStroke.ICLine;
			break;
		case defineLayerNo.ProvinceLine:// 行政界（省）
		case defineLayerNo.HKLine:// 特殊边界（当前指港澳区界）
			outerStroke = defineLayerNoStroke.ProvinceLine;
			break;
		default:
			outerStroke = defineLayerNoStroke.dif;
			break;
		}
		return outerStroke;
	}

	// 地图数据Layer分类
	static final class defineLayerNo {
		// 线类型
		public static final byte CoastalLine = 4;// 海疆线
		public static final byte SeaLine = 5;// 海岸线
		public static final byte CountryLine = 8;// 行政界（国界）
		public static final byte ICLine = 9;// 行政界（未定国界）IndeterminateCountryLine
		public static final byte ProvinceLine = 10;// 行政界（省）
		public static final byte HKLine = 11;// 特殊边界（当前指港澳区界）
		public static final byte CityLine = 12;// 行政界（市）
		public static final byte TownLine = 13;// 行政界（县）
		public static final byte HightWay = 59;// 高速道路
		public static final byte NationalRoad = 58;// 国道
		public static final byte CityFreeWay = 57;// 城市快速道路，城市间快速道路
		public static final byte ProcinceRoad = 56;// 省道
		public static final byte CityMajorRoad = 55;// 城市主干道
		public static final byte CityMinorRoad = 54;// 城市次干道
		public static final byte NormalRoad = 53;// 一般道
		public static final byte MinorRoad1 = 52;// 細道路（細道路1）
		public static final byte MinorRoad2 = 51;// 細道路（細道路2）
		public static final byte Freey = 50;// Ferry link（航道）
		// 背景面类型
		public static final byte Sea = 1;// 海域
		public static final byte Province = 2;// 省级行政面
		public static final byte Island = 3;// 岛
		public static final byte Park = 6;// 绿地、公园
		public static final byte Water = 7;// 水系
		// 地铁 轻轨
		public static final byte BGRoad = 40;// 背景道路
		public static final byte Railway = 41;// 铁路
		public static final byte Subway1 = 70;// 地铁１（建议色： RGB: 228, 76, 76）
		public static final byte Subway2 = 71;// 地铁２（建议色： RGB: 73, 122, 204）
		public static final byte Subway3 = 72;// 地铁３（建议色： RGB: 78, 186, 165）
		public static final byte Subway4 = 73;// 地铁４（建议色： RGB: 204, 122, 177）
		public static final byte Subway5 = 74;// 地铁５（建议色： RGB: 54, 155, 54）
		public static final byte Subway6 = 75;// 地铁６（建议色： RGB: 2, 162, 218）
		public static final byte Subway7 = 76;// 地铁７（建议色： RGB:250, 204, 88）
		public static final byte Subway8 = 77;// 地铁８（建议色： RGB: 153, 102, 255）
		public static final byte Subway9 = 78;// 地铁９（建议色： RGB: 214, 52, 65）
		public static final byte Subway10 = 79;// 地铁１０（建议色： RGB:160, 127, 84）
		public static final byte Subway11 = 80;// 地铁１１（建议色： RGB: 77, 185, 164）
		public static final byte Subway12 = 81;// 地铁１２（建议色： RGB: 107, 179, 71）
		public static final byte Subway13 = 82;// 地铁１３（建议色： RGB: 180, 74, 162）
		public static final byte Subway14 = 83;// 地铁１４（建议色： RGB: 115, 98, 111）
		public static final byte Lightway1 = 90;// 轻轨１（建议色： RGB: 224, 3, 172）
		public static final byte Lightway2 = 91;// 轻轨２（建议色： RGB: 44, 196, 39）
		public static final byte Lightway3 = 92;// 轻轨３（建议色： RGB: 101, 142, 211）
		public static final byte Lightway4 = 93;// 轻轨４（建议色：）
		public static final byte Magnetic1 = 110;// 磁悬浮１（建议色： RGB: 129, 130,130）

	}

	// 地图数据Layer分类对应颜色
	static final class defineLayerNoColor {
		public static final Color HightWay = new Color(0, 0, 255);// 蓝色
		public static final Color NationalRoad = new Color(31, 207, 63);// 墨绿
		public static final Color CityFreeWay = new Color(51, 153, 255);// 湛蓝
		public static final Color ProcinceRoad = new Color(53, 219, 199);// 湛蓝
		public static final Color CityMajorRoad = new Color(102, 153, 0);// 橘黄
		public static final Color CityMinorRoad = new Color(86, 170, 150);// 浅蓝
		public static final Color NormalRoad = new Color(132, 152, 182);// 土黄
		public static final Color MinorRoad1 = new Color(143, 129, 141);// 灰色
		public static final Color MinorRoad2 = new Color(255, 255, 0);// 黄色
		public static final Color Freey = new Color(100, 145, 97);// 棕色

		public static final Color Sea = new Color(100, 100, 250);
		// public static final Color Province = new Color(123, 123, 123);
		public static final Color Province = new Color(214, 197, 140);
		// public static final Color Island = new Color(245, 243, 240);
		public static final Color Island = new Color(214, 197, 140);
		public static final Color CoastalLine = new Color(123, 123, 123);
		public static final Color SeaLine = new Color(123, 123, 123);
		public static final Color Park = new Color(119, 189, 72);
		// public static final Color Water = new Color(0, 180, 214);
		public static final Color Water = new Color(153, 179, 204);
		public static final Color CountryLine = new Color(123, 123, 123);
		public static final Color ICLine = new Color(123, 123, 123);
		public static final Color ProvinceLine = new Color(123, 123, 123);
		public static final Color HKLine = new Color(123, 123, 123);
		public static final Color CityLine = new Color(123, 123, 123);
		public static final Color TownLine = new Color(123, 123, 123);
		public static final Color BGRoad = new Color(123, 123, 123);
		public static final Color Railway = new Color(0, 0, 0);
		public static final Color Subway1 = new Color(255, 128, 0);
	}

	// 地图数据Layer分类对应线样式
	static final class defineLayerNoStroke {
		static float[] ic = new float[] { 5, 2 };
		public static final Stroke CountryLine = new BasicStroke(2.0f,
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		public static final Stroke ICLine = new BasicStroke(2.0f,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, ic, 0.0f);
		public static final Stroke ProvinceLine = new BasicStroke(1.0f,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, ic, 0.0f);
		public static final Stroke roadline = new BasicStroke(1f,
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		public static final Stroke dif = new BasicStroke(0.5f,
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
	}

	/**
	 * 根据点击的点查询线类型是否是符合要求的元素
	 * 
	 * @param geoPoint
	 *            1/2560秒
	 * @param dis
	 *            像素距离
	 * @param blockID
	 * @param flagString 
	 * @return
	 */
	public List<RoadData> selectLineByPix(Point2D.Float point, int dis,
			BlockID blockID, String flagString) {
		List<RoadData> result = new ArrayList<RoadData>();
		for (int i = 0; i < layerDatas.size(); i++) {
			List<RoadData> layerDatasResult = layerDatas.get(i).selectLineByPix(point, dis, blockID,flagString);
			result.addAll(layerDatasResult);
		}
		return result;
	}

	public List<RoadTextLineData> selectRoadLineByPix(Point2D.Float point, int dis,
			BlockID blockID) {
		List<RoadTextLineData> result = new ArrayList<RoadTextLineData>();
		for (int i = 0; i < layerDatas.size(); i++) {
			List<RoadTextLineData> layerDatasResult = layerDatas.get(i).selectRoadLineByPix(point, dis, blockID);
			result.addAll(layerDatasResult);
		}
		return result;
	}

	/**
	 * 根据点击的点查询线类型是否是符合要求的元素
	 * 
	 * @param geoPoint
	 *            1/2560秒
	 * @param dis
	 *            像素距离
	 * @param blockID
	 * @return
	 */
	public List<TeleViewText> selectPointByPix(Point2D.Float point, int dis) {
		List<TeleViewText> texts = new ArrayList<TeleViewText>();
		for (int i = 0; i < layerDatas.size(); i++) {
			LayerManage layerman = layerDatas.get(i);
			layerman.makeLandMarkDrawData(texts, txtMessage, blockID);
		}
		List<TeleViewText> result = new ArrayList<TeleViewText>();
		if (texts != null) {
			for (int i = 0; i < texts.size(); i++) {
				TeleViewText landMark = texts.get(i);
				landMark.number = i;
				Point2D pixPoint = Device.geoToPix(new Point(landMark.x, landMark.y));
				double disPix = ToolsUnit.calcTwoPointDistance(point, pixPoint);
				if (disPix < dis) {
					result.add(landMark);
				}
			}
		}
		return result;
	}

	public List<TeleViewText> selectLandMark(String landMarkName, String provinceName, String cityName) throws SQLException {
		List<TeleViewText> texts = new ArrayList<TeleViewText>();
		for (int i = 0; i < layerDatas.size(); i++) {
			LayerManage layerman = layerDatas.get(i);
			layerman.makeLandMarkDrawData(texts, txtMessage, blockID);
		}
		List<TeleViewText> result = new ArrayList<TeleViewText>();
		if (texts != null) {
			for (int i = 0; i < texts.size(); i++) {
				TeleViewText landMark = texts.get(i);
				landMark.number = i;
				if (isEffictiveResult(landMark, landMarkName, provinceName, cityName) == true) {
					result.add(landMark);
				}

			}
		}
		return result;
	}

	/**
	 * 判断检索的地标是否有效
	 * 
	 * @param poi
	 * @param landMarkName
	 * @param provinceName
	 * @param cityName
	 * @return
	 * @throws SQLException
	 */
	private boolean isEffictiveResult(TeleViewText poi, String landMarkName,
			String provinceName, String cityName) throws SQLException {
		if (poi.str.contains(landMarkName)) {
			return true;
			// if (provinceName.equals("")) {
			// return true;
			// }
			// AdminCode adminCode = AdminCodes.getCityByCode(poi.x, poi.y);
			// if (!cityName.equals("")) {
			// if (adminCode.getProvince().equals(provinceName) &&
			// adminCode.getCity().equals(cityName)) {
			// return true;
			// }
			// }else {
			// if (adminCode.getProvince().equals(provinceName)) {
			// return true;
			// }
			// }
		}
		return false;

	}

	/**
	 * 根据点击的点查询线类型是否是符合要求的元素
	 * 
	 * @param geoPoint
	 *            1/2560秒
	 * @param dis
	 *            像素距离
	 * @param blockID
	 * @return
	 */
	public List<BackGroundData> selectBgByPix(Point2D.Float point) {
		List<BackGroundData> result = new ArrayList<BackGroundData>();
		for (int i = 0; i < layerDatas.size(); i++) {
			List<BackGroundData> layerDatasResult = layerDatas.get(i).selectBgByPix(point, blockID);
			result.addAll(layerDatasResult);
		}
		return result;
	}

}

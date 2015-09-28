package com.mansion.tele.business.viewdata;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mansion.tele.business.common.BaseShape;
import com.mansion.tele.business.common.LetterInfo;
import com.mansion.tele.business.common.MarkInfor;
import com.mansion.tele.business.common.TelePolygonShp;
import com.mansion.tele.business.common.TelePolylineShp;
import com.mansion.tele.business.common.TeleRoadPloylineShp;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.Util;

public class ShpVectorUtil {
	
	/** 全角括号 */
	public static final String Bracket = "（";
	/**
	 * 清除重复点
	 * 
	 * @param base
	 * @return
	 */
	public static BaseShape cleanIteratePoint(BaseShape base) {
		List<ShpPoint> shpPoints = null;
		if (base instanceof TelePolygonShp) {
			shpPoints = ((TelePolygonShp) base).getCoordinate();
			TelePolygonShp polygonShp = new TelePolygonShp();
			polygonShp.setCoordinate(cleanPoint(shpPoints));
			return polygonShp;
		}
		if (base instanceof TelePolylineShp) {
			shpPoints = ((TelePolylineShp) base).getCoordinate();
			TelePolylineShp polylineShp = new TelePolylineShp();
			polylineShp.setCoordinate(cleanPoint(shpPoints));
			return polylineShp;
		}
		if (base instanceof TeleRoadPloylineShp) {
			shpPoints = ((TeleRoadPloylineShp) base).getCoordinate();
			TeleRoadPloylineShp roadPloylineShp = new TeleRoadPloylineShp();
			roadPloylineShp.setCoordinate(cleanPoint(shpPoints));
			roadPloylineShp.setBuildindRoad(((TeleRoadPloylineShp) base).getBuildindRoad());
			roadPloylineShp.setTunnel(((TeleRoadPloylineShp) base).getTunnel());
			roadPloylineShp.setBridge(((TeleRoadPloylineShp) base).getBridge());
			roadPloylineShp.setGrade(((TeleRoadPloylineShp) base).getGrade());
			roadPloylineShp.setByStartGrade(((TeleRoadPloylineShp) base).getByStartGrade());
			roadPloylineShp.setByEndGrade(((TeleRoadPloylineShp) base).getByEndGrade());
			roadPloylineShp.setNrc(((TeleRoadPloylineShp) base).getNrc());
			roadPloylineShp.setLayerNO(((TeleRoadPloylineShp) base).getLayerNO());
			return roadPloylineShp;
		}

		return base;
	}

	/**
	 * 清除重复点
	 * 
	 * @param shpPoints List<ShpPoint> 传入的形状点集合
	 * @return List<ShpPoint> 返回清除过重复点的形状点集合
	 */
	public static List<ShpPoint> cleanPoint(List<ShpPoint> shpPoints) {
		List<ShpPoint> pointlist = new ArrayList<ShpPoint>();
		for (int i = 0; i < shpPoints.size(); i++) {
			if (pointlist.size() == 0) {
				pointlist.add(shpPoints.get(i));
			} else {
				if (pointlist.get(pointlist.size() - 1).x == shpPoints.get(i).x
						&& pointlist.get(pointlist.size() - 1).y == shpPoints
								.get(i).y) {
					continue;
				} else {
					pointlist.add(shpPoints.get(i));
				}
			}
		}
		return pointlist;
	}

	/**
	 * 获得背景面、线，道路图形信息
	 * 
	 * @param shape
	 * @return
	 */
	public static byte[] getVector(BaseShape shape) {
		List<ShpPoint> shpPoints = null;
		byte[] by = null;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);
		try {
			if (shape instanceof TelePolygonShp) {
				shpPoints = shape.getCoordinate();
				dos.write(1);
				getVectorPolygonShp(shape, dos);
			}
			if (shape instanceof TelePolylineShp) {
				shpPoints = shape.getCoordinate();
				getVectorPolylineShp(shpPoints, dos);
			}
			if (shape instanceof TeleRoadPloylineShp) {
				int buildindRoad = ((TeleRoadPloylineShp) shape).getBuildindRoad();
				int tunnel = ((TeleRoadPloylineShp) shape).getTunnel();
				int bridge = ((TeleRoadPloylineShp) shape).getBridge();
				int iStartGrade = ((TeleRoadPloylineShp) shape).getByStartGrade();
				int iEndGrade = ((TeleRoadPloylineShp) shape).getByEndGrade();
				shpPoints = ((TeleRoadPloylineShp) shape).getCoordinate();
				getVectorRoadShp(shpPoints, dos, buildindRoad, tunnel, bridge, iStartGrade, iEndGrade);
			}

			by = bao.toByteArray();
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return by;
	}

	/**
	 * 获得文字标记图形信息
	 * 
	 * @param shape
	 * @param letter
	 * @return
	 */
	public static byte[] getVector(BaseShape shape,
			Map<String, LetterInfo> letter) {
		MarkInfor markInfor = null;
		byte[] by = null;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);
		try {
			if (shape instanceof MarkInfor) {
				markInfor = (MarkInfor) shape;
				if (0 == markInfor.getiIconVS()) {

					// 文字
					// 文字个数大于6个并且不是道路名称就换行
					if (6 < markInfor.getStrString().length() && 1 == markInfor.getCoordinate().size()&& 0xbf90 != markInfor.getLetterMark()) {
						
						dos.write(25);
					}else if(markInfor.getCoordinate().size() > 1){
						
						dos.write(26);
					}else{
						dos.write(21);
					}
					getVectorTxtInfo(markInfor, dos, letter);
				} else {

					// POI
					dos.write(34);
					getVectorMarkInfo(markInfor, dos, letter);
				}

			}
			by = bao.toByteArray();
			dos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return by;
	}

	private static void getVectorPolygonShp(BaseShape shape,
			DataOutputStream dos) throws IOException {
		dos.write(shape.getCoordinate().size());
		for (int i = 0; i < shape.getCoordinate().size(); i++) {
			dos.write(shape.getCoordinate().get(i).x);
			dos.write(shape.getCoordinate().get(i).y);
		}
	}

	private static void getVectorPolylineShp(List<ShpPoint> shpPoints,
			DataOutputStream dos) throws IOException {

		dos.write(shpPoints.size());

		int data = 0;
		dos.write(data);

		for (int i = 0; i < shpPoints.size(); i++) {
			dos.write(shpPoints.get(i).x);
			dos.write(shpPoints.get(i).y);
		}
	}

	private static void getVectorRoadShp(List<ShpPoint> shpPoints,
			DataOutputStream dos, int buildindRoad, int tunnel, int bridge, int iStartGrade, int iEndGrade)
			throws IOException {
		dos.write(shpPoints.size());
		int data = 0;
//		data = Util.setBits(data, nrc, 0, 3);
		
		if (iEndGrade == 1) {
			data = Util.setBit(data, 3, true);
		}else {
			data = Util.setBit(data, 3, false);
		}
		
		if (iStartGrade == 1) {
			data = Util.setBit(data, 4, true);
		}else {
			data = Util.setBit(data, 4, false);
		}
		
		if (bridge == 1) {
			data = Util.setBit(data, 5, true);
		} else {
			data = Util.setBit(data, 5, false);
		}
		if (tunnel == 1) {
			data = Util.setBit(data, 6, true);
		} else {
			data = Util.setBit(data, 6, false);
		}
		if (buildindRoad == 1) {
			data = Util.setBit(data, 7, true);
		} else {
			data = Util.setBit(data, 7, false);
		}
	
		dos.write(data);

		for (int i = 0; i < shpPoints.size(); i++) {
			dos.write(shpPoints.get(i).x);
			dos.write(shpPoints.get(i).y);
//			System.out.println("x:" + shpPoints.get(i).x + ",y:" + shpPoints.get(i).y);
		}
	}

	private static void getVectorMarkInfo(MarkInfor markInfor,
			DataOutputStream dos, Map<String, LetterInfo> letter) {
		try {
			// 基准点坐标X
			dos.write(markInfor.getCoordinate().get(0).x);
			// 基准点坐标Y
			dos.write(markInfor.getCoordinate().get(0).y);
			// Icon的显示比例尺
			dos.write(markInfor.getiIconVS());
			// 文字记号番号
			dos.writeShort(markInfor.getLetterMark());
			// 文字的显示比例尺
			dos.write(markInfor.getiTextVS());
			// 文字表示属性
			int data = 0;
			data = Util.setBits(data, markInfor.getLetterMarkAdvices(), 0, 2);
			data = Util.setBits(data, 0, 3, 5);
			data = Util.setBit(data, 6, 0 != markInfor.getFlag());
			data = Util.setBit(data, 7, false);
			dos.write(data);
			if (6 < markInfor.getStrString().length() && 0 != markInfor.getiTextVS()) {
				// 需要换行
//				if (13 > markInfor.strString.length()) {
					
					// 行数，换两行
					dos.write(2);
					
					// 行管理记录信息
//					LetterInfo letterInfo = letter.get(markInfor.getStrString());
					// 第一行文字的个数
					int iLine1TextCnt = markInfor.getStrString().length() / 2 + markInfor.getStrString().length() % 2;
					// 第二行文字的个数
//					int iLine2TextCnt = markInfor.getStrString().length() - iLine1TextCnt;
					String markTxt1 = markInfor.getStrString().substring(0, iLine1TextCnt );
					String markTxt2 = markInfor.getStrString().substring(iLine1TextCnt);
					LetterInfo letterInfo = letter.get(markTxt1 + markTxt2);
//					int iLine2Offset = letterInfo.getOffSet() + markTxt1.getBytes(TeleView.coding).length;
					
					// 输出第一行文字信息
					dos.writeShort(letterInfo.getOffSet() / 2);
					dos.write(markTxt1.getBytes(TeleView.coding).length);
					dos.write(0);
					
					// 输出第二行文字信息
					letterInfo = letter.get(markTxt2 + markTxt1);
					dos.writeShort(letterInfo.getOffSet() / 2);
					dos.write(markTxt2.getBytes(TeleView.coding).length);
					dos.write(0);				
			}else {
				if (null != markInfor.getStrString() ) {
					// 行数，换一行
					dos.write(1);
					LetterInfo letterInfo = letter.get(markInfor.getStrString());
					if (letterInfo.getOffSet() % 2 != 0) {
						System.err.println("文字偏移量计算错误(不能被2整除) ： offset=" + letterInfo.getOffSet());
					}
					dos.writeShort(letterInfo.getOffSet() / 2);
					dos.write(letterInfo.getSize());
					dos.write(0);
				}else {
					
					dos.write(0);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 输出文字信息
	 * 
	 * @param markInfor
	 *            文字数据
	 * @param dos
	 *            输出流
	 * @param letter
	 *            显示的文字相关信息
	 */
	private static void getVectorTxtInfo(MarkInfor markInfor,
			DataOutputStream dos, Map<String, LetterInfo> letter) {
		try {
			if(markInfor.getCoordinate().size() > 1 ){
				// 文字的分类种别
				dos.writeShort(markInfor.getLetterMark());
				// 显示比例尺
				dos.write(markInfor.getiTextVS());
				// Offset
				LetterInfo letterInfo = letter.get(markInfor.getStrString());
				if (letterInfo.getOffSet() % 2 != 0) {
					System.err.println("文字偏移量计算错误(不能被2整除) ： offset=" + letterInfo.getOffSet());
				}
				dos.writeShort(letterInfo.getOffSet() / 2);
				// 文字SIze
				dos.write(letterInfo.getSize());
				// 半角size 当前半角文字长度固定为0
				dos.write(0);
				// 名称坐标点个数
				dos.write(markInfor.getCoordinate().size());
				// 名称显示的偏移坐标序列
					for (int i = 0; i < markInfor.getCoordinate().size(); i++) {
						dos.writeShort(markInfor.getCoordinate().get(i).x);
						dos.writeShort(markInfor.getCoordinate().get(i).y);
					}
				return;
			}else{
				
			// 基准点坐标
			dos.writeShort(markInfor.getCoordinate().get(0).x);
			dos.writeShort(markInfor.getCoordinate().get(0).y);
			// 文字的分类种别
			dos.writeShort(markInfor.getLetterMark());
			// 显示比例尺
			dos.write(markInfor.getiTextVS());
			}
			
			// 文字Data 5
			if (6 < markInfor.getStrString().length() && 1 == markInfor.getCoordinate().size() && 0xbf90 != markInfor.getLetterMark()) {
				// 文字表示属性
				int data = 0;
				data = Util.setBits(data, 0, 0, 5);
				data = Util.setBit(data, 6, true);
				data = Util.setBit(data, 7, true);
				dos.write(data);
//				if (13 > markInfor.strString.length()) {
					
					// 行数，换两行
					dos.write(2);
					
					// 行管理记录信息
					
					// 第一行文字的个数
					int iLine1TextCnt = markInfor.getStrString().length() / 2 + markInfor.getStrString().length() % 2;
					if (Bracket.equals(markInfor.getStrString().substring(iLine1TextCnt - 1, iLine1TextCnt))) {
						iLine1TextCnt--;
					}
					// 第二行文字的个数
//					int iLine2TextCnt = markInfor.getStrString().length() - iLine1TextCnt;
					String markTxt1 = markInfor.getStrString().substring(0,iLine1TextCnt);
					String markTxt2 = markInfor.getStrString().substring(iLine1TextCnt);
					LetterInfo letterInfo = letter.get(markTxt1 + markTxt2);
					
					// 输出第一行文字信息
					dos.writeShort(letterInfo.getOffSet() / 2);
					dos.write(markTxt1.getBytes(TeleView.coding).length);
					dos.write(0);
					
					// 输出第二行文字信息
					letterInfo = letter.get(markTxt2 + markTxt1);
					dos.writeShort(letterInfo.getOffSet() / 2);
					dos.write(markTxt2.getBytes(TeleView.coding).length);
					dos.write(0);
			}else {
					//文字Data 1
				
					// Offset
					LetterInfo letterInfo = letter.get(markInfor.getStrString());
					if (letterInfo.getOffSet() % 2 != 0) {
						System.err.println("文字偏移量计算错误(不能被2整除) ： offset=" + letterInfo.getOffSet());
					}
					dos.writeShort(letterInfo.getOffSet() / 2);
					// 文字SIze
					dos.write(letterInfo.getSize());
					// 半角size 当前半角文字长度固定为0
					dos.write(0);
				}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("输出文字信息失败");
		}
	}
	public static void main(String[] args) {
		String string = new String("黄岩岛（民族礁）");
		System.err.println(string.substring(3, 4));
		System.err.println("ok");
	}
}
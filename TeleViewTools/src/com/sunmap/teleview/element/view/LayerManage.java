package com.sunmap.teleview.element.view;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sunmap.teleview.element.view.data.BackGroundData;
import com.sunmap.teleview.element.view.data.BlockID;
import com.sunmap.teleview.element.view.data.ElementData;
import com.sunmap.teleview.element.view.data.RoadData;
import com.sunmap.teleview.element.view.data.RoadTextLineData;
import com.sunmap.teleview.element.view.data.TeleViewText;
import com.sunmap.teleview.element.view.data.TxtMessage;

/**
 * 图层类
 * @author niujiwei
 */
public class LayerManage{

	public List<ElementData> elementDatas = new ArrayList<ElementData>();
	public short priority;
	// 有效的数据层，具体的数字参考数据格式文档
	private byte validLayerNumber[] = null;
	
	public LayerManage(){
		validLayerNumber = new byte[255];
		for(int i = 1; i <= 13; i++){
			validLayerNumber[i] = 1;
		}
		validLayerNumber[40] = 1;
		validLayerNumber[41] = 1;
		for(int i = 50; i <= 59; i++){
			validLayerNumber[i] = 1;
		}
		for(int i = 70; i <= 83; i++){
			validLayerNumber[i] = 1;
		}
		for(int i = 90; i <= 93; i++){
			validLayerNumber[i] = 1;
		}
		validLayerNumber[110] = 1;
		validLayerNumber[200] = 1;
	}
	
	/**
	 * 解析一个图层数据
	 * @param dis 数据流
	 * @param txtMessage 文字信息
	 */
	public void parse(DataInputStream dis, TxtMessage txtMessage) {
		try {
			List<ElementManageRec> elementManages = parseManageTable(dis);
			int graphicSize = dis.readUnsignedShort() * 2;
			calcElementSize(elementManages, graphicSize);

			for (ElementManageRec emr : elementManages) {
				// 如果数据不要求显示则直接跳过
				if(validLayerNumber[emr.layerNo] == 0){
					dis.skipBytes(emr.size);
					continue;
				}
				ElementData elementData = new ElementData();
				elementData.layerNo = emr.layerNo;
				elementData.parse(dis,emr, txtMessage);
				elementDatas.add(elementData);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算每个元素的数据块大小
	 * @param elementManages 元素管理表
	 * @param graphicSize 总的数据大小
	 */
	private void calcElementSize(List<ElementManageRec> elementManages,
			int graphicSize) {
		for (int i = 0; i < elementManages.size() - 1; i++) {
			elementManages.get(i).size = elementManages.get(i + 1).offset
					- elementManages.get(i).offset;
		}
		elementManages.get(elementManages.size() - 1).size = graphicSize
				- elementManages.get(elementManages.size() - 1).offset + 2;
	}
	
	/**
	 * 解析数据表头
	 * @param dis 数据流
	 * @return 数据管理表
	 */
	private List<ElementManageRec> parseManageTable(DataInputStream dis) {
		List<ElementManageRec> elementManages = new ArrayList<ElementManageRec>();
		try {
			short elementCount = (short) dis.readUnsignedByte();
			for (int i = 0; i < elementCount; i++) {
				ElementManageRec elementManage = new ElementManageRec();
				elementManage.read(dis);
				elementManages.add(elementManage);
			}
			int byteSize = 1 + elementManages.size() * 5;
			if (byteSize % 2 != 0) {
				dis.readByte();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return elementManages;
	}
	

	/**
	 *  Layer管理信息
	 * @author chenzhen
	 */
	public class ElementManageRec {
		public short layerNo;
		public int offset;
		public int graphicCount;
		public int size;

		public void read(DataInputStream dis) {
			try {
				layerNo = (short) dis.readUnsignedByte();
				offset = dis.readUnsignedShort() * 2;
				graphicCount = dis.readUnsignedShort();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 制作道路数据
	 * @param mapOverRoad
	 * @param mapRoad
	 * @param blockID
	 */
	public void makeRoadDrawData(Map<Short, GeneralPath> mapRoad, BlockID blockID) {
		for (int i = 0; i < elementDatas.size(); i++) {
			elementDatas.get(i).makePolylineDrawData(mapRoad, blockID,priority);
		}
	}
	
	/**
	 * 制作区域数据
	 * @param mapOverRoad
	 * @param mapRoad
	 * @param blockID
	 */
	public void makeRegionDrawData(Map<Short, GeneralPath> regionRoad, BlockID blockID) {
		for (int i = 0; i < elementDatas.size(); i++) {
			elementDatas.get(i).makePolylineDrawData(regionRoad, blockID,priority);
		}
	}
	
	/**
	 * 制作背景数据
	 * @param mapBg
	 * @param blockID
	 */
	public void makeBGDrawData(Map<Short, GeneralPath> mapBg, BlockID blockID) {
		for (int i = 0; i < elementDatas.size(); i++) {
			elementDatas.get(i).makePolygonDrawData(mapBg, blockID);
		}
	}
	/**
	 * 制作地标数据
	 * @param texts
	 * @param txtMessage
	 * @param blockID
	 */
	public void makeLandMarkDrawData(List<TeleViewText> texts, TxtMessage txtMessage, BlockID blockID){
		for (int i = 0; i < elementDatas.size(); i++) {
			elementDatas.get(i).makePointDrawData(texts, txtMessage, blockID);
		}
	}

	/**
	 * 分析道路文字线数据
	 * @param mapRoadTextLine
	 * @param texts
	 * @param txtMessage
	 * @param blockID
	 */
	public void makeRoadTextLineData(Map<Short, GeneralPath> mapRoadTextLine,BlockID blockID) {
		for (int i = 0; i < elementDatas.size(); i++) {
			elementDatas.get(i).makeRoadTextLineDrawData(mapRoadTextLine, blockID);
		}
	}
	
	/**
	 * 是否为行政区划
	 */
	public boolean isAdministrativeArea(short type) {
		if (((type & 0xFFFF) >= 0xBF82 && (type & 0xFFFF) <= 0xBF84) || 
			((type & 0xFFFF) >= 0xBF86 && (type & 0xFFFF) <= 0xbF88)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 根据点击的点查询线类型是否是符合要求的元素
	 * @param geoPoint  1/2560秒
	 * @param dis	像素距离
	 * @param blockID	
	 * @param flagString 
	 * @return
	 */
	public List<RoadData> selectLineByPix(Point2D.Float point, int dis,BlockID blockID, String flagString) {
		List<RoadData> result = new ArrayList<RoadData>();
			for (int i = 0; i < elementDatas.size(); i++) {
				List<RoadData> elsmens = elementDatas.get(i).selectLineByPix(point,dis,blockID,flagString);
				result.addAll(elsmens);
			}
		return result;
	}

	public List<BackGroundData> selectBgByPix(Point2D.Float point,BlockID blockID) {
		List<BackGroundData> result = new ArrayList<BackGroundData>();
		for (int i = 0; i < elementDatas.size(); i++) {
			List<BackGroundData> elsmens = elementDatas.get(i).selectBgByPix(point,blockID);
			result.addAll(elsmens);
		}
		return result;
	}
	
	public List<RoadTextLineData> selectRoadLineByPix(Point2D.Float point,int dis,BlockID blockID) {
		List<RoadTextLineData> result = new ArrayList<RoadTextLineData>();
		for (int i = 0; i < elementDatas.size(); i++) {
			List<RoadTextLineData> elsmens = elementDatas.get(i).selectRoadLineByPix(point,dis,blockID);
			result.addAll(elsmens);
		}
		return result;
	}
	
}

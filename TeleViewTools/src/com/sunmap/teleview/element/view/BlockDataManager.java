package com.sunmap.teleview.element.view;


import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sunmap.teleview.element.view.data.BackGroundData;
import com.sunmap.teleview.element.view.data.BlockData;
import com.sunmap.teleview.element.view.data.BlockID;
import com.sunmap.teleview.element.view.data.RoadData;
import com.sunmap.teleview.element.view.data.RoadTextLineData;
import com.sunmap.teleview.element.view.data.TeleViewText;
import com.sunmap.teleview.net.BlockedDataParser;
import com.sunmap.teleview.net.view.ViewServerResource;

public class BlockDataManager{
	private List<BlockData> blockDatas = new ArrayList<BlockData>();
	protected boolean cancelDrawFlag = false;
	
	public void draw(Graphics2D graphics, List<BlockData> blockdatas) {
		for (int i = 0; i < blockdatas.size(); i++) {
			if (cancelDrawFlag == true || DrawEleType.isDrawBg == false) {
				break;
			}
			BlockData stemp = blockdatas.get(i);
			stemp.drawBG(graphics);			
		}
		for (int i = 0; i < blockdatas.size(); i++) {
			if (cancelDrawFlag == true || DrawEleType.isDrawRegionLine == false) {
				break;
			}
			BlockData stemp = blockdatas.get(i);
			stemp.drawRegionLine(graphics);		
		}
		for (int i = 0; i < blockdatas.size(); i++) {
			if (cancelDrawFlag == true || DrawEleType.isDrawRoad == false) {
				break;
			}
			BlockData stemp = blockdatas.get(i);
			stemp.drawRoad(graphics);
		}
		for (int i = 0; i < blockdatas.size(); i++) {
			if (cancelDrawFlag == true || DrawEleType.isDrawRoadTextLine == false) {
				break;
			}
			BlockData stemp = blockdatas.get(i);
			stemp.drawRoadTextLine(graphics);
		}
		for (int i = 0; i < blockdatas.size(); i++) {
			if (cancelDrawFlag == true || DrawEleType.isDrawBlockEdg == false) {
				break;
			}
			BlockData stemp = blockdatas.get(i);
			stemp.drawBlockEdging(graphics);		
		}
		for (int i = 0; i < blockdatas.size(); i++) {
			if (cancelDrawFlag == true || DrawEleType.isDrawLandMark == false) {
				break;
			}
			BlockData stemp = blockdatas.get(i);
			stemp.drawLandMark(graphics);		
		}
	}

	/**
	 * 通过blockIDs取得blockData列表
	 * @param blockIDs
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	public List<BlockData> getData(List<BlockID> blockIDs) throws Exception {
		List<BlockData> retblockdatas = new ArrayList<BlockData>();
		List<BlockID> notInMemeryBlockIDs = new ArrayList<BlockID>();
		for (int i = 0; i < blockIDs.size(); i++) {
			BlockID stemp = blockIDs.get(i);
			BlockData blockData = getBlockData(stemp);
			if(blockData == null){
				notInMemeryBlockIDs.add(stemp);
			}else {
				retblockdatas.add(blockData);
			}
		}
		//不存在的BlockData下载解析
		if (notInMemeryBlockIDs.size() > 0) {
			//计算下载url
			String headflag = "10010002blk=";
			for (int i = 0; i < notInMemeryBlockIDs.size(); i++) {
				//是否取消描画
				if (cancelDrawFlag == true) {
					retblockdatas.clear();
					break;
				}
				BlockID stemp = notInMemeryBlockIDs.get(i);
				String downloadurl = headflag + URLEncoder.encode(stemp.getURL());
				//下载BlockData
				byte[] databuf = download(downloadurl);//下载
				//解析
				List<BlockData> parseResults = parse(databuf);//解析
				//添加到返回值
				retblockdatas.addAll(parseResults);
				//添加到内存
				for (int k = 0; k < parseResults.size(); k++) {
					BlockData blockData = parseResults.get(k);
					addBlockData(blockData);
				}
			}
	
		}
		//查找内存中不存在的BlockData
		Collections.sort(this.blockDatas, BlockData.compare);
		return retblockdatas;
	}
	
	/**
	 * 下载blockData数据
	 * @param urls
	 * @return
	 * @throws Exception 
	 */
	private byte[] download(String urls) throws Exception{
		ViewServerResource viewServerResource = new ViewServerResource();
		byte [] data = viewServerResource.retrieve(urls);
		return data;
	}
	
	/**
	 * 解析TeleView数据头部
	 * @param dis
	 */
	private BlockID parseHead(BlockedDataParser dis)
	{
		BlockID blockID = null;
		try {
			blockID = new BlockID();
			//由于数据被服务器重组，所以头部格式要参考“服务器终端通信协议”
			dis.readByte();
			blockID.level = dis.readByte();
			blockID.xIndex = dis.readInt();
			blockID.yIndex = dis.readInt();
			blockID.dataSize = dis.readUnsignedShort() * 2;
			dis.readByte();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return blockID;
	}
	
	/**
	 * 解析blockData数据
	 * @param databuf
	 * @throws IOException 
	 */
	private List<BlockData> parse(byte[] databuf) throws IOException{
		List<BlockData> blockDatas = new ArrayList<BlockData>();
		InputStream inputStream = new ByteArrayInputStream(databuf);
		DataInputStream dis = null;
		BlockedDataParser bdis = null;
		dis = new DataInputStream(inputStream);
		bdis = new BlockedDataParser(dis);

		byte[] bufdata = null;
		short blockCount;

		blockCount = bdis.readUnsignedByte();
		List<BlockID> blockIDs = new ArrayList<BlockID>();
		// 先解析返回数据头部
		for (int i = 0; i < blockCount; i++) {
			blockIDs.add(parseHead(bdis));
		}
		for (int i = 0; i < blockIDs.size(); i++) {
			BlockID blockID = blockIDs.get(i);
			BlockData blockData = new BlockData(blockID);
			bufdata = new byte[blockID.dataSize];
			bdis.read(bufdata);
			try {
				blockData.parse(bufdata);// layerManage和TxtMessage的解析
			} catch (Exception e) {
				e.printStackTrace();
			}

			blockDatas.add(blockData);
		}
		if (bdis != null) {
			bdis.close();
		}
		if (dis != null) {
			dis.close();
		}
		return blockDatas;
	}
	
	/**
	 * 通过blockID取得blockData
	 * @param blockID
	 * @return
	 */
	private BlockData getBlockData(BlockID blockID){
		BlockData stemp = new BlockData(blockID);
		int index = Collections.binarySearch(blockDatas, stemp, BlockData.compare);
		if (index < 0) {
			return null;
		}
		BlockData blockData = blockDatas.get(index);
		return blockData;
	}
	
	/**
	 * 往内存中添加BlockData
	 * @param blockData
	 */
	private void addBlockData(BlockData blockData){
		blockDatas.add(blockData);
	}

	public void clearViewData() {
		blockDatas.clear();
	}

	
	/**
	 * 根据点击的点查询线类型是否是符合要求的元素
	 * @param blockID	
	 * @param geoPoint  1/2560秒
	 * @param dis	像素距离
	 * @return
	 */
	public List<RoadData> pickUpRoads(List<BlockID> blockIDs, Point2D.Float point, int dis) {
		List<RoadData> roads = new ArrayList<RoadData>();
		for (int i = 0; i < blockIDs.size(); i++) {
			BlockID blockID =blockIDs.get(i);
			BlockData blockData = getBlockData(blockID);
			if(blockData != null){
				List<RoadData> result = blockData.selectLineByPix(point, dis,blockID, "Road");
				roads.addAll(result);
			}
		}
		return roads;
	}
	
	private List<RoadData> pickUpRegionLine(List<BlockID> blockIDs, Point2D.Float point, int dis) {
		List<RoadData> roads = new ArrayList<RoadData>();
		for (int i = 0; i < blockIDs.size(); i++) {
			BlockID blockID =blockIDs.get(i);
			BlockData blockData = getBlockData(blockID);
			if(blockData != null){
				List<RoadData> result = blockData.selectLineByPix(point, dis, blockID, "RegionLine");
				roads.addAll(result);
			}
		}
		return roads;
	}
	
	public List<RoadTextLineData> pickUpRoadLine(List<BlockID> blockIDs, Point2D.Float point, int dis) {
		List<RoadTextLineData> roads = new ArrayList<RoadTextLineData>();
		for (int i = 0; i < blockIDs.size(); i++) {
			BlockID blockID =blockIDs.get(i);
			BlockData blockData = getBlockData(blockID);
			if(blockData != null){
				List<RoadTextLineData> result = blockData.selectRoadLineByPix(point, dis,blockID);
				roads.addAll(result);
			}
		}
		return roads;
	}
	
	
	
	/**
	 * 根据点击的点查询线类型是否是符合要求的元素
	 * @param blockID	
	 * @param geoPoint  1/2560秒
	 * @param dis	像素距离
	 * @return
	 */
	public List<TeleViewText> pickUpLandMark(List<BlockID> blockIDs, Point2D.Float point, int dis) {
		List<TeleViewText> landMarks = new ArrayList<TeleViewText>();
		for (int i = 0; i < blockIDs.size(); i++) {
			BlockID blockID =blockIDs.get(i);
			BlockData blockData = getBlockData(blockID);
			if(blockData != null){
				List<TeleViewText> result = blockData.selectPointByPix(point, dis);
				landMarks.addAll(result);
			}
		}
		return landMarks;
	}
	
	/**
	 * 检索地标
	 * @param landMarkName 地标名称
	 * @param cityIndex 
	 * @param provinceIndex 
	 * @return
	 * @throws SQLException 
	 */
	public List<TeleViewText> selectLandMark(String landMarkName, String provinceName, String cityName) throws SQLException{
		List<TeleViewText> results = new ArrayList<TeleViewText>();
		for (int i = 0; i < blockDatas.size(); i++) {
			BlockData teleView = blockDatas.get(i);
			List<TeleViewText> landmarks = teleView.selectLandMark(landMarkName, provinceName, cityName);
			for (int j = 0; j < landmarks.size(); j++) {
				if (results.contains(landmarks.get(j))) {
					continue;
				}
				results.add(landmarks.get(j));
			}
		}
		return results;
	}
	
	/**
	 * 根据点击的点查询面类型是否是符合要求的元素
	 * @param blockID	
	 * @param geoPoint  1/2560秒
	 * @param dis	像素距离
	 * @return
	 */
	private List<Object> pickUpBg(List<BlockID> blockIDs, Point2D.Float point) {
		List<Object> retBg = new ArrayList<Object>();
		for (int i = 0; i < blockIDs.size(); i++) {
			BlockID blockID =blockIDs.get(i);
			BlockData blockData = getBlockData(blockID);
			if(blockData != null){
				List<BackGroundData> result = blockData.selectBgByPix(point);
				retBg.addAll(result);
			}
		}
		
		return retBg;
	}
	
	/**
	 * 对结果按距离进行升序排序
	 * @param pickUpRet
	 * @return
	 */
	private List<Map.Entry<Integer, Double>> getSortResult(Map<Integer, Double> pickUpRet){
		List<Map.Entry<Integer, Double>> resultList = new ArrayList<Map.Entry<Integer,Double>>(pickUpRet.entrySet());
		Collections.sort(resultList, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Entry<Integer, Double> left,
					Entry<Integer, Double> right) {
				if (left.getValue() - right.getValue() > 0) {
					return 1;
				}
				if (left.getValue() - right.getValue() < 0) {
					return -1;
				}
				return 0;
			}
		});
		return resultList;
	}
	
	public Map<List<Object>, Double> calcPickUpInfo(List<BlockID> blockIDs,Point2D.Float pointFloat, int dis){
		Map<List<Object>, Double> pickresultMap = new HashMap<List<Object>, Double>();
		if (DrawEleType.pickRegionLine) {
			List<RoadData> roads = pickUpRegionLine(blockIDs, pointFloat, dis);// 选择符合条件的道路
			List<Object> resultRoads = new ArrayList<Object>();
			double roadDis = sortRoads(roads, resultRoads, pointFloat);
			pickresultMap.put(resultRoads, roadDis);
		}
		if (DrawEleType.pickBg) {
			List<Object> bg = pickUpBg(blockIDs,pointFloat);// 选择符合条件的道路
			pickresultMap.put(bg, (double)0);
		}
		if (DrawEleType.pickLandMark) {
			List<TeleViewText> pois = pickUpLandMark(blockIDs, pointFloat, dis * 3);// 选择符合条件的POI
			List<Object> landMarks = new ArrayList<Object>();
			double landMarkDis = sortLandMark(pois, landMarks, pointFloat);
			pickresultMap.put(landMarks, landMarkDis);
		}
		if (DrawEleType.pickRoad) {
			List<RoadData> roads = pickUpRoads(blockIDs, pointFloat, dis);// 选择符合条件的道路
			List<Object> resultRoads = new ArrayList<Object>();
			double roadDis = sortRoads(roads, resultRoads, pointFloat);
			pickresultMap.put(resultRoads, roadDis);
		}
		if (DrawEleType.pickRoadTextLine) {
			List<RoadTextLineData> lines = pickUpRoadLine(blockIDs, pointFloat, dis * 3);// 选择符合条件的道路
			List<Object> resultRoads = new ArrayList<Object>();
			double roadTextDis = sortRoadTextLines(lines, resultRoads, pointFloat);
			pickresultMap.put(resultRoads, roadTextDis);
		}
		return pickresultMap;
	}
	
	/**
	 * 对地标的拾取结果按距离进行排序
	 * @param pois
	 * @param landMarks
	 * @param pointFloat
	 * @return
	 */
	private double sortLandMark(List<TeleViewText> pois, List<Object> landMarks, Float pointFloat) {
		Map<Integer, Double> pickUpRet = new HashMap<Integer, Double>(); 
		for (int i = 0; i < pois.size(); i++) {
			double dis = pois.get(i).calcDis(pointFloat);
			pickUpRet.put(i, dis);
		}
		if (pickUpRet.size() == 0) {
			return 0;
		}
		List<Map.Entry<Integer, Double>> resultList = getSortResult(pickUpRet);
		for (int i = 0; i < resultList.size(); i++) {
			int index = resultList.get(i).getKey();
			landMarks.add(pois.get(index));
		}
		return resultList.get(0).getValue();
	}


	/**
	 * 对道路的拾取结果按距离进行排序
	 * @param roads
	 * @param resultRoads
	 * @param pointFloat
	 * @return
	 */
	private double sortRoads(List<RoadData> roads, List<Object> resultRoads, Float pointFloat) {
		Map<Integer, Double> pickUpRet = new HashMap<Integer, Double>(); 
		for (int i = 0; i < roads.size(); i++) {
			double dis = roads.get(i).calcDis(pointFloat);
			pickUpRet.put(i, dis);
		}
		if (pickUpRet.size() == 0) {
			return 0;
		}
		List<Map.Entry<Integer, Double>> resultList = getSortResult(pickUpRet);
		for (int i = 0; i < resultList.size(); i++) {
			int index = resultList.get(i).getKey();
			resultRoads.add(roads.get(index));
		}
		return resultList.get(0).getValue();
	}
	
	/**
	 * 对道路文字线的拾取结果按距离进行排序
	 * @param roadTextLines
	 * @param resultRoads
	 * @param pointFloat
	 * @return
	 */
	private double sortRoadTextLines(List<RoadTextLineData> roadTextLines, List<Object> resultRoads, Float pointFloat) {
		Map<Integer, Double> pickUpRet = new HashMap<Integer, Double>(); 
		for (int i = 0; i < roadTextLines.size(); i++) {
			double dis = roadTextLines.get(i).calcDis(pointFloat);
			pickUpRet.put(i, dis);
		}
		if (pickUpRet.size() == 0) {
			return 0;
		}
		List<Map.Entry<Integer, Double>> resultList = getSortResult(pickUpRet);
		for (int i = 0; i < resultList.size(); i++) {
			int index = resultList.get(i).getKey();
			resultRoads.add(roadTextLines.get(index));
		}
		return resultList.get(0).getValue();
	}
	
	

}
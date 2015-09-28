package com.sunmap.teleview.element.mm;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sunmap.teleview.element.mm.data.MMBlockID;
import com.sunmap.teleview.element.mm.data.Road;
import com.sunmap.teleview.net.BlockedDataParser;
import com.sunmap.teleview.net.mapmatch.MMServerResource;
import com.sunmap.teleview.util.ToolsUnit;

public class MMBlockDataManager{
	public List<MMBlockID> mmBlockIDs = new ArrayList<MMBlockID>();//telemm数据
	public boolean cancelDrawFlag = false;

	
	/**
	 * 通过blockIDs取得blockData列表
	 * @param blockIDs
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	public List<MMBlockID> getData(List<MMBlockID> blockIDs) throws Exception {
		List<MMBlockID> retblockdatas = new ArrayList<MMBlockID>();
		List<MMBlockID> notInMemeryBlockIDs = new ArrayList<MMBlockID>();
		for (int i = 0; i < blockIDs.size(); i++) {
			MMBlockID stemp = blockIDs.get(i);
			MMBlockID blockData = getBlockData(stemp);
			if(blockData == null){
				notInMemeryBlockIDs.add(stemp);
			}else {
				retblockdatas.add(blockData);
			}
		}
		//不存在的BlockData下载解析
		if (notInMemeryBlockIDs.size() > 0) {
			//计算下载url
			String headflag = "10010001blk=";
			for (int i = 0; i < notInMemeryBlockIDs.size(); i++) {
				//是否取消描画
				if (cancelDrawFlag == true) {
					retblockdatas.clear();
					break;
				}
				MMBlockID stemp = notInMemeryBlockIDs.get(i);
				String downloadurl = headflag + URLEncoder.encode(stemp.getURL());
				//下载BlockData
				byte[] databuf = download(downloadurl);//下载
				//解析
				List<MMBlockID> parseResults = parser(databuf);//解析
				//添加到返回值
				retblockdatas.addAll(parseResults);
				//添加到内存
				for (int k = 0; k < parseResults.size(); k++) {
					MMBlockID blockData = parseResults.get(k);
					addBlockData(blockData);
				}
			}
	
		}
		//查找内存中不存在的BlockData
		Collections.sort(mmBlockIDs, MMBlockID.compareByIndex);
		return retblockdatas;
	}
	
	/**
	 * 通过blockID取得blockData
	 * @param blockID
	 * @return
	 */
	private MMBlockID getBlockData(MMBlockID blockID){
		int index = Collections.binarySearch(mmBlockIDs, blockID, MMBlockID.compareByIndex);
		if (index < 0) {
			return null;
		}
		MMBlockID blockData = mmBlockIDs.get(index);
		return blockData;
	}
	
	/**
	 * 往内存中添加MMBlockID
	 * @param mmBlockID
	 */
	private void addBlockData(MMBlockID mmBlockID){
		mmBlockIDs.add(mmBlockID);
	}

	public void clearMMData() {
		mmBlockIDs.clear();
	}
	
	private byte[] download(String url) throws Exception{
		MMServerResource mmServerResource = new MMServerResource();
		byte [] data = mmServerResource.retrieve(url);
		return data;
	}
	
	private List<MMBlockID> parser(byte[] data) throws IOException {
		List<MMBlockID> returnMMBlockIDs = new ArrayList<MMBlockID>();
		InputStream inputStream = new ByteArrayInputStream(data);
		DataInputStream dis = null;
		BlockedDataParser bdis = null;
		dis = new DataInputStream(inputStream);
		bdis = new BlockedDataParser(dis);
		short blockCount;

		blockCount = bdis.readUnsignedByte();
		// 先解析返回数据头部
		for (int i = 0; i < blockCount; i++) {
			MMBlockID blockID = parseHead(bdis);
			if (blockID.size > 0) {
				byte[] bufdata = null;
				bufdata = new byte[blockID.size];
				bdis.read(bufdata);
				blockID.parse(bufdata);
				returnMMBlockIDs.add(blockID);
			}
		}
		if (bdis != null) {
			bdis.close();
		}
		if (dis != null) {
			dis.close();
		}
		return returnMMBlockIDs;
	}



	/**
	 * 描画
	 * @throws IOException 
	 */
	public void draw(Graphics2D g, List<MMBlockID> blockDatas) throws Exception {
		//描画道路
		for (int i = 0; i < blockDatas.size(); i++) {
			if (cancelDrawFlag || DrawEleType.drawMM == false) {
				break;
			}
			MMBlockID blk = blockDatas.get(i);
			if (blk.roads != null) {
				blk.draw(g);
			}
		}
		//描画边框
		for (int i = 0; i < blockDatas.size(); i++) {
			if (cancelDrawFlag || DrawEleType.drawMMBlock == false) {
				break;
			}
			MMBlockID blk = blockDatas.get(i);
			blk.drawMMBlockID(g);
		}
	}
	
	

	/**
	 *   根据形状点查找MM信息
	 * @param point 像素点
	 * @param dis  像素距离
	 * @return 符合要求的MM信息
	 */
	public Map<List<Object>, Double> calcPickUpInfo(List<MMBlockID> blockDataIDs, Point2D.Float point, int dis) {
		List<Object> retObjects = new ArrayList<Object>();
		List<Road> result = new ArrayList<Road>();
		// 根据point计算blockids
		// 计算备选道路列表
		for (int i = 0; i < blockDataIDs.size(); i++) {
			for (int j = 0; j < mmBlockIDs.size(); j++) {
				if (mmBlockIDs.get(j).equals(blockDataIDs.get(i))) {
					MMBlockID blockID = mmBlockIDs.get(j);
					for (int k = 0; k < blockID.roads.size(); k++) {
						Road inRoad = blockID.roads.get(k);
						List<Point2D> list = new ArrayList<Point2D>();
						for (int l = 0; l < inRoad.pointxsf.length; l++) {
							//正规化坐标变成1/2560秒
							Point p = blockID.unitToGeo(inRoad.pointxsf[l], inRoad.pointysf[l]);
							//1/2560秒变成像素
							Point2D.Double pix = Device.geoToPix(p);
							list.add(pix);
						}
						double apeak = ToolsUnit.getApeak(list , point,true);
						if (apeak <= dis) {
							if (inRoad.points.size() != inRoad.pointxsf.length ) {
								inRoad.points.clear();
								for (int a = 0; a < inRoad.pointxsf.length; a++) {
								Point Geopoint = blockID.UnitToGeo(inRoad.pointxsf[a],inRoad.pointysf[a]);
								inRoad.points.add(Geopoint);
								}
							}
							if (result.contains(inRoad) == false) {
								result.add(inRoad);
							}
						}
					}
					break;
				}
			}
		}
		Map<List<Object>, Double> pickresultMap = new HashMap<List<Object>, Double>();
		double roadDis = sortRoads(result, retObjects, point);
		pickresultMap.put(retObjects, roadDis);
		return pickresultMap;
		
	}
	
	/**
	 * 对MM道路的拾取结果按距离进行排序
	 * @param roads
	 * @param resultRoads
	 * @param pointFloat
	 * @return
	 */
	private double sortRoads(List<Road> roads, List<Object> resultRoads, Float pointFloat) {
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
	

	public MMBlockID getBlockID(short x, short y) {
		for (int i = 0; i < mmBlockIDs.size(); i++) {
			MMBlockID id = mmBlockIDs.get(i);
			if (id.xIndex == x&&id.yIndex == y) {
				return id;
			}
		}
		return null;
	}
	

	/**
	 * 解析TeleMm数据头部
	 * 
	 * @param dis
	 */
	private MMBlockID parseHead(BlockedDataParser dis) {
		// TODO Auto-generated method stub
		MMBlockID blockID = null;
		try {
			// 由于数据被服务器重组，所以头部格式要参�?“服务器终端通信协议�?
			dis.readByte();
			// BLOCK X方向编号
			int xIndex = dis.readInt();
			// BLOCK Y方向编号
			int yIndex = dis.readInt();
			blockID = new MMBlockID(xIndex, yIndex);
			// BLOCK 数据大小
			blockID.size = dis.readShort() - 11;
		} catch (IOException e) {
		}
		return blockID;
	}

	public void cancelDraw(){
		cancelDrawFlag = true;
	}
	
	public void revertDraw(){
		cancelDrawFlag = false;
	}
	
	public void clear(){
		mmBlockIDs.clear();
	}

	
}

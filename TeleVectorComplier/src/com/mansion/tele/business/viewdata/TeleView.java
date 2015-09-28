package com.mansion.tele.business.viewdata;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mansion.tele.business.Task;
import com.mansion.tele.business.TaskData.VectoryStyle;
import com.mansion.tele.business.background.Area;
import com.mansion.tele.business.background.Line;
import com.mansion.tele.business.common.BaseShape;
import com.mansion.tele.business.common.LetterInfo;
import com.mansion.tele.business.common.MarkInfor;
import com.mansion.tele.business.common.TelePolygonShp;
import com.mansion.tele.business.common.TelePolylineShp;
import com.mansion.tele.business.common.TeleRoadPloylineShp;
import com.mansion.tele.business.network.Route;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.LayerNo;

/**
 * 
 * @author Administrator
 * 
 */
public class TeleView extends TeleUnit {
	/** 矢量数据结果 */
	Map<BlockNo, Map<LayerNo, List<BaseShape>>> targetDataMap 
	= new HashMap<BlockNo, Map<LayerNo, List<BaseShape>>>();

	/**
	 * 按名称排序
	 */
	private Comparator<MarkInfor> compareUpLevelMarkInfor = new Comparator<MarkInfor>() {

		/**
		 * 比较方法
		 * @param o1 	第一个对象
		 * @param o2	第二个对象
		 * @return		这两个对象比较的值
		 */
		public int compare(MarkInfor o1, MarkInfor o2) {
			// TODO Auto-generated method stub
			int iTemp = 0;
			if (0 != o1.getStrString().compareTo(o2.getStrString())) {
				
				iTemp = o1.getStrString().compareTo(o2.getStrString());
			}

			

			return iTemp;
		}

	};
	
	/**
	 * 显示层级排序
	 */
	private static Comparator<MarkInfor> comparableMarkPri = new Comparator<MarkInfor>() {

		@Override
		public int compare(MarkInfor o1, MarkInfor o2) {
			// TODO Auto-generated method stub
			if (o1.getiPri() != o2.getiPri()) {
				return o1.getiPri() - o2.getiPri();
			}
			return 0;
		}

	};

	/**
	 * UTF-8编码
	 */
	public static String coding = "UTF-8";

	/**
	 * 文件后缀名
	 */
	private String fileEnd = ".tuv";

	/**
	 * 组织View所需block信息
	 * 
	 * @param level
	 *            制作层级
	 * @param blickNo
	 *            block号
	 * @return byte[] 返回block信息
	 * @throws Exception
	 *             1
	 */
	@Override
	protected byte[] makeBlock(byte level, BlockNo blickNo) throws Exception {
		byte[] blockBody = this.makeBlockBody(blickNo);
		if(blockBody.length==0){
			return blockBody;
		}
		return this.addBlockHeader(level, blickNo, blockBody);
	}

	/**
	 * 组织block内信息
	 * 
	 * @param blickNo
	 *            block号
	 * @return byte[] 返回block信息结构
	 * @throws Exception
	 *             1
	 */
	@SuppressWarnings("unchecked")
	protected byte[] makeBlockBody(BlockNo blickNo) throws Exception {

		// 存储文字信息
		StringBuffer letterInfo = null;
		// 该map的key为文字信息字符串，value中存放一个leafinfo对象，该对象有文字信息的大小，和偏移量
		Map<String, LetterInfo> letterMap = null;

		Map<LayerNo, List<BaseShape>> allshp = this.targetDataMap.get(blickNo);
		// block中的道路
		List<LayerNo> roadShp = new ArrayList<LayerNo>();
		// block中非道路的NO
		List<LayerNo> notRoadNO = new ArrayList<LayerNo>();
		// 收集数据区分其是否道路类型
		this.judgeIsNotTeleRoad(allshp, roadShp, notRoadNO);

		List<BaseShape> roadShpList = this
				.clearRoadRepeatPoint(allshp, roadShp);
		// 第一个Key为显示层级，第二个Key为要素Code
		Map<Byte, Map<LayerNo, List<BaseShape>>> elementKindMap = new HashMap<Byte, Map<LayerNo, List<BaseShape>>>();

		// 保存道路
		elementKindMap = this.collectRoad(roadShpList);

		// 保存非道路数据
		this.collectNoRoad(allshp, elementKindMap);
		if(elementKindMap.isEmpty()){
			return new byte[0];
		}

		List<Byte> roadGradeList = new ArrayList<Byte>();
		for (byte grade : elementKindMap.keySet()) {
			roadGradeList.add(grade);
		}
		// 对压垮由小到大排序
		Collections.sort(roadGradeList);

		// 存储每个显示层级的数据
		List<byte[]> dispGradeData = new ArrayList<byte[]>();
		// 存储要素Table数据
		List<byte[]> elementTableData = new ArrayList<byte[]>();

		// 显示层级个数
		int iDispGradeCount = 0;

		// 存储每个要素数据相对文件的偏移量
		int iElementDataOffect = 0;

		for (int i = 0; i < roadGradeList.size(); i++) {

			Map<LayerNo, List<BaseShape>> elementDataMap = elementKindMap
					.get(roadGradeList.get(i));
			// 同一个显示层级按要素种别由小到大排序
			List<LayerNo> elementKindCode = new ArrayList<LayerNo>();
			elementKindCode = this.getElementKindCode(elementDataMap);

			// 存储一个显示层级的要素信息
			List<byte[]> elementOneData = new ArrayList<byte[]>();
			// 存储一个显示层级的要素形状数据
			List<byte[]> elementShapOneData = new ArrayList<byte[]>();
			// 存储每个要素形状数据相对图形信息的偏移量
			int iElementShpdataOffect = 2;
			// 当前显示层级中在要素个数
			int iElementCnt = 0;
			// 要素形状数据size
			int iShapeSizeInElement = 0;

			// 组装一个显示层级的要素管理信息
			for (LayerNo layerNo : elementKindCode) {
				byte[] elementData = null;
				byte[] elementShapData = null;
				
				List<BaseShape> baseShape = elementDataMap.get(layerNo);
				
				// 背景面
				elementShapData = this.makeBackPolygon(
						elementShapData, baseShape);
				// 背景,线
				elementShapData = this.makeBackPloyline(
						elementShapData, baseShape);
				// 标记
//				elementShapData = this.makeMark(elementShapData, layerNo, baseShape, letterMap, letterInfo);
				// 标记
				if (!baseShape.isEmpty() && baseShape.get(0) instanceof MarkInfor) {
					if (letterMap == null) {
						letterMap = new HashMap<String, LetterInfo>();
					}
					if (letterInfo == null) {
						letterInfo = new StringBuffer();
					}
					List<MarkInfor> teleMarkList = new ArrayList<MarkInfor>();
					// 转换类型
					teleMarkList = (List<MarkInfor>) this.typeChange(baseShape);

					// 标记文字按优先级排序
					Collections.sort(teleMarkList, comparableMarkPri);
					// 再按照code排序
					
					for (int x = 0, j = 0; x < teleMarkList.size()-1; x++) {
						List<MarkInfor> list=new ArrayList<MarkInfor>();
						if (teleMarkList.get(x).getiPri() != teleMarkList.get(x + 1).getiPri()) {
							x=x+1;
						}
						else{
							list=teleMarkList.subList(j, x);
							//如果这个cood类型只有一个就不做判断
							if (list.size()<2) {
								
								j=x;
								continue;
							}
							Collections.sort(teleMarkList.subList(j, x), this.compareUpLevelMarkInfor);
							j = x;
						}
					}
					// 组装一个图像data部数据
					elementShapData = this.letterSign(layerNo, teleMarkList,
							letterInfo, letterMap);
				}
				
				
				// 道路
				elementShapData = this.makeRoad(elementShapData, baseShape);
				if(layerNo == null || baseShape == null || baseShape.isEmpty()){
//					System.out.println(layerNo+"baseShape is null");
				}else{
				// 组装一个要素信息
					elementData = this.assembleElementData(layerNo.iid, iElementShpdataOffect, baseShape.size());
					
					elementOneData.add(elementData);
					elementShapOneData.add(elementShapData);
					iShapeSizeInElement += elementShapData.length;
					iElementShpdataOffect += elementShapData.length;

					iElementCnt++;
				}
			}
//				// 道路
//				elementShapData = this.makeRoad(elementShapData, baseShape);
//				if(layerNo == null){
//					System.out.println("layerN0 is null");
//				}
//				if(baseShape == null || baseShape.isEmpty()){
//					System.out.println("baseShape is null");
//				}
//				if(layerNo.iid == LayerNo.WaterLine.iid){
//					layerNo.iid = LayerNo.Water.iid;
//				}
//				// 组装一个要素信息
//				elementData = this.assembleElementData(layerNo.iid,
//						iElementShpdataOffect, baseShape.size());
//				elementOneData.add(elementData);
//				elementShapOneData.add(elementShapData);
//				iShapeSizeInElement += elementShapData.length;
//				iElementShpdataOffect += elementShapData.length;
//
//				iElementCnt++;
//			}

			// 组装一个显示层级的所有要素信息
			byte[] allElementDataOfGrade = this.assembleAllElementOfGrade(
					iElementCnt, elementOneData);
			// 组装一个显示层级的所有图形
			byte[] allShpDataOfGrade = this.makeShpData(elementShapOneData,
					iShapeSizeInElement);

			// 当前显示层级的要素Table数据
			byte[] elementTableDataofGrade = new byte[allElementDataOfGrade.length
					+ allShpDataOfGrade.length];
			int iElementSize = 0;
			for (int j = 0; j < allElementDataOfGrade.length; j++) {
				elementTableDataofGrade[iElementSize++] = allElementDataOfGrade[j];
			}
			for (int j = 0; j < allShpDataOfGrade.length; j++) {
				elementTableDataofGrade[iElementSize++] = allShpDataOfGrade[j];
			}
			// 组装一个显示层级信息
			byte[] dispGrateData = this.assembleDispGradeData(
					iElementDataOffect, elementTableDataofGrade.length,
					roadGradeList.get(i));
			dispGradeData.add(dispGrateData);
			iElementDataOffect += elementTableDataofGrade.length;
			// 存储要素Table数据
			elementTableData.add(elementTableDataofGrade);

			++iDispGradeCount;
		}
		// 显示层级信息
		byte[] dispGrade = this.makeDispGrade(iDispGradeCount, dispGradeData);
		// 文字信息
		byte[] txt = this.makedLetter(letterInfo);
		// 要素Table信息
		byte[] elementTable = this.makeElementTable(elementTableData);

		byte[] blockBody = this.assembleBlockBody(dispGrade, txt, elementTable);

		return blockBody;
	}

	/**
	 * 道路去重复点
	 * 
	 * @param allshp
	 *            Map
	 * @param roadShp
	 *            List
	 * @return List
	 */
	private List<BaseShape> clearRoadRepeatPoint(
			Map<LayerNo, List<BaseShape>> allshp, List<LayerNo> roadShp) {
		// 当前Block中的道路数据
		List<BaseShape> roadShpList = new ArrayList<BaseShape>();
		for (int i = 0; i < roadShp.size(); i++) {
			// 取得道路的形状
			List<BaseShape> baseShape = this.clearRepeatPoint(allshp
					.get(roadShp.get(i)));
			if (baseShape == null || baseShape.size() == 0) {
				continue;
			}

			roadShpList.addAll(baseShape);
		}
		return roadShpList;
	}
	/**
	 * 制作道路数据
	 * 
	 * @param oldelementShapData
	 *            byte
	 * @param baseShape
	 *            List
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] makeRoad(byte[] oldelementShapData, List<BaseShape> baseShape)
			throws Exception {
		byte[] elementShapData = oldelementShapData;
		if (!baseShape.isEmpty() && baseShape.get(0) instanceof TeleRoadPloylineShp) {
			List<TeleRoadPloylineShp> teleRoadPloylineShpList = new ArrayList<TeleRoadPloylineShp>();
			// 转换类型
			teleRoadPloylineShpList = (List<TeleRoadPloylineShp>) this
					.typeChange(baseShape);
			// 组装一个图形信息
			elementShapData = this.roadPolyline(teleRoadPloylineShpList);
		}
		return elementShapData;
	}

	/**
	 * 制作背景，线数据
	 * 
	 * @param layerNo
	 *            LayerNo
	 * @param oldelementShapData
	 *            byte
	 * @param baseShape
	 *            List
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] makeBackPloyline(byte[] oldelementShapData,
			List<BaseShape> baseShape) throws Exception {
		byte[] elementShapData = oldelementShapData;

		if (!baseShape.isEmpty() && baseShape.get(0) instanceof TelePolylineShp) {
			List<TelePolylineShp> telePolylineShpList = new ArrayList<TelePolylineShp>();
			// 转换类型
			telePolylineShpList = (List<TelePolylineShp>) this
					.typeChange(baseShape);
			// 组装一个图像data部数据
			elementShapData = this.polylineToBuffer(telePolylineShpList);
		}
		return elementShapData;
	}

	/**
	 * 制作背景面
	 * 
	 * @param layerNo
	 *            LayerNo
	 * @param oldelementShapData
	 *            byte
	 * @param baseShape
	 *            List
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] makeBackPolygon(byte[] oldelementShapData,
			List<BaseShape> baseShape) throws Exception {

		byte[] elementShapData = oldelementShapData;

		// 背景,面
		if (!baseShape.isEmpty() && baseShape.get(0) instanceof TelePolygonShp) {
			List<TelePolygonShp> telePolygonShpList = new ArrayList<TelePolygonShp>();
			// 转换类型
			telePolygonShpList = (List<TelePolygonShp>) this
					.typeChange(baseShape);
			// 组装一个图像data部数据
			elementShapData = this.polygon(telePolygonShpList);
		}
		return elementShapData;
	}

	/**
	 * 整理道路信息数据
	 * 
	 * @param roadShpList
	 *            List<BaseShape>
	 * @return Map<Byte, Map<LayerNo, List<BaseShape>>>
	 */
	private Map<Byte, Map<LayerNo, List<BaseShape>>> collectRoad(
			List<BaseShape> roadShpList) {
		Map<Byte, Map<LayerNo, List<BaseShape>>> elementKindMap = new HashMap<Byte, Map<LayerNo, List<BaseShape>>>();
		if (roadShpList != null || roadShpList.size() != 0) {
			Map<Byte, List<BaseShape>> roadShpMap = new HashMap<Byte, List<BaseShape>>();
			// 合并相同跨压层级的道路
			roadShpMap = this.makeRoadShpByGrade(roadShpList);

			// 按要素种别区分道路种别相同的道路
			elementKindMap = this.makeElementKindBySameGrade(roadShpMap);
		}
		return elementKindMap;
	}

	/**
	 * 整理非道路数据
	 * 
	 * @param allshp
	 *            Map<LayerNo, List<BaseShape>> 全部数据信息
	 * @param elementKindMap
	 *            Map<Byte, Map<LayerNo, List<BaseShape>>> 区分不同数据类型信息
	 */
	private void collectNoRoad(Map<LayerNo, List<BaseShape>> allshp,
			Map<Byte, Map<LayerNo, List<BaseShape>>> elementKindMap) {
		for (Entry<LayerNo, List<BaseShape>> layerLoop : allshp.entrySet()) {
			byte bGrad = (byte) layerLoop.getKey().iGrad;
			if (0 == layerLoop.getKey().iGrad) { // 道路已经保存过了，不再保存。
				continue;
			}

			// 取得所属跨压层次
			Map<LayerNo, List<BaseShape>> notRoadMapTemp = elementKindMap
					.get(bGrad);
			if (null == notRoadMapTemp) {
				notRoadMapTemp = new HashMap<LayerNo, List<BaseShape>>();
				elementKindMap.put((byte) bGrad, notRoadMapTemp);
			}
			// 取得所属Layer
			List<BaseShape> baseShapeList = notRoadMapTemp.get(layerLoop
					.getKey());
			if (null == baseShapeList) {
				baseShapeList = new ArrayList<BaseShape>();
				notRoadMapTemp.put(layerLoop.getKey(), baseShapeList);
			}
			baseShapeList.addAll(layerLoop.getValue());
		}
	}

	/**
	 * 判断收集的数据是否是Road类型
	 * 
	 * @param allshp
	 *            Map<LayerNo, List<BaseShape>>
	 * @param roadShp
	 *            List<LayerNo>
	 * @param notRoadNO
	 *            List<LayerNo>
	 */
	private void judgeIsNotTeleRoad(Map<LayerNo, List<BaseShape>> allshp,
			List<LayerNo> roadShp, List<LayerNo> notRoadNO) {
		for (LayerNo layerNo : allshp.keySet()) {
			if ((layerNo.iid < LayerNo.RoadOTHERROAD.iid || layerNo.iid > LayerNo.RoadFREEWAY.iid)
					|| layerNo.iid == LayerNo.Mark.iid) {
				notRoadNO.add(layerNo);
			} else {

				// 将道路跨压设置为0 begin
				List<BaseShape> s = allshp.get(layerNo);
				List<BaseShape> s1 = new ArrayList<BaseShape>();
				for (int i = 0; i < s.size(); i++) {
					TeleRoadPloylineShp roadPloylineShp = (TeleRoadPloylineShp) s
							.get(i);
					// roadPloylineShp.grade = 0;
					s1.add(roadPloylineShp);
				}
				allshp.put(layerNo, s1);
				// 将道路跨压设置为0 end

				roadShp.add(layerNo);
			}
		}
	}

	/**
	 * 根据不同的压垮信息整合道路信息
	 * 
	 * @param roadShpList
	 *            List<BaseShape>
	 * @return Map<Byte, List<BaseShape>>
	 */
	private Map<Byte, List<BaseShape>> makeRoadShpByGrade(
			List<BaseShape> roadShpList) {
		Map<Byte, List<BaseShape>> teleRoadPloylineShpMap = new HashMap<Byte, List<BaseShape>>();
		List<TeleRoadPloylineShp> teleRoadPloylineShpList = (List<TeleRoadPloylineShp>) this
				.typeChange(roadShpList);

		for (TeleRoadPloylineShp teleRoadPloylineShp : teleRoadPloylineShpList) {
			if (teleRoadPloylineShpMap.get(teleRoadPloylineShp.getGrade()) == null) {
				List<BaseShape> roadPloylineShpList = new ArrayList<BaseShape>();
				roadPloylineShpList.add(teleRoadPloylineShp);
				teleRoadPloylineShpMap.put(teleRoadPloylineShp.getGrade(),
						roadPloylineShpList);
			} else {
				teleRoadPloylineShpMap.get(teleRoadPloylineShp.getGrade()).add(
						teleRoadPloylineShp);
			}
		}
		return teleRoadPloylineShpMap;
	}

	/**
	 * 区分垮压相同的道路信息
	 * 
	 * @param roadShpMap
	 *            显示层级相同的所有数据
	 * @return 显示层级相同的数据按要素种别区分
	 */
	private Map<Byte, Map<LayerNo, List<BaseShape>>> makeElementKindBySameGrade(
			Map<Byte, List<BaseShape>> roadShpMap) {

		// key是显示层级
		Map<Byte, Map<LayerNo, List<BaseShape>>> elementKindMap = new HashMap<Byte, Map<LayerNo, List<BaseShape>>>();

		for (Byte bDispGrade : roadShpMap.keySet()) {

			// 要素种别对应在道路信息, key是要素种别
			Map<LayerNo, List<BaseShape>> elementkind = new HashMap<LayerNo, List<BaseShape>>();

			List<BaseShape> sameDispGradeRoad = roadShpMap.get(bDispGrade);
			LayerNo layerNo;
			for (int i = 0; i < sameDispGradeRoad.size(); i++) {
				TeleRoadPloylineShp teleRoadPloylineShpTemPloylineShp = new TeleRoadPloylineShp();
				teleRoadPloylineShpTemPloylineShp = (TeleRoadPloylineShp) sameDispGradeRoad
						.get(i);
				layerNo = LayerNo.layerMap.get().get((int) teleRoadPloylineShpTemPloylineShp
								.getLayerNO());
				if (elementkind.containsKey(layerNo)) {
					elementkind.get(layerNo).add(sameDispGradeRoad.get(i));
				} else {
					List<BaseShape> roadTempList = new ArrayList<BaseShape>();
					roadTempList.add(sameDispGradeRoad.get(i));
					elementkind.put(layerNo, roadTempList);
				}
			}

			elementKindMap.put(bDispGrade, elementkind);
		}

		return elementKindMap;
	}

	/**
	 * 取得一个显示层级中的所有要素的KindCode
	 * 
	 * @param elementData
	 *            Map<LayerNo, List<BaseShape>>
	 * @return List<LayerNo>
	 */
	private List<LayerNo> getElementKindCode(
			Map<LayerNo, List<BaseShape>> elementData) {
		List<LayerNo> elementKindCode = new ArrayList<LayerNo>();
		for (LayerNo codeByte : elementData.keySet()) {
			if(codeByte != null){
				elementKindCode.add(codeByte);
			}
		}
		// 按要素种别由小到大排序
		Collections.sort(elementKindCode);

		return elementKindCode;
	}

	/**
	 * 组装完整的显示层级信息
	 * 
	 * @param iDispGradeCount
	 *            int
	 * @param dispGradeData
	 *            List<byte []>
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] makeDispGrade(int iDispGradeCount, List<byte[]> dispGradeData)
			throws Exception {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		dos.writeByte(iDispGradeCount);
		for (int i = 0; i < dispGradeData.size(); i++) {
			dos.write(dispGradeData.get(i));
		}
		// 字节对齐
		if (dos.size() % 2 != 0) {
			dos.write(0);
		}
		dos.close();

		return bao.toByteArray();
	}

	/**
	 * 组装完整的要素Table信息
	 * 
	 * @param elementTableData
	 *            List<byte[]>
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] makeElementTable(List<byte[]> elementTableData)
			throws Exception {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		for (int i = 0; i < elementTableData.size(); i++) {
			dos.write(elementTableData.get(i));
		}
		if (dos.size() % 2 != 0) {
			dos.write(0);
		}
		dos.close();

		return bao.toByteArray();
	}

	/**
	 * 组合Block体
	 * 
	 * @param dispGrade
	 *            byte 跨压
	 * @param txt
	 *            byte 文字
	 * @param elementTable
	 *            byte 要素信息
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] assembleBlockBody(byte[] dispGrade, byte[] txt,
			byte[] elementTable) throws Exception {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		dos.write(dispGrade, 0, dispGrade.length);
		dos.write(txt, 0, txt.length);
		dos.write(elementTable, 0, elementTable.length);
		dos.close();

		return bao.toByteArray();
	}

	/**
	 * 组织block文件data
	 * 
	 * @param level
	 *            byte
	 * @param blickNo
	 *            BlockNo
	 * @param blockBody
	 *            byte
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] addBlockHeader(byte level, BlockNo blickNo, byte[] blockBody)
			throws Exception {
		// blockBody.length 为Layer信息管理Table、文字信息 、图形信息排列 长度
		// 12为文件头固定长度
		// 1为文件结束标识长度

		final int headFinalLength = 12;
		int blockSize = blockBody.length + headFinalLength + 1;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		dos.write(level);
		dos.writeInt(blickNo.getiX());
		dos.writeInt(blickNo.getiY());
		if (blockSize % 2 != 0) {
			dos.writeShort((blockSize + 1 - headFinalLength) / 2);
		} else {
			dos.writeShort((blockSize - headFinalLength) / 2);
		}
		dos.write(0);
		dos.write(blockBody);
		dos.write(0);
		if (blockSize % 2 != 0) {
			dos.write(0);
		}
		dos.close();

		return bao.toByteArray();

	}

	/**
	 * 组织文字信息二进制
	 * 
	 * @param letterInfo
	 *            StringBuffer
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] makedLetter(StringBuffer letterInfo) throws Exception {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);


		if (null == letterInfo || 0 == letterInfo.length()) {
			dos.writeShort(0);
		} else {
			byte[] letterArray = letterInfo.toString().getBytes(coding);

			if (letterArray.length % 2 != 0) {
				dos.writeShort((letterArray.length + 1) / 2);
				dos.write(letterArray, 0, letterArray.length);
				dos.write(0);
			} else {
				dos.writeShort(letterArray.length / 2);
				dos.write(letterArray);
			}
		}
		dos.close();

		return bao.toByteArray();
	}

	/**
	 * 组装完整的图形信息（一个block）
	 * 
	 * @param shapData
	 *            List
	 * @param oldsize
	 *            Integer
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] makeShpData(List<byte[]> shapData, Integer oldsize)
			throws Exception {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);
		int size = oldsize;
		if (size % 2 != 0) {
			size += 1;
			System.out.println("图形数据大小为奇数，不符合条件！");
		}
		dos.writeShort(size / 2);
		for (byte[] buffer : shapData) {
			dos.write(buffer);
		}
		dos.close();

		return bao.toByteArray();
	}

	/**
	 * 组装一个显示层级信息
	 * 
	 * @param iOffsetToElementData
	 *            int
	 * @param iSize
	 *            int
	 * @param bDispGrade
	 *            byte
	 * @return byte
	 * @throws Exception
	 *             1
	 */

	private byte[] assembleDispGradeData(int iOffsetToElementData, int iSize,
			byte bDispGrade) throws Exception {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		// offset
		dos.writeShort(iOffsetToElementData / 2);
		// size
		dos.writeShort(iSize / 2);
		// 相对跨压层次
		dos.write(bDispGrade);
		dos.close();

		return bao.toByteArray();
	}

	/**
	 * 组装要素数据
	 * 
	 * @param iElementCode
	 *            int
	 * @param iOffsetToShape
	 *            int
	 * @param iShpCnt
	 *            int
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] assembleElementData(int iElementCode, int iOffsetToShape,
			int iShpCnt) throws Exception {
		if(iElementCode ==  VectoryStyle.WaterLine){
			iElementCode = VectoryStyle.Water;
		}
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		// 要素种别
		dos.write(iElementCode);
		// offset
		dos.writeShort(iOffsetToShape / 2);
		// 图形数
		dos.writeShort(iShpCnt);

		dos.close();

		return bao.toByteArray();

	}

	/**
	 * 组装一个显示层级中的所有要素数据
	 * 
	 * @param iElementCnt
	 *            int
	 * @param elementData
	 *            List<byte[]>
	 * @return byte
	 * @throws Exception
	 *             1
	 * 
	 */
	private byte[] assembleAllElementOfGrade(int iElementCnt,
			List<byte[]> elementData) throws Exception {

		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		dos.writeByte(iElementCnt);
		for (int i = 0; i < elementData.size(); i++) {
			dos.write(elementData.get(i));
		}
		if (dos.size() % 2 != 0) {
			dos.write(0);
		}
		dos.close();

		return bao.toByteArray();
	}

	/**
	 * 转换数据类型
	 * 
	 * @param baseShape
	 *            List<BaseShape>
	 * @return List
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<?> typeChange(List<BaseShape> baseShape) {
		List shape = new ArrayList();
		for (int i = 0; i < baseShape.size(); i++) {
			shape.add(baseShape.get(i));
		}
		return shape;
	}

	/**
	 * 组合文字信息（一个block）
	 * 
	 * @param markInfor
	 *            MarkInfor
	 * @param letterInfo
	 *            StringBuffer
	 * @param letterMap
	 *            Map
	 * @throws Exception
	 *             1
	 */
	private void letterInfo(MarkInfor markInfor, StringBuffer letterInfo,
			Map<String, LetterInfo> letterMap) throws Exception {

		if (markInfor.getStrString() != null) {
			int letterInfoOffext = letterInfo.toString().getBytes(coding).length;

			if (letterMakeLine(markInfor)) {
				int iLine1TextCnt = markInfor.getStrString().length() / 2
						+ markInfor.getStrString().length() % 2;
				if (0 == markInfor.getiIconVS()
						&& ShpVectorUtil.Bracket.equals(markInfor
								.getStrString().substring(iLine1TextCnt - 1,
										iLine1TextCnt))) {
					iLine1TextCnt--;
				}
				String markTxt1 = markInfor.getStrString().substring(0,
						iLine1TextCnt);
				String markTxt2 = markInfor.getStrString().substring(
						iLine1TextCnt);
				// if(letterMap.get(markTxt1) != null && letterMap.get(markTxt2)
				// != null){
				// return;
				// }
				byte[] bInfo1 = markTxt1.getBytes(coding);
				byte[] bInfo2 = markTxt2.getBytes(coding);
				letterInfo.append(new String(bInfo1, coding));



				if (bInfo1.length % 2 != 0) {
					letterInfo.append("0");
				}
				LetterInfo info = new LetterInfo();
				info.setOffSet(letterInfoOffext);
				info.setSize(bInfo1.length);
				letterMap.put(markTxt1 + markTxt2, info);
				letterInfoOffext = letterInfo.toString().getBytes(coding).length;
				letterInfo.append(new String(bInfo2, coding));

				if (bInfo2.length % 2 != 0) {
					letterInfo.append("0");
				}
				LetterInfo info1 = new LetterInfo();
				info1.setOffSet(letterInfoOffext);
				info1.setSize(bInfo2.length);
				letterMap.put(markTxt2 + markTxt1, info1);

			} else {
				if (letterMap.get(markInfor.getStrString()) != null) {
					return;
				}
				byte[] bInfo = markInfor.getStrString().getBytes(coding);



				letterInfo.append(new String(bInfo, coding));



				if (bInfo.length % 2 != 0) {
					letterInfo.append("0");
				}
				LetterInfo info = new LetterInfo();
				info.setOffSet(letterInfoOffext);
				info.setSize(bInfo.length);
				letterMap.put(markInfor.getStrString(), info);
			}

			// }
		}

	}

	/**
	 * 判断letter是否分行
	 * 
	 * @param markInfor
	 *            文字信息
	 * @return
	 */
	private boolean letterMakeLine(MarkInfor markInfor) {
		boolean flag = false;
		if (6 < markInfor.getStrString().length()
				&& 0xbf90 != markInfor.getLetterMark()) {
			if (markInfor.getiIconVS() != 0 && markInfor.getiTextVS() == 0) {
				flag = false;
			} else if (markInfor.getiIconVS() == 0
					&& 1 != markInfor.getCoordinate().size()) {
				flag = false;
			} else {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 获得背景,线二进制byte数组
	 * 
	 * @param layerNo
	 *            LayerNo
	 * @param telePolylineShpList
	 *            List
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] polylineToBuffer(List<TelePolylineShp> telePolylineShpList) throws Exception {
		// 调用工具类，将数据转换为byte数组
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		final int Roadinfo = 11;
		int iSize = 0;
		dos.write(Roadinfo);
		iSize += 1;
		for (TelePolylineShp telePolylineShp : telePolylineShpList) {
			byte[] data = null;
			data = ShpVectorUtil.getVector(telePolylineShp);
			if (data == null || data.length == 0) {
				System.out.println("数据不符合条件！");
			} else {
				dos.write(data);
				iSize += data.length;
			}

		}
		if (iSize % 2 != 0) {
			dos.write(0);
			iSize += 1;
		}
		dos.close();

		return bao.toByteArray();

	}

	/**
	 * 获得背景,面二进制byte数组(一个layer的)
	 * 
	 * @param layerNo
	 *            LayerNo
	 * @param telePolygonShpList
	 *            List
	 * @param size
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] polygon(
			List<TelePolygonShp> telePolygonShpList) throws Exception {
		// 调用工具类，将数据转换为byte数组
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		for (TelePolygonShp telePolygonShp : telePolygonShpList) {
			byte[] polygon = null;
			polygon = ShpVectorUtil.getVector(telePolygonShp);

			dos.write(polygon);
		}
		// 字节对齐
		if (1 == dos.size() % 2) {
			dos.write(0);
		}
		dos.close();

		return bao.toByteArray();
	}

	/**
	 * 获得道路二进制byte数组
	 * 
	 * @param teleRoadPloylineShpList
	 *            List
	 * @param size
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] roadPolyline(
			List<TeleRoadPloylineShp> teleRoadPloylineShpList) throws Exception {
		// 调用工具类，将数据转换为byte数组
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		final int RoadInfo = 11;
		int iSize = 0;
		dos.write(RoadInfo);
		iSize += 1;
		for (TeleRoadPloylineShp teleRoadPloylineShp : teleRoadPloylineShpList) {
			byte[] polygon = null;
			polygon = ShpVectorUtil.getVector(teleRoadPloylineShp);

			dos.write(polygon);
			iSize += polygon.length;
		}
		if (iSize % 2 != 0) {
			dos.write(0);
			iSize += 1;
		}
		dos.close();

		return bao.toByteArray();
	}

	/**
	 * 获得文字标记二进制byte数组
	 * 
	 * @param layerNo
	 *            LayerNo
	 * @param teleMarkList
	 *            List
	 * @param letterInfo
	 *            StringBuffer
	 * @param letterMap
	 *            Map
	 * @return byte
	 * @throws Exception
	 *             1
	 */
	private byte[] letterSign(LayerNo layerNo, List<MarkInfor> teleMarkList,
			StringBuffer letterInfo, Map<String, LetterInfo> letterMap)
			throws Exception {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);

		int iSize = 0;
		for (MarkInfor teleMarkShp : teleMarkList) {
			this.letterInfo(teleMarkShp, letterInfo, letterMap);
			byte[] polygon = null;
			polygon = ShpVectorUtil.getVector(teleMarkShp, letterMap);

			dos.write(polygon);
			iSize += polygon.length;
		}
		if (iSize % 2 != 0) {
			dos.write(0);
			iSize += 1;
		}
		dos.close();

		return bao.toByteArray();
	}

	/**
	 * 删除polyline和polygon中的重复形状点，保存符合条件的形状
	 * 
	 * @param shape
	 *            List
	 * @return List
	 */
	private List<BaseShape> clearRepeatPoint(List<BaseShape> shape) {

		List<BaseShape> baseShape = new ArrayList<BaseShape>();
		final int MAXLENGTH = 255;
		if (shape == null || shape.size() == 0) {
			return baseShape;
		} else {
			baseShape = new ArrayList<BaseShape>();
			for (BaseShape base : shape) {
				if (!(base instanceof MarkInfor)) {
					BaseShape bshp = ShpVectorUtil.cleanIteratePoint(base);
					if (bshp.getCoordinate() == null
							|| bshp.getCoordinate().size() == 0) {
						continue;
					}
					if (bshp.getCoordinate().size() == 1) {
						continue;
					}
					if (bshp.getCoordinate().size() > MAXLENGTH) {
						continue;
					}
					baseShape.add(bshp);
				} else {
					baseShape.add(base);
				}

			}
		}
		return baseShape;

	}
	/**
	 * 
	 */
	@Override
	protected List<BlockNo> collectData(Task task,ViewData data) throws Exception {
//		 Map<BlockNo, Map<LayerNo, List<BaseShape>>> 
		for (BlockData blockData : data.blockDatas) {
			BlockNo blockNo = blockData.blockNo;
//			if(blockNo.iX == 22928 && blockNo.iY == 6976){
//				System.out.println("debuge..");
//			}
			Map<LayerNo, List<BaseShape>> layerMap= null;
			if((layerMap = this.targetDataMap.get(blockNo)) == null){
				layerMap = new HashMap<LayerNo, List<BaseShape>>();
				this.targetDataMap.put(blockNo,layerMap);
			}

			//Route ---
			for (Route route : blockData.routes) {
//				System.out.println(route.layerNo);
				LayerNo layerNo = layerMapColl.get().get(Integer.valueOf(route.layerNo));
				List<BaseShape> shapeList = null;
				if((shapeList =layerMap.get(layerNo)) == null){
					shapeList = new ArrayList<BaseShape>();
					BaseShape baseShape = route.getBaseShape();
					if (baseShape != null) {
						shapeList.add(baseShape);
						layerMap.put(layerNo, shapeList);
					}
				} else {
					BaseShape baseShape = route.getBaseShape();
					if (baseShape != null) {
						shapeList.add(baseShape);
					}
				}
			}
			
			//LandMark--
			for (Landmark landmark : blockData.landmarks) {
				LayerNo layerNo = LayerNo.Mark;
				List<BaseShape> shapeList = null;
				if((shapeList =layerMap.get(layerNo)) == null){
					shapeList = new ArrayList<BaseShape>();
					layerMap.put(layerNo, shapeList);
				}
				shapeList.add(landmark.getBaseShape());
			}
			
			//BG ---面
			for (Area area : blockData.polygons) {
//				System.out.println(route.layerNo);
				LayerNo layerNo = layerMapColl.get().get(Integer.valueOf(area.layerNo));
				List<BaseShape> shapeList = null;
				if((shapeList =layerMap.get(layerNo)) == null){
					shapeList = new ArrayList<BaseShape>();
					BaseShape baseShape = area.getBaseShape();
					if(baseShape != null){
						shapeList.add(baseShape);
						layerMap.put(layerNo, shapeList);
					}
				}else{
					BaseShape baseShape = area.getBaseShape();
					if(baseShape != null){
						shapeList.add(baseShape);
					}
				}
			}
			//BG -- 线
			for (Line line : blockData.polylines) {
//				System.out.println(route.layerNo);
				LayerNo layerNo = layerMapColl.get().get(Integer.valueOf(line.layerNo));
				List<BaseShape> shapeList = null;
				if((shapeList =layerMap.get(layerNo)) == null){
					shapeList = new ArrayList<BaseShape>();
					layerMap.put(layerNo, shapeList);
				}
				BaseShape baseShape = line.getBaseShape();
				if(baseShape != null){
					shapeList.add(baseShape);
				}
			}
		}
		// 取得所有的block编号
		List<BlockNo> blockNoList = new ArrayList<BlockNo>();

		for (BlockNo blockNo : this.targetDataMap.keySet()) {
			if (this.targetDataMap.get(blockNo) == null || this.targetDataMap.get(blockNo).size() == 0){
			}else {
				blockNoList.add(blockNo);
			}
		}
		return blockNoList;
	}

    public static  ThreadLocal < Map<Integer, LayerNo> > layerMapColl =  new ThreadLocal < Map<Integer, LayerNo> > () {
        @Override protected Map<Integer, LayerNo> initialValue() {
        	Map<Integer, LayerNo> map = new HashMap<Integer, LayerNo>();
        	LayerNo[] layerNos = LayerNo.values();
			for (int i = 0; i < layerNos.length; i++) {
				map.put(layerNos[i].iid, layerNos[i]);
			}
            return map;
         }
    };

	protected File createResultFile(String midFile)
			throws Exception {
		String fileName = midFile +this.fileEnd;
		File file = new File(fileName);
		if (!file.exists()) {
			File filePath = new File(midFile.substring(0, midFile.length()-2));
			filePath.mkdirs();
		} else {
			file.delete();

			file.createNewFile();

		}
		return file;
	}

}

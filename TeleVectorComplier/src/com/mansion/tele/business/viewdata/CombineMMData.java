package com.mansion.tele.business.viewdata;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipException;

import org.hibernate.dialect.FirebirdDialect;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.TaskManager;
import com.mansion.tele.business.network.IntersectionNew;
import com.mansion.tele.business.network.Network;
import com.mansion.tele.business.network.NodeNew;
import com.mansion.tele.business.network.RoadNew;
import com.mansion.tele.business.network.RoadNew.defineCONSTRUCT;
import com.mansion.tele.business.network.RoadNew.defineDTF;
import com.mansion.tele.business.network.RoadNew.defineFOW;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.GeoLocation;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.Util;
/**
 * 制作mm数据
 * @author wxc
 *
 */
public class CombineMMData {
	
	public static int mmBlockWith = 72000;
	public static int mmblockheigh = 48000;
	// 缓冲区内存
	private static final int MEMORY = 104857600;
	
	static Comparator<MMlinkCoor> comparator = new Comparator<CombineMMData.MMlinkCoor>() {
		
		@Override
		public int compare(MMlinkCoor o1, MMlinkCoor o2) {
			return o1.num - o2.num;
		}
	};
	
	static Comparator<RoadNew> roadcomparatorByblock = new Comparator<RoadNew>() {
		
		@Override
		public int compare(RoadNew o1, RoadNew o2) {
			// TODO Auto-generated method stub
			BlockNo blockNo1 = o1.blockNo;
			BlockNo blockNo2 = o2.blockNo;
			if(blockNo1.iY > blockNo2.iY){
				return 1;
			}
			if(blockNo1.iY < blockNo2.iY){
				return -1;
			}
			if(blockNo1.iX > blockNo2.iX){
				return 1;
			}
			if(blockNo1.iX < blockNo2.iX){
				return -1;
			}
			return 0;
		}
	};
	/**
	 * 保存mm数据
	 * @param readpath
	 * @param outpath
	 * @param taskManagement
	 * @throws ZipException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void saveMM(String readpath, String outpath, TaskManager taskManagement) throws ZipException, IOException, ClassNotFoundException{
		makeMMInfoFile(readpath, outpath, taskManagement);
		combinOneFile(readpath, outpath, "mm");
	}
	/**
	 * 制作四个为合并的管理信息 + data信息文件
	 * @param readpath
	 * @param outpath
	 * @param taskManagement
	 * @throws FileNotFoundException
	 * @throws ZipException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void makeMMInfoFile(String readpath, String outpath,
			TaskManager taskManagement) throws FileNotFoundException,
			ZipException, IOException, ClassNotFoundException {
		File headfile = new File(readpath + File.separator + "mmheader");
		File unitmfile = new File(readpath + File.separator + "mmunitm");
		File blockmfile = new File(readpath + File.separator + "mmblockm");
		File blockDatafile = new File(readpath + File.separator + "mmblock");
		
		if(headfile.exists()){
			headfile.delete();
		}
		if(unitmfile.exists()){
			unitmfile.delete();
		}
		if(blockmfile.exists()){
			blockmfile.delete();
		}
		if(blockDatafile.exists()){
			blockDatafile.delete();
		}
		headfile.createNewFile();
		unitmfile.createNewFile();
		blockmfile.createNewFile();
		blockDatafile.createNewFile();
		int unitmsize = 0;
		int blockmsize = 0;
		int blockDatasize = 0;
		//整个文件流
		FileOutputStream headfileOutputStream = new FileOutputStream(headfile);
		FileOutputStream unitmfileOutputStream = new FileOutputStream(unitmfile);
		FileOutputStream blockmfileOutputStream = new FileOutputStream(blockmfile);
		FileOutputStream blockDatafileOutputStream = new FileOutputStream(blockDatafile);
		//mm header信息流
		BufferedOutputStream mmbaOutputStream = new BufferedOutputStream(headfileOutputStream,100000);
		DataOutputStream mmdatDataOutputStream = new DataOutputStream(mmbaOutputStream);
		//mm unit管理记录
		BufferedOutputStream byunitmanage = new BufferedOutputStream(unitmfileOutputStream,1000);
		DataOutputStream unitmanage = new DataOutputStream(byunitmanage);
		//mm block管理记录流
		BufferedOutputStream byblockmanage = new BufferedOutputStream(blockmfileOutputStream,1000);
		DataOutputStream blockmanage = new DataOutputStream(byblockmanage);
		//mm blockdata流
		BufferedOutputStream Data = new BufferedOutputStream(blockDatafileOutputStream,1000);
		DataOutputStream blockData = new DataOutputStream(Data);
		//unit范围
		int minunitx = taskManagement.eachLevelMinUnitNoX[0];
		int minunity = taskManagement.eachLevelMinUnitNoY[0];
		int maxunitx = taskManagement.eachLevelMaxUnitNoX[0];
		int maxunity = taskManagement.eachLevelMaxUnitNoY[0];
		//制作除去mm header以外内容
		for (int i = minunity; i <= maxunity; i++) {
			for (int j = minunitx; j <= maxunitx; j++) {
				TaskData taskData = readData(j, i, readpath);
				int blocksize = makeMMUnitData(taskData, unitmanage, blockmanage, blockData, j, i, blockmsize, blockDatasize);
				unitmsize = unitmsize + 12;
				if(blocksize != 0){
					blockmsize = blockmsize + 6 * (taskData.blockInfo.unitWidth / taskData.blockInfo.iBlockWidth) 
							* (taskData.blockInfo.unitHeight / taskData.blockInfo.iBlockHight);
				}
				blockDatasize = blockDatasize + blocksize;
			}
		}
		unitmanage.close();
		blockmanage.close();
		blockData.close();
		// mm header内容填充
		mmdatDataOutputStream.writeByte(36);
		mmdatDataOutputStream.writeInt(unitmsize);
		mmdatDataOutputStream.writeInt(blockmsize);
		mmdatDataOutputStream.writeInt(blockDatasize);
		mmdatDataOutputStream.writeByte(1);
		mmdatDataOutputStream.writeShort(0);
		mmdatDataOutputStream.writeInt(0);
		mmdatDataOutputStream.writeInt(minunitx);
		mmdatDataOutputStream.writeInt(minunity);
		mmdatDataOutputStream.writeInt(maxunitx - minunitx + 1);
		mmdatDataOutputStream.writeInt(maxunity - minunity + 1);
		byunitmanage.close();
		byblockmanage.close();
		Data.close();
		mmdatDataOutputStream.close();
		mmbaOutputStream.close();
		blockDatafileOutputStream.close();
		blockmfileOutputStream.close();
		unitmfileOutputStream.close();
		headfileOutputStream.close();
	}
	/**
	 * 将以上三个结果文件合并到一个文件中
	 */
	private static void combinOneFile(String path, String outpath,String result) {
		File file = new File(outpath + File.separator + result);
		File headfile = new File(path + File.separator + "mmheader");
		File unitmfile = new File(path + File.separator + "mmunitm");
		File blockmfile = new File(path + File.separator + "mmblockm");
		File blockDatafile = new File(path + File.separator + "mmblock");
		
		DataOutputStream dos = null;
		DataInputStream disH = null;
		DataInputStream disU = null;
		DataInputStream disM = null;
		DataInputStream disB = null;
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			dos = new DataOutputStream(new FileOutputStream(file));

			// 读头文件 begin 利用缓冲提高效率
			disH = new DataInputStream(new BufferedInputStream(new FileInputStream(headfile), MEMORY));
			byte[] buf = new byte[1024];

			int len = 0;
			while ((len = disH.read(buf)) != -1) {
				byte[] by = Arrays.copyOfRange(buf, 0, len);
				dos.write(by);
			}
			disU = new DataInputStream(new BufferedInputStream(new FileInputStream(unitmfile), MEMORY));
			while ((len = disU.read(buf)) != -1) {
				byte[] by = Arrays.copyOfRange(buf, 0, len);
				dos.write(by);
			}

			disM = new DataInputStream(new BufferedInputStream(new FileInputStream(blockmfile), MEMORY));
			while ((len = disM.read(buf)) != -1) {
				byte[] by = Arrays.copyOfRange(buf, 0, len);
				dos.write(by);
			}

			disB = new DataInputStream(new BufferedInputStream(new FileInputStream(blockDatafile), MEMORY));
			while ((len = disB.read(buf)) != -1) {
				byte[] by = Arrays.copyOfRange(buf, 0, len);
				dos.write(by);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				disH.close();
				disU.close();
				disM.close();
				disB.close();
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			disH = null;
			disU = null;
			disM = null;
			disB = null;
			dos = null;
		}

	}
	/**
	 * 读取数据
	 * @param unitx
	 * @param unity
	 * @param path
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static TaskData readData(int unitx , int unity, String path) throws ZipException, IOException, ClassNotFoundException{
		if(!new File(path + File.separator + TaskData.mmlevel + "_" + unitx + "_"+ unity).exists()){
			return null;
		}
		TaskData taskData = (TaskData) Util.readObjectFromFile(path + File.separator + TaskData.mmlevel + "_" + unitx + "_"+ unity);
		if(taskData != null){
			taskData.blockInfo = DataManager.getLevelInfo(88);
		}
		return taskData;
	}
	
	/**
	 * 制作mm uint数据
	 * @param network
	 * @param unitmanage
	 * @param Data
	 * @param unitx
	 * @param unity
	 * @throws IOException 
	 */
	public static int makeMMUnitData(TaskData taskData, DataOutputStream unitmanage,DataOutputStream blockmanage, DataOutputStream Data, int unitx, int unity, int blockmanagesize, int blockDataSize) throws IOException{
		if(taskData == null){
			unitmanage.writeInt(unitx);
			unitmanage.writeInt(unity);
			unitmanage.writeInt(-1);
			return 0;
		}
		List<RoadNew> roads = taskData.network.roadList;
		if(taskData.network == null || taskData.network.roadList == null || roads.size() == 0){
			unitmanage.writeInt(unitx);
			unitmanage.writeInt(unity);
			unitmanage.writeInt(-1);
			return 0;
		}
		int num = 0;
		int blockDatasize = 0;
		Collections.sort(roads, roadcomparatorByblock);
		BlockNo minblockno = BlockNo.valueOf(GeoLocation.valueOf(taskData.task.getLeft(), taskData.task.getBottom()), mmBlockWith, mmblockheigh);
		BlockNo maxblockno = BlockNo.valueOf(GeoLocation.valueOf(taskData.task.getRight(), taskData.task.getTop()), mmBlockWith, mmblockheigh);
		unitmanage.writeInt(unitx);
		unitmanage.writeInt(unity);
		unitmanage.writeInt(blockmanagesize);
		List<RoadNew> blockroadlist = new ArrayList<RoadNew>();
		for (int i = minblockno.iY , j = minblockno.iX, z = 0;
				i < maxblockno.iY && j < maxblockno.iX;) {
				if(z < roads.size() 
						&& (roads.get(z).blockNo.iX >= maxblockno.iX || roads.get(z).blockNo.iY >= maxblockno.iY)){
					System.out.println("test:" + roads.get(z).blockNo.iX  + ";" + roads.get(z).blockNo.iY);
					System.out.println("max:" + maxblockno.iX + ";" + maxblockno.iY);
					z++;
					continue;
				}else if(z < roads.size() && roads.get(z).blockNo.iX == j && roads.get(z).blockNo.iY == i){
					blockroadlist.add(roads.get(z));
					z++;
					continue;
				}else{
					if(blockroadlist.size()==0){
						blockmanage.writeInt(-1);
						blockmanage.writeShort(0);
						if(num > 128*128){
							System.out.println("sdsd");
						}
						num++;
					}else{
						byte[] blockDataFormat = CombineMMData.makeMMBlockData(blockroadlist, j, i, taskData);
						Data.write(blockDataFormat);
						blockmanage.writeInt(blockDataSize + blockDatasize);
						blockmanage.writeShort(blockDataFormat.length / 2);
						blockDatasize = blockDatasize + blockDataFormat.length;
						blockroadlist = new ArrayList<RoadNew>();
						if(num > 128*128){
							System.out.println("sdsd");
						}
						num++;
					}
					if(j < maxblockno.iX - 1){
						j++;
					}else {
						j = minblockno.iX;
						i++;
					}
				}
				
		}
		return blockDatasize;
		
	}
	/**
	 * 制作mmblock数据
	 * @param roadlist
	 * @param blockx
	 * @param blocky
	 * @param taskData
	 * @return
	 * @throws IOException
	 */
	public static byte[] makeMMBlockData(List<RoadNew> roadlist, int blockx, int blocky, TaskData taskData) throws IOException{
		if(roadlist == null || roadlist.size() == 0){
			return new byte[0];
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1000); 
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		
		ByteArrayOutputStream linkInfoOut = new ByteArrayOutputStream(1000);
		DataOutputStream linkInfoDataOut = new DataOutputStream(linkInfoOut);
		
		ByteArrayOutputStream nodeInfoOut = new ByteArrayOutputStream(1000); 
		DataOutputStream nodeInfoDataOut = new DataOutputStream(nodeInfoOut);
		
		ByteArrayOutputStream linkCoorOut = new ByteArrayOutputStream(1000); 
		DataOutputStream linkCoorDataOut = new DataOutputStream(linkCoorOut);
		List<MMlinkCoor> mmlinkCos = new ArrayList<CombineMMData.MMlinkCoor>();
		int linkinfosize = 0;
		int nodeinfosize = roadlist.size() * 11 + 20;
		int linkcorrsize = 0;
		int nodenum = 0;
		List<NodeNew> blocknode = new ArrayList<NodeNew>();
		int firstLinkID = 0;
		for (int i = 0; i < roadlist.size(); i++) {
			RoadNew roadNew = roadlist.get(i);
			if(i == 0){
				firstLinkID = roadNew.mmLineID;
			}
			MMlinkCoor mlinkCoor = new CombineMMData.MMlinkCoor(roadNew.blockmmNo, 
					roadNew.startNode, roadNew.endNode, roadNew.dtf, roadNew.fow,roadNew.nrc, roadNew.roadConstructType, roadNew.length);
			mlinkCoor.makeMMLink(roadNew, taskData);
			linkcorrsize = linkcorrsize + mlinkCoor.linkContext.length;
			mmlinkCos.add(mlinkCoor);
			if(roadNew.startNode.offset == -1){
				MMNodeInfo mInfo = new MMNodeInfo(nodeinfosize);
				mInfo.ToDoMMNodeInfo(roadNew.startNode, new BlockNo(blockx, blocky));
				nodeInfoDataOut.write(mInfo.nodeInfo);
				nodeinfosize = nodeinfosize + mInfo.nodeInfo.length;
				nodenum ++;
			}
			if(roadNew.startNode.mmBlockBorderNode){
				blocknode.add(roadNew.startNode);
			}
			if(roadNew.endNode.offset == -1){
				MMNodeInfo mInfo = new MMNodeInfo(nodeinfosize);
				mInfo.ToDoMMNodeInfo(roadNew.endNode, new BlockNo(blockx, blocky));
				nodeInfoDataOut.write(mInfo.nodeInfo);
				nodeinfosize = nodeinfosize + mInfo.nodeInfo.length;
				nodenum ++;
			}
			if(roadNew.endNode.mmBlockBorderNode){
				blocknode.add(roadNew.endNode);
			}
			linkinfosize = linkinfosize + 11; 
		}
		if(mmlinkCos.size() != 0 ){
			dataOutputStream.writeByte(0);
			if(blockx == 8470 && blocky == 7216){
				System.out.println("test");
			}
			dataOutputStream.writeInt(blockx);
			dataOutputStream.writeInt(blocky);
			if((nodeinfosize + linkcorrsize) % 2 == 1){
				dataOutputStream.writeShort(nodeinfosize + linkcorrsize + 1);
			}else{
				dataOutputStream.writeShort(nodeinfosize + linkcorrsize);
			}
			dataOutputStream.writeInt(firstLinkID);
			dataOutputStream.writeShort(mmlinkCos.size());
			dataOutputStream.writeShort(nodenum);
			dataOutputStream.writeByte(0);
			int linkCorr = 0;
			for (MMlinkCoor mMlinkCoor : mmlinkCos) {
				linkInfoDataOut.writeShort(nodeinfosize + linkCorr);
				linkInfoDataOut.writeShort(mMlinkCoor.startpoint.offset);
				linkInfoDataOut.writeShort(mMlinkCoor.endpoint.offset);
				linkInfoDataOut.writeInt(getLinkAttribute1(mMlinkCoor));
				linkInfoDataOut.writeByte(getLinkAttribute2(mMlinkCoor));
				linkCoorDataOut.write(mMlinkCoor.linkContext);
				linkCorr = linkCorr + mMlinkCoor.linkContext.length;
			}
			linkInfoDataOut.close();
			nodeInfoDataOut.close();
			linkCoorDataOut.close();
			dataOutputStream.write(linkInfoOut.toByteArray());
			dataOutputStream.write(nodeInfoOut.toByteArray());
			dataOutputStream.write(linkCoorOut.toByteArray());
			linkInfoOut.close();
			nodeInfoOut.close();
			linkCoorOut.close();
			dataOutputStream.close();
			if(byteArrayOutputStream.size() % 2 == 1){
				byteArrayOutputStream.write(0);
			}
			byte[] blockData = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
			//将边界点偏移初始化
			for (int i = 0; i < blocknode.size(); i++) {
				NodeNew bnode = blocknode.get(i);
				if(bnode.isIntersectionNode()){
					for (int j = 0; j < bnode.intersection.allNodes.size(); j++) {
						bnode.intersection.allNodes.get(j).offset = -1;
					}
				}else{
					bnode.offset = -1;
				}
			}
			return blockData;
		}
		
		return null;
	}
	
	/**
	 * 通过block获得相应道路
	 * @param roadlist
	 * @param blockx
	 * @param blocky
	 * @return
	 */
	public static List<RoadNew> getRoadListByBlock(List<RoadNew> roadlist, int blockx, int blocky){
		List<RoadNew> roadblocklist = new ArrayList<RoadNew>();
		for (int i = 0; i < roadlist.size(); i++) {
			RoadNew roadNew = roadlist.get(i);
			if(roadNew.blockNo.iX == blockx && roadNew.blockNo.iY == blocky){
				roadblocklist.add(roadNew);
			}
		}
		return roadblocklist;
	}
	/**
	 * 制作link属性1
	 * @param mlinkCoor
	 * @return
	 */
	protected static int getLinkAttribute1(MMlinkCoor mlinkCoor) {

		final int iBit30 = 30;
		final int iBit31 = 31;
		final int iBit25 = 25;
		final int iBit29 = 29;
		final int iBit24 = 24;
		final int iBit23 = 23;
		final int iBit22 = 22;
		final int iBit18 = 18;

		int data = 0;
		// 通行方向
		data = Util
				.setBits(data, mlinkCoor.dir, iBit30, iBit31);
		// link种别
		data = Util
				.setBits(data, mlinkCoor.fow, iBit25, iBit29);
		// 是否高架
		data = Util.setBit(data, iBit24, mlinkCoor.viaduct);
		// 是否隧道
		data = Util.setBit(data, iBit23, mlinkCoor.tunnel);
		// 是否桥
		data = Util.setBit(data, iBit22, mlinkCoor.bridge);
		// link长度
		data = Util.setBits(data, mlinkCoor.length, 0, iBit18);
		return data;
	}
	
	/**
	 * 写link的属性2
	 * 
	 * @param roadLink
	 *            roadlink信息
	 * @return 返回属性信息
	 */
	protected static byte getLinkAttribute2(MMlinkCoor mlinkCoor) {
		
		final int iBit4 = 4;
		final int iBit7 = 7;
		byte data = 0;
		// 管理等级
		data = (byte) Util.setBits(data,
				mlinkCoor.nrc, iBit4, iBit7);
		return data;
	}
	/**
	 * 坐标正规化
	 * @param road
	 * @param taskdata
	 * @return
	 */
	public static List<ShpPoint> calcRegular(RoadNew road, TaskData taskdata){
		List<ShpPoint> regular = new ArrayList<ShpPoint>();
		ShpPoint forward = null;
		for (int i = 0; i < road.coordinate.size(); i++) {
			ShpPoint currpoint = taskdata.calcRegular(road.coordinate.get(i));
			if(forward == null){
				forward = currpoint;
				regular.add(currpoint);
			}else{
				if(!forward.equals(currpoint)){
					regular.add(currpoint);
				}else if(i == road.coordinate.size() - 1){
					regular.add(currpoint);
				}
			}
		}
		if(regular.size() < 2){
			System.err.println(road.blockNo.iX + "," + road.blockNo.iY +", mm号"+ road.blockmmNo + "错误数据");
		}
		return regular;
	}
	/**
	 * mmlinkinfo
	 * @author wxc
	 *
	 */
	static class MMlinkCoor{
		int num;
		NodeNew startpoint;
		NodeNew endpoint;
		int dir;
		int fow;
		int nrc;
		boolean viaduct = false;//高架桥
		boolean tunnel = false;//隧道
		boolean bridge = false;//桥
		int length;
		byte[] linkContext;
		
		public MMlinkCoor(int num, NodeNew start, NodeNew end, byte dir, byte fow, byte nrc, byte roadConstructType, int length){
			switch (dir) {
			case defineDTF.TwoWayPass:
				this.dir = 0;
				break;
			case defineDTF.NegPass:
				this.dir = 2;
				break;
			case defineDTF.PosPass:
				this.dir = 1;
				break;
			case defineDTF.NoPass:
				this.dir = 3;
				break;

			default:
				break;
			}
			this.fow = fow;
			this.nrc = nrc;
			switch (roadConstructType) {
			case defineCONSTRUCT.Bridge:
				this.bridge = true;
				break;
			case defineCONSTRUCT.Tunnel:
				this.tunnel = true;
				break;
			case defineCONSTRUCT.OverHead:
				this.viaduct = true;
				break;
			default:
				break;
			}
			this.num = num;
			this.startpoint = start;
			this.endpoint = end;
			this.length = length * 100;
		}
		
		/**
		 * 制作link形状
		 * @param roadNew
		 * @param taskData
		 * @throws IOException
		 */
		public void makeMMLink(RoadNew roadNew, TaskData taskData) throws IOException{
			List<ShpPoint> regularPoints = CombineMMData.calcRegular(roadNew, taskData);
			ByteArrayOutputStream oneRoadOutputStream = new ByteArrayOutputStream(); 
			DataOutputStream oneRoadDataOutputStream = new DataOutputStream(oneRoadOutputStream);
			oneRoadDataOutputStream.writeShort(regularPoints.size());
			for (int j = 0; j < regularPoints.size(); j++) {
				oneRoadDataOutputStream.writeByte(regularPoints.get(j).x);
				oneRoadDataOutputStream.writeByte(regularPoints.get(j).y);
			}
			oneRoadDataOutputStream.close();
			this.linkContext = oneRoadOutputStream.toByteArray();
		}
	}
	/**
	 * mmnodeinfo
	 * @author wxc
	 *
	 */
	static class MMNodeInfo{
		int nodeOffSet;
		byte[] nodeInfo;
		List<RoadNew> out = new ArrayList<RoadNew>();
		List<RoadNew> in = new ArrayList<RoadNew>();
		public MMNodeInfo(int nodeOffset) {
			this.nodeOffSet = nodeOffset;
		}
		/**
		 * 制作nodeinfo
		 * @param nodenew
		 * @throws IOException
		 */
		public void ToDoMMNodeInfo(NodeNew nodenew, BlockNo blockNo) throws IOException{
			ByteArrayOutputStream oneNodeOutputStream = new ByteArrayOutputStream(); 
			DataOutputStream oneNodeDataOutputStream = new DataOutputStream(oneNodeOutputStream);
			if(nodenew.point.x < 1138061214 && nodenew.point.y < 384002314 && nodenew.point.x > 1138052326 && nodenew.point.y > 383996445){
				System.out.println("test");
			}
			if(this.isIntersectNode(nodenew)){
				int data = Util.setBit(0, 15, true);
				oneNodeDataOutputStream.writeShort(data);
				List<RoadNew> outRoads = new ArrayList<RoadNew>();
				List<RoadNew> innerRoads = new ArrayList<RoadNew>();
				List<NodeNew> innodes = new ArrayList<NodeNew>();
				this.getIntersectRoads(nodenew.intersection, outRoads, innerRoads,innodes);
				this.out.addAll(outRoads);
				this.in.addAll(innerRoads);
				oneNodeDataOutputStream.writeByte(outRoads.size());
				oneNodeDataOutputStream.writeByte(innerRoads.size());
				oneNodeDataOutputStream.writeByte(0);
				this.writeNodeLink(oneNodeDataOutputStream, outRoads, innerRoads, innodes, blockNo);
			}else{
				int data = Util.setBit(0, 15, false);
				oneNodeDataOutputStream.writeShort(data);
				List<NodeNew> innodes = new ArrayList<NodeNew>();
				List<RoadNew> outRoads  = new ArrayList<RoadNew>();
				List<RoadNew> innerRoads = new ArrayList<RoadNew>();
				this.getEasyNodeOutRoads(nodenew, innodes, outRoads);
				this.out.addAll(outRoads);
				this.in.addAll(innerRoads);
				oneNodeDataOutputStream.writeByte(outRoads.size());
				oneNodeDataOutputStream.writeByte(innerRoads.size());
				oneNodeDataOutputStream.writeByte(0);
				this.writeNodeLink(oneNodeDataOutputStream, outRoads, innerRoads, innodes, blockNo);
			}
			oneNodeDataOutputStream.close();
			this.nodeInfo = oneNodeOutputStream.toByteArray();
			oneNodeOutputStream.close();
		}
		/**
		 * 获取链接路
		 * @param node
		 * @return
		 */
		public void getEasyNodeOutRoads(NodeNew node, List<NodeNew> nodes, List<RoadNew> outRoads){
			List<NodeNew> taskbordernodes = Network.getTaskBorderNode(node);
			if(taskbordernodes == null){
				outRoads.addAll(node.roads);
				nodes.add(node);
			}else{
				for (NodeNew nodeNew : taskbordernodes) {
					outRoads.addAll(nodeNew.roads);
					nodes.add(nodeNew);
				}
			}
			node.offset = this.nodeOffSet;
		}
		
		/**
		 * 复杂交叉口
		 * @param intersectionNew
		 * @param outRoads
		 * @param innerRoads
		 * @param innodes
		 */
		public void getIntersectRoads(IntersectionNew intersectionNew, List<RoadNew> outRoads, List<RoadNew> innerRoads, List<NodeNew> innodes){
			Set<RoadNew> newoutRoads = new HashSet<RoadNew>();
			Set<RoadNew> newinnerRoads = new HashSet<RoadNew>();
			for (NodeNew nodeNew : intersectionNew.allNodes) {
				if(Network.getTaskBorderNode(nodeNew) != null){
					List<NodeNew> nodes = Network.getTaskBorderNode(nodeNew);
					for (NodeNew nodeNew2 : nodes) {
						for (RoadNew roadNew : nodeNew2.roads) {
							if(roadNew.fow == defineFOW.JunctionLink){
								newinnerRoads.add(roadNew);
							}else{
								newoutRoads.add(roadNew);
							}
						}
						innodes.add(nodeNew2);
					}
				}else{
					for (RoadNew roadNew : nodeNew.roads) {
						if(roadNew.fow == defineFOW.JunctionLink){
							newinnerRoads.add(roadNew);
						}else{
							newoutRoads.add(roadNew);
						}
					}
					innodes.add(nodeNew);
				}
				nodeNew.offset = this.nodeOffSet;
			}
			outRoads.addAll(newoutRoads);
			innerRoads.addAll(newinnerRoads);
		}
		
		/**
		 * 写link链接排列记录
		 * @param dataOutputStream
		 * @param outRoads
		 * @param inRoads
		 * @param innodes
		 * @param basicBlock
		 * @throws IOException
		 */
		public void writeNodeLink(DataOutputStream dataOutputStream, List<RoadNew> outRoads, List<RoadNew> inRoads, List<NodeNew> innodes, BlockNo basicBlock) throws IOException{
			for (RoadNew roadNew : outRoads) {
				int data = 0;
				if(innodes.contains(roadNew.startNode)){
					data = Util.setBit(data, 15, false);
				}else if(innodes.contains(roadNew.endNode)){
					data= Util.setBit(data, 15, true);
				}
				int num = location(roadNew.blockNo, basicBlock);
				data = Util.setBits(data, num, 0, 3);
				dataOutputStream.writeShort(data);
				dataOutputStream.writeShort(roadNew.blockmmNo);
			}
			for (RoadNew roadNew : inRoads) {
				int data = 0;
				if(innodes.contains(roadNew.startNode)){
					data = Util.setBit(data, 15, false);
				}else if(innodes.contains(roadNew.endNode)){
					data = Util.setBit(data, 15, true);
				}
				int num = location(roadNew.blockNo, basicBlock);
				data = Util.setBits(data, num, 0, 3);
				dataOutputStream.writeShort(data);
				dataOutputStream.writeShort(roadNew.blockmmNo);
			}
		}
		
		/**
		 * 判断是否是复杂交叉口点
		 * @param nodeNew
		 * @return
		 */
		public boolean isIntersectNode(NodeNew nodeNew){
			List<NodeNew> allNodes = Network.getTaskBorderNode(nodeNew);
			if(nodeNew.intersection != null){
				return true;
			}else if(allNodes == null || allNodes.size() == 0){
				return false;
			}else {
				for (NodeNew nodeNew2 : allNodes) {
					if(nodeNew2.intersection != null){
						return true;
					}
				}
				return false;
			}
		}
	}
	/**
	 * 两个block的位置比较
	 * @param blockNo block号
	 * @param basicBlock block号
	 * @return int 比较结果
	 */
	public static int location(BlockNo blockNo, BlockNo basicBlock) {
		
		int i = -1;
		if(blockNo == null){
			System.out.println("test");
		}
		int blockX = blockNo.iX;
		int blockY = blockNo.iY;
		int basicX = basicBlock.iX;
		int basicY = basicBlock.iY;
		if (blockX == basicX) {
			if (blockY == basicY) {
				i = SELF;
			}else if (blockY > basicY) {
				i = TOP;
			}else {
				i = BOTTOM;
			}
		}else if (blockX > basicX) {
			if (blockY > basicY) {
				i = RIGHT_TOP;
			}else if (blockY == basicY) {
				i = RIGHT;
			}else {
				i = RIGHT_BOTTOM;
			}
		}else {
			if (blockY > basicY) {
				i = LEFT_TOP;
			}else if (blockY == basicY) {
				i = LEFT;
			}else{
				i = LEFT_BOTTOM;
			}
		}
		return i;
	}
	
	public static final int SELF = 0;//link在当前block
	public static final int TOP = 1;//link在当前block正上方
	public static final int RIGHT_TOP = 2;//link在当前block右上方
	public static final int RIGHT = 3;//link在当前block右方
	public static final int RIGHT_BOTTOM = 4;//link在当前block右下方
	public static final int BOTTOM = 5;//link在当前block下方
	public static final int LEFT_BOTTOM = 6;//link在当前block左下方
	public static final int LEFT = 7;//link在当前block左方
	public static final int LEFT_TOP = 8;//link在当前block左上方
}

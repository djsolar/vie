package com.sunmap.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.DataManager.LevelInfo;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.sunmap.been.BlockData;
import com.sunmap.been.Point;
import com.sunmap.been.Polygon;
import com.sunmap.util.Constant;

public class ReadBlockInfo implements Runnable{
	
	static{
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	// 城市原始形状
	static Map<String, String> cities = new HashMap<String, String>();
	private static int city_count = 0;
	private static int city_total = 0;
	// 转换后的城市block
	public static Map<String, List<BlockData>> citymap = new HashMap<String, List<BlockData>>();
	Iterator<Entry<String, String>> it = null;
	// 0层block号数组
	static boolean[][] lv0block = new boolean[50000][50000];
	// 1层block号数组
	static boolean[][] lv1block = new boolean[10000][10000];
	// 2层block号数组
	static boolean[][] lv2block = new boolean[3000][3000];
	
	public Map<String, List<BlockData>> getCityBlockInfo() throws IOException{
		
		Map<String, List<BlockData>> cityBlockMap = new HashMap<String, List<BlockData>>();
		
		File info = new File("ViewBlockInfo.txt");
		FileReader fileReader = new FileReader(info);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String lineinfo = bufferedReader.readLine();
		lineinfo = bufferedReader.readLine();
		int countnum = 0;
		while(lineinfo != null){
//			System.out.println(lineinfo);
			String[] blockinfo = this.resloveReturnArr(lineinfo);
			if(blockinfo == null){
				System.out.println("?????????");
			}
			if("北京市".equals(blockinfo[6])){
				countnum++;
			}
//			if(Integer.parseInt(blockinfo[0]) == 1){
//				System.out.println(countnum);
//			}
			BlockData blockData = new BlockData();
			blockData.level = Integer.parseInt(blockinfo[0]);
			blockData.unitx = Integer.parseInt(blockinfo[1]);
			blockData.unity = Integer.parseInt(blockinfo[2]);
			blockData.blockx = Integer.parseInt(blockinfo[3]);
			blockData.blocky = Integer.parseInt(blockinfo[4]);
			if(cityBlockMap.containsKey(blockinfo[6])){
				cityBlockMap.get(blockinfo[6]).add(blockData);
			}else{
				List<BlockData> blockDatas = new ArrayList<BlockData>();
				blockDatas.add(blockData);
				cityBlockMap.put(blockinfo[6], blockDatas);
			}
			lineinfo = bufferedReader.readLine();
		}
		System.out.println(countnum);
		bufferedReader.close();
		fileReader.close();
		return cityBlockMap;
	}
	
	public String[] resloveReturnArr(String infoline) {

		if (infoline != null) {

			String[] strInfoArr = infoline.split("\t");

			return strInfoArr;
			
		}
		
		return null;
	}
	
	public void readFromDB() throws SQLException, InterruptedException{
		Connection con = DriverManager.getConnection(Constant.pro_url, Constant.username, Constant.password);
		String sql = Constant.sql;
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			String strPolygons = rs.getString(1);
			String cityName = rs.getString(2);
			cities.put(cityName, strPolygons);
		}
		ps.close();
		con.close();
		changeToBlock();
	}
	
	/**
	 * 获得城市形状的外边矩形
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @param levelinfo
	 * @return
	 */
	public Polygon getRect(long minX, long maxX, long minY, long maxY, LevelInfo levelinfo){
		int minblockx = (int) ((minX-Constant.MAP_GEO_LOCATION_LONGITUDE_MIN)/levelinfo.iBlockWidth);
		int minblocky = (int) ((minY-Constant.MAP_GEO_LOCATION_LATITUDE_MIN)/levelinfo.iBlockHight);
		int maxblockx = (int) ((maxX-Constant.MAP_GEO_LOCATION_LONGITUDE_MIN)/levelinfo.iBlockWidth);
		int maxblocky = (int) ((maxY-Constant.MAP_GEO_LOCATION_LATITUDE_MIN)/levelinfo.iBlockHight);
//		System.out.println("minblockx"+minblockx+"minblocky"+minblocky+"maxblockx"+maxblockx+"maxblocky"+maxblocky);
		Polygon rect = new Polygon();
		rect.left = minblockx * levelinfo.iBlockWidth + Constant.MAP_GEO_LOCATION_LONGITUDE_MIN;
		rect.bottom = minblocky * levelinfo.iBlockHight + Constant.MAP_GEO_LOCATION_LATITUDE_MIN;
		rect.right = (maxblockx+1) * levelinfo.iBlockWidth + Constant.MAP_GEO_LOCATION_LONGITUDE_MIN;
		rect.top = (maxblocky+1) * levelinfo.iBlockHight + Constant.MAP_GEO_LOCATION_LATITUDE_MIN;
		
		return rect;
	}
	
	public void changeToBlock() throws InterruptedException{
		it = cities.entrySet().iterator();
		Thread[] threads = new Thread[Constant.threadCount];
		city_total = cities.size();
		System.out.println("所有城市共：" + city_total);
		for(int i=0;i<Constant.threadCount;i++){
			Thread thread = new Thread(this);
			threads[i] = thread;
			thread.start();
		}
		for(int j=0;j<threads.length;j++){
			threads[j].join();
		}
	}
	
	public static void main(String[] args) throws IOException {
		ReadBlockInfo readBlockInfo = new ReadBlockInfo();
		readBlockInfo.getCityBlockInfo();
	}

	public void run() {
		Split sp = new Split();
		Map.Entry<String, String> entry = null;
		
		while(!Thread.interrupted()){
			synchronized (this) {
				if(city_count == city_total){
					break;
				}
				city_count ++;
				entry = it.next();
			}
			String cityName = entry.getKey();
			String strPolygons = entry.getValue();
			strPolygons = strPolygons.replace("POLYGON((", "");
			strPolygons = strPolygons.replace("))", "");
			String[] arrPolygons = strPolygons.split("\\),\\(");
			for(String strPolygon : arrPolygons){
				// 记录当前polygon被分割后的添加的block数
				int count = 0;
				// 记录当前形状的block
				List<BlockData> curBlocks = new ArrayList<BlockData>();
				Polygon polygon = new Polygon();
				long minX = Integer.MAX_VALUE;
				long maxX = 0L;
				long minY = Integer.MAX_VALUE;
				long maxY = 0L;
				String[] strPoints = strPolygon.split(",");
				for(String strPoint : strPoints){
					long x = Math.round(Double.parseDouble(strPoint.split(" ")[0]));
					long y = Math.round(Double.parseDouble(strPoint.split(" ")[1]));
					minX = Math.min(minX, x);
					maxX = Math.max(maxX, x);
					minY = Math.min(minY, y);
					maxY = Math.max(maxY, y);
					Point p = new Point(x, y);
					polygon.points.add(p);
				}
				for(int j=Constant.minLevel;j<=Constant.maxLevel;j++){
					LevelInfo levelinfo = DataManager.getLevelInfo(j);
					Polygon rect = this.getRect(minX, maxX, minY, maxY, levelinfo);
					List<Polygon> blocksPolygon = sp.splitBlock(polygon, rect, levelinfo);
					for(int k=0;k<blocksPolygon.size();k++){
						Polygon blockPolygon = blocksPolygon.get(k);
						BlockData data = new BlockData();
						data.level = levelinfo.iLevel;
						ShpPoint left_bottom = new ShpPoint((int)blockPolygon.left, (int)blockPolygon.bottom);
						BlockNo blockno = BlockNo.valueOf(left_bottom, levelinfo.iBlockWidth, levelinfo.iBlockHight);
						data.blockx = blockno.iX;
						data.blocky = blockno.iY;
						data.unitx = (int) ((blockPolygon.left - Constant.MAP_GEO_LOCATION_LONGITUDE_MIN)
								/ levelinfo.unitWidth);
						data.unity = (int) ((blockPolygon.bottom - Constant.MAP_GEO_LOCATION_LATITUDE_MIN)
								/ levelinfo.unitHeight);
						curBlocks.add(data);
						// 其他城市已经存在这个block则不保存
						if(this.isBlockExist(data)){
							continue;
						}
						count ++;
						if(citymap.containsKey(cityName)){
							citymap.get(cityName).add(data);
						}
						else{
							List<BlockData> blockDatas = new ArrayList<BlockData>();
							blockDatas.add(data);
							citymap.put(cityName, blockDatas);
						}
					}
				}
				// 如果当前polygon被分割后的block都是前一个polygon分割后存在的，则说明
				// 当前polygon是前一个polygon的子图形，如：阿克苏地区与阿拉尔市
				if(count == 0){
					this.reset(curBlocks, cityName);
				}
			}
			System.out.println(cityName + ":" + citymap.get(cityName).size());
		}
	}
	
	/**
	 * 判断此block是否已经在其他城市保存过
	 * @param data
	 * @return true：保存过 false：没保存过
	 */
	private boolean isBlockExist(BlockData data){
		switch (data.level) {
		case 0:
			if(lv0block[data.blockx][data.blocky]){
				return true;
			}
			else{
				lv0block[data.blockx][data.blocky] = true;
				return false;
			}
		case 1:
			if(lv1block[data.blockx][data.blocky]){
				return true;
			}
			else{
				lv1block[data.blockx][data.blocky] = true;
				return false;
			}
		case 2:
			if(lv2block[data.blockx][data.blocky]){
				return true;
			}
			else{
				lv2block[data.blockx][data.blocky] = true;
				return false;
			}
		default:
			System.out.println("错误的level！");
			break;
		}
		return false;
	}
	
	/**
	 * 重新设置添加状态，清除子图形
	 */
	private void reset(List<BlockData> blocks,String cityName){
		for(BlockData block : blocks){
			switch (block.level) {
			case 0:
				lv0block[block.blockx][block.blocky] = false;
				break;
			case 1:
				lv1block[block.blockx][block.blocky] = false;
				break;
			case 2:
				lv2block[block.blockx][block.blocky] = false;
				break;
			default:
				System.out.println("level不正确！");
				break;
			}
		}
		citymap.get(cityName).removeAll(blocks);
	}
}

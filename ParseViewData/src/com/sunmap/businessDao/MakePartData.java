package com.sunmap.businessDao;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.DataFormatException;

import com.mansion.tele.business.DataManager;
import com.sunmap.been.BlockData;
import com.sunmap.businessDao.Parse.ParseView;
import com.sunmap.businessDao.Parse.ParseViewByBlocks;
import com.sunmap.businessDao.TransForm.TransFormToPubData;
import com.sunmap.businessDao.compress.GzipCompress;
import com.sunmap.businessDao.makeJson.MakeJsonFile;
import com.sunmap.common.ReadBlockInfo;
import com.sunmap.util.Constant;

public class MakePartData implements Runnable{
	
	static Iterator<Entry<String, List<BlockData>>> it = null;
	private static int city_count = 0;
	private static int city_total = 0;
	
	public static void makeData() throws IOException, SQLException, InterruptedException, DataFormatException {
		System.out.println("开始读取城市信息");
		DataManager.init();
		ReadBlockInfo readBlockInfo = new ReadBlockInfo();
		readBlockInfo.readFromDB();
		System.out.println("读取完成");
		System.out.println("开始转换数据");
		city_total = ReadBlockInfo.citymap.size();
		it = ReadBlockInfo.citymap.entrySet().iterator();
		Thread[] threads = new Thread[Constant.threadCount];
		for(int i=0;i<Constant.threadCount;i++){
			Thread thread = new Thread(new MakePartData());
			threads[i] = thread;
			thread.start();
		}
		for(int j=0;j<threads.length;j++){
			threads[j].join();
		}
		System.out.println("开始转换全国基础地图");
		File viewfile = new File(Constant.inPath);
		ParseView ps = new ParseView();
		List<BlockData> nationalBase = ps.parse(viewfile);
		TransFormToPubData toPubData = new TransFormToPubData();
		toPubData.transForm(nationalBase, "全国基础地图");
//		GzipCompress gzipCompress = new GzipCompress();
//		gzipCompress.compress("全国基础地图");
		System.out.println("全国基础地图转换完成");
		System.out.println("全部转换完成");
		System.out.println("开始制作json文件");
		MakeJsonFile mjf = new MakeJsonFile();
		mjf.makeJson();
		System.out.println("json文件制作完毕");
	}

	public void run() {
		Map.Entry<String, List<BlockData>> entry = null;
		while(!Thread.interrupted()){
			synchronized (this) {
				if(city_count == city_total){
					break;
				}
				city_count ++;
				entry = it.next();
			}
			String cityName = entry.getKey();
			List<BlockData> datas = entry.getValue();
			ParseViewByBlocks parseViewByBlocks = new ParseViewByBlocks(datas);
			File viewfile = new File(Constant.inPath);
			if(viewfile.exists()){
				System.out.println(cityName + "转换开始，共:" + datas.size());
				List<BlockData> blockdatas;
				try {
					blockdatas = parseViewByBlocks.parse(viewfile);
					TransFormToPubData toPubData = new TransFormToPubData();
					toPubData.transForm(blockdatas, cityName);
//					GzipCompress gzipCompress = new GzipCompress();
//					gzipCompress.compress(cityName);
				} catch (IOException e) {
					System.out.println("转换压缩时IO异常！");
					e.printStackTrace();
				} catch (DataFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{
				System.out.println("view不存在");
				return;
			}
			System.out.println(cityName + "转换完成");
		}
	}

}

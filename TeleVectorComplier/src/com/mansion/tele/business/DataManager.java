package com.mansion.tele.business;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import com.mansion.tele.common.GeoRect;
import com.mansion.tele.db.bean.OrgTaskNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.db.factory.TeleHbSessionFactory;

public class DataManager {
	/**
	 * 各层的宽高等信息
	 */
	private static List<LevelInfo> levelInfos = new ArrayList<LevelInfo>();

	/**
	 * 各层的母库图页号列表
	 */
	private static List<List<TaskNoInOrg>> TaskRectList = new ArrayList<List<TaskNoInOrg>>();
	
	/** 地图经度方向最小值 */
	public static final int MAP_GEO_LOCATION_LONGITUDE_MIN = 184320000;                                                                                                                                                                                                          
	/** 地图纬度方向最小值 */
	public static final int MAP_GEO_LOCATION_LATITUDE_MIN = 0; 
	/** 地图经度方向最大值*/
	public static final int MAP_GEO_LOCATION_LONGITUDE_MAX = 1437696000; 
	/** 地图纬度方向最大值*/
	public static final int MAP_GEO_LOCATION_LATITUDE_MAX = 798720000;	

	public static LevelInfo getLevelInfo(int level){
		LevelInfo result = null;
		for (LevelInfo blockInfo : levelInfos) {
			if(blockInfo.iLevel == level){
				result = blockInfo;
				break;
			}
		}
		return result;
	}

	public static void init() {
		initMesh();		
		initBlockInfo();		
	}
	private static void initMesh(){
		TaskRectList.clear();
		for (byte level = 0; level <= 7; ++level) {
			Session session = TeleHbSessionFactory.getOrgHbSession(level).getSession();

			// 设置查询信息
			Criteria criteria = session.createCriteria(OrgTaskNo.class)
								.addOrder(Order.asc("strTaskNo"))
								.setProjection(Projections.id());

			// 取得查询结果
			if (null == criteria){
				continue;
			}

			@SuppressWarnings("unchecked")
			List<String> list = criteria.list();
			TaskRectList.add(change(list));			
		}
	}
	private static void initBlockInfo(){
		levelInfos.clear();
		LevelInfo block0 = new LevelInfo();
		block0.iLevel = 0;
		block0.iBlockWidth = 36000;
		block0.iBlockHight = 24000;
		block0.unitWidth = 9216000;
		block0.unitHeight = 6144000;
		block0.minScale = 10;
		block0.maxScale = 70;
		block0.scales = new int[]{10,20,50};
		levelInfos.add(block0);
		
		LevelInfo block1 = new LevelInfo();
		block1.iLevel = 1;
		block1.iBlockWidth = 144000;
		block1.iBlockHight = 96000;
		block1.unitWidth = 36864000;
		block1.unitHeight = 24576000;
		block1.minScale = 70;
		block1.maxScale = 300;
		block1.scales = new int[]{100,200};
		levelInfos.add(block1);
		
		LevelInfo block2 = new LevelInfo();
		block2.iLevel = 2;
		block2.iBlockWidth = 576000;
		block2.iBlockHight = 384000;
		block2.unitWidth = 73728000;
		block2.unitHeight = 49152000;
		block2.minScale = 300;
		block2.maxScale = 1500;
		block2.scales = new int[]{500,1000};
		levelInfos.add(block2);
		
		LevelInfo block3 = new LevelInfo();
		block3.iLevel = 3;
		block3.iBlockWidth = 2304000;
		block3.iBlockHight = 1536000;
		block3.unitWidth = 73728000;
		block3.unitHeight = 49152000;
		block3.minScale = 1500;
		block3.maxScale = 7500;
		block3.scales = new int[]{2000,5000};
		levelInfos.add(block3);
		
		LevelInfo block4 = new LevelInfo();
		block4.iLevel = 4;
		block4.iBlockWidth = 9216000;
		block4.iBlockHight = 6144000;
		block4.unitWidth = 73728000;
		block4.unitHeight = 49152000;
		block4.minScale = 7500;
		block4.maxScale = 35000;
		block4.scales = new int[]{10000,20000};
		levelInfos.add(block4);
		
		LevelInfo block5 = new LevelInfo();
		block5.iLevel = 5;
		block5.iBlockWidth = 36864000;
		block5.iBlockHight = 24576000;
		block5.unitWidth = 73728000;
		block5.unitHeight = 49152000;
		block5.minScale = 35000;
		block5.maxScale = 150000;
		block5.scales = new int[]{50000,100000};
		levelInfos.add(block5);
		
		LevelInfo block6 = new LevelInfo();
		block6.iLevel = 6;
		block6.iBlockWidth = 294912000;
		block6.iBlockHight = 196608000;
		block6.unitWidth = 294912000;
		block6.unitHeight = 196608000;
		block6.minScale = 150000;
		block6.maxScale = 750000;
		block6.scales = new int[]{200000,500000};
		levelInfos.add(block6);
		
		LevelInfo block7 = new LevelInfo();
		block7.iLevel = 7;
		block7.iBlockWidth = 589824000;
		block7.iBlockHight = 393216000;
		block7.unitWidth = 589824000;
		block7.unitHeight = 393216000;
		block7.minScale = 750000;
		block7.maxScale = 2000000;
		block7.scales = new int[]{1000000,2000000};
		levelInfos.add(block7);
		
		LevelInfo blockmm = new LevelInfo();
		blockmm.iLevel = 88;
		blockmm.iBlockWidth = 72000;
		blockmm.iBlockHight = 48000;
		blockmm.unitWidth = 9216000;
		blockmm.unitHeight = 6144000;
		blockmm.minScale = 10;
		blockmm.maxScale = 70;
		blockmm.scales = new int[]{10,20,50};
		levelInfos.add(blockmm);
	}
	
	/**
	 * 将一组母库中的任务编号转换成矩形信息。
	 * @param taskList  一组母库中的任务编号
	 * @return 矩形信息
	 */
	private static List<TaskNoInOrg> change(List<String> taskList){
		List<TaskNoInOrg> rectList = new ArrayList<TaskNoInOrg>();
		for(String strNo : taskList){
			TaskNoInOrg task = new TaskNoInOrg();
			task.rectOfTaskNo = calcGeoRectByMeshNo(strNo);
			task.strTaskNoInOrg = strNo;
			rectList.add(task);
		}
		
		return rectList;
	}
	
	/**
	 * 获得此任务对应母库的mesh号
	 * 
	 * @return 此任务对应母库的mesh号
	 */
	public static List<String> getMeshNos(Task task){
		List<String> result = new ArrayList<String>();
		//任务范围
		int left = task.getLeft();
		int bottom = task.getBottom();
		int right = task.getRight();
		int top = task.getTop();
		
		List<TaskNoInOrg> rectList = TaskRectList.get(task.getLevel());
		for(int i = 0; i < rectList.size(); i++){
			TaskNoInOrg mesh = rectList.get(i);
			GeoRect rect = mesh.rectOfTaskNo;
			//Mesh范围
			int l = rect.left;
			int b = rect.bottom;
			int r = rect.right;
			int t = rect.top;
			
			if(left <= l && r <=right && bottom <= b && t <= top){
				result.add(mesh.strTaskNoInOrg);
			}
		}
		return result;
	}
	
	/**
	 * 计算母库图叶号矩形范围
	 * @param strMeshNo
	 * @return
	 */
	private static GeoRect calcGeoRectByMeshNo(String strMeshNo){
		
		if (strMeshNo != null && strMeshNo.length() == 11) {
			String strMeshNoX = strMeshNo.substring(0, 4);
			String strMeshNoY = strMeshNo.substring(4, 8);
			String strSize = strMeshNo.substring(8, strMeshNo.length());
			int iMeshX = Integer.valueOf(strMeshNoX, 16);
			int iMeshY = Integer.valueOf(strMeshNoY, 16);
			int iSize = Integer.valueOf(strSize, 16);
			int lbx = (int) (iMeshX * 72000l - 180l * 2560 * 3600);
			int lby = (int) (iMeshY * 48000l - 90l * 2560 * 3600) ;
			int rtx = (int) (lbx + (iSize + 1) * 72000l);
			int rty = (int) (lby + (iSize + 1) * 48000l);

			return GeoRect.valueOf(new ShpPoint(lbx, lby), new ShpPoint(rtx,
					rty));
		}
		return null;		
	}

	static class TaskNoInOrg{
		//库中的mesh号
		String strTaskNoInOrg;
		//mesh的范围
		GeoRect rectOfTaskNo;
	}
	public static class LevelInfo{
		public int iLevel;
		public int iBlockWidth;//block的宽度：1/2560秒
		public int iBlockHight;//block的高度：1/2560秒
		public int unitWidth;//unit的宽度：1/2560秒
		public int unitHeight;//unit的高度：1/2560秒
		public int minScale;//适合显示的最小比例尺
		public int maxScale;//适合显示的最大比例尺
		public int[] scales;//适合显示的比例尺列表
	}
}

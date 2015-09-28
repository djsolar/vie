package com.mansion.tele.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mansion.tele.action.distriManage.TestConServlet;
import com.mansion.tele.common.GeoLocation;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.common.TaskDivInfo;
import com.mansion.tele.common.UnitNo;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TaskManager {
	/**
	 * 每一层的任务数
	 */
	public int[] eachLevelTasksNum;
	
	/**
	 * Level记录个数
	 */
	public int byLevelRecordCount;
	
	/**
	 * 每一层X方向任务数
	 */
	public int[] eachLevelUnitNumX;
	/**
	 * 每一层Y方向任务数
	 */
	public int[] eachLevelUnitNumY;
	/**
	 * 每一层X方向最小UnitNo
	 */
	public int[] eachLevelMinUnitNoX;
	/**
	 * 每一层Y方向最小UnitNo
	 */
	public int[] eachLevelMinUnitNoY;
	/**
	 * 每一层X方向最大UnitNo
	 */
	public int[] eachLevelMaxUnitNoX;
	/**
	 * 每一层Y方向最大UnitNo
	 */
	public int[] eachLevelMaxUnitNoY;
	/**
	 * 当前层执行过的任务数
	 */
	private int currentExecutingNum;
	/**
	 * 当前层执行完的任务数
	 */
	private int currentExecutedNum;
	/**
	 * 所有任务的集合
	 */
	private List<Task> tasks = new ArrayList<Task>(1000);

	/**
	 * 任务分配信息，按层
	 */
	private static Map<Integer, TaskDivInfo> taskDivInfos = new HashMap<Integer, TaskDivInfo>();

	/**
	 * 所有任务的集合
	 */
	public List<Task> getTasks() {
		return tasks;
	}

	/**
	 * 创建任务链表
	 */
	public void CreateTask() {
		loadMapTaskDivInfo();
		int count =1;
//		byLevelRecordCount = TestConServlet.MaxLevel- TestConServlet.MinLevel + 1;
		byLevelRecordCount = TestConServlet.MaxLevel + 1;
		
		this.eachLevelTasksNum = new int[byLevelRecordCount];
		this.eachLevelMinUnitNoX = new int[byLevelRecordCount];
		this.eachLevelMinUnitNoY = new int[byLevelRecordCount];
		this.eachLevelMaxUnitNoX = new int[byLevelRecordCount];
		this.eachLevelMaxUnitNoY = new int[byLevelRecordCount];
		this.eachLevelUnitNumX = new int[byLevelRecordCount];
		this.eachLevelUnitNumY = new int[byLevelRecordCount];
		                        
		GeoRect taskRect = TestConServlet.DataRect;

		//取得任务集中各level的所有task的矩形范围
		
		for (int level = TestConServlet.MinLevel; level <= TestConServlet.MaxLevel; level++) {

			//获取指定层的分割信息
			long[] aiTaskArea = new long[2];
			aiTaskArea[0] = taskDivInfos.get(level).width;
			aiTaskArea[1] = taskDivInfos.get(level).height;

			//制作数据左下角
			int minX = (int) (((taskRect.left-DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN)/aiTaskArea[0])*aiTaskArea[0])+ DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN;
			int minY = (int) ((taskRect.bottom/aiTaskArea[1])*aiTaskArea[1]);
			//右上
			int maxX = (int) (((taskRect.right-DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN)/aiTaskArea[0])*aiTaskArea[0])+ DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN;
			int maxY = (int) ((taskRect.top/aiTaskArea[1])*aiTaskArea[1]);
			
			
			UnitNo minUnitNo = UnitNo.calcUnitNo(GeoLocation.valueOf(minX,minY), level);
			UnitNo maxUnitNo = UnitNo.calcUnitNo(GeoLocation.valueOf(maxX,maxY), level);
//			System.out.println(minUnitNo.iX+" : "+maxUnitNo.iX+" : "+level);
			//每层开始最小UnitNo
			this.eachLevelMinUnitNoX[level] = minUnitNo.iX;
			this.eachLevelMinUnitNoY[level] = minUnitNo.iY;
			//每层开始最大 UnitNo
			this.eachLevelMaxUnitNoX[level] = maxUnitNo.iX;
			this.eachLevelMaxUnitNoY[level] = maxUnitNo.iY;
			
			//每层X方向任务数
			this.eachLevelUnitNumX[level] = maxUnitNo.iX - minUnitNo.iX + 1;
			this.eachLevelUnitNumY[level] = maxUnitNo.iY - minUnitNo.iY + 1;
			//每层任务数
			this.eachLevelTasksNum[level] = this.eachLevelUnitNumX[level] * this.eachLevelUnitNumY[level];
			for (int y = minY; y <= maxY; y += aiTaskArea[1]) {
				for (int x = minX; x <= maxX; x += aiTaskArea[0]) {
					Task task = new Task();
					task.iTaskID = count++;
					task.level= (byte) level;
					task.left = x;
					task.bottom = y;
					task.right = x + (int) aiTaskArea[0];
					task.top = y + (int) aiTaskArea[1];
					//所有任务列表
					this.tasks.add(task);
				}
			}
		}
	}

	public void getGeoRectList(GeoRect newrect, long[] aiTaskArea,
			List<GeoRect> listResult) {
		// 获取矩形范围
		int iLeftToComplie = newrect.left;
		int iDownToComplie = newrect.bottom;
		int iRightToComplie = newrect.right;
		int iUpToComplie = newrect.top;

		for (int x = iLeftToComplie; x < iRightToComplie; x += aiTaskArea[0]) {
			for (int y = iDownToComplie; y < iUpToComplie; y += aiTaskArea[1]) {
				GeoRect collectionRect = new GeoRect();
				collectionRect.left = x;
				collectionRect.bottom = y;
				collectionRect.right = x + (int) aiTaskArea[0];
				collectionRect.top= y + (int) aiTaskArea[1];
				listResult.add(collectionRect);
			}
		}
	}
	public synchronized Task getTask() {
		Task task = null;
		if(currentExecutingNum<this.tasks.size()){
			task = this.tasks.get(this.currentExecutingNum);	
			
			//底层未完成时返回null
			int levelTaskComplet = 0;
			for(int i = 0;i< task.getLevel();i++){
				levelTaskComplet += this.eachLevelTasksNum[i];
			}
//			System.out.println(levelTaskComplet+" getTask "+currentExecutingNum);
			if(levelTaskComplet > this.currentExecutedNum){
				return null;
			}
			
			this.currentExecutingNum++;	
		}
		return task;
	}
	

	/**
	 * 提交任务
	 * @return true:所有任务都完成了
	 */
	public synchronized boolean submitTask(Task task) {
		this.currentExecutedNum++;
		
		int levelTaskComplet = 0;
		for(int i = 0;i<= task.getLevel();i++){
			levelTaskComplet += this.eachLevelTasksNum[i];
		}
//		System.out.println(levelTaskComplet+"submit "+currentExecutedNum);
		if(levelTaskComplet == currentExecutedNum){
//			System.out.println("notifyAll ..." +Thread.currentThread().getName());
			this.notifyAll();
		}
		
		return currentExecutedNum == tasks.size();
	}
	
	public synchronized boolean isCompleted() {
		return currentExecutedNum == tasks.size();
	}
	public synchronized void waitLowTask() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void loadMapTaskDivInfo(){
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("TaskDivInfo",com.mansion.tele.common.TaskDivInfo.class);
		@SuppressWarnings("unchecked")
		List<TaskDivInfo> list =  (List<TaskDivInfo>) xstream.fromXML(TaskDivInfo.class
						.getResourceAsStream("TaskDivInfo.xml"));
		for (TaskDivInfo mapInfoTemp : list) {
			taskDivInfos.put(mapInfoTemp.level, mapInfoTemp);
		}
	}

	public static TaskDivInfo getTaskDivInfo(int level){
		return taskDivInfos.get(level);
	}
}

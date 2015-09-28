package com.mansion.tele.business;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.action.distriManage.TestConServlet;
import com.mansion.tele.common.GeoLocation;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.common.UnitNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

/**
 * 任务类，保存有任务需要的信息，用于分发、调度任务。
 * 
 * @author yangz
 * 
 */
public class Task implements Cloneable,Serializable{
	private static final long serialVersionUID = -4281554400145902069L;
	int iTaskID;
	byte level;
	//任务矩形
	int left,bottom,right,top;
	
	public byte getLevel(){
		return level;
	}
	/**
	 * 将对象转换成字符串。
	 * 
	 * @return 转换成的字符串
	 */
	public String toString() {
		String strTemp = " taskid " + this.iTaskID + "层号： "
				+ this.level + " 左： " + this.left
				+ " 下：  " + this.bottom + " 右: "
				+ this.right+ " 上: "	+ this.top;
		return strTemp;
	}

	/**
	 * 获取任务名称
	 * 
	 * @return
	 */
	public String getName() {
		return this.level + "_" + String.format("%02x", this.left / 3600 / 2560) + String.format("%02x", this.bottom / 2400 / 2560);
	}

	/**
	 * 获取任务存储路径
	 * 
	 * @return
	 */
	public String getPath() {
		return this.level + File.separator + String.format("%02x", this.left / 3600 / 2560) + File.separator
				+ String.format("%02x", this.bottom / 2400 / 2560);
	}
	
	/**
	 * 覆盖克隆方法
	 * @return 克隆对象
	 */
	public Task clone(){
		Task task = new Task();
		task.level = this.level;
		task.bottom = this.bottom;
		task.left = this.left;
		task.right = this.right;
		task.top = this.top;
		return task;
	}

	/**
	 * 获得任务矩形范围
	 * @return 任务矩形范围
	 */
	public int getLeft() {
		return this.left;
	}

	/**
	 * 获得任务下纬度
	 * @return 任务下纬度
	 */
	public int getBottom() {
		return this.bottom;
	}


	/**
	 * 获得任务右经度
	 * @return 任务右经度
	 */
	public int getRight() {
		return this.right;
	}

	/**
	 * 获得任务上纬度
	 * @return 任务上纬度
	 */
	public int getTop() {
		return this.top;
	}

	/**
	 * 得到下一层的task
	 * @param task task
	 * @return 下一层的task
	 */
	public static List<Task> getLowerTaskNos(Task task) {

		GeoRect upLevelRect = GeoRect.valueOf(
				new ShpPoint(task.getLeft(), task.getBottom()),
				new ShpPoint(task.getRight(), task.getTop()));
		List<GeoRect> geoList = new ArrayList<GeoRect>();
		
		long[] aiTaskArea = new long[2];
		aiTaskArea[0] = TaskManager.getTaskDivInfo(task.level - 1).width;
		aiTaskArea[1] = TaskManager.getTaskDivInfo(task.level - 1).height;
		
		TestConServlet.taskManager.getGeoRectList(upLevelRect, aiTaskArea, geoList);

		List<Task> downTaskList = new ArrayList<Task>();

		byte bDownLevel = (byte) (task.level - 1);
		for (int i = 0; i < geoList.size(); i++) {
			Task downtask = task.clone();
			downtask.level = bDownLevel;
			downtask.left = geoList.get(i).left;
			downtask.bottom = geoList.get(i).bottom;
			downtask.right = geoList.get(i).right;
			downtask.top = geoList.get(i).top;
			downTaskList.add(downtask);
		}

		return downTaskList;
	}
	
	public String getFilePath(){
		GeoLocation geoLocation = GeoLocation.valueOf(this.left, this.bottom);
		UnitNo unitNo = UnitNo.calcUnitNo(geoLocation, this.level);
		String strFilePath = this.level+ "_" + unitNo.iX + "_"+ unitNo.iY;
		return strFilePath;
	}
	
	public UnitNo getUnitNo(){
		GeoLocation location = GeoLocation.valueOf(this.left, this.bottom);
		return UnitNo.calcUnitNo(location, this.level);
	}
}

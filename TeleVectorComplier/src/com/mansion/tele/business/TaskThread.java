package com.mansion.tele.business;

import com.mansion.tele.action.distriManage.TestConServlet;
import com.mansion.tele.business.viewdata.CombineMMData;
import com.mansion.tele.business.viewdata.CombineParser;
import com.mansion.tele.util.EstimateTime;

public class TaskThread implements Runnable{
	private int taskNumber = TestConServlet.taskManager.getTasks().size();
	public static String dataPath = "../../data/vector";// 数据存储路径
	String midDataPath = dataPath + "/mid";//输出中间文件 包括（升层数据，格式化出去的.tuv数据）
	String outDataPath = dataPath + "/out";//输出最终结果文件
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			Task task = null;
			try {
				task = TestConServlet.taskManager.getTask();
				if(task != null){
					TaskData taskData = new TaskData(task);
					long start = System.currentTimeMillis();
					System.out.println(task.iTaskID +"开始任务读取 ： "+task.getLevel());
					//读数据、升层
					if(task.getLevel() == 0){
						taskData.loadDataFromDB();
					}else{
						taskData.loadDataFromLow(midDataPath);
					}
					System.out.println(task.iTaskID +"数据处理 ： "+task.getLevel());
					//数据处理
					taskData.buildData();
					//序列化出去。
					System.out.println(task.iTaskID +"开始任务保存 ： "+task.getLevel());
					taskData.saveData(midDataPath);
					System.out.println(task.iTaskID +"格式转换 ： "+task.getLevel());
					//格式转换
					taskData.formatData(midDataPath);
					
					System.out
							.println("task Id: "+task.iTaskID +" cost time："
									+ (System.currentTimeMillis() - start)
									+ " task name: "
									+ task.getName()+" complete： "+
									+ ((++TestConServlet.CompleteTask) * 100 / taskNumber)
									+ "%");
					if(taskData.task.getLevel() == 0 && TestConServlet.MakeMM && TestConServlet.MakeRoad){
						taskData.buildMMData(midDataPath);
						if(taskData.network.roadList != null && taskData.network.roadList.size() !=0){
							taskData.saveMMData(midDataPath);
						}
						taskData.task.level = 0;
					}
					//提交任务
					if(TestConServlet.taskManager.submitTask(task) == true){
						//缝合适量数据
						CombineParser.parser(midDataPath, outDataPath,TestConServlet.taskManager);
						//处理Mm边界
						if(TestConServlet.MakeMM && TestConServlet.MakeRoad){
							CombineMMData.saveMM(midDataPath, outDataPath, TestConServlet.taskManager);
						}
						System.out.println("程序结束运行："+EstimateTime.currentTimeToString());
						System.out.println("用时：" + EstimateTime.timeToString(TestConServlet.startTime));
					}
				}else{
					if(TestConServlet.taskManager.isCompleted() == false){
						TestConServlet.taskManager.waitLowTask();
					}else{
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(task != null){
					System.out.println("发生异常 task "+task.getName());
				}
				System.out.println("发生异常 程序退出 ");
				System.exit(0);
			}
		}
	}
}

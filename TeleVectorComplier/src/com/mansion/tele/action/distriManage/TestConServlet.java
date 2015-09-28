package com.mansion.tele.action.distriManage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.Task;
import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.TaskManager;
import com.mansion.tele.business.TaskThread;
import com.mansion.tele.business.background.LayerCollection;
import com.mansion.tele.business.landmark.Landmark;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.log.TelePrintStream;
import com.mansion.tele.util.EstimateTime;
import com.mansion.tele.util.Util;

/**
 * 启动socket服务器
 * 
 * @author wenxc
 * 
 */
public class TestConServlet {
	//线程数
	public static int threadCount;
	//数据制作范围
	public static GeoRect DataRect;
	//数据制作的最小最大层号
	public static int MinLevel;
	public static int MaxLevel;
	//制作要素 
	public static boolean MakeRoad;
	public static boolean MakeBackground;
	public static boolean MakeLandmark;
	public static boolean MakeMM;
	public static boolean OutRouteStrid;
	public static String dataPath = "../../data/vector";// 数据存储路径
	// 结果文件保存路径
	String strDestPath = dataPath + "/out";
	// 中间数据文件保存路径
	String strMPath = dataPath + "/mid";

	public static TaskManager taskManager = new TaskManager();
	public static List<TaskData> mm_data_mid = new ArrayList<TaskData>();
	public static List<Task> mm_task_mid = new ArrayList<Task>();
	public static int count_mm = 0;
	public static int CompleteTask = 0;//已完成的任务数

	public static long startTime;
	/**
	 * TeleCompiler入口函数
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		startTime = System.currentTimeMillis();
		
		System.out.println("程序开始运行：" + EstimateTime.currentTimeToString());
		initTool();
		initProperties();
		LayerCollection.init();
		DataManager.init();
		LayerCollection.init();
		TelePrintStream.setRedirectLog(false);
		
		// 清空工作路径文件
		File file = new File(TaskThread.dataPath);
		Util.delPath(file);

		Landmark.init();

		// 创建任务列表
		taskManager.CreateTask();

		// 以下为缝合测试代码
		// String midDataPath =
		// "\\\\10.1.7.100\\share\\temp\\tele\\data\\vector\\mid";//输出中间文件
		// 包括（升层数据，格式化出去的.tuv数据）
		// String outDataPath =
		// "\\\\10.1.7.100\\share\\temp\\tele\\data\\vector\\out";//输出最终结果文件
		// CombineParser.parser(midDataPath,
		// outDataPath,TestConServlet.taskManager);
		// if(true){
		// return;
		// }

		System.out.println("创建任务链表结束");
		for (int i = 0; i < taskManager.getTasks().size(); i++) {
			Task task = taskManager.getTasks().get(i);
			System.out.println(task.toString());
		}

		// 启动任务线程
		for (int i = 0; i < threadCount; i++) {
			Thread thread = new Thread(new TaskThread());
			thread.start();
		}
	}
	
	static void initProperties() throws FileNotFoundException, IOException{
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File("ThreadConfig.properties")));
		threadCount = Integer.parseInt(properties.getProperty("ClientThreadNum"));
		DataRect = new GeoRect();
		DataRect.left = Integer.parseInt(properties.getProperty("Left"));
		DataRect.bottom = Integer.parseInt(properties.getProperty("Bottom"));
		DataRect.right = Integer.parseInt(properties.getProperty("Right"));
		DataRect.top = Integer.parseInt(properties.getProperty("Top"));
		MinLevel = Integer.parseInt(properties.getProperty("MinLevel"));
		MaxLevel = Integer.parseInt(properties.getProperty("MaxLevel"));
		MakeRoad = Boolean.parseBoolean(properties.getProperty("MakeRoad"));
		MakeBackground = Boolean.parseBoolean(properties.getProperty("MakeBackground"));
		MakeLandmark = Boolean.parseBoolean(properties.getProperty("MakeLandmark"));
		MakeMM = Boolean.parseBoolean(properties.getProperty("MakeMM"));
		if(properties.getProperty("OutRouteStrid") != null){
			OutRouteStrid = Boolean.parseBoolean(properties.getProperty("OutRouteStrid"));
		}
	}
	
	static void initTool() throws IOException{
		if(Util.routefile.exists()){
			Util.routefile.delete();
		}
		Util.routefile.createNewFile();
	}
}

package com.sunmap.shpdata.tools.main;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sunmap.shpdata.tools.conf.ConfPro;
import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.constvalue.DataType;
import com.sunmap.shpdata.tools.dao.TableDAO;
import com.sunmap.shpdata.tools.daoimpl.shpdaoimpl.ShpTableDaoImpl;
import com.sunmap.shpdata.tools.execute.RemoveIndex;
import com.sunmap.shpdata.tools.execute.RenewIndex;
import com.sunmap.shpdata.tools.execute.ShpThread;
import com.sunmap.shpdata.tools.util.ConstantValue;
import com.sunmap.shpdata.tools.util.DataConnectionTool;
import com.sunmap.shpdata.tools.util.JdbcUtil;
import com.sunmap.shpdata.tools.util.Util;
import com.sunmap.shpdata.tools.vo.Table;

import form.Form;
import form.TableSubArea;
import formconf.Pro;

public class ImportMain {

	public static int x = 1;

	public void createAndImport() {
		ShpConf conf = new ShpConf();
		if (ConstantValue.IMPORT_TYPE_SHP.equals(conf.getStrImportType())) {
			
			this.shpDataImport(conf);
		}
	}

	public void shpDataImport(ShpConf conf) {
		JOptionPane jop = new JOptionPane();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		x = 0;
		Form.setTextCon("导入开始:"
				+ format.format(new Date(System.currentTimeMillis())));
		
		List<File> fileList = new ArrayList<File>();
		initstatic();
		//数据建表
		try {
			String strPath = conf.getInputpath();
			// 1.获得文件列表
			fileList = Util.filterList(Util.getFileList(strPath));
			Form.setLabel4(fileList.size() + "");
			if(fileList.size() < Integer.parseInt(conf.getThreadnum())){
				ConfPro confPro = new ConfPro();
				confPro.setShpThreadnum(fileList.size() + "");
				conf = new ShpConf();
			}
			TableDAO tableDAO = new ShpTableDaoImpl();
			boolean flag = false;
			Form.setTextCon("开始建表请等待...");
			flag = tableDAO.createTable1(Util.createtableList);
			if (!flag) {
				jop.showMessageDialog(null, "建表失败");
				x=1;
				Util.flag = false;
				return;
			} else {
//				String tableinfoString = this.isExistSubArea();
//				if(!"".equals(tableinfoString) && conf.getDriver().equals(Util.POSTGRESQLNAME)){
//					TableSubArea tableSubArea = new TableSubArea(new JFrame(), true,tableinfoString, fileList);
//					tableSubArea.setVisible(true);
//				}
//				if(!Util.flag){
//					return;
//				}
				Form.setTextCon("建表成功");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			jop.showMessageDialog(null, "建表失败");
			x=1;
			Util.flag = false;
			File file1 = new File("error.txt");
			try {
				file1.createNewFile();
				if(file1.exists()){
					file1.delete();
				}
				FileWriter out = new FileWriter(file1, true);
				PrintWriter writer = new PrintWriter(out);
				e1.printStackTrace(writer);
				writer.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			e1.printStackTrace();
			return;
		}
		
		
		//删除索引
		try {
			dropIndex();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//数据导入
		try {
			this.dataImport(conf, fileList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//插入Element和Mapid
		try {
			setElementAndMapID();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//创建索引
		try {
			createIndex();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		x = 1;
		
		Form.setTextCon("导入结束:"
				+ format.format(new Date(System.currentTimeMillis())));
	}

	/**
	 * 导入数据
	 * @param conf
	 * @param fileList
	 * @throws Exception
	 */
	private void dataImport(ShpConf conf, List<File> fileList) throws Exception {
		// TODO Auto-generated method stub
//		String strPath = conf.getInputpath();
//		// 1.获得文件列表
//		List<File> fileList = Util.filterList(Util.getFileList(strPath));
		int iThreadNum = Integer.parseInt(conf.getThreadnum());
		CountDownLatch donsignl1 = new CountDownLatch(iThreadNum);
		ShpThread rtShpThread = new ShpThread(fileList, DataType.SHP, donsignl1);
		final Thread[] astThread = new Thread[iThreadNum];
		// 3.解析文件到数据库
		for (int i = 0; i < iThreadNum; i++) {
			astThread[i] = new Thread(rtShpThread);
			astThread[i].start();
		}
		donsignl1.await();
	}
	
	/**
	 * 返回表名
	 * @return
	 */
	public static String returnElement(){
		ShpConf conf = new ShpConf();
		List<Table> list = Util.createtableList;
		String returnTableNameString = "";
		if(list != null){
			for (Table table : list) {
				if(Util.POSTGRESQLNAME.equals(conf.getDriver())){
					if(JdbcUtil.PostGresqlTableISExists(table.getTableName())){
						if("".equals(returnTableNameString)){
							returnTableNameString = table.getTableName() ;
						}else{
							returnTableNameString = returnTableNameString + "," + table.getTableName();
						}
					}
				}else if (Util.ORACLEDRIVERNAME.equals(conf.getDriver())) {
					if(JdbcUtil.OracleTableISExists(table.getTableName())){
						if("".equals(returnTableNameString)){
							returnTableNameString = table.getTableName() ;
						}else{
							returnTableNameString = returnTableNameString + "," + table.getTableName();
						}
					}
				}else if (Util.MYSQLDRIVERNAME.equals(conf.getDriver())) {
					if(JdbcUtil.MySqlTableISExists(table.getTableName())){
						if("".equals(returnTableNameString)){
							returnTableNameString = table.getTableName() ;
						}else{
							returnTableNameString = returnTableNameString + "," + table.getTableName();
						}
					}
				}
			}
		}
		return returnTableNameString;
	}
	
	/**
	 * 删除索引
	 * @throws Exception
	 */
	private void dropIndex() throws Exception{
		Map<String, String[]> map = Util.element;
		Iterator<Entry<String, String[]>> it = map.entrySet().iterator();
		List<String> list1 = new ArrayList<String>();
		while (it.hasNext()) {
			Entry entry = it.next();
			String strtableName = entry.getKey().toString();
			list1.add(strtableName);
		}
		int iThreadNum1 = list1.size();
		Thread[] astThread1 = new Thread[iThreadNum1];
		Thread t = Thread.currentThread();
		CountDownLatch doneSignal = new CountDownLatch(iThreadNum1);
		for (int i = 0; i < astThread1.length; i++) {
			RemoveIndex reindex = new RemoveIndex(list1.get(i), doneSignal);
			astThread1[i] = new Thread(reindex);
			astThread1[i].start();
		}
		doneSignal.await();
		System.out.println(DateFormat.getDateInstance().format(new Date(System.currentTimeMillis())));
	}
	/**
	 * 建立索引
	 * @throws Exception
	 */
	private void createIndex() throws Exception{
		Map<String, String[]> map1 = Util.element;
		Iterator<Entry<String, String[]>> it1 = map1.entrySet().iterator();
		List<String> list = new ArrayList<String>();
		while (it1.hasNext()) {
			Entry entry = it1.next();
			String strtableName = entry.getKey().toString();
			list.add(strtableName);
		}
		int iThreadNum2 = list.size();
		Util.threadNum = iThreadNum2;
		final Thread[] astThread2 = new Thread[iThreadNum2];
		CountDownLatch donsignl = new CountDownLatch(iThreadNum2);
		for (int i = 0; i < astThread2.length; i++) {
			RenewIndex reindex = new RenewIndex(list.get(i), donsignl);
			astThread2[i] = new Thread(reindex);
			astThread2[i].start();
		}
		donsignl.await();
	}
	/**
	 * 导入element以及Mapdata
	 * @throws Exception
	 */
	private void setElementAndMapID() throws Exception{
		Set<String> mapdata = Util.mymapidset;
		if (mapdata.size() > 0) {
			for (String string : mapdata) {
				if(!this.checkMapdata(string)){
				String sql = "insert into MAPDATA(id,mymapid,evaltype) values('"
					+ UUID.randomUUID() + "','" + string + "',0)";
				JdbcUtil.executeSql(sql);
				}
			}
		}
		Map<String, String[]> element = Util.element;
		if (element.size() > 0) {
			Iterator<Entry<String, String[]>> it2 = element.entrySet()
					.iterator();
			while (it2.hasNext()) {
				Entry<String, String[]> entry = it2.next();
				String strTableName = entry.getKey();
				String[] strAllattrscope = entry.getValue();
				if(checkElement(strTableName, strAllattrscope)){
					String insertsql = "insert into ELEMENT(id,elementname," +
					"allattributename,scope,evaltype)"
					+ " values('" + UUID.randomUUID()+"','"
					+ strTableName
					+ "','"
					+ strAllattrscope[0]
					                  + "',"
					                  + "'"
					                  + strAllattrscope[1] + "',0)";
					JdbcUtil.executeSql(insertsql);
				}
			}
	}
	}
	/**
	 * 检查element
	 * @param strTableName
	 * @param strAllattrscope
	 * @return
	 */
	private boolean checkElement(String strTableName,String[] strAllattrscope) {
		// TODO Auto-generated method stub
		String checksql = "select allattributename from element where elementname = '" + strTableName + "'";
		try {
			DataConnectionTool dataConnectionTool = new DataConnectionTool();
			Statement statement = dataConnectionTool.openNewStatement();
			ResultSet resultSet = statement.executeQuery(checksql);
			if(resultSet.next()){
				String content = resultSet.getString(1);
				List<String> list = Arrays.asList(strAllattrscope[0].split(","));
				boolean flag1 = false;
				for (String string : list) {
					if(!content.contains(string)){
						content = content + "," + string;
						flag1 = true;
					}
				}
				if (flag1) {
					dataConnectionTool.setConnectionCommit(false);
					strAllattrscope[0] = content;
					String detelevlues = "delete from element where elementname='" + strTableName + "'";
					statement.execute(detelevlues);
					dataConnectionTool.getConnection().commit();
					String insertsql = "insert into ELEMENT(id,elementname," +
					"allattributename,scope,evaltype)"
					+ " values('" + UUID.randomUUID()+"','"
					+ strTableName
					+ "','"
					+ strAllattrscope[0]
					+ "',"
					+ "'"
					+ strAllattrscope[1] + "',0)";
					statement.execute(insertsql);
					dataConnectionTool.getConnection().commit();
				}
				dataConnectionTool.closeStatement();
				dataConnectionTool.closeConnection();
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 检查Mapdata是否存在此值
	 * @param mymapid
	 * @return
	 * @throws Exception
	 */
	private boolean checkMapdata(String mymapid) throws Exception{
		String checksql = "select 1 from mapdata where mymapid = '" + mymapid + "'";
		DataConnectionTool dataConnectionTool = new DataConnectionTool();
		Statement statement = dataConnectionTool.openNewStatement();
		ResultSet resultSet = statement.executeQuery(checksql);
		boolean flag = resultSet.next();
		resultSet.close();
		dataConnectionTool.closeStatement();
		dataConnectionTool.closeConnection();
		return flag;
	}
	
	private String isExistSubArea() throws Exception{
		Map<String, Integer> tablerecordnuMap = Util.tableRecordnum;
		Set<Entry<String, Integer>> set = tablerecordnuMap.entrySet();
		Iterator<Entry<String, Integer>> iterator = set.iterator();
		String tableinfoString = "";
		while (iterator.hasNext()) {
			Entry<String , Integer> entry =  iterator.next();
			String tablenameString = entry.getKey();
			Integer recordnum = entry.getValue();
			boolean flag1 = (recordnum /10000) > 500;
			if(flag1){
				List<String> tablecolumn = JdbcUtil.retuTableColumnList(tablenameString);
				flag1 = (tablecolumn.contains("mymapid") || tablecolumn.contains("mymapid".toUpperCase()));
			}else {
				flag1 = false;
			}
			if(flag1){
				tableinfoString = tableinfoString + tablenameString + "(" + recordnum / 10000 + "w),";
			}
		}
		return tableinfoString;
	}
	/**
	 * 初始化信息集合
	 */
	public static void initstatic() {
		Util.mymapidset = new HashSet<String>();
		Util.subAreaName = new HashMap<String, List<String>>();
		Util.flag = true;
		ShpTableDaoImpl.createTaMap = new HashMap<String, Table>();
		ShpTableDaoImpl.errorString = null;
		File file1 = new File("log");
		if(!file1.exists()){
			file1.mkdir();
		}
		if(file1.listFiles().length > 20){
			File[] file1arr = file1.listFiles();
			for (int i = 0; i < file1arr.length; i++) {
				file1arr[i].delete();
			}
		}
		File file = new File(
				"log\\ExceptionLog" +
				new Date(System.currentTimeMillis()).toLocaleString().replaceAll(" ", "")
				.replaceAll(":", "") 
				+ ".log");
		try {
			file.createNewFile();
			Util.logName = file.getPath();
		} catch (IOException e) {
			e.printStackTrace();
		};
		
	}

}

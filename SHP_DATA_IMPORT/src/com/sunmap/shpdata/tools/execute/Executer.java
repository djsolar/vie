package com.sunmap.shpdata.tools.execute;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.dao.ToolDAO;
import com.sunmap.shpdata.tools.util.Util;

/**
 * 执行器 created by
 * 
 * @author daig
 */
public class Executer {
	/**
	 * 数据访问
	 */
	protected ToolDAO toolDao;
	/**
	 * MapID
	 */
	private File mapIDFile;
	/**
	 * 配置
	 */
	private ShpConf conf;


	/**
	 * sql and ctl
	 * 
	 * @param mapIDFile
	 *            MapID文件
	 * @param conf
	 *            shp配置文件
	 */
	public Executer(File mapIDFile, ShpConf conf) {
		this.mapIDFile = mapIDFile;
		this.conf = conf;
		this.toolDao = new ToolDAO(conf);
	}

	/**
	 * 创建sql和Ctl
	 * 
	 * @throws IOException
	 *             IO异常
	 */
	public void buildSqlAndCtl() throws Exception {

		File[] list = mapIDFile.listFiles();
		for (int i = 0; i < list.length; i++) {
			if (list[i].getName().endsWith("shp")) {
				int dot = list[i].getName().indexOf(".");
				String useName = list[i].getName().substring(0, dot);
				String tableName = useName + "_" + mapIDFile.getName();
				try {
					int nowgid = toolDao.findMaxGid(useName) + 1;

					String createSQLandCTL = "shp2sdo " + conf.getInputpath()
							+ "/" + mapIDFile.getName() + "/" + useName + " "
							+ conf.getOutputpath() + "/" + tableName
							+ " -i gid -s 8307 -g geom -d -n " + nowgid;
					System.out.println(createSQLandCTL);
					Util.doWaitFor(Runtime.getRuntime().exec(createSQLandCTL));
					MargeSql();
				} catch (Exception e) {
					System.out.println("寻找最带 gid 出错，跳过此元素（也可能此元素不存在）");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 合并生成出来的sql
	 * 
	 * @throws IOException
	 *             IO异常
	 */
	public void MargeSql() throws Exception {
		String outputpath = conf.getOutputpath();
		File file2 = new File(outputpath);
		File[] list2 = file2.listFiles();
		// 生成all.sql和quit.sql
		File allSQL = new File(outputpath + "/all.sql");
		allSQL.createNewFile();
		File quitSQL = new File(outputpath + "/quit.sql");
		quitSQL.createNewFile();
		FileOutputStream fos = new FileOutputStream(quitSQL);
		fos.write("quit;".getBytes());
		fos.flush();
		fos.close();
		FileWriter fw = new FileWriter(allSQL);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < list2.length; i++) {
			if (list2[i].getName().endsWith("sql")
					&& !list2[i].getName().contains("all")) {
				bw.write("@@" + list2[i].getName());
				bw.newLine();
			}
		}
		bw.write("@@quit.sql");
		bw.flush();
		bw.close();
		fw.close();
		buildTable();
	}

	/**
	 * 建表
	 * 
	 * @throws Exception
	 *             IO异常
	 */
	public void buildTable() throws Exception {
		String outputpath = conf.getOutputpath();
		String username = conf.getUsername();
		String password = conf.getPassword();
		// 建表
		String exeSQL = "sqlplus " + username + "/" + password + "@orcl @"
				+ outputpath + "/all.sql";
		System.out.println(exeSQL);
		Util.doWaitFor(Runtime.getRuntime().exec(exeSQL));
		saveCtlData();
	}

	/**
	 * 保存Ctl数据
	 * 
	 * @throws Exception
	 *             IO 异常
	 */
	public void saveCtlData() throws Exception {
		String outputpath = conf.getOutputpath();
		String username = conf.getUsername();
		String password = conf.getPassword();
		File file2 = new File(outputpath);
		File[] list2 = file2.listFiles();
		// 导数据
		for (int i = 0; i < list2.length; i++) {
			if (list2[i].getName().endsWith("ctl")) {
				int dot = list2[i].getName().indexOf(".");
				String tableName = list2[i].getName().substring(0, dot);
				Util.doWaitFor(Runtime.getRuntime().exec(
						"sqlldr " + username + "/" + password + "@orcl "
								+ outputpath + "/" + tableName));
				MargeTableDate(tableName);
			}
		}
	}

	/**
	 * 合并数据库内容
	 * 
	 * @param tableName
	 *            数据库表
	 * @throws Exception
	 *             SQLException
	 */
	private void MargeTableDate(String tableName) throws Exception {
		toolDao.margeTableDate(tableName);
		clean();
	}

	/**
	 * 清除output文件夹数据
	 */
	public void clean() {
		File outputFile = new File(conf.getOutputpath());
		File[] list = outputFile.listFiles();
		for (File file : list) {
			file.delete();
		}
	}

	/**
	 * 开始执行
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		buildSqlAndCtl();
	}
}

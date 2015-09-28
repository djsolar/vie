package com.sunmap.shpdata.tools.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.constvalue.DataType;
import com.sunmap.shpdata.tools.dao.IKiwiFileDao;
import com.sunmap.shpdata.tools.dao.IShpFiledao;
import com.sunmap.shpdata.tools.dao.TableDAO;
import com.sunmap.shpdata.tools.daoimpl.KiwiDaoImpl.KiwiDaoImpl;
import com.sunmap.shpdata.tools.daoimpl.KiwiDaoImpl.KiwiFileDaoImpl;
import com.sunmap.shpdata.tools.daoimpl.shpdaoimpl.ShpTableDaoImpl;
import com.sunmap.shpdata.tools.execute.KiwiThread;
import com.sunmap.shpdata.tools.execute.RemoveIndex;
import com.sunmap.shpdata.tools.execute.RenewIndex;
import com.sunmap.shpdata.tools.execute.RtShpThread;
import com.sunmap.shpdata.tools.factory.ShpFileFactory;
import com.sunmap.shpdata.tools.util.ConstantValue;
import com.sunmap.shpdata.tools.util.JdbcUtil;
import com.sunmap.shpdata.tools.util.Util;

import form.f;

public class ImportData {
	public static int x = 1;

	public static void importRoadNet(ShpConf conf) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(format.format(new Date(System.currentTimeMillis()))+"--���뿪ʼ");
		String strPath = conf.getInputpath();
		int iThreadNum = 0;
		long start = System.currentTimeMillis();
		// 1.����ļ��б�
		List<File> fileList = Util.getFileList(strPath);
		// 2.�����ļ��е��ֶ���Ϣ�����ݿ��н���
		TableDAO tabledao = new KiwiDaoImpl();
		f.setLabel3(fileList.size() + "");
		try {
			System.out.println(format.format(new Date(System.currentTimeMillis()))+"--����ʼ");
			f.setTextCon("����ļ�������....");
			tabledao.createTable(fileList);
			f.setTextCon("������");
		} catch (Exception e) {
			System.out.println("����ʧ��");
			e.printStackTrace();
		}
		iThreadNum = fileList.size();
		System.out.println(format.format(new Date(System.currentTimeMillis()))+"--�������");
		CountDownLatch donsignlKiwi = new CountDownLatch(iThreadNum);
		KiwiThread kiwiThread = new KiwiThread(fileList,donsignlKiwi);
		
		final Thread[] astThread = new Thread[iThreadNum];
		for (int i = 0; i < iThreadNum; i++) {
			astThread[i] = new Thread(kiwiThread);
			astThread[i].start();
		}
		donsignlKiwi.await();
//		IKiwiFileDao dao = new KiwiFileDaoImpl();
		
//		long begin = 0L;
//		long end = 0L;
//		long m = 0L;
//		long start = System.currentTimeMillis();
//		for (int i = 0; i < fileList.size(); i++) {
//			begin = System.currentTimeMillis();
//			dao.readFile(fileList.get(i));
//			end = System.currentTimeMillis();
//			m = (end-begin)/1000;
//			System.out.println(format.format(new Date(end))+"--"+fileList.get(i).getName()+"���ļ�����"+m/60+"m"+m%60+"s");
//		}
		long endl = System.currentTimeMillis();
		long m = (endl-start)/1000;
		System.out.println(format.format(new Date(endl))+"--�������,����ʱ��"+m/60+"m"+m%60+"s");
		
//		final Thread[] astThread2 = new Thread[iThreadNum];
//		CountDownLatch donsignl = new CountDownLatch(iThreadNum);
//		File dbffile = null;
//		int local = 0;
//		String strTableName = "";
//		for (int i = 0; i < astThread.length; i++) {
//			dbffile = fileList.get(i);
//			local = dbffile.getName().lastIndexOf(".");
//			strTableName = dbffile.getName().substring(0, local);
//			RenewIndex reindex = new RenewIndex(strTableName, donsignl);
//			astThread[i] = new Thread(reindex);
//			astThread[i].start();
//		}
//		donsignl.await();
		//ִ�нű��ļ�
		
		
	}
	public static void main(String[] arg) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		x = 0;
		f.setTextCon("���뿪ʼ:"
				+ format.format(new Date(System.currentTimeMillis())));
		/*
		 * ��ʼ����������
		 */
//		System.out.println(System.currentTimeMillis()+"----��ʼ���������ݿ�ʼ");
//		initstatic();
//		System.out.println(System.currentTimeMillis()+"----��ʼ���������ݽ���");
		/*
		 * ��ʼ��conf�ļ�
		 */
		ShpConf conf = null;
		try {
			conf = new ShpConf();
		} catch (Exception e) {
			System.out.println("conf�ļ���ʼ��ʧ��");
			e.printStackTrace();
			return;
		}
		try {
			importByType(conf);
		} catch (Exception e) {
			System.out.println("��������ʧ��");
			e.printStackTrace();
		}
		
		x = 1;
		
		f.setTextCon("�������:"
				+ format.format(new Date(System.currentTimeMillis())));
		
	}

	private static void importByType(ShpConf conf) throws Exception {
			if (ConstantValue.IMPORT_TYPE_ROADNET.equalsIgnoreCase(conf.getStrImportType())) {
				importRoadNet(conf);
			}
			if (ConstantValue.IMPORT_TYPE_SHP.equalsIgnoreCase(conf.getStrImportType())) {
				shpImport(conf);
			}
		
	}
	public static void shpImport(ShpConf conf) throws InterruptedException{
		int iThreadNum = Integer.parseInt(conf.getThreadnum());
		if(iThreadNum > 16){
			iThreadNum = 14;
		}
		initstatic();
		String strPath = conf.getInputpath();
		// 1.����ļ��б�
		List<File> fileList = Util.getFileList(strPath);
		// 2.�����ļ��е��ֶ���Ϣ�����ݿ��н���
		TableDAO tabledao = new ShpTableDaoImpl();
		f.setLabel3(fileList.size() + "");
		try {
			f.setTextCon("����ļ�������....");
			tabledao.createTable(fileList);
			f.setTextCon("������");
		} catch (Exception e) {
			System.out.println("����ʧ��");
			e.printStackTrace();
		}
		/**
		 * ȥ��Ԫ�ر�����
		 */
		Map<String, Integer> map = Util.tableFieldmap;
		Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
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
		System.out.println(fileList.size());
		System.out.println(DateFormat.getDateInstance().format(new Date(System.currentTimeMillis())));
		// 3.��ý��������ļ��Ķ���
		IShpFiledao shpFiledao = ShpFileFactory.createShpFiledao(DataType.RT);
		CountDownLatch donsignl1 = new CountDownLatch(iThreadNum);
		RtShpThread rtShpThread = new RtShpThread(fileList, DataType.RT, donsignl1);
		final Thread[] astThread = new Thread[iThreadNum];
		// 3.�����ļ������ݿ�
		for (int i = 0; i < iThreadNum; i++) {
			astThread[i] = new Thread(rtShpThread);
			astThread[i].start();
		}
		donsignl1.await();
		try {
			Set<String> mapdata = Util.mymapidset;
			if (mapdata.size() > 0) {
				Connection con1 = JdbcUtil.getConnection();
				con1.setAutoCommit(false);
				Statement stm1 = con1.createStatement();
				for (String string : mapdata) {
					String sql = "insert into MAPDATA(id,mymapid,evaltype) values('"
						+ UUID.randomUUID() + "','" + string + "',0)";
					stm1.execute(sql);
				}
				con1.commit();
				stm1.close();
				con1.close();
			}
			Map<String, String[]> element = Util.element;
			if (element.size() > 0) {
				Iterator<Entry<String, String[]>> it2 = element.entrySet()
						.iterator();
				Connection con1 = JdbcUtil.getConnection();
				con1.setAutoCommit(false);
				Statement stm1 = con1.createStatement();
				while (it2.hasNext()) {
					Entry<String, String[]> entry = it2.next();
					String strTableName = entry.getKey();
					String[] strAllattrscope = entry.getValue();
					String insertsql = "insert into ELEMENT(id,elementname," +
							"allattributename,scope,evaltype)"
							+ " values('" + UUID.randomUUID()+"','"
							+ strTableName
							+ "','"
							+ strAllattrscope[0]
							+ "',"
							+ "'"
							+ strAllattrscope[1] + "',0)";
					stm1.execute(insertsql);
				}
				con1.commit();
				stm1.close();
				con1.close();
			}
			Map<String, Integer> map1 = Util.tableFieldmap;
			Iterator<Entry<String, Integer>> it1 = map1.entrySet().iterator();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
//		x = 1;
//		f.setTextCon("�������:"
//				+ new Date(System.currentTimeMillis()).toLocaleString());
	}
	public static void initstatic() {
		Util.mymapidset = new HashSet<String>();
		Util.tableFieldmap = new HashMap<String, Integer>();
		Util.map = new HashMap<String, Integer>();
		Util.element = new HashMap<String, String[]>();
		Util.countNum = 0;
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

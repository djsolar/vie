package com.sunmap.shpdata.tools.execute;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.sunmap.shpdata.tools.dao.IKiwiFileDao;
import com.sunmap.shpdata.tools.daoimpl.KiwiDaoImpl.KiwiFileDaoImpl;
import com.sunmap.shpdata.tools.util.Util;

import form.f;

public class KiwiThread implements Runnable{

	private int fieldTallCount;
	private int finishCount;
	private List<File> fileList = null;
	private CountDownLatch donsignl1;
	private IKiwiFileDao kiwiFileDao = null;
	
	public KiwiThread(List<File> fileList,CountDownLatch donsignl1) {
		super();
		this.fieldTallCount = fileList.size();
		this.finishCount = 0;
		this.fileList = fileList;
		this.donsignl1 = donsignl1;
	}

	@Override
	public void run() {
//		this.writeThreadStart();
		kiwiFileDao = new KiwiFileDaoImpl();
		File dbffile = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long begin = System.currentTimeMillis();
		while (this.fieldTallCount >this.finishCount) {
			
			synchronized (this) {
				if (this.fieldTallCount >this.finishCount) {
						 dbffile = fileList.get(this.finishCount);
				}			
				this.finishCount++;
			}
			
			try {
				if (this.fieldTallCount >=this.finishCount) {
					System.out.println(format.format(new Date(System.currentTimeMillis()))+"--"+dbffile.getName()+"读文件开始");
					kiwiFileDao.readFile(dbffile);
					long end = System.currentTimeMillis();
					long f = (end-begin)/1000;
					System.out.println(format.format(new Date(end))+"--"+dbffile.getName()+"读文件结束"+f/60+"m"+f%60+"s");
					if (this.fieldTallCount <=this.finishCount){
						System.out.println(format.format(new Date(System.currentTimeMillis()))+"--1"+dbffile.getName());
					}
				}
				else{
					System.out.println(format.format(new Date(System.currentTimeMillis()))+"--2"+dbffile.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
				f.setTextCon(e.toString());
				continue;
			}
			f.setLabel1(this.finishCount + "");
		}
		donsignl1.countDown();
//		this.writeThreadEnd();
		
	}
	/**
	 * 判断线程开始
	 */
	private synchronized void writeThreadStart(){
		File file = new File(Util.logName);
		try {
			file.createNewFile();
			FileWriter out = new FileWriter(file,true);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			out.write(format.format(new Date(System.currentTimeMillis())) + 
					Thread.currentThread().getName() + ":开始\r" );
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 判断线程结束
	 */
	private synchronized void writeThreadEnd(){
		File file = new File(Util.logName);
		try {
			file.createNewFile();
			FileWriter out = new FileWriter(file,true);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			out.write(format.format(new Date(System.currentTimeMillis())) + 
					Thread.currentThread().getName() + ":结束\r" );
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

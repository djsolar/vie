package com.sunmap.shpdata.tools.execute;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.sunmap.shpdata.tools.constvalue.DataType;
import com.sunmap.shpdata.tools.dao.IShpFiledao;
import com.sunmap.shpdata.tools.factory.ShpFileFactory;
import com.sunmap.shpdata.tools.util.Util;

import form.f;

public class RtShpThread implements Runnable{
	private int fieldTallCount;
	private int finishCount;
	private List<File> fileList = null;
	private DataType eDataType=null;
	private CountDownLatch donsignl1;
	  
	  
	public RtShpThread(List<File> fileList,DataType eDataType, CountDownLatch donsignl1) {
		this.fieldTallCount = fileList.size();
		this.finishCount=0;
		this.fileList = fileList;
		this.eDataType = eDataType;
		this.donsignl1 = donsignl1;
	}
	@Override
	public void run() {
		this.writeThreadStart();
		IShpFiledao shpFiledao = ShpFileFactory.createShpFiledao(this.eDataType);
		File dbffile = null;
		File shpfile = null;
		File shxfile = null;
		while (this.fieldTallCount >this.finishCount) {
			
			synchronized (this) {
				if (this.fieldTallCount >this.finishCount) {
					 if(fileList.get(this.finishCount).getName().endsWith(".dbf")){
						 dbffile = fileList.get(this.finishCount);
						 shpfile = new File(dbffile.getPath().replace(".dbf", ".shp")); 
						 shxfile = new File(dbffile.getPath().replace(".dbf", ".shx"));
					 }
//					System.out.println(file.getParent()+file.getName());
				}			
				this.finishCount++;
			}
			
			try {
				if (this.fieldTallCount >=this.finishCount) {
					    shpFiledao.readShpFile(dbffile, shpfile, shxfile);
					if (this.fieldTallCount <=this.finishCount){
						System.out.println(new Date(System.currentTimeMillis()).toLocaleString());
					}
				}
				else{
					System.out.println(new Date(System.currentTimeMillis()).toLocaleString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				f.setTextCon(e.toString());
				continue;
			}
			f.setLabel1(this.finishCount + "");
		}
		donsignl1.countDown();
		this.writeThreadEnd();
	}
	/**
	 * 判断线程开始
	 */
	private synchronized void writeThreadStart(){
		File file = new File(Util.logName);
		try {
			file.createNewFile();
			FileWriter out = new FileWriter(file,true);
			out.write(new Date(System.currentTimeMillis()).toLocaleString() + 
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
			out.write(new Date(System.currentTimeMillis()).toLocaleString() + 
					Thread.currentThread().getName() + ":结束\r" );
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

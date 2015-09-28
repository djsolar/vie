package com.sunmap.shpdata.tools.execute;

import java.util.concurrent.CountDownLatch;

import com.sunmap.shpdata.tools.util.JdbcUtil;
import com.sunmap.shpdata.tools.util.Util;

import form.Form;

public class RenewIndex implements Runnable {
	
	public String strTableName;
	private CountDownLatch donsignl;
	public RenewIndex(String strTableName, CountDownLatch donsignl) {
		super();
		this.strTableName = strTableName;
		this.donsignl = donsignl;
	}
	@Override
	public void run() {
		try {
			long l = System.currentTimeMillis();
			JdbcUtil.createIndex(strTableName.toLowerCase() + "_index", "mymapid", "", strTableName);
			System.out.println(System.currentTimeMillis() - l);
			donsignl.countDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			String exce = strTableName + "表中不存在mymapid,无法建立索引";
			System.out.println(exce);
			Form.setTextCon(exce);
			donsignl.countDown();
		}
	}

}

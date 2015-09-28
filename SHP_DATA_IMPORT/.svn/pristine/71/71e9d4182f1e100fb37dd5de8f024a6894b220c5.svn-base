package com.sunmap.shpdata.tools.execute;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.util.JdbcUtil;
import com.sunmap.shpdata.tools.util.Util;

public class RemoveIndex implements Runnable {

private final CountDownLatch doneSignal;
	
public final static String ORACLEDRIVERNAME = "oracle.jdbc.driver.OracleDriver";
	
public final static String MYSQLDRIVERNAME = "com.mysql.jdbc.Driver";
	
public final static String POSTGRESQLNAME = "org.postgresql.Driver";
	
public String strTableName;

	public RemoveIndex(String strTableName , CountDownLatch doneSignal) {
		super();
		this.strTableName = strTableName;
		this.doneSignal = doneSignal;
	}
	@Override
	public void run() {
		try {
			ShpConf conf = new ShpConf();
			conf.initProperties();
			long l = System.currentTimeMillis();
			if(this.ORACLEDRIVERNAME.equals(conf.getDriver())){
				Connection con = JdbcUtil.getConnection();
				Statement stm = con.createStatement();
				ResultSet res = 
					stm.executeQuery("select * from user_indexes where index_name ='"
							+ strTableName.toUpperCase() + "_INDEX'");
				if(res.next()){
					JdbcUtil.removeIndex(strTableName.toUpperCase() + "_INDEX","");
				}
			}
			if(this.MYSQLDRIVERNAME.equals(conf.getDriver())){
				Connection con = JdbcUtil.getConnection();
				Statement stm = con.createStatement();
				String sql3 = "show index from " + strTableName.toUpperCase() 
				+ " where key_name = '" + strTableName.toLowerCase() + "_index" + "'";
				ResultSet res = 
					stm.executeQuery(sql3);
				if(res.next()){
					JdbcUtil.mysqlremoveIndex(strTableName.toLowerCase() + "_index" , strTableName);
				}
			}
			if(this.POSTGRESQLNAME.equals(conf.getDriver())){
				JdbcUtil.removeIndex(strTableName.toLowerCase() + "_index","if exists");
			}
			System.out.println(System.currentTimeMillis() - l);
			doneSignal.countDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

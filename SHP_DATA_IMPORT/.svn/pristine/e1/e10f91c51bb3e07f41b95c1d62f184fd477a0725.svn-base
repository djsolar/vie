package com.sunmap.shpdata.tools.execute;

import java.io.BufferedReader;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.dao.TableDAO;
import com.sunmap.shpdata.tools.util.Util;
import com.sunmap.shpdata.tools.vo.ShpFieldVO;

public class ReadFileThread implements Runnable {

	private Map<String, Object> EleRec = new HashMap<String, Object>();
	private BufferedReader br ;
	private TableDAO tabledao ;
	private PreparedStatement ps;
	private Connection con;
	private ShpConf conf;
	private List<ShpFieldVO> astFieldEle;
//	private CountDownLatch donsignl1;
	
	public ReadFileThread(BufferedReader reader, TableDAO tabledao, PreparedStatement ps, Connection con,
			ShpConf conf,List<ShpFieldVO> astFieldEle) {
		this.br = reader;
		this.tabledao = tabledao;
		this.ps = ps;
		this.con = con;
		this.conf = conf;
		this.astFieldEle = astFieldEle;
//		this.donsignl1 = donsignl1;
	}

	@Override
	public void run() {
		int index = 0;
		boolean flag = false;
		String recodeLine = null;
		String[] arrField;
		try {
			long begin = System.currentTimeMillis();
			long end = System.currentTimeMillis();
			while ((recodeLine = br.readLine()) != null) {
				flag = false;
				arrField = recodeLine.split(conf.getDivideSympol());
				if (arrField.length > astFieldEle.size()) {
					System.out.println("×Ö¶ÎÁÐÊý´íÎó--");
					break;
				}
				
				for (int i = 0; i < arrField.length; i++) {
					Object object = arrField[i].trim();
					EleRec.put(astFieldEle.get(i+1).getStrName(), object);
				}
				
//					ps = tabledao.insertTable(ps, astFieldEle, EleRec);
						if (++index % 1000 == 0) {
							flag = true;
							if(!Util.MYSQLDRIVERNAME.equals(conf.getDriver())){
//									ps.executeBatch();
							}
//							con.commit();
//							ps.clearBatch();
							end = System.currentTimeMillis();
							System.out.println((end-begin)+"ms-Thread:"+Thread.currentThread().getName());
							begin = System.currentTimeMillis();
						}
				
				EleRec.clear();
			}
//			if (!flag) {
//					ps.executeBatch();
//					con.commit();
//					ps.clearBatch();
//				
//			}
				con.close();
				br.close();
//				donsignl1.countDown();
		} catch (BatchUpdateException e) {
			e.getNextException();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

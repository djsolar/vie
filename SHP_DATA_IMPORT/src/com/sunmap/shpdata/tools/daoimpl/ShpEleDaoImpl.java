package com.sunmap.shpdata.tools.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.Map.Entry;

import com.sunmap.shpdata.tools.vo.ShpFieldVO;
import com.sunmap.struts2.Com.TypeDef.EleType;

public class ShpEleDaoImpl {

//	public static boolean saveEle(Map<String, Object> EleRec, EleType eletype, PreparedStatement ps) throws Exception{
//		boolean flag = false;
//		Iterator<Entry<String, Object>> iterator = EleRec.entrySet().iterator();
//		List<Object> list = new Vector<Object>();
//		String strEle = "";
//		String strMark = "";
//		while(iterator.hasNext()){
//			Entry<String, Object> entry = iterator.next();
//			 strEle = strEle + entry.getKey() + ",";
//			 list.add(entry.getValue());
//			 strMark = strMark + "?,";
//		}
//		strEle = strEle.substring(0, strEle.length() - 1);
//		strMark = strMark.substring(0, strMark.length() - 1);
//		String sql = "insert into " + eletype.getStrFileName().toUpperCase() + "(GID,"
//		+ strEle + ") " + "values(?," + strMark + ")";
//			ps.setObject(1, UUID.randomUUID().toString());
//			for (int i = 0; i < list.size(); i++) {
//				ps.setObject(i+2, list.get(i));
//			}
//			int i = ps.executeUpdate(sql);
//			if(i != 0){
//				flag = true;
//			}
//		return flag;
//	}
	
//	public static boolean saveEle1(Map<String, Object> EleRec, EleType eletype, Statement ss) throws Exception{
//		int flag = 0;
//		PreparedStatement ps = null;
//		Iterator<Entry<String, Object>> iterator = EleRec.entrySet().iterator();
//		String strEle = "";
//		String strMark = "";
//		while(iterator.hasNext()){
//			Entry<String, Object> entry = iterator.next();
//			 strEle = strEle + entry.getKey() + ",";
//			 if(entry.getValue() == null){
//				 strMark = strMark +"null,";
//			 }else if(entry.getValue() instanceof String){
//				 strMark = strMark +"'" + entry.getValue() + "',";
//			 }else if(entry.getValue() instanceof Long){
//				 strMark = strMark + entry.getValue() + ",";
//			 }else{
//				 strMark = strMark + entry.getValue() + ",";
//			 }
//		}
//		strEle = strEle.substring(0, strEle.length() - 1);
//		strMark = strMark.substring(0, strMark.length() - 1);
//		
//		String sql = "insert into " + eletype.getStrFileName().toUpperCase() + "(GID,"
//		+ strEle + ") " + "values('" + UUID.randomUUID().toString() + "'," + strMark + ")";
//		flag = ss.executeUpdate(sql);
//		return flag!=0;
//	}
	/**
	 * 
	 */
	public static PreparedStatement createSql(List<ShpFieldVO> astFieldEle, String strTableName, Connection con) throws Exception{
		PreparedStatement ps = null;
		String strEle = "";
		String strMark = "";
		for (int i = 0; i < astFieldEle.size(); i++) {
			strEle = strEle + astFieldEle.get(i).getStrName() + ",";
			strMark = strMark + "?,";
		}
		strEle = strEle.substring(0, strEle.length() - 1);
		strMark = strMark.substring(0, strMark.length() - 1);
		String sql = "insert into " + strTableName.toUpperCase() + "(GID,"
		+ strEle + ",MYMAPID) " + "values(?,?," + strMark + ")";
		ps = con.prepareStatement(sql);
		return ps;
	}
	/**
	 * 
	 * @param astFieldEle
	 * @param EleRec
	 * @param ps
	 * @return
	 * @throws Exception
	 */
	public static PreparedStatement insertSql(List<ShpFieldVO> astFieldEle, 
			Map<String , Object> EleRec, PreparedStatement ps) throws Exception{
		List<Object> list = new Vector<Object>();
		for (int j = 0; j < astFieldEle.size(); j++) {
			Object object = EleRec.get(astFieldEle.get(j).getStrName());
			ps.setObject(j+2, object);
			list.add(object);
		}
			ps.setString(1, UUID.randomUUID().toString());
			for (int t = 0; t < list.size(); t++) {
				ps.setObject(t+2, list.get(t));
			}
			ps.setObject(list.size()+2, EleRec.get("MYMAPID").toString());
			ps.addBatch();
			ps.executeBatch();
			return ps;
	}
}

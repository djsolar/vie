package main;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mansion.tele.common.GeoRect;

import bo.ViewNameCodePloygon;
import bo.ViewUnitNo;
import business.dao.FromFileGetRectDao;
import business.dao.MakeBlockInfoFile;
import business.dao.ReadBlockInfoMakeTeleDB;
import business.dao.CollectBlockInfo;

import util.JdbcUtil;
import util.MethodUtil;
/**
 * 
 * @author Administrator
 *
 */
public class MakeTeleDB {
	/**
	 * 
	 */
	private MakeTeleDB() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 制作方法
	 * @param args String[]
	 */
	public static void main(String[] args) {

		try {
			
			FromFileGetRectDao fileGetRectDao = new FromFileGetRectDao();
			
			Map<String, List<ViewNameCodePloygon>> viewNCPMap = fileGetRectDao.getAllGeoRect();
			
			 CollectBlockInfo useBlockInfo = new CollectBlockInfo();
			 
			 Map<Integer, List<ViewUnitNo>> viewMap = useBlockInfo.getViewUnitNoInfo(viewNCPMap);
			
			 MakeBlockInfoFile makeFile = new MakeBlockInfoFile();
			 
			 makeFile.makeViewInfoFile(viewMap);
			 
			 System.out.println("文件已生成");
			 
			 ReadBlockInfoMakeTeleDB readBlockInfoFile = new ReadBlockInfoMakeTeleDB();
			 
			 readBlockInfoFile.readViewBlockInfo();
			 
			 System.out.println("生成完成");
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	private void initTable() throws Exception {
//		// TODO Auto-generated method stub
//
//		try {
//			
//			Connection connection = JdbcUtil.getLocalConnection();
//			
//			connection.setAutoCommit(false);
//			
//			String sql = "drop table if exists blockinfo CASCADE ";
//			
//			Statement statement = connection.createStatement();
//			
//			statement.execute(sql);
//			
//			connection.commit();
//			
//			sql = "create table blockinfo(level int, unitx int, unity int, blockx int, blocky int, geom geometry ,admincode int, flag smallint )";
//			
//			statement.execute(sql);
//			
//			connection.commit();
//			
//			statement.close();
//			
//			connection.close();
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			System.out.println("链接本地数据库错误");
//			
//			e.printStackTrace();
//			
//			throw e;
//		}
//		
//	}
	
	

}

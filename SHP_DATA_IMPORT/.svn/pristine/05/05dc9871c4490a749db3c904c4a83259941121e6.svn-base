package com.sunmap.shpdata.tools.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.sunmap.shpdata.tools.util.DataConnectionTool;
import com.sunmap.shpdata.tools.vo.ShpFieldVO;
import com.sunmap.shpdata.tools.vo.Table;

public interface TableDAO {
	public boolean createTable(List<File> filelist) throws Exception;
	public PreparedStatement createSql(String tableName, List<ShpFieldVO> strField, Connection con) throws Exception;
	public PreparedStatement insertTable(PreparedStatement ps, List<ShpFieldVO> strField, Map<String, Object> map) throws Exception;
	public Connection removeIndex(String strTableName) throws Exception;
	public Connection renewIndex(String strTableName, Connection con) throws Exception;
	public boolean createTable1(List<Table> creatMap) throws Exception;
	public void insertDataBatch(DataConnectionTool dataConnectionTool,Map<String, Object> EleRec, String tableName, String datatype) throws Exception;
}

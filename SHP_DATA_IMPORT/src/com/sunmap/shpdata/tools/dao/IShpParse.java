package com.sunmap.shpdata.tools.dao;

import java.io.File;
import java.util.List;

public interface IShpParse {
	
	public Object shpparse(File file) throws Exception;
	
	public List<String> shpparse(File shpfile, File shxfile ,int iRecordNum) throws Exception;
}

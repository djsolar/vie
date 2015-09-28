package com.sunmap.shpdata.tools.factory;

import com.sunmap.shpdata.tools.constvalue.DataType;
import com.sunmap.shpdata.tools.dao.ISHPDataExpand;
import com.sunmap.shpdata.tools.daoimpl.rt.RtShpDataExpandImpl;
import com.sunmap.shpdata.tools.daoimpl.sw.SwSHPDataExpand;

public class SHPDataExpandFactory {

	/**
	 *构造类 
	 */
	SHPDataExpandFactory() {
		
	}
	
	/**
	 * 导入文件类型
	 * @param datatype : 数据类别
	 * @return shp文件的dao
	 */
	public static ISHPDataExpand createShpDataExpand(DataType datatype){
		ISHPDataExpand iDataExpand = null;
		if( datatype == null  ){
			return null ;
		}
		
		if(datatype==DataType.RT){
			
			iDataExpand = new RtShpDataExpandImpl();
			
		}
		if(datatype==DataType.SW){
			
			iDataExpand= new SwSHPDataExpand();			
		}
		return iDataExpand;
	}

}

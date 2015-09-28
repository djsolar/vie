package com.sunmap.shpdata.tools.factory;

import com.sunmap.shpdata.tools.constvalue.DataType;
import com.sunmap.shpdata.tools.dao.IShpFiledao;
import com.sunmap.shpdata.tools.daoimpl.rt.RtShpFiledaoImpl;
import com.sunmap.shpdata.tools.daoimpl.shpfiledaoimpl.ShpFiledaoImpl;

/**
 * 
 * @author Administrator
 *
 */
public class ShpFileFactory {
	/**
	 *构造类 
	 */
	ShpFileFactory() {
		
	}
	
	/**
	 * 导入文件类型
	 * @param datatype : 数据类别
	 * @return shp文件的dao
	 */
	public static IShpFiledao createShpFiledao(DataType datatype){
		IShpFiledao iShpFiledao = null;
		if( datatype == null  ){
			return null ;
		}
		
		if(datatype==DataType.RT){
			
			iShpFiledao = new RtShpFiledaoImpl();
			
		}
		if(datatype == DataType.SHP){
			
			iShpFiledao = new ShpFiledaoImpl();
		}
//		if(datatype==DataType.SW){
//			
//			iShpFiledao= new SwShpFiledaoImpl();			
//		}
		return iShpFiledao;
	}
}

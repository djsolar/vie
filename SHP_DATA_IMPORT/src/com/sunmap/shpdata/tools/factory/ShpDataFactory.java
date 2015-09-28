package com.sunmap.shpdata.tools.factory;

import com.sunmap.shpdata.tools.constvalue.DataType;
import com.sunmap.shpdata.tools.dao.IShpDatadao;
import com.sunmap.struts2.Com.TypeDef.EleType;

public class ShpDataFactory {
	
	public static IShpDatadao createShpDatadao(DataType datatype ,EleType eletype){
		
//		if( datatype == null || eletype == null ){
//			return null ;
//		}
//		
//		if( datatype == DataType.RT && eletype == EleType.NaviArc ){
//			
//			return new RtNaviArcdaoImpl();
//		}
//		
		return null ;
		
	}
}

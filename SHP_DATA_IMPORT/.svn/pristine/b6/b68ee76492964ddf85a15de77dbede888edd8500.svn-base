package com.sunmap.shpdata.tools.daoimpl.fieldreadimpl;

import com.sunmap.shpdata.tools.dao.ISHPFielddao;
import com.sunmap.shpdata.tools.util.Util;

/**
 * 
 * @author shanbq
 *
 */
public class ReadFieldByDouble implements ISHPFielddao{

	/**
	 * double ÀàÐÍ×ª»»
	 */
	public Object readField(byte[] data,int bTypeFlag) throws Exception {
		if(bTypeFlag == 1){
			String string = new String(data);
			string  = string.trim();
			return Double.valueOf(string);
		}else {
			return Util.convertToDouble(data, data.length, true);
		}
	}
}

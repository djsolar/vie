package com.sunmap.shpdata.tools.daoimpl.fieldreadimpl;

import com.sunmap.shpdata.tools.dao.ISHPFielddao;
import com.sunmap.shpdata.tools.util.Util;

/**
 * 
 * @author shanbq
 *
 */
public class ReadFieldByInt implements ISHPFielddao{
	/**
	 * 数字类型转换
	 * @throws Exception 
	 */
	public Object readField(byte[] data,int bTypeFlag) throws Exception {
		if (bTypeFlag == 1) {
			String string = new String(data);
			string  = string.trim();
			return Long.valueOf(string);
		}else {
			return Util.convertToInt(data, data.length, true);
		}

	}

}

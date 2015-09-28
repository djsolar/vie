package com.sunmap.shpdata.tools.daoimpl.fieldreadimpl;

import com.sunmap.shpdata.tools.dao.ISHPFielddao;
import com.sunmap.shpdata.tools.util.Util;

public class ReadFieldByBoolean implements ISHPFielddao{

	@Override
	public Object readField(byte[] data, int typeFlag) throws Exception {
		if (typeFlag == 1) {
			String string = new String(data);
			string  = string.trim();
			return Integer.valueOf(string);
		}else {
			return Util.convertToInt(data, data.length, true);
		}
	}

}

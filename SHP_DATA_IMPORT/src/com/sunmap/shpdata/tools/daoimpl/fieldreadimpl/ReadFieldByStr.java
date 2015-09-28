package com.sunmap.shpdata.tools.daoimpl.fieldreadimpl;

import com.sunmap.shpdata.tools.dao.ISHPFielddao;

/**
 * 
 * @author shanbq
 *
 */
public class ReadFieldByStr implements ISHPFielddao{

	@Override
	public String readField(byte[] data,int bTypeFlag) {
		String string = new String(data);
		string  = string.trim();
		string = string.replaceAll("\\\\", "\\\\\\\\");
		return string;
	}

}

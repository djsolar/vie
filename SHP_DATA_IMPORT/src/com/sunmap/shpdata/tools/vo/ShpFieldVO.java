package com.sunmap.shpdata.tools.vo;

import com.sunmap.shpdata.tools.dao.ISHPFielddao;
import com.sunmap.shpdata.tools.daoimpl.fieldreadimpl.ReadFieldByDouble;
import com.sunmap.shpdata.tools.util.Util;

public class ShpFieldVO {
	private String strName = null;
	private byte bType = 0;
	private int  bLength = 0;
	private byte bPrecision = 0;
	private ISHPFielddao iConvertData = null;
	
	public String getStrName() {
		return this.strName;
	}
	public void setStrName(String strName) {
		this.strName = strName;
	}
	public byte getBType() {
		return this.bType;
	}
	public void setBType(byte type) {
		this.bType = type;
		if (type == 'N'&& bPrecision != 0) {
			this.iConvertData =  new ReadFieldByDouble();
		}else {
			this.iConvertData = Util.fieldTypeToColumnType(type);
		}
		
	}
	public int getBLength() {
		return this.bLength;
	}
	public void setBLength(byte length) {
		if (length < 0) {
			this.bLength = length+256;
		}else {
			this.bLength = length;
		}
		
	}
	public byte getBPrecision() {
		return this.bPrecision;
	}
	public void setBPrecision(byte precision) {
		this.bPrecision = precision;
	}
	
	public ISHPFielddao getIConvertData() {
		return this.iConvertData;
	}
	public void setIConvertData(ISHPFielddao sHPFielddao) {
		this.iConvertData = sHPFielddao;
	}

}

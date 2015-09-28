package com.sunmap.shpdata.tools.dao;

/**
 * 数据类型转换接口
 * @author shanbq
 *
 */
public interface ISHPFielddao {
	public Object readField(byte[] bData, int i) throws Exception;
}

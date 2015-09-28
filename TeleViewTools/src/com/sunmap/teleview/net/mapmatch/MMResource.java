package com.sunmap.teleview.net.mapmatch;



/**
 * 
 * @author beif
 *	地图匹配模块
 */
public interface MMResource{
	/**@author beif
	 * @param [String:String,String:String]
	 * @return byte[]的二进制流
	 * 查询匹配的地图数据
	 */
    public byte[] retrieve(String blk) throws Exception;

}

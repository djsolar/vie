package com.sunmap.teleview.net.view;


/**
 * 查询地图显示的接口
 * @author beif
 *
 */

public interface ViewResource {
    	
    public byte[] retrieve(String blk) throws Exception;
}

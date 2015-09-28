package com.sunmap.teleview.net.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sunmap.teleview.parser.Parser;
import com.sunmap.teleview.parser.ParserFactory;
import com.sunmap.teleview.parser.ParserType;
import com.sunmap.teleview.util.ByteList;
import com.sunmap.teleview.util.ByteUtil;

/**
 * 
 * View模块ViewServerResource.java
 * 
 * @author yangxiuliang
 */
public class ViewServerResource implements ViewResource {

	/**
	 * 
	 * @param [searchId_x:int,searchId_y:int,searchId_level:int]
	 * @return byte[]的二进制流 查询匹配的地图数据
	 * @throws Exception 
	 */
	public byte[] retrieve(String blk) throws Exception {
		String[] blks = blk.split(",");
		// if (blks.length>20) {return new byte[0];}//限制最大的查询blk数量为20
		Map<byte[], byte[]> mapping_blocks = new HashMap<byte[], byte[]>();
		Parser parser = ParserFactory.getInstance().getParser(ParserType.VIEW);
		
		for (String b : blks) {
			String[] k = b.split("\\.");
			String search_level = k[1];
			String search_x = k[2];
			String search_y = k[3];
			byte[] body = parser.parser(search_level, search_x, search_y);
			if (body != null) {
				byte[] header = _makeViewHeader(Byte.parseByte(search_level),
						Integer.parseInt(search_x), Integer.parseInt(search_y),
						(short) (body.length / 2));// 服务器减半处理，传输数据变小，终端需要乘以2
				mapping_blocks.put(header, body);
			}
		}
		byte[] bytes = makeTeleView(mapping_blocks);
		return bytes;
	}
	
	private byte[] makeTeleView(Map<byte[],byte[]> mapping_blocks){
		ByteList byteList = new ByteList();
		byteList.setEndian(ByteUtil.BIG_ENDIAN);
		byteList.addByte((byte) mapping_blocks.size());
		
		ByteList bodys = new ByteList();
		for(Entry<byte[], byte[]> entry:mapping_blocks.entrySet()){
			byteList.addAll(entry.getKey());
			bodys.addAll(entry.getValue());
		}
		byteList.addAll(bodys.getAllBytes());
		return byteList.getAllBytes();
	}

	
	private byte[] _makeViewHeader(byte level, int block_x,
			int block_y, short body_size) {
		byte[] buf = new byte[13];
		buf[0]=48;//代表view数据
		// 层号用1个byte
		buf[1] = level;
		// blk_x用4个byte
		byte[] blk_x = ByteUtil.toBytes(block_x,
				ByteUtil.BIG_ENDIAN);
		buf[2] = blk_x[0];
		buf[3] = blk_x[1];
		buf[4] = blk_x[2];
		buf[5] = blk_x[3];
		// blk_y用4个byte
		byte[] blk_y = ByteUtil.toBytes(block_y,
				ByteUtil.BIG_ENDIAN);
		buf[6] = blk_y[0];
		buf[7] = blk_y[1];
		buf[8] = blk_y[2];
		buf[9] = blk_y[3];
		// body大小用2个byte

		byte[] bodySizeBytes = ByteUtil.toBytes(body_size,
				ByteUtil.BIG_ENDIAN);
		buf[10] = bodySizeBytes[0];
		buf[11] = bodySizeBytes[1];
		// 保留位
		buf[12] = 0;
		return buf;
	}
}
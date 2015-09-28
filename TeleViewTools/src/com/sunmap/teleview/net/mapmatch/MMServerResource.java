package com.sunmap.teleview.net.mapmatch;

import com.sunmap.teleview.parser.Parser;
import com.sunmap.teleview.parser.ParserFactory;
import com.sunmap.teleview.parser.ParserType;
import com.sunmap.teleview.util.ByteList;
import com.sunmap.teleview.util.ByteUtil;


/**
 * 地图匹配模块 重写从文件获取
 * 
 * @author yangxiuliang
 */
public class MMServerResource implements MMResource {

	/**
	 * @author yangxiuliang
	 * @param [searchId_x:int,searchId_y:int,searchId_level:int]
	 * @return byte[]的二进制流 查询匹配的地图数据
	 * @throws Exception 
	 */
	public byte[] retrieve(String blk) throws Exception {
		String[] yblk = blk.split(",");
		Parser parser = ParserFactory.getInstance().getParser(
				ParserType.MAPMATCH);
		ByteList list = new ByteList(ByteUtil.BIG_ENDIAN);

		for (String string : yblk) {
			String[] params = string.split("\\.");

			String level = "0";// MM数据暂时只有0层
			String block_x = params[2].trim();
			String block_y = params[3].trim();
			if (parser != null) {
				byte[] bytes = parser.parser(level, block_x, block_y);
				if (bytes != null && bytes.length > 0) {
					list.addByteArray(bytes);
				} else {
					ByteList nodatamm = new ByteList(ByteUtil.BIG_ENDIAN);
					nodatamm.addByte((byte) 1);
					nodatamm.addInt(Integer.parseInt(block_x));
					nodatamm.addInt(Integer.parseInt(block_y));
					nodatamm.addShort((short) 0);
					list.addByteArray(nodatamm.getAllBytes());
				}
			}
		}
		list.addByte(0, (byte) list.size());
		return list.getAllBytes();
	}
}
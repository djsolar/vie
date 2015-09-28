package com.sunmap.teleview.parser.tele;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.sunmap.teleview.element.mm.data.MMBaseInfo;
import com.sunmap.teleview.parser.App;
import com.sunmap.teleview.util.CloserHelper;


public class MapMatchTeleParser extends BaseTeleParser {

	private File filePath = null;
	private int[] blockUnit;
	private Header header;
	private List<Level> levels;

	@Override
	public int[] getBlockUnit() {
		return blockUnit;
	}

	@Override
	public File getFilePath() {
		filePath = new File(MMBaseInfo.mmFilePath + "/mm");
		return filePath;
	}

	public MapMatchTeleParser() {
		filePath = new File(MMBaseInfo.mmFilePath + "/mm");
		String unit_widths = App.PROPERTIES.getProperty("UNIT_WIDTH");
		String block_widths = App.PROPERTIES.getProperty("BLOCK_WIDTH");

		String[] unit_width = unit_widths.split(",");
		String[] block_width = block_widths.split(",");

		blockUnit = new int[1];

		blockUnit[0] = Integer.parseInt(unit_width[0].trim())
				/ Integer.parseInt(block_width[0].trim());
		blockUnit[0] = blockUnit[0] / 2;

		header = new Header();
		levels = new ArrayList<Level>();

		FileInputStream fis = null;
		DataInputStream dis = null;
		try {
			fis = new FileInputStream(filePath);
			dis = new DataInputStream(fis);

			int headerSize = dis.readUnsignedByte();
			long unitRecord = dis.readInt() & 0xFFFFFFFFL;
			long blockTabel = dis.readInt() & 0xFFFFFFFFL;
			long blockSize = dis.readInt() & 0xFFFFFFFFL;
			int levelNum = dis.readUnsignedByte();

			header.setBlockSize(blockSize);
			header.setBlockTabel(blockTabel);
			header.setHeaderSize(headerSize);
			header.setLevelNum(levelNum);
			header.setUnitRecord(unitRecord);

			for (int i = 0; i < levelNum; i++) {

				// level
				int index = dis.readUnsignedShort();
				long firstUnitOffset = dis.readInt() & 0xFFFFFFFFL;
				long xMinUnitCode = dis.readInt() & 0xFFFFFFFFL;
				long yMinUnitCode = dis.readInt() & 0xFFFFFFFFL;
				long xUnitNum = dis.readInt() & 0xFFFFFFFFL;
				long yUnitNum = dis.readInt() & 0xFFFFFFFFL;

				Level level = new Level(index, firstUnitOffset, xMinUnitCode,
						yMinUnitCode, xUnitNum, yUnitNum);

				levels.add(level);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			CloserHelper.close(dis, fis);
		}
	}

	@Override
	public Header getHearder() {
		return header;
	}

	@Override
	public List<Level> getLevels() {
		return levels;
	}

	@Override
	public boolean checkBlockArea(int level, int x, int y) {
		return true;
	}

}

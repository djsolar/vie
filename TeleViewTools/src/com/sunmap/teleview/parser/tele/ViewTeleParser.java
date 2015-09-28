package com.sunmap.teleview.parser.tele;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.sunmap.teleview.element.view.data.ViewBaseInfo;
import com.sunmap.teleview.parser.App;
import com.sunmap.teleview.util.CloserHelper;


public class ViewTeleParser extends BaseTeleParser {


	private File filePath = null;
	private int[] blockUnit;
	private Header header;
	private List<Level> levels;

	private int[] mapRect;
	private int[] mapBasePoint;

	private int[] unitWidth;
	private int[] blockWidth;
	private int[] blockHeight;

	@Override
	public int[] getBlockUnit() {
		return blockUnit;
	}

	@Override
	public File getFilePath() {
		filePath = new File(ViewBaseInfo.viewFilePath + "/view");
		return filePath;
	}

	public ViewTeleParser() {
		filePath = new File(ViewBaseInfo.viewFilePath + "/view");
		String MAP_RECT = App.PROPERTIES.getProperty("MAP_RECT");
		String[] mapRectStr = MAP_RECT.split(",");
		mapRect = new int[] { Integer.parseInt(mapRectStr[0]),
				Integer.parseInt(mapRectStr[1]),
				Integer.parseInt(mapRectStr[2]),
				Integer.parseInt(mapRectStr[3]) };

		String MAP_BASE_POINT = App.PROPERTIES.getProperty("MAP_BASE_POINT");
		String[] mapBasePointStr = MAP_BASE_POINT.split(",");
		mapBasePoint = new int[] { Integer.parseInt(mapBasePointStr[0]),
				Integer.parseInt(mapBasePointStr[1]) };

		String unit_widths = App.PROPERTIES.getProperty("UNIT_WIDTH");
		String block_widths = App.PROPERTIES.getProperty("BLOCK_WIDTH");

		String block_heights = App.PROPERTIES.getProperty("BLOCK_HEIGHT");

		String[] unit_width = unit_widths.split(",");
		String[] block_width = block_widths.split(",");
		String[] block_height = block_heights.split(",");

		blockUnit = new int[unit_width.length];

		unitWidth = new int[unit_width.length];
		blockWidth = new int[unit_width.length];
		blockHeight = new int[unit_width.length];

		for (int i = 0; i < unit_width.length; i++) {

			unitWidth[i] = Integer.parseInt(unit_width[i].trim());
			blockWidth[i] = Integer.parseInt(block_width[i].trim());
			blockHeight[i] = Integer.parseInt(block_height[i].trim());

			blockUnit[i] = Integer.parseInt(unit_width[i].trim())
					/ Integer.parseInt(block_width[i].trim());
		}

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
			long blockSize = dis.readLong() & 0xFFFFFFFFL;
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

		int blockMinX = (mapRect[0] - mapBasePoint[0]) / blockWidth[level];
		int blockMinY = (mapRect[1] - mapBasePoint[1]) / blockHeight[level];

		int blockMaxX = (mapRect[2] - mapBasePoint[0]) / blockWidth[level];
		int blockMaxY = (mapRect[3] - mapBasePoint[1]) / blockHeight[level];

		if (x < blockMinX || x > blockMaxX) {
			return false;
		}

		if (y < blockMinY || y > blockMaxY) {
			return false;
		}

		return true;
	}

	@Override
	byte[] getBytes(int levelIndex, int x, int y) throws IOException {
		List<Level> levels = this.getLevels();
		if (levels.size() == 0) {
			return null;
		}
		int[] block_unit = this.getBlockUnit();
		Header header = this.getHearder();

		byte[] bytes = null;
		Level level = levels.get(levelIndex);

		// int index=level.getIndex();
		long firstUnitOffset = level.getFirstUnitOffset();
		long xMinUnitCode = level.getxMinUnitCode();
		long yMinUnitCode = level.getyMinUnitCode();
		long xUnitNum = level.getxUnitNum();
		 long yUnitNum=level.getyUnitNum();

		long unitXIndex = x / block_unit[levelIndex];
		long unitYIndex = y / block_unit[levelIndex];
		
		if((unitXIndex >= xMinUnitCode + xUnitNum || unitXIndex < xMinUnitCode)
			|| (unitYIndex >= yMinUnitCode + yUnitNum || unitYIndex < yMinUnitCode)){
			return  new byte[0];
		}

		long blockXIndex = x % block_unit[levelIndex];
		long blockYIndex = y % block_unit[levelIndex];

		long unitIndex = (unitXIndex - xMinUnitCode)
				+ (unitYIndex - yMinUnitCode) * xUnitNum;

		long blockIndex = (blockXIndex) + blockYIndex * block_unit[levelIndex];

		long unitOffset = header.getHeaderSize() + firstUnitOffset + 12
				* unitIndex;

		File view = this.getFilePath();
		RandomAccessFile ras = null;
		try {
			ras = new RandomAccessFile(view, "r");
			ras.seek(unitOffset);

			// long tempX = ras.readInt() & 0xFFFFFFFFL;
			// long tempY = ras.readInt() & 0xFFFFFFFFL;

			ras.seek(8 + unitOffset);

			long blockMangerTableOffset = ras.readInt() & 0xFFFFFFFFL;
			if (blockMangerTableOffset >= BaseTeleParser.INVALIDE_NUMBER) {
				bytes = new byte[0];
			} else {
				long firstBlockManagerOffset = header.getHeaderSize()
						+ header.getUnitRecord() + blockMangerTableOffset;

				long blockManagerOffset = firstBlockManagerOffset + 10
						* blockIndex;
				
				if(blockManagerOffset>= BaseTeleParser.INVALIDE_NUMBER)
				{
					bytes = new byte[0];
				}
				else
				{
					ras.seek(blockManagerOffset);

					long blockOffset = ras.readLong();

					if (blockOffset == -1) {
						bytes = new byte[0];
					} else {
						int _blockSize = ras.readUnsignedShort();
						int blockSize = 2 * _blockSize;
						
						long dataOffset = header.getHeaderSize()
								+ header.getUnitRecord() + header.getBlockTabel()
								+ blockOffset;

						ras.seek(dataOffset);
						bytes = new byte[blockSize];
						ras.read(bytes);
					}
				}
				
			}

		} finally {
			CloserHelper.close(ras);
		}
		return bytes;
	}
	
	
	
	
}

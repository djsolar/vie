package com.sunmap.teleview.parser.tele;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import com.sunmap.teleview.parser.Parser;
import com.sunmap.teleview.util.CloserHelper;


public abstract class BaseTeleParser implements Parser {

	protected final static long INVALIDE_NUMBER = 0xffffffffL;

	abstract public int[] getBlockUnit();

	abstract public File getFilePath();
	
	abstract public Header getHearder();

	abstract public List<Level> getLevels();
	
	abstract public boolean checkBlockArea(int level,int x,int y);

	public static boolean isNumeric(String str)
	{
		boolean flag = true;

		if (str.length() == 0) {
			flag = false;
		} else {
			for (int i = str.length(); --i >= 0;) {
				if (!Character.isDigit(str.charAt(i))) {
					flag = false;
				}
			}
		}
		return flag;
	}

	@Override
	public byte[] parser(String... parser) throws Exception {
		
		if (parser.length != 3 || parser[0] == null || parser[0].length() == 0
				|| !parser[0].matches("(-[1-9]|[0-9])+") || parser[1] == null
				|| parser[1].length() == 0 || !parser[1].matches("(-[1-9]|[0-9])+")
				|| parser[2] == null || parser[2].length() == 0
				|| !parser[2].matches("(-[1-9]|[0-9])+")) {
			throw new Exception("参数不合法");
		}

		String level = parser[0];
		String x = parser[1];
		String y = parser[2];
		int levelInt = Integer.parseInt(level);
		int xInt = Integer.parseInt(x);
		int yInt = Integer.parseInt(y);
		
		if( !this.checkBlockArea(levelInt,xInt, yInt) )
		{
			return new byte[0];
		}
		return this.getBytes(levelInt, xInt, yInt);
	}

	byte[] getBytes(int levelIndex, int x, int y) throws IOException {
		List<Level> levels = this.getLevels();
		int[] block_unit = this.getBlockUnit();
		Header header = this.getHearder();

		byte[] bytes = null;
		Level level = levels.get(levelIndex);

		// int index=level.getIndex();
		long firstUnitOffset = level.getFirstUnitOffset();
		long xMinUnitCode = level.getxMinUnitCode();
		long yMinUnitCode = level.getyMinUnitCode();
		long xUnitNum = level.getxUnitNum();
		// long yUnitNum=level.getyUnitNum();

		long unitXIndex = x / block_unit[levelIndex];
		long unitYIndex = y / block_unit[levelIndex];

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

			if (blockMangerTableOffset >= INVALIDE_NUMBER) {
				bytes = new byte[0];
			} else {
				long firstBlockManagerOffset = header.getHeaderSize()
						+ header.getUnitRecord() + blockMangerTableOffset;

				long blockManagerOffset = firstBlockManagerOffset + 6
						* blockIndex;
				
				if(blockManagerOffset>= INVALIDE_NUMBER)
				{
					bytes = new byte[0];
				}
				else
				{
					ras.seek(blockManagerOffset);

					long blockOffset = ras.readInt() & 0xFFFFFFFFL;

					if (blockOffset >= INVALIDE_NUMBER) {
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

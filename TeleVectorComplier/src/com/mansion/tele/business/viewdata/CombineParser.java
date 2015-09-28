package com.mansion.tele.business.viewdata;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import com.mansion.tele.business.Task;
import com.mansion.tele.business.TaskManager;
import com.mansion.tele.util.EstimateTime;

public class CombineParser {

	// 缓冲区内存
	private static final int MEMORY = 104857600;

	/**
	 * "Header"
	 */
	public static String headerFilePath = "Header";

	/**
	 * "BlockManageRecord"
	 */
	public static String blockManageRecordFilePath = "BlockManageRecord";

	/**
	 * "UnitManageRecord"
	 */
	public static String unitManageRecordFilePath = "UnitManageRecord";

	/**
	 * view最终结果文件
	 */
	public static String viewResultFilePath = "view";

	/**
	 * mm最终结果文件
	 */
	public static String mmResultFilePath = "mm";

	/**
	 * "BlockData"
	 */
	public static String blockDataFilePath = "BlockData";

	/**
	 * Header大小
	 */
	public static int headSize;

	/**
	 * unit管理记录排列的大小
	 */
	public static int unitMRSize;

	/**
	 * Block管理记录排列的大小
	 */
	public static int blockMRSize;

	/**
	 * Block数据块排列的大小
	 */
	public static long blockDataSize;

	/**
	 * 首条Unit管理记录的偏移
	 */
	public static int[] firstUnitMROffsets;


	/**
	 * 头文件
	 */
	public static File headFile;

	/**
	 * unit管理记录文件
	 */
	public static File unitMRFile;

	/**
	 * block管理记录文件
	 */
	public static File blockMRFile;

	/**
	 * block数据块文件
	 */
	public static File blocDaFile;


	public static void parser(String inputFile, String outputFile, TaskManager management) {
		System.out.println("合并view文件开始!");
		firstUnitMROffsets = new int[management.byLevelRecordCount];
		// 生成三个合并结果文件
		composeFiles(inputFile + File.separator, management);
		// 将以上三个文件合并到一个文件中
		combinOneFile(inputFile + File.separator, outputFile + File.separator + "view");
		System.out.println("合并view文件结束!");
	}

	private static void init(String path) throws IOException {
		headFile = new File(path + headerFilePath);
		if (headFile.exists()) {
			headFile.delete();
		}
		headFile.createNewFile();

		unitMRFile = new File(path + unitManageRecordFilePath);
		if (unitMRFile.exists()) {
			unitMRFile.delete();
		}
		unitMRFile.createNewFile();
		blockMRFile = new File(path + blockManageRecordFilePath);
		if (blockMRFile.exists()) {
			blockMRFile.delete();
		}
		blockMRFile.createNewFile();

		blocDaFile = new File(path + blockDataFilePath);
		if (blocDaFile.exists()) {
			blocDaFile.delete();
		}
		blocDaFile.createNewFile();

	}

	/**
	 * 将以上三个结果文件合并到一个文件中
	 */
	private static void combinOneFile(String path, String result) {
		File file = new File(result);

		DataOutputStream dos = null;
		DataInputStream disH = null;
		DataInputStream disU = null;
		DataInputStream disM = null;
		DataInputStream disB = null;
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			dos = new DataOutputStream(new FileOutputStream(file));

			// 读头文件 begin 利用缓冲提高效率
			disH = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(path + headerFilePath)), MEMORY));
			byte[] buf = new byte[1024];

			int len = 0;
			while ((len = disH.read(buf)) != -1) {
				byte[] by = Arrays.copyOfRange(buf, 0, len);
				dos.write(by);
			}
			disU = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(path + unitManageRecordFilePath)), MEMORY));
			while ((len = disU.read(buf)) != -1) {
				byte[] by = Arrays.copyOfRange(buf, 0, len);
				dos.write(by);
			}

			disM = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(path + blockManageRecordFilePath)), MEMORY));
			while ((len = disM.read(buf)) != -1) {
				byte[] by = Arrays.copyOfRange(buf, 0, len);
				dos.write(by);
			}

			disB = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(path + blockDataFilePath)), MEMORY));
			while ((len = disB.read(buf)) != -1) {
				byte[] by = Arrays.copyOfRange(buf, 0, len);
				dos.write(by);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				disH.close();
				disU.close();
				disM.close();
				disB.close();
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			disH = null;
			disU = null;
			disM = null;
			disB = null;
			dos = null;
		}

	}

	/**
	 * 生成四个合并结果文件
	 */
	private static void composeFiles(String inputFile,TaskManager management) {
		List<Task> taskList = management.getTasks();
		DataOutputStream blocDaOs = null;
		DataOutputStream blocMROs = null;
		DataOutputStream unitMROs = null;
		DataOutputStream headOs = null;
		PrintWriter writer = null;
		try {
			init(inputFile);
			headOs = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(headFile), MEMORY));
			unitMROs = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(unitMRFile), MEMORY));
			blocMROs = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(blockMRFile), MEMORY));
			blocDaOs = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(blocDaFile), MEMORY));
			writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(new File("statistic.txt")), MEMORY));
			if (!taskList.isEmpty()) {
				int firstBlockMROffset = 0;
				unitMRSize = 0;
				blockMRSize = 0;
				blockDataSize = 0;
				byte keyLevel = -1;
				for (Task task : taskList) {
//					System.out.println(inputFile + File.separator + task.getPath());
					byte level = task.getLevel();
					if (level != keyLevel) {// 新一层的数据开始
						keyLevel = level;
						firstUnitMROffsets[level] = unitMRSize;
					}
					int unitx = task.getUnitNo().iX;
					int unity = task.getUnitNo().iY;
					File file = new File(inputFile + File.separator + task.getPath() + ".tuv");

					unitMRSize += 12;
					if (!file.exists()) {
						unitMROs.writeInt(unitx);
						unitMROs.writeInt(unity);
						unitMROs.writeInt(-1);
					} else {
						//TODO 文件未关闭，要调查。niujw20131228	
						DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file), MEMORY));
						DataInputStream dis2 = new DataInputStream(new BufferedInputStream(new FileInputStream(file), MEMORY));
						dis2.skip(10);
						unitMROs.writeInt(unitx);
						unitMROs.writeInt(unity);
						unitMROs.writeInt(firstBlockMROffset);
						int blockCount = dis2.readShort() * dis2.readShort();
						firstBlockMROffset += 10 * blockCount;

						int unitsize = dis2.readInt() * 2;
						dis2.skip(6 * blockCount);
						byte[] blockdata = new byte[unitsize - 6 * blockCount - 18];
						try {
							dis2.read(blockdata);
							dis.skip(18);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}

						int m = 0;
						int offset;
						int size;
						byte[] oneblock;
						int blockx;
						int blocky;
						int blockSize;
						int textSize;
						byte layerCN;
						int layerMRC;
						byte addstate;
						byte[] blockBody;
						while (m++ < blockCount) {
							// Block文件的偏移
							offset = dis.readInt() - 18 - 6 * blockCount;
							size = dis.readShort() * 2;

							blockMRSize += 10;
							if (size <= 0) {
								blocMROs.writeLong(-1);
								blocMROs.writeShort(0);

							} else {
								oneblock = Arrays.copyOfRange(blockdata, offset, size + offset);
								blockx = getInt(Arrays.copyOfRange(oneblock, 1, 5));
								blocky = getInt(Arrays.copyOfRange(oneblock, 5, 9));
								blockSize = getShort(Arrays.copyOfRange(oneblock, 9, 11)) * 2;
								if (blockSize < 0) {
									System.out.println("数据block小于0");
								}

								blockBody = Arrays.copyOfRange(oneblock, 12, blockSize + 12);
								blocMROs.writeLong(blockDataSize);
								blocMROs.writeShort((short) (blockSize / 2));
								blocDaOs.write(blockBody);
								// 获得文字信息size
								layerCN = blockBody[0];
								layerMRC = layerCN * 5;
								addstate = (byte) (layerCN % 2 == 0 ? 1 : 0);
								// 文字Size
								textSize = getShort(Arrays.copyOfRange(blockBody, layerMRC + 1 + addstate, layerMRC + 3 + addstate)) * 2;
								writer.write(blockx + "\t" + blocky + "\t" + textSize);
								writer.println();

								blockDataSize += blockSize;

							}

						}
						writer.flush();
//						System.out.println(unitx + "," + unity + "完成");
					}
//					System.out.println("完成" + level + "层");
				}
				int byLevelRecordCount = management.byLevelRecordCount;
				headSize = 18 + byLevelRecordCount * 22;
				headOs.writeByte(headSize);
				headOs.writeInt(unitMRSize);
				headOs.writeInt(blockMRSize);
				headOs.writeLong(blockDataSize);
				headOs.writeByte(byLevelRecordCount);
				for (int i = 0; i < byLevelRecordCount; i++) {
					headOs.writeShort(i);
					headOs.writeInt(firstUnitMROffsets[i]);
					headOs.writeInt(management.eachLevelMinUnitNoX[i]);
					headOs.writeInt(management.eachLevelMinUnitNoY[i]);
					headOs.writeInt(management.eachLevelUnitNumX[i]);
					headOs.writeInt(management.eachLevelUnitNumY[i]);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				headOs.close();
				unitMROs.close();
				blocMROs.close();
				blocDaOs.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * byte数组转换为short
	 * 
	 * @param copyOfRange
	 * @return
	 */
	private static int getShort(byte[] copyOfRange) {

		return (int) (((copyOfRange[0] & 0xff) << 8) | (copyOfRange[1] & 0xff));
	}

	/**
	 * byte数组转换为int
	 * 
	 * @param copyOfRange
	 * @return
	 */
	private static int getInt(byte[] copyOfRange) {
		return (int) (((copyOfRange[0] & 0xff) << 24) | ((copyOfRange[1] & 0xff) << 16) | ((copyOfRange[2] & 0xff) << 8) | (copyOfRange[3] & 0xff));
	}

	public static void main(String[] args) {
		// Combine.combine("D:\\work\\TeleVectorComplier\\tele\\data\\view\\mid\\",
		// "D:\\work\\TeleVectorComplier\\tele\\data\\view\\out");
		// String str = String.format("%02x",180);
		// String str = String.format("%02x",1135978935/2560/2400);
		// String str = String.format("%02x",90*3/2);
		// System.out.println(str);
		// System.out.println(Integer.valueOf(str, 16));
		// String s = "sdfsd\\s.a";
		// s = s.format(format, args)
		// System.out.println(s);
	}
}

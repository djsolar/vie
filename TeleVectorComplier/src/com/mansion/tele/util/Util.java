package com.mansion.tele.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import com.mansion.tele.business.Task;
import com.mansion.tele.business.landmark.SignUtil;
import com.mansion.tele.business.network.Route;
import com.mansion.tele.common.ConstantValue;

public class Util {
	
	public static File routefile = new File("路线.txt");
	
	public static int count = 0;
	
	// 将对象序列化写入文件中
	public static void saveObjectToFile(String path, Object obj)
			throws FileNotFoundException, IOException {
		// System.out.println("写入文件 "+path);
		ObjectOutputStream oos = null;
		try {
			File file = new File(path);
			file.getParentFile().mkdirs();
			file.createNewFile();
			oos = new ObjectOutputStream(new BufferedOutputStream(
					new GZIPOutputStream(new FileOutputStream(file, true))));
			oos.writeObject(obj);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
	}
	/**
	 * 保存路线
	 * @param strRoute
	 * @throws IOException
	 */
	public static synchronized void saveRoute(List<String> strRoute) throws IOException{
		if(strRoute == null || strRoute.size() == 0){
			return;
		}
		FileOutputStream fileOutputStream = new FileOutputStream(routefile, true);
		PrintWriter printWriter = new PrintWriter(fileOutputStream);
		printWriter.print("routeNo" + count + ":");
		for (int i = 0; i < strRoute.size(); i++) {
			String sign = ",";
			if(i == strRoute.size() - 1){
				sign = "";
			}
			printWriter.print(strRoute.get(i) + sign);
		}
		printWriter.println();
		count++;
		printWriter.close();
		fileOutputStream.close();
	}

	public static void delPath(File file) {
		File[] files = file.listFiles();
		if (files != null)
			for (File f : files)
				delPath(f);
		file.delete();
	}

	public static Object readObjectFromFile(String filePath)
			throws ZipException, IOException, ClassNotFoundException {
		Object result = null;
		// System.out.println("读取文件: "+filePath);
		// String strFilePath = midDataPath+File.separator+task.getFilePath();
		File file = new File(filePath);
		ObjectInputStream ois = null;
		try {

			ois = new ObjectInputStream(new BufferedInputStream(
					new GZIPInputStream(new FileInputStream(file))));
			result = ois.readObject();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 将字符串中的全角字符转换成半角字符
	 * 
	 * @param strSource
	 *            原字符串
	 * @return 转换后的半角字符串
	 */
	public static String stringToBSBC(String strSource) {
		char[] c = strSource.toCharArray();
		for (int i = 0; i < c.length; i++) {
			// 全角空格
			if (c[i] == ConstantValue.SBC_SPACE) {
				c[i] = (char) ConstantValue.DBC_SPACE;
				continue;
			}
			if (c[i] >= ConstantValue.SBC_CHAR_START
					&& c[i] <= ConstantValue.SBC_CHAR_END) {

				c[i] = (char) (c[i] - ConstantValue.CONVERT_STEP);
			}
		}
		return new String(c);
	}

	/**
	 * 将字符串中的半角字符转换成全角字符
	 * 
	 * @param strSrc
	 *            原字符串
	 * @return 转换后的全角字符串
	 */
	public static String stringToSBC(String strSrc) {

		char[] acSrc = strSrc.toCharArray();
		String strDes = "";
		char cTemp;
		for (int i = 0; i < acSrc.length; i++) {
			// 空格
			if (ConstantValue.DBC_SPACE == acSrc[i]) {

				cTemp = (char) ConstantValue.SBC_SPACE;
				strDes += cTemp;
			} else {

				// 半角字符
				if (ConstantValue.DBC_CHAR_START <= acSrc[i]
						&& ConstantValue.DBC_CHAR_END >= acSrc[i]) {

					cTemp = (char) (acSrc[i] + ConstantValue.CONVERT_STEP);
					strDes += cTemp;
				} else {

					strDes += acSrc[i];
				}
			}
		}

		return strDes;
	}

	/**
	 * 取得名称字符串
	 * 
	 * @param NameList
	 *            名称集合
	 * @return 返回取得的名称字符串
	 */
	public static String getNoBrackets(String name) {
		String NewName = "";
		String[] names = name.split("\\(");
		if (names.length == 1) {
			names = name.split(SignUtil.SBCLEFTBRACKER_STRING);
		}
		if (names[0] == null) {
			NewName = name;
		} else {
			NewName = names[0];
		}
		return NewName;
	}

	public static String FiveCityDelete(String strMapsignNameString,
			int orgCode, Task task) {
			int index = strMapsignNameString.lastIndexOf("市");
			if(index > 0){
				strMapsignNameString = strMapsignNameString.substring(0,
						index);
			}
		return strMapsignNameString;
	}

	/**
	 * 对道路名称进行转换处理
	 * 
	 * @param strRoadNameString
	 *            道路名称
	 * @return 转换后的道路名称
	 */
	public static String convertRoadName(String strRoadNameString) {

		String strNewRoadName = new String();
		// 去除道路名称中的简称
		int iIndex = strRoadNameString.indexOf(SignUtil.SBCLEFTBRACKER_STRING);
		if (0 <= iIndex) {

			strNewRoadName = strRoadNameString.substring(0, iIndex);
		} else {

			strNewRoadName = strRoadNameString;
		}

		iIndex = strNewRoadName.indexOf("/");
		if (0 <= iIndex) {

			strNewRoadName = strNewRoadName.substring(0, iIndex);
		}

		// 判断是否有数字
		if (!strNewRoadName.matches(".*\\d*")) {

			return strNewRoadName;
		}

		// 将数字转换成中文
		String capitalRoadName = new String();
		capitalRoadName = Util.replaceNumToChinese(strNewRoadName);

		return capitalRoadName;
	}

	/**
	 * 将字符串中的数字替换成中文数字
	 * 
	 * @param strSourceString
	 *            源字符串
	 * @return 返回转换成中文数字后的字符串
	 */
	private static String replaceNumToChinese(String strSourceString) {

		String strDestString = new String();

		char[] acSrc = strSourceString.toCharArray();
		for (int i = 0; i < acSrc.length; i++) {

			switch (acSrc[i]) {
			case SignUtil.SBC_CASE_CHAR_NUMBER_1:
				strDestString += SignUtil.CHINESE_1_STRING;
				break;
			case SignUtil.SBC_CASE_CHAR_NUMBER_2:
				strDestString += SignUtil.CHINESE_2_STRING;
				break;
			case SignUtil.SBC_CASE_CHAR_NUMBER_3:
				strDestString += SignUtil.CHINESE_3_STRING;
				break;
			case SignUtil.SBC_CASE_CHAR_NUMBER_4:
				strDestString += SignUtil.CHINESE_4_STRING;
				break;
			case SignUtil.SBC_CASE_CHAR_NUMBER_5:
				strDestString += SignUtil.CHINESE_5_STRING;
				break;
			case SignUtil.SBC_CASE_CHAR_NUMBER_6:
				strDestString += SignUtil.CHINESE_6_STRING;
				break;
			case SignUtil.SBC_CASE_CHAR_NUMBER_7:
				strDestString += SignUtil.CHINESE_7_STRING;
				break;
			case SignUtil.SBC_CASE_CHAR_NUMBER_8:
				strDestString += SignUtil.CHINESE_8_STRING;
				break;
			case SignUtil.SBC_CASE_CHAR_NUMBER_9:
				strDestString += SignUtil.CHINESE_9_STRING;
				break;
			case SignUtil.SBC_CASE_CHAR_NUMBER_0:
				strDestString += SignUtil.CHINESE_0_STRING;
				break;
			default:
				strDestString += acSrc[i];
				break;
			}
		}

		return strDestString;
	}

	/**
	 * 简化地铁出入口名称
	 * 
	 * @param strSubwayEntranceName
	 *            地铁出入口名称
	 * @return 输出简化后的地铁出入口名称
	 */
	public static String getSubwayEntranceName(String strSubwayEntranceName) {

		String strEntranceIndex = new String();

		// 先截取地铁站名称中括号内的出入口名称
		int iBIndex = strSubwayEntranceName.indexOf("(") + 1;
		int iEIndex = strSubwayEntranceName.indexOf(")");
		if (0 > iBIndex || 0 > iEIndex) {

			return "";
		}
		String strEntranceName = strSubwayEntranceName.substring(iBIndex,
				iEIndex);
		if ("出入口".equals(strEntranceName) || "出口".equals(strEntranceName)
				|| "入口".equals(strEntranceName)) {

			strEntranceIndex = strEntranceName.substring(0, 1);
		} else {

			char[] acEntranceName = strEntranceName.toCharArray();
			for (int i = 0; i < acEntranceName.length; i++) {

				if (Character.isDigit(acEntranceName[i])
						|| Character.isUpperCase(acEntranceName[i])) {

					strEntranceIndex += acEntranceName[i];
				}
			}
		}

		return strEntranceIndex;
	}

	/**
	 * 简化标记名称
	 * 
	 * @param strMarkName
	 *            mark名称
	 * @param bIsNameS
	 *            是否是简称
	 * @param iServiceType
	 *            mark的种别
	 * @return 返回简化后的名称
	 */
	public static String predigestMarkName(String strMarkName) {

		String strpredigestMarkName = strMarkName;
		int iIndex = strMarkName.lastIndexOf(SignUtil.SBCLEFTBRACKER_STRING);
		if (0 <= iIndex) {
			int iEndIndex = strMarkName
					.lastIndexOf(SignUtil.SBCRIGHTBRACKER_STRING);
			if ((iEndIndex + 1) == strMarkName.length()) {

				strpredigestMarkName = strMarkName.substring(0, iIndex);
			}
		}
		return strpredigestMarkName;
	}

	/**
	 * 设置某一个bit的值。
	 * @param iData			设置的基值
	 * @param bitOffset		设置的bit位置
	 * @param value			存入的bit值
	 * @return				返回结果值
	 */
	public static int setBit(int iData, int bitOffset, boolean value){
		int va = 0;
		if (value) {
			va = 1;
		}else {
			va = 0;
		}
		va = va << bitOffset;
		return iData | va ;
	}

	/**
	 * 将Value中的值保存在data的一段位区间中。
	 * @param data				保存数据的对象
	 * @param value				保存到对象中的值
	 * @param bitOffsetStart	data的开始bit
	 * @param bitOffsetEnd		data的结束bit。此数字大于等于开始bit。
	 * @return 保存处理后的data值
	 */
	public static int setBits(int data, int value, int bitOffsetStart, int bitOffsetEnd){
		if (bitOffsetStart > bitOffsetEnd) {
			Exception e = new Exception();
			e.printStackTrace();
			return 0;
		}
	
		return data | ((value << bitOffsetStart)&(0xFFFFFFFF>>(32-bitOffsetEnd)));
	}

}

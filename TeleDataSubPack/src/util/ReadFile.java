package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
/**
 * 
 * @author wenxc
 *
 */
public class ReadFile {

	/**
	 * �ļ���
	 */
	private static String pathline = "Area.properties";

	/**
	 * ��������
	 */
	private static Properties conf;
	/**
	 * ���1
	 */
	private static String sign1 = "1";
	/**
	 * ���2
	 */
	private static String sign2 = "2";

	/**
	 * 
	 */
	private ReadFile(){
		
	}
	
	/**
	 * 
	 * @return String
	 * @throws Exception 1
	 */
	public static String readFile() throws Exception {

		File file = new File("Area.txt");

		FileReader fileInputStream = new FileReader(file);

		BufferedReader bufferedInputStream = new BufferedReader(fileInputStream);

		String cityname = null;

		String allcity = "";
		do {

			cityname = bufferedInputStream.readLine();

			if (cityname != null) {

				allcity = allcity + cityname;
			}

		} while (cityname != null);

		System.out.println(allcity);

		bufferedInputStream.close();

		fileInputStream.close();

		return allcity;
	}
	

	/**
	 * 
	 * @return String
	 */
	public static String readProFileProvince() {

		ReadFile.loadFile();

		String provi = ReadFile.conf.getProperty("province");

		if (provi == null || provi.trim().equals("")) {

			System.out.println("�����ļ�province��дΪ��");

			System.exit(0);

		}

		return provi;

	}

	/**
	 * 
	 * @return String
	 */
	public static String readProFileStyle() {

		ReadFile.loadFile();

		String strStyle = ReadFile.conf.getProperty("style");

		if (strStyle == null
				|| (!strStyle.trim().equals(ReadFile.sign1) && !strStyle.trim()
						.equals(ReadFile.sign2))) {

			strStyle = ReadFile.sign1;

		}

		return strStyle;

	}
	
	/**
	 * 
	 * @return String
	 */
	public static String readProFileURL() {

		ReadFile.loadFile();

		String strLocalUrl = ReadFile.conf.getProperty("localurl");

		if (strLocalUrl == null
				|| strLocalUrl.trim().equals("")) {


			System.out.println("localurlû����д");
			
			System.exit(0);
			
		}

		return strLocalUrl;

	}
	
	
	/**
	 * 
	 * @return String
	 */
	public static String readProFileUSR() {

		ReadFile.loadFile();

		String strLocalUrl = ReadFile.conf.getProperty("user");

		if (strLocalUrl == null
				|| strLocalUrl.trim().equals("")) {


			System.out.println("userû����д");
			
			System.exit(0);
			
		}

		return strLocalUrl;

	}
	
	/**
	 * 
	 * @return String
	 */
	public static String readProFilePASSWD() {

		ReadFile.loadFile();

		String strLocalUrl = ReadFile.conf.getProperty("passwd");

		if (strLocalUrl == null
				|| strLocalUrl.trim().equals("")) {


			System.out.println("passwdû����д");
			
			System.exit(0);
			
		}

		return strLocalUrl;

	}
	
	

	/**
	 * 
	 * @return String
	 */
	public static String readProFileZero() {

		ReadFile.loadFile();

		String strZreo = ReadFile.conf.getProperty("zero");

		if (strZreo == null
				|| (!strZreo.trim().equals(ReadFile.sign1) && !strZreo.trim().equals(ReadFile.sign2))) {

			strZreo = ReadFile.sign1;

		}

		return strZreo;

	}

	/**
	 * 
	 * @return String
	 */
	public static String readProFileSaveViewPath() {

		ReadFile.loadFile();

		String strSaveDataPath = ReadFile.conf.getProperty("saveDataPath");

		if (strSaveDataPath == null || strSaveDataPath.trim().equals("")) {

			System.out.println("�����ļ�saveDataPath��дΪ��");

			System.exit(0);

		}

		File file = new File(strSaveDataPath);

		if (!file.exists()) {

			System.out.println("�������ݵ�·��������");

			System.exit(0);

		}

		return strSaveDataPath;

	}
	
	/**
	 * 
	 * @return String
	 */
	public static String readProFileViewPath() {

		ReadFile.loadFile();

		String strSaveDataPath = ReadFile.conf.getProperty("readViewDataPath");

		if (strSaveDataPath == null || strSaveDataPath.trim().equals("")) {

			System.out.println("�����ļ�readViewDataPath��дΪ��");

			System.exit(0);

		}

		File file = new File(strSaveDataPath);

		if (!file.exists()) {

			System.out.println("ԭʼview���ݵ�·��������");

			System.exit(0);

		}

		return strSaveDataPath;

	}

	/**
//	 * 
//	 * @return
//	 */
//	public static String readProFileAllViewPath() {
//
//		loadFile();
//
//		String strAllViewPath = conf.getProperty("AllViewPath");
//
//		if (strAllViewPath == null || strAllViewPath.trim().equals("")) {
//
//			System.out.println("�����ļ�AllViewPath��дΪ��");
//
//			System.exit(0);
//
//		}
//
//		File file = new File(strAllViewPath);
//
//		if (!file.exists()) {
//
//			System.out.println("ԭʼ���ݵ�·��������");
//
//			System.exit(0);
//
//		}
//
//		return strAllViewPath;
//
//	}

	/**
	 * 
	 */
	public static void loadFile() {

		if (ReadFile.conf == null) {

			ReadFile.conf = new Properties();
				// File file = new File(strConfFilePath);
				File file = new File(ReadFile.pathline);
				try {
					ReadFile.conf.load(new FileInputStream(file));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

//	public static void main(String[] args) {
//		try {
//			ReadFile.readFile();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}

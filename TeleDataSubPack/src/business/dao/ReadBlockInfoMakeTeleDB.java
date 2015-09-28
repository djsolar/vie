package business.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import util.ReadFile;

import bo.ViewUnitNo;

import com.mansion.tele.util.JDBCTeleToEdb;
import com.mansion.tele.util.TeleToEdb;
import com.mansion.tele.util.TeleViewData;
/**
 * 锟斤拷block锟斤拷息
 * @author wenxc
 *
 */
public class ReadBlockInfoMakeTeleDB {

	/**
	 * 
	 */
	private TeleViewData teleViewData = new TeleViewData(ReadFile.readProFileViewPath());
	
	/**
	 * blockX位锟斤拷
	 */
	private final int blockxindex = 3;
	/**
	 * blockY位锟斤拷
	 */
	private final int blockyindex = 4;
	

	/**
	 * admincode位锟斤拷
	 */
	private final int adminindex = 5;
	/**
	 * 锟斤拷锟轿伙拷锟�
	 */
	private final int nameindex = 6;
	
	/**
	 * 锟结交锟斤拷
	 */
	private final int commitNum = 1000;
	/**
	 * 锟街革拷锟斤拷
	 */
	private String sign = "\t";

	/**
	 * 锟矫碉拷锟斤拷锟�
	 * @return String
	 */
	public String getSign() {
		return this.sign;
	}

	/**
	 * 锟斤拷锟矫凤拷锟�
	 * @param sign String
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}

	/**
	 * 锟矫碉拷adminindex
	 * @return int
	 */
	public int getAdminindex() {
		return this.adminindex;
	}

	/**
	 * 锟斤拷锟絥ameindex
	 * @return int
	 */ 
	public int getNameindex() {
		return this.nameindex;
	}


	/**
	 * 锟矫碉拷TeleViewData
	 * @return TeleViewData
	 */
	public TeleViewData getTeleViewData() {
		return this.teleViewData;
	}

	/**
	 * 锟矫碉拷commitnum
	 * @return int
	 */
	public int getCommitNum() {
		return this.commitNum;
	}

	/**
	 * 锟斤拷锟斤拷teleViewData
	 * @param teleViewData TeleViewData
	 */
	public void setTeleViewData(TeleViewData teleViewData) {
		this.teleViewData = teleViewData;
	}

	/**
	 * 锟矫碉拷blockxindex
	 * @return int
	 */
	public int getBlockxindex() {
		return this.blockxindex;
	}

	/**
	 * 锟斤拷锟絙lockyindex
	 * @return int
	 */
	public int getBlockyindex() {
		return this.blockyindex;
	}

	/**
	 * 锟斤拷取View锟叫碉拷block锟斤拷锟斤拷锟斤拷
	 * @throws Exception 1
	 */
	public void readViewBlockInfo() throws Exception {

		FileReader fileReader = new FileReader("ViewBlockInfo.txt");

		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String firstline = bufferedReader.readLine();

		Map<String, JDBCTeleToEdb> nameToEdb = new HashMap<String, JDBCTeleToEdb>();

		this.initFileDir(nameToEdb);
		
		if (firstline != null && firstline.charAt(0) == '/') {

			int iCount = 0;
			long l = System.currentTimeMillis();

			String stroldname = "";

			while (true) {

				String infoline = bufferedReader.readLine();
				//锟斤拷取锟斤拷莸锟酵�
				if(infoline == null){
					this.commitInfo(nameToEdb);
					bufferedReader.close();
					fileReader.close();
					
					System.out.println("全锟斤拷锟结交");
					break;
					
				}
				//锟斤拷锟斤拷一锟斤拷锟斤拷息
				String[] resinfo = this.resloveReturnArr(infoline);
				//锟斤拷锟截达拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟絙lock锟斤拷锟�
				byte[] byTeleBlock = this.getBlockInfoByArr(resinfo);
				//锟叫断达拷Edb锟角凤拷锟斤拷前一EDB为锟斤拷同锟斤拷锟斤拷
				
				final int nameIndex = 6;
				
				if(!"".equals(stroldname) && !stroldname.equals(resinfo[nameIndex])){
					
					JDBCTeleToEdb teleToEdb = nameToEdb.get(stroldname);
					teleToEdb.jdbcCommit();
					System.out.println(stroldname + "锟街帮拷锟窖撅拷锟斤拷锟�");
					
				}
				
				JDBCTeleToEdb teleToEdb = nameToEdb.get(resinfo[nameIndex]);
				//锟斤拷锟斤拷锟斤拷锟�
				if(byTeleBlock != null && byTeleBlock.length > 0){
					
					teleToEdb.insertJDBCPAREBlockADDBATCH(Byte.parseByte(resinfo[0]),
							Integer.parseInt(resinfo[this.blockxindex]), 
							Integer.parseInt(resinfo[this.blockyindex]), byTeleBlock);
					iCount++;
				}
				
				//1000锟斤拷锟斤拷锟斤拷锟结交
				if(iCount % this.commitNum == 0){
					System.out.println("锟结交锟斤拷锟斤拷:" + iCount);
					System.out.println("1000锟斤拷时锟斤拷" + (System.currentTimeMillis() - l));
					l = System.currentTimeMillis();
					teleToEdb.executePreBatch(true);
					teleToEdb.jdbcCommit();
				}
				
			}

		}

	}

	/**
	 * 锟斤拷始锟斤拷锟侥硷拷锟斤拷锟斤拷锟斤拷EDB
	 * @param nameToEdb Map<String, TeleToEdb>
	 * @throws UnsupportedEncodingException 1
	 * @throws SQLite.Exception 1
	 * @throws Exception 
	 */
	public void initFileDir(Map<String, JDBCTeleToEdb> nameToEdb)
			throws UnsupportedEncodingException, SQLite.Exception, Exception {
		
		if ("1".equals(ReadFile.readProFileStyle())) {

			String[] arrName = null;

			String cityname = ReadFile.readProFileProvince();

			cityname = new String(cityname.getBytes("ISO8859-1"), "UTF-8");

			arrName = FromFileGetRectDao.getNameArr(cityname, arrName);

			for (String string : arrName) {

				File listfile = new File(ReadFile.readProFileSaveViewPath()
						+ "/" + string);

				if (!listfile.exists()) {

					listfile.mkdirs();
				}

				JDBCTeleToEdb jDBCTeleToEdb = null;
				try {
					jDBCTeleToEdb = new JDBCTeleToEdb(listfile.getPath()
							+ "/tele.jpg", true, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				nameToEdb.put(string, jDBCTeleToEdb);

			}
		}
	}


//	/**
//	 * 锟斤拷锟斤拷
//	 * @param infoline String
//	 * @return ViewUnitNo
//	 */
//	public ViewUnitNo reslove(String infoline) {
//
//		if (infoline != null) {
//
//			ViewUnitNo viewUnitNo = new ViewUnitNo();
//
//			String[] strInfoArr = infoline.split(this.sign);
//
//			viewUnitNo.setIlevel(Integer.parseInt(strInfoArr[0]));
//			viewUnitNo.setiX(Integer.parseInt(strInfoArr[1]));
//			viewUnitNo.setiY(Integer.parseInt(strInfoArr[2]));
//			viewUnitNo.setBlockNo(new BlockNo(Integer.parseInt(strInfoArr[this.blockxindex]),
//					Integer.parseInt(strInfoArr[this.blockyindex])));
//			viewUnitNo.setAdmincode(Integer.parseInt(strInfoArr[this.adminindex]));
//			viewUnitNo.setStrName(strInfoArr[this.nameindex]);
//			
//			return viewUnitNo;
//
//		}
//		
//		return null;
//	}
//	

	/**
	 * 锟斤拷锟斤拷
	 * @param infoline String
	 * @return String[]
	 */
	public String[] resloveReturnArr(String infoline) {

		if (infoline != null) {

			String[] strInfoArr = infoline.split(this.sign);

			return strInfoArr;
			
		}
		
		return null;
	}
	
	/**
	 * 锟矫碉拷block锟斤拷息
	 * @param viewUnitNo ViewUnitNo
	 * @return byte[]
	 * @throws Exception 1
	 */
	public byte[] getBlockInfo(ViewUnitNo viewUnitNo) throws Exception{
		
		TeleViewData teleViewData = new TeleViewData(ReadFile.readProFileViewPath());
		
		byte[] allbyte = teleViewData.getBlock(viewUnitNo.getIlevel(),
				viewUnitNo.getBlockNo().getiX(), viewUnitNo.getBlockNo().getiY());
		
		return allbyte;
		
	}
	
	/**
	 * 锟矫碉拷block锟斤拷息
	 * @param resinfo String[]
	 * @return byte[]
	 * @throws Exception 1
	 */
	public byte[] getBlockInfoByArr(String[] resinfo) throws Exception{
		
		byte[] allbyte = this.teleViewData.getBlockForEDB(Integer.parseInt(resinfo[0]), 
				Integer.parseInt(resinfo[this.blockxindex]), 
				Integer.parseInt(resinfo[this.blockyindex]));
		
		return allbyte;
		
	}
	
	
	/**
	 * 锟结交
	 * @param nameToEdb Map<String, TeleToEdb>
	 * @throws Exception 1
	 */
	public void commitInfo(Map<String, JDBCTeleToEdb> nameToEdb) throws Exception{
		
		for (String name : nameToEdb.keySet()) {
			
			nameToEdb.get(name).jdbcCommit();
			
			nameToEdb.get(name).jdbccloese();
			
		}
		
	}
}

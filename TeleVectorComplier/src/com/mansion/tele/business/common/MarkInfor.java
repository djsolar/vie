package com.mansion.tele.business.common;

/**
 * 
 * @author Administrator
 *
 */
public class MarkInfor extends BaseShape{

	/**
	 * 文字/POI
	 */
	private byte bType;	// 文字/POI
	/**
	 * 文字的各别Code
	 */
	private long lTextKindCode;	// 文字的各别Code
	/**
	 * Icon在显示比例尺
	 */
	private int iIconVS;
	/**
	 * 文字的显示比例尺
	 */
	private int iTextVS;
	/**
	 * 名称
	 */
	private String strString = new String();
	
	/**
	 * 显示坐标个数
	 */
	private byte bCoorCnt;
	/**
	 * 显示优先级
	 */
	private int iPri;
	
	/**
	 * 文字记号番号
	 */
	private int letterMark;
	/**
	 * 纵・横Flag
	 */
	private int flag;
	/**
	 * 文字列的记号配置情报记述值
	 */
	private int letterMarkAdvices;
	/**
	 * 
	 * @return byte
	 */
	public byte getbType() {
		return this.bType;
	}
	/**
	 * 
	 * @param bType byte
	 */
	public void setbType(byte bType) {
		this.bType = bType;
	}
	/**
	 * 
	 * @return long
	 */
	public long getlTextKindCode() {
		return this.lTextKindCode;
	}
	/**
	 * 
	 * @param lTextKindCode long
	 */
	public void setlTextKindCode(long lTextKindCode) {
		this.lTextKindCode = lTextKindCode;
	}
	/**
	 * 
	 * @return int
	 */
	public int getiIconVS() {
		return this.iIconVS;
	}
	/**
	 * 
	 * @param iIconVS int
	 */
	public void setiIconVS(int iIconVS) {
		this.iIconVS = iIconVS;
	}
	/**
	 * 
	 * @return int
	 */
	public int getiTextVS() {
		return this.iTextVS;
	}
	/**
	 * 
	 * @param iTextVS int
	 */
	public void setiTextVS(int iTextVS) {
		this.iTextVS = iTextVS;
	}
	/**
	 * 
	 * @return String
	 */
	public String getStrString() {
		return this.strString;
	}
	/**
	 * 
	 * @param strString String
	 */
	public void setStrString(String strString) {
		this.strString = strString;
	}
	/**
	 * 
	 * @return byte
	 */
	public byte getbCoorCnt() {
		return this.bCoorCnt;
	}
	/**
	 * 
	 * @param bCoorCnt byte
	 */
	public void setbCoorCnt(byte bCoorCnt) {
		this.bCoorCnt = bCoorCnt;
	}
	/**
	 * 
	 * @return int
	 */
	public int getiPri() {
		return this.iPri;
	}
	/**
	 * 
	 * @param iPri int
	 */
	public void setiPri(int iPri) {
		this.iPri = iPri;
	}
	/**
	 *  
	 * @return int
	 */
	public int getLetterMark() {
		return this.letterMark;
	}
	/**
	 * 
	 * @param letterMark int
	 */
	public void setLetterMark(int letterMark) {
		this.letterMark = letterMark;
	}
	/**
	 * 
	 * @return int
	 */
	public int getFlag() {
		return this.flag;
	}
	/**
	 * 
	 * @param flag int
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}
	/**
	 * 
	 * @return int
	 */
	public int getLetterMarkAdvices() {
		return this.letterMarkAdvices;
	}
	/**
	 * 
	 * @param letterMarkAdvices int
	 */
	public void setLetterMarkAdvices(int letterMarkAdvices) {
		this.letterMarkAdvices = letterMarkAdvices;
	}
	
	
	
}

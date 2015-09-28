package com.mansion.tele.db.bean.elemnet;

import java.util.List;

/**
 * MapSign entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("serial")
public class MapSign extends PointElement {

	// Fields
//	private int iMapSignAngle; // ��ǽǶ�
//	private byte byPosType; // λ������
	private int iMapSignType; // �������
//	private byte byDisplayLevel; // ��ʾ�㼶
//	private String strIconID; // ͼ��ID
	private List<BaseName> astMapSignNames; // ������
//	private byte byEleType; // Ҫ������
//	private String strEleID; // Ҫ��ID
//	private byte byAuto; //�Զ���ɻ��˹���ɵı�־

	
//	public byte getByAuto() {
//		return byAuto;
//	}
//	public void setByAuto(byte byAuto) {
//		this.byAuto = byAuto;
//	}
//	public int getImapsignangle() {
//		return iMapSignAngle;
//	}
//	public void setImapsignangle(int imapsignangle) {
//		this.iMapSignAngle = imapsignangle;
//	}
//	public byte getBypostype() {
//		return byPosType;
//	}
//	public void setBypostype(byte bypostype) {
//		this.byPosType = bypostype;
//	}
	public int getImapsigntype() {
		return iMapSignType;
	}
	public void setImapsigntype(int imapsigntype) {
		this.iMapSignType = imapsigntype;
	}
//	public byte getBydisplaylevel() {
//		return byDisplayLevel;
//	}
//	public void setBydisplaylevel(byte bydisplaylevel) {
//		this.byDisplayLevel = bydisplaylevel;
//	}
//	public String getStriconid() {
//		return strIconID;
//	}
//	public void setStriconid(String striconid) {
//		this.strIconID = striconid;
//	}
	public List<BaseName> getAstMapSignNames()
	{
		return astMapSignNames;
	}
	public void setAstMapSignNames(List<BaseName> astMapSignNames)
	{
		if (astMapSignNames == null && this.astMapSignNames != null) {
			this.astMapSignNames.clear();
		} else {
			this.astMapSignNames = astMapSignNames;
		}
	}
//	public byte getByEleType()
//	{
//		return byEleType;
//	}
//	public void setByEleType(byte byEleType)
//	{
//		this.byEleType = byEleType;
//	}
//	public String getStrEleID()
//	{
//		return strEleID;
//	}
//	public void setStrEleID(String strEleID)
//	{
//		this.strEleID = strEleID;
//	}

}
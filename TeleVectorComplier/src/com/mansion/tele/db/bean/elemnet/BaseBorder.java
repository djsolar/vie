package com.mansion.tele.db.bean.elemnet;

/**
 * AbstractAdmAreaBorder entity provides the base persistence definition of the
 * AdmAreaBorder entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("serial")
public class BaseBorder extends SubElement
{

	// Fields
	protected String strID;//���
	
	protected String strElementID; // ����Ҫ��ID
	
	protected String strGeomRecID;//����ӦBackgroundShp
	
	protected PolylineShp stGeom; // ��״

//	protected byte byBorderType; // �߽�����

	protected byte byIsDisplay; // ��ʾ��ʶ

//	protected byte byBorderDirection; // �߽緽��

	protected String strNextBorderID; // ����߽�ID

	// Constructors
	/** default constructor */
	public BaseBorder() {
	}
//	public void convert(BaseBorder border,String strElementID,String strGeoShpID,String strNextBorderID) {
////		this.strElementID = strElementID;
////		if (border.getStGeom() != null) {
////			this.stGeom = new PolylineShp();
////			this.stGeom.strID = strGeoShpID;
////			this.stGeom.convert(border.getStGeom());
////		}
////		this.strGeomRecID = strGeoShpID;
//////		this.byBorderType = border.byBorderType;
//////		this.byBorderDirection = border.byBorderDirection;
//////		this.byIsDisplay = border.byIsDisplay;
////		this.strNextBorderID = strNextBorderID;
//		
//	}
	public String getStrid()
	{
		return this.strID;
	}

	public void setStrid(String strid)
	{
		this.strID = strid;
	}
	
	String getStrelementid()
	{
		return strElementID;
	}
	
	void setStrelementid(String strElementID)
	{
		this.strElementID = strElementID;
	}

	public String getStrgeomrecid() {
		return strGeomRecID;
	}

	public void setStrgeomrecid(String strgeomrecid) {
		this.strGeomRecID = strgeomrecid;
	}

	public PolylineShp getStGeom()
	{
		return stGeom;
	}

	public void setStGeom(PolylineShp stGeom)
	{
		this.stGeom = stGeom;
	}

	public String getStrNextBorderID()
	{
		return strNextBorderID;
	}

	public void setStrNextBorderID(String strNextBorderID)
	{
		this.strNextBorderID = strNextBorderID;
	}

//	public byte getBybordertype()
//	{
//		return this.byBorderType;
//	}
//
//	public void setBybordertype(byte bybordertype)
//	{
//		this.byBorderType = bybordertype;
//	}
//
	public byte getByisdisplay()
	{
		return this.byIsDisplay;
	}

	public void setByisdisplay(byte byisdisplay)
	{
		this.byIsDisplay = byisdisplay;
	}
//
//	public byte getByborderdirection()
//	{
//		return this.byBorderDirection;
//	}
//
//	public void setByborderdirection(byte byborderdirection)
//	{
//		this.byBorderDirection = byborderdirection;
//	}
	public String getStrElementID() {
		return strElementID;
	}
	public void setStrElementID(String strElementID) {
		this.strElementID = strElementID;
	}
	

}
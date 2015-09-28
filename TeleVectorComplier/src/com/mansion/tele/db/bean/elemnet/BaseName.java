package com.mansion.tele.db.bean.elemnet;

/**
 * AbstractName entity provides the base persistence definition of the Name
 * entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("serial")
public class BaseName extends SubElement
{
	private Integer id;

	private Element mainEntity; // 名称属性

	private byte byNameType; //名称类型

	private byte byLanType; // ��语言类型

	private String strNameText; // 名称

	private String strPronText; // 前最名

	private String strVoiceID; // 

	// Constructors

	/** default constructor */
	public BaseName() {
	}

	/** minimal constructor */
	public BaseName(String strid, Element mainObj, byte bynametype, byte bylantype, String strnametext) {

		this.mainEntity = mainObj;
		this.byNameType = bynametype;
		this.byLanType = bylantype;
		this.strNameText = strnametext;
	}

	/** full constructor */
	public BaseName(String strid, Element mainObj, byte bynametype, byte bylantype, String strnametext,
			String strprontext, String strvoiceid) {

		this.mainEntity = mainObj;
		this.byNameType = bynametype;
		this.byLanType = bylantype;
		this.strNameText = strnametext;
		this.strPronText = strprontext;
		this.strVoiceID = strvoiceid;
	}

	

	public byte getBynametype()
	{
		return this.byNameType;
	}

	public void setBynametype(byte bynametype)
	{
		this.byNameType = bynametype;
	}

	public byte getBylantype()
	{
		return this.byLanType;
	}

	public void setBylantype(byte bylantype)
	{
		this.byLanType = bylantype;
	}

	public String getStrnametext()
	{
		return this.strNameText;
	}

	public void setStrnametext(String strnametext)
	{
		this.strNameText = strnametext;
	}

	public String getStrprontext()
	{
		return this.strPronText;
	}

	public void setStrprontext(String strprontext)
	{
		this.strPronText = strprontext;
	}

	public String getStrvoiceid()
	{
		return this.strVoiceID;
	}

	public void setStrvoiceid(String strvoiceid)
	{
		this.strVoiceID = strvoiceid;
	}

	@SuppressWarnings("unused")
	private Integer getId()
	{
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(Integer id)
	{
		this.id = id;
	}

	public Entity getMainEntity()
	{
		return mainEntity;
	}

	public void setMainEntity(Element mainObj)
	{
		this.mainEntity = mainObj;
	}

	public void convert(BaseName baseName) {
		this.byNameType = baseName.byNameType;
		this.byLanType = baseName.byLanType;
		this.strNameText = baseName.strNameText;
		this.strPronText = baseName.strPronText;
		this.strVoiceID = baseName.strVoiceID;
		
	}
	
	
}
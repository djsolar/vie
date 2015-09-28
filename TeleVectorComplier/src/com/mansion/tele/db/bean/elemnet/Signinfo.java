package com.mansion.tele.db.bean.elemnet;

import java.util.ArrayList;
import java.util.List;

/**
 * Signinfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */

@SuppressWarnings("serial")
public class Signinfo extends Element { 
	// Fields

//	private byte byFlag;
//	private byte byLevel;				/*显示Level*/
	private byte bBegin_level;			/*开始Level*/
	private byte bEnd_level;			/*结束Level*/		
	private byte byLanguage;			/* 0:简体中文，1:繁体中文，2:英文 */
	private int ikind;					/* 分类 */
	private int iIconVs;				/*Icon显示比例尺*/	
	private int iTextVs;				/*文字显示比例尺*/
	private int iPriority;				/* 优先级 */
	private int isBegin_Scaleno;			/*开始VS*/
	private int isEnd_Scaleno;			/*结束VS*/
	private int iTxtConfAdv;			/*文字配置情报*/
	private String strUnitNo;				/*unit编号*/
//	private int iblockx;				/*BlockX编号*/
//	private int iblocky;				/*BlockY编号*/
	private String strOrgId;			/* 母库中唯一ID */
	private String strParentServiceID;  /*父服务ID*/
	private String strName;				/* 名称 */
	private String strSelectName;		/* 用于Webjis拾取用的名称 */
	private String strTel;					// 电话
	private String strAddrInfo;				// 地址
	//private String strCoor;				/*Mark坐标*/
	private List<ShpPoint> coorList = new ArrayList<ShpPoint>();		/*Mark坐标*/
//	private String strFollowLineCoor;	/*沿道路的文字坐标*/
	private List<ShpPoint> followLineCoorList = new ArrayList<ShpPoint>();		/*沿道路的文字坐标*/
	private List<ShpPoint> roadNameLineCoorList = new ArrayList<ShpPoint>();  /* 道路名称显示的线 */
	private String strshpid;/*LANDMARK POIID*/
	

		
	// Constructors



	public String getStrshpid() {
		return strshpid;
	}


	public void setStrshpid(String strshpid) {
		this.strshpid = strshpid;
	}


	public List<ShpPoint> getRoadNameLineCoorList() {
		return roadNameLineCoorList;
	}


	public void setRoadNameLineCoorList(List<ShpPoint> roadNameLineCoorList) {
		this.roadNameLineCoorList = roadNameLineCoorList;
	}


	/** default constructor */

	
	/*
	 * SignInfor对象的值传递
	 * */
	public void convertSignInfor(Signinfo signinfo) {

		this.setStrid(signinfo.getStrid());					/*中间库ID*/
		this.setStrOrgId(signinfo.getStrOrgId());			/* 母库中唯一ID */
		this.setStrParentServiceID(signinfo.getStrParentServiceID()); /*父服务ID*/
		this.setStrName(signinfo.getStrName());				/* 名称 */
		this.setStrSelectName(signinfo.getStrSelectName()); /* 用于Webjis拾取用的名称 */
		this.setByLanguage(signinfo.getByLanguage());			/* 0:简体中文，1:繁体中文，2:英文 */
		this.setIkind(signinfo.getIkind());					/* 分类 */
		this.setiIconVs(signinfo.getiIconVs());				/*Icon显示比例尺*/	
		this.setiTextVs(signinfo.getiTextVs());				/*文字显示比例尺*/
		this.setiPriority(signinfo.getiPriority());			/* 优先级 */
		// 标记坐标转换
		if (null != signinfo.getCoorList() && 0 < signinfo.getCoorList().size()) {
			
			List<ShpPoint> destShpPoints = new ArrayList<ShpPoint>();
			List<ShpPoint> orgShpPoints = signinfo.getCoorList();
			for (int i = 0; i < orgShpPoints.size(); i++) {
				ShpPoint shpPoint = new ShpPoint();
				shpPoint.convert(orgShpPoints.get(i));
				destShpPoints.add(shpPoint);
			}
			
			this.setCoorList(destShpPoints);
		}
		
//		this.setStrCoor(signinfo.getStrCoor());				/*Mark坐标*/
		this.setbBegin_level(signinfo.getbBegin_level());	/*开始Level*/
		this.setIsBegin_Scaleno(signinfo.getIsBegin_Scaleno());/*开始VS*/
		this.setbEnd_level(signinfo.getbEnd_level());		/*结束Level*/
		this.setIsEnd_Scaleno(signinfo.getIsEnd_Scaleno());	/*结束VS*/
		this.setStrUnitNo(signinfo.getStrUnitNo());				/*unit编号*/
		this.setiBlockX((signinfo.getiBlockX()));				/*BlockY编号*/
		this.setiBlockX((signinfo.getiBlockY()));				/*BlockY编号*/
		this.setiTxtConfAdv(signinfo.getiTxtConfAdv());
		this.setStrTel(signinfo.getStrTel());				// 电话
		this.setStrAddrInfo(signinfo.getStrAddrInfo());		// 地址
		
		this.bygrade = signinfo.getBygrade();
		this.setStrshpid(signinfo.getStrshpid());
		List<ShpPoint> followLineCoorList1 = new ArrayList<ShpPoint>();
		List<ShpPoint> roadNameLineCoorList2 = new ArrayList<ShpPoint>();
		for(int i=0;i<signinfo.getFollowLineCoorList().size();i++){
			ShpPoint shp = new ShpPoint();
			shp.convert(signinfo.getFollowLineCoorList().get(i));
			followLineCoorList1.add(shp);
		}
		this.setFollowLineCoorList(followLineCoorList1);
		for(int i=0;i<signinfo.getRoadNameLineCoorList().size();i++){
			ShpPoint shp = new ShpPoint();
			shp.convert(signinfo.getRoadNameLineCoorList().get(i));
			roadNameLineCoorList2.add(shp);
		}
		this.setRoadNameLineCoorList(roadNameLineCoorList2);
	}

	
	public String getStrParentServiceID() {
		return strParentServiceID;
	}


	public void setStrParentServiceID(String strParentServiceID) {
		this.strParentServiceID = strParentServiceID;
	}


	public byte getByLanguage() {
		return byLanguage;
	}


	public void setByLanguage(byte byLanguage) {
		this.byLanguage = byLanguage;
	}

	
	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getStrName() {
		return strName;
	}

	public void setStrName(String strName) {
		this.strName = strName;
	}


	public String getStrSelectName() {
		return strSelectName;
	}


	public void setStrSelectName(String strSelectName) {
		this.strSelectName = strSelectName;
	}


	public int getIkind() {
		return ikind;
	}

	public void setIkind(int ikind) {
		this.ikind = ikind;
	}


	public int getiIconVs() {
		return iIconVs;
	}

	public void setiIconVs(int iIconVs) {
		this.iIconVs = iIconVs;
	}

	public int getiTextVs() {
		return iTextVs;
	}

	public void setiTextVs(int iTextVs) {
		this.iTextVs = iTextVs;
	}

	public int getiPriority() {
		return iPriority;
	}

	public void setiPriority(int iPriority) {
		this.iPriority = iPriority;
	}

	
	public String getStrTel() {
		return strTel;
	}


	public void setStrTel(String strTel) {
		this.strTel = strTel;
	}


	public String getStrAddrInfo() {
		return strAddrInfo;
	}


	public void setStrAddrInfo(String strAddrInfo) {
		this.strAddrInfo = strAddrInfo;
	}


//	public String getStrCoor() {
//		return strCoor;
//	}
//
//	public void setStrCoor(String strCoor) {
//		this.strCoor = strCoor;
//	}

	
//	public String getStrFollowLineCoor() {
//		return strFollowLineCoor;
//	}
//
//	public void setStrFollowLineCoor(String strFollowLineCoor) {
//		this.strFollowLineCoor = strFollowLineCoor;
//	}

	public List<ShpPoint> getCoorList() {
		return coorList;
	}


	public void setCoorList(List<ShpPoint> coorList) {
		this.coorList = coorList;
	}

	
	public List<ShpPoint> getFollowLineCoorList() {
		return followLineCoorList;
	}
	
	
	public void setFollowLineCoorList(List<ShpPoint> followLineCoorList) {
		this.followLineCoorList = followLineCoorList;
	}


//	public byte getByFlag() {
//		return byFlag;
//	}
//
//	public void setByFlag(byte byFlag) {
//		this.byFlag = byFlag;
//	}
//
//	public byte getByLevel() {
//		return byLevel;
//	}
//
//	public void setByLevel(byte byLevel) {
//		this.byLevel = byLevel;
//	}

	public byte getbBegin_level() {
		return bBegin_level;
	}

	public void setbBegin_level(byte bBegin_level) {
		this.bBegin_level = bBegin_level;
	}

	public int getIsBegin_Scaleno() {
		return isBegin_Scaleno;
	}

	public void setIsBegin_Scaleno(int isBegin_Scaleno) {
		this.isBegin_Scaleno = isBegin_Scaleno;
	}

	public byte getbEnd_level() {
		return bEnd_level;
	}

	public void setbEnd_level(byte bEnd_level) {
		this.bEnd_level = bEnd_level;
	}

	public int getIsEnd_Scaleno() {
		return isEnd_Scaleno;
	}

	public void setIsEnd_Scaleno(int isEnd_Scaleno) {
		this.isEnd_Scaleno = isEnd_Scaleno;
	}

	public String getStrUnitNo() {
		return strUnitNo;
	}


	public void setStrUnitNo(String strUnitNo) {
		this.strUnitNo = strUnitNo;
	}


//	public int getIblockx() {
//		return iblockx;
//	}
//
//	public void setIblockx(int iblockx) {
//		this.iblockx = iblockx;
//	}
//
//	public int getIblocky() {
//		return iblocky;
//	}
//
//	public void setIblocky(int iblocky) {
//		this.iblocky = iblocky;
//	}

	/**
	 * 文字匹配情报，显示方位
	 */
	public int getiTxtConfAdv() {
		return iTxtConfAdv;
	}


	public void setiTxtConfAdv(int iTxtConfAdv) {
		this.iTxtConfAdv = iTxtConfAdv;
	}
	public void print(){
		System.out.print("bBegin_level "+bBegin_level+" bEnd_level "+bEnd_level+" byLanguage "+byLanguage+
				" ikind "+ikind+" iIconVs "+iIconVs+" iTextVs "+iTextVs+" iPriority "+iPriority+" isBegin_Scaleno "+isBegin_Scaleno+
				" isEnd_Scaleno "+isEnd_Scaleno+" iTxtConfAdv "+iTxtConfAdv+" strUnitNo "+strUnitNo+" strOrgId "+strOrgId+
				" strName "+strName+" strshpid "+strshpid);
		if(coorList!=null&&coorList.size()>0){
			System.out.print(" coorList "+coorList.get(0)+" size "+coorList.size());
		}
		if(followLineCoorList!=null&&followLineCoorList.size()>0){
			System.out.print(" followLineCoorList "+followLineCoorList.get(0)+" size "+followLineCoorList.size());
		}
		if(roadNameLineCoorList!=null&&roadNameLineCoorList.size()>0){
			System.out.print(" roadNameLineCoorList "+roadNameLineCoorList.get(0)+" size "+roadNameLineCoorList.size());
		}
		System.out.println(" iBlockX "+iBlockX+" iBlockY "+iBlockY);
	}




}
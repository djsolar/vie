package com.mansion.tele.db.bean.elemnet;

import java.util.ArrayList;
import java.util.List;

/**
 * Intersection entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("serial")
public class Intersection extends PointExElement implements
		Comparable<Intersection> {

	// Fields
	private byte byIntersectionType; // 交叉口类型
	// transient private String strEleCenterID; // 中心点ID
	
	
	private String astInnerRoadList;
	
	private String astOutRoadList;
	
//	private List<BaseName> astIntersectionNames; // 交叉口名称

	private String astInnerNodeID;
	
	private String astOutNodeID;

	private List<String> astNodeIDList;

	
	public List<String> getAstNodeIDList() {
		return astNodeIDList;
	}


	public void setAstNodeIDList(List<String> astNodeIDList) {
		this.astNodeIDList = astNodeIDList;
	}


	public byte getByIntersectionType() {
		return byIntersectionType;
	}


	public void setByIntersectionType(byte byIntersectionType) {
		this.byIntersectionType = byIntersectionType;
	}


	public String getAstInnerRoadList() {
		return astInnerRoadList;
	}


	public String getAstOutRoadList() {
		return astOutRoadList;
	}


	public String getAstInnerNodeID() {
		return astInnerNodeID;
	}


	public String getAstOutNodeID() {
		return astOutNodeID;
	}


	public List<String> getAstInnerRoadByList() {
		List<String> list = null;
		if (astInnerRoadList != null && !"".equals(astInnerRoadList.trim())) {
			list = new ArrayList<String>();
			String[] arrStrings = astInnerRoadList.split(";");
			for (int i = 0; i < arrStrings.length; i++) {
				if (!"".equals(arrStrings[i])) {
					list.add(arrStrings[i]);
				}
				
			}
		}
		return list;
	}


	public void setAstInnerRoadList(String astInnerRoadList) {
		this.astInnerRoadList = astInnerRoadList;
	}


	public List<String> getAstOutRoadByList() {
		List<String> list = null;
		if (astOutRoadList != null && !"".equals(astOutRoadList.trim())) {
			list = new ArrayList<String>();
			String[] arrStrings = astOutRoadList.split(";");
			for (int i = 0; i < arrStrings.length; i++) {
				if (!"".equals(arrStrings[i])) {
					list.add(arrStrings[i]);
				}
			}
		}
		return list;
	}


	public void setAstOutRoadList(String astOutRoadList) {
		this.astOutRoadList = astOutRoadList;
	}


	public List<String> getAstInnerNodeIDList() {
		List<String> list = null;
		if (astInnerNodeID != null && !"".equals(astInnerNodeID.trim())) {
			list = new ArrayList<String>();
			String[] arrStrings = astInnerNodeID.split(";");
			for (int i = 0; i < arrStrings.length; i++) {
				if (!"".equals(arrStrings[i])) {
					list.add(arrStrings[i]);
				}
			}
		}
		return list;
	}


	public void setAstInnerNodeID(String astInnerNodeID) {
		this.astInnerNodeID = astInnerNodeID;
	}


	public List<String> getAstOutNodeIDList() {
		
		List<String> list = null;
		if (astOutNodeID != null && !"".equals(astOutNodeID.trim())) {
			list = new ArrayList<String>();
			String[] arrStrings = astOutNodeID.split(";");
			for (int i = 0; i < arrStrings.length; i++) {
				if (!"".equals(arrStrings[i])) {
					list.add(arrStrings[i]);
				}
			}
		}
		return list;
	}


	public void setAstOutNodeID(String astOutNodeID) {
		this.astOutNodeID = astOutNodeID;
	}


	public byte getByintersectiontype() {
		return byIntersectionType;
	}


	public void setByintersectiontype(byte byintersectiontype) {
		this.byIntersectionType = byintersectiontype;
	}

	// public String getStrEleCenterID() {
	// return strEleCenterID;
	// }
	// public void setStrEleCenterID(String strEleCenterID) {
	// this.strEleCenterID = strEleCenterID;
	// }
//	public List<String> getAstNodeIDList() {
//		return astNodeIDList;
//	}
//
//	public void setAstNodeIDList(List<String> astNodeIDList) {
//		if (astNodeIDList == null && this.astNodeIDList != null) {
//			this.astNodeIDList.clear();
//		} else {
//			this.astNodeIDList = astNodeIDList;
//		}
//	}

//	public List<BaseName> getAstIntersectionNames() {
//		return astIntersectionNames;
//	}
//
//	public void setAstIntersectionNames(List<BaseName> astIntersectionNames) {
//		if (astIntersectionNames == null && this.astIntersectionNames != null) {
//			this.astIntersectionNames.clear();
//		} else {
//			this.astIntersectionNames = astIntersectionNames;
//		}
//	}
//
//	public List<String> getAstInnerNodeIDList() {
//		return astInnerNodeIDList;
//	}
//
//	public void setAstInnerNodeIDList(List<String> astInnerNodeIDList) {
//		if (astInnerNodeIDList == null && this.astInnerNodeIDList != null) {
//			this.astInnerNodeIDList.clear();
//		} else {
//			this.astInnerNodeIDList = astInnerNodeIDList;
//		}
//	}

	public void convert(Intersection intersection) {


		this.astInnerNodeID = intersection.astInnerNodeID;
		this.astOutNodeID = intersection.astOutNodeID;
		this.astInnerRoadList = intersection.astInnerRoadList;
		this.astOutRoadList = intersection.astOutRoadList;
		this.setByintersectiontype(intersection.getByintersectiontype());

	}
	


	@Override
	public int compareTo(Intersection o) {
		return this.strID.compareTo(o.strID);
	}

}
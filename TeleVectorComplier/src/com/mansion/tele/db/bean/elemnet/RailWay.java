package com.mansion.tele.db.bean.elemnet;

import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.Task;

/**
 * RailWay entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("serial")
public class RailWay extends PolylineElement {

	// Fields
	protected int iRailWayType; //铁路类型
//	private byte byUndergroundFlag; // ���±�־
//	private byte byDisplayLevel; // ��ʾ�㼶
	private transient List<BaseName> astRailWayNames; // ��·���

	/**
	 * 由母库的结构转换为中间库M1的结构
	 * @param railWay 要素
	 * @param bLevel level号
	 * @param bStep  任务阶段号
	 * @param iUnitNo 任务号
	 */
	public void convert(RailWay railWay) {
		if (railWay.stGeom != null) {
			this.stGeom = new PolylineShp();
			this.stGeom.strID = this.strID;
			this.stGeom.convert(railWay.stGeom);
		}
		this.setIrailwaytype(railWay.getIrailwaytype());
		if (railWay.getAstRailWayNames() != null && railWay.getAstRailWayNames().size() > 0) {
			List<BaseName> astBaseNames = new ArrayList<BaseName>();
			for (int i = 0; i < railWay.getAstRailWayNames().size(); i++) {
				BaseName baseName = new BaseName();
				baseName.convert(railWay.getAstRailWayNames().get(i));
				baseName.setMainEntity(this);
				astBaseNames.add(baseName);
			}
			this.astRailWayNames = astBaseNames;
			
		}
		
	}
	public void copyobjec(RailWay railWay){
		this.iRailWayType = railWay.getIrailwaytype();
		if (railWay.getAstRailWayNames() != null && railWay.getAstRailWayNames().size() > 0) {
			List<BaseName> astBaseNames = new ArrayList<BaseName>();
			for (int i = 0; i < railWay.getAstRailWayNames().size(); i++) {
				BaseName baseName = new BaseName();
				baseName.convert(railWay.getAstRailWayNames().get(i));
				baseName.setMainEntity(this);
				astBaseNames.add(baseName);
			}
			this.astRailWayNames = astBaseNames;
			
		}
		PolylineShp poly = new PolylineShp();
		this.setStGeom(poly.copyobjec(railWay.getStGeom()));
	}
	
	// Constructors

	/** default constructor */
	public RailWay() {
	}

	public int getIrailwaytype() {
		return iRailWayType;
	}
	
	public void setIrailwaytype(int irailwaytype) {
		this.iRailWayType = irailwaytype;
	}
	
//	public byte getByundergroundflag() {
//		return byUndergroundFlag;
//	}
//
//	public void setByundergroundflag(byte byundergroundflag) {
//		this.byUndergroundFlag = byundergroundflag;
//	}

//	public byte getBydisplaylevel() {
//		return byDisplayLevel;
//	}
//
//	public void setBydisplaylevel(byte bydisplaylevel) {
//		this.byDisplayLevel = bydisplaylevel;
//	}

	public List<BaseName> getAstRailWayNames()
	{
		return astRailWayNames;
	}

	public void setAstRailWayNames(List<BaseName> astRailWayNames)
	{
		if (astRailWayNames == null && this.astRailWayNames != null) {
			this.astRailWayNames.clear();
		} else {
			this.astRailWayNames = astRailWayNames;
		}
	}

}
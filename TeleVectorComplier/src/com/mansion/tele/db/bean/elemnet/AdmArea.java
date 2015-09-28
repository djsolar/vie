package com.mansion.tele.db.bean.elemnet;

import java.util.List;

import com.mansion.tele.business.Task;

/**
 * AdmArea entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("serial")
public class AdmArea extends PolygonElement {

	private int byAdmAreaType;
	private transient List<AdmAreaBorder> astAdmAreaBorders; // ������߽�Ԫ��
	
	public void convert(AdmArea admArea){
		this.setByadmareatype(admArea.getByadmareatype());
		this.lArea = admArea.getlArea();
		if (admArea.getAstAdmAreaBorders() != null && admArea.getAstAdmAreaBorders().size() > 0) {
			PolygonShp polygonShp = new PolygonShp();
			polygonShp.convert(admArea.getAstAdmAreaBorders());
			this.stGeom = polygonShp;
		}
	}

	public int getByadmareatype() {
		return this.byAdmAreaType;
	}

	public void setByadmareatype(int byadmareatype) {
		this.byAdmAreaType = byadmareatype;
	}

	public List<AdmAreaBorder> getAstAdmAreaBorders() {
		return astAdmAreaBorders;
	}

	public void setAstAdmAreaBorders(List<AdmAreaBorder> astAdmAreaBorders) {
		if (astAdmAreaBorders == null && this.astAdmAreaBorders != null) {
			this.astAdmAreaBorders.clear();
		} else {
			this.astAdmAreaBorders = astAdmAreaBorders;
		}
	}

}

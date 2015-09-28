package com.mansion.tele.db.bean.elemnet;

import java.util.List;

import com.mansion.tele.business.Task;

/**
 * LandUse entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("serial")
public class LandUse extends PolygonElement {


	private int iLandUseType; 
	
//	private int iFloorCount;
	
//	private byte byDisplayLevel; 
	
//	private String strAdmCode; 

//	private List<BaseName> astLandUseNames; 
	
	private transient List<LandUseBorder> astLandUseBorders; 

	/** default constructor */
	public LandUse() {
		
	}

	public void convert(LandUse landUse) {
		this.setIlandusetype(landUse.getIlandusetype());
		this.lArea = landUse.getlArea();
		if (landUse.getAstLandUseBorders() != null && landUse.getAstLandUseBorders().size() > 0) {
			
			PolygonShp polygonShp = new PolygonShp();
			polygonShp.convert(landUse.getAstLandUseBorders());
			this.stGeom = polygonShp;
		}
	}
	public void copyobjec(LandUse landuse){
		this.setIlandusetype(landuse.getIlandusetype());
		this.setAstLandUseBorders(landuse.getAstLandUseBorders());
		this.lArea = landuse.getlArea();
		PolygonShp poly = new PolygonShp();
		this.setStGeom(poly.copyobjec(landuse.getStGeom()));
		this.bygrade = landuse.getBygrade();
		this.iBlockX = landuse.getiBlockX();
		this.iBlockY = landuse.getiBlockY();
		this.strID = landuse.getStrid();
	}
	

	public int getIlandusetype() {
		return this.iLandUseType;
	}

	public void setIlandusetype(int ilandusetype) {
		this.iLandUseType = ilandusetype;
	}


	public List<LandUseBorder> getAstLandUseBorders() {
		return astLandUseBorders;
	}

	public void setAstLandUseBorders(List<LandUseBorder> landUseBorder) {
		if (landUseBorder == null && this.astLandUseBorders != null) {
			this.astLandUseBorders.clear();
		} else {
			this.astLandUseBorders = landUseBorder;
		}
	}

}

package com.mansion.tele.db.bean.elemnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhaozhongyi
 *
 */

@SuppressWarnings("serial")
public class CRailwayLine implements Serializable{


	private String gid;
	
	private String crlineid;
	
	private String namec;
	
	private String geom1;
	
	private String geom2;
	
	private List<ShpPoint> geomList ;
	
	
	
	
	public String getGeom1() {
		return geom1;
	}

	public void setGeom1(String geom1) {
		this.geom1 = geom1;
	}

	public String getGeom2() {
		return geom2;
	}

	public void setGeom2(String geom2) {
		this.geom2 = geom2;
	}

	public List<ShpPoint> getGeomList() {
		if (geomList == null && geom1 != null && !"".equals(geom1)) {
			String coordinates = geom1;
			if (geom2 != null && !"".equals(geom2)) {
					coordinates = coordinates+geom2;
			}
			geomList = new ArrayList<ShpPoint>();
			String[] points = coordinates.split(",");
			for (int i = 0; i < points.length; i++) {
				double dx = Double.parseDouble(points[i].split("\\s")[0]);
				double dy = Double.parseDouble(points[i].split("\\s")[1]);
				int x = (int) (dx * 2560 * 3600);
				int y = (int) (dy * 2560 * 3600);
				ShpPoint point = new ShpPoint(x,y);
				geomList.add(point);
			}
		}
		return geomList;
	}
	
	@Override
	public boolean equals(Object arg0) {
		CRailwayLine subsway = (CRailwayLine)arg0;
		if(this.getCrlineid().substring(0,8).equals(subsway.getCrlineid().substring(0, 8))
				&& this.getNamec().split("\\u0028")[0].equals(subsway.getNamec().split("\\u0028")[0])){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Integer.parseInt(this.getCrlineid().substring(0,8));
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getCrlineid() {
		return crlineid;
	}

	public void setCrlineid(String crlineid) {
		this.crlineid = crlineid;
	}

	public String getNamec() {
		return namec;
	}

	public void setNamec(String namec) {
		this.namec = namec;
	}
}

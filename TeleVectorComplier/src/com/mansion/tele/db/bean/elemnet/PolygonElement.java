package com.mansion.tele.db.bean.elemnet;

@SuppressWarnings("serial")
public class PolygonElement extends Element
{
	protected PolygonShp stGeom;
	
	protected long lArea ;


	public long getlArea() {
		return lArea;
	}

	public void setlArea(long lArea) {
		this.lArea = lArea;
	}

	public PolygonShp getStGeom()
	{
		return stGeom;
	}

	public void setStGeom(PolygonShp stGeom)
	{
		this.stGeom = stGeom;
	}
}

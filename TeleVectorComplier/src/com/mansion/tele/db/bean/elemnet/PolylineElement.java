package com.mansion.tele.db.bean.elemnet;

@SuppressWarnings("serial")
public class PolylineElement extends Element
{
	protected PolylineShp stGeom;

	public PolylineShp getStGeom()
	{
		return stGeom;
	}

	public void setStGeom(PolylineShp stGeom)
	{
		this.stGeom = stGeom;
	}
}

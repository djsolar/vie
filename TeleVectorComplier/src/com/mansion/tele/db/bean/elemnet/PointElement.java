package com.mansion.tele.db.bean.elemnet;

@SuppressWarnings("serial")
public class PointElement extends Element
{
	protected PointShp stGeom;

	public PointShp getStGeom()
	{
		return stGeom;
	}

	public void setStGeom(PointShp stGeom)
	{
		this.stGeom = stGeom;
	}
}

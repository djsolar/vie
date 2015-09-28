package com.mansion.tele.db.bean.elemnet;


public class PointShpEx extends PointShp
{
	private static final long serialVersionUID = 6426864651622217245L;

	private ShpPoint lbCoordinate; // ���µ�

	private ShpPoint rtCoordinate; // ���ϵ�

	public ShpPoint getLbCoordinate()
	{
		return lbCoordinate;
	}

	public void setLbCoordinate(ShpPoint lbCoordinate)
	{
		this.lbCoordinate = lbCoordinate;
	}

	public ShpPoint getRtCoordinate()
	{
		return rtCoordinate;
	}

	public void setRtCoordinate(ShpPoint rtCoordinate)
	{
		this.rtCoordinate = rtCoordinate;
	}

}

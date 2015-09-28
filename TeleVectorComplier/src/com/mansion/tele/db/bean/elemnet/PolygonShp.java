package com.mansion.tele.db.bean.elemnet;

import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.common.GeoRect;
import com.mansion.tele.util.BorderUtil;
import com.mansion.tele.util.GeoUtil;


@SuppressWarnings("serial")
public class PolygonShp extends BaseShp
{

	private ShpPoint lbCoordinate; // ���µ�

	private ShpPoint rtCoordinate; // ���ϵ�

	private List<ShpPoint> coordinate; // ���������,�ͻ���
	
	
	public PolygonShp() {
	}

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
	public void setCoordinate(List<ShpPoint> coordinate)
	{
		this.coordinate = coordinate;
	}

	public List<ShpPoint> getCoordinate() {
		return coordinate;
	}

	public <T extends BaseBorder> void convert(List<T> astBorders) {
		List<ShpPoint> coordinate = BorderUtil.getPolygon(astBorders);
		this.coordinate = coordinate;
		GeoRect rect = GeoUtil.getRectByPoints(coordinate);
		this.rtCoordinate = new ShpPoint(rect.right, rect.top);
		this.lbCoordinate = new ShpPoint(rect.left, rect.bottom);
	}
	public PolygonShp copyobjec(PolygonShp polygonshp){
		PolygonShp poly = new PolygonShp();
		List<ShpPoint> shp_list = new ArrayList<ShpPoint>();
		poly.lbCoordinate = new ShpPoint();
		poly.rtCoordinate = new ShpPoint();
		poly.lbCoordinate.convert(polygonshp.getLbCoordinate());
		poly.rtCoordinate.convert(polygonshp.getRtCoordinate());
		for(int i=0;i<polygonshp.getCoordinate().size();i++){
			ShpPoint shp = new ShpPoint();
			shp.convert(polygonshp.getCoordinate().get(i));
			shp_list.add(shp);
		}
		poly.setCoordinate(shp_list);
		return poly;
	}
	

}

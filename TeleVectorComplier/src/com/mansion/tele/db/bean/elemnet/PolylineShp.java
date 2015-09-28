package com.mansion.tele.db.bean.elemnet;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class PolylineShp extends BaseShp
{

	
	private transient String aicoordinate; // 坐标点的字符串形式
	private transient String aicoordinate1; // 坐标点的字符串形式
	
	protected ShpPoint lbCoordinate; // 矩形框的左下

	protected ShpPoint rtCoordinate; // 右上

	private List<ShpPoint> coordinate; //形状点的x和y
	
	

	
	public PolylineShp() {
	}
	public PolygonShp copyobjec(PolygonShp polygonshp){
		PolygonShp poly = new PolygonShp();
		List<ShpPoint> shp_list = new ArrayList<ShpPoint>();
		this.lbCoordinate = new ShpPoint();
		this.rtCoordinate = new ShpPoint();
		this.lbCoordinate.convert(polygonshp.getLbCoordinate());
		this.rtCoordinate.convert(polygonshp.getRtCoordinate());
		for(int i=0;i<polygonshp.getCoordinate().size();i++){
			ShpPoint shp = new ShpPoint();
			shp.convert(polygonshp.getCoordinate().get(i));
			shp_list.add(shp);
		}
		poly.setCoordinate(shp_list);
		return poly;
	}
	public PolylineShp copyobjec(PolylineShp polygonshp){
		PolylineShp poly = new PolylineShp();
		List<ShpPoint> shp_list = new ArrayList<ShpPoint>();
		this.lbCoordinate = new ShpPoint();
		this.rtCoordinate = new ShpPoint();
		this.lbCoordinate.convert(polygonshp.getLbCoordinate());
		this.rtCoordinate.convert(polygonshp.getRtCoordinate());
		for(int i=0;i<polygonshp.getCoordinate().size();i++){
			ShpPoint shp = new ShpPoint();
			shp.convert(polygonshp.getCoordinate().get(i));
			shp_list.add(shp);
		}
		poly.setCoordinate(shp_list);
		return poly;
	}


	public void convert(PolylineShp stGeom) {
		this.lbCoordinate = new ShpPoint();
		this.lbCoordinate.convert(stGeom.lbCoordinate);
		this.rtCoordinate = new ShpPoint();
		this.rtCoordinate.convert(stGeom.rtCoordinate);
		if (stGeom.getCoordinate() != null && stGeom.getCoordinate().size() > 0) {
			List<ShpPoint> shpPoints = new ArrayList<ShpPoint>();
			List<ShpPoint> shpPoints2 = stGeom.getCoordinate();
			for (int i = 0; i < shpPoints2.size(); i++) {
				ShpPoint shpPoint = new ShpPoint();
				shpPoint.convert(shpPoints2.get(i));
				shpPoints.add(shpPoint);
			}
			this.setCoordinate(shpPoints);
		}
	}
	
	public void changeCoordinateSeq(){
		
		List<ShpPoint> list = this.getCoordinate();
		
		List<ShpPoint> newlist = new ArrayList<ShpPoint>();
		
		for (int i = list.size() - 1; i >= 0 ; i--) {
			
			newlist.add(list.get(i));
			
		}
		
		this.setCoordinate(newlist);
		
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

	public List<ShpPoint> getCoordinate()
	{
		if (coordinate == null && aicoordinate != null && !"".equals(aicoordinate)) {
			String coordinates = aicoordinate;
			if (aicoordinate1 != null || !"".equals(aicoordinate1)) {
				coordinates = coordinates + aicoordinate1;
			}
			coordinate = new ArrayList<ShpPoint>();
			String[] points = coordinates.split(";");
			for (int i = 0; i < points.length - 2; i+=3) {
				try {
					int x = Integer.parseInt(points[i]);
					int y = Integer.parseInt(points[i+1]);
					ShpPoint point = new ShpPoint(x,y);
					coordinate.add(point);
				} catch (Exception e) {
					System.out.println(coordinates);
					System.out.println(points.length);
					e.printStackTrace();
				}
				
			}
		}
		aicoordinate = null;
		aicoordinate1 = null;
		return coordinate;
	}

	public void setCoordinate(List<ShpPoint> coordinate)
	{
		this.coordinate = coordinate;
	}
	
	public String getAicoordinate() {
		return this.aicoordinate;
	}

	public void setAicoordinate(String aicoordinate) {
		this.aicoordinate = aicoordinate;
	}

	public String getAicoordinate1() {
		return this.aicoordinate1;
	}

	public void setAicoordinate1(String aicoordinate1) {
		this.aicoordinate1 = aicoordinate1;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		result = prime * result + ((lbCoordinate == null) ? 0 : lbCoordinate.hashCode());
		result = prime * result + ((rtCoordinate == null) ? 0 : rtCoordinate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PolylineShp other = (PolylineShp) obj;
		if (coordinate == null) {
			if (other.coordinate != null)
				return false;
		} else if (!coordinate.equals(other.coordinate))
			return false;
		if (lbCoordinate == null) {
			if (other.lbCoordinate != null)
				return false;
		} else if (!lbCoordinate.equals(other.lbCoordinate))
			return false;
		if (rtCoordinate == null) {
			if (other.rtCoordinate != null)
				return false;
		} else if (!rtCoordinate.equals(other.rtCoordinate))
			return false;
		return true;
	}
	
	public void setRectangleRange(List<ShpPoint> coordinate){
		rtCoordinate.setX(coordinate.get(0).getX());
		rtCoordinate.setX(coordinate.get(0).getY());
		lbCoordinate.setX(coordinate.get(0).getX());
		lbCoordinate.setX(coordinate.get(0).getY());
		for (ShpPoint shpPoint : coordinate) {
			if(shpPoint.getX()>rtCoordinate.getX()){
				rtCoordinate.setX(shpPoint.getX());
			}
			if(shpPoint.getY()>rtCoordinate.getY()){
				rtCoordinate.setY(shpPoint.getY());
			}
			if(shpPoint.getX()<lbCoordinate.getX()){
				lbCoordinate.setX(shpPoint.getX());
			}
			if(shpPoint.getY()<lbCoordinate.getY()){
				lbCoordinate.setY(shpPoint.getY());
			}			
		}
	}
}

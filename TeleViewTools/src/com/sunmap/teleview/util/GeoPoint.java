package com.sunmap.teleview.util;

/**
 * 
 * 表示一个地理坐标点，存放经度和纬度，单位：1/2560秒
 *
 */
public class GeoPoint {
	public int latitude;
	public int longitude;

	/**
	 * 
	 * @param latitude	纬度，单位：1/2560秒
	 * @param longitude	经度，单位：1/2560秒
	 */
    public GeoPoint(int latitude, int longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public GeoPoint(GeoPoint point) {
    	if (point != null) {
            this.latitude = point.latitude;
            this.longitude = point.longitude;
		}
    }

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }
    
    public void setLatitude(int latitude) {
    	this.latitude = latitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "GeoPoint: Latitude: " + latitude + ", Longitude: " + longitude;
    }
    
    public void copy(GeoPoint point){
    	if (point != null) {
        	this.longitude = point.longitude;
        	this.latitude = point.latitude;
		}
    }

	@Override
	public boolean equals(Object o) {
		if (o != null &&
			this.latitude == ((GeoPoint) o).latitude &&
			this.longitude == ((GeoPoint) o).longitude) {
			return true;
		}
		return false;
	}
}

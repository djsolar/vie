/**
 * 
 */
package com.sunmap.teleview.util;

import java.awt.Point;

/**
 * @author lijingru
 *
 */
public class AdminCode {

	private int admin_code;
	private String province;
	private String city;
	private int lon;
	private int lat;
	public AdminCode() {
		
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public int getAdmin_code() {
		return admin_code;
	}
	public void setAdmin_code(int admin_code) {
		this.admin_code = admin_code;
	}
	public int getLon() {
		return lon;
	}
	public void setLon(int lon) {
		this.lon = lon;
	}
	public int getLat() {
		return lat;
	}
	public void setLat(int lat) {
		this.lat = lat;
	}
	public Point getPoint(){
		return new Point(lon, lat);
	}
	
}

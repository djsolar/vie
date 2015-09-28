package com.sunmap.been;

import java.util.List;

public class Province {

	private String province_name;
	private long size;
	private List<City> pro_city;
	private transient String province_pinyin;
	private transient int pri;
	private transient String admin_code;
	public String getProvinceName() {
		return province_name;
	}
	public void setProvinceName(String provinceName) {
		this.province_name = provinceName;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public List<City> getProCity() {
		return pro_city;
	}
	public void setProCity(List<City> proCity) {
		this.pro_city = proCity;
	}
	public String getProvince_pinyin() {
		return province_pinyin;
	}
	public void setProvince_pinyin(String province_pinyin) {
		this.province_pinyin = province_pinyin;
	}
	public int getPri() {
		return pri;
	}
	public void setPri(int pri) {
		this.pri = pri;
	}
	public String getAdmin_code() {
		return admin_code;
	}
	public void setAdmin_code(String admin_code) {
		this.admin_code = admin_code;
	}
	
}

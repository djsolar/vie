package com.sunmap.been;

public class City {
	
	private String city_name;
	private long size;
	public String getName() {
		return city_name;
	}
	public void setName(String name) {
		this.city_name = name;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	@Override
	public boolean equals(Object obj) {
		City c = (City)obj;
		return this.city_name.equals(c.city_name);
	}

}

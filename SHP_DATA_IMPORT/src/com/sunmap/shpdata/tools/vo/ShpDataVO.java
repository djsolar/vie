package com.sunmap.shpdata.tools.vo;

import java.util.HashMap;
import java.util.Map;

public class ShpDataVO {
	private Map<String , String > cityMap = new HashMap<String ,String>();

	public Map<String, String> getCityMap() {
		return cityMap;
	}

	public void setCityMap(Map<String, String> cityMap) {
		this.cityMap = cityMap;
	}
}

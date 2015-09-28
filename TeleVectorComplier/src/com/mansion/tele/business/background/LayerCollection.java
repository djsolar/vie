package com.mansion.tele.business.background;

import java.util.HashMap;
import java.util.Map;

import com.mansion.tele.common.LayerNo;

/**
 * 要素分类属性信息表
 * @author hefeng
 * 
 */
public class LayerCollection {

	public static void init(){
		AdmArea.minlevelMap = new HashMap<Integer, Integer>();
		AdmArea.maxlevelMap = new HashMap<Integer, Integer>();
    	AdmArea[] admAreas = AdmArea.values();
		for (int i = 0; i < admAreas.length; i++) {
			AdmArea.minlevelMap.put(admAreas[i].iid, admAreas[i].iMinLevel);
			AdmArea.maxlevelMap.put(admAreas[i].iid, admAreas[i].iMaxLevel);
		}

		Water.minlevelMap = new HashMap<Integer, Integer>();
		Water.maxlevelMap = new HashMap<Integer, Integer>();
		Water[] waters = Water.values();
		for (int i = 0; i < waters.length; i++) {
			Water.minlevelMap.put(waters[i].iid, waters[i].iMinLevel);
			Water.maxlevelMap.put(waters[i].iid, waters[i].iMaxLevel);
		}

		LandUse.minlevelMap = new HashMap<Integer, Integer>();
		LandUse.maxlevelMap = new HashMap<Integer, Integer>();
		LandUse[] landuse = LandUse.values();
		for (int i = 0; i < landuse.length; i++) {
			LandUse.minlevelMap.put(landuse[i].iid, landuse[i].iMinLevel);
			LandUse.maxlevelMap.put(landuse[i].iid, landuse[i].iMaxLevel);
		}

		RailWay.minlevelMap = new HashMap<Integer, Integer>();
		RailWay.maxlevelMap = new HashMap<Integer, Integer>();
		RailWay[] railWay = RailWay.values();
		for (int i = 0; i < railWay.length; i++) {
			RailWay.minlevelMap.put(railWay[i].iid, railWay[i].iMinLevel);
			RailWay.maxlevelMap.put(railWay[i].iid, railWay[i].iMaxLevel);
		}
}

	public static enum AdmArea {
		ProvinceAdmArea(1, LayerNo.ProvinceAdmArea, "省级行政面",6,7);//将国家面做成省级行政面
		public int iid;
		public LayerNo layerNo;
		public String strContext;
		public int iMinLevel;
		public int iMaxLevel;
	    public static  Map<Integer, Integer> minlevelMap,maxlevelMap;

		private AdmArea(int iid, LayerNo layerNo, String strContext,int iMinLevel,int iMaxLevel) {
			this.iid = iid;
			this.layerNo = layerNo;
			this.strContext = strContext;
			this.iMinLevel = iMinLevel;
			this.iMaxLevel = iMaxLevel;
		}

	}

	public static enum Water {
		WaterSeaArea(257, LayerNo.SeaArea, "海域",0,5), 
		BigRiver(289, LayerNo.Water, "大河",0,6),
		SmallRiver(290, LayerNo.Water, "小河",0,6),
		Canal(291, LayerNo.Water, "运河",0,6),
		Reservoir(298, LayerNo.Water, "水库",0,6),
		Lake(299, LayerNo.Water, "湖泊",0,6),
		Pond(300, LayerNo.Water, "池塘",0,6);
		public int iid;
		public LayerNo layerNo;
		public String strContext;
		public int iMinLevel;
		public int iMaxLevel;
	    public static  Map<Integer, Integer>  minlevelMap,maxlevelMap;

		private Water(int iid, LayerNo layerNo, String strContext,int iMinLevel,int iMaxLevel) {
			this.iid = iid;
			this.layerNo = layerNo;
			this.strContext = strContext;
			this.iMinLevel = iMinLevel;
			this.iMaxLevel = iMaxLevel;
		}

	}

	public static enum LandUse {
		//12Q3版本发现公园code
		Park(775, LayerNo.Land, "公园", 0 , 3),
		GreenStreet(777, LayerNo.Land, "街头绿地", 0 , 3),
		Forest(1539, LayerNo.Land, "林区", 0 , 3),
		Lawn(1540, LayerNo.Land, "草地", 0 , 3),
		Island(1537,LayerNo.Island, "岛屿", 0 , 6);
		public int iid;
		public LayerNo layerNo;
		public String strContext;
		public int iMinLevel;
		public int iMaxLevel;
	    public static  Map<Integer, Integer>  minlevelMap,maxlevelMap;

		private LandUse(int iid, LayerNo layerNo, String strContext,int iMinLevel,int iMaxLevel) {
			this.iid = iid;
			this.layerNo = layerNo;
			this.strContext = strContext;
			this.iMinLevel = iMinLevel;
			this.iMaxLevel = iMaxLevel;
		}

	}

	public static enum RailWay {
		Rail(1, LayerNo.RailWay, "铁路",0,5),
		LightRail(2,LayerNo.RailWay, "轻轨1",0,3),
		Subway(3,LayerNo.RailWay, "地铁",0,3),
		Suspension(4,LayerNo.RailWay, "磁悬浮",0,3),
		LightRailOne(90,LayerNo.LightRailOne, "轻轨1",0,3),
		LightRailTwo(91,LayerNo.LightRailTwo, "轻轨2",0,3),
		LightRailThree(92,LayerNo.LightRailThree, "轻轨3",0,3),
		SubwayOne(70,LayerNo.SubwayOne, "地铁1",0,3),
		SubwayTwo(71,LayerNo.SubwayTwo, "地铁2",0,3),
		SubwayThree(72,LayerNo.SubwayThree, "地铁3",0,3),
		SubwayFour(73,LayerNo.SubwayFour, "地铁4",0,3),
		SubwayFive(74,LayerNo.SubwayFive, "地铁5",0,3),
		SubwaySix(75,LayerNo.SubwaySix, "地铁6",0,3),
		SubwaySeven(76,LayerNo.SubwaySeven, "地铁7",0,3),
		SubwayEight(77,LayerNo.SubwayEight, "地铁8",0,3),
		SubwayNine(78,LayerNo.SubwayNine, "地铁9",0,3),
		SubwayTen(79,LayerNo.SubwayTen, "地铁10",0,3),
		SubwayEleven(80,LayerNo.SubwayEleven, "地铁11",0,3),
		SubwayTwelve(81,LayerNo.SubwayTwelve, "地铁12",0,3),
		SubwayThirteen(82,LayerNo.SubwayThirteen, "地铁13",0,3),
		SubwayFourteen(83,LayerNo.SubwayFourteen, "地铁14",0,3),
		SuspensionOne(110,LayerNo.Suspension, "磁悬浮",0,3),
		NationBorder(11, LayerNo.AdmaBorderNation, "国界",5,7),
		// 不制作海岸线（因为海与海岸线套合不好）
		//SeaBankBorder(12,LayerNo.SeaBank, "海岸线",1,5),
		SeaBankBorder(12,LayerNo.SeaBank, "海岸线",-1,-1),
		SpecialBorder(13, LayerNo.AdmaBorderSpecial, "特殊边界（当前指港澳区界）",1,5),
		UnNationBorder(14, LayerNo.AdmaBorderUnNation, "行政界（未定国界）",5,7),
		Canal(15,LayerNo.WaterLine,"运河（线）",0,6),
		River(16,LayerNo.WaterLine,"河流（线）",0,6),
		ProviceBorder(17,LayerNo.AdmaBorderProvince,"省级区界",2,6),
		CityBorder(18,LayerNo.AdmaBorderCity,"市级区界",-1,-1),
		CountryBorder(19,LayerNo.AdmaBorderCountry,"区县级区界",-1,-1),
		UnProviceBorder(20,LayerNo.AdmaBorderProvince,"未定省界",2,6);

		public int iid;
		public LayerNo layerNo;
		public String strContext;
		public int iMinLevel;
		public int iMaxLevel;
	    public static  Map<Integer, Integer>  minlevelMap,maxlevelMap;

		private RailWay(int iid, LayerNo layerNo, String strContext,int iMinLevel,int iMaxLevel) {
			this.iid = iid;
			this.layerNo = layerNo;
			this.strContext = strContext;
			this.iMinLevel = iMinLevel;
			this.iMaxLevel = iMaxLevel;
		}

	}
}

package com.mansion.tele.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 矢量数据的layer编号
 * 
 * @author hefeng
 * 
 */
public enum LayerNo {

	SeaArea(1, -128, "海域"),
	ProvinceAdmArea(2, -127, "省级行政面"),
	Island(3, -126, "岛"),
	SeaFrontier(4, -125, "海疆线"),
	SeaBank(5, -124, "海岸线"),
	Land(6, -123, "绿地、公园"),
	Water(7, -122, "面性水系"),
	WaterLine(99, -121, "线性水系"),
	AdmaBorderNation(8, -120, "行政界（国界）"),
	AdmaBorderUnNation(9, -120, "行政界（未定国界）"),
	AdmaBorderProvince(10, -120, "行政界（省）"),
	AdmaBorderSpecial(11, -120, "特殊边界（当前指港澳区界）"),
	AdmaBorderCity(12, -120, "行政界（市）"),
	AdmaBorderCountry(13, -120, "行政界（县）"),
	BackRoad(40, -20, "背景路"),
	RailWay(41, -119, "铁路"),
	RoadOTHERROAD(50, 0, "航道"),
	RoadCOUNTYROAD(51, 0, "细道路1"),
	RoadCOUNTRYROAD(52, 0, "细道路2"),
	RoadCOMMONTROAD(53, 0, "一般道路"),
	RoadCITYSECONDROAD(54, 0,"城市次干路"),
	RoadCITYMAINROAD(55, 0, "城市主干路"),
	RoadPROVINCEROAD(56, 0, "省道"),
	RoadCITYTHRUWAY(57, 0, "城市快速路"),
	RoadNATIONROAD(58, 0, "国道"),
	RoadFREEWAY(59, 0, "高速道路"),
	SubwayOne(70, 100, "地铁1"),
	SubwayTwo(71, 100, "地铁2"),
	SubwayThree(72, 100, "地铁3"),
	SubwayFour(73, 100, "地铁4"),
	SubwayFive(74, 100, "地铁5"),
	SubwaySix(75, 100, "地铁6"),
	SubwaySeven(76, 100, "地铁7"),
	SubwayEight(77, 100, "地铁8"),
	SubwayNine(78, 100, "地铁9"),
	SubwayTen(79, 100, "地铁10"),
	SubwayEleven(80, 100, "地铁11"),
	SubwayTwelve(81, 100, "地铁12"),
	SubwayThirteen(82, 100, "地铁13"),
	SubwayFourteen(83, 100, "地铁14"),
	LightRailOne(90, 100, "轻轨1"),
	LightRailTwo(91, 100, "轻轨2"),
	LightRailThree(92, 100, "轻轨3"),
	Suspension(110, 100, "磁悬浮"),
	Mark(200, 127, "记号");


	/** layer编号 */
	public int iid;
	
	/**
	 * 跨压层级
	 */
	public int iGrad;

	/** layer名称文本 */
	public String strContext;

//	public static Map<Integer, LayerNo> layerMap = new HashMap<Integer, LayerNo>();
	public static ThreadLocal<Map<Integer, LayerNo>> layerMap = new ThreadLocal<Map<Integer, LayerNo>>(){
		@Override protected Map<Integer, LayerNo> initialValue() {
			Map<Integer, LayerNo>  layerMap1 = new HashMap<Integer, LayerNo>(); 
			LayerNo[] layerNos = LayerNo.values();
			for (int i = 0; i < layerNos.length; i++) {
				layerMap1.put(layerNos[i].iid, layerNos[i]);
			}
            return layerMap1;
         }
	};

	/**
	 * 构造函数
	 * @param iid			枚举值
	 * @param iGrad			跨压层级
	 * @param strContext	描述信息
	 */
	private LayerNo(int iid, int iGrad, String strContext) {
		
		this.iid = iid;
		this.iGrad = iGrad;
		this.strContext = strContext;
	}
	
//	static {
//		LayerNo[] layerNos = LayerNo.values();
//		for (int i = 0; i < layerNos.length; i++) {
//			layerMap.get().put(layerNos[i].iid, layerNos[i]);
//		}
//	}

}

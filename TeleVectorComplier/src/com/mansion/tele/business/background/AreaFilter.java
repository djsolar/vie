package com.mansion.tele.business.background;

/**
 * 面积过滤阀值
 * @author hefeng
 *
 */
public class AreaFilter {
	
	/**
	 * 不同水体类型，不同层的水体面积过滤阀值
	 */
	public static final int[][] WATER = {{0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0},
		{0, 0, 5000, 10000, 20000, 0, 0, 0},
		{0, 0, 5000, 7000, 10000, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0}};
	/**
	 * 公园 3层的面积过滤阀值
	 */
	public static final int LANDUSE_PARK_3 = 20000;
	/**
	 * 街头绿地3层的面积过滤阀值
	 */
	public static final int LANDUSE_PARK1_3 = 10000;
	/**
	 * 岛屿3层的面积过滤阀值
	 */
	public static final int LANDUSE_ISLAND_3 = 50000;
	/**
	 * 岛屿4层的面积过滤阀值
	 */
	public static final int LANDUSE_ISLAND_4 = 100000;
	/**
	 * 岛屿5层的面积过滤阀值
	 */
	public static final int LANDUSE_ISLAND_5 = 100000;
	/**
	 * 岛屿6层的面积过滤阀值
	 */
	public static final int LANDUSE_ISLAND_6 = 100000;
	
	/**
	 * 默认构造器
	 */
	private AreaFilter() {
		super();
	}
	
	

}

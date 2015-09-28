package com.sunmap.teleview.element.view;

import java.util.ArrayList;
import java.util.List;


public class LevelManage {
	// 数据与比例尺的对应关系
	private DataLevel dataLevels[];
	public ZoomLevel zoomLevels[];
	public DataLevel getDataLevel(double mapScale) {
		return dataLevels[getZoomLevel(mapScale).dataLevel];
	}

	public DataLevel getDataLevel(byte dataLevel) {
		return dataLevels[dataLevel];
	}

	public ZoomLevel getZoomLevel(double mapScale) {
		return zoomLevels[getZoomIndex(mapScale)];
	}

	public int getMapScaleByZoomIndex(int zoomIndex) {
		return zoomLevels[zoomIndex].scale;
	}

	public int getMaxMapScale() {
		// return zoomLevels.length - 1;
		int maxMapScale = 0;
		for (int i = 0; i < zoomLevels.length; i++) {
			if (zoomLevels[i].scale > maxMapScale) {
				maxMapScale = zoomLevels[i].scale;
			}
		}
		return maxMapScale;
	}

	public int getMinMapScale() {
		return zoomLevels[zoomLevels.length - 1].scale;
	}

	/**
	 * 返回当前zoomLevel对应的真实比例尺，单位：米每厘米
	 * 
	 * @param zoomLevel
	 * @return
	 */
	public double getScale(double zoomLevel) {
		int zoomLeveli = (int) zoomLevel;
		double ratio = zoomLevel - zoomLeveli;
		double scale1 = zoomLevels[zoomLeveli].scale;
		double scale2 = zoomLevels[zoomLeveli].scale;
		if (ratio > 0 && zoomLeveli < zoomLevels.length - 1) {
			scale2 = zoomLevels[zoomLeveli + 1].scale;
		}

		return scale1 + (scale2 - scale1) * ratio;
	}

	/*
	 * 根据scale获得zoomLevel
	 */
	public int getZoomLevelByScale(int scale) {
		if(scale >= zoomLevels[0].scale){
			return 0;
		}
		if(scale <= zoomLevels[zoomLevels.length - 1].scale){
			return zoomLevels.length - 1;
		}
		int retzoomLevelIndex = 0;
		for (int i = 0; i < zoomLevels.length - 1; i++) {
			int maxscale = zoomLevels[i].scale;
			int minscale = zoomLevels[i + 1].scale;
			if(scale >= minscale && scale < maxscale){
				if(scale - minscale > maxscale - scale){
					retzoomLevelIndex =  i;
				}else{
					retzoomLevelIndex =  i + 1;
				}
				
				break;
			}
		}
		return retzoomLevelIndex;
	}

	public void init() {
		zoomLevels = new ZoomLevel[16];
		dataLevels = new DataLevel[8];

		for (int i = 0; i < zoomLevels.length; i++) {
			zoomLevels[i] = new ZoomLevel();
		}
		zoomLevels[0].scale = 1000000;
		zoomLevels[0].zoomLevel = 1;
		zoomLevels[0].dataLevel = 7;
		zoomLevels[1].scale = 500000;
		zoomLevels[1].zoomLevel = 2;
		zoomLevels[1].dataLevel = 6;
		zoomLevels[2].scale = 200000;
		zoomLevels[2].zoomLevel = 1;
		zoomLevels[2].dataLevel = 6;
		zoomLevels[3].scale = 100000;
		zoomLevels[3].zoomLevel = 2;
		zoomLevels[3].dataLevel = 5;
		zoomLevels[4].scale = 50000;
		zoomLevels[4].zoomLevel = 1;
		zoomLevels[4].dataLevel = 5;
		zoomLevels[5].scale = 25000;
		zoomLevels[5].zoomLevel = 2;
		zoomLevels[5].dataLevel = 4;
		zoomLevels[6].scale = 10000;
		zoomLevels[6].zoomLevel = 1;
		zoomLevels[6].dataLevel = 4;
		zoomLevels[7].scale = 5000;
		zoomLevels[7].zoomLevel = 2;
		zoomLevels[7].dataLevel = 3;
		zoomLevels[8].scale = 2000;
		zoomLevels[8].zoomLevel = 1;
		zoomLevels[8].dataLevel = 3;
		zoomLevels[9].scale = 1000;
		zoomLevels[9].zoomLevel = 2;
		zoomLevels[9].dataLevel = 2;
		zoomLevels[10].scale = 500;
		zoomLevels[10].zoomLevel = 1;
		zoomLevels[10].dataLevel = 2;
		zoomLevels[11].scale = 200;
		zoomLevels[11].zoomLevel = 2;
		zoomLevels[11].dataLevel = 1;
		zoomLevels[12].scale = 100;
		zoomLevels[12].zoomLevel = 1;
		zoomLevels[12].dataLevel = 1;
		zoomLevels[13].scale = 50;
		zoomLevels[13].zoomLevel = 3;
		zoomLevels[13].dataLevel = 0;
		zoomLevels[14].scale = 25;
		zoomLevels[14].zoomLevel = 2;
		zoomLevels[14].dataLevel = 0;
		zoomLevels[15].scale = 10;
		zoomLevels[15].zoomLevel = 1;
		zoomLevels[15].dataLevel = 0;

		for (int i = 0; i < dataLevels.length; i++) {
			dataLevels[i] = new DataLevel();
		}

		dataLevels[0].level = 0;
		dataLevels[0].lonBlockWidth = 36000;
		dataLevels[0].latBlockHeight = 24000;
		for (int i = 1; i < 6; i++) {
			dataLevels[i].level = (byte) i;
			dataLevels[i].lonBlockWidth = dataLevels[i - 1].lonBlockWidth * 4;
			dataLevels[i].latBlockHeight = dataLevels[i - 1].latBlockHeight * 4;
		}
		dataLevels[6].level = 6;
		dataLevels[6].lonBlockWidth = dataLevels[5].lonBlockWidth * 8;
		dataLevels[6].latBlockHeight = dataLevels[5].latBlockHeight * 8;
		dataLevels[7].level = 7;
		dataLevels[7].lonBlockWidth = dataLevels[6].lonBlockWidth * 2;
		dataLevels[7].latBlockHeight = dataLevels[6].latBlockHeight * 2;
	}

	public static class DataLevel {
		public byte level;
		public int lonBlockWidth;// block的宽度（1/2560秒）
		public int latBlockHeight;//
	}

	public class ZoomLevel {
		public int scale;// 真实比例尺
		public byte zoomLevel;// 比例尺索引
		public byte dataLevel;// 数据层号
	}

	/**
	 * 根据dataLevel获得对应的比例尺
	 * 
	 * @param dataLevel
	 * @return
	 */
	public int[] getScalesByDataLevel(byte dataLevel) {
		List<Integer> scales = new ArrayList<Integer>();
		for (int i = 0; i < zoomLevels.length; i++) {
			if (zoomLevels[i].dataLevel == dataLevel) {
				scales.add(zoomLevels[i].scale);
			}
		}
		int size = scales.size();
		int[] result = new int[size];
		for (int i = 0; i < size; i++) {
			result[i] = scales.get(i);
		}
		return result;
	}

	/**
	 * 读取使用dataLevel的比例尺
	 * 
	 * @param dataLevel
	 * @return 比例尺列表
	 */
	public int[] getZoomindexByDataLevel(byte dataLevel) {
		List<Integer> indexs = new ArrayList<Integer>();
		for (int i = 0; i < zoomLevels.length; i++) {
			if (zoomLevels[i].dataLevel == dataLevel) {
				indexs.add(i);
			}
		}
		int size = indexs.size();
		int[] result = new int[size];
		for (int i = 0; i < size; i++) {
			result[i] = indexs.get(i);
		}
		return result;
	}

	/**
	 * 获取指定地图比例尺下的zoomindex
	 * 
	 * @param mapScale
	 *            ：地图比例尺（实际比例尺）
	 * @return
	 */
	public int getZoomIndex(double mapScale) {
		int retIndex = 0;
		// for(ScaleStyle varscaleSty: StyleManage.scaleStyles){
		// // int size = StyleManage.scaleStyles.size();
		// // for(int i = 0; i < size; i++){
		// // ScaleStyle varscaleSty = StyleManage.scaleStyles.get(i);
		// if(mapScale >= varscaleSty.minScale && mapScale <
		// varscaleSty.maxScale){
		// retIndex = varscaleSty.zoomIndex;
		// break;
		// }
		// }
		return retIndex;
	}
}

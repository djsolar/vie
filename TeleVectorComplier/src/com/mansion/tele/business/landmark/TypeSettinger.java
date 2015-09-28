package com.mansion.tele.business.landmark;

import com.mansion.tele.common.GeoRect;
/**
 * 文字排版比较功能
 * @author wxc
 *
 */
public class TypeSettinger {
	
	/**
	 * 线性文字与icon型比较方法		
	 * @param lineMarkLand
	 * @param signLandMark
	 */
	public static boolean lineComSign(MarkPoint lineMarkLand, MarkPoint signLandMark, int scaleNo ){
//		//线性与icon地标排版
//		GeoRect signGeoRect = signLandMark.signRect;
//		List<GeoRect> lineRect = lineMarkLand.eyeoneRectlist;
//		List<SignTypeSetRect> setRect = signLandMark.station.ableStations;
//		List<SignTypeSetRect> del = new ArrayList<SignTypeSetRect>();
//		//标示icon的文字框被删除的标示
//		boolean[] txtDel = new boolean[setRect.size()];
//		//比较两个地标是否必须删除一个的标示
//		boolean oneMustDel = false;
//		//线性文字所在的大矩形框与icon所在矩形框比较
//		if(lineMarkLand.comPareMaxGeo(signLandMark)){
//			//循环线性文字的每个小矩形框
//			for (int i = 0; i < lineRect.size(); i++) {
//				//线性框与icon比较
//				if(GeoRect.rectInRect(signGeoRect, lineRect.get(i))){
//					oneMustDel = true;
//					break;
//				}else{
//					//与icon的文字框比较
//					for (int j = setRect.size() - 1; j >= 0; j--) {
//						if(GeoRect.rectInRect(setRect.get(j).geoRects, lineRect.get(i))){
//							txtDel[j] = true;
//							del.add(setRect.remove(j));
//						}
//					}
//				}
//			}
//			//判断比较结果
//			byte flag = isAllDel(txtDel);
//			if(flag == -1 || oneMustDel){
////				TypeSettingMethod.comparePri(lineMarkLand, signLandMark,scaleNo);
//				if(lineMarkLand.bvs[scaleNo]){
//					signLandMark.station.ableStations = del;
//				}
//				return true;
//			}
//		}
		return false;
	}
	
	/**
	 * 路名与路名比较
	 * @param roadName1
	 * @param roadName2
	 */
	public static boolean lineCompareLine(MarkPoint roadName1, MarkPoint roadName2, int scaleNo){
		for(int i=0;i<roadName1.ableStations.size();i++){
			GeoRect rect1 = roadName1.ableStations.get(i).geoRects;
			for(int j=0;j<roadName2.ableStations.size();j++){
				GeoRect rect2 = roadName2.ableStations.get(j).geoRects;
				if(GeoRect.rectInRect(rect1, rect2)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 线性文字与文字类型进行比较
	 * @param lineMarkLand
	 * @param txtLandMark
	 */
	public static boolean lineCompareTxt(MarkPoint lineMarkLand, MarkPoint txtLandMark, int scaleNo){
//		GeoRect txtRect = txtLandMark.maxRect;
//		if(lineMarkLand.comPareMaxGeo(txtLandMark)){
//			for (int i = 0; i < lineMarkLand.eyeoneRectlist.size(); i++) {
//				if(GeoRect.rectInRect(lineMarkLand.eyeoneRectlist.get(i), txtRect)){
////					TypeSettingMethod.comparePri(lineMarkLand, txtLandMark,scaleNo);
//					return true;
//				}
//			}
//		}
		return false;
	}
	/**
	 * 文字与文字进行比较
	 * @param txtLandMark1
	 * @param txtLandMark2
	 */
	public static boolean LandMarkCompareLandMark(MarkPoint txtLandMark1, MarkPoint txtLandMark2,
			int scaleNo){
		GeoRect textRect1 = txtLandMark1.ableStations.get(0).geoRects;
		GeoRect textRect2 = txtLandMark2.ableStations.get(0).geoRects;
		if(GeoRect.rectInRect(textRect1, textRect2)){
			return true;
		}
		if(txtLandMark1.iconRect != null && GeoRect.rectInRect(txtLandMark1.iconRect.geoRects, 
				textRect2)){
			return true;
		}
		if(txtLandMark2.iconRect != null && GeoRect.rectInRect(txtLandMark2.iconRect.geoRects, 
				textRect1)){
			return true;
		}
		if(txtLandMark1.iconRect != null && txtLandMark2.iconRect != null && 
				GeoRect.rectInRect(txtLandMark1.iconRect.geoRects, txtLandMark2.iconRect.geoRects)){
			return true;
		}
		return false;
	}
	
	public static boolean lineCompareOther(MarkPoint roadName, MarkPoint otherMark){
		GeoRect textRect2 = otherMark.ableStations.get(0).geoRects;
		for(int i=0;i<roadName.ableStations.size();i++){
			GeoRect textRect1 = roadName.ableStations.get(i).geoRects;
			if(GeoRect.rectInRect(textRect1, textRect2)){
				return true;
			}
			if(otherMark.iconRect != null && 
					GeoRect.rectInRect(otherMark.iconRect.geoRects, textRect1)){
				return true;
			}
		}
		return false;
	}
	
//	/**
//	 * 文字与icon地标进行比较
//	 * @param txtLandMark
//	 * @param signLandMark
//	 */
//	public static boolean txtCompareSign(LandMarkType txtLandMark, LandMarkType signLandMark, int scaleNo){
//		if(GeoRect.rectInRect(txtLandMark.ableStations.get(0).geoRects, signLandMark.ableStations.get(0).geoRects)){
//			return true;
//		}
//		return false;
//	}

	
//	/**
//	 * 比较优先级
//	 * @param landMarkType1
//	 * @param landMarkType2
//	 */
//	public static void comparePri(LandMarkType landMarkType1, LandMarkType landMarkType2, int scaleNo){
//			if(landMarkType1.pri < landMarkType2.pri){
//				landMarkType1.bvs[scaleNo] = true;
//			}else if(landMarkType1.pri > landMarkType2.pri){
//				landMarkType2.bvs[scaleNo] = true;
//			}else{
//				landMarkType1.bvs[scaleNo] = true;
//			}
//	}
}

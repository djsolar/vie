package com.mansion.tele.business.landmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.mansion.tele.db.bean.elemnet.LandMark;
import com.mansion.tele.db.factory.TeleHbSessionFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SpecialLandMark {
	

	XStream xstream = new XStream(new DomDriver());
	final int LANDMARKPRI = 112;
	
	Map<Integer, Map<String, DistrictDispInfo>> specialStyle = new HashMap<Integer, Map<String,DistrictDispInfo>>();
	List<String> landmarkid = new ArrayList<String>();
	
	
	private void makeLandMarkString(){
		Session session = TeleHbSessionFactory.getOrgOtherSession()
				.getSession();
		List<LandMark> landmarks = session.createCriteria(LandMark.class).list();
		for (LandMark landMark : landmarks) {
			landmarkid.add(landMark.getPoiId());
		}
		Collections.sort(landmarkid);
	}

	boolean isShowByLandMark(String strshpid){
		if("".equals(strshpid) || strshpid == null){
			return false;
		}
		int index = Collections.binarySearch(landmarkid, strshpid);
		if(index > 0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 加载行政区划名称显示信息
	 * @return boolean 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private boolean loadDistrictDispInfo(){
			xstream.alias("DistrictDispInfo", com.mansion.tele.business.landmark.DistrictDispInfo.class);
			List<DistrictDispInfo> list = (List<DistrictDispInfo>)xstream.fromXML(DistrictDispInfo.class.getResourceAsStream("DistrictDispInfo.xml"));
			for (int i = 0; i < list.size(); i++) {
				DistrictDispInfo dispInfo = list.get(i);
				if(specialStyle.containsKey(dispInfo.getLevel())){
					Map<String, DistrictDispInfo> specnamemap = specialStyle.get(dispInfo.getLevel());
					specnamemap.put(dispInfo.getDistrictName(), dispInfo);
				}else{
					Map<String, DistrictDispInfo> specnamemap = new HashMap<String, DistrictDispInfo>();
					specnamemap.put(dispInfo.getDistrictName(), dispInfo);
					specialStyle.put(dispInfo.getLevel(), specnamemap);
				}
			}
		return true;
	}
	
	public void initSpectial(){
		this.loadDistrictDispInfo();
		this.makeLandMarkString();
	}
	
	/**
	 * 处理位置
	 * @param landMarkType
	 * @param level
	 */
	public void handleStation(MarkPoint landMarkType, int level){
		if(this.specialStyle.containsKey(level)){
			DistrictDispInfo dispInfo = this.specialStyle.get(level).get(landMarkType.name);
			if(dispInfo != null && dispInfo.getiCoorX() != 0 && dispInfo.getiCoorY() != 0){
				landMarkType.x = dispInfo.getiCoorX();
				landMarkType.y = dispInfo.getiCoorY();
			}
		}
	}
	
	public void handleName(MarkPoint landMarkType, int level){
		
	}
	
	public void handlePri(MarkPoint landMarkType, int level){
		
	}
	
	public void handleShowType(MarkPoint landMarkType , int level){
		
	}
	
	public boolean handleShowScale(MarkPoint landMarkType, int level){
		return true;
	}
	
	public boolean judgeThisMarkIsShow(MarkPoint landMarkType , int level, int scaleNo){
		if(this.specialStyle.containsKey(level)){
			DistrictDispInfo dispInfo = this.specialStyle.get(level).get(landMarkType.name);
			if(dispInfo != null && (dispInfo.isbIconFlag() || dispInfo.isbTxtFlag()) && dispInfo.isMustShow()
					&& scaleNo >= dispInfo.getiBeginVS() && scaleNo <= dispInfo.getiEndVS()){
				landMarkType.bvs[scaleNo] = true;
				landMarkType.indexivs[scaleNo] = true;
				return true;
			}
		}
		return false;
	}
	/**
	 * 处理矩形框
	 * @param landMarkType
	 * @param level
	 * @param scaleNo
	 */
	public void handleTypeSetRect(MarkPoint landMarkType, int level, int scaleNo, int lonToMe, int latToMe){
		if(this.specialStyle.containsKey(level)){
			DistrictDispInfo dispInfo = this.specialStyle.get(level).get(landMarkType.name);
			if(dispInfo != null && dispInfo.isbIconFlag() && dispInfo.isbTxtFlag()){
				landMarkType.createRectBySignByStation(level, scaleNo, dispInfo.getiTxtStation(), lonToMe, latToMe);
			}
		}
//		else if(landMarkType.isLandMark){
//			landMarkType.bvs = new boolean[]{true,true,true};
//			landMarkType.createRectBySignByStation(level, scaleNo, 1, lonToMe, latToMe);
//		}
	}
	
	public boolean handleShowLevel(MarkPoint landMarkType){
		return false;
	}
	
	public boolean isShowByTxtAndCode(MarkPoint landMarkType){
		if(2010105 == landMarkType.telecode && landMarkType.name.contains("街道")){
			return true;
		}
		return false;
	}

}

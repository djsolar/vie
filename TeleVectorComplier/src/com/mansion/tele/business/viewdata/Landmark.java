package com.mansion.tele.business.viewdata;

import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.common.BaseShape;
import com.mansion.tele.business.common.MarkInfor;
import com.mansion.tele.business.landmark.MarkPoint;
//import com.mansion.tele.business.landmark.LineMarkLand;
//import com.mansion.tele.business.landmark.SignLandMark;
//import com.mansion.tele.business.landmark.TxtLandMark;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

public class Landmark {
	
	public ShpPoint station;
	public int layerNo=200;
	public long code;
	public int ivs;
	public String txtname;
	public int iPri;
	public BlockNo blockNo;
	public int iCoorcount;
	public List<ShpPoint> list;
	public int markTxtStation;
	public boolean txtoricon;
	
	public BaseShape getBaseShape(){
		MarkInfor info = new MarkInfor();
		if (this.ivs != 0) {
			info.setbType((byte) 0);
		} else {
			info.setbType((byte) 1);
		}
		info.setlTextKindCode(this.code);
		info.setiIconVS(this.ivs);
		info.setiTextVS(this.ivs);
		info.setStrString(this.txtname);
		if(list == null || list.size() == 0){
			List<ShpPoint> markStation = new ArrayList<ShpPoint>();
			markStation.add(this.station);
			info.setCoordinate(markStation);
			info.setbCoorCnt((byte) 0);
		}else{
			List<ShpPoint> stations = new ArrayList<ShpPoint>();
			stations.add(station);
			stations.addAll(list);
			info.setCoordinate(stations);
			info.setbCoorCnt((byte) list.size());
		}
		info.setiPri(this.iPri);
		info.setLetterMark((int) this.code);
		info.setFlag(1);
		if(this.txtoricon){
			info.setiIconVS(0);
		}else{
			
		if(this.markTxtStation == 0){
			info.setiTextVS(0);
		}
		info.setLetterMarkAdvices(this.markTxtStation);
		if(this.markTxtStation == 5){
			info.setLetterMarkAdvices(0);
		}
		}
		return info;
	}

}

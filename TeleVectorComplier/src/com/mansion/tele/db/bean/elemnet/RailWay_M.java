package com.mansion.tele.db.bean.elemnet;

import com.mansion.tele.business.Task;
import com.mansion.tele.util.NumberUtil;


/**
 * 对应中间库M的铁路表
 * @author hefeng
 *
 */
@SuppressWarnings("serial")
public class RailWay_M extends RailWay{


	public String getStrName() {
		return strName;
	}

	public void setStrName(String strName) {
		this.strName = strName;
	}

	/**  正式名*/
	private String strName;
	

	
	public RailWay_M() {
		super();
	}


	public void convert(RailWay_M railWay) {
		this.strID = railWay.strID;
		this.iRailWayType = railWay.iRailWayType;
		this.setiBlockX(railWay.getiBlockX());
		this.setiBlockY(railWay.getiBlockY());
		this.strName = railWay.strName;
	}
	public void copyobjec1(RailWay_M railWay){
		this.strName = railWay.strName;
		this.copyobjec(railWay);		
	}
	
	@Override
	public void convert(RailWay railWay) {
		super.convert(railWay);
		
		if(railWay.getAstRailWayNames() != null){
			
		for (BaseName baseName : railWay.getAstRailWayNames()) {
			
			if(baseName.getBylantype() == NumberUtil.BASENAMELANETYPE_1 
					&& baseName.getBynametype() == NumberUtil.BASENAMENAMETYPE_1){
				
				this.strName = baseName.getStrnametext().trim();
				
			}
			
		}
		}
	}

	@Override
	public int hashCode() {
		int result = 1;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RailWay_M other = (RailWay_M) obj;
		if (this.strID != other.strID)
			return false;
		return true;
	}

}

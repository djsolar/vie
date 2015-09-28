package com.mansion.tele.business.background;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.db.bean.elemnet.ShpPoint;

public class Edge implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7486276235126013832L;
	Type type;//Visable:可见边；Invisable：内部边；
	public List<ShpPoint> coordinate = new ArrayList<ShpPoint>();
	public static enum Type{
		Visable,
		Invisable;//该边是由于各种分割产生，不需要描画
	}
}

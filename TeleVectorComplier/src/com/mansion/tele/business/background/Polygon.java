package com.mansion.tele.business.background;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.background.Background.Type;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.LayerNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
/**
 * Basic object,use for build background.
 * @author zhangj
 *
 */

public class Polygon implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4000289899060942139L;
	public BlockNo blockNo;
	Type type;
	public List<Edge> edges = new ArrayList<Edge>(2);
	String name;
	String id;
}

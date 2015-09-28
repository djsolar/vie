package com.sunmap.shpdata.tools.daoimpl.rt;

import java.util.HashMap;
import java.util.Map;

import com.sunmap.shpdata.tools.constvalue.DataType;
import com.sunmap.shpdata.tools.dao.ISHPDataExpand;

public class RtShpDataExpandImpl implements ISHPDataExpand {

	/**
	 * rtœÚMYMAPID¥Ê»ÎMAPID
	 */
	@Override
	public Map<String, Object> dealwith(Object type, Map<String, Object> EleRec){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("MYMAPID", EleRec.get("MAPID").toString());
		return map;
	}

}

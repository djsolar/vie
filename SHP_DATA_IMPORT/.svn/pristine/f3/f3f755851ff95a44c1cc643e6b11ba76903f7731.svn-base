package com.sunmap.shpdata.tools.daoimpl.sw;

import java.util.HashMap;
import java.util.Map;

import com.sunmap.shpdata.tools.dao.ISHPDataExpand;
import com.sunmap.shpdata.tools.util.Util;

/**
 * sw数据处理
 * 
 * @author shanbq
 * 
 */
public class SwSHPDataExpand implements ISHPDataExpand {

	public Map<String, Object> dealwith(Object type, Map<String, Object> EleRec) {
		// if (type == SwEleType.FName){
		// try {
		// String MESHID = ((String)EleRec.get("FEATID")).substring(0,4);
		// EleRec.put("MYMAPID", MESHID);
		// } catch (Exception e) {
		// System.err.println("the featid is error");
		// }
		//			
		// }
		// if (type == SwEleType.R_Name) {
		// try {
		// String MESHID = ((String)EleRec.get("ROUTEID")).substring(0,4);
		// EleRec.put("MYMAPID", MESHID);
		// } catch (Exception e) {
		// System.err.println("the ROUTEID is error");
		// }
		//			
		// }
		if (type.equals("") || type.equals("%")) {
			String sString = "";
			Map<String, Object> map = new HashMap<String, Object>();
			if (EleRec.get("MESHID") != null) {
				if (EleRec.get("MESHID").toString().length() >= 4) {
					sString = EleRec.get("MESHID").toString().substring(0, 4);
				} else {
					sString = EleRec.get("MESHID").toString();
				}
			} else if (EleRec.get("MapID") != null) {
				if (EleRec.get("MapID").toString().length() >= 4) {
					sString = EleRec.get("MapID").toString().substring(0, 4);
				} else {
					sString = EleRec.get("MapID").toString();
				}
			} else if (EleRec.get("MAPID") != null) {
				if (EleRec.get("MAPID").toString().length() >= 4) {
					sString = EleRec.get("MapID".toUpperCase()).toString()
							.substring(0, 4);
				} else {
					sString = EleRec.get("MapID".toUpperCase()).toString();
				}
			}
			if (sString.endsWith("%")) {
				// int i = sString.indexOf("%");
				sString = sString.substring(0, sString.length() - 1);
			}
			map.put("MYMAPID", sString);
			return map;
		} else {
			if (Util.parentFiledirFlag == 1) {
				if (type.toString().endsWith("%")) {

					EleRec.put("MYMAPID", type.toString().substring(0,
							type.toString().length() - 1));
				} else {
					EleRec.put("MYMAPID", type.toString());
				}
			} else {

				if (EleRec.get("MESHID") == null
						&& EleRec.get("MapID".toUpperCase()) == null
						&& EleRec.get("MapID") == null) {
					if (type.toString().endsWith("%")) {
						// int i = sString.indexOf("%");
						type = type.toString().substring(0,
								type.toString().length() - 1);
					}
					EleRec.put("MYMAPID", type.toString());
				} else {
					String ss = "";
					if (EleRec.get("MESHID") != null) {
						if (EleRec.get("MESHID").toString().length() >= 4) {
							ss = EleRec.get("MESHID").toString()
									.substring(0, 4);
						} else {
							ss = EleRec.get("MESHID").toString();
						}
					} else if (EleRec.get("MAPID") != null) {
						if (EleRec.get("MAPID").toString().length() >= 4) {
							ss = EleRec.get("MapID".toUpperCase()).toString()
									.substring(0, 4);
						} else {
							ss = EleRec.get("MapID".toUpperCase()).toString();
						}
					} else if (EleRec.get("MapID") != null) {
						if (EleRec.get("MapID").toString().length() >= 4) {
							ss = EleRec.get("MapID").toString().substring(0, 4);
						} else {
							ss = EleRec.get("MapID").toString();
						}
					}
					if (type.toString().endsWith("%")) {

						EleRec.put("MYMAPID", type.toString() + ss);
					} else {

						EleRec.put("MYMAPID", type.toString() + "%" + ss);
					}
				}
			}
		}
		// if(type.equals("")){
		// EleRec.put("MYMAPID", "");
		// }

		return EleRec;
	}

}

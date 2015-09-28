package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.mansion.tele.business.common.TeleConfig;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.util.NumberUtil;

import bo.ViewUnitNo;
/**
 * 工具方法
 * @author wenxc
 *
 */
public class MethodUtil {
	

	/**
	 * 构造方法
	 */
	private MethodUtil() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 判断是否制作0层
	 * @param viewMap Map<Integer, List<ViewUnitNo>>
	 */
	public static void  saveLevelMap(Map<Integer, List<ViewUnitNo>> viewMap) {

		String strZero = ReadFile.readProFileZero();

		if (strZero.equals("1")) {

			for (int i = 0; i <= NumberUtil.LEVEL_7; i++) {

				List<ViewUnitNo> viewSet = new ArrayList<ViewUnitNo>();

				viewMap.put(i, viewSet);

			}

		}

		if (strZero.equals("2")) {

			for (int i = 1; i <= NumberUtil.LEVEL_7; i++) {

				ArrayList<ViewUnitNo> viewSet = new ArrayList<ViewUnitNo>();

				viewMap.put(i, viewSet);

			}

		}

	}

}

package com.mansion.tele.business.background;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import com.mansion.tele.db.bean.elemnet.PolylineShp;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
/**
 * 背景线的合并，来源（原始代码中铁路线的合并）
 * @author zhangj
 *
 */
public class MergeLine {
	/**
	 * 合并省级区界线
	 */
	public List<Polyline> combire(List<Polyline> result) {
		Map<ShpPoint, List<Polyline>> map = new HashMap<ShpPoint, List<Polyline>>();
		Polyline Polyline = new Polyline();
		List<Polyline> result1 = new ArrayList<Polyline>();
		List<Polyline> list = new ArrayList<Polyline>();
		for (int i = 0; i < result.size(); i++) {
			Polyline = result.get(i);
				list.add(Polyline);
				map = this.extract(Polyline, map);
		}
		result1 = this.connectionvalue(map, Polyline);
		for (Polyline railway : list) {
			result.remove(railway);
		}
		for (Polyline railway : result1) {
			result.add(railway);
		}
		return result;
	}

	/**
	 * 提取key值和value值（坐标点和Polyline的list集合）
	 */
	private Map<ShpPoint, List<Polyline>> extract(Polyline polyline, Map<ShpPoint, List<Polyline>> map) {

		ShpPoint key1 = polyline.getHeadPoint();
		ShpPoint key2 = polyline.getTailPoint();
		// 判读key是否存在参数
		boolean cz1 = true;
		boolean cz2 = true;
		Set entries = map.entrySet();
		// 判读map是否为空
		if (!entries.isEmpty()) {
			// 比较key值在map中是否存在
			if (map.containsKey(key1)) {
				map.get(key1).add(polyline);
				cz1 = false;
			}
			if (map.containsKey(key2)) {
				map.get(key2).add(polyline);
				cz2 = false;
			}
			if (cz1) {
				List<Polyline> list = new ArrayList<Polyline>();
				list.add(polyline);
				map.put(key1, list);
			}
			if (cz2) {
				List<Polyline> list = new ArrayList<Polyline>();
				list.add(polyline);
				map.put(key2, list);
			}
		} else {
			this.assignment(map, key1, key2, polyline);
		}
		return map;
	}

	/**
	 * 起点到终点
	 */
	private List<Polyline> connectionvalue(Map<ShpPoint, List<Polyline>> map, Polyline polyline) {
		for (ShpPoint shpPoint : map.keySet()) {
			try {

				if (map.get(shpPoint).size() == 2) {
					Polyline r1 = map.get(shpPoint).get(0);
					Polyline r2 = map.get(shpPoint).get(1);
					Polyline polyline2 = this.hebinlist(map.get(shpPoint), shpPoint);
					ShpPoint shpPoint1 = polyline2.getHeadPoint();
					ShpPoint shpPoint2 = polyline2.getTailPoint();
					List<Polyline> rs1 = map.get(shpPoint1);
					List<Polyline> rs2 = map.get(shpPoint2);
					rs1.remove(r1);
					rs1.remove(r2);
					rs2.remove(r1);
					rs2.remove(r2);
					rs1.add(polyline2);
					rs2.add(polyline2);
					map.get(shpPoint).remove(r1);
					map.get(shpPoint).remove(r2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Set<Polyline> set = new HashSet<Polyline>();
		for (Entry<ShpPoint, List<Polyline>> b : map.entrySet()) {
			List<Polyline> rlista = b.getValue();
			if (rlista != null) {
				set.addAll(rlista);
			}
		}
		List<Polyline> list = new ArrayList<Polyline>();
		list.addAll(set);
		return list;
	}

	/**
	 * 往map里赋key值和value值
	 */
	private Map<ShpPoint, List<Polyline>> assignment(Map<ShpPoint, List<Polyline>> map, ShpPoint key1, ShpPoint key2, Polyline Polyline) {
		List<Polyline> list = new ArrayList<Polyline>();
		List<Polyline> list1 = new ArrayList<Polyline>();
		list.add(Polyline);
		list1.add(Polyline);
		map.put(key1, list);
		map.put(key2, list1);
		return map;
	}

	/**
	 * 合并两个list集合
	 */

	private Polyline hebinlist(List<Polyline> value, ShpPoint key) {
		Polyline rm = new Polyline();
		rm.copy(value.get(0));
		List<ShpPoint> list1 = value.get(0).edges.get(0).coordinate;
		List<ShpPoint> list2 = value.get(1).edges.get(0).coordinate;
//		PolylineShp ps1 = value.get(0);
//		PolylineShp ps2 = value.get(1);
		PolylineShp newps = new PolylineShp();
		List<ShpPoint> slist = new ArrayList<ShpPoint>();
		if (list1.get(0).equals(key) && list2.get(0).equals(key)) {
			Collections.reverse(list1);
			slist.addAll(list1);
			slist.addAll(list2);
		} else if (list1.get(list1.size() - 1).equals(key) && list2.get(list2.size() - 1).equals(key)) {
			Collections.reverse(list2);
			slist.addAll(list1);
			slist.addAll(list2);
		} else if (list1.get(list1.size() - 1).equals(key) && list2.get(0).equals(key)) {
			slist.addAll(list1);
			slist.addAll(list2);
		} else if (list2.get(list2.size() - 1).equals(key) && list1.get(0).equals(key)) {
			slist.addAll(list2);
			slist.addAll(list1);
		}
		// 对矩形框坐标进行比较
		// 左下比较x取小，
		int lbx = 0;
		int lby = 0;
		int rtx = 0;
		int rty = 0;
		// 保存集合
		newps.setCoordinate(slist);
		newps.setLbCoordinate(new ShpPoint(lbx, lby));
		newps.setRtCoordinate(new ShpPoint(rtx, rty));
		Edge edge = new Edge();
		edge.coordinate = slist;
		rm.edges.add(edge);
		return rm;
	}
}

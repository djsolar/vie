package com.mansion.tele.business.landmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import com.mansion.tele.business.Task;
import com.mansion.tele.common.ConstantValue;
import com.mansion.tele.db.bean.elemnet.BaseName;
import com.mansion.tele.db.bean.elemnet.BusLine;
import com.mansion.tele.db.bean.elemnet.BusStop;
import com.mansion.tele.db.bean.elemnet.MapSign;
import com.mansion.tele.db.bean.elemnet.RailWayStop;
import com.mansion.tele.db.bean.elemnet.Service;
import com.mansion.tele.db.bean.elemnet.SubwayEntrance;
import com.mansion.tele.db.daoImpl.TeleDao;
import com.mansion.tele.db.factory.TeleHbSessionFactory;
import com.mansion.tele.util.NumberUtil;

public class LandMarkLoader {
	
	static Comparator<BusStop> stopComparator = new Comparator<BusStop>() {

		@Override
		public int compare(BusStop stop1, BusStop stop2) {
			return stop1.getNamec().compareTo(stop2.getNamec());
		}
	};
	
	static Comparator<String> lineComparator = new Comparator<String>() {

		@Override
		public int compare(String str1, String str2) {
			return str1.compareTo(str2);
		}
	};
	
	/**
	 * 公交数据加载转换
	 * @param task
	 * @param meshNos
	 * @return
	 */
	public List<MarkPoint> loadBusAndTransform(Task task, List<String> meshNos){
		Session session = TeleHbSessionFactory.getOrgOtherSession().getSession();
		List<MarkPoint> landMarkTypeList = new ArrayList<MarkPoint>();
		List<BusStop> busStopsList = new ArrayList<BusStop>();
		for (String strMeshNo : meshNos) {
			List<BusStop> meshBusStoplist = TeleDao.getStopList(session, strMeshNo, BusStop.class);
			for (int i = 0; i < meshBusStoplist.size(); i++) {
				BusStop busStop = meshBusStoplist.get(i);
					List<BusLine> buslineByStop = Landmark.style.getBuslineByid(busStop.getBstopid());
					busStop.setBusStopLineInfo(this.getStopLineName(buslineByStop));
			}
			busStopsList.addAll(meshBusStoplist);
		}
		busStopsList = this.delRepeatStop(busStopsList);
		for (int i = 0; i < busStopsList.size(); i++) {
			// 只抽取需要显示的文数据
			if (!Landmark.style.isShowByLevel(Landmark.style.getTeleCodeByPOI(
					Long.parseLong(busStopsList.get(i).getType())), (int)task.getLevel())) {

				continue;
			}
			// 组合公交站点和路线名称
			String strBusStopNameAndBusLineName = this
					.assembleStopNameAndLineName(
							busStopsList.get(i).getNamec(), busStopsList.get(i)
									.getBusStopLineInfo());
			// Mark坐标转int
			String strCoorString = String
					.format(busStopsList.get(i).getGeom1());
			String[] acStrCoorStrings = strCoorString
					.split(SignUtil.SPACE_STRING);
			double dX = Double.parseDouble(acStrCoorStrings[0]);
			double dY = Double.parseDouble(acStrCoorStrings[1]);

			int iX = (int) (dX * ConstantValue.DEGREETOSECOND);
			int iY = (int) (dY * ConstantValue.DEGREETOSECOND);
//			Style.SignStyle signStyle = Landmark.style.getStyle(Integer.parseInt(busStopsList.get(i).getType()), task.getbLevel(), 0);
//				LandMarkType signLandMark = new LandMarkType(busStopsList
//						.get(i).getSmid() , Integer.parseInt(busStopsList
//								.get(i).getType()), signStyle.pri,
//						strBusStopNameAndBusLineName, iX, iY, signStyle.font, signStyle.xSpace, signStyle.ySpace, signStyle.display);
//				if (!signLandMark.ifNUll()) {
//					continue;
//				}
			MarkPoint landMarkType = new MarkPoint(Landmark.style.getTeleCodeByPOI(Long.parseLong(busStopsList.get(i).getType())), strBusStopNameAndBusLineName,
					busStopsList.get(i).getSmid(), iX, iY, "");
				landMarkTypeList.add(landMarkType);
		}
		return landMarkTypeList;
	}
	
	/**
	 * 一个任务内同名的公交数据
	 * 只保留最佳的一个
	 * @param busstops
	 */
	private List<BusStop> delRepeatStop(List<BusStop> busstops){
		Collections.sort(busstops, stopComparator);
		List<BusStop> result = new ArrayList<BusStop>();
		List<BusStop> repeat = new ArrayList<BusStop>();
		for(int i=0;i<busstops.size()-1;i++){
			BusStop busstop1 = busstops.get(i);
			BusStop busstop2 = busstops.get(i+1);
			if(repeat.isEmpty()){
				repeat.add(busstop1);
			}
			if(busstop1.getNamec().equals(busstop2.getNamec())){
				repeat.add(busstop2);
			}
			else if(i == busstops.size()-2){
				result.add(busstop2);
			}
			else{
				BusStop bestOne = this.chooseBestOne(repeat);
				result.add(bestOne);
				repeat.clear();
			}
			busstop1 = busstops.get(i);
		}
		return result;
	} 
	
	/**
	 * 选择最佳的站点，规则为：
	 * 此站点距离其他站点距离和最近
	 * @param repeat
	 * @return
	 */
	private BusStop chooseBestOne(List<BusStop> repeat){
		int index = 0;
		if(repeat.size() == 1){
			return repeat.get(0);
		}
		else{
			double len = 0;
			// 取出到其他同名车站点距离和最短的点
			for (int j = 0; j < repeat.size(); j++) {
				double length = 0;
				for (int i = 0; i < repeat.size(); i++) {
					Double[] coordinateJ = getLonAndLat(repeat.get(j).getGeom1());
					Double[] coordinateI = getLonAndLat(repeat.get(i).getGeom1());
					length += Math.sqrt(Math.pow((coordinateJ[0] - coordinateI[0]), 2.0) +
										Math.pow((coordinateJ[1] - coordinateI[1]), 2.0));
				}
				if (j == 0) {
					len = length;
					index = j;
				} else {
					if (len > length) {
						len = length;
						index = j;
					}
				}
			}
			Set<String> lines = new HashSet<String>();
			String busStopLineInfo = "";
			for(int i=0;i<repeat.size();i++){
				String busStopLines[] = repeat.get(i).getBusStopLineInfo().split(",");
				for(int j=0;j<busStopLines.length;j++){
					lines.add(busStopLines[j]);
				}
			}
			List<String> result = new ArrayList<String>();
			result.addAll(lines);
			Collections.sort(result, lineComparator);
			for(String strline : result){
				busStopLineInfo += strline+",";
			}
			busStopLineInfo = busStopLineInfo.substring(0,busStopLineInfo.length()-1);
			repeat.get(index).setBusStopLineInfo(busStopLineInfo);
		}
		return repeat.get(index);
	}
	
	/**
	 * 组合经纬度
	 * @param strLonAndLat	经纬度字符串
	 * @return	返回组合后的经纬度
	 */
	private static Double[] getLonAndLat(String strLonAndLat) {
		String[] lonAndLat = strLonAndLat.split(SignUtil.SPACE_STRING);
		Double[] lonandlat = new Double[lonAndLat.length];
		for (int i = 0; i < lonAndLat.length; i++) {
			lonandlat[i] = Double.valueOf(lonAndLat[i]) * ConstantValue.DEGREETOSECOND;
		}
		return lonandlat;
	}
	
	/**
	 * 组合公交站点名称和路线名称
	 * 
	 * @param strBusStopName
	 *            公交站点名称
	 * @param strBusStopLineInfo
	 *            途径公交站的公交线路名称
	 * @return 返回组合后的公交站点名称和路线名称
	 */
	private String assembleStopNameAndLineName(String strBusStopName,
			String strBusStopLineInfo) {

		String[] busLineNameList = strBusStopLineInfo
				.split(SignUtil.COMMA_STRING);
		// 公交站点名称和路线名称的组合文字个数在32个以内
		String busLineNameTemp = strBusStopName + SignUtil.LEFTBRACKER_STRING
				+ busLineNameList[0];
		String busLineName = strBusStopName + SignUtil.LEFTBRACKER_STRING
				+ busLineNameList[0];
		for (int j = 1; j < busLineNameList.length; j++) {
			busLineNameTemp = busLineNameTemp
					+ (SignUtil.COMMA_STRING + busLineNameList[j]);
			if (ConstantValue.BUSSTOPNAMEMAXLEN < busLineNameTemp.length()) {
				busLineName += "等";
				break;
			}
			busLineName = busLineName
					+ (SignUtil.COMMA_STRING + busLineNameList[j]);
		}
		busLineName += ")";

		return busLineName;
	}
	
	/**
	 * 取得路线名称字符串
	 * 
	 * @param stopLineNameList
	 *            公交站点名称集合
	 * @return 返回取得的路线名称字符串
	 */
	public String getStopLineName(List<BusLine> stopLineNameList) {
		Map<String, String> lineNames = new HashMap<String, String>();
		List<String> stopLineNames = new ArrayList<String>();
		for (BusLine busline : stopLineNameList) {
			String name = busline.getNamec();
			String[] names = name.split("\\(");
			if (names.length == 1) {
				names = name.split(SignUtil.SBCLEFTBRACKER_STRING);
			}

			if (lineNames.get(names[0]) == null) {
				lineNames.put(names[0], names[0]);
				stopLineNames.add(names[0]);
			}
		}
		Collections.sort(stopLineNames);

		// 将路线名称组合成字符串
		String strStopLineNames = "";
		for (int i = 0; i < stopLineNames.size(); i++) {
			strStopLineNames += stopLineNames.get(i) + SignUtil.COMMA_STRING;
		}
		strStopLineNames = strStopLineNames.substring(0,
				strStopLineNames.length() - 1);
		return strStopLineNames;
	}
	/**
	 * 读取0层mark数据
	 * 
	 * @param task
	 *            当前任务
	 * @return 返回0层mark数据
	 */
	public List<MarkPoint> loadLevel0Mark(Task task, List<String> astTaskNos){
		Session session = TeleHbSessionFactory
				.getOrgHbSession(task.getLevel()).getSession();
		List<MarkPoint> landMarkTypeList = new ArrayList<MarkPoint>();
		List<Service> serviceList = new ArrayList<Service>();
		for (String taskNo : astTaskNos) {
			serviceList.addAll(TeleDao.getServiceList(taskNo, session));
		}
		// 组合SignLandMarkr和SignCodeChange SignCodeFirst信息
		if (serviceList == null || serviceList.size() == 0) {
			return landMarkTypeList;
		}
		// 获取父MARK集合
		Map<String, MarkPoint> fatherMap = this.getFather(task, serviceList);
		for (int i = 0; i < serviceList.size(); i++) {
			Service service = serviceList.get(i);
			if(service.getiRTServiceTypeOrg() == null){
				continue;
			}
			// 加载SignCodeChange与SignCodeFirst信息
//			Style.SignStyle signStyle = Landmark.style.getStyle(Landmark.style.getTeleCodeByPOI(
//					Long.parseLong(service.getiRTServiceTypeOrg())), task.getbLevel(), 0);
//			// 只抽取需要显示的文数据
//			if (signStyle == null) {
//				continue;
//			}
			if (!Landmark.style.isShowByLevel(Landmark.style.getTeleCodeByPOI(
					Long.parseLong(service.getiRTServiceTypeOrg())), (int)task.getLevel())) {
				continue;
			}
			if (fatherMap.get(serviceList.get(i).getStrid()) != null) {
				landMarkTypeList.add(fatherMap.get(serviceList.get(i).getStrid()));
				continue;
			}
			// 按比例尺获取Id，parentId，Ivs,Kindcode,Orgcode,Name,X坐标，Y坐标
				String strname = getName(service);
				if(strname == null){
					continue;
				}else{
//					 signLandMark = new SignLandMark(serviceList.get(i)
//							.getStrid(), getParent(serviceList.get(i)
//							.getStrParentServiceID(), fatherMap),Integer.parseInt(serviceList.get(i).getiRTServiceTypeOrg()), signStyle.pri,
//							strname, serviceList.get(i).getStGeom().getCoordinate().x, serviceList
//									.get(i).getStGeom().getCoordinate().y, signStyle.font, signStyle.xSpace, signStyle.ySpace, signStyle.display);
					if(service.getStrSWServiceType() != null && !service.getStrSWServiceType().contains(";")
							&& !service.getStrSWServiceType().trim().isEmpty()
							&& Landmark.style.getBrandByCode(Integer.parseInt(service.getStrSWServiceType())) != null){
						MarkPoint landMarkType = new MarkPoint(
								Landmark.style.getTeleCodeByPOI(service.getiRTServiceType()), strname, 
								service.getStrid(), service.getStGeom().getCoordinate().x, 
								service.getStGeom().getCoordinate().y, getParent(serviceList.get(i)
										.getStrParentServiceID(), fatherMap),Landmark.style.getBrandByCode(Integer.parseInt(service.getStrSWServiceType())), service.getStrshpid());
						landMarkType.isLandMark = Landmark.specialLandMark.isShowByLandMark(landMarkType.strshpid);
						landMarkTypeList.add(landMarkType);
					}else{
						MarkPoint landMarkType = new MarkPoint(
								Landmark.style.getTeleCodeByPOI(service.getiRTServiceType()), strname, 
								service.getStrid(), service.getStGeom().getCoordinate().x, 
								service.getStGeom().getCoordinate().y, getParent(serviceList.get(i)
										.getStrParentServiceID(), fatherMap), service.getStrshpid());
						landMarkType.isLandMark = Landmark.specialLandMark.isShowByLandMark(landMarkType.strshpid);
						landMarkTypeList.add(landMarkType);
					}
				}
		}
		return landMarkTypeList;
	}

	/**
	 * 制作父对象
	 * 
	 *             异常处理
	 */
	private Map<String, MarkPoint> getFather(Task task, List<Service> service){
		Set<String> set = new HashSet<String>();
		Map<String, MarkPoint> FatherSign = new HashMap<String, MarkPoint>();
		for (int i = 0; i < service.size(); i++) {
			if (service.get(i).getStrParentServiceID() != null) {
				set.add(service.get(i).getStrParentServiceID());
			}
		}
		for (int j = 0; j < service.size(); j++) {
			for (String str : set) {
				if (str.equals(service.get(j).getStrid())) {
					
					if(service.get(j).getiRTServiceTypeOrg() == null){
						continue;
					}
					
					// 加载SignCodeChange与SignCodeFirst信息
					Style.SignStyle signStyle = Landmark.style.getStyle(
							Landmark.style.getTeleCodeByPOI(
									Long.parseLong(service.get(j).getiRTServiceTypeOrg())), (int)task.getLevel(), 0);
					
					if (signStyle == null) {
						continue;
					}
					if (!Landmark.style.isShowByLevel(Integer.parseInt(service.get(j).getiRTServiceTypeOrg()), task.getLevel())) {
						continue;
					}
						// 获取strid,ivsid,Ivs,Kindcode,Orgcode,Name,X坐标，Y坐标
//						SignLandMark signLandMark = new SignLandMark(service
//								.get(j).getStrid(), 
//								Integer.parseInt(service.get(j)
//										.getiRTServiceTypeOrg()), signStyle.pri,
//								getMarkName(service.get(j)), service.get(j)
//										.getStGeom().getCoordinate().x, service
//										.get(j).getStGeom().getCoordinate().y,
//										signStyle.font, signStyle.xSpace, signStyle.ySpace, signStyle.display);
					String name =  getMarkName(service.get(j));
					if(name.trim().isEmpty()){
						continue;
					}
					if(service.get(j).getStrSWServiceType() != null && !service.get(j).getStrSWServiceType().contains(";")
							&& !service.get(j).getStrSWServiceType().trim().isEmpty()
								&& Landmark.style.getBrandByCode(Integer.parseInt(service.get(j).getStrSWServiceType())) != null){
						MarkPoint landMarkType = new MarkPoint(Landmark.style.getTeleCodeByPOI(
								Integer.parseInt(service.get(j).getiRTServiceTypeOrg())),name,
								service.get(j).getStrid(), service.get(j).getStGeom().getCoordinate().x, service.get(j).getStGeom().getCoordinate().y
								,Landmark.style.getBrandByCode(Integer.parseInt(service.get(j).getStrSWServiceType())), service.get(j).getStrshpid());	
						landMarkType.isLandMark = Landmark.specialLandMark.isShowByLandMark(landMarkType.strshpid);
						FatherSign.put(service.get(j).getStrid(),landMarkType);
					}else{
						MarkPoint landMarkType = new MarkPoint(Landmark.style.getTeleCodeByPOI(
								Integer.parseInt(service.get(j).getiRTServiceTypeOrg())), name,
								service.get(j).getStrid(), service.get(j).getStGeom().getCoordinate().x, service.get(j).getStGeom().getCoordinate().y, service.get(j).getStrshpid());	
						landMarkType.isLandMark = Landmark.specialLandMark.isShowByLandMark(landMarkType.strshpid);
						FatherSign.put(service.get(j).getStrid(),landMarkType);
					}
				}
				}

		}

		return FatherSign;
	}

	/**
	 * 获取父对象
	 * 
	 */
	private MarkPoint getParent(String parentServiceID,
			Map<String, MarkPoint> fatherMap) {
		if (null == parentServiceID) {
			return null;
		} 
		return fatherMap.get(parentServiceID);

	}

	/**
	 * 忽略名称个数大于能够显示的文字的最大数
	 * 
	 * @param service
	 *            标记信息
	 * @return 返回标记名
	 */
	public String getName(Service service) {
		// 如果POI名称中有空格，则拾取文件中拾取的名称要保留空格。显示的名称将空格去掉
		String strMarkNameString = this.getMarkName(service).replace(
				SignUtil.SBCSPACE_STRING, SignUtil.EMPTY_STRING);
		if(strMarkNameString.trim().isEmpty()){
			return null;
		}
		// 忽略名称个数大于能够显示的文字的最大数
		if (service.getiRTServiceType() != 2110201
				&& NumberUtil.TXT_DISP_MOST_CNT < strMarkNameString.length()) {
			return null;
		}
		if (service.getiRTServiceType() == 2110201
				&& NumberUtil.TXT_DISP_MOST_CNT < strMarkNameString.length()) {
			// 银行分类
			strMarkNameString = Landmark.style.getStrName(service.getStrSWServiceType());
			if ("其他".equals(strMarkNameString)) {
				return null;
			}
		}
		return strMarkNameString;
	}
	/**
	 * 取得标记名称
	 * 
	 * @param service
	 *            标记信息
	 * @return 返回标记名
	 */
	public String getMarkName(Service service) {

		String strMarkNameString = new String();

		if (1 == service.getAstServNames().size()) {
			strMarkNameString = service.getAstServNames().get(0)
					.getStrnametext();
		} else {
			int iIndexNameC = -1; // 全名
			int iIndexNameS = -1; // 别名
			for (int j = 0; j < service.getAstServNames().size(); j++) {
				if (1 == service.getAstServNames().get(j).getBylantype()
						&& 1 == service.getAstServNames().get(j)
								.getBynametype()) {

					iIndexNameC = j;
				}
				if (1 == service.getAstServNames().get(j).getBylantype()
						&& 2 == service.getAstServNames().get(j)
								.getBynametype()) {

					iIndexNameS = j;
				}
			}
			if (-1 != iIndexNameS) {

				// 别名
				strMarkNameString = service.getAstServNames().get(iIndexNameS)
						.getStrnametext();
			} else if(iIndexNameC != -1){

				// 全名
				strMarkNameString = service.getAstServNames().get(iIndexNameC)
						.getStrnametext();
			}
		}
		return strMarkNameString;
	}
	
	/**
	 * 加载地铁出入口数据
	 * 
	 * @param task
	 *            当前任务
	 * @return 地铁出入口数据
	 */
	public List<MarkPoint> loadSubwayEntranceInfor(Task task, List<String> strTaskNos){
		Session session = TeleHbSessionFactory
				.getOrgOtherSession().getSession();
		List<MarkPoint> landMarkTypeList = new ArrayList<MarkPoint>();
		List<SubwayEntrance> entrancesList = new ArrayList<SubwayEntrance>();
		// 加载地铁出入口数据
		for (String strTaskNo : strTaskNos) {
			entrancesList.addAll(TeleDao.getStopList(session, strTaskNo, SubwayEntrance.class));
		}
		// 组合SignLandMarkr和SignCodeChange SignCodeFirst信息
		if (null != entrancesList && 0 < entrancesList.size()) {
			for (int i = 0; i < entrancesList.size(); i++) {
				
				// 加载SignCodeChange与SignCodeFirst信息
				// 只抽取需要显示的文数据
				if (!Landmark.style.isShowByLevel(
						Landmark.style.getTeleCodeByPOI(Long.parseLong(entrancesList.get(i).getType())), task.getLevel())) {
					continue;
				}
				// Mark坐标转int
				String strCoorString = String.format(entrancesList.get(i)
						.getGeom1());
				String[] acStrCoorStrings = strCoorString.split(" ");
				double dX = Double.parseDouble(acStrCoorStrings[0]);
				double dY = Double.parseDouble(acStrCoorStrings[1]);
				int iX = (int) (dX * ConstantValue.DEGREETOSECOND);
				int iY = (int) (dY * ConstantValue.DEGREETOSECOND);
//				Style.SignStyle signStyle = Landmark.style.getStyle(Integer.parseInt(entrancesList.get(i).getType()), task.getbLevel(), 0);
				// 按比例尺获取strid,ivsid,Ivs,Kindcode,Orgcode,Name,X坐标，Y坐标
//					SignLandMark signLandMark = new SignLandMark(entrancesList
//							.get(i).getSmid(), Integer.parseInt(entrancesList.get(i).getType()), signStyle.pri,
//							entrancesList.get(i).getNamec(), iX, iY, signStyle.font, signStyle.xSpace, signStyle.ySpace, signStyle.display);
				MarkPoint landMarkType = new MarkPoint(
						Landmark.style.getTeleCodeByPOI(Integer.parseInt(entrancesList.get(i).getType()))
						, entrancesList.get(i).getNamec(), entrancesList.get(i).getSmid(), iX, iY, "");	
//				if (!signLandMark.ifNUll()) {
//						continue;
//					}
					landMarkTypeList.add(landMarkType);
			}
		}
		return landMarkTypeList;
	}
	/**
	 * 加载地铁站点数据
	 * 
	 * @param task
	 *            当前任务
	 * @return 返回加载的地铁站点数据
	 */
	public List<MarkPoint> loadRailWayStopInfor(Task task, List<String> strTaskNos){
		Session session = TeleHbSessionFactory
							.getOrgOtherSession().getSession();
		List<MarkPoint> landMarkTypeList = new ArrayList<MarkPoint>();
		List<RailWayStop> railWayStopsList = new ArrayList<RailWayStop>();
		for (String strTaskNo : strTaskNos) {
			railWayStopsList.addAll(TeleDao.getStopList(session, strTaskNo, RailWayStop.class));
		}
		// 加载城铁站点数据
		// 组合SignLandMarkr和SignCodeChange SignCodeFirst信息
		if (null != railWayStopsList && 0 < railWayStopsList.size()) {

			for (int i = 0; i < railWayStopsList.size(); i++) {

				// 加载SignCodeChange与SignCodeFirst信息
				if (!Landmark.style.isShowByLevel(
						Landmark.style.getTeleCodeByPOI(Long.parseLong(railWayStopsList.get(i).getType())), task.getLevel())) {
					continue;
				}
				// Mark坐标转int
				String strCoorString = String.format(railWayStopsList.get(i)
						.getGeom1());
				String[] acStrCoorStrings = strCoorString.split(" ");
				double dX = Double.parseDouble(acStrCoorStrings[0]);
				double dY = Double.parseDouble(acStrCoorStrings[1]);

				int iX = (int) (dX * ConstantValue.DEGREETOSECOND);
				int iY = (int) (dY * ConstantValue.DEGREETOSECOND);
//				Style.SignStyle signStyle = Landmark.style.getStyle(Integer.parseInt(railWayStopsList.get(i).getType()), task.getbLevel(), 0);
//					SignLandMark signLandMark = new SignLandMark(
//							railWayStopsList.get(i).getSmid(), Integer.parseInt(railWayStopsList.get(i).getType()), signStyle.pri,
//							railWayStopsList.get(i).getNamec(), iX, iY, signStyle.font, signStyle.xSpace, signStyle.ySpace, signStyle.display);
//					if (!signLandMark.ifNUll()) {
//						continue;
//					}
				MarkPoint landMarkType = new MarkPoint(
						Landmark.style.getTeleCodeByPOI(Integer.parseInt(railWayStopsList.get(i).getType()))
						, railWayStopsList.get(i).getNamec(), railWayStopsList.get(i).getSmid(), iX, iY, "");	
					landMarkTypeList.add(landMarkType);
			}
		}
		return landMarkTypeList;
	}
	
	/**
	 * 加载文字数据
	 * 
	 * @param task
	 *            当前任务
	 * @return 返回加载的文字数据
	 */
	public List<MarkPoint> loadmapSign(Task task, List<String> strTaskNos){
		Session session = TeleHbSessionFactory
				.getOrgHbSession(task.getLevel()).getSession();
		List<MarkPoint> landMarkTypeList = new ArrayList<MarkPoint>();
		List<MapSign> mapSignList = new ArrayList<MapSign>();
		// 加载文字数据
		for (String taskNo : strTaskNos) {
			mapSignList.addAll(TeleDao.getMapSign(taskNo, session));
		}
		if (mapSignList.size() == 0) {
			return landMarkTypeList;
		}
		// 组合TxtLandMarkr和SignCodeChange SignCodeFirst信息
		if (null != mapSignList && 0 < mapSignList.size()) {
			for (int i = 0; i < mapSignList.size(); i++) {
				// 加载SignCodeChange与SignCodeFirst信息
				boolean flag = true;// 不是特殊岛屿

				for (BaseName bn : mapSignList.get(i).getAstMapSignNames()) {
					// 只抽取需要显示的文字,台湾周边普通岛屿留下
					if (task.getLevel() < 6
							&& (bn.getStrnametext().equalsIgnoreCase("澎湖列岛")
									|| bn.getStrnametext().equals("兰屿")
									|| bn.getStrnametext().equals("绿岛（火烧岛）") || bn
									.getStrnametext().equals("彭佳屿"))) {
						System.err.println("台湾周边岛屿");
						flag = false;
					}
				}
				
				if(flag){
					MarkPoint landMarkType = new MarkPoint(Landmark.style.getTeleCodeByPOI(
							mapSignList.get(i).getImapsigntype()), getMapSignName(mapSignList.get(i)),
							mapSignList.get(i).getStrid(), mapSignList.get(i).getStGeom().getCoordinate().x,
							mapSignList.get(i).getStGeom().getCoordinate().y, "");
					landMarkTypeList.add(landMarkType);
				}
				
//				if (flag && (!Landmark.style.isShowByLevel(mapSignList.get(i).getImapsigntype(), task.getbLevel()))) {
//					continue;
//				}
//				Style.SignStyle signStyle = Landmark.style.getStyle(mapSignList.get(i).getImapsigntype(), task.getbLevel(), 0);
//				if(signStyle == null){
//					System.out.println("?????????");
//				}
//				if(signStyle.display == Style.DisPlay.isTxtLandMark.ordinal()){
//					TxtLandMark TxtLandMark = new TxtLandMark(mapSignList
//							.get(i).getStrid(), mapSignList.get(i)
//							.getImapsigntype(),getMapSignName(mapSignList.get(i))
//							, signStyle.pri, mapSignList.get(i).getStGeom().getCoordinate().x,
//							mapSignList.get(i).getStGeom().getCoordinate().y, signStyle.font, signStyle.xSpace, signStyle.ySpace);
//					landMarkTypeList.add(TxtLandMark);
//				}else{
//					SignLandMark signLandMark = new SignLandMark(mapSignList
//							.get(i).getStrid(),mapSignList.get(i).getImapsigntype(), signStyle.pri,
//							getMapSignName(mapSignList.get(i)), mapSignList.get(i)
//									.getStGeom().getCoordinate().x, mapSignList
//									.get(i).getStGeom().getCoordinate().y,
//									signStyle.font, signStyle.xSpace, signStyle.ySpace, signStyle.display);
//					landMarkTypeList.add(signLandMark);
//				}
			}
		}
		return landMarkTypeList;
	}

	/**
	 * 取得MapSign的文字
	 * 
	 * @param txtInfor
	 *            MapSign信息
	 * @return 返回MapSign的名称
	 */
	private String getMapSignName(MapSign txtInfor) {
		String strMapsignNameString = new String();

		int iIndexNameC = -1; // 全名
		int iIndexNameS = -1; // 别名
		for (int j = 0; j < txtInfor.getAstMapSignNames().size(); j++) {
			if (1 == txtInfor.getAstMapSignNames().get(j).getBylantype()
					&& 1 == txtInfor.getAstMapSignNames().get(j)
							.getBynametype()) {

				iIndexNameC = j;
			}
			if (1 == txtInfor.getAstMapSignNames().get(j).getBylantype()
					&& 2 == txtInfor.getAstMapSignNames().get(j)
							.getBynametype()) {

				iIndexNameS = j;
			}
		}

		if (-1 != iIndexNameS
				&& ConstantValue.ORGCODEOFPROVINCIAL != txtInfor
						.getImapsigntype()) {

			// 别名
			strMapsignNameString = txtInfor.getAstMapSignNames()
					.get(iIndexNameS).getStrnametext();
		} else {

			// 全名
			strMapsignNameString = txtInfor.getAstMapSignNames()
					.get(iIndexNameC).getStrnametext();
		}

		return strMapsignNameString;
	}
}

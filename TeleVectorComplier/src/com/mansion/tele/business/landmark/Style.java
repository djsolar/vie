package com.mansion.tele.business.landmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.mansion.tele.business.network.RoadNew.defineNRC;
import com.mansion.tele.db.bean.elemnet.BusLine;
import com.mansion.tele.db.bean.elemnet.BusStopL;
import com.mansion.tele.db.daoImpl.TeleDao;
import com.mansion.tele.db.factory.TeleHbSessionFactory;
import com.mansion.tele.util.NumberUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 定义：orgCode：母库的Code；TeleCode：系统排版等操作用的code；userCode：产品中的code
 * TeleCode应该和userCode是相同的，但目前不一致，//TODO 等到格式重新定义时修改过来。
 * 
 * @author wxc
 * 
 */
public class Style {
	List<SignCodeChange> signCodeChangeList = null;// 母库code和Telecode转换表
	List<SignCodeFirst> signCodeFirstList = null;// 扩展的母库code和式样对应表
	List<Object> roadNameChangeList = null;// 道路名称和扩展母库code转换表
	List<ShowStyle> showStyleList = null;// 文字大小
	Map<String, List<BusLine>> stopToBusline = new HashMap<String, List<BusLine>>();// 公交线路
	Map<String, String> SWServiceTypeMap = new HashMap<String, String>();// 品牌名
	Map<Integer, Brand> codeToBrand = new HashMap<Integer, Style.Brand>();
	int xSpace, ySpace;
	int typeSetRoadLevel = 1; // 文字排版与道路作比较的层级
	List<ShowStyle> roadStyleList = new ArrayList<ShowStyle>();
	
	
	static Comparator<SignCodeChange> signCodeChangeComparator = new Comparator<SignCodeChange>() {

		@Override
		public int compare(SignCodeChange o1, SignCodeChange o2) {
			return (int) (o1.orgCode - o2.orgCode);
		}
	};

	static Comparator<SignCodeFirst> signCodeFirstComparator = new Comparator<SignCodeFirst>() {

		@Override
		public int compare(SignCodeFirst o1, SignCodeFirst o2) {
			return (int) (o1.teleCode - o2.teleCode);
		}
	};

	static Comparator<ShowStyle> showStyleComparator = new Comparator<ShowStyle>() {
		@Override
		public int compare(ShowStyle o1, ShowStyle o2) {
			return (o1.level + ";" + o1.lMarkType).compareTo(o2.level + ";"
					+ o2.lMarkType);
		}
	};
	
	@SuppressWarnings("unchecked")
	void init() {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("SignCodeChange", SignCodeChange.class);
		xstream.alias("SignCodeFirst", SignCodeFirst.class);
		xstream.alias("ShowStyle", ShowStyle.class);
		signCodeChangeList = (List<SignCodeChange>) xstream
				.fromXML(SignCodeChange.class
						.getResourceAsStream("SignCodeChange.xml"));
		signCodeFirstList = (List<SignCodeFirst>) xstream
				.fromXML(SignCodeFirst.class
						.getResourceAsStream("SignCodeFirst.xml"));
		showStyleList = (List<ShowStyle>) xstream.fromXML(ShowStyle.class
				.getResourceAsStream("ShowStyle.xml"));
//		this.getTerminalStyle();
		this.makeStopToBusline();
		if (SWServiceTypeMap.isEmpty()) {
			xstream = new XStream(new DomDriver());
			xstream.alias("POIBankName", POIBankName.class);
			List<POIBankName> poiNameList = (List<POIBankName>) xstream
					.fromXML(POIBankName.class
							.getResourceAsStream("POIBankName.xml"));
			for (POIBankName poiBankName : poiNameList) {
				SWServiceTypeMap.put(poiBankName.getBankid(),
						poiBankName.getBankname());
			}
		}
		for (SignCodeFirst signCodeFirst : signCodeFirstList) {
			if(signCodeFirst.brandName != null){
					signCodeFirst.brand = Brand.valueOf(signCodeFirst.brandName);
					codeToBrand.put(signCodeFirst.brandCode, signCodeFirst.brand);
			}
		}
		Collections.sort(signCodeChangeList, signCodeChangeComparator);
		Collections.sort(signCodeFirstList, signCodeFirstComparator);
		Collections.sort(showStyleList, showStyleComparator);
	}
	
	/**
	 * 根据终端配置文件得到显示式样
	 */
	void getTerminalStyle(){
		if(showStyleList == null){
			showStyleList = new ArrayList<ShowStyle>();
		}
		InputStream is = null;
		BufferedReader br = null;
		String line = "";
		try {
			is = ShowStyle.class.getResourceAsStream("point.txt");
			br = new BufferedReader(new InputStreamReader(is));
			br.readLine();
			while((line = br.readLine())!= null){
				String[] info = line.split("\\s");
				List<Integer> levels = this.getLevels(info[11]);					
				long lMarkType = Long.parseLong(info[0].replace("0x", ""), 16);
				double font = Double.parseDouble(info[1]);
				double xSpace = Double.parseDouble(info[2]);					
				double ySpace = xSpace;					
				byte display = this.getDisplay(info[8], info[9], font, lMarkType);					
				double letterSpacing = Double.parseDouble(info[3]);
				for(int i=0;i<levels.size();i++){
					int level = levels.get(i);
					ShowStyle style = new ShowStyle();
					style.level = level;
					style.lMarkType = lMarkType;
					style.font = font;
					style.xSpace = xSpace;
					style.ySpace = ySpace;
					style.display = display;
					style.letterSpacing = letterSpacing;
					showStyleList.add(style);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				br.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 获得式样的层号
	 * @param scales
	 * @return
	 */
	List<Integer> getLevels(String scales){
		List<Integer> levels = new ArrayList<Integer>();
		if(scales.contains("0x0001") || scales.contains("0x0002") || scales.contains("0x0003")){
			levels.add(0);
		}
		if(scales.contains("0x0004") || scales.contains("0x0005")){
			levels.add(1);
		}
		if(scales.contains("0x0006") || scales.contains("0x0007")){
			levels.add(2);
		}
		if(scales.contains("0x0008") || scales.contains("0x0009")){
			levels.add(3);
		}
		if(scales.contains("0x000a") || scales.contains("0x000b")){
			levels.add(4);
		}
		if(scales.contains("0x000c") || scales.contains("0x000d")){
			levels.add(5);
		}
		if(scales.contains("0x000e") || scales.contains("0x000f")){
			levels.add(6);
		}
		if(scales.contains("0x0010")){
			levels.add(7);
		}
		return levels;
	}
	
	/**
	 * 获得显示方式
	 * @param backtextureid 是否显示icon
	 * @param backbmpflag 是否显示文字
	 * @param font
	 * @param lMarkType TODO
	 * @return -1:此层没有该类型数据;0:只显示icon;1：只显示文字;2：中心显示;3：外面显示 ;4：线性显示
	 */
	byte getDisplay(String backtextureid, String backbmpflag, double font, long lMarkType){
		if((lMarkType>=0 && lMarkType<9) || lMarkType==10){
			return 4;
		}
		if(!"-1".equals(backtextureid) && font == 0){
			return 0;
		}
		// backbmpflag = 1时icon可拉伸，认为是文字类型
		if("-1".equals(backtextureid) || "1".equals(backbmpflag)){
			return 1;
		}
		if(!"-1".equals(backtextureid) && "2".equals(backbmpflag)){
			return 2;
		}
		if(!"-1".equals(backtextureid) && "0".equals(backbmpflag)){
			return 3;
		}
		return -1;
	}

	/**
	 * 获得telecode
	 * 
	 * @param orgCode
	 *            ：母库Code
	 * @return ：在格式转换时使用
	 */
	int getTeleCodeByPOI(long orgCode) {
		// SignCodeChange signCodeChange = new SignCodeChange();
		// signCodeChange.orgCode = orgCode;
		// int index = Collections.binarySearch(this.signCodeChangeList,
		// signCodeChange, signCodeChangeComparator);
		// if(index >= 0){
		// return this.signCodeChangeList.get(index).kind;
		// }
		// return -1;
		return (int) orgCode;
	}

	
	
	/**
	 * 获得存储到格式中的usercode
	 * 
	 * @param teleCode
	 * @return : 在格式转换时使用
	 */
	int getUserCode(int teleCode) {
		SignCodeChange signCodeChange = new SignCodeChange();
		signCodeChange.orgCode = teleCode;
		int index = Collections.binarySearch(this.signCodeChangeList,
				signCodeChange, signCodeChangeComparator);
		if (index >= 0) {
			return this.signCodeChangeList.get(index).kind;
		}
		return -1;
	}

	/**
	 * 获得式样
	 * 
	 * @param teleCode
	 *            ：teleCode
	 * @return：式样对象，返回null表示当前层不显示
	 */
	SignStyle getStyle(int teleCode, int level, int scale) {
		SignCodeFirst signCodeFirst = new SignCodeFirst();
		signCodeFirst.teleCode = teleCode;
		SignStyle signStyle = new SignStyle();
		int signcodeindex = Collections.binarySearch(this.signCodeFirstList,
				signCodeFirst, signCodeFirstComparator);
		// modify by yuxy
		if (signcodeindex >= 0) {
			signCodeFirst = this.signCodeFirstList.get(signcodeindex);
			if (signCodeFirst.beginLevel > level || signCodeFirst.endLevel < level) {
				return null;
			} 
			else if ((signCodeFirst.beginScaleNo > scale && signCodeFirst.beginLevel == level)
					|| (signCodeFirst.endScaleNo < scale && signCodeFirst.endLevel == level)) {
				return null;
			}
			signStyle.pri = (int) signCodeFirst.pri;
		} 
		else {
			return null;
		}
		return getStyle(teleCode, level, signStyle);
	}

	/**
	 * 获得式样
	 * @param teleCode
	 * @param level
	 * @param signStyle
	 * @return
	 */
	SignStyle getStyle(int teleCode, int level, SignStyle signStyle) {
		ShowStyle showStyle = new ShowStyle();
		int usercode = this.getUserCode(teleCode);
		showStyle.lMarkType = usercode;
		showStyle.level = level;
		int showstyleindex = Collections.binarySearch(this.showStyleList,
				showStyle, showStyleComparator);
		if (showstyleindex >= 0) {
			showStyle = this.showStyleList.get(showstyleindex);
			signStyle.font = showStyle.font;
			signStyle.xSpace = showStyle.xSpace;
			signStyle.ySpace = showStyle.ySpace;
			signStyle.displayValue = showStyle.display;
			signStyle.letterSpacing = showStyle.letterSpacing;
			switch (showStyle.display) {
			case 0:
				signStyle.display = DisPlay.notShowTxt;
				break;
			case 1:
				signStyle.display = DisPlay.isTxtLandMark;
				break;
			case 2:
				signStyle.display = DisPlay.centerShow;
				break;
			case 3:
				signStyle.display = DisPlay.otherShow;
				break;
			case 4:
				signStyle.display = DisPlay.lineShow;
				break;
			default:
				return null;
			}

		} 
		else {
			return null;
		}
		return signStyle;
	}

	/**
	 * 获取道路名和编号式样
	 */
	void getRoadStyle(){
		for(ShowStyle showStyle : showStyleList){
			long styleCode = showStyle.lMarkType;
			if((styleCode >= defineNRC.HightWay && styleCode <= defineNRC.Ferry)
			|| styleCode == NumberUtil.HightWayNo || styleCode == NumberUtil.NationalRoadNo 
			|| styleCode == NumberUtil.ProcinceRoadNo){
				roadStyleList.add(showStyle);
			}
		}
		Collections.sort(roadStyleList, showStyleComparator);
	}
	
	/**
	 * 类型数据可升层等级
	 * 
	 * @param telecode
	 * @param level
	 * @return
	 */
	boolean isShowByLevel(int teleCode, int level) {
		SignCodeFirst signCodeFirst = new SignCodeFirst();
		signCodeFirst.teleCode = teleCode;
		int index = Collections.binarySearch(this.signCodeFirstList,
				signCodeFirst, signCodeFirstComparator);
		if (index >= 0) {
			signCodeFirst = this.signCodeFirstList.get(index);
			if (signCodeFirst.endLevel >= level) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 类型数据显示等级
	 * 
	 * @param telecode
	 * @param level
	 * @return
	 */
	boolean isShowLevel(int teleCode, int level) {
		SignCodeFirst signCodeFirst = new SignCodeFirst();
		signCodeFirst.teleCode = teleCode;
		int index = Collections.binarySearch(this.signCodeFirstList,
				signCodeFirst, signCodeFirstComparator);
		if (index >= 0) {
			signCodeFirst = this.signCodeFirstList.get(index);
			if (signCodeFirst.endLevel >= level
					&& signCodeFirst.beginLevel <= level) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 根据teleCode判断是否为填充层级
	 * @param teleCode
	 * @param level
	 * @return
	 */
	boolean isFillLevel(int teleCode, int level){
		SignCodeFirst signCodeFirst = new SignCodeFirst();
		signCodeFirst.teleCode = teleCode;
		int index = Collections.binarySearch(this.signCodeFirstList,
				signCodeFirst, signCodeFirstComparator);
		if (index >= 0) {
			signCodeFirst = this.signCodeFirstList.get(index);
			if (signCodeFirst.fillEDLevel >= level
					&& signCodeFirst.fillBGLevel <= level) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	
	}

	/**
	 * 类型数据此层的显示比例尺
	 * 
	 * @param teleCode
	 * @param level
	 * @param scale
	 * @return
	 */
	public boolean isShowByLevelAndScale(int teleCode, int level, int scale) {
		SignCodeFirst signCodeFirst = new SignCodeFirst();
		signCodeFirst.teleCode = teleCode;
		int index = Collections.binarySearch(this.signCodeFirstList,
				signCodeFirst, signCodeFirstComparator);
		// modify by yuxy
		if (index >= 0) {
			signCodeFirst = this.signCodeFirstList.get(index);
			if (signCodeFirst.beginLevel < level && signCodeFirst.endLevel > level) {
				return true;
			}
			else if(signCodeFirst.beginLevel == level && signCodeFirst.beginScaleNo <= scale){
				return true;
			}
			else if(signCodeFirst.endLevel == level && signCodeFirst.endScaleNo >= scale){
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}

	public class SignStyle {
		public double font;// 毫米,表示文字和icon宽高
		public byte displayValue;// 0:不显示文字；1：只显示文字；2：文字在icon中心显示；3：文字在icon外面显示；4：线性文字
		public double xSpace;// 水平方向的间隔；mm
		public double ySpace;// 垂直方向的间隔;mm
		public int pri;// 优先级
		public DisPlay display;
		public double letterSpacing;
	}
	
	enum DisPlay {
		notShowTxt, isTxtLandMark, centerShow, otherShow, lineShow
	}

	static Comparator<BusLine> buslineComparator = new Comparator<BusLine>() {

		@Override
		public int compare(BusLine o1, BusLine o2) {
			// TODO Auto-generated method stub
			return o1.getBlineid().compareTo(o2.getBlineid());
		}

	};

	/**
	 * 通过id获得公交名
	 * 
	 * @param stopid
	 * @return
	 */
	List<BusLine> getBuslineByid(String stopid) {
		List<BusLine> buslineByStop = stopToBusline.get(stopid);
		return buslineByStop;
	}

	private void makeStopToBusline() {
		Session session = TeleHbSessionFactory.getOrgOtherSession()
				.getSession();
		TeleDao teleDao = new TeleDao();
		List<BusLine> buslinelist = (List<BusLine>) teleDao
				.getAllPlusDataObject(BusLine.class, session);
		List<BusStopL> busStopllist = (List<BusStopL>) teleDao
				.getAllPlusDataObject(BusStopL.class, session);
		Collections.sort(buslinelist, buslineComparator);
		for (int i = 0; i < busStopllist.size(); i++) {
			BusStopL busStopL = busStopllist.get(i);
			if (!stopToBusline.containsKey(String.valueOf(busStopL.getBstopid()))) {
				List<BusLine> busline = new ArrayList<BusLine>();
				BusLine example = new BusLine();
				example.setBlineid(String.valueOf(busStopL.getBlineid()));
				int buslineindex = Collections.binarySearch(buslinelist,
						example, buslineComparator);
				if (buslineindex >= 0) {
					BusLine buslinebyid = buslinelist.get(buslineindex);
					busline.add(buslinebyid);
					stopToBusline.put(String.valueOf(busStopL.getBstopid()),
							busline);
				}
			} else {
				List<BusLine> busline = stopToBusline
						.get(String.valueOf(busStopL.getBstopid()));
				BusLine example = new BusLine();
				example.setBlineid(String.valueOf(busStopL.getBlineid()));
				int buslineindex = Collections.binarySearch(buslinelist,
						example, buslineComparator);
				if (buslineindex >= 0) {
					BusLine buslinebyid = buslinelist.get(buslineindex);
					busline.add(buslinebyid);
					stopToBusline.put(String.valueOf(busStopL.getBstopid()),
							busline);
				}
			}
		}
	}
	
	/**
	 * 银行名称转换
	 * 
	 * @param service
	 *            标记信息
	 * @return 返回标记名
	 */
	String getStrName(int[] strSWServiceType) {
		if (strSWServiceType != null) {
			for (int i = 0; i < strSWServiceType.length; i++) {
				return SWServiceTypeMap.get(strSWServiceType[i]);
			}
		}
		return null;
	}

	/**
	 * 银行名称转换
	 * 
	 * @param service
	 *            标记信息
	 * @return 返回标记名
	 */
	String getStrName(String strSWServiceType) {
		return SWServiceTypeMap.get(strSWServiceType);
	}
	
	/**
	 * 获得品牌
	 * @param BrandCode
	 * @return
	 */
	Brand getBrandByCode(int BrandCode){
		return this.codeToBrand.get(BrandCode);
	}
	
	public enum Brand{
		二十一世纪,
		七Eleven,
		七天连锁,
		BP加油站,
		Caltex石油加油站,
		Esso石油加油站,
		GMC,
		MG,
		Mobil石油加油站,
		OK,
		Scion,
		Shell石油加油站,
		smart,
		阿尔法罗米欧,
		阿斯顿马丁,
		奥迪,
		百盛,
		百信,
		保时捷,
		宝马,
		北京奔驰,
		北京华联,
		北京克莱斯勒,
		北京农村商业银行,
		北京现代,
		北京银行,
		北汽制造,
		奔驰,
		本草,
		本田,
		比亚迪,
		必胜客,
		标致,
		别克,
		宾利,
		渤海银行,
		卜蜂莲花,
		布嘉迪,
		昌河铃木,
		昌河汽车,
		长安,
		长安福特,
		长安铃木,
		长安马自达,
		长安沃尔沃,
		长城,
		长城人寿保险,
		长丰猎豹,
		长丰三菱,
		长丰扬子,
		超市发,
		成大方圆,
		大发,
		大家乐,
		大娘水饺,
		大润发,
		大星发超市,
		大宇,
		大众,
		道奇,
		德克士,
		德威治,
		迪欧咖啡,
		迪亚天天,
		帝豪,
		电信,
		东方人寿保险,
		东风本田,
		东风标致,
		东风风神,
		东风柳汽,
		东风日产,
		东风雪铁龙,
		东风裕隆,
		东风悦达起亚,
		东骏,
		东南,
		东南道奇,
		东南克莱斯勒,
		东南三菱,
		东亚银行,
		二兴益,
		法拉利,
		菲亚特,
		丰田,
		福迪,
		福兰德,
		福特,
		福田汽车,
		复星,
		富康,
		格林豪泰,
		光大永明人寿保险,
		广东发展银行,
		广州本田,
		广州丰田,
		国大36524,
		国家开发银行,
		国药,
		哈飞汽车,
		海马汽车,
		海王星辰,
		悍马,
		汉庭酒店,
		好德,
		好邻居,
		好伦哥,
		好又多,
		合众人寿保险,
		河北中兴,
		荷兰银行,
		恒丰银行,
		恒生银行,
		红旗,
		花旗银行,
		华晨宝马,
		华诚,
		华冠超市,
		华联,
		华润万家,
		华氏,
		华泰,
		华泰保险,
		华夏银行,
		黄海,
		汇丰银行,
		霍顿,
		吉奥汽车,
		吉利,
		吉普,
		吉野家,
		嘉事堂,
		家乐福,
		江淮汽车,
		江铃陆风,
		江南汽车,
		交通银行,
		捷豹,
		金杯,
		金狮100超市化宾馆,
		金象,
		锦江之星,
		京客隆,
		开瑞,
		凯迪拉克,
		柯尼赛格,
		可的,
		克莱斯勒,
		肯德基,
		快客,
		兰博基尼,
		蓝旗亚,
		劳斯莱斯,
		老百姓,
		老家肉饼,
		乐购,
		乐杰士,
		雷克萨斯,
		雷诺,
		雷允上,
		丽华快餐,
		力帆,
		联华,
		联通,
		莲花,
		莲花汽车,
		良友金伴,
		两岸咖啡,
		林肯,
		凌志,
		铃木,
		路虎,
		罗森,
		玛莎拉蒂,
		马兰拉面,
		马自达,
		迈巴赫,
		麦当劳,
		麦德龙,
		美国加州牛肉面大王,
		美国友邦保险,
		美宜佳,
		迷你,
		面点王,
		民生人寿保险,
		名典咖啡,
		莫泰168,
		莫泰268,
		南京菲亚特,
		南汽新雅途,
		农工商,
		欧宝,
		欧尚,
		讴歌,
		帕加尼,
		旁蒂克,
		七斗星商旅酒店,
		其他,
		其他城市商业银行,
		其他农村商业银行,
		其它,
		奇瑞,
		起亚,
		千家伴,
		庆丰包子铺,
		屈臣氏,
		全家,
		全球鹰,
		全顺,
		全新,
		人人乐,
		日产,
		荣威,
		如家快捷,
		瑞麒,
		瑞穗实业银行,
		萨博,
		三菱,
		三菱东京日联银行,
		上岛咖啡,
		上海大众,
		上海大众斯柯达,
		上海华联,
		上海华普,
		上海浦东发展银行,
		上海通用别克,
		上海通用凯迪拉克,
		上海通用雪佛兰,
		上海药房,
		上海银行,
		上海英伦,
		上汽通用五菱雪佛兰,
		深圳发展银行,
		生命人寿保险,
		世纪家家福,
		世爵,
		双环汽车,
		双龙,
		顺天府,
		斯巴鲁,
		斯柯达,
		苏果,
		速8连锁,
		塔塔,
		太极,
		太平洋保险,
		泰康人寿保险,
		天诚,
		天津一汽,
		天客隆,
		天乐医药,
		天马汽车,
		铁通,
		通用,
		同济堂,
		同仁堂,
		桐君阁,
		万好万家,
		网通,
		威兹曼,
		威麟,
		为波旅馆,
		卫通,
		味千拉面,
		沃尔玛,
		沃尔沃,
		五菱,
		物美,
		西雅特,
		喜士多,
		夏利,
		现代,
		小白羊超市,
		新华人寿保险,
		新世纪商城加盟便利,
		新亚大包,
		新一佳,
		新宇之星,
		欣燕都,
		鑫绿都便利店,
		信诚人寿,
		兴业银行,
		星巴克咖啡,
		星展银行,
		雪佛兰,
		雪铁龙,
		雅阁,
		雅悦酒店,
		阳光保险,
		养和堂,
		一汽奥迪,
		一汽奔腾,
		一汽大众,
		一汽丰田,
		一汽华利,
		一汽吉林,
		一汽马自达,
		一汽自由风,
		宜必思,
		移动,
		易购,
		驿居酒店,
		英菲尼迪,
		永和大王,
		永和豆浆,
		永源飞碟,
		友邦保险,
		约泰酒店,
		渣打银行,
		招商银行,
		浙商银行,
		真功夫,
		郑州日产,
		中德安联人寿保险,
		中国工商银行,
		中国光大银行,
		中国海洋石油加油站,
		中国建设银行,
		中国进出口银行,
		中国民生银行,
		中国农业发展银行,
		中国农业银行,
		中国平安保险,
		中国平安人寿保险,
		中国人民保险,
		中国人民银行,
		中国人寿保险,
		中国石化加油站,
		中国石油加油站,
		中国太平,
		中国银行,
		中国邮政储蓄银行,
		中宏人寿,
		中华,
		中江之旅,
		中联,
		中美大都会人寿保险,
		中信银行,
		中兴,
		中意人寿,
		中银保险,
		中英人寿保险,
		中州快捷,
		众泰,
		众泰汽车,
		重庆力帆
	}

	// /**
	// * 标记原始code比较器
	// */
	// private static Comparator<SignCodeOrder> compareOrgCode = new
	// Comparator<SignCodeOrder>() {
	//
	// @Override
	// public int compare(SignCodeOrder arg0, SignCodeOrder arg1) {
	//
	// return arg0.getOrgCode().compareTo(arg1.getOrgCode());
	// }
	// };

	// @SuppressWarnings("unchecked")
	// public static SignCodeChange getSignCodeChange(long orgCode) {
	// if (signCodeChangeList == null) {
	// xstream.alias("SignCodeChange", SignCodeChange.class);
	// signCodeChangeList = (List<SignCodeChange>) xstream
	// .fromXML(SignCodeChange.class
	// .getResourceAsStream("SignCodeChange.xml"));
	// Collections.sort(signCodeChangeList, compareOrgCode);
	// }
	// SignCodeOrder signCode = new SignCodeOrder();
	// signCode.setOrgCode(orgCode);
	// int index = Collections.binarySearch(signCodeChangeList, signCode,
	// compareOrgCode);
	// if (index < 0) {
	// return null;
	// }
	// SignCodeChange signCodeChange = (SignCodeChange) signCodeChangeList
	// .get(index);
	// return signCodeChange;
	// }
	//
	// @SuppressWarnings("unchecked")
	// public static SignCodeFirst getSignCodeFirst(long orgCode) {
	// if (signCodeFirstList == null) {
	// xstream.alias("SignCodeFirst", SignCodeFirst.class);
	// signCodeFirstList = (List<SignCodeFirst>) xstream
	// .fromXML(SignCodeFirst.class
	// .getResourceAsStream("SignCodeFirst.xml"));
	// Collections.sort(signCodeFirstList, compareOrgCode);
	// }
	// SignCodeOrder signCode = new SignCodeOrder();
	// signCode.setOrgCode(orgCode);
	// int index = Collections.binarySearch(signCodeFirstList, signCode,
	// compareOrgCode);
	// if (index < 0) {
	// return null;
	// }
	// SignCodeFirst signCodeFrist = (SignCodeFirst) signCodeFirstList
	// .get(index);
	// return signCodeFrist;
	// }
	//
	// public static int getFirstBLevel(int orgCode) {
	// int result = 0;
	// if (scb == null) {
	// XStream a = new XStream(new DomDriver());
	// a.alias("SignCodeFirst", SignCodeFirst.class);
	// scb = (List<SignCodeFirst>) a.fromXML(SignCodeFirst.class
	// .getResourceAsStream("SignCodeFirst.xml"));
	// }
	// for (SignCodeFirst signCodeFirst : scb) {
	// if (signCodeFirst.getOrgCode() == orgCode) {
	// result = signCodeFirst.getBeginLevel();
	// break;
	// }
	// }
	// return result;
	// }
	//
	// public static int getFirstELevel(int orgCode) {
	// int result = 0;
	// if (sce == null) {
	// XStream a = new XStream(new DomDriver());
	// a.alias("SignCodeFirst", SignCodeFirst.class);
	// sce = (List<SignCodeFirst>) a.fromXML(SignCodeFirst.class
	// .getResourceAsStream("SignCodeFirst.xml"));
	// }
	// for (SignCodeFirst signCodeFirst : sce) {
	// if (signCodeFirst.getOrgCode() == orgCode) {
	// result = signCodeFirst.getEndLevel();
	// break;
	// }
	// }
	// return result;
	// }
	// public static int getPri(int orgCode) {
	// int result = 0;
	// if (scb == null) {
	// XStream a = new XStream(new DomDriver());
	// a.alias("SignCodeFirst", SignCodeFirst.class);
	// scb = (List<SignCodeFirst>) a.fromXML(SignCodeFirst.class
	// .getResourceAsStream("SignCodeFirst.xml"));
	// }
	// for (SignCodeFirst signCodeFirst : scb) {
	// if (signCodeFirst.getOrgCode() == orgCode) {
	// result = (int) signCodeFirst.getPri();
	// break;
	// }
	// }
	// return result;
	// }

}

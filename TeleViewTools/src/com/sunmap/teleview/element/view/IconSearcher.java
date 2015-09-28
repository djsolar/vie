package com.sunmap.teleview.element.view;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sunmap.teleview.util.ByteArrayStream;


/**Icon数据管理类
 * @author lijingru
 *
 */
public class IconSearcher {
	public static Map<String, String> iconFileNames = new HashMap<String, String>();
	
private static final String RES_MAP_ICON_PATH = "ass/viewIcon/mapicon.dat";// 地图icon文件路径
	
	private static final String RES_LAYER_ICON_PATH = "ass/viewIcon/eeyeicon.dat";// 电子眼及加油站icon文件路径
	
	private static final int DATA_FMT_ZZZY = 0x5A5A5A59;
	
	@SuppressWarnings("unused")
	private static final int DATA_FMT_ZZZZ = 0x5A5A5A5A;
	
	private static final int DAY_CATEGORY = 0x0A01;
	
	private static final int NIGHT_CATEGORY = 0x0A02;
	
	private static final int LAYER_CATEGORY = 0x0B02;
	
	private static IconSearcher mInstance = null;
	
	private HashMap<Integer, Object> mMapIconMap = null;
	
	private HashMap<Integer, Object> mLayerIconMap = null;
	
	private HashMap<Integer, HashMap<Integer, Integer>> mIconOffsetMap = null;
	
	private boolean mIsDayMapIcons = false;
	
	private boolean mIsMapIconLoaded = false;
	
	private boolean mIsLayerIconLoaded = false;
	
	private IconSearcher() {
		this.mMapIconMap = new HashMap<Integer, Object>();
		this.mLayerIconMap = new HashMap<Integer, Object>();
		this.mIconOffsetMap = new HashMap<Integer, HashMap<Integer, Integer>>();
	}
	
	public static IconSearcher getInstance() {
		if (IconSearcher.mInstance == null) {
			synchronized (IconSearcher.class) {
				if (IconSearcher.mInstance == null) {
					IconSearcher.mInstance = new IconSearcher();
				}
			}
		}
		return IconSearcher.mInstance;
	}
	
	/**
	 * 获取电子眼及加油站icon
	 * @param id
	 * @return
	 */
	public Image getLayerIcon(int id) {
		Image result = null;
		try {
			int layerCode = this.convertLayerCode(id & 0xFFFF);
			if (layerCode != -1) {
				Object value = this.mLayerIconMap.get(layerCode & 0xFFFF);
				if (value != null) {
					if (value instanceof Image) {
						result = (Image) value;
					} else if (value instanceof Integer) {
						result = (Image) this.mLayerIconMap.get((Integer) value);
					}
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 获取地图icon
	 * @param id
	 * @return
	 */
	public Image getMapIcon(int id) {
		Image result = null;
		try {
			Object value = this.mMapIconMap.get(id & 0xFFFF);
			if (value != null) {
				if (value instanceof Image) {
					result = (Image) value;
				} else if (value instanceof Integer) {
					result = (Image) this.mMapIconMap.get((Integer) value);
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
	
	public boolean isMapIconLoaded() {
		return this.mIsMapIconLoaded;
	}
	
	public boolean isLayerIconLoaded() {
		return this.mIsLayerIconLoaded;
	}
	
	/**
	 * 加载icon，第一个参数为昼夜色，默认不加载电子眼及加油站icon
	 * @param context
	 * @param isDay
	 */
	public void loadIcons(boolean isDay) {
		this.loadIcons(isDay, false);
	}
	
	/**
	 * 加载icon，第一个参数为昼夜色，第二个参数为是否加载电子眼及加油站icon
	 * @param context
	 * @param isDay
	 * @param loadLayer
	 */
	public void loadIcons(boolean isDay, boolean loadLayer) {
		if (this.checkLayerIcons() && loadLayer) {
			this.loadIcons(this.mLayerIconMap, IconSearcher.RES_LAYER_ICON_PATH,
					IconSearcher.LAYER_CATEGORY);
			this.mIsLayerIconLoaded = true;
		}
		
		if (this.checkMapIcons(isDay)) {
			this.loadIcons(this.mMapIconMap, IconSearcher.RES_MAP_ICON_PATH, 
					isDay ? IconSearcher.DAY_CATEGORY : IconSearcher.NIGHT_CATEGORY);
			this.mIsMapIconLoaded = true;
		}
	}
	
	private boolean checkLayerIcons() {
		return this.mLayerIconMap.size() == 0;
	}
	
	private boolean checkMapIcons(boolean isDay) {
		boolean needUpdate = false;
		if (this.mMapIconMap.size() == 0) {
			this.mIsDayMapIcons = isDay;
			needUpdate = true;
		} else {
			if (this.mIsDayMapIcons != isDay) {
				this.mIsDayMapIcons = isDay;
				this.mMapIconMap.clear();
				needUpdate = true;
			}
		}
		return needUpdate;
	}

	private void loadIcons(HashMap<Integer, Object> iconMap, String path, int... requireCategories) {
		InputStream fileIs = null;
		try {
			fileIs = new FileInputStream(new File(path));
			if (fileIs != null) {
				int fileSize = fileIs.available();
				byte[] fileBuffer = new byte[fileSize];
				fileIs.read(fileBuffer);
				
				ByteArrayStream bas = new ByteArrayStream(fileBuffer);
				int fileFormat = bas.readHLInt();
				
				if (fileFormat == IconSearcher.DATA_FMT_ZZZY) {
					this.loadIcons(bas, iconMap, requireCategories);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileIs != null) {
				try {
					fileIs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private void loadIcons(ByteArrayStream bas, HashMap<Integer, Object> iconMap, int... requireCategories) throws IOException {

		@SuppressWarnings("unused")
		int cateInfoSize = bas.readHLInt();
		int categoryCount = bas.readHLInt();
		int categoryOffset = bas.offset();
		
		List<CategoryInfo> categoryInfos = new ArrayList<IconSearcher.CategoryInfo>(categoryCount);
		for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
			bas.position(categoryOffset);
			
			CategoryInfo info = new CategoryInfo();
			info.category = this.convertCategory(bas.readHLShort());
			info.iconCount = bas.readHLShort();
			info.iconInfoOffset = bas.readHLInt();

			categoryOffset = bas.offset();
			if (this.mIconOffsetMap.get(info.category) == null) {
				this.mIconOffsetMap.put(info.category, new HashMap<Integer, Integer>());
				bas.position(info.iconInfoOffset);
				for (int iconIndex = 0; iconIndex < info.iconCount; iconIndex++) {
					int offset = bas.offset();
					this.loadIconByCategory(bas, iconMap, info.category, false);
					bas.position(offset + 8);
				}
			}
			categoryInfos.add(info);
		}

		for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
			CategoryInfo info = categoryInfos.get(categoryIndex);
			if (this.isCategoryValid(info.category, requireCategories)) {
				bas.position(info.iconInfoOffset);
				for (int iconIndex = 0; iconIndex < info.iconCount; iconIndex++) {
					int offset = bas.offset();
					this.loadIconByCategory(bas, iconMap, info.category, true);
					bas.position(offset + 8);
				}
			}
		}
	}
	
	private boolean isCategoryValid(int category, int[] requireCategories) {
		boolean isValid = false;
		if (requireCategories != null) {
			for (int requireCategory : requireCategories) {
				if (requireCategory == category) {
					isValid = true;
					break;
				}
			}
		}
		return isValid;
	}
	
	private void loadIconByCategory(ByteArrayStream bas, HashMap<Integer, Object> iconMap, int category, boolean isLoadIcon) throws IOException {
		
		int code = bas.readHLShort();
		int flag = bas.readHLShort();

		int iconID = this.convertToIconID(category, code);

		if (this.isShareFlag(flag)) {
			if (isLoadIcon) {
				int shareCate = this.convertCategory(bas.readHLShort());
				int shareCode = bas.readHLShort();
				int shareIconID = this.convertToIconID(shareCate, shareCode);
				if (shareCate == category) {
					iconMap.put(code & 0xFFFF, shareCode & 0xFFFF);
				} else {
					HashMap<Integer, Integer> offsetMap = this.mIconOffsetMap.get(shareCate);
					if (offsetMap != null) {
						Integer iconOffset = offsetMap.get(shareIconID);
						if (iconOffset != null) {
							this.loadIcon(bas, iconMap, iconOffset, code);
						}
					}
				}
			}
		} else {
			int iconDataOffset = bas.readHLInt();
			if (isLoadIcon) {
				this.loadIcon(bas, iconMap, iconDataOffset, code);
			} else {
				HashMap<Integer, Integer> offsetMap = this.mIconOffsetMap.get(category);
				if (offsetMap != null) {
					offsetMap.put(iconID, iconDataOffset);
				}
			}
		}
	}
	
	private void loadIcon(ByteArrayStream bas, HashMap<Integer, Object> iconMap, int offset, int code) throws IOException {
		int iconCode = code & 0xFFFF;
		Image icon = loadImage(bas, offset);
		if (icon != null) {
			iconMap.put(iconCode, icon);
		}
	}

	private Image loadImage(ByteArrayStream bas, int offset) throws IOException {
		
		bas.position(offset);
		
		short width = bas.readUnsignedByte();
		short height = bas.readUnsignedByte();
		@SuppressWarnings("unused")
		short offsetX = bas.readUnsignedByte();
		@SuppressWarnings("unused")
		short offsetY = bas.readUnsignedByte();
		short bitCount = bas.readUnsignedByte();
		@SuppressWarnings("unused")
		short sepFlag = bas.readUnsignedByte();

		int patternSize = width * height * (bitCount >> 3);
		byte[] pattern = new byte[patternSize];
		bas.read(pattern);
		
		return this.decodeImage(pattern, width, height);
	}
	
	private Image decodeImage(byte[] buffer, int width, int height) {
		Image result = null;
		try {
			int[] evoPixels = new int[width * height];
			int pixelEvo = 0;
			int channelR = 0;
			int channelG = 0;
			int channelB = 0;
			int channelA = 0;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					int pxlOffset = (i * width + j) * 4;
					channelB = buffer[pxlOffset] & 0xff;
					channelG = buffer[pxlOffset + 1] & 0xff;
					channelR = buffer[pxlOffset + 2] & 0xff;
					channelA = buffer[pxlOffset + 3] & 0xff;
					pixelEvo = channelA << 24 | channelR << 16 | channelG << 8 | channelB;
					evoPixels[i * width + j] = pixelEvo;
				}
			}
			MemoryImageSource source = new MemoryImageSource(width, height, evoPixels, 0, width);
			result = Toolkit.getDefaultToolkit().createImage(source);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private boolean isShareFlag(int flag) {
		return (flag & (1 << 1)) != 0;
	}
	
	private int convertToIconID(int category, int kind) {
		return (category << 16 & 0xFFFF0000) | (kind & 0x0000FFFF);
	}
	
	private int convertCategory(int category) {
		switch(category) {
			case 0x0C00:	
			case 0x0C01:	
			case 0x0C02:	
			case 0x0C03:	
			case 0x0C04:	
			case 0x0C05:	
			case 0x0C06:	
			case 0x0C07:	
			case 0x0C08:	
			case 0x0C09:	
			case 0x0C0A:	
			case 0x0C0B:	
			case 0x0C0C:	
			case 0x0C0D:	
			case 0x0C0E:	
				return (category << 4) & 0xFFFF;
				
			default:		
				return category;
		}
	}
	
	@SuppressWarnings("unused")
	private boolean isEEye(int type) {
		boolean result = false;
		switch (type) {
			case 0x0020:
			case 0x0030:
			case 0x0040:
			case 0x0050:
			case 0x0060:
			case 0x0070:
			case 0x0080:
			case 0x0090:
			case 0x0100:
			case 0x0110:
			case 0x0120:
			case 0x0121:
			case 0x0122:
			case 0x0123:
				result = true;
				break;
	
			default:
				break;
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	private boolean isGasStation(int type) {
		boolean result = false;
		switch (type) {
			case 0xC800:
			case 0xC801:
			case 0xC802:
			case 0xC803:
			case 0xC804:
			case 0xC805:
			case 0xC807:
			case 0xC808:
			case 0xC809:
				result = true;
				break;
	
			default:
				break;
		}
		return result;
	}

	
	private int convertLayerCode(int type) {
		int result = -1;
		switch (type) {
			case 0x1001:
				result = 0x0122;
				break;
			case 0x1002:
				result = 0x0020;
				break;
			case 0x1003:
				result = 0x0030;
				break;
			case 0x1004:
				result = 0x0040;
				break;
			case 0x1005:
				result = 0x0050;
				break;
			case 0x1006:
				result = 0x0060;
				break;
			case 0x1007:
				result = 0x0070;
				break;
			case 0x1008:
				result = 0x0080;
				break;
			case 0x1009:
				result = 0x0090;
				break;
			case 0x100A:
				result = 0x0100;
				break;
			case 0x100B:
				result = 0x0110;
				break;
			case 0x100C:
				result = 0x0120;
				break;
			case 0x100D:
				result = 0x0121;
				break;
			case 0x100E:
				result = 0x0123;
				break;
				
			case 0x1010:
				result = 0xC800;
				break;
			case 0x1011:
				result = 0xC801;
				break;
			case 0x1012:
				result = 0xC802;
				break;
			case 0x1013:
				result = 0xC803;
				break;
			case 0x1014:
				result = 0xC804;
				break;
			case 0x1015:
				result = 0xC805;
				break;
			case 0x1016:
				result = 0xC807;
				break;
			case 0x1017:
				result = 0xC808;
				break;
			case 0x1018:
				result = 0xC809;
				break;
	
			default:
				return -1;
		}
		return result & 0xFFFF;
	}
	
	private class CategoryInfo {
		
		int category;
		int iconCount;
		int iconInfoOffset;
	}
	
	/**
	 * 通过类型得到类型代表的名称
	 * @param type 类型  
	 * @return
	 */
	public static  String getTypeName(short type) {
		String typeName ="";
		switch (type) {
		case	(short)	0xbf82	:	typeName	=	"	首都	"	;	break;
		case	(short)	0xbf83	:	typeName	=	"	省直辖市	"	;	break;
		case	(short)	0xbf85	:	typeName	=	"	市	"	;	break;
		case	(short)	0xbf86	:	typeName	=	"	区县	"	;	break;
		case	(short)	0xbf87	:	typeName	=	"	乡镇	"	;	break;
		case	(short)	0xbf88	:	typeName	=	"	村	"	;	break;
		case	(short)	0xc400	:	typeName	=	"	地标"	;	break;
		case	(short)	0x4800	:	typeName	=	"	高速出入口	"	;	break;
		case	(short)	0xc480	:	typeName	=	"	立交桥	"	;	break;
		case	(short)	0xc500	:	typeName	=	"	山脉	"	;	break;
		case	(short)	0xbf81	:	typeName	=	"	敏感岛屿	"	;	break;
		case	(short)	0xc300	:	typeName	=	"	大厦	"	;	break;
		case	(short)	0xc280	:	typeName	=	"	小区	"	;	break;
		case	(short)	0xb080	:	typeName	=	"	政府机关	"	;	break;
		case	(short)	0xb100	:	typeName	=	"	公安机关	"	;	break;
		case	(short)	0xb180	:	typeName	=	"	司法机关	"	;	break;
		case	(short)	0xb200	:	typeName	=	"	涉外机构	"	;	break;
		case	(short)	0xb280	:	typeName	=	"	驻地机构	"	;	break;
		case	(short)	0xb300	:	typeName	=	"	社会团体	"	;	break;
		case	(short)	0x5081	:	typeName	=	"	五星级酒店	"	;	break;
		case	(short)	0x5082	:	typeName	=	"	四星级酒店	"	;	break;
		case	(short)	0x5083	:	typeName	=	"	三星级酒店	"	;	break;
		case	(short)	0x508f	:	typeName	=	"	其他酒店	"	;	break;
		case	(short)	0x5084	:	typeName	=	"	连锁酒店	"	;	break;
		case	(short)	0x5180	:	typeName	=	"	度假村	"	;	break;
		case	(short)	0x1080	:	typeName	=	"	餐厅	"	;	break;
		case	(short)	0x1100	:	typeName	=	"	快餐	"	;	break;
		case	(short)	0x2100	:	typeName	=	"	公园	"	;	break;
		case	(short)	0x2200	:	typeName	=	"	动植物园	"	;	break;
		case	(short)	0x2280	:	typeName	=	"	水族馆	"	;	break;
		case	(short)	0x5200	:	typeName	=	"	景点	"	;	break;
		case	(short)	0xa780	:	typeName	=	"	宗教	"	;	break;
		case	(short)	0xa580	:	typeName	=	"	图书馆	"	;	break;
		case	(short)	0xa500	:	typeName	=	"	博物馆	"	;	break;
		case	(short)	0xa600	:	typeName	=	"	文化场馆	"	;	break;
		case	(short)	0x8080	:	typeName	=	"	运动场馆	"	;	break;
		case	(short)	0x8180	:	typeName	=	"	高尔夫	"	;	break;
		case	(short)	0x8100	:	typeName	=	"	健身中心	"	;	break;
		case	(short)	0x2380	:	typeName	=	"	影剧院	"	;	break;
		case	(short)	0x2180	:	typeName	=	"	游乐园	"	;	break;
		case	(short)	0x2080	:	typeName	=	"	广场	"	;	break;
		case	(short)	0x2400	:	typeName	=	"	娱乐城	"	;	break;
		case	(short)	0x7880	:	typeName	=	"	洗浴	"	;	break;
		case	(short)	0x2480	:	typeName	=	"	游艺厅	"	;	break;
		case	(short)	0x1200	:	typeName	=	"	酒吧	"	;	break;
		case	(short)	0x1180	:	typeName	=	"	茶楼咖啡	"	;	break;
		case	(short)	0x2F80	:	typeName	=	"	其他	"	;	break;
		case	(short)	0x6080	:	typeName	=	"	综合商场	"	;	break;
		case	(short)	0x6180	:	typeName	=	"	超市	"	;	break;
		case	(short)	0x6200	:	typeName	=	"	便利店	"	;	break;
		case	(short)	0x6100	:	typeName	=	"	专营店	"	;	break;
		case	(short)	0x4080	:	typeName	=	"	机场	"	;	break;
		case	(short)	0x4180	:	typeName	=	"	火车站	"	;	break;
		case	(short)	0x4200	:	typeName	=	"	城铁站	"	;	break;
		case	(short)	0x4900	:	typeName	=	"	地铁出入口	"	;	break;
		case	(short)	0x4280	:	typeName	=	"	汽车站	"	;	break;
		case	(short)	0x4300	:	typeName	=	"	公交车站	"	;	break;
		case	(short)	0x4500	:	typeName	=	"	码头	"	;	break;
		case	(short)	0x4680	:	typeName	=	"	服务区	"	;	break;
		case	(short)	0x4700	:	typeName	=	"	收费站	"	;	break;
		case	(short)	0x3100	:	typeName	=	"	停车场	"	;	break;
		case	(short)	0x3080	:	typeName	=	"	加油站	"	;	break;
		case	(short)	0x3090	:	typeName	=	"	加气站	"	;	break;
		case	(short)	0x4880	:	typeName	=	"	公路服务站	"	;	break;
		case	(short)	0x3180	:	typeName	=	"	汽车服务	"	;	break;
		case	(short)	0x4780	:	typeName	=	"	售票处	"	;	break;
		case	(short)	0xa100	:	typeName	=	"	大学	"	;	break;
		case	(short)	0xa180	:	typeName	=	"	重点中学	"	;	break;
		case	(short)	0xa200	:	typeName	=	"	小学	"	;	break;
		case	(short)	0xa280	:	typeName	=	"	幼儿园	"	;	break;
		case	(short)	0xa302	:	typeName	=	"	职业学校	"	;	break;
		case	(short)	0xa380	:	typeName	=	"	特殊学校	"	;	break;
		case	(short)	0xa080	:	typeName	=	"	科研机构	"	;	break;
		case	(short)	0x9080	:	typeName	=	"	医院	"	;	break;
		case	(short)	0x9100	:	typeName	=	"	诊所	"	;	break;
		case	(short)	0x9180	:	typeName	=	"	药店	"	;	break;
		case	(short)	0x9280	:	typeName	=	"	防疫站	"	;	break;
		case	(short)	0x9380	:	typeName	=	"	血液中心	"	;	break;
		case	(short)	0x9200	:	typeName	=	"	兽药店	"	;	break;
		case	(short)	0x7180	:	typeName	=	"	电信	"	;	break;
		case	(short)	0x7200	:	typeName	=	"	邮局	"	;	break;
		case	(short)	0xa880	:	typeName	=	"	电视塔	"	;	break;
		case	(short)	0xa800	:	typeName	=	"	出版社	"	;	break;
		case	(short)	0xb780	:	typeName	=	"	福利院	"	;	break;
		case	(short)	0xb800	:	typeName	=	"	事务所	"	;	break;
		case	(short)	0xb880	:	typeName	=	"	公共厕所	"	;	break;
		case	(short)	0x7800	:	typeName	=	"	美容美发	"	;	break;
		case	(short)	0x5280	:	typeName	=	"	旅行社	"	;	break;
		case	(short)	0x7a80	:	typeName	=	"	中介	"	;	break;
		case	(short)	0x7b00	:	typeName	=	"	家政搬家	"	;	break;
		case	(short)	0x7b80	:	typeName	=	"	装潢广告	"	;	break;
		case	(short)	0x7900	:	typeName	=	"	影楼彩扩	"	;	break;
		case	(short)	0x7980	:	typeName	=	"	物流快递	"	;	break;
		case	(short)	0xb900	:	typeName	=	"	殡仪馆	"	;	break;
		case	(short)	0x7f80	:	typeName	=	"	其他服务	"	;	break;
		case	(short)	0xc180	:	typeName	=	"	公司	"	;	break;
		case	(short)	0xc200	:	typeName	=	"	企业	"	;	break;
		case	(short)	0x7080	:	typeName	=	"	银行	"	;	break;
		case	(short)	0x7100	:	typeName	=	"	ATM	"	;	break;
		case	(short)	0x7280	:	typeName	=	"	证券	"	;	break;
		case	(short)	0x7300	:	typeName	=	"	保险	"	;	break;
		case	(short)	0x7a00	:	typeName	=	"	投资公司	"	;	break;
		case	(short)	0xbf84	:	typeName	=	"	省会	"	;	break;
		case	(short)	0xbf89	:	typeName	=	"	特别行政区	"	;	break;
		case	(short)	0xbf90	:	typeName	=	"	国家	"	;	break;
		default:  typeName = "其他";
			break;
		}
		return typeName;
	}
}

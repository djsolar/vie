package com.mansion.tele.common;



/**
 * 常量
 * @author hefeng
 *
 */
public class ConstantValue {
	/** 刷出个数  */
	public static final double FLUSH = 100;	
	/** 形状点个数最大值  */
	public static final int MAX_POINT_COUNT = 254;
	/** 制作状态 成功 */
	public static final byte STATUS_SUCCESS = 0;
	/** 制作状态 失败 */
	public static final byte STATUS_FAILED = 1;
	
	//delete by xuzhaohui for  此状态使用状态 STATUS_NOTDATA代替 begin 2012年2月14日14:52:52 
//	/** 制作状态 不制作 */
//	public static final byte STATUS_NOTDO = 2;
	//delete by xuzhaohui for  此状态使用状态 STATUS_NOTDATA代替 end 2012年2月14日14:52:52 
	
	/** 制作状态 无数据 */
	public static final byte STATUS_NOTDATA = 3;
	/** 无断点续作 */
	public static final byte NOBREAKPOINT = 0;
	/** 断点续作 */
	public static final byte BREAKPOINT_CONTINUE = 1;
	/** 数据所属状态中间库M1 */
	public static final byte M1 = 1;
	/** 数据所属状态中间库M2 */
	public static final byte M2 = 2;
	/** 数据所属状态中间库M3 */
	public static final byte M3 = 3;
	/** 数据所属状态中间库M2并且为MM所用 */
	public static final byte MM2 = 4;
	/**  显示对象 */
	public static final int BORDER_BYISDISPLAY_YES_1 =  1;
	/**  非显示对象 */
	public static final int BORDER_BYISDISPLAY_NO_2 =  2;
	/** 无效值 */
	public static final byte BYTE_INVALID = 0X7F;
	/** 255*/
	public static final int BYTE_255 = 0XFF;
	/** 面积筛选的阀值*/
	public static final int MAREA_10 = 10; 
	/** 面积筛选的阀值*/
	public static final int MAREA_20 = 20; 
	/** Windows制作*/
	public static final int IPC_WINDOWS = 0;
	/** LINUX制作*/
	public static final int IPC_LINUX = 1;
	/** dll路径 */
	public static final String GEOMITRY_DLL_PATH = "C:/windows/TeleCompiler/GeomLibrary_";
	/** so路径 */
	public static final String GEOMITRY_SO_PATH = "/share/TeleCompiler/GeomLibrary_";
	/** 栅格制作 */
	public static final int IGRID = 2;
	/** view制作 */
	public static final int IVIEW = 1;
	/** 数据类型  0 ：线数据 */
	public static final int IDATATYPE_0 = 0; 
	/** 数据类型  1：面数据 */
	public static final int IDATATYPE_1 = 1; 
	/** 数据类型  2：高层的面（6，7层） */
	public static final int IDATATYPE_2 = 2; 
	/** 两铁路的连接关系 1 首首相交    */
	public static final int RAILWAYCONNECTION_1 = 1;
	/** 两铁路的连接关系   2 首尾相交   */
	public static final int RAILWAYCONNECTION_2 = 2; 
	/** 两铁路的连接关系    3 尾首相交   4 尾尾相交 */
	public static final int RAILWAYCONNECTION_3 = 3; 
	/** 两铁路的连接关系    4 尾尾相交 */
	public static final int RAILWAYCONNECTION_4 = 4;
	/** 符号" ," */
	public static final String COMMA = ",";
	/** 符号";" */
	public static final String SPLIT_STRING = ";";
	/** 符号tab */
	public static final String TAB = "\t";
	/** 最大角度  */
	public static final int MAXANGLE = 75;
	/** 移位8位  */
	public static final int SHIFT_8 = 8;

	
	
	
	/**
	 * 敏感岛屿的原始code
	 */
	public static final int ORGCODEOFSENSITIVEISLAND = 92010204;
	
	/**
	 * 省会的原始code
	 */
	public static final int ORGCODEOFPROVINCIALCAPITAL = 81601032;
	
	/**
	 * 首都的原始code
	 */
	public static final int ORGCODEOFCAPITAL = 81601011;
	
	/**
	 * 直辖市的原始code
	 */
	public static final int ORGCODEOFDIRECTCITY = 81601053;
	
	/**
	 * 省名的原始code
	 */
	public static final int ORGCODEOFPROVINCIAL = 81601031;
	
	/**
	 *  首都的TeleCode
	 */
	public static final int TELECODEOFCAPITAL = 0xbf82;
	
	/**
	 *  直辖市的TeleCode
	 */
	public static final int TELECODEOFDIRECTCITY = 0xbf83;
	
	
	/**
	 *  省会的TeleCode
	 */
	public static final int TELECODEOFPROVINCIALCAPITAL = 0xbf84 ;
	
	/**
	 * 山的TeleCode
	 */
	public static final int TELECODEOFMOUNTAIN = 0xc500;
	
	/**
	 * 乡镇的原始code
	 */
	public static final int ORGCODEOFVILLAGE = 2010105;
	
	/**
	 * 市名的原始code
	 */
	public static final int TELECODEOFCITY = 0xbf85;
	
	/**
	 * 特别行政区的原始code
	 */
	public static final int TELECODEOFSPECIAL_ADMINISTRATIVE_REGION = 0xbf89;
	/**
	 * 公园的code
	 */
	public static final int TELE_CODE_OF_LANDPARK =0x2100;
	
	/**
	 *公交站点名称和路线名称的组合文字个数在32个以内 
	 */
	public static final int BUSSTOPNAMEMAXLEN = 30;
	
	/**
	 * 需要进行重复处理的公交数据的最大数
	 */
	public static final int DEL_REAP_BUSSTOP_MAXCNT = 3;
	/**
	 * 	地铁名称显示优先级
	 */
	public static final int ISUBWAYNAMEPRI = 106;
	
	/**
	 * 坐标从度转换成1/2560秒
	 */
	public static final int DEGREETOSECOND = 2560 * 3600;
	
	/**
	 * 第一个比例尺
	 */
	public static final int SCALE_1 = 1;
	
	/**
	 * 第二个比例尺
	 */
	public static final int SCALE_2 = 2;
	
	/**
	 *	第三个比例尺 
	 */
	public static final int SCALE_3 = 3;
	
	/**
	 * 高速公路显示优先级
	 */
	public static final int HIGH_WAY_DISP_PRI = 112;
	
	/**
	 * 国道显示优先级
	 */
	public static final int NATIONAL_ROAD_DISP_PRI = 113;
	
	/**
	 * 城市快速路显示优先级
	 */
	public static final int CITY_FREE_WAY_DISP_PRI = 114;
	
	/**
	 * 省道显示优先级
	 */
	public static final int PROCINCE_ROAD_DISP_PRI = 115;
	
	/**
	 * 城市主干道显示优先级
	 */
	public static final int CITY_MAJOR_ROAD_DISP_PRI = 116;
	
	/**
	 * 城市次干道显示优先级
	 */
	public static final int CITY_MINOR_ROAD_DISP_PRI = 117;
	
	/**
	 * 一般道路显示优先级
	 */
	public static final int NORMAL_ROAD_DISP_PRI = 118;

	/**
	 * 其他道路显示优先级
	 */
	public static final int OTHER_ROAD_DISP_PRI = 119;
	
	
	/**
	 * 高速的路线番号种别
	 */
	public static final int HIGH_WAY_NO_KIND = 0xc600;
	
	/**
	 * 国道的路线番号种别
	 */
	public static final int NATIONAL_ROAD_NO_KIND = 0xc680;
	
	/**
	 * 省道的路线番号种别
	 */
	public static final int PROCINCE_ROAD_NO_KIND = 0xc700;
	
	/**
	 * 公交站code
	 */
	public static final int TELE_CODE_OF_BUS_STOP = 0x4300;
	
	/**
	 * 地铁出入口code
	 */
	public static final int TELE_CODE_OF_RAILWAY_ENTRANCE = 0x4900;
	
	/**
	 * 高速出入口code
	 */
	public static final int TELE_CODE_OF_HIGH_WAY_ENTRANCE = 0x4800;
	
	/**
	 * 停车场code
	 */
	public static final int TELE_CODE_OF_PARK = 0x3100;
	
	/**
	 * 公共厕所code
	 */
	public static final int TELE_CODE_OF_LATRINE = 0xb880;
	
	/**
	 * 道路连接方式首首相交
	 */
	public static final int ROAD_CONNECT_MODE_BEGIN_TO_BEGIN= 1;
	
	/**
	 * 道路连接方式首尾相交
	 */
	public static final int ROAD_CONNECT_MODE_BEGIN_TO_END= 2;
	
	/**
	 *道路连接方式 尾首相交
	 */
	public static final int ROAD_CONNECT_MODE_END_TO_BEGIN = 3;
	
	/**
	 *道路连接方式尾尾相交 
	 */
	public static final int ROAD_CONNECT_MODE_END_TO_END = 4;
	
	/**
	 * 道路连接允许的最大角度	
	 */
	public static final int PERMISSION_ROAD_CONNECT_MAX_ANGLE = 89;
	
	/**
	 * 道路连接允许的最大角度	
	 */
	public static final double ROAD_CONNECT_INVALID_ANGLE = -999.0;
	
	/**
	 * 调整道路名称的文字之间压盖的阀值
	 */
	public static final int ADJUSTMENT_ROAD_TXT_OVERLAP_VALUE = 50;

	/**
	 * 允许道路名称的文字之间压盖的阀值
	 */
	public static final int PERMISSION_ROAD_TXT_OVERLAP_VALUE = 5;
	
	/**
	 * 高架道路构造类型
	 */
	public static final int ROAD_CONSTRACT_TYPE_ELEVATED = 5;
	
	/**
	 * 全角空格的ASCII编码
	 */
	public static final int SBC_SPACE = 12288;
	
	/**
	 * 半角空格的ASCII编码
	 */
	public static final int DBC_SPACE = 32;
	
	/**
	 * 全角字符的开始ASCII编码
	 */
	public static final int SBC_CHAR_START = 65281;
	
	/**
	 * 全角字符的结束ASCII编码
	 */
	public static final int SBC_CHAR_END = 65374;
	
    /**
     * 半角字符的开始ASCII编码
     */
    public static final char DBC_CHAR_START = 33;  
    
    /** 
     * 半角字符的结束ASCII编码
     */  
    public static final char DBC_CHAR_END = 126;  
	
	 /**
	 * 全角半角转换间隔
	 */
	public static final int CONVERT_STEP = 65248;
	
	
	/**
	 * 检索中Admin_code的字符串长度
	 */
	public static final int ADMIN_CODE_SIZE = 4;
	
	/**
	 * 省级code的填充字符
	 */
	public static final String PROCINCE_CODE_FILL_STRING = "0000";
	
	/**
	 * 市级code在填充字符
	 */
	public static final String CITY_CODE_FILL_STRING = "00";
	
	/**
	 * 北京省级code的开始字符
	 */
	public static final String BEIJING_P_CODE_BEGIN_STRING = "11";

	/**
	 * 天津省级code的开始字符
	 */
	public static final String TIANJIN_P_CODE_BEGIN_STRING = "12";
	
	/**
	 * 上海省级code的开始字符
	 */
	public static final String SHANGHAI_P_CODE_BEGIN_STRING = "31";
	
	/**
	 * 重庆省级code的开始字符
	 */
	public static final String CHONGQING_P_CODE_BEGIN_STRING = "50";
	
	
	public static final String EMPTYTASK = "客户端接收到空任务";
	/**
	 * 默认构造器
	 */
	private ConstantValue() {
		super();
	}
}

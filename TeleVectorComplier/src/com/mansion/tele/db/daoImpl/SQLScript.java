package com.mansion.tele.db.daoImpl;

/**
 * sql脚本的工具
 * @author hefeng
 *
 */
public class SQLScript {
	
	/** from */
	public static final String FROM = "from ";
	/** 空格 */
	public static final String S = " ";
	/** where */
	public static final String STRWHERE = " u where u.strid >= '";
	/** and */
	public static final String STRAND = " and u.strid <= '";
	/** 最小数值 */
	public static final String STRMINID = "0000000'";
	/** 最大数值 */
	public static final String STRMAXID = "9999999'";
	/** left join */
	public static final String STRLEFTJOIN = " u left join fetch u.stGeom where ";
	
	public static final String STRID = "u.strid >= '";

	public static final String SMID = " b where b.smid >= '";
	
	public static final String SMIDRAND = " and b.smid <= '";

}

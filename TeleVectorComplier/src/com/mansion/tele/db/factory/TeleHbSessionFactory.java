package com.mansion.tele.db.factory;

/**
 * 
 * @author Admin
 *
 */
public class TeleHbSessionFactory {

	/**
	 * 0层母库
	 */
	private static String orgJndi_0 = "org0";
	/**
	 * 1层母库
	 */
	private static String orgJndi_1 = "org1";
	/**
	 * 2层母库
	 */
	private static String orgJndi_2 = "org2";
	/**
	 * 3层母库
	 */
	private static String orgJndi_3 = "org3";
	/**
	 * 4层母库
	 */
	private static String orgJndi_4 = "org4";
	/**
	 * 5层母库
	 */
	private static String orgJndi_5 = "org5";
	/**
	 * 6层母库
	 */
	private static String orgJndi_6 = "org6";
	/**
	 * 7层母库
	 */
	private static String orgJndi_7 = "org7";
	/**
	 * 中间库
	 */
	private static final String MIDJNDI = "mid";
	/**
	 * 公交库
	 */
	private static final String ORGPLUGJNDI = "plug";
	/**
	 * 0层session
	 */
	private static TeleHbSession org_0 = new TeleHbSession(TeleHbSessionFactory.orgJndi_0);
	/**
	 * 1层session
	 */
	private static TeleHbSession org_1 = new TeleHbSession(TeleHbSessionFactory.orgJndi_1);
	/**
	 * 2层session
	 */
	private static TeleHbSession org_2 = new TeleHbSession(TeleHbSessionFactory.orgJndi_2);
	/**
	 * 3层session
	 */
	private static TeleHbSession org_3 = new TeleHbSession(TeleHbSessionFactory.orgJndi_3);
	/**
	 * 4层session
	 */
	private static TeleHbSession org_4 = new TeleHbSession(TeleHbSessionFactory.orgJndi_4);
	/**
	 * 5层session
	 */
	private static TeleHbSession org_5 = new TeleHbSession(TeleHbSessionFactory.orgJndi_5);
	/**
	 * 6层session
	 */
	private static TeleHbSession org_6 = new TeleHbSession(TeleHbSessionFactory.orgJndi_6);
	/**
	 * 7层session
	 */
	private static TeleHbSession org_7 = new TeleHbSession(TeleHbSessionFactory.orgJndi_7);
	/**
	 * 中间库session
	 */
	private static TeleHbSession mid = new TeleHbSession(TeleHbSessionFactory.MIDJNDI);
	
	/**
	 * 公交库session
	 */
	private static TeleHbSession orgPlug = new TeleHbSession(TeleHbSessionFactory.ORGPLUGJNDI);

	/**
	 * 获得session
	 * @param bLevel byte
	 * @return TeleHbSession
	 */
	public static TeleHbSession getOrgHbSession(byte bLevel){
		TeleHbSession needSession = null;
		switch (bLevel) {
		case 0:
			needSession = TeleHbSessionFactory.org_0;
			break;
		case 1:
			needSession = TeleHbSessionFactory.org_1;
			break;
		case 2:
			needSession = TeleHbSessionFactory.org_2;
			break;
		case 3:
			needSession = TeleHbSessionFactory.org_3;
			break;
		case 4:
	
			needSession = TeleHbSessionFactory.org_4;
			break;
		case 5:
			needSession = TeleHbSessionFactory.org_5;
			break;
		case 6:
			needSession = TeleHbSessionFactory.org_6;
			break;
		case 7:
			needSession = TeleHbSessionFactory.org_7;
			break;
		default :
			Exception e = new Exception();
			e.printStackTrace();
		}
		if (null != needSession){
			TeleHbSessionFactory.closeOther(needSession);
		}
		return needSession;
	}
	
	/**
	 * 取得母库的外挂数据所在数据库的session。
	 * @return TeleHbSession
	 */
	public static TeleHbSession getOrgOtherSession(){
		TeleHbSessionFactory.closeOther(TeleHbSessionFactory.orgPlug);
		return TeleHbSessionFactory.orgPlug;
	}
	/**
	 * 关闭其他session
	 * @param needSession TeleHbSession
	 */
	private static void closeOther(TeleHbSession needSession){
		
		TeleHbSessionFactory.comCloseTeleSessionFrom0By5(needSession);
		
		TeleHbSessionFactory.comCloseTeleSessionFrom6By7(needSession);
		
		if (null != TeleHbSessionFactory.mid){
			TeleHbSessionFactory.mid.closeSession();
		}
		if (null != TeleHbSessionFactory.orgPlug){
			TeleHbSessionFactory.orgPlug.closeSession();
		}
	}

	/**
	 * 比较关闭0~5 session
	 * @param needSession TeleHbSession
	 */
	public static void comCloseTeleSessionFrom0By5(TeleHbSession needSession) {
		
		if (TeleHbSessionFactory.isNullorSame(needSession, TeleHbSessionFactory.org_0)){
			TeleHbSessionFactory.org_0.closeSession();
		}
		if (TeleHbSessionFactory.isNullorSame(needSession, TeleHbSessionFactory.org_1)){
			TeleHbSessionFactory.org_1.closeSession();
		}
		if (TeleHbSessionFactory.isNullorSame(needSession, TeleHbSessionFactory.org_2)){
			TeleHbSessionFactory.org_2.closeSession();
		}
		if (TeleHbSessionFactory.isNullorSame(needSession, TeleHbSessionFactory.org_3)){
			TeleHbSessionFactory.org_3.closeSession();
		}
		if (TeleHbSessionFactory.isNullorSame(needSession, TeleHbSessionFactory.org_4)){
			TeleHbSessionFactory.org_4.closeSession();
		}
		if (TeleHbSessionFactory.isNullorSame(needSession, TeleHbSessionFactory.org_5)){
			TeleHbSessionFactory.org_5.closeSession();
		}
		
	}

	/**
	 * 比较关闭6~7 session
	 * @param needSession TeleHbSession
	 */
	public static void comCloseTeleSessionFrom6By7(TeleHbSession needSession) {
		if (TeleHbSessionFactory.isNullorSame(needSession, TeleHbSessionFactory.org_6)){
			TeleHbSessionFactory.org_6.closeSession();
		}
		if (TeleHbSessionFactory.isNullorSame(needSession, TeleHbSessionFactory.org_7)){
			TeleHbSessionFactory.org_7.closeSession();
		}
	}
	/**
	 * 比较两个session 
	 * @param needSession TeleHbSession
	 * @param comSession TeleHbSession
	 * @return boolean
	 */
	private static boolean isNullorSame(TeleHbSession needSession, TeleHbSession comSession){
		
		if (null != comSession
				&& needSession != comSession){
				return true;
			}
		return false;
	}
}

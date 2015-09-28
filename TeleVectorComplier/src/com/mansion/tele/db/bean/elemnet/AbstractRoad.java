package com.mansion.tele.db.bean.elemnet;

import java.util.Comparator;
import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractRoad extends PolylineElement
{
	protected byte byFRC; // 功能等级

	protected byte byNRC; // 管理等级

	protected byte byFOW; // 道路形态
	
	protected byte byoriginalityFow;// 道路原始形态（主要针对虚拟道路）

	protected byte bySRT; // 匝道类型
	
	protected byte byDTF; // 交通流方向
	
	protected byte byRoadConstructType; // 构造物类型

	protected byte byFerryType; // 车渡类型
	
	protected byte byCS; // 建筑状态

	protected int iRoadEleLength; // 道路元素长度
	
	protected double dRoadAngle_head;  //道路北偏东角度(头坐标)
	
	protected double dRoadAngle_tail;  //道路北偏东角度（尾坐标）
	
	protected  boolean  connection;//true连接过 false 没有连接过
	
	protected ShpPoint point_head;//道路头坐标
	
	protected ShpPoint point_tail;//道路尾坐标
	
	protected byte bstate;//道路状态， 1删除 2 保留

	protected String strStartNodeID; // 起始连接点ID

	protected String strEndNodeID; // 终止连接点ID

	protected transient List<BaseName> astRoadNames; // 道路名称

	protected RouteNo routeNo1; // 路线编号1，与数据库通信用
	
	protected RouteNo routeNo2; // 路线编号2，与数据库通信用

	public byte getByfrc()
	{
		return this.byFRC;
	}

	public void setByfrc(byte byfrc)
	{
		this.byFRC = byfrc;
	}

	public byte getBynrc()
	{
		return this.byNRC;
	}

	public void setBynrc(byte bynrc)
	{
		this.byNRC = bynrc;
	}

	public byte getByfow()
	{
		return this.byFOW;
	}

	public void setByfow(byte byfow)
	{
		this.byFOW = byfow;
	}
	
	public byte getByoriginalityFow()
	{
		return this.byoriginalityFow;
	}

	public void setByoriginalityFow(byte byoriginalityFow)
	{
		this.byoriginalityFow = byoriginalityFow;
	}

	public byte getBysrt()
	{
		return this.bySRT;
	}

	public void setBysrt(byte bysrt)
	{
		this.bySRT = bysrt;
	}

	public byte getByferrytype()
	{
		return this.byFerryType;
	}

	public void setByferrytype(byte byferrytype)
	{
		this.byFerryType = byferrytype;
	}
	public byte getBydtf()
	{
		return this.byDTF;
	}

	public void setBydtf(byte bydtf)
	{
		this.byDTF = bydtf;
	}

	public int getIroadelelength()
	{
		return this.iRoadEleLength;
	}

	public void setIroadelelength(int iroadelelength)
	{
		this.iRoadEleLength = iroadelelength;
	}

	public byte getBycs()
	{
		return this.byCS;
	}

	public void setBycs(byte bycs)
	{
		this.byCS = bycs;
	}
	
	public byte getByroadconstructtype()
	{
		return this.byRoadConstructType;
	}

	public void setByroadconstructtype(byte byroadconstructtype)
	{
		this.byRoadConstructType = byroadconstructtype;
	}

	public String getStrStartNodeID()
	{
		return strStartNodeID;
	}

	public void setStrStartNodeID(String strStartNodeID)
	{
		this.strStartNodeID = strStartNodeID;
	}

	public String getStrEndNodeID()
	{
		return strEndNodeID;
	}

	public void setStrEndNodeID(String strEndNodeID)
	{
		this.strEndNodeID = strEndNodeID;
	}

	public List<BaseName> getAstRoadNames()
	{
		return astRoadNames;
	}

	public void setAstRoadNames(List<BaseName> astRoadNames)
	{
		if (astRoadNames == null && this.astRoadNames != null) {
			this.astRoadNames.clear();
		} else {
			this.astRoadNames = astRoadNames;
		}
	}
	public RouteNo getRouteNo1()
	{
		return routeNo1;
	}

	public void setRouteNo1(RouteNo routeNo1)
	{
		this.routeNo1 = routeNo1;
	}

	public RouteNo getRouteNo2()
	{
		return routeNo2;
	}

	public void setRouteNo2(RouteNo routeNo2)
	{
		this.routeNo2 = routeNo2;
	}

	public double getdRoadAngle_head() {
		return dRoadAngle_head;
	}

	public void setdRoadAngle_head(double dRoadAngle_head) {
		this.dRoadAngle_head = dRoadAngle_head;
	}

	public double getdRoadAngle_tail() {
		return dRoadAngle_tail;
	}

	public void setdRoadAngle_tail(double dRoadAngle_tail) {
		this.dRoadAngle_tail = dRoadAngle_tail;
	}
	/**
	 * true连接过 false 没有连接过
	 */
	public boolean isConnection() {
		return connection;
	}
	/**
	 * true连接过 false 没有连接过
	 */
	public void setConnection(boolean connection) {
		this.connection = connection;
	}

	public ShpPoint getPoint_head() {
		return point_head;
	}

	public void setPoint_head(ShpPoint point_head) {
		this.point_head = point_head;
	}

	public ShpPoint getPoint_tail() {
		return point_tail;
	}

	public void setPoint_tail(ShpPoint point_tail) {
		this.point_tail = point_tail;
	}

	public byte getBstate() {
		return bstate;
	}

	public void setBstate(byte bstate) {
		this.bstate = bstate;
	}
}

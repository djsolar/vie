package com.mansion.tele.db.bean.elemnet;
import java.util.List;

/**
 * ServiceInfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("serial")
public class Service extends PointElement
{

//	private byte byDisplayLevel; // 显示层级

	private String strTelList; // 电话号码列表,多个电话号码之间用半角分号";"分隔

//	private String strFaxList; // 传真号列表,多个传真号码之间用半角分号";"分隔
//
//	private String strHomePage; // 主页
//
//	private String strEmailAddr; // 电子邮件地址

//	private String strValidPeriodList; // 开店时间
//
//	private String strServTypeList; // 服务类型列表
//
	private String strAddrInfo; // 地址信息
//
//	private String strPostCode; // 邮编
//
//	private byte byHasPark; // 停车设施
//
//	private int iParkingSpaceCount; // 停车设施数量
//
//	private String strNote; // 说明
//
//	private byte byIsUnderground; // 地下标识
//
//	private String strPhotoIDList; // 照片ID列表
//
//	private byte byInvestStatus; // 调查状态
//
//	private String strAdmCode; // 所属行政区划编码
//
//	private String strBankCardTypeList; // 银行卡类型列表
//
	private int iRTServiceType; // RT服务类型
	private String iRTServiceTypeOrg; // RT服务类型
//	
	private String strSWServiceType;
//
//	private byte byParkType; // 停车场类型
//
//	private String strFuelTypeList; // 燃油类型列表
//
//	private byte byStarrating; // 星级(酒店)
//
//	private byte byHasTicketServ; // 提供订票服务
//
//	private byte byHasBreakfast;  // 提供早餐
//
//	private byte byHasLunch; // 提供工作午餐
//
//	private byte byHasRestaurant; // 提供餐饮设施
//
//	private byte byIsMultipleShop; // 连锁店标识
//
//	private String strSpecialMarkIDList; // 专用图标ID
//
//	private String str3DObjectID; // 3D对象ID
//
//	private int iAverageConsumption; // 平均消费
//
//	private byte byHasTakeOutServ; // 提供外卖
//
//	private byte byHasPrivateRoom; // 提供包房
//
//	private byte byHasKaraoke; // 提供卡拉OK
	
	//strshpid
	private String strshpid;
	// Constructors
	private List<BaseName> astServNames; // 服务名称

//	private List<BaseName> astServAddrNames; // 服务地址

//	private List<ParkCharge> astParkCharges; // 停车场收费信息

//	private List<Room> astRooms; // 房间信息
	
	private String strParentServiceID; // 父服务ID
	
//	public byte getByDisplayLevel() {
//		return byDisplayLevel;
//	}
//
//	public void setByDisplayLevel(byte byDisplayLevel) {
//		this.byDisplayLevel = byDisplayLevel;
//	}
//
	
	public String getStrTelList() {
		return strTelList;
	}

	public String getStrshpid() {
		return strshpid;
	}

	public void setStrshpid(String strshpid) {
		this.strshpid = strshpid;
	}

	public void setStrTelList(String strTelList) {
		this.strTelList = strTelList;
	}
//
//	public String getStrFaxList() {
//		return strFaxList;
//	}
//
//	public void setStrFaxList(String strFaxList) {
//		this.strFaxList = strFaxList;
//	}
//
//	public String getStrHomePage() {
//		return strHomePage;
//	}
//
//	public void setStrHomePage(String strHomePage) {
//		this.strHomePage = strHomePage;
//	}
//
//	public String getStrEmailAddr() {
//		return strEmailAddr;
//	}
//
//	public void setStrEmailAddr(String strEmailAddr) {
//		this.strEmailAddr = strEmailAddr;
//	}
//
//	public String getStrValidPeriodList() {
//		return strValidPeriodList;
//	}
//
//	public void setStrValidPeriodList(String strValidPeriodList) {
//		this.strValidPeriodList = strValidPeriodList;
//	}
//
//	public String getStrServTypeList() {
//		return strServTypeList;
//	}
//
//	public void setStrServTypeList(String strServTypeList) {
//		this.strServTypeList = strServTypeList;
//	}
//
	public String getStrAddrInfo() {
		return strAddrInfo;
	}

	public void setStrAddrInfo(String strAddrInfo) {
		this.strAddrInfo = strAddrInfo;
	}

//	public String getStrPostCode() {
//		return strPostCode;
//	}
//
//	public void setStrPostCode(String strPostCode) {
//		this.strPostCode = strPostCode;
//	}
//
//	public byte getByHasPark() {
//		return byHasPark;
//	}
//
//	public void setByHasPark(byte byHasPark) {
//		this.byHasPark = byHasPark;
//	}
//
//	public int getiParkingSpaceCount() {
//		return iParkingSpaceCount;
//	}
//
//	public void setiParkingSpaceCount(int iParkingSpaceCount) {
//		this.iParkingSpaceCount = iParkingSpaceCount;
//	}
//
//	public String getStrNote() {
//		return strNote;
//	}
//
//	public void setStrNote(String strNote) {
//		this.strNote = strNote;
//	}
//
//	public byte getByIsUnderground() {
//		return byIsUnderground;
//	}
//
//	public void setByIsUnderground(byte byIsUnderground) {
//		this.byIsUnderground = byIsUnderground;
//	}
//
//	public String getStrPhotoIDList() {
//		return strPhotoIDList;
//	}
//
//	public void setStrPhotoIDList(String strPhotoIDList) {
//		this.strPhotoIDList = strPhotoIDList;
//	}
//
//	public byte getByInvestStatus() {
//		return byInvestStatus;
//	}
//
//	public void setByInvestStatus(byte byInvestStatus) {
//		this.byInvestStatus = byInvestStatus;
//	}
//
//	public String getStrAdmCode() {
//		return strAdmCode;
//	}
//
//	public void setStrAdmCode(String strAdmCode) {
//		this.strAdmCode = strAdmCode;
//	}
//
//	public String getStrBankCardTypeList() {
//		return strBankCardTypeList;
//	}
//
//	public void setStrBankCardTypeList(String strBankCardTypeList) {
//		this.strBankCardTypeList = strBankCardTypeList;
//	}

	public int getiRTServiceType() {
		return iRTServiceType;
	}

	public void setiRTServiceType(int iRTServiceType) {
		this.iRTServiceType = iRTServiceType;
	}

	public String getStrSWServiceType() {
		return strSWServiceType;
	}

	public void setStrSWServiceType(String strSWServiceType) {
		this.strSWServiceType = strSWServiceType;
	}
//
//	public byte getByParkType() {
//		return byParkType;
//	}
//
//	public void setByParkType(byte byParkType) {
//		this.byParkType = byParkType;
//	}
//
//	public String getStrFuelTypeList() {
//		return strFuelTypeList;
//	}
//
//	public void setStrFuelTypeList(String strFuelTypeList) {
//		this.strFuelTypeList = strFuelTypeList;
//	}
//
//	public byte getByStarrating() {
//		return byStarrating;
//	}
//
//	public void setByStarrating(byte byStarrating) {
//		this.byStarrating = byStarrating;
//	}
//
//	public byte getByHasTicketServ() {
//		return byHasTicketServ;
//	}
//
//	public void setByHasTicketServ(byte byHasTicketServ) {
//		this.byHasTicketServ = byHasTicketServ;
//	}
//
//	public byte getByHasBreakfast() {
//		return byHasBreakfast;
//	}
//
//	public void setByHasBreakfast(byte byHasBreakfast) {
//		this.byHasBreakfast = byHasBreakfast;
//	}
//
//	public byte getByHasLunch() {
//		return byHasLunch;
//	}
//
//	public void setByHasLunch(byte byHasLunch) {
//		this.byHasLunch = byHasLunch;
//	}
//
//	public byte getByHasRestaurant() {
//		return byHasRestaurant;
//	}
//
//	public void setByHasRestaurant(byte byHasRestaurant) {
//		this.byHasRestaurant = byHasRestaurant;
//	}
//
//	public byte getByIsMultipleShop() {
//		return byIsMultipleShop;
//	}
//
//	public void setByIsMultipleShop(byte byIsMultipleShop) {
//		this.byIsMultipleShop = byIsMultipleShop;
//	}
//
//	public String getStrSpecialMarkIDList() {
//		return strSpecialMarkIDList;
//	}
//
//	public void setStrSpecialMarkIDList(String strSpecialMarkIDList) {
//		this.strSpecialMarkIDList = strSpecialMarkIDList;
//	}
//
//	public String getStr3DObjectID() {
//		return str3DObjectID;
//	}
//
//	public void setStr3DObjectID(String str3dObjectID) {
//		str3DObjectID = str3dObjectID;
//	}
//
//	public int getiAverageConsumption() {
//		return iAverageConsumption;
//	}
//
//	public void setiAverageConsumption(int iAverageConsumption) {
//		this.iAverageConsumption = iAverageConsumption;
//	}
//
//	public byte getByHasTakeOutServ() {
//		return byHasTakeOutServ;
//	}
//
//	public void setByHasTakeOutServ(byte byHasTakeOutServ) {
//		this.byHasTakeOutServ = byHasTakeOutServ;
//	}
//
//	public byte getByHasPrivateRoom() {
//		return byHasPrivateRoom;
//	}
//
//	public void setByHasPrivateRoom(byte byHasPrivateRoom) {
//		this.byHasPrivateRoom = byHasPrivateRoom;
//	}
//
//	public byte getByHasKaraoke() {
//		return byHasKaraoke;
//	}
//
//	public void setByHasKaraoke(byte byHasKaraoke) {
//		this.byHasKaraoke = byHasKaraoke;
//	}

	public List<BaseName> getAstServNames() {
		return astServNames;
	}

	public void setAstServNames(List<BaseName> astServNames) {
		this.astServNames = astServNames;
	}

//	public List<BaseName> getAstServAddrNames() {
//		return astServAddrNames;
//	}
//
//	public void setAstServAddrNames(List<BaseName> astServAddrNames) {
//		this.astServAddrNames = astServAddrNames;
//	}
//
//	public List<ParkCharge> getAstParkCharges() {
//		return astParkCharges;
//	}
//
//	public void setAstParkCharges(List<ParkCharge> astParkCharges) {
//		this.astParkCharges = astParkCharges;
//	}
//
//	public List<Room> getAstRooms() {
//		return astRooms;
//	}
//
//	public void setAstRooms(List<Room> astRooms) {
//		this.astRooms = astRooms;
//	}
//
	public String getStrParentServiceID() {
		return strParentServiceID;
	}

	public void setStrParentServiceID(String strParentServiceID) {
		this.strParentServiceID = strParentServiceID;
	}

	public String getiRTServiceTypeOrg() {
		if(this.iRTServiceTypeOrg != null){
			this.setiRTServiceType(Integer.valueOf(iRTServiceTypeOrg));
		}
		return iRTServiceTypeOrg;
	}

	public void setiRTServiceTypeOrg(String iRTServiceTypeOrg) {
		this.iRTServiceTypeOrg = iRTServiceTypeOrg;
		if(iRTServiceTypeOrg != null){
			this.setiRTServiceType(Integer.valueOf(iRTServiceTypeOrg));
		}
	}
	
	
}

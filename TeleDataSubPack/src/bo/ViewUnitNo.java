package bo;

import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.GeoLocation;
import com.mansion.tele.common.UnitNo;

/**
 * view中包括Unit对象
 * @author wenxc
 *
 */
public class ViewUnitNo  {
	
	
	/**
	 * 层级
	 */
	private int ilevel;

	/**
	 * unitx
	 */
	private int iX;
	
	/**
	 * unity
	 */
	private int iY;
	/**
	 * 行政code
	 */
	private int admincode;
//	/**
//	 * 包含的block集合
//	 */
//	private List<BlockNo> blocklist;
	/**
	 * 名字
	 */
	private String strName;
	/**
	 * block号
	 */
	private BlockNo blockNo;
	/**
	 * 得到block号
	 * @return BlockNo
	 */
	public BlockNo getBlockNo() {
		return this.blockNo;
	}


	/**
	 * 设置block号
	 * @param blockNo BlockNo
	 */
	public void setBlockNo(BlockNo blockNo) {
		this.blockNo = blockNo;
	}
	
	
	

	/**
	 * 得到名称
	 * @return String
	 */

	public String getStrName() {
		return this.strName;
	}


	/**
	 * 设置名称
	 * @param strName String
	 */
	public void setStrName(String strName) {
		this.strName = strName;
	}

	/**
	 * 得到level
	 * @return int
	 */
	public int getIlevel() {
		return this.ilevel;
	}


	/**
	 * 设置level
	 * @param ilevel int
	 */
	public void setIlevel(int ilevel) {
		this.ilevel = ilevel;
	}
	/**
	 * 到达AdminCode
	 * @return int
	 */
	public int getAdmincode() {
		return this.admincode;
	}


	/**
	 * 设置AdminCode
	 * @param admincode int
	 */
	public void setAdmincode(int admincode) {
		this.admincode = admincode;
	}


//	public List<BlockNo> getBlocklist() {
//		return blocklist;
//	}
//
//
//	public void setBlocklist(List<BlockNo> blocklist) {
//		this.blocklist = blocklist;
//	}



	/**得到x
	 * @return int
	 */
	public int getiX() {
		return this.iX;
	}




	/**
	 * 设置x
	 * @param  iX int
	 */
	public void setiX(int iX) {
		this.iX = iX;
	}


	/**
	 * 得到y
	 * @return int
	 */
	public int getiY() {
		return this.iY;
	}


	/**
	 * 设置y
	 * @param iY int
	 */
	public void setiY(int iY) {
		this.iY = iY;
	}
	/**
	 * 获得viewUnitNo对象
	 * @param stPosInUnit GeoLocation
	 * @param lUnitWidth long
	 * @param lUnitHight long
	 * @return ViewUnitNo
	 */
	public static ViewUnitNo valueOf(GeoLocation stPosInUnit, long lUnitWidth, long lUnitHight){
		
		ViewUnitNo viewUnitNo =  new ViewUnitNo();
		
		UnitNo unitNo = UnitNo.valueOf(stPosInUnit, lUnitWidth, lUnitHight);
		
		viewUnitNo.setiX(unitNo.iX);
		
		viewUnitNo.setiY(unitNo.iY);
		
		return viewUnitNo;
		
	}
//	/**
//	 * 获得相对应的block集合
//	 * @param stlbPosInUnit
//	 * @param strtPosInUnit
//	 * @param lBlockWidth
//	 * @param lBlockHight
//	 */
//	public void setBlock(GeoLocation stlbPosInUnit,GeoLocation strtPosInUnit, long lBlockWidth, long lBlockHight){
//		
//		blocklist = new ArrayList<BlockNo>();
//		
//		BlockNo lbblockNo = BlockNo.valueOf(stlbPosInUnit, lBlockWidth, lBlockHight);
//		
//		BlockNo rtblockNo = BlockNo.valueOf(strtPosInUnit, lBlockWidth, lBlockHight);
//		
//		for (int i = lbblockNo.iX; i <= rtblockNo.iX; i++) {
//			
//			for (int j = lbblockNo.iY; j <= rtblockNo.iY; j++) {
//				
//				BlockNo blockNo = new BlockNo(i, j);
//				
//				blocklist.add(blockNo);
//				
//			}
//			
//		}
//		 
//		
//	}



}

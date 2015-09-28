package bo;

import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.GeoLocation;
import com.mansion.tele.common.UnitNo;

/**
 * view�а���Unit����
 * @author wenxc
 *
 */
public class ViewUnitNo  {
	
	
	/**
	 * �㼶
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
	 * ����code
	 */
	private int admincode;
//	/**
//	 * ������block����
//	 */
//	private List<BlockNo> blocklist;
	/**
	 * ����
	 */
	private String strName;
	/**
	 * block��
	 */
	private BlockNo blockNo;
	/**
	 * �õ�block��
	 * @return BlockNo
	 */
	public BlockNo getBlockNo() {
		return this.blockNo;
	}


	/**
	 * ����block��
	 * @param blockNo BlockNo
	 */
	public void setBlockNo(BlockNo blockNo) {
		this.blockNo = blockNo;
	}
	
	
	

	/**
	 * �õ�����
	 * @return String
	 */

	public String getStrName() {
		return this.strName;
	}


	/**
	 * ��������
	 * @param strName String
	 */
	public void setStrName(String strName) {
		this.strName = strName;
	}

	/**
	 * �õ�level
	 * @return int
	 */
	public int getIlevel() {
		return this.ilevel;
	}


	/**
	 * ����level
	 * @param ilevel int
	 */
	public void setIlevel(int ilevel) {
		this.ilevel = ilevel;
	}
	/**
	 * ����AdminCode
	 * @return int
	 */
	public int getAdmincode() {
		return this.admincode;
	}


	/**
	 * ����AdminCode
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



	/**�õ�x
	 * @return int
	 */
	public int getiX() {
		return this.iX;
	}




	/**
	 * ����x
	 * @param  iX int
	 */
	public void setiX(int iX) {
		this.iX = iX;
	}


	/**
	 * �õ�y
	 * @return int
	 */
	public int getiY() {
		return this.iY;
	}


	/**
	 * ����y
	 * @param iY int
	 */
	public void setiY(int iY) {
		this.iY = iY;
	}
	/**
	 * ���viewUnitNo����
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
//	 * ������Ӧ��block����
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

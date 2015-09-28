package business.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.MethodUtil;

import com.mansion.tele.business.common.TeleConfig;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.GeoLocation;
import com.mansion.tele.common.UnitNo;
import com.mansion.tele.db.bean.elemnet.PolygonShp;
import com.mansion.tele.util.NumberUtil;
import com.mansion.tele.util.PolygonUtil;

import bo.ViewNameCodePloygon;
import bo.ViewUnitNo;

/**
 * �༭block��Ϣ
 * @author wenxc
 * 
 */
public class CollectBlockInfo {

	/**
	 * ����blockX
	 */
	public static final int LBX = 258048000;
	/**
	 * ����blockY
	 */
	public static final int LBY = 12288000;
	/**
	 * ����blockX
	 */
	public static final int RTX = 1437696000;
	/**
	 * ����blockY
	 */
	public static final int RTY = 798720000;

	/**
	 * �Ƚ���
	 */
	private static Comparator<ViewUnitNo> comparable = new Comparator<ViewUnitNo>() {

		public int compare(ViewUnitNo o1, ViewUnitNo o2) {
			// TODO Auto-generated method stub
			int iValue = 0;
			
			if (o1.getiX() != o2.getiX()) {
				iValue = o1.getiX() - o2.getiX();
			}else if (o1.getiY() != o2.getiY()) {
				iValue =  o1.getiY() - o2.getiY();
			}else if (o1.getBlockNo().getiX() != o2.getBlockNo().getiX()) {
				iValue =  o1.getBlockNo().getiX() - o2.getBlockNo().getiX();
			}else if (o1.getBlockNo().getiY() != o2.getBlockNo().getiY()) {
				iValue = o1.getBlockNo().getiY() - o2.getBlockNo().getiY();
			}else if (o1.getIlevel() != o2.getIlevel()) {
				iValue =  o1.getIlevel() - o2.getIlevel();
			}
			return iValue;
		}

	};

	// /**
	// *
	// * @param viewUnitNoMap
	// * @throws Exception
	// */
	// public void saveBlock(Map<Integer, Set<ViewUnitNo>> viewUnitNoMap) throws
	// Exception{
	//
	// int iCount = 0;
	//
	// LocalDataJdbcModel localDataJdbcModel = new LocalDataJdbcModel(true);
	//
	// for (Integer ilevel : viewUnitNoMap.keySet()) {
	//
	// int iBlockWH[] = TeleConfig.get().getBlockDivInfo(ilevel);
	//
	// for (ViewUnitNo viewUnitNo : viewUnitNoMap.get(ilevel)) {
	//
	// if(viewUnitNo.getBlocklist() != null){
	//
	// for (BlockNo blockNo : viewUnitNo.getBlocklist()) {
	//
	// GeoRect geoRect = blockNo.getRect(iBlockWH[0], iBlockWH[1]);
	//
	// String strLB = ((double)geoRect.getLeftDown().getLongitude() / 2560 /
	// 3600) + " " + ((double)geoRect.getLeftDown().getLatitude() / 2560 /
	// 3600);
	//
	// String strRB = ((double)geoRect.getRightUp().getLongitude() / 2560 /
	// 3600) + " " + ((double)geoRect.getLeftDown().getLatitude() / 2560 /
	// 3600);
	//
	// String strRT = ((double)geoRect.getRightUp().getLongitude()/ 2560 / 3600)
	// + " " + ((double)geoRect.getRightUp().getLatitude() / 2560 / 3600);
	//
	// String strLT = ((double)geoRect.getLeftDown().getLongitude() / 2560 /
	// 3600) + " " + ((double)geoRect.getRightUp().getLatitude() / 2560 / 3600);
	//
	// String strgeom = "POLYGON((" + strLB + "," + strRB + "," + strRT + "," +
	// strLT + "," + strLB + "))";
	//
	//
	//
	// String insql = "insert into blockinfo(level,unitx,unity,blockx,"
	// + "blocky,geom,admincode) values("
	// + ilevel + ","
	// + viewUnitNo.getiX() + ","
	// + viewUnitNo.getiY() + ","
	// + blockNo.iX + ","
	// + blockNo.iY + ","
	// + "GeomFromText('"+ strgeom + "')" + ","
	// + viewUnitNo.getAdmincode()
	// +")";
	//
	// boolean commitFlag = false;
	//
	// if(iCount % 100 == 0 && iCount >= 100){
	//
	// commitFlag = true;
	// }
	//
	// System.out.println("���ڵ���admincode"+viewUnitNo.getAdmincode() + "blockx" +
	// blockNo.iX + "blocky" + blockNo.iY);
	//
	// localDataJdbcModel.executeBatchSql(insql, commitFlag);
	//
	// iCount = iCount + 1;
	// }
	// }
	// }
	//
	//
	// }
	//
	// localDataJdbcModel.executeOnlyBatch();
	//
	// localDataJdbcModel.closeLocalData();
	//
	// }

	/**
	 * ���vidw��Ϣ
	 * 
	 * @param viewNCPMap Map<String, List<ViewNameCodePloygon>>
	 * @return Map<Integer, List<ViewUnitNo>>
	 */
	public Map<Integer, List<ViewUnitNo>> getViewUnitNoInfo(
			Map<String, List<ViewNameCodePloygon>> viewNCPMap) {

		Map<Integer, List<ViewUnitNo>> viewMap = new HashMap<Integer, List<ViewUnitNo>>();

		MethodUtil.saveLevelMap(viewMap);

		for (String strName : viewNCPMap.keySet()) {

			List<ViewNameCodePloygon> vifolList = viewNCPMap.get(strName);

			if (vifolList != null) {

				for (ViewNameCodePloygon viewNameCodePloygon : vifolList) {

					this.makelowData(viewMap, strName, viewNameCodePloygon);
				}

				this.makeHighData(viewMap, strName);
			}
		}

		return viewMap;
	}

	/**
	 * �����Ͳ�
	 * @param viewMap Map<Integer, List<ViewUnitNo>>
	 * @param strName String
	 * @param viewNameCodePloygon ViewNameCodePloygon
	 */
	public void makelowData(Map<Integer, List<ViewUnitNo>> viewMap,
			String strName, ViewNameCodePloygon viewNameCodePloygon) {
		
		for (Integer ilevel : viewMap.keySet()) {

			if (ilevel >= NumberUtil.LEVEL_5) {

				break;
			}

			int[] iblockwidth = TeleConfig.get().getBlockDivInfo(ilevel);

			Map<BlockNo, List<PolygonShp>> divMap = PolygonUtil
					.getOverlapBlock(iblockwidth[0], iblockwidth[1],
							viewNameCodePloygon.getPolygonShp());

			for (BlockNo blockNo : divMap.keySet()) {

				if (divMap.get(blockNo) == null
						|| divMap.get(blockNo).size() == 0) {
					continue;
				} else {

					ViewUnitNo viewUnitNo = new ViewUnitNo();

					viewUnitNo.setAdmincode(Integer
							.parseInt(viewNameCodePloygon.getStrAdminCode()));

					byte blevel = Byte.parseByte(ilevel + "");

					UnitNo unitNo = UnitNo.valueOf(blockNo.toGeoLocation(
							iblockwidth[0], iblockwidth[1]), blevel);

					viewUnitNo.setiX(unitNo.iX);
					viewUnitNo.setiY(unitNo.iY);
					viewUnitNo.setStrName(strName);
					viewUnitNo.setBlockNo(blockNo);
					viewUnitNo.setIlevel(ilevel);
					viewMap.get(ilevel).add(viewUnitNo);

				}
			}
			// compuUnitNo(viewMap.get(ilevel));
		}
	}

	/**
	 * �����߲����
	 * 
	 * @param viewMap Map<Integer, List<ViewUnitNo>>
	 * @param strName String
	 */
	public void makeHighData(Map<Integer, List<ViewUnitNo>> viewMap,
			String strName) {
		for (int i = NumberUtil.LEVEL_5; i <= NumberUtil.LEVEL_7; i++) {

			GeoLocation lbLocation = GeoLocation.valueOf(this.LBX, this.LBY);

			GeoLocation rtLocation = GeoLocation.valueOf(this.RTX, this.RTY);

			int[] iBlockWH = TeleConfig.get().getBlockDivInfo(i);

			BlockNo lbBlockNo = BlockNo.valueOf(lbLocation, iBlockWH[0],
					iBlockWH[1]);

			BlockNo rtBlockNo = BlockNo.valueOf(rtLocation, iBlockWH[0],
					iBlockWH[1]);

			for (int j = lbBlockNo.getiX(); j <= rtBlockNo.getiX() + 1; j++) {

				for (int j2 = lbBlockNo.getiY(); j2 < rtBlockNo.getiY() + 1; j2++) {

					BlockNo blockNo = new BlockNo(j, j2);

					ViewUnitNo viewUnitNo = new ViewUnitNo();
					byte blevel = Byte.parseByte(i + "");

					UnitNo unitNo = UnitNo.valueOf(
							blockNo.toGeoLocation(iBlockWH[0], iBlockWH[1]),
							blevel);

					viewUnitNo.setiX(unitNo.iX);
					viewUnitNo.setiY(unitNo.iY);
					viewUnitNo.setStrName(strName);
					viewUnitNo.setBlockNo(blockNo);
					viewUnitNo.setIlevel(i);
					viewMap.get(i).add(viewUnitNo);

				}

			}
		}
	}

	/**
	 * ����
	 * 
	 * @param list List<ViewUnitNo>
	 */
	public static void sortUnitNo(List<ViewUnitNo> list) {

		Collections.sort(list, CollectBlockInfo.comparable);

	}

}

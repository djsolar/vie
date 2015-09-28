package business.dao;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.JdbcUtil;
import util.ReadFile;

import bo.ViewNameCodePloygon;

import com.mansion.tele.common.GeoLocation;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.db.bean.elemnet.PolygonShp;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.GeoUtil;

/**
 * �������ļ��ж�ȡ������״
 * @author wenxc
 * 
 */
public class FromFileGetRectDao {

	/**
	 * ��þ��μ���
	 * 
	 * @return Map<String, List<ViewNameCodePloygon>>
	 * @throws Exception
	 *             1
	 */
	public Map<String, List<ViewNameCodePloygon>> getAllGeoRect()
			throws Exception {

		String cityname = ReadFile.readProFileProvince();

		cityname = new String(cityname.getBytes("ISO8859-1"), "UTF-8");

		if ("".equals(cityname)) {

			return null;

		}

		Map<String, List<ViewNameCodePloygon>> viewNCPMap 
			= new HashMap<String, List<ViewNameCodePloygon>>();

		String[] arrcity = null;

		arrcity = this.getNameArr(cityname, arrcity);

		if (arrcity != null) {

			this.addPloygon(arrcity, viewNCPMap);

		}

		return viewNCPMap;

	}

	/**
	 * �õ�ʡ�������
	 * 
	 * @param cityname String
	 * @param arrcity String[]
	 * @return String[]
	 */
	public static String[] getNameArr(String cityname, String[] arrcity) {

		String sign1 = ";";
		
		String sign2 = "��";
		
		String[] newarrcity = arrcity;
		
		if (cityname.contains(sign1)) {

			newarrcity = cityname.split(sign1);

		} else if (cityname.contains(sign2)) {

			newarrcity = cityname.split(sign2);
		} else {
			newarrcity = new String[] { cityname };
		}
		return newarrcity;
	}

	// /**
	// * �򼯺�����Ӿ��η�Χ
	// * @param geoList
	// * @param arrcity
	// * @throws Exception
	// * @throws SQLException
	// */
	// public void addRectList(Map<Integer,List<GeoRect>> geoMap, String[]
	// arrcity)
	// throws Exception, SQLException {
	// Connection connection = JdbcUtil.getConnection();
	//
	// Statement statement = connection.createStatement();
	//
	// for (int i = 0; i < arrcity.length; i++) {
	//
	// String sql = "SELECT \"minX\",\"minY\",\"maxX\",\"maxY\" ,admin_code"
	// + " FROM geo.search_address_code where province = '"
	// + arrcity[i]
	// + "' "
	// + "and (admin_code :: varchar not like '%0000' or "
	// +
	// "admin_code = 110000 or admin_code = 120000 or admin_code = 310000 or admin_code = 500000) and  "
	// +
	// "(admin_code :: varchar like '%00' or  admin_code :: varchar like '__90__')";
	//
	//
	// ResultSet rs = statement.executeQuery(sql);
	//
	// while(rs.next()){
	//
	// long minx = rs.getLong(1);
	//
	// long miny = rs.getLong(2);
	//
	// long maxx = rs.getLong(3);
	//
	// long maxy = rs.getLong(4);
	//
	// int iAdmincode = rs.getInt(5);
	//
	// iAdmincode = iAdmincode / 10000 * 10000;
	//
	// if(geoMap.get(iAdmincode) == null){
	//
	// List<GeoRect> geolist = new ArrayList<GeoRect>();
	//
	// geoMap.put(iAdmincode, geolist);
	//
	// }
	//
	// List<GeoRect> geolist = geoMap.get(iAdmincode);
	//
	// GeoLocation geoLBLocation = new GeoLocation();
	//
	// geoLBLocation.setLongitude((int) minx);
	//
	// geoLBLocation.setLatitude((int) miny);
	//
	// GeoLocation geoRTLocation = new GeoLocation();
	//
	// geoRTLocation.setLongitude((int) maxx);
	//
	// geoRTLocation.setLatitude((int) maxy);
	//
	// GeoRect geoRect = new GeoRect();
	//
	// geoRect.setLeftDown(geoLBLocation);
	//
	// geoRect.setRightUp(geoRTLocation);
	//
	// geolist.add(geoRect);
	//
	// System.out.println("��ӵ�����" + geoLBLocation.Longitude + ";" +
	// geoLBLocation.Latitude);
	// }
	//
	// }
	// }
	/**
	 * ���������
	 * @param arrcity String[]
	 * @param viewNCPMap Map<String, List<ViewNameCodePloygon>>
	 * @throws Exception 1
	 */
	public void addPloygon(String[] arrcity,
			Map<String, List<ViewNameCodePloygon>> viewNCPMap) throws Exception {

		Connection connection = JdbcUtil.getLocalConnection();

		Statement statement = connection.createStatement();

		for (int i = 0; i < arrcity.length; i++) {

			String sql = "select admin_code, strname, strgeom1 from geo.location where strname = '"
					+ arrcity[i] + "'";

			ResultSet rs = statement.executeQuery(sql);

			String strCName = arrcity[i];

			if (viewNCPMap.get(strCName) == null) {

				List<ViewNameCodePloygon> viewlist = new ArrayList<ViewNameCodePloygon>();

				viewNCPMap.put(strCName, viewlist);
			}

			List<ViewNameCodePloygon> viewlist = viewNCPMap.get(strCName);

			while (rs.next()) {

				String stradmincode = rs.getString(1);

				String strName = rs.getString(2);

				final int state = 3;
				
				String strgeom = rs.getString(state);

				ViewNameCodePloygon viewNameCodePloygon = new ViewNameCodePloygon();

				viewNameCodePloygon.setStrAdminCode(stradmincode);

				viewNameCodePloygon.setStrName(strName);

				viewNameCodePloygon.setPolygonShp(this.fromLocationGeom(strgeom));

				viewlist.add(viewNameCodePloygon);
			}
		}

	}

	/**
	 * ��Location���
	 * 
	 * @param strgeom String
	 * @return PolygonShp
	 */
	private PolygonShp fromLocationGeom(String strgeom) {

		PolygonShp polygonShp = new PolygonShp();

		String[] arrpoint = strgeom.split(",");

		List<ShpPoint> shpList = new ArrayList<ShpPoint>();

		for (String string2 : arrpoint) {

			String[] xy = string2.split(" ");
			
			final int num = 2560 * 3600;

			ShpPoint shpPoint = new ShpPoint(
					(int) ((Double.parseDouble(xy[0])) * num),
					(int) ((Double.parseDouble(xy[1])) * num));

			System.out.println(shpPoint.x + "; " + shpPoint.y);

			shpList.add(shpPoint);

		}

		if (shpList.size() > 0
				&& shpList.get(0).x != shpList.get(shpList.size() - 1).x
				&& shpList.get(0).y != shpList.get(shpList.size() - 1).y) {

			shpList.add(shpList.get(0));

		}

		GeoRect rect = GeoUtil.getRectByPoints(shpList);
		polygonShp.setLbCoordinate(ShpPoint.valueOf(rect.getLeftDown()));
		polygonShp.setRtCoordinate(ShpPoint.valueOf(rect.getRightUp()));
		polygonShp.setCoordinate(shpList);
		return polygonShp;
	}
}

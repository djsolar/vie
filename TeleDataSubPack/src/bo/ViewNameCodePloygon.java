package bo;

import com.mansion.tele.db.bean.elemnet.PolygonShp;
/**
 * bo
 * @author wenxc
 *
 */
public class ViewNameCodePloygon {
	
	/**
	 * 行政区code
	 */
	private String strAdminCode;
	/**
	 * 行政区名称
	 */
	private String strName;
	/**
	 * 行政形状
	 */
	private PolygonShp polygonShp;

	/**
	 * 得到AdminCode
	 * @return String
	 */
	public String getStrAdminCode() {
		return this.strAdminCode;
	}

	/**
	 * 设置AdminCode
	 * @param strAdminCode String
	 */
	public void setStrAdminCode(String strAdminCode) {
		this.strAdminCode = strAdminCode;
	}

	/**
	 * 得到name
	 * @return String
	 */
	public String getStrName() {
		return this.strName;
	}

	/**
	 * 设置name
	 * @param strName String
	 */
	public void setStrName(String strName) {
		this.strName = strName;
	}

	/**
	 * 得到行政面
	 * @return PolygonShp
	 */
	public PolygonShp getPolygonShp() {
		return this.polygonShp;
	}

	/**
	 * 设置行政面
	 * @param polygonShp PolygonShp
	 */
	public void setPolygonShp(PolygonShp polygonShp) {
		this.polygonShp = polygonShp;
	}
	
	
	
	
	
	

}

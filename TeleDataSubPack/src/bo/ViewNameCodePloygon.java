package bo;

import com.mansion.tele.db.bean.elemnet.PolygonShp;
/**
 * bo
 * @author wenxc
 *
 */
public class ViewNameCodePloygon {
	
	/**
	 * ������code
	 */
	private String strAdminCode;
	/**
	 * ����������
	 */
	private String strName;
	/**
	 * ������״
	 */
	private PolygonShp polygonShp;

	/**
	 * �õ�AdminCode
	 * @return String
	 */
	public String getStrAdminCode() {
		return this.strAdminCode;
	}

	/**
	 * ����AdminCode
	 * @param strAdminCode String
	 */
	public void setStrAdminCode(String strAdminCode) {
		this.strAdminCode = strAdminCode;
	}

	/**
	 * �õ�name
	 * @return String
	 */
	public String getStrName() {
		return this.strName;
	}

	/**
	 * ����name
	 * @param strName String
	 */
	public void setStrName(String strName) {
		this.strName = strName;
	}

	/**
	 * �õ�������
	 * @return PolygonShp
	 */
	public PolygonShp getPolygonShp() {
		return this.polygonShp;
	}

	/**
	 * ����������
	 * @param polygonShp PolygonShp
	 */
	public void setPolygonShp(PolygonShp polygonShp) {
		this.polygonShp = polygonShp;
	}
	
	
	
	
	
	

}

package com.mansion.tele.db.daoImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.mansion.tele.db.bean.elemnet.Element;
import com.mansion.tele.db.bean.elemnet.Intersection_O;
import com.mansion.tele.db.bean.elemnet.MapSign;
import com.mansion.tele.db.bean.elemnet.Node;
import com.mansion.tele.db.bean.elemnet.Road;
import com.mansion.tele.db.bean.elemnet.Service;

public class TeleDao {
	@SuppressWarnings("unchecked")
	public List<Node> getNodeList(String taskNo,
			Session session){
		List<Node> result = new ArrayList<Node>();
		session.clear();
		String hql = SQLScript.FROM + Node.class.getName() + SQLScript.STRLEFTJOIN
				+ SQLScript.STRID + taskNo + SQLScript.STRMINID
				+ SQLScript.STRAND + taskNo + SQLScript.STRMAXID;
		Query query = session.createQuery(hql);
		query.setReadOnly(true);
		result.addAll(query.list());
		return result;
	}
	@SuppressWarnings("unchecked")
	public List<Road> getRoadList(String taskNo,
			Session session){
		List<Road> result = new ArrayList<Road>();
		session.clear();
		String hql = SQLScript.FROM + Road.class.getName() + SQLScript.STRLEFTJOIN
		+ SQLScript.STRID + taskNo + SQLScript.STRMINID
		+ SQLScript.STRAND + taskNo + SQLScript.STRMAXID;
		Query query = session.createQuery(hql);
		query.setReadOnly(true);
		result.addAll(query.list());
		return result;
	}
	@SuppressWarnings("unchecked")
	public List<Intersection_O> getIntersectionList(String taskNo,
			Session session) {
		Criterion criterion = Restrictions.like("astInnerRoadList",taskNo
				.trim() + "%");
		Criteria criteria = session.createCriteria(Intersection_O.class);
		criteria.add(criterion);
		return  criteria.list();
	}
	
	/**
	 * 根据指定属性获取母库数据
	 * @param <T> 继承element的要素类
	 * @param element 要素类对象
	 * @param astTaskNos  母库mesh号集合
	 * @param session session对象
	 * @param propertyName 属性名
	 * @return 数据集合
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Element> List<T> getEleDataFromOrg(Class<?> element,
			List<String> astTaskNos, Session session, String propertyName){
		List<T> result = new ArrayList<T>();
		int i = 0;
		Criterion criterion = Restrictions.like(propertyName, astTaskNos.get(i)
				.trim() + "%");
		while(i < astTaskNos.size()){
			if (i != 0 && i % 10 == 0) {
				Criteria criteria = session.createCriteria(element);
				criteria.add(criterion);
				List<T> list = criteria.list();
				if (list != null && list.size() > 0) {
					result.addAll(list);
				}
				criterion = Restrictions.like(propertyName, astTaskNos.get(i)
						.trim() + "%");
			}else if (i != 0) {
				criterion = Restrictions.or(criterion, Restrictions.like(propertyName, astTaskNos.get(i).trim() + "%"));
			}
			i++;
		}
		Criteria criteria = session.createCriteria(element);
		criteria.add(criterion);
		List<T> list = criteria.list();
		if (list != null && list.size() > 0) {
			result.addAll(list);
		}
		return result;
	}
	/**
	 * 获得公交站点数据
	 * @param session
	 * @return
	 */
	public static <T extends Element>List<T> getStopList(Session session, String strTaskNo, Class<T> classes){
		String hql = "from " + classes.getName() 
				+  SQLScript.SMID + strTaskNo + SQLScript.STRMINID 
				+ SQLScript.SMIDRAND + strTaskNo + SQLScript.STRMAXID;
		if(!session.isOpen()){
			System.out.println("????");
		}
		Query query = session.createQuery(hql);
		List<T> list = query.list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static List<?> getAllPlusDataObject(Class<?> classes, Session session) {
		String hqlString = "from " + classes.getName();
		List<?> pluseleList = session.createQuery(hqlString).list();
		return pluseleList;
	}
	
	
	public static List<Service> getServiceList(String taskNo,Session session){
		String hqlString = "from " + Service.class.getName() + SQLScript.STRLEFTJOIN
				+ SQLScript.STRID + taskNo + SQLScript.STRMINID
				+ SQLScript.STRAND + taskNo + SQLScript.STRMAXID;
		List<Service> servicelist = session.createQuery(hqlString).list();
		return servicelist;
	}
	
	public static List<MapSign> getMapSign(String taskNo, Session session){
		String hqlString = "from " + MapSign.class.getName() + SQLScript.STRLEFTJOIN
				+ SQLScript.STRID + taskNo + SQLScript.STRMINID
				+ SQLScript.STRAND + taskNo + SQLScript.STRMAXID;
		List<MapSign> mapsignlist = session.createQuery(hqlString).list();
		return mapsignlist;
	}
}

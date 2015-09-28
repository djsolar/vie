package com.sunmap.businessDao.makeJson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.sunmap.been.City;
import com.sunmap.been.Province;
import com.sunmap.util.Constant;

public class MakeJsonFile {
	
	public static List<Province> data = new ArrayList<Province>();
	
	static Comparator<Province> comparator = new Comparator<Province>() {

		public int compare(Province o1, Province o2) {
			String pinyin1 = o1.getProvince_pinyin();
			String pinyin2 = o2.getProvince_pinyin();
			String code1 = o1.getAdmin_code();
			String code2 = o2.getAdmin_code();
			int pri1 = o1.getPri();
			int pri2 = o2.getPri();
			if(pri1 == pri2){
				if(pinyin1.equals(pinyin2)){
					return code1.compareTo(code2);
				}
				else{
					return pinyin1.compareTo(pinyin2);
				}
			}
			else{
				return pri1 - pri2;
			}
		}
	};
	
	static{
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void makeJson() throws SQLException, IOException{
		this.readAdminData();
		Gson gson = new Gson();
		String json = gson.toJson(data);
		System.out.println(data.size());
        System.out.println("json = " + json);
        String fileName = Constant.outPath + File.separator + "meta_data.json";
        File file = new File(fileName);
        if(file.exists()){
        	file.delete();
        }
        file.createNewFile();
        FileWriter fw = new FileWriter(file);
        fw.write(json);
        fw.flush();
        fw.close();
//        this.check();
	}
	
	public void readAdminData() throws SQLException{
		Connection con = DriverManager.getConnection(Constant.emg_url, Constant.username, Constant.password);
		String sql = "SELECT distinct admin_code, province, city, province_pinyin FROM geo.address_code"
				+ " where admin_code like '%90__' or admin_code like '%00' order by admin_code";
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			String admin_code = rs.getString(1);
			String province = rs.getString(2);
			String city = rs.getString(3);
			String province_pinyin = rs.getString(4);
			// 省级code
			if(admin_code.endsWith("0000")){
				Province pro = new Province();
				List<City> proCity = new ArrayList<City>();
				pro.setProvinceName(province);
				pro.setSize(0);
				pro.setProCity(proCity);
				pro.setAdmin_code(admin_code);
				pro.setProvince_pinyin(province_pinyin);
				// 直辖市
				if(admin_code.equals("110000") || admin_code.equals("120000")
						|| admin_code.equals("500000") || admin_code.equals("310000")){
					pro.setPri(Constant.prior_city);
				}
				else if(admin_code.equals("810000") || admin_code.equals("820000")){
					pro.setPri(Constant.Macco_Hongkong);
				}
				else{
					pro.setPri(Constant.normal_city);
				}
				data.add(pro);
			}
			else{
				Province pro = new Province();
				pro.setAdmin_code(admin_code.substring(0, 2) + "0000");
				pro.setPri(Constant.normal_city);
				pro.setProvince_pinyin(province_pinyin);
				Collections.sort(data, comparator);
				int index = Collections.binarySearch(data, pro, comparator);
				if(index >= 0){
					Province p = data.get(index);
					City c = new City();
					List<City> proCity = p.getProCity();
					c.setName(city);
					c.setSize(0);
					proCity.add(c);
				}
				else{
					System.err.println("找不到该城市");
				}
			}
		}
		rs.close();
		ps.close();
		con.close();
		Collections.sort(data, comparator);
		for(int i=0;i<data.size();i++){
			Province pro = data.get(i);
			List<City> proCitys = pro.getProCity();
			// 直辖市
			if(proCitys.size() == 0){
				String fileName = Constant.outPath + File.separator + pro.getProvinceName();
				File file = new File(fileName);
				pro.setSize(file.length());
			}
			else{
				long proLength = 0;
				for(int j=0;j<proCitys.size();j++){
					City city = proCitys.get(j);
					String fileName = Constant.outPath + File.separator + city.getName();
					File file = new File(fileName);
					city.setSize(file.length());
					proLength += file.length();
				}
				pro.setSize(proLength);
			}
		}
		// 全国基础数据
		Province allData = new Province();
		allData.setProvinceName("全国基础地图");
		String fileName = Constant.outPath + File.separator + "全国基础地图";
		File file = new File(fileName);
		allData.setSize(file.length());
		data.add(0, allData);
	}
	
	public void check() throws SQLException{
		int count = 0;
		List<String> citys = new ArrayList<String>();
		for(int i=0;i<data.size();i++){
			Province pro = data.get(i);
			List<City> proCitys = pro.getProCity();
			for(int j=0;j<proCitys.size();j++){
				count ++;
				City city = proCitys.get(j);
				citys.add(city.getName());
				String fileName = Constant.outPath + File.separator + city.getName();
				File file = new File(fileName);
				if(!file.exists()){
					System.out.println("给出的文件没有的：" + city.getName());
				}
			}
		}
		File file = new File(Constant.outPath);
		File[] files = file.listFiles();
		for(File f : files){
			String name = f.getName();
			if(!citys.contains(name)){
				System.out.println("给出文件多出的："+name);
			}
		}
		System.out.println("json:"+count);
	}
}

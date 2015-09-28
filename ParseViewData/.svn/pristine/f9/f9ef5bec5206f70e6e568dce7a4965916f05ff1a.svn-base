package com.sunmap.businessDao.TransForm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import com.sunmap.been.City;
import com.sunmap.been.Province;
import com.sunmap.util.Constant;

public class Transform {
	
	static{
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Comparator<Province> nameComparator = new Comparator<Province>() {

		public int compare(Province o1, Province o2) {
			return o1.getProvinceName().compareTo(o2.getProvinceName());
		}
	};
	
	String dataPath = "result";
	
	public List<String> getRegionTomake() throws IOException{
		List<String> result = new ArrayList<String>();
		FileReader fr = new FileReader("城市列表.txt");
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		String[] regions = null;
		br.readLine();
		while((line = br.readLine()) != null){
			byte[] strBytes = line.getBytes();
			line.replace("\\s", "");
			line = new String(strBytes, "UTF-8");
			regions = line.split("，");
			if(regions != null){
				for(String s : regions){
					result.add(s);
				}
			}
			else{
				System.out.println("请检查输入！");
			}
		}
		br.close();
		return result;
	}
	
	public void makeFile(List<String> regions) throws SQLException, IOException{
		new File(dataPath).mkdir();
		List<Province> data = new ArrayList<Province>();
		Connection con = DriverManager.getConnection(Constant.emg_url, Constant.username, Constant.password);
		String sql = "select admin_code, province, city from geo.address_code where "
				+ "(admin_code like '%90__' or admin_code like '%00') and (province like ? or city like ?)";
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = null;
		for(int i=0;i<regions.size();i++){
			String region = "%" + regions.get(i) + "%";
			ps.setString(1, region);
			ps.setString(2, region);
			rs = ps.executeQuery();
			while(rs.next()){
				String admin_code = rs.getString(1);
				String province = rs.getString(2);
				String city = rs.getString(3);
				Province pro = new Province();
				pro.setProvinceName(province);
				if(admin_code.equals("810000")||admin_code.equals("820000")||admin_code.equals("110000")
						||admin_code.equals("120000")||admin_code.equals("500000")||admin_code.equals("310000")){
					data.add(pro);
				}
				else if(!admin_code.endsWith("0000")){
					City c = new City();
					c.setName(city);
					Collections.sort(data, nameComparator);
					int index = Collections.binarySearch(data, pro, nameComparator);
					if(index >= 0){
						Province p = data.get(index);
						if(!p.getProCity().contains(c)){
							p.getProCity().add(c);
						}
					}
					else{
						List<City> cities = new ArrayList<City>();
						cities.add(c);
						pro.setProCity(cities);
						data.add(pro);
					}
				}
			}
		}
		Province p = new Province();
		p.setProvinceName("全国基础地图");
		data.add(p);
		for(int j=0;j<data.size();j++){
			Province province = data.get(j);
			this.write(province);
		}
		rs.close();
		ps.close();
		con.close();
	}
	
	private void write(Province province) throws IOException{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		// 直辖市
		if(province.getProCity() == null || province.getProCity().size() == 0){
			String filePath = dataPath + File.separator + province.getProvinceName();
			File inFile = new File(Constant.outPath + File.separator + province.getProvinceName());
			fis = new FileInputStream(inFile);
			fos = new FileOutputStream(filePath);
			byte[] b = new byte[(int) inFile.length()];
			fis.read(b);
			fos.write(b);
			fos.flush();
			fos.close();
			fis.close();
			System.out.println(province.getProvinceName() + "完成");
		}
		else{
			File floder = new File(dataPath + File.separator + province.getProvinceName());
			floder.mkdir();
			for(int i=0;i<province.getProCity().size();i++){
				City city = province.getProCity().get(i);
				String filePath = floder.getAbsolutePath() + File.separator + city.getName();
				File inFile = new File(Constant.outPath + File.separator + city.getName());
				fis = new FileInputStream(inFile);
				fos = new FileOutputStream(filePath);
				byte[] b = new byte[(int) inFile.length()];
				fis.read(b);
				fos.write(b);
				fos.flush();
				fos.close();
				fis.close();
				System.out.println(city.getName() + "完成");
			}
			System.out.println(province.getProvinceName() + "完成");
		}
	}
	
	public static void delPath(File file) {
		File[] files = file.listFiles();
		if (files != null)
			for (File f : files)
				delPath(f);
		file.delete();
	}
	
	public void initPath(){
		File dataPathFile = new File(dataPath);
		if(dataPathFile.exists()){
			delPath(dataPathFile);
		}
	}
	
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		System.out.println("制作开始");
		Transform tf = new Transform();
		tf.initPath();
		List<String> regions = tf.getRegionTomake();
		tf.makeFile(regions);
		System.out.println(regions.size());
		System.out.println(regions.get(0));
		System.out.println("制作结束");
	}

}

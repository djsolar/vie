package com.sunmap.teleview.view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.sql.SQLException;

import com.sunmap.teleview.util.AdminCodes;

/**
 * 描画信息数据
 * @author lijingru
 *
 */
public class DrawParams {

	public int dpi = 240;
	public int widthPixels = 0;//窗口的像素宽度
	public int heightPixels = 0;//窗口的像素高度
	public double one_2560th_per_Pixels_X = 0.0; // 1像素等于多少1/2560经纬度x
	public double one_2560th_per_Pixels_Y = 0.0;// 1像素等级多少1/2560经纬度y
	// 屏幕中心点XY
	public int scaleUINO = 3;//  缺省值 - 初始设置第3个比例尺
	public int currMapScale = 50;//  100M/cm
	public Point imageCentre = new Point(1072719135, 367798020);//地图中心点
	public Point panelCentre = new Point(imageCentre.x, imageCentre.y);//窗口中心点
	//View文件路径
	public String WindowsTeleFile = "windows.teledata.path";
	public String LinuxTeleFile = "linux.teledata.path";
	
	public String getCurDrawParams() {
		StringBuffer strb = new StringBuffer();
		strb.append("com.sunmap.teleview.map.lon=" + imageCentre.x + ",");
		strb.append("com.sunmap.teleview.map.lat=" + imageCentre.y + ",");
		strb.append("com.sunmap.teleview.scaleNO=" + scaleUINO + ",");
		strb.append("windows.teledata.path=" + WindowsTeleFile + ",");
		strb.append("linux.teledata.path=" + LinuxTeleFile + ",");
		return strb.toString();
	}
	
	public void setCurDrawParams(String string){
		try {
			String[] CurDrawParam = string.split(",");
			for (int i = 0; i < CurDrawParam.length; i++) {
				String[] drawParam = CurDrawParam[i].split("=");
				if (drawParam[0].equals("com.sunmap.teleview.map.lon")) {
					imageCentre.x = Integer.parseInt(drawParam[1]);
					panelCentre.x = imageCentre.x;
				}
				if (drawParam[0].equals("com.sunmap.teleview.map.lat")) {
					imageCentre.y = Integer.parseInt(drawParam[1]);
					panelCentre.y = imageCentre.y;
				}
				if (drawParam[0].equals("com.sunmap.teleview.scaleNO")) {
					scaleUINO = Integer.parseInt(drawParam[1]);
				}
				if (drawParam[0].equals("windows.teledata.path")) {
					WindowsTeleFile = drawParam[1];
				}
				if (drawParam[0].equals("linux.teledata.path")) {
					LinuxTeleFile = drawParam[1];
				}
			}
			updataMapData();
		} catch (Exception e) {
			System.out.println("第一次使用，使用默认值");
		}
		
	}
	
	public void init(){
		// 得到屏幕像素 获取DPI
		this.dpi = Toolkit.getDefaultToolkit().getScreenResolution();// 一英寸包含的像素个数
		double dpi_cm = this.dpi / 2.54f; // 一厘米包含的像素个数
		this.one_2560th_per_Pixels_X = 100 * 2560 * 3600 / dpi_cm / 111111;
		this.one_2560th_per_Pixels_Y = 100 * 2560 * 3600 / dpi_cm / 111111;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.widthPixels = screenSize.width;
		this.heightPixels = screenSize.height;
		//初始化省名称
		try {
			//城市表设置
			AdminCodes.getProvince();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	

	/**
	 * 更新描画参数
	 * @param flag  是否更新矩形
	 */
	public void updataMapData() {
		switch (scaleUINO) {
		case 1:
			currMapScale = 10;
			break;
		case 2:
			currMapScale = 25;
			break;
		case 3:
			currMapScale = 50;
			break;
		case 4:
			currMapScale = 100;
			break;
		case 5:
			currMapScale = 200;
			break;
		case 6:
			currMapScale = 500;
			break;
		case 7:
			currMapScale = 1000;
			break;
		case 8:
			currMapScale = 2000;
			break;
		case 9:
			currMapScale = 5000;
			break;
		case 10:
			currMapScale = 10000;
			break;
		case 11:
			currMapScale = 20000;
			break;
		case 12:
			currMapScale = 50000;
			break;
		case 13:
			currMapScale = 100000;
			break;
		case 14:
			currMapScale = 200000;
			break;
		case 15:
			currMapScale = 1000000;
			break;
		}
		
	}
	
	/**
	 * 设置中心点
	 * @param geo 中线点经纬度   单位1/2560秒
	 */
	public void setCenterGeo(Point geo){
		if (geo != null&&geo.x != 0 && geo.y != 0) {
			imageCentre.x = geo.x;
			imageCentre.y = geo.y;
		}
	}
	
	
	
	/**
	 *  像素转经纬度
	 * @param geo   中心点坐标 单位1/2560秒  如果传入 0.0  采用默认中心点经纬度
	 * @param point	屏幕中想要的点   （单位：像素）
	 * @return
	 */
	public Point PixToGeo(Point2D.Float point) {
		int x = (int) (imageCentre.x + (point.x - widthPixels/2) * one_2560th_per_Pixels_X * currMapScale / 100 + 0.5);
		int y = (int) (imageCentre.y - (point.y - heightPixels/2) * one_2560th_per_Pixels_Y * currMapScale / 100 + 0.5);
		return new Point(x, y);

	}
	public Point PixToGeo(float x,float y) {
		Point point2 = PixToGeo( new Point2D.Float(x, y));
		return point2;

	}

	/**
	 * 经纬度转像素
	 * @param point	经纬度点（单位：1/2560秒）
	 * @return
	 */
	public Point2D.Double GeoToPix(Point point) {
		double stscreenX = (point.x - imageCentre.x) / (one_2560th_per_Pixels_X * currMapScale / 100) + widthPixels/2;
		double stscreenY = (imageCentre.y - point.y) / (one_2560th_per_Pixels_Y * currMapScale / 100) + heightPixels/2;
		Point2D.Double point2d = new Point2D.Double(stscreenX, stscreenY);
		return point2d;

	}
	
	public Point2D.Double GeoToPix(int longtiude, int latitude) {
		Point2D.Double point2d = GeoToPix(new Point(longtiude, latitude));
		return point2d;
	}

	/**
	 * 经纬度转像素
	 * @param 经纬度差值  (绝对值)
	 * @return 像素差值  (绝对值)
	 */
	public int GeoToPix (int cha) {
		int mark = cha >= 0 ? 1 : -1;
		int stscreenX = (int) (cha / (one_2560th_per_Pixels_X * currMapScale / 100) + mark * 0.5) ;
		return stscreenX;
	}

	/**
	 * 减小比例尺
	 */
	public void scaleDown() {
		scaleUINO++;
		if (scaleUINO >= 15) {
			scaleUINO = 15;
		}
		updataMapData();// 更新描画参数
	}

	/**
	 * 增大比例尺
	 */
	public void scaleUp() {
		scaleUINO--;
		if (scaleUINO <= 1) {
			scaleUINO = 1;
		}
		updataMapData();// 更新描画参数
	}
}

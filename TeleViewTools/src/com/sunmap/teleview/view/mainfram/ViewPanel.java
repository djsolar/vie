package com.sunmap.teleview.view.mainfram;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.view.assist.style.ShapeManage;

/**
 * @author lijingru
 *
 */
public class ViewPanel extends JPanel implements MouseListener,MouseMotionListener,MouseWheelListener{

	public static final Color backColor = new Color(214, 197, 140);// 背景色	
	private static final long serialVersionUID = 1L;
	public ImageInfo imageInfo = new ImageInfo();
	private String scaleTxt = "";//显示比例尺
	private int scaleUpHigth ;//文字偏移位置
	private Point startPoint=new Point();//按下点的坐标
	private long lastMouseWheelTime = 0;
	
	private Lock lock = new ReentrantLock();

	/**
	 * 初始化
	 */
	public void initComponents() {
		addMouseMotionListener(this);// 鼠标事件
		addMouseListener(this);
		addMouseWheelListener(this);
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(backColor);
		g.fillRect(-200, -200, Controller.drawParams.widthPixels+200,Controller.drawParams.heightPixels+200);
		//描画位置
		int xPix = -(Controller.drawParams.widthPixels-getWidth())/2;
		int yPix = -(Controller.drawParams.heightPixels-getHeight())/2;
		lock.lock();
		try{
			int x = Controller.drawParams.GeoToPix(Controller.drawParams.panelCentre.x - imageInfo.imagePoint.x);
			int y = Controller.drawParams.GeoToPix(Controller.drawParams.panelCentre.y - imageInfo.imagePoint.y);
			g.drawImage(imageInfo.image, xPix-x , yPix+y, Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, null);
		}finally{
			lock.unlock();
		}
		//中心标记
		g.setColor(new Color(255,0,0));
		Shape s = ShapeManage.crossShape(new Point2D.Float(xPix+720, yPix+450), 10);
		((Graphics2D) g).fill(s);
		//比例尺
		g.setColor(Color.BLACK);
		((Graphics2D) g).setStroke(new BasicStroke(2.0f));
		g.drawString(scaleTxt, 0, getHeight()-scaleUpHigth-15);
		Shape line = ShapeManage.rule(new Point2D.Float(5, getHeight()-scaleUpHigth-10), Controller.drawParams.dpi / 2.54f);
		((Graphics2D) g).draw(line);
 	}
	
	public void combinImage(ImageInfo newImageInfo){
		// 同步地图
		if (newImageInfo != null) {//未同步，要修改
			Graphics2D g1 = (Graphics2D) newImageInfo.image.getGraphics();
			for (int i = 0; i < newImageInfo.imagList.size(); i++) {
				g1.drawImage(newImageInfo.imagList.get(i), 0, 0, Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, null);
			}
			lock.lock();
			try{
				this.imageInfo.image = newImageInfo.image;
				this.imageInfo.imagePoint.x = newImageInfo.imagePoint.x;
				this.imageInfo.imagePoint.y = newImageInfo.imagePoint.y;
			}finally{
				lock.unlock();
			}
		}
		repaint(0, 0, Controller.drawParams.widthPixels, Controller.drawParams.heightPixels);
	}

	/**
	 * 设置比例尺
	 * @param str
	 */
	public void setScale(String str) {
		if (str != null) {
			this.scaleTxt = str;
		}
	}
	/**
	 * 设置比例尺显示位置
	 * @param y
	 */
	public void setScaleHight(int y) {
		this.scaleUpHigth = y;
	}	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int xPix = (Controller.drawParams.widthPixels-getWidth())/2;
		int yPix = (Controller.drawParams.heightPixels-getHeight())/2;
		int x = e.getX()+xPix;
		int y = e.getY()+yPix;
		Point point = Controller.drawParams.PixToGeo(new Point2D.Float(x, y));
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
			Controller.calcPickUpInfo(new Point(x, y));
		}
		Controller.showStatusInfo(point);
	}
	

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		startPoint = Controller.drawParams.PixToGeo(x, y);
		Controller.showStatusInfo(startPoint);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		Point endPoint = Controller.drawParams.PixToGeo(x, y);
		int xpianyi = endPoint.x - startPoint.x; 
		int ypianyi = endPoint.y - startPoint.y;
		Controller.moveMap(new Point(Controller.drawParams.imageCentre.x - xpianyi,
				Controller.drawParams.imageCentre.y - ypianyi));
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		//拖拽
		int x = e.getX();
		int y = e.getY();
		Point endPoint = Controller.drawParams.PixToGeo(x, y);
		int xpianyi = endPoint.x - startPoint.x;
		int ypianyi = endPoint.y - startPoint.y;
		Controller.drawParams.panelCentre.x = Controller.drawParams.imageCentre.x  - xpianyi;
		Controller.drawParams.panelCentre.y = Controller.drawParams.imageCentre.y  - ypianyi;
		Controller.combinImage(null);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		int xPix = (Controller.drawParams.widthPixels-getWidth())/2;
		int yPix = (Controller.drawParams.heightPixels-getHeight())/2;
		int x = e.getX()+xPix;
		int y = e.getY()+yPix;
		Point endPoint = Controller.drawParams.PixToGeo(x, y);
		if (Controller.UI != null) {
			Controller.showStatusInfo(endPoint);
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int xPix = (Controller.drawParams.widthPixels-getWidth())/2;
		int yPix = (Controller.drawParams.heightPixels-getHeight())/2;
		int x = e.getX()+xPix;
		int y = e.getY()+yPix;
		int upOrDown = e.getUnitsToScroll();
		if (System.currentTimeMillis() - lastMouseWheelTime > 150) {
			lastMouseWheelTime = System.currentTimeMillis();
			Controller.changeScale(upOrDown, Controller.drawParams.PixToGeo(x,y));
		}
	}
		
}


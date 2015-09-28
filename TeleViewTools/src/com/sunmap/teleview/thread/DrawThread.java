package com.sunmap.teleview.thread;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.view.mainfram.ImageInfo;

/**
 * @author lijingru
 * 
 */
public class DrawThread extends Thread implements Runnable {
	
	private static Graphics2D gView = null;
	private static Graphics2D gSpecial = null;
	private static Graphics2D gMM = null;
	private static BufferedImage imageView = new BufferedImage(Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, BufferedImage.TYPE_INT_ARGB);
	private static BufferedImage imageSpe = new BufferedImage(Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, BufferedImage.TYPE_INT_ARGB);
	private static BufferedImage imageMM = new BufferedImage(Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, BufferedImage.TYPE_INT_ARGB);

	public enum DrawType {
		View, Eye, Special ,ALL, MM
		}
	
	
	private BlockingQueue<DrawType> drawqueue = new ArrayBlockingQueue<DrawType>(20);
	public DrawThread() {
		
	}

	@Override
	public void run() {
		while (true) {
			try {
				DrawType draw = drawqueue.take();
				Point centerpoint = new Point(Controller.drawParams.imageCentre.x, Controller.drawParams.imageCentre.y);
				int curScale = Controller.drawParams.currMapScale;
				init(draw);
				revertDraw();
				//大于5层背景变成蓝色
				if (curScale >= 200000) {
					Color color = null;
					color = new Color(100, 100, 250);
					gMM.setColor(color);
					gMM.fillRect(0, 0, Controller.drawParams.widthPixels, Controller.drawParams.heightPixels); 
				}
				if (draw == DrawType.ALL) {
					Controller.telemanage.draw(gView, centerpoint,curScale);
					Controller.mmManage.draw(gMM, centerpoint, curScale);
					Controller.informationDataManage.draw(gSpecial);
				}else if (draw == DrawType.View) {
					Controller.telemanage.draw(gView, centerpoint, curScale);
				}else if (draw == DrawType.MM) {
					Controller.mmManage.draw(gMM, centerpoint, curScale);
				}else if (draw == DrawType.Special) {
					Controller.informationDataManage.draw(gSpecial);
				}
				ImageInfo imageInfo = new ImageInfo();
				imageInfo.scale = curScale;
				imageInfo.imagePoint = centerpoint;
				imageInfo.imagList.add(imageMM);
				imageInfo.imagList.add(imageView);
				imageInfo.imagList.add(imageSpe);
				Controller.combinImage(imageInfo);
			}catch (Exception e) {
				if(e.getMessage().equals("cancelDrawFlag") == false){
					e.printStackTrace();
				}
//				Controller.combinImage(null);
			}

		}

	}
	
	private void revertDraw() {
		Controller.telemanage.revertDraw();
		Controller.mmManage.revertDraw();
	}

	public void putDrawQueue(DrawType drawType){
		State state = Controller.draw.getState();
		if(state == State.RUNNABLE && drawType != DrawType.Special){
			//设置取消描画
			Controller.telemanage.cancelDraw();
			Controller.mmManage.cancelDraw();
			//清空列表
			drawqueue.clear();
		}
		drawqueue.offer(drawType);
	}
	
	//根据命令重新初始化设备  只重构需要的设备
	private void init(DrawType draw) {
		if (draw == DrawType.ALL) {
			imageView = new BufferedImage(Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, BufferedImage.TYPE_INT_ARGB);
			imageMM = new BufferedImage(Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, BufferedImage.TYPE_INT_ARGB);
			imageSpe = new BufferedImage(Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, BufferedImage.TYPE_INT_ARGB);
		} else if (draw == DrawType.View) {
			imageView = new BufferedImage(Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, BufferedImage.TYPE_INT_ARGB);
		}else if (draw == DrawType.Special) {
			imageSpe = new BufferedImage(Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, BufferedImage.TYPE_INT_ARGB);
		}else if (draw == DrawType.MM) {
			imageMM = new BufferedImage(Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, BufferedImage.TYPE_INT_ARGB);
		}
		gView = (Graphics2D) imageView.getGraphics();
		gView.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gSpecial = (Graphics2D) imageSpe.getGraphics();
		gSpecial.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gMM = (Graphics2D) imageMM.getGraphics();
		gMM.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
}

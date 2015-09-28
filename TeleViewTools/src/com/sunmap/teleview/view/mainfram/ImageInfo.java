package com.sunmap.teleview.view.mainfram;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.sunmap.teleview.Controller;

public class ImageInfo {
	public List<BufferedImage> imagList = new ArrayList<BufferedImage>();
	public BufferedImage image = new BufferedImage(Controller.drawParams.widthPixels, Controller.drawParams.heightPixels, BufferedImage.TYPE_INT_ARGB);
	public int scale;
	public Point imagePoint = new Point(Controller.drawParams.imageCentre.x, Controller.drawParams.imageCentre.y);
}

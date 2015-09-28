package com.sunmap.teleview.element.view.data;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.sunmap.teleview.element.view.Device;


public class PointGraphic implements Graphic{


	public short flag;//文字类型，值域是21--25
	public short pointxs[];//基准点坐标X
	public short pointys[];
	public short type;//文字的分类种别
	public byte info;//文字表示情报     
	public short angle;//文字的旋转角度
	public byte rowCount;//行数
	public boolean scale1;//显示比例尺
	public boolean scale2;//显示比例尺
	public boolean scale3;//显示比例尺
	public boolean textScale1;//文字显示比例尺
	public boolean textScale2;//文字显示比例尺
	public boolean textScale3;//文字显示比例尺
	public int[] offset;  	//如果是多行 就是多个offset
	public int[] count;		// 字符数  汉子的话 除以3  
	public short iconID;//ICON编号
	public short number;//数字记号番号
	public short numData;//数字Data
	public byte positionInfo;//文字配置情报
	
	public void read(DataInputStream dis,short flag) {
		try {
			this.flag = flag;
			switch(flag){
			case 21:
				read21(dis);
				break;
			case 22:
				read22(dis);
				break;
			case 23:
				read23(dis);
				break;
			case 24:
				read23(dis);//文字Data４ 格式与 文字Data3相同
				break;
			case 25:
				read25(dis);
				break;
			case 26:
				read26(dis);
				break;
			case 31:
				read31(dis);
				break;
			case 32:
				read32(dis);
				break;
			case 33:
				read33(dis);
				break;
			case 34:
				read34(dis);
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
	
	private void read21(DataInputStream dis) throws IOException{
		short pointx;
		short pointy;
		pointx = dis.readShort();
		pointy = dis.readShort();
		type = dis.readShort();
		byte scale = dis.readByte();
		if((scale & 0x80) != 0){
			scale1 = true;
		}
		if((scale & 0x40) != 0){
			scale2 = true;
		}
		if((scale & 0x20) != 0){
			scale3 = true;
		}
		offset = new int[1];
		count = new int[1];
		offset[0] = dis.readShort() * 2;//OffSet
		count[0] = dis.readUnsignedByte();//文字Size
		dis.readUnsignedByte();//半角Size
		
//		int pointCount = dis.readUnsignedByte();//名称坐标点的个数
//		pointxs = new short[pointCount + 1];
//		pointys = new short[pointCount + 1];
//		pointxs[0] = pointx;
//		pointys[0] = pointy;
//		for(int i = 0; i < pointCount; i++){
//			pointxs[i + 1] = dis.readShort();
//			pointys[i + 1] = dis.readShort();				
//		}
		
		pointxs = new short[] { pointx };
		pointys = new short[] { pointy };
	}
	
	private void read22(DataInputStream dis) throws IOException{
		short pointx;
		short pointy;
		pointx = dis.readShort();
		pointy = dis.readShort();
		type = dis.readShort();
		byte scale = dis.readByte();
		if((scale & 0x80) != 0){
			scale1 = true;
		}
		if((scale & 0x40) != 0){
			scale2 = true;
		}
		if((scale & 0x20) != 0){
			scale3 = true;
		}
		info = dis.readByte();
		offset = new int[1];
		count = new int[1];
		offset[0] = dis.readShort() * 2;//OffSet
		count[0] = dis.readUnsignedByte();//文字Size
		dis.readUnsignedByte();//半角Size
		
		pointxs = new short[1];
		pointys = new short[1];
		pointxs[0] = pointx;
		pointys[0] = pointy;
	}
	
	private void read23(DataInputStream dis) throws IOException{
		short pointx;
		short pointy;
		pointx = dis.readShort();
		pointy = dis.readShort();
		type = dis.readShort();
		byte scale = dis.readByte();
		if((scale & 0x80) != 0){
			scale1 = true;
		}
		if((scale & 0x40) != 0){
			scale2 = true;
		}
		if((scale & 0x20) != 0){
			scale3 = true;
		}
		info = dis.readByte();
		angle = (short) dis.readUnsignedByte();//文字的旋转角度
//		count[0] = dis.readUnsignedByte();//文字的大小
		dis.readUnsignedByte();//文字的大小
		rowCount = dis.readByte();//行数
		offset = new int[rowCount];
		count = new int[rowCount];
		for(byte i = 0; i < rowCount; i++){
			offset[i] = dis.readShort() * 2;//OffSet
			count[i] = dis.readUnsignedByte();//文字Size
			dis.readUnsignedByte();//半角Size			
		}
		
		pointxs = new short[1];
		pointys = new short[1];
		pointxs[0] = pointx;
		pointys[0] = pointy;
	}
	private void read25(DataInputStream dis) throws IOException{
		short pointx;
		short pointy;
		pointx = dis.readShort();
		pointy = dis.readShort();
		type = dis.readShort();
		byte scale = dis.readByte();
		if((scale & 0x80) != 0){
			scale1 = true;
		}
		if((scale & 0x40) != 0){
			scale2 = true;
		}
		if((scale & 0x20) != 0){
			scale3 = true;
		}
		info = dis.readByte();
		rowCount = dis.readByte();//行数
		offset = new int[rowCount];
		count = new int[rowCount];
		for(byte i = 0; i < rowCount; i++){
			offset[i] = dis.readShort() * 2;//OffSet
			count[i] = dis.readUnsignedByte();//文字Size
			dis.readUnsignedByte();//半角Size			
		}
		
		pointxs = new short[1];
		pointys = new short[1];
		pointxs[0] = pointx;
		pointys[0] = pointy;
	}
	
	private void read26(DataInputStream dis) throws IOException {
		type = dis.readShort();
		byte scale = dis.readByte();
		if ((scale & 0x80) != 0) {
			scale1 = true;
		}
		if ((scale & 0x40) != 0) {
			scale2 = true;
		}
		if ((scale & 0x20) != 0) {
			scale3 = true;
		}
		offset = new int[] { dis.readShort() * 2 };// OffSet
		count = new int[] { dis.readUnsignedByte() };// 文字size
		dis.readUnsignedByte();// 半角size
		int pointCount = dis.readUnsignedByte();// 名称坐标点的个数
		pointxs = new short[pointCount];
		pointys = new short[pointCount];
		for (int i = 0; i < pointCount; i++) {
			pointxs[i] = dis.readShort();
			pointys[i] = dis.readShort();
		}
	}
	
	private void read31(DataInputStream dis) throws IOException{
		short pointx;
		short pointy;
		pointx = (short) dis.readUnsignedByte();
		pointy = (short) dis.readUnsignedByte();
		byte scale = dis.readByte();
		if((scale & 0x80) != 0){
			scale1 = true;
		}
		if((scale & 0x40) != 0){
			scale2 = true;
		}
		if((scale & 0x20) != 0){
			scale3 = true;
		}
		iconID = dis.readShort();

		pointxs = new short[1];
		pointys = new short[1];
		pointxs[0] = pointx;
		pointys[0] = pointy;
	}
	private void read32(DataInputStream dis) throws IOException{
		short pointx;
		short pointy;
		pointx = (short) dis.readUnsignedByte();
		pointy = (short) dis.readUnsignedByte();
		byte scale = dis.readByte();
		if((scale & 0x80) != 0){
			scale1 = true;
		}
		if((scale & 0x40) != 0){
			scale2 = true;
		}
		if((scale & 0x20) != 0){
			scale3 = true;
		}
		number = dis.readShort();
		numData = dis.readShort();

		pointxs = new short[1];
		pointys = new short[1];
		pointxs[0] = pointx;
		pointys[0] = pointy;
	}
	private void read33(DataInputStream dis) throws IOException{
		short pointx;
		short pointy;
		pointx = (short) dis.readUnsignedByte();
		pointy = (short) dis.readUnsignedByte();
		byte scale = dis.readByte();
		if((scale & 0x80) != 0){
			scale1 = true;
		}
		if((scale & 0x40) != 0){
			scale2 = true;
		}
		if((scale & 0x20) != 0){
			scale3 = true;
		}
		iconID = dis.readShort();//文字记号番号
		dis.readShort();
		dis.readShort();
		dis.readShort();

		pointxs = new short[1];
		pointys = new short[1];
		pointxs[0] = pointx;
		pointys[0] = pointy;
	}
	private void read34(DataInputStream dis) throws IOException{
		short pointx;
		short pointy;
		pointx = (short) dis.readUnsignedByte();
		pointy = (short) dis.readUnsignedByte();
		byte scale = dis.readByte();
		if((scale & 0x80) != 0){
			scale1 = true;
		}
		if((scale & 0x40) != 0){
			scale2 = true;
		}
		if((scale & 0x20) != 0){
			scale3 = true;
		}
		iconID = dis.readShort();//文字记号番号
		scale = dis.readByte();
		if((scale & 0x80) != 0){
			textScale1 = true;
		}
		if((scale & 0x40) != 0){
			textScale2 = true;
		}
		if((scale & 0x20) != 0){
			textScale3 = true;
		}
		positionInfo = (byte) (dis.readByte() & 0x7);
		rowCount = dis.readByte();
		offset = new int[rowCount];
		count = new int[rowCount];
		for(byte i = 0; i < rowCount; i++){
			offset[i] = dis.readShort() * 2;//OffSet
			count[i] = dis.readUnsignedByte();//文字Size
			dis.readUnsignedByte();//半角Size			
		}

		pointxs = new short[1];
		pointys = new short[1];
		pointxs[0] = pointx;
		pointys[0] = pointy;
	}
	
	
	/**
	 * 制作view显示文字
	 * @param texts 文字存放位置
	 * @param txtMessage	文字部
	 * @param blockID	所在blockID
	 */
	public void makeTextsDrawData( List<TeleViewText> texts, TxtMessage txtMessage, BlockID blockID) {
		boolean isText = isShowText();
		if (isText){
			StringBuffer tagText  = new StringBuffer();
			byte[] textBytes = txtMessage.textBytes;
			for(int i = 0; i < count.length; i++){
				byte[] str = new byte[count[i]];
				for (int y= 0; y< count[i]; y++) {
					str[y] = textBytes[offset[i] + y];
				}
				if(i > 0){
					tagText.append("\r\n");
				}
				try {
					tagText.append(new String(str,"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			// 正规化坐标转2560分之一秒
			Point point = blockID.unitToGeo(pointxs[0], pointys[0]);
			TeleViewText text = new TeleViewText(tagText.toString(),point.x,point.y);
			if (pointxs.length > 1) {
				text.type = 0;
			} else {
				text.type = this.iconID;
			}
			text.positionInfo = this.positionInfo;
			texts.add(text);
		}
	}
	
	private boolean isShowText() {
		boolean isshowTextflag = false;
		byte scale = (byte) ViewBaseInfo.curDataScale;
		switch (scale) {
		case 1:
			if(scale1){
				isshowTextflag = true;
			}
			break;
		case 2:
			if(scale2){
				isshowTextflag =  true;
			}
			break;
		case 3:
			if(scale3){
				isshowTextflag =  true;
			}
			break;
		}
		return isshowTextflag;
	}	

	public boolean isRoadTextLine(){
		return isShowText()&& pointxs.length > 1;
	}
	
	/**
	 * 制作道路名称描画线
	 * @param path
	 * @param blockID
	 */
	public void makeRoadTextLineData(GeneralPath path,BlockID blockID){
		boolean isText = isShowText();
		if (isText && pointxs.length > 1){
				makePath(path, blockID);
		}
	}

	/**
	 * 制作文字线
	 * @param path
	 * @param blockID
	 */
	private void makePath(GeneralPath path, BlockID blockID) {
		for (int j = 0; j < pointxs.length; j++) {
			Point  geoPoint = blockID.unitToGeo(pointxs[j], pointys[j]);
			Point2D point = Device.geoToPix(geoPoint);
			if (j == 0) {
				path.moveTo(point.getX(), point.getY());
			} else {
				path.lineTo(point.getX(), point.getY());
			}
		}
		
	}


}

package com.sunmap.teleview.element.view.data;

import java.io.DataInputStream;
import java.io.IOException;


/**
 * 文字类
 * @author niujiwei
 */
public class TxtMessage{
	short[] bytes;
	public byte[] textBytes = null;
	int font= 20;
	int width = 0;
	int height = font;
	public void parse(DataInputStream dis) {
		try {
			int size;
			size = dis.readUnsignedShort() * 2;
			textBytes = new byte[size];
			dis.read(textBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{

		}
	}

}

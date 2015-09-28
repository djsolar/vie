package com.sunmap.teleview.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.sunmap.teleview.util.CloserHelper;


public class BlockedDataParser {
	DataInputStream dis = null;
	public BlockedDataParser(InputStream is) throws IOException{
		dis = new DataInputStream(is);
	}
	
	public short readShort() throws IOException{
		byte[] dst = new byte[2];
		dis.readFully(dst);
		return (short)((dst[0] << 8) | (dst[1] & 0xff));
	}

	public int readUnsignedShort() throws IOException{
		byte[] dst = new byte[2];
		dis.readFully(dst);
		return (int)(((dst[0] & 0xff) << 8) | (dst[1] & 0xff));
	}

	public int readInt() throws IOException{
		byte[] dst = new byte[4];
		dis.readFully(dst);
		return (int) (((dst[0] & 0xff) << 24) | ((dst[1] & 0xff) << 16) |
				  ((dst[2] & 0xff) << 8) | (dst[3] & 0xff));
	}
	public byte readByte() throws IOException{
		byte[] dst = new byte[1];
		dis.readFully(dst);
		return dst[0];
	}
	
	public short readUnsignedByte() throws IOException{
		byte[] dst = new byte[1];
		dis.readFully(dst);
		return (short) (dst[0] & 0xff);
	}
	public void read(byte[] buf) throws IOException{
		dis.readFully(buf);
	}

	public void close() throws IOException {
		CloserHelper.close(dis);
	}
}

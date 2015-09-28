package com.sunmap.teleview.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class ByteUtil {

	public final static boolean LITTLE_ENDIAN =true;
	public final static boolean BIG_ENDIAN =false;	
	
	public static short toShort(byte byaTemp[])
	{
		return ByteUtil.toShort(byaTemp, 0, byaTemp.length);
	}
	
	public static short toShort(byte byaTemp[], int nOff, int nLen)
	{
		if (nLen > 2)
			nLen = 2;
		if (byaTemp.length < nOff + nLen)
			return -1;
		short sRes = 0;
		for (int i = nLen-1; i >=0 ; i--)
		{
			sRes <<= 8;
			int nTemp = byaTemp[(nOff + nLen) - 1 - i];
			nTemp &= 0xff;
			sRes |= nTemp;
		}

		return sRes;
	}
	
	public static int toInt(byte byaTemp[], int nOff, int nLen)
	{
		if (nLen > 4)
			nLen = 4;
		if (byaTemp.length < nOff + nLen)
			return -1;
		int nRes = 0;
		for (int i = nLen-1; i >=0 ; i--)
		{
			nRes <<= 8;
			int nTemp = byaTemp[(nOff + nLen) - 1 - i];
			nTemp &= 0xff;
			nRes |= nTemp;
		}
		
		return nRes;
	}
	public static int toInt(byte[] byaTemp)
	{
		return ByteUtil.toInt(byaTemp, 0, byaTemp.length);
	}
	
	public static long toLong(byte[] byaTemp)
	{
		return ByteUtil.toLong(byaTemp, 0, byaTemp.length);
	}
	
	public static int toLong(byte byaTemp[], int nOff, int nLen)
	{
		if (nLen > 8)
			nLen = 8;
		if (byaTemp.length < nOff + nLen)
			return -1;
		int nRes = 0;
		for (int i = nLen-1; i >=0 ; i--)
		{
			nRes <<= 8;
			int nTemp = byaTemp[(nOff + nLen) - 1 - i];
			nTemp &= 0xff;
			nRes |= nTemp;
		}
		
		return nRes;
	}
	
	public static double toDouble(byte[] b) {
		long l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		l &= 0xffffffffl;
		l |= ((long) b[4] << 32);
		l &= 0xffffffffffl;
		l |= ((long) b[5] << 40);
		l &= 0xffffffffffffl;
		l |= ((long) b[6] << 48);
		l &= 0xffffffffffffffl;
		l |= ((long) b[7] << 56);
		
		return Double.longBitsToDouble(l);
	}
	
	public static byte[] toBytes(float f,boolean endian) {
		
		byte[] b = new byte [4];
		
		ByteBuffer bb = ByteBuffer.allocate(4);
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(f);
		bb.get(b);
		
		if( endian == ByteUtil.LITTLE_ENDIAN )
		{			
			
		}
		else
		{
			byte t=b[0];
			b[0]=b[3];
			b[3]=t;
			
			t=b[1];
			b[1]=b[2];
			b[2]=t;
		}
		
		return b;
	}
	
	
	public static float toFloat(byte[] b) {		
		byte[] t=new byte[4];
		t[0]=b[3];
		t[1]=b[2];
		t[2]=b[1];
		t[3]=b[0];		
		
		ByteBuffer bb = ByteBuffer.wrap(t);
		FloatBuffer fb = bb.asFloatBuffer();
		return fb.get();
	}
	
	public static byte[] toBytes(short s,boolean endian)
	{
		byte[] b=new byte[2];
		
		if( endian == ByteUtil.LITTLE_ENDIAN )
		{
			b[1]=(byte)((s>>8)&0xff);
			b[0]=(byte)((s>>0)&0xff);
		}
		else
		{
			b[0]=(byte)((s>>8)&0xff);
			b[1]=(byte)((s>>0)&0xff);
		}
		
		return b;
	}
	
	public static byte[] toBytes(int i,boolean endian)
	{
		byte[] b=new byte[4];
		
		if( endian == ByteUtil.LITTLE_ENDIAN )
		{
			b[3]=(byte)((i>>24)&0xff);
			b[2]=(byte)((i>>16)&0xff);
			b[1]=(byte)((i>>8)&0xff);
			b[0]=(byte)((i>>0)&0xff);
		}
		else
		{
			b[0]=(byte)((i>>24)&0xff);
			b[1]=(byte)((i>>16)&0xff);
			b[2]=(byte)((i>>8)&0xff);
			b[3]=(byte)((i>>0)&0xff);
		}
		
		return b;
	}
	
	public static byte[] toBytes(long i,boolean endian)
	{
		byte[] b=new byte[8];
		
		if( endian == ByteUtil.LITTLE_ENDIAN )
		{
			b[7]=(byte)((i>>56)&0xff);
			b[6]=(byte)((i>>48)&0xff);
			b[5]=(byte)((i>>40)&0xff);
			b[4]=(byte)((i>>32)&0xff);
			b[3]=(byte)((i>>24)&0xff);
			b[2]=(byte)((i>>16)&0xff);
			b[1]=(byte)((i>>8)&0xff);
			b[0]=(byte)((i>>0)&0xff);
		}
		else
		{			
			b[0]=(byte)((i>>56)&0xff);
			b[1]=(byte)((i>>48)&0xff);
			b[2]=(byte)((i>>40)&0xff);
			b[3]=(byte)((i>>32)&0xff);
			b[4]=(byte)((i>>24)&0xff);
			b[5]=(byte)((i>>16)&0xff);
			b[6]=(byte)((i>>8)&0xff);
			b[7]=(byte)((i>>0)&0xff);
		}
		
		return b;
	}
	
	public static byte[] toBytes(double d,boolean endian) {
		byte[] b = new byte[8];
		
		if( endian == ByteUtil.LITTLE_ENDIAN )
		{			
			long l = Double.doubleToLongBits(d);
			for (int i = b.length-1; i >= 0 ; i--) {
				b[i] = new Long(l).byteValue();
				l = l >> 8;
			}
		}
		else
		{			
			long l = Double.doubleToLongBits(d);
			for (int i = 0; i < b.length; i++) {
				b[i] = new Long(l).byteValue();
				l = l >> 8;
			}
		}
		
		return b;
	}
}

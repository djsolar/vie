package com.sunmap.teleview.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class ByteList
{
	
	private List<ByteElement<?>> list;
	private boolean endian = ByteType.LITTLE_ENDIAN;
	private String encode = "UTF-8";
	
	
	public int size()
	{
		return list.size();
	}
	
	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public ByteList() {		
		this.list = new ArrayList<ByteElement<?>>();
	}
	
	public ByteList(boolean endian) {	
		this.endian=endian;
		this.list = new ArrayList<ByteElement<?>>();
	}
		
	public boolean isEndian() {
		return endian;
	}

	public void setEndian(boolean endian) {
		this.endian = endian;
	}

	/*****************************************************************************************
	 * byte
	 * 
	 * 
	 *****************************************************************************************/
	public void addByte(byte b) 
	{
		ByteElement<Byte> be=new ByteElement<Byte>();
		be.setElement(b);
		this.list.add(be);
	}
	
	public void addByte(int index,byte b) 
	{
		ByteElement<Byte> be=new ByteElement<Byte>();
		be.setElement(b);
		this.list.add(index,be);
	}
	
	public void setByte(int index,byte b)
	{
		ByteElement<Byte> be=new ByteElement<Byte>();
		be.setElement(b);
		this.list.set(index,be);
	}
	
	public void remove(int index)
	{		
		this.list.remove(index);
	}
	
	public byte getByte(int index)
	{
		return (Byte) list.get(index).getElement();
	}
	
	public void addAll(byte[] bytes) 
	{
		for(byte b:bytes)
		{
			ByteElement<Byte> be=new ByteElement<Byte>();
			be.setElement(b);
			this.list.add(be);
		}		
	}

	/*****************************************************************************************
	 * short
	 * 
	 * 
	 *****************************************************************************************/
	public void addShort(short s)
	{
		ByteElement<Short> be=new ByteElement<Short>();
		be.setElement(s);
		be.setEndian(endian);
		this.list.add(be);
	}
	
	public void addShort(short s,boolean endian)
	{
		ByteElement<Short> be=new ByteElement<Short>();
		be.setElement(s);
		be.setEndian(endian);
		this.list.add(be);
	}
	
	public void addShort(int index,short s)
	{
		ByteElement<Short> be=new ByteElement<Short>();
		be.setElement(s);
		be.setEndian(endian);
		this.list.add(index,be);
	}
	
	public void addShort(int index,short s,boolean endian)
	{
		ByteElement<Short> be=new ByteElement<Short>();
		be.setElement(s);
		be.setEndian(endian);
		this.list.add(index,be);
	}
	
	public void setShort(int index,short s)
	{
		ByteElement<Short> be=new ByteElement<Short>();
		be.setElement(s);
		be.setEndian(endian);
		this.list.set(index,be);
	}
	
	public void setShort(int index,short s,boolean endian)
	{
		ByteElement<Short> be=new ByteElement<Short>();
		be.setElement(s);
		be.setEndian(endian);
		this.list.set(index,be);
	}
	
	public short getShort(int index)
	{
		return (Short) list.get(index).getElement();
	}
	
	/*****************************************************************************************
	 * int
	 * 
	 * 
	 *****************************************************************************************/
	public void addInt(int i) {
		ByteElement<Integer> be=new ByteElement<Integer>();
		be.setElement(i);
		be.setEndian(endian);
		this.list.add(be);
	}
	
	public void addInt(int i,boolean endian) {
		ByteElement<Integer> be=new ByteElement<Integer>();
		be.setElement(i);
		be.setEndian(endian);
		this.list.add(be);
	}
	
	public void addInt(int index,int i) {
		ByteElement<Integer> be=new ByteElement<Integer>();
		be.setElement(i);
		be.setEndian(endian);
		this.list.add(index,be);
	}
	
	public void addInt(int index,int i,boolean endian) {
		ByteElement<Integer> be=new ByteElement<Integer>();
		be.setElement(i);
		be.setEndian(endian);
		this.list.add(index,be);
	}
	
	public void setInt(int index,int i) {
		ByteElement<Integer> be=new ByteElement<Integer>();
		be.setElement(i);
		be.setEndian(endian);
		this.list.set(index,be);
	}
	
	public void setInt(int index,int i,boolean endian) {
		ByteElement<Integer> be=new ByteElement<Integer>();
		be.setElement(i);
		be.setEndian(endian);
		this.list.set(index,be);
	}
	
	public int getInt(int index)
	{
		return (Integer) list.get(index).getElement();
	}
	
	/*****************************************************************************************
	 * long
	 * 
	 * 
	 *****************************************************************************************/
	public void addLong(long l) {
		ByteElement<Long> be=new ByteElement<Long>();
		be.setElement(l);
		be.setEndian(endian);
		this.list.add(be);
	}
	
	public void addLong(long l,boolean endian) {
		ByteElement<Long> be=new ByteElement<Long>();
		be.setElement(l);
		be.setEndian(endian);
		this.list.add(be);
	}
	
	public void addLong(int index,long l) {
		ByteElement<Long> be=new ByteElement<Long>();
		be.setElement(l);
		be.setEndian(endian);
		this.list.add(index,be);
	}
	
	public void addLong(int index,long l,boolean endian) {
		ByteElement<Long> be=new ByteElement<Long>();
		be.setElement(l);
		be.setEndian(endian);
		this.list.add(index,be);
	}
	
	public void setLong(int index,long l) {
		ByteElement<Long> be=new ByteElement<Long>();
		be.setElement(l);
		be.setEndian(endian);
		this.list.set(index,be);
	}
	
	public void setLong(int index,long l,boolean endian) {
		ByteElement<Long> be=new ByteElement<Long>();
		be.setElement(l);
		be.setEndian(endian);
		this.list.set(index,be);
	}
	
	public long getLong(int index)
	{
		return (Long) list.get(index).getElement();
	}
	
	/*****************************************************************************************
	 * float
	 * 
	 * 
	 *****************************************************************************************/
	public void addFloat(float f) {
		ByteElement<Float> be=new ByteElement<Float>();
		be.setElement(f);
		be.setEndian(endian);
		this.list.add(be);
	}
	
	public void addFloat(float f,boolean endian) {
		ByteElement<Float> be=new ByteElement<Float>();
		be.setElement(f);
		be.setEndian(endian);
		this.list.add(be);
	}
	
	public void addFloat(int index,float f) {
		ByteElement<Float> be=new ByteElement<Float>();
		be.setElement(f);
		be.setEndian(endian);
		this.list.add(index,be);
	}
	
	public void addFloat(int index,float f,boolean endian) {
		ByteElement<Float> be=new ByteElement<Float>();
		be.setElement(f);
		be.setEndian(endian);
		this.list.add(index,be);
	}
	
	public void setFloat(int index,float f) {
		ByteElement<Float> be=new ByteElement<Float>();
		be.setElement(f);
		be.setEndian(endian);
		this.list.set(index,be);
	}
	
	public void setFloat(int index,float f,boolean endian) {
		ByteElement<Float> be=new ByteElement<Float>();
		be.setElement(f);
		be.setEndian(endian);		
		this.list.set(index,be);
	}
	
	public float getFloat(int index)
	{
		return (Float) list.get(index).getElement();
	}
	
	/*****************************************************************************************
	 * double
	 * 
	 * 
	 *****************************************************************************************/
	public void addDouble(double d) 
	{
		ByteElement<Double> be=new ByteElement<Double>();
		be.setElement(d);
		be.setEndian(endian);
		this.list.add(be);
	}
	
	public void addDouble(double d,boolean endian) 
	{
		ByteElement<Double> be=new ByteElement<Double>();
		be.setElement(d);
		be.setEndian(endian);
		this.list.add(be);
	}
	
	public void addDouble(int index,double d) 
	{
		ByteElement<Double> be=new ByteElement<Double>();
		be.setElement(d);
		be.setEndian(endian);
		this.list.add(index,be);
	}
	
	public void addDouble(int index,double d,boolean endian) 
	{
		ByteElement<Double> be=new ByteElement<Double>();
		be.setElement(d);
		be.setEndian(endian);		
		this.list.add(index,be);
	}
	
	public void setDouble(int index,double d) 
	{
		ByteElement<Double> be=new ByteElement<Double>();
		be.setElement(d);
		be.setEndian(endian);
		this.list.set(index,be);
	}
	
	public void setDouble(int index,double d,boolean endian) 
	{
		ByteElement<Double> be=new ByteElement<Double>();
		be.setElement(d);
		be.setEndian(endian);		
		this.list.set(index,be);
	}
	
	public double getDouble(int index)
	{
		return (Double) list.get(index).getElement();
	}
	
	/*****************************************************************************************
	 * String
	 * 
	 * 
	 *****************************************************************************************/
	public void addString(String string) {
		ByteElement<String> be=new ByteElement<String>();
		be.setElement(string);
		this.list.add(be);		
	}
	
	public void addString(int index,String string) {
		ByteElement<String> be=new ByteElement<String>();
		be.setElement(string);
		this.list.add(index,be);		
	}
	
	public void setString(int index,String string) {
		ByteElement<String> be=new ByteElement<String>();
		be.setElement(string);
		this.list.set(index,be);		
	}
	
	public String getString(int index)
	{
		return (String) list.get(index).getElement();
	}	

	
	/*****************************************************************************************
	 * byte[]
	 * 
	 * 
	 *****************************************************************************************/
	public void addByteArray(byte[] byteArray) {
		ByteElement<byte[]> be=new ByteElement<byte[]>();
		be.setElement(byteArray);
		this.list.add(be);		
	}
	
	public void addByteArray(int index,byte[] byteArray) {
		ByteElement<byte[]> be=new ByteElement<byte[]>();
		be.setElement(byteArray);
		this.list.add(index,be);		
	}
	
	public void setByteArray(int index,byte[] byteArray) {
		ByteElement<byte[]> be=new ByteElement<byte[]>();
		be.setElement(byteArray);
		this.list.set(index,be);		
	}
	
	public byte[] getByteArray(int index)
	{
		return (byte[]) list.get(index).getElement();
	}	
	
	
	@SuppressWarnings("unchecked")
	public <T> T get(int index)
	{
		ByteElement<T> ele=(ByteElement<T>) list.get(index);
		return (T) ele.getElement();
	}
	
	
	public byte[] getAllBytes()
	{
		int length=0;
		
		for ( ByteElement<?> be : list ) {			
			Object o=be.getElement();
			
			if( o instanceof Byte )
			{
				length+=1;
			}
			else if( o instanceof Short )
			{
				length+=2;
			}
			else if( o instanceof Integer )
			{
				length+=4;
			}
			else if( o instanceof Long )
			{
				length+=8;
			}
			else if( o instanceof Float )
			{
				length+=4;
			}
			else if( o instanceof Double )
			{
				length+=8;
			}
			else if( o instanceof String )
			{
				String s=(String)o;
				try {
					length+=s.getBytes(encode).length;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if( o instanceof byte[] )
			{
				byte[] bs=(byte[])o;
				length+=bs.length;			
			}			
		}
		
		
		byte[] buf=new byte[length];
		int position=0;
		boolean endian=ByteType.LITTLE_ENDIAN;
		for ( ByteElement<?> be : list ) {			
			
			
			Object o=be.getElement();
			endian=be.isEndian();
			
			if( o instanceof Byte )
			{
				buf[position]=(Byte)o;
				position+=1;
			}
			else if( o instanceof Short )
			{
				byte[] b=ByteUtil.toBytes((Short)o, endian);
				buf[position]=b[0];
				buf[position+1]=b[1];
				position+=2;
			}
			else if( o instanceof Integer )
			{
				byte[] b=ByteUtil.toBytes((Integer)o, endian);
				buf[position]=b[0];
				buf[position+1]=b[1];
				buf[position+2]=b[2];
				buf[position+3]=b[3];
				position+=4;
			}
			else if( o instanceof Long )
			{
				byte[] b=ByteUtil.toBytes((Long)o, endian);
				buf[position]=b[0];
				buf[position+1]=b[1];
				buf[position+2]=b[2];
				buf[position+3]=b[3];
				buf[position+4]=b[4];
				buf[position+5]=b[5];
				buf[position+6]=b[6];
				buf[position+7]=b[7];
				position+=8;
			}
			else if( o instanceof Float )
			{
				byte[] b=ByteUtil.toBytes((Float)o, endian);
				buf[position]=b[0];
				buf[position+1]=b[1];
				buf[position+2]=b[2];
				buf[position+3]=b[3];				
				position+=4;
			}
			else if( o instanceof Double )
			{
				byte[] b=ByteUtil.toBytes((Double)o, endian);
				buf[position]=b[0];
				buf[position+1]=b[1];
				buf[position+2]=b[2];
				buf[position+3]=b[3];
				buf[position+4]=b[4];
				buf[position+5]=b[5];
				buf[position+6]=b[6];
				buf[position+7]=b[7];
				position+=8;
			}
			else if( o instanceof String )
			{
				String s=(String)o;
				byte[] b = null;
				try {
					b = s.getBytes(encode);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for (int i = 0; i < b.length; i++) {
					buf[position+i]=b[i];
				}
				
				position+=b.length;
			}
			else if( o instanceof byte[] )
			{
				byte[] bs=(byte[])o;
				
				for (int i = 0; i < bs.length; i++) {
					buf[position+i]=bs[i];
				}
				position+=bs.length;				
			}
		}
		
		
		return buf;
	}
	
	@Override
	public String toString() {
		
		for ( ByteElement<?> be : list ) {			
			System.out.println( be.getElement().getClass()+"="+be.getElement() );
		}
		return super.toString();
	}
}

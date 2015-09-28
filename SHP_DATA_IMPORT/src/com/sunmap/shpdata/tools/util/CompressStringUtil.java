package com.sunmap.shpdata.tools.util;

import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressStringUtil {

    /*private static CompressStringUtil instance=null;
    public static CompressStringUtil getInstance()
    {
	if(instance==null){
	    instance=new CompressStringUtil();
	}
	return instance; 
    }
    
    public void destroyInstance()
    {
	if(instance != null){
	    compresser.finish();
	    decompresser.end();
	}
    }
    
    private Deflater compresser=null;
    
    private Inflater decompresser=null;
    
    public int compress(byte[] input,byte[] output) throws UnsupportedEncodingException
    {	
	compresser.setInput(input);
	//compresser.finish();
	int compressedDataLength = compresser.deflate(output);	
	return compressedDataLength;
    }    
    
    public String decompress(int compressedDataLength,byte[] output,int length) throws DataFormatException, UnsupportedEncodingException 
    {
	
	decompresser.setInput(output, 0, compressedDataLength);
	byte[] result = new byte[length];
	int resultLength = decompresser.inflate(result);
	//decompresser.end();
	return new String(result, 0, resultLength, "US-ASCII");
    }*/
    
    static Deflater compresser = new Deflater();
    static Inflater decompresser = new Inflater();
    
    /**
     * @param input
     * @param output
     * @return 压缩的实际字节量
     * @throws UnsupportedEncodingException
     */
    public static int compress(byte[] input,byte[] output) 
    	throws UnsupportedEncodingException
    {	
	compresser.reset();
	compresser.setInput(input);
	compresser.finish();
	int compressedDataLength = compresser.deflate(output);	
	return compressedDataLength;
    }    
    
    /**
     * @param compressedDataLength
     * @param output
     * @param length 待返回解压字节数组最大值
     * @return
     * @throws DataFormatException
     * @throws UnsupportedEncodingException
     */
    public static String decompress(int compressedDataLength,byte[] output,int length) 
    	throws DataFormatException, UnsupportedEncodingException 
    {
	decompresser = new Inflater();
	//decompresser.reset();
	
	decompresser.setInput(output, 0, compressedDataLength);
	byte[] result = new byte[length];
	int resultLength = decompresser.inflate(result);
	decompresser.end();
	return new String(result, 0, resultLength, "US-ASCII");
    }
    
    public static byte[] intToBytes(int i)
    {
	byte[] b=new byte[4];
	b[0]=(byte)(0xff&i);
	b[1]=(byte)((0xff00&i)>>8);
	b[2]=(byte)((0xff0000&i)>>16);
	b[3]=(byte)((0xff000000&i)>>24);
	
	return b;
    }
    
    public static int bytesToInt(byte[] b)
    {
	int addr=b[0]&0xFF;
	addr|=(b[1]<<8)&0xFF00;
	addr|=(b[2]<<16)&0xFF0000;
	addr|=(b[3]<<24)&0xFF000000;
	
	return addr;
    }
    
    
}

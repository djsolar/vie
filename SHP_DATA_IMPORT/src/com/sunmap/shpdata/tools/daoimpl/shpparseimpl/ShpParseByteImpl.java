package com.sunmap.shpdata.tools.daoimpl.shpparseimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import com.sunmap.shpdata.tools.dao.IShpParse;
import com.sunmap.shpdata.tools.util.CompressStringUtil;
import com.sunmap.shpdata.tools.util.Util;

/**
 * 
 * @author shanbq
 *
 */
public class ShpParseByteImpl implements IShpParse {

	
	//头文件固定长度
	    final static int SHP_HEADER_LENGTH = 100;
	    
	    private byte[] compressedBytes(byte[] input) throws UnsupportedEncodingException
	    {
		//压缩数据	
		int length = input.length;

		//System.out.println("length=" + length);

		if (length > 0) {
		    byte[] output = new byte[length * 2];
		    int compressedDataLength = CompressStringUtil.compress(input, output);

		    byte[] tempBytes = CompressStringUtil
			    .intToBytes(compressedDataLength);

		    // 实际压缩字节量
		    byte[] bytes = new byte[8 + compressedDataLength];
		    bytes[0] = tempBytes[0];
		    bytes[1] = tempBytes[1];
		    bytes[2] = tempBytes[2];
		    bytes[3] = tempBytes[3];

		    // 原来字符长度
		    tempBytes = CompressStringUtil.intToBytes(length);
		    bytes[4] = tempBytes[0];
		    bytes[5] = tempBytes[1];
		    bytes[6] = tempBytes[2];
		    bytes[7] = tempBytes[3];

		    for (int k = 0; k < compressedDataLength; k++) {
			bytes[k + 8] = output[k];
		    }

		    //System.out.println("compressedDataLength=" + compressedDataLength);

		    return bytes;
		}
		// 空数据
		else {
		    return new byte[] {};
		}
	    }
	    
	    //private 
	    
	    public byte[] convertBytes(byte[] x,byte[] y)
	    {
		byte[] doubleBytes=new byte[16];
		
		for(int i=0;i<8;i++)
		{
		    doubleBytes[i]=x[i];
		}
		
		for(int i=0;i<8;i++)
		{
		    doubleBytes[i+8]=y[i];
		}
		
		return doubleBytes;
	    }
	    
	    /**
	     * 解析点状目标文件
	     * @param fis FileInputStream 文件流
	     * @return String[]
	     * @throws Exception 
	     */    
	    private List<byte[]> parsePoint ( FileInputStream fis ) throws Exception {
		
		byte[] headerBytes = new byte[4];	
		byte[] xBytes = new byte[8];
		byte[] yBytes = new byte[8];
		
		List<byte[]> list=new ArrayList<byte[]>();
			
		while( fis.read(headerBytes) != -1 ) {
		    	    
		    //过滤无用数据
		    fis.skip( 8 );   
		    
		    //坐标    
		    fis.read(xBytes);
		    fis.read(yBytes);
		    
		    byte[] temp=convertBytes(xBytes,yBytes);
		    
		    list.add( temp );
		    
		    //反解析
//		    double x = Util2.convertToDouble(temp,0, 8, false);	    
//		    double y = Util2.convertToDouble(temp, 8 ,8, false);
//		    System.out.println( x );
//		    System.out.println( y );
		    //System.out.println("length="+temp.length );
		}
		
		return list;
	    }
	    
	    
	    /**
	     * 解析线状目标文件
	     * @param fis FileInputStream 文件流
	     * @return String[]
	     * @throws Exception 
	     */
	    private List<byte[]> parsePolyLine ( FileInputStream fis ) throws Exception {
		List<byte[]> list=new ArrayList<byte[]>();
			
		int recordNumber = -1;
		byte[] recordNumberBytes = new byte[4];

		//临时变量
		byte[] pointsXBytes = new byte[8];
		byte[] pointsYBytes = new byte[8];
		byte[] numPartsBytes = new byte[4];
		byte[] numPointsBytes = new byte[4];
		byte[] partsBytes = new byte[4];
		byte[] xBytes = new byte[8];
		byte[] yBytes = new byte[8];
		
		byte[] bytes = new byte[16];
		
		while( fis.read(recordNumberBytes) != -1 )
		{
		    recordNumber = Util.convertToInt(recordNumberBytes,recordNumberBytes.length, true);   

		    // 目标的几何类型  读Box...
		    int i = 0;
		    
		    //过滤无用数据
		    fis.skip( 5*8 );

		    // 读NumParts和NumPoints	    
		    fis.read(numPartsBytes);
		    int numParts = Util.convertToInt(numPartsBytes, 4, false);	   
		    
		    fis.read(numPointsBytes);
		    int numPoints = Util.convertToInt(numPointsBytes, 4, false);	    

		    // 读Parts和Points
		    int[] parts = new int[numParts];	    
		    for (i = 0; i < numParts; i++) {		
			fis.read(partsBytes);
			parts[i] = Util.convertToInt(partsBytes, 4, false);	
		    }
		    	   
		    int pointNum = 0;

		    byte[] allBytes=null;
		    
		    for (i = 0; i < numParts; i++) {

			if (i != numParts - 1) {
			    pointNum = parts[i + 1] - parts[i];
			} else {
			    pointNum = numPoints - parts[i];
			}	
			    
			allBytes=new byte[ 8*pointNum*2 ];
			
			for (int j = 0; j < pointNum; j++) {
			    		    
			    fis.read(bytes);		    
			    
			    for(int k=0;k<16;k++)
			    {			
				allBytes[j*16+k]=bytes[k];
			    }
			    
			}		
		    }
		    	    
		    list.add( allBytes );
		    //System.out.println("length="+allBytes.length );
//		    //反解析
//		    for(int m=0;m<allBytes.length/8;m++)
//		    {
//			double x = Util2.convertToDouble(allBytes,m*8, 8, false);
//			System.out.println( x );
//		    }
		}
		
		return list;
	    }
	    
	    /**
	     * 解析面状目标文件
	     * @param fis FileInputStream 文件流
	     * @return String[]
	     * @throws Exception 
	     */
	    private List<byte[]> parseArea ( FileInputStream fis ) throws Exception {
		List<byte[]> list=new ArrayList<byte[]>();
			
		int recordNumber = -1;
		byte[] recordNumberBytes = new byte[4];
		byte[] numPartsBytes = new byte[4];
		byte[] partsBytes = new byte[4];
		byte[] pointsXBytes = new byte[8];
		byte[] pointsYBytes = new byte[8];
		byte[] bytes = new byte[16];
		
		while( fis.read(recordNumberBytes) != -1 )
		{	    
		    recordNumber = Util.convertToInt(recordNumberBytes,
			    recordNumberBytes.length, true);
		    
		    int i = 0;
		    
		    //过滤无用数据
		    fis.skip( 5*8 );

		    // 读NumParts和NumPoints	    
		    fis.read(numPartsBytes);
		    int numParts = Util.convertToInt(numPartsBytes, 4, false);	    

		    byte[] numPointsBytes = new byte[4];
		    fis.read(numPointsBytes);
		    int numPoints = Util.convertToInt(numPointsBytes, 4, false);	    
		    	    
		    // 读Parts和Points
		    int[] parts = new int[numParts];
		    for (i = 0; i < numParts; i++) {		
			fis.read(partsBytes);
			parts[i] = Util.convertToInt(partsBytes, 4, false);		
		    }
		    	    
		    //点的数目
		    int pointNum = 0;
		    byte[] input=null;
		    
		    byte[] allBytes=null;
		    
		    for (i = 0; i < numParts; i++) {

			if (i != numParts - 1) {
			    pointNum = parts[i + 1] - parts[i];
			} else {
			    pointNum = numPoints - parts[i];
			}	

			allBytes=new byte[ 8*pointNum*2+ 8 ];	

			for (int j = 0; j < pointNum; j++) {
			    fis.read(bytes);
			    for(int k=0;k<16;k++)
			    {			
				allBytes[j*16+k]=bytes[k];
			    }
			    
			}
			
			//分隔标记 doule负数来分隔环
			allBytes[ ((pointNum-1) * 16 + 16)+0 ]=0;
			allBytes[ ((pointNum-1) * 16 + 16)+1 ]=0;
			allBytes[ ((pointNum-1) * 16 + 16)+2 ]=0;
			allBytes[ ((pointNum-1) * 16 + 16)+3 ]=0;
			allBytes[ ((pointNum-1) * 16 + 16)+4 ]=0;
			allBytes[ ((pointNum-1) * 16 + 16)+5 ]=0;
			allBytes[ ((pointNum-1) * 16 + 16)+6 ]=-16;
			allBytes[ ((pointNum-1) * 16 + 16)+7 ]=-65;

			
		    }    
		    
		    
		    list.add( allBytes );
		    
//		    //if( ){}
//		    System.out.println("原来大小length="+allBytes.length );
//		    System.out.println("压缩后大小length="+compressedBytes(allBytes).length );
//		    
//		    System.out.println( );
		    
		    //反解析
//		    for(int m=0;m<allBytes.length/8;m++)
//		    {
//			double x = Util2.convertToDouble(allBytes,m*8, 8, false);
//			System.out.println( x );
//		    }
		}
		
		return list;
	    }
	    
	    /**
	     * 解析shp文件
	     * @param file File
	     * @return String[]
	     * @throws Exception 
	     * @throws Exception 
	     */
	    public List<byte[]> shpparse ( File file ) throws Exception {
		List<byte[]> shpList=null;
		
		FileInputStream fis=null;
			 
		    
		    fis=new FileInputStream(file);	    
		    
		    //文件头    
		    int headerShapeType=-1;	    
		    byte[] recordHeaderBytes=new byte[4];
		    fis.skip(32);
		    fis.read(recordHeaderBytes);	
		    fis.skip(64);	    
		    headerShapeType=Util.convertToInt(recordHeaderBytes, 4, false);
		    
		    switch (headerShapeType) {
		    	case 1:// 点状目标		
//		    	    System.out.println("正在解析的文件类型 点状目标");
		    	    shpList=this.parsePoint ( fis );	    	    
		    	    break;
		    	case 3:// 线状目标
//		    	    System.out.println("正在解析的文件类型 线状目标");
		    	    shpList=this.parsePolyLine ( fis );	    	    
		    	    break;	    	
		    	case 5:// 面状目标
//		    	    System.out.println("正在解析的文件类型 面状目标");
		    	    shpList=this.parseArea ( fis );	    	    
		    	    break;
		    	    
			default:
			    break;
			
	    }
		    if( fis != null) {
		    	fis.close();
		    }
		    return shpList;

	    }

		@Override
		public List<String> shpparse(File shpfile, File shxfile, int recordNum) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
}
	
	
	
	
	
	
	
	
	
	    
	    

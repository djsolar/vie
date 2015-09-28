package com.mansion.tele.log;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * @author 徐朝辉
 * 重定向输出文件
 */
public class TelePrintStream extends PrintStream{
	
	/**
	 * 构造器
	 * @param out1 输出流
	 * @param out2 输出流
	 */
	public TelePrintStream(PrintStream out1, PrintStream out2){
		super(out1);
		this.out=out2;
	}
	
	/**
	 * 设置重定向的输出log到文件
	 * @param bIsRedirectLog true 输出，false 不输出
	 */
	public static void  setRedirectLog(boolean bIsRedirectLog) {
		if (bIsRedirectLog) {
			try{
				PrintStream out=new PrintStream(new FileOutputStream("info.log"));
				PrintStream teleOut = new TelePrintStream(System.out, out);
				System.setOut(teleOut);
				PrintStream err=new PrintStream(new FileOutputStream("error.log"));
				teleOut=new TelePrintStream(System.err, err);
				System.setErr(teleOut);
				}catch(FileNotFoundException e){
					e.printStackTrace();
			  }
		}
		
	}
	
}

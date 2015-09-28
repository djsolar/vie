package com.mansion.tele.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 计时器
 * @author Administrator
 *
 */
public class EstimateTime {
	/**
	 * 取当前时间字符串（显示时间格式：yyyyMMdd-HH:mm:ss）
	 * @return
	 */
	public  static String  currentTimeToString(){
		long start = System.currentTimeMillis();
		Date d = new Date(start);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");		
		return sdf.format(d);
	}
	
	/**
	 * 取到当前时间段的字符串
	 * @param startTime
	 * @return
	 */
	public static String timeToString(long startTime){
		String result;
		DecimalFormat df = new DecimalFormat("0.00"); 
		long endTime = System.currentTimeMillis();
		double t = (endTime - startTime)/1000.0;
		if(t < 60){
			result = df.format(t) + " 秒";
		}else{
			t = t / 60.0;
			if(t < 60){
				result = df.format(t) + " 分";
			}else{
				t = t / 60.0;
				result = df.format(t) + " 小时";
			}
		}
		return result;
	}
}

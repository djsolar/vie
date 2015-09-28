package com.mansion.tele.business.landmark;

import java.io.Serializable;

import com.mansion.tele.business.landmark.Style.Brand;

public class SignCodeFirst  implements Serializable {
	
	int teleCode;
	int brandCode;
	String typeName;
	String brandName;
	Brand brand;
	/**
	 * 显示优先级
	 */
	long pri;
	/**
	 * 建议显示开始level
	 */
	short beginLevel;
	/**
	 * 显示最高level，通过此信息判断数据是否需要生层
	 */
	short endLevel;
	/**
	 * 建议显示开始比例尺：表示在显示开始level中的第几个比例尺，从0开始
	 */
	short beginScaleNo;
	/**
	 * 建议显示结束比例尺：表示在显示结束level中的第几个比例尺，从0开始
	 */
	short endScaleNo;
	/**
	 * 开始填充level
	 */
	short fillBGLevel;
	/**
	 * 结束填充level
	 */
	short fillEDLevel;

}

package com.sunmap.shpdata.tools.vo;

import java.util.ArrayList;
import java.util.List;

public class Table {
	private String tableName;
	private List<String[]> field = new ArrayList<String[]>();
	private int recordNum;
	public int getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(int recordNum) {
		this.recordNum = recordNum;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * 
	 * @param fieldName 字段名称
	 * @param fieldType 字段类型
	 * @param fieldLength 字段长度
	 */
	public void addField(String fieldName, String fieldType, String fieldLength, String strdefault) {
		String[] strings = new String[] {fieldName, fieldType, fieldLength, strdefault};
		this.field.add(strings);
	}

	public List<String[]> getField() {
		return field;
	}

	public void setField(List<String[]> field) {
		this.field = field;
	}

	public String getTableName() {
		return tableName;
	}
	
	public void setFieldList(List<String[]> field){
		this.field.addAll(field);
	}
}

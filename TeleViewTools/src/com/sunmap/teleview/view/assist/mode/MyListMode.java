package com.sunmap.teleview.view.assist.mode;

import javax.swing.AbstractListModel;


public class MyListMode extends AbstractListModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String[] strings = {"","",""};

	public MyListMode(){
		
	}
	
	public MyListMode(String[] str) {
		strings = str;
	}
	@Override
	public Object getElementAt(int index) {
		return strings[index];
	}

	@Override
	public int getSize() {
		return strings.length;
	}
	
	 public void setStrings(String[] strings) {
		this.strings = strings;
		System.out.println(strings);
	}

	
}

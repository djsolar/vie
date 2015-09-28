package com.sunmap.teleview.view.assist.mode;
import java.awt.Point;

import javax.swing.table.AbstractTableModel;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.thread.DrawThread.DrawType;
import com.sunmap.teleview.view.assist.fram.MyTable.ITableListVale;

/**
 * @author lijingru
 *
 */
public class MyTableModle extends AbstractTableModel  implements ITableListVale{


	private static final long serialVersionUID = 1L;
	Object[] titles;
	Object[] values;
	String[] colum = {"名称", "内容"};
	static int  columnSize = 2;
	
	public MyTableModle(String[] str) {
    	titles = new Object[str.length - 1];
    	for(int i = 0; i < str.length - 1; i++){
    		titles[i] = str[i];
    	}
	}
	public MyTableModle() {
	}

	@Override
	public int getColumnCount() {
		return colum.length;
	}

	@Override
	public int getRowCount() {
		return titles.length;
	}

	@Override
	public Object getValueAt(int row,int col) {
		if(col == 0){
			return titles[row];
	   	}else{
	   		return values[row];
	   	}
	}
	public void setHeader(String[] header){
		titles = new Object[header.length - 1];
		for(int i = 0; i < header.length - 1; i++){
			titles[i] = header[i];
		}
	}
	public String getColumnName(int col) {
		return colum[col];
	}
	public void setValues(String[] values){
    	this.values = values;
    }
	
	@Override
	public void valueTableChanged(int index) {
		String titleString = String.valueOf(titles[index]);
		String valueString = String.valueOf(values[index]).replaceAll(" ", "");
		//读取startnode连接路信息
		if (titleString.contains("Node")) {
			Controller.pickMMNodeLinkRoad(valueString);
			return;
		}
		//读取要跳转的坐标点
		if (valueString.indexOf("：")== -1) {
			return;
		}
		try {
			valueString = valueString.substring(valueString.indexOf("：")+1);
			String x = valueString.substring(0, valueString.indexOf(",")).trim();
			String y = valueString.substring(valueString.indexOf(",")+1,valueString.indexOf("|")).trim();
			Point point = new Point(Integer.valueOf(x), Integer.valueOf(y));
			Controller.informationDataManage.pickP=point;
			Controller.draw.putDrawQueue(DrawType.Special);
		} catch (Exception e) {
		}
		
	}
}

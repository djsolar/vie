package com.sunmap.teleview.view.mainfram;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.element.mm.data.Road;
import com.sunmap.teleview.element.view.data.BackGroundData;
import com.sunmap.teleview.element.view.data.RoadData;
import com.sunmap.teleview.element.view.data.RoadTextLineData;
import com.sunmap.teleview.element.view.data.TeleViewText;
import com.sunmap.teleview.thread.DrawThread.DrawType;
import com.sunmap.teleview.view.assist.fram.MyList;
import com.sunmap.teleview.view.assist.fram.MyList.IListVale;
import com.sunmap.teleview.view.assist.fram.MyTable;
import com.sunmap.teleview.view.assist.mode.MyListMode;
import com.sunmap.teleview.view.assist.mode.MyTableModle;
/**信息栏
 * @author lijingru
 *
 */
public class Information extends javax.swing.JPanel implements IListVale{

	private static Information uniqueInstance = null;	
	private static final long serialVersionUID = 1L;
	private javax.swing.JScrollPane listScrollPane ;
	private javax.swing.JScrollPane infoScrollPane ;
	private MyList myList =null;
	private MyListMode listMode = null;
	private MyTable road_table = new MyTable();
	private MyTableModle tableModle = null;
	private InfoType type;
	public enum InfoType{
		Pickup,Select
	}
	public static Information getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Information();
		}
		return uniqueInstance;
	}
	public void setInfoType(InfoType type){
		this.type = type;
	}
	private Information(){
		//初始化
		init();
		setVisible(true);// 显示
	}
	
	/**
	 * 设置内容
	 * @param strs
	 */
	public void setList(List<String> strs) {
		String[] strings = new String[strs.size()];
		for (int i = 0; i < strs.size(); i++) {
			strings[i] = strs.get(i);
		}
		listMode  = new MyListMode(strings);
		myList.setModel(listMode);
		listScrollPane.setViewportView(myList);
	}
	
	@Override
	public void valueChanged(int index) {
		if (index >= 0) {
			updateTable(index);	
		}
	}
	
	@Override
	public void moveChanged(int index) {
		if (index >= 0&&this.type == InfoType.Select ) {
			if (Controller.informationDataManage.listInInfor.size() <= 0) {
				return;
			}
			Object o = Controller.informationDataManage.listInInfor.get(index);
			if (o instanceof TeleViewText) {
				TeleViewText one = (TeleViewText) o;
				Point point = new Point(one.x, one.y);
				Controller.informationDataManage.pickP = point;
				Controller.moveMap(point);
			}
		}
	}
	
	@Override
	public void drawChanged(int index) {
		if (index<0||Controller.informationDataManage.listInInfor.size() == 0||this.type == InfoType.Select ) {
			return;
		}
		Object o = Controller.informationDataManage.listInInfor.get(index);
		if (o instanceof RoadData) {
			RoadData roadData = (RoadData) o;
			Controller.informationDataManage.setSpecialDraw(roadData);
		}else if (o instanceof Road) {
			Road road = (Road) o;
			Controller.informationDataManage.setSpecialDraw(road);
		}else if (o instanceof TeleViewText) {
			TeleViewText road = (TeleViewText) o;
			Controller.informationDataManage.setSpecialDraw(road);
		}else if (o instanceof BackGroundData) {
			BackGroundData bg = (BackGroundData) o;
			Controller.informationDataManage.setSpecialDraw(bg);
		}else if (o instanceof RoadTextLineData) {
			RoadTextLineData roadLine = (RoadTextLineData) o;
			Controller.informationDataManage.setSpecialDraw(roadLine);
		}
		 Controller.draw.putDrawQueue(DrawType.Special);
	}
	
	private void init() {
		listScrollPane = new javax.swing.JScrollPane();
		infoScrollPane = new javax.swing.JScrollPane();
		// 设置窗口大小
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int farmwidth = screenSize.width* 2 / 3;
		int farmHeight = screenSize.height * 1 / 3;
		setSize(farmwidth, farmHeight);
		// 位置
		setLocation((screenSize.width - farmwidth) / 2, (screenSize.height - farmHeight) / 5);
		// 布局
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout
			.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
			.addComponent(listScrollPane,GroupLayout.PREFERRED_SIZE,200,GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(infoScrollPane,GroupLayout.DEFAULT_SIZE,200, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
			.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(listScrollPane,GroupLayout.DEFAULT_SIZE, 300,Short.MAX_VALUE)
			.addComponent(infoScrollPane,GroupLayout.DEFAULT_SIZE, 300,Short.MAX_VALUE));
		myList = new MyList();
		myList.reqIListvales(this);
	}
	
	/**
	 * 设置表格属性
	 */
	private void setTable(){
		infoScrollPane.setViewportView(road_table);
		// 设置行高
		road_table.setRowHeight(25);
		// 可以多选可以单选
		road_table.setCellSelectionEnabled(true);
		// 表格选取模式 可以用CTRL
		road_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		TableColumnModel   cm   =   road_table.getColumnModel();     //表格的列模型
		TableColumn   column  = cm.getColumn(0);//得到第i个列对象   
		column.setPreferredWidth(40);//将此列的首选宽度设置为 preferredWidth。
		TableColumn   column1  = cm.getColumn(1);//得到第i个列对象   
		column1.setPreferredWidth(500);//将此列的首选宽度设置为 preferredWidth。
		//如果 preferredWidth 超出最小或最大宽度，则将其调整为合适的界限值。
	}
	
	
	/**
	 * 画板通知表格
	 */
	public void updateTable(int index) {
		tableModle = new MyTableModle();
		if (Controller.informationDataManage.listInInfor.size() == 0) {
			tableModle.setHeader(new String[1]);
			tableModle.setValues(new String[1]);
			this.road_table.setModel(tableModle);
			setTable();
			return;
		}
		Object o = Controller.informationDataManage.listInInfor.get(index);
		if (o instanceof RoadData) {
			RoadData roadData = (RoadData) o;
			tableModle.setHeader(roadData.getHeader());
			tableModle.setValues(roadData.getValues());
		}else if (o instanceof Road) {
			Road road = (Road) o;
			tableModle.setHeader(road.getHeader());
			tableModle.setValues(road.getValues());
		}else if (o instanceof TeleViewText) {
			TeleViewText road = (TeleViewText) o;
			tableModle.setHeader(road.getHeader());
			tableModle.setValues(road.getValues());
		}else if (o instanceof BackGroundData) {
			BackGroundData bg = (BackGroundData) o;
			tableModle.setHeader(bg.getHeader());
			tableModle.setValues(bg.getValues());
		}else if (o instanceof RoadTextLineData) {
			RoadTextLineData roadLine = (RoadTextLineData) o;
			tableModle.setHeader(roadLine.getHeader());
			tableModle.setValues(roadLine.getValues());
		}
		this.road_table.setModel(tableModle);
		this.road_table.reqIListvales(tableModle);
		setTable();
	}
	

}

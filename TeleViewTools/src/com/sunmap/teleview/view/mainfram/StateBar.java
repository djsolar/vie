package com.sunmap.teleview.view.mainfram;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import com.sunmap.teleview.Controller;

/**状态栏
 * @author lijingru
 * 
 */
public class StateBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JLabel txt_centerPoint;
	private javax.swing.JLabel txt_info;

	
	/**
	 * 设置中心点状态栏
	 * @param str
	 */
	public void setTxt_centerPoint(String str){
		txt_centerPoint.setText(str);
	}
	/**
	 * 设置信息窗口状态栏
	 * @param str
	 */
	public void setTxt_Info(String str){
		txt_info.setText(str);
	}
	
	/**
	 * 初始化
	 */
	@SuppressWarnings("static-access")
	public void initComponents() {
		int level = Controller.telemanage.getViewBaseInfo().curDataLevel;
		txt_centerPoint = new javax.swing.JLabel();
		txt_info = new javax.swing.JLabel();
		txt_centerPoint.setText("mouse");
		txt_info.setText("信息：");
		String s = "信息    " + " level :  " + level +"  scale :" + Controller.drawParams.currMapScale;
		setTxt_Info(s);
		GroupLayout jPanel2Layout = new GroupLayout(this);
		this.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup()
				.addComponent(txt_centerPoint, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(txt_info, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
				jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(txt_info)
				.addComponent(txt_centerPoint)
						));
	}
}

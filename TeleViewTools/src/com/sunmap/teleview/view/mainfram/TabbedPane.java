package com.sunmap.teleview.view.mainfram;

import javax.swing.JTabbedPane;


/**选项卡
 * @author lijingru
 *
 */
public class TabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	
	public Information information = Information.getInstance();
	public Console console = Console.getInstance();
	public enum TabbedMode {
		Info,Console
	}
	/**
	 * 初始化
	 */
	public void initComponents(){
		addTab("属性", null, information, null);
		addTab("控制台", null, console, null);
	}

	/**
	 * 设置显示
	 * @param selected 是否显示
	 * @param mode	选项卡类型
	 */
	public void setTabbedVisible(boolean selected, TabbedMode mode) {
		switch (mode) {
		case Info:
			if (selected == true) {
				addTab("属性", null, information, null);
				selectParam(mode);
			} else {
				remove(information);
			}
			break;
		case Console:
			if (selected == true) {
				addTab("控制台", null, console, null);
				selectParam(mode);
			} else {
				remove(console);
			}
			break;
		}
		if (this.getTabCount() == 0) {
			setVisible(false);
		} else {
			setVisible(true);
		}
	}
	
	/**
	 * 跳转选项卡
	 * 
	 * @param inputRoads
	 */
	private void selectParam(TabbedMode mode) {
		switch (mode) {
		case Info:
			setSelectedComponent(information);
			break;
		case Console:
			setSelectedComponent(console);
			break;
		}
	}
	
}

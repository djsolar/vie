package com.sunmap.teleview.view.mainfram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.view.assist.fram.HelpDialog;
import com.sunmap.teleview.view.assist.fram.Picture;
import com.sunmap.teleview.view.assist.fram.SelectBlockD;
import com.sunmap.teleview.view.assist.fram.SelectLocationD;
import com.sunmap.teleview.view.assist.fram.SelectMMBlockD;
import com.sunmap.teleview.view.assist.fram.SelectLandMarkD;
import com.sunmap.teleview.view.mainfram.TabbedPane.TabbedMode;

/**
 * @author lijingru
 *
 */
public class MenuBar extends JMenuBar implements ActionListener{

	private static final long serialVersionUID = 1L;
	private javax.swing.JMenu menu_file;
	private javax.swing.JMenu menu_help;
	private javax.swing.JMenu menu_look;
	private javax.swing.JMenu menu_select;
	private javax.swing.JMenu menu_windows;
	public javax.swing.JCheckBoxMenuItem cmit_infoShow;
	public javax.swing.JCheckBoxMenuItem cmit_consoleShow;
	public javax.swing.JCheckBoxMenuItem cmit_stateShow;
	public javax.swing.JCheckBoxMenuItem cmit_toolShow;
	private javax.swing.JMenuItem mit_openData;
	private javax.swing.JMenuItem mit_quit;
	private javax.swing.JMenuItem mit_about;
	private javax.swing.JMenuItem mit_helper;
	private javax.swing.JMenuItem mit_layer;
	private javax.swing.JMenuItem mit_lock;
	private javax.swing.JMenuItem mit_selLocation;
	private javax.swing.JMenuItem mit_selViewBID;
	private javax.swing.JMenuItem mit_selMMBID;
	private javax.swing.JMenuItem mit_selPOI;
	
	
	
	private javax.swing.JSeparator jSeparator[] = new javax.swing.JSeparator[7];

	public void setTabbedPaneShow(boolean b, TabbedMode mode){
		switch (mode) {
		case Info:
			cmit_infoShow.setSelected(b);
			break;
		case Console:
			cmit_consoleShow.setSelected(b);
			break;
		}
	}
	@SuppressWarnings("static-access")
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == mit_openData) {
			Controller.openFile();
		}else if (e.getSource() == mit_lock) {
			boolean locklever = Controller.telemanage.getViewBaseInfo().isLockLevel;
			Controller.UI.toolsBarBase.btn_lockLever.setSelected(!locklever);
			Controller.lockLayer(!locklever);
		}else if (e.getSource() == mit_about) {
			HelpDialog dialog = new HelpDialog(Controller.UI, true);
			dialog.setVisible(true);
		}else if (e.getSource() == mit_helper) {
			
		}else if (e.getSource() == cmit_infoShow) {
			Controller.setInfoVisible(cmit_infoShow.isSelected());
		}else if (e.getSource() == cmit_consoleShow) {
			Controller.setConsoleVisible(cmit_consoleShow.isSelected());
		}else if (e.getSource() == cmit_toolShow) {
			Controller.UI.toolsBarBase.setVisible(cmit_toolShow.isSelected());
			Controller.saveUIStatus("./prefer.properties");
			Controller.loadUIStatus("./prefer.properties");
		}else if (e.getSource() == cmit_stateShow) {
			Controller.UI.stateBar.setVisible(cmit_stateShow.isSelected());
			Controller.saveUIStatus("./prefer.properties");
			Controller.loadUIStatus("./prefer.properties");
		} else if (e.getSource() == mit_layer) {
			Picture picture = new Picture(Controller.UI, true,"图层");
			picture.setVisible(true);
		}else if (e.getSource() == mit_quit) {
			Controller.UI.dispose();
			System.exit(0);
		}else if (e.getSource() == mit_selLocation) {
			//位置检索
			SelectLocationD location = new SelectLocationD(Controller.UI, " 位置检索 ", true);
			location.setVisible(true);
		}else if (e.getSource() == mit_selViewBID) {
			//ViewBlockID检索
			SelectBlockD selectBlockD = new SelectBlockD(Controller.UI, "ViewBlockID检索", true);
			selectBlockD.setVisible(true);
		}else if (e.getSource() == mit_selMMBID) {
			//MMBlockID检索
			SelectMMBlockD selectBlockD = new SelectMMBlockD(Controller.UI, "MMBlockID检索", true);
			selectBlockD.setVisible(true);
		}else if (e.getSource() == mit_selPOI) {
			//地标名称检索
			SelectLandMarkD selectPoi= new SelectLandMarkD(Controller.UI, "地标名称检索", true);
			selectPoi.setVisible(true);
		}
	}
	
	public void initComponents() {
		menu_file = new javax.swing.JMenu();
		menu_look = new javax.swing.JMenu();
		menu_select = new javax.swing.JMenu();
		menu_windows = new javax.swing.JMenu();
		menu_help = new javax.swing.JMenu();
		mit_openData = new javax.swing.JMenuItem();
		mit_quit = new javax.swing.JMenuItem();
		mit_layer = new javax.swing.JMenuItem();
		mit_lock = new javax.swing.JMenuItem();
		mit_selLocation = new javax.swing.JMenuItem(); 
		mit_selViewBID	= new javax.swing.JMenuItem();
		mit_selMMBID	= new javax.swing.JMenuItem();
		mit_selPOI	= new javax.swing.JMenuItem();
		cmit_infoShow = new javax.swing.JCheckBoxMenuItem();
		cmit_consoleShow = new javax.swing.JCheckBoxMenuItem();
		cmit_toolShow = new javax.swing.JCheckBoxMenuItem();
		cmit_stateShow = new javax.swing.JCheckBoxMenuItem();
		mit_about = new javax.swing.JMenuItem();
		mit_helper = new javax.swing.JMenuItem();
		jSeparator[0] = new javax.swing.JSeparator();
		jSeparator[1] = new javax.swing.JSeparator();
		jSeparator[2] = new javax.swing.JSeparator();
		jSeparator[3] = new javax.swing.JSeparator();
		jSeparator[4] = new javax.swing.JSeparator();
		jSeparator[5] = new javax.swing.JSeparator();
		jSeparator[6] = new javax.swing.JSeparator();
		initTxt();
	}
	
	private void initTxt() {
		//文件菜单
		menu_file.setText("文件");
		//打开数据子菜单
		mit_openData.setIcon(new ImageIcon("ass/icon/openfile.png")); 
		mit_openData.setText("打开文件");
		mit_openData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
		//打开数据子菜单加入到文件菜单
		menu_file.add(mit_openData);
		menu_file.add(jSeparator[0]);
		//退出菜单项
		mit_quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.CTRL_MASK));
		mit_quit.setIcon(new ImageIcon("ass/icon/close.png"));
		mit_quit.setText("退出");
		menu_file.add(mit_quit);
		add(menu_file);
		//检索菜单
		menu_select.setText("检索");
		//位置检索
		mit_selLocation.setText("位置检索");
		mit_selLocation.setIcon(new ImageIcon("ass/icon/s_location.png"));
		menu_select.add(mit_selLocation);
		menu_select.add(jSeparator[5]);
		//ViewBlockID检索
		mit_selViewBID.setText("ViewBlockID检索");
		mit_selViewBID.setIcon(new ImageIcon("ass/icon/s_block.png"));
		menu_select.add(mit_selViewBID);
		//MMBlockID检索
		mit_selMMBID.setText("MMBlockID检索");
		mit_selMMBID.setIcon(new ImageIcon("ass/icon/s_mmblock.png"));
		menu_select.add(mit_selMMBID);
		menu_select.add(jSeparator[6]);
		//	地标名称检索
		mit_selPOI.setText("地标名称检索");
		mit_selPOI.setIcon(new ImageIcon("ass/icon/s_poi.png"));
		menu_select.add(mit_selPOI);
		add(menu_select);
		//视图菜单
		menu_look.setText("视图");
		//图层菜单项
		mit_layer.setIcon(new ImageIcon("ass/icon/picture.png"));
		mit_layer.setText("图层");
		menu_look.add(mit_layer);
		menu_look.add(jSeparator[1]);
		//显示工具选择窗口
		cmit_toolShow.setText("显示工具栏");
		cmit_toolShow.setIcon(new ImageIcon("ass/icon/tools_l.png"));
		menu_look.add(cmit_toolShow);
		//显示状态栏选择窗口
		cmit_stateShow.setText("显示状态栏");
		cmit_stateShow.setIcon(new ImageIcon("ass/icon/state.png"));
		menu_look.add(cmit_stateShow);
		menu_look.add(jSeparator[3]);
		//锁层菜单项
		mit_lock.setIcon(new ImageIcon("ass/icon/lock_off.png"));
		mit_lock.setText("锁层");
		menu_look.add(mit_lock);
		add(menu_look);
		//窗口菜单
		menu_windows.setText("窗口");
		//属性窗口选项卡选择窗口
		cmit_infoShow.setSelected(true);
		cmit_infoShow.setText("属性窗口");
		cmit_infoShow.setIcon(new ImageIcon("ass/icon/info.png"));
		menu_windows.add(cmit_infoShow);
		//属性窗口选项卡选择窗口
		cmit_consoleShow.setSelected(true);
		cmit_consoleShow.setText("控制台窗口");
		cmit_consoleShow.setIcon(new ImageIcon("ass/icon/console.png"));
		menu_windows.add(cmit_consoleShow);
		add(menu_windows);
		//帮助菜单
		menu_help.setText("帮助");
		//关于版本菜单项
		mit_about.setIcon(new ImageIcon("ass/imge/help.png")); 
		mit_about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_MASK));
		mit_about.setText("关于版本");
		menu_help.add(mit_about);
		menu_help.add(jSeparator[4]);
		//小助手菜单项
		mit_helper.setIcon(new ImageIcon("ass/icon/helper.png")); 
		mit_helper.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.CTRL_MASK));
		mit_helper.setText("小助手");
		menu_help.add(mit_helper);
		add(menu_help);
		//菜单栏监听
		mit_openData.addActionListener(this);
		mit_quit .addActionListener(this);
		mit_about.addActionListener(this);
		mit_helper.addActionListener(this);
		mit_layer.addActionListener(this);
		mit_lock.addActionListener(this);
		cmit_toolShow.addActionListener(this);
		cmit_stateShow.addActionListener(this);
		cmit_infoShow.addActionListener(this);
		cmit_consoleShow.addActionListener(this);
		mit_selLocation.addActionListener(this);
		mit_selViewBID.addActionListener(this);
		mit_selMMBID.addActionListener(this);
		mit_selPOI.addActionListener(this);
	}
	
}

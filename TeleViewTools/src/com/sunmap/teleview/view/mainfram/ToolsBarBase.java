package com.sunmap.teleview.view.mainfram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.view.assist.fram.HelpDialog;
import com.sunmap.teleview.view.assist.fram.Picture;
import com.sunmap.teleview.view.assist.fram.SelectBlockD;
import com.sunmap.teleview.view.assist.fram.SelectLocationD;
import com.sunmap.teleview.view.assist.fram.SelectMMBlockD;
import com.sunmap.teleview.view.assist.fram.SelectLandMarkD;


/**工具栏
 * @author lijingru
 *
 */
public class ToolsBarBase extends javax.swing.JToolBar implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private javax.swing.JButton btn_oView;
	private javax.swing.JButton btn_picture;
	public javax.swing.JToggleButton tn_info;
	public javax.swing.JToggleButton tn_console;
	private javax.swing.JButton btn_sLocation;
	private javax.swing.JButton btn_sBlock;
	private javax.swing.JButton btn_sMMBlock;
	private javax.swing.JButton btn_sPoi;
	public javax.swing.JToggleButton btn_lockLever;
	private javax.swing.JButton btn_helper;
	
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_picture) {
			Picture picture = new Picture(Controller.UI, true,"图层");
			picture.setVisible(true);
		}else if (e.getSource() == btn_oView) {
			Controller.openFile();
		}else if (e.getSource() == tn_info) {
			Controller.setInfoVisible(tn_info.isSelected());
		}else if (e.getSource() == tn_console) {
			Controller.setConsoleVisible(tn_console.isSelected());
		}else if (e.getSource() == btn_lockLever) {
			btn_lockLever.setSelected(btn_lockLever.isSelected());
			Controller.lockLayer(btn_lockLever.isSelected());
		}else if (e.getSource() == btn_helper) {
			HelpDialog dialog = new HelpDialog(Controller.UI, true);
			dialog.setVisible(true);
		}else if (e.getSource() == btn_sLocation) {
			//位置检索
			SelectLocationD location = new SelectLocationD(Controller.UI, " 位置检索 ", true);
			location.setVisible(true);
		}else if (e.getSource() == btn_sBlock) {
			//ViewBlockID检索
			SelectBlockD selectBlockD = new SelectBlockD(Controller.UI, "ViewBlockID检索", true);
			selectBlockD.setVisible(true);
		}else if (e.getSource() == btn_sMMBlock) {
			//MMBlockID检索
			SelectMMBlockD selectBlockD = new SelectMMBlockD(Controller.UI, "MMBlockID检索", true);
			selectBlockD.setVisible(true);
		}else if (e.getSource() == btn_sPoi) {
			//TODO  弹出检索窗口
			//地标名称检索
			SelectLandMarkD selectPoi= new SelectLandMarkD(Controller.UI, "地标名称检索", true);
			selectPoi.setVisible(true);
		}
		
	}

	
	/**
	 * 初始化
	 */
	public void initComponents() {
		btn_lockLever = new javax.swing.JToggleButton();
		btn_oView = new javax.swing.JButton();
		btn_picture = new javax.swing.JButton();
		tn_info = new javax.swing.JToggleButton();
		tn_console = new javax.swing.JToggleButton();
		btn_sLocation = new javax.swing.JButton();
		btn_sBlock = new javax.swing.JButton();
		btn_sMMBlock = new javax.swing.JButton();
		btn_sPoi = new javax.swing.JButton();
		btn_lockLever = new javax.swing.JToggleButton();
		btn_helper = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		initPlace();
	}
	
	private void initPlace() {
		//不让拖拽
		setFloatable(false);
		//鼠标移动到高亮显示
		this.setRollover(true);
		jLabel1.setText("	||	");
		add(jLabel1);
		//打开数据按钮
		btn_oView.setIcon(new ImageIcon("ass/icon/openfile.png")); 
		btn_oView.setToolTipText("打开view数据");
		btn_oView.setFocusable(false);
		btn_oView.setHorizontalTextPosition(SwingConstants.CENTER);
		btn_oView.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn_oView.addActionListener(this);
		add(btn_oView);
		jLabel2.setText("	||	");
		add(jLabel2);
		//图层按钮
		btn_picture.setIcon(new ImageIcon("ass/icon/picture.png")); 
		btn_picture.setToolTipText("图层");
		btn_picture.setFocusable(false);
		btn_picture.setHorizontalTextPosition(SwingConstants.CENTER);
		btn_picture.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn_picture.addActionListener(this);
		add(btn_picture);
		jLabel3.setText("	||	    ");
		add(jLabel3);
		//检索区域
		jLabel8.setIcon(new ImageIcon("ass/icon/select.png"));
		jLabel8.setText("	 	");
		add(jLabel8);
		//检索位置按钮
		btn_sLocation.setIcon(new ImageIcon("ass/icon/s_location.png")); 
		btn_sLocation.setToolTipText("位置检索");
		btn_sLocation.setFocusable(false);
		btn_sLocation.setHorizontalTextPosition(SwingConstants.CENTER);
		btn_sLocation.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn_sLocation.addActionListener(this);
		add(btn_sLocation);
		// 检索Block
		btn_sBlock.setIcon(new ImageIcon("ass/icon/s_block.png"));
		btn_sBlock.setToolTipText("检索Block");
		btn_sBlock.setFocusable(false);
		btn_sBlock.setHorizontalTextPosition(SwingConstants.CENTER);
		btn_sBlock.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn_sBlock.addActionListener(this);
		add(btn_sBlock);
		// 检索MMblock
		btn_sMMBlock.setIcon(new ImageIcon("ass/icon/s_mmblock.png"));
		btn_sMMBlock.setToolTipText("检索mmBlock");
		btn_sMMBlock.setFocusable(false);
		btn_sMMBlock.setHorizontalTextPosition(SwingConstants.CENTER);
		btn_sMMBlock.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn_sMMBlock.addActionListener(this);
		add(btn_sMMBlock);
		// 检索POI
		btn_sPoi.setIcon(new ImageIcon("ass/icon/s_poi.png"));
		btn_sPoi.setToolTipText("检索POI");
		btn_sPoi.setFocusable(false);
		btn_sPoi.setHorizontalTextPosition(SwingConstants.CENTER);
		btn_sPoi.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn_sPoi.addActionListener(this);
		add(btn_sPoi);
		jLabel4.setText("	||	");
		add(jLabel4);
		//锁层按钮
		btn_lockLever.setIcon(new ImageIcon("ass/icon/lock_on.png")); 
		btn_lockLever.setSelectedIcon(new ImageIcon("ass/icon/lock_off.png"));
		btn_lockLever.setToolTipText("锁层");
		btn_lockLever.setFocusable(false);
		btn_lockLever.setHorizontalTextPosition(SwingConstants.CENTER);
		btn_lockLever.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn_lockLever.addActionListener(this);
		add(btn_lockLever);
		jLabel5.setText(" 	||	 ");
		add(jLabel5);
		//属性选项卡按钮
		tn_info.setIcon(new ImageIcon("ass/icon/info.png")); 
		tn_info.setToolTipText("属性窗口");
		tn_info.setFocusable(false);
		tn_info.setHorizontalTextPosition(SwingConstants.CENTER);
		tn_info.setVerticalTextPosition(SwingConstants.BOTTOM);
		tn_info.addActionListener(this);
		add(tn_info);
		//控制台选项卡按钮
		tn_console.setIcon(new ImageIcon("ass/icon/console.png")); 
		tn_console.setToolTipText("控制台窗口");
		tn_console.setFocusable(false);
		tn_console.setHorizontalTextPosition(SwingConstants.CENTER);
		tn_console.setVerticalTextPosition(SwingConstants.BOTTOM);
		tn_console.addActionListener(this);
		add(tn_console);
		jLabel6.setText(" 	||	 ");
		add(jLabel6);
		//帮助
		btn_helper.setIcon(new ImageIcon("ass/icon/help.png")); 
		btn_helper.setToolTipText("帮助");
		btn_helper.setFocusable(false);
		btn_helper.setHorizontalTextPosition(SwingConstants.CENTER);
		btn_helper.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn_helper.addActionListener(this);
		add(btn_helper);
		jLabel7.setText("	||	");
		add(jLabel7);
	}
	
	

}

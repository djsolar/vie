package com.sunmap.teleview.view.assist.fram;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.view.mainfram.DrawEleType;


/**
 *
 * @author  lijingru
 */
public class Picture extends  JDialog implements ActionListener{
	
	private  JButton btn_OK;
	private  JButton btn_cancel;
	private  JCheckBox cbl_MM;
	private  JCheckBox cbl_all;
	private  JCheckBox cbl_bg;
	private  JCheckBox cbl_eye;
	private  JCheckBox cbl_poi;
	private  JCheckBox cbl_road;
	private  JCheckBox cbl_roadName;
	private  JCheckBox cbl_block;
	private  JCheckBox cbp_MM;
	private  JCheckBox cbp_block;
	private  JCheckBox cbp_all;
	private  JCheckBox cbp_bg;
	private  JCheckBox cbp_eye;
	private  JCheckBox cbp_poi;
	private  JCheckBox cbp_road;
	private  JCheckBox cbp_roadName;
	private  JPanel tit;
	private  JLabel txt_MM;
	private  JLabel txt_block;
	private  JLabel txt_all;
	private  JLabel txt_bg;
	private  JLabel txt_eye;
	private  JLabel txt_look;
	private  JLabel txt_name;
	private  JLabel txt_pick;
	private  JLabel txt_poi;
	private  JLabel txt_road;
	private  JLabel txt_roadName;
	private  JLabel txt_mmblock;
	private  JCheckBox cbl_mmblock;
	private  JCheckBox cbp_mmblock;
	//记录上次配置文件中描画属性
	private boolean isLookRoad;
	private boolean isLookBg;
	private boolean isLookPoi;
	private boolean isLookRoadName;
	private boolean isLookEye;
	private boolean isLookMM;
	private boolean isPickEye;
	private boolean isPickRoad;
	private boolean isPickMM;
	private boolean isPickPoi;
	private boolean isPickRoadName;
	private boolean isPickBg;
	private boolean isLookBlock;
	private boolean isLookMMBlock;
	
	private static final long serialVersionUID = 1L;
	
	public Picture(java.awt.Frame parent, boolean modal,String str) {
		super(parent, modal);
		initComponents(str);
		setResizable(false);//禁止改变窗口大小
		this.setLocation(parent.getLocation().x + 300, parent.getLocation().y + 100);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_OK) {
			DrawEleType drawEleType = new DrawEleType();
			drawEleType.drawRoad = cbl_road.isSelected();
			drawEleType.drawBg = cbl_bg.isSelected();
			drawEleType.drawRoadName = cbl_roadName.isSelected();
			drawEleType.drawPOI = cbl_poi.isSelected();
			drawEleType.drawEye = cbl_eye.isSelected();
			drawEleType.drawBlock = cbl_block.isSelected();
			drawEleType.pickEye = cbp_eye.isSelected();
			drawEleType.pickRoad = cbp_road.isSelected();
			drawEleType.pickBg = cbp_bg.isSelected();
			drawEleType.pickPoi = cbp_poi.isSelected();
			drawEleType.pickRoadName = cbp_roadName.isSelected();
			drawEleType.drawMM = cbl_MM.isSelected();
			drawEleType.drawMMBlock = cbl_mmblock.isSelected();
			drawEleType.pickMM = cbp_MM.isSelected();
			Controller.layerChanger(drawEleType);
			this.dispose();
		}else if (e.getSource() == btn_cancel) {
			this.dispose();
		}else if (e.getSource() == cbl_all) {
			cbl_road.setSelected(cbl_all.isSelected());
			cbl_bg.setSelected(cbl_all.isSelected());
			cbl_poi.setSelected(cbl_all.isSelected());
			cbl_eye.setSelected(cbl_all.isSelected());
			cbl_MM.setSelected(cbl_all.isSelected());
			cbl_block.setSelected(cbl_all.isSelected());
			cbl_mmblock.setSelected(cbl_all.isSelected());
			cbl_roadName.setSelected(cbl_all.isSelected());
			cbp_all.setEnabled(cbl_all.isSelected());
			cbp_eye.setEnabled(cbl_all.isSelected());
			cbp_MM.setEnabled(cbl_all.isSelected());
			cbp_road.setEnabled(cbl_all.isSelected());
			cbp_poi.setEnabled(cbl_all.isSelected());
			cbp_roadName.setEnabled(cbl_all.isSelected());
			cbp_bg.setEnabled(cbl_all.isSelected());
			if (cbl_all.isSelected() == false) {
				cbp_all.setSelected(false);
				cbp_eye.setSelected(false);
				cbp_MM.setSelected(false);
				cbp_road.setSelected(false);
				cbp_poi.setSelected(false);
				cbp_roadName.setSelected(false);
				cbp_bg.setSelected(false);
			}
		}else if (e.getSource() == cbp_all) {
			if (cbl_eye.isSelected() == true) {
				cbp_eye.setSelected(cbp_all.isSelected());
			}
			if (cbl_road.isSelected() == true) {
				cbp_road.setSelected(cbp_all.isSelected());
			}
			if (cbl_MM.isSelected() == true) {
				cbp_MM.setSelected(cbp_all.isSelected());
			}
			if (cbl_poi.isSelected() == true) {
				cbp_poi.setSelected(cbp_all.isSelected());
			}
			if (cbl_roadName.isSelected() == true) {
				cbp_roadName.setSelected(cbp_all.isSelected());
			}
			if (cbl_bg.isSelected() == true) {
				cbp_bg.setSelected(cbp_all.isSelected());
			}
		}else if (e.getSource() == cbl_road) {
			cbp_road.setEnabled(cbl_road.isSelected());
			if (cbl_road.isSelected()==false) {
				cbp_road.setSelected(false);
			}
		}else if (e.getSource() == cbl_MM) {
			cbp_MM.setEnabled(cbl_MM.isSelected());
			if (cbl_MM.isSelected()==false) {
				cbp_MM.setSelected(false);
			}
		}else if (e.getSource() == cbl_poi) {
			cbp_poi.setEnabled(cbl_poi.isSelected());
			if (cbl_poi.isSelected()==false) {
				cbp_poi.setSelected(false);
			}
		}else if (e.getSource() == cbl_roadName) {
			cbp_roadName.setEnabled(cbl_roadName.isSelected());
			if (cbl_roadName.isSelected()==false) {
				cbp_roadName.setSelected(false);
			}
		}else if (e.getSource() == cbl_eye) {
			cbp_eye.setEnabled(cbl_eye.isSelected());
			if (cbl_eye.isSelected()==false) {
				cbp_eye.setSelected(false);
			}
		}else if (e.getSource() == cbl_bg) {
			cbp_bg.setEnabled(cbl_bg.isSelected());
			if (cbl_bg.isSelected() == false) {
				cbp_bg.setSelected(false);
			}
		}
	}
	
	private void initComponents(String str) {
		this.setTitle(str);
		tit = new  JPanel();
		txt_name = new  JLabel();
		txt_look = new  JLabel();
		txt_pick = new  JLabel();
		txt_road = new  JLabel();
		txt_bg = new  JLabel();
		txt_poi = new  JLabel();
		txt_roadName = new  JLabel();
		txt_MM = new JLabel();
		txt_block = new JLabel();
		txt_mmblock = new JLabel();
		txt_eye = new  JLabel();
		txt_all = new  JLabel();
		btn_OK = new  JButton();
		btn_cancel = new  JButton();
		cbl_road = new  JCheckBox();
		cbl_bg = new  JCheckBox();
		cbl_poi = new  JCheckBox();
		cbl_roadName = new  JCheckBox();
		cbl_MM = new JCheckBox();
		cbl_block = new JCheckBox();
		cbl_mmblock = new JCheckBox();
		cbl_eye = new  JCheckBox();
		cbl_all = new  JCheckBox();
		cbp_road = new  JCheckBox();
		cbp_bg = new  JCheckBox();
		cbp_poi = new  JCheckBox();
		cbp_roadName = new  JCheckBox();
		cbp_MM = new JCheckBox();
		cbp_block = new JCheckBox();
		cbp_mmblock = new JCheckBox(); 
		cbp_eye = new  JCheckBox();
		cbp_all = new  JCheckBox();
		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE);
		tit.setBackground(new java.awt.Color(153, 153, 255));
		txt_name.setText("名称");
		txt_look.setText("显示");
		txt_pick.setText("拾取");
		 GroupLayout titLayout = new  GroupLayout(tit);
		tit.setLayout(titLayout);
		titLayout.setHorizontalGroup(titLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(titLayout.createSequentialGroup().addContainerGap()
										.addComponent(txt_name).addGap(60)
										.addComponent(txt_look).addGap(47)
										.addComponent(txt_pick).addGap(26)));
		titLayout.setVerticalGroup(titLayout.createParallelGroup( GroupLayout.Alignment.LEADING)
						.addGroup(titLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(txt_name)
										.addComponent(txt_look)
										.addComponent(txt_pick)));

		txt_road.setText("view道路");
		txt_bg.setText("背景");
		txt_poi.setText("地标");
		txt_roadName.setText("道路文字");
		txt_MM.setText("MM");
		txt_block.setText("Block边框");
		txt_mmblock.setText("MMBlock边框");
		txt_eye.setText("电子眼");
		txt_all.setText("全选");
		btn_OK.setText("确定");
		btn_cancel.setText("取消");
		cbl_all.addActionListener(this);
		cbp_block.setEnabled(false);
		cbp_mmblock.setEnabled(false);
		btn_OK.addActionListener(this);
		btn_cancel.addActionListener(this);
		cbp_all.addActionListener(this);
		cbl_road.addActionListener(this);
		cbl_MM.addActionListener(this);
		cbl_poi.addActionListener(this);
		cbl_roadName.addActionListener(this);
		cbl_eye.addActionListener(this);
		cbl_bg.addActionListener(this);
		GroupLayout layout = new  GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
								 GroupLayout.Alignment.LEADING)
								 .addComponent(tit)
						.addGroup(layout.createSequentialGroup()
										.addContainerGap()
										.addComponent(btn_OK).addGap(79)
										.addComponent(btn_cancel)
										.addContainerGap())
						.addGroup(layout.createSequentialGroup()
										.addContainerGap()
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(txt_road)
														.addComponent(txt_bg)
														.addComponent(txt_poi)
														.addComponent(txt_roadName)
														.addComponent(txt_MM)
														.addComponent(txt_block)
														.addComponent(txt_mmblock)
														.addComponent(txt_eye)
														.addComponent(txt_all))
										.addGap(38)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(cbl_all)
														.addComponent(cbl_eye)
														.addComponent(cbl_MM)
														.addComponent(cbl_block)
														.addComponent(cbl_mmblock)
														.addComponent(cbl_roadName)
														.addComponent(cbl_poi)
														.addComponent(cbl_bg)
														.addComponent(cbl_road))
										.addGap(48)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(cbp_road)
														.addComponent(cbp_bg)
														.addComponent(cbp_poi)
														.addComponent(cbp_roadName)
														.addComponent(cbp_MM)
														.addComponent(cbp_block)
														.addComponent(cbp_mmblock)
														.addComponent(cbp_eye)
														.addComponent(cbp_all))));
		layout.setVerticalGroup(layout.createParallelGroup( GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addComponent(tit)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(txt_road)
														.addComponent(cbl_road)
														.addComponent(cbp_road))
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(txt_bg)
														.addComponent(cbl_bg)
														.addComponent(cbp_bg))	
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(txt_poi)
														.addComponent(cbl_poi)
														.addComponent(cbp_poi))	
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(txt_roadName)
														.addComponent(cbl_roadName)
														.addComponent(cbp_roadName))	
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(txt_MM)
														.addComponent(cbl_MM)
														.addComponent(cbp_MM))
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(txt_block)
														.addComponent(cbl_block)
														.addComponent(cbp_block))
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(txt_mmblock)
														.addComponent(cbl_mmblock)
														.addComponent(cbp_mmblock))
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(txt_eye)
														.addComponent(cbl_eye)
														.addComponent(cbp_eye))	
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(txt_all)
														.addComponent(cbl_all)
														.addComponent(cbp_all))	
										.addGap(66)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(btn_OK)
														.addComponent(btn_cancel))
														.addComponent(cbl_all))
										);

		pack();
		getPropertiesValue();
	}

	/**
	 * 获取首选项文件内容
	 * 
	 * @param fileChooser
	 */
	private void getPropertiesValue() {
		// 获取properties文件中内容
		try {
			System.getProperties().load(new FileInputStream("./prefer.properties"));
				//描画道路
				isLookRoad = Controller.UI.drawEleType.drawRoad;
				cbl_road.setSelected(isLookRoad);
				cbp_road.setEnabled(isLookRoad);
				//描画背景
				isLookBg = Controller.UI.drawEleType.drawBg;
				cbl_bg.setSelected(isLookBg);
				cbp_bg.setEnabled(isLookBg);
				//描画POI
				isLookPoi = Controller.UI.drawEleType.drawPOI;
				cbl_poi.setSelected(isLookPoi);
				cbp_poi.setEnabled(isLookPoi);
				//描画道路名称
				isLookRoadName = Controller.UI.drawEleType.drawRoadName;
				cbl_roadName.setSelected(isLookRoadName);
				//描画电子眼
				isLookEye = Controller.UI.drawEleType.drawEye;
				cbl_eye.setSelected(isLookEye);
				cbp_eye.setEnabled(isLookEye);
				//描画MM
				isLookMM = Controller.UI.drawEleType.drawMM;
				cbl_MM.setSelected(isLookMM);
				cbp_MM.setEnabled(isLookMM);
				//描画Block
				isLookBlock = Controller.UI.drawEleType.drawBlock;
				cbl_block.setSelected(isLookBlock);
				//描画mmBlock
				isLookMMBlock = Controller.UI.drawEleType.drawMMBlock;
				cbl_mmblock.setSelected(isLookMMBlock);
				//拾取电子眼
				isPickEye =Controller.UI.drawEleType.pickEye; 
				cbp_eye.setSelected(isPickEye);
				//拾取道路
				isPickRoad = Controller.UI.drawEleType.pickRoad;
				cbp_road.setSelected(isPickRoad);
				//拾取MM
				isPickMM = Controller.UI.drawEleType.pickMM;
				cbp_MM.setSelected(isPickMM);
				//拾取Poi
				isPickPoi= Controller.UI.drawEleType.pickPoi;
				cbp_poi.setSelected(isPickPoi);
				//拾取道路文字线
				isPickRoadName = Controller.UI.drawEleType.pickRoadName;
				cbp_roadName.setSelected(isPickRoadName);
				//拾取bg
				isPickBg= Controller.UI.drawEleType.pickBg;
				cbp_bg.setSelected(isPickBg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
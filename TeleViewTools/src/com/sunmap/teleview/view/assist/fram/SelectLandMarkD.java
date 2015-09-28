package com.sunmap.teleview.view.assist.fram;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.util.AdminCode;
import com.sunmap.teleview.util.AdminCodes;

/*
 * SelectPOID.java
 *
 * Created on __DATE__, __TIME__
 */

/**
 *
 * @author  __USER__
 */
public class SelectLandMarkD extends JDialog  implements KeyListener{

	private static final long serialVersionUID = 1L;
	private JButton btn_cancel;
	private JButton btn_select;
	private JComboBox cbx_city;
	private JComboBox cbx_pro;
	private JTextField edt_adss;
	private JLabel txt_city;
	private JLabel txt_poi;
	private JLabel txt_pro;
	
	public SelectLandMarkD(java.awt.Frame parent, String str,boolean modal) {
		super(parent, str,modal);
		initComponents();
		setResizable(false);//禁止改变窗口大小
		this.setLocation(parent.getLocation().x + 300, parent.getLocation().y + 200);
		cbx_pro.setModel(new DefaultComboBoxModel(AdminCodes.getAdminProvince()));
	}

	private void initComponents() {
		txt_poi = new JLabel();
		cbx_pro = new JComboBox();
		txt_pro = new JLabel();
		cbx_city = new JComboBox();
		txt_city = new JLabel();
		edt_adss = new JTextField();
		btn_select = new JButton();
		btn_cancel = new JButton();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		txt_poi.setText("POI\u540d\u79f0\uff1a");
		txt_pro.setText("\u7701");
		txt_city.setText("\u5e02");
		btn_select.setText("\u67e5\u8be2");
		btn_cancel.setText("\u53d6\u6d88");
		cbx_pro.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					cbx_proActionPerformed(evt);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		btn_select.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_selectActionPerformed();
			}
		});
		btn_cancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_cancelActionPerformed();
			}
		});
		edt_adss.addKeyListener(this);
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
														.addContainerGap()
														.addComponent(txt_poi)
														.addGap(26)
														.addComponent(cbx_pro,
																GroupLayout.PREFERRED_SIZE,100,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(txt_pro)
														.addComponent(cbx_city,
																GroupLayout.PREFERRED_SIZE,100,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(txt_city)
														.addContainerGap())
										.addGroup(layout.createSequentialGroup()
														.addContainerGap()
														.addComponent(txt_poi)
														.addGap(26)
														.addComponent(edt_adss,
																GroupLayout.PREFERRED_SIZE,220,
																GroupLayout.PREFERRED_SIZE)
														.addContainerGap())
										.addGroup(layout.createSequentialGroup()
														.addContainerGap()
														.addGap(100)
														.addComponent(btn_select)
														.addGap(30)
														.addComponent(btn_cancel)
														.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
										.addContainerGap()
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(txt_poi)
														.addComponent(cbx_pro,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(txt_pro)
														.addComponent(cbx_city,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(txt_city))
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)//间隙
										.addComponent(edt_adss,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addGap(10)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(btn_cancel)
														.addComponent(btn_select))
										.addContainerGap(GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)));

		pack();
	}
	
	private void btn_selectActionPerformed() {
		// TODO Auto-generated method stub
		try {
			String landMarkName = edt_adss.getText();
			String provinceName = (String) cbx_pro.getSelectedItem() ;
			String cityName = (String) cbx_city.getSelectedItem() ;
			if (edt_adss.getText().trim().equals("")) {
				showErrorDialog();
				return;
			}
			boolean resultFlag = Controller.landMarkSearch(landMarkName, provinceName, cityName);
			if (resultFlag == false) {
				showNoResultDialog();
				return;
			}
			this.dispose();
		} catch (Exception e) {
			showSelectError();
			e.printStackTrace();
		}
		
	}
	
	private void showSelectError() {
		JOptionPane.showMessageDialog(Controller.UI, "检索错误", "提示", JOptionPane.ERROR_MESSAGE); 
	}
	
	private void showNoResultDialog() {
		JOptionPane.showMessageDialog(Controller.UI, "没有检索到结果", "提示", JOptionPane.ERROR_MESSAGE); 
	}

	private void showErrorDialog() {
		JOptionPane.showMessageDialog(Controller.UI, "输入的值不正确，请重新输入", "提示", JOptionPane.ERROR_MESSAGE); 
	}

	private void btn_cancelActionPerformed() {
		this.dispose();
	}

	private void cbx_proActionPerformed(java.awt.event.ActionEvent evt) throws SQLException {
		int num = cbx_pro.getSelectedIndex();
		if (num == 0) {
			return;
		}
		List<AdminCode> list = AdminCodes.getCityCode(AdminCodes.admins.get(num-1).getAdmin_code());	
		String[] strs = new String[list.size()+1];
		for (int i = 0; i < list.size(); i++) {
			strs[i+1] = list.get(i).getCity();
		}
		strs[0] = "";
		cbx_city.setModel(new DefaultComboBoxModel(strs));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			btn_selectActionPerformed();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
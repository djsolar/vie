package com.sunmap.teleview.view.assist.fram;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.util.AdminCode;
import com.sunmap.teleview.util.AdminCodes;
import com.sunmap.teleview.util.ToolsUnit;
import com.sunmap.teleview.view.mainfram.Console;

/**
 *
 * @author  lijingru
 */
public class SelectLocationD extends JDialog  implements KeyListener{

	private static final long serialVersionUID = 1L;
	
	private ButtonGroup btnGroup;
	private List<JComponent> btnG_location;
	private List<JComponent> btnG_city;
	private JButton btn_OK;
	private JButton btn_location;
	private JComboBox cob_city;
	private JComboBox cob_pro;
	private JComboBox cob_unit; // 单位 0表示1/2560秒	 1表示度
	private JTextField edt_x;
	private JTextField edt_y;
	private JRadioButton rbt_l_one;//经纬度
	private JRadioButton rbt_l_two;//城市
	private JLabel txt_city;
	private JLabel txt_pro;
	private JLabel txt_x;
	private JLabel txt_y;
//	TitledBorder nameTitle =new TitledBorder("FileName List"); 
	
	public SelectLocationD(java.awt.Frame parent, String str,boolean modal) {
		super(parent, str,modal);
		initComponents();
		setResizable(false);//禁止改变窗口大小
		this.setLocation(parent.getLocation().x + 300, parent.getLocation().y + 200);
		cob_pro.setModel(new DefaultComboBoxModel(AdminCodes.getAdminProvince()));
		rbt_l_one.setSelected(true);
		rbt_l_oneActionPerformed();
	}

	
	private void initComponents() {
		btnGroup = new ButtonGroup();
		btnG_location = new ArrayList<JComponent>();
		btnG_city = new ArrayList<JComponent>();
		rbt_l_one = new JRadioButton();
		rbt_l_two = new JRadioButton();
		txt_x = new JLabel();
		txt_y = new JLabel();
		edt_x = new JTextField();
		edt_y = new JTextField();
		cob_unit = new JComboBox();
		cob_pro = new JComboBox();
		txt_pro = new JLabel();
		cob_city = new JComboBox();
		txt_city = new JLabel();
		btn_location = new JButton();
		btn_OK = new JButton();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		rbt_l_one.setText("\u7ecf\u7eac\u5ea6");
		rbt_l_two.setText("\u57ce\u5e02");
		txt_x.setText("x:");
		txt_y.setText("y:");
		cob_unit.setModel(new DefaultComboBoxModel(new String[] {
				"1/2560秒", "度" }));
		txt_pro.setText("\u7701");
		txt_city.setText("\u5e02");
		btn_location.setText("\u5f53\u524d");
		btn_OK.setText("\u786e\u5b9a");
		//单选按钮组
		btnGroup.add(rbt_l_one);
		btnGroup.add(rbt_l_two);
		//经纬度位置组
		btnG_location.add(edt_x);
		btnG_location.add(edt_y);
		btnG_location.add(cob_unit);
		btnG_location.add(txt_x);
		btnG_location.add(txt_y);
		//城市位置组
		btnG_city.add(txt_y);
		btnG_city.add(cob_city);
		btnG_city.add(cob_pro);
		btnG_city.add(txt_city);
		btnG_city.add(txt_pro);
		
		rbt_l_one.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				rbt_l_oneActionPerformed();
			}
		});
		
		rbt_l_two.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				rbt_l_twoActionPerformed();
			}
		});
		cob_unit.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				cob_unitStateChanged(arg0);
			}
		});
		
		cob_pro.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					cob_proActionPerformed();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		
		btn_location.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_locationActionPerformed();
			}
		});
		
		btn_OK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_OKActionPerformed();
			}
		});
		//键盘监听
		edt_x.addKeyListener(this);
		edt_y.addKeyListener(this);
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
						GroupLayout.Alignment.LEADING)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
														.addContainerGap()
														.addComponent(rbt_l_one))
						.addGroup(layout.createSequentialGroup()
														.addContainerGap()
														.addGap(50)
														.addComponent(txt_x)
														.addComponent(edt_x,GroupLayout.PREFERRED_SIZE,95,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(txt_y)
														.addComponent(edt_y,GroupLayout.PREFERRED_SIZE,95,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(cob_unit)
														.addContainerGap())
						.addGroup(layout.createSequentialGroup()
														.addContainerGap()
														.addComponent(rbt_l_two)
														.addComponent(cob_pro,GroupLayout.PREFERRED_SIZE,95,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(txt_pro)
														.addComponent(cob_city,GroupLayout.PREFERRED_SIZE,95,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(txt_city))
						.addGroup(layout.createSequentialGroup()
														.addContainerGap()
														.addGap(100)
														.addComponent(btn_location)
														.addGap(30)
														.addComponent(btn_OK))
														)
								);
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(rbt_l_one)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(txt_x)
												.addComponent(txt_y)
												.addComponent(edt_x,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(edt_y,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(cob_unit))
								.addGap(31)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(rbt_l_two)
												.addComponent(txt_pro)
												.addComponent(cob_pro,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(cob_city,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(txt_city))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,28, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(btn_OK)
												.addComponent(btn_location))
								.addGap(25)));
		pack();
	}

	private void rbt_l_twoActionPerformed() {
		boolean aFlag = rbt_l_two.isSelected();
		for (JComponent iterable : btnG_location) {
			iterable.setEnabled(!aFlag);
		}
		for (JComponent iterable : btnG_city) {
			iterable.setEnabled(aFlag);
		}
	}

	private void rbt_l_oneActionPerformed() {
		boolean aFlag = rbt_l_one.isSelected();
		for (JComponent iterable : btnG_location) {
			iterable.setEnabled(aFlag);
		}
		for (JComponent iterable : btnG_city) {
			iterable.setEnabled(!aFlag);
		}
	}


	private void cob_proActionPerformed() throws SQLException {
		int num = cob_pro.getSelectedIndex();
		if (num == 0) {
			return;
		}
		List<AdminCode> list = AdminCodes.getCityCode(AdminCodes.admins.get(num-1).getAdmin_code());	
		String[] strs = new String[list.size()+1];
		for (int i = 0; i < list.size(); i++) {
			strs[i+1] = list.get(i).getCity();
		}
		strs[0] = "";
		cob_city.setModel(new DefaultComboBoxModel(strs));
		
	}

	private void btn_OKActionPerformed() {
		try {
			//按照坐标检索
			if (rbt_l_one.isSelected() == true) {
				int x = 0;
				int y = 0;
				if (cob_unit.getSelectedIndex() == 1) {//	按1/2560秒检索
					x = (int) (Double.parseDouble(edt_x.getText()) * 2560 *3600);
					y = (int) (Double.parseDouble(edt_y.getText()) * 2560 *3600);
				}else {
					x = Integer.parseInt(edt_x.getText());
					y = Integer.parseInt(edt_y.getText());
				}
				if (ToolsUnit.isInChina(x, y) == false) {
					showNotInChina();
					Console.getInstance().setTxt("提示：输入的坐标不在中国范围内！");
					return;
				}
				if (x != Controller.drawParams.imageCentre.x
						|| y != Controller.drawParams.imageCentre.y) {
					Controller.locationSearch(x, y);
				}
				this.dispose();
			}
			//按城市检索
			if (rbt_l_two.isSelected() == true) {
				int provinceIndex = cob_pro.getSelectedIndex() ;
				int cityIndex = cob_city.getSelectedIndex() ;
				if (provinceIndex != 0) {
					AdminCode adminCode;
					if (cityIndex == 0) {
						adminCode = AdminCodes.admins.get(provinceIndex - 1);
					}else {
						List<AdminCode> list = AdminCodes.getCityCode(AdminCodes.admins.get(provinceIndex - 1).getAdmin_code());
						adminCode = list.get(cityIndex - 1);
					}
					Controller.locationSearch(adminCode.getLon(), adminCode.getLat());
					this.dispose();
				}else {
					showErrorDialog();
				}
			}
			
		} catch (Exception e) {
			Console.getInstance().setTxt("输入的参数错误：" + e.getStackTrace());
			showErrorDialog();
			System.out.println("参数错误");
		}
	}

	
	private void cob_unitStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange()==ItemEvent.DESELECTED) {
			return;
		}
		if (edt_x.getText().trim().equals("") && edt_y.getText().trim().equals("")) {
			System.out.println(edt_x.getText());
			return;
		}
		try {
			int num = cob_unit.getSelectedIndex();
			if (num == 0) {//	按1/2560秒检索
				double centerX = Double.parseDouble(edt_x.getText().trim()) * 2560 * 3600;
				double centerY = Double.parseDouble(edt_y.getText().trim()) * 2560 * 3600;
				edt_x.setText(String.valueOf((int)centerX));
				edt_y.setText(String.valueOf((int)centerY));
			}else {
				DecimalFormat df = new DecimalFormat("#.00000000");// 格式化显示
				double centerX = Double.parseDouble(edt_x.getText().trim()) / 2560 / 3600;
				double centerY = Double.parseDouble(edt_y.getText().trim()) / 2560 / 3600;
				edt_x.setText(String.valueOf(df.format(centerX)));
				edt_y.setText(String.valueOf(df.format(centerY)));
			}
		} catch (Exception e) {
			showErrorDialog();
		}
	}
	
	private void btn_locationActionPerformed() {
		if (rbt_l_one.isSelected() == true) {
			List<String> locationsList = Controller.getLocation();
			if (cob_unit.getSelectedIndex() == 0) {//	1/2560秒
				edt_x.setText(locationsList.get(0));
				edt_y.setText(locationsList.get(1));
			}else {
				DecimalFormat df = new DecimalFormat("#.00000000");// 格式化显示
				double centerX = Double.parseDouble(locationsList.get(0)) / 2560 / 3600;
				double centerY = Double.parseDouble(locationsList.get(1)) / 2560 / 3600;
				edt_x.setText(String.valueOf(df.format(centerX)));
				edt_y.setText(String.valueOf(df.format(centerY)));
			}
		}
		if (rbt_l_two.isSelected() == true) {
			try {
				List<String> cityStrings = Controller.getCity();
				if (cityStrings.size() == 0) {
					showNotInChina();
					return;
				}
				cob_pro.setSelectedItem(cityStrings.get(0));
				if (cityStrings.size() > 1) {
					cob_city.setSelectedItem(cityStrings.get(1));
				}
			} catch (SQLException e) {
				showNotInChina();
				Console.getInstance().setTxt("获取当前城市错误：" + e.getStackTrace());
				e.printStackTrace();
			}
		}
	}

	private void showNotInChina() {
		JOptionPane.showMessageDialog(Controller.UI, "检索的结果不在中国范围内", "提示", JOptionPane.ERROR_MESSAGE); 
		
	}

	private void showErrorDialog() {
		JOptionPane.showMessageDialog(Controller.UI, "输入的值不正确，请重新输入", "提示", JOptionPane.ERROR_MESSAGE); 
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			btn_OKActionPerformed();
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
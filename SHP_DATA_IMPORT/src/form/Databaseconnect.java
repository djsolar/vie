/*
 * databaseconnect.java
 *
 * Created on __DATE__, __TIME__
 */

package form;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import formconf.TestJdbcUtil;
import formconf.Pro;


/**
 * 
 * @author __USER__
 */
public class Databaseconnect extends javax.swing.JDialog {
	private String srid;
	/** Creates new form databaseconnect */
	public Databaseconnect(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}
	public Databaseconnect(String srid,java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		this.srid = srid;
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {
		this.setLocation(330, 200);
		buttonGroup1 = new javax.swing.ButtonGroup();
		jScrollPane1 = new javax.swing.JScrollPane();
		jPanel1 = new javax.swing.JPanel();
		jRadioButton3 = new javax.swing.JRadioButton();
		jRadioButton2 = new javax.swing.JRadioButton();
		jRadioButton1 = new javax.swing.JRadioButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jTextField1 = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		jTextField2 = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		jTextField3 = new javax.swing.JTextField();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jTextField5 = new javax.swing.JTextField();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jPasswordField1 = new javax.swing.JPasswordField();
		buttonGroup1.add(jRadioButton3);
		buttonGroup1.add(jRadioButton1);
		buttonGroup1.add(jRadioButton2);
		jTextField5.setEditable(false);
		jTextField1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				jTextField2ActionPerformed(e);
			}

		});
		jTextField1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				jTextField1ActionPerformed(e);
			}

		});
		jTextField2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				jTextField2ActionPerformed(e);
			}

		});
		this.setTitle("数据导入工具");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		jScrollPane1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("数据库连接"));
		jScrollPane1
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane1
				.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		jRadioButton3.setText("POSTGRESQL");
		jRadioButton3.setActionCommand("POSTGRESQL");
		jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton3ActionPerformed(evt);
			}
		});

		jRadioButton2.setText("MYSQL");
		jRadioButton2.setActionCommand("MYSQL");
		jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton2ActionPerformed(evt);
			}
		});

		jRadioButton1.setText("ORACLE");
		jRadioButton1.setActionCommand("ORACLE");
		jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton1ActionPerformed(evt);
			}
		});

		if(srid != null){
			Pro pro = new Pro();
			String valString = pro.getValue(srid);
			String[] arr=valString.split("%");
			if(jRadioButton1.getText().equals(arr[0])){
				jRadioButton1.setSelected(true);
			}
			if(jRadioButton2.getText().equals(arr[0])){
				jRadioButton2.setSelected(true);
			}
			if(jRadioButton3.getText().equals(arr[0])){
				jRadioButton3.setSelected(true);
			}
			jTextField1.setText(arr[2]);
			jTextField2.setText(arr[1]);
			jTextField3.setText(arr[3]);
			jTextField5.setText(srid);
			if(arr.length == 5){
				jPasswordField1.setText(arr[4]);
			}
		}
		jLabel1.setText("选择数据库：");

		jLabel2.setText("数据库IP：");

		jLabel3.setText("数据库库名：");

		jLabel4.setText("用户名：");

		jLabel5.setText("密码：");

		jLabel6.setText("数据库连接名");

		jButton1.setText("测试连接");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jButton2.setText("保存连接");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		jPasswordField1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jPasswordField1ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGap(34, 34, 34)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel6)
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				jTextField5,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				230,
																				Short.MAX_VALUE)
																		.addGap(
																				95,
																				95,
																				95)
																		.addComponent(
																				jButton1)
																		.addGap(
																				208,
																				208,
																				208))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								jPanel1Layout
																										.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.LEADING)
																										.addGroup(
																												jPanel1Layout
																														.createSequentialGroup()
																														.addComponent(
																																jLabel1)
																														.addGap(
																																58,
																																58,
																																58)
																														.addComponent(
																																jRadioButton1)
																														.addPreferredGap(
																																javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																																52,
																																Short.MAX_VALUE)
																														.addComponent(
																																jRadioButton2)
																														.addGap(
																																10,
																																10,
																																10))
																										.addGroup(
																												jPanel1Layout
																														.createSequentialGroup()
																														.addComponent(
																																jLabel2)
																														.addGap(
																																18,
																																18,
																																18)
																														.addGroup(
																																jPanel1Layout
																																		.createParallelGroup(
																																				javax.swing.GroupLayout.Alignment.LEADING)
																																		.addGroup(
																																				jPanel1Layout
																																						.createSequentialGroup()
																																						.addComponent(
																																								jTextField3,
																																								javax.swing.GroupLayout.PREFERRED_SIZE,
																																								183,
																																								javax.swing.GroupLayout.PREFERRED_SIZE)
																																						.addPreferredGap(
																																								javax.swing.LayoutStyle.ComponentPlacement.RELATED))
																																		.addComponent(
																																				jTextField1,
																																				javax.swing.GroupLayout.DEFAULT_SIZE,
																																				230,
																																				Short.MAX_VALUE))))
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel4)
																										.addGap(
																												237,
																												237,
																												237)))
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addGap(
																												35,
																												35,
																												35)
																										.addGroup(
																												jPanel1Layout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addComponent(
																																jRadioButton3)
																														.addGroup(
																																jPanel1Layout
																																		.createSequentialGroup()
																																		.addComponent(
																																				jLabel3)
																																		.addGap(
																																				26,
																																				26,
																																				26)
																																		.addComponent(
																																				jTextField2,
																																				javax.swing.GroupLayout.PREFERRED_SIZE,
																																				161,
																																				javax.swing.GroupLayout.PREFERRED_SIZE))))
																						.addGroup(
																								jPanel1Layout
																										.createSequentialGroup()
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												jLabel5)
																										.addGap(
																												42,
																												42,
																												42)
																										.addComponent(
																												jPasswordField1,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												194,
																												javax.swing.GroupLayout.PREFERRED_SIZE)))
																		.addGap(
																				114,
																				114,
																				114))))
						.addGroup(
								jPanel1Layout.createSequentialGroup().addGap(
										314, 314, 314).addComponent(jButton2)
										.addContainerGap(355, Short.MAX_VALUE)));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGap(29, 29, 29)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jRadioButton3)
																						.addComponent(
																								jRadioButton2))
																		.addGap(
																				23,
																				23,
																				23))
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel1)
																						.addComponent(
																								jRadioButton1))
																		.addGap(
																				25,
																				25,
																				25)))
										.addGap(2, 2, 2)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel2)
														.addComponent(jLabel3)
														.addComponent(
																jTextField1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																jTextField2,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(36, 36, 36)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel4)
														.addComponent(jLabel5)
														.addComponent(
																jTextField3,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																jPasswordField1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(37, 37, 37)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel6)
														.addComponent(
																jTextField5,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jButton1))
										.addGap(32, 32, 32).addComponent(
												jButton2).addGap(40, 40, 40)));

		jScrollPane1.setViewportView(jPanel1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(
						jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE,
						708, Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(
						jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE,
						341, Short.MAX_VALUE).addContainerGap()));
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("/load1/apple.JPG")));
		pack();

	}// </editor-fold>

	private void jTextField2ActionPerformed(FocusEvent e) {
		if (jRadioButton1.isSelected()
				&& (jTextField1.getText() != null && !"".equals(jTextField1
						.getText()))
				&& (jTextField2.getText() != null && !"".equals(jTextField2
						.getText()))) {
			jTextField5.setText(jRadioButton1.getText() + "_"
					+ jTextField1.getText() + "_" + jTextField2.getText());
			// jTextField5.setText("sdfsdfsdsdsdf");
			// repaint();
		}
		if (jRadioButton2.isSelected()
				&& (jTextField1.getText() != null && !"".equals(jTextField1.getText()))
				&& (jTextField2.getText() != null && !"".equals(jTextField2
						.getText()))) {
			jTextField5.setText(jRadioButton2.getText() + "_"
					+ jTextField1.getText() + "_" + jTextField2.getText());
		}
		if (jRadioButton3.isSelected()
				&& (jTextField1.getText() != null && !"".equals(jTextField1.getText()))
				&& (jTextField2.getText() != null && !"".equals(jTextField2
						.getText()))) {
			jTextField5.setText(jRadioButton3.getText() + "_"
					+ jTextField1.getText() + "_" + jTextField2.getText());
		}
	}

	private void jTextField1ActionPerformed(FocusEvent e) {
		if (jRadioButton1.isSelected()
				&& (jTextField1.getText() != null && !"".equals(jTextField1
						.getText()))
				&& (jTextField2.getText() != null && !"".equals(jTextField2
						.getText()))) {
			jTextField5.setText(jRadioButton1.getText() + "_"
					+ jTextField1.getText() + "_" + jTextField2.getText());
		}
		if (jRadioButton2.isSelected()
				&& (jTextField1.getText() != null && !"".equals(jTextField1.getText()))
				&& (jTextField2.getText() != null && !"".equals(jTextField2
						.getText()))) {
			jTextField5.setText(jRadioButton2.getText() + "_"
					+ jTextField1.getText() + "_" + jTextField2.getText());
		}
		if (jRadioButton3.isSelected()
				&& (jTextField1.getText() != null && !"".equals(jTextField1.getText()))
				&& (jTextField2.getText() != null && !"".equals(jTextField2
						.getText()))) {
			jTextField5.setText(jRadioButton3.getText() + "_"
					+ jTextField1.getText() + "_" + jTextField2.getText());
		}
	}

	private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {
	}

	private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {
		if (jTextField1.getText() != null && !"".equals(jTextField1)
				&& jTextField2.getText() != null
				&& !"".equals(jTextField2.getText())) {
			jTextField5.setText(jRadioButton3.getText() + "_"
					+ jTextField1.getText() + "_" + jTextField2.getText());
		}
	}

	private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		if (jTextField1.getText() != null && !"".equals(jTextField1)
				&& jTextField2.getText() != null
				&& !"".equals(jTextField2.getText())) {
			jTextField5.setText(jRadioButton2.getText() + "_"
					+ jTextField1.getText() + "_" + jTextField2.getText());
		}
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		JOptionPane jop = new JOptionPane();
		boolean flag = false;
		if((!jRadioButton1.isSelected())&&(!jRadioButton2.isSelected())&&(!jRadioButton3.isSelected())){
			flag = true;
			jop.showMessageDialog(null, "您没有选择数据库类型");
		}else if(jTextField1.getText() == null || "".equals(jTextField1.getText())){
			flag = true;
			jop.showMessageDialog(null, "您没有填写数据库所在IP");
		}else if(jTextField2.getText() == null || "".equals(jTextField2.getText())){
			flag = true;
			jop.showMessageDialog(null, "您没有填写所要导入的数据库名称");
		}else if(jTextField3.getText() == null || "".equals(jTextField3.getText())){
			flag = true;
			jop.showMessageDialog(null, "您没有填写数据库用户名");
		}
		if(flag){
			return;
		}
		
		String datatype = buttonGroup1.getSelection().getActionCommand();
		try {
			Connection con = TestJdbcUtil.connect(jTextField2.getText(), jTextField1.getText(), datatype, jTextField3.getText(), jPasswordField1.getText());
			if(con == null){
				jop.showMessageDialog(null, "数据库连接失败");
			}
			jop.showMessageDialog(null, "数据库连接成功！！！");
		} catch (Exception e) {
//			e.printStackTrace();
			jop.showMessageDialog(null, "数据库连接失败");
		}
		
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		JOptionPane jop = new JOptionPane();
		boolean flag = false;
		if((!jRadioButton1.isSelected())&&(!jRadioButton2.isSelected())&&(!jRadioButton3.isSelected())){
			flag = true;
			jop.showMessageDialog(null, "您没有选择数据库类型");
		}else if(jTextField1.getText() == null || "".equals(jTextField1.getText())){
			flag = true;
			jop.showMessageDialog(null, "您没有填写数据库所在IP");
		}else if(jTextField2.getText() == null || "".equals(jTextField2.getText())){
			flag = true;
			jop.showMessageDialog(null, "您没有填写所要导入的数据库名称");
		}else if(jTextField3.getText() == null || "".equals(jTextField3.getText())){
			flag = true;
			jop.showMessageDialog(null, "您没有填写数据库用户名");
		}
		if(flag){
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("SRID", jTextField5.getText());
		map.put("datatype", buttonGroup1.getSelection().getActionCommand());
		map.put("dataname", jTextField2.getText());
		map.put("ip", jTextField1.getText());
		map.put("user", jTextField3.getText());
		map.put("pass", jPasswordField1.getText());
		Pro pro = new Pro();
		try {
			if(srid != null){
				pro.remove(srid);
			}
			pro.writeFile(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			jop.showMessageDialog(null, "保存失败");
			e.printStackTrace();
		}
		Selecter.setJComboBox1(jTextField5.getText());
		this.dispose();
		if(srid != null){
			jop.showMessageDialog(null, "修改成功");
		}else {
			jop.showMessageDialog(null, "保存成功");
		}
		
	}

	private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		if (jTextField1.getText() != null && !"".equals(jTextField1)
				&& jTextField2.getText() != null
				&& !"".equals(jTextField2.getText())) {
			jTextField5.setText(jRadioButton1.getText() + "_"
					+ jTextField1.getText() + "_" + jTextField2.getText());
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				Databaseconnect datat = new Databaseconnect(new javax.swing.JFrame(), true);
				datat.setResizable(false);
				datat.setVisible(true);
			}
		});
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPasswordField jPasswordField1;
	private javax.swing.JRadioButton jRadioButton1;
	private javax.swing.JRadioButton jRadioButton2;
	private javax.swing.JRadioButton jRadioButton3;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField2;
	private javax.swing.JTextField jTextField3;
	private javax.swing.JTextField jTextField5;

	// End of variables declaration//GEN-END:variables
	public javax.swing.JPasswordField getJPasswordField1() {
		return jPasswordField1;
	}

	public void setJPasswordField1(javax.swing.JPasswordField passwordField1) {
		jPasswordField1 = passwordField1;
	}

	public javax.swing.JRadioButton getJRadioButton1() {
		return jRadioButton1;
	}

	public void setJRadioButton1(javax.swing.JRadioButton radioButton1) {
		jRadioButton1 = radioButton1;
	}

	public javax.swing.JRadioButton getJRadioButton2() {
		return jRadioButton2;
	}

	public void setJRadioButton2(javax.swing.JRadioButton radioButton2) {
		jRadioButton2 = radioButton2;
	}

	public javax.swing.JRadioButton getJRadioButton3() {
		return jRadioButton3;
	}

	public void setJRadioButton3(javax.swing.JRadioButton radioButton3) {
		jRadioButton3 = radioButton3;
	}

	public javax.swing.JTextField getJTextField1() {
		return jTextField1;
	}

	public void setJTextField1(javax.swing.JTextField textField1) {
		jTextField1 = textField1;
	}

	public javax.swing.JTextField getJTextField2() {
		return jTextField2;
	}

	public void setJTextField2(javax.swing.JTextField textField2) {
		jTextField2 = textField2;
	}

	public javax.swing.JTextField getJTextField3() {
		return jTextField3;
	}

	public void setJTextField3(javax.swing.JTextField textField3) {
		jTextField3 = textField3;
	}

	public javax.swing.JTextField getJTextField5() {
		return jTextField5;
	}

	public void setJTextField5(javax.swing.JTextField textField5) {
		jTextField5 = textField5;
	}

}
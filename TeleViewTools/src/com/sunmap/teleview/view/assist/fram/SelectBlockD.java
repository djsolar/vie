package com.sunmap.teleview.view.assist.fram;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.sunmap.teleview.Controller;

/**
 *
 * @author  lijingru
 */
public class SelectBlockD extends JDialog  implements KeyListener{
	
	private static final long serialVersionUID = 1L;
	private JButton btn_OK;
	private JButton btn_location;
	private JTextField edt_bx;
	private JTextField edt_by;
	private JTextField edt_level;
	private JTextField edt_ux;
	private JTextField edt_uy;
	private JLabel txt_block;
	private JLabel txt_bx;
	private JLabel txt_by;
	private JLabel txt_level;
	private JLabel txt_ux;
	private JLabel txt_uy;
	private JCheckBox cbox_unit;

	/**
	 * 构造方法
	 * @param parent
	 * @param str
	 * @param modal		
	 * @param unitFlag			Unit falg
	 */
	public SelectBlockD(java.awt.Frame parent,String str, boolean modal) {
		super(parent, str,modal);
		initComponents();
		setResizable(false);//禁止改变窗口大小
		this.setLocation(parent.getLocation().x + 300, parent.getLocation().y + 200);
		cbox_unitActionPerformed();
	}

	private void initComponents() {
		txt_level = new JLabel();
		edt_level = new JTextField();
		txt_block = new JLabel();
		txt_bx = new JLabel();
		edt_bx = new JTextField();
		txt_by = new JLabel();
		edt_by = new JTextField();
		txt_ux = new JLabel();
		edt_ux = new JTextField();
		txt_uy = new JLabel();
		edt_uy = new JTextField();
		btn_location = new JButton();
		btn_OK = new JButton();
		cbox_unit = new JCheckBox();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		txt_level.setText("level : ");
		txt_block.setText("BlockID : ");
		txt_bx.setText("xIndex : ");
		txt_by.setText("yIndex : ");
		txt_ux.setText("xIndex : ");
		txt_uy.setText("yIndex : ");
		btn_location.setText("\u5f53\u524d");
		btn_OK.setText("\u786e\u5b9a");
		btn_location.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_locationActionPerformed();
			}
		});
		cbox_unit.setText("UnitID : ");
		cbox_unit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cbox_unitActionPerformed();
			}
		});
		btn_OK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_OKActionPerformed();
			}
		});
		edt_level.addKeyListener(this);
		edt_bx.addKeyListener(this);
		edt_by.addKeyListener(this);
		edt_ux.addKeyListener(this);
		edt_uy.addKeyListener(this);
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(txt_level)
												.addComponent(txt_block)
												.addComponent(txt_bx)
												.addComponent(cbox_unit)
												.addComponent(txt_ux))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(edt_level)
												.addComponent(edt_bx)
												.addComponent(edt_ux)
												.addComponent(btn_location))
								.addGap(10)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(txt_by)
												.addComponent(txt_uy))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(edt_by)
												.addComponent(edt_uy)
												.addComponent(btn_OK))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
								.addGap(10)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(txt_level)
												.addComponent(edt_level))
								.addGap(30)
								.addComponent(txt_block)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(txt_bx)
												.addComponent(txt_by)
												.addComponent(edt_bx)
												.addComponent(edt_by))
								.addGap(30)
								.addComponent(cbox_unit)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(txt_ux)
												.addComponent(edt_ux)
												.addComponent(txt_uy)
												.addComponent(edt_uy))
								.addGap(30)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(btn_OK)
												.addComponent(btn_location))
								.addContainerGap()));

		pack();
	}

	private void cbox_unitActionPerformed() {
		boolean flag =  cbox_unit.isSelected();
		txt_ux.setEnabled(flag);
		txt_uy.setEnabled(flag);
		edt_ux.setEnabled(flag);
		edt_uy.setEnabled(flag);
	}

	private void btn_OKActionPerformed() {
		try {
			byte blockLevel = Byte.parseByte(edt_level.getText().trim());
			if (cbox_unit.isSelected() == true && edt_bx.getText().trim().equals("") //使用unit检索
					&& edt_by.getText().trim().equals("")) {
				int unitX = Integer.parseInt(edt_ux.getText().trim());
				int unitY = Integer.parseInt(edt_uy.getText().trim());
				if (blockLevel < 0 || unitX < 0 || unitY < 0) {
					showErrorDialog();
				}else {
					boolean resultFlag = Controller.viewBlockSearch(blockLevel, unitX, unitY, "unit");
					if (resultFlag == false) {
						showNotInChina();
					}
					this.dispose();
				}
			}else {//使用block检索
				int blockX = Integer.parseInt(edt_bx.getText().trim());
				int blockY = Integer.parseInt(edt_by.getText().trim());
				if (blockLevel < 0 || blockX < 0 || blockY < 0) {
					showErrorDialog();
				}else {
					boolean resultFlag = Controller.viewBlockSearch(blockLevel, blockX, blockY, "block");
					if (resultFlag == false) {
						showNotInChina();
					}else {
						this.dispose();
					}
				}
			}
		} catch (Exception e) {
			showErrorDialog();
			e.printStackTrace();
		}
		
		

	}

	private void showNotInChina() {
		JOptionPane.showMessageDialog(Controller.UI, "检索的结果不在中国范围内", "提示", JOptionPane.ERROR_MESSAGE); 
		
	}

	private void showErrorDialog() {
		JOptionPane.showMessageDialog(Controller.UI, "输入的值不正确，请重新输入", "提示", JOptionPane.ERROR_MESSAGE); 
	}

	private void btn_locationActionPerformed() {
		List<String> viewBlockStrings = Controller.getViewBlock();
		edt_level.setText(viewBlockStrings.get(0));
		edt_bx.setText(viewBlockStrings.get(1));
		edt_by.setText(viewBlockStrings.get(2));
		if (cbox_unit.isSelected() == true) {
			edt_ux.setText(viewBlockStrings.get(3));
			edt_uy.setText(viewBlockStrings.get(4));
		}
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
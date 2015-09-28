package com.sunmap.teleview.view.assist.fram;
/**
 * 
 */


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.sunmap.teleview.Controller;

/**
 * @author lijingru
 *
 */
public class SelectMMBlockD extends JDialog implements KeyListener{

	private static final long serialVersionUID = 1L;
	private JButton btn_OK;
	private JButton btn_location;
	private JTextField edt_bx;
	private JTextField edt_by;
	private JLabel txt_levelNumber;
	private JLabel txt_block;
	private JLabel txt_bx;
	private JLabel txt_by;
	private JLabel txt_level;

	/**
	 * 构造方法
	 * @param parent
	 * @param str
	 * @param modal		
	 * @param unitFlag			Unit falg
	 */
	public SelectMMBlockD(java.awt.Frame parent,String str, boolean modal) {
		super(parent, str,modal);
		initComponents();
		setResizable(false);//禁止改变窗口大小
		this.setLocation(parent.getLocation().x + 300, parent.getLocation().y + 200);
	}

	private void initComponents() {

		txt_level = new JLabel();
		txt_levelNumber = new JLabel();
		txt_block = new JLabel();
		txt_bx = new JLabel();
		edt_bx = new JTextField();
		txt_by = new JLabel();
		edt_by = new JTextField();
		btn_location = new JButton();
		btn_OK = new JButton();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		txt_level.setText("level : ");
		txt_levelNumber.setText("0");
		txt_block.setText("BlockID : ");
		txt_bx.setText("xIndex : ");
		txt_by.setText("yIndex : ");
		btn_location.setText("\u5f53\u524d");
		btn_OK.setText("\u786e\u5b9a");
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
		edt_bx.addKeyListener(this);
		edt_by.addKeyListener(this);
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(txt_level)
												.addComponent(txt_block)
												.addComponent(txt_bx))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(txt_levelNumber)
												.addComponent(edt_bx)
												.addComponent(btn_location))
								.addGap(10)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(txt_by))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(edt_by)
												.addComponent(btn_OK))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
								.addGap(10)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(txt_level)
												.addComponent(txt_levelNumber))
								.addGap(30)
								.addComponent(txt_block)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(txt_bx)
												.addComponent(txt_by)
												.addComponent(edt_bx)
												.addComponent(edt_by))
								.addGap(30)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(btn_OK)
												.addComponent(btn_location))
								.addContainerGap()));

		pack();
	}

	private void btn_OKActionPerformed() {
		try {
			int mmBlockX = Integer.parseInt(edt_bx.getText().trim());
			int mmBlockY = Integer.parseInt(edt_by.getText().trim());
			if (mmBlockX < 0 || mmBlockY < 0) {
				showErrorDialog();
			}else {
				boolean resultFlag = Controller.MMBlockSearch(mmBlockX, mmBlockY);
				if (resultFlag == false) {
					showNotInChina();
				}else {
					this.dispose();
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
		List<String> MMBlockStrings = Controller.getMMBlock();
		edt_bx.setText(MMBlockStrings.get(0));
		edt_by.setText(MMBlockStrings.get(1));
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

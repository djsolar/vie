package form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.sunmap.shpdata.tools.util.Util;

public class ForShpDes extends javax.swing.JDialog{
	private java.awt.Frame parent;
	private boolean modal;
	private JCheckBox jCheckBox;
	private JRadioButton jRadioButton1;
	private JRadioButton jRadioButton2;
	public ForShpDes(java.awt.Frame parent, boolean modal, JCheckBox jCheckBox) {
		// TODO Auto-generated constructor stub
		super(parent,modal);
		this.parent = parent;
		this.modal = modal;
		this.jCheckBox = jCheckBox;
		this.initComponents();
	}
	
	private void initComponents(){
//		this.setLocation(440, 300);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		int with = 250;
		int high = 150;
		this.setBounds(600, 330, with, high);
		getContentPane().setLayout(null);
//		this.setSize(300, 200);
		this.setTitle("�����shp����");
		JPanel jPanel = new JPanel();
		jPanel.setLayout(null);
		jPanel.setBounds(0,0,with,high);
		jPanel.setBackground(new java.awt.Color(244, 241, 229));
		JButton jButton_a = new JButton("ȷ��");
		jButton_a.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(jRadioButton1.isSelected()){
					Util.parentFiledirFlag = Util.PARENTDIR;
					jCheckBox.setSelected(true);
					dispose();
				}else if (jRadioButton2.isSelected()) {
					Util.parentFiledirFlag = Util.PARENTDIRADDMAPID;
					jCheckBox.setSelected(true);
					dispose();
				}else {
					JOptionPane jOptionPane = new JOptionPane();
					jOptionPane.showMessageDialog(null, "��ѡ������Ҫ����ķ�ʽ");
				}
			}
			
		});
		JButton jButton_b = new JButton("ȡ��");
		jButton_b.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				jCheckBox.setSelected(false);
				dispose();
			}
			
		});
		jRadioButton1 = new JRadioButton();
		jRadioButton1.setText("�ļ�����");
		jRadioButton1.setBounds(10, 30, 100, 20);
		jRadioButton2 = new JRadioButton();
		jRadioButton2.setText("�ļ�����+Mapid");
		jRadioButton2.setBounds(120, 30, 120, 20);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(jRadioButton1);
		buttonGroup.add(jRadioButton2);
		jPanel.add(jRadioButton1);
		jPanel.add(jRadioButton2);
		jButton_a.setBounds(45, 70, 60, 25);
		jButton_b.setBounds(145, 70, 60, 25);
		jPanel.add(jButton_a,null);
		jPanel.add(jButton_b,null);
		this.getContentPane().add(jPanel,null);
		
//		pack();
	}
	
	public static void main(String[] args) {
		ForShpDes forShpDes = new ForShpDes(new JFrame(), true, null);
		forShpDes.setVisible(true);
	}
}

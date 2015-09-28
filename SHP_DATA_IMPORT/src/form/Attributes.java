/*
 * Attributes.java
 *
 * Created on __DATE__, __TIME__
 */

package form;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author  __USER__
 */
public class Attributes extends javax.swing.JDialog {

	private String tablename = "";
	private String[] attr;
	private List<String> seleList;
	private List<JCheckBox> jClist = new ArrayList<JCheckBox>();  
	private JCheckBox jCheckBox;
	private Map<String, List<String>> createtableMap;
	/** Creates new form Attributes */
	public Attributes(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}
	public Attributes(java.awt.Frame parent, boolean modal,String tabString, String[] attr,List<String> seleList,JCheckBox jCheckBox, Map<String, List<String>> createtableMap) {
		super(parent, modal);
		this.tablename = tabString;
		this.attr = attr;
		this.jCheckBox = jCheckBox;
		this.seleList = seleList;
		this.createtableMap = createtableMap;
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {
		
		//注释
		this.setBounds(340, 200, 500, 400);
		this.setBackground(new java.awt.Color(245, 243, 236));
		this.setTitle("数据导入工具");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		//注释
		JPanel jPanel = new JPanel();
		jPanel.setLayout(null);
		jPanel.setBounds(0,0,500,400);
		jPanel.setBackground(new java.awt.Color(244, 241, 229));
		//注释
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setBounds(10, 10, 350, 300);
		jScrollPane.setBackground(new java.awt.Color(244, 241, 229));
		jScrollPane.setBorder(BorderFactory.createTitledBorder("选择要素" + tablename + "导入的属性"));
		
		
		JPanel jPanel2 = new JPanel();
		jPanel2.setBackground(new java.awt.Color(244, 241, 229));
		GridLayout gridLayout = new GridLayout(attr.length/3 + 1,3);
		jPanel2.setLayout(gridLayout);
		
		
		for (String string : attr) {
			JCheckBox jCheckBox = new JCheckBox(string);
			jCheckBox.setBackground(new java.awt.Color(244, 241, 229));
			jPanel2.add(jCheckBox);
			jClist.add(jCheckBox);
		}
		
		jScrollPane.setViewportView(jPanel2);
		jScrollPane
		.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jPanel.add(jScrollPane);
		
		JButton jButton1 = new JButton();
		jButton1.setText("全选");
		jButton1.setBounds(400, 30, 60, 30);
		jButton1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (jClist.size() != 0) {
					for (JCheckBox jCheckBox : jClist) {
							jCheckBox.setSelected(true);
					}
				}
			}
			
		});
		JButton jButton3 = new JButton();
		jButton3.setText("反选");
		jButton3.setBounds(400, 80, 60, 30);
		jButton3.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (jClist.size() != 0) {
					for (JCheckBox jCheckBox : jClist) {
						if(jCheckBox.isSelected() == false){
							jCheckBox.setSelected(true);
						}else{
							jCheckBox.setSelected(false);
						}
					}
				}
			}
			
		});
		JButton jButton2 = new JButton();
		jButton2.setText("确定");
		jButton2.setBounds(400, 300, 60, 30);
		jButton2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				for (JCheckBox jCheckBox : jClist) {
					if(jCheckBox.isSelected()){
						seleList.add(jCheckBox.getActionCommand());
					}
				}
				if(seleList.size() == 0){
					jCheckBox.setSelected(false);
				}else {
					if(seleList.contains("geom2") && !seleList.contains("geom1")){
						seleList.add("geom");
					}
					jCheckBox.setSelected(true);
					seleList.add("gid");
					createtableMap.put(tablename, seleList);
				}
				dispose();
			}
			
		});
		jPanel.add(jButton1,null);
		jPanel.add(jButton2,null);
		jPanel.add(jButton3,null);
		this.getContentPane().add(jPanel,null);
		this.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				e.getWindow().toFront();
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("/load1/apple.JPG")));
//		pack();
//		this.repaint();
	}// </editor-fold>
	//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
//				new Attributes().setVisible(true);
			}
		});
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	// End of variables declaration//GEN-END:variables

}
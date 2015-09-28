package com.sunmap.teleview.view.mainfram;
/**
 * 
 */


import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author lijingru
 *
 */
public class Console extends JPanel {

	private JTextArea textArea;
	private String str;
	private static Console uniqueInstance = null;	
	private static final long serialVersionUID = 1L;
	public static Console getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Console();
		}
		return uniqueInstance;
	}

	private Console(){
		//初始化
		init();
		setVisible(true);// 显示
	}

	private void init() {
		textArea = new JTextArea();
		add(textArea);
	}
	
	
	public void setTxt(String str) {
		clear();
		this.str+= str;
		textArea.setText(this.str);
	}
	
	private void clear() {
		if (textArea.getLineCount()>50) {
			this.str = "";
		}
	}
}

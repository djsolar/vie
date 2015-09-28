/**
 * 
 */
package com.sunmap.teleview.view.assist.fram;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * @author lijingru
 * 
 */
public class HelpDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final String ID = "1.0_T";
	JLabel jText = new JLabel();
	JButton jButtonOK = new JButton("OK");

	public HelpDialog(java.awt.Frame parent, boolean modal) {
		super(parent, "关于工具", modal);
		// 布局
		dialogLayout();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// 点击取消button触发
		if (e.getSource() == jButtonOK) {
			// 关闭窗体，释放资源
			dispose();
		}
	}
	
	private void dialogLayout() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int farmwidth = screenSize.width * 1 / 4;
		int farmHeight = screenSize.height * 1 / 4;
		// 窗口大小
		setSize(farmwidth, farmHeight);
		// 设置窗口位置（左上角）
		setLocation((screenSize.width - farmwidth) / 2, (screenSize.height - farmHeight) / 2);
		// 显示
		// 布局
		setLayout(null);
		add(jText);
		jText.setBounds(12, 12, (int) (farmwidth - 40), (int) (farmHeight ));
		jText.setVerticalAlignment(JLabel.TOP);
		add(jButtonOK);
		jButtonOK.setBounds(farmwidth / 2 - 45, (int) (farmHeight * 0.65), 100, 25);
		jText.setText("<html>版本：   " + ID + " <br> <br>  " + ID + "功能说明：" + "" +
				"<br>本版本为TeleViewTools工具1.0T版本"+ 
				"<br>包含了Tele组截止到1月15日提出的需求." + 
				"<br>有其他需求请邮件联系。" + 
				"<br>" + 
				"<br>"+ 
				"<br>" + 
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>" +
				"<br>"+ 
				"<br>" +
				"<br>          沈阳美行科技LBS-RG</html>");
		jButtonOK.addActionListener(this);

	}

}

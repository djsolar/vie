package form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class JAttributelistener implements ActionListener {
	private String name;
	private boolean flag;
	private JCheckBox jCheckBox;
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
	public JAttributelistener() {
		// TODO Auto-generated constructor stub
	}
	public JAttributelistener(String name,JCheckBox jCheckBox) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.jCheckBox = jCheckBox;
	}
	

}

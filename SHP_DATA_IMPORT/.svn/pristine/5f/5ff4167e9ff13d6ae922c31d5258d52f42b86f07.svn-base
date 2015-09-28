package form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;

import com.sunmap.shpdata.tools.util.Util;

public class Jcheckerlistener implements ActionListener {
	private String name;
	private boolean flag;
	private JCheckBox jCheckBox;
	private Map<String, List<String>> createtableMap;

	public Jcheckerlistener(String name, JCheckBox jCheckBox, Map<String, List<String>> createtableMap) {
		this.name = name;
		this.jCheckBox = jCheckBox;
		this.createtableMap = createtableMap;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		List<String> seleList = new ArrayList<String>();
		if (jCheckBox.isSelected()) {
			jCheckBox.setSelected(false);
			String nameString = jCheckBox.getActionCommand();
			String arrStrings = Util.allelementatt.get(nameString);
			String[] attr = arrStrings.split(",");
			Attributes as = new Attributes(new javax.swing.JFrame(), true,name, attr, seleList,jCheckBox,createtableMap);
			as.setResizable(false);
			as.setVisible(true);
		}else {
			createtableMap.remove(jCheckBox.getActionCommand());
		}
	}

}

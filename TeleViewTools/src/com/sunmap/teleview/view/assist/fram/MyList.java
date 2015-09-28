package com.sunmap.teleview.view.assist.fram;

import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author lijingru
 * 
 */
public class MyList extends JList implements ListSelectionListener{
	java.util.List<IListVale> ilistVales = new ArrayList<MyList.IListVale>();
	private static final long serialVersionUID = 1L;
	// 列表当前显示索引
	public MyList() {
		addListSelectionListener(this);
	}
	
	@Override
	public void setModel(ListModel model) {
		super.setModel(model);
	}
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			getSelectedIndex();
			for (int i = 0; i < ilistVales.size(); i++) {
				ilistVales.get(i).valueChanged(getSelectedIndex());
				ilistVales.get(i).moveChanged(getSelectedIndex());
				ilistVales.get(i).drawChanged(getSelectedIndex());
			}

		}
	}
	
	public interface IListVale{
		public void valueChanged(int index);

		public void moveChanged(int index);
		
		public void drawChanged(int index);
	}
	public void reqIListvales(IListVale iListVale){
		ilistVales.add(iListVale);
	}
}

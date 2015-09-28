/**
 * 
 */
package com.sunmap.teleview.view.assist.fram;


import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



/**
 * @author lijingru
 *
 */
public class MyTable extends JTable implements ListSelectionListener {

	ITableListVale tableListVale = null;
	private static final long serialVersionUID = 1L;

	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);	
		if (e.getValueIsAdjusting() == false) {
			int index = ((DefaultListSelectionModel)e.getSource()).getAnchorSelectionIndex();
			if (index != -1) {
				tableListVale.valueTableChanged(index);
			}

		}
	}	
	public interface ITableListVale{
		public void valueTableChanged(int index);
	}
	public void reqIListvales(ITableListVale iListVale){
		tableListVale = iListVale;
	}
}

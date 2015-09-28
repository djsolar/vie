package com.sunmap.teleview.view.mainfram;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.GroupLayout;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.util.ToolsUnit;
import com.sunmap.teleview.view.mainfram.TabbedPane.TabbedMode;


/**主窗口
 * 
 * @author lijingru
 */
public class TeleToolsFrame extends javax.swing.JFrame{
	
	private static final long serialVersionUID = 1L;
	public ViewPanel viewPanel;
	public ToolsBarBase toolsBarBase;
	public StateBar stateBar;
	public TabbedPane tabbedPane;
	public MenuBar menuBar;
	public DrawEleType drawEleType = new DrawEleType();
	
	public TeleToolsFrame() {
		super("TeleView");
		initComponents();
	}
	
	public String getCurUIStatus(){
		StringBuffer UIStatus = new StringBuffer();
		UIStatus.append("com.sunmap.teleview.tools.toolsbar").append("=").append(toolsBarBase.isVisible()).append(",");
		UIStatus.append("com.sunmap.teleview.tools.startbar").append("=").append(stateBar.isVisible()).append(",");
		boolean infoFlag = false;
		boolean consoleFlag = false;
		for (int i = 0; i < tabbedPane.getComponents().length; i++) {
			if (tabbedPane.getComponents()[i].getClass() .getName() .contains("Information")) {
				infoFlag = true;
			}
			if (tabbedPane.getComponents()[i].getClass() .getName() .contains("Console")) {
				consoleFlag = true;
			}
		}
		UIStatus.append("com.sunmap.teleview.tools.info").append("=").append(infoFlag).append(",");
		UIStatus.append("com.sunmap.teleview.tools.console").append("=").append(consoleFlag);
		UIStatus.append("|");
		UIStatus.append(drawEleType.getDrawEleType());
		return UIStatus.toString();
	}
	
	public void setCurUIStatus(String string){
		String[] UIStatus = string.split("\\|");
		String[] uIStrings = UIStatus[0].split(",");
		for (int i = 0; i < uIStrings.length; i++) {
			String[] statusStrings = uIStrings[i].split("=");
			if (statusStrings[0].equals("com.sunmap.teleview.tools.toolsbar")) {
				menuBar.cmit_toolShow.setSelected(Boolean.parseBoolean(statusStrings[1]));
				toolsBarBase.setVisible(Boolean.parseBoolean(statusStrings[1]));
			}
			if (statusStrings[0].equals("com.sunmap.teleview.tools.startbar")) {
				menuBar.cmit_stateShow.setSelected(Boolean.parseBoolean(statusStrings[1]));
				stateBar.setVisible(Boolean.parseBoolean(statusStrings[1]));
			}
			if (statusStrings[0].equals("com.sunmap.teleview.tools.info")) {
				menuBar.cmit_infoShow.setSelected(Boolean.parseBoolean(statusStrings[1]));
				toolsBarBase.tn_info.setSelected(Boolean.parseBoolean(statusStrings[1]));
				setTabbedInfoVisible(Boolean.parseBoolean(statusStrings[1]));
			}
			if (statusStrings[0].equals("com.sunmap.teleview.tools.console")) {
				menuBar.cmit_consoleShow.setSelected(Boolean.parseBoolean(statusStrings[1]));
				toolsBarBase.tn_console.setSelected(Boolean.parseBoolean(statusStrings[1]));
				setTabbedConsoleVisible(Boolean.parseBoolean(statusStrings[1]));
			}
		}
		drawEleType.setDrawEleType(UIStatus[1]);
	}
	
	private void setTabbedConsoleVisible(boolean parseBoolean) {
		boolean consoleFlag = false;
		for (int i = 0; i < tabbedPane.getComponents().length; i++) {
			if (tabbedPane.getComponents()[i].getClass() .getName() .contains("Console")) {
				consoleFlag = true;
			}
		}
		if (consoleFlag != parseBoolean) {
			tabbedPane.setTabbedVisible(parseBoolean, TabbedMode.Console);
		}
	}

	private void setTabbedInfoVisible(boolean parseBoolean) {
		boolean infoFlag = false;
		for (int i = 0; i < tabbedPane.getComponents().length; i++) {
			if (tabbedPane.getComponents()[i].getClass() .getName() .contains("Information")) {
				infoFlag = true;
			}
		}
		if (infoFlag != parseBoolean) {
			tabbedPane.setTabbedVisible(parseBoolean, TabbedMode.Info);
		}
	}


	/**
	 * 设置比例尺
	 * @param str
	 */
	public void setScale(int str) {
		viewPanel.setScale(ToolsUnit.FormatDist(str));
		int i ;
		if (tabbedPane.isVisible() == false) {
			i = 0;
		}else {
			 i = 200;
		}
		viewPanel.setScaleHight(i);
	}
	
	private void initComponents() {
		// 成员初始化
		init();
		// 关闭时释放内存
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		// 设置窗口大小
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int farmwidth = screenSize.width* 6 / 7;
		int farmHeight = screenSize.height * 4 / 5;
		// 显示大小
		setSize(farmwidth, farmHeight);
		// 位置
		setLocation((screenSize.width - farmwidth) / 2, (screenSize.height - farmHeight) / 3);
		// 设置布局
		setWindowLayout(farmwidth);
		setVisible(true);
		//设置菜单栏
		menuBar = new MenuBar();
		menuBar.initComponents();
		setJMenuBar(menuBar);
	}

	//成员初始化
	private void init() {
		toolsBarBase = new ToolsBarBase();
		viewPanel = new ViewPanel();
		stateBar = new StateBar();
		tabbedPane = new TabbedPane();
		toolsBarBase.initComponents();
		viewPanel.initComponents();
		stateBar.initComponents();
		tabbedPane.initComponents();
		setScale(Controller.drawParams.currMapScale);
	}

	//窗口布局
	private void setWindowLayout(int farmwidth) {
		GroupLayout layoutView = new GroupLayout(viewPanel);
		viewPanel.setLayout(layoutView);
		layoutView.setHorizontalGroup(layoutView.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(tabbedPane,  farmwidth,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layoutView.setVerticalGroup(layoutView.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(GroupLayout.Alignment.LEADING,
								layoutView.createSequentialGroup()
										.addContainerGap((int) (farmwidth*0.38), Short.MAX_VALUE)
										.addComponent(tabbedPane,GroupLayout.PREFERRED_SIZE,200,
												GroupLayout.PREFERRED_SIZE)));
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING)
				.addComponent(toolsBarBase)
				.addComponent(viewPanel,farmwidth,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
		.addComponent(stateBar, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
										.addComponent(toolsBarBase,
												GroupLayout.PREFERRED_SIZE,25,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(viewPanel,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
										.addComponent(stateBar)
								));
		pack();
	}
	
}
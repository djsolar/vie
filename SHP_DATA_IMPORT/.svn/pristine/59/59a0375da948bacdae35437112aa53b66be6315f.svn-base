package form;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class Treet extends JDialog {
	public Treet() {
		// TODO Auto-generated constructor stub
	}
	public Treet(java.awt.Frame parent, boolean modal) {
		super(parent,modal);
		defaultMutableTreeNode = new DefaultMutableTreeNode("div", true);
		this.setTitle("配置解析");
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		jTree = new JTree(defaultMutableTreeNode);
		popup = new JPopupMenu();
		popup1 = new JPopupMenu();
		popup2 = new JPopupMenu();
		popup3 = new JPopupMenu();
		jButton = new JButton();
		jButton.setText("确定");
		jButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				okAction(e);
			}
			
		});
		treeModel = (DefaultTreeModel) jTree.getModel();
		JMenuItem cutItem = new JMenuItem("添加一个新的level ");
		JMenuItem cMenuItem2 = new JMenuItem("清除所有的level");
		cMenuItem2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DefaultMutableTreeNode rooTreeNode = (DefaultMutableTreeNode) treeModel
						.getRoot();
				rooTreeNode.removeAllChildren();
				treeModel.reload();
			}

		});
		cutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Level level = new Level(new javax.swing.JFrame(), true);
				level.setLocation(300, 200);
				level.setVisible(true);
				// DefaultMutableTreeNode parenTreeNode = null;
				// DefaultMutableTreeNode newTreeNode = new
				// DefaultMutableTreeNode("level");
				// newTreeNode.setAllowsChildren(true);
				// TreePath pareneTreePath = jTree.getSelectionPath();
				// parenTreeNode =
				// (DefaultMutableTreeNode)(pareneTreePath.getLastPathComponent());
				// treeModel.insertNodeInto(newTreeNode, parenTreeNode,
				// parenTreeNode.getChildCount());
				// jTree.scrollPathToVisible(new TreePath(newTreeNode.getPath()
				// ));
			}
		});
		popup.add(cutItem);
		popup.add(cMenuItem2);
		JMenuItem cutItem1 = new JMenuItem("删除所选中的level ");
		cutItem1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				TreePath path = jTree.getSelectionPath();
				if (path != null) {
					DefaultMutableTreeNode selecTreeNode = (DefaultMutableTreeNode) path
							.getLastPathComponent();
					TreeNode parent = (TreeNode) selecTreeNode.getParent();
					if (parent != null) {
						treeModel.removeNodeFromParent(selecTreeNode);
					}
				}
			}
		});
		popup1.add(cutItem1);
		JMenuItem cutItem2 = new JMenuItem("修改所选中的leveldiv_name的值 ");
		cutItem2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				LevelName levelName = new LevelName(new JFrame(), true,
						(DefaultMutableTreeNode) jTree.getSelectionPath()
								.getLastPathComponent());
				levelName.setLocation(300, 200);
				levelName.setVisible(true);
			}

		});
		popup2.add(cutItem2);
		JMenuItem cutItem3 = new JMenuItem("修改所选中的passname_parentdiv的值 ");
		cutItem3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				PassName passName = new PassName(new JFrame(), true,
						(DefaultMutableTreeNode) jTree.getSelectionPath()
								.getLastPathComponent());
				passName.setLocation(300, 200);
				passName.setVisible(true);
			}

		});
		popup3.add(cutItem3);

		// popup.add(new JMenuItem("删除level "));
		// popup.add(new JMenuItem("Paste "));
		jTree.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					if (flag) {
						popup.show(jTree, e.getX(), e.getY());
						flag = false;
					} else if (flag1) {
						popup1.show(jTree, e.getX(), e.getY());
						flag1 = false;
					} else if (flag2) {
						popup2.show(jTree, e.getX(), e.getY());
						flag2 = false;
					} else if (flag3) {
						popup3.show(jTree, e.getX(), e.getY());
						flag3 = false;
					}
				}
			}

			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					TreePath path = jTree
							.getPathForLocation(e.getX(), e.getY());
					if (path != null) {
						String nodeString = ((DefaultMutableTreeNode) (path
								.getLastPathComponent())).getUserObject()
								.toString();
						System.out.println(nodeString);
						if ("div".equals(nodeString)) {
							flag = true;
						} else if ("level".equals(nodeString)) {
							flag1 = true;
						} else if ("leveldiv_name"
								.equals(((DefaultMutableTreeNode) (path
										.getParentPath().getLastPathComponent()))
										.getUserObject().toString())) {
							flag2 = true;
						} else if ("passname_parentdiv"
								.equals(((DefaultMutableTreeNode) (path
										.getParentPath().getLastPathComponent()))
										.getUserObject().toString())) {
							flag3 = true;
						}
						jTree.setSelectionPath(path);
					} else {
						flag = false;
						flag1 = false;
						flag2 = false;
						flag3 = false;
					}
				}
			}
		});
		this.setBounds(400, 200, 200, 100);
		this.getContentPane().add(new JScrollPane(jTree), BorderLayout.CENTER);
		this.getContentPane().add(jButton, BorderLayout.SOUTH);
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("/load1/apple.JPG")));
		pack();
	}

	private void okAction(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
		File file = new File(this.getClass().getClassLoader()
				.getResource("div.xml").toURI());
		if(file.exists()){
			file.delete();
		}
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n");
			if(((DefaultMutableTreeNode)treeModel.getRoot()).isLeaf()){
				fileWriter.write("<div>\n\r</div>");
			}else {
				this.SaveToFile((DefaultMutableTreeNode)treeModel.getRoot(), fileWriter);
			}
			fileWriter.close();
			dispose();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	public static void setJTreeNode(
			DefaultMutableTreeNode defaultMutableTreeNode) {
		DefaultMutableTreeNode parenTreeNode = null;
		defaultMutableTreeNode.setAllowsChildren(true);
		TreePath pareneTreePath = jTree.getSelectionPath();
		parenTreeNode = (DefaultMutableTreeNode) (pareneTreePath
				.getLastPathComponent());
		treeModel.insertNodeInto(defaultMutableTreeNode, parenTreeNode,
				parenTreeNode.getChildCount());
		jTree
				.scrollPathToVisible(new TreePath(defaultMutableTreeNode
						.getPath()));
	}

	public void SaveToFile(DefaultMutableTreeNode root, FileWriter fw) {
		try {
			if (root.isLeaf())
				fw.write(root.toString() + "\r\n");
			// 如果是叶子节点则直接将该节点输出到文件中
			else { // 不是叶子节点的话递归输出该节点 
				fw.write("<"+root.toString()+">\r\n");
				for (int i = 0; i < root.getChildCount(); i++) {
					DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) root
							.getChildAt(i);
					SaveToFile(childNode, fw); // 递归输出该节点的所有子节点
				}
				fw.write("</" + root.toString() + ">\r\n");
			}
			fw.flush();
//			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Treet().setVisible(true);
	}

	private JPopupMenu popup;
	private JPopupMenu popup1;
	private JPopupMenu popup2;
	private JPopupMenu popup3;
	private static JTree jTree;
	private static DefaultTreeModel treeModel;
	private DefaultMutableTreeNode defaultMutableTreeNode;
	private JButton jButton;
	private boolean flag;
	private boolean flag1;
	private boolean flag2;
	private boolean flag3;
}

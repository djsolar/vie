package draw;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.util.List;

import split.Point;
import split.PolygonHandle;

public abstract class PointIOApplet extends Applet {

	Color[] color = { Color.green, Color.black, Color.orange, Color.blue,
			Color.pink };
	static Draw draw = new Draw(4000);
	static Anim2D result = null;
	static boolean detail = false;
	static boolean animating = false;

	private Button clear_button = new Button("清空");
	private Button delete_button = new Button("后退");
	private Button detail_button = new Button("序号");
	private Button ccw = new Button("逆时针");
	private Button update_button = new Button("分割");

	public void init() {
		Panel p = new Panel();
		setLayout(new BorderLayout());
		setBackground(Color.white);
		setForeground(Color.black);
		addbuttons(p);
		add("South", p);
	}

	void addbuttons(Panel p) {
		p.add(clear_button);
		p.add(delete_button);
		p.add(detail_button);
		p.add(update_button);
		p.add(ccw);
	}

	boolean handlebuttons(Object target) {
		if (target == clear_button) {
			draw.removeAll();
			result = null;
			update(getGraphics());
		} else if (target == delete_button) {
			draw.deleteLast();
			result = null;
			update(getGraphics());
		}else if (target == ccw) {
			if (!draw.isEmpty()) {
				if (!draw.ccw()) {//逆时针方向调整
					draw.reverse();
				}
			}
			result = null;
			update(getGraphics());
		} else if (target == detail_button) {
			detail = !detail;
			update(getGraphics());
		} else if (target == update_button) {
			
			
			List<Point> pointList =draw.getList();
//			List<List<Point>> listPointList = MCD.compute(list);
			PolygonHandle polygon = new PolygonHandle();
			Graphics gs = getGraphics();
			Graphics g = getGraphics();
			int colorNo =0;		
			List<List<Integer>> result = polygon.compute(pointList,g);
			for (List<Integer> list : result){
			g.setColor(color[colorNo%5]);	
			draw.pointToDraw(list,pointList,g);
			colorNo++;
			}
			
			//分割结果描画
			
//			int a = 0;
//			for (List<Point> pointList : listPointList) {
//				g.setColor(color[a]);
//				pts.listToDraw(g, pointList);
//				if (a == color.length) {
//					a = 0;
//				} else{
//					a++;
//				}
//			}
//			int a = 0;
//			for (List<Point> pointList : listPointList) {
//				g.setColor(color[a]);
//				pts.listToDraw(gs,g, pointList);
//				if (a == 4) {
//					a = 0;
//				} else
//					a++;
//			}
		} else
			return false;
		return true;
	}
	
	public boolean action(Event e, Object obj) {
		if (e.target instanceof Button)
			return handlebuttons(e.target);
		return true;
	}

	public boolean mouseDown(Event e, int x, int y) {
		Graphics g = getGraphics();
		g.setColor(Color.black);
		g.fillOval(x - 3, y - 3, 6, 6);
//		System.out.println(x + ":" + y);
		draw.add(x, y);
		result = null;
		repaint();
		return true;
	}

	static public void makeStandalone(String title, PointIOApplet ap,
			int width, int height) {
		Frame f = new Frame(title);
		ap.init();
		ap.start();
		f.add("Center", ap);
		f.resize(width, height);
		f.show();
	}

	public void paint(Graphics g) {
		draw.draw(g, detail);
		draw.drawPolyline(g, draw.number());
		g.setColor(Color.blue);
		if (result != null)
			result.draw(g, detail);
	}

}

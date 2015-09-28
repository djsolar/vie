package draw;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import split.Point;
import java.util.Random;
import java.awt.BasicStroke;
import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.Stroke;

public class Draw extends Anim2D {
	public Draw(int n) {
		counter = 0;
		p = new Point[n];
	}

	private Point[] p;
	private int counter;

	final public Point p(int i) {
		return p[i];
	}

	final public int number() {
		return counter;
	}

	final public boolean isEmpty() {
		return counter <= 0;
	};

	protected void dr() {
		if (animating())
			draw(animGraphics, true);
	}

	public void add(int x, int y) {
		//check collineation with two point before
		if(counter>=2 && ((p(counter-1).x == x && p(counter-1).y == -y)||(p(counter-1).x == x && x ==  p(counter-2).x)||(p(counter-1).y == -y && -y ==  p(counter-2).y))){
			p[counter-1] = new Point(x, y);
		}else{
			if(counter >= p.length){
				p = grow(p, 2);
			}
			p[counter++] = new Point(x, y);
		}
		System.out.println(x+":"+y);
		dr();
	}
	  private Point[] grow(Point[] a, int factor) {
		  Point tmp[] = a; a = new Point[a.length*factor]; 
		    //   for (int k = 0; k < tmp.length; k++) a[k] = tmp[k]; 
		    System.arraycopy(tmp, 0, a, 0, tmp.length); 
		    return tmp;
		  }
	public Point deleteLast() {
		if (!isEmpty())
			p[counter-1] =null;
			counter--;
		return p[counter];
	}

	public void removeAll() {
		counter = 0;
		p = new Point[4000];
	};

	final public void drawPolyline(Graphics g, int n) {
		g.setColor(Color.red);
		Graphics2D g2d=(Graphics2D)g;
		Stroke stroke=new BasicStroke(1.0f);
		g2d.setStroke(stroke);
		if (n > 1)
			for (int i = 1; i < n; i++)
				g2d.drawLine(p(i - 1).x(), p(i - 1).y(), p(i).x(), p(i).y());
	}

	public void draw(Graphics g, boolean label) {
		g.setColor(Color.red);
		for (int i = 0; i < number(); i++)
			g.fillOval(p(i).x() - 2, p(i).y() - 2, 4, 4);
		if (label){
			g.setColor(Color.black);
			for (int i = 0; i < number(); i++)
				g.drawString(Integer.toString(i), p(i).x(), p(i).y());}
	}

	public List<Point> getList() {
		List<Point> pointList = new ArrayList<Point>();
		for (int i = 0; i < counter; i++) {
			pointList.add(p[i]);
		}
		return pointList;

	}

	public void listToDraw(Graphics gs,Graphics g,List<Point> pointList) {
		Graphics2D g2d=(Graphics2D)gs;
		Stroke stroke=new BasicStroke(1.0f);
		g2d.setStroke(stroke);	
		g2d.setColor(Color.red);
		g2d.drawLine(p(0).x(), p(0).y(), p(counter-1).x(), p(counter-1).y());
		for (int i = 0; i < pointList.size(); i++)
			g.fillOval(pointList.get(i).x() - 2, pointList.get(i).y() - 2, 4, 4);
		if (pointList.size() > 1) {
			for (int j = 1; j < pointList.size(); j++)
				g.drawLine(pointList.get(j - 1).x(), pointList.get(j - 1).y(),
						pointList.get(j).x(), pointList.get(j).y());
			g.drawLine(pointList.get(0).x(), pointList.get(0).y(), pointList
					.get(pointList.size() - 1).x(),
					pointList.get(pointList.size() - 1).y());
		}

	}
	public void pointToDraw(List<Integer> list,List<Point> pointList,Graphics g){
      for (int i = 0; i< list.size()-1;i++){
    	  g.drawLine(pointList.get(list.get(i)).x(),pointList.get(list.get(i)).y(),pointList.get(list.get(i+1)).x(),pointList.get(list.get(i+1)).y());
      }
      g.drawLine(pointList.get(list.get(0)).x(),pointList.get(list.get(0)).y(),pointList.get(list.get(list.size()-1)).x(),pointList.get(list.get(list.size()-1)).y());		
	}
	public boolean ccw() {
		int length = p.length;
		for (int i = 0; i < p.length; i++) {
			if (p[i] == null) {
				length = i;
				break;
			}
		}
		double minx = p[0].x();
		int index = 0;
		for (int i = 1; i < length; i++) {
			int current = p[i].x();
			if (minx > current) {
				minx = current;
				index = i;
			}
		}
		double ccw = 0;
		while (index < length) {
			int indexNext = (index + 1) % length;
			int indexForward = (index - 1 + length) % length;
			ccw = (p[index].x - p[indexForward].x)
					* (p[index].y - p[indexNext].y)
					- (p[index].y - p[indexForward].y)
					* (p[index].x - p[indexNext].x);
			if (ccw == 0) {
				index++;
			} else {
				break;
			}
			
		}
		return ccw < 0;
	}

	public void reverse() {
		int length = p.length;
		for (int i = 0; i < p.length; i++) {
			if (p[i] == null) {
				length = i;
				break;
			}
		}
		for (int i = 0; i < length / 2; i++) {
			swap(i, length - 1 - i);
		}
	}
	final public void swap(int i, int j) {
		Point tmp = p[i];
		p[i] = p[j];
		p[j] = tmp;
		 dr();
		
	}

}

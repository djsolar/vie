package com.sunmap.teleview.element;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.element.mm.data.Node;
import com.sunmap.teleview.element.mm.data.Road;
import com.sunmap.teleview.element.view.data.BackGroundData;
import com.sunmap.teleview.element.view.data.RoadData;
import com.sunmap.teleview.element.view.data.RoadTextLineData;
import com.sunmap.teleview.element.view.data.TeleViewText;
import com.sunmap.teleview.util.ToolsUnit;
import com.sunmap.teleview.view.assist.style.ShapeManage;
import com.sunmap.teleview.view.mainfram.Information;
import com.sunmap.teleview.view.mainfram.Information.InfoType;
import com.sunmap.teleview.view.mainfram.ViewPanel;

/**
 * 信息栏数据
 * @author lijingru
 *
 */
public class InformationDataManage {

	public List<Object> listInInfor = new ArrayList<Object>();
	public List<Point> pick = new ArrayList<Point>();
	private List<Road> pickRoads = new ArrayList<Road>();
	public List<Point> pickPolygon = new ArrayList<Point>();
	public Point pickP = null;
	public static boolean isDrawShapePoint;
	
	
	/**
	 * 清理拾取数据
	 */
	public  void clearPick() {
		if (listInInfor!=null&&listInInfor.size()!=0) {
			listInInfor.clear();
		}
	}

	/**
	 * 清理特殊描画
	 */
	public  void clearPickDraw() {
		if (pick!=null&&pick.size()!=0) {
			pick.clear();
		}
		if (pickRoads!=null&&pickRoads.size()!=0) {
			pickRoads.clear();
		}
		if (pickPolygon!=null&&pickPolygon.size()!=0) {
			pickPolygon.clear();
		}
		if (pickP!=null) {
			pickP.clone();
			pickP = null;
		}
	}

	/**
	 * 加入特殊描画列表
	 * @param o
	 */
	public  void setSpecialDraw(Object o) {
		pick.clear();
		pickRoads.clear();
		pickPolygon.clear();
		pickP = null;
		if (o instanceof RoadData) {
			//view道路
			pick.addAll(((RoadData)o).points);
		}else if (o instanceof Road) {
			//MM道路
			pick.addAll(((Road) o).points);
		}else if (o instanceof TeleViewText) {
			//POI
			Point point = new Point(((TeleViewText) o).x, ((TeleViewText) o).y);	
			pickP = point;
		}else if (o instanceof BackGroundData) {
			//背景
			pickPolygon.addAll(((BackGroundData)o).points);
		}else if (o instanceof RoadTextLineData) {
			//道路文字线
			pick.addAll(((RoadTextLineData)o).points);
		}
	}
	
	/**
	 * 设置Node点包含的道路
	 * @param roads
	 * @param drawNodeString
	 */
	public void setDrawNodeLinkRoad(List<Road> roads){
		pick.clear();
		pickRoads.clear();
		pickP = null;
		for (int i = 0; i < roads.size(); i++) {
			pickRoads.add(roads.get(i));
		}
	}

	/**
	 * 描画拾取特殊显示内容
	 * @param g
	 */
	@SuppressWarnings("static-access")
	public void draw(Graphics2D g) {
		//清理
		g.setBackground(new Color(ViewPanel.backColor.getRed(), ViewPanel.backColor.getGreen(), ViewPanel.backColor.getBlue(),0));
		g.clearRect(0, 0, Controller.drawParams.widthPixels, Controller.drawParams.heightPixels);
		int dataLevel = Controller.telemanage.getViewBaseInfo().curDataLevel;
		if (pickRoads.size() == 0) {//描画拾取道路
			List<Point> pickDraw = drawLine(g);
			drawPath(g, pickDraw);
		}else {//描画Node点相关道路
			drawNodeLinkRoad(g);
			if (dataLevel < 2) {
				drawNodeLinkRoadPath(g);
			}
		}
		//拾取点描画
		drawPoint(g);
		//描画背景
		drawBg(g);
	}

	//描画背景
	private void drawBg(Graphics2D g) {
		GeneralPath pathPolygon = new GeneralPath();
		List<Point> pickDrawPolygon = new ArrayList<Point>(pickPolygon);
		Point startP = new Point();
		for (int i = 0; i < pickDrawPolygon.size(); i++) {
			Point2D point =Controller.drawParams.GeoToPix(pickDrawPolygon.get(i).x, pickDrawPolygon.get(i).y);
			if (i != 0) {
				pathPolygon.lineTo(point.getX(), point.getY());
			}else if (i == 0) {
				startP.x = (int) (point.getX() + 0.5);
				startP.y = (int) (point.getY() + 0.5);
				pathPolygon.moveTo(point.getX(), point.getY());
			}
		}
		g.setColor(new Color(244, 58, 53,150));
		g.fill(pathPolygon);
		g.setColor(new Color(3, 109, 169));
		g.draw(pathPolygon);
	}

	//拾取点描画
	private void drawPoint(Graphics2D g) {
		g.setColor(Color.RED);
		if (pickP!=null) {
			Point2D point =Controller.drawParams.GeoToPix(pickP.x, pickP.y);
			Shape s = ShapeManage.triangleShape(point,10);
			g.fill(s);
		}
	}

	//方向描画
	private void drawPath(Graphics2D g, List<Point> pickDraw) {
		g.setColor(new Color(255,0,128));
		for (int i = 0; i < pickDraw.size(); i++) {
			Point2D p =Controller.drawParams.GeoToPix(pickDraw.get(i));
			g.drawOval((int)(p.getX() + 0.5), (int)(p.getY() + 0.5), 3, 3);
		}
	}
	
	/**
	 * 描画node点所包含的道路的方向
	 * @param g
	 */
	private void drawNodeLinkRoadPath(Graphics2D g) {
		if (pickRoads.size() == 0) {
			return;
		}
		//取得Node点
		Node node = getSelectNode();
		//描画方向
		g.setColor(new Color(255,0,128));
		for (int i = 0; i < pickRoads.size(); i++) {
			Road road = pickRoads.get(i);
			for (int k = 0; k < road.points.size()-1; k++) {
				Point2D p1 =Controller.drawParams.GeoToPix(road.points.get(k));
				Point2D p2 =Controller.drawParams.GeoToPix(road.points.get(k+1));
				double theta =ToolsUnit.calcAngel(road.points.get(k).x, road.points.get(k).y, road.points.get(k+1).x, road.points.get(k+1).y) ;
				Point2D point;
				if (road.startNode.equals(node) == true) {
					point = ToolsUnit.oneTenthPoint(p1, p2);
				}else {
					point = ToolsUnit.oneTenthPoint(p2, p1);
				}
				g.setStroke(new BasicStroke(1.5f));
				g.draw(ShapeManage.arrowShape(point,theta,6));
			}
		}
		
	}

	//拾取线描画
	private List<Point> drawLine(Graphics2D g) {
		GeneralPath pathLine = new GeneralPath();
		List<Point> pickDraw = new ArrayList<Point>(pick);
		Point startP = new Point();
		for (int i = 0; i < pickDraw.size(); i++) {
			Point2D point =Controller.drawParams.GeoToPix(pickDraw.get(i).x, pickDraw.get(i).y);
			if (i != 0) {
				pathLine.lineTo(point.getX(), point.getY());
			}else if (i == 0) {
				startP.x = (int)( point.getX() + 1.5);
				startP.y = (int)( point.getY() + 1.5);
				pathLine.moveTo(point.getX(), point.getY());
			}
		}
		g.setStroke(new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER));
		g.setColor(new Color(3, 109, 169));
		g.draw(pathLine);
		// 起点画圆圈
		if (startP.x != 0 || startP.y != 0) {
			g.setColor(Color.RED);
			g.drawOval(startP.x, startP.y, 5, 5);
			g.drawOval(startP.x, startP.y, 2, 2);
		}
		return pickDraw;
	}

	/**
	 * 描画node点所包含的道路
	 * @param g
	 * @return
	 */
	private void drawNodeLinkRoad(Graphics2D g) {
		if (pickRoads.size() == 0) {
			return;
		}
		GeneralPath pathLine = new GeneralPath();
		Point startP = new Point();
		for (int i = 0; i < pickRoads.size(); i++) {
			Road road = pickRoads.get(i);
			for (int k = 0; k < road.points.size(); k++) {
				Point2D point =Controller.drawParams.GeoToPix(road.points.get(k).x, road.points.get(k).y);
				if (k != 0) {
					pathLine.lineTo(point.getX(), point.getY());
				}else if (k == 0) {
					startP.x = (int) (point.getX() + 0.5);
					startP.y = (int) (point.getY() + 0.5);
					pathLine.moveTo(point.getX(), point.getY());
				}
			}
			g.setStroke(new BasicStroke(2.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER));
			g.setColor(new Color(3, 109, 169));
			g.draw(pathLine);
		}
		// 画Node圆圈
		Point node = getNodeLocation();
		g.setColor(Color.RED);
		g.drawOval(node.x, node.y, 5, 5);
		g.drawOval(node.x, node.y, 2, 2);
	}
	
	/**
	 * 获取node点的位置
	 * @return
	 */
	private Point getNodeLocation() {
		Node pickNode = getSelectNode();
		long stempx = 0;
		long stempy = 0;
		for (int i = 0; i < pickRoads.size(); i++) {
			Road curRoad = pickRoads.get(i);
			if (curRoad.startNode.equals(pickNode)) {
				stempx += (long)curRoad.points.get(0).x;
				stempy += (long)curRoad.points.get(0).y;
			}else {
				stempx += (long)curRoad.points.get(curRoad.points.size() - 1).x;
				stempy += (long)curRoad.points.get(curRoad.points.size() - 1).y;
			}
		}
		int centerx = (int) (stempx / pickRoads.size());
		int centery = (int) (stempy / pickRoads.size());
		Point2D.Double centerPoint = Controller.drawParams.GeoToPix(new Point(centerx, centery));
		Point node = new Point();
		node.x = (int) (centerPoint.x+0.5);
		node.y = (int) (centerPoint.y+0.5);
		return node;
	}
	
	/**
	 * 得到你所选择的Node点
	 * @return
	 */
	private Node getSelectNode(){
		Node pickNode = new Node();
		if (pickRoads.size() == 1) {
			Road curRoad = pickRoads.get(0);
			if (curRoad.startNode.roadIndex.length == 1) {
				pickNode = curRoad.startNode;
			}
			if (curRoad.endNode.roadIndex.length == 1) {
				pickNode = curRoad.endNode;
			}
		}
		if (pickRoads.size() > 1) {
			Road curRoad = pickRoads.get(0);
			Road nextRoad = pickRoads.get(1);
			if (curRoad.startNode.equals(nextRoad.startNode)) {
				pickNode = curRoad.startNode;
			}else if (curRoad.startNode.equals(nextRoad.endNode)) {
				pickNode = curRoad.startNode;
			}else if (curRoad.endNode.equals(nextRoad.startNode)) {
				pickNode = curRoad.endNode;
			}else if (curRoad.endNode.equals(nextRoad.endNode)) {
				pickNode = curRoad.endNode;
			}
		}
		return pickNode;
	}
	
	/**
	 * 加入到信息框并且显示
	 */
	public void makeList(){
		java.util.List<String> strs = new ArrayList<String>();
		for (int i = 0; i < listInInfor.size(); i++) {
			Object o = listInInfor.get(i);
			if (o instanceof RoadData) {//view道路
				strs.add( ((RoadData)o).getStrList());
			}else if (o instanceof Road) {//mm道路
				strs.add(((Road)o).getStrList());
			}else if (o instanceof TeleViewText) {//POI
				strs.add(((TeleViewText)o).getStrList());
			}else if (o instanceof BackGroundData) {//背景
				strs.add(((BackGroundData)o).getStrList());
			}else if (o instanceof RoadTextLineData) {	//道路文字线
				strs.add(((RoadTextLineData)o).getStrList());
			}
		}
		Information information = Information.getInstance();
		information.setInfoType(InfoType.Pickup);
		information.setVisible(true);
		information.setList(strs);
		information.updateTable(0);
		information.drawChanged(0);
	}
	
	/**
	 * 加入到信息框并且显示
	 */
	public void makeSelectList(){
		java.util.List<String> strs = new ArrayList<String>();
		for (int i = 0; i < listInInfor.size(); i++) {
			Object o = listInInfor.get(i);
			if (o instanceof TeleViewText) {//POI
				strs.add(((TeleViewText)o).getStrList());
			}
		}
		Information information = Information.getInstance();
		information.setInfoType(InfoType.Select);
		information.setVisible(true);
		information.setList(strs);
		information.updateTable(0);
		information.moveChanged(0);
		information.drawChanged(0);
	}
	
}

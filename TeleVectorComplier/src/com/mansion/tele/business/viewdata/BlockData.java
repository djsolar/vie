package com.mansion.tele.business.viewdata;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.background.Area;
import com.mansion.tele.business.background.Line;
import com.mansion.tele.business.network.Route;
import com.mansion.tele.common.BlockNo;

public class BlockData {
	public BlockNo blockNo;
	public List<Route> routes = new ArrayList<Route>();
	public List<Landmark> landmarks = new ArrayList<Landmark>();
	public List<Area> polygons = new ArrayList<Area>();
	public List<Line> polylines = new ArrayList<Line>();
	
	//下面保存格式相关的内容
	int blockSize;//
	
	public void createFormat(OutputStream os){
		
	}
	
	
}

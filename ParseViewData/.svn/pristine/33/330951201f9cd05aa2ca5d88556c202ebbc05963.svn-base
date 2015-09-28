package com.sunmap.common;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.FileReader;
//import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.DataManager.LevelInfo;
import com.sunmap.been.Point;
import com.sunmap.been.Polygon;

public class Split {
	
	static int map_left = 1063692000;
	static int map_bottom = 363480000;
	static int map_right = 1082988000;
	static int map_top = 378408000;
	static int count = 0;
	JudgeRelation jr = new JudgeRelation();

	public static void main(String[] args) throws IOException {
//		JudgeRelation jr = new JudgeRelation();
		FileReader fr = new FileReader("C:\\Users\\yuxy\\Desktop\\����strgeom.txt");
		BufferedReader br = new BufferedReader(fr);
//		FileWriter fw = new FileWriter("C:\\Users\\yuxy\\Desktop\\����block.txt");
//		BufferedWriter bw = new BufferedWriter(fw);
		String data = "";
		String line = null;
		while((line = br.readLine()) != null){
			data += line;
		}
		String[] strPoints = data.split(",");
		Polygon polygon = new Polygon();
		for(String strPoint : strPoints){
			int x = Integer.parseInt(strPoint.split(" ")[0]);
			int y = Integer.parseInt(strPoint.split(" ")[1]);
			Point p = new Point(x, y);
			polygon.points.add(p);
		}
//		List<String> result = new ArrayList<String>();
//		for(int y = map_bottom;y <= map_top;y += 24000){
//			for(int x = map_left;x <= map_right;x += 36000){
//				Polygon block = new Polygon();
//				block.left_bottom = new Point(x, y);
//				block.left_top = new Point(x, y+24000);
//				block.right_bottom = new Point(x+36000, y);
//				block.right_top = new Point(x+36000, y+24000);
//				if(jr.relation(block, polygon)){
////					int blockx = (x - 184320000) / 36000;
////					int blocky = y / 24000;
////					bw.write(blockx + " " + blocky);
////					bw.newLine();
//					count ++;
//					if(count % 1000 == 0){
//						System.out.println("��"+ count + "����" + x + "," + y);
//					}
//					result.add(x + "," + y);
//				}
//			}
//		}
//		System.out.println(result.size());
		br.close();
//		bw.flush();
//		bw.close();
//		Split test = new Split();
//		List<Polygon> result = test.splitBlock(polygon);
//		System.out.println(result.size());
	}
	
	public List<Polygon> splitBlock(Polygon polygon, Polygon rect, LevelInfo levelInfo){
		List<Polygon> blocks = new ArrayList<Polygon>();
		List<Polygon> result = new ArrayList<Polygon>();
//		Polygon original = new Polygon(); 
//		original.left_bottom = new Point(map_left, map_bottom);
//		original.left_top = new Point(map_left, map_top+24000);
//		original.right_bottom = new Point(map_right+36000, map_bottom);
//		original.right_top = new Point(map_right+36000, map_top+24000);
//		blocks.add(original);
		blocks.add(rect);
		while(blocks.size() > 0){
			List<Polygon> splitedBlocks = new ArrayList<Polygon>();
			for(Polygon block : blocks){
				splitedBlocks.addAll(split(block, result, polygon, levelInfo));
			}
			blocks = splitedBlocks;
		}
		return result;
	}
	
	public List<Polygon> split(Polygon block, List<Polygon> result, Polygon polygon, LevelInfo levelInfo){
		List<Polygon> splitedBlocks = new ArrayList<Polygon>();
		long width = block.right - block.left;
		long height = block.top - block.bottom;
		if(width == levelInfo.iBlockWidth){
			if(height == levelInfo.iBlockHight){
				result.add(block);
			}
			else{
				int count = (int) (height / levelInfo.iBlockHight);
				Polygon split1 = new Polygon();
				split1.left = block.left;
				split1.bottom = block.bottom;
				split1.right = block.right;
				split1.top =  block.bottom + (count+1)/2 * levelInfo.iBlockHight;
				Polygon split2 = new Polygon();
				split2.left = split1.left;
				split2.bottom = split1.top;
				split2.right = split1.right;
				split2.top = block.top;
				if(jr.relation(split1, polygon)){
					splitedBlocks.add(split1);
				}
				if(jr.relation(split2, polygon)){
					splitedBlocks.add(split2);
				}
			}
		}
		else{
			if(height == levelInfo.iBlockHight){
				int count = (int) (width / levelInfo.iBlockWidth);
				Polygon split1 = new Polygon();
				split1.left = block.left;
				split1.bottom = block.bottom; 
				split1.right = block.left + (count+1)/2 * levelInfo.iBlockWidth;
				split1.top = block.top;
				Polygon split2 = new Polygon();
				split2.left = split1.right;
				split2.bottom = split1.bottom;
				split2.right = block.right;
				split2.top = split1.top;
				if(jr.relation(split1, polygon)){
					splitedBlocks.add(split1);
				}
				if(jr.relation(split2, polygon)){
					splitedBlocks.add(split2);
				}
			}
			else{
				int countx = (int) (width / levelInfo.iBlockWidth);
				int county = (int) (height / levelInfo.iBlockHight);
				Polygon split1 = new Polygon();
				split1.left = block.left;
				split1.bottom = block.bottom;
				split1.right = block.left + (countx+1)/2 * levelInfo.iBlockWidth;
				split1.top = block.bottom + (county+1)/2 * levelInfo.iBlockHight;
				Polygon split2 = new Polygon();
				split2.left = split1.right;
				split2.bottom = split1.bottom;
				split2.right = block.right; 
				split2.top = split1.top;		
				Polygon split3 = new Polygon();
				split3.left = split1.left;
				split3.bottom = split1.top;
				split3.right = split1.right;
				split3.top = block.top;
				Polygon split4 = new Polygon();
				split4.left = split1.right;
				split4.bottom = split1.top;
				split4.right = block.right;
				split4.top = block.top;
				if(jr.relation(split1, polygon)){
					splitedBlocks.add(split1);
				}
				if(jr.relation(split2, polygon)){
					splitedBlocks.add(split2);
				}
				if(jr.relation(split3, polygon)){
					splitedBlocks.add(split3);
				}
				if(jr.relation(split4, polygon)){
					splitedBlocks.add(split4);
				}
			}
		}
		return splitedBlocks;
	}

}

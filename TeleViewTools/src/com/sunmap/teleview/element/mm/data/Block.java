package com.sunmap.teleview.element.mm.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sunmap.teleview.element.mm.data.NodeRec.EdgeNode;


/**
 * @author lijingru
 *
 */
public class Block {

		short blockIndexX;
		short blockIndexY;
		
		public List<LinkRec> links = new ArrayList<LinkRec>();
		List<NodeRec> nodes = new ArrayList<NodeRec>();
		List<ShpRec> shps = new ArrayList<ShpRec>();
		
		private int offset = 0; //从block头到当前位置的偏移。数据解析用。
		
		public Block(int xIndex, int yIndex) {
			// TODO Auto-generated constructor stub
			blockIndexX = (short) xIndex;
			blockIndexY = (short) yIndex;
		}

	public void parse(DataInputStream dis) throws IOException {

		// 第一条Link的LinkID
		int linkID = dis.readInt();
		linkID = linkID << 32;
		// Link记录个数
		int linkCount = dis.readUnsignedShort();
		// Node记录个数
		int nodeCount = dis.readUnsignedShort();
		// 保留的
		dis.readByte();
		offset += 20;
		// Link信息记录
		for (int i = 0; i < linkCount; i++) {
			LinkRec link = parseLinkRec(dis);
			link.road.roadID = linkID;
			linkID++;
			links.add(link);
		}
		offset += linkCount * 11;
		// Node信息记录
		for (int i = 0; i < nodeCount; i++) {
			NodeRec node = parseNodeRec(dis);
			nodes.add(node);
		}
		// Link形状信息Table
		// 形状点个数
		for (int i = 0; i < linkCount; i++) {
			LinkRec link = links.get(i);
			int shpCount = dis.readUnsignedShort();
			link.road.pointxsf = new float[shpCount];
			link.road.pointysf = new float[shpCount];
			// Link形状信息记录
			for (int j = 0; j < shpCount; j++) {
				if (link.shapeOffset == offset) {
					link.road.pointxsf[j] = dis.readUnsignedByte();
					link.road.pointysf[j] = dis.readUnsignedByte();
				} else {
					System.out.println("link记录和形状记录顺序不一致。");
				}
			}
			offset += 2 + shpCount * 2;

		}
		// 制作拓扑
		NodeRec node = new NodeRec();
		for (int i = 0; i < linkCount; i++) {
			LinkRec link = links.get(i);

			node.offset = link.startNodeOffset;
			int index = Collections
					.binarySearch(nodes, node, NodeRec.comprator);
			link.road.startNode = nodes.get(index).node;

			node.offset = link.endNodeOffset;
			index = Collections.binarySearch(nodes, node, NodeRec.comprator);
			link.road.endNode = nodes.get(index).node;

		}
	}

		private NodeRec parseNodeRec(DataInputStream dis) throws IOException {
			// TODO Auto-generated method stub
			NodeRec node = new NodeRec();
			node.node = new Node();
			
			// Node属性
			node.attr = dis.readShort();
			node.offset = offset;
			offset += 2;
			//连接Link个数
			node.linkCount = dis.readByte();
			offset += 1;
			
			//交叉点内部的Link个数
			node.dummyLinkCount = dis.readByte();
			offset += 1;

			//禁止通行规则记录个数
			node.ruleCount = dis.readByte();
			offset += 1;
			//连接Link记录排列
			node.node.roadIndex = new short[node.linkCount];
			node.node.blockIndexX = new short[node.linkCount];
			node.node.blockIndexY = new short[node.linkCount];
			for(int i = 0; i < node.linkCount; i++){
				short atrr = dis.readShort();
				int blocknum = atrr & 0x0F;
				if(blocknum != 0){
					node.node.edgeFlag = true;
				}
				switch(blocknum){
				case 0://当前
					node.node.blockIndexX[i] = blockIndexX;
					node.node.blockIndexY[i] = blockIndexY;
					break;
				case 1://上方
					node.node.blockIndexX[i] = blockIndexX;
					node.node.blockIndexY[i] = (short) (blockIndexY + 1);
					break;
				case 2://右上方
					node.node.blockIndexX[i] = (short) (blockIndexX + 1);
					node.node.blockIndexY[i] = (short) (blockIndexY + 1);
					break;
				case 3://右方
					node.node.blockIndexX[i] = (short) (blockIndexX + 1);
					node.node.blockIndexY[i] = blockIndexY;
					break;
				case 4://右下
					node.node.blockIndexX[i] = (short) (blockIndexX + 1);
					node.node.blockIndexY[i] = (short) (blockIndexY - 1);
					break;
				case 5://下方
					node.node.blockIndexX[i] = blockIndexX;
					node.node.blockIndexY[i] = (short) (blockIndexY - 1);
					break;
				case 6://左下方
					node.node.blockIndexX[i] = (short) (blockIndexX - 1);
					node.node.blockIndexY[i] = (short) (blockIndexY - 1);
					break;
				case 7://左方
					node.node.blockIndexX[i] = (short) (blockIndexX - 1);
					node.node.blockIndexY[i] = blockIndexY;
					break;
				case 8://左上方
					node.node.blockIndexX[i] = (short) (blockIndexX - 1);
					node.node.blockIndexY[i] = (short) (blockIndexY + 1);
					break;
				}
				node.node.roadIndex[i] = dis.readShort();
			}
			offset += node.linkCount * 4;
			//交叉点内部的Link记录排列
			for(int i = 0; i < node.dummyLinkCount; i++){
				dis.readShort();
				dis.readShort();
			}
			offset += node.dummyLinkCount * 4;		
			
			//禁止通行规则记录排列
			for(int i = 0; i < node.ruleCount; i++){
				dis.readShort();
			}
			offset += node.ruleCount * 2;

			//边界node道路有序,目的：跨边界相同node查找
			if(node.node.edgeFlag){
				List<EdgeNode> edgeNodes = new ArrayList<EdgeNode>();
				for(int i = 0; i < node.node.blockIndexX.length; i++){
					EdgeNode edgeNodeItem = new EdgeNode();
					edgeNodeItem.roadIndex = node.node.roadIndex[i];
					edgeNodeItem.blockIndexX = node.node.blockIndexX[i];
					edgeNodeItem.blockIndexY = node.node.blockIndexY[i];
					edgeNodes.add(edgeNodeItem);
				}
				Collections.sort(edgeNodes,EdgeNode.comprator);
				for(int i = 0; i < edgeNodes.size(); i++){
					EdgeNode edgeNodeItem = edgeNodes.get(i);
					node.node.roadIndex[i] = edgeNodeItem.roadIndex;
					node.node.blockIndexX[i] = edgeNodeItem.blockIndexX;
					node.node.blockIndexY[i] = edgeNodeItem.blockIndexY;
				}
			}
			return node;
		}

		private LinkRec parseLinkRec(DataInputStream dis) throws IOException {
			// TODO Auto-generated method stub
			LinkRec link = new LinkRec();
			link.road = new Road();
			
			//Link形状信息Table偏移
			link.shapeOffset = dis.readUnsignedShort();
			//始点Node信息记录偏移
			link.startNodeOffset = dis.readUnsignedShort();
			//终点Node信息记录偏移
			link.endNodeOffset = dis.readUnsignedShort();
			//Link属性
			link.road.linkAttr = dis.readInt();
			//Link属性2
			link.road.linkAttr2 = dis.readByte();

			return link;
		}
		

}

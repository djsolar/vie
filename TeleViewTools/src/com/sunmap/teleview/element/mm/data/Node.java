package com.sunmap.teleview.element.mm.data;



/**
 * 说明：
 * 1，不保存交叉点内道路；
 * 2，不论是否能通行，所有路口链接路都保存；
 * @author niujw
 *
 */
public class Node {
	public short roadIndex[];//连接路在相应blockData中的索引
	public short blockIndexX[];//链接路所在的x方向的block号；
	public short blockIndexY[];
	public byte rules[][];//通行code矩阵：逆行、有规制、正常通行、引导code。	
	public boolean edgeFlag;	//边界标记 true:是边界node

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		Node otherNode = (Node)o;
		if(roadIndex.length != otherNode.roadIndex.length){
			return false;
		}
		for(int i = 0; i < roadIndex.length; i++){
			if(roadIndex[i] != otherNode.roadIndex[i]
					|| blockIndexX[i] != otherNode.blockIndexX[i]
					|| blockIndexY[i] != otherNode.blockIndexY[i]){
				return false;
			}
		}
		return true;
	}

	public void clone(Node node){
		this.blockIndexX = node.blockIndexX;
		this.blockIndexY = node.blockIndexY;
		this.roadIndex = node.roadIndex;
		this.rules = node.rules;
		this.edgeFlag = node.edgeFlag;
	}
}
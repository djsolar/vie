/**
 * 
 */
package com.sunmap.teleview.element.mm.data;

import java.util.Comparator;

/**
 * @author lijingru
 *
 */
public class NodeRec {
public int offset;//从block头到当前位置的偏移。制作拓扑关系用。
	
	public Node node;
	//Node属性
	short attr;
	//连接Link个数
	byte linkCount;
	//交叉点内部的Link个数
	byte dummyLinkCount;
	//禁止通行规则记录个数
	byte ruleCount;
	//连接Link记录排列
	//交叉点内部的Link记录排列
	//禁止通行规则记录排列
	
	public static Comparator<NodeRec> comprator = new Comparator<NodeRec>(){
		public int compare(NodeRec o1, NodeRec o2) {
			if(o1.offset < o2.offset){
				return -1;
			}else if(o1.offset == o2.offset){
				return 0;
			}else{
				return 1;
			}
		}
	};
	
	static class EdgeNode{
		public short roadIndex;//连接路在相应blockData中的索引
		public short blockIndexX;//链接路所在的x方向的block号；
		public short blockIndexY;
		public static Comparator<EdgeNode> comprator = new Comparator<EdgeNode>(){
			public int compare(EdgeNode l, EdgeNode r) {
				if(l.roadIndex < r.roadIndex){
					return -1;
				}
				if(l.roadIndex > r.roadIndex){
					return 1;
				}
				if(l.blockIndexX < r.blockIndexX){
					return -1;
				}
				if(l.blockIndexX > r.blockIndexX){
					return 1;
				}
				if(l.blockIndexY < r.blockIndexY){
					return -1;
				}
				if(l.blockIndexY > r.blockIndexY){
					return 1;
				}
				return 0;
			}
		};
		
	}
}

package com.mansion.tele.db.bean.elemnet;

import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class Node extends PointElement
{
//	protected byte byIsAdmBorder;                // �Ƿ���������߽��
	protected byte byIsTaskBorder;               // �Ƿ�������߽��
//	protected byte byIsTollGate;                 // �Ƿ����շ�վ
	protected byte byGrade;                      // �߶ȵȼ�����ֱ����ĸ߶ȣ�3��2��
	protected byte byIsDummyNode;                // �Ƿ�������l�ӵ�
	protected List<String> astRoadIDList;     	 // ���l�ӵ���l�ĵ�·Ԫ��ID������
//	protected List<BaseName> astNodeNames;       // l�ӵ����
	
	protected Set<String> astStartRoadIDs;      // �Դ�l�ӵ�Ϊ���ĵ�·Ԫ��ID������,��ݿ�road����
	protected Set<String> astEndRoadIDs;        // �Դ�l�ӵ�Ϊ�յ�ĵ�·Ԫ��ID������,��ݿ�road����
	
//	public byte getByisadmborder() {
//		return this.byIsAdmBorder;
//	}
//
//	public void setByisadmborder(byte byisadmborder) {
//		this.byIsAdmBorder = byisadmborder;
//	}

	public byte getByistaskborder() {
		return this.byIsTaskBorder;
	}

	public void setByistaskborder(byte byistaskborder) {
		this.byIsTaskBorder = byistaskborder;
	}

//	public byte getByistollgate() {
//		return this.byIsTollGate;
//	}
//
//	public void setByistollgate(byte byistollgate) {
//		this.byIsTollGate = byistollgate;
//	}

//	public byte getBygrade() {
//		return this.byGrade;
//	}
//
//	public void setBygrade(byte bygrade) {
//		this.byGrade = bygrade;
//	}
	
	
//
	public byte getByisdummynode() {
		return this.byIsDummyNode;
	}

	public void setByisdummynode(byte byisdummynode) {
		this.byIsDummyNode = byisdummynode;
	}
	public List<String> getAstRoadIDList() {
		return astRoadIDList;
	}

	public byte getByGrade() {
		return byGrade;
	}

	public void setByGrade(byte byGrade) {
		this.byGrade = byGrade;
	}

	public void setAstRoadIDList(List<String> aststrRoadIDs) {
		if (aststrRoadIDs == null && this.astRoadIDList != null) {
			this.astRoadIDList.clear();
		} else {
			this.astRoadIDList = aststrRoadIDs;
		}
	}

//	public List<BaseName> getAstNodeNames()
//	{
//		return astNodeNames;
//	}
//	
//	public void setAstNodeNames(List<BaseName> astNodeNames)
//	{
//		if (astNodeNames == null && this.astNodeNames != null) {
//			this.astNodeNames.clear();
//		} else {
//			this.astNodeNames = astNodeNames;
//		}
//	}

	public Set<String> getAstStartRoadIDs() {
		return astStartRoadIDs;
	}
	
	public void setAstStartRoadIDs(Set<String> astStartRoadIDs) {
		if (astStartRoadIDs == null && this.astStartRoadIDs != null) {
			this.astStartRoadIDs.clear();
		} else {
			this.astStartRoadIDs = astStartRoadIDs;
		}
	}
	
	public Set<String> getAstEndRoadIDs() {
		return astEndRoadIDs;
	}

	public void setAstEndRoadIDs(Set<String> astEndRoadIDs) {
		if (astEndRoadIDs == null && this.astEndRoadIDs != null) {
			this.astEndRoadIDs.clear();
		} else {
			this.astEndRoadIDs = astEndRoadIDs;
		}
	}
	//式样
	public static final class NodeType{
		public static final byte meshBorder = 5;
	}
}

package com.mansion.tele.db.bean.elemnet;

public class RouteNo extends SubElement
{

	private static final long serialVersionUID = 6955692815044294518L;
	private String prefix; // ǰ׺��������
	private String number; // ·�߱��
	private String postfix; // ��׺���?��
	
	public String getPrefix()
	{
		return prefix;
	}
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
	public String getNumber()
	{
		return number;
	}
	public void setNumber(String number)
	{
		this.number = number;
	}
	public String getPostfix()
	{
		return postfix;
	}
	public void setPostfix(String postfix)
	{
		this.postfix = postfix;
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();
		if(prefix != null){
			strBuf.append(prefix);
		} 
		if(number != null){
			strBuf.append(number);
		} 
		if(postfix != null){
			strBuf.append(postfix);
		}
		
		return strBuf.toString();
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((postfix == null) ? 0 : postfix.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RouteNo other = (RouteNo) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (postfix == null) {
			if (other.postfix != null)
				return false;
		} else if (!postfix.equals(other.postfix))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		return true;
	}

}
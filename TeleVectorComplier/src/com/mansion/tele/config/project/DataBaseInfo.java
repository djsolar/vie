package com.mansion.tele.config.project;

import java.io.Serializable;

public class DataBaseInfo implements Serializable{
	/**
	 * jndi
	 */
	private String strJNDI;
	/**
	 * url
	 */
	private String strURL;
	/**
	 * name
	 */
	private String strUserName;
	/**
	 * passwd
	 */
	private String strPassword;
	/**
	 * 
	 * @return String
	 */
	public String getStrJNDI() {
		return this.strJNDI;
	}
	/**
	 * 
	 * @param strJNDI String
	 */
	public void setStrJNDI(String strJNDI) {
		this.strJNDI = strJNDI;
	}
	/**
	 * 
	 * @return String
	 */
	public String getStrURL() {
		return this.strURL;
	}
	/**
	 * 
	 * @param strURL String
	 */
	public void setStrURL(String strURL) {
		this.strURL = strURL;
	}
	/**
	 * 
	 * @return String
	 */
	public String getStrUserName() {
		return this.strUserName;
	}
	/**
	 * 
	 * @param strUserName String
	 */
	public void setStrUserName(String strUserName) {
		this.strUserName = strUserName;
	}
	/**
	 * 
	 * @return String
	 */
	public String getStrPassword() {
		return this.strPassword;
	}
	/**
	 * 
	 * @param strPassword String
	 */
	public void setStrPassword(String strPassword) {
		this.strPassword = strPassword;
	}


}

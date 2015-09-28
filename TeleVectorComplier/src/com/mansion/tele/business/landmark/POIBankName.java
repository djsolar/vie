package com.mansion.tele.business.landmark;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 * 
 */
public class POIBankName implements Serializable {
	private String bankname;
	private String bankid;

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBankid() {
		return bankid;
	}

	public void setBankid(String bankid) {
		this.bankid = bankid;
	}

}
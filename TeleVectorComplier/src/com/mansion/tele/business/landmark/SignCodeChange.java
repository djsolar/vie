package com.mansion.tele.business.landmark;

import java.io.Serializable;

public class SignCodeChange implements Serializable {
	private static final long serialVersionUID = -527586826384036111L;

	/**
	 * 母库中的Code
	 */
	long orgCode;

	/**
	 * Tele的Code
	 */
	int kind;
	
	public int getKind(long orgCode){
		return 0;
	}


}

package com.power.report.entity;

import java.math.BigDecimal;

public class txfcllpmbDataVO {
	private String txffullname;
	private BigDecimal txfsr = BigDecimal.ZERO;
	private String cllfullname;
	private Integer cll = 0;
	public String getTxffullname() {
		return txffullname;
	}
	public void setTxffullname(String txffullname) {
		this.txffullname = txffullname;
	}
	public BigDecimal getTxfsr() {
		return txfsr;
	}
	public void setTxfsr(BigDecimal txfsr) {
		this.txfsr = txfsr;
	}
	public String getCllfullname() {
		return cllfullname;
	}
	public void setCllfullname(String cllfullname) {
		this.cllfullname = cllfullname;
	}
	public Integer getCll() {
		return cll;
	}
	public void setCll(Integer cll) {
		this.cll = cll;
	}
	
}

package com.power.report.entity;

import java.math.BigDecimal;

public class TxfsrVO {
	private String fullname;
	private Integer flag;
	private String sfrq;
	private BigDecimal cqsr=BigDecimal.ZERO;
	private BigDecimal chsr=BigDecimal.ZERO;
	private BigDecimal sychsr=BigDecimal.ZERO;
	private BigDecimal qnchsr=BigDecimal.ZERO;
	private BigDecimal bnljchsr=BigDecimal.ZERO;
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public String getSfrq() {
		return sfrq;
	}
	public void setSfrq(String sfrq) {
		this.sfrq = sfrq;
	}
	public BigDecimal getCqsr() {
		return cqsr;
	}
	public void setCqsr(BigDecimal cqsr) {
		this.cqsr = cqsr;
	}
	public BigDecimal getChsr() {
		return chsr;
	}
	public void setChsr(BigDecimal chsr) {
		this.chsr = chsr;
	}
	public BigDecimal getSychsr() {
		return sychsr;
	}
	public void setSychsr(BigDecimal sychsr) {
		this.sychsr = sychsr;
	}
	public BigDecimal getQnchsr() {
		return qnchsr;
	}
	public void setQnchsr(BigDecimal qnchsr) {
		this.qnchsr = qnchsr;
	}
	public BigDecimal getBnljchsr() {
		return bnljchsr;
	}
	public void setBnljchsr(BigDecimal bnljchsr) {
		this.bnljchsr = bnljchsr;
	}
	
}

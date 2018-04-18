package com.power.report.entity;

import java.math.BigDecimal;

public class LtmxbDataVO {
	private String dw;
	private String hj;
	private String sfrq;
	private String fullName;
	private BigDecimal bymzje=BigDecimal.ZERO;
	private BigDecimal symzje=BigDecimal.ZERO;
	private BigDecimal bymzjeysyb=BigDecimal.ZERO;
	private Integer bymzll = 0;
	private Integer symzll = 0;
	private BigDecimal bymzllysyb=BigDecimal.ZERO;
	private BigDecimal bnljmzje=BigDecimal.ZERO;
	private Integer bnljmzll=0;
	private BigDecimal zktljmzje=BigDecimal.ZERO;
	private Integer zktljmzll=0;
	
	public String getHj() {
		return hj;
	}
	public void setHj(String hj) {
		this.hj = hj;
	}
	public String getDw() {
		return dw;
	}
	public void setDw(String dw) {
		this.dw = dw;
	}
	
	public String getSfrq() {
		return sfrq;
	}
	public void setSfrq(String sfrq) {
		this.sfrq = sfrq;
	}

	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public BigDecimal getBymzje() {
		return bymzje;
	}
	public void setBymzje(BigDecimal bymzje) {
		this.bymzje = bymzje;
	}
	public BigDecimal getSymzje() {
		return symzje;
	}
	public void setSymzje(BigDecimal symzje) {
		this.symzje = symzje;
	}
	public BigDecimal getBymzjeysyb() {
		return bymzjeysyb;
	}
	public void setBymzjeysyb(BigDecimal bymzjeysyb) {
		this.bymzjeysyb = bymzjeysyb;
	}
	public Integer getBymzll() {
		return bymzll;
	}
	public void setBymzll(Integer bymzll) {
		this.bymzll = bymzll;
	}
	public Integer getSymzll() {
		return symzll;
	}
	public void setSymzll(Integer symzll) {
		this.symzll = symzll;
	}
	public BigDecimal getBymzllysyb() {
		return bymzllysyb;
	}
	public void setBymzllysyb(BigDecimal bymzllysyb) {
		this.bymzllysyb = bymzllysyb;
	}
	public BigDecimal getBnljmzje() {
		return bnljmzje;
	}
	public void setBnljmzje(BigDecimal bnljmzje) {
		this.bnljmzje = bnljmzje;
	}
	
	public Integer getBnljmzll() {
		return bnljmzll;
	}
	public void setBnljmzll(Integer bnljmzll) {
		this.bnljmzll = bnljmzll;
	}
	public BigDecimal getZktljmzje() {
		return zktljmzje;
	}
	public void setZktljmzje(BigDecimal zktljmzje) {
		this.zktljmzje = zktljmzje;
	}
	public Integer getZktljmzll() {
		return zktljmzll;
	}
	public void setZktljmzll(Integer zktljmzll) {
		this.zktljmzll = zktljmzll;
	}
	
}

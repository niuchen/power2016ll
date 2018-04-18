package com.power.report.entity;

import java.math.BigDecimal;

public class BbfxsmDataVO {
	private String zdmc;
	
	private BigDecimal bychsr = BigDecimal.ZERO;
	private BigDecimal sychsr = BigDecimal.ZERO;
	private BigDecimal tqchsr = BigDecimal.ZERO;
	private BigDecimal bnchsr = BigDecimal.ZERO;
	private BigDecimal bntqchsr = BigDecimal.ZERO;
	private String hj;
	private String bz;
	public String getZdmc() {
		return zdmc;
	}
	public void setZdmc(String zdmc) {
		this.zdmc = zdmc;
	}
	public BigDecimal getBychsr() {
		return bychsr;
	}
	public void setBychsr(BigDecimal bychsr) {
		this.bychsr = bychsr;
	}
	public BigDecimal getSychsr() {
		return sychsr;
	}
	public void setSychsr(BigDecimal sychsr) {
		this.sychsr = sychsr;
	}
	public BigDecimal getTqchsr() {
		return tqchsr;
	}
	public void setTqchsr(BigDecimal tqchsr) {
		this.tqchsr = tqchsr;
	}
	public BigDecimal getBnchsr() {
		return bnchsr;
	}
	public void setBnchsr(BigDecimal bnchsr) {
		this.bnchsr = bnchsr;
	}
	public String getHj() {
		return hj;
	}
	public void setHj(String hj) {
		this.hj = hj;
	}
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	public BigDecimal getBntqchsr() {
		return bntqchsr;
	}
	public void setBntqchsr(BigDecimal bntqchsr) {
		this.bntqchsr = bntqchsr;
	}
	
	
	
}

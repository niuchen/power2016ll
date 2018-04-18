package com.power.report.entity;

import java.math.BigDecimal;

/**
 * 项目名称：power2016 <br>
 * 类名称：JkrbDataVO <br>
 * 创建时间：2017-4-16 下午5:29:55 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class JkrbDataVO {
	private String sfzdm;
	private String zdmc;
	private String sfrq;
	private String jfrq1;
	private String jfrq2;
	private BigDecimal yjje1;
	private BigDecimal yjje2;
	private BigDecimal sjje1;
	private BigDecimal sjje2;
	private BigDecimal yjjehj;
	private BigDecimal sjjehj;
	private String bz;
	
	
	public String getSfzdm() {
		return sfzdm;
	}
	public void setSfzdm(String sfzdm) {
		this.sfzdm = sfzdm;
	}
	public String getJfrq1() {
		return jfrq1;
	}
	public void setJfrq1(String jfrq1) {
		this.jfrq1 = jfrq1;
	}
	public String getJfrq2() {
		return jfrq2;
	}
	public void setJfrq2(String jfrq2) {
		this.jfrq2 = jfrq2;
	}
	public BigDecimal getYjje1() {
		return yjje1;
	}
	public void setYjje1(BigDecimal yjje1) {
		this.yjje1 = yjje1;
	}
	public BigDecimal getYjje2() {
		return yjje2;
	}
	public void setYjje2(BigDecimal yjje2) {
		this.yjje2 = yjje2;
	}
	public BigDecimal getSjje1() {
		return sjje1;
	}
	public void setSjje1(BigDecimal sjje1) {
		this.sjje1 = sjje1;
	}
	public BigDecimal getSjje2() {
		return sjje2;
	}
	public void setSjje2(BigDecimal sjje2) {
		this.sjje2 = sjje2;
	}
	public String getZdmc() {
		return zdmc;
	}
	public void setZdmc(String zdmc) {
		this.zdmc = zdmc;
	}
	public String getSfrq() {
		return sfrq;
	}
	public void setSfrq(String sfrq) {
		this.sfrq = sfrq;
	}
	public BigDecimal getYjjehj() {
		return yjjehj;
	}
	public void setYjjehj(BigDecimal yjjehj) {
		this.yjjehj = yjjehj;
	}
	public BigDecimal getSjjehj() {
		return sjjehj;
	}
	public void setSjjehj(BigDecimal sjjehj) {
		this.sjjehj = sjjehj;
	}
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
}

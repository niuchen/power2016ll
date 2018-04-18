package com.power.app.entity;

import java.math.BigDecimal;

/**
 * 项目名称：power2016 <br>
 * 类名称：FyzbVO <br>
 * 创建时间：2017-4-7 下午5:14:00 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class FyzbVO {
	private Integer month;
	private BigDecimal sfhj=BigDecimal.ZERO;
	private BigDecimal etc=BigDecimal.ZERO;
	private BigDecimal mtc=BigDecimal.ZERO;
	private Integer mzcrkhj=0;
	private Integer kccrkhj=0;
	private Integer hccrkhj=0;
	private Integer crkllhj=0;
	private BigDecimal kcsrzj=BigDecimal.ZERO;
	private BigDecimal hcsrzj=BigDecimal.ZERO;
	
	public FyzbVO() {}
	/**
	 * @param month
	 */
	public FyzbVO(Integer month) {
		super();
		this.month = month;
	}
	
	public Integer getCrkllhj() {
		return crkllhj;
	}
	public void setCrkllhj(Integer crkllhj) {
		this.crkllhj = crkllhj;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public BigDecimal getSfhj() {
		return sfhj;
	}
	public void setSfhj(BigDecimal sfhj) {
		this.sfhj = sfhj;
	}
	public BigDecimal getEtc() {
		return etc;
	}
	public void setEtc(BigDecimal etc) {
		this.etc = etc;
	}
	public BigDecimal getMtc() {
		return mtc;
	}
	public void setMtc(BigDecimal mtc) {
		this.mtc = mtc;
	}
	public Integer getMzcrkhj() {
		return mzcrkhj;
	}
	public void setMzcrkhj(Integer mzcrkhj) {
		this.mzcrkhj = mzcrkhj;
	}
	public Integer getKccrkhj() {
		return kccrkhj;
	}
	public void setKccrkhj(Integer kccrkhj) {
		this.kccrkhj = kccrkhj;
	}
	public Integer getHccrkhj() {
		return hccrkhj;
	}
	public void setHccrkhj(Integer hccrkhj) {
		this.hccrkhj = hccrkhj;
	}
	public BigDecimal getKcsrzj() {
		return kcsrzj;
	}
	public void setKcsrzj(BigDecimal kcsrzj) {
		this.kcsrzj = kcsrzj;
	}
	public BigDecimal getHcsrzj() {
		return hcsrzj;
	}
	public void setHcsrzj(BigDecimal hcsrzj) {
		this.hcsrzj = hcsrzj;
	}

}

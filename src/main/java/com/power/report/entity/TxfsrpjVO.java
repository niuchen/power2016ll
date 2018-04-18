package com.power.report.entity;

import java.math.BigDecimal;

/**
 * 通行费收入及票据
 * 项目名称：power2016 <br>
 * 类名称：TxfsrpjVO <br>
 * 创建时间：2017-4-26 上午9:27:48 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class TxfsrpjVO {
	private String hj;
	private BigDecimal bysr = BigDecimal.ZERO;
	private BigDecimal bnsr = BigDecimal.ZERO;
	private BigDecimal zktsr = BigDecimal.ZERO;
	private BigDecimal bychsr = BigDecimal.ZERO;
	private BigDecimal bnchsr = BigDecimal.ZERO;
	private BigDecimal zktchsr = BigDecimal.ZERO;
	private BigDecimal byck = BigDecimal.ZERO;
	private BigDecimal bnck = BigDecimal.ZERO;
	private BigDecimal zktck = BigDecimal.ZERO;
	private BigDecimal bydk = BigDecimal.ZERO;
	private BigDecimal bndk = BigDecimal.ZERO;
	private BigDecimal zktdk = BigDecimal.ZERO;
	private BigDecimal byjzsr = BigDecimal.ZERO;
	private BigDecimal bnjzsr = BigDecimal.ZERO;
	private BigDecimal zktjzsr = BigDecimal.ZERO;
	private Integer jsjbbs = 0;
	private Integer sjsjs = 0;
	
	public String getHj() {
		return hj;
	}
	public void setHj(String hj) {
		this.hj = hj;
	}
	public BigDecimal getBysr() {
		return bysr;
	}
	public void setBysr(BigDecimal bysr) {
		this.bysr = bysr;
	}
	public BigDecimal getBnsr() {
		return bnsr;
	}
	public void setBnsr(BigDecimal bnsr) {
		this.bnsr = bnsr;
	}
	public BigDecimal getZktsr() {
		return zktsr;
	}
	public void setZktsr(BigDecimal zktsr) {
		this.zktsr = zktsr;
	}
	public BigDecimal getBychsr() {
		return bychsr;
	}
	public void setBychsr(BigDecimal bychsr) {
		this.bychsr = bychsr;
	}
	public BigDecimal getBnchsr() {
		return bnchsr;
	}
	public void setBnchsr(BigDecimal bnchsr) {
		this.bnchsr = bnchsr;
	}
	public BigDecimal getZktchsr() {
		return zktchsr;
	}
	public void setZktchsr(BigDecimal zktchsr) {
		this.zktchsr = zktchsr;
	}
	
	public BigDecimal getZktck() {
		return zktck;
	}
	public void setZktck(BigDecimal zktck) {
		this.zktck = zktck;
	}
	public Integer getJsjbbs() {
		return jsjbbs;
	}
	public void setJsjbbs(Integer jsjbbs) {
		this.jsjbbs = jsjbbs;
	}
	public Integer getSjsjs() {
		return sjsjs;
	}
	public void setSjsjs(Integer sjsjs) {
		this.sjsjs = sjsjs;
	}
	public BigDecimal getByjzsr() {
		return byjzsr;
	}
	public void setByjzsr(BigDecimal byjzsr) {
		this.byjzsr = byjzsr;
	}
	public BigDecimal getBnjzsr() {
		return bnjzsr;
	}
	public void setBnjzsr(BigDecimal bnjzsr) {
		this.bnjzsr = bnjzsr;
	}
	public BigDecimal getZktjzsr() {
		return zktjzsr;
	}
	public void setZktjzsr(BigDecimal zktjzsr) {
		this.zktjzsr = zktjzsr;
	}
	public BigDecimal getByck() {
		return byck;
	}
	public void setByck(BigDecimal byck) {
		this.byck = byck;
	}
	public BigDecimal getBnck() {
		return bnck;
	}
	public void setBnck(BigDecimal bnck) {
		this.bnck = bnck;
	}
	public BigDecimal getBydk() {
		return bydk;
	}
	public void setBydk(BigDecimal bydk) {
		this.bydk = bydk;
	}
	public BigDecimal getBndk() {
		return bndk;
	}
	public void setBndk(BigDecimal bndk) {
		this.bndk = bndk;
	}
	public BigDecimal getZktdk() {
		return zktdk;
	}
	public void setZktdk(BigDecimal zktdk) {
		this.zktdk = zktdk;
	}
	
}

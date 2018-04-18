package com.power.app.entity;

import java.math.BigDecimal;

/**
 * 项目名称：power2016 <br>
 * 类名称：EtcVO <br>
 * 创建时间：2017-4-7 上午9:25:08 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class EtcVO {
	private BigDecimal etcBefore=BigDecimal.ZERO;
	private BigDecimal mtcBefore=BigDecimal.ZERO;
	private BigDecimal etcAfter=BigDecimal.ZERO;
	private BigDecimal mtcAfter=BigDecimal.ZERO;
	
	//
	BigDecimal wan = new BigDecimal(10000);
	//
	public BigDecimal getEtcBefore() {
		return etcBefore.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}
	public void setEtcBefore(BigDecimal etcBefore) {
		this.etcBefore = etcBefore;
	}
	public BigDecimal getMtcBefore() {
		return mtcBefore.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}
	public void setMtcBefore(BigDecimal mtcBefore) {
		this.mtcBefore = mtcBefore;
	}
	public BigDecimal getEtcAfter() {
		return etcAfter.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}
	public void setEtcAfter(BigDecimal etcAfter) {
		this.etcAfter = etcAfter;
	}
	public BigDecimal getMtcAfter() {
		return mtcAfter.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}
	public void setMtcAfter(BigDecimal mtcAfter) {
		this.mtcAfter = mtcAfter;
	}
}

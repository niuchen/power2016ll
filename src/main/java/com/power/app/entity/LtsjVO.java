package com.power.app.entity;

import java.math.BigDecimal;

/**
 * 项目名称：power2016 <br>
 * 类名称：LtsjVO <br>
 * 创建时间：2017-4-7 上午10:19:48 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class LtsjVO {
	private String sszd;
	private Integer ltchj=0;
	private BigDecimal ltcjehj=BigDecimal.ZERO;
	BigDecimal wan = new BigDecimal(10000);
	
	public String getSszd() {
		return sszd;
	}
	public void setSszd(String sszd) {
		this.sszd = sszd;
	}
	public Integer getLtchj() {
		return ltchj;
	}
	public void setLtchj(Integer ltchj) {
		this.ltchj = ltchj;
	}
	public BigDecimal getLtcjehj() {
		return ltcjehj.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}
	public void setLtcjehj(BigDecimal ltcjehj) {
		this.ltcjehj = ltcjehj;
	}
}

package com.power.app.entity;

import java.math.BigDecimal;

/**
 * 项目名称：power2016 <br>
 * 类名称：LlfyVO <br>
 * 创建时间：2017-4-8 上午11:41:03 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class LlfyVO {
	private String zdmc;
	private Integer crkllhj=0;
	private BigDecimal sfhj=BigDecimal.ZERO;
	public String getZdmc() {
		return zdmc;
	}
	public void setZdmc(String zdmc) {
		this.zdmc = zdmc;
	}
	public Integer getCrkllhj() {
		return crkllhj;
	}
	public void setCrkllhj(Integer crkllhj) {
		this.crkllhj = crkllhj;
	}
	public BigDecimal getSfhj() {
		return sfhj;
	}
	public void setSfhj(BigDecimal sfhj) {
		this.sfhj = sfhj;
	}
}

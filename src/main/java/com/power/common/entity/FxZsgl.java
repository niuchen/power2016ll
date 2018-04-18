package com.power.common.entity;

import java.math.BigDecimal;
import java.util.Date;

public class FxZsgl {
	 private Long xh;

	    private String sszd;

	    private String zdmc;
	    
	    private Date lrsj;
	    
	    private BigDecimal jjzs = BigDecimal.ONE;
	    
	    private BigDecimal zczs = BigDecimal.ONE;

		public Long getXh() {
			return xh;
		}

		public void setXh(Long xh) {
			this.xh = xh;
		}

		public String getSszd() {
			return sszd;
		}

		public void setSszd(String sszd) {
			this.sszd = sszd;
		}

		public String getZdmc() {
			return zdmc;
		}

		public void setZdmc(String zdmc) {
			this.zdmc = zdmc;
		}

		public Date getLrsj() {
			return lrsj;
		}

		public void setLrsj(Date lrsj) {
			this.lrsj = lrsj;
		}

		public BigDecimal getJjzs() {
			return jjzs;
		}

		public void setJjzs(BigDecimal jjzs) {
			this.jjzs = jjzs;
		}

		public BigDecimal getZczs() {
			return zczs;
		}

		public void setZczs(BigDecimal zczs) {
			this.zczs = zczs;
		}
	    
}

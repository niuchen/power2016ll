package com.power.common.entity;

import java.math.BigDecimal;
import java.util.Date;

public class FxRwgl {
	 private Long xh;

	    private String sszd;

	    private String zdmc;
	    
	    private Date lrsj;
	    
	    private BigDecimal rwz = BigDecimal.ZERO;

	    private String lryxh;

	    private String lrymc;
	    
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

		public BigDecimal getRwz() {
			return rwz;
		}

		public void setRwz(BigDecimal rwz) {
			this.rwz = rwz;
		}

		public String getLryxh() {
			return lryxh;
		}

		public void setLryxh(String lryxh) {
			this.lryxh = lryxh;
		}

		public String getLrymc() {
			return lrymc;
		}

		public void setLrymc(String lrymc) {
			this.lrymc = lrymc;
		}

}

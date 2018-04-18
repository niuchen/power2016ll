package com.power.common.entity;

import java.math.BigDecimal;
import java.util.Date;

public class FxRcsj {
    private Long xh;

    private String sszd;

    private String zdmc;

    private String zdzz;

    private Integer zt;

    private String bz;

    private Date sfrq;

    private String lryxh;

    private String lrymc;

    private Date lrsj;

    private BigDecimal sfhj = BigDecimal.ZERO;

    private BigDecimal xjhj = BigDecimal.ZERO;

  

    private BigDecimal zcsr = BigDecimal.ZERO;

    private BigDecimal jzsr = BigDecimal.ZERO;

    private BigDecimal jsk = BigDecimal.ZERO;

    private BigDecimal ck = BigDecimal.ZERO;

    private BigDecimal etccq = BigDecimal.ZERO;
    
    private BigDecimal ydzf = BigDecimal.ZERO;

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
        this.sszd = sszd == null ? null : sszd.trim();
    }

    public String getZdmc() {
        return zdmc;
    }

    public void setZdmc(String zdmc) {
        this.zdmc = zdmc == null ? null : zdmc.trim();
    }

    public String getZdzz() {
        return zdzz;
    }

    public void setZdzz(String zdzz) {
        this.zdzz = zdzz == null ? null : zdzz.trim();
    }

    public Integer getZt() {
        return zt;
    }

    public void setZt(Integer zt) {
        this.zt = zt;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz == null ? null : bz.trim();
    }

    public Date getSfrq() {
        return sfrq;
    }

    public void setSfrq(Date sfrq) {
        this.sfrq = sfrq;
    }

    public String getLryxh() {
        return lryxh;
    }

    public void setLryxh(String lryxh) {
        this.lryxh = lryxh == null ? null : lryxh.trim();
    }

    public String getLrymc() {
        return lrymc;
    }

    public void setLrymc(String lrymc) {
        this.lrymc = lrymc == null ? null : lrymc.trim();
    }

    public Date getLrsj() {
        return lrsj;
    }

    public void setLrsj(Date lrsj) {
        this.lrsj = lrsj;
    }

    public BigDecimal getSfhj() {
        return sfhj;
    }

    public void setSfhj(BigDecimal sfhj) {
        this.sfhj = sfhj;
    }

    public BigDecimal getXjhj() {
        return xjhj;
    }

    public void setXjhj(BigDecimal xjhj) {
        this.xjhj = xjhj;
    }


    public BigDecimal getZcsr() {
        return zcsr;
    }

    public void setZcsr(BigDecimal zcsr) {
        this.zcsr = zcsr;
    }

    public BigDecimal getJzsr() {
        return jzsr;
    }

    public void setJzsr(BigDecimal jzsr) {
        this.jzsr = jzsr;
    }

    public BigDecimal getJsk() {
        return jsk;
    }

    public void setJsk(BigDecimal jsk) {
        this.jsk = jsk;
    }

    public BigDecimal getCk() {
        return ck;
    }

    public void setCk(BigDecimal ck) {
        this.ck = ck;
    }

    public BigDecimal getEtccq() {
        return etccq;
    }

    public void setEtccq(BigDecimal etccq) {
        this.etccq = etccq;
    }

	public BigDecimal getYdzf() {
		return ydzf;
	}

	public void setYdzf(BigDecimal ydzf) {
		this.ydzf = ydzf;
	}
    
}
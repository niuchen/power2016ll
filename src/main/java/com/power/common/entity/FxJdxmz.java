package com.power.common.entity;

import java.math.BigDecimal;
import java.util.Date;

public class FxJdxmz {
    private Long xh;

    private String sszd;

    private String zdmc;

    private Integer zt;

    private String bz;

    private Date sfrq;

    private String lryxh;

    private String lrymc;

    private Date lrsj;

    private String mzsj;

    private Integer clr = 0;

    private Integer clc = 0;
    
    private Integer clhj = 0;

    private BigDecimal mfje = BigDecimal.ZERO;

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

    public String getMzsj() {
        return mzsj;
    }

    public void setMzsj(String mzsj) {
        this.mzsj = mzsj == null ? null : mzsj.trim();
    }

    public Integer getClr() {
        return clr;
    }

    public void setClr(Integer clr) {
        this.clr = clr;
    }

    public Integer getClc() {
        return clc;
    }

    public void setClc(Integer clc) {
        this.clc = clc;
    }

    public BigDecimal getMfje() {
        return mfje;
    }

    public void setMfje(BigDecimal mfje) {
        this.mfje = mfje;
    }

	public Integer getClhj() {
		return clhj;
	}

	public void setClhj(Integer clhj) {
		this.clhj = clhj;
	}
    
    
}
package com.power.common.entity;


import java.util.Date;

public class FxIck {
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

    private Integer jy =0;

    private Integer fk=0;

    private Integer hs=0;

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

    public Integer getJy() {
        return jy;
    }

    public void setJy(Integer jy) {
        this.jy = jy;
    }

    public Integer getFk() {
        return fk;
    }

    public void setFk(Integer fk) {
        this.fk = fk;
    }

    public Integer getHs() {
        return hs;
    }

    public void setHs(Integer hs) {
        this.hs = hs;
    }
}
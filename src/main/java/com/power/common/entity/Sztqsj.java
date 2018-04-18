package com.power.common.entity;

import java.util.Date;

public class Sztqsj {
    private Integer xh;

    private String bt;

    private String zdmc;

    private Integer zt;

    private Date lrsj;

    private String nr;

    private String lrymc;

    public Integer getXh() {
        return xh;
    }

    public void setXh(Integer xh) {
        this.xh = xh;
    }

    public String getBt() {
        return bt;
    }

    public void setBt(String bt) {
        this.bt = bt == null ? null : bt.trim();
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

    public Date getLrsj() {
        return lrsj;
    }

    public void setLrsj(Date lrsj) {
        this.lrsj = lrsj;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr == null ? null : nr.trim();
    }

    public String getLrymc() {
        return lrymc;
    }

    public void setLrymc(String lrymc) {
        this.lrymc = lrymc == null ? null : lrymc.trim();
    }
}
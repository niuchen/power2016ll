package com.power.report.entity;

import java.math.BigDecimal;

/**
 * 项目名称：power2016 <br>
 * 类名称：KhcllfyListVO <br>
 * 创建时间：2017-4-17 下午2:26:11 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class KhcllfyListVO {

	private Integer nd;
	private Integer yf;
	private String llhj;
	private String jehj;
	
	//拆分数据
	private Integer sl_kc=0;
	private Integer sl_hc=0;
	private Integer sl_mz=0;
	private BigDecimal sl_kc_je=BigDecimal.ZERO;
	private BigDecimal sl_hc_je=BigDecimal.ZERO;
	
	private Integer xnh_kc=0;
	private Integer xnh_hc=0;
	private Integer xnh_mz=0;
	private BigDecimal xnh_kc_je=BigDecimal.ZERO;
	private BigDecimal xnh_hc_je=BigDecimal.ZERO;
	
	private Integer jj_kc=0;
	private Integer jj_hc=0;
	private Integer jj_mz=0;
	private BigDecimal jj_kc_je=BigDecimal.ZERO;
	private BigDecimal jj_hc_je=BigDecimal.ZERO;
	
	private Integer jx_kc=0;
	private Integer jx_hc=0;
	private Integer jx_mz=0;
	private BigDecimal jx_kc_je=BigDecimal.ZERO;
	private BigDecimal jx_hc_je=BigDecimal.ZERO;
	
	private Integer hx_kc=0;
	private Integer hx_hc=0;
	private Integer hx_mz=0;
	private BigDecimal hx_kc_je=BigDecimal.ZERO;
	private BigDecimal hx_hc_je=BigDecimal.ZERO;
	
	private Integer js_kc=0;
	private Integer js_hc=0;
	private Integer js_mz=0;
	private BigDecimal js_kc_je=BigDecimal.ZERO;
	private BigDecimal js_hc_je=BigDecimal.ZERO;
	
	private Integer jl_kc=0;
	private Integer jl_hc=0;
	private Integer jl_mz=0;
	private BigDecimal jl_kc_je=BigDecimal.ZERO;
	private BigDecimal jl_hc_je=BigDecimal.ZERO;
	
	private Integer kc_hj=0;
	private Integer hc_hj=0;
	private Integer mz_hj=0;
	private BigDecimal kc_je_hj=BigDecimal.ZERO;
	private BigDecimal hc_je_hj=BigDecimal.ZERO;
	
	private Integer lj=0;
	private BigDecimal je=BigDecimal.ZERO;
	
	//
	
	public Integer getNd() {
		return nd;
	}
	public BigDecimal getSl_kc_je() {
		return sl_kc_je;
	}
	public void setSl_kc_je(BigDecimal sl_kc_je) {
		this.sl_kc_je = sl_kc_je;
	}
	public BigDecimal getSl_hc_je() {
		return sl_hc_je;
	}
	public void setSl_hc_je(BigDecimal sl_hc_je) {
		this.sl_hc_je = sl_hc_je;
	}
	public BigDecimal getXnh_kc_je() {
		return xnh_kc_je;
	}
	public void setXnh_kc_je(BigDecimal xnh_kc_je) {
		this.xnh_kc_je = xnh_kc_je;
	}
	public BigDecimal getXnh_hc_je() {
		return xnh_hc_je;
	}
	public void setXnh_hc_je(BigDecimal xnh_hc_je) {
		this.xnh_hc_je = xnh_hc_je;
	}
	public BigDecimal getJj_kc_je() {
		return jj_kc_je;
	}
	public void setJj_kc_je(BigDecimal jj_kc_je) {
		this.jj_kc_je = jj_kc_je;
	}
	public BigDecimal getJj_hc_je() {
		return jj_hc_je;
	}
	public void setJj_hc_je(BigDecimal jj_hc_je) {
		this.jj_hc_je = jj_hc_je;
	}
	public BigDecimal getJx_kc_je() {
		return jx_kc_je;
	}
	public void setJx_kc_je(BigDecimal jx_kc_je) {
		this.jx_kc_je = jx_kc_je;
	}
	public BigDecimal getJx_hc_je() {
		return jx_hc_je;
	}
	public void setJx_hc_je(BigDecimal jx_hc_je) {
		this.jx_hc_je = jx_hc_je;
	}
	public BigDecimal getHx_kc_je() {
		return hx_kc_je;
	}
	public void setHx_kc_je(BigDecimal hx_kc_je) {
		this.hx_kc_je = hx_kc_je;
	}
	public BigDecimal getHx_hc_je() {
		return hx_hc_je;
	}
	public void setHx_hc_je(BigDecimal hx_hc_je) {
		this.hx_hc_je = hx_hc_je;
	}
	public BigDecimal getJs_kc_je() {
		return js_kc_je;
	}
	public void setJs_kc_je(BigDecimal js_kc_je) {
		this.js_kc_je = js_kc_je;
	}
	public BigDecimal getJs_hc_je() {
		return js_hc_je;
	}
	public void setJs_hc_je(BigDecimal js_hc_je) {
		this.js_hc_je = js_hc_je;
	}
	public BigDecimal getJl_kc_je() {
		return jl_kc_je;
	}
	public void setJl_kc_je(BigDecimal jl_kc_je) {
		this.jl_kc_je = jl_kc_je;
	}
	public BigDecimal getJl_hc_je() {
		return jl_hc_je;
	}
	public void setJl_hc_je(BigDecimal jl_hc_je) {
		this.jl_hc_je = jl_hc_je;
	}
	public BigDecimal getKc_je_hj() {
		return kc_je_hj;
	}
	public void setKc_je_hj(BigDecimal kc_je_hj) {
		this.kc_je_hj = kc_je_hj;
	}
	public BigDecimal getHc_je_hj() {
		return hc_je_hj;
	}
	public void setHc_je_hj(BigDecimal hc_je_hj) {
		this.hc_je_hj = hc_je_hj;
	}
	public BigDecimal getJe() {
		return je;
	}
	public void setJe(BigDecimal je) {
		this.je = je;
	}
	public Integer getKc_hj() {
		return kc_hj;
	}
	public void setKc_hj(Integer kc_hj) {
		this.kc_hj = kc_hj;
	}
	public Integer getHc_hj() {
		return hc_hj;
	}
	public void setHc_hj(Integer hc_hj) {
		this.hc_hj = hc_hj;
	}
	public Integer getMz_hj() {
		return mz_hj;
	}
	public void setMz_hj(Integer mz_hj) {
		this.mz_hj = mz_hj;
	}
	public Integer getLj() {
		return lj;
	}
	public void setLj(Integer lj) {
		this.lj = lj;
	}
	public Integer getSl_kc() {
		return sl_kc;
	}
	public void setSl_kc(Integer sl_kc) {
		this.sl_kc = sl_kc;
	}
	public Integer getSl_hc() {
		return sl_hc;
	}
	public void setSl_hc(Integer sl_hc) {
		this.sl_hc = sl_hc;
	}
	public Integer getSl_mz() {
		return sl_mz;
	}
	public void setSl_mz(Integer sl_mz) {
		this.sl_mz = sl_mz;
	}
	public Integer getXnh_kc() {
		return xnh_kc;
	}
	public void setXnh_kc(Integer xnh_kc) {
		this.xnh_kc = xnh_kc;
	}
	public Integer getXnh_hc() {
		return xnh_hc;
	}
	public void setXnh_hc(Integer xnh_hc) {
		this.xnh_hc = xnh_hc;
	}
	public Integer getXnh_mz() {
		return xnh_mz;
	}
	public void setXnh_mz(Integer xnh_mz) {
		this.xnh_mz = xnh_mz;
	}
	public Integer getJj_kc() {
		return jj_kc;
	}
	public void setJj_kc(Integer jj_kc) {
		this.jj_kc = jj_kc;
	}
	public Integer getJj_hc() {
		return jj_hc;
	}
	public void setJj_hc(Integer jj_hc) {
		this.jj_hc = jj_hc;
	}
	public Integer getJj_mz() {
		return jj_mz;
	}
	public void setJj_mz(Integer jj_mz) {
		this.jj_mz = jj_mz;
	}
	public Integer getJx_kc() {
		return jx_kc;
	}
	public void setJx_kc(Integer jx_kc) {
		this.jx_kc = jx_kc;
	}
	public Integer getJx_hc() {
		return jx_hc;
	}
	public void setJx_hc(Integer jx_hc) {
		this.jx_hc = jx_hc;
	}
	public Integer getJx_mz() {
		return jx_mz;
	}
	public void setJx_mz(Integer jx_mz) {
		this.jx_mz = jx_mz;
	}
	public Integer getHx_kc() {
		return hx_kc;
	}
	public void setHx_kc(Integer hx_kc) {
		this.hx_kc = hx_kc;
	}
	public Integer getHx_hc() {
		return hx_hc;
	}
	public void setHx_hc(Integer hx_hc) {
		this.hx_hc = hx_hc;
	}
	public Integer getHx_mz() {
		return hx_mz;
	}
	public void setHx_mz(Integer hx_mz) {
		this.hx_mz = hx_mz;
	}
	public Integer getJs_kc() {
		return js_kc;
	}
	public void setJs_kc(Integer js_kc) {
		this.js_kc = js_kc;
	}
	public Integer getJs_hc() {
		return js_hc;
	}
	public void setJs_hc(Integer js_hc) {
		this.js_hc = js_hc;
	}
	public Integer getJs_mz() {
		return js_mz;
	}
	public void setJs_mz(Integer js_mz) {
		this.js_mz = js_mz;
	}
	public Integer getJl_kc() {
		return jl_kc;
	}
	public void setJl_kc(Integer jl_kc) {
		this.jl_kc = jl_kc;
	}
	public Integer getJl_hc() {
		return jl_hc;
	}
	public void setJl_hc(Integer jl_hc) {
		this.jl_hc = jl_hc;
	}
	public Integer getJl_mz() {
		return jl_mz;
	}
	public void setJl_mz(Integer jl_mz) {
		this.jl_mz = jl_mz;
	}
	public void setNd(Integer nd) {
		this.nd = nd;
	}
	public Integer getYf() {
		return yf;
	}
	public void setYf(Integer yf) {
		this.yf = yf;
	}
	public String getLlhj() {
		return llhj;
	}
	public void setLlhj(String llhj) {
		this.llhj = llhj;
	}
	public String getJehj() {
		return jehj;
	}
	public void setJehj(String jehj) {
		this.jehj = jehj;
	}
}

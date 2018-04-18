package com.power.app.entity;

import java.math.BigDecimal;

/**
 * 项目名称：power2016 <br>
 * 类名称：YhsjVO <br>
 * 创建时间：2017-4-7 上午11:23:22 <br>
 * 
 * @author LRF <br>
 * @version 1.0
 */
public class YhsjVO {
	private BigDecimal yjje1 = BigDecimal.ZERO;
	private BigDecimal sjje1 = BigDecimal.ZERO;
	private BigDecimal yjje2 = BigDecimal.ZERO;
	private BigDecimal sjje2 = BigDecimal.ZERO;
	private BigDecimal yjjehj = BigDecimal.ZERO;
	private BigDecimal sjjehj = BigDecimal.ZERO;
	
	BigDecimal wan = new BigDecimal(10000);

	public BigDecimal getYjje1() {
		return yjje1.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}

	public void setYjje1(BigDecimal yjje1) {
		this.yjje1 = yjje1;
	}

	public BigDecimal getSjje1() {
		return sjje1.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}

	public void setSjje1(BigDecimal sjje1) {
		this.sjje1 = sjje1;
	}

	public BigDecimal getYjje2() {
		return yjje2.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}

	public void setYjje2(BigDecimal yjje2) {
		this.yjje2 = yjje2;
	}

	public BigDecimal getSjje2() {
		return sjje2.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}

	public void setSjje2(BigDecimal sjje2) {
		this.sjje2 = sjje2;
	}

	public BigDecimal getYjjehj() {
		return yjjehj.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}

	public void setYjjehj(BigDecimal yjjehj) {
		this.yjjehj = yjjehj;
	}

	public BigDecimal getSjjehj() {
		return sjjehj.divide(wan,2,BigDecimal.ROUND_HALF_UP);
	}

	public void setSjjehj(BigDecimal sjjehj) {
		this.sjjehj = sjjehj;
	}
	
}

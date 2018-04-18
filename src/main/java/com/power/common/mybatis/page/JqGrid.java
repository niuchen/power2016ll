package com.power.common.mybatis.page;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.annotation.JSONField;


/**
 * JqGrid 表格参数
 * 项目名称：power2016 <br>
 * 类名称：JqGrid <br>
 * 创建时间：2016-9-6 下午4:28:35 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class JqGrid extends Page{
	@JSONField(serialize=false)
	private static final long serialVersionUID = 8761467177629516822L;
	private String sidx;
	private String sord;
	
	public String getSidx() {
		return sidx;
	}
	public void setSidx(String sidx) {
		this.sidx = sidx;
	}
	public String getSord() {
		return sord;
	}
	public void setSord(String sord) {
		this.sord = sord;
	}
	public JqGrid() {
        if (page < 1)
            this.page = 1;
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE, true, true);
    }
}

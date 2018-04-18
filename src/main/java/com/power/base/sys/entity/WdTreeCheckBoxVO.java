package com.power.base.sys.entity;

/**
 * 
 * 项目名称：power2016 <br>
 * 类名称：WdTreeCheckVO <br>
 * 创建时间：2016-10-26 下午3:15:44 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class WdTreeCheckBoxVO extends WdTreeVO {
	private boolean showcheck=true;
	private Integer checkstate=0;
	
	public boolean getShowcheck() {
		return showcheck;
	}
	public void setShowcheck(boolean showcheck) {
		this.showcheck = showcheck;
	}
	public Integer getCheckstate() {
		return checkstate;
	}
	public void setCheckstate(Integer checkstate) {
		this.checkstate = checkstate;
	}

}

package com.power.base.sys.entity;

import java.io.Serializable;

/**
 * 登录用户 VO
 * 项目名称：power2016 <br>
 * 类名称：AuthUserVO <br>
 * 创建时间：2016-10-31 下午5:48:55 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class AuthUserVO implements Serializable {
	private static final long serialVersionUID = 5134520047202117315L;
	private String uid;
	private String pwd;
	private String loginName;
	private String realName;
	private String deptName;
	private String deptId;
	private String orgName;
	private String orgId;
	private Integer gender;
	private Integer enabledmark;
	private String mobile;//手机
	private String token;//token 
	//
	private String icon;
	private String sign;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public Integer getEnabledmark() {
		return enabledmark;
	}
	public void setEnabledmark(Integer enabledmark) {
		this.enabledmark = enabledmark;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof AuthUserVO)){return false;}
		AuthUserVO p = (AuthUserVO)obj;
	   return this.uid == p.getUid();
	}
}
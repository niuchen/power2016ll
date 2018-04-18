package com.power.base.sys.entity;

import java.util.List;

/**
 * 项目名称：power2016 <br>
 * 类名称：PrivilegeVO <br>
 * 创建时间：2016-11-3 上午11:08:38 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class PrivilegeVO {

	private String url;
	private List<String> roles;
	private List<String> perms;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public List<String> getPerms() {
		return perms;
	}
	public void setPerms(List<String> perms) {
		this.perms = perms;
	}
	
}

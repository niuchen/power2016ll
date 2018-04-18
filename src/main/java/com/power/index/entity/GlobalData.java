package com.power.index.entity;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *  kv 
 * 项目名称：power2016 <br>
 * 类名称：KeyValue <br>
 * 创建时间：2016-10-28 上午11:08:57 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class GlobalData implements Serializable{
	private static final long serialVersionUID = 629022835220271395L;
	private String id;
	private String name;
	private String code;
	private String pid;
	private String orgid;
	
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@JSONField(serialize=false)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}

}

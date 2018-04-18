package com.power.base.file.entity;

/**
 * 项目名称：ams <br>
 * 类名称：WjDownLoadVO <br>
 * 创建时间：2014-6-23 上午10:55:13 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class DownLoadVO {

	private String name;
	private String path;
	private String ext;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
}

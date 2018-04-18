package com.power.base.sys.entity;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * wdTree 插件VO对象
 * 项目名称：power2016 <br>
 * 类名称：WdTreeVO <br>
 * 创建时间：2016-9-28 上午8:51:43 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class WdTreeVO {

	private String id;
	private String text;
	private String value;
	private String img;
	private boolean isexpand=false;
	private boolean complete=false;
	private boolean hasChildren=false;
	private String parentnodes;
	private List<WdTreeVO> ChildNodes;
	
	//
	private String sort;
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getParentnodes() {
		return parentnodes;
	}
	public void setParentnodes(String parentnodes) {
		this.parentnodes = parentnodes;
	}
	@JSONField(name="ChildNodes")
	public List<WdTreeVO> getChildNodes() {
		return ChildNodes;
	}
	public void setChildNodes(List<WdTreeVO> childNodes) {
		ChildNodes = childNodes;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	public boolean isIsexpand() {
		return isexpand;
	}
	public void setIsexpand(boolean isexpand) {
		this.isexpand = isexpand;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public boolean isHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	
}

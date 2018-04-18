package com.power.base.sys.entity;

/**
 * 权限类型 枚举
 * 项目名称：power2016 <br>
 * 类名称：AuthCategoryEnum <br>
 * 创建时间：2016-10-28 上午7:53:36 <br>
 * @author LRF <br>
 * @version 1.0
 */
public enum AuthCategoryEnum {
	DEPT("部门",1),ROLE("角色",2),DUTY("岗位",3),POST("职位",4),WORKGROUP("工作组",5),USER("用户",6);

	public static String getName(int index) {
        for (AuthCategoryEnum c : AuthCategoryEnum.values()) {
            if (c.getIndex() == index) {  
                return c.name;  
            }
        }
        return "";
    }
	
	private String name;
    private int index;
    private AuthCategoryEnum(String name, int index) {  
        this.name = name;  
        this.index = index;  
    }
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}

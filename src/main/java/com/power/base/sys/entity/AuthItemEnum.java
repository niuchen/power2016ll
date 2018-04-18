package com.power.base.sys.entity;

/**
 * 资源类别 枚举
 * 项目名称：power2016 <br>
 * 类名称：AuthItemEnum <br>
 * 创建时间：2016-10-28 上午7:53:36 <br>
 * @author LRF <br>
 * @version 1.0
 */
public enum AuthItemEnum {
	MODULE("菜单",1),BUTTON("按钮",2),COLUMN("视图",3),FORM("表单",4);

	public static String getName(int index) {
        for (AuthItemEnum c : AuthItemEnum.values()) {
            if (c.getIndex() == index) {  
                return c.name;  
            }
        }
        return "";
    }
	
	private String name;
    private int index;
    private AuthItemEnum(String name, int index) {  
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

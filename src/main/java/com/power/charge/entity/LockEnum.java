package com.power.charge.entity;


/**
 * 项目名称：power2016 <br>
 * 类名称：LockEmun <br>
 * 创建时间：2017-4-5 上午9:27:18 <br>
 * @author LRF <br>
 * @version 1.0
 */
public enum LockEnum {
	DELETE("删除",0),UNLOCK("未锁定",1),LOCK("已锁定",2);
	public static String getName(int index) {
        for (LockEnum c : LockEnum.values()) {
            if (c.getIndex() == index) {  
                return c.name;  
            }
        }
        return "";
    }
	private String name;
    private int index;
    private LockEnum(String name, int index) {  
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

package com.power.app.entity;

/**
 * 
 * 类名称：ExtReturn <br>
 * @version 1.0
 */
public class AppReturn {
    private boolean result;
    private String message;
    private Object obj;
    
    public AppReturn() {}
    public AppReturn(String msg) {
        this(false,msg,null);
    }
	public AppReturn(boolean success, String msg,Object data) {
        super();
        this.result = success;
        this.message = msg;
        this.obj = data;
    }
	public AppReturn(boolean success,Object data) {
        super();
        this.result = success;
        this.obj = data;
    }
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
}

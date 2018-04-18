/**
 * 
 */
package com.power.common.springmvc;

/**
 * 项目名称：spring3mvc <br>
 * 类名称：ExtReturn <br>
 * 创建时间：2012-2-20 上午08:13:10 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class ExtReturn {
    private boolean success;
    private Object msg;
    
    public ExtReturn(Object msg) {
        this(false,msg);
    }
    
    public ExtReturn(boolean success, Object msg) {
        super();
        this.success = success;
        this.msg = msg;
    }
    
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public Object getMsg() {
        return msg;
    }
    public void setMsg(Object msg) {
        this.msg = msg;
    }  
    
}

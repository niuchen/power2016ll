package com.power.base.log;
import java.lang.annotation.Documented;  
import java.lang.annotation.ElementType;  
import java.lang.annotation.Inherited;  
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.Target;  
  
/**
 * 系统日志注解。
 * 项目名称：power2016 <br>
 * 类名称：Log <br>
 * 创建时间：2016-9-5 上午9:53:03 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented    
@Inherited
public @interface Log {
	/**
	 * 模块信息
	 * @return
	 */
	String m();
	/**
	 * 日志类型 insert/edit/del
	 * @return
	 */
	String t() default "";
	String desc() default "no desc";//描述信息
}
package com.power.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.power.common.entity.SysDepartment;
import com.power.common.freemarker.StaticPageUtil;

/**
 * 项目名称：power2016 <br>
 * 类名称：DeptInterceptor <br>
 * 创建时间：2017-4-6 上午11:05:16 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Aspect
@Component
public class DeptInterceptor implements ServletContextAware {
	//切入点要拦截的类
	@Pointcut("execution (* com.power.base.sys.web.DeptController.newDeptSub(..)) or execution (* com.power.base.sys.web.DeptController.delDept(..))")
	private void editMethod(){} //声明一个切入点,修改/删除
	//最终通知
	@After("editMethod()")
	public void doAfter(){
		//生成静态列表，APP使用
		createDeptHtml();
	}
	
	private ServletContext ctx;
	@Override
	public void setServletContext(ServletContext arg0) {
		this.ctx = arg0;
	}
	@Autowired
	private StaticPageUtil freemarkerUtil;
	@Autowired
	private AppService appService;
	
	/**
	 * 生成部门静态列表，APP使用
	 * @param acid
	 */
	public void createDeptHtml(){
		String path = ctx.getRealPath("/WEB-INF/t/app");
		Map<String,Object> map = new HashMap<String, Object>(3);
		List<SysDepartment> deptList = appService.getAllDeptList();//站点信息
		map.put("depts", deptList);
		freemarkerUtil.createHtml1(map, path+"/dept_list.ftl", path+"/dept_list.html");
		freemarkerUtil.createHtml1(map, path+"/dept_chose.ftl", path+"/dept_chose.html");
	}
}

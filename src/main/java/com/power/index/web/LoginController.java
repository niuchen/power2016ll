package com.power.index.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.power.base.log.LogUtils;
import com.power.base.sys.service.UserUtils;
import com.power.common.shiro.auth.FormAuthenticationFilter;
import com.power.common.springmvc.CookieUtils;
import com.power.common.springmvc.ExtReturn;
import com.power.common.utils.CacheUtils;

/**
 * 登录控制
 * 项目名称：power2016 <br>
 * 类名称：LoginController <br>
 * 创建时间：2016-9-5 上午9:55:47 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Controller
public class LoginController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 管理登录
	 */
	@RequestMapping(value = {"","/login"}, method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
		String username = UserUtils.getUserName();
//		// 如果已登录，再次访问主页，则退出原账号。
//		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))){
//			CookieUtils.setCookie(response, "LOGINED", "false");
//		}
		// 如果已经登录，则跳转到管理首页
		if(username != null){
			return "redirect:index";
		}
		return "login";
	}

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Object loginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
		String principal = UserUtils.getUserName();
		// 如果已经登录，则跳转到管理首页
		if(principal != null){
//			if (!Servlets.isAjaxRequest((HttpServletRequest)request)) {// 不是ajax请求
//	            return "redirect:index";
//	        }
			return new ExtReturn(true,"index");
		}
		//处理错误信息
		String username = WebUtils.getCleanParam(request, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
		String exception = (String)request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
		String message = (String)request.getAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);
		// 非授权异常，登录失败，验证码加1。
		if (!UnauthorizedException.class.getName().equals(exception)){
			isValidateCodeLogin(username, true, false);
		}
		return new ExtReturn(message);
	}

	/**
	 */
	@RequestMapping("index")
	public String index(HttpServletRequest request, HttpServletResponse response) {
//		// 更新登录IP和时间
//		getSystemService().updateUserLoginInfo(user);
		// 记录登录日志
		LogUtils.saveLog(request, "系统登录");
		//
		String username = UserUtils.getUserName();
		// 登录成功后，验证码计算器清零
		isValidateCodeLogin(username, false, true);
		// 如果已登录，再次访问主页，则退出原账号。
//		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))){
//			String logined = CookieUtils.getCookie(request, "LOGINED");
//			if (StringUtils.isBlank(logined) || "false".equals(logined)){
//				CookieUtils.setCookie(response, "LOGINED", "true");
//			}else if (StringUtils.equals(logined, "true")){
//				UserUtils.getSubject().logout();
//				return "redirect:login";
//			}
//		}
		return "index";
	}
	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		//记录日志,处理session
		Subject subject = UserUtils.getSubject();
		subject.logout();
		logger.debug("----------logout");
		CookieUtils.setCookie(response, "power_rememberMe", "0");
		CookieUtils.setCookie(response, "power_username", "");
		CookieUtils.setCookie(response, "power_password", "");
		return "login";
	}

	
	/**
	 * 获取主题方案
	 */
	@RequestMapping(value = "/theme/{theme}")
	public String getThemeInCookie(@PathVariable String theme, HttpServletRequest request, HttpServletResponse response){
		if (StringUtils.isNotBlank(theme)){
			CookieUtils.setCookie(response, "theme", theme);
		}else{
			theme = CookieUtils.getCookie(request, "theme");
		}
		return "redirect:"+request.getParameter("url");
	}
	
	/**
	 * 是否是验证码登录
	 * @param useruame 用户名
	 * @param isFail 计数加1
	 * @param clean 计数清零
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean){
		Map<String, Integer> loginFailMap = (Map<String, Integer>)CacheUtils.get("loginFailMap");
		if (loginFailMap==null){
			loginFailMap = Maps.newHashMap();
			CacheUtils.put("loginFailMap", loginFailMap);
		}
		Integer loginFailNum = loginFailMap.get(useruame);
		if (loginFailNum==null){
			loginFailNum = 0;
		}
		if (isFail){
			loginFailNum++;
			loginFailMap.put(useruame, loginFailNum);
		}
		if (clean){
			loginFailMap.remove(useruame);
		}
		return loginFailNum >= 3;
	}
}

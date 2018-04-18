package com.power.common.shiro.auth;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Service;

import com.power.base.sys.service.UserUtils;
import com.power.base.sys.web.ValidateCodeServlet;
import com.power.common.springmvc.ResponseUtils;
import com.power.common.springmvc.Servlets;
import com.power.index.web.LoginController;

/**
 * 表单验证（包含验证码）过滤类
 */
@Service("formAuthFilter")
public class FormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {

	public static final String DEFAULT_MESSAGE_PARAM = "message";
	private String messageParam = DEFAULT_MESSAGE_PARAM;

	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
		String username = getUsername(request);
		String password = getPassword(request);
		if (password==null){
			password = "";
		}
		boolean rememberMe = isRememberMe(request);
 		return new UsernamePasswordToken(username, password, rememberMe);
	}

	public String getMessageParam() {
		return messageParam;
	}
	
	/**
	 * 验证码校验
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if(this.isLoginRequest(request, response)) {
//        	boolean rememberMe = isRememberMe(request);
//        	if(!rememberMe){
//        		String username = getUsername(request);
//        		String captcha = WebUtils.getCleanParam(request, ValidateCodeServlet.VALIDATE_CODE);//
//        		// 校验登录验证码
//        		if (!LoginController.isValidateCodeLogin(username, false, false)){
//        			Session session = UserUtils.getSession();
//        			String code = (String)session.getAttribute(ValidateCodeServlet.VALIDATE_CODE);
//        			if (captcha == null || !captcha.toUpperCase().equals(code)){
//        				request.setAttribute(getMessageParam(), "验证码错误！");
//        				return true;
//        			}
//        		}
//        	}
        }
        return super.onAccessDenied(request, response);
	}
	/**
	 * 登录成功，
	 * 处理AJAX跳转
	 */
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (!Servlets.isAjaxRequest(httpRequest)) {// 不是ajax请求
            issueSuccessRedirect(request, response);
        } else {
            ResponseUtils.renderJson(httpResponse, "{\"success\":true,\"msg\":\"index\"}");
        }
        return false;
    }
	/**
	 * 登录失败调用事件
	 */
	@Override
	protected boolean onLoginFailure(AuthenticationToken token,AuthenticationException e, ServletRequest request, ServletResponse response) {
		String className = e.getClass().getName(), message = "";
		if (UnknownAccountException.class.getName().equals(className)){
			message = "账号不存在";
		}else if(IncorrectCredentialsException.class.getName().equals(className)){
			message = "密码错误, 请重试.";
		}else if (e.getMessage() != null && StringUtils.startsWith(e.getMessage(), "msg:")){
			message = StringUtils.replace(e.getMessage(), "msg:", "");
		}else{
			message = "系统出现点问题，请稍后再试！";
			e.printStackTrace(); // 输出到控制台
		}
		request.setAttribute(getFailureKeyAttribute(), className);
        request.setAttribute(getMessageParam(), message);
        return true;
	}
	
}
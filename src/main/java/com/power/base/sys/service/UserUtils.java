package com.power.base.sys.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.PrivilegeVO;
import com.power.common.mybatis.DaoHelper;
import com.power.common.springmvc.SpringContextHolder;
import com.power.common.utils.Reflections;

/**
 * 用户工具类
 * 
 * 项目名称：power2016 <br>
 * 类名称：UserUtils <br>
 * 创建时间：2016-11-4 上午9:46:52 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class UserUtils {
	private static DaoHelper daoHelper = SpringContextHolder.getBean(DaoHelper.class);
	private static UserService userService = SpringContextHolder.getBean(UserService.class);
	
	/**
	 * 获取用户登录信息
	 * @param loginName
	 * @return
	 */
	public static AuthUserVO getAuthUserByLoginName(String loginName){
		return userService.getAuthUserByLoginName(loginName);
	}
	/**
	 * 根据用户编号获取 角色 权限
	 * @param uid 用户主键
	 * @return
	 */
	public static List<PrivilegeVO> getRolesAndPerms(String uid) {
		Map<String, String> map = new HashMap<String, String>(2);
		map.put("userid",uid);
		return daoHelper.findAll("mapper.SysAuthorizeMapper.getRolesAndPerms",map);
	}
	
	/**
	 * 获取全部角色 权限信息
	 * @return
	 */
	public static List<PrivilegeVO> getAllRoleAndPerms() {
		return daoHelper.findAll("mapper.SysAuthorizeMapper.getAllRoleAndPerms");
	}
	/**
	 * 设置对象 的操作用户
	 * createdate 创建时间 
	 * createusername 创建人
	 * createuserid 创建人编号
	 * @param obj
	 */
	public static void setObjectCreateUser(Object obj){
		AuthUserVO user = getAuthUser();
		Reflections.setFieldValue(obj, "createuserid", user.getUid());
		Reflections.setFieldValue(obj, "createusername", user.getRealName());
		Reflections.setFieldValue(obj, "createdate", new Date());
	}
	/**
	 * 设置对象 的修改用户
	 * modifydate 创建时间
	 * modifyusername 创建人
	 * modifyuserid 创建人编号
	 * @param obj
	 */
	public static void setObjectModifyUser(Object obj){
		AuthUserVO user = getAuthUser();
		Reflections.setFieldValue(obj, "modifyuserid", user.getUid());
		Reflections.setFieldValue(obj, "modifyusername", user.getRealName());
		Reflections.setFieldValue(obj, "modifydate", new Date());
	}
	/**
	 * 获取授权主要对象
	 */
	public static Subject getSubject(){
		return SecurityUtils.getSubject();
	}
	/**
	 * 获取用户信息
	 * @return
	 */
	public static AuthUserVO getAuthUser(){
		String username = getUserName();
		if (username!=null){
			//重新查询 用户信息
			AuthUserVO user = getAuthUserByLoginName(username);
			if (user != null){
				return user;
			}
			return new AuthUserVO();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new AuthUserVO();
	}
	/**
	 * 获取当前登录者对象
	 */
	public static String getUserName(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Object principal = subject.getPrincipal();
			if (principal != null){
				return (String)principal;
			}
		}catch (UnavailableSecurityManagerException e) {
			
		}catch (InvalidSessionException e){
			
		}
		return null;
	}
	
	public static Session getSession(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null){
				session = subject.getSession();
			}
			if (session != null){
				return session;
			}
		}catch (InvalidSessionException e){
		}
		return null;
	}
}

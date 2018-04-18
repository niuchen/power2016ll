package com.power.common.shiro.auth;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.PrivilegeVO;
import com.power.base.sys.service.UserService;
import com.power.base.sys.service.UserUtils;
import com.power.common.utils.Encodes;


/**
 * 系统安全认证实现类
 * 项目名称：power2016 <br>
 * 类名称：SystemAuthorizingRealm <br>
 * 创建时间：2016-11-3 下午2:11:44 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("sysAuthRealm")
public class SystemAuthorizingRealm extends AuthorizingRealm {

	private Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 认证回调函数, 登录时调用
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		// 校验用户名密码
		AuthUserVO user = UserUtils.getAuthUserByLoginName(token.getUsername());
		if (user != null) {
//			if (Global.NO.equals(user.getLoginFlag())){
//				throw new AuthenticationException("msg:该已帐号禁止登录.");
//			}
			byte[] salt = Encodes.decodeHex(user.getPwd().substring(0,16));
			return new SimpleAuthenticationInfo(token.getUsername(),user.getPwd().substring(16), ByteSource.Util.bytes(salt), getName());
		} else {
			return null;
		}
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String username = (String) getAvailablePrincipal(principals);
//		// 获取当前已登录的用户
//		if (!Global.TRUE.equals(Global.getConfig("user.multiAccountLogin"))){
//			Collection<Session> sessions = getSessionDao().getActiveSessions(true, principal, UserUtils.getSession());
//			if (sessions.size() > 0){
//				// 如果是登录进来的，则踢出已在线用户
//				if (UserUtils.getSubject().isAuthenticated()){
//					for (Session session : sessions){
//						.getSessionDao().delete(session);
//					}
//				}
//				// 记住我进来的，并且当前用户已登录，则退出当前用户提示信息。
//				else{
//					UserUtils.getSubject().logout();
//					throw new AuthenticationException("msg:账号已在其它地方登录，请重新登录。");
//				}
//			}
//		}
		AuthUserVO user = UserUtils.getAuthUserByLoginName(username);
		if (user != null) {
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			List<PrivilegeVO> pris = UserUtils.getRolesAndPerms(user.getUid());
			for(PrivilegeVO pri:pris){
	            if(pri!=null){
//	            	List<String> roles = pri.getRoles();
//	            	if(roles!=null && roles.size()>0){
//	            		info.addRole(Joiner.on(",").skipNulls().join(roles));
//	            	}
	            	List<String> perms = pri.getPerms();
	            	if(perms!=null && perms.size()>0){
	            		String s= Joiner.on(",").skipNulls().join(perms);
	            		info.addStringPermissions(perms);
	            		logger.debug(user.getRealName()+"====pris:"+s);
	            	}
	            }
	        }
			return info;
		} else {
			logger.warn("Authorization err user null");
			return null;
		}
	}
	
	/**
	 * 设定密码校验的Hash算法与迭代次数
	 */
	@PostConstruct
	public void initCredentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(UserService.HASH_ALGORITHM);
		matcher.setHashIterations(UserService.HASH_INTERATIONS);
		setCredentialsMatcher(matcher);
	}
	
	/**
	 * 清除AuthInfo缓存
	 */
	
	public void clearAuthz(){
		this.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
	}
}

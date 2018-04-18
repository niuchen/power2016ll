package com.power.base.sys.service;


import java.util.HashMap;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.KeyValue;
import com.power.common.entity.SysUser;
import com.power.common.mybatis.DaoHelper;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.utils.Encodes;
import com.power.common.utils.encrypt.Digests;

/**
 * 
 * 项目名称： power2016
 * 类名称： UserService
 * 创建时间： 2016年10月28日 下午2:47:33
 * @author WJZ
 * @version 1.0
 */
@Service("userService")
public class UserService {
	
	@Autowired
	private DaoHelper daoHelper;
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;
	
	//用户列表
	public void showUser(HashMap<String, Object> map,JqGrid grid) {
		daoHelper.findRows("mapper.SysUserMapper.showUser", map, grid);
	}
	
	//新增用户
	@CacheEvict(value="sysCache",allEntries=true)
	public void newUser(SysUser user){//,String rid,String pid){
		user.setUserid(daoHelper.findTableKeyUUID());
		user.setPassword(entryptPassword(user.getPassword()));
		daoHelper.insertSelective(user);
//		Date d = new Date();
//		if(StringUtils.isNotBlank(rid)){
//			SysObjUserRelation sobj = new SysObjUserRelation();
//			sobj.setCategory(AuthEnum.ROLE.getIndex());
//			sobj.setObjectid(rid);
//			sobj.setUserid(user.getUserid());
//			sobj.setCreatedate(d);
//			daoHelper.insert(sobj);
//		}
//		if(StringUtils.isNotBlank(pid)){
//			SysObjUserRelation sobj = new SysObjUserRelation();
//			sobj.setCategory(AuthEnum.POST.getIndex());
//			sobj.setObjectid(pid);
//			sobj.setUserid(user.getUserid());
//			sobj.setCreatedate(d);
//			daoHelper.insert(sobj);
//		}
	}
	
	//根据userid查询用户
	public SysUser getUser(String userid) {
		return daoHelper.findObject(SysUser.class, userid);
	}
	
	//编辑用户
	@CacheEvict(value={"userCache","sysCache"},key="#user.loginName",allEntries=true)
	public void editUser(SysUser user){//,String rid,String pid){
		user.setPassword(null);//编辑用户时禁止修改密码
		daoHelper.updateSelective(user);
	}
	/**
	 * 删除用户
	 * 只修改用户状态
	 * @param userid
	 */
	@CacheEvict(value={"userCache","sysCache"},key="#user.loginName",allEntries=true)
	public void delUser(String userid){
		SysUser user = new SysUser();
		user.setUserid(userid);
		user.setDeletemark(1);
		daoHelper.updateSelective(user);
	}
	
	//根据用户名查询用户
	public SysUser getUserByAccount(String account){
		return (SysUser)daoHelper.findObject("mapper.SysUserMapper.getUserByAccount", account);
	}
	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}
	/**
	 * 获取用户登录信息
	 * @param loginName
	 * @return
	 */
	@Cacheable(value="userCache",key="#loginName")
	public AuthUserVO getAuthUserByLoginName(String loginName){
		AuthUserVO user = (AuthUserVO)daoHelper.findObject("mapper.SysAuthorizeMapper.getAuthUserByLoginName",loginName);
		if (user == null){
			throw new AuthenticationException("msg:用户名不存在: " + loginName);
		}
		return user;
	}
	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}

	/**
	 * 修改用户密码，未验证 原始密码
	 * @param user
	 */
	 @CacheEvict(value={"userCache", "sysCache"}, allEntries=true)
	  public void editUserPwd(SysUser user)
	  {
	    user.setPassword(entryptPassword(user.getPassword()));
	    this.daoHelper.updateSelective(user);
	  }

	/**
	 * 获取站长信息
	 * @param deptId
	 * @return
	 */
	public List<KeyValue> getZdzzJson(String deptId) {
		return daoHelper.findAll("mapper.SysUserMapper.getZdzzJson", deptId);
	}
}

package com.power.base.sys.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.base.sys.entity.AuthCategoryEnum;
import com.power.base.sys.entity.AuthItemEnum;
import com.power.base.sys.entity.AuthUserVO;
import com.power.common.entity.SysAuthorize;
import com.power.common.entity.SysModule;
import com.power.common.entity.SysModuleButton;
import com.power.common.entity.SysUserRelation;
import com.power.common.mybatis.DaoHelper;
import com.power.common.shiro.auth.SystemAuthorizingRealm;

/**
 * 项目名称：power2016 <br>
 * 类名称：AuthService <br>
 * 创建时间：2016-10-27 上午9:46:38 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("authService")
public class AuthService{

	private static final Logger log = LoggerFactory.getLogger(AuthService.class);
	@Autowired
	private DaoHelper daoHelper;
	@Autowired
	private SystemAuthorizingRealm sysAuthRealm;
	
	private static String AUTH_PREFIX="mapper.SysAuthorizeMapper.";
	
	/** 
	 * 用户授权
	 * @param objId 对象主键
	 * @param moduleIds 菜单
	 * @param moduleButtonIds 按钮数组
	 * @param category 分类类型
	 */
	public void newUserAuthorize(String objId,String moduleIds,String moduleButtonIds,Integer category) {
		//删除原来关系
		Map<String,Object> param = new HashMap<String, Object>(3);
		param.put("category", category);
		param.put("objectid", objId);
		daoHelper.delete(AUTH_PREFIX+"delAuthorizeByObjId", param);
		log.debug("delAuthorizeByObjId");
		//保存 菜单关系/按钮关系
		String[] mIds = moduleIds.split(",");
		Date cd = new Date();
		AuthUserVO user = UserUtils.getAuthUser();
		if(mIds.length>0){
			for (int i = 0; i < mIds.length; i++) {
				SysAuthorize authorize=new SysAuthorize();
				authorize.setObjectid(objId);
				authorize.setCategory(category);//用户授权
				authorize.setItemtype(AuthItemEnum.MODULE.getIndex());
				authorize.setItemid(mIds[i]);
				
				authorize.setCreatedate(cd);
				authorize.setCreateuserid(user.getUid());
				authorize.setCreateusername(user.getRealName());
				daoHelper.insert(authorize);
			}
			String[] bIds = moduleButtonIds.split(",");
			for (int i = 0; i < bIds.length; i++) {
				SysAuthorize authorize=new SysAuthorize();
				authorize.setObjectid(objId);
				authorize.setCategory(category);//用户授权
				authorize.setItemtype(AuthItemEnum.BUTTON.getIndex());
				authorize.setItemid(bIds[i]);
				
				authorize.setCreatedate(cd);
				authorize.setCreateuserid(user.getUid());
				authorize.setCreateusername(user.getRealName());
				
				daoHelper.insert(authorize);
			}
		}
		//FIXME 清除已有的权限缓存，只清除自己
		sysAuthRealm.clearAuthz();
	}
	
	
	/**
	 * 菜单权限管理信息
	 * @param uid
	 * @return
	 */
	public List<SysModule> getModuleAuth(Map<String,Object> param) {
		return daoHelper.findAll(AUTH_PREFIX+"getModuleAuth", param);
	}

	/**
	 * 按钮权限列表
	 * @param param
	 * @return
	 */
	public List<SysModuleButton> getModuleBtnAuth(Map<String, Object> param) {
		return daoHelper.findAll(AUTH_PREFIX+"getModuleBtnAuth", param);
	}


	/**
	 * 获取角色对应 用户列表
	 * @param roleId
	 * @return 
	 */
	public List<AuthUserVO> getRoleUser(Map<String, Object> param) {
		return daoHelper.findAll("mapper.SysUserRelationMapper.getRoleUser", param);
	}


	/**
	 * 保存角色用户关系
	 * @param roleId
	 * @param userIds
	 */
	public void newRoleUser(String roleId, String userIds) {
		//删除原来关系
		Integer category = AuthCategoryEnum.ROLE.getIndex();
		Map<String,Object> param = new HashMap<String, Object>(3);
		param.put("category",category );
		param.put("objectid", roleId);
		daoHelper.delete("mapper.SysUserRelationMapper.delUserRelation", param);
		log.debug("delUserRelation");
		//保存 角色 用户关系
		String[] mIds = userIds.split(",");
		Date cd = new Date();
		AuthUserVO user = UserUtils.getAuthUser();
		for (int i = 0; i < mIds.length; i++) {
			SysUserRelation uRel=new SysUserRelation();
			uRel.setUserid(mIds[i]);
			uRel.setObjectid(roleId);
			uRel.setCategory(category);//角色用户
			uRel.setCreatedate(cd);
			uRel.setCreateuserid(user.getUid());
			uRel.setCreateusername(user.getRealName());
			daoHelper.insert(uRel);
		}
		//FIXME 清除已有的权限缓存，只清除自己
		sysAuthRealm.clearAuthz();
	}
	
}

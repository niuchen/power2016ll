package com.power.base.sys.service;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.base.sys.entity.KeyValue;
import com.power.common.entity.SysRole;
import com.power.common.entity.SysUserRelationKey;
import com.power.common.mybatis.DaoHelper;
import com.power.common.mybatis.page.JqGrid;


@Service("RoleService")
public class RoleService {
	
	
	
	@Autowired
	private DaoHelper daoHelper;
	/**
	 * 列表展示
	 * @param map
	 * @param grid分页
	 */
	public void showRoles(Map<String, Object> map,JqGrid grid){
		daoHelper.findRows("mapper.SysRoleMapper.showRoles", map, grid);
	}
	/**
	 * 新增
	 * @param role
	 * @return
	 */
	public Object newRole(SysRole role) {
		role.setRoleid(daoHelper.findTableKeyUUID());
		return daoHelper.insertSelective(role);
	}
	/**
	 * 获取被修改的信息
	 * @param id 被修改的roleid
	 * @return
	 */
	public SysRole getRole(String id) {
		
		return (SysRole)daoHelper.findObject(SysRole.class,id);
	}
	/**
	 * 修改角色信息
	 * @param role
	 * @return
	 */
	public Object editRole(SysRole role) {
	
		return daoHelper.updateSelective(role);
	}
	/**
	 * 删除角色信息
	 * @param roleid
	 * @return
	 */
	public String delRole(String roleid) {
		Object obj = daoHelper.findObject("mapper.SysRoleMapper.getRoleCanDel", roleid);
	if(obj==null){
		 throw new RuntimeException("get role error,role："+roleid);
	}
	int count =(Integer)obj;
	if(count != 0){
		 return   "该角色与用户存在关联，无法删除";
	}
	SysRole role = new SysRole();
	role.setRoleid(roleid);
	role.setDeletemark(1);
	return ""+daoHelper.updateSelective(role);
	 
	}
	public List<KeyValue> showRole(Map<String, Object> map){
		return daoHelper.findAll("mapper.SysRoleMapper.showRole", map);
	}
	public void saveMember(String roleid,String userid){
		//删除原表中的关联关系
		Object obj = daoHelper.findObject("mapper.SysUserRelationMapper.getRoleUser", roleid);
		if(obj != null){
			
			daoHelper.delete("mapper.SysUserRelationMapper.delRoleMember", roleid);
		}
		//通过，分开
		String[] uid= userid.split(",");
		String sr = uid.toString();
		//再把循环的值set进去
		SysUserRelationKey ur = new SysUserRelationKey();
		for(int i = 0; i < uid.length; i++){
			ur.setUserid(sr);
    	
 	}
		
	}
	
	
}

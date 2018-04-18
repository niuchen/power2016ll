package com.power.base.sys.web;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.power.base.sys.service.RoleService;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.SysRole;
import com.power.common.mybatis.DaoHelper;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.springmvc.ExtReturn;


@Controller
@RequestMapping("sys/role")
public class RoleController {
	private static final Logger log = LoggerFactory.getLogger(RoleController.class);
	/**
	 * 注入RoleService;DaoHelper.
	 */
	@Autowired
	private RoleService roleService;
	@Autowired
	private DaoHelper daoHelper;
	
	private String PREFIX="sys/role/";
	private String POSTPREFIX="sys/post/";
	/*
	 * 页面跳转
	 */
	@RequestMapping("index")
	public String Index(){
		return PREFIX+"index";
	}
	@RequestMapping("post")
	public String post(){
		return POSTPREFIX+"index";
	}
	/**
	 * 角色、岗位列表展示
	 */
	@RequestMapping("showRoles/{key}")
	@ResponseBody
	public JqGrid showRoles(@PathVariable("key")Integer key,
							@RequestParam(value="condition",required=false)String colname,
			                @RequestParam(value="keyword",required=false)String value,
			                JqGrid grid)throws IOException{	
		Map<String, Object> map = new HashMap<String, Object>();
		if(colname!=null){
			map.put(colname, value);
		}
		map.put("category", key);
//		AuthUserVO user = UserUtils.getAuthUser();
//		map.put("orgId", user.getOrgId());
		roleService.showRoles(map, grid);
		return grid;
	
	}
	/**
	 * 角色/岗位新增/修改页面
	 * @param id 角色为2、岗位为3
	 * @return
	 */
	@RequestMapping("newRole/{key}")
	public Object newRole(@PathVariable("key")Integer id){
		if(id==2){
			return PREFIX+"newRole";
		}else{
			return POSTPREFIX+"newpost";
		}
	}
	/**
	 * 获取被修改的信息
	 * @param id被修改信息的roleid
	 * @return
	 */
	@RequestMapping("getFormJson")
	@ResponseBody
	public Object getFormJson(@RequestParam("keyValue")String id){
		SysRole comid = roleService.getRole(id);
		return new ExtReturn(true,comid);
	}
	
	/**
	 * 角色/岗位新增/修改信息保存
	 * @param role
	 * @return
	 */
	@RequestMapping(value="newRoleSubmit",method = RequestMethod.POST)
	@ResponseBody
	public Object newRoleSubmit(SysRole role){
		role.setCreatedate(new Date());
		try{
			if(StringUtils.isNotBlank(role.getRoleid())){
				UserUtils.setObjectModifyUser(role);
				roleService.editRole(role);
			}else{
				UserUtils.setObjectCreateUser(role);
				roleService.newRole(role);
			}
			return new ExtReturn(true,"操作成功");
		}catch(Exception e){
			return new ExtReturn(false,"操作失败");
		}
	}
	/**
	 * 角色/岗位信息删除
	 * @param roleid
	 * @return
	 */
	@RequestMapping("delRole")
	@ResponseBody
	public Object delRole(@RequestParam("keyValue")String roleid){
		try {	
			String dr = roleService.delRole(roleid);
			if(dr.equals("1")){
				return new ExtReturn(true,"操作成功");
			}else{
				return new ExtReturn(false,"操作失败</br>"+dr);		
			}		
			} catch (Exception e) {
				log.error("delRole error:{}"+e);
				return new ExtReturn(false,"操作失败".getClass());
			}
		}
		
	/*	@RequestMapping("roleMember")
		public String roleMember(){
			return PREFIX+"roleMember";
		}
	
		@RequestMapping("saveMember")
		public void saveMember(@RequestParam("roleid")String roleid,
                @RequestParam("userId")String userid){
			roleService.saveMember(roleid, userid);
		}*/
	/**
	 * 角色信息封装
	 * @param orgid
	 * @return
	 */
	@RequestMapping("getRoleItem")
	@ResponseBody
	public Object getRoleItem(@RequestParam("orgid")String orgid){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgid",orgid);
		map.put("category", 2);
		return roleService.showRole(map);
	}
	/**
	 * 岗位信息封装
	 * @param orgid
	 * @return
	 */
	@RequestMapping("getPostItem")
	@ResponseBody
	public Object getPostItem(@RequestParam("orgid")String orgid){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgid",orgid);
		map.put("category", 3);
		return roleService.showRole(map);
	}
}
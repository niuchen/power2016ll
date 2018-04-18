package com.power.base.sys.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.power.base.sys.entity.AuthCategoryEnum;
import com.power.base.sys.entity.AuthItemEnum;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.WdTreeCheckBoxVO;
import com.power.base.sys.entity.WdTreeVO;
import com.power.base.sys.service.AuthService;
import com.power.base.sys.service.DeptService;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.SysDepartment;
import com.power.common.entity.SysModule;
import com.power.common.entity.SysModuleButton;
import com.power.common.springmvc.ExtReturn;

/**
 * 授权管理
 * 项目名称：power2016 <br>
 * 类名称：AuthController <br>
 * 创建时间：2016-10-27 上午9:42:27 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Controller
@RequestMapping("sys/auth")
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
	@Autowired
	private AuthService authService;
	@Autowired
	private DeptService deptService;
	private String PREFIX="sys/auth/";
	
	/**
	* 权限 设置
	* @param userid
	* @param model
	* @return
	*/
	@RequestMapping("setAuth/{category}")
	public String setAuth(@PathVariable("category")Integer category,
			@RequestParam("objId")String objId,Model model){
		model.addAttribute("category", category);
		model.addAttribute("objId", objId);
		return PREFIX+"setAuth";
	}
	
//	/**
//	 * 设置 用户权限
//	 * @param userid
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping("setUserAuth")
//	public String setUserAuth(@RequestParam("userId")Integer userid){
//		return PREFIX+"setUserAuth";
//	}
//	/**
//	 * 设置 角色权限
//	 * @param userid
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping("setRoleAuth")
//	public String setRoleAuth(@RequestParam("roleId")Integer userid){
//		return PREFIX+"setRoleAuth";
//	}
	
	
	/**
	 * 添加/修改 模块 
	 * @param module
	 * @param btnJson
	 * @param keyValue module 主键
	 * @return
	 */
	@RequestMapping("saveAuth/{category}")
	@ResponseBody
	public Object saveAuth(
			@PathVariable("category")Integer category,
			@RequestParam(value="objId",required=false)String objId,
			@RequestParam("moduleIds")String moduleIds,
			@RequestParam("moduleButtonIds")String moduleButtonIds){
		//权限分类 1部门 2角色 3岗位 4职务 5用户组 6用户
		String re = AuthCategoryEnum.getName(category);
		try {
			authService.newUserAuthorize(objId,moduleIds,moduleButtonIds,category);// 对象授权
			return new ExtReturn(true,re+"授权成功");
		} catch (Exception e) {
			log.error("saveAuth err "+e);
			return new ExtReturn(false,re+"操作失败");
		}
	}
	
	/**
	 * wdTree 树形列表
	 * @param list
	 * @param treeList
	 * @param parentId
	 */
	private void getModuleChildren(List<SysModule> list,List<WdTreeVO> treeList,final String parentId){
		List<SysModule> moduleList = Lists.newArrayList(Collections2.filter(list, new Predicate<SysModule>() {
            public boolean apply(SysModule module) {
                return module.getParentid().equals(parentId);
            }
        }));
		for (int i=0;i<moduleList.size();i++) {
			SysModule item = moduleList.get(i);
			WdTreeCheckBoxVO tree = new WdTreeCheckBoxVO();
    		//
    		List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
    		getModuleChildren(list,subTree,item.getModuleid());
    		tree.setChildNodes(subTree);
    		tree.setHasChildren(subTree.size()>0?true:false);
    		//
    		if(item.getIspublic()>0){//选中
    			tree.setCheckstate(1);
    		}
    		tree.setId(item.getModuleid());
    		tree.setValue(item.getModuleid());
    		tree.setText(item.getFullname());
    		tree.setImg(item.getIcon());
    		tree.setComplete(true);
    		tree.setIsexpand(true);
    		tree.setParentnodes(parentId);
    		//
    		treeList.add(tree);
    	}
	}
	/**
	 * 权限分配，树形
	 * @return
	 */
	@RequestMapping("getModuleTree/{category}")
	@ResponseBody
	public Object getModuleTree(
			@PathVariable("category")Integer category,
			@RequestParam(value="objId",required=false)String objId){
		//
		Map<String, Object> param = new HashMap<String, Object>(3);
		param.put("objectid", objId);
		param.put("category", category);
		//权限菜单
		List<SysModule> list = authService.getModuleAuth(param);
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		getModuleChildren(list,treeList,"0");
		return treeList;
	}
	/**
	 * getModuleBtnChildren
	 * module btn 封装
	 * @param list
	 * @param btnList
	 * @param treeList
	 * @param parentId
	 * @param param
	 */
	private void getModuleBtnCheckBox(List<SysModule> list,List<SysModuleButton> btnList,List<WdTreeVO> treeList,final String parentId,Map<String, Object> param){
		List<SysModule> moduleList = Lists.newArrayList(Collections2.filter(list, new Predicate<SysModule>() {
            public boolean apply(SysModule module) {
                return module.getParentid().equals(parentId);
            }
        }));
		for (int i=0;i<moduleList.size();i++) {
			SysModule item = moduleList.get(i);
			WdTreeCheckBoxVO tree = new WdTreeCheckBoxVO();
    		List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
    		getModuleBtnCheckBox(list,btnList,subTree,item.getModuleid(),param);
    		//获取按钮
    		final String moduleid=item.getModuleid();
    		List<SysModuleButton> btnModuleList = Lists.newArrayList(Collections2.filter(btnList, new Predicate<SysModuleButton>() {
                public boolean apply(SysModuleButton btn) {
                    return btn.getModuleid().equals(moduleid);
                }
            }));
    		if(btnModuleList.size()>0){
    			getBtnCheckBox(btnModuleList,subTree);
    		}
    		//
    		tree.setChildNodes(subTree);
    		tree.setHasChildren(subTree.size()>0?true:false);
    		//
    		if(item.getIspublic()>0){//选中
    			tree.setCheckstate(1);
    		}
    		tree.setId(item.getModuleid());
    		tree.setValue(item.getModuleid());
    		tree.setText(item.getFullname());
    		tree.setImg(item.getIcon());
    		tree.setComplete(true);
    		tree.setIsexpand(true);
    		tree.setParentnodes(parentId);
    		//
    		treeList.add(tree);
		}
	}
	/**
	 * getBtnChildren
	 * module btn 封装
	 * @param list
	 * @param treeList
	 * @param parentId
	 */
	private void getBtnCheckBox(List<SysModuleButton> list,List<WdTreeVO> treeList){
		for (int i=0;i<list.size();i++) {
			SysModuleButton item = list.get(i);
			WdTreeCheckBoxVO tree = new WdTreeCheckBoxVO();
    		tree.setHasChildren(false);
    		//
    		//判断是否选择
    		if(item.getChecked()>0){//选中
    			tree.setCheckstate(1);
    		}
    		tree.setId(item.getButtonid());
    		tree.setValue(item.getButtonid());
    		tree.setText(item.getFullname());
    		tree.setImg("fa fa-wrench "+item.getModuleid());//fa fa-wrench 文件
    		tree.setComplete(true);
    		tree.setIsexpand(true);
    		tree.setParentnodes(item.getModuleid());
    		//
    		treeList.add(tree);
		}
	}
	/**
	 * 权限分配，
	 * 获取菜单 和按钮
	 * @return
	 */
	@RequestMapping("getModuleAndBtnTree/{category}")
	@ResponseBody
	public Object getModuleBtnTree(
			@PathVariable("category")Integer category,
			@RequestParam(value="objId",required=false)String objId){
		//
		Map<String, Object> param = new HashMap<String, Object>(4);
		param.put("objectid", objId);
		param.put("category", category);
		param.put("itemtypeModule", AuthItemEnum.MODULE.getIndex());//菜单标志
		//
		Map<String,Object> reMap=Maps.newHashMap();
		//权限菜单
		List<SysModule> list = authService.getModuleAuth(param);
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		getModuleChildren(list,treeList,"0");
		reMap.put("moduleTree", treeList);
		
		param.put("itemtypeBtn", AuthItemEnum.BUTTON.getIndex());//按钮标志
		List<SysModuleButton> btnlist = authService.getModuleBtnAuth(param);
		//
		treeList = new ArrayList<WdTreeVO>();
		getModuleBtnCheckBox(list,btnlist,treeList,"0",param);
		reMap.put("moduleBtnTree", treeList);
		return reMap;
	}
	
	/**
	 * 设置角色成员
	 * @param roleId
	 * @return
	 */
	@RequestMapping("setRoleMember")
	public String setRoleMember(@RequestParam(value="roleId",required=false)String roleId){
		return PREFIX+"setRoleMember";
	}
	/**
	 * 设置角色成员
	 * @param roleId
	 * @return
	 */
	@RequestMapping("getRoleUser")
	@ResponseBody
	public Object getRoleUser(@RequestParam("roleId")String roleId){
		Map<String, Object> param = new HashMap<String, Object>(2);
		//只查询自己部门下属
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){//判断是否未选择部门
			List<SysDepartment> deptList = deptService.getAllChildren("all",user.getDeptId());
			//封装全部子部门
			StringBuffer depts = new StringBuffer();
			for (SysDepartment dept : deptList) {
				depts.append(",'").append(dept.getDepartmentid()).append("'");
			}
			param.put("depts", depts.deleteCharAt(0).toString());//删除首个逗号
		}
		param.put("roleId", roleId);
		//
		return authService.getRoleUser(param);
	}
	/**
	 *  保存角色成员关系
	 * @param roleId
	 * @param userIds
	 * @return
	 */
	@RequestMapping("saveRoleUser")
	@ResponseBody
	public Object saveRoleUser(@RequestParam("roleId")String roleId,@RequestParam("userIds")String userIds){
		try {
			authService.newRoleUser(roleId,userIds);//保存角色成员
			return new ExtReturn(true,"角色成员授权成功");
		} catch (Exception e) {
			log.error("saveRoleUser err "+e);
			return new ExtReturn(false,"角色成员操作失败");
		}
	}
	
	

}

package com.power.base.sys.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.WdTreeVO;
import com.power.base.sys.service.DeptService;
import com.power.base.sys.service.OrgService;
import com.power.base.sys.service.UserService;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.SysDepartment;
import com.power.common.entity.SysOrganize;
import com.power.common.entity.SysUser;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.springmvc.ExtReturn;

/**
 * 项目名称： power2016
 * 类名称： UserController
 * 创建时间： 2016年10月25日 下午1:20:54
 * @author WJZ
 * @version 1.0
 */
@Controller
@RequestMapping("sys/user")   
public class UserController {
	
	private static final Logger log=LoggerFactory.getLogger(UserController.class);

	
	private String prefix="sys/user/";
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		CustomDateEditor editor=new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"),true);
		binder.registerCustomEditor(Date.class,editor);
	}

	@Autowired
	private UserService userService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private DeptService deptService;
	
	/**
	 * 用户管理首页
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping("index")
	public String userIndex(HttpServletRequest req,Model model){
		
		return prefix+"index";
	}
	
	/**
	 * 用户管理首页
	 * @param colname
	 * @param value
	 * @param map
	 * @param grid
	 * @return
	 * */
	@RequestMapping("showUser")
	@ResponseBody
	public Object showUser(@RequestParam(value="condition",required=false)String colname,
			@RequestParam(value="keyword",required=false)String value,HashMap<String, Object> map,JqGrid grid) throws IOException{
		if(colname!=null){
			map.put(colname,value);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){//判断是否未选择部门
			List<SysDepartment> deptList = deptService.getAllChildren("all",user.getDeptId());
			//封装全部子部门
			StringBuffer depts = new StringBuffer();
			for (SysDepartment dept : deptList) {
				depts.append(",'").append(dept.getDepartmentid()).append("'");
			}
			map.put("depts", depts.deleteCharAt(0).toString());//删除首个逗号
		}else{
			List<SysOrganize> orgList = orgService.getOrgAllChildren(user.getOrgId());
			//封装全部子部门
			StringBuffer orgs = new StringBuffer();
			for (SysOrganize org : orgList) {
				orgs.append(",'").append(org.getOrganizeid()).append("'");
			}
			map.put("orgs", orgs.deleteCharAt(0).toString());//删除首个逗号
		}
		userService.showUser(map,grid);
		return grid;
	}
	
	/**
	 * 左侧树形列表
	 * @return
	 */
	@RequestMapping("deptTree")
	@ResponseBody
	public Object deptTree(@RequestParam(value="organizeid")String organizeid){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(organizeid, organizeid);
		AuthUserVO user = UserUtils.getAuthUser();
		List<SysDepartment> list = null;
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		//
		if(StringUtils.isNoneBlank(user.getDeptId())){//判断是否未选择部门
			list = deptService.getAllChildren(organizeid,user.getDeptId());
			if(list.size()>0){
				getCompanyDept(list,treeList,list.get(0).getParentid());//获取自己的第一个元素
			}
			//封装全部子部门
		}else{
			list = orgService.getDeptsByCompanyId(organizeid);
			getCompanyDept(list,treeList,"0");
		}
		//
		return treeList;
	}
	
	/**
	 * wdTree 树形列表
	 * 公司 / 部门   级联
	 * @param list
	 * @param treeList
	 * @param parentId
	 */
	private void getCompanyDept(List<SysDepartment> list,List<WdTreeVO> treeList,String parentId){
		WdTreeVO tree=null;
		for (int i=0;i<list.size();i++) {
			SysDepartment item = list.get(i);
			tree = new WdTreeVO();
	    	if(item.getParentid().equals(parentId)){
	    		//
	    		List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
	    		getCompanyDept(list,subTree,item.getDepartmentid());

	    		tree.setChildNodes(subTree);
	    		tree.setHasChildren(subTree.size()>0?true:false);
	    		//
	    		tree.setId(item.getDepartmentid());
    			tree.setValue(item.getDepartmentid());
    			tree.setText(item.getFullname());
    			tree.setComplete(true);
    			tree.setIsexpand(true);
    			tree.setParentnodes(parentId);
    			//
    			treeList.add(tree);
	    	}
		}
	}
	
	/**
	 * 新增用户
	 * @param req
	 * @param model
	 * @return
	 * */
	@RequestMapping("form")
	public String userForm(HttpServletRequest req,Model model){
		
		return prefix+"new";
	}
	
	/**
	 * 编辑用户获取数据
	 * @param userid
	 * @param model
	 * @return
	 * */
	@RequestMapping("getFormJson")
	@ResponseBody
	public Object editUser(@RequestParam(value="keyValue")String userid,Model model){
		SysUser user=userService.getUser(userid);
		return new ExtReturn(true, user);
	}
	
	/**
	 * 用户新增/编辑提交
	 * @param req
	 * @param user
	 * @return
	 * */
	@RequestMapping(value="userSub",method = RequestMethod.POST)
	@ResponseBody
	public Object userSub(HttpServletRequest req,SysUser user){//(@RequestParam("roleid")String rid ,@RequestParam("postid")String pid,HttpServletRequest req,SysUser user){
		try {
			if(StringUtils.isNotBlank(user.getUserid())){
				UserUtils.setObjectModifyUser(user);
				userService.editUser(user);//userService.editUser(user,rid,pid);
			}else{
				if(userService.getUserByAccount(user.getAccount())==null){
					UserUtils.setObjectCreateUser(user);
					userService.newUser(user);//userService.newUser(user,rid,pid);
				}else{
					return new ExtReturn(false,"该用户已存在！");
				}
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("newUser or editUser error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
	}
	
	/**
	 *删除用户
	 *@param userid
	 *@return
	 * */
	@RequestMapping(value="delUser",method = RequestMethod.POST)
	@ResponseBody
	public Object delUser(@RequestParam("keyValue")String userid){
		try {	
			userService.delUser(userid);
		} catch (Exception e) {
			log.error("delUser error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 * 重置密码
	 * @param userid
	 * @param model
	 * @return
	 * */
	@RequestMapping("rePass")
	public String rePass(){
		return prefix+"rePass";
	}
	/**
	 * 重置密码 保存
	 * @param user
	 * @return
	 */
	@RequestMapping("revisePwd")
	@ResponseBody
	public Object revisePwd(SysUser user){
		try {	
			userService.editUserPwd(user);
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("editUserPwd error:{}"+e);
			return new ExtReturn(false,"密码修改失败");
		}
	}
	
	/**
	 * 密码验证
	 * @param uid
	 * @param oldpassword
	 * @return
	 */
	@RequestMapping("validatePassword")
	@ResponseBody
	public Object validatePassword(@RequestParam("userid")String uid,@RequestParam("OldPassword")String oldpassword){
		SysUser user = userService.getUser(uid);
		String pwd = user.getPassword();
		if(userService.validatePassword(oldpassword, pwd)){
			return new ExtReturn(true,"验证成功");
		}else{
			return new ExtReturn(false,"验证失败");
		}
	}
	//
	/**
	 * 获取站长信息
	 * @param orgid
	 * @return
	 */
	@RequestMapping("getZdzzJson")
	@ResponseBody
	public Object getZdzzJson(){
		AuthUserVO usr = UserUtils.getAuthUser();
		return userService.getZdzzJson(usr.getDeptId());
	}
}

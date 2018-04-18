package com.power.base.sys.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.power.base.sys.entity.WdTreeVO;
import com.power.base.sys.service.OrgService;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.SysOrganize;
import com.power.common.entity.SysDepartment;
import com.power.common.entity.SysUser;
import com.power.common.springmvc.ExtReturn;
import com.power.common.springmvc.ResponseUtils;

/**
 * 组织机构管理
 * 项目名称：power2016 <br>
 * 类名称：OrgController <br>
 * 创建时间：2016-10-12 上午09:30:45 <br>
 * @author WJZ <br>
 * @version 1.0
 */
@Controller
@RequestMapping("sys/org")   
public class OrgController {
	
	private static final Logger log=LoggerFactory.getLogger(OrgController.class);
	
	private String prefix="sys/org/";
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		CustomDateEditor editor=new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"),true);
		binder.registerCustomEditor(Date.class,editor);
	}
	
	@Autowired
	private OrgService orgService;

	/**
	 * 组织机构首页
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping("index")
	public String orgIndex(HttpServletRequest req,Model model){
		
		return prefix+"index";
	}
	
	/**
	 * 组织机构列表
	 * @param clname
	 * @param value
	 * @param rsp
	 * @return
	 */
	@RequestMapping("showCompany")
//	@ResponseBody
	public void showCompany(@RequestParam(value="condition",required=false)String colname,
			@RequestParam(value="keyword",required=false)String value,HttpServletResponse rsp) throws IOException{
		HashMap<String, Object> map = new HashMap<String, Object>();
		if(colname!=null){
			map.put(colname,value);
		}
		List<SysOrganize> list = orgService.showCompany(map);
		StringBuffer sb = new StringBuffer();
        if(colname!=null){
        	sb.append("{ \"rows\":[ ");
        	for (int i=0;i<list.size();i++) {
        		if(i!=0){
        			sb.append(",");
        		}
        		SysOrganize comp = list.get(i);
                StringBuffer strJson = new StringBuffer(JSON.toJSONString(comp));
                strJson = strJson.insert(1, "\"level\":1,");//层级关系
                strJson = strJson.insert(1, "\"isLeaf\":true,");//是否叶子
                strJson = strJson.insert(1, "\"expanded\":true,");//默认全部展开
                sb.append(strJson);
            }
        	sb.append("]}");
        }else{
        	sb.append("{ \"rows\": [");
        	sb.append(TreeGridJson(list, -1,"0"));
        	sb.append("]}");
        }
        ResponseUtils.renderJson(rsp, sb.toString());
	}
	
	/**
	 * 树形Grid 封装.
	 * @param list
	 * @param index
	 * @param parentId
	 * @return
	 */
    public String TreeGridJson(List<SysOrganize> list, int index, String parentId){
        StringBuilder sb = new StringBuilder();
        List<SysOrganize> ChildNodeList=new ArrayList<SysOrganize>();
        for (int i=0;i<list.size();i++) {//只过滤父级元素
    		SysOrganize item = list.get(i);
	    	if(item.getParentid().equals(parentId)){
	    		ChildNodeList.add(item);
	    	}
	    }
        if (ChildNodeList.size() > 0) { index++;}
        for (SysOrganize SysOrganize : ChildNodeList) {
            StringBuffer strJson = new StringBuffer(JSON.toJSONString(SysOrganize));
            strJson = strJson.insert(1, "\"level\":" + index + ",");//层级关系
            boolean leaf = true;
            for (SysOrganize sys : list) {//判断是否有下级
				if(sys.getParentid().equals(SysOrganize.getOrganizeid())){
					leaf = false;
					break;
				}
			}
            strJson = strJson.insert(1, "\"isLeaf\":" +leaf+ ",");//是否叶子
            strJson = strJson.insert(1, "\"expanded\":true,");//默认全部展开
            sb.append(strJson);
            sb.append(TreeGridJson(list, index, SysOrganize.getOrganizeid()));
        }
        return sb.toString().replaceAll("\\}\\{", "},{");
    }
    /**
	 * 左侧树形列表
	 * @return
	 */
	@RequestMapping("orgTree")
	@ResponseBody
	public Object treeJson(){
		List<SysOrganize> list = orgService.showCompany(null);
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		getChildren(list,treeList,"0");
		return treeList;
	}
    
    /**
	 * wdTree 树形列表
	 * @param list
	 * @param treeList
	 * @param parentId
	 */
	private void getChildren(List<SysOrganize> list,List<WdTreeVO> treeList,final String parentId){
		List<SysOrganize> parentList = Lists.newArrayList(Collections2.filter(list, new Predicate<SysOrganize>() {
            public boolean apply(SysOrganize compy) {
                return compy.getParentid().equals(parentId);
            }
        }));
		for (SysOrganize item : parentList) {
			WdTreeVO tree = new WdTreeVO();
			List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
			getChildren(list,subTree,item.getOrganizeid());
			tree.setChildNodes(subTree);
			tree.setHasChildren(subTree.size()>0?true:false);
			//
			tree.setId(item.getOrganizeid());
			tree.setValue(item.getOrganizeid());
			tree.setText(item.getFullname());
			tree.setComplete(true);
			tree.setIsexpand(true);
			tree.setParentnodes(parentId);
			//
			treeList.add(tree);
		}
	}
	
	/**
	 * 左侧树形列表
	 * @return
	 */
	@RequestMapping("zrrTree")
	@ResponseBody
	public Object zrrTree(){
		List<SysOrganize> list = orgService.showCompany(null);
		List<SysDepartment> dlist = orgService.getAllDepts();
		List<SysUser> ulist = orgService.getAllUsers();
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		getCompanyDept(list,dlist,ulist,treeList,"0");
		return treeList;
	}
	
	/**
	 * wdTree 树形列表
	 * 公司 /部门 /人员 级联
	 * @param list
	 * @param treeList
	 * @param parentId
	 */
	private void getCompanyDept(List<SysOrganize> list,List<SysDepartment> dlist,List<SysUser> ulist,List<WdTreeVO> treeList,final String parentId){
		WdTreeVO tree=null;
		List<SysOrganize> parentList = Lists.newArrayList(Collections2.filter(list, new Predicate<SysOrganize>() {
            public boolean apply(SysOrganize compy) {
                return compy.getParentid().equals(parentId);
            }
        }));
		for (int i=0;i<parentList.size();i++) {
			SysOrganize item = parentList.get(i);
			tree = new WdTreeVO();
    		List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
    		getCompanyDept(list,dlist,ulist,subTree,item.getOrganizeid());
    		//获取部门列表
    		final String cpid=item.getOrganizeid();
    		List<SysDepartment> deptList = Lists.newArrayList(Collections2.filter(dlist, new Predicate<SysDepartment>() {
                public boolean apply(SysDepartment dept) {
                    return dept.getOrganizeid().equals(cpid);
                }
            }));
    		if(deptList.size()>0){
    			getDeptChildren(deptList,ulist,subTree,"0");
    		}
    		if(subTree.size()>0){
    			tree.setChildNodes(subTree);
    			tree.setHasChildren(subTree.size()>0?true:false);
    			tree.setId(item.getOrganizeid());
    			tree.setValue(item.getOrganizeid());
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
	 * 部门/人员树 关联
	 * @param list
	 * @param treeList
	 * @param parentId
	 */
	private void getDeptChildren(List<SysDepartment> list,List<SysUser> ulist,List<WdTreeVO> treeList,final String parentId){
		WdTreeVO tree=null;
		List<SysDepartment> parentList = Lists.newArrayList(Collections2.filter(list, new Predicate<SysDepartment>() {
            public boolean apply(SysDepartment compy) {
                return compy.getParentid().equals(parentId);
            }
        }));
		for (int i=0;i<parentList.size();i++) {
			SysDepartment item = parentList.get(i);
			tree = new WdTreeVO();
    		List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
    		getDeptChildren(list,ulist,subTree,item.getDepartmentid());
    		//获取用户列表
    		final String dpid=item.getDepartmentid();
    		List<SysUser> userList = Lists.newArrayList(Collections2.filter(ulist, new Predicate<SysUser>() {
                public boolean apply(SysUser user) {
                    return user.getDepartmentid().equals(dpid);
                }
            }));	
    		if( userList.size()>0){
    			getUsers(userList,subTree);
    		}
    		if(subTree.size()>0){
    			tree.setChildNodes(subTree);
    			tree.setHasChildren(subTree.size()>0?true:false);
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
	 * 用户信息封装
	 * @param list
	 * @param treeList
	 */
	private void getUsers(List<SysUser> list,List<WdTreeVO> treeList){
		WdTreeVO tree=null;
		for (int i=0;i<list.size();i++) {
			SysUser item = list.get(i);
			tree = new WdTreeVO();
    		//
//	    		tree.setChildNodes();
    		tree.setHasChildren(false);
    		//
    		tree.setImg("fa fa-user");
    		//
    		tree.setId(item.getUserid());
    		tree.setValue(item.getUserid());
    		tree.setText(item.getRealname());
    		tree.setComplete(true);
    		tree.setIsexpand(true);
    		tree.setParentnodes(item.getDepartmentid());
    		//
    		treeList.add(tree);
		}
	}
	
	/**
	 * 新增/编辑组织机构
	 * @param req
	 * @param model
	 * @return
     */
	@RequestMapping("form")
	public String orgForm(HttpServletRequest req,Model model){
		
		return prefix+"new";
	}
	
	/**
	 *编辑组织机构获取数据
	 *@param comid
	 *@param model
	 *@return
	 */
	@RequestMapping("getFormJson")
	@ResponseBody
	public Object editOrg(@RequestParam(value="keyValue")String comid,Model model){
		SysOrganize comp=orgService.getOrg(comid);
		return new ExtReturn(true, comp);
	}
	
	/**
	 *组织机构新增/编辑提交
	 *@param req
	 *@param comp
	 *@return
	 */
	@RequestMapping(value="OrgSub",method = RequestMethod.POST)
	@ResponseBody
	public Object OrgSub(HttpServletRequest req,SysOrganize comp){
		try {
			if(StringUtils.isNotBlank(comp.getOrganizeid())){
				UserUtils.setObjectModifyUser(comp);
				orgService.editOrg(comp);
			}else{
//				AuthUserVO user = new AuthUserVO();
//				comp.setCreateuserid(user.getUid());
//				comp.setCreateusername(user.getRealName());
				UserUtils.setObjectCreateUser(comp);
				orgService.newOrg(comp);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("newOrg or editOrg error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
	}

	/**
	 *删除组织机构
	 *@param comid
	 *@return
	 */
	@RequestMapping(value="delOrg",method = RequestMethod.POST)
	@ResponseBody
	public Object delOrg(@RequestParam("keyValue")String comid){
		try {	
			orgService.delOrg(comid);
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("delOrg error:{}"+e);
			return new ExtReturn(false,e.getMessage());
		}
	}
}

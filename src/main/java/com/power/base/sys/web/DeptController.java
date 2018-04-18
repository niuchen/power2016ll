package com.power.base.sys.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.KeyValue;
import com.power.base.sys.entity.WdTreeVO;
import com.power.base.sys.service.DeptService;
import com.power.base.sys.service.OrgService;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.SysDepartment;
import com.power.common.entity.SysOrganize;
import com.power.common.springmvc.ExtReturn;
import com.power.common.springmvc.ResponseUtils;

/**
 * 
 * 项目名称： power2016
 * 类名称： DeptController
 * 创建时间： 2016年10月25日 下午1:03:20
 * @author WJZ
 * @version 1.0
 */
@Controller
@RequestMapping("sys/dept")  
public class DeptController {
	
	private static final Logger log=LoggerFactory.getLogger(DeptController.class);

	
	private String prefix="sys/dept/";
	
	@Autowired
	private DeptService deptService;
	@Autowired
	private OrgService orgService;

	/**
	 * 部门管理首页
	 * @param req
	 * @param model
	 * @return
	 * */
	@RequestMapping("index")
	public String deptIndex(HttpServletRequest req,Model model){
		
		return prefix+"index";
	}
	
	/**
	 * 部门列表
	 * @param colname
	 * @param value
	 * @param map
	 * @return
	 * */
	@RequestMapping("showDept")
	@ResponseBody
	public void showDept(@RequestParam(value="condition",required=false)String colname,@RequestParam(value="keyword",required=false)String value,HashMap<String, Object> map,HttpServletResponse rsp){
		if(colname!=null){
			map.put(colname,value);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("{ \"rows\": [");
		//
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){//判断是否未选择部门
			List<SysDepartment> deptList = deptService.getAllChildren(user.getOrgId(),user.getDeptId());
			//封装全部子部门
			StringBuffer depts = new StringBuffer();
			for (SysDepartment dept : deptList) {
				depts.append(",'").append(dept.getDepartmentid()).append("'");
			}
			map.put("depts", depts.deleteCharAt(0).toString());//删除首个逗号
			//
			List<SysDepartment> list = deptService.showDept(map);
			if(list.size()>0){
				sb.append(TreeGridJson(list, -1,list.get(0).getParentid()));
			}
		}else{
			List<SysOrganize> orgList = orgService.getOrgAllChildren(user.getOrgId());
			//封装全部子部门
			StringBuffer orgs = new StringBuffer();
			for (SysOrganize org : orgList) {
				orgs.append(",'").append(org.getOrganizeid()).append("'");
			}
			map.put("orgs", orgs.deleteCharAt(0).toString());//删除首个逗号
			//
			List<SysDepartment> list = deptService.showDept(map);
	        sb.append(TreeGridJson(list, -1,"0"));
		}
        sb.append("]}");
        ResponseUtils.renderJson(rsp, sb.toString());
	}
	
	/**
	 * 树形Grid 封装.
	 * @param list
	 * @param index
	 * @param parentId
	 * @return
	 */
    public String TreeGridJson(List<SysDepartment> list, int index, String parentId){
        StringBuilder sb = new StringBuilder();
        List<SysDepartment> ChildNodeList=new ArrayList<SysDepartment>();
        for (int i=0;i<list.size();i++) {//只过滤父级元素
    		SysDepartment item = list.get(i);
	    	if(item.getParentid().equals(parentId)){
	    		ChildNodeList.add(item);
	    	}
	    }
        if (ChildNodeList.size() > 0) { index++;}
        for (SysDepartment department : ChildNodeList) {
            StringBuffer strJson = new StringBuffer(JSON.toJSONString(department));
            strJson = strJson.insert(1, "\"level\":" + index + ",");//层级关系
            boolean leaf = true;
            for (SysDepartment sys : list) {//判断是否有下级
				if(sys.getParentid().equals(department.getDepartmentid())){
					leaf = false;
					break;
				}
			}
            strJson = strJson.insert(1, "\"isLeaf\":" +leaf+ ",");//是否叶子
            strJson = strJson.insert(1, "\"expanded\":true,");//默认全部展开
            sb.append(strJson);
            sb.append(TreeGridJson(list, index, department.getDepartmentid()));
        }
        return sb.toString().replaceAll("\\}\\{", "},{");
    }
	/**
	 * 左侧树形列表
	 * 当前组织机构下面的所有信息
	 * @return
	 */
	@RequestMapping("deptTree")
	@ResponseBody
	public Object deptTree(@RequestParam(value="showParent",required=false)Integer showParent){
		AuthUserVO user = UserUtils.getAuthUser();
		//只显示自己 组织机构下属的信息
		List<WdTreeVO> reTree = new ArrayList<WdTreeVO>();
		//顶级用户权限控制
		if(StringUtils.isBlank(user.getDeptId())){
//			List<SysOrganize> list = orgService.showCompany(null);
			List<SysOrganize> orgList = orgService.getOrgAllChildren(user.getOrgId());
			if(orgList.size()>0){
				getCompanyDept(orgList,reTree,orgList.get(0).getParentid());
			}
		}else{
			if(showParent!=null){
				SysOrganize item = orgService.getOrg(user.getOrgId());
				//部门信息列表
				List<SysDepartment> deptList = deptService.getAllChildren(user.getOrgId(), user.getDeptId());
				//
				List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
				if(deptList.size()>0){
					getDeptChildren(deptList,subTree,deptList.get(0).getParentid());
				}
				WdTreeVO tree = new WdTreeVO();
				tree.setChildNodes(subTree);
				tree.setHasChildren(subTree.size()>0?true:false);
				//
				tree.setId(item.getOrganizeid());
				tree.setValue(item.getOrganizeid());
				tree.setText(item.getFullname());
				tree.setComplete(true);
				tree.setIsexpand(true);
				tree.setParentnodes("0");
				tree.setSort("org");//
				//
				reTree.add(tree);
			}else{//不显示父节点
				List<SysDepartment> deptList = deptService.getAllChildren(user.getOrgId(), user.getDeptId());
				//
				if(deptList.size()>0){
					getDeptChildren(deptList,reTree,deptList.get(0).getParentid());
				}
			}
		}
		//
		return reTree;
	}
	
	/**
	 * wdTree 树形列表
	 * 公司 / 部门 /人员 级联
	 * @param list
	 * @param treeList
	 * @param parentId
	 */
	private void getCompanyDept(List<SysOrganize> list,List<WdTreeVO> treeList,String parentId){
		WdTreeVO tree=null;
		for (int i=0;i<list.size();i++) {
			SysOrganize item = list.get(i);
			tree = new WdTreeVO();
	    	if(item.getParentid().equals(parentId)){
	    		//
	    		List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
	    		getCompanyDept(list,subTree,item.getOrganizeid());
	    		//部门信息列表
	    		List<SysDepartment> deptList = deptService.getAllChildren(item.getOrganizeid(),"all");
	    		//
	    		if(deptList.size()>0){
	    			getDeptChildren(deptList,subTree,"0");//
	    		}
	    		tree.setChildNodes(subTree);
	    		tree.setHasChildren(subTree.size()>0?true:false);
	    		//
	    		tree.setId(item.getOrganizeid());
    			tree.setValue(item.getOrganizeid());
    			tree.setText(item.getFullname());
    			tree.setComplete(true);
    			tree.setIsexpand(true);
    			tree.setParentnodes(parentId);
    			tree.setSort("org");//
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
	private void getDeptChildren(List<SysDepartment> list,List<WdTreeVO> treeList,String parentId){
		WdTreeVO tree=null;
		for (int i=0;i<list.size();i++) {
			SysDepartment item = list.get(i);
			tree = new WdTreeVO();
	    	if(item.getParentid().equals(parentId)){
	    		//
	    		List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
	    		getDeptChildren(list,subTree,item.getDepartmentid());
    		    //
    			tree.setChildNodes(subTree);
    			tree.setHasChildren(subTree.size()>0?true:false);
    			//
    			tree.setId(item.getDepartmentid());
    			tree.setValue(item.getDepartmentid());
    			tree.setText(item.getFullname());
    			tree.setComplete(true);
    			tree.setIsexpand(true);
    			tree.setParentnodes(parentId);
    			tree.setSort("dept");
    			//
    			treeList.add(tree);
	    		
	    	}
		}
	}
	/**
	 * 新增/编辑部门
	 * @param req
	 * @param model
	 * @return
	 * */
	@RequestMapping("form")
	public String deptForm(HttpServletRequest req,Model model){
		
		return prefix+"new";
	}
	
	/**
	 * 编辑部门获取数据
	 * @param deptid
	 * @param model
	 * @return
	 * */
	@RequestMapping("getFormJson")
	@ResponseBody
	public Object editDept(@RequestParam(value="keyValue")String deptid,Model model){
		SysDepartment dept = deptService.getDept(deptid);
		return new ExtReturn(true, dept);
	}
	/**
	 * 部门新增/编辑提交
	 * @param req
	 * @param dept
	 * @return
	 * */
	@RequestMapping(value="deptSub",method = RequestMethod.POST)
	@ResponseBody
	public Object newDeptSub(HttpServletRequest req,SysDepartment dept){
		try {
			if (StringUtils.isNotBlank(dept.getDepartmentid())) {
				UserUtils.setObjectModifyUser(dept);
				deptService.editDept(dept);
			} else {
				UserUtils.setObjectCreateUser(dept);
				deptService.newDept(dept);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("newDept or editDept error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
	}

	/**
	 * 删除部门
	 * @param deptid
	 * @return
	 * */
	@RequestMapping(value="delDept",method = RequestMethod.POST)
	@ResponseBody
	public Object delDept(@RequestParam("keyValue")String deptid){
		try {	
			deptService.delDept(deptid);
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("delDept error:{}"+e);
			return new ExtReturn(false,e.getMessage());
		}
	}
	////////
	/**
	 * 获取出下属路段信息
	 * @param orgid
	 * @return
	 */
	@RequestMapping("getZxLdJson")
	@ResponseBody
	public Object getZxLdJson(){
		AuthUserVO usr = UserUtils.getAuthUser();
		Map<String, Object> param = new HashMap<String, Object>();
	//	param.put("orgId", usr.getOrgId());//机构编号
		param.put("pId","0");
		List<KeyValue> reList = new ArrayList<KeyValue>();
		List<SysDepartment> deptList = deptService.showDept(param);
		for (SysDepartment dept : deptList) {
			KeyValue kv = new KeyValue();
			kv.setId(dept.getDepartmentid());
			kv.setName(dept.getFullname());
			reList.add(kv);
		}
		return reList;
	}
	
	/**
	 * 获取出下属路段信息
	 * @param orgid
	 * @return
	 */
	@RequestMapping("getZLdJson")
	@ResponseBody
	public Object getZLdJson(){
		AuthUserVO usr = UserUtils.getAuthUser();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("deptId",usr.getDeptId());
		List<SysDepartment> depts = deptService.showPid(param);
		for (SysDepartment de : depts) {
			param.put("depts","'"+ de.getParentid()+"'");//机构编号
		}
		param.put("pId","0");
		List<KeyValue> reList = new ArrayList<KeyValue>();
		List<SysDepartment> deptList = deptService.showDept(param);
		for (SysDepartment dept : deptList) {
			KeyValue kv = new KeyValue();
			kv.setId(dept.getDepartmentid());
			kv.setName(dept.getFullname());
			reList.add(kv);
		}
		return reList;
	}
	
	
}

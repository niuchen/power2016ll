package com.power.index.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.service.DeptService;
import com.power.base.sys.service.OrgService;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.SysDepartment;
import com.power.common.entity.SysDict;
import com.power.common.entity.SysModule;
import com.power.common.entity.SysModuleButton;
import com.power.common.entity.SysOrganize;
import com.power.common.entity.SysUser;
import com.power.common.mybatis.DaoHelper;
import com.power.index.entity.GlobalData;

/**
 * 项目名称：power2016 <br>
 * 类名称：IndexService <br>
 * 创建时间：2016-10-28 上午9:14:29 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("indexService")
public class IndexService {
	@Autowired
	private DaoHelper daoHelper;
	@Autowired
	private OrgService orgService;
	@Autowired
	private DeptService deptService;

	/**
	 * 所有菜单列表
	 * @return
	 */
	public List<SysModule> getAuthMenu(String uid) {
		return daoHelper.findAll("mapper.SysModuleMapper.getAuthMenu",uid);
	}

	/**
	 * 全局参数，公司列表
	 * @return
	 */
	public List<GlobalData> getOrg() {
		List<SysOrganize> orgList = orgService.getOrgAllChildren("all");
		List<GlobalData> orgData = Lists.newArrayList();
		for (SysOrganize org : orgList) {
			GlobalData vo = new GlobalData();
			vo.setId(org.getOrganizeid());
			vo.setName(org.getFullname());
			vo.setCode(vo.getCode());
			orgData.add(vo);
		}
		return orgData;
	}
	
	/**
	 * 全局参数，部门信息列表
	 * @return
	 */
	public List<GlobalData> getDept() {
		//全部部门信息
		List<SysDepartment> allList = deptService.getAllChildren("all", "all");
		List<GlobalData> deptList = Lists.newArrayList();
		for (SysDepartment dept : allList) {
			GlobalData vo = new GlobalData();
			vo.setId(dept.getDepartmentid());
			vo.setName(dept.getFullname());
			vo.setCode(dept.getCode());
			vo.setOrgid(dept.getOrganizeid());
			vo.setPid(dept.getParentid());
			deptList.add(vo);
		}
		return deptList;
	}

	/**
	 * 全局 字典
	 * @return
	 */
	@Cacheable(value="sysCache",key="#flag")
	public List<SysDict> getDict(String flag) {
		return daoHelper.findAll("mapper.SysDictMapper.getDict");
	}

	/**
	 * 全局 用户
	 * @return
	 */
	@Cacheable(value="sysCache",key="#flag")
	public List<SysUser> getUser(String flag) {
		return daoHelper.findAll("mapper.SysUserMapper.getUser");
	}
	/**
	 *  全局 按钮
	 * @param uid 
	 * @return
	 */
	public List<SysModuleButton> getBtn(String uid) {
		return daoHelper.findAll("mapper.SysModuleButtonMapper.getBtn",uid);
	}
	
	// //////////////////////////////
	// 部门列表信息封装
	// //////////////////////////////
	/**
	 * 当前登录用户 机构列表/站点列表 处理中心/下属部门
	 * @param user
	 *            当前登录用户对象
	 * @param param
	 *            put(orgs,...) <br>
	 *            put(depts,...)
	 */
	public void setDeptChildParam(AuthUserVO user, Map<String, Object> param) {
		List<SysDepartment> deptList = Lists.newArrayList();// 子部门
		if (user.getDeptId() == null && user.getEnabledmark() == 102002) {// 102002管理处
			param.put("title", user.getOrgName());//选择机构
			param.put("orgNature",user.getEnabledmark());//中心
			return;
		} else if (user.getDeptId() == null && user.getEnabledmark() == 102001) {// 102001中心
		// 获取下属单位
			List<SysOrganize> orgList = orgService.getOrgAllChildren(user.getOrgId());
			// 封装全部子部门
			StringBuffer orgs = new StringBuffer();
			for (SysOrganize org : orgList) {// 直接计算
				orgs.append(",'").append(org.getOrganizeid()).append("'");
				deptList.addAll(deptService.getAllChildren(org.getOrganizeid(), "all"));// 全部子部门
			}
			param.put("orgs", orgs.deleteCharAt(0).toString());// 删除首个逗号
			if(orgList.size()>0){
				param.put("title", orgList.get(0).getFullname());//选择机构
				param.put("orgNature", orgList.get(0).getNature());//
			}
			//
		} else if (StringUtils.isNoneBlank(user.getDeptId())) {
			deptList = deptService.getAllChildren(user.getOrgId(), user.getDeptId());
		}
		// 封装全部子部门
		StringBuffer depts = new StringBuffer();
		for (SysDepartment dept : deptList) {
			depts.append(",'").append(dept.getDepartmentid()).append("'");
		}
		param.put("depts", depts.deleteCharAt(0).toString());// 删除首个逗号
		if(deptList.size()>0 && param.get("title")==null){
			param.put("title", deptList.get(0).getFullname());//选择机构
			param.put("orgNature", deptList.get(0).getNature());//
		}
	}

	/**
	 * 页面选择机构/站点 或者无参数封装
	 * @param orgId
	 *            页面选择 组织机构
	 * @param deptId
	 *            页面选择 部门
	 * @param param
	 *            查询参数，param put(orgs,...) <br>
	 *            put(depts,...)<br>
	 *            put(title,...)<br>
	 *            put(orgNature,...) 机构性质
	 */
	public void setOrgDeptParam(String orgId, String deptId,Map<String, Object> param) {
		AuthUserVO user = UserUtils.getAuthUser();
		List<SysDepartment> deptList = Lists.newArrayList();// 子部门
		if (StringUtils.isNotBlank(orgId)) {// 前端选择中心查询
			List<SysOrganize> orgList = orgService.getOrgAllChildren(orgId);
			// 封装全部子部门
			StringBuffer orgs = new StringBuffer();
			for (SysOrganize org : orgList) {
				orgs.append(",'").append(org.getOrganizeid()).append("'");
				deptList.addAll(deptService.getAllChildren(org.getOrganizeid(), "all"));// 全部子部门
			}
			param.put("orgs", orgs.deleteCharAt(0).toString());// 删除首个逗号
			if(orgList.size()>0){
				param.put("title", orgList.get(0).getFullname());//选择机构
				param.put("orgNature",orgList.get(0).getNature());//中心
			}
		} else if (StringUtils.isNotBlank(deptId)) {// 前端选择部门查询
			deptList = deptService.getAllChildren("all", deptId);
		} else {// 默认值
			setDeptChildParam(user, param);
			return;// 封装完成
		}
		// 封装全部子部门
		StringBuffer depts = new StringBuffer();
		for (SysDepartment dept : deptList) {
			depts.append(",'").append(dept.getDepartmentid()).append("'");
		}
		param.put("depts", depts.deleteCharAt(0).toString());// 删除首个逗号
		if(deptList.size()>0 && param.get("title")==null){
			param.put("title", deptList.get(0).getFullname());//选择机构
			param.put("orgNature",deptList.get(0).getNature());//中心
		}
	}
	
}

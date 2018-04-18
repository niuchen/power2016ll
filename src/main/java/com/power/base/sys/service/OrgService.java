package com.power.base.sys.service;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.power.common.entity.SysDepartment;
import com.power.common.entity.SysOrganize;
import com.power.common.entity.SysUser;
import com.power.common.mybatis.DaoHelper;

/**
 * 项目名称： power2016
 * 类名称： OrgService
 * 创建时间： 2016年10月31日 上午10:44:47
 * @author WJZ
 * @version 1.0
 */
@Service("orgService")
public class OrgService {
	
	private static final Logger log = LoggerFactory.getLogger(OrgService.class);
	
	@Autowired
	private DaoHelper daoHelper;
	
	//组织机构列表
	public List<SysOrganize> showCompany(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.SysOrganizeMapper.showCompany",map);
	}
	
	//新增组织机构
	@CacheEvict(value="sysCache",allEntries=true)
	public void newOrg(SysOrganize comp){
		comp.setOrganizeid(daoHelper.findTableKeyUUID());
		daoHelper.insertSelective(comp);
	}
	
	//根据comid查询组织机构
	public SysOrganize getOrg(String comid) {
		return daoHelper.findObject(SysOrganize.class, comid);
	}
	
	//编辑组织机构
	@CacheEvict(value="sysCache",allEntries=true)
	public void editOrg(SysOrganize comp){
		daoHelper.updateSelective(comp);
	}
	
	//删除组织机构
	@CacheEvict(value="sysCache",allEntries=true)
	public void delOrg(String comid){
		Object obj = daoHelper.findObject("mapper.SysOrganizeMapper.getCanDel", comid);
		if(obj==null){
			throw new RuntimeException("get organize error,companyid："+comid);
		}
		Integer count = (Integer)obj;
		if(count>0){
			log.warn("该公司已经有下属公司，或者已有附属部门，暂时无法删除！");
			throw new RuntimeException("该公司已经有下属公司，或者已有附属部门，暂时无法删除！");
		}
		SysOrganize comp = new SysOrganize();
		comp.setOrganizeid(comid);;
		comp.setDeletemark(1);
		daoHelper.updateSelective(comp);
		// daoHelper.delete(SysOrganize.class, comid);
	}


	/**
	 * 根据organizeid查询部门
	 * @param organizeid
	 * @return
	 */
	public List<SysDepartment> getDeptsByCompanyId(String organizeid) {
		return daoHelper.findAll("mapper.SysDepartmentMapper.getDeptsByCompanyId",organizeid);
	}
	
	/**
	 * 查询全部部门
	 * @return
	 */
	public List<SysDepartment> getAllDepts() {
		return daoHelper.findAll("mapper.SysDepartmentMapper.getAllDepts");
	}


	/**
	 * 查询所有用户
	 * @return
	 */
	public List<SysUser> getAllUsers() {
		return daoHelper.findAll("mapper.SysUserMapper.getAllUsers");
	}
	
	/**
	 * 获取当前公司，和下属公司信息
	 * @param orgId 如果null,返回全部机构信息
	 * @return
	 */
	@Cacheable(value="sysCache",key="#orgId")//
	public List<SysOrganize> getOrgAllChildren(final String orgId) {
		List<SysOrganize> allList = showCompany(null);
		if(StringUtils.isBlank(orgId)|| orgId.equals("all")){return allList;}
		List<SysOrganize> ChildNodeList=Lists.newLinkedList();
		List<SysOrganize> orgList = Lists.newLinkedList(Collections2.filter(allList, new Predicate<SysOrganize>() {
            public boolean apply(SysOrganize dept) {
                return dept.getOrganizeid().equals(orgId);
            }
        }));
		ChildNodeList.addAll(orgList);
		//
		getOrgChildren(allList,ChildNodeList,orgId);
        return ChildNodeList;
	}
	private void getOrgChildren(List<SysOrganize> list,List<SysOrganize> reList,final String parentId){
		List<SysOrganize> deptList = Lists.newLinkedList(Collections2.filter(list, new Predicate<SysOrganize>() {
            public boolean apply(SysOrganize dept) {
                return dept.getParentid().equals(parentId);
            }
        }));
		//
		for (int i=0;i<deptList.size();i++) {
			SysOrganize item = deptList.get(i);
			getOrgChildren(list,reList,item.getOrganizeid());
    		//
    		reList.add(item);
		}
	}
}

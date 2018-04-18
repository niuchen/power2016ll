package com.power.base.sys.service;

import java.util.List;
import java.util.Map;

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
import com.power.common.mybatis.DaoHelper;

/**
 * 
 * 项目名称： power2016
 * 类名称： DeptService
 * 创建时间： 2016年10月28日 下午2:49:37
 * @author WJZ
 * @version 1.0
 */
@Service("deptService")
public class DeptService {
	private static final Logger log = LoggerFactory.getLogger(DeptService.class);
	@Autowired
	private DaoHelper daoHelper;
	@Autowired
	private OrgService orgService;
	
	//部门列表
	public List<SysDepartment> showDept(Map<String, Object> map) {
		return daoHelper.findAll("mapper.SysDepartmentMapper.showDept",map);
	}
	public List<SysDepartment> showPid(Map<String, Object> map) {
		return daoHelper.findAll("mapper.SysDepartmentMapper.showPid",map);
	}
	//新增部门
	@CacheEvict(value="sysCache",allEntries=true)
	public void newDept(SysDepartment dept){
		dept.setDepartmentid(daoHelper.findTableKeyUUID());
		daoHelper.insertSelective(dept);
	}
	
	//根据deptid查询部门
	public SysDepartment getDept(String deptid) {
		return daoHelper.findObject(SysDepartment.class, deptid);
	}
	//编辑部门
	@CacheEvict(value="sysCache",allEntries=true)
	public void editDept(SysDepartment dept){
		daoHelper.updateSelective(dept);
	}
	//删除部门
	@CacheEvict(value="sysCache",allEntries=true)
	public void delDept(String deptid){
		Object obj = daoHelper.findObject("mapper.SysDepartmentMapper.getCanDel", deptid);
		if(obj==null){
			throw new RuntimeException("get department error,department："+deptid);
		}
		Integer count = (Integer)obj;
		if(count>0){
			log.warn("该部门已经有下属部门，或者已有附属用户，暂时无法删除！");
			throw new RuntimeException("该部门已经有下属部门，或者已有附属用户，暂时无法删除！");
		}
		SysDepartment dept = new SysDepartment();
		dept.setDepartmentid(deptid);
		dept.setDeletemark(1);
		daoHelper.updateSelective(dept);
		//
		// daoHelper.delete(SysDepartment.class, deptid);
	}
	/**
	 * 获取当前部门，和下属部门信息
	 * @param orgId null ,全部部门信息
	 * @param deptId
	 * @return
	 */
	@Cacheable(value="sysCache",key="#orgId.concat(#deptId)")//
	public List<SysDepartment> getAllChildren(String orgId,final String deptId) {
		if(orgId.equals("all")){orgId = null;}
		List<SysDepartment> allList = daoHelper.findAll("mapper.SysDepartmentMapper.getDeptsByCompanyId",orgId);
		if(StringUtils.isBlank(deptId)|| deptId.equals("all")){return allList;}
		List<SysDepartment> ChildNodeList=Lists.newLinkedList();
		List<SysDepartment> deptList = Lists.newLinkedList(Collections2.filter(allList, new Predicate<SysDepartment>() {
            public boolean apply(SysDepartment dept) {
                return dept.getDepartmentid().equals(deptId);
            }
        }));
		ChildNodeList.addAll(deptList);
		//
		getDeptChildren(allList,ChildNodeList,deptId);
        return ChildNodeList;
	}
	private void getDeptChildren(List<SysDepartment> list,List<SysDepartment> reList,final String parentId){
		List<SysDepartment> deptList = Lists.newLinkedList(Collections2.filter(list, new Predicate<SysDepartment>() {
            public boolean apply(SysDepartment dept) {
                return dept.getParentid().equals(parentId);
            }
        }));
		//
		for (int i=0;i<deptList.size();i++) {
			SysDepartment item = deptList.get(i);
    		getDeptChildren(list,reList,item.getDepartmentid());
    		//
    		reList.add(item);
		}
	}
}

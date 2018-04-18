package com.power.base.sys.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.power.base.sys.entity.KeyValue;
import com.power.common.entity.SysDict;
import com.power.common.mybatis.DaoHelper;

/**
 * 
 * 项目名称： power2016
 * 类名称： DictService
 * 创建时间： 2016年10月28日 下午2:50:46
 * @author WJZ
 * @version 1.0
 */
@Service("dictService")
public class DictService {
	private static final Logger log = LoggerFactory.getLogger(DictService.class);
	
	@Autowired
	private DaoHelper daoHelper;

	private static String PREFIX="mapper.SysDictMapper.";
	
	//左侧树形列表
	public List<SysDict> getTreeList() {
		return daoHelper.findAll(PREFIX+"getTreeList");
	}
	
	/**
	 * 根据parenid现实字典列表
	 * @param param
	 * @return
	 */
	public List<SysDict> getDictListByParentId(Map<String, Object> param) {
		return daoHelper.findAll(PREFIX+"getAllList",param);
	}
	
	//新增字典
	@CacheEvict(value="sysCache",allEntries=true)
	public void newDict(SysDict dict){
		dict.setDictid((String)daoHelper.findObject(PREFIX+"getMax", Integer.parseInt(dict.getDictid())));
		daoHelper.insertSelective(dict);
	}
	
	//根据dictid查询
	public SysDict getDict(String dictid){
		return daoHelper.findObject(SysDict.class, dictid);
	}
	
	//编辑字典、字典分类
	@CacheEvict(value="sysCache",allEntries=true)
	public void editDict(SysDict dict){
		daoHelper.updateSelective(dict);
	}
	
	//删除字典
	@CacheEvict(value="sysCache",allEntries=true)
	public void delDict(String dictid){
		Object obj = daoHelper.findObject("mapper.SysDictMapper.getCanDel", dictid);
		if(obj==null){
			throw new RuntimeException("get dict error,dictid："+dictid);
		}
		Integer count = (Integer)obj;
		if(count>0){
			log.warn("该字典分类存在字典项，暂时无法删除！");
			throw new RuntimeException("该字典分类存在字典项，暂时无法删除！");
		}
		SysDict dict = new SysDict();
		dict.setDictid(dictid);
		dict.setDeletemark(1);
		daoHelper.updateSelective(dict);
		//daoHelper.delete(SysDict.class, dictid);
	}
	
	//查询字典分类列表
	public List<SysDict> getclassList() {
		return daoHelper.findAll(PREFIX+"getTreeList");
	}
	
	//新增字典分类
	@CacheEvict(value="sysCache",allEntries=true)
	public void newClass(SysDict dict){
		dict.setDictid((String)daoHelper.findObject(PREFIX+"getMax", Integer.parseInt(dict.getDictid())));
		daoHelper.insertSelective(dict);
	}

	/**
	 * 根据字典分类获取
	 * 字典信息
	 * @param classId
	 * @return
	 */
	public List<KeyValue> getDictItem(String classId) {
		return daoHelper.findAll(PREFIX+"getDictItem",classId);
	}
}

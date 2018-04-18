package com.power.base.sys.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.power.common.entity.SysModule;
import com.power.common.entity.SysModuleButton;
import com.power.common.mybatis.DaoHelper;

/**
 * 项目名称：power2016 <br>
 * 类名称：ModuleService <br>
 * 创建时间：2016-9-19 下午3:54:45 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("moduleService")
public class ModuleService {
	private static final Logger log = LoggerFactory.getLogger(ModuleService.class);
	@Autowired
	private DaoHelper daoHelper;
	private static String PREFIX="mapper.SysModuleMapper.";
	private static String BUTTON_PREFIX="mapper.SysModuleButtonMapper.";

	/**
	 * 所有模块
	 * @return
	 */
	public List<SysModule> getAllList() {
		return daoHelper.findAll(PREFIX+"getAllList");
	}

	/**
	 * 模块列表 
	 * @param param 父级元素
	 * @return
	 */
	public List<SysModule> getModuleListByParentId(Map<String, Object> params) {
		return daoHelper.findAll(PREFIX+"getAllList",params);
	}
	/**
	 * 模块按钮信息列表
	 * @param param
	 * @return
	 */
	public List<SysModuleButton> getModuleBtnList(Map<String, Object> param) {
		return daoHelper.findAll(BUTTON_PREFIX+"getModuleBtnList",param);
	}

	/**
	 * 添加 修改模块信息
	 * @param module
	 * @param list
	 */
	public void newModule(SysModule module, List<SysModuleButton> list) {
		if(StringUtils.isNotBlank(module.getModuleid())){//修改
			UserUtils.setObjectModifyUser(module);
			daoHelper.updateSelective(module);
			//保存btn list 
			
			//删除废弃的按钮
			StringBuffer btns = new StringBuffer();
			for (SysModuleButton btn : list) {
				//修改
				SysModuleButton editBtn = daoHelper.findObject(SysModuleButton.class, btn.getButtonid());
				if(editBtn == null){
					btn.setModuleid(module.getModuleid());
					daoHelper.insert(btn);
				}else{
					editBtn.setActionaddress(btn.getActionaddress());
					editBtn.setFullname(btn.getFullname());
					editBtn.setParentid(btn.getParentid());
					editBtn.setChecked(btn.getChecked());
					editBtn.setCode(btn.getCode());
					editBtn.setIcon(btn.getIcon());
					
					daoHelper.updateSelective(editBtn);
				}
				btns.append(",'").append(btn.getButtonid()).append("'");
			}
			if(btns.length()>1){
				Map<String, Object> param = Maps.newHashMap();
				param.put("moduleid", module.getModuleid());
				param.put("btns", btns.deleteCharAt(0).toString());
				//删除废弃的按钮  包括权限
				daoHelper.update(BUTTON_PREFIX+"delBtnByBtns",param);
			}
			log.debug("---修改 module");
		}else{
			UserUtils.setObjectCreateUser(module);
			module.setModuleid(daoHelper.findTableKeyUUID());
			daoHelper.insertSelective(module);
			//保存btn list 
			for (SysModuleButton btn : list) {
				btn.setModuleid(module.getModuleid());
				daoHelper.insert(btn);
			}
		}
	}

	/**
	 * module 表单
	 * @param key
	 * @return
	 */
	public SysModule getModuleById(String key) {
		return daoHelper.findObject(SysModule.class, key);
	}

	/**
	 * 删除模块
	 * @param key
	 */
	public void delModel(String key) {
		//删除权限 按钮 菜单
		daoHelper.delete(SysModule.class,key);
		daoHelper.delete(BUTTON_PREFIX+"delBtnByModuleId", key);
	}
	
}

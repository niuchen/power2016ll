package com.power.base.sys.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.power.base.sys.entity.WdTreeVO;
import com.power.base.sys.service.ModuleService;
import com.power.common.entity.SysModule;
import com.power.common.entity.SysModuleButton;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.springmvc.ExtReturn;
import com.power.common.springmvc.ResponseUtils;

/**
 * 系统功能 
 * 模块/按钮
 * 项目名称：power2016 <br>
 * 类名称：ModuleController <br>
 * 创建时间：2016-9-13 下午2:58:29 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Controller
@RequestMapping("sys/module")
public class ModuleController {
	
	private static final Logger log = LoggerFactory.getLogger(ModuleController.class);
	
	@Autowired
	private ModuleService moduleService;
	private static String PREFIX = "sys/module/";
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping("index")
    public String module() {
    	return PREFIX+"index";
    }
	/**
	 * wdTree 树形列表
	 * @param list
	 * @param treeList
	 * @param parentId
	 */
	private void getChildren(List<SysModule> list,List<WdTreeVO> treeList,final String parentId){
		List<SysModule> moduleList = Lists.newArrayList(Collections2.filter(list, new Predicate<SysModule>() {
            public boolean apply(SysModule module) {
                return module.getParentid().equals(parentId);
            }
        }));
		for (int i=0;i<moduleList.size();i++) {
			SysModule item = moduleList.get(i);
		    WdTreeVO tree = new WdTreeVO();
	    		//
    		List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
    		getChildren(list,subTree,item.getModuleid());
    		tree.setChildNodes(subTree);
    		tree.setHasChildren(subTree.size()>0?true:false);
    		//
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
	 * 左侧树形列表
	 * @return
	 */
	@RequestMapping("getModuleTree")
	@ResponseBody
	public Object treeJson(){
		List<SysModule> list = moduleService.getAllList();
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		getChildren(list,treeList,"0");
		return treeList;
	}
	
	/**
	 * 功能模块信息列表
	 * 无分页
	 * @param pid
	 * @param condition 查询列
	 * @param keyword 关键字
	 * @param grid
	 * @return
	 */
	@RequestMapping("getModuleList")
	@ResponseBody
	public Object getModuleList(@RequestParam(value="pid",required=false)String pid,
			@RequestParam(value="condition",required=false)String condition,
			@RequestParam(value="keyword",required=false)String keyword,JqGrid grid){
		Map<String,Object> param = new HashMap<String, Object>(3);
		param.put("pid",pid);
		if(StringUtils.isNotBlank(condition)){//查询条件
			param.put(condition,keyword);
		}
		
		List<SysModule> list = moduleService.getModuleListByParentId(param);
		grid.setRows(list);
		return grid;
	}
	/**
	 * 模块 新增 
	 * 修改
	 * @return
	 */
	@RequestMapping("form")
	public String form(){
		return PREFIX+"new";
	}
	
	/**
	 * 模块修改 Form 数据 
	 * @param key
	 * @return
	 */
	@RequestMapping("getModuleFormJson")
	@ResponseBody
	public Object getModuleFormJson(@RequestParam("key")String key){
		SysModule module = moduleService.getModuleById(key);
		return new ExtReturn(true, module);
	}
	
	/**
	 * 添加/修改 模块 
	 * @param module
	 * @param btnJson
	 * @param keyValue module 主键
	 * @return
	 */
	@RequestMapping("newModuleSubmit")
	@ResponseBody
	public Object newModuleSubmit(SysModule module,
			@RequestParam("moduleButtonListJson")String btnJson,
			@RequestParam("keyValue")String keyValue){
		List<SysModuleButton> list=null;
		if(StringUtils.isNotBlank(btnJson)){
			list = JSON.parseArray(btnJson, SysModuleButton.class);
		}
		try {
			moduleService.newModule(module,list);
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("newModuleSubmit err "+e);
			return new ExtReturn(false,"操作失败");
		}
	}
	
	/**
	 * 删除 module
	 * @param key
	 * @return
	 */
	@RequestMapping("delModule")
	@ResponseBody
	public Object delModule(@RequestParam("id")String key){
		moduleService.delModel(key);
		return new ExtReturn(true, "操作成功");
	}
	
	
	/**
	 * 选择图标
	 * @return
	 */
	@RequestMapping("selectIcon")
	public String selectIcon(){
		return PREFIX+"selectIcon";
	}
	/**
	 * 模块 按钮 
	 * 新增 parentId moduleId
	 * 修改 buttonId moduleId
	 * @param parentId btn上下级主键
	 * @param buttonId 修改时候使用
	 * @param moduleId
	 * @return
	 */
	@RequestMapping("btnForm")
	public String btnForm(@RequestParam(value="parentId",required=false)String parentId,
			@RequestParam(value="buttonId",required=false)String buttonId,
			@RequestParam(value="moduleId",required=false)String moduleId,Model model){
		if(StringUtils.isNotBlank(buttonId)){//修改
			
		}else{//新增
			model.addAttribute("buttonId", UUID.randomUUID().toString().replace("-", ""));//生成主键
		}
		model.addAttribute("moduleId", moduleId);//
		return PREFIX+"newBtn";
	}
	
	/**
	 * wdTree 树形列表
	 * @param list
	 * @param treeList
	 * @param parentId
	 */
	private void getBtnChildren(List<SysModuleButton> list,List<WdTreeVO> treeList,String parentId){
		final String moduleid=parentId;
		List<SysModuleButton> btnModuleList = Lists.newArrayList(Collections2.filter(list, new Predicate<SysModuleButton>() {
            public boolean apply(SysModuleButton btn) {
                return btn.getModuleid().equals(moduleid);
            }
        }));
		for (int i=0;i<btnModuleList.size();i++) {
			SysModuleButton item = btnModuleList.get(i);
		    WdTreeVO tree = new WdTreeVO();
		    //
    		List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
    		getBtnChildren(list,subTree,item.getButtonid());
    		tree.setChildNodes(subTree);
    		tree.setHasChildren(subTree.size()>0?true:false);
    		//
    		tree.setId(item.getButtonid());
    		tree.setValue(item.getButtonid());
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
	 * 将列表转换为 tree
	 * @param btnJson
	 * @return
	 */
	@RequestMapping("btnListToTreeJson")
	@ResponseBody
	public Object btnListToTreeJson(@RequestParam("moduleButtonJson")String btnJson){
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		if(StringUtils.isNotBlank(btnJson)){
			List<SysModuleButton> list = JSON.parseArray(btnJson, SysModuleButton.class);
			getBtnChildren(list,treeList,"0");
		}
		return treeList;
	}
	/**
	 * 将 List 转换为 TreeGrid
	 * @param btnJson
	 * @param rsp
	 */
	@RequestMapping("btnListToGrid")
	public void btnListToGrid(@RequestParam("moduleButtonJson")String btnJson,HttpServletResponse rsp){
		if(StringUtils.isNotBlank(btnJson)){
			List<SysModuleButton> list = JSON.parseArray(btnJson, SysModuleButton.class);
			StringBuffer sb = new StringBuffer();
			sb.append("{ \"rows\": [");
			sb.append(TreeGridJson(list, -1,"0"));
			sb.append("]}");
			
			ResponseUtils.renderJson(rsp, sb.toString());
		}
	}
	

	/**
	 * 树形Grid 封装.
	 * @param list
	 * @param index
	 * @param parentId
	 * @return
	 */
	public String TreeGridJson(List<SysModuleButton> list, int index,final String parentId){
	    StringBuilder sb = new StringBuilder();
	   //只过滤父级元素
	    List<SysModuleButton> ChildNodeList = Lists.newArrayList(Collections2.filter(list, new Predicate<SysModuleButton>() {
            public boolean apply(SysModuleButton btn) {
                return btn.getParentid().equals(parentId);
            }
        }));
	    list.removeAll(ChildNodeList);//移除已使用节点
	    if (ChildNodeList.size() > 0) { index++;}
	    for (SysModuleButton sysBtn : ChildNodeList) {
	        StringBuffer strJson = new StringBuffer(JSON.toJSONString(sysBtn));
	        strJson = strJson.insert(1, "\"level\":" + index + ",");//层级关系
	        boolean leaf = true;
	        for (SysModuleButton sys : list) {//判断是否有下级
				if(sys.getParentid().equals(sysBtn.getButtonid())){
					leaf = false;
					break;
				}
			}
	        strJson = strJson.insert(1, "\"isLeaf\":" +leaf+ ",");//是否叶子
	        strJson = strJson.insert(1, "\"expanded\":true,");//默认全部展开
	//        strJson = strJson.insert(1, "\"loaded\":true,");//已加载
	        sb.append(strJson);
	        sb.append(TreeGridJson(list, index, sysBtn.getButtonid()));
	    }
	    return sb.toString().replaceAll("\\}\\{", "},{");
	}
	
	/**
	 * 按钮信息列表
	 * 无分页
	 * @param moduleid
	 * @return
	 */
	@RequestMapping("getModuleBtnList")
	@ResponseBody
	public Object getModuleBtnList(@RequestParam(value="moduleId",required=false)String moduleid){
		Map<String,Object> param = new HashMap<String, Object>(3);
		param.put("moduleid",moduleid);
		
		List<SysModuleButton> list = moduleService.getModuleBtnList(param);
		return list;
	}
	
	
}

package com.power.base.sys.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.power.base.sys.entity.WdTreeVO;
import com.power.base.sys.service.DictService;
import com.power.common.entity.SysDict;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.springmvc.ExtReturn;
import com.power.common.springmvc.ResponseUtils;


/**
 * 项目名称： power2016
 * 类名称： DictController
 * 创建时间： 2016年10月17日 下午3:18:37
 * @author WJZ
 * @version 1.0
 */
@Controller
@RequestMapping("sys/dict")
public class DictController {
	
	private static final Logger log=LoggerFactory.getLogger(DictController.class);
	
	private String prefix="sys/dict/";
	
	@Autowired
	private DictService dictService;
	
	/**
	 * 通用字典首页
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping("index")
	public String dictIndex(HttpServletRequest req,Model model){
		
		return prefix+"index";
	}
	/**
	 * wdTree 树形列表
	 * FIXME 递归，避免大数据
	 * @param list
	 * @param treeList
	 * @param parentId
	 */
	private void getChildren(List<SysDict> list,List<WdTreeVO> treeList,String parentId){
		for (int i=0;i<list.size();i++) {
			SysDict item = list.get(i);
		    WdTreeVO tree = new WdTreeVO();
	    	if(item.getParentid().equals(parentId)){
	    		//list.remove(i);
	    		//
	    		List<WdTreeVO> subTree = new ArrayList<WdTreeVO>();
	    		getChildren(list,subTree,item.getDictid());
	    		tree.setChildNodes(subTree);
	    		tree.setHasChildren(subTree.size()>0?true:false);
	    		//
	    		tree.setId(item.getDictid()+"");
	    		tree.setValue(item.getDictid()+"");
	    		tree.setText(item.getFullname());
	    		//tree.setImg(item.getIcon());
	    		tree.setComplete(true);
//	    		tree.setIsexpand(item.getModuleid()==1?true:false);
	    		tree.setIsexpand(true);
	    		tree.setParentnodes(parentId+"");
	    		//
	    		treeList.add(tree);
	    	}
		}
	}
	/**
	 * 左侧树形列表
	 * @return
	 */
	@RequestMapping("getDictTree")
	@ResponseBody
	public Object treeJson(){
		List<SysDict> list = dictService.getTreeList();
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		getChildren(list,treeList,"0");
		return treeList;
	}
	
	/**
	 * 功能模块信息列表
	 * 无分页
	 * @param pid
	 * @return
	 */
	@RequestMapping("getDictList")
	@ResponseBody
	public Object getDictList(@RequestParam(value="pid",required=false)String pid,
							  @RequestParam(value="condition",required=false)String colname,
							  @RequestParam(value="keyword",required=false)String value,JqGrid grid){
		Map<String,Object> param = new HashMap<String, Object>(3);
		if(colname!=null){
			param.put(colname,value);
		}
		param.put("pid",pid);
		
		List<SysDict> list = dictService.getDictListByParentId(param);
		grid.setRows(list);
		return grid;
	}
	
	/**
	 * 新增/编辑通用字典
	 * @param req
	 * @param model
	 * @return
	 * */
	@RequestMapping("form")
	public String dictForm(HttpServletRequest req,Model model){
		
		return prefix+"new";
	}
	
	/**
	 *编辑通用字典获取数据
	 * @param dictid
	 * @param model
	 * @return
	 * */
	@RequestMapping("getFormJson")
	@ResponseBody
	public Object editDict(@RequestParam("keyValue")String dictid,Model model){
		 SysDict dict = dictService.getDict(dictid);
		return new ExtReturn(true, dict);
	}
	
	/**
	 *用户字典新增/编辑提交
	 *@param req
	 *@param dict
	 *@return
	 */
	@RequestMapping(value = "dictSub",method = RequestMethod.POST)
	@ResponseBody
	public Object newDictSub(HttpServletRequest req,SysDict dict){
		try {
			if(dict.getDictid().length()>3){
				dictService.editDict(dict);
			}else{
				dict.setDeletemark(0);
				dictService.newDict(dict);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("newDict or editDict error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
	}

	/**
	 * 删除用户字典
	 * @param dictid
	 * @return
	 * */
	@RequestMapping(value = "delDict",method = RequestMethod.POST)
	@ResponseBody
	public Object delDict(@RequestParam("keyValue")String dictid){
		try {
			dictService.delDict(dictid);
		} catch (Exception e) {
			log.error("delDict error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 *通用字典详细
	 * @param dictid
	 * @param model
	 * @return
	 * */
	@RequestMapping("detail/{keyValue}")
	public String detailDict(@PathVariable("keyValue")String dictid,Model model){
		model.addAttribute("Dict", dictService.getDict(dictid));
		return prefix+"detail";
	}
	
	/**
	 * 字典分类首页
	 * @param req
	 * @param model
	 * @return
	 * */
	@RequestMapping("class")
	public String classIndex(HttpServletRequest req,Model model){
		
		return prefix+"class/classIndex";
	}
	
	/**
	 * 字典分类列表
	 * @param rsp
	 * */
	@RequestMapping("showClass")
	public void showClass(HttpServletResponse rsp){
		List<SysDict> list = dictService.getclassList();
		StringBuffer sb = new StringBuffer();
		sb.append("{ \"rows\": [");
        sb.append(TreeGridJson(list, -1, "0"));
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
    public String TreeGridJson(List<SysDict> list, int index, String parentId){
        StringBuilder sb = new StringBuilder();
        List<SysDict> ChildNodeList=new ArrayList<SysDict>();
        for (int i=0;i<list.size();i++) {//只过滤父级元素
    		SysDict item = list.get(i);
	    	if(item.getParentid().equals(parentId)){
	    		ChildNodeList.add(item);
	    	}
	    }
        if (ChildNodeList.size() > 0) { index++;}
        for (SysDict sysDict : ChildNodeList) {
            StringBuffer strJson = new StringBuffer(JSON.toJSONString(sysDict));
            strJson = strJson.insert(1, "\"level\":" + index + ",");//层级关系
            boolean leaf = true;
            for (SysDict sys : list) {//判断是否有下级
				if(sys.getParentid().equals(sysDict.getDictid())){
					leaf = false;
					break;
				}
			}
            strJson = strJson.insert(1, "\"isLeaf\":" +leaf+ ",");//是否叶子
            strJson = strJson.insert(1, "\"expanded\":true,");//默认全部展开
            sb.append(strJson);
            sb.append(TreeGridJson(list, index, sysDict.getDictid()));
        }
        return sb.toString().replaceAll("\\}\\{", "},{");
    }
    
	/**
	 * 新增/编辑字典分类
	 * @param req
	 * @param model
	 * @return
	 * */
	@RequestMapping("classform")
	public String classform(HttpServletRequest req,Model model){
		
		return prefix+"class/new";
	}
	
	/**
	 *编辑字典分类获取数据
	 * @param dictid
	 * @param model
	 * @return
	 * */
	@RequestMapping("getClassFormJson")
	@ResponseBody
	public Object editClass(@RequestParam("keyValue")String dictid,Model model){
		SysDict classdict = dictService.getDict(dictid);
		return new ExtReturn(true, classdict);
	}
	
	/**
	 *字典分类新增/编辑提交
	 *@param req
	 *@param dict
	 *@return
	 */
	@RequestMapping(value = "CalssSub",method = RequestMethod.POST)
	@ResponseBody
	public Object newClassSub(HttpServletRequest req,SysDict dict){
		try {
			if(dict.getDictid().length() == 3){
				dictService.editDict(dict);
			}else{
				dict.setDeletemark(0);
				dictService.newClass(dict);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("newDictClass or editDictClass error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
	}

	/**
	 * 删除字典分类
	 * @param dictid
	 * @return
	 * */
	@RequestMapping(value = "delClass",method = RequestMethod.POST)
	@ResponseBody
	public Object delClass(@RequestParam("keyValue")String dictid){
		try {
			dictService.delDict(dictid);
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("delDictClass error:{}"+e);
			return new ExtReturn(false,e.getMessage());
		}
	}
	///////
	/**
	 * 根据字典分类获取
	 * 字典信息
	 * @param classId
	 * @return
	 */
	@RequestMapping("getDictItem")
	@ResponseBody
	public Object getDictItem(@RequestParam("classId")String classId){
		return dictService.getDictItem(classId);
	}
}

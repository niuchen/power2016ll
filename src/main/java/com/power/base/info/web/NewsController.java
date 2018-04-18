package com.power.base.info.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.power.base.info.service.NewsService;
import com.power.base.sys.entity.KeyValue;
import com.power.base.sys.entity.WdTreeVO;
import com.power.base.sys.service.DictService;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.BaseNews;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.springmvc.ExtReturn;

/**
 * 项目名称：power2016 <br>
 * 类名称：NewsController <br>
 * 创建时间：2016-11-9 下午12:43:12 <br>
 * @version 1.0
 */
@Controller
@RequestMapping("base/news")
public class NewsController {
	private static final Logger log = LoggerFactory.getLogger(NewsController.class);
	/**
	 * 时间格式规范
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder){
		CustomDateEditor editor=new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"),true);
		binder.registerCustomEditor(Date.class,editor);
	}
	/**
	 * 注入newService
	 */
	
	@Autowired
	private NewsService newsService;
	@Autowired
	private DictService dictService;
	
	private String PREFIX="base/news/";
	private String NoticePREFIX="base/notice/";
	/**
	 * 新闻/公告主页面跳转
	 * @return
	 */
	@RequestMapping("index")
	public String index(){
		return PREFIX+"index";
	}
	@RequestMapping("notice")
	public String notice(){
		return NoticePREFIX+"notice";
	}
	
	/**
	 * 列表展示
	 * @param key值为0，代表新闻，1代表公告
	 * @param category 新闻栏目
	 * @param fullhead 标题
	 * @param grid 分页
	 * @return
	 * @throws IOException
	 */
	 
	@RequestMapping("showNews/{key}")
	@ResponseBody
		public Object showNews(@PathVariable("key")Integer key,
				     		   @RequestParam(value = "category",required=false)String category,
							   @RequestParam(value = "fullhead",required = false)String fullhead,
							   JqGrid grid)throws IOException{
		Map<String, Object> map = new HashMap<String, Object>(3);
		if(category!=null){
		map.put("category", category);
		}
		if(fullhead!=null){
		map.put("fullhead", fullhead);
		}
		map.put("typeid", key);
		List<BaseNews> list = newsService.showNews(map);
		grid.setRows(list);
		 return grid;
	}
	/**
	 * 新增页面跳转
	 * @param model 传当前时间到页面
	 * @return
	 */
	@RequestMapping("newNews/{key}")
	public Object newNews(@PathVariable("key")Integer id,Model model){
		model.addAttribute("nowtime",new Date());
		model.addAttribute("newColor", new Object());
		if(id==0){
		return PREFIX+"new";
		}else{
			return NoticePREFIX+"newNotice";
		}
	}

	/**
	 * wdTree 树形列表
	 * @param list
	 * @param treeList
	 */
	private void getList(List<KeyValue> list,List<WdTreeVO> treeList){
		for (KeyValue item : list) {
		    WdTreeVO tree = new WdTreeVO();
		    tree.setId(item.getId());
    		tree.setValue(item.getId());
    		tree.setText(item.getName());
    		tree.setComplete(true);
    		tree.setIsexpand(true);
	    		treeList.add(tree);
	}
	}
	/**
	 * 左侧树形列表
	 * @param 
	 * @param 
	 * @return
	 */
	@RequestMapping("newsTree")
	@ResponseBody
	public Object newTree(){		
		List<KeyValue> list = dictService.getDictItem("100");		
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		getList(list,treeList);
		return treeList ;
		
	}
	@RequestMapping("noticeTree")
	@ResponseBody
	public Object noticeTree(){		
		List<KeyValue> list = dictService.getDictItem("101");		
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		getList(list,treeList);
		return treeList ;
		
	}
	/**
	 *获取要被修改的值传到修改页面上
	 * @param id
	 * @return
	 */
	@RequestMapping("getFormJson")
	@ResponseBody
	public Object getFormJson(@RequestParam("keyValue")String id){
		BaseNews news = newsService.getNews(id);
		news.setReleasetime(null);
		return new ExtReturn(true,news);
	}
	/**
	 * 新增/编辑保存
	 * @param news
	 * @return
	 */
	@RequestMapping(value="saveForm",method = RequestMethod.POST)
	@ResponseBody
	public Object saveForm(BaseNews news){
		try{
			if(StringUtils.isNotBlank(news.getNewsid())){
				UserUtils.setObjectModifyUser(news);
				newsService.editNews(news);
			}else{
				UserUtils.setObjectCreateUser(news);
				newsService.newNews(news);
			}
			return new ExtReturn(true,"操作成功");
		}catch(Exception e){
			return new ExtReturn(false,"操作失败");
		}
		
	}
	/**
	 * 删除
	 * @param newsid
	 * @return
	 */
	@RequestMapping(value="delNews",method = RequestMethod.POST)
	@ResponseBody	
	public Object delNews(@RequestParam("keyValue")String newsid){
		try {	
			 newsService.delNews(newsid);
			
		} catch (Exception e) {
		log.error("delNews error:{}"+e);
			return new ExtReturn(false,"操作失败".getClass());
	}
			return new ExtReturn(true,"操作成功");
}
	@RequestMapping("newsColumn")
	public String newsColumn(){
		return PREFIX+"newsColumn";
	}
	
	
	
	
}
	


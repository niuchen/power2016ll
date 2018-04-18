package com.power.base.info.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.common.entity.BaseNews;
import com.power.common.entity.SysRole;
import com.power.common.mybatis.DaoHelper;
import com.power.common.mybatis.page.JqGrid;

/**
 * 项目名称：power2016 <br>
 * 类名称：NewsService <br>
 * 创建时间：2016-11-9 下午12:43:23 <br>
 * @version 1.0
 */
@Service("NewService")
public class NewsService {
	@Autowired
	private DaoHelper daoHelper;
	/**
	 * 列表展示
	 * @param map
	 * @return
	 */
	public List<BaseNews> showNews(Map<String, Object> map){
		return  daoHelper.findAll("mapper.BaseNewsMapper.showNews",map);
	}
	/**
	 * 新增
	 * @param news
	 * @return
	 */
	public Object newNews(BaseNews news){
		news.setNewsid(daoHelper.findTableKeyUUID());
		return daoHelper.insertSelective(news);
	}
	/**
	 * 获取被修改的值
	 * @param id 被修改的newsid
	 * @return
	 */
	public BaseNews getNews(String id) {
		
		return (BaseNews)daoHelper.findObject(BaseNews.class,id);
	}
	/**
	 * 修改
	 * @param news
	 * @return
	 */
	public Object editNews(BaseNews news) {
		
		return daoHelper.updateSelective(news);
	}
	/**
	 * 删除
	 * @param newsid
	 */
	 public void delNews(String newsid) {
		 BaseNews bn  = new BaseNews();
		 bn.setNewsid(newsid);
		 bn.setDeletemark(1);
		daoHelper.updateSelective(bn);
	 }
}

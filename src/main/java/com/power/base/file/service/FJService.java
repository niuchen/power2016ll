package com.power.base.file.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.power.base.file.entity.DownLoadVO;
import com.power.common.mybatis.DaoHelper;

/**
 * 项目名称：base_ms <br>
 * 类名称：FJImpl <br>
 * 创建时间：2014-7-7 上午10:01:35 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("fjImpl")
public class FJService {
	@Resource(name="daoHelper")
	private DaoHelper daoHelper;
	public void setDaoHelper(DaoHelper daoHelper) {
		this.daoHelper = daoHelper;
	}
	
	public DownLoadVO editWjVO(Integer xh, Integer uid) {
		daoHelper.update("mapper.PmFjxxMapper.updateDown", xh);
		Map<String,Object> map = new HashMap<String,Object>(2);
		map.put("xh", xh);
		map.put("uid", uid);
		return (DownLoadVO) daoHelper.findObject("mapper.PmFjxxMapper.getDownWj", map);
	}

}

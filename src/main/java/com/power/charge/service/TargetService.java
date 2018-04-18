package com.power.charge.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.FxRcsj;
import com.power.common.entity.FxRwgl;
import com.power.common.entity.FxZsgl;
import com.power.common.mybatis.DaoHelper;
import com.power.common.mybatis.page.JqGrid;

@Service("targetService")
public class TargetService {
	@Autowired
	private DaoHelper daoHelper;

	
		/**
		 * 任务管理数据
		 * @param map
		 * @param grid
		 */
	public void getRwData(HashMap<String, Object> map, JqGrid grid) {
			daoHelper.findRows("mapper.FxRwglMapper.getRwData", map, grid);
		}
		
	/**
	 * 编辑任务数据
	 * @param fr
	 */
	public void editRw(FxRwgl fr) {
		
		daoHelper.update(fr);
	}

	/**
	 * 新增任务数据
	 * @param fr
	 */
	public void newRw(FxRwgl fr) {
		//rwCanDel(fr.getXh());
		daoHelper.insert(fr);
	
	}
	/**
	 * 判断站点是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 
	private void rwCanDel(Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxRwglMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("同一站点只能存在一条吗，无法重复添加！");
		}
	}*/
	/**获取被编辑数据
	 * @param xh
	 * @return
	 */
	public FxRwgl getRw(Long xh) {
		return daoHelper.findObject(FxRwgl.class, xh);
	}
	
	//////////////////////////指标service
	/**
	 * 指标管理数据
	 * @param map
	 * @param grid
	 */
public void getZbData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxZsglMapper.getZsData", map, grid);
	}
	
/**
 * 编辑任务数据
 * @param fr
 */
public void editZb(FxZsgl fz) {
	
	daoHelper.update(fz);
}

/**
 * 新增任务数据
 * @param fr
 */
public void newZb(FxZsgl fz) {
//	rwCanDel(fz.getXh());
	daoHelper.insert(fz);

}
/**
 * 判断站点是否重复
 * @param sfrq 收费日期
 * @param xh null:新增
 */
private void ZbCanDel(Long xh){
	Map<String,Object> map = new HashMap<String,Object>(3);
	
	if(xh!=null){
		map.put("xh", xh);
	}
	AuthUserVO user = UserUtils.getAuthUser();
	map.put("sszd",user.getDeptId());
	//判断是否 已存在
	Integer count = (Integer)daoHelper.findObject("mapper.FxZsglMapper.getCanDel", map);
	if(count>0){
		throw new RuntimeException("同一站点只能存在一条吗，无法重复添加！");
	}
}
/**获取被编辑数据
 * @param xh
 * @return
 */
public FxZsgl getZb(Long xh) {
	return daoHelper.findObject(FxZsgl.class, xh);
}

}

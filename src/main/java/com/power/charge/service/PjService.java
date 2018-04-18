package com.power.charge.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.service.UserUtils;
import com.power.charge.entity.LockEnum;
import com.power.common.entity.FxPjgl;
import com.power.common.mybatis.DaoHelper;
import com.power.common.mybatis.page.JqGrid;

@Service("PjService")
public class PjService {
	private static final Logger log = LoggerFactory.getLogger(lvTongDataService.class);
	@Autowired
	private DaoHelper daoHelper;
	
	public void getPjData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxPjglMapper.getPjData", map, grid);
	}
	/**
	 * 票据被编辑信息获取
	 */
	public FxPjgl getPjInfo(Long xh) {
		return daoHelper.findObject(FxPjgl.class, xh);
	}
	
	/**
	 * 票据编辑
	 */
	public void editPj(FxPjgl yh) {
		jzclYbCanDel(yh.getSfrq(),yh.getXh());
		daoHelper.update(yh);
	}

	/**
	 * 票据新增
	 */
	public void newPj(FxPjgl yh) {
		jzclYbCanDel(yh.getSfrq(),null);
		daoHelper.insert(yh);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void jzclYbCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxPjglMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	/**
	 * 票据删除
	 */
	public void delPj(Long xh) {
		daoHelper.delete(FxPjgl.class, xh);
	}
	public void editPjLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxPjgl fp = daoHelper.findObject(FxPjgl.class, new Long(xh));
			fp.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(fp);
		}
	}
}

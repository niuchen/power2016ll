package com.power.charge.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.KeyValue;
import com.power.base.sys.service.UserUtils;
import com.power.charge.entity.LockEnum;
import com.power.common.entity.FxJdxmz;
import com.power.common.entity.SzJdxmz;
import com.power.common.mybatis.DaoHelper;
import com.power.common.mybatis.page.JqGrid;

@Service("JdxmzService")
public class JdxmzService {
@Autowired
private DaoHelper daoHelper;



	public void getjdxmzData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.SzJdxmzMapper.getJdxmzData", map, grid);
	}


	public void editJdsz(SzJdxmz sj) {
		jdmzCanDel(sj.getSjmc(),sj.getXh());
		daoHelper.update(sj);
		
	}


	public void newJdsz(SzJdxmz sj) {
		jdmzCanDel(sj.getSjmc(),null);
		daoHelper.insert(sj);
	}


	public void delJdsz(Integer xh) {
		daoHelper.delete(SzJdxmz.class, xh);
	}


	public SzJdxmz getJdszFormInfo(Integer xh) {
		return daoHelper.findObject(SzJdxmz.class, xh);
	}

	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void jdmzCanDel(String sjmc,Integer xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sjmc", sjmc);
		if(xh!=null){
			map.put("xh", xh);
		}
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.SzJdxmzMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("事件名称不能重复！");
		}
	}

	public void editjdszLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			SzJdxmz sj = daoHelper.findObject(SzJdxmz.class, new Long(xh));
			sj.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(sj);
		}
	}

///////////阶段性免征数据
	public void getjdxmzDaData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxJdxmzMapper.getJdxmzData", map, grid);
	}


	public FxJdxmz getJddataFormInfo(Long xh) {
		return daoHelper.findObject(FxJdxmz.class, xh);
	}
	
	/**
	 * 免征
	 * 限制时间段，每天录入一条
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 * @param sjxh : 事件编号
	 */
	private void allowReport(Date sfrq,Long xh,Integer sjxh){
		Integer mz = (Integer)daoHelper.findObject("mapper.FxJdxmzMapper.allowReport",sjxh);
		if(mz== null || mz<1){//
			throw new RuntimeException("免征时间段已截止或未开始！");
		}
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxJdxmzMapper.isExist", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	public void editJdData(FxJdxmz fj) {
		allowReport(fj.getSfrq(),fj.getXh(),Integer.valueOf(fj.getMzsj()));
		daoHelper.update(fj);
	}
	public void newJdData(FxJdxmz fj) {
		allowReport(fj.getSfrq(),null,Integer.valueOf(fj.getMzsj()));
		daoHelper.insert(fj);
	}


	public void delJdData(Long xh) {
		daoHelper.delete(FxJdxmz.class, xh);
	}


	public void editjdDataLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxJdxmz sj = daoHelper.findObject(FxJdxmz.class, new Long(xh));
			sj.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(sj);
		}
	}
	public List<KeyValue> getMzsjTree(){
		return daoHelper.findAll("mapper.SzJdxmzMapper.getMzsjTree");
	}
}

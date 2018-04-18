package com.power.charge.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.service.UserUtils;
import com.power.charge.entity.LockEnum;
import com.power.common.entity.FxRcetcmx;
import com.power.common.entity.FxRcclhz;
import com.power.common.entity.FxRcmtcmx;
import com.power.common.entity.FxRcsj;
import com.power.common.entity.FxSfrcsj;
import com.power.common.mybatis.DaoHelper;
import com.power.common.mybatis.page.JqGrid;

/**
 * 项目名称：power2016 <br>
 * 类名称：ChargeService <br>
 * 创建时间：2017-3-28 上午11:42:35 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("chargeService")
public class ChargeService {
	@Autowired
	private DaoHelper daoHelper;
	
	
	/**
	 * @param xh
	 * @return
	 */
	public FxRcsj getRcsj(Long xh) {
		return daoHelper.findObject(FxRcsj.class, xh);
	}
	/**
	 * 编辑日常数据上报
	 * @param fr
	 */
	public void editRcsj(FxRcsj fr) {
		rcsjCanDel(fr.getSfrq(),fr.getXh());
		daoHelper.update(fr);
	}

	/**
	 * 新增日常数据上报
	 * @param fr
	 */
	public void newRcsj(FxRcsj fr) {
		rcsjCanDel(fr.getSfrq(),null);
		daoHelper.insert(fr);
	
	}
	
	/**
	 *删除日常数据上报
	 *@param xh
	 *@return
	 * */
	public void delRcsj(Long xh) {
		daoHelper.delete(FxRcsj.class, xh);
	}
	
	/**
	 * 判断日期是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void rcsjCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxRcsjMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	
	/**
	 * 日常数据上报数据
	 * @param map
	 * @param grid
	 */
	public void getRcsjData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxRcsjMapper.getRcsjData", map, grid);
	}
	
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editRcsjLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxRcsj fr = daoHelper.findObject(FxRcsj.class, new Long(xh));
			fr.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(fr);
		}
	}
	
	///////////////////////////////etc日常
	
	/**
	 * @param xh
	 * @return
	 */
	public FxRcetcmx getEtcRcsj(Long xh) {
		return daoHelper.findObject(FxRcetcmx.class, xh);
	}
	/**
	 * 编辑日常数据上报
	 * @param fr
	 */
	public void editEtcRcsj(FxRcetcmx fr) {
		AuthUserVO user = UserUtils.getAuthUser();
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sszd", user.getDeptId());
		map.put("sfrq", fr.getSfrq());
		//daoHelper.delete("mapper.FxRcsjMapper.delMtc", map);
		etcrcsjCanDel(fr.getSfrq(),fr.getXh());
		daoHelper.update(fr); 
	}

	/**
	 * 新增日常数据上报
	 * @param fr
	 */
	public void newEtcRcsj(FxRcetcmx fr) {
		etcrcsjCanDel(fr.getSfrq(),null);
		daoHelper.insert(fr);
	
	}
	
	/**1
	 *删除日常数据上报
	 *@param xh
	 *@return
	 * */
	public void delEtcRcsj(Long xh) {
		daoHelper.delete(FxRcetcmx.class, xh);
	}
	
	/**
	 * 判断日期是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void etcrcsjCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxRcetcmxMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	
	/**
	 * 日常数据上报数据
	 * @param map
	 * @param grid
	 */
	public void getEtcRcsjData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxRcetcmxMapper.getEtcRcsjData", map, grid);
	}
	
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editEtcRcsjLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxRcetcmx fr = daoHelper.findObject(FxRcetcmx.class, new Long(xh));
			fr.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(fr);
		}
	}
	
	///////////////////////收费日常
	
	/**
	 * @param xh
	 * @return
	 */
	public FxRcclhz getRcclhz(Long xh) {
		return daoHelper.findObject(FxRcclhz.class, xh);
	}
	/**
	 * 编辑日常数据上报
	 * @param fr
	 */
	public void editRcclhz(FxRcclhz fr) {
		AuthUserVO user = UserUtils.getAuthUser();
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sszd", user.getDeptId());
		map.put("sfrq", fr.getSfrq());
		//daoHelper.delete("mapper.FxRcsjMapper.delMtc", map);
		rcclhzCanDel(fr.getSfrq(),fr.getXh());
		daoHelper.update(fr);
	}

	/**
	 * 新增日常数据上报
	 * @param fr
	 */
	public void newRcclhz(FxRcclhz fr) {
		rcclhzCanDel(fr.getSfrq(),null);
		daoHelper.insert(fr);
	
	}
	
	/**
	 *删除日常数据上报
	 *@param xh
	 *@return
	 * */
	public void delRcclhz(Long xh) {
		daoHelper.delete(FxRcclhz.class, xh);
	}
	
	/**
	 * 判断日期是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void rcclhzCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxRcclhzMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	
	/**
	 * 日常数据上报数据
	 * @param map
	 * @param grid
	 */
	public void getRcclhzData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxRcclhzMapper.getRcclhzData", map, grid);
	}
	
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editRcclhzLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxRcclhz fr = daoHelper.findObject(FxRcclhz.class, new Long(xh));
			fr.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(fr);
		}
	}
	///////////////////////
	

	/**
	 * @param xh
	 * @return
	 */
	public FxRcmtcmx getMtcRcsj(Long xh) {
		return daoHelper.findObject(FxRcmtcmx.class, xh);
	}
	/**
	 * 编辑日常数据上报
	 * @param fr
	 */
	public void editMtcRcsj(FxRcmtcmx fr) {
		AuthUserVO user = UserUtils.getAuthUser();
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sszd", user.getDeptId());
		map.put("sfrq", fr.getSfrq());
		//daoHelper.delete("mapper.FxRcsjMapper.delMtc", map);
		mtcrcsjCanDel(fr.getSfrq(),fr.getXh());
		daoHelper.update(fr); 
	}

	/**
	 * 新增日常数据上报
	 * @param fr
	 */
	public void newMtcRcsj(FxRcmtcmx fr) {
		mtcrcsjCanDel(fr.getSfrq(),null);
		daoHelper.insert(fr);
	
	}
	
	/**1
	 *删除日常数据上报
	 *@param xh
	 *@return
	 * */
	public void delMtcRcsj(Long xh) {
		daoHelper.delete(FxRcmtcmx.class, xh);
	}
	
	/**
	 * 判断日期是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void mtcrcsjCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxRcmtcmxMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	
	/**
	 * 日常数据上报数据
	 * @param map
	 * @param grid
	 */
	public void getMtcRcsjData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxRcmtcmxMapper.getMtcRcsjData", map, grid);
	}
	
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editMtcRcsjLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxRcmtcmx fr = daoHelper.findObject(FxRcmtcmx.class, new Long(xh));
			fr.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(fr);
		}
	}
}

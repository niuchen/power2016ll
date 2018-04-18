package com.power.charge.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.service.UserUtils;
import com.power.charge.entity.LockEnum;
import com.power.common.entity.FxCgcl;
import com.power.common.entity.FxChsjYb;
import com.power.common.entity.FxCzfc;
import com.power.common.entity.FxHmd;
import com.power.common.entity.FxIck;
import com.power.common.entity.FxJzclYb;
import com.power.common.entity.FxLsk;
import com.power.common.entity.FxMfclYb;
import com.power.common.entity.FxPjyb;
import com.power.common.entity.FxQzzh;
import com.power.common.mybatis.DaoHelper;
import com.power.common.mybatis.page.JqGrid;

/**
 * 项目名称：power2016 <br>
 * 类名称：MonthService <br>
 * 创建时间：2017-3-28 下午3:16:39 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("monthService")
public class MonthService {
	@Autowired
	private DaoHelper daoHelper;
	/**
	 * @param xh
	 * @return
	 */
	public FxJzclYb getJzclYbInfo(Long xh) {
		return daoHelper.findObject(FxJzclYb.class, xh);
	}

	/**
	 * @param xh
	 */
	public void delJzclYb(Long xh) {
		daoHelper.delete(FxJzclYb.class, xh);
	}

	/**
	 * @param yb
	 */
	public void editJzclYb(FxJzclYb yb) {
		jzclYbCanDel(yb.getSfrq(),yb.getXh());
		daoHelper.update(yb);
	}

	/**
	 * @param yb
	 */
	public void newJzclYb(FxJzclYb yb) {
		jzclYbCanDel(yb.getSfrq(),null);
		daoHelper.insert(yb);
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
		Integer count = (Integer)daoHelper.findObject("mapper.FxJzclYbMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	/**
	 * @param map
	 * @param grid
	 */
	public void getJzclYbData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxJzclYbMapper.getJzclYbData", map, grid);
	}
	
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editJzclYbLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxJzclYb jzcl = daoHelper.findObject(FxJzclYb.class, new Long(xh));
			jzcl.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(jzcl);
		}
	}
	
	//////////////////////免费车辆月报
	
	
	
	/**
	 * 免费车辆列表显示
	 * @param map
	 * @param grid分页
	 */
	public void getMfclData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxMfclYbMapper.getMfclData", map, grid);
	}
	
	/**
	 * 
	 * @param xh
	 * @return
	 */
	public FxMfclYb getMfclYbInfo(Long xh) {
		return daoHelper.findObject(FxMfclYb.class, xh);
	}
	/**
	 * 免费车辆编辑
	 * @param yb
	 */
	public void editMfcl(FxMfclYb yb) {
		mfclCanDel(yb.getSfrq(),yb.getXh());
		daoHelper.update(yb);
	}
	/**
	 * 免费车辆新增
	 * @param yb
	 */
	public void newMfcl(FxMfclYb yb) {
		mfclCanDel(yb.getSfrq(),null);
		daoHelper.insert(yb);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void mfclCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxMfclYbMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	public void delMfclyb(Long xh) {
		daoHelper.delete(FxMfclYb.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editmfclYbLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxMfclYb mfcl = daoHelper.findObject(FxMfclYb.class, new Long(xh));
			mfcl.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(mfcl);
		}
	}
///////////////////////////拆后数据月报
	/**
	 * 拆后数据新增
	 * @param yb
	 */
	public void newChsj(FxChsjYb yb) {
		chsjCanDel(yb.getSfrq(),null,yb.getLdbh());
		daoHelper.insert(yb);
	}

	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void chsjCanDel(Date sfrq,Long xh,String ldbh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getOrgId());
		map.put("ldbh",ldbh);
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxChsjYbMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	/**
	 * 拆后数据编辑
	 * @param yb
	 */
	public void editChsj(FxChsjYb yb) {
		chsjCanDel(yb.getSfrq(),yb.getXh(),yb.getLdbh());
		daoHelper.update(yb);
	}
	/**获取被编辑数据
	 * @param map
	 * @param grid
	 */
	public FxChsjYb getChsjInfo(Long xh) {
		return daoHelper.findObject(FxChsjYb.class, xh);
	}
	/**
	 * 拆后数据列表展示
	 * @param yb
	 */
	public void getChsjData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxChsjYbMapper.getChsjData", map, grid);
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	public void delChsjyb(Long xh) {
		daoHelper.delete(FxChsjYb.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editchsjYbLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxChsjYb chsj = daoHelper.findObject(FxChsjYb.class, new Long(xh));
			chsj.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(chsj);
		}
	}
	//////////////////////流失卡
	/**
	 * 流失卡车辆列表显示
	 * @param map
	 * @param grid分页
	 */
	public void getLskData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxLskMapper.getLskData", map, grid);
	}
	
	/**
	 * 
	 * @param xh
	 * @return
	 */
	public FxLsk getLskInfo(Long xh) {
		return daoHelper.findObject(FxLsk.class, xh);
	}
	/**
	 * 流失卡车辆编辑
	 * @param yb
	 */
	public void editLsk(FxLsk yb) {
		lskCanDel(yb.getSfrq(),yb.getXh());
		daoHelper.update(yb);
	}
	/**
	 * 流失卡车辆新增
	 * @param yb
	 */
	public void newLsk(FxLsk yb) {
		lskCanDel(yb.getSfrq(),null);
		daoHelper.insert(yb);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void lskCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		//Integer count = (Integer)daoHelper.findObject("mapper.FxLskMapper.getCanDel", map);
		//if(count>0){
		//	throw new RuntimeException("数据已上报，无需重复添加！");
		//}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	public void delLsk(Long xh) {
		daoHelper.delete(FxLsk.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editLskLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxLsk mfcl = daoHelper.findObject(FxLsk.class, new Long(xh));
			mfcl.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(mfcl);
		}
	}
/////////////////////////
	/**
	 * 拆账后列表显示
	 * @param map
	 * @param grid分页
	 */
	public void getCzfcData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxCzfcMapper.getCzfcData", map, grid);
	}
	
	/**
	 * 
	 * @param xh
	 * @return
	 */
	public FxCzfc getCzfcInfo(Long xh) {
		return daoHelper.findObject(FxCzfc.class, xh);
	}
	/**
	 * 拆账后编辑
	 * @param yb
	 */
	public void editCzfc(FxCzfc yb) {
		CzfcCanDel(yb.getSfrq(),yb.getXh(),yb.getLdbh());
		daoHelper.update(yb);
	}
	/**
	 * 拆账后新增
	 * @param yb
	 */
	public void newCzfc(FxCzfc yb) {
		CzfcCanDel(yb.getSfrq(),null,yb.getLdbh());
		daoHelper.insert(yb);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void CzfcCanDel(Date sfrq,Long xh,String ldbh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getOrgId());
		map.put("ldbh",ldbh);
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxCzfcMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	public void delCzfc(Long xh) {
		daoHelper.delete(FxCzfc.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editCzfcLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxCzfc mfcl = daoHelper.findObject(FxCzfc.class, new Long(xh));
			mfcl.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(mfcl);
		}
	}
	
	//////////////////////ic通行卡
	
	/**
	 * ic卡列表显示
	 * @param map
	 * @param grid分页
	 */
	public void getIckData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxIckMapper.getIckData", map, grid);
	}
	
	/**
	 * 
	 * @param xh
	 * @return
	 */
	public FxIck getIckInfo(Long xh) {
		return daoHelper.findObject(FxIck.class, xh);
	}
	/**
	 * ic卡编辑
	 * @param yb
	 */
	public void editIck(FxIck yb) {
		IckCanDel(yb.getSfrq(),yb.getXh());
		daoHelper.update(yb);
	}
	/**
	 * ic卡新增
	 * @param yb
	 */
	public void newIck(FxIck yb) {
		IckCanDel(yb.getSfrq(),null);
		daoHelper.insert(yb);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void IckCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxIckMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	public void delIck(Long xh) {
		daoHelper.delete(FxIck.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editIckLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxIck mfcl = daoHelper.findObject(FxIck.class, new Long(xh));
			mfcl.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(mfcl);
		}
	}
	
	///////////////////////票据月报
	/**
	 * 票据月报列表显示
	 * @param map
	 * @param grid分页
	 */
	public void getPjybData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxPjybMapper.getPjybData", map, grid);
	}
	
	/**
	 * 
	 * @param xh
	 * @return
	 */
	public FxPjyb getPjybInfo(Long xh) {
		return daoHelper.findObject(FxPjyb.class, xh);
	}
	/**
	 * 票据月报编辑
	 * @param yb
	 */
	public void editPjyb(FxPjyb yb) {
		PjybCanDel(yb.getSfrq(),yb.getXh());
		daoHelper.update(yb);
	}
	/**
	 * 票据月报新增
	 * @param yb
	 */
	public void newPjyb(FxPjyb yb) {
		PjybCanDel(yb.getSfrq(),null);
		daoHelper.insert(yb);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void PjybCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxPjybMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	public void delPjyb(Long xh) {
		daoHelper.delete(FxPjyb.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editPjybLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxPjyb mfcl = daoHelper.findObject(FxPjyb.class, new Long(xh));
			mfcl.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(mfcl);
		}
	}
	
	
	////////////////////////////////////灰名单
	
	/**
	 * 灰名单车辆列表显示
	 * @param map
	 * @param grid分页
	 */
	public void getHmdData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxHmdMapper.getHmdData", map, grid);
	}
	
	/**
	 * 
	 * @param xh
	 * @return
	 */
	public FxHmd getHmdInfo(Long xh) {
		return daoHelper.findObject(FxHmd.class, xh);
	}
	/**
	 * 灰名单车辆编辑
	 * @param yb
	 */
	public void editHmd(FxHmd yb) {
		hmdCanDel(yb.getSfrq(),yb.getXh());
		daoHelper.update(yb);
	}
	/**
	 * 灰名单车辆新增
	 * @param yb
	 */
	public void newHmd(FxHmd yb) {
		hmdCanDel(yb.getSfrq(),null);
		daoHelper.insert(yb);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void hmdCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
	//	Integer count = (Integer)daoHelper.findObject("mapper.FxHmdMapper.getCanDel", map);
	//	if(count>0){
	//		throw new RuntimeException("数据已上报，无需重复添加！");
	//	}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	public void delHmd(Long xh) {
		daoHelper.delete(FxHmd.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editHmdLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxHmd mfcl = daoHelper.findObject(FxHmd.class, new Long(xh));
			mfcl.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(mfcl);
		}
	}
	
	////////////////////////////////////闯关车辆
	
	/**
	 * 闯关车辆车辆列表显示
	 * @param map
	 * @param grid分页
	 */
	public void getCgclData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxCgclMapper.getCgclData", map, grid);
	}
	
	/**
	 * 
	 * @param xh
	 * @return
	 */
	public FxCgcl getCgclInfo(Long xh) {
		return daoHelper.findObject(FxCgcl.class, xh);
	}
	/**
	 * 闯关车辆车辆编辑
	 * @param yb
	 */
	public void editCgcl(FxCgcl yb) {
		cgclCanDel(yb.getSfrq(),yb.getXh());
		daoHelper.update(yb);
	}
	/**
	 * 闯关车辆车辆新增
	 * @param yb
	 */
	public void newCgcl(FxCgcl yb) {
		cgclCanDel(yb.getSfrq(),null);
		daoHelper.insert(yb);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void cgclCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
	//	Integer count = (Integer)daoHelper.findObject("mapper.FxCgclMapper.getCanDel", map);
	//	if(count>0){
	//		throw new RuntimeException("数据已上报，无需重复添加！");
	//	}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	public void delCgcl(Long xh) {
		daoHelper.delete(FxCgcl.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editCgclLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxCgcl mfcl = daoHelper.findObject(FxCgcl.class, new Long(xh));
			mfcl.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(mfcl);
		}
	}
	
	
	////////////////////////////////////强制组合
	
	/**
	 * 强制组合车辆列表显示
	 * @param map
	 * @param grid分页
	 */
	public void getQzzhData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxQzzhMapper.getQzzhData", map, grid);
	}
	
	/**
	 * 
	 * @param xh
	 * @return
	 */
	public FxQzzh getQzzhInfo(Long xh) {
		return daoHelper.findObject(FxQzzh.class, xh);
	}
	/**
	 * 强制组合车辆编辑
	 * @param yb
	 */
	public void editQzzh(FxQzzh yb) {
		qzzhCanDel(yb.getSfrq(),yb.getXh());
		daoHelper.update(yb);
	}
	/**
	 * 强制组合车辆新增
	 * @param yb
	 */
	public void newQzzh(FxQzzh yb) {
		qzzhCanDel(yb.getSfrq(),null);
		daoHelper.insert(yb);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void qzzhCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
	//	Integer count = (Integer)daoHelper.findObject("mapper.FxQzzhMapper.getCanDel", map);
	//	if(count>0){
	//		throw new RuntimeException("数据已上报，无需重复添加！");
	//	}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	public void delQzzh(Long xh) {
		daoHelper.delete(FxQzzh.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editQzzhLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxQzzh mfcl = daoHelper.findObject(FxQzzh.class, new Long(xh));
			mfcl.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(mfcl);
		}
	}
	
}

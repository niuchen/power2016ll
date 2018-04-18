package com.power.charge.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.power.app.entity.YhsjVO;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.service.UserUtils;
import com.power.charge.entity.LockEnum;
import com.power.common.entity.FxEtcsj;
import com.power.common.entity.FxJzclYb;
import com.power.common.entity.FxLtcl;
import com.power.common.entity.FxLtsj;
import com.power.common.entity.FxYdsj;
import com.power.common.entity.FxYhsj;
import com.power.common.mybatis.DaoHelper;
import com.power.common.mybatis.page.JqGrid;









/**
 * 项目名称：power2016 <br>
 * 类名称：lvTongDataService <br>
 * 创建时间：2017-3-28 上午11:42:35 <br>
 * @author xt <br>
 * @version 1.0
 */
@Service("lvTongDataService")
public class lvTongDataService {
	
	private static final Logger log = LoggerFactory.getLogger(lvTongDataService.class);
	@Autowired
	private DaoHelper daoHelper;
	
	
	
	/**
	 * 银行列表查询
	 * @param map
	 * @param grid分页
	 */
	public void getYhsjData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxYhsjMapper.getYhsjData", map, grid);
	}
	/**
	 * 银行被编辑信息获取
	 */
	public FxYhsj getYhsjInfo(Long xh) {
		return daoHelper.findObject(FxYhsj.class, xh);
	}

	/**
	 * 银行编辑
	 */
	public void editYhsj(FxYhsj yh) {
		if(yh.getBz1().equals(yh.getBz2())){
			throw new RuntimeException("班组不可重复！");
		}
		yhsjCanDel(yh.getSfrq(),yh.getXh());
		daoHelper.update(yh);
	}

	/**
	 * 银行新增
	 */
	public void newYhsj(FxYhsj yh) {
		if(yh.getBz1().equals(yh.getBz2())){
			throw new RuntimeException("班组不可重复！");
		}
		yhsjCanDel(yh.getSfrq(),null);
		daoHelper.insert(yh);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void yhsjCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxYhsjMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}

	/**
	 * 银行删除
	 */
	public void delYhsj(Long xh) {
		daoHelper.delete(FxYhsj.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void edityhsjLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxYhsj yhsj = daoHelper.findObject(FxYhsj.class, new Long(xh));
			yhsj.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(yhsj);
		}
	}
	
////////////////////////绿通Service
	/**
	 * 绿通列表查询
	 * @param map
	 * @param grid分页
	 */
	public void getLtsjData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxLtsjMapper.getLtsjData", map, grid);
	}
	/**
	 * 绿通被编辑信息获取
	 */
	public FxLtsj getLtsjInfo(Long xh) {
		return daoHelper.findObject(FxLtsj.class, xh);
	}
	
	/**
	 * 绿通编辑
	 */
	public void editLtsj(FxLtsj lt) {
		ltsjCanDel(lt.getSfrq(),lt.getXh());
		daoHelper.update(lt);
	}

	/**
	 * 绿通新增
	 */
	public void newLtsj(FxLtsj lt) {
		ltsjCanDel(lt.getSfrq(),null);
		daoHelper.insert(lt);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void ltsjCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxLtsjMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}
	/**
	 *  绿通删除
	 */
	public void delLtsj(Long xh) {
		daoHelper.delete(FxLtsj.class, xh);
}
	
	/**
	 * 绿通数据锁定
	 * @param xhs
	 */
	public void editLtsjLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxLtsj ltsj = daoHelper.findObject(FxLtsj.class, new Long(xh));
			ltsj.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(ltsj);
		}
	}
	
	///////////////////移动支付
	/**
	 * 移动支付数据列表
	 * @param map
	 * @param grid
	 */
	public void getYdsjData(HashMap<String, Object> map, JqGrid grid) {
		daoHelper.findRows("mapper.FxYdsjMapper.getYdsjData", map, grid);
		
	}
	/**
	 * 银行被编辑信息获取
	 */
	public FxYdsj getYdsjInfo(Long xh) {
		return daoHelper.findObject(FxYdsj.class, xh);
	}

	/**
	 * 银行编辑
	 */
	public void editYdsj(FxYdsj yh) {
		if(yh.getBz1().equals(yh.getBz2())){
			throw new RuntimeException("班组不可重复！");
		}
		ydsjCanDel(yh.getSfrq(),yh.getXh());
		daoHelper.update(yh);
	}

	/**
	 * 银行新增
	 */
	public void newYdsj(FxYdsj yh) {
		if(yh.getBz1().equals(yh.getBz2())){
			throw new RuntimeException("班组不可重复！");
		}
		ydsjCanDel(yh.getSfrq(),null);
		daoHelper.insert(yh);
	}
	/**
	 * 判断是否重复
	 * @param sfrq 收费日期
	 * @param xh null:新增
	 */
	private void ydsjCanDel(Date sfrq,Long xh){
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("sfrq", sfrq);
		if(xh!=null){
			map.put("xh", xh);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		map.put("sszd",user.getDeptId());
		//判断是否 已存在
		Integer count = (Integer)daoHelper.findObject("mapper.FxYdsjMapper.getCanDel", map);
		if(count>0){
			throw new RuntimeException("数据已上报，无需重复添加！");
		}
	}

	/**
	 * 银行删除
	 */
	public void delYdsj(Long xh) {
		daoHelper.delete(FxYdsj.class, xh);
	}
	/**
	 * 数据锁定
	 * @param xhs
	 */
	public void editydsjLock(String xhs) {
		Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
		for (String xh : xhList) {//修改数据状态
			FxYdsj yhsj = daoHelper.findObject(FxYdsj.class, new Long(xh));
			yhsj.setZt(LockEnum.LOCK.getIndex());//锁定状态
			daoHelper.updateSelective(yhsj);
		}
	}
	
	//////////////////////////计重车辆上报
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
	
	//查询绿通车辆数量、金额
		public void getLtclData(HashMap<String, Object> map, JqGrid grid) {
			daoHelper.findRows("mapper.FxLtclMapper.getLtclData", map, grid);
		}
		
		public FxLtcl getLtclInfo(Long xh) {
			return daoHelper.findObject(FxLtcl.class, xh);
		}
		
		public void editLtcl(FxLtcl ltcl) {
			ltclCanDel(ltcl.getSfrq(),ltcl.getXh());
			daoHelper.update(ltcl);
		}


		public void newLtcl(FxLtcl ltcl) {
			ltclCanDel(ltcl.getSfrq(),null);
			daoHelper.insert(ltcl);
		}
		
		private void ltclCanDel(Date sfrq,Long xh){
			Map<String,Object> map = new HashMap<String,Object>(3);
			map.put("sfrq", sfrq);
			if(xh!=null){
				map.put("xh", xh);
			}
			AuthUserVO user = UserUtils.getAuthUser();
			map.put("sszd",user.getDeptId());
			//判断是否 已存在
			Integer count = (Integer)daoHelper.findObject("mapper.FxLtclMapper.getCanDel", map);
			if(count>0){
				throw new RuntimeException("数据已上报，无需重复添加！");
			}
		}
		
		public void delLtcl(Long xh) {
			daoHelper.delete(FxLtcl.class, xh);
		}
		
		public void editLtclLock(String xhs) {
			Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
			for (String xh : xhList) {//修改数据状态
				FxLtcl ltcl = daoHelper.findObject(FxLtcl.class, new Long(xh));
				ltcl.setZt(LockEnum.LOCK.getIndex());//锁定状态
				daoHelper.updateSelective(ltcl);
			}
		}
	
		
		////////////////////////ETCSJ

		/**
		 * ETC列表查询
		 * @param map
		 * @param grid分页
		 */
		public void getEtcsjData(HashMap<String, Object> map, JqGrid grid) {
			daoHelper.findRows("mapper.FxEtcsjMapper.getEtcsjData", map, grid);
		}
		/**
		 * ETC被编辑信息获取
		 */
		public FxEtcsj getEtcsjInfo(Long xh) {
			return daoHelper.findObject(FxEtcsj.class, xh);
		}

		/**
		 * ETC编辑
		 */
		public void editEtcsj(FxEtcsj yh) {
			if(yh.getBz1().equals(yh.getBz2())){
				throw new RuntimeException("班组不可重复！");
			}
			etcsjCanDel(yh.getSfrq(),yh.getXh());
			daoHelper.update(yh);
		}

		/**
		 * ETC新增
		 */
		public void newEtcsj(FxEtcsj yh) {
			if(yh.getBz1().equals(yh.getBz2())){
				throw new RuntimeException("班组不可重复！");
			}
			etcsjCanDel(yh.getSfrq(),null);
			daoHelper.insert(yh);
		}
		/**
		 * 判断是否重复
		 * @param sfrq 收费日期
		 * @param xh null:新增
		 */
		private void etcsjCanDel(Date sfrq,Long xh){
			Map<String,Object> map = new HashMap<String,Object>(3);
			map.put("sfrq", sfrq);
			if(xh!=null){
				map.put("xh", xh);
			}
			AuthUserVO user = UserUtils.getAuthUser();
			map.put("sszd",user.getDeptId());
			//判断是否 已存在
			Integer count = (Integer)daoHelper.findObject("mapper.FxEtcsjMapper.getCanDel", map);
			if(count>0){
				throw new RuntimeException("数据已上报，无需重复添加！");
			}
		}

		/**
		 * ETC删除
		 */
		public void delEtcsj(Long xh) {
			daoHelper.delete(FxEtcsj.class, xh);
		}
		/**
		 * 数据锁定
		 * @param xhs
		 */
		public void editetcsjLock(String xhs) {
			Iterable<String> xhList = Splitter.on(',').trimResults().omitEmptyStrings().split(xhs);
			for (String xh : xhList) {//修改数据状态
				FxEtcsj etcsj = daoHelper.findObject(FxEtcsj.class, new Long(xh));
				etcsj.setZt(LockEnum.LOCK.getIndex());//锁定状态
				daoHelper.updateSelective(etcsj);
			}
		}
}


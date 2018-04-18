package com.power.charge.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.service.UserUtils;
import com.power.charge.entity.LockEnum;
import com.power.charge.service.lvTongDataService;
import com.power.common.entity.FxEtcsj;
import com.power.common.entity.FxJzclYb;
import com.power.common.entity.FxLtcl;
import com.power.common.entity.FxLtsj;
import com.power.common.entity.FxYdsj;
import com.power.common.entity.FxYhsj;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.springmvc.ExtReturn;
import com.power.index.service.IndexService;
/**
 * 项目名称：power2016 <br>
 * 类名称：ChargeController <br>
 * 创建时间：2017-3-28 上午11:36:52 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Controller
@RequestMapping("charge")
public class lvTongDataController {
	private final static Logger log = LoggerFactory.getLogger(lvTongDataService.class);
	@Autowired
	private lvTongDataService lvTongDataService;
	@Autowired
	private IndexService indexService;
	private String LTSJ = "charge/ltsj/";
	private String YHSJ = "charge/yhsj/";
	private String YDSJ = "charge/ydsj/";
	private String JZCL = "charge/jzcl/";
	private String LTCL = "charge/ltcl/";
	private String ETCSJ = "charge/etcsj/";
	/**
	 * 时间格式规范
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder){
		CustomDateEditor editor=new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"),true);
		binder.registerCustomEditor(Date.class,editor);
	}
	
	/**
	 * 银行数据上报管理首页
	 * @return
	 */
	@RequestMapping("yhsjIndex")
	public String yhsjIndex(){
		return YHSJ+"index";
	}
	/**
	 * 银行数据列表查询
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("yhsjData")
	@ResponseBody
	public Object yhsjData(JqGrid grid,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId,
			@RequestParam(value="lock",required=false)Integer lock){
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(st)){
			map.put("st",st);
		}
		if(StringUtils.isNoneBlank(et)){
			map.put("et",et);
		}
		if(null!=lock){//数据状态
			map.put("lock",lock);
		}
		//层级关系封装
		indexService.setOrgDeptParam(orgId, deptId, map);
		//
		if(StringUtils.isNoneBlank(grid.getSidx())){
			map.put("order", grid.getSidx()+" " + grid.getSord());//删除首个逗号
		}
		lvTongDataService.getYhsjData(map,grid);
		return grid;
	}
	
	
	/**
	 * 银行数据上报新增
	 * @return
	 * */
	@RequestMapping("newYhsj")
	public String newYhsj(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return YHSJ+"detail"; 
		}
		return YHSJ+"newYhsj";
	}
	
	
	/**
	 * 编辑YHSJ获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getYhsjFormJson")
	@ResponseBody
	public Object getYhsjFormJson(@RequestParam(value="keyValue")Long xh){
		FxYhsj yh = lvTongDataService.getYhsjInfo(xh);
		return new ExtReturn(true, yh);
	}
	/**
	 * 银行数据上报新增/编辑提交
	 * @param yh
	 * @return
	 * */
	@RequestMapping(value="newYhsjSubmit")
	@ResponseBody
	public Object newYhsjSubmit(FxYhsj yh){
		AuthUserVO user = UserUtils.getAuthUser();
		yh.setLrsj(new Date());
		yh.setLryxh(user.getUid());
		yh.setLrymc(user.getRealName());
		yh.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != yh.getXh()){
				lvTongDataService.editYhsj(yh);
			}else{
				lvTongDataService.newYhsj(yh);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("YhsjSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
//	
	/**
	 *银行删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delYhsj",method = RequestMethod.POST)
	@ResponseBody
	public Object delYhsj(@RequestParam("keyValue")Long xh){
		try {	
			lvTongDataService.delYhsj(xh);
		} catch (Exception e) {
			log.error("delYhsj error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="yhsjLock",method = RequestMethod.POST)
	@ResponseBody
	public Object yhsjLock(@RequestParam("xhs")String xhs){
		try {	
			lvTongDataService.edityhsjLock(xhs);
		} catch (Exception e) {
			log.error("chsjYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
////////////////////////////////////绿通管理controller
	/**
	 * 绿通数据上报管理首页
	 * @return
	 */
	@RequestMapping("ltsjIndex")
	public String ltsjIndex(){
		return LTSJ+"index";
	}
	/**
	 * 绿通数据列表查询
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("ltsjData")
	@ResponseBody
	public Object ltsjData(JqGrid grid,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId,
			@RequestParam(value="lock",required=false)Integer lock){
		HashMap<String, Object> map = new HashMap<String, Object>(5);
		if(StringUtils.isNoneBlank(st)){
			map.put("st",st);
		}
		if(StringUtils.isNoneBlank(et)){
			map.put("et",et);
		}
		if(null!=lock){//数据状态
			map.put("lock",lock);
		}
		//层级关系封装
		indexService.setOrgDeptParam(orgId, deptId, map);
		//将排序所需字段存入map
		if(StringUtils.isNoneBlank(grid.getSidx())){
			map.put("order", grid.getSidx()+" " + grid.getSord());
		}
		//
		lvTongDataService.getLtsjData(map,grid);
		return grid;
	}
	
	/**
	 * 绿通数据上报新增
	 * @return
	 * */
	@RequestMapping("newLtsj")
	public String newLtsj(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return LTSJ+"detail"; 
		}
		model.addAttribute("curDate",new Date());
		return LTSJ+"newLtsj";
	}
	/**
	 * 编辑LTSJ获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getLtsjFormJson")
	@ResponseBody
	public Object getLtsjFormJson(@RequestParam(value="keyValue")Long xh){
		FxLtsj lt = lvTongDataService.getLtsjInfo(xh);
		return new ExtReturn(true, lt);
	}
	/**
	 * 绿通数据上报新增/编辑提交
	 * @param lt
	 * @return
	 * */
	@RequestMapping(value="newLtsjSubmit")
	@ResponseBody
	public Object newLtsjSubmit(FxLtsj lt){
	
		AuthUserVO user = UserUtils.getAuthUser();
		lt.setLrsj(new Date());
		lt.setLryxh(user.getUid());
		lt.setLrymc(user.getRealName());
		lt.setZt(LockEnum.UNLOCK.getIndex());
		try {
			if(null != lt.getXh()){
				lvTongDataService.editLtsj(lt);
			}else{
				lvTongDataService.newLtsj(lt);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("LtsjSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	/**
	 *绿通删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delLtsj",method = RequestMethod.POST)
	@ResponseBody
	public Object delLtsj(@RequestParam("keyValue")Long xh){
		try {	
			lvTongDataService.delLtsj(xh);
		} catch (Exception e) {
			log.error("delLtsj error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 *绿通数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="ltsjLock",method = RequestMethod.POST)
	@ResponseBody
	public Object ltsjLock(@RequestParam("xhs")String xhs){
		try {	
			lvTongDataService.editLtsjLock(xhs);
		} catch (Exception e) {
			log.error("ltsjLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}

	///////////////////////////////移动支付
	/**
	 * 移动支付上报管理首页
	 * @return
	 */
	@RequestMapping("ydsjIndex")
	public String ydsjIndex(){
		return YDSJ+"index";
	}
	
	/**
	 * 移动支付数据上报新增
	 * @return
	 * */
	@RequestMapping("newYdsj")
	public String newYdsj(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return YDSJ+"detail"; 
		}
		model.addAttribute("curDate",new Date());
		return YDSJ+"newYdsj";
	}
	/**
	 * 银行数据列表查询
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("ydsjData")
	@ResponseBody
	public Object ydsjData(JqGrid grid,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId,
			@RequestParam(value="lock",required=false)Integer lock){
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(st)){
			map.put("st",st);
		}
		if(StringUtils.isNoneBlank(et)){
			map.put("et",et);
		}
		if(null!=lock){//数据状态
			map.put("lock",lock);
		}
		//层级关系封装
		indexService.setOrgDeptParam(orgId, deptId, map);
		//
		if(StringUtils.isNoneBlank(grid.getSidx())){
			map.put("order", grid.getSidx()+" " + grid.getSord());//删除首个逗号
		}
		lvTongDataService.getYdsjData(map,grid);
		return grid;
	}
	
	
	
	
	/**
	 * 编辑YHSJ获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getYdsjFormJson")
	@ResponseBody
	public Object getYdsjFormJson(@RequestParam(value="keyValue")Long xh){
		FxYdsj yh = lvTongDataService.getYdsjInfo(xh);
		return new ExtReturn(true, yh);
	}
	/**
	 * 银行数据上报新增/编辑提交
	 * @param yh
	 * @return
	 * */
	@RequestMapping(value="newYdsjSubmit")
	@ResponseBody
	public Object newYdsjSubmit(FxYdsj yh){
		AuthUserVO user = UserUtils.getAuthUser();
		yh.setLrsj(new Date());
		yh.setLryxh(user.getUid());
		yh.setLrymc(user.getRealName());
		yh.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != yh.getXh()){
				lvTongDataService.editYdsj(yh);
			}else{
				lvTongDataService.newYdsj(yh);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("YhsjSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
//	
	/**
	 *银行删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delYdsj",method = RequestMethod.POST)
	@ResponseBody
	public Object delYdsj(@RequestParam("keyValue")Long xh){
		try {	
			lvTongDataService.delYdsj(xh);
		} catch (Exception e) {
			log.error("delYhsj error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="ydsjLock",method = RequestMethod.POST)
	@ResponseBody
	public Object ydsjLock(@RequestParam("xhs")String xhs){
		try {	
			lvTongDataService.editydsjLock(xhs);
		} catch (Exception e) {
			log.error("chsjYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	
	//////////////////////////
	/**
	 * 记重车辆管理首页
	 * @return
	 */
	@RequestMapping("jzclYbIndex")
	public String jzclYbIndex(){
		return JZCL+"index";
	}
	/**
	 * 记重车辆月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("jzclYbData")
	@ResponseBody
	public Object jzclYbData(JqGrid grid,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId,
			@RequestParam(value="lock",required=false)Integer lock){
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(st)){
			map.put("st",st);
		}
		if(StringUtils.isNoneBlank(et)){
			map.put("et",et);
		}
		if(null!=lock){//数据状态
			map.put("lock",lock);
		}
		//层级关系封装
		indexService.setOrgDeptParam(orgId, deptId, map);
		if(StringUtils.isNoneBlank(grid.getSidx())){
			map.put("order", grid.getSidx()+" " + grid.getSord());//删除首个逗号
		}
		//
		lvTongDataService.getJzclYbData(map,grid);
		return grid;
	}
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("jzclYbForm")
	public String jzclForm(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return JZCL+"detail"; 
		}
		return JZCL+"new";
	}
	
	/**
	 * 编辑JZCL获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getJzclYbFormJson")
	@ResponseBody
	public Object getJzclYbFormJson(@RequestParam(value="keyValue")Long xh){
		FxJzclYb yb = lvTongDataService.getJzclYbInfo(xh);
		return new ExtReturn(true, yb);
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * */
	@RequestMapping(value="jzclYbSubmit")
	@ResponseBody
	public Object jzclYbSubmit(FxJzclYb yb){
		AuthUserVO user = UserUtils.getAuthUser();
		yb.setLrsj(new Date());
		yb.setLryxh(user.getUid());
		yb.setLrymc(user.getRealName());
		yb.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != yb.getXh()){
				lvTongDataService.editJzclYb(yb);
			}else{
				lvTongDataService.newJzclYb(yb);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("jzclYbSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
//	
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delJzclYb",method = RequestMethod.POST)
	@ResponseBody
	public Object delJzclYb(@RequestParam("keyValue")Long xh){
		try {	
			lvTongDataService.delJzclYb(xh);
		} catch (Exception e) {
			log.error("delJzclYb error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="jzclYbLock",method = RequestMethod.POST)
	@ResponseBody
	public Object jzclYbLock(@RequestParam("xhs")String xhs){
		try {	
			lvTongDataService.editJzclYbLock(xhs);
		} catch (Exception e) {
			log.error("jzclYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	
	
	
	///////////////////绿通车辆
	
//	
//	
//	/**
//	 * 绿通车辆月报首页
//	 * @return
//	 */
//	@RequestMapping("ltclIndex")
//	public String ltclIndex(){
//		return LTCL+"index";
//	}
//	/**
//	 * 绿通车辆月报数据
//	 * @param colname
//	 * @param value
//	 * @param grid
//	 * @return
//	 * */
//	@RequestMapping("ltclData")
//	@ResponseBody
//	public Object ltclData(JqGrid grid,
//			@RequestParam(value="st",required=false)String st,
//			@RequestParam(value="et",required=false)String et,
//			@RequestParam(value="orgId",required=false)String orgId,
//			@RequestParam(value="deptId",required=false)String deptId,
//			@RequestParam(value="lock",required=false)Integer lock){
//		HashMap<String, Object> map = new HashMap<String, Object>(6);
//		if(StringUtils.isNoneBlank(st)){
//			map.put("st",st);
//		}
//		if(StringUtils.isNoneBlank(et)){
//			map.put("et",et);
//		}
//		if(null!=lock){//数据状态
//			map.put("lock",lock);
//		}
//		//层级关系封装
//		indexService.setOrgDeptParam(orgId, deptId, map);
//		if(StringUtils.isNoneBlank(grid.getSidx())){
//			map.put("order", grid.getSidx()+" " + grid.getSord());//删除首个逗号
//		}
//		//
//		lvTongDataService.getLtclData(map,grid);
//		return grid;
//	}
//	
//	/**
//	 * 新增
//	 * @return
//	 * */
//	@RequestMapping("LtclForm")
//	public String ltclForm(@RequestParam(required=false,value="detail")Integer detail,Model model){
//		AuthUserVO user = UserUtils.getAuthUser();
//		model.addAttribute("user", user);
//		if(detail!=null){
//			return LTCL+"detail"; 
//		}
//		return LTCL+"new";
//	}
//	
//	/**
//	 * 编辑JZCL获取数据
//	 * @param xh
//	 * @return
//	 * */
//	@RequestMapping("getLtclFormJson")
//	@ResponseBody
//	public Object getLtclFormJson(@RequestParam(value="keyValue")Long xh){
//		FxLtcl ltcl = lvTongDataService.getLtclInfo(xh);
//		return new ExtReturn(true, ltcl);
//	}
//	
//	/**
//	 * 新增/编辑提交
//	 * @param yb
//	 * @return
//	 * */
//	@RequestMapping(value="ltclSubmit")
//	@ResponseBody
//	public Object ltclSubmit(FxLtcl ltcl){
//		AuthUserVO user = UserUtils.getAuthUser();
//		ltcl.setLrsj(new Date());
//		ltcl.setLryxh(user.getUid());
//		ltcl.setLrymc(user.getRealName());
//		ltcl.setZt(LockEnum.UNLOCK.getIndex());
//		//
//		try {
//			if(null != ltcl.getXh()){
//				lvTongDataService.editLtcl(ltcl);
//			}else{
//				lvTongDataService.newLtcl(ltcl);
//			}
//			return new ExtReturn(true,"操作成功");
//		} catch (Exception e) {
//			log.error("ltclSubmit error:{}"+e);
//			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
//		}
//	}
//	
//	/**
//	 *删除
//	 *@param xh
//	 *@return
//	 * */
//	@RequestMapping(value="delLtcl",method = RequestMethod.POST)
//	@ResponseBody
//	public Object delLtcl(@RequestParam("keyValue")Long xh){
//		try {	
//			lvTongDataService.delLtcl(xh);
//		} catch (Exception e) {
//			log.error("delLtcl error:{}"+e);
//			return new ExtReturn(false,"操作失败");
//		}
//		return new ExtReturn(true,"操作成功");
//	}
//	/**
//	 *数据锁定
//	 *@param xh
//	 *@return
//	 * */
//	@RequestMapping(value="ltclLock",method = RequestMethod.POST)
//	@ResponseBody
//	public Object ltclLock(@RequestParam("xhs")String xhs){
//		try {	
//			lvTongDataService.editLtclLock(xhs);
//		} catch (Exception e) {
//			log.error("ltclLock error:{}"+e);
//			return new ExtReturn(false,"操作失败,"+e.getMessage());
//		}
//		return new ExtReturn(true,"操作成功");
//	}
//	
	
	/////////etcsj日数据上报
	/**
	 * ETC数据上报管理首页
	 * @return
	 */
	@RequestMapping("etcsjIndex")
	public String etcsjIndex(){
		return ETCSJ+"index";
	}
	/**
	 * ETC数据列表查询
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("etcsjData")
	@ResponseBody
	public Object etcsjData(JqGrid grid,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId,
			@RequestParam(value="lock",required=false)Integer lock){
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(st)){
			map.put("st",st);
		}
		if(StringUtils.isNoneBlank(et)){
			map.put("et",et);
		}
		if(null!=lock){//数据状态
			map.put("lock",lock);
		}
		//层级关系封装
		indexService.setOrgDeptParam(orgId, deptId, map);
		//
		if(StringUtils.isNoneBlank(grid.getSidx())){
			map.put("order", grid.getSidx()+" " + grid.getSord());//删除首个逗号
		}
		lvTongDataService.getEtcsjData(map,grid);
		return grid;
	}
	
	
	/**
	 * ETC数据上报新增
	 * @return
	 * */
	@RequestMapping("newEtcsj")
	public String newEtcsj(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return ETCSJ+"detail"; 
		}
		return ETCSJ+"newEtcsj";
	}
	
	
	/**
	 * 编辑YHSJ获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getEtcsjFormJson")
	@ResponseBody
	public Object getEtcsjFormJson(@RequestParam(value="keyValue")Long xh){
		FxEtcsj yh = lvTongDataService.getEtcsjInfo(xh);
		return new ExtReturn(true, yh);
	}
	/**
	 * ETC数据上报新增/编辑提交
	 * @param yh
	 * @return
	 * */
	@RequestMapping(value="newEtcsjSubmit")
	@ResponseBody
	public Object newEtcsjSubmit(FxEtcsj yh){
		AuthUserVO user = UserUtils.getAuthUser();
		yh.setLrsj(new Date());
		yh.setLryxh(user.getUid());
		yh.setLrymc(user.getRealName());
		yh.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != yh.getXh()){
				lvTongDataService.editEtcsj(yh);
			}else{
				lvTongDataService.newEtcsj(yh);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("EtcsjSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
//	
	/**
	 *ETC删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delEtcsj",method = RequestMethod.POST)
	@ResponseBody
	public Object delEtcsj(@RequestParam("keyValue")Long xh){
		try {	
			lvTongDataService.delEtcsj(xh);
		} catch (Exception e) {
			log.error("delEtcsj error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="etcsjLock",method = RequestMethod.POST)
	@ResponseBody
	public Object etcsjLock(@RequestParam("xhs")String xhs){
		try {	
			lvTongDataService.editetcsjLock(xhs);
		} catch (Exception e) {
			log.error("chsjYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
}

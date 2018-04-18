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
import com.power.charge.service.ChargeService;
import com.power.common.entity.FxRcetcmx;
import com.power.common.entity.FxRcclhz;
import com.power.common.entity.FxRcmtcmx;
import com.power.common.entity.FxRcsj;
import com.power.common.entity.FxSfrcsj;
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
public class ChargeController {
	private final static Logger log = LoggerFactory.getLogger(ChargeController.class);

	@Autowired
	private ChargeService chargeService;
	@Autowired
	private IndexService indexService;
	
	private String RCSJ = "charge/rcsj/";
	private String RCCLHZ = "charge/rcclhz/";
	private String ETCRCSJ = "charge/etcrcsj/";
	private String MTCRCSJ = "charge/mtcrcsj/";
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
	 * 日常数据管理首页
	 * @return
	 */
	@RequestMapping("rcsjIndex")
	public String rcsjIndex(){
		return RCSJ+"index";
	}
	
	/**
	 * 日常数据上报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("rcsjData")
	@ResponseBody
	public Object rcsjData(JqGrid grid,
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
		chargeService.getRcsjData(map,grid);
		return grid;
	}
	/**
	 * 新增日常数据上报
	 * @param req
	 * @param detail model
	 * @return
	 * */
	@RequestMapping("rcsjForm")
	public String rcsjForm(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return RCSJ+"detail"; 
		}
		//model.addAttribute("curDate",new Date());
		return RCSJ+"new";
	}
	/**
	 * 编辑日常数据上报
	 * @param xh
	 * @return
	 */
	@RequestMapping("getRcsjFormJson")
	@ResponseBody
	public Object getRcsjFormJson(@RequestParam(value="keyValue")Long xh){
		FxRcsj fr = chargeService.getRcsj(xh);
		return new ExtReturn(true, fr);
	}
	/**
	 * 新增/编辑提交
	 * @param fr
	 * @return
	 * */
	@RequestMapping(value="rcsjSubmit")
	@ResponseBody
	public Object rcsjSubmit(FxRcsj fr){
		AuthUserVO user = UserUtils.getAuthUser();
		fr.setLrsj(new Date());
		fr.setLryxh(user.getUid());
		fr.setLrymc(user.getRealName());
		fr.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != fr.getXh()){
				chargeService.editRcsj(fr);
			}else{
				chargeService.newRcsj(fr);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("rcsjSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
	/**
	 *删除日常数据上报
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delRcsj",method = RequestMethod.POST)
	@ResponseBody
	public Object delRcsj(@RequestParam("keyValue")Long xh){
		try {	
			chargeService.delRcsj(xh);
		} catch (Exception e) {
			log.error("delRcsj error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="rcsjLock",method = RequestMethod.POST)
	@ResponseBody
	public Object rcsjLock(@RequestParam("xhs")String xhs){
		try {	
			chargeService.editRcsjLock(xhs);
		} catch (Exception e) {
			log.error("rcsjLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	///////////////////////日常数据上报总计信息
	

	/**
	 * 日常数据总计
	 * @return
	 */
	@RequestMapping("etcrcsjIndex")
	public String etcrcsjIndex(){
		return ETCRCSJ+"index";
	}
	
	
	/**
	 * 日常数据上报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("etcrcsjData")
	@ResponseBody
	public Object etcrcsjYbData(JqGrid grid,
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
		chargeService.getEtcRcsjData(map,grid);
		return grid;
	}
	/**
	 * 新增日常数据上报
	 * @param req
	 * @param detail model
	 * @return
	 * */
	@RequestMapping("etcrcsjForm")
	public String etcrcsjForm(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return ETCRCSJ+"detail"; 
		}
		//model.addAttribute("curDate",new Date());
		return ETCRCSJ+"new";
	}
	/**
	 * 编辑日常数据上报
	 * @param xh
	 * @return
	 */
	@RequestMapping("getetcRcsjFormJson")
	@ResponseBody
	public Object getetcRcsjFormJson(@RequestParam(value="keyValue")Long xh){
		FxRcetcmx fr = chargeService.getEtcRcsj(xh);
		return new ExtReturn(true, fr);
	}
	/**
	 * 新增/编辑提交
	 * @param fr
	 * @return
	 * */
	@RequestMapping(value="etcrcsjSubmit")
	@ResponseBody
	public Object etcrcsjSubmit(FxRcetcmx fr){
		AuthUserVO user = UserUtils.getAuthUser();
		fr.setLrsj(new Date());
		fr.setLryxh(user.getUid());
		fr.setLrymc(user.getRealName());
		fr.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != fr.getXh()){
				chargeService.editEtcRcsj(fr);
			}else{
				chargeService.newEtcRcsj(fr);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("rcsjSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
	/**
	 *删除日常数据上报
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="deletcRcsj",method = RequestMethod.POST)
	@ResponseBody
	public Object deletcRcsj(@RequestParam("keyValue")Long xh){
		try {	
			chargeService.delEtcRcsj(xh);
		} catch (Exception e) {
			log.error("delRcsj error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="etcrcsjLock",method = RequestMethod.POST)
	@ResponseBody
	public Object etcrcsjLock(@RequestParam("xhs")String xhs){
		try {	
			chargeService.editEtcRcsjLock(xhs);
		} catch (Exception e) {
			log.error("etcrcsjLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	
	/////////////////////////////日常汇总
	
	/**
	 * 日常数据管理首页
	 * @return
	 */
	@RequestMapping("rcclhzIndex")
	public String rcclhzIndex(){
		return RCCLHZ+"index";
	}
	
	/**
	 * 日常数据上报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("rcclhzData")
	@ResponseBody
	public Object rcclhzData(JqGrid grid,
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
		chargeService.getRcclhzData(map,grid);
		return grid;
	}
	/**
	 * 新增日常数据上报
	 * @param req
	 * @param detail model
	 * @return
	 * */
	@RequestMapping("rcclhzForm")
	public String rcclhzForm(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return RCCLHZ+"detail"; 
		}
		//model.addAttribute("curDate",new Date());
		return RCCLHZ+"new";
	}
	/**
	 * 编辑日常数据上报
	 * @param xh
	 * @return
	 */
	@RequestMapping("getRcclhzFormJson")
	@ResponseBody
	public Object getRcclhzFormJson(@RequestParam(value="keyValue")Long xh){
		FxRcclhz fr = chargeService.getRcclhz(xh);
		return new ExtReturn(true, fr);
	}
	/**
	 * 新增/编辑提交
	 * @param fr
	 * @return
	 * */
	@RequestMapping(value="rcclhzSubmit")
	@ResponseBody
	public Object rcclhzSubmit(FxRcclhz fr){
		AuthUserVO user = UserUtils.getAuthUser();
		fr.setLrsj(new Date());
		fr.setLryxh(user.getUid());
		fr.setLrymc(user.getRealName());
		fr.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != fr.getXh()){
				chargeService.editRcclhz(fr);
			}else{
				chargeService.newRcclhz(fr);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("rcclhzSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
	/**
	 *删除日常数据上报
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delRcclhz",method = RequestMethod.POST)
	@ResponseBody
	public Object delRcclhz(@RequestParam("keyValue")Long xh){
		try {
			chargeService.delRcclhz(xh);
		} catch (Exception e) {
			log.error("delRcclhz error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="rcclhzLock",method = RequestMethod.POST)
	@ResponseBody
	public Object rcclhzLock(@RequestParam("xhs")String xhs){
		try {	
			chargeService.editRcclhzLock(xhs);
		} catch (Exception e) {
			log.error("rcclhzLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/////////////////////////////
	

	/**
	 * 日常数据总计
	 * @return
	 */
	@RequestMapping("mtcrcsjIndex")
	public String mtcrcsjIndex(){
		return MTCRCSJ+"index";
	}
	
	
	/**
	 * 日常数据上报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("mtcrcsjData")
	@ResponseBody
	public Object mtcrcsjYbData(JqGrid grid,
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
		chargeService.getMtcRcsjData(map,grid);
		return grid;
	}
	/**
	 * 新增日常数据上报
	 * @param req
	 * @param detail model
	 * @return
	 * */
	@RequestMapping("mtcrcsjForm")
	public String mtcrcsjForm(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return MTCRCSJ+"detail"; 
		}
		//model.addAttribute("curDate",new Date());
		return MTCRCSJ+"new";
	}
	/**
	 * 编辑日常数据上报
	 * @param xh
	 * @return
	 */
	@RequestMapping("getmtcRcsjFormJson")
	@ResponseBody
	public Object getmtcRcsjFormJson(@RequestParam(value="keyValue")Long xh){
		FxRcmtcmx fr = chargeService.getMtcRcsj(xh);
		return new ExtReturn(true, fr);
	}
	/**
	 * 新增/编辑提交
	 * @param fr
	 * @return
	 * */
	@RequestMapping(value="mtcrcsjSubmit")
	@ResponseBody
	public Object mtcrcsjSubmit(FxRcmtcmx fr){
		AuthUserVO user = UserUtils.getAuthUser();
		fr.setLrsj(new Date());
		fr.setLryxh(user.getUid());
		fr.setLrymc(user.getRealName());
		fr.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != fr.getXh()){
				chargeService.editMtcRcsj(fr);
			}else{
				chargeService.newMtcRcsj(fr);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("rcsjSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
	/**
	 *删除日常数据上报
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delmtcRcsj",method = RequestMethod.POST)
	@ResponseBody
	public Object delmtcRcsj(@RequestParam("keyValue")Long xh){
		try {	
			chargeService.delMtcRcsj(xh);
		} catch (Exception e) {
			log.error("delRcsj error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="mtcrcsjLock",method = RequestMethod.POST)
	@ResponseBody
	public Object mtcrcsjLock(@RequestParam("xhs")String xhs){
		try {	
			chargeService.editMtcRcsjLock(xhs);
		} catch (Exception e) {
			log.error("mtcrcsjLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	
}



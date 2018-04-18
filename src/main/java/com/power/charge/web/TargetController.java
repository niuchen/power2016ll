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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.service.UserUtils;
import com.power.charge.service.TargetService;
import com.power.common.entity.FxRwgl;
import com.power.common.entity.FxZsgl;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.springmvc.ExtReturn;
import com.power.index.service.IndexService;

@Controller
@RequestMapping("tar")
public class TargetController {
	private final static Logger log = LoggerFactory.getLogger(ChargeController.class);
	
	@Autowired
	private TargetService targetService;
	@Autowired
	private IndexService indexService;
	
	private String  RWZ = "target/mission/";
	private String  ZB = "target/forecast/";
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
	 * 任务管理首页
	 * @return
	 */
	@RequestMapping("rwIndex")
	public String rwIndex(){
		return RWZ+"index";
	}
	/**
	 * 任务管理数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("rwData")
	@ResponseBody
	public Object rwData(JqGrid grid,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId,
			@RequestParam(value="lock",required=false)Integer lock){
		HashMap<String, Object> map = new HashMap<String, Object>(5);
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNotBlank(user.getDeptId())){
			map.put("depts", user.getDeptId());
		}else{
			map.put("depts", user.getOrgId());
		}
		//
		targetService.getRwData(map,grid);
		return grid;
	}
	
	/**
	 * 新增任务
	 * @param req
	 * @param detail model
	 * @return
	 * */
	@RequestMapping("rwForm")
	public String rwForm(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return RWZ+"detail"; 
		}
		return RWZ+"new";
	}
	/**
	 * 新增/编辑提交
	 * @param fr
	 * @return
	 * */
	@RequestMapping(value="rwSubmit")
	@ResponseBody
	public Object rwSubmit(FxRwgl fr){
		AuthUserVO user = UserUtils.getAuthUser();
		fr.setLrsj(new Date());
		fr.setLryxh(user.getUid());
		fr.setLrymc(user.getRealName());
		//
		try {
			if(null != fr.getXh()){
				targetService.editRw(fr);
			}else{
				targetService.newRw(fr);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("rwSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
	/**
	 * 编辑日常数据上报
	 * @param xh
	 * @return
	 */
	@RequestMapping("getRwFormJson")
	@ResponseBody
	public Object getRwFormJson(@RequestParam(value="keyValue")Long xh){
		FxRwgl fr = targetService.getRw(xh);
		return new ExtReturn(true, fr);
	}
	///////////////////指标管理
	/**
	 * 指标管理首页
	 * @return
	 */
	@RequestMapping("zbIndex")
	public String zbIndex(){
		return ZB+"index";
	}
	/**
	 * 任务管理数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("zbData")
	@ResponseBody
	public Object zbData(JqGrid grid,
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
		//
		if(StringUtils.isNoneBlank(grid.getSidx())){
			map.put("order", grid.getSidx()+" " + grid.getSord());//删除首个逗号
		}
		//
		targetService.getZbData(map,grid);
		return grid;
	}
	
	/**
	 * 新增任务
	 * @param req
	 * @param detail model
	 * @return
	 * */
	@RequestMapping("zbForm")
	public String zbForm(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return ZB+"detail"; 
		}
		return ZB+"new";
	}
	/**
	 * 新增/编辑提交
	 * @param fr
	 * @return
	 * */
	@RequestMapping(value="zbSubmit")
	@ResponseBody
	public Object zbSubmit(FxZsgl fz){
		fz.setLrsj(new Date());
		//
		try {
			if(null != fz.getXh()){
				targetService.editZb(fz);
			}else{
				targetService.newZb(fz);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("ZbSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
	/**
	 * 编辑日常数据上报
	 * @param xh
	 * @return
	 */
	@RequestMapping("getZbFormJson")
	@ResponseBody
	public Object getZbFormJson(@RequestParam(value="keyValue")Long xh){
		FxZsgl fz = targetService.getZb(xh);
		return new ExtReturn(true, fz);
	}
}

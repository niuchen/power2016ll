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
import com.power.charge.service.PjService;
import com.power.common.entity.FxPjgl;
import com.power.common.entity.FxRcsj;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.springmvc.ExtReturn;
import com.power.index.service.IndexService;


@Controller
@RequestMapping("pj")
public class PjDataController {
	private final static Logger log = LoggerFactory.getLogger(ChargeController.class);

	@Autowired
	private PjService pjService;
	@Autowired
	private IndexService indexService;
	private String PJ = "target/PjglData/";
	/**
	 * 时间格式规范
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder){
		CustomDateEditor editor=new CustomDateEditor(new SimpleDateFormat("yyyy-MM"),true);
		binder.registerCustomEditor(Date.class,editor);
	}
	/**
	 * 票据管理首页
	 * @return
	 */
	@RequestMapping("pjIndex")
	public String pjIndex(){
		return PJ+"index";
	}
	/**
	 * 票据管理数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("pjData")
	@ResponseBody
	public Object jzclYbData(JqGrid grid,
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
		//if(StringUtils.isNoneBlank(grid.getSidx())){
		//	map.put("order", grid.getSidx()+" " + grid.getSord());
		//}
		//
		pjService.getPjData(map,grid);
		return grid;
	}
	/**
	 * 新增票据管理
	 * @param req
	 * @param detail model
	 * @return
	 * */
	@RequestMapping("newPj")
	public String rcsjForm(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return PJ+"detail"; 
		}
		//model.addAttribute("curDate",new Date());
		return PJ+"new";
	}
	/**
	 * 编辑票据管理
	 * @param xh
	 * @return
	 */
	@RequestMapping("getPjFormJson")
	@ResponseBody
	public Object getPjFormJson(@RequestParam(value="keyValue")Long xh){
		FxPjgl fp = pjService.getPjInfo(xh);
		return new ExtReturn(true, fp);
	}
	/**
	 * 新增/编辑提交
	 * @param fr
	 * @return
	 * */
	@RequestMapping(value="pjSubmit")
	@ResponseBody
	public Object pjSubmit(FxPjgl fr){
		AuthUserVO user = UserUtils.getAuthUser();
		fr.setLrsj(new Date());
		fr.setLryxh(user.getUid());
		fr.setLrymc(user.getRealName());
		fr.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != fr.getXh()){
				pjService.editPj(fr);
			}else{
				pjService.newPj(fr);
				
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("pjSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
	/**
	 *删除票据管理
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delPj",method = RequestMethod.POST)
	@ResponseBody
	public Object delPj(@RequestParam("keyValue")Long xh){
		try {	
			pjService.delPj(xh);
		} catch (Exception e) {
			log.error("delPj error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="pjLock",method = RequestMethod.POST)
	@ResponseBody
	public Object pjLock(@RequestParam("xhs")String xhs){
		try {	
			pjService.editPjLock(xhs);
		} catch (Exception e) {
			log.error("rcsjLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
}

package com.power.charge.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.KeyValue;
import com.power.base.sys.entity.WdTreeVO;
import com.power.base.sys.service.DictService;
import com.power.base.sys.service.UserUtils;
import com.power.charge.entity.LockEnum;
import com.power.charge.service.MonthService;
import com.power.charge.service.lvTongDataService;
import com.power.common.entity.FxCgcl;
import com.power.common.entity.FxChsjYb;
import com.power.common.entity.FxCzfc;
import com.power.common.entity.FxHmd;
import com.power.common.entity.FxIck;
import com.power.common.entity.FxJzclYb;
import com.power.common.entity.FxLsk;
import com.power.common.entity.FxLsk;
import com.power.common.entity.FxLtcl;
import com.power.common.entity.FxMfclYb;
import com.power.common.entity.FxPjyb;
import com.power.common.entity.FxQzzh;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.springmvc.ExtReturn;
import com.power.index.service.IndexService;
import com.sun.tools.internal.ws.processor.model.Request;


/**
 * 月数据上报
 * 创建时间：2017-3-28 上午11:36:52 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Controller
@RequestMapping("yb")
public class MonthDataController {
	private final static Logger log = LoggerFactory.getLogger(MonthDataController.class);
	@Autowired
	private MonthService monthService;
	@Autowired
	private IndexService indexService;
	@Autowired
	private DictService dictService;
	@Autowired
	private lvTongDataService lvTongDataService;
	private String JZCL = "yb/jzcl/";
	private String MFCL = "yb/mfcl/";
	private String CHSJ = "yb/chsj/";
	private String LSK = "yb/lsk/";
	private String CZFC = "yb/czfc/";
	private String ICK = "yb/ick/";
	private String PJYB = "yb/pjyb/";
	private String HMD = "yb/hmd/";
	private String CGCL = "yb/cgcl/";
	private String QZZH = "yb/qzzh/";
	private String LTCL = "charge/ltcl/";
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
		monthService.getJzclYbData(map,grid);
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
		FxJzclYb yb = monthService.getJzclYbInfo(xh);
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
				monthService.editJzclYb(yb);
			}else{
				monthService.newJzclYb(yb);
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
			monthService.delJzclYb(xh);
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
			monthService.editJzclYbLock(xhs);
		} catch (Exception e) {
			log.error("jzclYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	
	///////////////////////////免费车辆月报
	/**
	 * 免费车辆月报管理首页
	 * @return
	 */
	@RequestMapping("mfclYbIndex")
	public String mfclYbIndex(){
		return MFCL+"index";
	}
	/**
	 * 免费车辆月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("mfclYbData")
	@ResponseBody
	public Object mfclYbData(JqGrid grid,
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
		monthService.getMfclData(map,grid);
		return grid;
	}
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("mfclYbForm")
	public String mfclYbForm(@RequestParam(required=false,value="detail")Integer detail ,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return MFCL+"detail"; 
		}
		return MFCL+"new";
	}
	
	/**
	 * 编辑MFCL获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getMfclYbFormJson")
	@ResponseBody
	public Object getMfclYbFormJson(@RequestParam(value="keyValue")Long xh){
		FxMfclYb yb = monthService.getMfclYbInfo(xh);
		return new ExtReturn(true, yb);
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * */
	@RequestMapping(value="mfclYbSubmit")
	@ResponseBody
	public Object mfclYbSubmit(FxMfclYb yb){
		AuthUserVO user = UserUtils.getAuthUser();
		yb.setLtr(0);
		yb.setLrsj(new Date());
		yb.setLryxh(user.getUid());
		yb.setLrymc(user.getRealName());
		yb.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != yb.getXh()){
				monthService.editMfcl(yb);
			}else{
			    monthService.newMfcl(yb);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("mfclYbSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delMfclyb",method = RequestMethod.POST)
	@ResponseBody
	public Object delMfclyb(@RequestParam("keyValue")Long xh){
		try {	
			monthService.delMfclyb(xh);
		} catch (Exception e) {
			log.error("delMfclyb error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="mfclYbLock",method = RequestMethod.POST)
	@ResponseBody
	public Object mfclYbLock(@RequestParam("xhs")String xhs){
		try {	
			monthService.editmfclYbLock(xhs);
		} catch (Exception e) {
			log.error("mfclYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	
	
	/////////////////////////拆后数据月报
	/**
	 * 拆后数据月报首页
	 * @return
	 */
	@RequestMapping("chsjYbIndex")
	public String chsjIndex(){
		return CHSJ+"index";
	}
	/**
	 * 拆后车辆月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("chsjYbData")
	@ResponseBody
	public Object chsjYbData(JqGrid grid,
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
		monthService.getChsjData(map,grid);
		return grid;
	}
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("chsjForm")
	public String chsjForm(@RequestParam(required=false,value="detail")Integer detail ,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return CHSJ+"detail"; 
		}
		return CHSJ+"new";
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * */
	@RequestMapping(value="chsjYbSubmit")
	@ResponseBody
	public Object chsjYbSubmit(FxChsjYb yb){
		AuthUserVO user = UserUtils.getAuthUser();
		yb.setLrsj(new Date());
		yb.setLryxh(user.getUid());
		yb.setLrymc(user.getRealName());
		yb.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != yb.getXh()){
				monthService.editChsj(yb);
			}else{
			    monthService.newChsj(yb);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("chsjYbSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	/**
	 * 编辑MFCL获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getChsjYbFormJson")
	@ResponseBody
	public Object getChsjYbFormJson(@RequestParam(value="keyValue")Long xh){
		FxChsjYb yb = monthService.getChsjInfo(xh);
		return new ExtReturn(true, yb);
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delChsjyb",method = RequestMethod.POST)
	@ResponseBody
	public Object delChsjyb(@RequestParam("keyValue")Long xh){
		try {	
			monthService.delChsjyb(xh);
		} catch (Exception e) {
			log.error("delMfclyb error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="chsjYbLock",method = RequestMethod.POST)
	@ResponseBody
	public Object chsjYbLock(@RequestParam("xhs")String xhs){
		try {	
			monthService.editchsjYbLock(xhs);
		} catch (Exception e) {
			log.error("chsjYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	///////////////////////灰名单、流失卡
	
	/**
	 * 免费车辆月报管理首页
	 * @return
	 */
	@RequestMapping("lskIndex")
	public String lskIndex(){
		return LSK+"index";
	}
	/**
	 * 免费车辆月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("lskData")
	@ResponseBody
	public Object lskData(JqGrid grid,
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
		monthService.getLskData(map,grid);
		return grid;
	}
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("lskForm")
	public String lskForm(@RequestParam(required=false,value="detail")Integer detail ,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return LSK+"detail"; 
		}
		return LSK+"new";
	}
	
	/**
	 * 编辑MFCL获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getLskFormJson")
	@ResponseBody
	public Object getLskFormJson(@RequestParam(value="keyValue")Long xh){
		FxLsk yb = monthService.getLskInfo(xh);
		return new ExtReturn(true, yb);
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * @throws ParseException 
	 * */
	@RequestMapping(value="lskSubmit")
	@ResponseBody
	public Object lskSubmit(@RequestParam(required=false,value="lskrqbm")String lskrqbm,		
			@RequestParam(required=false,value="lskszsjbm")String lskszsjbm,
			FxLsk yb) throws ParseException{
		System.out.println(lskrqbm);
		System.out.println(lskszsjbm);
		AuthUserVO user = UserUtils.getAuthUser();
		yb.setLrsj(new Date());
		yb.setLryxh(user.getUid());
		yb.setLrymc(user.getRealName());
		yb.setZt(LockEnum.UNLOCK.getIndex());
		DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
			yb.setLskrq(fmt.parse(lskrqbm));
			DateFormat fmts =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			yb.setLskszsj(fmts.parse(lskszsjbm));
			
		//
		try {
			if(null != yb.getXh()){
				monthService.editLsk(yb);
			}else{
			    monthService.newLsk(yb);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("lskSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delLsk",method = RequestMethod.POST)
	@ResponseBody
	public Object delLsk(@RequestParam("keyValue")Long xh){
		try {	
			monthService.delLsk(xh);
		} catch (Exception e) {
			log.error("delMfclyb error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	

	/**
	 * wdTree 树形列表
	 * @param list
	 * @param treeList
	 */
	private void getList(List<KeyValue> list,List<WdTreeVO> treeList){
		for (KeyValue item : list) {
		    WdTreeVO tree = new WdTreeVO();
		    tree.setId(item.getId());
    		tree.setValue(item.getId());
    		tree.setText(item.getName());
    		tree.setComplete(true);
    		tree.setIsexpand(true);
	    		treeList.add(tree);
	}
	}
	/**
	 * 左侧树形列表
	 * @param 
	 * @param 
	 * @return
	 */
	@RequestMapping("cxTree")
	@ResponseBody
	public Object newTree(){		
		List<KeyValue> list = dictService.getDictItem("108");		
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		getList(list,treeList);
		return treeList ;
		
	}
	
	/**
	 * 左侧树形列表
	 * @param 
	 * @param 
	 * @return
	 */
	@RequestMapping("sfTree")
	@ResponseBody
	public Object sfTree(){		
		List<KeyValue> list = dictService.getDictItem("109");		
		List<WdTreeVO> treeList = new ArrayList<WdTreeVO>();
		getList(list,treeList);
		return treeList ;
		
	}
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="lskLock",method = RequestMethod.POST)
	@ResponseBody
	public Object lskLock(@RequestParam("xhs")String xhs){
		try {	
			monthService.editLskLock(xhs);
		} catch (Exception e) {
			log.error("mfclYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	/////////////////////////拆账后分车型数据录入
	/**
	 * 拆账后分车型数据管理首页
	 * @return
	 */
	@RequestMapping("czfcIndex")
	public String czfcIndex(){
		return CZFC+"index";
	}
	/**
	 * 免费车辆月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("czfcData")
	@ResponseBody
	public Object czfcData(JqGrid grid,
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
		monthService.getCzfcData(map,grid);
		return grid;
	}
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("czfcForm")
	public String czfcForm(@RequestParam(required=false,value="detail")Integer detail ,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return CZFC+"detail"; 
		}
		return CZFC+"new";
	}
	
	/**
	 * 编辑CZFC获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getCzfcFormJson")
	@ResponseBody
	public Object getCzfcFormJson(@RequestParam(value="keyValue")Long xh){
		FxCzfc yb = monthService.getCzfcInfo(xh);
		return new ExtReturn(true, yb);
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * */
	@RequestMapping(value="czfcSubmit")
	@ResponseBody
	public Object czfcSubmit(FxCzfc yb){
		AuthUserVO user = UserUtils.getAuthUser();
		yb.setLrsj(new Date());
		yb.setLryxh(user.getUid());
		yb.setLrymc(user.getRealName());
		yb.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != yb.getXh()){
				monthService.editCzfc(yb);
			}else{
			    monthService.newCzfc(yb);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("czfcSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delCzfc",method = RequestMethod.POST)
	@ResponseBody
	public Object delCzfc(@RequestParam("keyValue")Long xh){
		try {	
			monthService.delCzfc(xh);
		} catch (Exception e) {
			log.error("delCzfc error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="czfcLock",method = RequestMethod.POST)
	@ResponseBody
	public Object czfcLock(@RequestParam("xhs")String xhs){
		try {	
			monthService.editmfclYbLock(xhs);
		} catch (Exception e) {
			log.error("czfcLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	//////////////////IC通行卡月盘存
	/**
	 * 拆账后分车型数据管理首页
	 * @return
	 */
	@RequestMapping("ickIndex")
	public String ickIndex(){
		return ICK+"index";
	}
	/**
	 * 免费车辆月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("ickData")
	@ResponseBody
	public Object ickData(JqGrid grid,
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
		monthService.getIckData(map,grid);
		return grid;
	}
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("ickForm")
	public String ickForm(@RequestParam(required=false,value="detail")Integer detail ,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return ICK+"detail"; 
		}
		return ICK+"new";
	}
	
	/**
	 * 编辑MFCL获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getIckFormJson")
	@ResponseBody
	public Object getIckFormJson(@RequestParam(value="keyValue")Long xh){
		FxIck yb = monthService.getIckInfo(xh);
		return new ExtReturn(true, yb);
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * */
	@RequestMapping(value="ickSubmit")
	@ResponseBody
	public Object ickSubmit(FxIck yb){
		AuthUserVO user = UserUtils.getAuthUser();
		yb.setLrsj(new Date());
		yb.setLryxh(user.getUid());
		yb.setLrymc(user.getRealName());
		yb.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != yb.getXh()){
				monthService.editIck(yb);
			}else{
			    monthService.newIck(yb);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("ickSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delIck",method = RequestMethod.POST)
	@ResponseBody
	public Object delIck(@RequestParam("keyValue")Long xh){
		try {	
			monthService.delIck(xh);
		} catch (Exception e) {
			log.error("delIck error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="ickLock",method = RequestMethod.POST)
	@ResponseBody
	public Object ickLock(@RequestParam("xhs")String xhs){
		try {	
			monthService.editIckLock(xhs);
		} catch (Exception e) {
			log.error("ickLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	////////////////////////////////票据月报
	/**
	 * 拆账后分车型数据管理首页
	 * @return
	 */
	@RequestMapping("pjybIndex")
	public String pjybIndex(){
		return PJYB+"index";
	}
	/**
	 * 免费车辆月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("pjybData")
	@ResponseBody
	public Object pjybData(JqGrid grid,
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
		monthService.getPjybData(map,grid);
		return grid;
	}
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("pjybForm")
	public String pjybForm(@RequestParam(required=false,value="detail")Integer detail ,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return PJYB+"detail"; 
		}
		return PJYB+"new";
	}
	
	/**
	 * 编辑MFCL获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getPjybFormJson")
	@ResponseBody
	public Object getPjybFormJson(@RequestParam(value="keyValue")Long xh){
		FxPjyb yb = monthService.getPjybInfo(xh);
		return new ExtReturn(true, yb);
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * */
	@RequestMapping(value="pjybSubmit")
	@ResponseBody
	public Object pjybSubmit(HttpServletRequest request,FxPjyb yb){
		AuthUserVO user = UserUtils.getAuthUser();
		yb.setLrsj(new Date());
		yb.setLryxh(user.getUid());
		yb.setLrymc(user.getRealName());
		yb.setZt(LockEnum.UNLOCK.getIndex());
		String[] zsk = request.getParameterValues("a1");
	System.out.println(zsk);
		//
		try {
			if(null != yb.getXh()){
				monthService.editPjyb(yb);
			}else{
			    monthService.newPjyb(yb);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("ickSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delPjyb",method = RequestMethod.POST)
	@ResponseBody
	public Object delPjyb(@RequestParam("keyValue")Long xh){
		try {	
			monthService.delPjyb(xh);
		} catch (Exception e) {
			log.error("delPjyb error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="pjybLock",method = RequestMethod.POST)
	@ResponseBody
	public Object pjybLock(@RequestParam("xhs")String xhs){
		try {	
			monthService.editPjybLock(xhs);
		} catch (Exception e) {
			log.error("pjybLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	//////////////////////////灰名单
	/**
	 * 灰名单月报管理首页
	 * @return
	 */
	@RequestMapping("hmdIndex")
	public String hmdIndex(){
		return HMD+"index";
	}
	/**
	 * 灰名单月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("hmdData")
	@ResponseBody
	public Object hmdData(JqGrid grid,
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
		monthService.getHmdData(map,grid);
		return grid;
	}
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("hmdForm")
	public String hmdForm(@RequestParam(required=false,value="detail")Integer detail ,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return HMD+"detail"; 
		}
		return HMD+"new";
	}
	
	/**
	 * 编辑MFCL获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getHmdFormJson")
	@ResponseBody
	public Object getHmdFormJson(@RequestParam(value="keyValue")Long xh){
		FxHmd yb = monthService.getHmdInfo(xh);
		return new ExtReturn(true, yb);
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * @throws ParseException 
	 * */
	@RequestMapping(value="hmdSubmit")
	@ResponseBody
	public Object hmdSubmit(@RequestParam(required=false,value="hmdfksjbm")String hmdfksjbm,
			FxHmd yb) throws ParseException{
		
		AuthUserVO user = UserUtils.getAuthUser();
		yb.setLrsj(new Date());
		yb.setLryxh(user.getUid());
		yb.setLrymc(user.getRealName());
		yb.setZt(LockEnum.UNLOCK.getIndex());
		
			
			DateFormat fmts =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			yb.setHmdfksj(fmts.parse(hmdfksjbm));
			
		//
		try {
			if(null != yb.getXh()){
				monthService.editHmd(yb);
			}else{
			    monthService.newHmd(yb);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("hmdSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delHmd",method = RequestMethod.POST)
	@ResponseBody
	public Object delhmd(@RequestParam("keyValue")Long xh){
		try {	
			monthService.delHmd(xh);
		} catch (Exception e) {
			log.error("delMfclyb error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	

	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="hmdLock",method = RequestMethod.POST)
	@ResponseBody
	public Object hmdLock(@RequestParam("xhs")String xhs){
		try {	
			monthService.editHmdLock(xhs);
		} catch (Exception e) {
			log.error("mfclYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	
	//////////////////////////闯关车辆
	/**
	 * 闯关车辆月报管理首页
	 * @return
	 */
	@RequestMapping("cgclIndex")
	public String cgclIndex(){
		return CGCL+"index";
	}
	/**
	 * 闯关车辆月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("cgclData")
	@ResponseBody
	public Object cgclData(JqGrid grid,
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
		monthService.getCgclData(map,grid);
		return grid;
	}
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("cgclForm")
	public String cgclForm(@RequestParam(required=false,value="detail")Integer detail ,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return CGCL+"detail"; 
		}
		return CGCL+"new";
	}
	
	/**
	 * 编辑MFCL获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getCgclFormJson")
	@ResponseBody
	public Object getCgclFormJson(@RequestParam(value="keyValue")Long xh){
		FxCgcl yb = monthService.getCgclInfo(xh);
		return new ExtReturn(true, yb);
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * @throws ParseException 
	 * */
	@RequestMapping(value="cgclSubmit")
	@ResponseBody
	public Object cgclSubmit(
			@RequestParam(required=false,value="cgcksjbm")String cgcksjbm,
			@RequestParam(required=false,value="cgtgsjbm")String cgtgsjbm,FxCgcl yb) throws ParseException{
		
		AuthUserVO user = UserUtils.getAuthUser();
		yb.setLrsj(new Date());
		yb.setLryxh(user.getUid());
		yb.setLrymc(user.getRealName());
		yb.setZt(LockEnum.UNLOCK.getIndex());
		
			
			DateFormat fmts =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			yb.setCgcksj(fmts.parse(cgcksjbm));
			yb.setCgtgsj(fmts.parse(cgtgsjbm));
		//
		try {
			if(null != yb.getXh()){
				monthService.editCgcl(yb);
			}else{
			    monthService.newCgcl(yb);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("cgclSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delCgcl",method = RequestMethod.POST)
	@ResponseBody
	public Object delcgcl(@RequestParam("keyValue")Long xh){
		try {	
			monthService.delCgcl(xh);
		} catch (Exception e) {
			log.error("delMfclyb error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	

	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="cgclLock",method = RequestMethod.POST)
	@ResponseBody
	public Object cgclLock(@RequestParam("xhs")String xhs){
		try {	
			monthService.editCgclLock(xhs);
		} catch (Exception e) {
			log.error("mfclYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	
	
	
	
	
	
	
	//////////////////////////强制组合
	/**
	 * 强制组合月报管理首页
	 * @return
	 */
	@RequestMapping("qzzhIndex")
	public String qzzhIndex(){
		return QZZH+"index";
	}
	/**
	 * 强制组合月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("qzzhData")
	@ResponseBody
	public Object qzzhData(JqGrid grid,
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
		monthService.getQzzhData(map,grid);
		return grid;
	}
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("qzzhForm")
	public String qzzhForm(@RequestParam(required=false,value="detail")Integer detail ,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return QZZH+"detail"; 
		}
		return QZZH+"new";
	}
	
	/**
	 * 编辑MFCL获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getQzzhFormJson")
	@ResponseBody
	public Object getQzzhFormJson(@RequestParam(value="keyValue")Long xh){
		FxQzzh yb = monthService.getQzzhInfo(xh);
		return new ExtReturn(true, yb);
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * @throws ParseException 
	 * */
	@RequestMapping(value="qzzhSubmit")
	@ResponseBody
	public Object qzzhSubmit(
			@RequestParam(required=false,value="zhrqbm")String zhrqbm,
			FxQzzh yb) throws ParseException{
		AuthUserVO user = UserUtils.getAuthUser();
		yb.setLrsj(new Date());
		yb.setLryxh(user.getUid());
		yb.setLrymc(user.getRealName());
		yb.setZt(LockEnum.UNLOCK.getIndex());
		
			
			DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
			yb.setZhrq(fmt.parse(zhrqbm));
			
		//
		try {
			if(null != yb.getXh()){
				monthService.editQzzh(yb);
			}else{
			    monthService.newQzzh(yb);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("qzzhSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delQzzh",method = RequestMethod.POST)
	@ResponseBody
	public Object delqzzh(@RequestParam("keyValue")Long xh){
		try {	
			monthService.delQzzh(xh);
		} catch (Exception e) {
			log.error("delMfclyb error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	

	
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="qzzhLock",method = RequestMethod.POST)
	@ResponseBody
	public Object qzzhLock(@RequestParam("xhs")String xhs){
		try {	
			monthService.editQzzhLock(xhs);
		} catch (Exception e) {
			log.error("mfclYbLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
	
	
	
	
	/**
	 * 绿通车辆月报首页
	 * @return
	 */
	@RequestMapping("ltclIndex")
	public String ltclIndex(){
		return LTCL+"index";
	}
	/**
	 * 绿通车辆月报数据
	 * @param colname
	 * @param value
	 * @param grid
	 * @return
	 * */
	@RequestMapping("ltclData")
	@ResponseBody
	public Object ltclData(JqGrid grid,
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
		lvTongDataService.getLtclData(map,grid);
		return grid;
	}
	
	/**
	 * 新增
	 * @return
	 * */
	@RequestMapping("LtclForm")
	public String ltclForm(@RequestParam(required=false,value="detail")Integer detail,Model model){
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if(detail!=null){
			return LTCL+"detail"; 
		}
		return LTCL+"new";
	}
	
	/**
	 * 编辑JZCL获取数据
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getLtclFormJson")
	@ResponseBody
	public Object getLtclFormJson(@RequestParam(value="keyValue")Long xh){
		FxLtcl ltcl = lvTongDataService.getLtclInfo(xh);
		return new ExtReturn(true, ltcl);
	}
	
	/**
	 * 新增/编辑提交
	 * @param yb
	 * @return
	 * */
	@RequestMapping(value="ltclSubmit")
	@ResponseBody
	public Object ltclSubmit(FxLtcl ltcl){
		AuthUserVO user = UserUtils.getAuthUser();
		ltcl.setLrsj(new Date());
		ltcl.setLryxh(user.getUid());
		ltcl.setLrymc(user.getRealName());
		ltcl.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if(null != ltcl.getXh()){
				lvTongDataService.editLtcl(ltcl);
			}else{
				lvTongDataService.newLtcl(ltcl);
			}
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("ltclSubmit error:{}"+e);
			return new ExtReturn(false,"操作失败,【"+e.getMessage()+"】");
		}
	}
	
	/**
	 *删除
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="delLtcl",method = RequestMethod.POST)
	@ResponseBody
	public Object delLtcl(@RequestParam("keyValue")Long xh){
		try {	
			lvTongDataService.delLtcl(xh);
		} catch (Exception e) {
			log.error("delLtcl error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}
		return new ExtReturn(true,"操作成功");
	}
	/**
	 *数据锁定
	 *@param xh
	 *@return
	 * */
	@RequestMapping(value="ltclLock",method = RequestMethod.POST)
	@ResponseBody
	public Object ltclLock(@RequestParam("xhs")String xhs){
		try {	
			lvTongDataService.editLtclLock(xhs);
		} catch (Exception e) {
			log.error("ltclLock error:{}"+e);
			return new ExtReturn(false,"操作失败,"+e.getMessage());
		}
		return new ExtReturn(true,"操作成功");
	}
	
}

package com.power.charge.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.power.base.sys.entity.KeyValue;
import com.power.base.sys.service.DictService;
import com.power.base.sys.service.UserUtils;
import com.power.charge.entity.LockEnum;
import com.power.charge.service.JdxmzService;
import com.power.common.entity.FxJdxmz;
import com.power.common.entity.SzJdxmz;
import com.power.common.mybatis.page.JqGrid;
import com.power.common.springmvc.ExtReturn;
import com.power.index.service.IndexService;

@Controller
@RequestMapping("mz")
public class JdxmzController {
	private final static Logger log = LoggerFactory.getLogger(MonthDataController.class);
	@Autowired
	private JdxmzService jdxmzService;
	@Autowired
	private IndexService indexService;
	@Autowired
	private DictService dictService;
	private String MZLR = "target/jdxmzData/";
	private String MZSZ = "target/jdxmzInstall/";

	/**
	 * 时间格式规范
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat(
				"yyyy-MM-dd"), true);
		binder.registerCustomEditor(Date.class, editor);
	}

	/**
	 * 阶段性免征设置首页
	 * 
	 * @return
	 */
	@RequestMapping("jdxmzIndex")
	public String jdxmzIndex() {
		return MZSZ + "index";
	}

	@RequestMapping("jdxmzData")
	@ResponseBody
	public Object jdxmzData(JqGrid grid) {
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		jdxmzService.getjdxmzData(map, grid);
		return grid;
	}

	/**
	 * 阶段性免征设置新增
	 * 
	 * @return
	 * */
	@RequestMapping("newJdxmzsz")
	public String newJdxmzsz(
			@RequestParam(required = false, value = "detail") Integer detail,
			Model model) {
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if (detail != null) {
			return MZSZ + "detail";
		}
		return MZSZ + "new";
	}

	/**
	 * 免征事件类型字典
	 * 
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping("mzcllxTree")
	@ResponseBody
	public Object mzcllxTree() {
		List<KeyValue> list = dictService.getDictItem("107");
		return list;
	}

	/**
	 * 编辑JZCL获取数据
	 * 
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getJdszFormJson")
	@ResponseBody
	public Object getJdszFormJson(@RequestParam(value = "keyValue") Integer xh) {
		SzJdxmz sj = jdxmzService.getJdszFormInfo(xh);
		return new ExtReturn(true, sj);
	}

	/**
	 * 新增/编辑提交
	 * 
	 * @param yb
	 * @return
	 * */
	@RequestMapping(value = "JdxmzInSubmit")
	@ResponseBody
	public Object JdxmzInSubmit(SzJdxmz sj) {
		AuthUserVO user = UserUtils.getAuthUser();
		sj.setTxsj(new Date());

		if (StringUtils.isNotBlank(user.getDeptId())) {
			sj.setDwbh(user.getDeptId());
		} else if (StringUtils.isNotBlank(user.getOrgId())) {
			sj.setDwbh(user.getOrgId());
		}
		//
		try {
			if (null != sj.getXh()) {
				sj.setXgr(user.getUid());
				sj.setXgsj(new Date());
				jdxmzService.editJdsz(sj);
			} else {
				sj.setTxr(user.getUid());
				sj.setTxrmc(user.getRealName());
				jdxmzService.newJdsz(sj);
			}
			return new ExtReturn(true, "操作成功");
		} catch (Exception e) {
			log.error("JdxmzInSubmit error:{}" + e);
			return new ExtReturn(false, "操作失败,【" + e.getMessage() + "】");
		}
	}

	//
	/**
	 * 删除
	 * 
	 * @param xh
	 * @return
	 * */
	@RequestMapping(value = "delJdxmzInstall", method = RequestMethod.POST)
	@ResponseBody
	public Object delJdxmzInstall(@RequestParam("keyValue") Integer xh) {
		try {
			jdxmzService.delJdsz(xh);
		} catch (Exception e) {
			log.error("delJzclYb error:{}" + e);
			return new ExtReturn(false, "操作失败");
		}
		return new ExtReturn(true, "操作成功");
	}

	/**
	 * 数据锁定
	 * 
	 * @param xh
	 * @return
	 * */
	@RequestMapping(value = "jdszLock", method = RequestMethod.POST)
	@ResponseBody
	public Object jdszLock(@RequestParam("xhs") String xhs) {
		try {
			jdxmzService.editjdszLock(xhs);
		} catch (Exception e) {
			log.error("jzclYbLock error:{}" + e);
			return new ExtReturn(false, "操作失败," + e.getMessage());
		}
		return new ExtReturn(true, "操作成功");
	}

	// ////////////////////////阶段性免征数据
	/**
	 * 阶段性免征设置首页
	 * 
	 * @return
	 */
	@RequestMapping("jdxmzDataIndex")
	public String jdxmzDataIndex() {
		return MZLR + "index";
	}

	@RequestMapping("jdxmzDaData")
	@ResponseBody
	public Object jdxmzDaData(JqGrid grid,
			@RequestParam(value = "st", required = false) String st,
			@RequestParam(value = "et", required = false) String et,
			@RequestParam(value = "orgId", required = false) String orgId,
			@RequestParam(value = "deptId", required = false) String deptId,
			@RequestParam(value = "lock", required = false) Integer lock,
			@RequestParam(value = "mzsj", required = false) Integer mzsj) {
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if (StringUtils.isNoneBlank(st)) {
			map.put("st", st);
		}
		if (StringUtils.isNoneBlank(et)) {
			map.put("et", et);
		}
		if (null != lock) {// 数据状态
			map.put("lock", lock);
		}
		if (null != mzsj) {// 数据状态
			map.put("mzsj", mzsj);
		}
		//层级关系封装
		indexService.setOrgDeptParam(orgId, deptId, map);
		//
		if (StringUtils.isNoneBlank(grid.getSidx())) {
			map.put("order", grid.getSidx() + " " + grid.getSord());// 删除首个逗号
		}
		jdxmzService.getjdxmzDaData(map, grid);
		return grid;
	}

	/**
	 * 阶段性免征设置新增
	 * 
	 * @return
	 * */
	@RequestMapping("newJdxmzData")
	public String newJdxmzData(
			@RequestParam(required = false, value = "detail") Integer detail,
			Model model) {
		AuthUserVO user = UserUtils.getAuthUser();
		model.addAttribute("user", user);
		if (detail != null) {
			return MZLR + "detail";
		}
		return MZLR + "new";
	}

	/**
	 * 启用的免征事件列表
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping("mzsjTree")
	@ResponseBody
	public Object mzsjTree() {
		List<KeyValue> list = jdxmzService.getMzsjTree();
		return list;
	}

	/**
	 * 编辑JZCL获取数据
	 * 
	 * @param xh
	 * @return
	 * */
	@RequestMapping("getJddataFormJson")
	@ResponseBody
	public Object getJddataFormJson(@RequestParam(value = "keyValue") Long xh) {
		FxJdxmz sj = jdxmzService.getJddataFormInfo(xh);
		return new ExtReturn(true, sj);
	}

	/**
	 * 新增/编辑提交
	 * 
	 * @param yb
	 * @return
	 * */
	@RequestMapping(value = "jdxmzDataSubmit")
	@ResponseBody
	public Object jdxmzDataSubmit(FxJdxmz fj) {
		AuthUserVO user = UserUtils.getAuthUser();
		fj.setLrsj(new Date());
		fj.setLryxh(user.getUid());
		fj.setLrymc(user.getRealName());
		fj.setZt(LockEnum.UNLOCK.getIndex());
		//
		try {
			if (null != fj.getXh()) {
				jdxmzService.editJdData(fj);
			} else {
				jdxmzService.newJdData(fj);
			}
			return new ExtReturn(true, "操作成功");
		} catch (Exception e) {
			log.error("JdxmzDataSubmit error:{}" + e);
			return new ExtReturn(false, "操作失败,【" + e.getMessage() + "】");
		}
	}

	//
	/**
	 * 删除
	 * 
	 * @param xh
	 * @return
	 * */
	@RequestMapping(value = "delJdxmzData", method = RequestMethod.POST)
	@ResponseBody
	public Object delJdxmzData(@RequestParam("keyValue") Long xh) {
		try {
			jdxmzService.delJdData(xh);
		} catch (Exception e) {
			log.error("delJdxmzData error:{}" + e);
			return new ExtReturn(false, "操作失败");
		}
		return new ExtReturn(true, "操作成功");
	}

	/**
	 * 数据锁定
	 * 
	 * @param xh
	 * @return
	 * */
	@RequestMapping(value = "jdDataLock", method = RequestMethod.POST)
	@ResponseBody
	public Object jzDataLock(@RequestParam("xhs") String xhs) {
		try {
			jdxmzService.editjdDataLock(xhs);
		} catch (Exception e) {
			log.error("jdDataLock error:{}" + e);
			return new ExtReturn(false, "操作失败," + e.getMessage());
		}
		return new ExtReturn(true, "操作成功");
	}

}

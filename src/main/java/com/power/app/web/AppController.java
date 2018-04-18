package com.power.app.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.power.app.entity.AppReturn;
import com.power.app.entity.EtcVO;
import com.power.app.entity.FyzbVO;
import com.power.app.entity.KhlxVO;
import com.power.app.entity.LlfyVO;
import com.power.app.entity.LtsjVO;
import com.power.app.entity.UserInfoCommand;
import com.power.app.entity.YhsjVO;

import com.power.app.service.AppService;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.KeyValue;
import com.power.base.sys.service.DeptService;
import com.power.base.sys.service.OrgService;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.SysDepartment;
import com.power.common.entity.SysOrganize;
import com.power.common.entity.SysUser;
import com.power.common.entity.SysUserRelationKey;
import com.power.common.entity.Sztqsj;
import com.power.common.springmvc.ExtReturn;
import com.power.common.utils.CacheUtils;

/**
 * 项目名称：power2016 <br>
 * 类名称：AppController <br>
 * 创建时间：2017-4-5 下午3:05:07 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Controller
public class AppController {

	private final static Logger log = LoggerFactory.getLogger(AppController.class);
	private String APP_CACHE_NAME="app-cache";
	private String LOGIN="app/reLogin";
	@Autowired
	private AppService appService;
	
	@Autowired
	private DeptService deptService;
	@Autowired
	private OrgService orgService;
	
	
	/**
	 * app 
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/member/login.json")
	@ResponseBody
	public Object login(@RequestParam("loginName")String username,
			//@RequestParam("jiguang_key")String jiguang_key,
			@RequestParam("password")String password,Map<String, String> map){
		if(StringUtils.isBlank(username)){
			return new AppReturn("请输入用户名!");
		}
		if(StringUtils.isBlank(password)){
			return new AppReturn("请输入密码!");
		}
		
		//登录
    	try {
			AuthUserVO user = appService.appLogin(username, password);
			if(user !=null){
				/**极光key更新**/

				UserInfoCommand command = new UserInfoCommand();
				
				command.setMobile(user.getMobile());
				command.setName(user.getLoginName());
				command.setPassword(password);
				command.setRealName(user.getRealName());
				//
				user.setToken(UUID.randomUUID().toString().replaceAll("-", ""));//生成规则 待定
				CacheUtils.put(APP_CACHE_NAME, user.getToken(), user);
				//
				
				command.setToken(user.getToken());
				command.setVersionID("1.0");
				command.setDownUrl("http://139.129.214.90:8089/res/app/highspeed_android.apk");
				
				log.info("{} {} {} App登陆成功",user.getLoginName(),user.getUid(),user.getRealName());
				// TODO 记录日志
				return new AppReturn(true,command);
			}
			//登录失败
			return new AppReturn("登录失败，未知错误！");
		} catch (Exception e) {
			log.error("登录失败,"+e.getMessage());
			return new AppReturn("登录失败,"+e.getMessage());
		}
	}
	/**
	 * 处理中心/下属部门
	 * @param user
	 * @param param
	 */
	private void deptList(AuthUserVO user, Map<String, Object> param)
	  {
	    List<SysDepartment> deptList = null;
	    if ((user.getDeptId() == null) && (user.getEnabledmark().intValue() == 102002)) {
	      return;
	    }
	    if ((user.getDeptId() == null) && (user.getEnabledmark().intValue() == 102001))
	    {
	      List<SysOrganize> orgList = this.orgService.getOrgAllChildren(user.getOrgId());
	      
	      StringBuffer orgs = new StringBuffer();
	      for (SysOrganize org : orgList) {
	        orgs.append(",'").append(org.getOrganizeid()).append("'");
	      }
	      param.put("orgs", orgs.deleteCharAt(0).toString());
	      
	      deptList = this.deptService.showDept(param);
	    }
	    else if (StringUtils.isNoneBlank(new CharSequence[] { user.getDeptId() }))
	    {
	      deptList = this.deptService.getAllChildren(user.getOrgId(), user.getDeptId());
	    }
	    StringBuffer depts = new StringBuffer();
	    for (SysDepartment dept : deptList) {
	      depts.append(",'").append(dept.getDepartmentid()).append("'");
	    }
	    param.put("depts", depts.deleteCharAt(0).toString());
	  }
	/**
	 * app 首页
	 * @param token
	 * @return
	 */
	 @RequestMapping({"index.htm"})
	  public String index(@RequestParam("token") String token, Model model)
	  {
	    AuthUserVO user = (AuthUserVO)CacheUtils.get(this.APP_CACHE_NAME, token);
	    if (user == null) {
	      return this.LOGIN;
	    }
	    Map<String, Object> param = new HashMap();
	    deptList(user, param);
	    
	    DateTime dt = new DateTime();
		String year = dt.getYear()+"";
		String yearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(-1);
		String preYear = dt.getYear()+"";
		String preYearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(1).plusMonths(-1);
		String preMonth = dt.toString("yyyyMM");
	    
	    param.put("year", year);
		param.put("rw", year);
		param.put("preYearMonth", preYearMonth);
		param.put("preMonth", preMonth);
		param.put("yearMonth", yearMonth);
		param.put("preYear", preYear);
		List<KeyValue> rwMap = appService.getNzsrw(param);
		for (KeyValue value : rwMap) {
			param.put(value.getId(), value.getName());
		}
		BigDecimal wan = new BigDecimal(10000);
		BigDecimal rw = param.get(year)==null?BigDecimal.ZERO:new BigDecimal((String)param.get(year));
		model.addAttribute("rw", rw.divide(wan,2,BigDecimal.ROUND_HALF_UP));
		List<KeyValue> reMap = appService.getZswcqk(param);
		for (KeyValue value : reMap) {
			param.put(value.getId(), value.getName());
		}
		//本年征收
		//本月征收
		//环比增长
		//同比增长
		
		BigDecimal qn = param.get(preYearMonth)==null?BigDecimal.ZERO:new BigDecimal((String)param.get(preYearMonth));
		BigDecimal sy = param.get(preMonth)==null?BigDecimal.ZERO:new BigDecimal((String)param.get(preMonth));
		BigDecimal bn = param.get(year)==null?BigDecimal.ZERO:new BigDecimal((String)param.get(year));
		BigDecimal by = param.get(yearMonth)==null?BigDecimal.ZERO:new BigDecimal((String)param.get(yearMonth));
		BigDecimal qnn = param.get(preYear)==null?BigDecimal.ZERO:new BigDecimal((String)param.get(preYear));
		//
		
		model.addAttribute("bn", bn.divide(wan,2,BigDecimal.ROUND_HALF_UP));
		model.addAttribute("by", by.divide(wan,2,BigDecimal.ROUND_HALF_UP));
		
		//
		
		BigDecimal huanbi = BigDecimal.ZERO;
		BigDecimal tongbi = BigDecimal.ZERO;
		BigDecimal qntongbi = BigDecimal.ZERO;
		BigDecimal rwb = BigDecimal.ZERO;
		if(sy!=BigDecimal.ZERO){
			/**环比增长率＝（本期数－上期数）/上期数*100％ 反映本期比上期增长了多少*/
			huanbi = (by.subtract(sy)).multiply(new BigDecimal(100)).divide(sy,2, BigDecimal.ROUND_HALF_UP);
		}
		if(qn!=BigDecimal.ZERO){
			/**同比增长率＝（本期数－同期数）/同期数*100％ 指和去年同期相比较的增长率。**/
			tongbi = (by.subtract(qn)).multiply(new BigDecimal(100)).divide(qn,2, BigDecimal.ROUND_HALF_UP);
		}
		if(qnn!=BigDecimal.ZERO){
			/**环比增长率＝（本年数－上年数）/上年数*100％ 反映本年比上年增长了多少*/
			qntongbi = (bn.subtract(qnn)).multiply(new BigDecimal(100)).divide(qnn,2,BigDecimal.ROUND_HALF_UP);
			
		}
		if(rw!=BigDecimal.ZERO){
			/**环比增长率＝本年数/任务值*100％ 反映本年完成了任务多少*/
			rwb = bn.multiply(new BigDecimal(100)).divide(rw,2,BigDecimal.ROUND_HALF_UP);
			
		}
		
		model.addAttribute("tongbi", tongbi);
		model.addAttribute("huanbi", huanbi);
		model.addAttribute("qntongbi",qntongbi);
		model.addAttribute("rwb",rwb);
		//任务完成率
	    
	    String sszd = user.getDeptId();
	    if (StringUtils.isBlank(user.getDeptId())) {
	      sszd = user.getOrgId();
	    }
	   
	    BigDecimal ycsz = this.appService.getZdYc(sszd);
	    BigDecimal wcl = bn.multiply(new BigDecimal(100)).divide(ycsz == null ? BigDecimal.ZERO : ycsz, 2, 4);
	    model.addAttribute("wcl", wcl);
	    
	    String title = user.getOrgName();
	    if ((user.getDeptId() == null) && (user.getEnabledmark().intValue() == 102001))
	    {
	      model.addAttribute("orgId", user.getOrgId());
	    }
	    else if (user.getDeptId() != null)
	    {
	      model.addAttribute("deptId", user.getDeptId());
	      title = user.getDeptName();
	    }
	    model.addAttribute("title", title);
	    return "app/index";
	  }
	
	/**
	 * 首页数据
	 * @param token
	 * @return
	 */
	  @RequestMapping({"app/first.json"})
	  @ResponseBody
	  public Object firstData(@RequestParam("token") String token)
	  {
	    AuthUserVO user = (AuthUserVO)CacheUtils.get(this.APP_CACHE_NAME, token);
	    if (user == null) {
	      return this.LOGIN;
	    }
	    Map<String, Object> param = new HashMap();
	    deptList(user, param);
	    
	    DateTime dt = new DateTime();
	    String yearMonth = dt.toString("yyyyMM");
	    param.put("rq", yearMonth);
	    
	    EtcVO etc = this.appService.getEtcData(param);
	    
	    List<LtsjVO> ltList = this.appService.getLtData(param);
	    Map<String, Object> reMap = new HashMap(2);
	    reMap.put("etc", etc);
	    reMap.put("lt", ltList);
	    return new ExtReturn(true, reMap);
	  }
	  
	  @RequestMapping({"app/yhData.json"})
	  @ResponseBody
	  public Object yhData(@RequestParam("token") String token, @RequestParam(value="rq", required=false) String rq, @RequestParam(value="depts", required=false) String depts)
	  {
	    AuthUserVO user = (AuthUserVO)CacheUtils.get(this.APP_CACHE_NAME, token);
	    if (user == null) {
	      return this.LOGIN;
	    }
	    Map<String, Object> param = new HashMap(3);
	    if (StringUtils.isBlank(rq))
	    {
	      DateTime dt = new DateTime();
	      rq = dt.toString("yyyy-MM-dd");
	    }
	    param.put("rq", rq);
	    if (StringUtils.isNoneBlank(new CharSequence[] { depts })) {
	      param.put("depts", depts);
	    } else {
	      deptList(user, param);
	    }
	    YhsjVO yhData = this.appService.getYhData(param);
	    if (null == yhData) {
	      yhData = new YhsjVO();
	    }
	    return new ExtReturn(true, yhData);
	  }
	
	/**
	 * 态势 页面
	 * @param token
	 * @return
	 */
	@RequestMapping("app/second.htm")
	public String second(@RequestParam("token")String token,Model model){
		AuthUserVO user = (AuthUserVO) CacheUtils.get(APP_CACHE_NAME, token);
		if(user==null){
			return LOGIN;//重新登录
		}
		//选择控制部门展示
		String title=user.getOrgName();
		if(user.getDeptId() == null && user.getEnabledmark()==102001){//中心
			model.addAttribute("orgId", user.getOrgId());
		}else if(user.getDeptId()!=null){//项目部
			model.addAttribute("deptId", user.getDeptId());
			title = user.getDeptName();
		}
		model.addAttribute("title", title);
		return "app/second";
	}
	/**
	 * 征收态势
	 * @param token
	 * @param depts
	 * @return
	 */
	@RequestMapping("app/zsts.json")
	@ResponseBody
	public Object zstsData(@RequestParam("token")String token,@RequestParam(value="depts",required=false)String depts){
		AuthUserVO user = (AuthUserVO) CacheUtils.get(APP_CACHE_NAME, token);
		if(user==null){return LOGIN;}
		//
		Map<String,Object> param = new HashMap<String,Object>();
		if(StringUtils.isNoneBlank(depts)){
			param.put("depts", depts);
		}else{
			deptList(user,param);//
		}
		//
		DateTime dt = new DateTime();
		int y = dt.getYear();
		int y1 = dt.plusYears(-1).getYear();
		int y2 = dt.plusYears(-2).getYear();
		param.put("y",y);
		param.put("y1",y1);
		param.put("y2",y2);
		//
		BigDecimal zero = BigDecimal.ZERO;
		BigDecimal wan = new BigDecimal(10000);
		//
		Map<String,Object> reMap = new HashMap<String, Object>();
		reMap.put(y+"", new BigDecimal[]{zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero});
		reMap.put(y1+"", new BigDecimal[]{zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero});
		reMap.put(y2+"", new BigDecimal[]{zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero});
		List<KeyValue> zstsList = appService.getzstsData(param);
		//
		for (KeyValue key : zstsList) {
			BigDecimal[] datas = (BigDecimal[])reMap.get(key.getId());
			//拆分map
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(key.getName());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//wan 保留2位
				datas[Integer.valueOf(entry.getKey())-1] = new BigDecimal(entry.getValue()).divide(wan,2,BigDecimal.ROUND_HALF_UP);
			}  
//			reMap.put(key.getId(), value);
		}
		reMap.put("y",y);
		reMap.put("y1",y1);
		reMap.put("y2",y2);
		return new ExtReturn(true,reMap);
	}
	/**
	 * 拆后收入走势
	 * @param token
	 * @param depts
	 * @return
	 */
	@RequestMapping("app/chsrzs.json")
	@ResponseBody
	public Object chsrzsData(@RequestParam("token")String token,@RequestParam(value="depts",required=false)String depts){
		AuthUserVO user = (AuthUserVO) CacheUtils.get(APP_CACHE_NAME, token);
		if(user==null){return LOGIN;}
		//
		Map<String,Object> param = new HashMap<String,Object>();
		if(StringUtils.isNoneBlank(depts)){
			param.put("depts", depts);
		}else{
			deptList(user,param);//
		}
		//
		DateTime dt = new DateTime();
		int y = dt.getYear();
		int y1 = dt.plusYears(-1).getYear();
		int y2 = dt.plusYears(-2).getYear();
		param.put("y",y);
		param.put("y1",y1);
		param.put("y2",y2);
		//
		BigDecimal zero = BigDecimal.ZERO;
		BigDecimal wan = new BigDecimal(10000);
		//
		Map<String,Object> reMap = new HashMap<String, Object>();
		reMap.put(y+"", new BigDecimal[]{zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero});
		reMap.put(y1+"", new BigDecimal[]{zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero});
		reMap.put(y2+"", new BigDecimal[]{zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero});
		List<KeyValue> zstsList = appService.getchsrzsData(param);
		//
		for (KeyValue key : zstsList) {
			BigDecimal[] datas = (BigDecimal[])reMap.get(key.getId());
			//拆分map
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(key.getName());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//wan 保留2位
				datas[Integer.valueOf(entry.getKey())-1] = new BigDecimal(entry.getValue()).divide(wan,2,BigDecimal.ROUND_HALF_UP);
			}  
//			reMap.put(key.getId(), value);
		}
		reMap.put("y",y);
		reMap.put("y1",y1);
		reMap.put("y2",y2);
		return new ExtReturn(true,reMap);
	}
	/**
	 * 客货车费用/流量占比信息
	 * @param token
	 * @param depts
	 * @return
	 */
	@RequestMapping("app/fyzb.json")
	@ResponseBody
	public Object fyzbData(@RequestParam("token")String token,@RequestParam(value="depts",required=false)String depts){
		AuthUserVO user = (AuthUserVO) CacheUtils.get(APP_CACHE_NAME, token);
		if(user==null){return LOGIN;}
		//
		Map<String,Object> param = new HashMap<String,Object>();
		if(StringUtils.isNoneBlank(depts)){
			param.put("depts", depts);
		}else{
			deptList(user,param);//
		}
		List<FyzbVO> fyList = appService.getFyzbData(param);
		//
		List<Integer> listAll = Lists.newArrayList(1,2,3,4,5,6,7,8,9,10,11,12);
		List<Integer> list = Lists.newArrayList();
		for (int j = 0; j < fyList.size(); j++) {
			list.add(fyList.get(j).getMonth());
		}
		listAll.removeAll(list);//差集
		for (Integer integer : listAll) {
			fyList.add(new FyzbVO(integer));
		}
		//排序
		fyList = new Ordering<FyzbVO>() {
			  @Override
			  public int compare(FyzbVO left, FyzbVO right) {
			        return left.getMonth() - right.getMonth();
			  }
		}.immutableSortedCopy(fyList);
		Map<String,Object> reMap = Maps.newHashMap();
		reMap.put("fyzb", fyList);
		KhlxVO khVO = appService.getKhLxData(param);
		reMap.put("khlx", khVO);
		return new ExtReturn(true,reMap);
	}
	/**
	 * 绿通车次走势
	 * @param token
	 * @param depts
	 * @return
	 */
	@RequestMapping("app/cczs.json")
	@ResponseBody
	public Object cczsData(@RequestParam("token")String token,@RequestParam(value="depts",required=false)String depts){
		AuthUserVO user = (AuthUserVO) CacheUtils.get(APP_CACHE_NAME, token);
		if(user==null){return LOGIN;}
		//
		Map<String,Object> param = new HashMap<String,Object>();
		if(StringUtils.isNoneBlank(depts)){
			param.put("depts", depts);
		}else{
			deptList(user,param);//
		}
		//
		DateTime dt = new DateTime();
		int y = dt.getYear();
		int y1 = dt.plusYears(-1).getYear();
		param.put("y",y);
		param.put("y1",y1);
		//
		BigDecimal zero = BigDecimal.ZERO;
		//
		Map<String,Object> reMap = new HashMap<String, Object>();
		reMap.put(y+"", new BigDecimal[]{zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero});
		reMap.put(y1+"", new BigDecimal[]{zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero,zero});
		List<KeyValue> zstsList = appService.getcctsData(param);
		//
		for (KeyValue key : zstsList) {
			BigDecimal[] datas = (BigDecimal[])reMap.get(key.getId());
			//拆分map
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(key.getName());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//wan 保留2位
				datas[Integer.valueOf(entry.getKey())-1] = new BigDecimal(entry.getValue());
			}  
		}
		reMap.put("y",y);
		reMap.put("y1",y1);
		return new ExtReturn(true,reMap);
	}
	/**
	 * P3
	 * @param token
	 * @return
	 */
	 @RequestMapping({"app/third.htm"})
	  public String third(@RequestParam("token") String token, Model model)
	  {
	    AuthUserVO user = (AuthUserVO)CacheUtils.get(this.APP_CACHE_NAME, token);
	    if (user == null) {
	      return this.LOGIN;
	    }
	    String title = user.getOrgName();
	    if ((user.getDeptId() != null) || (user.getEnabledmark().intValue() != 102001)) {
	      if (user.getDeptId() != null) {
	        title = user.getDeptName();
	      }
	    }
	    model.addAttribute("title", title);
	    
	    return "app/third";
	  }
	  
	  @RequestMapping({"app/fyfb.json"})
	  @ResponseBody
	  public Object fyfbData(@RequestParam("token") String token)
	  {
	    AuthUserVO user = (AuthUserVO)CacheUtils.get(this.APP_CACHE_NAME, token);
	    if (user == null) {
	      return this.LOGIN;
	    }
	    Map<String, Object> param = new HashMap();
	    deptList(user, param);
	    
	    List<LlfyVO> llList = this.appService.getLLFY(param);
	    
	    Map<String, Object> reMap = Maps.newHashMap();
	    reMap.put("llfy", llList);
	    return new ExtReturn(true, reMap);
	  }
	
	@RequestMapping({"app/jjrTj.json"})
	  @ResponseBody
	  public Object jjrTj(@RequestParam("token") String token)
	  {
	    AuthUserVO user = (AuthUserVO)CacheUtils.get(this.APP_CACHE_NAME, token);
	    if (user == null) {
	      return this.LOGIN;
	    }
	    Map<String, Object> param = new HashMap(5);
	    deptList(user, param);
	    
	    DateTime dt = new DateTime();
	    int y = dt.getYear();
	    int y1 = dt.plusYears(-1).getYear();
	    int y2 = dt.plusYears(-2).getYear();
	    param.put("y", Integer.valueOf(y));
	    param.put("y1", Integer.valueOf(y1));
	    param.put("y2", Integer.valueOf(y2));
	    
	    Map<String, Object> reMap = Maps.newHashMap();
	    
	    reMap.put(y + "", new Integer[] { Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0) });
	    reMap.put(y1 + "", new Integer[] { Integer.valueOf(2314242), Integer.valueOf(1127270), Integer.valueOf(1430671), Integer.valueOf(3111200) });
	    
	    reMap.put(y2 + "", new Integer[] { Integer.valueOf(2163799), Integer.valueOf(981454), Integer.valueOf(1336906), Integer.valueOf(2723394) });
	    
	    reMap.put("y", Integer.valueOf(y));
	    reMap.put("y1", Integer.valueOf(y1));
	    reMap.put("y2", Integer.valueOf(y2));
	    

	    List<KeyValue> jjrList = this.appService.getJjrData(param);
	    for (int i = 0; i < jjrList.size(); i++)
	    {
	      KeyValue vo = (KeyValue)jjrList.get(i);
	      if (vo != null)
	      {
	        String year = vo.getId().split("_")[0];
	        String jrIndex = vo.getId().split("_")[1];
	        Integer[] jrArr = (Integer[])reMap.get(year);
	        jrArr[Integer.valueOf(jrIndex).intValue()] = Integer.valueOf(vo.getName());
	      }
	    }
	    return new ExtReturn(true, reMap);
	  }
	/**
	 * @param token
	 * @return
	 */
	@RequestMapping("app/forth.htm")
	public String forth(@RequestParam("token")String token,Model model){
		AuthUserVO user = (AuthUserVO) CacheUtils.get(APP_CACHE_NAME, token);
		if(user==null){
			return LOGIN;//重新登录
		}
		//选择控制部门展示
		String title=user.getOrgName();
		if(StringUtils.isNoneBlank(user.getDeptId())){//
			title = user.getDeptName();
			model.addAttribute("deptId", user.getDeptId());
		}
		model.addAttribute("title", title);
		//本单位联系人
		List<AuthUserVO> userList = appService.getUserList();//15
		model.addAttribute("users", userList);
		//最新10条 特殊情况数据
		List<Sztqsj> sjList = appService.getSztqsjList();//
		model.addAttribute("sjList", sjList);
		//
		model.addAttribute("token", token);
		return "app/forth";
	}
	/**
	 * 新增 特情信息
	 * @return
	 */
	@RequestMapping(value="app/addTqsj")
	@ResponseBody
	public Object addTqsj(@RequestParam("token")String token,
			@RequestParam("bt")String bt,
			@RequestParam("nr")String nr){
		AuthUserVO user = (AuthUserVO) CacheUtils.get(APP_CACHE_NAME, token);
		if(user==null){return LOGIN;}
		Sztqsj tqsj = new Sztqsj();
		tqsj.setLrsj(new Date());
		tqsj.setZdmc(user.getDeptName());
		tqsj.setLrymc(user.getRealName());
		tqsj.setZt(1);
		//
		tqsj.setBt(bt);
		tqsj.setNr(nr);
		try{
			appService.newSztqsj(tqsj);
			return new ExtReturn(true,"操作成功");
		}catch(Exception e){
			return new ExtReturn(false,"操作失败");
		}
	}
	/**
	 * 特情信息 明细
	 * @return
	 */
	@RequestMapping(value="app/tqsjMx")
	public String addTqsj(@RequestParam("token")String token,
			@RequestParam("xh")Integer xh,Model model){
		AuthUserVO user = (AuthUserVO) CacheUtils.get(APP_CACHE_NAME, token);
		if(user==null){return LOGIN;}
		
		model.addAttribute("token", token);
		model.addAttribute("info", appService.getSztqMx(xh));
		return "app/tqmx";
	}

}
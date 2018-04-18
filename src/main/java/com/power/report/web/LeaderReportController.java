package com.power.report.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.power.app.service.AppService;
import com.power.base.sys.service.DeptService;
import com.power.common.entity.FxChsjYb;
import com.power.common.entity.FxJdxmz;
import com.power.common.entity.FxMfclYb;
import com.power.common.entity.FxRcsj;
import com.power.common.springmvc.ResponseUtils;
import com.power.common.utils.Reflections;
import com.power.index.service.IndexService;
import com.power.report.entity.CrkcllDataVO;
import com.power.report.entity.CzjzbDataVO;
import com.power.report.entity.DlxxbDataVO;
import com.power.report.entity.JdxmzDataVO;
import com.power.report.entity.LtmxbDataVO;
import com.power.report.service.LeaderReportService;



@Controller
@RequestMapping("report")


	
public class LeaderReportController {
	
	private final static Logger log = LoggerFactory.getLogger(ReportController.class);
	@Autowired
	private AppService appService;
	@Autowired
	private LeaderReportService leaderReportService;
	@Autowired
	private IndexService indexService;
	
	/**
	 * 计算环比
	 * @param bq 本期
	 * @param sq 上期
	 * @return
	 */
	private BigDecimal getHuanbi(BigDecimal bq,BigDecimal sq){
		BigDecimal huanbi = BigDecimal.ZERO;
		if(0!=sq.compareTo(BigDecimal.ZERO)){
			/**环比增长率＝（本期数－上期数）/上期数*100％ 反映本期比上期增长了多少*/
			huanbi = (bq.subtract(sq)).multiply(new BigDecimal(100)).divide(sq,2, BigDecimal.ROUND_HALF_UP);
		}
		return huanbi;
	}
	/**
	 * 计算tongbi
	 * @param bq 本期
	 * @param sq 上期
	 * @return
	 */
	private BigDecimal getTongbi(BigDecimal bq,BigDecimal tq){
		BigDecimal tongbi = BigDecimal.ZERO;
		if(0!=tq.compareTo(BigDecimal.ZERO)){
			/**同比增长率＝（本期数－同期数）/同期数*100％ 指和去年同期相比较的增长率。**/
			tongbi = (bq.subtract(tq)).divide(tq,2, BigDecimal.ROUND_HALF_UP);
		}
		return tongbi;
	}
	
	/**
	 * 绿通明细表页面跳转
	 */
	@RequestMapping("ltmxbIndex")
	public String ltmxbIndex(Model model){
		//上月
				DateTime dt = new DateTime();
				String st = dt.plusMonths(-1).toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/ltmxbIndex";
	}
	/**
	 * 绿通明细表数据
	 * @param ch
	 * @param rsp
	 * @param rc
	 * @param st
	 * @param et
	 * @param orgId
	 * @param deptId
	 */
	@RequestMapping("ltmxbData")
	public void ltmxbData(HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		//处理时间
		String preMonth = st.substring(st.indexOf("年") + 1);
		String preYear = st.substring(0,4);
		String month = st.replace("年", "").replace("月", "");
		String preSt = "";
		if(preMonth.equals("01月")){
			String preM = "12";
			Integer preyear =  Integer.parseInt(preYear);
			String preY = preyear-1+"";
			 preSt = preY+preM;
			 preYear =  preyear-1+"";
		}else{
			Integer premonth =  Integer.parseInt(month);
			String preM = premonth-1+"";
				 preSt = preM;
		}
		
		//将时间map到xml
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(month)){
			map.put("month",month);//本月
		}
		if(StringUtils.isNoneBlank(preSt)){
			map.put("preSt",preSt);//上月
		}
		if(StringUtils.isNoneBlank(preYear)){
			map.put("preYear",preYear);//本年
		}
		
		//定义的键值对
		Map<String,String> ldMap=Maps.newHashMap();
		ldMap.put(preSt, "symz");//上月
		ldMap.put(month, "bymz");//本月
		ldMap.put(preYear, "bnljmz");//本年
		ldMap.put("all", "zktljmz");//自开通
		
		indexService.setOrgDeptParam(orgId,deptId,map);
		//数据拆分		
		List<LtmxbDataVO>  list = leaderReportService.getltmxbData(map);
		for(LtmxbDataVO lt :list){
			Map<String,String> strMap  = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(lt.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
			String yf = ldMap.get(entry.getKey());
			List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
			Reflections.setFieldValue(lt, yf+"ll", Integer.valueOf(ll.get(0)));
			Reflections.setFieldValue(lt, yf+"je", new BigDecimal(ll.get(1)));
			lt.setFullName(lt.getFullName());;
		}
		}
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
		for(LtmxbDataVO vo:list){
			sb.append("<Detail><sfrq>").append(st).append("</sfrq>");
			sb.append("<dw>").append(vo.getFullName()).append("</dw>");
			sb.append("<bymzje>").append(vo.getBymzje()).append("</bymzje>");
			sb.append("<symzje>").append(vo.getSymzje()).append("</symzje>");
			sb.append("<bymzjeysyb>").append(getHuanbi(vo.getBymzje(), vo.getSymzje())+"%").append("</bymzjeysyb>");
			sb.append("<bymzll>").append(vo.getBymzll()).append("</bymzll>");
			sb.append("<symzll>").append(vo.getSymzll()).append("</symzll>");
			BigDecimal bymzll = new BigDecimal(vo.getBymzll());
			BigDecimal symzll = new BigDecimal(vo.getSymzll());
			sb.append("<bymzllysyb>").append(getHuanbi(bymzll, symzll)+"%").append("</bymzllysyb>");
			sb.append("<bnljmzje>").append(vo.getBnljmzje()).append("</bnljmzje>");
			sb.append("<bnljmzll>").append(vo.getBnljmzll()).append("</bnljmzll>");
			sb.append("<zktljmzje>").append(vo.getZktljmzje()).append("</zktljmzje>");
			sb.append("<zktljmzll>").append(vo.getZktljmzll()).append("</zktljmzll></Detail>");
		}
		sb.append("</xml><_grparam><st>"+st+"</st></_grparam></report>");
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	/**
	 * 通行费超载计重收入明细表页面跳转
	 */
	@RequestMapping("dlxxbIndex")
	public String dlxxbIndex(Model model){
		//上月
				DateTime dt = new DateTime();
				String st = dt.plusMonths(-1).toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/dlxxbIndex";
	}
	
	/**
	 * 道路信息表数据
	 * @param ch
	 * @param rsp
	 * @param rc
	 * @param st
	 * @param et
	 * @param orgId
	 * @param deptId
	 */
	@RequestMapping("dlxxbData")
	public void dlxxbData(FxMfclYb fm,HttpServletResponse rsp,FxRcsj rc,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		
		//
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		String ssr = st.replace("年", "").replace("月", "");
		if(StringUtils.isNoneBlank(ssr)){
			map.put("st",ssr);
		}
		if(StringUtils.isBlank(st) && StringUtils.isBlank(et)){
			map.put("dt",DateTime.now().toString("yyyyMM"));
		}
		//数据拆分	
		indexService.setOrgDeptParam(orgId,deptId,map);
		List<DlxxbDataVO> list = leaderReportService.getDlxxbData(map);
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
		for(DlxxbDataVO vo:list){
			sb.append("<Detail><sfrq>").append(st).append("</sfrq>");
			sb.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
			sb.append("<gwcrhj>").append(vo.getGwcrhj()).append("</gwcrhj>");
			sb.append("<jccrhj>").append(vo.getJccrhj()).append("</jccrhj>");
			sb.append("<jchecrhj>").append(vo.getJchecrhj()).append("</jchecrhj>");
			sb.append("<mfcrhj>").append(vo.getMfcrhj()).append("</mfcrhj>");
			sb.append("<cdhj>").append(vo.getCdhj()).append("</cdhj>");
			sb.append("<ltc>").append(vo.getLtc()).append("</ltc>");
			sb.append("<cghj>").append(vo.getCghj()).append("</cghj>");
			sb.append("<crkllhj>").append(vo.getCrkllhj()).append("</crkllhj></Detail>");
		}
		sb.append("</xml><_grparam><st>"+st+"</st></_grparam></report>");
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	/**
	 * 通行费超载计重收入明细表页面跳转
	 */
	@RequestMapping("czjzbIndex")
	public String czjzbIndex(Model model){
		//上月
				DateTime dt = new DateTime();
				String st = dt.plusMonths(-1).toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/czjzbIndex";
	}
	/**
	 * 通行费超载计重收入明细表数据
	 * @param ch
	 * @param rsp
	 * @param rc
	 * @param st
	 * @param et
	 * @param orgId
	 * @param deptId
	 */
	@RequestMapping("czjzbData")
	public void czjzbData(FxMfclYb fm,HttpServletResponse rsp,FxRcsj rc,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		String ssr = st.replace("年", "-").replace("月", "");
		DateTime dt = new DateTime(ssr);
		Integer year = dt.getYear();
		String yearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(-1);
		String preYearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(1).plusMonths(-1);
		String preMonth = dt.toString("yyyyMM");
		
		//
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(null!=year){
			map.put("year",year);
		}
		if(StringUtils.isNoneBlank(yearMonth)){
			map.put("yearMonth",yearMonth);
		}
		if(StringUtils.isNoneBlank(preYearMonth)){
			map.put("preYearMonth",preYearMonth);
		}
		if(StringUtils.isNoneBlank(preMonth)){
			map.put("preMonth",preMonth);
		}
		String Year = year+"";
		//定义的键值对
		Map<String,String> sbMap= Maps.newHashMap();
		sbMap.put(Year, "bnljczsr");//本年
		sbMap.put(yearMonth, "byczsr");//本月
		sbMap.put(preMonth, "syczsr");//上月
		sbMap.put(preYearMonth, "sntqljczsr");//同期
		//数据拆分	
		indexService.setOrgDeptParam(orgId,deptId,map);
		List<CzjzbDataVO> list = leaderReportService.getCzjzbData(map);
		for(CzjzbDataVO lt :list){
			Map<String,String> strMap  = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(lt.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
			String yf = sbMap.get(entry.getKey());
			List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
			
			Reflections.setFieldValue(lt, yf, new BigDecimal(ll.get(0)));
			lt.setZdmc(lt.getZdmc());;
		}
		}
		DateTime dt1 = new DateTime(st.replace("年", "-").replace("月", ""));
		int mean = dt1.dayOfMonth().getMaximumValue();
		BigDecimal mean1 = new BigDecimal(mean);
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
		for(CzjzbDataVO vo:list){
			sb.append("<row><sfrq>").append(st).append("</sfrq>");
			sb.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
			BigDecimal byczsr = vo.getByczsr()==null?BigDecimal.ZERO:vo.getByczsr();
			sb.append("<byczsr>").append(byczsr).append("</byczsr>");
			BigDecimal syczsr = vo.getSyczsr()==null?BigDecimal.ZERO:vo.getSyczsr();
			sb.append("<syczsr>").append(syczsr).append("</syczsr>");
			BigDecimal Bnljczsr = vo.getBnljczsr()==null?BigDecimal.ZERO:vo.getBnljczsr();
			sb.append("<bnljczsr>").append(Bnljczsr).append("</bnljczsr>");
			BigDecimal Sntqljczsr = vo.getSntqljczsr()==null?BigDecimal.ZERO:vo.getSntqljczsr();
			sb.append("<sntqljczsr>").append(Sntqljczsr).append("</sntqljczsr>");
			sb.append("<bybsy>").append(getHuanbi(byczsr, syczsr)+"%").append("</bybsy>");
			sb.append("<bnysnb>").append(getHuanbi(Bnljczsr, Sntqljczsr)+"%").append("</bnysnb>");
			BigDecimal rj = byczsr.divide(mean1, 2, BigDecimal.ROUND_UP);
			sb.append("<rjczsr>").append(rj).append("</rjczsr></row>");
		}
		sb.append("</xml><_grparam><st>"+st+"</st></_grparam></report>");
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	/**
	 * 出入口车辆表页面跳转
	 */
	@RequestMapping("crkcllIndex")
	public String crkcllIndex(Model model){
		//上月
				DateTime dt = new DateTime();
				String st = dt.plusMonths(-1).toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/crkcllIndex";
	}
	/**
	 * 出入口车辆数据
	 * @param ch
	 * @param rsp
	 * @param rc
	 * @param st
	 * @param et
	 * @param orgId
	 * @param deptId
	 */
	@RequestMapping("crkcllData")
	public void crkcllData(FxMfclYb fm,HttpServletResponse rsp,FxRcsj rc,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		
		//
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		String ssr = st.replace("年", "").replace("月", "");
		if(StringUtils.isNoneBlank(ssr)){
			map.put("st",ssr);
		}
		if(StringUtils.isBlank(st) && StringUtils.isBlank(et)){
			map.put("dt",DateTime.now().toString("yyyyMM"));
		}
		//数据拆分	
		indexService.setOrgDeptParam(orgId,deptId,map);
		List<CrkcllDataVO> list = leaderReportService.getCrkcllData(map);
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
		
		
		for(CrkcllDataVO vo:list){
			sb.append("<row><rk>").append("1").append("</rk>");
			sb.append("<crk>").append("（入口）").append("</crk>");
			sb.append("<sfrq>").append(st).append("</sfrq>");
			sb.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
			sb.append("<kca>").append(vo.getKcr1()).append("</kca>");
			sb.append("<kcb>").append(vo.getKcr2()).append("</kcb>");
			sb.append("<kcc>").append(vo.getKcr3()).append("</kcc>");
			sb.append("<kcd>").append(vo.getKcr4()).append("</kcd>");
			sb.append("<hca>").append(vo.getHcr1()).append("</hca>");
			sb.append("<hcb>").append(vo.getHcr2()).append("</hcb>");
			sb.append("<hcc>").append(vo.getHcr3()).append("</hcc>");
			sb.append("<hcd>").append(vo.getHcr4()).append("</hcd>");
			sb.append("<hce>").append(vo.getHcr5()).append("</hce>");
			sb.append("<hcf>").append(vo.getHcr6()).append("</hcf></row>");
		}
			
			
		for(CrkcllDataVO vo:list){
			sb.append("<row><rk>").append("2").append("</rk>");
			sb.append("<crk>").append("（出口）").append("</crk>");
			sb.append("<sfrq>").append(st).append("</sfrq>");
			sb.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
			sb.append("<kca>").append(vo.getKcc1()).append("</kca>");
			sb.append("<kcb>").append(vo.getKcc2()).append("</kcb>");
			sb.append("<kcc>").append(vo.getKcc3()).append("</kcc>");
			sb.append("<kcd>").append(vo.getKcc4()).append("</kcd>");
			sb.append("<hca>").append(vo.getHcc1()).append("</hca>");
			sb.append("<hcb>").append(vo.getHcc2()).append("</hcb>");
			sb.append("<hcc>").append(vo.getHcc3()).append("</hcc>");
			sb.append("<hcd>").append(vo.getHcc4()).append("</hcd>");
			sb.append("<hce>").append(vo.getHcc5()).append("</hce>");
			sb.append("<hcf>").append(vo.getHcc6()).append("</hcf></row>");
		}
		sb.append("</xml><_grparam><st>"+st+"</st></_grparam></report>");
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	/**
	 *阶段性免征表页面跳转
	 */
	@RequestMapping("jdxmzIndex")
	public String jdxmzIndex(Model model){
		//上月
				
		return "report/jdxmzIndex";
	}
	
	/**
	 * 阶段性免征表数据
	 * @param ch
	 * @param rsp
	 * @param rc
	 * @param st
	 * @param et
	 * @param orgId
	 * @param deptId
	 */
	@RequestMapping("jdxmzData")
	public void jdxmzData(FxJdxmz fj,HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="mzsj",required=false)String mzsj){
		
		//
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		String sst = st.replace("年", "-").replace("月", "-").replace("日", "");
		
		if(StringUtils.isNoneBlank(sst)){
			map.put("st",sst);
		}
		if(StringUtils.isNoneBlank(mzsj)){
			map.put("mzsj",mzsj);
		}
		//数据拆分	
		List<JdxmzDataVO> list = leaderReportService.getJdxmzData(map);
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
		for(JdxmzDataVO vo:list){
			sb.append("<row>");
			sb.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
			sb.append("<rk>").append(vo.getRk()).append("</rk>");
			sb.append("<rkhb>").append(vo.getRkhb()).append("</rkhb>");
			sb.append("<ck>").append(vo.getCk()).append("</ck>");
			sb.append("<ckhb>").append(vo.getCkhb()).append("</ckhb>");
			sb.append("<crkhj>").append(vo.getCrk()).append("</crkhj>");
			sb.append("<crkhjhb>").append(vo.getCrkhb()).append("</crkhjhb>");
			sb.append("</row>");
		}
		sb.append("</xml><_grparam>");
		sb.append("<mzsj>").append(mzsj).append("</mzsj>");
		String sr = st.replace("年", "年 ").replace("月", "月 ").replace("日", "日 ");
		sb.append("<rq>").append(sr).append("</rq></_grparam></report>");
		ResponseUtils.renderXml(rsp, sb.toString());
	}
}
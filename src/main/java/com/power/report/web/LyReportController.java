package com.power.report.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.FxCgcl;
import com.power.common.entity.FxChsjYb;
import com.power.common.entity.FxHmd;
import com.power.common.entity.FxLsk;
import com.power.common.entity.FxLtcl;
import com.power.common.entity.FxLtsj;
import com.power.common.entity.FxMfclYb;
import com.power.common.entity.FxPjgl;
import com.power.common.entity.FxPjyb;
import com.power.common.entity.FxQzzh;
import com.power.common.entity.FxRcsj;
import com.power.common.springmvc.ResponseUtils;
import com.power.common.utils.Reflections;
import com.power.index.service.IndexService;
import com.power.report.entity.BbfxsmDataVO;
import com.power.report.entity.DlxxbDataVO;
import com.power.report.entity.FcxllDataVO;
import com.power.report.entity.IcksyqktjbDataVO;
import com.power.report.entity.JellpmDataVO;
import com.power.report.entity.KhcllfyListVO;
import com.power.report.entity.LtmstxfVO;
import com.power.report.entity.TxfsrpjVO;
import com.power.report.entity.txfchsrhzbVO;
import com.power.report.entity.txfcllpmbDataVO;
import com.power.report.entity.txfjcllmxhzDataVO;
import com.power.report.service.LeaderReportService;
import com.power.report.service.LyReportService;

@Controller
@RequestMapping("report")

public class LyReportController {
	private final static Logger log = LoggerFactory.getLogger(ReportController.class);
	@Autowired
	private AppService appService;
	@Autowired
	private LyReportService lyReportService;
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
	 * 报表分析说明页面跳转
	 */
	@RequestMapping("01bbfssmIndex")
	public String bbfssmIndex(Model model){
		//上月
				DateTime dt = new DateTime();
				Integer nd = dt.getYear();
				String st = dt.plusMonths(-1).toString("yyyy年MM月");
				model.addAttribute("st", st);
				model.addAttribute("nd", nd+"年");
		return "report/01bbfssmIndex";
	}
	
	@RequestMapping("0101bbfssmData")
	public void bbfssmndData(FxChsjYb fc,HttpServletResponse rsp,FxRcsj rc,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
		String ssr = st.replace("年", ""); 
		DateTime dt = new DateTime(ssr);
		Integer year = dt.getYear();
		Integer pyear = year-1;
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(ssr)){
			map.put("year",ssr);
		}
		if(null!=pyear){
			map.put("preyear",pyear);
		}
		System.out.println(pyear);
		indexService.setOrgDeptParam(orgId,deptId,map);
		List<BbfxsmDataVO> list = lyReportService.getBbfxsmndData(map);
		Map<String,String> sdMap= Maps.newHashMap();
		sdMap.put("bn", "bn");//本年
		sdMap.put("qn", "bntq");//同期
		for(BbfxsmDataVO vo:list){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
//				System.err.println(entry.getKey()+"-----"+entry.getValue());
				String sj = sdMap.get(entry.getKey());
				//流量 数据 kc/hc/mzc
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, sj+"chsr", new BigDecimal(ll.get(0)));
				
				//
			}
		}
		//报表数据封装
				StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
				for(BbfxsmDataVO vo:list){
					
					sb.append("<sj>").append(st).append("</sj>");
					sb.append("<zdmc>").append(map.get("title")).append("</zdmc>");
					sb.append("<bnchsr>").append(vo.getBnchsr()).append("</bnchsr>");
					sb.append("<bnzj>").append(vo.getBnchsr().subtract(vo.getBntqchsr()).doubleValue()).append("</bnzj>");
					BigDecimal bntqzz = getHuanbi(vo.getBnchsr(), vo.getBntqchsr());
					sb.append("<bnzz>").append(bntqzz+"%").append("</bnzz>");
				}
				sb.append("</xml><_grparam><st>"+st+"</st></_grparam></report>");
				
				ResponseUtils.renderXml(rsp, sb.toString());
	}
			
	@RequestMapping("0102bbfssmData")
	public void bbfssmData(FxChsjYb fc,HttpServletResponse rsp,FxRcsj rc,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		String ssr = st.replace("年", "-").replace("月", ""); 
		DateTime dt = new DateTime(ssr);
		Integer year = dt.getYear();
		Integer pyear = year-1;
		String yearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(-1);
		String preYearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(1).plusMonths(-1);
		String preMonth = dt.toString("yyyyMM");
		String Yearst = year+"01";
		String preyear = pyear+"01";
		//
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(Yearst)){
			map.put("year",Yearst);
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
		if(StringUtils.isNoneBlank(preyear)){
			map.put("preyear",preyear);
		}
		
		//定义的键值对
		Map<String,String> sbMap= Maps.newHashMap();
		sbMap.put("bn", "bn");//本年
		sbMap.put(yearMonth, "by");//本月
		sbMap.put(preMonth, "sy");//上月
		sbMap.put(preYearMonth, "tq");//同期
		sbMap.put("qn", "bntq");//同期
		//数据拆分	
		indexService.setOrgDeptParam(orgId,deptId,map);
		
		List<BbfxsmDataVO> list = lyReportService.getBbfxsmData(map);
		for(BbfxsmDataVO vo:list){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
//				System.err.println(entry.getKey()+"-----"+entry.getValue());
				String sj = sbMap.get(entry.getKey());
				//流量 数据 kc/hc/mzc
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, sj+"chsr", new BigDecimal(ll.get(0)));
				
				//
			}
		}
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
		for(BbfxsmDataVO vo:list){
			
			sb.append("<sj>").append(st).append("</sj>");
			sb.append("<zdmc>").append(map.get("title")).append("</zdmc>");
			sb.append("<bychsr>").append(vo.getBychsr()).append("</bychsr>");
			sb.append("<syzj>").append(vo.getBychsr().subtract(vo.getSychsr()).doubleValue()).append("</syzj>");
			BigDecimal syzz = getHuanbi(vo.getBychsr(), vo.getSychsr());
			sb.append("<syzz>").append(syzz+"%").append("</syzz>");
			sb.append("<bytqzj>").append(vo.getBychsr().subtract(vo.getTqchsr()).doubleValue()).append("</bytqzj>");
			BigDecimal bytqzz = getHuanbi(vo.getBychsr(), vo.getTqchsr());
			sb.append("<bytqzz>").append(bytqzz+"%").append("</bytqzz>");
			sb.append("<bntqzj>").append(vo.getBnchsr().subtract(vo.getBntqchsr()).doubleValue()).append("</bntqzj>");
			BigDecimal bntqzz = getHuanbi(vo.getBnchsr(), vo.getBntqchsr());
			sb.append("<bntqzz>").append(bntqzz+"%").append("</bntqzz>");
		}
		sb.append("</xml><_grparam><st>"+st+"</st></_grparam></report>");
		
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	/**
	 * 通行费拆后收入汇总表
	 * @param model
	 * @return
	 */
	@RequestMapping("02txfchsrhzbIndex")
	public String txfchsrhzbIndex(Model model){
		//上月
				DateTime dt = new DateTime();
				String st = dt.plusMonths(-1).toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/02txfchsrhzbIndex";
	}
	
	@RequestMapping("02txfchsrhzbData")
	public void txfchsrhzbData(HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		
		//
		HashMap<String, Object> map = new HashMap<String, Object>(6);
	
		
		//定义的键值对
		Map<String,String> ldMap= Maps.newHashMap();
		ldMap.put("lsd", "lsd");//本年
		ldMap.put("sld", "sld");//本月；
		ldMap.put("lnd", "lnd");//上月
		ldMap.put("nld", "nld");//同期
		ldMap.put("glc", "glc");//同期
		//数据拆分	
		indexService.setOrgDeptParam(orgId,deptId,map);
		
		List<txfchsrhzbVO> list = lyReportService.getTxfchsrhzData(map);
		List<txfchsrhzbVO> listzkt = lyReportService.getTxfchsrzktData(map);
		for(txfchsrhzbVO vo:list){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getJehj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
//				System.err.println(entry.getKey()+"-----"+entry.getValue());
				String sj = ldMap.get(entry.getKey());
				//流量 数据 kc/hc/mzc
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, sj+"zcsr", new BigDecimal(ll.get(0)));
				Reflections.setFieldValue(vo, sj+"jzsr", new BigDecimal(ll.get(1)));
				Reflections.setFieldValue(vo, sj+"ck", new BigDecimal(ll.get(2)));
				Reflections.setFieldValue(vo, sj+"jsk", new BigDecimal(ll.get(3)));
				Reflections.setFieldValue(vo, sj+"etccq", new BigDecimal(ll.get(4)));
				//
			}
		}
		for(txfchsrhzbVO vo:listzkt){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getJehj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
//				System.err.println(entry.getKey()+"-----"+entry.getValue());
				String sj = ldMap.get(entry.getKey());
				//流量 数据 kc/hc/mzc
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, sj+"zcsr", new BigDecimal(ll.get(0)));
				Reflections.setFieldValue(vo, sj+"jzsr", new BigDecimal(ll.get(1)));
				Reflections.setFieldValue(vo, sj+"ck", new BigDecimal(ll.get(2)));
				Reflections.setFieldValue(vo, sj+"jsk", new BigDecimal(ll.get(3)));
				Reflections.setFieldValue(vo, sj+"etccq", new BigDecimal(ll.get(4)));
				//
			}
		}
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
		for(txfchsrhzbVO vo:list){
			sb.append("<row><nd>").append(vo.getNd()).append("</nd>");
			sb.append("<sj>").append(vo.getNd()+"年"+vo.getYf()+"月").append("</sj>");
			
			sb.append("<lsdzcsr>").append(vo.getLsdzcsr()).append("</lsdzcsr>");
			sb.append("<lsdjzsr>").append(vo.getLsdjzsr()).append("</lsdjzsr>");
			sb.append("<lsdxj1>").append(vo.getLsdzcsr().add(vo.getLsdjzsr()).doubleValue()).append("</lsdxj1>");
			sb.append("<lsdck>").append(vo.getLsdck()).append("</lsdck>");
			sb.append("<lsdjsk>").append(vo.getLsdjsk()).append("</lsdjsk>");
			sb.append("<lsdxj2>").append(vo.getLsdck().add(vo.getLsdjsk()).doubleValue()).append("</lsdxj2>");
			sb.append("<lsdhj>").append(vo.getLsdck().add(vo.getLsdjsk()).add(vo.getLsdzcsr()).add(vo.getLsdjzsr()).doubleValue()).append("</lsdhj>");
			sb.append("<lsdetc>").append(vo.getLsdetccq()).append("</lsdetc>");
			sb.append("<lsdzj>").append(vo.getLsdck().add(vo.getLsdjsk()).add(vo.getLsdzcsr()).add(vo.getLsdjzsr()).add(vo.getLsdetccq()).doubleValue()).append("</lsdzj>");
			
			sb.append("<sldzcsr>").append(vo.getSldzcsr()).append("</sldzcsr>");
			sb.append("<sldjzsr>").append(vo.getSldjzsr()).append("</sldjzsr>");
			sb.append("<sldxj1>").append(vo.getSldzcsr().add(vo.getSldjzsr()).doubleValue()).append("</sldxj1>");
			sb.append("<sldck>").append(vo.getSldck()).append("</sldck>");
			sb.append("<sldjsk>").append(vo.getSldjsk()).append("</sldjsk>");
			sb.append("<sldxj2>").append(vo.getSldck().add(vo.getSldjsk()).doubleValue()).append("</sldxj2>");
			sb.append("<sldhj>").append(vo.getSldck().add(vo.getSldjsk()).add(vo.getSldzcsr()).add(vo.getSldjzsr()).doubleValue()).append("</sldhj>");
			sb.append("<sldetc>").append(vo.getSldetccq()).append("</sldetc>");
			sb.append("<sldzj>").append(vo.getSldck().add(vo.getSldjsk()).add(vo.getSldzcsr()).add(vo.getSldjzsr()).add(vo.getSldetccq()).doubleValue()).append("</sldzj>");
			
			sb.append("<lndzcsr>").append(vo.getLndzcsr()).append("</lndzcsr>");
			sb.append("<lndjzsr>").append(vo.getLndjzsr()).append("</lndjzsr>");
			sb.append("<lndxj1>").append(vo.getLndzcsr().add(vo.getLndjzsr()).doubleValue()).append("</lndxj1>");
			sb.append("<lndck>").append(vo.getLndck()).append("</lndck>");
			sb.append("<lndjsk>").append(vo.getLndjsk()).append("</lndjsk>");
			sb.append("<lndxj2>").append(vo.getLndck().add(vo.getLndjsk()).doubleValue()).append("</lndxj2>");
			sb.append("<lndhj>").append(vo.getLndck().add(vo.getLndjsk()).add(vo.getLndzcsr()).add(vo.getLndjzsr()).doubleValue()).append("</lndhj>");
			sb.append("<lndetc>").append(vo.getLndetccq()).append("</lndetc>");
			sb.append("<lndzj>").append(vo.getLndck().add(vo.getLndjsk()).add(vo.getLndzcsr()).add(vo.getLndjzsr()).add(vo.getLndetccq()).doubleValue()).append("</lndzj>");
			
			sb.append("<nldzcsr>").append(vo.getNldzcsr()).append("</nldzcsr>");
			sb.append("<nldjzsr>").append(vo.getNldjzsr()).append("</nldjzsr>");
			sb.append("<nldxj1>").append(vo.getNldzcsr().add(vo.getNldjzsr()).doubleValue()).append("</nldxj1>");
			sb.append("<nldck>").append(vo.getNldck()).append("</nldck>");
			sb.append("<nldjsk>").append(vo.getNldjsk()).append("</nldjsk>");
			sb.append("<nldxj2>").append(vo.getNldck().add(vo.getNldjsk()).doubleValue()).append("</nldxj2>");
			sb.append("<nldhj>").append(vo.getNldck().add(vo.getNldjsk()).add(vo.getNldzcsr()).add(vo.getNldjzsr()).doubleValue()).append("</nldhj>");
			sb.append("<nldetc>").append(vo.getNldetccq()).append("</nldetc>");
			sb.append("<nldzj>").append(vo.getNldck().add(vo.getNldjsk()).add(vo.getNldzcsr()).add(vo.getNldjzsr()).add(vo.getNldetccq()).doubleValue()).append("</nldzj>");
			
			sb.append("<glczcsr>").append(vo.getGlczcsr()).append("</glczcsr>");
			sb.append("<glcjzsr>").append(vo.getGlcjzsr()).append("</glcjzsr>");
			sb.append("<glcxj1>").append(vo.getGlczcsr().add(vo.getGlcjzsr()).doubleValue()).append("</glcxj1>");
			sb.append("<glcck>").append(vo.getGlcck()).append("</glcck>");
			sb.append("<glcjsk>").append(vo.getGlcjsk()).append("</glcjsk>");
			sb.append("<glcxj2>").append(vo.getGlcck().add(vo.getGlcjsk()).doubleValue()).append("</glcxj2>");
			sb.append("<glchj>").append(vo.getGlcck().add(vo.getGlcjsk()).add(vo.getGlczcsr()).add(vo.getGlcjzsr()).doubleValue()).append("</glchj>");
			sb.append("<glcetc>").append(vo.getGlcetccq()).append("</glcetc>");
			sb.append("<glczj>").append(vo.getGlcck().add(vo.getGlcjsk()).add(vo.getGlczcsr()).add(vo.getGlcjzsr()).add(vo.getGlcetccq()).doubleValue()).append("</glczj>");
			sb.append("</row>");
		}
		sb.append("</xml><_grparam>");
		for(txfchsrhzbVO vo:listzkt){
			sb.append("<lsdzcsr>").append(vo.getLsdzcsr()).append("</lsdzcsr>");
			sb.append("<lsdjzsr>").append(vo.getLsdjzsr()).append("</lsdjzsr>");
			sb.append("<lsdxj1>").append(vo.getLsdzcsr().add(vo.getLsdjzsr()).doubleValue()).append("</lsdxj1>");
			sb.append("<lsdck>").append(vo.getLsdck()).append("</lsdck>");
			sb.append("<lsdjsk>").append(vo.getLsdjsk()).append("</lsdjsk>");
			sb.append("<lsdxj2>").append(vo.getLsdck().add(vo.getLsdjsk()).doubleValue()).append("</lsdxj2>");
			sb.append("<lsdhj>").append(vo.getLsdck().add(vo.getLsdjsk()).add(vo.getLsdzcsr()).add(vo.getLsdjzsr()).doubleValue()).append("</lsdhj>");
			sb.append("<lsdetc>").append(vo.getLsdetccq()).append("</lsdetc>");
			sb.append("<lsdzj>").append(vo.getLsdck().add(vo.getLsdjsk()).add(vo.getLsdzcsr()).add(vo.getLsdjzsr()).add(vo.getLsdetccq()).doubleValue()).append("</lsdzj>");
			
			sb.append("<sldzcsr>").append(vo.getSldzcsr()).append("</sldzcsr>");
			sb.append("<sldjzsr>").append(vo.getSldjzsr()).append("</sldjzsr>");
			sb.append("<sldxj1>").append(vo.getSldzcsr().add(vo.getSldjzsr()).doubleValue()).append("</sldxj1>");
			sb.append("<sldck>").append(vo.getSldck()).append("</sldck>");
			sb.append("<sldjsk>").append(vo.getSldjsk()).append("</sldjsk>");
			sb.append("<sldxj2>").append(vo.getSldck().add(vo.getSldjsk()).doubleValue()).append("</sldxj2>");
			sb.append("<sldhj>").append(vo.getSldck().add(vo.getSldjsk()).add(vo.getSldzcsr()).add(vo.getSldjzsr()).doubleValue()).append("</sldhj>");
			sb.append("<sldetc>").append(vo.getSldetccq()).append("</sldetc>");
			sb.append("<sldzj>").append(vo.getSldck().add(vo.getSldjsk()).add(vo.getSldzcsr()).add(vo.getSldjzsr()).add(vo.getSldetccq()).doubleValue()).append("</sldzj>");
			
			sb.append("<lndzcsr>").append(vo.getLndzcsr()).append("</lndzcsr>");
			sb.append("<lndjzsr>").append(vo.getLndjzsr()).append("</lndjzsr>");
			sb.append("<lndxj1>").append(vo.getLndzcsr().add(vo.getLndjzsr()).doubleValue()).append("</lndxj1>");
			sb.append("<lndck>").append(vo.getLndck()).append("</lndck>");
			sb.append("<lndjsk>").append(vo.getLndjsk()).append("</lndjsk>");
			sb.append("<lndxj2>").append(vo.getLndck().add(vo.getLndjsk()).doubleValue()).append("</lndxj2>");
			sb.append("<lndhj>").append(vo.getLndck().add(vo.getLndjsk()).add(vo.getLndzcsr()).add(vo.getLndjzsr()).doubleValue()).append("</lndhj>");
			sb.append("<lndetc>").append(vo.getLndetccq()).append("</lndetc>");
			sb.append("<lndzj>").append(vo.getLndck().add(vo.getLndjsk()).add(vo.getLndzcsr()).add(vo.getLndjzsr()).add(vo.getLndetccq()).doubleValue()).append("</lndzj>");
			
			sb.append("<nldzcsr>").append(vo.getNldzcsr()).append("</nldzcsr>");
			sb.append("<nldjzsr>").append(vo.getNldjzsr()).append("</nldjzsr>");
			sb.append("<nldxj1>").append(vo.getNldzcsr().add(vo.getNldjzsr()).doubleValue()).append("</nldxj1>");
			sb.append("<nldck>").append(vo.getNldck()).append("</nldck>");
			sb.append("<nldjsk>").append(vo.getNldjsk()).append("</nldjsk>");
			sb.append("<nldxj2>").append(vo.getNldck().add(vo.getNldjsk()).doubleValue()).append("</nldxj2>");
			sb.append("<nldhj>").append(vo.getNldck().add(vo.getNldjsk()).add(vo.getNldzcsr()).add(vo.getNldjzsr()).doubleValue()).append("</nldhj>");
			sb.append("<nldetc>").append(vo.getNldetccq()).append("</nldetc>");
			sb.append("<nldzj>").append(vo.getNldck().add(vo.getNldjsk()).add(vo.getNldzcsr()).add(vo.getNldjzsr()).add(vo.getNldetccq()).doubleValue()).append("</nldzj>");
			
			sb.append("<glczcsr>").append(vo.getGlczcsr()).append("</glczcsr>");
			sb.append("<glcjzsr>").append(vo.getGlcjzsr()).append("</glcjzsr>");
			sb.append("<glcxj1>").append(vo.getGlczcsr().add(vo.getGlcjzsr()).doubleValue()).append("</glcxj1>");
			sb.append("<glcck>").append(vo.getGlcck()).append("</glcck>");
			sb.append("<glcjsk>").append(vo.getGlcjsk()).append("</glcjsk>");
			sb.append("<glcxj2>").append(vo.getGlcck().add(vo.getGlcjsk()).doubleValue()).append("</glcxj2>");
			sb.append("<glchj>").append(vo.getGlcck().add(vo.getGlcjsk()).add(vo.getGlczcsr()).add(vo.getGlcjzsr()).doubleValue()).append("</glchj>");
			sb.append("<glcetc>").append(vo.getGlcetccq()).append("</glcetc>");
			sb.append("<glczj>").append(vo.getGlcck().add(vo.getGlcjsk()).add(vo.getGlczcsr()).add(vo.getGlcjzsr()).add(vo.getGlcetccq()).doubleValue()).append("</glczj>");
			;
		}
		sb.append("</_grparam></report>");
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	
	
	/**
	 * 通行费及车流量明细汇总表
	 * 页面跳转
	 * @param model
	 * @return
	 */
	@RequestMapping("03txfjcllmxhzbIndex")
	public String txfjcllmxhzbIndex(Model model){
		//上月
		DateTime dt = new DateTime();
		Integer nd = dt.getYear();
		String st = dt.plusMonths(-1).toString("yyyy年MM月");
		model.addAttribute("st", st);
		model.addAttribute("nd", nd+"年");
		return "report/03txfjcllmxhzbIndex";
	}
	/**
	 * 通行费及车流量明细汇总表
	 * 明细数据
	 * @param model
	 * @return
	 */
	
	@RequestMapping("0301txfjcllmxbData")
	public void txfjcllmxbData(HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		String ssr = st.replace("年", "-").replace("月", ""); 
		DateTime dt = new DateTime(ssr);
		int mean = dt.dayOfMonth().getMaximumValue();
		Integer year = dt.getYear();
		Integer pyear = year-1;
		String month = st.substring(5,8);
		String yearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(-1);
		String preYearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(1).plusMonths(-1);
		String preMonth = dt.toString("yyyyMM");
		String Yearst = year+"01";
		String preyear = pyear+"01";
		//
		String[] StartTime = {yearMonth+"01",yearMonth+"11",yearMonth+"21"};
		String[] EndTime = {yearMonth+"10",yearMonth+"20",yearMonth+mean};
		
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(Yearst)){
			map.put("year",Yearst);
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
		if(StringUtils.isNoneBlank(preyear)){
			map.put("preyear",preyear);
		}
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){
			map.put("deptid",user.getDeptId());
		}
		Map<String,String> zdMap= Maps.newHashMap();
		zdMap.put("洛龙站", "llz");
		zdMap.put("伊川西站", "ycxz");
		zdMap.put("嵩县产业集聚区站", "sxcyjjqz");
		zdMap.put("嵩县站", "sxz");
		zdMap.put("旧县站", "jxz");
		zdMap.put("九龙山站", "jlsz");
		zdMap.put("重渡沟站", "cdgz");
		zdMap.put("栾川站", "lcz");
		zdMap.put("周山站", "zsz");
		zdMap.put("宜阳站", "yyz");
		zdMap.put("韩城站", "ycz");
		zdMap.put("洛宁站", "lnz");
		zdMap.put("洛宁西站", "lnxz");
		zdMap.put("长水站", "csz");
		zdMap.put("上戈站", "sgz");
	
	indexService.setOrgDeptParam(orgId,deptId,map);
	/*
	 * 金额
	 */
	List<txfjcllmxhzDataVO> listby = lyReportService.getTxfchsrmxbyData(map);
	List<txfjcllmxhzDataVO> listbn = lyReportService.getTxfchsrmxbnData(map);
	List<txfjcllmxhzDataVO> listzkt = lyReportService.getTxfchsrmxzktData(map);
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
	/*
	 * 上中下旬金额
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getTxfchsrmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
				}
				detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
				
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月金额
	 */
	for(txfjcllmxhzDataVO vo : listby){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
			
			
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年金额
	 */
	for(txfjcllmxhzDataVO vo : listbn){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
			
			
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通金额
	 */
	for(txfjcllmxhzDataVO vo : listzkt){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
			
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	/**
	 * 入口流量
	 */
	List<txfjcllmxhzDataVO> listbyrkll = lyReportService.getCllmxbyData(map);
	List<txfjcllmxhzDataVO> listbnrkll = lyReportService.getCllmxbnData(map);
	List<txfjcllmxhzDataVO> listzktrkll = lyReportService.getCllmxzktData(map);
	
	/*
	 * 上中下旬流量
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getCllmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
				}
				detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
				detail.append("<xm2>").append("入口车流量").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月流量
	 */
	for(txfjcllmxhzDataVO vo : listbyrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("入口车流量").append("</xm2>");
			
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年流量
	 */
	for(txfjcllmxhzDataVO vo : listbnrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("入口车流量").append("</xm2>");
			
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通流量
	 */
	for(txfjcllmxhzDataVO vo : listzktrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("入口车流量").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	
	/**
	 * 出口流量
	 */
	List<txfjcllmxhzDataVO> listbyckll = lyReportService.getCllckmxbyData(map);
	List<txfjcllmxhzDataVO> listbnckll = lyReportService.getCllckmxbnData(map);
	List<txfjcllmxhzDataVO> listzktckll = lyReportService.getCllckmxzktData(map);
	
	/*
	 * 上中下旬流量
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getCllckmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
				}
				detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
				detail.append("<xm2>").append("出口车流量").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月流量
	 */
	for(txfjcllmxhzDataVO vo : listbyckll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出口车流量").append("</xm2>");
			detail.append("<fz>").append("2").append("</fz>");
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年流量
	 */
	for(txfjcllmxhzDataVO vo : listbnckll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出口车流量").append("</xm2>");
			detail.append("<fz>").append("1").append("</fz>");
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通流量
	 */
	for(txfjcllmxhzDataVO vo : listzktckll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出口车流量").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/**
	 * 出入口流量
	 */
	List<txfjcllmxhzDataVO> listbycrkll = lyReportService.getCllcrkmxbyData(map);
	List<txfjcllmxhzDataVO> listbncrkll = lyReportService.getCllcrkmxbnData(map);
	List<txfjcllmxhzDataVO> listzktcrkll = lyReportService.getCllcrkmxzktData(map);
	
	/*
	 * 上中下旬流量
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getCllcrkmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
				}
				detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
				detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月流量
	 */
	for(txfjcllmxhzDataVO vo : listbycrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
			
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年流量
	 */
	for(txfjcllmxhzDataVO vo : listbncrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
			
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通流量
	 */
	for(txfjcllmxhzDataVO vo : listzktcrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	sb.append(detail).append("</xml><_grparam>");
	sb.append("<rq>").append(st).append("</rq></_grparam></report>");
	
	ResponseUtils.renderXml(rsp, sb.toString());
	
	}
	/**
	 * 通行费及车流量明细汇总表
	 * 汇总数据
	 * @param model
	 * @return
	 */
	
	@RequestMapping("0302txfjcllmxbData")
	public void txfjcllhzbData(HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		String year = st.replace("年", ""); 
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(year)){
			map.put("year",year);
		}
		Map<String,String> zdMap= Maps.newHashMap();
		zdMap.put("洛龙站", "llz");
		zdMap.put("伊川西站", "ycxz");
		zdMap.put("嵩县产业集聚区站", "sxcyjjqz");
		zdMap.put("嵩县站", "sxz");
		zdMap.put("旧县站", "jxz");
		zdMap.put("九龙山站", "jlsz");
		zdMap.put("重渡沟站", "cdgz");
		zdMap.put("栾川站", "lcz");
		zdMap.put("周山站", "zsz");
		zdMap.put("宜阳站", "yyz");
		zdMap.put("韩城站", "ycz");
		zdMap.put("洛宁站", "lnz");
		zdMap.put("洛宁西站", "lnxz");
		zdMap.put("长水站", "csz");
		zdMap.put("上戈站", "sgz");
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){
			map.put("deptid",user.getDeptId());
		}
	indexService.setOrgDeptParam(orgId,deptId,map);
	
	List<txfjcllmxhzDataVO> listll = lyReportService.getCllhzData(map);
	List<txfjcllmxhzDataVO> listje = lyReportService.getTxfhzData(map);
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
		
	/*
	 * 金额
	 */
	for(txfjcllmxhzDataVO vo : listje){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><bt>").append("实际通行费（元）").append("</bt>");
			detail.append("<bt1>").append("各站通行费小计").append("</bt1>");
			detail.append("<bt2>").append("通行费合计").append("</bt2>");
			detail.append("<sj>").append(vo.getYf()+"月").append("</sj>");
			detail.append("<fz>").append("1").append("</fz>");
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 车流量汇总
	 */
	for(txfjcllmxhzDataVO vo : listll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><bt>").append("车流量（辆）").append("</bt>");
			detail.append("<bt1>").append("各站车流量小计(出口+进口)").append("</bt1>");
			detail.append("<bt2>").append("通行费合计").append("</bt2>");
			detail.append("<sj>").append(vo.getYf()+"月").append("</sj>");
			detail.append("<fz>").append("2").append("</fz>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	sb.append(detail).append("</xml><_grparam>");
	sb.append("<rq>").append(st).append("</rq></_grparam></report>");
	
	ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	
	
	
	
	@RequestMapping("04txfcllhzbIndex")
	public String crkllb(Model model){
		//上月
				DateTime dt = new DateTime();
				String st = dt.plusMonths(-1).toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/04txfcllhzbIndex";
	}
	
	/**
	 * 通行费汇总表数据
	 * 
	 * @param model
	 * @return
	 */
	
	@RequestMapping("0401txfhzbData")
	public void txfhzbData(HttpServletResponse rsp,
		
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		
		Map<String,String> zdMap= Maps.newHashMap();
		zdMap.put("洛龙站", "llz");
		zdMap.put("伊川西站", "ycxz");
		zdMap.put("嵩县产业集聚区站", "sxcyjjqz");
		zdMap.put("嵩县站", "sxz");
		zdMap.put("旧县站", "jxz");
		zdMap.put("九龙山站", "jlsz");
		zdMap.put("重渡沟站", "cdgz");
		zdMap.put("栾川站", "lcz");
		zdMap.put("周山站", "zsz");
		zdMap.put("宜阳站", "yyz");
		zdMap.put("韩城站", "ycz");
		zdMap.put("洛宁站", "lnz");
		zdMap.put("洛宁西站", "lnxz");
		zdMap.put("长水站", "csz");
		zdMap.put("上戈站", "sgz");
	
	indexService.setOrgDeptParam(orgId,deptId,map);
	AuthUserVO user = UserUtils.getAuthUser();
	if(StringUtils.isNoneBlank(user.getDeptId())){
		map.put("deptid",user.getDeptId());
	}
	List<txfjcllmxhzDataVO> listll = lyReportService.getTxfhzhjData(map);
	List<txfjcllmxhzDataVO> listllzkt = lyReportService.getTxfhzhjzktData(map);
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
		
	/*
	 * 车流量汇总
	 */
	for(txfjcllmxhzDataVO vo : listll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><bt>").append("车流量（辆）").append("</bt>");
			detail.append("<nd>").append(vo.getNd()+"年").append("</nd>");
			detail.append("<yf>").append(vo.getYf()+"月").append("</yf>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	sb.append(detail).append("</xml><_grparam>");
	/*
	 * 车流量汇总自开通
	 */
	for(txfjcllmxhzDataVO vo : listllzkt){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}

			sb.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			sb.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			sb.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			sb.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			sb.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			sb.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			sb.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			sb.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			sb.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			sb.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			sb.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			sb.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			sb.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			sb.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			sb.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			sb.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			sb.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			sb.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			sb.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			sb.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj>");
		}
	
	}
	sb.append("<rq>").append("").append("</rq></_grparam></report>");
	
	ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	/**
	 * 通行费汇总表数据
	 * 
	 * @param model
	 * @return
	 */
	
	@RequestMapping("0402cllhzbData")
	public void cllhzbData(HttpServletResponse rsp,
		
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		
		Map<String,String> zdMap= Maps.newHashMap();
		zdMap.put("洛龙站", "llz");
		zdMap.put("伊川西站", "ycxz");
		zdMap.put("嵩县产业集聚区站", "sxcyjjqz");
		zdMap.put("嵩县站", "sxz");
		zdMap.put("旧县站", "jxz");
		zdMap.put("九龙山站", "jlsz");
		zdMap.put("重渡沟站", "cdgz");
		zdMap.put("栾川站", "lcz");
		zdMap.put("周山站", "zsz");
		zdMap.put("宜阳站", "yyz");
		zdMap.put("韩城站", "ycz");
		zdMap.put("洛宁站", "lnz");
		zdMap.put("洛宁西站", "lnxz");
		zdMap.put("长水站", "csz");
		zdMap.put("上戈站", "sgz");
	
	indexService.setOrgDeptParam(orgId,deptId,map);
	AuthUserVO user = UserUtils.getAuthUser();
	if(StringUtils.isNoneBlank(user.getDeptId())){
		map.put("deptid",user.getDeptId());
	}
	List<txfjcllmxhzDataVO> listll = lyReportService.getCllhzhjData(map);
	List<txfjcllmxhzDataVO> listllzkt = lyReportService.getCllhzhjzktData(map);
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
	
	/*
	 * 车流量汇总
	 */
	for(txfjcllmxhzDataVO vo : listll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><bt>").append("车流量（辆）").append("</bt>");
			detail.append("<nd>").append(vo.getNd()+"年").append("</nd>");
			detail.append("<yf>").append(vo.getYf()+"月").append("</yf>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	sb.append(detail).append("</xml><_grparam>");
	/*
	 * 车流量汇总自开通
	 * 
	 */
	for(txfjcllmxhzDataVO vo : listllzkt){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			
			sb.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			sb.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			sb.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			sb.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			sb.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			sb.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			sb.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			sb.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			sb.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			sb.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			sb.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			sb.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			sb.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			sb.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			sb.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			
			sb.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			sb.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			sb.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			sb.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			sb.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj>");
		}
	
	}
	sb.append("<rq>").append("").append("</rq></_grparam></report>");
	
	ResponseUtils.renderXml(rsp, sb.toString());
	}
	/**
	 * 通行费收入及票据月报
	 * @param model
	 * @return
	 */
	@RequestMapping("05txfsrjpjybIndex")
	public String txfsrjpjyb(Model model){
		//上月
				DateTime dt = new DateTime();
				String st = dt.plusMonths(-1).toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/05txfsrjpjybIndex";
	}
	
	/**
	 * 通行费票据月报表数据
	 * @param st
	 * @param et
	 * @param orgId
	 * @param deptId
	 */
	@RequestMapping("05txfsrjpjybData")
	public void txfsrjpjybData(FxPjgl fm,HttpServletResponse rsp,FxRcsj rc,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		
		//
		
		
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
		Map<String,String> sbMap=Maps.newHashMap();
		sbMap.put("bn", "bn");//本年
		sbMap.put(yearMonth, "by");//本月
		sbMap.put("zkt", "zkt");//自开通
		//数据拆分	
		indexService.setOrgDeptParam(orgId,deptId,map);
		List<FxPjgl> list = lyReportService.getPjglData(map);
		List<TxfsrpjVO> listpj = lyReportService.getTxfsrpjData(map);
		List<TxfsrpjVO> listch = lyReportService.getTxfsrpjchData(map);
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><_grparam>");
		//数据拆分
				for(TxfsrpjVO vo:listpj){
					if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
//						System.err.println(entry.getKey()+"-----"+entry.getValue());
						String yf = sbMap.get(entry.getKey());
						//流量 数据 kc/hc/mzc
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, yf+"sr", new BigDecimal(ll.get(0)));
						Reflections.setFieldValue(vo, yf+"ck", new BigDecimal(ll.get(1)));
						Reflections.setFieldValue(vo, yf+"jzsr", new BigDecimal(ll.get(2)));
					}
					BigDecimal bysr = vo.getBysr()==null?BigDecimal.ZERO:vo.getBysr();
						sb.append("<bysr>").append(bysr).append("</bysr>");
						BigDecimal bnsr = vo.getBnsr()==null?BigDecimal.ZERO:vo.getBnsr();
						sb.append("<bnsr>").append(bnsr).append("</bnsr>");
						BigDecimal zktsr = vo.getZktsr()==null?BigDecimal.ZERO:vo.getZktsr();
						sb.append("<zktsr>").append(zktsr).append("</zktsr>");
						BigDecimal ck = vo.getByck()==null?BigDecimal.ZERO:vo.getByck();
						sb.append("<ck>").append(ck).append("</ck>");
						BigDecimal byjzjssr = vo.getByjzsr()==null?BigDecimal.ZERO:vo.getByjzsr();
						sb.append("<byjzjssr>").append(byjzjssr).append("</byjzjssr>");
						BigDecimal bnjzjssr = vo.getBnjzsr()==null?BigDecimal.ZERO:vo.getBnjzsr();
						sb.append("<bnjzjssr>").append(bnjzjssr).append("</bnjzjssr>");
						}
				}
				for(TxfsrpjVO vo:listch){
					if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
//						System.err.println(entry.getKey()+"-----"+entry.getValue());
						String yf = sbMap.get(entry.getKey());
						//流量 数据 kc/hc/mzc
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, yf+"chsr", new BigDecimal(ll.get(0)));
						//
					}
					BigDecimal bycjsr = vo.getBychsr()==null?BigDecimal.ZERO:vo.getBychsr();
					sb.append("<bychsr>").append(bycjsr).append("</bychsr>");
					BigDecimal bnchsr = vo.getBnchsr()==null?BigDecimal.ZERO:vo.getBnchsr();
					sb.append("<bnchsr>").append(bnchsr).append("</bnchsr>");
					BigDecimal zktchsr = vo.getZktchsr()==null?BigDecimal.ZERO:vo.getZktchsr();
					sb.append("<zktchsr>").append(zktchsr).append("</zktchsr>");
				}		
				}
		//报表数据封装
				if(list.size()>0){
		for(FxPjgl vo:list){
			Integer syjza = vo.getSyjza()==null?0:vo.getSyjza();
			sb.append("<syjza>").append(syjza).append("</syjza>");
			Integer syjzb = vo.getSyjzb()==null?0:vo.getSyjzb();
			sb.append("<syjzb>").append(syjzb).append("</syjzb>");
			Integer syjzc = vo.getSyjzc()==null?0:vo.getSyjzc();
			sb.append("<syjzc>").append(syjzc).append("</syjzc>");
			Integer syjzd = vo.getSyjzd()==null?0:vo.getSyjzd();
			sb.append("<syjzd>").append(syjzd).append("</syjzd>");
			Integer syjze = vo.getSyjze()==null?0:vo.getSyjze();
			sb.append("<syjze>").append(syjze).append("</syjze>"); 
			Integer syjzf = vo.getSyjzf()==null?0:vo.getSyjzf();
			sb.append("<syjzf>").append(syjzf).append("</syjzf>");
			Integer syjzg = vo.getSyjzg()==null?0:vo.getSyjzg();
			sb.append("<syjzg>").append(syjzg).append("</syjzg>");
			Integer syjzh = vo.getSyjzh()==null?0:vo.getSyjzh();
			sb.append("<syjzh>").append(syjzh).append("</syjzh>");
			Integer syjzhj = vo.getSyjzhj()==null?0:vo.getSyjzhj();
			sb.append("<syjzhj>").append(syjzhj).append("</syjzhj>");
			Integer syjzi = vo.getSyjzi()==null?0:vo.getSyjzi();
			sb.append("<syjzi>").append(syjzi).append("</syjzi>");
			
			Integer bylra = vo.getBylra()==null?0:vo.getBylra();
			sb.append("<bylra>").append(bylra).append("</bylra>");
			Integer bylrb = vo.getBylrb()==null?0:vo.getBylrb();
			sb.append("<bylrb>").append(bylrb).append("</bylrb>");
			Integer bylrc = vo.getBylrc()==null?0:vo.getBylrc();
			sb.append("<bylrc>").append(bylrc).append("</bylrc>");
			Integer bylrd = vo.getBylrd()==null?0:vo.getBylrd();
			sb.append("<bylrd>").append(bylrd).append("</bylrd>");
			Integer bylre = vo.getBylre()==null?0:vo.getBylre();
			sb.append("<bylre>").append(bylre).append("</bylre>"); 
			Integer bylrf = vo.getBylrf()==null?0:vo.getBylrf();
			sb.append("<bylrf>").append(bylrf).append("</bylrf>");
			Integer bylrg = vo.getBylrg()==null?0:vo.getBylrg();
			sb.append("<bylrg>").append(bylrg).append("</bylrg>");
			Integer bylrh = vo.getBylrh()==null?0:vo.getBylrh();
			sb.append("<bylrh>").append(bylrh).append("</bylrh>");
			Integer bylrhj = vo.getBylrhj()==null?0:vo.getBylrhj();
			sb.append("<bylrhj>").append(bylrhj).append("</bylrhj>");
			Integer bylri = vo.getBylri()==null?0:vo.getBylri();
			sb.append("<bylri>").append(bylri).append("</bylri>");
			
			
			
			Integer byhya = vo.getByhya()==null?0:vo.getByhya();
			sb.append("<byhya>").append(byhya).append("</byhya>");
			Integer byhyb = vo.getByhyb()==null?0:vo.getByhyb();
			sb.append("<byhyb>").append(byhyb).append("</byhyb>");
			Integer byhyc = vo.getByhyc()==null?0:vo.getByhyc();
			sb.append("<byhyc>").append(byhyc).append("</byhyc>");
			Integer byhyd = vo.getByhyd()==null?0:vo.getByhyd();
			sb.append("<byhyd>").append(byhyd).append("</byhyd>");
			Integer byhye = vo.getByhye()==null?0:vo.getByhye();
			sb.append("<byhye>").append(byhye).append("</byhye>"); 
			Integer byhyf = vo.getByhyf()==null?0:vo.getByhyf();
			sb.append("<byhyf>").append(byhyf).append("</byhyf>");
			Integer byhyg = vo.getByhyg()==null?0:vo.getByhyg();
			sb.append("<byhyg>").append(byhyg).append("</byhyg>");
			Integer byhyh = vo.getByhyh()==null?0:vo.getByhyh();
			sb.append("<byhyh>").append(byhyh).append("</byhyh>");
			Integer byhyhj = vo.getByhyhj()==null?0:vo.getByhyhj();
			sb.append("<byhyhj>").append(byhyhj).append("</byhyhj>");
			Integer byhyi = vo.getByhyi()==null?0:vo.getByhyi();
			sb.append("<byhyi>").append(byhyi).append("</byhyi>");
			
			Integer byjca = vo.getByjca()==null?0:vo.getByjca();
			sb.append("<byjca>").append(byjca).append("</byjca>");
			Integer byjcb = vo.getByjcb()==null?0:vo.getByjcb();
			sb.append("<byjcb>").append(byjcb).append("</byjcb>");
			Integer byjcc = vo.getByjcc()==null?0:vo.getByjcc();
			sb.append("<byjcc>").append(byjcc).append("</byjcc>");
			Integer byjcd = vo.getByjcd()==null?0:vo.getByjcd();
			sb.append("<byjcd>").append(byjcd).append("</byjcd>");
			Integer byjce = vo.getByjce()==null?0:vo.getByjce();
			sb.append("<byjce>").append(byjce).append("</byjce>"); 
			Integer byjcf = vo.getByjcf()==null?0:vo.getByjcf();
			sb.append("<byjcf>").append(byjcf).append("</byjcf>");
			Integer byjcg = vo.getByjcg()==null?0:vo.getByjcg();
			sb.append("<byjcg>").append(byjcg).append("</byjcg>");
			Integer byjch = vo.getByjch()==null?0:vo.getByjch();
			sb.append("<byjch>").append(byjch).append("</byjch>");
			Integer byjchj = vo.getByjchj()==null?0:vo.getByjchj();
			sb.append("<byjchj>").append(byjchj).append("</byjchj>");
			Integer byjci = vo.getByjci()==null?0:vo.getByjci();
			sb.append("<byjci>").append(byjci).append("</byjci>");
			
			
			BigDecimal dk = vo.getDk()==null?BigDecimal.ZERO:vo.getDk();
			sb.append("<dk>").append(dk).append("</dk>");
			Integer bbs = vo.getBbs()==null?0:vo.getBbs();
			sb.append("<jsjbbs>").append(bbs).append("</jsjbbs>");
		}
		}else{
			sb.append("<syjza>").append(0).append("</syjza>");
			sb.append("<syjzb>").append(0).append("</syjzb>");
			sb.append("<syjzc>").append(0).append("</syjzc>");
			sb.append("<syjzd>").append(0).append("</syjzd>");
			sb.append("<syjze>").append(0).append("</syjze>"); 
			sb.append("<syjzf>").append(0).append("</syjzf>");
			sb.append("<syjzg>").append(0).append("</syjzg>");
			sb.append("<syjzh>").append(0).append("</syjzh>");
			sb.append("<syjzhj>").append(0).append("</syjzhj>");
			sb.append("<syjzi>").append(0).append("</syjzi>");
			
			sb.append("<bylra>").append(0).append("</bylra>");
			sb.append("<bylrb>").append(0).append("</bylrb>");
			sb.append("<bylrc>").append(0).append("</bylrc>");
			sb.append("<bylrd>").append(0).append("</bylrd>");
			sb.append("<bylre>").append(0).append("</bylre>");
			sb.append("<bylrf>").append(0).append("</bylrf>");
			sb.append("<bylrg>").append(0).append("</bylrg>");
			sb.append("<bylrh>").append(0).append("</bylrh>");
			sb.append("<bylrhj>").append(0).append("</bylrhj>");
			sb.append("<bylri>").append(0).append("</bylri>");
			
			sb.append("<byhya>").append(0).append("</byhya>");
			sb.append("<byhyb>").append(0).append("</byhyb>");
			sb.append("<byhyc>").append(0).append("</byhyc>");
			sb.append("<byhyd>").append(0).append("</byhyd>");
			sb.append("<byhye>").append(0).append("</byhye>");
			sb.append("<byhyf>").append(0).append("</byhyf>");
			sb.append("<byhyg>").append(0).append("</byhyg>");
			sb.append("<byhyh>").append(0).append("</byhyh>");
			sb.append("<byhyhj>").append(0).append("</byhyhj>");
			sb.append("<byhyi>").append(0).append("</byhyi>");
			
			sb.append("<byjca>").append(0).append("</byjca>");
			sb.append("<byjcb>").append(0).append("</byjcb>");
			sb.append("<byjcc>").append(0).append("</byjcc>");
			sb.append("<byjcd>").append(0).append("</byjcd>");
			sb.append("<byjce>").append(0).append("</byjce>");
			sb.append("<byjcf>").append(0).append("</byjcf>");
			sb.append("<byjcg>").append(0).append("</byjcg>");
			sb.append("<byjch>").append(0).append("</byjch>");
			sb.append("<byjchj>").append(0).append("</byjchj>");
			sb.append("<byjci>").append(0).append("</byjci>");
			sb.append("<dk>").append(0).append("</dk>");
			sb.append("<jsjbbs>").append(0).append("</jsjbbs>");
		}
		
		
		
		sb.append("<sfrq>").append(st).append("</sfrq>");
		sb.append("<dw>").append(map.get("title")).append("</dw>");
		sb.append("<rq>"+st+"</rq></_grparam></report>");
		ResponseUtils.renderXml(rsp, sb.toString());
	}

	
	
	
	//通行费收入月报首页
		@RequestMapping("06txfsrybIndex")
		public String txfsryb(Model model){
			//上月
					DateTime dt = new DateTime();
					String st = dt.plusMonths(-1).toString("yyyy年MM月");
					model.addAttribute("st", st);
			return "report/06txfsrybIndex";
		}
		
		//通行费收入月报
		@RequestMapping("06txfsrybData")
		public void txfsrybData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
			String ssr = st.replace("年", "-").replace("月", ""); 
			DateTime dt = new DateTime(ssr);
			int mean = dt.dayOfMonth().getMaximumValue();
			Integer year = dt.getYear();
			Integer pyear = year-1;
			String month = st.substring(5,8);
			String yearMonth = dt.toString("yyyyMM");
			dt = dt.plusYears(-1);
			String preYearMonth = dt.toString("yyyyMM");
			dt = dt.plusYears(1).plusMonths(-1);
			String preMonth = dt.toString("yyyyMM");
			String Yearst = year+"01";
			String preyear = pyear+"01";
		
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			if(StringUtils.isNoneBlank(Yearst)){
				map.put("year",Yearst);
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
			if(StringUtils.isNoneBlank(preyear)){
				map.put("preyear",preyear);
			}
			
			Map<String,String> zdMap= Maps.newHashMap();
			zdMap.put("洛龙站", "llz");
			zdMap.put("伊川西站", "ycxz");
			zdMap.put("嵩县产业集聚区站", "sxcyjjqz");
			zdMap.put("嵩县站", "sxz");
			zdMap.put("旧县站", "jxz");
			zdMap.put("九龙山站", "jlsz");
			zdMap.put("重渡沟站", "cdgz");
			zdMap.put("栾川站", "lcz");
			zdMap.put("周山站", "zsz");
			zdMap.put("宜阳站", "yyz");
			zdMap.put("韩城站", "ycz");
			zdMap.put("洛宁站", "lnz");
			zdMap.put("洛宁西站", "lnxz");
			zdMap.put("长水站", "csz");
			zdMap.put("上戈站", "sgz");
		
			List<txfjcllmxhzDataVO> listby = lyReportService.getTxfsrybbyData(map);
			List<txfjcllmxhzDataVO> listbn = lyReportService.getTxfsrybbnData(map);
			List<txfjcllmxhzDataVO> listzkt = lyReportService.getTxfsrybzktData(map);
			StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
			StringBuffer detail = new StringBuffer();
			detail.append("</xml><_grparam>");
			for(txfjcllmxhzDataVO vo : listby){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
					}
					detail.append("<llbyje>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</llbyje>");
					detail.append("<ycxbyje>").append(vo.getYcxz_je()==null?BigDecimal.ZERO:vo.getYcxz_je()).append("</ycxbyje>");
					detail.append("<sxcqbyje>").append(vo.getSxcyjjqz_je()==null?BigDecimal.ZERO:vo.getSxcyjjqz_je()).append("</sxcqbyje>");
					detail.append("<sxbyje>").append(vo.getSxz_je()==null?BigDecimal.ZERO:vo.getSxz_je()).append("</sxbyje>");
					detail.append("<jxbyje>").append(vo.getJxz_je()==null?BigDecimal.ZERO:vo.getJxz_je()).append("</jxbyje>");
					detail.append("<jlsbyje>").append(vo.getJlsz_je()==null?BigDecimal.ZERO:vo.getJlsz_je()).append("</jlsbyje>");
					detail.append("<cdgbyje>").append(vo.getCdgz_je()==null?BigDecimal.ZERO:vo.getCdgz_je()).append("</cdgbyje>");
					detail.append("<lcbyje>").append(vo.getLcz_je()==null?BigDecimal.ZERO:vo.getLcz_je()).append("</lcbyje>");
					detail.append("<zsbyje>").append(vo.getZsz_je()==null?BigDecimal.ZERO:vo.getZsz_je()).append("</zsbyje>");
					detail.append("<yybyje>").append(vo.getYyz_je()==null?BigDecimal.ZERO:vo.getYyz_je()).append("</yybyje>");
					detail.append("<hcbyje>").append(vo.getYcz_je()==null?BigDecimal.ZERO:vo.getYcz_je()).append("</hcbyje>");
					detail.append("<lnbyje>").append(vo.getLnz_je()==null?BigDecimal.ZERO:vo.getLnz_je()).append("</lnbyje>");
					detail.append("<lnxbyje>").append(vo.getLnxz_je()==null?BigDecimal.ZERO:vo.getLnxz_je()).append("</lnxbyje>");
					detail.append("<csbyje>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</csbyje>");
					detail.append("<sgbyje>").append(vo.getSgz_je()==null?BigDecimal.ZERO:vo.getSgz_je()).append("</sgbyje>");
				}else{
					detail.append("<llbyje>").append(BigDecimal.ZERO).append("</llbyje>");
					detail.append("<ycxbyje>").append(BigDecimal.ZERO).append("</ycxbyje>");
					detail.append("<sxcqbyje>").append(BigDecimal.ZERO).append("</sxcqbyje>");
					detail.append("<sxbyje>").append(BigDecimal.ZERO).append("</sxbyje>");
					detail.append("<jxbyje>").append(BigDecimal.ZERO).append("</jxbyje>");
					detail.append("<jlsbyje>").append(BigDecimal.ZERO).append("</jlsbyje>");
					detail.append("<cdgbyje>").append(BigDecimal.ZERO).append("</cdgbyje>");
					detail.append("<lcbyje>").append(BigDecimal.ZERO).append("</lcbyje>");
					detail.append("<zsbyje>").append(BigDecimal.ZERO).append("</zsbyje>");
					detail.append("<yybyje>").append(BigDecimal.ZERO).append("</yybyje>");
					detail.append("<hcbyje>").append(BigDecimal.ZERO).append("</hcbyje>");
					detail.append("<lnbyje>").append(BigDecimal.ZERO).append("</lnbyje>");
					detail.append("<lnxbyje>").append(BigDecimal.ZERO).append("</lnxbyje>");
					detail.append("<csbyje>").append(BigDecimal.ZERO).append("</csbyje>");
					detail.append("<sgbyje>").append(BigDecimal.ZERO).append("</sgbyje>");
				}
			
			}
			for(txfjcllmxhzDataVO vo : listbn){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
					}
					detail.append("<llbnje>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</llbnje>");
					detail.append("<ycxbnje>").append(vo.getYcxz_je()==null?BigDecimal.ZERO:vo.getYcxz_je()).append("</ycxbnje>");
					detail.append("<sxcqbnje>").append(vo.getSxcyjjqz_je()==null?BigDecimal.ZERO:vo.getSxcyjjqz_je()).append("</sxcqbnje>");
					detail.append("<sxbnje>").append(vo.getSxz_je()==null?BigDecimal.ZERO:vo.getSxz_je()).append("</sxbnje>");
					detail.append("<jxbnje>").append(vo.getJxz_je()==null?BigDecimal.ZERO:vo.getJxz_je()).append("</jxbnje>");
					detail.append("<jlsbnje>").append(vo.getJlsz_je()==null?BigDecimal.ZERO:vo.getJlsz_je()).append("</jlsbnje>");
					detail.append("<cdgbnje>").append(vo.getCdgz_je()==null?BigDecimal.ZERO:vo.getCdgz_je()).append("</cdgbnje>");
					detail.append("<lcbnje>").append(vo.getLcz_je()==null?BigDecimal.ZERO:vo.getLcz_je()).append("</lcbnje>");
					detail.append("<zsbnje>").append(vo.getZsz_je()==null?BigDecimal.ZERO:vo.getZsz_je()).append("</zsbnje>");
					detail.append("<yybnje>").append(vo.getYyz_je()==null?BigDecimal.ZERO:vo.getYyz_je()).append("</yybnje>");
					detail.append("<hcbnje>").append(vo.getYcz_je()==null?BigDecimal.ZERO:vo.getYcz_je()).append("</hcbnje>");
					detail.append("<lnbnje>").append(vo.getLnz_je()==null?BigDecimal.ZERO:vo.getLnz_je()).append("</lnbnje>");
					detail.append("<lnxbnje>").append(vo.getLnxz_je()==null?BigDecimal.ZERO:vo.getLnxz_je()).append("</lnxbnje>");
					detail.append("<csbnje>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</csbnje>");
					detail.append("<sgbnje>").append(vo.getSgz_je()==null?BigDecimal.ZERO:vo.getSgz_je()).append("</sgbnje>");
				}else{
					detail.append("<llbnje>").append(BigDecimal.ZERO).append("</llbnje>");
					detail.append("<ycxbnje>").append(BigDecimal.ZERO).append("</ycxbnje>");
					detail.append("<sxcqbnje>").append(BigDecimal.ZERO).append("</sxcqbnje>");
					detail.append("<sxbnje>").append(BigDecimal.ZERO).append("</sxbnje>");
					detail.append("<jxbnje>").append(BigDecimal.ZERO).append("</jxbnje>");
					detail.append("<jlsbnje>").append(BigDecimal.ZERO).append("</jlsbnje>");
					detail.append("<cdgbnje>").append(BigDecimal.ZERO).append("</cdgbnje>");
					detail.append("<lcbnje>").append(BigDecimal.ZERO).append("</lcbnje>");
					detail.append("<zsbnje>").append(BigDecimal.ZERO).append("</zsbnje>");
					detail.append("<yybnje>").append(BigDecimal.ZERO).append("</yybnje>");
					detail.append("<hcbnje>").append(BigDecimal.ZERO).append("</hcbnje>");
					detail.append("<lnbnje>").append(BigDecimal.ZERO).append("</lnbnje>");
					detail.append("<lnxbnje>").append(BigDecimal.ZERO).append("</lnxbnje>");
					detail.append("<csbnje>").append(BigDecimal.ZERO).append("</csbnje>");
					detail.append("<sgbnje>").append(BigDecimal.ZERO).append("</sgbnje>");
				}
				
			}
			for(txfjcllmxhzDataVO vo : listzkt){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
					}
					detail.append("<llzktje>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</llzktje>");
					detail.append("<ycxzktje>").append(vo.getYcxz_je()==null?BigDecimal.ZERO:vo.getYcxz_je()).append("</ycxzktje>");
					detail.append("<sxcqzktje>").append(vo.getSxcyjjqz_je()==null?BigDecimal.ZERO:vo.getSxcyjjqz_je()).append("</sxcqzktje>");
					detail.append("<sxzktje>").append(vo.getSxz_je()==null?BigDecimal.ZERO:vo.getSxz_je()).append("</sxzktje>");
					detail.append("<jxzktje>").append(vo.getJxz_je()==null?BigDecimal.ZERO:vo.getJxz_je()).append("</jxzktje>");
					detail.append("<jlszktje>").append(vo.getJlsz_je()==null?BigDecimal.ZERO:vo.getJlsz_je()).append("</jlszktje>");
					detail.append("<cdgzktje>").append(vo.getCdgz_je()==null?BigDecimal.ZERO:vo.getCdgz_je()).append("</cdgzktje>");
					detail.append("<lczktje>").append(vo.getLcz_je()==null?BigDecimal.ZERO:vo.getLcz_je()).append("</lczktje>");
					detail.append("<zszktje>").append(vo.getZsz_je()==null?BigDecimal.ZERO:vo.getZsz_je()).append("</zszktje>");
					detail.append("<yyzktje>").append(vo.getYyz_je()==null?BigDecimal.ZERO:vo.getYyz_je()).append("</yyzktje>");
					detail.append("<hczktje>").append(vo.getYcz_je()==null?BigDecimal.ZERO:vo.getYcz_je()).append("</hczktje>");
					detail.append("<lnzktje>").append(vo.getLnz_je()==null?BigDecimal.ZERO:vo.getLnz_je()).append("</lnzktje>");
					detail.append("<lnxzktje>").append(vo.getLnxz_je()==null?BigDecimal.ZERO:vo.getLnxz_je()).append("</lnxzktje>");
					detail.append("<cszktje>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</cszktje>");
					detail.append("<sgzktje>").append(vo.getSgz_je()==null?BigDecimal.ZERO:vo.getSgz_je()).append("</sgzktje>");
				}else{
					detail.append("<llzktje>").append(BigDecimal.ZERO).append("</llzktje>");
					detail.append("<ycxzktje>").append(BigDecimal.ZERO).append("</ycxzktje>");
					detail.append("<sxcqzktje>").append(BigDecimal.ZERO).append("</sxcqzktje>");
					detail.append("<sxzktje>").append(BigDecimal.ZERO).append("</sxzktje>");
					detail.append("<jxzktje>").append(BigDecimal.ZERO).append("</jxzktje>");
					detail.append("<jlszktje>").append(BigDecimal.ZERO).append("</jlszktje>");
					detail.append("<cdgzktje>").append(BigDecimal.ZERO).append("</cdgzktje>");
					detail.append("<lczktje>").append(BigDecimal.ZERO).append("</lczktje>");
					detail.append("<zszktje>").append(BigDecimal.ZERO).append("</zszktje>");
					detail.append("<yyzktje>").append(BigDecimal.ZERO).append("</yyzktje>");
					detail.append("<hczktje>").append(BigDecimal.ZERO).append("</hczktje>");
					detail.append("<lnzktje>").append(BigDecimal.ZERO).append("</lnzktje>");
					detail.append("<lnxzktje>").append(BigDecimal.ZERO).append("</lnxzktje>");
					detail.append("<cszktje>").append(BigDecimal.ZERO).append("</cszktje>");
					detail.append("<sgzktje>").append(BigDecimal.ZERO).append("</sgzktje>");
				}
			}
			List<txfjcllmxhzDataVO> listsy = lyReportService.getTxfsrybsyData(map);
			List<txfjcllmxhzDataVO> listsntq = lyReportService.getTxfsrsntqData(map);
			for(txfjcllmxhzDataVO vo : listsy){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
					}
					detail.append("<llhbjes>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</llhbjes>");
					detail.append("<ycxhbjes>").append(vo.getYcxz_je()==null?BigDecimal.ZERO:vo.getYcxz_je()).append("</ycxhbjes>");
					detail.append("<sxcqhbjes>").append(vo.getSxcyjjqz_je()==null?BigDecimal.ZERO:vo.getSxcyjjqz_je()).append("</sxcqhbjes>");
					detail.append("<sxhbjes>").append(vo.getSxz_je()==null?BigDecimal.ZERO:vo.getSxz_je()).append("</sxhbjes>");
					detail.append("<jxhbjes>").append(vo.getJxz_je()==null?BigDecimal.ZERO:vo.getJxz_je()).append("</jxhbjes>");
					detail.append("<jlshbjes>").append(vo.getJlsz_je()==null?BigDecimal.ZERO:vo.getJlsz_je()).append("</jlshbjes>");
					detail.append("<cdghbjes>").append(vo.getCdgz_je()==null?BigDecimal.ZERO:vo.getCdgz_je()).append("</cdghbjes>");
					detail.append("<lchbjes>").append(vo.getLcz_je()==null?BigDecimal.ZERO:vo.getLcz_je()).append("</lchbjes>");
					detail.append("<zshbjes>").append(vo.getZsz_je()==null?BigDecimal.ZERO:vo.getZsz_je()).append("</zshbjes>");
					detail.append("<yyhbjes>").append(vo.getYyz_je()==null?BigDecimal.ZERO:vo.getYyz_je()).append("</yyhbjes>");
					detail.append("<hchbjes>").append(vo.getYcz_je()==null?BigDecimal.ZERO:vo.getYcz_je()).append("</hchbjes>");
					detail.append("<lnhbjes>").append(vo.getLnz_je()==null?BigDecimal.ZERO:vo.getLnz_je()).append("</lnhbjes>");
					detail.append("<lnxhbjes>").append(vo.getLnxz_je()==null?BigDecimal.ZERO:vo.getLnxz_je()).append("</lnxhbjes>");
					detail.append("<cshbjes>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</cshbjes>");
					detail.append("<sghbjes>").append(vo.getSgz_je()==null?BigDecimal.ZERO:vo.getSgz_je()).append("</sghbjes>");
				}else{
					detail.append("<llhbjes>").append(BigDecimal.ZERO).append("</llhbjes>");
					detail.append("<ycxhbjes>").append(BigDecimal.ZERO).append("</ycxhbjes>");
					detail.append("<sxcqhbjes>").append(BigDecimal.ZERO).append("</sxcqhbjes>");
					detail.append("<sxhbjes>").append(BigDecimal.ZERO).append("</sxhbjes>");
					detail.append("<jxhbjes>").append(BigDecimal.ZERO).append("</jxhbjes>");
					detail.append("<jlshbjes>").append(BigDecimal.ZERO).append("</jlshbjes>");
					detail.append("<cdghbjes>").append(BigDecimal.ZERO).append("</cdghbjes>");
					detail.append("<lchbjes>").append(BigDecimal.ZERO).append("</lchbjes>");
					detail.append("<zshbjes>").append(BigDecimal.ZERO).append("</zshbjes>");
					detail.append("<yyhbjes>").append(BigDecimal.ZERO).append("</yyhbjes>");
					detail.append("<hchbjes>").append(BigDecimal.ZERO).append("</hchbjes>");
					detail.append("<lnhbjes>").append(BigDecimal.ZERO).append("</lnhbjes>");
					detail.append("<lnxhbjes>").append(BigDecimal.ZERO).append("</lnxhbjes>");
					detail.append("<cshbjes>").append(BigDecimal.ZERO).append("</cshbjes>");
					detail.append("<sghbjes>").append(BigDecimal.ZERO).append("</sghbjes>");
				}
			
			}
			for(txfjcllmxhzDataVO vo : listsntq){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
					}
					detail.append("<lltbjes>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</lltbjes>");
					detail.append("<ycxtbjes>").append(vo.getYcxz_je()==null?BigDecimal.ZERO:vo.getYcxz_je()).append("</ycxtbjes>");
					detail.append("<sxcqtbjes>").append(vo.getSxcyjjqz_je()==null?BigDecimal.ZERO:vo.getSxcyjjqz_je()).append("</sxcqtbjes>");
					detail.append("<sxtbjes>").append(vo.getSxz_je()==null?BigDecimal.ZERO:vo.getSxz_je()).append("</sxtbjes>");
					detail.append("<jxtbjes>").append(vo.getJxz_je()==null?BigDecimal.ZERO:vo.getJxz_je()).append("</jxtbjes>");
					detail.append("<jlstbjes>").append(vo.getJlsz_je()==null?BigDecimal.ZERO:vo.getJlsz_je()).append("</jlstbjes>");
					detail.append("<cdgtbjes>").append(vo.getCdgz_je()==null?BigDecimal.ZERO:vo.getCdgz_je()).append("</cdgtbjes>");
					detail.append("<lctbjes>").append(vo.getLcz_je()==null?BigDecimal.ZERO:vo.getLcz_je()).append("</lctbjes>");
					detail.append("<zstbjes>").append(vo.getZsz_je()==null?BigDecimal.ZERO:vo.getZsz_je()).append("</zstbjes>");
					detail.append("<yytbjes>").append(vo.getYyz_je()==null?BigDecimal.ZERO:vo.getYyz_je()).append("</yytbjes>");
					detail.append("<hctbjes>").append(vo.getYcz_je()==null?BigDecimal.ZERO:vo.getYcz_je()).append("</hctbjes>");
					detail.append("<lntbjes>").append(vo.getLnz_je()==null?BigDecimal.ZERO:vo.getLnz_je()).append("</lntbjes>");
					detail.append("<lnxtbjes>").append(vo.getLnxz_je()==null?BigDecimal.ZERO:vo.getLnxz_je()).append("</lnxtbjes>");
					detail.append("<cstbjes>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</cstbjes>");
					detail.append("<sgtbjes>").append(vo.getSgz_je()==null?BigDecimal.ZERO:vo.getSgz_je()).append("</sgtbjes>");
				}else{
					detail.append("<lltbjes>").append(BigDecimal.ZERO).append("</lltbjes>");
					detail.append("<ycxtbjes>").append(BigDecimal.ZERO).append("</ycxtbjes>");
					detail.append("<sxcqtbjes>").append(BigDecimal.ZERO).append("</sxcqtbjes>");
					detail.append("<sxtbjes>").append(BigDecimal.ZERO).append("</sxtbjes>");
					detail.append("<jxtbjes>").append(BigDecimal.ZERO).append("</jxtbjes>");
					detail.append("<jlstbjes>").append(BigDecimal.ZERO).append("</jlstbjes>");
					detail.append("<cdgtbjes>").append(BigDecimal.ZERO).append("</cdgtbjes>");
					detail.append("<lctbjes>").append(BigDecimal.ZERO).append("</lctbjes>");
					detail.append("<zstbjes>").append(BigDecimal.ZERO).append("</zstbjes>");
					detail.append("<yytbjes>").append(BigDecimal.ZERO).append("</yytbjes>");
					detail.append("<hctbjes>").append(BigDecimal.ZERO).append("</hctbjes>");
					detail.append("<lntbjes>").append(BigDecimal.ZERO).append("</lntbjes>");
					detail.append("<lnxtbjes>").append(BigDecimal.ZERO).append("</lnxtbjes>");
					detail.append("<cstbjes>").append(BigDecimal.ZERO).append("</cstbjes>");
					detail.append("<sgtbjes>").append(BigDecimal.ZERO).append("</sgtbjes>");
				}
				
			}
			List<txfjcllmxhzDataVO> listsn = lyReportService.getTxfsrsnData(map);
			for(txfjcllmxhzDataVO vo : listsn){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
					}
					detail.append("<llbntbjes>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</llbntbjes>");
					detail.append("<ycxbntbjes>").append(vo.getYcxz_je()==null?BigDecimal.ZERO:vo.getYcxz_je()).append("</ycxbntbjes>");
					detail.append("<sxcqbntbjes>").append(vo.getSxcyjjqz_je()==null?BigDecimal.ZERO:vo.getSxcyjjqz_je()).append("</sxcqbntbjes>");
					detail.append("<sxbntbjes>").append(vo.getSxz_je()==null?BigDecimal.ZERO:vo.getSxz_je()).append("</sxbntbjes>");
					detail.append("<jxbntbjes>").append(vo.getJxz_je()==null?BigDecimal.ZERO:vo.getJxz_je()).append("</jxbntbjes>");
					detail.append("<jlsbntbjes>").append(vo.getJlsz_je()==null?BigDecimal.ZERO:vo.getJlsz_je()).append("</jlsbntbjes>");
					detail.append("<cdgbntbjes>").append(vo.getCdgz_je()==null?BigDecimal.ZERO:vo.getCdgz_je()).append("</cdgbntbjes>");
					detail.append("<lcbntbjes>").append(vo.getLcz_je()==null?BigDecimal.ZERO:vo.getLcz_je()).append("</lcbntbjes>");
					detail.append("<zsbntbjes>").append(vo.getZsz_je()==null?BigDecimal.ZERO:vo.getZsz_je()).append("</zsbntbjes>");
					detail.append("<yybntbjes>").append(vo.getYyz_je()==null?BigDecimal.ZERO:vo.getYyz_je()).append("</yybntbjes>");
					detail.append("<hcbntbjes>").append(vo.getYcz_je()==null?BigDecimal.ZERO:vo.getYcz_je()).append("</hcbntbjes>");
					detail.append("<lnbntbjes>").append(vo.getLnz_je()==null?BigDecimal.ZERO:vo.getLnz_je()).append("</lnbntbjes>");
					detail.append("<lnxbntbjes>").append(vo.getLnxz_je()==null?BigDecimal.ZERO:vo.getLnxz_je()).append("</lnxbntbjes>");
					detail.append("<csbntbjes>").append(vo.getLlz_je()==null?BigDecimal.ZERO:vo.getLlz_je()).append("</csbntbjes>");
					detail.append("<sgbntbjes>").append(vo.getSgz_je()==null?BigDecimal.ZERO:vo.getSgz_je()).append("</sgbntbjes>");
				}else{
					detail.append("<llbntbjes>").append(BigDecimal.ZERO).append("</llbntbjes>");
					detail.append("<ycxbntbjes>").append(BigDecimal.ZERO).append("</ycxbntbjes>");
					detail.append("<sxcqbntbjes>").append(BigDecimal.ZERO).append("</sxcqbntbjes>");
					detail.append("<sxbntbjes>").append(BigDecimal.ZERO).append("</sxbntbjes>");
					detail.append("<jxbntbjes>").append(BigDecimal.ZERO).append("</jxbntbjes>");
					detail.append("<jlsbntbjes>").append(BigDecimal.ZERO).append("</jlsbntbjes>");
					detail.append("<cdgbntbjes>").append(BigDecimal.ZERO).append("</cdgbntbjes>");
					detail.append("<lcbntbjes>").append(BigDecimal.ZERO).append("</lcbntbjes>");
					detail.append("<zsbntbjes>").append(BigDecimal.ZERO).append("</zsbntbjes>");
					detail.append("<yybntbjes>").append(BigDecimal.ZERO).append("</yybntbjes>");
					detail.append("<hcbntbjes>").append(BigDecimal.ZERO).append("</hcbntbjes>");
					detail.append("<lnbntbjes>").append(BigDecimal.ZERO).append("</lnbntbjes>");
					detail.append("<lnxbntbjes>").append(BigDecimal.ZERO).append("</lnxbntbjes>");
					detail.append("<csbntbjes>").append(BigDecimal.ZERO).append("</csbntbjes>");
					detail.append("<sgbntbjes>").append(BigDecimal.ZERO).append("</sgbntbjes>");
				}
				
			}
			
			List<txfjcllmxhzDataVO> listbycrkll = lyReportService.getCllcrkmxbyData(map);
			List<txfjcllmxhzDataVO> listbncrkll = lyReportService.getCllcrkmxbnData(map);
			List<txfjcllmxhzDataVO> listzktcrkll = lyReportService.getCllcrkmxzktData(map);
			for(txfjcllmxhzDataVO vo : listbycrkll){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
					}
					detail.append("<llbyll>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</llbyll>");
					detail.append("<ycxbyll>").append(vo.getYcxz_ll()==null?0:vo.getYcxz_ll()).append("</ycxbyll>");
					detail.append("<sxcqbyll>").append(vo.getSxcyjjqz_ll()==null?0:vo.getSxcyjjqz_ll()).append("</sxcqbyll>");
					detail.append("<sxbyll>").append(vo.getSxz_ll()==null?0:vo.getSxz_ll()).append("</sxbyll>");
					detail.append("<jxbyll>").append(vo.getJxz_ll()==null?0:vo.getJxz_ll()).append("</jxbyll>");
					detail.append("<jlsbyll>").append(vo.getJlsz_ll()==null?0:vo.getJlsz_ll()).append("</jlsbyll>");
					detail.append("<cdgbyll>").append(vo.getCdgz_ll()==null?0:vo.getCdgz_ll()).append("</cdgbyll>");
					detail.append("<lcbyll>").append(vo.getLcz_ll()==null?0:vo.getLcz_ll()).append("</lcbyll>");
					detail.append("<zsbyll>").append(vo.getZsz_ll()==null?0:vo.getZsz_ll()).append("</zsbyll>");
					detail.append("<yybyll>").append(vo.getYyz_ll()==null?0:vo.getYyz_ll()).append("</yybyll>");
					detail.append("<hcbyll>").append(vo.getYcz_ll()==null?0:vo.getYcz_ll()).append("</hcbyll>");
					detail.append("<lnbyll>").append(vo.getLnz_ll()==null?0:vo.getLnz_ll()).append("</lnbyll>");
					detail.append("<lnxbyll>").append(vo.getLnxz_ll()==null?0:vo.getLnxz_ll()).append("</lnxbyll>");
					detail.append("<csbyll>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</csbyll>");
					detail.append("<sgbyll>").append(vo.getSgz_ll()==null?0:vo.getSgz_ll()).append("</sgbyll>");
				}else{
					detail.append("<llbyll>").append(0).append("</llbyll>");
					detail.append("<ycxbyll>").append(0).append("</ycxbyll>");
					detail.append("<sxcqbyll>").append(0).append("</sxcqbyll>");
					detail.append("<sxbyll>").append(0).append("</sxbyll>");
					detail.append("<jxbyll>").append(0).append("</jxbyll>");
					detail.append("<jlsbyll>").append(0).append("</jlsbyll>");
					detail.append("<cdgbyll>").append(0).append("</cdgbyll>");
					detail.append("<lcbyll>").append(0).append("</lcbyll>");
					detail.append("<zsbyll>").append(0).append("</zsbyll>");
					detail.append("<yybyll>").append(0).append("</yybyll>");
					detail.append("<hcbyll>").append(0).append("</hcbyll>");
					detail.append("<lnbyll>").append(0).append("</lnbyll>");
					detail.append("<lnxbyll>").append(0).append("</lnxbyll>");
					detail.append("<csbyll>").append(0).append("</csbyll>");
					detail.append("<sgbyll>").append(0).append("</sgbyll>");
				}
			
			}
			
			for(txfjcllmxhzDataVO vo : listbncrkll){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
					}
					detail.append("<llbnll>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</llbnll>");
					detail.append("<ycxbnll>").append(vo.getYcxz_ll()==null?0:vo.getYcxz_ll()).append("</ycxbnll>");
					detail.append("<sxcqbnll>").append(vo.getSxcyjjqz_ll()==null?0:vo.getSxcyjjqz_ll()).append("</sxcqbnll>");
					detail.append("<sxbnll>").append(vo.getSxz_ll()==null?0:vo.getSxz_ll()).append("</sxbnll>");
					detail.append("<jxbnll>").append(vo.getJxz_ll()==null?0:vo.getJxz_ll()).append("</jxbnll>");
					detail.append("<jlsbnll>").append(vo.getJlsz_ll()==null?0:vo.getJlsz_ll()).append("</jlsbnll>");
					detail.append("<cdgbnll>").append(vo.getCdgz_ll()==null?0:vo.getCdgz_ll()).append("</cdgbnll>");
					detail.append("<lcbnll>").append(vo.getLcz_ll()==null?0:vo.getLcz_ll()).append("</lcbnll>");
					detail.append("<zsbnll>").append(vo.getZsz_ll()==null?0:vo.getZsz_ll()).append("</zsbnll>");
					detail.append("<yybnll>").append(vo.getYyz_ll()==null?0:vo.getYyz_ll()).append("</yybnll>");
					detail.append("<hcbnll>").append(vo.getYcz_ll()==null?0:vo.getYcz_ll()).append("</hcbnll>");
					detail.append("<lnbnll>").append(vo.getLnz_ll()==null?0:vo.getLnz_ll()).append("</lnbnll>");
					detail.append("<lnxbnll>").append(vo.getLnxz_ll()==null?0:vo.getLnxz_ll()).append("</lnxbnll>");
					detail.append("<csbnll>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</csbnll>");
					detail.append("<sgbnll>").append(vo.getSgz_ll()==null?0:vo.getSgz_ll()).append("</sgbnll>");
				}else{
					detail.append("<llbnll>").append(0).append("</llbnll>");
					detail.append("<ycxbnll>").append(0).append("</ycxbnll>");
					detail.append("<sxcqbnll>").append(0).append("</sxcqbnll>");
					detail.append("<sxbnll>").append(0).append("</sxbnll>");
					detail.append("<jxbnll>").append(0).append("</jxbnll>");
					detail.append("<jlsbnll>").append(0).append("</jlsbnll>");
					detail.append("<cdgbnll>").append(0).append("</cdgbnll>");
					detail.append("<lcbnll>").append(0).append("</lcbnll>");
					detail.append("<zsbnll>").append(0).append("</zsbnll>");
					detail.append("<yybnll>").append(0).append("</yybnll>");
					detail.append("<hcbnll>").append(0).append("</hcbnll>");
					detail.append("<lnbnll>").append(0).append("</lnbnll>");
					detail.append("<lnxbnll>").append(0).append("</lnxbnll>");
					detail.append("<csbnll>").append(0).append("</csbnll>");
					detail.append("<sgbnll>").append(0).append("</sgbnll>");
				}
				
			}
			
			for(txfjcllmxhzDataVO vo : listzktcrkll){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
					}
					detail.append("<llzktll>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</llzktll>");
					detail.append("<ycxzktll>").append(vo.getYcxz_ll()==null?0:vo.getYcxz_ll()).append("</ycxzktll>");
					detail.append("<sxcqzktll>").append(vo.getSxcyjjqz_ll()==null?0:vo.getSxcyjjqz_ll()).append("</sxcqzktll>");
					detail.append("<sxzktll>").append(vo.getSxz_ll()==null?0:vo.getSxz_ll()).append("</sxzktll>");
					detail.append("<jxzktll>").append(vo.getJxz_ll()==null?0:vo.getJxz_ll()).append("</jxzktll>");
					detail.append("<jlszktll>").append(vo.getJlsz_ll()==null?0:vo.getJlsz_ll()).append("</jlszktll>");
					detail.append("<cdgzktll>").append(vo.getCdgz_ll()==null?0:vo.getCdgz_ll()).append("</cdgzktll>");
					detail.append("<lczktll>").append(vo.getLcz_ll()==null?0:vo.getLcz_ll()).append("</lczktll>");
					detail.append("<zszktll>").append(vo.getZsz_ll()==null?0:vo.getZsz_ll()).append("</zszktll>");
					detail.append("<yyzktll>").append(vo.getYyz_ll()==null?0:vo.getYyz_ll()).append("</yyzktll>");
					detail.append("<hczktll>").append(vo.getYcz_ll()==null?0:vo.getYcz_ll()).append("</hczktll>");
					detail.append("<lnzktll>").append(vo.getLnz_ll()==null?0:vo.getLnz_ll()).append("</lnzktll>");
					detail.append("<lnxzktll>").append(vo.getLnxz_ll()==null?0:vo.getLnxz_ll()).append("</lnxzktll>");
					detail.append("<cszktll>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</cszktll>");
					detail.append("<sgzktll>").append(vo.getSgz_ll()==null?0:vo.getSgz_ll()).append("</sgzktll>");
				}else{
					detail.append("<llzktll>").append(0).append("</llzktll>");
					detail.append("<ycxzktll>").append(0).append("</ycxzktll>");
					detail.append("<sxcqzktll>").append(0).append("</sxcqzktll>");
					detail.append("<sxzktll>").append(0).append("</sxzktll>");
					detail.append("<jxzktll>").append(0).append("</jxzktll>");
					detail.append("<jlszktll>").append(0).append("</jlszktll>");
					detail.append("<cdgzktll>").append(0).append("</cdgzktll>");
					detail.append("<lczktll>").append(0).append("</lczktll>");
					detail.append("<zszktll>").append(0).append("</zszktll>");
					detail.append("<yyzktll>").append(0).append("</yyzktll>");
					detail.append("<hczktll>").append(0).append("</hczktll>");
					detail.append("<lnzktll>").append(0).append("</lnzktll>");
					detail.append("<lnxzktll>").append(0).append("</lnxzktll>");
					detail.append("<cszktll>").append(0).append("</cszktll>");
					detail.append("<sgzktll>").append(0).append("</sgzktll>");
				}
				
			}

			List<txfjcllmxhzDataVO> listsycrkll = lyReportService.getCllcrkmxsyData(map);
			List<txfjcllmxhzDataVO> listsntqcrkll = lyReportService.getCllcrkmxsntqData(map);
			List<txfjcllmxhzDataVO> listsncrkll = lyReportService.getCllcrkmxsnData(map);
			for(txfjcllmxhzDataVO vo : listsycrkll){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
					}
					detail.append("<llhblls>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</llhblls>");
					detail.append("<ycxhblls>").append(vo.getYcxz_ll()==null?0:vo.getYcxz_ll()).append("</ycxhblls>");
					detail.append("<sxcqhblls>").append(vo.getSxcyjjqz_ll()==null?0:vo.getSxcyjjqz_ll()).append("</sxcqhblls>");
					detail.append("<sxhblls>").append(vo.getSxz_ll()==null?0:vo.getSxz_ll()).append("</sxhblls>");
					detail.append("<jxhblls>").append(vo.getJxz_ll()==null?0:vo.getJxz_ll()).append("</jxhblls>");
					detail.append("<jlshblls>").append(vo.getJlsz_ll()==null?0:vo.getJlsz_ll()).append("</jlshblls>");
					detail.append("<cdghblls>").append(vo.getCdgz_ll()==null?0:vo.getCdgz_ll()).append("</cdghblls>");
					detail.append("<lchblls>").append(vo.getLcz_ll()==null?0:vo.getLcz_ll()).append("</lchblls>");
					detail.append("<zshblls>").append(vo.getZsz_ll()==null?0:vo.getZsz_ll()).append("</zshblls>");
					detail.append("<yyhblls>").append(vo.getYyz_ll()==null?0:vo.getYyz_ll()).append("</yyhblls>");
					detail.append("<hchblls>").append(vo.getYcz_ll()==null?0:vo.getYcz_ll()).append("</hchblls>");
					detail.append("<lnhblls>").append(vo.getLnz_ll()==null?0:vo.getLnz_ll()).append("</lnhblls>");
					detail.append("<lnxhblls>").append(vo.getLnxz_ll()==null?0:vo.getLnxz_ll()).append("</lnxhblls>");
					detail.append("<cshblls>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</cshblls>");
					detail.append("<sghblls>").append(vo.getSgz_ll()==null?0:vo.getSgz_ll()).append("</sghblls>");
				}else{
					detail.append("<llhblls>").append(0).append("</llhblls>");
					detail.append("<ycxhblls>").append(0).append("</ycxhblls>");
					detail.append("<sxcqhblls>").append(0).append("</sxcqhblls>");
					detail.append("<sxhblls>").append(0).append("</sxhblls>");
					detail.append("<jxhblls>").append(0).append("</jxhblls>");
					detail.append("<jlshblls>").append(0).append("</jlshblls>");
					detail.append("<cdghblls>").append(0).append("</cdghblls>");
					detail.append("<lchblls>").append(0).append("</lchblls>");
					detail.append("<zshblls>").append(0).append("</zshblls>");
					detail.append("<yyhblls>").append(0).append("</yyhblls>");
					detail.append("<hchblls>").append(0).append("</hchblls>");
					detail.append("<lnhblls>").append(0).append("</lnhblls>");
					detail.append("<lnxhblls>").append(0).append("</lnxhblls>");
					detail.append("<cshblls>").append(0).append("</cshblls>");
					detail.append("<sghblls>").append(0).append("</sghblls>");
				}
				
			}
			
			for(txfjcllmxhzDataVO vo : listsntqcrkll){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
					}
					detail.append("<llbntblls>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</llbntblls>");
					detail.append("<ycxbntblls>").append(vo.getYcxz_ll()==null?0:vo.getYcxz_ll()).append("</ycxbntblls>");
					detail.append("<sxcqbntblls>").append(vo.getSxcyjjqz_ll()==null?0:vo.getSxcyjjqz_ll()).append("</sxcqbntblls>");
					detail.append("<sxbntblls>").append(vo.getSxz_ll()==null?0:vo.getSxz_ll()).append("</sxbntblls>");
					detail.append("<jxbntblls>").append(vo.getJxz_ll()==null?0:vo.getJxz_ll()).append("</jxbntblls>");
					detail.append("<jlsbntblls>").append(vo.getJlsz_ll()==null?0:vo.getJlsz_ll()).append("</jlsbntblls>");
					detail.append("<cdgbntblls>").append(vo.getCdgz_ll()==null?0:vo.getCdgz_ll()).append("</cdgbntblls>");
					detail.append("<lcbntblls>").append(vo.getLcz_ll()==null?0:vo.getLcz_ll()).append("</lcbntblls>");
					detail.append("<zsbntblls>").append(vo.getZsz_ll()==null?0:vo.getZsz_ll()).append("</zsbntblls>");
					detail.append("<yybntblls>").append(vo.getYyz_ll()==null?0:vo.getYyz_ll()).append("</yybntblls>");
					detail.append("<hcbntblls>").append(vo.getYcz_ll()==null?0:vo.getYcz_ll()).append("</hcbntblls>");
					detail.append("<lnbntblls>").append(vo.getLnz_ll()==null?0:vo.getLnz_ll()).append("</lnbntblls>");
					detail.append("<lnxbntblls>").append(vo.getLnxz_ll()==null?0:vo.getLnxz_ll()).append("</lnxbntblls>");
					detail.append("<csbntblls>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</csbntblls>");
					detail.append("<sgbntblls>").append(vo.getSgz_ll()==null?0:vo.getSgz_ll()).append("</sgbntblls>");
				}else{
					detail.append("<llbntblls>").append(0).append("</llbntblls>");
					detail.append("<ycxbntblls>").append(0).append("</ycxbntblls>");
					detail.append("<sxcqbntblls>").append(0).append("</sxcqbntblls>");
					detail.append("<sxbntblls>").append(0).append("</sxbntblls>");
					detail.append("<jxbntblls>").append(0).append("</jxbntblls>");
					detail.append("<jlsbntblls>").append(0).append("</jlsbntblls>");
					detail.append("<cdgbntblls>").append(0).append("</cdgbntblls>");
					detail.append("<lcbntblls>").append(0).append("</lcbntblls>");
					detail.append("<zsbntblls>").append(0).append("</zsbntblls>");
					detail.append("<yybntblls>").append(0).append("</yybntblls>");
					detail.append("<hcbntblls>").append(0).append("</hcbntblls>");
					detail.append("<lnbntblls>").append(0).append("</lnbntblls>");
					detail.append("<lnxbntblls>").append(0).append("</lnxbntblls>");
					detail.append("<csbntblls>").append(0).append("</csbntblls>");
					detail.append("<sgbntblls>").append(0).append("</sgbntblls>");
				}
				
			}
			
			for(txfjcllmxhzDataVO vo : listsncrkll){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
					}
					detail.append("<llbntblls>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</llbntblls>");
					detail.append("<ycxbntblls>").append(vo.getYcxz_ll()==null?0:vo.getYcxz_ll()).append("</ycxbntblls>");
					detail.append("<sxcqbntblls>").append(vo.getSxcyjjqz_ll()==null?0:vo.getSxcyjjqz_ll()).append("</sxcqbntblls>");
					detail.append("<sxbntblls>").append(vo.getSxz_ll()==null?0:vo.getSxz_ll()).append("</sxbntblls>");
					detail.append("<jxbntblls>").append(vo.getJxz_ll()==null?0:vo.getJxz_ll()).append("</jxbntblls>");
					detail.append("<jlsbntblls>").append(vo.getJlsz_ll()==null?0:vo.getJlsz_ll()).append("</jlsbntblls>");
					detail.append("<cdgbntblls>").append(vo.getCdgz_ll()==null?0:vo.getCdgz_ll()).append("</cdgbntblls>");
					detail.append("<lcbntblls>").append(vo.getLcz_ll()==null?0:vo.getLcz_ll()).append("</lcbntblls>");
					detail.append("<zsbntblls>").append(vo.getZsz_ll()==null?0:vo.getZsz_ll()).append("</zsbntblls>");
					detail.append("<yybntblls>").append(vo.getYyz_ll()==null?0:vo.getYyz_ll()).append("</yybntblls>");
					detail.append("<hcbntblls>").append(vo.getYcz_ll()==null?0:vo.getYcz_ll()).append("</hcbntblls>");
					detail.append("<lnbntblls>").append(vo.getLnz_ll()==null?0:vo.getLnz_ll()).append("</lnbntblls>");
					detail.append("<lnxbntblls>").append(vo.getLnxz_ll()==null?0:vo.getLnxz_ll()).append("</lnxbntblls>");
					detail.append("<csbntblls>").append(vo.getLlz_ll()==null?0:vo.getLlz_ll()).append("</csbntblls>");
					detail.append("<sgbntblls>").append(vo.getSgz_ll()==null?0:vo.getSgz_ll()).append("</sgbntblls>");
				}else{
					detail.append("<llbntblls>").append(0).append("</llbntblls>");
					detail.append("<ycxbntblls>").append(0).append("</ycxbntblls>");
					detail.append("<sxcqbntblls>").append(0).append("</sxcqbntblls>");
					detail.append("<sxbntblls>").append(0).append("</sxbntblls>");
					detail.append("<jxbntblls>").append(0).append("</jxbntblls>");
					detail.append("<jlsbntblls>").append(0).append("</jlsbntblls>");
					detail.append("<cdgbntblls>").append(0).append("</cdgbntblls>");
					detail.append("<lcbntblls>").append(0).append("</lcbntblls>");
					detail.append("<zsbntblls>").append(0).append("</zsbntblls>");
					detail.append("<yybntblls>").append(0).append("</yybntblls>");
					detail.append("<hcbntblls>").append(0).append("</hcbntblls>");
					detail.append("<lnbntblls>").append(0).append("</lnbntblls>");
					detail.append("<lnxbntblls>").append(0).append("</lnxbntblls>");
					detail.append("<csbntblls>").append(0).append("</csbntblls>");
					detail.append("<sgbntblls>").append(0).append("</sgbntblls>");
				}
				
			}
			
			Map<String,String> ldmcMap= Maps.newHashMap();
			ldmcMap.put("洛嵩段", "lsd");
			ldmcMap.put("嵩栾段", "sld");
			ldmcMap.put("洛宁段", "lnd");
			ldmcMap.put("宁卢段", "nld");
			
			List<txfjcllmxhzDataVO> listbychje = lyReportService.getTxfchjebyData(map);
			List<txfjcllmxhzDataVO> listbnchje = lyReportService.getTxfchjebnData(map);
			List<txfjcllmxhzDataVO> listzktchje = lyReportService.getTxfchjezktData(map);
			for(txfjcllmxhzDataVO vo : listbychje){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String ldmc = ldmcMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, ldmc+"_ch", new BigDecimal(ll.get(0)));
					}
					detail.append("<lsdbych>").append(vo.getLsd_ch()==null?BigDecimal.ZERO:vo.getLsd_ch()).append("</lsdbych>");
					detail.append("<sldbych>").append(vo.getSld_ch()==null?BigDecimal.ZERO:vo.getSld_ch()).append("</sldbych>");
					detail.append("<lndbych>").append(vo.getLnd_ch()==null?BigDecimal.ZERO:vo.getLnd_ch()).append("</lndbych>");
					detail.append("<nldbych>").append(vo.getNld_ch()==null?BigDecimal.ZERO:vo.getNld_ch()).append("</nldbych>");
				}else{
					detail.append("<lsdbych>").append(BigDecimal.ZERO).append("</lsdbych>");
					detail.append("<sldbych>").append(BigDecimal.ZERO).append("</sldbych>");
					detail.append("<lndbych>").append(BigDecimal.ZERO).append("</lndbych>");
					detail.append("<nldbych>").append(BigDecimal.ZERO).append("</nldbych>");
				}
				
			}
			for(txfjcllmxhzDataVO vo : listbnchje){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String ldmc = ldmcMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, ldmc+"_ch", new BigDecimal(ll.get(0)));
					}
					detail.append("<lsdbnch>").append(vo.getLsd_ch()==null?BigDecimal.ZERO:vo.getLsd_ch()).append("</lsdbnch>");
					detail.append("<sldbnch>").append(vo.getSld_ch()==null?BigDecimal.ZERO:vo.getSld_ch()).append("</sldbnch>");
					detail.append("<lndbnch>").append(vo.getLnd_ch()==null?BigDecimal.ZERO:vo.getLnd_ch()).append("</lndbnch>");
					detail.append("<nldbnch>").append(vo.getNld_ch()==null?BigDecimal.ZERO:vo.getNld_ch()).append("</nldbnch>");
				}else{
					detail.append("<lsdbnch>").append(BigDecimal.ZERO).append("</lsdbnch>");
					detail.append("<sldbnch>").append(BigDecimal.ZERO).append("</sldbnch>");
					detail.append("<lndbnch>").append(BigDecimal.ZERO).append("</lndbnch>");
					detail.append("<nldbnch>").append(BigDecimal.ZERO).append("</nldbnch>");
				}
				
			}
			for(txfjcllmxhzDataVO vo : listzktchje){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String ldmc = ldmcMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, ldmc+"_ch", new BigDecimal(ll.get(0)));
					}
					detail.append("<lsdzktch>").append(vo.getLsd_ch()==null?BigDecimal.ZERO:vo.getLsd_ch()).append("</lsdzktch>");
					detail.append("<sldzktch>").append(vo.getSld_ch()==null?BigDecimal.ZERO:vo.getSld_ch()).append("</sldzktch>");
					detail.append("<lndzktch>").append(vo.getLnd_ch()==null?BigDecimal.ZERO:vo.getLnd_ch()).append("</lndzktch>");
					detail.append("<nldzktch>").append(vo.getNld_ch()==null?BigDecimal.ZERO:vo.getNld_ch()).append("</nldzktch>");
				}else{
					detail.append("<lsdzktch>").append(BigDecimal.ZERO).append("</lsdzktch>");
					detail.append("<sldzktch>").append(BigDecimal.ZERO).append("</sldzktch>");
					detail.append("<lndzktch>").append(BigDecimal.ZERO).append("</lndzktch>");
					detail.append("<nldzktch>").append(BigDecimal.ZERO).append("</nldzktch>");
				}
				
			}
			
			List<txfjcllmxhzDataVO> listsychje = lyReportService.getTxfchjesyData(map);
			List<txfjcllmxhzDataVO> listsntqchje = lyReportService.getTxfchjesntqData(map);
			List<txfjcllmxhzDataVO> listsnchje = lyReportService.getTxfchjesnData(map);
			
			for(txfjcllmxhzDataVO vo : listsychje){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String ldmc = ldmcMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, ldmc+"_ch", new BigDecimal(ll.get(0)));
					}
					detail.append("<lsdbyzj>").append(vo.getLsd_ch()==null?BigDecimal.ZERO:vo.getLsd_ch()).append("</lsdbyzj>");
					detail.append("<sldbyzj>").append(vo.getSld_ch()==null?BigDecimal.ZERO:vo.getSld_ch()).append("</sldbyzj>");
					detail.append("<lndbyzj>").append(vo.getLnd_ch()==null?BigDecimal.ZERO:vo.getLnd_ch()).append("</lndbyzj>");
					detail.append("<nldbyzj>").append(vo.getNld_ch()==null?BigDecimal.ZERO:vo.getNld_ch()).append("</nldbyzj>");
				}else{
					detail.append("<lsdbyzj>").append(BigDecimal.ZERO).append("</lsdbyzj>");
					detail.append("<sldbyzj>").append(BigDecimal.ZERO).append("</sldbyzj>");
					detail.append("<lndbyzj>").append(BigDecimal.ZERO).append("</lndbyzj>");
					detail.append("<nldbyzj>").append(BigDecimal.ZERO).append("</nldbyzj>");
				}
				
			}
			for(txfjcllmxhzDataVO vo : listsntqchje){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String ldmc = ldmcMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, ldmc+"_ch", new BigDecimal(ll.get(0)));
					}
					detail.append("<lsdtqzj>").append(vo.getLsd_ch()==null?BigDecimal.ZERO:vo.getLsd_ch()).append("</lsdtqzj>");
					detail.append("<sldtqzj>").append(vo.getSld_ch()==null?BigDecimal.ZERO:vo.getSld_ch()).append("</sldtqzj>");
					detail.append("<lndtqzj>").append(vo.getLnd_ch()==null?BigDecimal.ZERO:vo.getLnd_ch()).append("</lndtqzj>");
					detail.append("<nldtqzj>").append(vo.getNld_ch()==null?BigDecimal.ZERO:vo.getNld_ch()).append("</nldtqzj>");
				}else{
					detail.append("<lsdtqzj>").append(BigDecimal.ZERO).append("</lsdtqzj>");
					detail.append("<sldtqzj>").append(BigDecimal.ZERO).append("</sldtqzj>");
					detail.append("<lndtqzj>").append(BigDecimal.ZERO).append("</lndtqzj>");
					detail.append("<nldtqzj>").append(BigDecimal.ZERO).append("</nldtqzj>");
				}
				
			}
			for(txfjcllmxhzDataVO vo : listsnchje){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String ldmc = ldmcMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, ldmc+"_ch", new BigDecimal(ll.get(0)));
					}
					detail.append("<lsdqnzj>").append(vo.getLsd_ch()==null?BigDecimal.ZERO:vo.getLsd_ch()).append("</lsdqnzj>");
					detail.append("<sldqnzj>").append(vo.getSld_ch()==null?BigDecimal.ZERO:vo.getSld_ch()).append("</sldqnzj>");
					detail.append("<lndqnzj>").append(vo.getLnd_ch()==null?BigDecimal.ZERO:vo.getLnd_ch()).append("</lndqnzj>");
					detail.append("<nldqnzj>").append(vo.getNld_ch()==null?BigDecimal.ZERO:vo.getNld_ch()).append("</nldqnzj>");
				}else{
					detail.append("<lsdqnzj>").append(BigDecimal.ZERO).append("</lsdqnzj>");
					detail.append("<sldqnzj>").append(BigDecimal.ZERO).append("</sldqnzj>");
					detail.append("<lndqnzj>").append(BigDecimal.ZERO).append("</lndqnzj>");
					detail.append("<nldqnzj>").append(BigDecimal.ZERO).append("</nldqnzj>");
				}
				
			}
			
			List<FxLtcl> listbyLtcl = lyReportService.getLtclbyData(map);
			List<FxLtcl> listbnLtcl = lyReportService.getLtclbnData(map);
			List<FxLtcl> listzktLtcl = lyReportService.getLtclzktData(map);
			for(FxLtcl vo : listbyLtcl){
				if(vo!=null){
					detail.append("<ltbysl>").append(vo.getLtsl()).append("</ltbysl>");
					detail.append("<ltbyje>").append(vo.getLtje()).append("</ltbyje>");
				}else{
					detail.append("<ltbysl>").append(0).append("</ltbysl>");
					detail.append("<ltbyje>").append(BigDecimal.ZERO).append("</ltbyje>");
				}
				
			}
			for(FxLtcl vo : listbnLtcl){
				if(vo!=null){
					detail.append("<ltbnsl>").append(vo.getLtsl()).append("</ltbnsl>");
					detail.append("<ltbnje>").append(vo.getLtje()).append("</ltbnje>");
				}else{
					detail.append("<ltbnsl>").append(0).append("</ltbnsl>");
					detail.append("<ltbnje>").append(BigDecimal.ZERO).append("</ltbnje>");
				}
				
			}
			for(FxLtcl vo : listzktLtcl){
				if(vo!=null){
					detail.append("<ltzktsl>").append(vo.getLtsl()).append("</ltzktsl>");
					detail.append("<ltzktje>").append(vo.getLtje()).append("</ltzktje>");
				}else{
					detail.append("<ltzktsl>").append(0).append("</ltzktsl>");
					detail.append("<ltzktje>").append(BigDecimal.ZERO).append("</ltzktje>");
				}
				
			}
			
			sb.append(detail).append("<st>").append(st).append("</st></_grparam></report>");
			
			ResponseUtils.renderXml(rsp, sb.toString());
			
			
		}
	
	
	@RequestMapping("07pjybbIndex")
	public String pjybb(Model model){
		//上月
				DateTime dt = new DateTime();
				String st = dt.plusMonths(-1).toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/07pjybbIndex";
	}
	@RequestMapping("08etcmxhzbIndex")
	public String etcmxhzbIndex(Model model){
		//上月
		DateTime dt = new DateTime();
		Integer nd = dt.getYear();
		String st = dt.plusMonths(-1).toString("yyyy年MM月");
		model.addAttribute("st", st);
		model.addAttribute("nd", nd+"年");
		return "report/08etcmxhzbIndex";
	}
	
	/**
	 * 0801通行费及车流量明细汇总表
	 * 明细数据
	 * @param model
	 * @return
	 */
	
	@RequestMapping("0801etcmxbData")
	public void etcmxbData(HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		String ssr = st.replace("年", "-").replace("月", ""); 
		DateTime dt = new DateTime(ssr);
		int mean = dt.dayOfMonth().getMaximumValue();
		Integer year = dt.getYear();
		Integer pyear = year-1;
		String month = st.substring(5,8);
		String yearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(-1);
		String preYearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(1).plusMonths(-1);
		String preMonth = dt.toString("yyyyMM");
		String Yearst = year+"01";
		String preyear = pyear+"01";
		//
		String[] StartTime = {yearMonth+"01",yearMonth+"11",yearMonth+"21"};
		String[] EndTime = {yearMonth+"10",yearMonth+"20",yearMonth+mean};
		
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(Yearst)){
			map.put("year",Yearst);
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
		if(StringUtils.isNoneBlank(preyear)){
			map.put("preyear",preyear);
		}
		
		Map<String,String> zdMap= Maps.newHashMap();
		zdMap.put("洛龙站", "llz");
		zdMap.put("伊川西站", "ycxz");
		zdMap.put("嵩县产业集聚区站", "sxcyjjqz");
		zdMap.put("嵩县站", "sxz");
		zdMap.put("旧县站", "jxz");
		zdMap.put("九龙山站", "jlsz");
		zdMap.put("重渡沟站", "cdgz");
		zdMap.put("栾川站", "lcz");
		zdMap.put("周山站", "zsz");
		zdMap.put("宜阳站", "yyz");
		zdMap.put("韩城站", "ycz");
		zdMap.put("洛宁站", "lnz");
		zdMap.put("洛宁西站", "lnxz");
		zdMap.put("长水站", "csz");
		zdMap.put("上戈站", "sgz");
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){
			map.put("deptid",user.getDeptId());
		}
	indexService.setOrgDeptParam(orgId,deptId,map);
	/*
	 * 金额
	 */
	List<txfjcllmxhzDataVO> listby = lyReportService.getEtcTxfchsrmxbyData(map);
	List<txfjcllmxhzDataVO> listbn = lyReportService.getEtcTxfchsrmxbnData(map);
	List<txfjcllmxhzDataVO> listzkt = lyReportService.getEtcTxfchsrmxzktData(map);
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
	/*
	 * 上中下旬金额
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getEtcTxfchsrmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
				}
				detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
				detail.append("<xm2>").append("实际免收通行费收入（元）").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月金额
	 */
	for(txfjcllmxhzDataVO vo : listby){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
			detail.append("<xm2>").append("实际免收通行费收入（元）").append("</xm2>");
			
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年金额
	 */
	for(txfjcllmxhzDataVO vo : listbn){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
			detail.append("<xm2>").append("实际免收通行费收入（元）").append("</xm2>");
			
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通金额
	 */
	for(txfjcllmxhzDataVO vo : listzkt){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
			detail.append("<xm2>").append("实际免收通行费收入（元）").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	/**
	 * 入口流量
	 */
	List<txfjcllmxhzDataVO> listbyrkll = lyReportService.getEtcCllmxbyData(map);
	List<txfjcllmxhzDataVO> listbnrkll = lyReportService.getEtcCllmxbnData(map);
	List<txfjcllmxhzDataVO> listzktrkll = lyReportService.getEtcCllmxzktData(map);
	
	/*
	 * 上中下旬流量
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getEtcCllmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
				}
				detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
				detail.append("<xm2>").append("入口车流量").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月流量
	 */
	for(txfjcllmxhzDataVO vo : listbyrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("入口车流量").append("</xm2>");
			
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年流量
	 */
	for(txfjcllmxhzDataVO vo : listbnrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("入口车流量").append("</xm2>");
			
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通流量
	 */
	for(txfjcllmxhzDataVO vo : listzktrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("入口车流量").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	
	/**
	 * 出口流量
	 */
	List<txfjcllmxhzDataVO> listbyckll = lyReportService.getEtcCllckmxbyData(map);
	List<txfjcllmxhzDataVO> listbnckll = lyReportService.getEtcCllckmxbnData(map);
	List<txfjcllmxhzDataVO> listzktckll = lyReportService.getEtcCllckmxzktData(map);
	
	/*
	 * 上中下旬流量
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getEtcCllckmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
				}
				detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
				detail.append("<xm2>").append("出口车流量").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月流量
	 */
	for(txfjcllmxhzDataVO vo : listbyckll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出口车流量").append("</xm2>");
			detail.append("<fz>").append("2").append("</fz>");
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年流量
	 */
	for(txfjcllmxhzDataVO vo : listbnckll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出口车流量").append("</xm2>");
			detail.append("<fz>").append("1").append("</fz>");
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通流量
	 */
	for(txfjcllmxhzDataVO vo : listzktckll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出口车流量").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/**
	 * 出入口流量
	 */
	List<txfjcllmxhzDataVO> listbycrkll = lyReportService.getEtcCllcrkmxbyData(map);
	List<txfjcllmxhzDataVO> listbncrkll = lyReportService.getEtcCllcrkmxbnData(map);
	List<txfjcllmxhzDataVO> listzktcrkll = lyReportService.getEtcCllcrkmxzktData(map);
	
	/*
	 * 上中下旬流量
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getEtcCllcrkmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
				}
				detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
				detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月流量
	 */
	for(txfjcllmxhzDataVO vo : listbycrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
			
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年流量
	 */
	for(txfjcllmxhzDataVO vo : listbncrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
			
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通流量
	 */
	for(txfjcllmxhzDataVO vo : listzktcrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	sb.append(detail).append("</xml><_grparam>");
	sb.append("<rq>").append(st).append("</rq></_grparam></report>");
	
	ResponseUtils.renderXml(rsp, sb.toString());
	
	}
	
	/**
	 * 通行费及车流量明细汇总表
	 * 汇总数据
	 * @param model
	 * @return
	 */
	
	@RequestMapping("0802etchzbData")
	public void etchzbData(HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		String year = st.replace("年", ""); 
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(year)){
			map.put("year",year);
		}
		Map<String,String> zdMap= Maps.newHashMap();
		zdMap.put("洛龙站", "llz");
		zdMap.put("伊川西站", "ycxz");
		zdMap.put("嵩县产业集聚区站", "sxcyjjqz");
		zdMap.put("嵩县站", "sxz");
		zdMap.put("旧县站", "jxz");
		zdMap.put("九龙山站", "jlsz");
		zdMap.put("重渡沟站", "cdgz");
		zdMap.put("栾川站", "lcz");
		zdMap.put("周山站", "zsz");
		zdMap.put("宜阳站", "yyz");
		zdMap.put("韩城站", "ycz");
		zdMap.put("洛宁站", "lnz");
		zdMap.put("洛宁西站", "lnxz");
		zdMap.put("长水站", "csz");
		zdMap.put("上戈站", "sgz");
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){
			map.put("deptid",user.getDeptId());
		}
	indexService.setOrgDeptParam(orgId,deptId,map);
	
	List<txfjcllmxhzDataVO> listll = lyReportService.getEtcCllhzData(map);
	List<txfjcllmxhzDataVO> listje = lyReportService.getEtcTxfhzData(map);
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
		
	/*
	 * 金额
	 */
	for(txfjcllmxhzDataVO vo : listje){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><bt>").append("实际通行费（元）").append("</bt>");
			detail.append("<bt1>").append("各站通行费小计").append("</bt1>");
			detail.append("<bt2>").append("通行费合计").append("</bt2>");
			detail.append("<sj>").append(vo.getYf()+"月").append("</sj>");
			detail.append("<fz>").append("1").append("</fz>");
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 车流量汇总
	 */
	for(txfjcllmxhzDataVO vo : listll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><bt>").append("车流量（辆）").append("</bt>");
			detail.append("<bt1>").append("各站车流量小计(出口+进口)").append("</bt1>");
			detail.append("<bt2>").append("通行费合计").append("</bt2>");
			detail.append("<sj>").append(vo.getYf()+"月").append("</sj>");
			detail.append("<fz>").append("2").append("</fz>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	sb.append(detail).append("</xml><_grparam>");
	sb.append("<rq>").append(st).append("</rq></_grparam></report>");
	
	ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	
	
	@RequestMapping("09mtcmxhzbIndex")
	public String mtcmxhzbIndex(Model model){
		//上月
		DateTime dt = new DateTime();
		Integer nd = dt.getYear();
		String st = dt.plusMonths(-1).toString("yyyy年MM月");
		model.addAttribute("st", st);
		model.addAttribute("nd", nd+"年");
		return "report/09mtcmxhzbIndex";
	}
	
	/**
	 * 0901人工通行费及车流量明细汇总表
	 * 明细数据
	 * @param model
	 * @return
	 */
	
	@RequestMapping("0901MtcmxbData")
	public void MtcmxbData(HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		String ssr = st.replace("年", "-").replace("月", ""); 
		DateTime dt = new DateTime(ssr);
		int mean = dt.dayOfMonth().getMaximumValue();
		Integer year = dt.getYear();
		Integer pyear = year-1;
		String month = st.substring(5,8);
		String yearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(-1);
		String preYearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(1).plusMonths(-1);
		String preMonth = dt.toString("yyyyMM");
		String Yearst = year+"01";
		String preyear = pyear+"01";
		//
		String[] StartTime = {yearMonth+"01",yearMonth+"11",yearMonth+"21"};
		String[] EndTime = {yearMonth+"10",yearMonth+"20",yearMonth+mean};
		
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(Yearst)){
			map.put("year",Yearst);
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
		if(StringUtils.isNoneBlank(preyear)){
			map.put("preyear",preyear);
		}
		
		Map<String,String> zdMap= Maps.newHashMap();
		zdMap.put("洛龙站", "llz");
		zdMap.put("伊川西站", "ycxz");
		zdMap.put("嵩县产业集聚区站", "sxcyjjqz");
		zdMap.put("嵩县站", "sxz");
		zdMap.put("旧县站", "jxz");
		zdMap.put("九龙山站", "jlsz");
		zdMap.put("重渡沟站", "cdgz");
		zdMap.put("栾川站", "lcz");
		zdMap.put("周山站", "zsz");
		zdMap.put("宜阳站", "yyz");
		zdMap.put("韩城站", "ycz");
		zdMap.put("洛宁站", "lnz");
		zdMap.put("洛宁西站", "lnxz");
		zdMap.put("长水站", "csz");
		zdMap.put("上戈站", "sgz");
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){
			map.put("deptid",user.getDeptId());
		}
	indexService.setOrgDeptParam(orgId,deptId,map);
	/*
	 * 金额
	 */
	List<txfjcllmxhzDataVO> listby = lyReportService.getMtcTxfchsrmxbyData(map);
	List<txfjcllmxhzDataVO> listbn = lyReportService.getMtcTxfchsrmxbnData(map);
	List<txfjcllmxhzDataVO> listzkt = lyReportService.getMtcTxfchsrmxzktData(map);
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
	/*
	 * 上中下旬金额
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getMtcTxfchsrmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
				}
				detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
				detail.append("<xm2>").append("实际免收通行费收入（元）").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月金额
	 */
	for(txfjcllmxhzDataVO vo : listby){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
			detail.append("<xm2>").append("实际免收通行费收入（元）").append("</xm2>");
			
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年金额
	 */
	for(txfjcllmxhzDataVO vo : listbn){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
			detail.append("<xm2>").append("实际免收通行费收入（元）").append("</xm2>");
			
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通金额
	 */
	for(txfjcllmxhzDataVO vo : listzkt){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><xm1>").append("实际免收通行费收入（元）").append("</xm1>");
			detail.append("<xm2>").append("实际免收通行费收入（元）").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}
	/**
	 * 入口流量
	 */
	List<txfjcllmxhzDataVO> listbyrkll = lyReportService.getMtcCllmxbyData(map);
	List<txfjcllmxhzDataVO> listbnrkll = lyReportService.getMtcCllmxbnData(map);
	List<txfjcllmxhzDataVO> listzktrkll = lyReportService.getMtcCllmxzktData(map);
	
	/*
	 * 上中下旬流量
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getMtcCllmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
				}
				detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
				detail.append("<xm2>").append("入口车流量").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月流量
	 */
	for(txfjcllmxhzDataVO vo : listbyrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("入口车流量").append("</xm2>");
			
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年流量
	 */
	for(txfjcllmxhzDataVO vo : listbnrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("入口车流量").append("</xm2>");
			
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通流量
	 */
	for(txfjcllmxhzDataVO vo : listzktrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("入口车流量").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	
	/**
	 * 出口流量
	 */
	List<txfjcllmxhzDataVO> listbyckll = lyReportService.getMtcCllckmxbyData(map);
	List<txfjcllmxhzDataVO> listbnckll = lyReportService.getMtcCllckmxbnData(map);
	List<txfjcllmxhzDataVO> listzktckll = lyReportService.getMtcCllckmxzktData(map);
	
	/*
	 * 上中下旬流量
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getMtcCllckmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
				}
				detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
				detail.append("<xm2>").append("出口车流量").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月流量
	 */
	for(txfjcllmxhzDataVO vo : listbyckll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出口车流量").append("</xm2>");
			detail.append("<fz>").append("2").append("</fz>");
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年流量
	 */
	for(txfjcllmxhzDataVO vo : listbnckll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出口车流量").append("</xm2>");
			detail.append("<fz>").append("1").append("</fz>");
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通流量
	 */
	for(txfjcllmxhzDataVO vo : listzktckll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出口车流量").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/**
	 * 出入口流量
	 */
	List<txfjcllmxhzDataVO> listbycrkll = lyReportService.getMtcCllcrkmxbyData(map);
	List<txfjcllmxhzDataVO> listbncrkll = lyReportService.getMtcCllcrkmxbnData(map);
	List<txfjcllmxhzDataVO> listzktcrkll = lyReportService.getMtcCllcrkmxzktData(map);
	
	/*
	 * 上中下旬流量
	 */
	for(int i = 0;i < StartTime.length;i++){
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("StartTime", StartTime[i]);
			map.put("EndTime", EndTime[i]);
		}
		
		List<txfjcllmxhzDataVO> list = lyReportService.getMtcCllcrkmxData(map);
		for(txfjcllmxhzDataVO vo : list){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					//System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = zdMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
				}
				detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
				detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
				String szxx = "";
				if(StartTime[i].substring(6, 8).equals("01") ){
					szxx = "上旬";
					
				}else if(StartTime[i].substring(6, 8).equals("11")){
					szxx = "中旬";
					
				}else if(StartTime[i].substring(6, 8).equals("21")){
					szxx = "下旬";
					
				}
				detail.append("<sj>").append(szxx).append("</sj>");
				
				detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
				detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
				detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
				detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
				
				detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
				detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
				detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
				detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
				
				detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
				detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
				detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
				detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
				
				detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
				detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
				detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
				detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
				detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
				detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
				detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
				detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
			}
		}
	}
	/*
	 * 本月流量
	 */
	for(txfjcllmxhzDataVO vo : listbycrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
			
			detail.append("<sj>").append("本月合计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 本年流量
	 */
	for(txfjcllmxhzDataVO vo : listbncrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
			
			detail.append("<sj>").append("本年累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	
	/*
	 * 自开通流量
	 */
	for(txfjcllmxhzDataVO vo : listzktcrkll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><xm1>").append("收费站车流量（量）").append("</xm1>");
			detail.append("<xm2>").append("出入口合计车流量").append("</xm2>");
			
			detail.append("<sj>").append("自开通累计").append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	sb.append(detail).append("</xml><_grparam>");
	sb.append("<rq>").append(st).append("</rq></_grparam></report>");
	
	ResponseUtils.renderXml(rsp, sb.toString());
	
	}
	
	/**
	 * 0902人工通行费及车流量明细汇总表
	 * 汇总数据
	 * @param model
	 * @return
	 */
	
	@RequestMapping("0902MtchzbData")
	public void MtchzbData(HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		String year = st.replace("年", ""); 
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(year)){
			map.put("year",year);
		}
		Map<String,String> zdMap= Maps.newHashMap();
		zdMap.put("洛龙站", "llz");
		zdMap.put("伊川西站", "ycxz");
		zdMap.put("嵩县产业集聚区站", "sxcyjjqz");
		zdMap.put("嵩县站", "sxz");
		zdMap.put("旧县站", "jxz");
		zdMap.put("九龙山站", "jlsz");
		zdMap.put("重渡沟站", "cdgz");
		zdMap.put("栾川站", "lcz");
		zdMap.put("周山站", "zsz");
		zdMap.put("宜阳站", "yyz");
		zdMap.put("韩城站", "ycz");
		zdMap.put("洛宁站", "lnz");
		zdMap.put("洛宁西站", "lnxz");
		zdMap.put("长水站", "csz");
		zdMap.put("上戈站", "sgz");
	
	indexService.setOrgDeptParam(orgId,deptId,map);
	AuthUserVO user = UserUtils.getAuthUser();
	if(StringUtils.isNoneBlank(user.getDeptId())){
		map.put("deptid",user.getDeptId());
	}
	List<txfjcllmxhzDataVO> listll = lyReportService.getMtcCllhzData(map);
	List<txfjcllmxhzDataVO> listje = lyReportService.getMtcTxfhzData(map);
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
		
	/*
	 * 金额
	 */
	/*for(txfjcllmxhzDataVO vo : listje){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row><bt>").append("实际通行费（元）").append("</bt>");
			detail.append("<bt1>").append("各站通行费小计").append("</bt1>");
			detail.append("<bt2>").append("通行费合计").append("</bt2>");
			detail.append("<sj>").append(vo.getYf()+"月").append("</sj>");
			detail.append("<fz>").append("1").append("</fz>");
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_je()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_je()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_je()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_je()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_je()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_je()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_je()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_je()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_je()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_je()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_je()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_je()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_je()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</hj></row>");
		}
	
	}*/
	
	/*
	 * 车流量汇总
	 */
	for(txfjcllmxhzDataVO vo : listll){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_ll", Integer.valueOf(ll.get(0)));
			}
			detail.append("<row><bt>").append("车流量（辆）").append("</bt>");
			detail.append("<bt1>").append("各站车流量小计(出口+进口)（除去ETC）").append("</bt1>");
			detail.append("<bt2>").append("通行费合计").append("</bt2>");
			detail.append("<sj>").append(vo.getYf()+"月").append("</sj>");
			detail.append("<fz>").append("2").append("</fz>");
			
			detail.append("<llz>").append(vo.getLlz_ll()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_ll()).append("</ycxz>");
			detail.append("<sxcyjjqz>").append(vo.getSxcyjjqz_ll()).append("</sxcyjjqz>");
			detail.append("<sxz>").append(vo.getSxz_ll()).append("</sxz>");
			
			detail.append("<jxz>").append(vo.getJxz_ll()).append("</jxz>");
			detail.append("<jlsz>").append(vo.getJlsz_ll()).append("</jlsz>");
			detail.append("<cdgz>").append(vo.getCdgz_ll()).append("</cdgz>");
			detail.append("<lcz>").append(vo.getLcz_ll()).append("</lcz>");
			
			detail.append("<zsz>").append(vo.getZsz_ll()).append("</zsz>");
			detail.append("<yyz>").append(vo.getYyz_ll()).append("</yyz>");
			detail.append("<ycz>").append(vo.getYcz_ll()).append("</ycz>");
			detail.append("<lnz>").append(vo.getLnz_ll()).append("</lnz>");
			
			detail.append("<lnxz>").append(vo.getLnxz_ll()).append("</lnxz>");
			detail.append("<csz>").append(vo.getCsz_ll()).append("</csz>");
			detail.append("<sgz>").append(vo.getSgz_ll()).append("</sgz>");
			detail.append("<lsd>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())).append("</lsd>");
			detail.append("<sld>").append((vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())).append("</sld>");
			detail.append("<lnd>").append((vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())).append("</lnd>");
			detail.append("<nld>").append((vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</nld>");
			detail.append("<hj>").append((vo.getLlz_ll())+(vo.getYcxz_ll())+(vo.getSxcyjjqz_ll())+(vo.getSxz_ll())+(vo.getJxz_ll())+(vo.getJlsz_ll())+(vo.getCdgz_ll())+(vo.getLcz_ll())+(vo.getZsz_ll())+(vo.getYyz_ll())+(vo.getYcz_ll())+(vo.getLnz_ll())+(vo.getLnxz_ll())+(vo.getCsz_ll())+(vo.getSgz_ll())).append("</hj></row>");
		}
	
	}
	sb.append(detail).append("</xml><_grparam>");
	sb.append("<rq>").append(st).append("</rq></_grparam></report>");
	
	ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	
	
	
	@RequestMapping("10czhfcxllbIndex")
	public String czhfcxllbIndex(Model model){
		//上月
				DateTime dt = new DateTime();
				Integer st = dt.getYear();
				model.addAttribute("st", st+"年");
		return "report/10czhfcxllbIndex";
	}
	
	
	@RequestMapping("10fcxllData")
	public void fcxllData(HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		String ssr = st.replace("年", ""); 
		DateTime dt = new DateTime(ssr);
		Integer year = dt.getYear();
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(null!=year){
			map.put("year",year);
		}
		System.out.println(deptId);
		if(deptId.equals("洛阳管理处")){
		map.put("deptId",null);
		}else{
			map.put("deptId",deptId);
		}
		
		List<FcxllDataVO> list = lyReportService.getFcxllData(map);
		StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
		for(FcxllDataVO vo:list){
			sb.append("<row>");
			sb.append("<sj>").append(vo.getYf()+"月").append("</sj>");
			sb.append("<zdmc>").append(map.get("title")).append("</zdmc>");
			sb.append("<hj>").append(vo.getKc1()+vo.getKc2()+vo.getKc3()+vo.getKc4()+vo.getHc1()+vo.getHc2()+vo.getHc3()+vo.getHc4()+vo.getHc5()+vo.getHc6()).append("</hj>");
			sb.append("<kchj>").append(vo.getKc1()+vo.getKc2()+vo.getKc3()+vo.getKc4()).append("</kchj>");
			sb.append("<hchj>").append(vo.getHc1()+vo.getHc2()+vo.getHc3()+vo.getHc4()+vo.getHc5()+vo.getHc6()).append("</hchj>");
			sb.append("<kc1>").append(vo.getKc1()).append("</kc1>");
			sb.append("<kc2>").append(vo.getKc2()).append("</kc2>");
			sb.append("<kc3>").append(vo.getKc3()).append("</kc3>");
			sb.append("<kc4>").append(vo.getKc4()).append("</kc4>");
			sb.append("<hc1>").append(vo.getHc1()).append("</hc1>");
			sb.append("<hc2>").append(vo.getHc2()).append("</hc2>");
			sb.append("<hc3>").append(vo.getHc3()).append("</hc3>");
			sb.append("<hc4>").append(vo.getHc4()).append("</hc4>");
			sb.append("<hc5>").append(vo.getHc5()).append("</hc5>");
			sb.append("<hc6>").append(vo.getHc6()).append("</hc6>");
			sb.append("</row>");
		}
		
		sb.append("</xml><_grparam>");
		if(deptId.equals("洛阳管理处")){
			sb.append("<ldmc>").append("").append("</ldmc>");
		}else{
			sb.append("<ldmc>").append(deptId).append("</ldmc>");
		}
		sb.append("<rq>").append(st).append("</rq></_grparam></report>");
		
		ResponseUtils.renderXml(rsp, sb.toString());
		
	}
	@RequestMapping("11czhfcxjebIndex")
	public String czhfcxjebIndex(Model model){
		//上月
		DateTime dt = new DateTime();
		Integer st = dt.getYear();
		model.addAttribute("st", st+"年");
		return "report/11czhfcxjebIndex";
	}
	
	@RequestMapping("11fcxjeData")
	public void fcxjeData(HttpServletResponse rsp,
		@RequestParam(value="st",required=false)String st,
		@RequestParam(value="et",required=false)String et,
		@RequestParam(value="orgId",required=false)String orgId,
		@RequestParam(value="deptId",required=false)String deptId){
		String ssr = st.replace("年", ""); 
		DateTime dt = new DateTime(ssr);
		Integer year = dt.getYear();
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(null!=year){
			map.put("year",year);
		}
		System.out.println(deptId);
		if(deptId.equals("洛阳管理处")){
		map.put("deptId",null);
		}else{
			map.put("deptId",deptId);
		}
		
		List<FcxllDataVO> list = lyReportService.getFcxllData(map);
		StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
		for(FcxllDataVO vo:list){
			sb.append("<row>");
			sb.append("<sj>").append(vo.getYf()+"月").append("</sj>");
			sb.append("<zdmc>").append(map.get("title")).append("</zdmc>");
			sb.append("<hj>").append(vo.getKc1()+vo.getKc2()+vo.getKc3()+vo.getKc4()+vo.getHc1()+vo.getHc2()+vo.getHc3()+vo.getHc4()+vo.getHc5()+vo.getHc6()).append("</hj>");
			sb.append("<kchj>").append(vo.getKc1()+vo.getKc2()+vo.getKc3()+vo.getKc4()).append("</kchj>");
			sb.append("<hchj>").append(vo.getHc1()+vo.getHc2()+vo.getHc3()+vo.getHc4()+vo.getHc5()+vo.getHc6()).append("</hchj>");
			sb.append("<kc1>").append(vo.getKc1()).append("</kc1>");
			sb.append("<kc2>").append(vo.getKc2()).append("</kc2>");
			sb.append("<kc3>").append(vo.getKc3()).append("</kc3>");
			sb.append("<kc4>").append(vo.getKc4()).append("</kc4>");
			sb.append("<hc1>").append(vo.getHc1()).append("</hc1>");
			sb.append("<hc2>").append(vo.getHc2()).append("</hc2>");
			sb.append("<hc3>").append(vo.getHc3()).append("</hc3>");
			sb.append("<hc4>").append(vo.getHc4()).append("</hc4>");
			sb.append("<hc5>").append(vo.getHc5()).append("</hc5>");
			sb.append("<hc6>").append(vo.getHc6()).append("</hc6>");
			sb.append("</row>");
		}
		
		sb.append("</xml><_grparam>");
		if(deptId.equals("洛阳管理处")){
			sb.append("<ldmc>").append("").append("</ldmc>");
		}else{
			sb.append("<ldmc>").append(deptId).append("</ldmc>");
		}
		sb.append("<rq>").append(st).append("</rq></_grparam></report>");
		
		ResponseUtils.renderXml(rsp, sb.toString());
		
	}
	//流失卡、灰名单表首页页
		@RequestMapping("12lskhmdbIndex")
		public String lskhmdbIndex(Model model){
			//上月
					DateTime dt = new DateTime();
					String st = dt.plusMonths(-1).toString("yyyy年MM月");
					model.addAttribute("st", st);
			return "report/12lskhmdbIndex";
		}

		//正常丢失卡已缴纳通行费的车辆统计表
		@RequestMapping("1201zclskyjntxftjbData")
		public void zclskyjntxftjbData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
			
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			String sd =st.replace("年", "").replace("月", "");
			if(StringUtils.isNoneBlank(sd)){
				map.put("st",sd);
			}
			List<FxLsk> list = lyReportService.getZclskyjntxftjbData(map);
			int xh = 1;
			StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
			for(FxLsk vo : list){
				sb.append("<row><xh>").append(xh).append("</xh>");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String lskrq = formatter.format(vo.getLskrq());
				sb.append("<lskrq>").append(lskrq).append("</lskrq>");
				sb.append("<lskszzdmc>").append(vo.getLskszzdmc()).append("</lskszzdmc>");
				SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lskszsj = formatter2.format(vo.getLskszsj());
				sb.append("<lskszsj>").append(lskszsj).append("</lskszsj>");
				sb.append("<lskthkh>").append(vo.getLskthkh()).append("</lskthkh>");
				sb.append("<cxmc>").append(vo.getCxmc()).append("</cxmc>");
				sb.append("<lskcph>").append(vo.getLskcph()).append("</lskcph>");
				sb.append("<lskclzdmc>").append(vo.getLskclzdmc()).append("</lskclzdmc>");
				sb.append("<lskclqksm>").append(vo.getLskclqksm()).append("</lskclqksm></row>");
				xh++;
			}
			
			sb.append("</xml><_grparam>");
			sb.append("<st>").append(st).append("</st></_grparam></report>");
			
			ResponseUtils.renderXml(rsp, sb.toString());
		}
		
		//拟列入“灰名单”车辆申请表
		@RequestMapping("1202hmdclsqbData")
		public void hmdclsqbData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
			
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			String sd =st.replace("年", "").replace("月", "");
			if(StringUtils.isNoneBlank(sd)){
				map.put("st",sd);
			}
			indexService.setOrgDeptParam(orgId,deptId,map);
			List<FxHmd> list = lyReportService.getHmdclsqbData(map);
			int xh = 1;
			StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
			for(FxHmd vo : list){
				sb.append("<row><xh>").append(xh).append("</xh>");
				sb.append("<hmdthkh>").append(vo.getHmdthkh()).append("</hmdthkh>");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String hmdfksj = formatter.format(vo.getHmdfksj());
				sb.append("<hmdfksj>").append(hmdfksj).append("</hmdfksj>");
				sb.append("<hmdkhh>").append(vo.getHmdkhh()).append("</hmdkhh>");
				sb.append("<hmdfkzm>").append(vo.getHmdfkzm()).append("</hmdfkzm>"); 
				sb.append("<hmdfkygh>").append(vo.getHmdfkygh()).append("</hmdfkygh>"); 
				sb.append("<cxmc>").append(vo.getCxmc()).append("</cxmc>"); 
				sb.append("<hmdffcx>").append(vo.getHmdffcx()).append("</hmdffcx>"); 
				sb.append("<hmdcp>").append(vo.getHmdcp()).append("</hmdcp>"); 
				sb.append("<sfmc>").append(vo.getSfmc()==null?"":vo.getSfmc()).append("</sfmc></row>");
				xh++;
			}
			
			sb.append("</xml><_grparam>");
			sb.append("<st>").append(st+"份").append("</st>");
			sb.append("<hj>").append(xh-1+"辆").append("</hj>");
			sb.append("<dw>").append(map.get("title")).append("</dw></_grparam></report>");
			
			ResponseUtils.renderXml(rsp, sb.toString());
		}
		
		//闯关、事故车辆调查表
		@RequestMapping("1203cgsgclbData")
		public void cgsgclbData(HttpServletResponse rsp,
				@RequestParam(value="st",required=false)String st,
				@RequestParam(value="orgId",required=false)String orgId,
				@RequestParam(value="deptId",required=false)String deptId){
			
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			String sd =st.replace("年", "").replace("月", "");
			if(StringUtils.isNoneBlank(sd)){
				map.put("st",sd);
			}
			List<FxCgcl> list = lyReportService.getCgsgclbData(map);
			int xh = 1;
			int cgcl = 0;
			int sgcl = 0;
			StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
			for(FxCgcl vo : list){
				sb.append("<row><xh>").append(xh).append("</xh>");
				sb.append("<ldmc>").append(vo.getLdmc()).append("</ldmc>");
				sb.append("<cgsfzmc>").append(vo.getCgsfzmc()).append("</cgsfzmc>");
				sb.append("<cgcksjcl>").append(vo.getCgcksjcl()==null?"":vo.getCgcksjcl()).append("</cgcksjcl>");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(vo.getCgcksj()!=null){
				String cgcksj = formatter.format(vo.getCgcksj());
				sb.append("<cgcksj>").append(cgcksj).append("</cgcksj>");}
				sb.append("<cgckqksm>").append(vo.getCgckqksm()==null?"":vo.getCgckqksm()).append("</cgckqksm>");
				sb.append("<cgcljg>").append(vo.getCgcljg()==null?"":vo.getCgcljg()).append("</cgcljg>");
				sb.append("<cgsgcp>").append(vo.getCgsgcp()==null?"":vo.getCgsgcp()).append("</cgsgcp>");
				if(vo.getCgtgsj()!=null){
				String cgtgsj = formatter.format(vo.getCgtgsj());
				sb.append("<cgtgsj>").append(cgtgsj).append("</cgtgsj>");}
				sb.append("<cgwhsk>").append(vo.getCgwhsk()==null?"":vo.getCgwhsk()).append("</cgwhsk>");
				sb.append("<cgsgcljg>").append(vo.getCgsgcljg()==null?"":vo.getCgsgcljg()).append("</cgsgcljg></row>");
				xh++;
				if(vo.getCgcksjcl()!=null){
					cgcl++;
				}
				if(vo.getCgsgcp()!=null){
					sgcl++;
				}
			}
			
			sb.append("</xml><_grparam>");
			sb.append("<st>").append(st+"份").append("</st>");
			sb.append("<ck>").append(cgcl+"辆").append("</ck>");
			sb.append("<sg>").append(sgcl+"辆").append("</sg></_grparam></report>");
			
			ResponseUtils.renderXml(rsp, sb.toString());
		}
		
		//通行卡强制组合情况统计表
		@RequestMapping("1204qzzhqkbData")
		public void qzzhqkbData(HttpServletResponse rsp,
				@RequestParam(value="st",required=false)String st,
				@RequestParam(value="orgId",required=false)String orgId,
				@RequestParam(value="deptId",required=false)String deptId){
			
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			String sd =st.replace("年", "").replace("月", "");
			if(StringUtils.isNoneBlank(sd)){
				map.put("st",sd);
			}
			List<FxQzzh> list = lyReportService.getQzzhqkbData(map);
			int xh = 1;
			int ls = 0; int lshc = 0; int lskc = 0;
			int sl = 0; int slhc = 0; int slkc = 0;
			int ln = 0; int lnhc = 0; int lnkc = 0;
			int nl = 0; int nlhc = 0; int nlkc = 0;
			StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
			for(FxQzzh vo : list){
				sb.append("<row><xh>").append(xh).append("</xh>");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String zhrq = formatter.format(vo.getZhrq());
				sb.append("<zhrq>").append(zhrq).append("</zhrq>");
				sb.append("<zhzdmc>").append(vo.getZhzdmc()).append("</zhzdmc>");
				sb.append("<zhthkh>").append(vo.getZhthkh()).append("</zhthkh>");
				sb.append("<zhtxkzs>").append(vo.getZhtxkzs()).append("</zhtxkzs>");
				sb.append("<cxmc>").append(vo.getCxmc()).append("</cxmc>");
				sb.append("<zhcph>").append(vo.getZhcph()).append("</zhcph>");
				sb.append("<zhbz>").append(vo.getZhbz()==null?"":vo.getZhbz()).append("</zhbz></row>");
				if(vo.getLdmc().equals("洛嵩段")&&(vo.getZhcx().equals("108001")||vo.getZhcx().equals("108002")||vo.getZhcx().equals("108003")||vo.getZhcx().equals("108004"))){
					ls++;lskc++;
				}else if(vo.getLdmc().equals("洛嵩段")&&(vo.getZhcx().equals("108005")||vo.getZhcx().equals("108006")||vo.getZhcx().equals("108007")||vo.getZhcx().equals("108008")||vo.getZhcx().equals("108009")||vo.getZhcx().equals("108010"))){
					ls++;lshc++;
				}else if(vo.getLdmc().equals("嵩栾段")&&(vo.getZhcx().equals("108001")||vo.getZhcx().equals("108002")||vo.getZhcx().equals("108003")||vo.getZhcx().equals("108004"))){
					sl++;slkc++;
				}else if(vo.getLdmc().equals("嵩栾段")&&(vo.getZhcx().equals("108005")||vo.getZhcx().equals("108006")||vo.getZhcx().equals("108007")||vo.getZhcx().equals("108008")||vo.getZhcx().equals("108009")||vo.getZhcx().equals("108010"))){
					sl++;slhc++;
				}else if(vo.getLdmc().equals("洛宁段")&&(vo.getZhcx().equals("108001")||vo.getZhcx().equals("108002")||vo.getZhcx().equals("108003")||vo.getZhcx().equals("108004"))){
					ln++;lnkc++;
				}else if(vo.getLdmc().equals("洛宁段")&&(vo.getZhcx().equals("108005")||vo.getZhcx().equals("108006")||vo.getZhcx().equals("108007")||vo.getZhcx().equals("108008")||vo.getZhcx().equals("108009")||vo.getZhcx().equals("108010"))){
					ln++;lnhc++;
				}else if(vo.getLdmc().equals("宁卢段")&&(vo.getZhcx().equals("108001")||vo.getZhcx().equals("108002")||vo.getZhcx().equals("108003")||vo.getZhcx().equals("108004"))){
					nl++;nlkc++;
				}else if(vo.getLdmc().equals("宁卢段")&&(vo.getZhcx().equals("108005")||vo.getZhcx().equals("108006")||vo.getZhcx().equals("108007")||vo.getZhcx().equals("108008")||vo.getZhcx().equals("108009")||vo.getZhcx().equals("108010"))){
					nl++;nlhc++;
				}
				xh++;
			}
			
			sb.append("</xml><_grparam>");
			sb.append("<st>").append(st+"份").append("</st>");
			sb.append("<ls>").append(ls).append("</ls>");
			sb.append("<sl>").append(sl).append("</sl>");
			sb.append("<ln>").append(ln).append("</ln>");
			sb.append("<nl>").append(nl).append("</nl>");
			sb.append("<lskc>").append(lskc).append("</lskc>");
			sb.append("<lshc>").append(lshc).append("</lshc>");
			sb.append("<slkc>").append(slkc).append("</slkc>");
			sb.append("<slhc>").append(slhc).append("</slhc>");
			sb.append("<lnkc>").append(lnkc).append("</lnkc>");
			sb.append("<lnhc>").append(lnhc).append("</lnhc>");
			sb.append("<nlkc>").append(nlkc).append("</nlkc>");
			sb.append("<nlhc>").append(nlhc).append("</nlhc></_grparam></report>");
			
			ResponseUtils.renderXml(rsp, sb.toString());
		}
		
		//通行费车流量排名表首页
		@RequestMapping("13txfcllpmbIndex")
		public String txfcllpmbIndex(Model model){
			//上月
					DateTime dt = new DateTime();
					String st = dt.plusMonths(-1).toString("yyyy年MM月");
					model.addAttribute("st", st);
			return "report/13txfcllpmbIndex";
		}
		
		//通行费车流量排名表
		@RequestMapping("13txfcllpmbData")
		public void txfcllpmbData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
			
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			String sd =st.replace("年", "").replace("月", "");
			if(StringUtils.isNoneBlank(sd)){
				map.put("st",sd);
			}
			
			List<txfcllpmbDataVO> txfList = lyReportService.getTxfpmbData(map);
			List<txfcllpmbDataVO> cllList = lyReportService.getcllpmbData(map);
			int txfxh = 1;
			int cllxh = 1;
			StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
			for(int i=0;i<(txfList.size());i++){
				sb.append("<row><txfxh>").append(txfxh).append("</txfxh>");
				sb.append("<txffullname>").append(txfList.get(i).getTxffullname()).append("</txffullname>");
				sb.append("<txfsr>").append(txfList.get(i).getTxfsr()).append("</txfsr>");
				sb.append("<cllxh>").append(cllxh).append("</cllxh>");
				sb.append("<cllfullname>").append(cllList.get(i).getCllfullname()).append("</cllfullname>");
				sb.append("<cll>").append(cllList.get(i).getCll()).append("</cll></row>");
				txfxh++;
				cllxh++;
			}
			
			sb.append("</xml><_grparam>");
			sb.append("<st>").append(st).append("</st></_grparam></report>");
			
			ResponseUtils.renderXml(rsp, sb.toString());
		}
			
		//ic卡使用情况统计表首页
		@RequestMapping("14icksyqktjbIndex")
		public String ticksyqktjbIndex(Model model){
			//上月
			DateTime dt = new DateTime();
			String st = dt.plusMonths(-1).toString("yyyy年MM月");
			model.addAttribute("st", st);
			return "report/14icksyqktjbIndex";
		}
		
		//ic卡使用情况统计表
		@RequestMapping("14icksyqktjbData")
		public void icksyqktjbData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
			
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			String sd =st.replace("年", "").replace("月", "");
			if(StringUtils.isNoneBlank(sd)){
				map.put("st",sd);
			}
			List<IcksyqktjbDataVO> list = lyReportService.getIcksyqktjbData(map);
			StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
			for(IcksyqktjbDataVO vo : list){
				sb.append("<row><zdmc>").append(vo.getZdmc()==null?"":vo.getZdmc()).append("</zdmc>");
				sb.append("<jy>").append(vo.getJy()==null?"0":vo.getJy()).append("</jy>");
				sb.append("<fk>").append(vo.getFk()==null?"0":vo.getFk()).append("</fk>");
				sb.append("<hs>").append(vo.getHs()==null?"0":vo.getHs()).append("</hs>");
				sb.append("<bz>").append(vo.getBz()==null?"":vo.getBz()).append("</bz></row>");
			}
			sb.append("</xml><_grparam>");
			sb.append("<st>").append(st).append("</st></_grparam></report>");
			
			ResponseUtils.renderXml(rsp, sb.toString());
		}
	
		
		/**
		 * 绿通种类首页
		 * @param model
		 * @return
		 */
				@RequestMapping("ltzlIndex")
				public String ltzlIndex(Model model){
					//上月
					DateTime dt = new DateTime();
					String st = dt.plusMonths(-1).toString("yyyy年MM月");
					model.addAttribute("st", st);
					return "report/ltzlIndex";
				}
				
				/**
				 * 绿通种类数据
				 * @param rsp
				 * @param st
				 * @param orgId
				 * @param deptId
				 */
				@RequestMapping("ltzlData")
				public void ltzlData(HttpServletResponse rsp,
					@RequestParam(value="st",required=false)String st,
					@RequestParam(value="orgId",required=false)String orgId,
					@RequestParam(value="deptId",required=false)String deptId){
					
					HashMap<String, Object> map = new HashMap<String, Object>(6);
					String sd =st.replace("年", "").replace("月", "");
					if(StringUtils.isNoneBlank(sd)){
						map.put("st",sd);
					}
					List<FxLtsj> list = lyReportService.getLtzlData(map);
					StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
					for(FxLtsj vo : list){
						sb.append("<row><zdmc>").append(vo.getZdmc()==null?"":vo.getZdmc()).append("</zdmc>");
						sb.append("<xxsc>").append(vo.getXxsc()==null?"0":vo.getXxsc()).append("</xxsc>");
						sb.append("<xxsg>").append(vo.getXxsg()==null?"0":vo.getXxsg()).append("</xxsg>");
						sb.append("<xhscp>").append(vo.getXhscp()==null?"0":vo.getXhscp()).append("</xhscp>");
						sb.append("<hdxq>").append(vo.getHdxq()==null?"":vo.getHdxq()).append("</hdxq>");
						sb.append("<hdxq>").append(vo.getHdxq()==null?"":vo.getHdxq()).append("</hdxq>");
						sb.append("<xxdrdn>").append(vo.getXxdrdn()==null?"":vo.getXxdrdn()).append("</xxdrdn>");
						sb.append("<syhls>").append(vo.getSyhls()==null?"":vo.getSyhls()).append("</syhls>");
						sb.append("<hj>").append(vo.getZlhj()==null?"":vo.getZlhj()).append("</hj>");
						sb.append("</row>");
					}
					sb.append("</xml><_grparam>");
					sb.append("<st>").append(st).append("</st></_grparam></report>");
					
					ResponseUtils.renderXml(rsp, sb.toString());
				}
}

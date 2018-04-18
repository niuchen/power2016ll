package com.power.report.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.power.report.entity.LdtxfrDataVO;
import com.power.report.entity.LtmstxfVO;
import com.power.report.entity.TxfcllrDataVO;
import com.power.report.entity.TxfsrpjVO;
import com.power.report.entity.txfchsrhzbVO;
import com.power.report.entity.txfcllpmbDataVO;
import com.power.report.entity.txfjcllmxhzDataVO;
import com.power.report.service.LeaderReportService;
import com.power.report.service.LyReportService;
import com.power.report.service.YhReportService;

@Controller
@RequestMapping("yhreport")

public class YhReportController {
	private final static Logger log = LoggerFactory.getLogger(ReportController.class);
	@Autowired
	private AppService appService;
	@Autowired
	private YhReportService yhReportService;
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
	
	private static Map<String,String> zdMap;
	static{
		zdMap = Maps.newHashMap();
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
	@RequestMapping("1txfcllrIndex")
	public String txfcllrIndex(Model model){
		//上月
				DateTime dt = new DateTime();
				String st = dt.toString("yyyy年MM月dd日");
				model.addAttribute("st", st);
		return "report/1txfcllrIndex";
	}

	
	
	
	@RequestMapping("1txfcllrData")
	public void txfcllrData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,	
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		
		if(StringUtils.isNoneBlank(st)){
			map.put("st",st);
		}
		
		indexService.setOrgDeptParam(orgId,deptId,map);
		List<TxfcllrDataVO> list = yhReportService.getTxfcllrData(map);
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
			for(TxfcllrDataVO vo:list){
				sb.append("<row>");	
				sb.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
				sb.append("<yhsjje1>").append(vo.getYhsjje1()).append("</yhsjje1>");
				sb.append("<etcsjje1>").append(vo.getEtcsjje1()).append("</etcsjje1>");
				sb.append("<ydydxj1>").append(vo.getYdydxj1()).append("</ydydxj1>");
				sb.append("<xj1>").append(vo.getXj1()).append("</xj1>");
				sb.append("<yhsjje2>").append(vo.getYhsjje2()).append("</yhsjje2>");
				sb.append("<etcsjje2>").append(vo.getEtcsjje2()).append("</etcsjje2>");
				sb.append("<ydydxj2>").append(vo.getYdydxj2()).append("</ydydxj2>");
				sb.append("<xj2>").append(vo.getXj2()).append("</xj2>");
				sb.append("<yhsjjehj>").append(vo.getYhsjje1().add(vo.getYhsjje2())).append("</yhsjjehj>");
				sb.append("<etcsjjehj>").append(vo.getEtcsjje1().add(vo.getEtcsjje2())).append("</etcsjjehj>");
				sb.append("<ydydhj>").append(vo.getYdydxj1().add(vo.getYdydxj2())).append("</ydydhj>");
				sb.append("<xj>").append(vo.getXj1().add(vo.getXj2())).append("</xj>");
				sb.append("<rkllzj>").append(vo.getRkllzj()).append("</rkllzj>");
				sb.append("<ckllzj>").append(vo.getCkllzj()).append("</ckllzj>");
				sb.append("<crkllhj>").append(vo.getCrkllhj()).append("</crkllhj>");
				sb.append("</row>");	
			}
			sb.append("</xml><_grparam>");
			String nd=st.substring(0,4);
			String rq = st.substring(st.length()-2);
			String ny = st.substring(0,st.length()-3);
			String yf = ny.substring(ny.length()-2);
			sb.append("<rq>").append(nd+"年"+yf+"月"+rq+"日").append("</rq></_grparam></report>");
			
			ResponseUtils.renderXml(rsp, sb.toString());
	}	
	
	
	/**
	 * 报表分析说明页面跳转
	 */
	@RequestMapping("2txfcllyIndex")
	public String txfcllyIndex(Model model){
		//上月
				DateTime dt = new DateTime();
				String st = dt.toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/2txfcllyIndex";
	}
	
	
	@RequestMapping("2txfcllyData")
	public void txfcllyData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
			//
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			if(StringUtils.isNoneBlank(st)){
				map.put("st",st);
			}
			if(StringUtils.isNoneBlank(et)){
				map.put("et",et);
			}
		
		indexService.setOrgDeptParam(orgId,deptId,map);
		List<LdtxfrDataVO> list = yhReportService.getLdTxfyData(map);
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
			for(LdtxfrDataVO vo:list){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						//System.err.println(entry.getKey()+"-----"+entry.getValue());
						String zdmc = zdMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"_yhsjjehj", new BigDecimal(ll.get(0)));
						Reflections.setFieldValue(vo, zdmc+"_ydydhj", new BigDecimal(ll.get(1)));
						Reflections.setFieldValue(vo, zdmc+"_etcsjjehj", new BigDecimal(ll.get(2)));
					}
				sb.append("<row>");	
				Date sj = vo.getSfrq();
				SimpleDateFormat formatter = new SimpleDateFormat("d");
			    String dateString = formatter.format(sj);

				sb.append("<rq>").append(dateString).append("</rq>");
				
				sb.append("<yhsjje1>").append((vo.getLlz_yhsjjehj()).add(vo.getYcxz_yhsjjehj()).add(vo.getSxcyjjqz_yhsjjehj()).add(vo.getSxz_yhsjjehj()).add(vo.getJxz_yhsjjehj()).add(vo.getJlsz_yhsjjehj()).add(vo.getCdgz_yhsjjehj()).add(vo.getLcz_yhsjjehj())).append("</yhsjje1>");
				sb.append("<etcsjje1>").append((vo.getLlz_etcsjjehj()).add(vo.getYcxz_etcsjjehj()).add(vo.getSxcyjjqz_etcsjjehj()).add(vo.getSxz_etcsjjehj()).add(vo.getJxz_etcsjjehj()).add(vo.getJlsz_etcsjjehj()).add(vo.getCdgz_etcsjjehj()).add(vo.getLcz_etcsjjehj())).append("</etcsjje1>");
				sb.append("<ydydxj1>").append((vo.getLlz_ydydhj()).add(vo.getYcxz_ydydhj()).add(vo.getSxcyjjqz_ydydhj()).add(vo.getSxz_ydydhj()).add(vo.getJxz_ydydhj()).add(vo.getJlsz_ydydhj()).add(vo.getCdgz_ydydhj()).add(vo.getLcz_ydydhj())).append("</ydydxj1>");
				sb.append("<xj1>").append((vo.getLlz_yhsjjehj()).add(vo.getYcxz_yhsjjehj()).add(vo.getSxcyjjqz_yhsjjehj()).add(vo.getSxz_yhsjjehj()).add(vo.getJxz_yhsjjehj()).add(vo.getJlsz_yhsjjehj()).add(vo.getCdgz_yhsjjehj()).add(vo.getLcz_yhsjjehj()).add(vo.getLlz_etcsjjehj()).add(vo.getYcxz_etcsjjehj()).add(vo.getSxcyjjqz_etcsjjehj()).add(vo.getSxz_etcsjjehj()).add(vo.getJxz_etcsjjehj()).add(vo.getJlsz_etcsjjehj()).add(vo.getCdgz_etcsjjehj()).add(vo.getLcz_etcsjjehj()).add(vo.getLlz_ydydhj()).add(vo.getYcxz_ydydhj()).add(vo.getSxcyjjqz_ydydhj()).add(vo.getSxz_ydydhj()).add(vo.getJxz_ydydhj()).add(vo.getJlsz_ydydhj()).add(vo.getCdgz_ydydhj()).add(vo.getLcz_ydydhj())).append("</xj1>");
				
				sb.append("<yhsjje2>").append((vo.getZsz_yhsjjehj()).add(vo.getYyz_yhsjjehj()).add(vo.getYcz_yhsjjehj()).add(vo.getLnz_yhsjjehj()).add(vo.getLnxz_yhsjjehj()).add(vo.getCsz_yhsjjehj()).add(vo.getSgz_yhsjjehj())).append("</yhsjje2>");
				sb.append("<etcsjje2>").append((vo.getZsz_etcsjjehj()).add(vo.getYyz_etcsjjehj()).add(vo.getYcz_etcsjjehj()).add(vo.getLnz_etcsjjehj()).add(vo.getLnxz_etcsjjehj()).add(vo.getCsz_etcsjjehj()).add(vo.getSgz_etcsjjehj())).append("</etcsjje2>");
				sb.append("<ydydxj2>").append((vo.getZsz_ydydhj()).add(vo.getYyz_ydydhj()).add(vo.getYcz_ydydhj()).add(vo.getLnz_ydydhj()).add(vo.getLnxz_ydydhj()).add(vo.getCsz_ydydhj()).add(vo.getSgz_ydydhj())).append("</ydydxj2>");
				sb.append("<xj2>").append((vo.getZsz_yhsjjehj()).add(vo.getYyz_yhsjjehj()).add(vo.getYcz_yhsjjehj()).add(vo.getLnz_yhsjjehj()).add(vo.getLnxz_yhsjjehj()).add(vo.getCsz_yhsjjehj()).add(vo.getSgz_yhsjjehj()).add(vo.getZsz_etcsjjehj()).add(vo.getYyz_etcsjjehj()).add(vo.getYcz_etcsjjehj()).add(vo.getLnz_etcsjjehj()).add(vo.getLnxz_etcsjjehj()).add(vo.getCsz_etcsjjehj()).add(vo.getSgz_etcsjjehj()).add(vo.getZsz_ydydhj()).add(vo.getYyz_ydydhj()).add(vo.getYcz_ydydhj()).add(vo.getLnz_ydydhj()).add(vo.getLnxz_ydydhj()).add(vo.getCsz_ydydhj()).add(vo.getSgz_ydydhj())).append("</xj2>");
				
				sb.append("<yhsjjehj>").append((vo.getLlz_yhsjjehj()).add(vo.getYcxz_yhsjjehj()).add(vo.getSxcyjjqz_yhsjjehj()).add(vo.getSxz_yhsjjehj()).add(vo.getJxz_yhsjjehj()).add(vo.getJlsz_yhsjjehj()).add(vo.getCdgz_yhsjjehj()).add(vo.getLcz_yhsjjehj()).add(vo.getZsz_yhsjjehj()).add(vo.getYyz_yhsjjehj()).add(vo.getYcz_yhsjjehj()).add(vo.getLnz_yhsjjehj()).add(vo.getLnxz_yhsjjehj()).add(vo.getCsz_yhsjjehj()).add(vo.getSgz_yhsjjehj())).append("</yhsjjehj>");
				sb.append("<etcsjjehj>").append((vo.getLlz_etcsjjehj()).add(vo.getYcxz_etcsjjehj()).add(vo.getSxcyjjqz_etcsjjehj()).add(vo.getSxz_etcsjjehj()).add(vo.getJxz_etcsjjehj()).add(vo.getJlsz_etcsjjehj()).add(vo.getCdgz_etcsjjehj()).add(vo.getLcz_etcsjjehj()).add(vo.getZsz_etcsjjehj()).add(vo.getYyz_etcsjjehj()).add(vo.getYcz_etcsjjehj()).add(vo.getLnz_etcsjjehj()).add(vo.getLnxz_etcsjjehj()).add(vo.getCsz_etcsjjehj()).add(vo.getSgz_etcsjjehj())).append("</etcsjjehj>");
				sb.append("<ydydhj>").append((vo.getLlz_ydydhj()).add(vo.getYcxz_ydydhj()).add(vo.getSxcyjjqz_ydydhj()).add(vo.getSxz_ydydhj()).add(vo.getJxz_ydydhj()).add(vo.getJlsz_ydydhj()).add(vo.getCdgz_ydydhj()).add(vo.getLcz_ydydhj()).add(vo.getZsz_ydydhj()).add(vo.getYyz_ydydhj()).add(vo.getYcz_ydydhj()).add(vo.getLnz_ydydhj()).add(vo.getLnxz_ydydhj()).add(vo.getCsz_ydydhj()).add(vo.getSgz_ydydhj())).append("</ydydhj>");
				sb.append("<xj>").append((vo.getLlz_yhsjjehj()).add(vo.getYcxz_yhsjjehj()).add(vo.getSxcyjjqz_yhsjjehj()).add(vo.getSxz_yhsjjehj()).add(vo.getJxz_yhsjjehj()).add(vo.getJlsz_yhsjjehj()).add(vo.getCdgz_yhsjjehj()).add(vo.getLcz_yhsjjehj()).add(vo.getZsz_yhsjjehj()).add(vo.getYyz_yhsjjehj()).add(vo.getYcz_yhsjjehj()).add(vo.getLnz_yhsjjehj()).add(vo.getLnxz_yhsjjehj()).add(vo.getCsz_yhsjjehj()).add(vo.getSgz_yhsjjehj()).add(vo.getLlz_etcsjjehj()).add(vo.getYcxz_etcsjjehj()).add(vo.getSxcyjjqz_etcsjjehj()).add(vo.getSxz_etcsjjehj()).add(vo.getJxz_etcsjjehj()).add(vo.getJlsz_etcsjjehj()).add(vo.getCdgz_etcsjjehj()).add(vo.getLcz_etcsjjehj()).add(vo.getZsz_etcsjjehj()).add(vo.getYyz_etcsjjehj()).add(vo.getYcz_etcsjjehj()).add(vo.getLnz_etcsjjehj()).add(vo.getLnxz_etcsjjehj()).add(vo.getCsz_etcsjjehj()).add(vo.getSgz_etcsjjehj()).add(vo.getLlz_ydydhj()).add(vo.getYcxz_ydydhj()).add(vo.getSxcyjjqz_ydydhj()).add(vo.getSxz_ydydhj()).add(vo.getJxz_ydydhj()).add(vo.getJlsz_ydydhj()).add(vo.getCdgz_ydydhj()).add(vo.getLcz_ydydhj()).add(vo.getZsz_ydydhj()).add(vo.getYyz_ydydhj()).add(vo.getYcz_ydydhj()).add(vo.getLnz_ydydhj()).add(vo.getLnxz_ydydhj()).add(vo.getCsz_ydydhj()).add(vo.getSgz_ydydhj())).append("</xj>");
				
				sb.append("</row>");	
			}
				}
			sb.append("</xml><_grparam>");
			String r = st.substring(0, 7);
			String rq = r.replace("-", "年");
			sb.append("<rq>").append(rq+"月").append("</rq></_grparam></report>");
			ResponseUtils.renderXml(rsp, sb.toString());
	}	
	/**
	 * 报表分析说明页面跳转
	 */
	@RequestMapping("3yhtxfrIndex")
	public String yhtxfrIndex(Model model){
		
				DateTime dt = new DateTime();
				String st = dt.toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/3yhtxfrIndex";
	}
	
	@RequestMapping("3yhtxfyData")
	public void yhtxfyData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
			//
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			if(StringUtils.isNoneBlank(st)){
				map.put("st",st);
			}
			if(StringUtils.isNoneBlank(et)){
				map.put("et",et);
			}
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){
			map.put("deptid",user.getDeptId());
		}
		
	
	indexService.setOrgDeptParam(orgId,deptId,map);
	List<txfjcllmxhzDataVO> list = yhReportService.getYhTxfyData(map);
		//报表数据封装
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
	for(txfjcllmxhzDataVO vo : list){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row>");
			
			Date sj = vo.getSfrq();
			SimpleDateFormat formatter = new SimpleDateFormat("d");
		    String dateString = formatter.format(sj);

			detail.append("<sj>").append(dateString).append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<jjqz>").append(vo.getSxcyjjqz_je()).append("</jjqz>");
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
			detail.append("<xj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</xj></row>");
		}
	
	}
	
		sb.append(detail).append("</xml><_grparam>");
		String r = st.substring(0, 7);
		String rq = r.replace("-", "年");
		sb.append("<rq>").append(rq+"月").append("</rq></_grparam></report>");
			ResponseUtils.renderXml(rsp, sb.toString());
	}	
	
	
	/**
	 * 报表分析说明页面跳转
	 */
	@RequestMapping("4ydtxfrIndex")
	public String ydtxfrIndex(Model model){
		
				DateTime dt = new DateTime();
				String st = dt.toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/4ydtxfrIndex";
	}
	
	@RequestMapping("4ydtxfyData")
	public void ydtxfyData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
			//
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			if(StringUtils.isNoneBlank(st)){
				map.put("st",st);
			}
			if(StringUtils.isNoneBlank(et)){
				map.put("et",et);
			}
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){
			map.put("deptid",user.getDeptId());
		}
		
	
	indexService.setOrgDeptParam(orgId,deptId,map);
	List<txfjcllmxhzDataVO> list = yhReportService.getYdTxfyData(map);
	//报表数据封装
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
	for(txfjcllmxhzDataVO vo : list){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row>");
			
			Date sj = vo.getSfrq();
			SimpleDateFormat formatter = new SimpleDateFormat("d");
		    String dateString = formatter.format(sj);

			detail.append("<sj>").append(dateString).append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<jjqz>").append(vo.getSxcyjjqz_je()).append("</jjqz>");
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
			detail.append("<xj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</xj></row>");
		}
	
	}
	
		sb.append(detail).append("</xml><_grparam>");
		String r = st.substring(0, 7);
		String rq = r.replace("-", "年");
		sb.append("<rq>").append(rq+"月").append("</rq></_grparam></report>");
			ResponseUtils.renderXml(rsp, sb.toString());
	}	
	
	
	
	/**
	 * 报表分析说明页面跳转
	 */
	@RequestMapping("5etctxfrIndex")
	public String etctxfrIndex(Model model){
		
				DateTime dt = new DateTime();
				String st = dt.toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/5etctxfrIndex";
	}
	
	@RequestMapping("5etctxfyData")
	public void etctxfyData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
			//
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			if(StringUtils.isNoneBlank(st)){
				map.put("st",st);
			}
			if(StringUtils.isNoneBlank(et)){
				map.put("et",et);
			}
		AuthUserVO user = UserUtils.getAuthUser();
		if(StringUtils.isNoneBlank(user.getDeptId())){
			map.put("deptid",user.getDeptId());
		}
		
	
	indexService.setOrgDeptParam(orgId,deptId,map);
	List<txfjcllmxhzDataVO> list = yhReportService.getEtcTxfyData(map);
	//报表数据封装
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
	for(txfjcllmxhzDataVO vo : list){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row>");
			
			Date sj = vo.getSfrq();
			SimpleDateFormat formatter = new SimpleDateFormat("d");
		    String dateString = formatter.format(sj);

			detail.append("<sj>").append(dateString).append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<jjqz>").append(vo.getSxcyjjqz_je()).append("</jjqz>");
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
			detail.append("<xj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</xj></row>");
		}
	
	}
	
		sb.append(detail).append("</xml><_grparam>");
		String r = st.substring(0, 7);
		String rq = r.replace("-", "年");
		sb.append("<rq>").append(rq+"月").append("</rq></_grparam></report>");
			ResponseUtils.renderXml(rsp, sb.toString());
	}	
	
	
	/**
	 * 报表分析说明页面跳转
	 */
	@RequestMapping("6hztxfrIndex")
	public String hztxfrIndex(Model model){
		
				DateTime dt = new DateTime();
				String st = dt.toString("yyyy年MM月");
				model.addAttribute("st", st);
		return "report/6hztxfrIndex";
	}
	
	@RequestMapping("6hztxfyData")
	public void hztxfyData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
			//
			HashMap<String, Object> map = new HashMap<String, Object>(6);
			if(StringUtils.isNoneBlank(st)){
				map.put("st",st);
			}
			if(StringUtils.isNoneBlank(et)){
				map.put("et",et);
			}
			AuthUserVO user = UserUtils.getAuthUser();
			if(StringUtils.isNoneBlank(user.getDeptId())){
				map.put("deptid",user.getDeptId());
			}
	
	indexService.setOrgDeptParam(orgId,deptId,map);
	List<txfjcllmxhzDataVO> list = yhReportService.getHzTxfyData(map);
		//报表数据封装
	StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
	StringBuffer detail = new StringBuffer();
	for(txfjcllmxhzDataVO vo : list){
		if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
				//System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = zdMap.get(entry.getKey());
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"_je", new BigDecimal(ll.get(0)));
			}
			detail.append("<row>");
			
			Date sj = vo.getSfrq();
			SimpleDateFormat formatter = new SimpleDateFormat("d");
		    String dateString = formatter.format(sj);

			detail.append("<sj>").append(dateString).append("</sj>");
			
			detail.append("<llz>").append(vo.getLlz_je()).append("</llz>");
			detail.append("<ycxz>").append(vo.getYcxz_je()).append("</ycxz>");
			detail.append("<jjqz>").append(vo.getSxcyjjqz_je()).append("</jjqz>");
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
			detail.append("<xj>").append((vo.getLlz_je()).add(vo.getYcxz_je()).add(vo.getSxcyjjqz_je()).add(vo.getSxz_je()).add(vo.getJxz_je()).add(vo.getJlsz_je()).add(vo.getCdgz_je()).add(vo.getLcz_je()).add(vo.getZsz_je()).add(vo.getYyz_je()).add(vo.getYcz_je()).add(vo.getLnz_je()).add(vo.getLnxz_je()).add(vo.getCsz_je()).add(vo.getSgz_je())).append("</xj></row>");
		}
	
	}
	
		sb.append(detail).append("</xml><_grparam>");
		String r = st.substring(0, 7);
		String rq = r.replace("-", "年");
		sb.append("<rq>").append(rq+"月").append("</rq></_grparam></report>");
			ResponseUtils.renderXml(rsp, sb.toString());
	}	
}

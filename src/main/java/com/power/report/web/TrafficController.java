package com.power.report.web;
import java.math.BigDecimal;
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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.power.app.entity.FyzbVO;
import com.power.app.service.AppService;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.KeyValue;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.FxChsjYb;
import com.power.common.entity.FxRcsj;
import com.power.common.springmvc.ExtReturn;
import com.power.common.springmvc.ResponseUtils;
import com.power.common.utils.Reflections;
import com.power.index.service.IndexService;
import com.power.report.entity.JkrbDataVO;
import com.power.report.entity.JtlybVO;
import com.power.report.entity.KhcllfyListVO;
import com.power.report.entity.LtmstxfVO;
import com.power.report.entity.TxfsrVO;
import com.power.report.service.ReportService;

/**
 * 项目名称：power2016 <br>
 * 类名称：TrafficController <br>
 * 创建时间：2017-4-8 下午5:57:05 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Controller
@RequestMapping("report")
public class TrafficController {
	
	private final static Logger log = LoggerFactory.getLogger(TrafficController.class);
	@Autowired
	private AppService appService;
	@Autowired
	private ReportService reportService;
	@Autowired
	private IndexService indexService;
	
	/**
	 * 交通量月报
	 * @param st
	 * @param orgId
	 * @param deptId
	 * @return
	 */
	@RequestMapping("jtlybData")
	public void jtlybData(@RequestParam(value="st",required=false)String st,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId,
			HttpServletResponse rsp){
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		String sd =st.replace("年", "").replace("月", "");
		if(StringUtils.isNoneBlank(sd)){
			map.put("st",sd);
		}
		indexService.setOrgDeptParam(orgId,deptId,map);
		List<JtlybVO> list = reportService.getJtlybData(map);
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
		int i=0;
		DateTime dt = new DateTime(st.replace("年", "-").replace("月", ""));
		int mean = dt.dayOfMonth().getMaximumValue();
		for(JtlybVO obj:list){
			i++;
			sb.append("<row><xh>").append(i).append("</xh>");
			sb.append("<fullname>").append(obj.getFullname()).append("</fullname>");
			sb.append("<rjjtlr>").append(obj.getYjtlr()/mean).append("</rjjtlr>");
			sb.append("<rjjtlc>").append(obj.getYjtlc()/mean).append("</rjjtlc>");
			sb.append("<yjtlr>").append(obj.getYjtlr()).append("</yjtlr>");
			sb.append("<yjtlc>").append(obj.getYjtlc()).append("</yjtlc>");
			sb.append("<kcr1>").append(obj.getKcr1()).append("</kcr1>");
			sb.append("<kcc1>").append(obj.getKcc1()).append("</kcc1>");
			sb.append("<kcr2>").append(obj.getKcr2()).append("</kcr2>");
			sb.append("<kcc2>").append(obj.getKcc2()).append("</kcc2>");
			sb.append("<kcr3>").append(obj.getKcr3()).append("</kcr3>");
			sb.append("<kcc3>").append(obj.getKcc3()).append("</kcc3>");
			sb.append("<kcr4>").append(obj.getKcr4()).append("</kcr4>");
			sb.append("<kcc4>").append(obj.getKcc4()).append("</kcc4>");
			sb.append("<hcr1>").append(obj.getHcr1()).append("</hcr1>");
			sb.append("<hcc1>").append(obj.getHcc1()).append("</hcc1>");
			sb.append("<hcr2>").append(obj.getHcr2()).append("</hcr2>");
			sb.append("<hcc2>").append(obj.getHcc2()).append("</hcc2>");
			sb.append("<hcr3>").append(obj.getHcr3()).append("</hcr3>");
			sb.append("<hcc3>").append(obj.getHcc3()).append("</hcc3>");
			sb.append("<hcr4>").append(obj.getHcr4()).append("</hcr4>");
			sb.append("<hcc4>").append(obj.getHcc4()).append("</hcc4>");
			sb.append("<hcr5>").append(obj.getHcr5()).append("</hcr5>");
			sb.append("<hcc5>").append(obj.getHcc5()).append("</hcc5>");
			sb.append("<hcr6>").append(obj.getHcr6()).append("</hcr6>");
			sb.append("<hcc6>").append(obj.getHcc6()).append("</hcc6>");
			sb.append("<mzr>").append(obj.getMzr()).append("</mzr>");
			sb.append("<mzc>").append(obj.getMzc()).append("</mzc>");
			BigDecimal mzl = BigDecimal.ZERO;
			BigDecimal mz = (new BigDecimal(obj.getMzr()).add(new BigDecimal(obj.getMzc()))).multiply(new BigDecimal(100));
			BigDecimal yjtl = new BigDecimal(obj.getYjtlr()).add(new BigDecimal(obj.getYjtlc()));
			if(0!=yjtl.compareTo(BigDecimal.ZERO)){
				mzl = mz.divide(yjtl,2, BigDecimal.ROUND_HALF_UP);
			}
			sb.append("<mzl>").append(mzl+"%").append("</mzl></row>");
		}
		sb.append("</xml><_grparam>");
		sb.append("<dw>").append(map.get("title")).append("</dw>");
		sb.append("<rq>").append(st+"份").append("</rq></_grparam></report>");
		System.err.println("---"+sb.toString());
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	/**
	 * 交通量月报表
	 * @return
	 */
	@RequestMapping("jtlybIndex")
	public String jtlybIndex(Model model) {
		// 上月
		DateTime dt = new DateTime();
		String st = dt.plusMonths(-1).toString("yyyy年MM月");
		model.addAttribute("st", st);
		return "report/jtlybIndex";
	}
	
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
	 * 通行费收入月报
	 * @param st
	 * @return
	 */
	@RequestMapping("txfsrData")
	public void txfsrData(HttpServletResponse rsp,
			@RequestParam(value = "st", required = false) String st) {
		// 处理时间
		String month = st.replace("年", "-").replace("月", "");
		DateTime dt = new DateTime(month);
		int year = dt.getYear();
		String yearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(-1);
		String preYearMonth = dt.toString("yyyyMM");
		dt = dt.plusYears(1).plusMonths(-1);
		String preMonth = dt.toString("yyyyMM");
		// 将时间map到xml
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if (StringUtils.isNoneBlank(String.valueOf(year))) {
			map.put("year", year);
		}
		if (StringUtils.isNoneBlank(yearMonth)) {
			map.put("yearMonth", yearMonth);
		}
		if (StringUtils.isNoneBlank(preYearMonth)) {
			map.put("preYearMonth", preYearMonth);
		}
		if (StringUtils.isNoneBlank(preMonth)) {
			map.put("preMonth", preMonth);
		}
		List<TxfsrVO> List = reportService.editTxfsrData(map);
		StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
		DateTime mt = new DateTime(st.replace("年", "-").replace("月", ""));
		BigDecimal mean = new BigDecimal(mt.dayOfMonth().getMaximumValue());
		BigDecimal cqhj = BigDecimal.ZERO;
		BigDecimal chhj = BigDecimal.ZERO;
		BigDecimal sychhj = BigDecimal.ZERO;
		BigDecimal qnchhj = BigDecimal.ZERO;
		BigDecimal nljchhj = BigDecimal.ZERO;
		sb.append("<row><fullname>合计</fullname>");
		for (TxfsrVO vo : List) {
			if (vo.getFlag() != null) {
				if (vo.getFlag() == 1) {
					cqhj = cqhj.add(vo.getCqsr());
					chhj = chhj.add(vo.getChsr());
					sychhj = sychhj.add(vo.getSychsr());
					qnchhj = qnchhj.add(vo.getQnchsr());
					nljchhj = nljchhj.add(vo.getBnljchsr());
				}
			}
		}
		sb.append("<cqsr>").append(cqhj).append("</cqsr>");
		sb.append("<chsr>").append(chhj).append("</chsr>");
		sb.append("<sychsr>").append(sychhj).append("</sychsr>");
		sb.append("<jsychsrb>").append(getHuanbi(chhj, sychhj)+"%").append("</jsychsrb>");
		sb.append("<jqntqchsr>").append(qnchhj).append("</jqntqchsr>");
		sb.append("<jqntqchsrb>").append(getHuanbi(chhj, qnchhj)+"%").append("</jqntqchsrb>");
		sb.append("<rjsr>").append(chhj.divide(mean,2, BigDecimal.ROUND_HALF_UP)).append("</rjsr>");
		sb.append("<nljsr>").append(nljchhj).append("</nljsr></row>");
		for (TxfsrVO vo : List) {
			sb.append("<row><fullname>").append(vo.getFullname()).append("</fullname>");
			sb.append("<cqsr>").append(vo.getCqsr()).append("</cqsr>");
			sb.append("<chsr>").append(vo.getChsr()).append("</chsr>");
			sb.append("<sychsr>").append(vo.getSychsr()).append("</sychsr>");
			sb.append("<jsychsrb>").append(getHuanbi(vo.getChsr(), vo.getSychsr())+"%").append("</jsychsrb>");
			sb.append("<jqntqchsr>").append(vo.getQnchsr()).append("</jqntqchsr>");
			sb.append("<jqntqchsrb>").append(getHuanbi(vo.getChsr(), vo.getQnchsr())+"%").append("</jqntqchsrb>");
			sb.append("<rjsr>").append(vo.getChsr().divide(mean,2, BigDecimal.ROUND_HALF_UP)).append("</rjsr>");
			sb.append("<nljsr>").append(vo.getBnljchsr()).append("</nljsr></row>");
		}
		sb.append("</xml><_grparam>");
		sb.append("<rq>").append(st+"份").append("</rq></_grparam></report>");
		ResponseUtils.renderXml(rsp, sb.toString());
	}

	/**
	 * 通行费收入月报表首页
	 * @return
	 */
	@RequestMapping("txfsrIndex")
	public String txfsrIndex(Model model){
		//上月
		DateTime dt = new DateTime();
		String st = dt.plusMonths(-1).toString("yyyy年MM月");
		model.addAttribute("st", st);
		return "report/txfsrIndex";
	}
	
	//站点固定
		private static Map<String,String> ldMap;
		static{
			ldMap = Maps.newHashMap();
			ldMap.put("登封东站", "dfd_");
			ldMap.put("登封西站", "dfx_");
			ldMap.put("吕店站", "ld_");
			ldMap.put("君召站", "jz_");
			ldMap.put("洛阳新区站","lyxq_");
			ldMap.put("洛阳涧西站","lyjx_");
			ldMap.put("高新区站","lygxq_");
			ldMap.put("伊川北站","ycb_");
			ldMap.put("济源东站","jyd_");
			ldMap.put("柏香站","bx_");
			ldMap.put("沁阳站","qy_");
			ldMap.put("焦作东站","jzd_");
			ldMap.put("修武站","xw_");
			ldMap.put("修武东站","xwd_");
			ldMap.put("获嘉站","hj_");
			ldMap.put("新乡西站","xxx_");
			ldMap.put("新乡东站","xxd_");
			ldMap.put("吉利孟州站","jlmz_");
			ldMap.put("济源南站","jyn_");
			ldMap.put("王屋山站","wws_");
			ldMap.put("邵原站","sy_");
			ldMap.put("豫晋省界站","yjsj_");
			/*ldMap.put("少洛段小计","sl_");
			ldMap.put("洛阳西南绕城段小计","lyxnrc_");
			ldMap.put("济焦段小计","jj_");
			ldMap.put("焦修段小计","jx_");
			ldMap.put("获新段小计","hx_");
			ldMap.put("济洛段小计","jl_");
			ldMap.put("济邵段小计","js_");*/
		}
	/**
	 * 绿通免收通行费及车流量明细月报表
	 * @param st
	 * @return
	 */
	@RequestMapping("ltmstxfData")
	public void ltmstxfData(HttpServletResponse rsp,
			@RequestParam(value = "st", required = false) String st) {
		DateTime dt = new DateTime(st.replace("年", "-").replace("月", ""));
		int mean = dt.dayOfMonth().getMaximumValue();
		String month = st.substring(5,8);
		String yearMonth = dt.toString("yyyyMM");
		Integer year = dt.getYear();
		
		String[] StartTime = {yearMonth+"01",yearMonth+"06",yearMonth+"11",yearMonth+"16",yearMonth+"21",yearMonth+"26"};
		String[] EndTime = {yearMonth+"05",yearMonth+"10",yearMonth+"15",yearMonth+"20",yearMonth+"25",yearMonth+mean};
		
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(null!=year){
			map.put("year",year);
		}
		if(StringUtils.isNoneBlank(yearMonth)){
			map.put("yearMonth",yearMonth);
		}
		List<LtmstxfVO> listby = reportService.getLtmstxfbyData(map);
		List<LtmstxfVO> listbn = reportService.getLtmstxfbnData(map);
		List<LtmstxfVO> listzkt = reportService.getLtmstxfzktData(map);
		StringBuffer sb=new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><report><xml>");//报表数据
		StringBuffer detail = new StringBuffer();
		for(int i = 0;i < StartTime.length;i++){
			if (StringUtils.isNoneBlank(yearMonth)) {
				map.put("StartTime", StartTime[i]);
				map.put("EndTime", EndTime[i]);
			}
			List<LtmstxfVO> list = reportService.getLtmstxfData(map);
			for(LtmstxfVO vo : list){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						System.err.println(entry.getKey()+"-----"+entry.getValue());
						String zdmc = ldMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"je", new BigDecimal(ll.get(0)));
					}
					detail.append("<row><xm>").append("实际免收通行费收入（元）").append("</xm>");
					detail.append("<fullname>").append(month+StartTime[i].substring(6, 8)+"日至"+month+EndTime[i].substring(6, 8)+"日").append("</fullname>");
					detail.append("<dfd_je>").append(vo.getDfd_je()).append("</dfd_je>");
					detail.append("<dfx_je>").append(vo.getDfx_je()).append("</dfx_je>");
					detail.append("<ld_je>").append(vo.getLd_je()).append("</ld_je>");
					detail.append("<jz_je>").append(vo.getJz_je()).append("</jz_je>");
					detail.append("<lyxq_je>").append(vo.getLyxq_je()).append("</lyxq_je>");
					detail.append("<lyjx_je>").append(vo.getLyjx_je()).append("</lyjx_je>");
					detail.append("<lygxq_je>").append(vo.getLygxq_je()).append("</lygxq_je>");
					detail.append("<ycb_je>").append(vo.getYcb_je()).append("</ycb_je>");
					detail.append("<jyd_je>").append(vo.getJyd_je()).append("</jyd_je>");
					detail.append("<bx_je>").append(vo.getBx_je()).append("</bx_je>");
					detail.append("<qy_je>").append(vo.getQy_je()).append("</qy_je>");
					detail.append("<jzd_je>").append(vo.getJzd_je()).append("</jzd_je>");
					detail.append("<xw_je>").append(vo.getXw_je()).append("</xw_je>");
					detail.append("<xwd_je>").append(vo.getXwd_je()).append("</xwd_je>");
					detail.append("<hj_je>").append(vo.getHj_je()).append("</hj_je>");
					detail.append("<xxx_je>").append(vo.getXxx_je()).append("</xxx_je>");
					detail.append("<xxd_je>").append(vo.getXxd_je()).append("</xxd_je>");
					detail.append("<jlmz_je>").append(vo.getJlmz_je()).append("</jlmz_je>");
					detail.append("<jyn_je>").append(vo.getJyn_je()).append("</jyn_je>");
					detail.append("<wws_je>").append(vo.getWws_je()).append("</wws_je>");
					detail.append("<sy_je>").append(vo.getSy_je()).append("</sy_je>");
					detail.append("<yjsj_je>").append(vo.getYjsj_je()).append("</yjsj_je>");
					detail.append("<sl_xj>").append((vo.getDfd_je()).add(vo.getDfx_je()).add(vo.getLd_je()).add(vo.getJz_je())).append("</sl_xj>");
					detail.append("<xnh_xj>").append((vo.getLyxq_je()).add(vo.getLyjx_je()).add(vo.getLygxq_je()).add(vo.getYcb_je())).append("</xnh_xj>");
					detail.append("<jj_xj>").append((vo.getJyd_je()).add(vo.getBx_je()).add(vo.getQy_je())).append("</jj_xj>");
					detail.append("<jx_xj>").append((vo.getJzd_je()).add(vo.getXw_je()).add(vo.getXwd_je())).append("</jx_xj>");
					detail.append("<hx_xj>").append((vo.getHj_je()).add(vo.getXxx_je()).add(vo.getXxd_je())).append("</hx_xj>");
					detail.append("<jl_xj>").append(vo.getJlmz_je()).append("</jl_xj>");
					detail.append("<js_xj>").append((vo.getJyn_je()).add(vo.getWws_je()).add(vo.getSy_je()).add(vo.getYjsj_je())).append("</js_xj>");
					detail.append("<hj>").append((vo.getDfd_je()).add(vo.getDfx_je()).add(vo.getLd_je()).add(vo.getLyxq_je()).add(vo.getLyjx_je()).add(vo.getLygxq_je()).add(vo.getYcb_je()).add(vo.getJyd_je()).add(vo.getBx_je()).add(vo.getQy_je()).add(vo.getJzd_je()).add(vo.getXw_je()).add(vo.getXwd_je()).add(vo.getHj_je()).add(vo.getXxx_je()).add(vo.getXxd_je()).add(vo.getJlmz_je()).add(vo.getJyn_je()).add(vo.getWws_je()).add(vo.getSy_je()).add(vo.getYjsj_je())).append("</hj></row>");
				}
			}
		}
		for(LtmstxfVO vo : listby){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					System.err.println(entry.getKey()+"-----"+entry.getValue());
					String zdmc = ldMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"je", new BigDecimal(ll.get(0)));
				}
			
			detail.append("<row><xm>").append("实际免收通行费收入（元）").append("</xm>");
			detail.append("<fullname>").append("本月合计").append("</fullname>");
			detail.append("<dfd_je>").append(vo.getDfd_je()).append("</dfd_je>");
			detail.append("<dfx_je>").append(vo.getDfx_je()).append("</dfx_je>");
			detail.append("<ld_je>").append(vo.getLd_je()).append("</ld_je>");
			detail.append("<jz_je>").append(vo.getJz_je()).append("</jz_je>");
			detail.append("<lyxq_je>").append(vo.getLyxq_je()).append("</lyxq_je>");
			detail.append("<lyjx_je>").append(vo.getLyjx_je()).append("</lyjx_je>");
			detail.append("<lygxq_je>").append(vo.getLygxq_je()).append("</lygxq_je>");
			detail.append("<ycb_je>").append(vo.getYcb_je()).append("</ycb_je>");
			detail.append("<jyd_je>").append(vo.getJyd_je()).append("</jyd_je>");
			detail.append("<bx_je>").append(vo.getBx_je()).append("</bx_je>");
			detail.append("<qy_je>").append(vo.getQy_je()).append("</qy_je>");
			detail.append("<jzd_je>").append(vo.getJzd_je()).append("</jzd_je>");
			detail.append("<xw_je>").append(vo.getXw_je()).append("</xw_je>");
			detail.append("<xwd_je>").append(vo.getXwd_je()).append("</xwd_je>");
			detail.append("<hj_je>").append(vo.getHj_je()).append("</hj_je>");
			detail.append("<xxx_je>").append(vo.getXxx_je()).append("</xxx_je>");
			detail.append("<xxd_je>").append(vo.getXxd_je()).append("</xxd_je>");
			detail.append("<jlmz_je>").append(vo.getJlmz_je()).append("</jlmz_je>");
			detail.append("<jyn_je>").append(vo.getJyn_je()).append("</jyn_je>");
			detail.append("<wws_je>").append(vo.getWws_je()).append("</wws_je>");
			detail.append("<sy_je>").append(vo.getSy_je()).append("</sy_je>");
			detail.append("<yjsj_je>").append(vo.getYjsj_je()).append("</yjsj_je>");
			detail.append("<sl_xj>").append((vo.getDfd_je()).add(vo.getDfx_je()).add(vo.getLd_je()).add(vo.getJz_je())).append("</sl_xj>");
			detail.append("<xnh_xj>").append((vo.getLyxq_je()).add(vo.getLyjx_je()).add(vo.getLygxq_je()).add(vo.getYcb_je())).append("</xnh_xj>");
			detail.append("<jj_xj>").append((vo.getJyd_je()).add(vo.getBx_je()).add(vo.getQy_je())).append("</jj_xj>");
			detail.append("<jx_xj>").append((vo.getJzd_je()).add(vo.getXw_je()).add(vo.getXwd_je())).append("</jx_xj>");
			detail.append("<hx_xj>").append((vo.getHj_je()).add(vo.getXxx_je()).add(vo.getXxd_je())).append("</hx_xj>");
			detail.append("<jl_xj>").append(vo.getJlmz_je()).append("</jl_xj>");
			detail.append("<js_xj>").append((vo.getJyn_je()).add(vo.getWws_je()).add(vo.getSy_je()).add(vo.getYjsj_je())).append("</js_xj>");
			detail.append("<hj>").append((vo.getDfd_je()).add(vo.getDfx_je()).add(vo.getLd_je()).add(vo.getLyxq_je()).add(vo.getLyjx_je()).add(vo.getLygxq_je()).add(vo.getYcb_je()).add(vo.getJyd_je()).add(vo.getBx_je()).add(vo.getQy_je()).add(vo.getJzd_je()).add(vo.getXw_je()).add(vo.getXwd_je()).add(vo.getHj_je()).add(vo.getXxx_je()).add(vo.getXxd_je()).add(vo.getJlmz_je()).add(vo.getJyn_je()).add(vo.getWws_je()).add(vo.getSy_je()).add(vo.getYjsj_je())).append("</hj></row>");
		}
		}
			for(LtmstxfVO vo : listbn){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						System.err.println(entry.getKey()+"-----"+entry.getValue());
						String zdmc = ldMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"je", new BigDecimal(ll.get(0)));
					}
				
				detail.append("<row><xm>").append("实际免收通行费收入（元）").append("</xm>");
				detail.append("<fullname>").append("本年累计").append("</fullname>");
				detail.append("<dfd_je>").append(vo.getDfd_je()).append("</dfd_je>");
				detail.append("<dfx_je>").append(vo.getDfx_je()).append("</dfx_je>");
				detail.append("<ld_je>").append(vo.getLd_je()).append("</ld_je>");
				detail.append("<jz_je>").append(vo.getJz_je()).append("</jz_je>");
				detail.append("<lyxq_je>").append(vo.getLyxq_je()).append("</lyxq_je>");
				detail.append("<lyjx_je>").append(vo.getLyjx_je()).append("</lyjx_je>");
				detail.append("<lygxq_je>").append(vo.getLygxq_je()).append("</lygxq_je>");
				detail.append("<ycb_je>").append(vo.getYcb_je()).append("</ycb_je>");
				detail.append("<jyd_je>").append(vo.getJyd_je()).append("</jyd_je>");
				detail.append("<bx_je>").append(vo.getBx_je()).append("</bx_je>");
				detail.append("<qy_je>").append(vo.getQy_je()).append("</qy_je>");
				detail.append("<jzd_je>").append(vo.getJzd_je()).append("</jzd_je>");
				detail.append("<xw_je>").append(vo.getXw_je()).append("</xw_je>");
				detail.append("<xwd_je>").append(vo.getXwd_je()).append("</xwd_je>");
				detail.append("<hj_je>").append(vo.getHj_je()).append("</hj_je>");
				detail.append("<xxx_je>").append(vo.getXxx_je()).append("</xxx_je>");
				detail.append("<xxd_je>").append(vo.getXxd_je()).append("</xxd_je>");
				detail.append("<jlmz_je>").append(vo.getJlmz_je()).append("</jlmz_je>");
				detail.append("<jyn_je>").append(vo.getJyn_je()).append("</jyn_je>");
				detail.append("<wws_je>").append(vo.getWws_je()).append("</wws_je>");
				detail.append("<sy_je>").append(vo.getSy_je()).append("</sy_je>");
				detail.append("<yjsj_je>").append(vo.getYjsj_je()).append("</yjsj_je>");
				detail.append("<sl_xj>").append((vo.getDfd_je()).add(vo.getDfx_je()).add(vo.getLd_je()).add(vo.getJz_je())).append("</sl_xj>");
				detail.append("<xnh_xj>").append((vo.getLyxq_je()).add(vo.getLyjx_je()).add(vo.getLygxq_je()).add(vo.getYcb_je())).append("</xnh_xj>");
				detail.append("<jj_xj>").append((vo.getJyd_je()).add(vo.getBx_je()).add(vo.getQy_je())).append("</jj_xj>");
				detail.append("<jx_xj>").append((vo.getJzd_je()).add(vo.getXw_je()).add(vo.getXwd_je())).append("</jx_xj>");
				detail.append("<hx_xj>").append((vo.getHj_je()).add(vo.getXxx_je()).add(vo.getXxd_je())).append("</hx_xj>");
				detail.append("<jl_xj>").append(vo.getJlmz_je()).append("</jl_xj>");
				detail.append("<js_xj>").append((vo.getJyn_je()).add(vo.getWws_je()).add(vo.getSy_je()).add(vo.getYjsj_je())).append("</js_xj>");
				detail.append("<hj>").append((vo.getDfd_je()).add(vo.getDfx_je()).add(vo.getLd_je()).add(vo.getLyxq_je()).add(vo.getLyjx_je()).add(vo.getLygxq_je()).add(vo.getYcb_je()).add(vo.getJyd_je()).add(vo.getBx_je()).add(vo.getQy_je()).add(vo.getJzd_je()).add(vo.getXw_je()).add(vo.getXwd_je()).add(vo.getHj_je()).add(vo.getXxx_je()).add(vo.getXxd_je()).add(vo.getJlmz_je()).add(vo.getJyn_je()).add(vo.getWws_je()).add(vo.getSy_je()).add(vo.getYjsj_je())).append("</hj></row>");
			}
			}
				for(LtmstxfVO vo : listzkt){
					if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
						Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
						for (Map.Entry<String, String> entry : strMap.entrySet()) {
							System.err.println(entry.getKey()+"-----"+entry.getValue());
							String zdmc = ldMap.get(entry.getKey());
							List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
							Reflections.setFieldValue(vo, zdmc+"je", new BigDecimal(ll.get(0)));
						}
					
					detail.append("<row><xm>").append("实际免收通行费收入（元）").append("</xm>");
					detail.append("<fullname>").append("自开通累计").append("</fullname>");
					detail.append("<dfd_je>").append(vo.getDfd_je()).append("</dfd_je>");
					detail.append("<dfx_je>").append(vo.getDfx_je()).append("</dfx_je>");
					detail.append("<ld_je>").append(vo.getLd_je()).append("</ld_je>");
					detail.append("<jz_je>").append(vo.getJz_je()).append("</jz_je>");
					detail.append("<lyxq_je>").append(vo.getLyxq_je()).append("</lyxq_je>");
					detail.append("<lyjx_je>").append(vo.getLyjx_je()).append("</lyjx_je>");
					detail.append("<lygxq_je>").append(vo.getLygxq_je()).append("</lygxq_je>");
					detail.append("<ycb_je>").append(vo.getYcb_je()).append("</ycb_je>");
					detail.append("<jyd_je>").append(vo.getJyd_je()).append("</jyd_je>");
					detail.append("<bx_je>").append(vo.getBx_je()).append("</bx_je>");
					detail.append("<qy_je>").append(vo.getQy_je()).append("</qy_je>");
					detail.append("<jzd_je>").append(vo.getJzd_je()).append("</jzd_je>");
					detail.append("<xw_je>").append(vo.getXw_je()).append("</xw_je>");
					detail.append("<xwd_je>").append(vo.getXwd_je()).append("</xwd_je>");
					detail.append("<hj_je>").append(vo.getHj_je()).append("</hj_je>");
					detail.append("<xxx_je>").append(vo.getXxx_je()).append("</xxx_je>");
					detail.append("<xxd_je>").append(vo.getXxd_je()).append("</xxd_je>");
					detail.append("<jlmz_je>").append(vo.getJlmz_je()).append("</jlmz_je>");
					detail.append("<jyn_je>").append(vo.getJyn_je()).append("</jyn_je>");
					detail.append("<wws_je>").append(vo.getWws_je()).append("</wws_je>");
					detail.append("<sy_je>").append(vo.getSy_je()).append("</sy_je>");
					detail.append("<yjsj_je>").append(vo.getYjsj_je()).append("</yjsj_je>");
					detail.append("<sl_xj>").append((vo.getDfd_je()).add(vo.getDfx_je()).add(vo.getLd_je()).add(vo.getJz_je())).append("</sl_xj>");
					detail.append("<xnh_xj>").append((vo.getLyxq_je()).add(vo.getLyjx_je()).add(vo.getLygxq_je()).add(vo.getYcb_je())).append("</xnh_xj>");
					detail.append("<jj_xj>").append((vo.getJyd_je()).add(vo.getBx_je()).add(vo.getQy_je())).append("</jj_xj>");
					detail.append("<jx_xj>").append((vo.getJzd_je()).add(vo.getXw_je()).add(vo.getXwd_je())).append("</jx_xj>");
					detail.append("<hx_xj>").append((vo.getHj_je()).add(vo.getXxx_je()).add(vo.getXxd_je())).append("</hx_xj>");
					detail.append("<jl_xj>").append(vo.getJlmz_je()).append("</jl_xj>");
					detail.append("<js_xj>").append((vo.getJyn_je()).add(vo.getWws_je()).add(vo.getSy_je()).add(vo.getYjsj_je())).append("</js_xj>");
					detail.append("<hj>").append((vo.getDfd_je()).add(vo.getDfx_je()).add(vo.getLd_je()).add(vo.getLyxq_je()).add(vo.getLyjx_je()).add(vo.getLygxq_je()).add(vo.getYcb_je()).add(vo.getJyd_je()).add(vo.getBx_je()).add(vo.getQy_je()).add(vo.getJzd_je()).add(vo.getXw_je()).add(vo.getXwd_je()).add(vo.getHj_je()).add(vo.getXxx_je()).add(vo.getXxd_je()).add(vo.getJlmz_je()).add(vo.getJyn_je()).add(vo.getWws_je()).add(vo.getSy_je()).add(vo.getYjsj_je())).append("</hj></row>");
				}
		}
		for(int i = 0;i < StartTime.length;i++){
			if (StringUtils.isNoneBlank(yearMonth)) {
				map.put("StartTime", StartTime[i]);
				map.put("EndTime", EndTime[i]);
			}
			List<LtmstxfVO> list = reportService.getLtmstxfData(map);
			for(LtmstxfVO vo : list){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						String zdmc = ldMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"ll", Integer.valueOf(ll.get(1)));
					}
					detail.append("<row><xm>").append("绿通免费车流量（辆）").append("</xm>");
					detail.append("<fullname>").append(month+StartTime[i].substring(6, 8)+"日至"+month+EndTime[i].substring(6, 8)+"日").append("</fullname>");
					detail.append("<dfd_je>").append(vo.getDfd_ll()).append("</dfd_je>");
					detail.append("<dfx_je>").append(vo.getDfx_ll()).append("</dfx_je>");
					detail.append("<ld_je>").append(vo.getLd_ll()).append("</ld_je>");
					detail.append("<jz_je>").append(vo.getJz_ll()).append("</jz_je>");
					detail.append("<lyxq_je>").append(vo.getLyxq_ll()).append("</lyxq_je>");
					detail.append("<lyjx_je>").append(vo.getLyjx_ll()).append("</lyjx_je>");
					detail.append("<lygxq_je>").append(vo.getLygxq_ll()).append("</lygxq_je>");
					detail.append("<ycb_je>").append(vo.getYcb_ll()).append("</ycb_je>");
					detail.append("<jyd_je>").append(vo.getJyd_ll()).append("</jyd_je>");
					detail.append("<bx_je>").append(vo.getBx_ll()).append("</bx_je>");
					detail.append("<qy_je>").append(vo.getQy_ll()).append("</qy_je>");
					detail.append("<jzd_je>").append(vo.getJzd_ll()).append("</jzd_je>");
					detail.append("<xw_je>").append(vo.getXw_ll()).append("</xw_je>");
					detail.append("<xwd_je>").append(vo.getXwd_ll()).append("</xwd_je>");
					detail.append("<hj_je>").append(vo.getHj_ll()).append("</hj_je>");
					detail.append("<xxx_je>").append(vo.getXxx_ll()).append("</xxx_je>");
					detail.append("<xxd_je>").append(vo.getXxd_ll()).append("</xxd_je>");
					detail.append("<jlmz_je>").append(vo.getJlmz_ll()).append("</jlmz_je>");
					detail.append("<jyn_je>").append(vo.getJyn_ll()).append("</jyn_je>");
					detail.append("<wws_je>").append(vo.getWws_ll()).append("</wws_je>");
					detail.append("<sy_je>").append(vo.getSy_ll()).append("</sy_je>");
					detail.append("<yjsj_je>").append(vo.getYjsj_ll()).append("</yjsj_je>");
					detail.append("<sl_xj>").append(vo.getDfd_ll()+vo.getDfx_ll()+vo.getLd_ll()+vo.getJz_ll()).append("</sl_xj>");
					detail.append("<xnh_xj>").append(vo.getLyxq_ll()+vo.getLyjx_ll()+vo.getLygxq_ll()+vo.getYcb_ll()).append("</xnh_xj>");
					detail.append("<jj_xj>").append(vo.getJyd_ll()+vo.getBx_ll()+vo.getQy_ll()).append("</jj_xj>");
					detail.append("<jx_xj>").append(vo.getJzd_ll()+vo.getXw_ll()+vo.getXwd_ll()).append("</jx_xj>");
					detail.append("<hx_xj>").append(vo.getHj_ll()+vo.getXxx_ll()+vo.getXxd_ll()).append("</hx_xj>");
					detail.append("<jl_xj>").append(vo.getJlmz_ll()).append("</jl_xj>");
					detail.append("<js_xj>").append(vo.getJyn_ll()+vo.getWws_ll()+vo.getSy_ll()+vo.getYjsj_ll()).append("</js_xj>");
					detail.append("<hj>").append(vo.getDfd_ll()+vo.getDfx_ll()+vo.getLd_ll()+vo.getLyxq_ll()+vo.getLyjx_ll()+vo.getLygxq_ll()+vo.getYcb_ll()+vo.getJyd_ll()+vo.getBx_ll()+vo.getQy_ll()+vo.getJzd_ll()+vo.getXw_ll()+vo.getXwd_ll()+vo.getHj_ll()+vo.getXxx_ll()+vo.getXxd_ll()+vo.getJlmz_ll()+vo.getJyn_ll()+vo.getWws_ll()+vo.getSy_ll()+vo.getYjsj_ll()).append("</hj></row>");
				}
			}
		}
		for(LtmstxfVO vo : listby){
			if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
				Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
				for (Map.Entry<String, String> entry : strMap.entrySet()) {
					String zdmc = ldMap.get(entry.getKey());
					List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
					Reflections.setFieldValue(vo, zdmc+"ll", Integer.valueOf(ll.get(1)));
				}
			
				detail.append("<row><xm>").append("绿通免费车流量（辆）").append("</xm>");
				detail.append("<fullname>").append("本月合计").append("</fullname>");
				detail.append("<dfd_je>").append(vo.getDfd_ll()).append("</dfd_je>");
				detail.append("<dfx_je>").append(vo.getDfx_ll()).append("</dfx_je>");
				detail.append("<ld_je>").append(vo.getLd_ll()).append("</ld_je>");
				detail.append("<jz_je>").append(vo.getJz_ll()).append("</jz_je>");
				detail.append("<lyxq_je>").append(vo.getLyxq_ll()).append("</lyxq_je>");
				detail.append("<lyjx_je>").append(vo.getLyjx_ll()).append("</lyjx_je>");
				detail.append("<lygxq_je>").append(vo.getLygxq_ll()).append("</lygxq_je>");
				detail.append("<ycb_je>").append(vo.getYcb_ll()).append("</ycb_je>");
				detail.append("<jyd_je>").append(vo.getJyd_ll()).append("</jyd_je>");
				detail.append("<bx_je>").append(vo.getBx_ll()).append("</bx_je>");
				detail.append("<qy_je>").append(vo.getQy_ll()).append("</qy_je>");
				detail.append("<jzd_je>").append(vo.getJzd_ll()).append("</jzd_je>");
				detail.append("<xw_je>").append(vo.getXw_ll()).append("</xw_je>");
				detail.append("<xwd_je>").append(vo.getXwd_ll()).append("</xwd_je>");
				detail.append("<hj_je>").append(vo.getHj_ll()).append("</hj_je>");
				detail.append("<xxx_je>").append(vo.getXxx_ll()).append("</xxx_je>");
				detail.append("<xxd_je>").append(vo.getXxd_ll()).append("</xxd_je>");
				detail.append("<jlmz_je>").append(vo.getJlmz_ll()).append("</jlmz_je>");
				detail.append("<jyn_je>").append(vo.getJyn_ll()).append("</jyn_je>");
				detail.append("<wws_je>").append(vo.getWws_ll()).append("</wws_je>");
				detail.append("<sy_je>").append(vo.getSy_ll()).append("</sy_je>");
				detail.append("<yjsj_je>").append(vo.getYjsj_ll()).append("</yjsj_je>");
				detail.append("<sl_xj>").append(vo.getDfd_ll()+vo.getDfx_ll()+vo.getLd_ll()+vo.getJz_ll()).append("</sl_xj>");
				detail.append("<xnh_xj>").append(vo.getLyxq_ll()+vo.getLyjx_ll()+vo.getLygxq_ll()+vo.getYcb_ll()).append("</xnh_xj>");
				detail.append("<jj_xj>").append(vo.getJyd_ll()+vo.getBx_ll()+vo.getQy_ll()).append("</jj_xj>");
				detail.append("<jx_xj>").append(vo.getJzd_ll()+vo.getXw_ll()+vo.getXwd_ll()).append("</jx_xj>");
				detail.append("<hx_xj>").append(vo.getHj_ll()+vo.getXxx_ll()+vo.getXxd_ll()).append("</hx_xj>");
				detail.append("<jl_xj>").append(vo.getJlmz_ll()).append("</jl_xj>");
				detail.append("<js_xj>").append(vo.getJyn_ll()+vo.getWws_ll()+vo.getSy_ll()+vo.getYjsj_ll()).append("</js_xj>");
				detail.append("<hj>").append(vo.getDfd_ll()+vo.getDfx_ll()+vo.getLd_ll()+vo.getLyxq_ll()+vo.getLyjx_ll()+vo.getLygxq_ll()+vo.getYcb_ll()+vo.getJyd_ll()+vo.getBx_ll()+vo.getQy_ll()+vo.getJzd_ll()+vo.getXw_ll()+vo.getXwd_ll()+vo.getHj_ll()+vo.getXxx_ll()+vo.getXxd_ll()+vo.getJlmz_ll()+vo.getJyn_ll()+vo.getWws_ll()+vo.getSy_ll()+vo.getYjsj_ll()).append("</hj></row>");
		}
		}
			for(LtmstxfVO vo : listbn){
				if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
					Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
					for (Map.Entry<String, String> entry : strMap.entrySet()) {
						System.err.println(entry.getKey()+"-----"+entry.getValue());
						String zdmc = ldMap.get(entry.getKey());
						List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
						Reflections.setFieldValue(vo, zdmc+"ll", Integer.valueOf(ll.get(1)));
					}
				
					detail.append("<row><xm>").append("绿通免费车流量（辆）").append("</xm>");
					detail.append("<fullname>").append("本年累计").append("</fullname>");
					detail.append("<dfd_je>").append(vo.getDfd_ll()).append("</dfd_je>");
					detail.append("<dfx_je>").append(vo.getDfx_ll()).append("</dfx_je>");
					detail.append("<ld_je>").append(vo.getLd_ll()).append("</ld_je>");
					detail.append("<jz_je>").append(vo.getJz_ll()).append("</jz_je>");
					detail.append("<lyxq_je>").append(vo.getLyxq_ll()).append("</lyxq_je>");
					detail.append("<lyjx_je>").append(vo.getLyjx_ll()).append("</lyjx_je>");
					detail.append("<lygxq_je>").append(vo.getLygxq_ll()).append("</lygxq_je>");
					detail.append("<ycb_je>").append(vo.getYcb_ll()).append("</ycb_je>");
					detail.append("<jyd_je>").append(vo.getJyd_ll()).append("</jyd_je>");
					detail.append("<bx_je>").append(vo.getBx_ll()).append("</bx_je>");
					detail.append("<qy_je>").append(vo.getQy_ll()).append("</qy_je>");
					detail.append("<jzd_je>").append(vo.getJzd_ll()).append("</jzd_je>");
					detail.append("<xw_je>").append(vo.getXw_ll()).append("</xw_je>");
					detail.append("<xwd_je>").append(vo.getXwd_ll()).append("</xwd_je>");
					detail.append("<hj_je>").append(vo.getHj_ll()).append("</hj_je>");
					detail.append("<xxx_je>").append(vo.getXxx_ll()).append("</xxx_je>");
					detail.append("<xxd_je>").append(vo.getXxd_ll()).append("</xxd_je>");
					detail.append("<jlmz_je>").append(vo.getJlmz_ll()).append("</jlmz_je>");
					detail.append("<jyn_je>").append(vo.getJyn_ll()).append("</jyn_je>");
					detail.append("<wws_je>").append(vo.getWws_ll()).append("</wws_je>");
					detail.append("<sy_je>").append(vo.getSy_ll()).append("</sy_je>");
					detail.append("<yjsj_je>").append(vo.getYjsj_ll()).append("</yjsj_je>");
					detail.append("<sl_xj>").append(vo.getDfd_ll()+vo.getDfx_ll()+vo.getLd_ll()+vo.getJz_ll()).append("</sl_xj>");
					detail.append("<xnh_xj>").append(vo.getLyxq_ll()+vo.getLyjx_ll()+vo.getLygxq_ll()+vo.getYcb_ll()).append("</xnh_xj>");
					detail.append("<jj_xj>").append(vo.getJyd_ll()+vo.getBx_ll()+vo.getQy_ll()).append("</jj_xj>");
					detail.append("<jx_xj>").append(vo.getJzd_ll()+vo.getXw_ll()+vo.getXwd_ll()).append("</jx_xj>");
					detail.append("<hx_xj>").append(vo.getHj_ll()+vo.getXxx_ll()+vo.getXxd_ll()).append("</hx_xj>");
					detail.append("<jl_xj>").append(vo.getJlmz_ll()).append("</jl_xj>");
					detail.append("<js_xj>").append(vo.getJyn_ll()+vo.getWws_ll()+vo.getSy_ll()+vo.getYjsj_ll()).append("</js_xj>");
					detail.append("<hj>").append(vo.getDfd_ll()+vo.getDfx_ll()+vo.getLd_ll()+vo.getLyxq_ll()+vo.getLyjx_ll()+vo.getLygxq_ll()+vo.getYcb_ll()+vo.getJyd_ll()+vo.getBx_ll()+vo.getQy_ll()+vo.getJzd_ll()+vo.getXw_ll()+vo.getXwd_ll()+vo.getHj_ll()+vo.getXxx_ll()+vo.getXxd_ll()+vo.getJlmz_ll()+vo.getJyn_ll()+vo.getWws_ll()+vo.getSy_ll()+vo.getYjsj_ll()).append("</hj></row>");
			}
			}
				for(LtmstxfVO vo : listzkt){
					if(vo!=null && StringUtils.isNoneBlank(vo.getHj())){
						Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getHj());
						for (Map.Entry<String, String> entry : strMap.entrySet()) {
							System.err.println(entry.getKey()+"-----"+entry.getValue());
							String zdmc = ldMap.get(entry.getKey());
							List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
							Reflections.setFieldValue(vo, zdmc+"ll", Integer.valueOf(ll.get(1)));
						}
					
						detail.append("<row><xm>").append("绿通免费车流量（辆）").append("</xm>");
						detail.append("<fullname>").append("自开通累计").append("</fullname>");
						detail.append("<dfd_je>").append(vo.getDfd_ll()).append("</dfd_je>");
						detail.append("<dfx_je>").append(vo.getDfx_ll()).append("</dfx_je>");
						detail.append("<ld_je>").append(vo.getLd_ll()).append("</ld_je>");
						detail.append("<jz_je>").append(vo.getJz_ll()).append("</jz_je>");
						detail.append("<lyxq_je>").append(vo.getLyxq_ll()).append("</lyxq_je>");
						detail.append("<lyjx_je>").append(vo.getLyjx_ll()).append("</lyjx_je>");
						detail.append("<lygxq_je>").append(vo.getLygxq_ll()).append("</lygxq_je>");
						detail.append("<ycb_je>").append(vo.getYcb_ll()).append("</ycb_je>");
						detail.append("<jyd_je>").append(vo.getJyd_ll()).append("</jyd_je>");
						detail.append("<bx_je>").append(vo.getBx_ll()).append("</bx_je>");
						detail.append("<qy_je>").append(vo.getQy_ll()).append("</qy_je>");
						detail.append("<jzd_je>").append(vo.getJzd_ll()).append("</jzd_je>");
						detail.append("<xw_je>").append(vo.getXw_ll()).append("</xw_je>");
						detail.append("<xwd_je>").append(vo.getXwd_ll()).append("</xwd_je>");
						detail.append("<hj_je>").append(vo.getHj_ll()).append("</hj_je>");
						detail.append("<xxx_je>").append(vo.getXxx_ll()).append("</xxx_je>");
						detail.append("<xxd_je>").append(vo.getXxd_ll()).append("</xxd_je>");
						detail.append("<jlmz_je>").append(vo.getJlmz_ll()).append("</jlmz_je>");
						detail.append("<jyn_je>").append(vo.getJyn_ll()).append("</jyn_je>");
						detail.append("<wws_je>").append(vo.getWws_ll()).append("</wws_je>");
						detail.append("<sy_je>").append(vo.getSy_ll()).append("</sy_je>");
						detail.append("<yjsj_je>").append(vo.getYjsj_ll()).append("</yjsj_je>");
						detail.append("<sl_xj>").append(vo.getDfd_ll()+vo.getDfx_ll()+vo.getLd_ll()+vo.getJz_ll()).append("</sl_xj>");
						detail.append("<xnh_xj>").append(vo.getLyxq_ll()+vo.getLyjx_ll()+vo.getLygxq_ll()+vo.getYcb_ll()).append("</xnh_xj>");
						detail.append("<jj_xj>").append(vo.getJyd_ll()+vo.getBx_ll()+vo.getQy_ll()).append("</jj_xj>");
						detail.append("<jx_xj>").append(vo.getJzd_ll()+vo.getXw_ll()+vo.getXwd_ll()).append("</jx_xj>");
						detail.append("<hx_xj>").append(vo.getHj_ll()+vo.getXxx_ll()+vo.getXxd_ll()).append("</hx_xj>");
						detail.append("<jl_xj>").append(vo.getJlmz_ll()).append("</jl_xj>");
						detail.append("<js_xj>").append(vo.getJyn_ll()+vo.getWws_ll()+vo.getSy_ll()+vo.getYjsj_ll()).append("</js_xj>");
						detail.append("<hj>").append(vo.getDfd_ll()+vo.getDfx_ll()+vo.getLd_ll()+vo.getLyxq_ll()+vo.getLyjx_ll()+vo.getLygxq_ll()+vo.getYcb_ll()+vo.getJyd_ll()+vo.getBx_ll()+vo.getQy_ll()+vo.getJzd_ll()+vo.getXw_ll()+vo.getXwd_ll()+vo.getHj_ll()+vo.getXxx_ll()+vo.getXxd_ll()+vo.getJlmz_ll()+vo.getJyn_ll()+vo.getWws_ll()+vo.getSy_ll()+vo.getYjsj_ll()).append("</hj></row>");
				}
		}
		
		sb.append(detail).append("</xml></report>");
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	/**
	 * 绿通免收通行费及车流量明细月报表首页
	 * @return
	 */
	@RequestMapping("ltmstxfIndex")
	public String ltmstxfIndex(Model model){
		//上月
		DateTime dt = new DateTime();
		String st = dt.plusMonths(-1).toString("yyyy年MM月");
		model.addAttribute("st", st);
		return "report/ltmstxfIndex";
	}
}
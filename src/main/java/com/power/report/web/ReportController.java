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
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.power.app.entity.FyzbVO;
import com.power.app.service.AppService;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.KeyValue;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.FxLtsj;
import com.power.common.springmvc.ExtReturn;
import com.power.common.springmvc.ResponseUtils;
import com.power.common.utils.Reflections;
import com.power.index.service.IndexService;
import com.power.report.entity.JkrbDataVO;
import com.power.report.entity.KhcllfyListVO;
import com.power.report.entity.LtmxbDataVO;
import com.power.report.entity.TxfsrpjVO;
import com.power.report.service.ReportService;

/**
 * 项目名称：power2016 <br>
 * 类名称：ReportController <br>
 * 创建时间：2017-4-8 下午5:57:05 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Controller
@RequestMapping("report")
public class ReportController {
	
	private final static Logger log = LoggerFactory.getLogger(ReportController.class);
	@Autowired
	private AppService appService;
	@Autowired
	private ReportService reportService;
	@Autowired
	private IndexService indexService;
	
	
	/**
	 * home 图表信息
	 * @return
	 */
	@RequestMapping("home.json")
	@ResponseBody
	public Object firstData(){
		AuthUserVO user =UserUtils.getAuthUser();
		Map<String,Object> param = new HashMap<String,Object>();
		indexService.setDeptChildParam(user,param);//
		//
		List<FyzbVO> fyList = appService.getFyzbData(param);//费用信息
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
		List<KeyValue> llfyList = reportService.getLlfyData(param);//年/月顺序
		reMap.put("llfy", llfyList);
		return new ExtReturn(true,reMap);
	}
	/**
	 * 银行缴款日报
	 * @return
	 */
	@RequestMapping("jkrbIndex")
	public String jkrbIndex(@RequestParam(value="t",required=false)Integer flag){
		if(null!=flag && flag ==1 ){
			return "report/jkrbReport";
		}
		return "report/jkrbIndex";
	}
	/**
	 * 银行缴款日报数据
	 * 子报表/分组/交叉/统计
	 * @param rsp
	 * @param st
	 * @param et
	 * @param orgId
	 * @param deptId
	 */
	@RequestMapping("jkrbData")
	public void jkrbData(HttpServletResponse rsp,
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
		//层级关系处理
		indexService.setOrgDeptParam(orgId,deptId,map);
		List<JkrbDataVO>  list = reportService.getJkrbData(map);
		//报表数据封装
		StringBuffer sb=new StringBuffer("<xml>");
		StringBuffer master = new StringBuffer();
		StringBuffer detail=new StringBuffer();
		Map<String,Object> masterMap = Maps.newHashMap();
		//Master
		for(JkrbDataVO vo:list){
			if(!masterMap.containsKey(vo.getSfrq())){//生成Master
				master.append("<Master><st>").append(st).append("</st>");
				master.append("<et>").append(et).append("</et>");
				master.append("<sfrq>").append(vo.getSfrq()).append("</sfrq></Master>");
				masterMap.put(vo.getSfrq(), vo.getSfrq());
			}
			//明细
			if(vo.getJfrq1().compareTo(vo.getJfrq2())<0){
				detail.append("<Detail1><sfrq>").append(vo.getSfrq()).append("</sfrq>");
				detail.append("<sfzdm>").append(vo.getSfzdm()).append("</sfzdm>");
				detail.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
				detail.append("<yjje>").append(vo.getYjje1()).append("</yjje>");
				detail.append("<sjje>").append(vo.getSjje1()).append("</sjje>");
				detail.append("<jfrq>").append(vo.getJfrq1().substring(4)).append("</jfrq>");
				detail.append("<bz>").append(vo.getBz()).append("</bz></Detail1>");
				
				detail.append("<Detail1><sfrq>").append(vo.getSfrq()).append("</sfrq>");
				detail.append("<sfzdm>").append(vo.getSfzdm()).append("</sfzdm>");
				detail.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
				detail.append("<yjje>").append(vo.getYjje2()).append("</yjje>");
				detail.append("<sjje>").append(vo.getSjje2()).append("</sjje>");
				detail.append("<jfrq>").append(vo.getJfrq2().substring(4)).append("</jfrq>");
				detail.append("<bz>").append(vo.getBz()).append("</bz></Detail1>");
			}else{
				detail.append("<Detail1><sfrq>").append(vo.getSfrq()).append("</sfrq>");
				detail.append("<sfzdm>").append(vo.getSfzdm()).append("</sfzdm>");
				detail.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
				detail.append("<yjje>").append(vo.getYjje2()).append("</yjje>");
				detail.append("<sjje>").append(vo.getSjje2()).append("</sjje>");
				detail.append("<jfrq>").append(vo.getJfrq2().substring(4)).append("</jfrq>");
				detail.append("<bz>").append(vo.getBz()).append("</bz></Detail1>");
				
				detail.append("<Detail1><sfrq>").append(vo.getSfrq()).append("</sfrq>");
				detail.append("<sfzdm>").append(vo.getSfzdm()).append("</sfzdm>");
				detail.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
				detail.append("<yjje>").append(vo.getYjje1()).append("</yjje>");
				detail.append("<sjje>").append(vo.getSjje1()).append("</sjje>");
				detail.append("<jfrq>").append(vo.getJfrq1().substring(4)).append("</jfrq>");
				detail.append("<bz>").append(vo.getBz()).append("</bz></Detail1>");
			}
			//
			detail.append("<Detail1><sfrq>").append(vo.getSfrq()).append("</sfrq>");
			detail.append("<sfzdm>").append(vo.getSfzdm()).append("</sfzdm>");
			detail.append("<zdmc>").append(vo.getZdmc()).append("</zdmc>");
			detail.append("<yjje>").append(vo.getYjjehj()).append("</yjje>");
			detail.append("<sjje>").append(vo.getSjjehj()).append("</sjje>");
			detail.append("<jfrq>").append("合计").append("</jfrq>");
			detail.append("<bz>").append(vo.getBz()).append("</bz></Detail1>");
		}
		sb.append(master).append(detail).append("</xml>");
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	
	/**
	 * 路段 客、货、免征车流量
	 * @return
	 */
	@RequestMapping("khcllIndex")
	public String khcllIndex(){
		return "report/khcllIndex";
	}
	/**
	 * 路段 客、货、免征车 收入
	 * @return
	 */
	@RequestMapping("khcsrIndex")
	public String khcsrIndex(){
		return "report/khcsrIndex";
	}
	//路段固定
	private static Map<String,String> ldMap;
	static{
		ldMap = Maps.newHashMap();
		ldMap.put("少洛段", "sl_");
		ldMap.put("西南环段", "xnh_");
		ldMap.put("济焦段", "jj_");
		ldMap.put("焦修段", "jx_");
		ldMap.put("获新段","hx_");
		ldMap.put("济邵段","js_");
		ldMap.put("济洛段","jl_");
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
	 * 计算tongbi
	 * @param bq 本期
	 * @param sq 上期
	 * @return
	 */
	private BigDecimal getTongbi(BigDecimal bq,BigDecimal tq){
		BigDecimal tongbi = BigDecimal.ZERO;
		if(0!=tq.compareTo(BigDecimal.ZERO)){
			/**同比增长率＝（本期数－同期数）/同期数*100％ 指和去年同期相比较的增长率。**/
			tongbi = (bq.subtract(tq)).multiply(new BigDecimal(100)).divide(tq,2, BigDecimal.ROUND_HALF_UP);
		}
		return tongbi;
	}
	/**
	 * 路段 客、货、免征车流量 汇总
	 * 
	 * @param rsp
	 * @param st
	 * @param et
	 */
	@RequestMapping("khcllData")
	public void khcllData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et){
		//
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(st)){
			map.put("st",st);
		}
		if(StringUtils.isNoneBlank(et)){
			map.put("et",et);
		}
		if(StringUtils.isBlank(st) && StringUtils.isBlank(et)){
			map.put("dt",DateTime.now().toString("yyyyMM"));
		}
		List<KhcllfyListVO>  list = reportService.getKhcllData(map);
		//路段，数据拆分
		for(KhcllfyListVO vo:list){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getLlhj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
//				System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = ldMap.get(entry.getKey());
				//流量 数据 kc/hc/mzc
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"kc", Integer.valueOf(ll.get(0)));
				Reflections.setFieldValue(vo, zdmc+"hc", Integer.valueOf(ll.get(1)));
				Reflections.setFieldValue(vo, zdmc+"mz", Integer.valueOf(ll.get(2)));
				//
			}
		}
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
		StringBuffer detail=new StringBuffer();
		//计算同比使用
		Map<String,KhcllfyListVO> tongbiMap = Maps.newHashMap();
		//
		for(int i=0;i<list.size();i++){
			KhcllfyListVO vo = list.get(i);
			detail.append("<row><nd>").append(vo.getNd()).append("年</nd>");
			detail.append("<yf>").append(vo.getYf()).append("月</yf>");
			//站，数据拆分
			detail.append("<sl_kc>").append(vo.getSl_kc()).append("</sl_kc>");
			detail.append("<sl_hc>").append(vo.getSl_hc()).append("</sl_hc>");
			detail.append("<sl_mz>").append(vo.getSl_mz()).append("</sl_mz>");
			
			detail.append("<xnh_kc>").append(vo.getXnh_kc()).append("</xnh_kc>");
			detail.append("<xnh_hc>").append(vo.getXnh_hc()).append("</xnh_hc>");
			detail.append("<xnh_mz>").append(vo.getXnh_mz()).append("</xnh_mz>");
			
			detail.append("<jj_kc>").append(vo.getJj_kc()).append("</jj_kc>");
			detail.append("<jj_hc>").append(vo.getJj_hc()).append("</jj_hc>");
			detail.append("<jj_mz>").append(vo.getJj_mz()).append("</jj_mz>");
			
			detail.append("<jx_kc>").append(vo.getJx_kc()).append("</jx_kc>");
			detail.append("<jx_hc>").append(vo.getJx_hc()).append("</jx_hc>");
			detail.append("<jx_mz>").append(vo.getJx_mz()).append("</jx_mz>");
			
			detail.append("<hx_kc>").append(vo.getHx_kc()).append("</hx_kc>");
			detail.append("<hx_hc>").append(vo.getHx_hc()).append("</hx_hc>");
			detail.append("<hx_mz>").append(vo.getHx_mz()).append("</hx_mz>");
			
			detail.append("<js_kc>").append(vo.getJs_kc()).append("</js_kc>");
			detail.append("<js_hc>").append(vo.getJs_hc()).append("</js_hc>");
			detail.append("<js_mz>").append(vo.getJs_mz()).append("</js_mz>");
			
			detail.append("<jl_kc>").append(vo.getJl_kc()).append("</jl_kc>");
			detail.append("<jl_hc>").append(vo.getJl_hc()).append("</jl_hc>");
			detail.append("<jl_mz>").append(vo.getJl_mz()).append("</jl_mz>");
			//计算合计/累计/同比/环比
			vo.setKc_hj(vo.getSl_kc()+vo.getXnh_kc()+vo.getJj_kc()+vo.getJx_kc()+vo.getHx_kc()+vo.getJs_kc()+vo.getJl_kc());
			vo.setHc_hj(vo.getSl_hc()+vo.getXnh_hc()+vo.getJj_hc()+vo.getJx_hc()+vo.getHx_hc()+vo.getJs_hc()+vo.getJl_hc());
			vo.setMz_hj(vo.getSl_mz()+vo.getXnh_mz()+vo.getJj_mz()+vo.getJx_mz()+vo.getHx_mz()+vo.getJs_mz()+vo.getJl_mz());
			//累计
			vo.setLj(vo.getKc_hj()+vo.getHc_hj()+vo.getMz_hj());
			//
			detail.append("<kc_hj>").append(vo.getKc_hj()).append("</kc_hj>");
			detail.append("<hc_hj>").append(vo.getHc_hj()).append("</hc_hj>");
			detail.append("<mz_hj>").append(vo.getMz_hj()).append("</mz_hj>");
			detail.append("<lj>").append(vo.getLj()).append("</lj>");
			//
			//上期
			if(i>0){
				KhcllfyListVO sqVo = list.get(i-1);
				BigDecimal hb = getHuanbi(new BigDecimal(vo.getLj()), new BigDecimal(sqVo.getLj()));
				detail.append("<hb>").append(hb).append("%</hb>");
				//TODO 同比
				
				//
			}
			detail.append("</row>");
			//TODO 转换成Map 计算同比
//			tongbiMap.put(vo.getNd()+""+vo.getYf(), vo);
		}
		//
		if(list.size()>1){//开始计算行 环比/同比
			//环比
			KhcllfyListVO bqVO = list.get(list.size()-1);//本期
			KhcllfyListVO sqVo = list.get(list.size()-2);//上期
			//
			detail.append("<row><nd>").append(bqVO.getNd()).append("年</nd>");
//			detail.append("<yf>").append(vo.getYf()).append("月</yf>");
			//站，数据拆分
			detail.append("<sl_kc>").append(getHuanbi(new BigDecimal(bqVO.getSl_kc()), new BigDecimal(sqVo.getSl_kc()))).append("%</sl_kc>");
			detail.append("<sl_hc>").append(getHuanbi(new BigDecimal(bqVO.getSl_hc()), new BigDecimal(sqVo.getSl_hc()))).append("%</sl_hc>");
			detail.append("<sl_mz>").append(getHuanbi(new BigDecimal(bqVO.getSl_mz()), new BigDecimal(sqVo.getSl_mz()))).append("%</sl_mz>");
			
			detail.append("<xnh_kc>").append(getHuanbi(new BigDecimal(bqVO.getXnh_kc()), new BigDecimal(sqVo.getXnh_kc()))).append("%</xnh_kc>");
			detail.append("<xnh_hc>").append(getHuanbi(new BigDecimal(bqVO.getXnh_hc()), new BigDecimal(sqVo.getXnh_hc()))).append("%</xnh_hc>");
			detail.append("<xnh_mz>").append(getHuanbi(new BigDecimal(bqVO.getXnh_mz()), new BigDecimal(sqVo.getXnh_mz()))).append("%</xnh_mz>");
			
			detail.append("<jj_kc>").append(getHuanbi(new BigDecimal(bqVO.getJj_kc()), new BigDecimal(sqVo.getJj_kc()))).append("%</jj_kc>");
			detail.append("<jj_hc>").append(getHuanbi(new BigDecimal(bqVO.getJj_hc()), new BigDecimal(sqVo.getJj_hc()))).append("%</jj_hc>");
			detail.append("<jj_mz>").append(getHuanbi(new BigDecimal(bqVO.getJj_mz()), new BigDecimal(sqVo.getJj_mz()))).append("%</jj_mz>");
			
			detail.append("<jx_kc>").append(getHuanbi(new BigDecimal(bqVO.getJx_kc()), new BigDecimal(sqVo.getJx_kc()))).append("%</jx_kc>");
			detail.append("<jx_hc>").append(getHuanbi(new BigDecimal(bqVO.getJx_hc()), new BigDecimal(sqVo.getJx_hc()))).append("%</jx_hc>");
			detail.append("<jx_mz>").append(getHuanbi(new BigDecimal(bqVO.getJx_mz()), new BigDecimal(sqVo.getJx_mz()))).append("%</jx_mz>");
			
			detail.append("<hx_kc>").append(getHuanbi(new BigDecimal(bqVO.getHx_kc()), new BigDecimal(sqVo.getHx_kc()))).append("%</hx_kc>");
			detail.append("<hx_hc>").append(getHuanbi(new BigDecimal(bqVO.getHx_hc()), new BigDecimal(sqVo.getHx_hc()))).append("%</hx_hc>");
			detail.append("<hx_mz>").append(getHuanbi(new BigDecimal(bqVO.getHx_mz()), new BigDecimal(sqVo.getHx_mz()))).append("%</hx_mz>");
			
			detail.append("<js_kc>").append(getHuanbi(new BigDecimal(bqVO.getJs_kc()), new BigDecimal(sqVo.getJs_kc()))).append("%</js_kc>");
			detail.append("<js_hc>").append(getHuanbi(new BigDecimal(bqVO.getJs_hc()), new BigDecimal(sqVo.getJs_hc()))).append("%</js_hc>");
			detail.append("<js_mz>").append(getHuanbi(new BigDecimal(bqVO.getJs_mz()), new BigDecimal(sqVo.getJs_mz()))).append("%</js_mz>");
			
			detail.append("<jl_kc>").append(getHuanbi(new BigDecimal(bqVO.getJl_kc()), new BigDecimal(sqVo.getJl_kc()))).append("%</jl_kc>");
			detail.append("<jl_hc>").append(getHuanbi(new BigDecimal(bqVO.getJl_hc()), new BigDecimal(sqVo.getJl_hc()))).append("%</jl_hc>");
			detail.append("<jl_mz>").append(getHuanbi(new BigDecimal(bqVO.getJl_mz()), new BigDecimal(sqVo.getJl_mz()))).append("%</jl_mz>");
			//
			detail.append("<kc_hj>").append(getHuanbi(new BigDecimal(bqVO.getKc_hj()), new BigDecimal(sqVo.getKc_hj()))).append("%</kc_hj>");
			detail.append("<hc_hj>").append(getHuanbi(new BigDecimal(bqVO.getHc_hj()), new BigDecimal(sqVo.getHc_hj()))).append("%</hc_hj>");
			detail.append("<mz_hj>").append(getHuanbi(new BigDecimal(bqVO.getMz_hj()), new BigDecimal(sqVo.getMz_hj()))).append("%</mz_hj>");
			detail.append("</row>");
			//同比
//			KhcllfyListVO tqVo = list.get(i-1);//同期
		}
		
		sb.append(detail).append("</xml><_grparam></_grparam></report>");
//		System.err.println(sb.toString());
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	/**
	 * 路段 客、货、免征车 收入 汇总
	 * 
	 * @param rsp
	 * @param st
	 * @param et
	 */
	@RequestMapping("khcsrData")
	public void khcsrData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et){
		//
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(st)){
			map.put("st",st);
		}
		if(StringUtils.isNoneBlank(et)){
			map.put("et",et);
		}
		if(StringUtils.isBlank(st) && StringUtils.isBlank(et)){
			map.put("dt",DateTime.now().toString("yyyyMM"));
		}
		List<KhcllfyListVO>  list = reportService.getKhcsrData(map);
		//路段，数据拆分
		for(KhcllfyListVO vo:list){
			Map<String,String> strMap = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(vo.getLlhj());
			for (Map.Entry<String, String> entry : strMap.entrySet()) {
//				System.err.println(entry.getKey()+"-----"+entry.getValue());
				String zdmc = ldMap.get(entry.getKey());
				//流量 数据 kc/hc/mzc
				List<String> ll = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(entry.getValue());
				Reflections.setFieldValue(vo, zdmc+"kc_je", new BigDecimal(ll.get(0)));
				Reflections.setFieldValue(vo, zdmc+"hc_je", new BigDecimal(ll.get(1)));
				//
			}
		}
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
		StringBuffer detail=new StringBuffer();
		//计算同比使用
		Map<String,KhcllfyListVO> tongbiMap = Maps.newHashMap();
		//
		for(int i=0;i<list.size();i++){
			KhcllfyListVO vo = list.get(i);
			detail.append("<row><nd>").append(vo.getNd()).append("年</nd>");
			detail.append("<yf>").append(vo.getYf()).append("月</yf>");
			//站，数据拆分
			detail.append("<sl_kc>").append(vo.getSl_hc_je()).append("</sl_kc>");
			detail.append("<sl_hc>").append(vo.getSl_hc_je()).append("</sl_hc>");
			
			detail.append("<xnh_kc>").append(vo.getXnh_kc_je()).append("</xnh_kc>");
			detail.append("<xnh_hc>").append(vo.getXnh_hc_je()).append("</xnh_hc>");
			
			detail.append("<jj_kc>").append(vo.getJj_kc_je()).append("</jj_kc>");
			detail.append("<jj_hc>").append(vo.getJj_hc_je()).append("</jj_hc>");
			
			detail.append("<jx_kc>").append(vo.getJx_kc_je()).append("</jx_kc>");
			detail.append("<jx_hc>").append(vo.getJx_hc_je()).append("</jx_hc>");
			
			detail.append("<hx_kc>").append(vo.getHx_kc_je()).append("</hx_kc>");
			detail.append("<hx_hc>").append(vo.getHx_hc_je()).append("</hx_hc>");
			
			detail.append("<js_kc>").append(vo.getJs_kc_je()).append("</js_kc>");
			detail.append("<js_hc>").append(vo.getJs_hc_je()).append("</js_hc>");
			
			detail.append("<jl_kc>").append(vo.getJl_kc_je()).append("</jl_kc>");
			detail.append("<jl_hc>").append(vo.getJl_hc_je()).append("</jl_hc>");
			//计算合计/累计/同比/环比
			vo.setKc_je_hj(vo.getSl_kc_je().add(vo.getXnh_kc_je()).add(vo.getJj_kc_je()).add(vo.getJx_kc_je()).add(vo.getHx_kc_je()).add(vo.getJs_kc_je()).add(vo.getJl_kc_je()));
			vo.setHc_je_hj(vo.getSl_hc_je().add(vo.getXnh_hc_je()).add(vo.getJj_hc_je()).add(vo.getJx_hc_je()).add(vo.getHx_hc_je()).add(vo.getJs_hc_je()).add(vo.getJl_hc_je()));
			//累计
			vo.setJe(vo.getKc_je_hj().add(vo.getHc_je_hj()));
			//
			detail.append("<kc_hj>").append(vo.getKc_je_hj()).append("</kc_hj>");
			detail.append("<hc_hj>").append(vo.getHc_je_hj()).append("</hc_hj>");
			detail.append("<lj>").append(vo.getJe()).append("</lj>");
			//
			//上期
			if(i>0){
				KhcllfyListVO sqVo = list.get(i-1);
				BigDecimal hb = getHuanbi(vo.getJe(), sqVo.getJe());
				detail.append("<hb>").append(hb).append("%</hb>");
				//TODO 同比
				
				//
			}
			detail.append("</row>");
			//TODO 转换成Map 计算同比
//			tongbiMap.put(vo.getNd()+""+vo.getYf(), vo);
		}
		//
		if(list.size()>1){//开始计算行 环比/同比
			//环比
			KhcllfyListVO bqVO = list.get(list.size()-1);//本期
			KhcllfyListVO sqVo = list.get(list.size()-2);//上期
			//
			detail.append("<row><nd>").append(bqVO.getNd()).append("年</nd>");
			//站，数据拆分
			detail.append("<sl_kc>").append(getHuanbi(bqVO.getSl_kc_je(), sqVo.getSl_kc_je())).append("%</sl_kc>");
			detail.append("<sl_hc>").append(getHuanbi(bqVO.getSl_hc_je(), sqVo.getSl_hc_je())).append("%</sl_hc>");
			
			detail.append("<xnh_kc>").append(getHuanbi(bqVO.getXnh_kc_je(), sqVo.getXnh_kc_je())).append("%</xnh_kc>");
			detail.append("<xnh_hc>").append(getHuanbi(bqVO.getXnh_hc_je(), sqVo.getXnh_hc_je())).append("%</xnh_hc>");
			
			detail.append("<jj_kc>").append(getHuanbi(bqVO.getJj_kc_je(), sqVo.getJj_kc_je())).append("%</jj_kc>");
			detail.append("<jj_hc>").append(getHuanbi(bqVO.getJj_hc_je(), sqVo.getJj_hc_je())).append("%</jj_hc>");
			
			detail.append("<jx_kc>").append(getHuanbi(bqVO.getJx_kc_je(), sqVo.getJx_kc_je())).append("%</jx_kc>");
			detail.append("<jx_hc>").append(getHuanbi(bqVO.getJx_hc_je(), sqVo.getJx_hc_je())).append("%</jx_hc>");
			
			detail.append("<hx_kc>").append(getHuanbi(bqVO.getHx_kc_je(), sqVo.getHx_kc_je())).append("%</hx_kc>");
			detail.append("<hx_hc>").append(getHuanbi(bqVO.getHx_hc_je(), sqVo.getHx_hc_je())).append("%</hx_hc>");
			
			detail.append("<js_kc>").append(getHuanbi(bqVO.getJs_kc_je(), sqVo.getJs_kc_je())).append("%</js_kc>");
			detail.append("<js_hc>").append(getHuanbi(bqVO.getJs_hc_je(), sqVo.getJs_hc_je())).append("%</js_hc>");
			
			detail.append("<jl_kc>").append(getHuanbi(bqVO.getJl_kc_je(), sqVo.getJl_kc_je())).append("%</jl_kc>");
			detail.append("<jl_hc>").append(getHuanbi(bqVO.getJl_hc_je(), sqVo.getJl_hc_je())).append("%</jl_hc>");
			//
			detail.append("<kc_hj>").append(getHuanbi(bqVO.getKc_je_hj(), sqVo.getKc_je_hj())).append("%</kc_hj>");
			detail.append("<hc_hj>").append(getHuanbi(bqVO.getHc_je_hj(), sqVo.getHc_je_hj())).append("%</hc_hj>");
			detail.append("</row>");
			//同比
//			KhcllfyListVO tqVo = list.get(i-1);//同期
		}
		
		sb.append(detail).append("</xml><_grparam></_grparam></report>");
//		System.err.println(sb.toString());
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	/**
	 * 绿色通道查询
	 * @return
	 */
	@RequestMapping("lstdcxIndex")
	public String lstdcxIndex(Model model){
		//本月第一天
		DateTime dt = new DateTime();
		String st = dt.dayOfMonth().setCopy(1).toString("yyyy年MM月dd日");
		model.addAttribute("st", st);
		model.addAttribute("et", dt.toString("yyyy年MM月dd日"));
		return "report/lstdcxIndex";
	}
	/**
	 * 绿色通道数据报表
	 * @param rsp
	 * @param st
	 * @param et
	 * @param orgId
	 * @param deptId
	 */
	@RequestMapping("getLstdData")
	public void getLstdData(HttpServletResponse rsp,
			@RequestParam(value="st",required=false)String st,
			@RequestParam(value="et",required=false)String et,
			@RequestParam(value="orgId",required=false)String orgId,
			@RequestParam(value="deptId",required=false)String deptId){
		HashMap<String, Object> map = new HashMap<String, Object>(6);
		if(StringUtils.isNoneBlank(st)){
			map.put("st",st.replace("年", "-").replace("月", "-").replace("日", ""));
		}
		if(StringUtils.isNoneBlank(et)){
			map.put("et",et.replace("年", "-").replace("月", "-").replace("日", ""));
		}
		//层级关系处理
		indexService.setOrgDeptParam(orgId,deptId,map);
		List<FxLtsj>  list = reportService.getLtsjData(map);
		//报表数据封装
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><report><xml>");
		StringBuffer detail=new StringBuffer();
		//
		if(list.size()>1){
			FxLtsj vo = list.get(0);
			detail.append("<row><mc>下道</mc>");
			detail.append("<xm>车辆数</xm>");
			detail.append("<ax>").append(vo.getLtc1()).append("</ax>");
			detail.append("<bx>").append(vo.getLtc2()).append("</bx>");
			detail.append("<cx>").append(vo.getLtc3()).append("</cx>");
			detail.append("<dx>").append(vo.getLtc4()).append("</dx>");
			detail.append("<ex>").append(vo.getLtc5()).append("</ex>");
			detail.append("<fx>").append(vo.getLtc6()).append("</fx>");
			detail.append("<hj>").append(vo.getLtchj()).append("</hj></row>");
			
			detail.append("<row><mc>下道</mc>");
			detail.append("<xm>免费金额</xm>");
			detail.append("<ax>").append(vo.getLtcje1()).append("</ax>");
			detail.append("<bx>").append(vo.getLtcje2()).append("</bx>");
			detail.append("<cx>").append(vo.getLtcje3()).append("</cx>");
			detail.append("<dx>").append(vo.getLtcje4()).append("</dx>");
			detail.append("<ex>").append(vo.getLtcje5()).append("</ex>");
			detail.append("<fx>").append(vo.getLtcje6()).append("</fx>");
			detail.append("<hj>").append(vo.getLtcjehj()).append("</hj></row>");
			//
			vo = list.get(1);
			detail.append("<row><mc>下道累计</mc>");
			detail.append("<xm>车辆数</xm>");
			detail.append("<ax>").append(vo.getLtc1()).append("</ax>");
			detail.append("<bx>").append(vo.getLtc2()).append("</bx>");
			detail.append("<cx>").append(vo.getLtc3()).append("</cx>");
			detail.append("<dx>").append(vo.getLtc4()).append("</dx>");
			detail.append("<ex>").append(vo.getLtc5()).append("</ex>");
			detail.append("<fx>").append(vo.getLtc6()).append("</fx>");
			detail.append("<hj>").append(vo.getLtchj()).append("</hj></row>");
			
			detail.append("<row><mc>下道累计</mc>");
			detail.append("<xm>免费金额</xm>");
			detail.append("<ax>").append(vo.getLtcje1()).append("</ax>");
			detail.append("<bx>").append(vo.getLtcje2()).append("</bx>");
			detail.append("<cx>").append(vo.getLtcje3()).append("</cx>");
			detail.append("<dx>").append(vo.getLtcje4()).append("</dx>");
			detail.append("<ex>").append(vo.getLtcje5()).append("</ex>");
			detail.append("<fx>").append(vo.getLtcje6()).append("</fx>");
			detail.append("<hj>").append(vo.getLtcjehj()).append("</hj></row>");
		}
		sb.append(detail).append("</xml>");
		sb.append("<_grparam><st>").append(st).append("</st>");
		sb.append("<et>").append(et).append("</et>");
		sb.append("<dw>").append(map.get("title")).append("</dw>");//
		sb.append("</_grparam></report>");
//		System.err.println(sb.toString());
		ResponseUtils.renderXml(rsp, sb.toString());
	}
	
	
}
package com.power.report.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.base.sys.entity.KeyValue;
import com.power.common.entity.FxLtsj;
import com.power.common.entity.FxYhsj;
import com.power.common.mybatis.DaoHelper;
import com.power.report.entity.JkrbDataVO;
import com.power.report.entity.JtlybVO;
import com.power.report.entity.KhcllfyListVO;
import com.power.report.entity.LtmstxfVO;
import com.power.report.entity.TxfsrVO;
import com.power.report.entity.TxfsrpjVO;

/**
 * 项目名称：power2016 <br>
 * 类名称：ReportService <br>
 * 创建时间：2017-4-8 下午6:15:46 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("reportService")
public class ReportService {
	@Autowired
	private DaoHelper daoHelper;

	/**
	 * 流量/费用 统计
	 * 年 流量 费用
	 * 月 流量 费用
	 * @param param
	 * @return
	 */
	public List<KeyValue> getLlfyData(Map<String, Object> param) {
		return daoHelper.findAll("mapper.ReportMapper.getLlfyData",param);
	}
	
	public List<FxYhsj> jkrbReport(Map<String,Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getReportData",map);
	}

	/**
	 * 缴款日报数据列表
	 * @param map
	 * @return
	 */
	public List<JkrbDataVO> getJkrbData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getJkrbData",map);
	}

	/**
	 * 路段 客、货、免征车流量 汇总
	 * @param map
	 * @return
	 */
	public List<KhcllfyListVO> getKhcllData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getKhcllData",map);
	}
	/**
	 * 路段 客、货、免征车 收入 汇总
	 * @param map
	 * @return
	 */
	public List<KhcllfyListVO> getKhcsrData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getKhcsrData",map);
	}

	/**
	 * 绿通数据查询
	 * @param map
	 * @return
	 */
	public List<FxLtsj> getLtsjData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getLtsjData",map);
	}

	/**
	 * 通行费收入及票据
	 * @param map
	 * @return
	 */
	public List<TxfsrpjVO> getTxfsrpjData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getTxfsrpjData",map);
	}
	/**
	 * 交通量月报
	 * @param map
	 * @return
	 */
	public List<JtlybVO> getJtlybData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getjtlybData",map);
	}
	
	/**
	 * 通行费收入月报表
	 * @param map
	 * @return
	 */
	public List<TxfsrVO> editTxfsrData(HashMap<String, Object> map){
		return daoHelper.findAll("mapper.ReportMapper.gettxfsrData",map);
	}
	
	/**
	 * 绿通免收通行费及车流量明细月报表
	 * @param map
	 * @return
	 */
	public List<LtmstxfVO> getLtmstxfData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getLtmstxfData",map);
	}
	/**
	 * 绿通免收通行费及车流量明细月报表本年
	 * @param map
	 * @return
	 */
	public List<LtmstxfVO> getLtmstxfbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getLtmstxfbnData",map);
	}
	/**
	 * 绿通免收通行费及车流量明细月报表本月
	 * @param map
	 * @return
	 */
	public List<LtmstxfVO> getLtmstxfbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getLtmstxfbyData",map);
	}
	/**
	 * 绿通免收通行费及车流量明细月报表自开通
	 * @param map
	 * @return
	 */
	public List<LtmstxfVO> getLtmstxfzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getLtmstxfzktData",map);
	}
}

package com.power.report.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.base.sys.entity.KeyValue;
import com.power.common.entity.FxYhsj;
import com.power.common.mybatis.DaoHelper;
import com.power.report.entity.CrkcllDataVO;
import com.power.report.entity.CzjzbDataVO;
import com.power.report.entity.JdxmzDataVO;
import com.power.report.entity.JkrbDataVO;
import com.power.report.entity.KhcllfyListVO;
import com.power.report.entity.DlxxbDataVO;
import com.power.report.entity.LtmxbDataVO;

/**
 * 项目名称：power2016 <br>
 * 类名称：ReportService <br>
 * 创建时间：2017-4-8 下午6:15:46 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("leaderReportService")
public class LeaderReportService {
	@Autowired
	private DaoHelper daoHelper;


	/**
	 * 绿通明细表数据
	 * @param map
	 * @return
	 */
	public List<LtmxbDataVO> getltmxbData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getltmxbData",map);
	}

	
	/**
	 * 道路信息表
	 * @param map
	 * @return
	 */
	public List<DlxxbDataVO> getDlxxbData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getDlxxbData",map);
	}

	/**
	 * 超载计重表
	 * @param map
	 * @return
	 */
	public List<CzjzbDataVO> getCzjzbData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getCzjzbData",map);
	}

	/**
	 * 出入口车流量表
	 * @param map
	 * @return
	 */
	public List<CrkcllDataVO> getCrkcllData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getCrkcllData",map);
	}


	public List<JdxmzDataVO> getJdxmzData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.ReportMapper.getJdxmzData",map);
	}

}

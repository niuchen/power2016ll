package com.power.report.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.common.mybatis.DaoHelper;
import com.power.report.entity.LdtxfrDataVO;
import com.power.report.entity.TxfcllrDataVO;
import com.power.report.entity.txfjcllmxhzDataVO;

/**
 * 项目名称：power2016ll <br>
 * 类名称：ReportService <br>
 * 创建时间：2017-4-8 下午6:15:46 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("YhReportService")
public class YhReportService { 
	@Autowired
	private DaoHelper daoHelper;

	public List<TxfcllrDataVO> getTxfcllrData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.YhReportMapper.getTxfcllrData",map);
	}

	public List<TxfcllrDataVO> getTxfcllyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.YhReportMapper.getTxfcllyData",map);
	}
	public List<txfjcllmxhzDataVO> getYhTxfyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.YhReportMapper.getYhTxfyData",map);
	}
	
	public List<txfjcllmxhzDataVO> getYdTxfyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.YhReportMapper.getYdTxfyData",map);
	}
	
	public List<txfjcllmxhzDataVO> getEtcTxfyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.YhReportMapper.getEtcTxfyData",map);
	}
	
	public List<txfjcllmxhzDataVO> getHzTxfyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.YhReportMapper.getHzTxfyData",map);
	}
	
	public List<LdtxfrDataVO> getLdTxfyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.YhReportMapper.getLdTxfyData",map);
	}
}

	

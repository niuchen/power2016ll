package com.power.report.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.common.entity.FxCgcl;
import com.power.common.entity.FxHmd;
import com.power.common.entity.FxLsk;
import com.power.common.entity.FxLtcl;
import com.power.common.entity.FxLtsj;
import com.power.common.entity.FxPjgl;
import com.power.common.entity.FxQzzh;
import com.power.common.mybatis.DaoHelper;
import com.power.report.entity.BbfxsmDataVO;
import com.power.report.entity.FcxllDataVO;
import com.power.report.entity.IcksyqktjbDataVO;
import com.power.report.entity.JellpmDataVO;
import com.power.report.entity.TxfsrpjVO;
import com.power.report.entity.txfchsrhzbVO;
import com.power.report.entity.txfcllpmbDataVO;
import com.power.report.entity.txfjcllmxhzDataVO;
/**
 * 项目名称：power2016ll <br>
 * 类名称：ReportService <br>
 * 创建时间：2017-4-8 下午6:15:46 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("LyReportService")
public class LyReportService {
	@Autowired
	private DaoHelper daoHelper;

	public List<JellpmDataVO> getJellpmData(HashMap<String, Object> map) {
		
		return daoHelper.findAll("mapper.ReportMapper.getJellpmData",map);
	}

	public List<FxPjgl> getPjglData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getPjglData",map);
	}
	public List<TxfsrpjVO> getTxfsrpjData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfsrpjData",map);
	}
	public List<TxfsrpjVO> getTxfsrpjchData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfsrpjchData",map);
	}
	/**
	 * 报表分析说明年度
	 * @param map
	 * @return
	 */
	public List<BbfxsmDataVO> getBbfxsmndData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getBbfxsmndData",map);
	}
	/**
	 * 报表分析说明月份
	 * @param map
	 * @return
	 */
	public List<BbfxsmDataVO> getBbfxsmData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getBbfxsmData",map);
	}
	
	/**
	 * 02通行费拆后收入汇总表自开通累计
	 * @param map
	 * @return
	 */
	public List<txfchsrhzbVO> getTxfchsrzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrzktData",map);
	}
	
	/**
	 * 02通行费拆后收入汇总表
	 * @param map
	 * @return
	 */
	public List<txfchsrhzbVO> getTxfchsrhzData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrhzData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getTxfchsrmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrmxData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getTxfchsrmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrmxbnData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getTxfchsrmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrmxbyData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getTxfchsrmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrmxzktData",map);
	}
	
	/**
	 * 03通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllmxData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllmxbnData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllmxbyData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllmxzktData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllckmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllckmxData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllckmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllckmxbnData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllckmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllckmxbyData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllckmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllckmxzktData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllcrkmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllcrkmxData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 06车流量本年
	 
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllcrkmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllcrkmxbnData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 06车流量本月
	 * 
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllcrkmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllcrkmxbyData",map);
	}
	/**
	 * 03通行费及车流量明细表
	 * 06自开通
	 * 车流量自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllcrkmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllcrkmxzktData",map);
	}
	/**
	 * 03通行费及车流量汇总表
	 * 
	 * 汇总金额
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getTxfhzData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfhzData",map);
	}
	/**
	 * 03通行费及车流量汇总表
	 * 汇总流量
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllhzData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllhzData",map);
	}
	
	
	/**
	 * 04通行费及车流量汇总表
	 * 
	 * 汇总金额
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getTxfhzhjData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfhzhjData",map);
	}
	/**
	 * 04通行费及车流量汇总表
	 * 自开通
	 * 汇总金额
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getTxfhzhjzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getTxfhzhjzktData",map);
	}
	/**
	 * 04通行费及车流量汇总表
	 * 汇总流量
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllhzhjData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllhzhjData",map);
	}
	/**
	 * 04通行费及车流量汇总表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getCllhzhjzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getCllhzhjzktData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcTxfchsrmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcTxfchsrmxData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcTxfchsrmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcTxfchsrmxbnData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcTxfchsrmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcTxfchsrmxbyData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcTxfchsrmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcTxfchsrmxzktData",map);
	}
	
	/**
	 * 08通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllmxData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllmxbnData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllmxbyData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllmxzktData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllckmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllckmxData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllckmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllckmxbnData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllckmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllckmxbyData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllckmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllckmxzktData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllcrkmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllcrkmxData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllcrkmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllcrkmxbnData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllcrkmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllcrkmxbyData",map);
	}
	/**
	 * 08通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllcrkmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllcrkmxzktData",map);
	}
	
	/**
	 * 08通行费及车流量汇总表
	 * 
	 * 汇总金额
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcTxfhzData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcTxfhzData",map);
	}
	/**
	 * 08通行费及车流量汇总表
	 * 汇总流量
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getEtcCllhzData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getEtcCllhzData",map);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 09通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcTxfchsrmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcTxfchsrmxData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcTxfchsrmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcTxfchsrmxbnData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcTxfchsrmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcTxfchsrmxbyData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcTxfchsrmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcTxfchsrmxzktData",map);
	}
	
	/**
	 * 09通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllmxData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllmxbnData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllmxbyData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllmxzktData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllckmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllckmxData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllckmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllckmxbnData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllckmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllckmxbyData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllckmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllckmxzktData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 10天
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllcrkmxData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllcrkmxData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 本年
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllcrkmxbnData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllcrkmxbnData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 本月
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllcrkmxbyData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllcrkmxbyData",map);
	}
	/**
	 * 09通行费及车流量明细表
	 * 自开通
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllcrkmxzktData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllcrkmxzktData",map);
	}
	
	/**
	 * 09通行费及车流量汇总表
	 * 
	 * 汇总金额
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcTxfhzData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcTxfhzData",map);
	}
	/**
	 * 09通行费及车流量汇总表
	 * 汇总流量
	 * @param map
	 * @return
	 */
	public List<txfjcllmxhzDataVO> getMtcCllhzData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getMtcCllhzData",map);
	}

	public List<FcxllDataVO> getFcxllData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getFcxllData",map);
	}
	public List<FcxllDataVO> getFcxjeData(HashMap<String, Object> map) {
		return daoHelper.findAll("mapper.LyReportMapper.getFcxjeData",map);
	}
	
	
	//ic卡使用情况统计表
		public List<IcksyqktjbDataVO> getIcksyqktjbData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getIcksyqktjbData",map);
		}
		//通行费、车流量排名表
		public List<txfcllpmbDataVO> getTxfpmbData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfpmbData",map);
		}
		public List<txfcllpmbDataVO> getcllpmbData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getcllpmbData",map);
		}
		//流失卡表
		public List<FxLsk> getZclskyjntxftjbData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getZclskyjntxftjbData",map);
		}
		//灰名单表
		public List<FxHmd> getHmdclsqbData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.gethmdclsqbData",map);
		}
		//闯关事故车辆表
		public List<FxCgcl> getCgsgclbData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getcgsgclbData",map);
		}
		//强制组合表
		public List<FxQzzh> getQzzhqkbData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getqzzhqkbData",map);
		}
		//实收金额本月
		public List<txfjcllmxhzDataVO> getTxfsrybbyData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrmxbyData",map);
		}
		//实收金额本年
		public List<txfjcllmxhzDataVO> getTxfsrybbnData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrmxbnData",map);
		}
		//实收金额自开通
		public List<txfjcllmxhzDataVO> getTxfsrybzktData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrmxzktData",map);
		}
		//实收金额上月
		public List<txfjcllmxhzDataVO> getTxfsrybsyData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrmxsyData",map);
		}
		//实收金额上年同期
		public List<txfjcllmxhzDataVO> getTxfsrsntqData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrmxsntqData",map);
		}
		//实收金额上年累计
		public List<txfjcllmxhzDataVO> getTxfsrsnData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchsrmxsnData",map);
		}
		//车流量上月
		public List<txfjcllmxhzDataVO> getCllcrkmxsyData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getCllcrkmxsyData",map);
		}
		//车流量上年同期
		public List<txfjcllmxhzDataVO> getCllcrkmxsntqData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getCllcrkmxsntqData",map);
		}
		//车流量上年累计
		public List<txfjcllmxhzDataVO> getCllcrkmxsnData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getCllcrkmxsnData",map);
		}
		//拆后金额本月
		public List<txfjcllmxhzDataVO> getTxfchjebyData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchjebyData",map);
		}
		//拆后金额本年
		public List<txfjcllmxhzDataVO> getTxfchjebnData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchjebnData",map);
		}
		//拆后金额自开通
		public List<txfjcllmxhzDataVO> getTxfchjezktData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchjezktData",map);
		}
		//拆后金额上月
		public List<txfjcllmxhzDataVO> getTxfchjesyData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchjesyData",map);
		}
		//拆后金额上年同期
		public List<txfjcllmxhzDataVO> getTxfchjesntqData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchjesntqData",map);
		}
		//拆后金额上年累计
		public List<txfjcllmxhzDataVO> getTxfchjesnData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getTxfchjesnData",map);
		}
		//绿通车辆月报本月
		public List<FxLtcl> getLtclbyData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getLtclbyData",map);
		}
		//绿通车辆月报本年
		public List<FxLtcl> getLtclbnData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getLtclbnData",map);
		}
		//绿通车辆月报自开通
		public List<FxLtcl> getLtclzktData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getLtclzktData",map);
		}
		//绿通种类
		public List<FxLtsj> getLtzlData(HashMap<String, Object> map) {
			return daoHelper.findAll("mapper.LyReportMapper.getLtzlData",map);
		}
}

	

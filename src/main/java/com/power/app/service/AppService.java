package com.power.app.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.app.entity.EtcVO;
import com.power.app.entity.FyzbVO;
import com.power.app.entity.KhlxVO;
import com.power.app.entity.LlfyVO;
import com.power.app.entity.LtsjVO;
import com.power.app.entity.YhsjVO;
import com.power.base.sys.entity.AuthUserVO;
import com.power.base.sys.entity.KeyValue;
import com.power.base.sys.service.UserService;
import com.power.common.entity.SysDepartment;
import com.power.common.entity.Sztqsj;
import com.power.common.mybatis.DaoHelper;

/**
 * 项目名称：power2016 <br>
 * 类名称：AppService <br>
 * 创建时间：2017-4-5 下午3:06:43 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("appService")
public class AppService {
	@Autowired
	private DaoHelper daoHelper;
	/**
	 * APP 用户登录 
	 * @param username
	 * @param password
	 * @return
	 */
	public AuthUserVO appLogin(String username, String password){
		AuthUserVO user  = (AuthUserVO)daoHelper.findObject("mapper.SysAuthorizeMapper.getAuthUserByLoginName",username);
		if (user == null){
			throw new AuthenticationException("用户名不存在: " + username);
		}
		if(!UserService.validatePassword(password, user.getPwd())){
			throw new AuthenticationException("密码错误!");
		}
		return user;
	}
	/**
	 * 全部子部门
	 * @return
	 */
	public List<SysDepartment> getAllDeptList() {
		return daoHelper.findAll("mapper.AppMapper.getAllDept");
	}
	/**
	 * 首页征收完成情况
	 * @param param
	 * @return
	 */
	public List<KeyValue> getNzsrw(Map<String, Object> param) {
		return daoHelper.findAll("mapper.AppMapper.getNzsrw",param);
	}
	public List<KeyValue> getZswcqk(Map<String, Object> param) {
		return daoHelper.findAll("mapper.AppMapper.getZswcqk",param);
	}
	/**
	 * ETC Data
	 * @param param
	 * @return
	 */
	public EtcVO getEtcData(Map<String, Object> param) {
		return (EtcVO) daoHelper.findObject("mapper.AppMapper.getEtcData",param);
	}
	/**
	 * lt Data
	 * @param param
	 * @return
	 */
	public List<LtsjVO> getLtData(Map<String, Object> param) {
		return daoHelper.findAll("mapper.AppMapper.getLtData",param);
	}
	/**
	 * @param param
	 * @return
	 */
	public YhsjVO getYhData(Map<String, Object> param) {
		return (YhsjVO) daoHelper.findObject("mapper.AppMapper.getYhData",param);
	}
	/**
	 * @param param
	 * @return
	 */
	public List<KeyValue> getzstsData(Map<String, Object> param) {
		return daoHelper.findAll("mapper.AppMapper.getzstsData",param);
	}
	/**
	 * @param param
	 * @return
	 */
	public List<KeyValue> getchsrzsData(Map<String, Object> param) {
		return daoHelper.findAll("mapper.AppMapper.getchsrzsData",param);
	}
	/**
	 * @param param
	 * @return
	 */
	public List<FyzbVO> getFyzbData(Map<String, Object> param) {
		return daoHelper.findAll("mapper.AppMapper.getFyzbData",param);
	}
	/**
	 * @param param
	 * @return
	 */
	public KhlxVO getKhLxData(Map<String, Object> param) {
		return (KhlxVO) daoHelper.findObject("mapper.AppMapper.getKhLxData",param);
	}
	/**
	 * @param param
	 * @return
	 */
	public List<KeyValue> getcctsData(Map<String, Object> param) {
		return daoHelper.findAll("mapper.AppMapper.getcctsData",param);
	}
	/**
	 * @param param
	 * @return
	 */
	public List<LlfyVO> getLLFY(Map<String, Object> param) {
		return daoHelper.findAll("mapper.AppMapper.getLLFY",param);
	}
	/**
	 * @return
	 */
	public List<Sztqsj> getSztqsjList() {
		return daoHelper.findAll("mapper.SztqsjMapper.getSztqsjList");
	}
	/**
	 * 添加特情数据
	 * @param tqsj
	 */
	public void newSztqsj(Sztqsj tqsj) {
		daoHelper.insert(tqsj);
	}
	/**
	 * 通讯录信息
	 * @return
	 */
	public List<AuthUserVO> getUserList() {
		return daoHelper.findAll("mapper.AppMapper.getUserList");
	}
	/**
	 * @param xh
	 * @return
	 */
	public Sztqsj getSztqMx(Integer xh) {
		return daoHelper.findObject(Sztqsj.class, xh);
	}
	 public List<KeyValue> getJjrData(Map<String, Object> param)
	  {
	    return this.daoHelper.findAll("mapper.AppMapper.getJjrData", param);
	  }
	  
	  public BigDecimal getZdYc(String sszd)
	  {
	    return (BigDecimal)this.daoHelper.findObject("mapper.AppMapper.getZdYc", sszd);
	  }
	}



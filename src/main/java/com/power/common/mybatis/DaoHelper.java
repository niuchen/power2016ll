package com.power.common.mybatis;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.power.common.mybatis.page.Page;

/**
 * Mybatis 数据库Dao层帮助类
 * 项目名称：base_ms <br>
 * 类名称：DaoHelper <br>
 * 创建时间：2015-5-29 下午12:33:18 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Repository("daoHelper")
public class DaoHelper{
	public static Logger logger = Logger.getLogger(DaoHelper.class);
	public static final String PACKAGE = "mapper.";
	public static final String INSERT = "Mapper.insert";
	public static final String INSERT_SELECTIVE = "Mapper.insertSelective";
	public static final String UPDATE = "Mapper.updateByPrimaryKey";
	public static final String UPDATE_SELECTIVE = "Mapper.updateByPrimaryKeySelective";
	public static final String DELETE_KEY = "Mapper.deleteByPrimaryKey";
	public static final String SELECT_KEY = "Mapper.selectByPrimaryKey";
	 
	@Resource(name = "sqlSessionTemplate") 
	private SqlSessionTemplate  sqlSessionFactory;
	public void setSqlSessionTemplate(SqlSessionTemplate  sqlSessionFactory) {
		this.sqlSessionFactory=sqlSessionFactory;
	}
	/**
	  * 新增信息
	  * @param record
	  * @return 影响行数
	  */
	 public int insert(Object record) {
		 return sqlSessionFactory.insert(PACKAGE+record.getClass().getSimpleName() + INSERT, record);
	 }
	 /**
	  * 新增信息
	  * @param record
	  * @return 影响行数
	  */
	 public int insertSelective(Object record) {
		 return sqlSessionFactory.insert(PACKAGE+record.getClass().getSimpleName() + INSERT_SELECTIVE, record);
	 }
	 /**
	  * 新增信息
	  * @param statement
	  * @param record
	  * @return 影响行数
	  */
	public int insert(String statement,Object record){
		return sqlSessionFactory.insert(statement, record);
	}
	/**
	  * 更新信息
	  * @param record
	  * @return 影响行数
	  */
	 public int update(Object record) {
		 return sqlSessionFactory.update(PACKAGE+record.getClass().getSimpleName() + UPDATE, record);
	 }
	 /**
	  * 更新信息
	  * @param record
	  * @return 影响行数
	  */
	 public int updateSelective(Object record) {
		 return sqlSessionFactory.update(PACKAGE+record.getClass().getSimpleName() + UPDATE_SELECTIVE, record);
	 }
	 /**
	  * 修改对象,并返回修改数目
	  * @param statementName
	  * @param record
	  * @return
	  */
	public int update(String statementName,Object record){
		return sqlSessionFactory.update(statementName, record);
	}
	 /**
	  * 删除对象
	  * @param entityClass
	  * @param pk
	  * @return
	  */
	 public <T,PK> int delete(Class<T> entityClass,PK pk) {
	  return sqlSessionFactory.delete(PACKAGE+entityClass.getSimpleName() + DELETE_KEY, pk);
	 }
	 /**
	 *  @describe 删除数据 
	 *  @param statement
	 *  @param parameter
	 *  @return
	 */
	public int delete(String statement,Object parameter){
		return sqlSessionFactory.delete(statement, parameter);
	}
	
	/**
	 * 查找对象
	 * @param entityClass
	 * @param pk
	 * @return
	 */
	public <T,PK> T findObject(Class<T> entityClass,PK pk){
		return sqlSessionFactory.selectOne(PACKAGE+entityClass.getSimpleName() +SELECT_KEY, pk);
	}
	/**
	 *  @describe 查找对象
	 *  @param statement
	 *  @param parameter
	 *  @return
	 */
	public Object findObject(String statement,Object parameter){
		return sqlSessionFactory.selectOne(statement, parameter);
	}
	
	/**
	 * 分页查找
	 * @param statement
	 * @param parameter
	 * @param page 分页对象
	 * @return
	 */
	public <T> List<T> findRows(String statementName,Object parameter,Page page){
		return sqlSessionFactory.selectList(statementName, parameter,page);
	}
	/**
	 * 不分页进行查找
	 * @param statementName
	 * @param parameter
	 * @return
	 */
	public <T> List<T> findAll(String statementName,Object parameter){
		return sqlSessionFactory.selectList(statementName, parameter);
	}
	/**
	 * 不分页进行查找
	 * @param statementName
	 * @param parameter
	 * @return
	 */
	public <T> List<T> findAll(String statementName){
		return sqlSessionFactory.selectList(statementName);
	}
	/**
	 * 返回Map<K,V>
	 * @param statementName
	 * @param parameter
	 * @param mapKey
	 * @return
	 */
	public <K,V> Map<K,V> findMap(String statementName,Object parameter,String mapKey){
		return sqlSessionFactory.selectMap(statementName,parameter,mapKey);
	}
	
	/**
	 * @return
	 */
	public String findTableKeyUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}
}

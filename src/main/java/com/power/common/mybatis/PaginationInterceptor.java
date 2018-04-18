package com.power.common.mybatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.power.common.mybatis.dialect.Dialect;
import com.power.common.mybatis.dialect.MySQLDialect;
import com.power.common.mybatis.page.Page;

/**
 * Mybatis 分页拦截器,只拦截select部分
 * 项目名称：base_ms <br>
 * 类名称：PaginationInterceptor <br>
 * 创建时间：2015-5-29 下午1:47:19 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Intercepts({@Signature(type = Executor.class,method = "query",args = { MappedStatement.class, Object.class,RowBounds.class, ResultHandler.class }) })
public class PaginationInterceptor implements Interceptor {
	protected Log log = LogFactory.getLog(this.getClass());//
	Dialect dialect = new MySQLDialect();//

	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		if (args[2] == null || !(args[2] instanceof Page)) {
			return invocation.proceed();
		}
		Long st = System.currentTimeMillis();
		// 是否分页
		Page page = (Page) args[2];
		MappedStatement ms = (MappedStatement) args[0];
		BoundSql boundSql = ms.getBoundSql(args[1]);
		String originalSql = boundSql.getSql().trim();
		if (boundSql == null || StringUtils.isBlank(originalSql)) {
			return null;
		}
		// 得到总记录数
		if (page.getTotal() == 0) {// 对符合条件的数据进行统计 生成总页数
			BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), getCountSql(originalSql), boundSql.getParameterMappings(), boundSql.getParameterObject());
	        args[0] = MSUtils.newCountMappedStatement(ms,new BoundSqlSqlSource(newBoundSql));
	        args[2] = RowBounds.DEFAULT;
	        Object result = invocation.proceed();
	        //设置总数
	        page.setTotal((Integer) ((List) result).get(0));
		}
		// 分页查询 本地化对象 修改数据库注意修改实现
		if (page.getTotal() > page.getPS()) {
			originalSql = generatePageSql(originalSql, page, dialect);
		}
		log.debug(originalSql);
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), originalSql,boundSql.getParameterMappings(), boundSql.getParameterObject());
		MappedStatement newMs = copyFromMappedStatement(ms,new BoundSqlSqlSource(newBoundSql));
		args[0] = newMs;
		page.setRows((List) invocation.proceed());
		log.debug("sql time:" + (System.currentTimeMillis() - st));
		return null;
	}
    
    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;
        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }
    public void setProperties(Properties arg0) {}

    private void setParameters(PreparedStatement statement,MappedStatement mappedStatement,BoundSql boundSql,Object parameterObject)throws SQLException{
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler.setParameters(statement);
    }
    //
    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
	    if (ms.getKeyProperties() != null) {
	        for (String keyProperty : ms.getKeyProperties()) {
	            builder.keyProperty(keyProperty);
	        }
	    }
	    builder.timeout(ms.getTimeout());
	    builder.parameterMap(ms.getParameterMap());
	    builder.resultMaps(ms.getResultMaps());
	    builder.cache(ms.getCache());
	    return builder.build();
    }
    /**
     * 查询总纪录数
     * @param sql
     * @param page
     * @param mappedStatement
     * @param boundSql
     * @throws SQLException
     */
    public void getCount(String sql,Page page,final MappedStatement mappedStatement,final BoundSql boundSql) throws SQLException {
        final String countSql = getCountSql(sql);
        log.debug("COUNT SQL:"+countSql);
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
        	if (conn == null){
        		conn = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
            }
            statement = conn.prepareStatement(countSql);
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            setParameters(statement, mappedStatement, countBS, boundSql.getParameterObject());
            rs = statement.executeQuery();
            long totalCount = 0;
            if(rs.next()){
                totalCount = rs.getInt(1);
            }
            page.setTotal(totalCount);
        } catch (SQLException e) {
            log.error("PagePlugin error get total count:",e);
        }finally{
            try {
                rs.close();
                statement.close();
            } catch (SQLException e) {
                log.error("PagePlugin error close rs or stmt:",e);
            }
        }
    }

	/**
	 * 将SQL语句变成一条语句，并且每个单词的间隔都是1个空格
	 * @param sql SQL语句
	 * @return 如果sql是NULL返回空，否则返回转化后的SQL
	 */
	private String getLineSql(String sql) {
		return sql.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
	}
	/**
	 * 判断括号"()"是否匹配,并不会判断排列顺序是否正确
	 * @param text 要判断的文本
	 * @return 如果匹配返回TRUE,否则返回FALSE
	 */
	private boolean isBracketCanPartnership(String text) {
		if (text == null || (getIndexOfCount(text, '(') != getIndexOfCount(text, ')'))) {
			return false;
		}
		return true;
	}
	/**
	 * 得到一个字符在另一个字符串中出现的次数
	 * @param text	文本
	 * @param ch    字符
	 */
	private int getIndexOfCount(String text, char ch) {
		int count = 0;
		for (int i = 0; i < text.length(); i++) {
			count = (text.charAt(i) == ch) ? count + 1 : count;
		}
		return count;
	}
	/**
	 * 得到SQL第一个正确的FROM的的插入点
	 */
	private int getAfterFormInsertPoint(String querySelect) {
		String regex = "\\s+FROM\\s+";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(querySelect);
		while (matcher.find()) {
			int fromStartIndex = matcher.start(0);
			String text = querySelect.substring(0, fromStartIndex);
			if (isBracketCanPartnership(text)) {
				return fromStartIndex;
			}
		}
		return 0;
	}
	/**
	 * 得到最后一个Order By的插入点位置
	 * @return 返回最后一个Order By插入点的位置
	 */
	private int getLastOrderInsertPoint(String querySelect){
		int orderIndex = querySelect.toLowerCase().lastIndexOf("order by");
		if (orderIndex == -1 || !isBracketCanPartnership(querySelect.substring(orderIndex,querySelect.length()))) {
			orderIndex = 0;
//			throw new RuntimeException("SQL 2005 分页必须要有Order by 语句!");
		}
		return orderIndex;
	}
	
	public String getCountSql(String sql){
		sql = getLineSql(sql);
		int orderIndex  = getLastOrderInsertPoint(sql);
		int formIndex   = getAfterFormInsertPoint(sql);
		String select   = sql.substring(0, formIndex);
		StringBuffer reBuf = new StringBuffer(sql.length());
		
		//如果SELECT 中包含 DISTINCT 只能在外层包含COUNT
		if (select.toLowerCase().indexOf("select distinct") != -1 || sql.toLowerCase().indexOf("group by")!=-1) {
			reBuf.append("select count(1) count_ from (");
			if(orderIndex ==0){
				reBuf.append(sql.substring(0));
			}else{
				reBuf.append(sql.substring(0, orderIndex));
			}
			reBuf.append(" ) t_");
		} else {
			reBuf.append("select count(1) count_ ");
			if(orderIndex ==0){
				reBuf.append(sql.substring(formIndex));
			}else{
				reBuf.append(sql.substring(formIndex, orderIndex));
			}
		}
		return reBuf.toString();
	}
    /**
     * 根据数据库方言，生成特定的分页sql
     * @param sql     Mapper中的Sql语句
     * @param page    分页对象
     * @param dialect 方言类型
     * @return 分页SQL
     */
    public String generatePageSql(String sql, Page page, Dialect dialect) {
        if (dialect.supportsLimit()) {
            return dialect.getLimitString(sql, page.Skip(), page.Max());
        } else {
            return sql;
        }
    }
}

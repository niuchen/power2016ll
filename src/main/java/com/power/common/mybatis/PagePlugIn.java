package com.power.common.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
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
//@Intercepts({@Signature(type = Executor.class,method = "query",args = { MappedStatement.class, Object.class,RowBounds.class, ResultHandler.class }) })
public class PagePlugIn implements Interceptor {
	protected Log log = LogFactory.getLog(this.getClass());//
	Dialect dialect = new MySQLDialect();//

    public Object intercept(Invocation invocation) throws Throwable {
    	Object[] args = invocation.getArgs();
        if (args[2] == null || !(args[2] instanceof Page)) {
        	return invocation.proceed();
        }
        Long st = System.currentTimeMillis();
        //
        Page page = (Page) args[2];//
        //获取原始的ms
        MappedStatement ms = (MappedStatement) args[0];
        MetaObject metaObject = SystemMetaObject.forObject(ms);
        BoundSql boundSql = ms.getBoundSql(args[1]);
        String originalSql = boundSql.getSql().trim();//yuan shi sql
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), getCountSql(originalSql), boundSql.getParameterMappings(), boundSql.getParameterObject());
        //
//        msCountMap.put(ms.getId(), );
        //忽略RowBounds-否则会进行Mybatis自带的内存分页
        args[0] = MSUtils.newCountMappedStatement(ms,new BoundSqlSqlSource(newBoundSql));
        args[2] = RowBounds.DEFAULT;
        //替换MS
        //
        Object result = invocation.proceed();
      //设置总数
        page.setTotal((Integer) ((List) result).get(0));
        if (page.getTotal() == 0) {
        	return null;
        }
        if(page.getTotal()>page.getPS()){
        	originalSql = generatePageSql(originalSql,page,dialect);
        }
        log.debug(originalSql);
        //
        //还原ms
        args[0] = ms;
        //
//        args[1] = processParameter(ms,args[1],boundSql);
        //得到处理结果
        page.setRows((List) invocation.proceed());
        System.out.println("time:"+ (System.currentTimeMillis()-st));
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
            return dialect.getLimitString(sql, (page.getPage() - 1) * page.getPS(), page.getPS());
        } else {
            return sql;
        }
    }
}

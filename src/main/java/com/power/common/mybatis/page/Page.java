package com.power.common.mybatis.page;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.session.RowBounds;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 分页信息
 * 项目名称：front <br>
 * 类名称：Page <br>
 * 创建时间：2012-4-13 上午09:02:40 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class Page extends RowBounds implements Serializable   { //
	@JSONField(serialize=false)
    private static final long        serialVersionUID = 558475703658683233L;
    protected int                    pS= 10;//每页默认总行数
    protected int                    page= 1;//默认当前页
    protected long                   total= 0;//总行数
    protected long                   pages=0;//总页数
    protected List<? extends Object> rows;//数据列

    public long getPages() {
    	if (total > 0){
            return total % this.pS == 0 ? total / this.pS : total / this.pS + 1;
        }else{
            return 1;
        }
	}
	public void setPages(long pages) {
		this.pages = pages;
	}
	public Page() {
        if (page < 1)
            this.page = 1;
    }
    public Page(int ps, int page, long total, List<? extends Object> rows) {
        super();
        this.pS = ps;
        this.page = page;
        this.total = total;
        this.rows = rows;
    }

    public Page(int ps, int page) {
        super();
        this.pS = ps;
        this.page = page;
    }

    /**
     * 返回 分页 起始行
     * @return
     */
    public int Skip() {
        return (this.page - 1) * this.pS ;
    }

    /**
     * 行数
     * @return
     */
    public int Max() {
        return this.pS;
    }
    public int getPS() {
        return pS;
    }

    public void setPS(int ps) {
        this.pS = ps;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<? extends Object> getRows() {
        return rows;
    }

    public void setRows(List<? extends Object> rows) {
        this.rows = rows;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE, true, true);
    }
}

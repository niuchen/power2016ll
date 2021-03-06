package com.power.common.mybatis.page;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * 
 * 项目名称：base <br>
 * 类名称：LigerGrid <br>
 * 创建时间：2014-1-17 上午10:12:06 <br>
 * @author LRF
 * @version 1.0
 */
public class LigerGrid extends Page{
    private static final long serialVersionUID = -5286202009820063662L;
    private String                 sortname;
    /** 升序或者降序; desc;asc */
    private String                 sortorder;

    public LigerGrid() {
        if (page < 1)  this.page = 1;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE, true, true);
    }

    public String getSortname() {
        return sortname;
    }

    public void setSortname(String sortname) {
        this.sortname = sortname;
    }

    public String getSortorder() {
        return sortorder;
    }

    public void setSortorder(String sortorder) {
        this.sortorder = sortorder;
    }
}

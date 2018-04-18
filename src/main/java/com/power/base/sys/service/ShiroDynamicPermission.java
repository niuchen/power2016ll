package com.power.base.sys.service;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.power.base.sys.entity.PrivilegeVO;
import com.power.common.shiro.dynamic.DynamicPermissionDao;

/**
 * 动态权限  实现类
 * 项目名称：power2016 <br>
 * 类名称：ShiroDynamicPermission <br>
 * 创建时间：2016-11-2 上午11:04:41 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class ShiroDynamicPermission implements DynamicPermissionDao {
    /**
     * 资源权限的配置字符串模板
     */
    public static final String PREMISSION_STRING="perms[{0}]";
    /**
     * 角色权限的配置字符串模板
     */
    public static final String ROLE_STRING="roles[{0}]";
    
	@Override
	public Map<String, String> getDefinitionsMap() {
		 //获取url和角色及细粒度权限的list生成LinkedHashMap
		List<PrivilegeVO> priList = UserUtils.getAllRoleAndPerms();
        LinkedHashMap<String,String> linkedHashMap=Maps.newLinkedHashMap();
        String roleStr="",permStr="";
        for(PrivilegeVO pri:priList){
            if(pri!=null){
//            	List<String> roles = pri.getRoles();
//            	if(roles!=null && roles.size()>0){
//            		roleStr=ROLE_STRING.replace("{0}", Joiner.on(",").skipNulls().join(roles));
//            	}
            	List<String> perms = pri.getPerms();
            	if(perms!=null && perms.size()>0){
            		permStr=MessageFormat.format(PREMISSION_STRING,Joiner.on(",").skipNulls().join(perms));
            	}
//            	String s = Joiner.on(",").join(roleStr,permStr);
//              System.err.println("-----------"+pri.getUrl()+"="+permStr);
                linkedHashMap.put(pri.getUrl(),permStr);
            }
        }
        linkedHashMap.put("/**", "authc");
        return linkedHashMap;
	}

}

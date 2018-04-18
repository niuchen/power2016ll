package com.power.common.shiro.dynamic;
import java.util.Map;

/**
 * 动态权限资源获取DAO接口,根据自己需要实现<br>
 * 从文件/数据库来加载权限map<ant url,comma-delimited chain definition>
 */
public interface DynamicPermissionDao {
  Map<String,String> getDefinitionsMap();
}
package com.power.common.shiro.dynamic;

import java.util.Map;

import org.apache.shiro.config.Ini;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * shiro动态权限管理，</br>
 * 配置权限时，尽量不要使用/**来配置（重置时将被清除，如果要使用，在实现类中最后添加key="/**"，value="anon"），每个链接都应该配置独立的权限信息</br>
 *
 */
public class DynamicPermissionService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private AbstractShiroFilter shiroFilter;
    private DynamicPermissionDao dynamicPermissionDao;
	private String definitions = "";

    public synchronized void init() {
        logger.debug("初始化filter加权限信息，在servlet的init方法之前。");
        reloadPermission();
        logger.debug("初始化filter权限信息成功，开始执行servlet的init方法。");
    }

    public synchronized void reloadPermission() {
        logger.debug("reload资源权限配置开始！");
        try {
            Map<String, String> pers = generateSection();
            DefaultFilterChainManager manager = getFilterChainManager();
            manager.getFilterChains().clear();
            addToChain(manager, pers);
        } catch (Exception e) {
            logger.error("reload资源权限配置发生错误！", e);
        }
        logger.debug("reload资源权限配置结束！");
    }

    private DefaultFilterChainManager getFilterChainManager() throws Exception {
        // 获取过滤管理器
        PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                .getFilterChainResolver();
        DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
        return manager;
    }

    private void addToChain(DefaultFilterChainManager manager, Map<String, String> definitions) throws Exception {
        if (definitions == null || CollectionUtils.isEmpty(definitions)) {
            return;
        }
        //移除/**的过滤器链，防止新加的权限不起作用。
        manager.getFilterChains().remove("/**");
        for (Map.Entry<String, String> entry : definitions.entrySet()) {
            String url = entry.getKey();
            String chainDefinition = entry.getValue().trim().replace(" ", "");
            manager.createChain(url, chainDefinition);
        }
    }

    public synchronized void updatePermission(Map<String, String> newDefinitions) {
        logger.debug("更新资源配置开始！");
        try {
            // 获取和清空初始权限配置
            DefaultFilterChainManager manager = getFilterChainManager();
//            newDefinitions.put("/**","anon");
            addToChain(manager, newDefinitions);
            logger.debug("更新资源权限配置成功。");
        } catch (Exception e) {
            logger.error("更新资源权限配置发生错误!", e);
        }
    }

    /**
     * 将静态配置的资源与要添加的动态资源整合在一起，生成shiro使用的权限map
     * {@see org.apache.shiro.spring.web.ShiroFilterFactoryBean#setFilterChainDefinitions(String)}
     *
     * @return Section
     */
    private Map<String,String> generateSection() {
        Ini ini = new Ini();
        ini.load(definitions); // 加载资源文件节点串定义的初始化权限信息
        Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS); // 使用默认节点
        if (CollectionUtils.isEmpty(section)) {
            section = ini.getSection(Ini.DEFAULT_SECTION_NAME);//如不存在默认节点切割,则使用空字符转换
        }
        /**
         * 加载非初始化定义的权限信息
         */
        Map<String, String> permissionMap = loadDynamicPermission();
        if (!CollectionUtils.isEmpty(permissionMap)) {
            if (CollectionUtils.isEmpty(section)) {
                logger.error("*********获取初始静态配置URL权限映射失败，将使用外部加载权限信息进行配置*********");
                return permissionMap;
            } else {
                section.putAll(permissionMap);
            }
        }
        return section;
    }

    /**
     * 加载动态权限资源配置,map<ant url,comma-delimited chain definition>
     * @return map
     */
    private Map<String, String> loadDynamicPermission() {
        Map<String,String> map=dynamicPermissionDao.getDefinitionsMap();
        if(CollectionUtils.isEmpty(map)){
            map=Maps.newLinkedHashMapWithExpectedSize(2);
            logger.error("**********没有进行初始化权限配置，将配置为全部不用验证！**********");
        }
        logger.debug("*************自定义权限配置完成，将其余链接设置为不用验证*************");
        return map;
    }

    public AbstractShiroFilter getShiroFilter() {
        return shiroFilter;
    }

    public void setShiroFilter(AbstractShiroFilter shiroFilter) {
        this.shiroFilter = shiroFilter;
    }

    public String getDefinitions() {
        return definitions;
    }

    public void setDefinitions(String definitions) {
        this.definitions = definitions;
    }
    public void setDynamicPermissionDao(DynamicPermissionDao dynamicPermissionDao) {
		this.dynamicPermissionDao = dynamicPermissionDao;
	}
}
package com.power.common.freemarker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.power.common.utils.FileNameUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 创建静态页面
 * 项目名称：front <br>
 * 类名称：StaticPageUtil <br>
 * 创建时间：2012-5-8 上午02:35:57 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("freemarkerUtil")
public class StaticPageUtil  {
	private static final Logger logger = LoggerFactory.getLogger(StaticPageUtil.class);
    /**
     * 加载freemarker 模板
     * @param ftlFile 模板文件
     * @return
     */
    public Template getTempldate(String ftlFile){
        Configuration config=new Configuration();
        String path=ftlFile.substring(0,ftlFile.lastIndexOf("/"));
        String fname=ftlFile.substring(ftlFile.lastIndexOf("/")+1);
        logger.info("path:{},fname:{}",path,fname);
        try {
            config.setDefaultEncoding("UTF-8");
            //获取模板 目录
            config.setDirectoryForTemplateLoading(new File(path));
            return config.getTemplate(fname);
        } catch (IOException e) {
        	logger.error("模板未找到！！",e);
        }
        return null;                
    }
    /**
     * 创建静态输出页面
     * @param root model
     * @param tplName 模板名称
     * @param outFileName 输出文件 "/xx/xx.html"
     * @return
     */
    public boolean createHtml1(Map root,String tplName,String outFileName){         
        try {
            Template t = getTempldate(tplName);
            //静态文件输出路径
            File outfile=new File(outFileName);
            File dir = outfile.getParentFile();            
            if(!dir.exists()){
                dir.mkdirs();
            }
            //文件路径及文件名
            Writer out = new OutputStreamWriter(new FileOutputStream(outfile),"UTF-8");
            t.process(root, out);
            out.flush();
            return true;
        }catch (Exception e) {
        	logger.error("createHtml Error",e);
        }
        return false;
    }
    /**
     * 创建静态文件
     * @param root data model
     * @param tplName 模板名称
     * @param outPath 输出路径
     * @return res/h/201205/02121212xxx.html <br/> null
     */
    public String createHtml(Map root,String tplName,String outPath){         
        try {
            Template t = getTempldate(tplName);
            String result = outPath+"/h"+FileNameUtils.genPathName()+"/"+FileNameUtils.genFileName(".html");
            //静态文件输出路径
            File outfile=new File(result);
            File dir = outfile.getParentFile();            
            if(!dir.exists()){
                dir.mkdirs();
            }
            //文件路径及文件名
            Writer out = new OutputStreamWriter(new FileOutputStream(outfile),"UTF-8");
            t.process(root, out);
            out.flush();
            return result;
        }catch (Exception e) {
        	logger.error("createHtml Error",e);
        }
        return null;
    }
}

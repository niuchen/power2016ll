package com.power.common.springmvc;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpServletResponse帮助类 <br>
 * 项目名称：ywt2.0 <br>
 * 类名称：ResponseUtils <br>
 * 创建时间：2011-12-6 上午02:40:25 <br>
 * @author LRF <br>
 * @version 1.0
 */
public final class ResponseUtils {
    private static final Logger log = LoggerFactory.getLogger(ResponseUtils.class);

    /**
     * 发送文本。使用UTF-8编码。
     * 
     * @param response HttpServletResponse
     * @param text 发送的字符串
     */
    public static void renderText(HttpServletResponse response, String text) {
        render(response, "text/plain;charset=UTF-8", text);
    }
    
    public static void renderHtml(HttpServletResponse response, String text) {
    	render(response, "text/html;charset=UTF-8", text);
    }
    /**
     * 发送json。使用UTF-8编码。
     * 
     * @param response HttpServletResponse
     * @param text 发送的字符串
     */
    public static void renderJson(HttpServletResponse response, String text) {
        render(response, "application/json;charset=UTF-8", text);
    }

    /**
     * 发送xml。使用UTF-8编码。
     * 
     * @param response HttpServletResponse
     * @param text 发送的字符串
     */
    public static void renderXml(HttpServletResponse response, String text) {
        render(response, "text/xml;charset=UTF-8", text);
    }
    
    /**
     * 发送js 脚本。使用UTF-8编码。<br/>
     * <code>
     *  ResponseUtils.renderScript(rsp,"alert('OK');");
     * </code>
     * @param response HttpServletResponse
     * @param text 发送的脚本内容
     */
    public static void renderScript(HttpServletResponse response, String text) {
        render(response, "text/plain;charset=UTF-8", "<script>"+text+"</script>");//type=\"text/javascript\"
    }
    /**
     * 发送内容。使用UTF-8编码。
     * 
     * @param response
     * @param contentType
     * @param text
     */
    public static void render(HttpServletResponse response, String contentType, String text) {
        response.setContentType(contentType);
        response.setStatus(200);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(text);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 在 web层抛出 500,异常信息
     * 
     * @param rsp HttpServletResponse
     * @param e Exception
     */
    public static void throwException(HttpServletResponse rsp, Exception e) {
        rsp.setStatus(500);
        rsp.setCharacterEncoding("UTF-8");
        try {
            if (e.getCause() == null || e.getCause().getCause() == null) {
                rsp.getWriter().print(e.getMessage());
            } else {
                rsp.getWriter().print("操作失败！\n原因:\n" + e.getCause().getCause().getMessage());
            }
        } catch (IOException e1) {
            log.error("抛出异常！");
        }
    }

    /**
     * 在 web层抛出 500,异常信息
     * @param rsp HttpServletResponse
     * @param e String 错误信息
     */
    public static void throwException(HttpServletResponse rsp, String e) {
        rsp.setStatus(500);
        rsp.setCharacterEncoding("UTF-8");
        try {
            rsp.getWriter().print(e);
        } catch (IOException e1) {
            log.error("抛出异常！");
        }
    }

}

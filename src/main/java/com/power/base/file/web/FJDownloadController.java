package com.power.base.file.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.power.base.file.service.FJService;
import com.power.common.utils.FileRepository;

/**
 * 项目名称：ams <br>
 * 类名称：WkUploadController <br>
 * 创建时间：2014-4-25 下午2:22:14 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Controller
@Scope("prototype")
@RequestMapping("fj")
public class FJDownloadController {

	private final Logger log = LoggerFactory.getLogger(FJDownloadController.class);
	
	@Resource(name="fileRepository")
	private FileRepository fileUtil;
	public void setFileUtil(FileRepository fileUtil) {
		this.fileUtil = fileUtil;
	}
	@Resource(name="fjImpl")
	private FJService fjImpl;
	public void setFjImpl(FJService fjImpl) {
		this.fjImpl = fjImpl;
	}

	private ResponseEntity<byte[]> re(byte[] by,String fileName){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<byte[]>(by, headers, HttpStatus.OK);
	}

	/**
	 * 对文件流输出下载的中文文件名进行编码 
	 * 屏蔽各种浏览器版本的差异性
	 * @throws UnsupportedEncodingException
	 */
	public static String getFileName(HttpServletRequest request, String pFileName)throws UnsupportedEncodingException {
		String filename = null;
		String agent = request.getHeader("USER-AGENT");
		if (null != agent) {
			if (-1 != agent.indexOf("Firefox")) {// Firefox
				filename = "=?UTF-8?B?"+ (new String(Base64.encodeBase64(pFileName.getBytes("UTF-8")))) + "?=";
			} else if (-1 != agent.indexOf("Chrome")) {// Chrome
				filename = new String(pFileName.getBytes(), "ISO8859-1");
			} else {// IE7+
				filename = URLEncoder.encode(pFileName, "UTF-8");
				filename = StringUtils.replace(filename, "+", "%20");// 替换空格
			}
		} else {
			filename = pFileName;
		}
		return filename;
	}
	/**
	 * 附件信息下载
	 * @param req
	 * @param headers
	 * @param fn
	 * @param path
	 * @return
	 */
    @RequestMapping("down")
    @ResponseBody
	public ResponseEntity<byte[]> download(HttpServletRequest req,@RequestParam("xh")Integer xh){
//		AuthUserVO user = (AuthUserVO) req.getAttribute(Constants.USER_KEY);
//    	if(user==null|| user.getRoleId()>3){
//    		log.error("/down get User null");
//    		return re("无操作权限.".getBytes(),"error.txt");
//    	}
//    	//TODO 1、下载权限验证；2、记录下载信息，更新下载次数
//    	DownLoadVO wj = fjImpl.editWjVO(xh,user.getUid());
//    	try {
//    		String fn = getFileName(req,wj.getName())+"."+wj.getExt();
//    		return re(FileUtils.readFileToByteArray(new File(Constants.IMG_ABS_PATH+wj.getPath())),fn);
//		} catch (IOException e) {
//			log.error("down doc error.{}",e.getMessage());
//		}
			return re("文件不存在.".getBytes(),"error.txt");
	}
	
	
}

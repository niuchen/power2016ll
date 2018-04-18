package com.power.base.file.web;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Splitter;
import com.power.base.sys.service.UserService;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.SysUser;
import com.power.common.springmvc.ResponseUtils;
import com.power.common.utils.FileRepository;
import com.power.common.utils.Global;
import com.power.common.utils.UploadUtils;

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
public class FJUploadController {

	private final Logger log = LoggerFactory.getLogger(FJUploadController.class);
	
	@Autowired
	private UserService userService;
	
	@Resource(name="fileRepository")
	private FileRepository fileUtil;
	public void setFileUtil(FileRepository fileUtil) {
		this.fileUtil = fileUtil;
	}
	
    private String rspMsg(int code,String message) {
        StringBuffer sb = new StringBuffer("{\"status\":0").append(",\"msg\":\"").append(message).append("\"}");
        return sb.toString();
    }
	private String insertEditor(String name,String saveUrl) {
	    StringBuffer sb = new StringBuffer("{\"status\":1,\"url\":\"").append(saveUrl).append("\"}");
        return sb.toString();
    }
	/**
     * work 工作图片 上传
     * @param file 
     * @param request
     * @param rsp
     */
    @RequestMapping(value = "hdImg")
    public void upload(@RequestParam(value = "uploadFile", required = false) MultipartFile file,
    		HttpServletRequest request,HttpServletResponse rsp) {
    	//判断文件是否存在
    	if (null == file) {
			ResponseUtils.renderHtml(rsp, rspMsg(-1,"文件不存在"));
			return ;
		}
//		//判断扩展名是否正确  
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(Locale.ENGLISH);
//		if (!Arrays.asList(Constants.IMAGETYPES).contains(ext)) {
//			ResponseUtils.renderHtml(rsp, rspMsg(3,""));
//			return ;
//		}
//		//文件大小判断
//		long fileSize = file.getSize();
//		if (fileSize > Constants.UPLOAD_IMG_MAXSIZE) {
//			ResponseUtils.renderHtml(rsp, rspMsg(4,Constants.UPLOAD_IMG_MAXSIZE/(1024*1024)+""));
//			return ;
//		}
		String fileUrl = "";
		try {
		   fileUrl = fileUrl + fileUtil.storeByExt("/power2016/src/main/webapp/res/uImg", ext, file);
		   //图片压缩 
		   // 缩略图 --> 提交完成后就删除
		   //fileUtil.markThumb(fileUrl, 1024);
		   
		   log.info("fileURl"+"---------------->"+fileUrl);
		}catch (IOException e) {
		   log.error("upload file error!", e);
		   ResponseUtils.renderHtml(rsp, rspMsg(-1,"上传图片失败！"));
		   return ;
		}
		ResponseUtils.renderHtml(rsp,insertEditor(FilenameUtils.getBaseName(origName),fileUrl));
    }
	
    @RequestMapping(value = "scImg")
    public void upImg(@RequestParam(value = "uploadFile", required = false) MultipartFile file,
    		HttpServletRequest request,HttpServletResponse rsp) {
    	//判断文件是否存在
    	if (null == file) {
			ResponseUtils.renderHtml(rsp, rspMsg(-1,"文件不存在"));
		}
//		//判断扩展名是否正确  
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(Locale.ENGLISH);
//		if (!Arrays.asList(Constants.IMAGETYPES).contains(ext)) {
//			ResponseUtils.renderHtml(rsp, rspMsg(3,""));
//			return ;
//		}
//		//文件大小判断
//		long fileSize = file.getSize();
//		if (fileSize > Constants.UPLOAD_IMG_MAXSIZE) {
//			ResponseUtils.renderHtml(rsp, rspMsg(4,Constants.UPLOAD_IMG_MAXSIZE/(1024*1024)+""));
//			return ;
//		}
		String fileUrl = "";
		String filename = "F:/Myeclipse-2014/Workspaces/MyEclipse Professional 2014/power2016/src/main/webapp/";
		try {
			fileUrl = fileUrl + fileUtil.storeByExt(filename+"res/uImg", ext, file);
			log.info("fileURl"+"---------------->"+fileUrl);
		   //图片压缩 
		   // 缩略图 --> 提交完成后就删除
		   //fileUtil.markThumb(fileUrl, 1024);
			SysUser user = new SysUser();
			user.setHeadicon(fileUrl.replace(filename, ""));
			user.setUserid(UserUtils.getAuthUser().getUid());
			userService.editUser(user);
		}catch (IOException e) {
		   log.error("upload file error!", e);
		   ResponseUtils.renderHtml(rsp, rspMsg(-1,"上传图片失败！"));
		}
		ResponseUtils.renderHtml(rsp, rspMsg(1, "上传成功"));
    }
    
    /**
     * im 文件传输返回
     * @param code
     * @param msg
     * @param name
     * @return
     */
    private String imMsg(int code,String msg,String name) {
        StringBuffer sb = new StringBuffer("{\"code\":").append(code)
        		.append(",\"msg\":\"").append(msg)
        		.append("\",\"name\":\"").append(name).append("\"}");
        return sb.toString();
    }
    /**
     * im 文件传输
     * @param file
     * @param request
     * @param rsp
     */
    @RequestMapping(value = "imFile")
    public void imFile(@RequestParam(value = "file", required = false) MultipartFile file,HttpServletRequest request,HttpServletResponse rsp) {
    	//判断文件是否存在
    	if (null == file) {
			ResponseUtils.renderHtml(rsp, imMsg(-1,"文件不存在",""));
		}
//		//判断扩展名是否正确  
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(Locale.ENGLISH);
		if (!Splitter.on(",").trimResults().splitToList(Global.getConfig("im.file.suffix")).contains(ext)) {
			ResponseUtils.renderHtml(rsp, imMsg(3,"传输类型不允许",""));
			return ;
		}
//		//文件大小判断
		long fileSize = file.getSize();
		long maxSize = Long.valueOf(Global.getConfig("im.file.maxsize"));
		if (fileSize > maxSize) {
			ResponseUtils.renderHtml(rsp, imMsg(4,"允许传输最大文件["+maxSize/(1024*1024)+"]M",""));
			return ;
		}
		String fileUrl = Global.getConfig("im.file.dir")+UploadUtils.getMonthPath()+origName;
		String filename = Global.getProjectResPath()+File.separator+fileUrl;
		try {
			fileUtil.storeByFilename(filename, file);
			log.info("fileURl"+"---------------->"+filename);
			
		}catch (IOException e) {
		   log.error("imFile file error!", e);
		   ResponseUtils.renderHtml(rsp, imMsg(-1,"发送失败！",""));
		}
		ResponseUtils.renderHtml(rsp, imMsg(0,"res/"+fileUrl.replaceAll("\\\\", "/"),origName));
    }
    
    
    
}


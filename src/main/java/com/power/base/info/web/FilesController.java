package com.power.base.info.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Splitter;
import com.power.base.info.entity.fileMessageVO;
import com.power.base.info.service.FilesService;
import com.power.base.sys.service.UserUtils;
import com.power.common.entity.BaseFileFolder;
import com.power.common.entity.BaseFileInfo;
import com.power.common.springmvc.ExtReturn;
import com.power.common.utils.FileRepository;
import com.power.common.utils.Global;
import com.power.common.utils.UploadUtils;


/**
 * 项目名称： power2016
 * 类名称： FilesController
 * 创建时间： 2016年11月9日 下午3:14:27
 * @author WJZ
 * @version 1.0
 */
@Controller
@RequestMapping("base/file")
public class FilesController {
	private static final Logger log=LoggerFactory.getLogger(FilesController.class);
	
	private String prefix = "base/file/";
	
	@Autowired
	private FilesService filesService;
	
	@Resource(name="fileRepository")
	private FileRepository fileUtil;
	public void setFileUtil(FileRepository fileUtil) {
		this.fileUtil = fileUtil;
	}
	/**
	 * 文件资料首页
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping("index")
	public String fileIndex(HttpServletRequest req,Model model){
		
		return prefix+"index";
	}
	/**
	 * 查询所有文件
	 * @param map
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("showAllFile")
	@ResponseBody
	public List<fileMessageVO> showAllFile(HashMap<String, Object> map)throws IOException{
		return filesService.showAllFile(null);
	}
	/**
	 * 查询文档文件或图片文件
	 * @param map
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("showFile")
	@ResponseBody
	public List<BaseFileInfo> showDocumentFile(@RequestParam("type") String type,HttpServletResponse rsp)throws IOException{
		HashMap<String, Object> map = new HashMap<String, Object>();
		if(type.equals("allDocument")){
			map.put("filetype", "'txt','doc'");
		}
		if(type.equals("allImage")){
			map.put("filetype", "'jpg','png'");
		}
		return filesService.showFileByFiletype(map);
	}
	/**
	 * 查询回收站文件
	 * @param map
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("showDeletemarkFile")
	@ResponseBody
	public List<BaseFileInfo> showFileByDeletemark(HashMap<String, Object> map)throws IOException{
		return filesService.showFileByDeletemark(null);
	}
	
	/**
	 * 文件上传页面
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping("uploadifyForm")
	public String uploadifyForm(HttpServletRequest req,Model model){
		return prefix+"uploadifyForm";
	}
	
    /**
     * 文件上传
     * @param file
     * @param request
     * @param model
     * @return
     */
	@RequestMapping("uploadDoc")
	@ResponseBody
	public String upload(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "folderid", required = false)String folderid,HttpServletResponse rsp,
			HttpServletRequest request, Model model) {
		// 判断文件是否存在
		if (null == file) {
			model.addAttribute("message", "请选择上传文件！");
			return prefix + "uploadifyForm";
		}
		//判断扩展名是否正确  
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(Locale.ENGLISH);
		if (!Splitter.on(",").trimResults().splitToList(Global.getConfig("upload.file.suffix")).contains(ext)) {
			model.addAttribute("message", "传输类型不允许！");
			return prefix + "uploadifyForm";
		}
		//文件大小判断
		long fileSize = file.getSize();
		long maxSize = Long.valueOf(Global.getConfig("upload.file.maxsize"));
		if (fileSize > maxSize) {
			model.addAttribute("message", "允许传输最大文件["+maxSize/(1024*1024)+"]M");
			return prefix + "uploadifyForm";
		}
		//String fileUrl = "";
		//String filename = "F:/Myeclipse-2014/Workspaces/MyEclipse Professional 2014/power2016/src/main/webapp/";
		String fileUrl = Global.getConfig("upload.file.dir")+UploadUtils.getMonthPath()+origName;
		String filename = Global.getProjectResPath()+File.separator+fileUrl;
		try {
			 // fileUrl = fileUrl + fileUtil.storeByExt(filename+"res/uploadify/uploadFile", ext, file);
			fileUtil.storeByFilename(filename, file);
			log.info("fileURl" + "---------------->" + fileUrl);
			BaseFileInfo fileInfo = new BaseFileInfo();
			fileInfo.setFilename(origName);
			fileInfo.setFolderid("0");
			fileInfo.setFilepath(fileUrl.replace(filename, ""));
			fileInfo.setFiletype(ext);
			filesService.newFileinfo(fileInfo);
		} catch (IOException e) {
			log.error("upload file error:{}", e);
			model.addAttribute("message", "上传文件失败-->" + e.getMessage());
			return prefix + "uploadifyForm";
		}
		model.addAttribute("fileUrl", fileUrl);
		return prefix + "uploadifyForm";
	}
	
	/**
	 * 新建文件夹页面
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping("folderForm")
	public String folderForm(HttpServletRequest req,Model model){
		return prefix+"newFolder";
	}
	
	@RequestMapping("newFolder")
	@ResponseBody
	public Object newFolder(HttpServletRequest req,BaseFileFolder folder){
		try {
			UserUtils.setObjectCreateUser(folder);
			filesService.newFolder(folder);
			return new ExtReturn(true,"操作成功");
		} catch (Exception e) {
			log.error("newFolder error:{}"+e);
			return new ExtReturn(false,"操作失败");
		}

	}
}

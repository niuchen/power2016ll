package com.power.base.info.service;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.base.info.entity.fileMessageVO;
import com.power.common.entity.BaseFileFolder;
import com.power.common.entity.BaseFileInfo;
import com.power.common.mybatis.DaoHelper;


/**
 * 项目名称： power2016
 * 类名称： FilesService
 * 创建时间： 2016年11月9日 下午3:15:49
 * @author WJZ
 * @version 1.0
 */
@Service("filesService")
public class FilesService {
	private static final Logger log = LoggerFactory.getLogger(FilesService.class);
	
	@Autowired
	private DaoHelper daoHelper;
	
	public List<fileMessageVO> showAllFile(HashMap<String, Object> map){
		return daoHelper.findAll("mapper.BaseFileInfoMapper.showAllFile", map);
	}
	
	public List<BaseFileInfo> showFileByFiletype(HashMap<String, Object> map){
		return daoHelper.findAll("mapper.BaseFileInfoMapper.showFileByFiletype", map);
	}
	
	public List<BaseFileInfo> showFileByDeletemark(HashMap<String, Object> map){
		return daoHelper.findAll("mapper.BaseFileInfoMapper.showFileByDeletemark", map);
	}
	
	public void newFileinfo(BaseFileInfo fileInfo){
		fileInfo.setFileid(daoHelper.findTableKeyUUID());
		
		daoHelper.insertSelective(fileInfo);
	}
	
	public void newFolder(BaseFileFolder folder){
		folder.setFolderid(daoHelper.findTableKeyUUID());
		daoHelper.insertSelective(folder);
	}
}

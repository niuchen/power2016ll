package com.power.common.utils;

import java.io.File;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储
 * 项目名称：ams <br>
 * 类名称：FileRepository <br>
 * 创建时间：2014-5-22 下午3:24:22 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Service("fileRepository")
public class FileRepository {
	
	private static final Logger log = LoggerFactory.getLogger(FileRepository.class);
	private final String IMG_ABS_PATH = "";
    /**
	 * 存储文件 
	 * @param path 存储路径
	 * @param ext 扩展名
	 * @param file Spring MultipartFile
	 * @return
	 * @throws IOException
	 */
    public String storeByExt(String path, String ext, MultipartFile file) throws IOException {
        String filename = UploadUtils.generateFilename(path, ext);
        File dest = new File(IMG_ABS_PATH+filename);
        store(file, dest);
        return filename;
    }
    /**
	 * 存储文件 重载
	 * @param path 存储路径
	 * @param ext 扩展名
	 * @param file File
	 * @return
	 * @throws IOException
	 */
    public String storeByExt(String path, String ext, File file) throws IOException {
		String filename = UploadUtils.generateFilename(path, ext);
		File dest = new File(IMG_ABS_PATH+filename);
		store(file, dest);
		return filename;
	}
    /**
	 * 存储文件 重载
	 * @param filename 特定文件名称
	 * @param file MultipartFile
	 * @return
	 * @throws IOException
	 */  
    public String storeByFilename(String filename, MultipartFile file)
            throws IOException {
        File dest = new File(IMG_ABS_PATH+filename);
        store(file, dest);
        return filename;
    }
    /**
	 * 存储文件 重载
	 * @param filename 特定文件名称
	 * @param file File
	 * @return
	 * @throws IOException
	 */
    public String storeByFilename(String filename, File file) throws IOException {
        File dest = new File(IMG_ABS_PATH+filename);
        store(file, dest);
        return filename;
    }

    private void store(MultipartFile file, File dest) throws IOException {
        try {
            UploadUtils.checkDirAndCreate(dest.getParentFile());
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("Transfer file error when upload file", e);
            throw e;
        }
    }

    private void store(File file, File dest) throws IOException {
        try {
            UploadUtils.checkDirAndCreate(dest.getParentFile());
            FileUtils.copyFile(file, dest);
        } catch (IOException e) {
            log.error("Transfer file error when upload file", e);
            throw e;
        }
    }
    /**
     * getFile by name
     * @param name /path/name
     * @return file
     */
    public File retrieve(String name) {
        return new File(IMG_ABS_PATH+name);
    }   
	
	/**
	 * 制作缩略图。
	 * @param sourcePath
	 * @param width
	 * @return
	 */
	public String markThumb(String sourcePath,int width) {
		//filename
		if(StringUtils.isNotBlank(sourcePath)){
			String sf = IMG_ABS_PATH+sourcePath;
			String filename = FilenameUtils.getBaseName(sf);
			String destFileName =  filename+"_"+width;
			log.info("source file:{}",sf);
			//new URL(sourcePath)
			File file = FileUtils.getFile(sf);
			if(file!=null && file.exists()){
				try {
					Thumbnails.of(file)
					          .width(width)
//							  .crop(Positions.CENTER)// 定位
							  .toFile(new File(sf.replace(filename, destFileName)));
					return sourcePath.replace(filename, destFileName);
				} catch (IOException e) {
					log.error("markThumb Error",e);
					return null;
				}
			}
		}
		return null;
	}
//	public String markThumb(String sourcePath,int x1,int y1,int x2,int y2,int width,int height) {
//		// 没有使用 URL 直接加载 
//		//filename
//		if(StringUtils.isNotBlank(sourcePath)){
//			String sf = IMG_ABS_PATH+sourcePath.replace(Constants.DOMAIN_PREFIX, "");
//			String filename = FilenameUtils.getBaseName(sf);
//			String destFileName =  filename+"_"+width+"x"+height;
//			log.info("source file:{}",sf);	
//			//new URL(sourcePath)
//			File file = FileUtils.getFile(sf);
//			if(file!=null && file.exists()){
//				try {
//					Thumbnails.of(file).sourceRegion(x1, y1, x2, y2).scale(1f)
//							  .toFile(new File(sf.replace(filename, destFileName)));
//					return sourcePath.replace(filename, destFileName);
//				} catch (IOException e) {
//					log.error("markThumb Error",e);
//					return null;
//				}
//			}
//		}
//		return null;
//	}

}

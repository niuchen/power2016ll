package com.power.common.utils;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * int 类型的时间操作工具类
 * 项目名称：front <br>
 * 类名称：IntTimeUtil <br>
 * 创建时间：2012-4-20 上午06:34:58 <br>
 * @author LRF <br>
 * @version 1.0
 */
public class IntTimeUtil {

	/**
	 * 返回int 类型的时间，精确到秒
	 * @return
	 */
	public static int getTime(){
		return (int) (System.currentTimeMillis()/1000);
	}
	/**
	 * 返回int 类型的时间，精确到秒
     * @param time
	 * @return
	 */
	public static int getTime(Long time){
		return (int) (time/1000);
	}
	/**
	 * 获取时间
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static int getTime(String time,String pattern){
		Date tmpDate = null;
		try {
			tmpDate = DateUtils.parseDate(time, pattern);
		} catch (ParseException e) {
			return 0;
		}
		return (int) (tmpDate.getTime()/1000);
	}
	/**
	 * @param time
	 * @return java.util.Date
	 */
	public static Date getDateTime(int time){
		return new Date(time*1000L);
	}
	/**
	 * format int time to String default pattern "yyyy-MM-dd HH:mm:ss"
	 * @param time
	 * @return String
	 */
	public static String getFormatDateTime(int time){		
		return DateFormatUtils.format(time*1000L, "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * format int time to String,
	 * @param time
	 * @param pattern
	 * @return String
	 */
	public static String getFormatDateTime(int time,String pattern){		
		return DateFormatUtils.format(time*1000L, pattern);
	}
	
	public static void main(String[] args) {
		int time = getTime();
		System.out.println(time);
		System.out.println(getDateTime(time));
		System.out.println(getFormatDateTime(time));
		System.out.println(getFormatDateTime(1343606400));
		
	}
}

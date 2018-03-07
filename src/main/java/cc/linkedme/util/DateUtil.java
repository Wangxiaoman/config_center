/*
 * Copyright 2007 HUAPU (http://www.huapu.com) 
 */
package cc.linkedme.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateUtil {
    
    private static Logger logger = Logger.getLogger(DateUtil.class);
    
	protected static String datePattern = "yyyy-MM-dd";
	
	
    /** 每秒的毫秒数 */
    private static final long MILLIONSECONDS_PER_SECOND = 1000;
    /** 日期时间格式 */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static String format(Date date, String pattern) {
		if(date != null){
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			return formatter.format(date);
		}
		return null;
	}
	
	// 传入两个string时间(HH:mm:ss),获得时间差
    public static long getSecondsBetweenDate(Date startDate, Date endDate) {
        return ((endDate.getTime() - startDate.getTime())/MILLIONSECONDS_PER_SECOND);
    }
    
    public static String getCurrent4yyyymmddhhmmss(){
        return DateUtil.format(new Date(), DEFAULT_DATETIME_FORMAT);
    }
    
    /**
     * @Title: parseFormatDate
     * @Description: 按照格式返回日期
     * @param @param aDateStr
     * @param @param aDateFmtStr
     * @param @return    设定文件
     * @return Date    返回类型
     * @throws
     */
     public static Date parseFormatDate(String aDateStr, String aDateFmtStr) {
         SimpleDateFormat smt = new SimpleDateFormat(aDateFmtStr);
         Date ret = null;
         if (aDateStr == null || aDateStr.equals(""))
             return null;
         try {
             ret = smt.parse(aDateStr.trim());
         } catch (ParseException e) {
             logger.error("parse format date error,ex:",e);
         }
         return ret;
     }
     
     // 获取date的之后的n天
     public static Date getDayBegin(Date date) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         calendar.set(Calendar.HOUR_OF_DAY, 0);
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MILLISECOND, 0);
         return calendar.getTime();
     }
     
     // 获取date的之后的n天
     public static Date getNextNDay(Date date, int n) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         calendar.add(Calendar.DAY_OF_YEAR, n);
         calendar.set(Calendar.HOUR_OF_DAY, 0);
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MILLISECOND, 0);
         return calendar.getTime();
     }
}

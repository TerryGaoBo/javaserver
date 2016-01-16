package com.dol.cdf.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
	public static final int SECOND = 1000;
	public static final int MINITE = 60 * SECOND;
	public static final int HOUR = 60 * MINITE;
	public static final int DAY = 24 * HOUR;
	
	/**
	 * 获取当前时间，去掉毫秒
	 * 
	 * @return
	 */
	public static int getCurrentTime() {
		long b = System.currentTimeMillis() / 1000;
		return (int) (b);
	}

	public static String formatDate(Date date) {
		return formatDateLong(date.getTime());
	}
	/**
	 * 把时间换成系统语言相关的格式
	 * 
	 * @param time
	 * @return
	 */
	public static String formatDateLong(long time)
	{
		if(time <= 0)
		{
			return "";
		}
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}
	
	/**
	 * 把时间换成系统语言相关的格式，无毫秒
	 * 
	 * @param time
	 * @return
	 */
	public static String formatDateInt(int timeEndSecond) {
		return formatDateLong(timeEndSecond * 1000L);
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String formatDateLongWithSec(long time) {
		if (time <= 0) {
			return "";
		}
		Date date = new Date(time);
		return sdf.format(date);
	}
	
	public static String getCurrentDateString()
	{
		Date date = new Date();
		return sdf.format(date);
	}

	/**
	 * 把时间换成系统语言相关的格式，无毫秒
	 * 
	 * @param time
	 * @return
	 */
	public static String formatDateLongWithSec(int timeEndSecond) {
		return formatDateLongWithSec(timeEndSecond * 1000L);
	}

	public static String formatDateLong(long time, String format)
	{
		if(time <= 0)
		{
			return "";
		}
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	public static long formatDateString(String time)
	{
		if(time == null)
		{
			return 0;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return sdf.parse(time).getTime();
		} catch (ParseException e) {
			return 0;
		}
	}
	
	public static Date formatDateStringShort(String time)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(time);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * converts a given time from seconds -> miliseconds
	 * @param seconds
	 * @return
	 */
	public static int convertSecondsToMiliseconds(int secondsToConvert)
	{
		return secondsToConvert * MINITE;
	}

	/**
	 * converts a given time from minutes -> miliseconds
	 * @param string
	 * @return
	 */
	public static int convertMinutesToMiliseconds(int minutesToConvert)
	{
		return minutesToConvert * MINITE;
	}

	/**
	 * converts a given time from minutes -> seconds
	 * @param string
	 * @return
	 */
	public static int convertMinutesToSeconds(int minutesToConvert)
	{
		return minutesToConvert * 60;
	}
	
	public static int convertMilisecondsToSeconds(long millis) {
		return (int)(millis / 1000);
	}
	
	/**
	 * 毫秒级别的时间戳的时间比较
	 * 
	 * @param oldTime
	 * @param newTime
	 * @return
	 */
    public static boolean isSameDay(long oldTime, long newTime) {
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTimeInMillis(oldTime);
        
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTimeInMillis(newTime);
        
        return oldCalendar.get(Calendar.DAY_OF_YEAR) == newCalendar.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * 与当前比较是否是同一天
     * @param oldTime
     * @return
     */
    public static boolean isSameDay(int oldTime) {
		return isSameDay(oldTime, getCurrentTime());
	}
    
	/**
	 * 秒级的时间戳的时间比较
	 *  
	 * @param oldTime
	 * @param newTime
	 * @return
	 */
	public static boolean isSameDay(int oldTime, int newTime) {
		Calendar oldCalendar = Calendar.getInstance();
		oldCalendar.setTimeInMillis(oldTime * 1000L);

		Calendar newCalendar = Calendar.getInstance();
		newCalendar.setTimeInMillis(newTime * 1000L);

		return oldCalendar.get(Calendar.DAY_OF_YEAR) == newCalendar.get(Calendar.DAY_OF_YEAR);
	}
	
    public static long getTomorrowFixHour(int hour)
	{
		Calendar c = Calendar.getInstance();
		
		Date date = new Date();
		
		c.setTime(date);
		
		c.add(Calendar.DATE, 1);
		
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTimeInMillis();
	}
    
    public static long getYesterdayFixHour(int hour)
	{
		Calendar c = Calendar.getInstance();
		
		Date date = new Date();
		
		c.setTime(date);
		
		c.add(Calendar.DATE, -1);
		
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTimeInMillis();
	}
    
    public static int nextYear(){
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.YEAR, 1);
    	return c.get(Calendar.YEAR);
    }
    
    public static long getTodayFixHour(int hour)
	{
		Calendar c = Calendar.getInstance();
		
		Date date = new Date();
		
		c.setTime(date);
		
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTimeInMillis();
	}
    
    public static long getTodayPlusMinute(int minute)
	{
		Calendar c = Calendar.getInstance();
		
		Date date = new Date();
		
		c.setTime(date);
		
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTimeInMillis();
	}
    
    public static int getEndOfDays(int from, int days) {
    	Calendar c = Calendar.getInstance();
		Date date = new Date(from*1000L);
		
		c.setTime(date);
		c.add(Calendar.DATE, days);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return (int) (c.getTimeInMillis()/1000);
    }
    
	private static final DateFormat DF_YEAR4_MON2_DAY2_UNDERLINE = new SimpleDateFormat("yyyy_MM_dd");
	
	private static final DateFormat CHINESE_FORMAT = new SimpleDateFormat("M月d日H时m分");
	
	public static String formatY4_M2_D2(long time)
	{
		Date date = new Date(time);
		return DF_YEAR4_MON2_DAY2_UNDERLINE.format(date);
	}
	
	public static String formatChineseTime(Date date) {
		return CHINESE_FORMAT.format(date);
	}
	
	/**
	 * 把时间换成系统语言相关的格式
	 * 
	 * @param time
	 * @return
	 */
	public static long formatY4_M2_D2Long(String time)
	{
		if(time==null)
		{
			return 0;
		}
		
		try {
			return DF_YEAR4_MON2_DAY2_UNDERLINE.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	
	public static Date getDayBeforeOrAfter(int dayCount) {
		Date date = new Date();
		return getDayBeforeOrAfter(date,dayCount);
	}
	public static Date getDayBeforeOrAfter(long time,int dayCount) {
		Date date = new Date(time);
		return getDayBeforeOrAfter(date,dayCount);
	}
	
	public static Date getDayBeforeOrAfter(Date date,int dayCount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, dayCount);
		return c.getTime();
	}
	
	public static Date getDayBeforeOrAfterWithSecond(Date date,	int second) {
		Calendar c = Calendar.getInstance();

		c.setTime(date);

		c.add(Calendar.SECOND, second);

		return c.getTime();
	}

	
	/**
	 * 返回两个时间戳差别的天数
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long diffDays(long time1, long time2) {
		return TimeUnit.MILLISECONDS.toDays(time1 - time2);
	}
	
	
	
	/**
	 * 返回两个时间戳差别的天数（过了12点就算一天）
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static int getDays(long from, long to) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(from);
		int yearFrom = c.get(Calendar.YEAR);
		int dayFrom = c.get(Calendar.DAY_OF_YEAR);
		int maxDayFrom = c.getActualMaximum(Calendar.DAY_OF_YEAR);
		c.setTimeInMillis(to);
		int yearTo = c.get(Calendar.YEAR);
		int dayTo = c.get(Calendar.DAY_OF_YEAR);
		if (yearFrom == yearTo) {
			return dayTo - dayFrom;
		} else {
			return dayTo + (maxDayFrom - dayFrom );
		}
	}
	public static int getDays(int from, int to) {
		long FROM = from * 1000L;
		long TO = to * 1000L;
		return getDays(FROM, TO);
	}
	
	public static int getDays(Date from, Date to) {
		return getDays(from.getTime(), to.getTime());
	}

	/**
	 * 固定月份的日期时间戳
	 * 
	 * @param months
	 * @param day
	 * @param hour
	 * @param minite
	 * @param second
	 * @return
	 */
	public static long getFixMonthDate(short[] months, int day, int hour, int minite, int second) {
		Calendar c = Calendar.getInstance();
		int year = -1;
		int thisYear = c.get(Calendar.YEAR);
		int thisMonth = Calendar.NOVEMBER;
		int month = -1;
		for (short m : months) {
			if (m - 1 > thisMonth) {
				month = m;
				year = thisYear;
				break;
			}
		}
		if (month == -1) {
			int nextYear = TimeUtil.nextYear();
			year = nextYear;
			month = months[0];
		}
		String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + minite + second;
		return TimeUtil.formatDateString(dateStr);
	}
	
	
	
	public static void main(String[] arge) {
		int dttm = TimeUtil.getEndOfDays(TimeUtil.getCurrentTime(), 1);
		System.out.println(dttm);
		System.out.println(TimeUtil.formatDateLong(dttm*1000L));
	}

}

package com.bjtl.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description: 日期转化工具类
 * @Author: leitianquan
 * @Date: 2020/07/09
 **/
public  class TimeChange {
    /**
     * 将时间转为UNIX时间格式
     * @param time
     * @return
     */
    public static String timeToUnixTime(String time){
        String newTime = time.substring(0,10)+"T"+time.substring(11)+".000Z";
        return newTime;
    }

    /**
     * 获取一天的开始时间
     * @param todayDate
     * @return
     */
    public static String getMinTodayTime(String todayDate){
        String startToday = TimeChange.timeToUnixTime(todayDate+" 00:00:00");
        return startToday;
    }

    /**
     *  获取一天的结束时间
     * @param todayDate
     * @return
     */
    public static String getMaxTodayTime(String todayDate){
        String endToday =TimeChange.timeToUnixTime(todayDate+" 23:59:59");
        return endToday;
    }
    /**
     * 获取本月第一天
     * @return
     */
    public static String getMinMouthTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String newTime = simpleDateFormat.format(calendar.getTime())+" 00:00:00";
        return TimeChange.timeToUnixTime(newTime);
    }

    /**
     * 获取本月最后一天
     * @return
     */
    public static  String getMaxMouthTime(){
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date());
        calendar2.set(Calendar.DAY_OF_MONTH, calendar2.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String newTime = simpleDateFormat.format(calendar2.getTime())+" 23:59:59";
        return  TimeChange.timeToUnixTime(newTime);
    }
}

package com.example.myapplication.utile;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {
    /** * 用于显示时间 */
    public static final String TODAY = "今天";
    public static final String YESTERDAY = "昨天";
    public static final String TOMORROW = "明天";
    public static final String BEFORE_YESTERDAY = "前天";
    public static final String AFTER_TOMORROW = "后天";
    /** * 获取对应时间戳转换的今天、明天。。。。的日期 * @param time * @return */
    public static String getToday(String time)  {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = null;
        try{

            date = new Date(Long.parseLong(time) * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            return showDateDetail(diffDay, time);

        }
        return time;
    }



    public static int getMonthLastDay(int year, int month)
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    public static int getCurrentMonthLastDay()
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /** * 将日期差显示为今天、明天或者星期 * @param diffDay * @param time * @return */
    private static String showDateDetail(int diffDay, String time){
        switch(diffDay){
            case 0:
                return TODAY;
            case 1:
                return TOMORROW;
            case 2:
                return AFTER_TOMORROW;
            case -1:
                return YESTERDAY;
            case -2:
                return BEFORE_YESTERDAY;
            default:
                return  getWeek(time);
        }
    }
    /** * 计算周几 */
    public static String getWeek(String data) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
//        long lcc = Long.valueOf(data);
        int i = Integer.parseInt(data);
        String times = sdr.format(new Date(i * 1000L));
        Date date = null;
        int mydate = 0;
        String week = "";
        try {
            date = sdr.parse(times);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            mydate = cd.get(Calendar.DAY_OF_WEEK);
            // 获取指定日期转换成星期几
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (mydate == 1) {
            week = "星期日";
        } else if (mydate == 2) {
            week = "星期一";
        } else if (mydate == 3) {
            week = "星期二";
        } else if (mydate == 4) {
            week = "星期三";
        } else if (mydate == 5) {
            week = "星期四";
        } else if (mydate == 6) {
            week = "星期五";
        } else if (mydate == 7) {
            week = "星期六";
        }
        return week;
    }
    /** * 计算周几 */
    public static String getdataWeek(String data) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
//        long lcc = Long.valueOf(data);
        int i = Integer.parseInt(data);
        String times = sdr.format(new Date(i * 1000L));
        Date date = null;
        int mydate = 0;
        String week = "";
        try {
            date = sdr.parse(times);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            mydate = cd.get(Calendar.DAY_OF_WEEK);
            // 获取指定日期转换成星期几
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        if (mydate == 1) {
            week = "日";
        } else if (mydate == 2) {
            week = "一";
        } else if (mydate == 3) {
            week = "二";
        } else if (mydate == 4) {
            week = "三";
        } else if (mydate == 5) {
            week = "四";
        } else if (mydate == 6) {
            week = "五";
        } else if (mydate == 7) {
            week = "六";
        }
        return week;
    }
    public static String getDateToString(long milSecond) {
        Date date = new Date(milSecond* 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getDateToStringY(long milSecond) {
        Date date = new Date(milSecond* 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(date);
    }
    public static String getDateToStringM(long milSecond) {
        Date date = new Date(milSecond* 1000);
        SimpleDateFormat format = new SimpleDateFormat("MM");
        return format.format(date);
    }
    public static String getDateToStringD(long milSecond) {
        Date date = new Date(milSecond* 1000);
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return format.format(date);
    }

    public static String getDateToStringH(long milSecond) {
        Date date = new Date(milSecond* 1000);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }
    public static String getDateHh(long milSecond) {
        Date date = new Date(milSecond* 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    //1-星期日
//
//2-星期一
//
//3-星期二
//
//4-星期三
//
//5-星期四
//
//6-星期五
//
//7-星期六
    public static int getDayofWeek(String dateTime) {
        Calendar cal = Calendar.getInstance();
        if (dateTime.equals("")) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(dateTime);
            } catch (ParseException e) {
                date = null;
                e.printStackTrace();
            }
            if (date != null) {
                cal.setTime(new Date(date.getTime()));
            }
        }
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static String timeStamp2Date(long time, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    public static String data(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-M-d",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    public static List<String> getTodayString(int year, int month) {
        List<String> mlist = new ArrayList<>();
        String data;
        int y = year;
        int m = month;
        int monthLastDay = 0;
        if (m < 10) {
            data = y + "年0" + m + "月";
        } else {
            data = y + "年" + m + "月";
        }
        String replace = data.replace("年", "-").replace("月", "-");
        String[] split = replace.split("-");
        if (!split[1].equals("10")) {
            String replace1 = split[1].replace("0", "");
            monthLastDay = DateUtils.getMonthLastDay(Integer.valueOf(split[0]), Integer.valueOf(replace1));
        } else {
            monthLastDay = DateUtils.getMonthLastDay(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
        }
        int dayofWeek = DateUtils.getDayofWeek(replace + "01");
//        if (dayofWeek == 2) {
//            mlist.add("");
//        } else if (dayofWeek == 3) {
//            mlist.add("");
//            mlist.add("");
//        } else if (dayofWeek == 4) {
//            mlist.add("");
//            mlist.add("");
//            mlist.add("");
//        } else if (dayofWeek == 5) {
//            mlist.add("");
//            mlist.add("");
//            mlist.add("");
//            mlist.add("");
//        } else if (dayofWeek == 6) {
//            mlist.add("");
//            mlist.add("");
//            mlist.add("");
//            mlist.add("");
//            mlist.add("");
//        } else if (dayofWeek == 7) {
//            mlist.add("");
//            mlist.add("");
//            mlist.add("");
//            mlist.add("");
//            mlist.add("");
//            mlist.add("");
//        }
        for (int j = 0; j < monthLastDay; j++) {
            if((j + 1)<10){
                mlist.add(replace+"0" + (j + 1));
            }else{
                mlist.add(replace+"" + (j + 1));
            }

        }
        return mlist;
    }
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
//        long seconds = (mss % (1000 * 60)) / 1000;
        return days + " 天 " + hours + " 小时 " + minutes + " 分钟 ";
    }
    public static void printRunningService(Context context){
        Log.e("tag","*****************************");
        Log.e("tag","***当前运行中服务");
        Log.e("tag","*****************************");
        ActivityManager activityManager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services=activityManager.getRunningServices(1000);
        for (int i = 0; i <services.size(); i++) {
            ActivityManager.RunningServiceInfo info=services.get(i);
            Log.e("tag",""+info.service.getClassName());
        }
        Log.e("tag","*****************************");
        Log.e("tag","***任务结束");
        Log.e("tag","*****************************");
    }
}

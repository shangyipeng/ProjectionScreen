package com.example.myapplication.utile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.myapplication.bean.WeekDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatePhotoUtils {
    public static boolean isPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(167)|(165)|(17[0,1,2,3,5,6,7,8])|(18[0-9])|(19[0-9]))\\d{8}$";
        if (phone.length() != 11) {

            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            return isMatch;
        }
    }

    public static long getTime(String user_time) {
        long l = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(user_time);
            l = d.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return l;
    }

    public static long getTime3(String user_time) {
        long l = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(user_time);
            l = d.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return l;
    }

    public static long getDayTime3() {
        return System.currentTimeMillis();
    }

    //比较传入时间和当前时间
    public static boolean geTimeSize(String user_time) {
        boolean aBoolean = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            long l1 = System.currentTimeMillis();
            if (l1 < l) {
                aBoolean = true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return aBoolean;
    }

    public static String getTimeExpend(String startTime, String endTime) {
        //传入字串类型 2016/06/28 08:30
        long longStart = getTime(startTime); //获取开始时间毫秒数
        long longEnd = getTime(endTime);  //获取结束时间毫秒数
//        long longExpend = longEnd - longStart;  //获取时间差

//        long longHours = longExpend / (60 * 60 * 1000); //根据时间差来计算小时数
//        long longMinutes = (longExpend - longHours * (60 * 60 * 1000)) / (60 * 1000);   //根据时间差来计算分钟数
//        long longsecond = (longExpend - longHours * (1000 * 60 * 60 * 24)  * (1000 * 60 * 60)  * (1000 * 60)) / 1000;

        long l = longEnd - longStart;
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (hour < 10) {
            if (min < 10) {
                if (s < 10) {
                    return "0" + hour + ":0" + min + ":0" + s;
                } else {
                    return "0" + hour + ":0" + min + ":" + s;
                }
            } else {
                if (s < 10) {
                    return "0" + hour + ":" + min + ":0" + s;
                } else {
                    return "0" + hour + ":" + min + ":" + s;
                }
            }
        } else {
            if (min < 10) {
                if (s < 10) {
                    return hour + ":0" + min + ":0" + s;
                } else {
                    return hour + ":0" + min + ":" + s;
                }
            } else {
                if (s < 10) {
                    return hour + ":" + min + ":" + s;
                } else {
                    return hour + ":" + min + ":0" + s;
                }
            }
        }

    }

    public static String getTime1(Long createtime) {
        SimpleDateFormat sdfTwo = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String d = "";
        try {
            Long aLong = Long.valueOf(createtime + "000");
            d = sdfTwo.format(aLong);
        } catch (Exception e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return d;
    }

    public static String getTime2(Long createtime) {
        SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String d = "";
        try {
//            Long aLong = Long.valueOf(createtime + "000");
            d = sdfTwo.format(createtime);
        } catch (Exception e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return d;
    }

    public static String getTime4(Long createtime) {
        SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String d = "";
        try {
            Long aLong = Long.valueOf(createtime + "000");
            d = sdfTwo.format(aLong);
        } catch (Exception e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return d;
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date getDate(String str) {
        try {
            java.text.SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(str);
            return date;
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("TAG", "异常  " + e.getMessage());
        }
        return null;
    }

    //行走轨迹 本周
    public static List<WeekDay> getWeekDay() {
        List<WeekDay> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // 获取本周的第一天
        int firstDayOfWeek = calendar.getFirstDayOfWeek();

        for (int i = 0; i < firstDayOfWeek; i++) {
            calendar.add(Calendar.DATE, -4);
        }
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, 1);
            WeekDay weekDay = new WeekDay();
            // 获取星期的显示名称，例如：周一、星期一、Monday等等
            weekDay.week = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
            weekDay.day = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            list.add(weekDay);
        }
        return list;
    }

    //行走轨迹 上周
    public static List<WeekDay> getWeekDay1() {
        List<WeekDay> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // 获取本周的第一天
        int firstDayOfWeek = calendar.getFirstDayOfWeek();

        for (int i = 0; i < firstDayOfWeek; i++) {
            calendar.add(Calendar.DATE, -11);
        }
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, 1);
            WeekDay weekDay = new WeekDay();
            // 获取星期的显示名称，例如：周一、星期一、Monday等等
            weekDay.week = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
            weekDay.day = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            list.add(weekDay);
        }
        return list;
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
            mlist.add("" + (j + 1));
        }
        return mlist;
    }

    //获取间隔天数
    public static List<WeekDay> getDifference(String beginTime, String endTime) {
        List<WeekDay> list = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//输入日期的格式
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(beginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        cal1.setTime(date1);
        cal2.setTime(date2);
        double dayCount = (cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (1000 * 3600 * 24);//从间隔毫秒变成间隔天数

        for (int i = 0; i < dayCount; i++) {
            WeekDay weekDay = new WeekDay();
            // 获取星期的显示名称，例如：周一、星期一、Monday等等
            weekDay.day = beforeAfterDate(i, beginTime);
            list.add(weekDay);
        }
        return list;
    }

    public static String beforeAfterDate(int days, String beginTime) {
        long nowTime = getTime3(beginTime);
        long changeTimes = days * 24L * 60 * 60 * 1000;
        return getStrTime(String.valueOf(nowTime + changeTimes), "yyyy-MM-dd HH:mm:ss");
    }

    //时间戳转字符串

    public static String getStrTime(String timeStamp, String format) {
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;

    }


    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }


    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public static void callPhone1(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    //yyyy-MM-dd
    public static String getCurrentTime() {
        long timecurrentTimeMillis = System.currentTimeMillis();
        String time2 = DatePhotoUtils.getTime2(timecurrentTimeMillis);
        return time2;
    }


    //时间往后推一年
    public static String getTime_Year(int day) {
        int i = (int) day / 12;//取整
        int j = day % 12;//取余
        int integer = 0;
        int integer1 = 0;
        long timecurrentTimeMillis = System.currentTimeMillis();
        String time2 = DatePhotoUtils.getTime2(timecurrentTimeMillis);
        String[] split = time2.split("-");
        String s = split[0];//年
        String s1 = split[1];//月
        integer = Integer.valueOf(s);
        integer1 = Integer.valueOf(s1);
        integer = integer + i;
        if (j != 0) {
            if ((integer1 + j) > 12) {
                integer = integer + 1;
                integer1 = (integer1 + j) - 12;
            }
        }
        return integer + "-" + integer1 + "-" + split[2];
    }

    //时间往后推一个月
    public static String getTime_Month() {
        long timecurrentTimeMillis = System.currentTimeMillis();
        String time2 = DatePhotoUtils.getTime2(timecurrentTimeMillis);
        String[] split = time2.split("-");
        String s = split[0];
        String s1 = split[1];
        int integer = Integer.valueOf(s);
        int integer1 = Integer.valueOf(s1);
        if (integer1 == 12) {
            integer1 = 1;
            integer = integer + 1;
        } else {
            integer1 = integer1 + 1;
        }
        return integer + "-" + integer1 + "-" + split[2];
    }


    public static String getTimes(String dateStart, String dateStop) {
        String mTime = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);
            //毫秒ms
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;

            long diffMinutes = diff / (60 * 1000) % 60;

            long diffHours = diff / (60 * 60 * 1000) % 24;

            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print("两个时间相差：");

            System.out.print(diffDays + " 天, ");

            System.out.print(diffHours + " 小时, ");

            System.out.print(diffMinutes + " 分钟, ");

            System.out.print(diffSeconds + " 秒.");

            if (diffDays != 0) {
                mTime += diffDays + " 天, ";
            } else if (diffHours != 0) {
                mTime += diffHours + " 小时, ";
            } else if (diffMinutes != 0) {
                mTime += diffMinutes + " 分钟, ";
            } else if (diffSeconds != 0) {
                mTime += diffSeconds + " 秒";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mTime;
    }
}

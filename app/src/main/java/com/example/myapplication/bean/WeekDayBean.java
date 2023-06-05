package com.example.myapplication.bean;

/**
 * 日期与周几
 */
public class WeekDayBean {
    String week;
    String day;
    boolean isSelect;
    long  dayTim;

    public long getDayTim() {
        return dayTim;
    }

    public void setDayTim(long dayTim) {
        this.dayTim = dayTim;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}

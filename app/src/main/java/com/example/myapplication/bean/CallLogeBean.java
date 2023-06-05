package com.example.myapplication.bean;

/**
 * 删除手机联系人
 */
public class CallLogeBean {
    String phone;//手机号码
    String type;//类型
    String date;//时间
    String time;//通话时长

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

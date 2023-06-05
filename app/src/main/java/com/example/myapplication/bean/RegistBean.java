package com.example.myapplication.bean;

import java.util.List;

/**
 * 注册返回bean
 */
public class RegistBean {
    int code;
    String  msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * FLnkID : 50108581-2e22-4672-bd24-9f749b73e1f4
         * Title : 如何注册账号？
         * Content : <div class="c-line-clamp3" data-a-5fa5e84a=""><div target="_blank" data-visited="off"
         */

        private String FLnkID;
        private String Title;
        private String Content;

        public String getFLnkID() {
            return FLnkID;
        }

        public void setFLnkID(String FLnkID) {
            this.FLnkID = FLnkID;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String Content) {
            this.Content = Content;
        }
    }
}

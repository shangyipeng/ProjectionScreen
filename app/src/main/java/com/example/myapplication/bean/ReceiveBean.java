package com.example.myapplication.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 接收数据返回
 */
public class ReceiveBean {
    /**
     * {"mobile":"18625196127","userId":4,"content":"测试数据"}
     */
    /**
     * mobile
     */
    @JSONField(name = "mobile")
    private String mobile;
    /**
     * userId
     */
    @JSONField(name = "userId")
    private Integer userId;
    /**
     * content
     */
    @JSONField(name = "content")
    private String content;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

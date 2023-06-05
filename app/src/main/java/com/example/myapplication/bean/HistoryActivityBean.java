package com.example.myapplication.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class HistoryActivityBean {

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
         * count
         */
        @JSONField(name = "count")
        private Integer count;
        /**
         * pageNo
         */
        @JSONField(name = "pageNo")
        private Integer pageNo;
        /**
         * pageSize
         */
        @JSONField(name = "pageSize")
        private Integer pageSize;
        /**
         * lists
         */
        @JSONField(name = "lists")
        private List<ListsDTO> lists;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getPageNo() {
            return pageNo;
        }

        public void setPageNo(Integer pageNo) {
            this.pageNo = pageNo;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public List<ListsDTO> getLists() {
            return lists;
        }

        public void setLists(List<ListsDTO> lists) {
            this.lists = lists;
        }

        public static class ListsDTO {
            /**
             * id
             */
            @JSONField(name = "id")
            private Integer id;
            /**
             * userId
             */
            @JSONField(name = "userId")
            private Integer userId;
            /**
             * type
             */
            @JSONField(name = "type")
            private Integer type;
            /**
             * content
             */
            @JSONField(name = "content")
            private String content;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Integer getUserId() {
                return userId;
            }

            public void setUserId(Integer userId) {
                this.userId = userId;
            }

            public Integer getType() {
                return type;
            }

            public void setType(Integer type) {
                this.type = type;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}

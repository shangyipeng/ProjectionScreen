package com.example.myapplication.bean;

public class ReceiveResultBean {
    String Type;//类型
    int Result;//结果
    String types;//发送或接收

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getResult() {
        return Result;
    }

    public void setResult(int result) {
        Result = result;
    }
}

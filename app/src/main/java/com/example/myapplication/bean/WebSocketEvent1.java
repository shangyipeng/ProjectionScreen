package com.example.myapplication.bean;

public class WebSocketEvent1 {
    private String message;

    public WebSocketEvent1(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

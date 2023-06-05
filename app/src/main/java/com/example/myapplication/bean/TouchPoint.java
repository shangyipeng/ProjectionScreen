package com.example.myapplication.bean;

public class TouchPoint {
    private String name;
    private int x;
    private int y;
    private int x1;
    private int y1;
    private int delay;

    public TouchPoint(String name, int x, int y, int delay) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.delay = delay;
    }
    public TouchPoint(String name, int x, int y, int delay,int x1, int y1) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
        this.delay = delay;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getDelay() {
        return delay;
    }
}

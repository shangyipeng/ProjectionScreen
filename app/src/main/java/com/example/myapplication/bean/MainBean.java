package com.example.myapplication.bean;

public class MainBean {
    String name;
    boolean Type;

    public MainBean(String name, boolean Type ) {
        this.name=name;
        this.Type=Type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isType() {
        return Type;
    }

    public void setType(boolean type) {
        Type = type;
    }
}

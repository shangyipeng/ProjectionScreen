package com.example.myapplication.bean;

import android.graphics.drawable.Drawable;

public class HistoryBean {
    public String PackageName;
    public String LastTimeVisible;
    public String LastTimeUsed;

    public String drawable;

    public String getDrawable() {
        return drawable;
    }

    public void setDrawable(String drawable) {
        this.drawable = drawable;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public String getLastTimeVisible() {
        return LastTimeVisible;
    }

    public void setLastTimeVisible(String lastTimeVisible) {
        LastTimeVisible = lastTimeVisible;
    }

    public String getLastTimeUsed() {
        return LastTimeUsed;
    }

    public void setLastTimeUsed(String lastTimeUsed) {
        LastTimeUsed = lastTimeUsed;
    }
}

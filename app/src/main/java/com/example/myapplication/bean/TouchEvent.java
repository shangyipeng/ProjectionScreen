package com.example.myapplication.bean;

import androidx.annotation.NonNull;


import com.example.myapplication.utile.GsonUtils;

import org.greenrobot.eventbus.EventBus;
/**
 * 辅助功能，屏幕共享发送消息相关
 */
public class TouchEvent {

    public static final int ACTION_START = 1;
    public static final int ACTION_PAUSE = 2;
    public static final int ACTION_CONTINUE = 3;
    public static final int ACTION_STOP = 4;
    public static final int ACTION_MOVE=5;//滑动

    public static final int ACTION_BACK=6;//返回上一级
    public static final int ACTION_HOME=7;//返回主页
    public static final int ACTION_RECENTS=8;//打开任务栏
    public static final int ACTION_ALLOW=9;//自动允许点击
    public static final int ACTION_SCREEN=10;//设置屏幕遮罩层
    public static final int ACTION_CLOSESCREEN=11;//设置屏幕遮罩层

    private int action;
    private TouchPoint touchPoint;

    private TouchEvent(int action) {
        this(action, null);
    }

    private TouchEvent(int action, TouchPoint touchPoint) {
        this.action = action;
        this.touchPoint = touchPoint;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public TouchPoint getTouchPoint() {
        return touchPoint;
    }

    public static void postStartAction(TouchPoint touchPoint) {
        postAction(new TouchEvent(ACTION_START, touchPoint));
    }
    public static void postStartAction1(TouchPoint touchPoint) {
        postAction(new TouchEvent(ACTION_MOVE, touchPoint));
    }

    public static void postPauseAction() {
        postAction(new TouchEvent(ACTION_PAUSE));
    }
    public static void postContinueAction() {
        postAction(new TouchEvent(ACTION_CONTINUE));
    }
    public static void postContinueActionMove() {
        postAction(new TouchEvent(ACTION_MOVE));
    }
    public static void postStopAction() {
        postAction(new TouchEvent(ACTION_STOP));
    }

    public static void postBACKAction( ) {
        postAction(new TouchEvent(ACTION_BACK));
    }
    public static void postHOMEAction() {
        postAction(new TouchEvent(ACTION_HOME));
    }
    public static void postALLOWAction() {
        postAction(new TouchEvent(ACTION_ALLOW));
    }

    public static void postSCREENAction() {
        postAction(new TouchEvent(ACTION_SCREEN));
    }
    public static void postCLOSESAction(){
        postAction(new TouchEvent(ACTION_CLOSESCREEN));
    }
    public static void postRECENTSAction() {
        postAction(new TouchEvent(ACTION_RECENTS));
    }

    private static void postAction(TouchEvent touchEvent) {
        EventBus.getDefault().post(touchEvent);
    }

    @NonNull
    @Override
    public String toString() {
        return "action=" + action + " touchPoint=" + (touchPoint == null ? "null" : GsonUtils.beanToJson(touchPoint));
    }
}

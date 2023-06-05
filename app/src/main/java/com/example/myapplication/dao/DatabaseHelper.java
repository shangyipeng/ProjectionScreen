package com.example.myapplication.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.jessyan.autosize.utils.LogUtils;

/**
 * 创建数据库保存短信数据
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static String name = "record.db";
    private static Integer version = 1;


    public DatabaseHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //打开数据库，建立了一个叫records的表，里面只有一列name来存储历史记录：
        db.execSQL("create table records(id integer primary key autoincrement,msg varchar(65533),name varchar(65530))");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}

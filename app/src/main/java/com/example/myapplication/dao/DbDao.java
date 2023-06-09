package com.example.myapplication.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DbDao {
        private Context context;
        private SQLiteDatabase db;

        public DbDao(Context context) {
            this.context = context;
            getInstance(context);
        }
    private static DatabaseHelper mInstance = null;
    public synchronized static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

        public List<String> queryData(String tempName) {
            List<String> data = new ArrayList<>();
            //模糊搜索
            Cursor cursor = mInstance.getReadableDatabase().rawQuery(
                    "select id as _id,msg from records where msg like '%" + tempName + "%' order by id desc ", null);

            while (cursor.moveToNext()) {
                //注意这里的name跟建表的name统一
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("msg"));
                data.add(name);
            }
            cursor.close();
            return data;

        }

        /**
         * 检查数据库中是否已经有该条记录
         *
         * @param tempName
         * @return
         */
        public boolean hasData(String tempName) {
            //从Record这个表里找到name=tempName的id
            Cursor cursor = mInstance.getReadableDatabase().rawQuery(
                    "select id as _id,name from records where name =?", new String[]{tempName});
            //判断是否有下一个
            return cursor.moveToNext();
        }

        /**
         * 插入数据
         *
         * @param tempName
         */
        public void insertData(String tempName,String type) {
            db = mInstance.getWritableDatabase();
            db.execSQL("insert into records(msg,name) values('"+type+"','"+tempName+"')");
            db.close();
        }

        /**
         * 删除数据
         *
         * @param name
         * @return
         */

        public int delete(String name) {
            // 获取数据
            SQLiteDatabase db = mInstance.getWritableDatabase();
            // 执行SQL
            int delete = db.delete("records", " name=?", new String[]{name});
            // 关闭数据库连接
            db.close();
            return delete;
        }

        /**
         * 清空数据
         */
        public void deleteData() {
            db = mInstance.getWritableDatabase();
            db.execSQL("delete from records");
            db.close();
        }
}

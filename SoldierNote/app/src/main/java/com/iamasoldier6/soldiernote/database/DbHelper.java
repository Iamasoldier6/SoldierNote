package com.iamasoldier6.soldiernote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Iamasoldier6 on 5/25/16.
 */
public class DbHelper extends SQLiteOpenHelper {

    /**
     * 数据库名字
     */
    public static final String DB_NAME = "soldiernote.db";
    /**
     * 数据库版本
     */
    private static final int version = 1;
    /**
     * 数据库对象
     */
    private static SQLiteDatabase db;

    private static DbHelper dbHelper = null;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    /**
     * 获取数据库对象 db
     *
     * @param context
     * @return
     */
    public static SQLiteDatabase getSQLiteDatabase(Context context) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context);
        }
        db = dbHelper.getWritableDatabase();
        return db;
    }

    /**
     * 创建数据库
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}

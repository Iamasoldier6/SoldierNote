package com.iamasoldier6.soldiernote.database;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.util.Date;


/**
 * 创建数据库表 Note
 *
 * Created by Iamasoldier6 on 5/25/16.
 */
public class Note implements BaseColumns {

    public static final String TABLE = "note"; //表名
    public static final String _TITLE = "title"; //标题
    public static final String _CONTENT = "content"; //内容
    public static final String _CREATE_TIME = "create_time"; //创建时间
    public static final String _MODIFY_TIME = "modify_time"; //修改时间
    public static final String[] ALL = {_ID, _TITLE, _CONTENT, _CREATE_TIME, _MODIFY_TIME}; //全部字段

    /**
     * 创建表语句
     */
    public static final String SQL_CREATE_TABLE = String.format(
            "create table %s(_id integer primary key autoincrement, %s text, %s text, %s integer, %s integer)",
            TABLE, _TITLE, _CONTENT, _CREATE_TIME, _MODIFY_TIME);

    /**
     * 删除表语句
     */
    public static final String SQL_DELETE_TABLE = String.format("delete table if exists %s", TABLE);

    String content;

    Date date;

    public Note(String content) {
        this.content = content;
        date = new Date();
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(_CONTENT, content);
        values.put(_CREATE_TIME, date.getTime());
        return values;
    }

}

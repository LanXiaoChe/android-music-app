package com.william.RDC.musicplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    private static final String CREATE_SONGS =
            "create table SONGS ("
                    + "id integer primary key autoincrement, "
                    + "title text, "//歌名
                    + "artist text, "//歌手
                    + "duration integer,"//时长
                    + "dataPath text, "//文件路径
                    + "listId integer,"//在DisplayActivity 的listView中的位置
                    + "isLove text)";//是否是喜爱的歌曲

    private static final String CREATE_USERS =
            "create table USERS ("
                    + "id integer primary key autoincrement, "
                    + "username text, "
                    + "password text)";//用户表

    private Context mContext;

    // 请注意，我已经把版本号从原来的1改为2
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, 2);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SONGS);//创建SONGS表
        db.execSQL(CREATE_USERS);//创建USERS表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 更新表的操作，这里简单地删除旧表，然后重新创建
        db.execSQL("drop table if exists SONGS");
        db.execSQL("drop table if exists USERS");
        onCreate(db);
    }
}

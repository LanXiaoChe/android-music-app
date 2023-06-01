package com.william.RDC.musicplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;

import com.william.RDC.musicplayer.model.Song;
import com.william.RDC.musicplayer.tool.PictureDealHelper;

public class DatabaseOperation {
    //数据库名
    public static final String DB_NAME = "Music";
    //数据库版本
    public static final int VERSION = 2;
    private volatile static DatabaseOperation databaseOperation;
    private SQLiteDatabase db;
    private Context context;
    //私有化构造方法,单例模式
    private DatabaseOperation(Context context){
        db = new Database(context, DB_NAME, null).getWritableDatabase();
        this.context = context;
    }
    /*双重锁模式*/
    public static DatabaseOperation getInstance(Context context){
        if(databaseOperation == null){//为了避免不必要的同步
            synchronized (DatabaseOperation.class){
                if(databaseOperation ==null){//为了在实例为空时才创建实例
                    databaseOperation = new DatabaseOperation(context);
                }
            }
        }
        return databaseOperation;
    }
    /**
     * 将Song实例存储到数据库的SONGS表中*/
    public void saveSong(Song song){
        if(song != null && db != null){
            ContentValues values = new ContentValues();
            values.put("title",song.getTitle());
            values.put("artist",song.getArtist());
            values.put("duration",song.getDuration());
            values.put("dataPath",song.getDataPath());
            values.put("listId",song.getList_id_display());
            if(song.isLove())
                values.put("isLove","true");
            else
                values.put("isLove","false");
            db.insert("SONGS",null,values);
        }
    }
    /**
     * 将Song实例从数据库的MyLoveSongs表中删除*/
    public void removeSong(Song song){
        if(song != null && db != null){
            //db.execSQL("delete from lxrData where name=?", new String[] { name });
            db.delete("SONGS","dataPath=?",new String[]{song.getDataPath()});
        }
    }
    /**
     * 给SONGS表中的某个歌曲修改isLove标志*/
    public void setLove(Song song,String flag){
        ContentValues values = new ContentValues();
        values.put("isLove",flag);
        db.update("SONGS",values,"dataPath=?",new String[]{song.getDataPath()});
    }
    /**
     * 从数据库读取SONGS表中所有的我喜爱的歌曲*/
    public ArrayList<Song> loadMyLoveSongs(){
        ArrayList<Song> list = new ArrayList<>();
        if(db != null){
            Cursor cursor = db.query("SONGS",null,"isLove =?",new String[]{"true"},null,null,null);
            if(cursor.moveToFirst()){
                do{
                    Song song = new Song();
                    song.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    song.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
                    song.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
                    song.setDataPath(cursor.getString(cursor.getColumnIndex("dataPath")));
                    song.setList_id_display(cursor.getInt(cursor.getColumnIndex("listId")));
                    song.setLove(true);
                    song.setAlbum_icon(PictureDealHelper.getAlbumPicture(context,song.getDataPath(),96,96));
                    list.add(song);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }
    /**
     * 读取数据库中的所有歌曲*/
    public ArrayList<Song> loadAllSongs(){
        ArrayList<Song> list = new ArrayList<>();
        if(db != null){
            Cursor cursor = db.query("SONGS",null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    Song song = new Song();
                    song.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    song.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
                    song.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
                    song.setDataPath(cursor.getString(cursor.getColumnIndex("dataPath")));
                    song.setList_id_display(cursor.getInt(cursor.getColumnIndex("listId")));
                    String flag = cursor.getString(cursor.getColumnIndex("isLove"));
                    if(flag.equals("true"))
                        song.setLove(true);
                    else
                        song.setLove(false);
                    song.setAlbum_icon(PictureDealHelper.getAlbumPicture(context,song.getDataPath(),96,96));
                    list.add(song);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }


    public boolean registerUser(String username, String password) {
        // 检查用户名和密码是否为空
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false; // 用户名或密码为空，返回false表示注册失败
        }

        // 检查密码长度是否符合要求
        if (password.length() < 6 || password.length() > 16) {
            return false; // 密码长度不符合要求，返回false表示注册失败
        }

        // 检查用户名是否已经存在
        Cursor cursor = db.query("USERS", null, "username = ?", new String[]{username}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return false; // 用户名已存在，返回false表示注册失败
        }

        // 如果用户名不存在且满足注册限制条件，将新用户的信息插入到USERS表中
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        db.insert("USERS", null, values);
        cursor.close();
        return true; // 返回true表示注册成功
    }


    public int resetPassword(String username, String oldPassword, String newPassword) {
        // 检查用户名和新密码和旧密码不能为空
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
            return -1;
        }

        // 用户名必须存在
        Cursor userCursor = db.query("USERS", null, "username = ?", new String[]{username}, null, null, null);
        if (userCursor.getCount() == 0) {
            userCursor.close();
            return 1; // 用户名不存在
        }
        userCursor.close();

        // 旧密码必须正确
        Cursor passCursor = db.query("USERS", null, "username = ? and password = ?", new String[]{username, oldPassword}, null, null, null);
        if (passCursor.getCount() == 0) {
            passCursor.close();
            return 2; // 旧密码不正确
        }
        passCursor.close();

        // 新密码必须在6到12个字符之间
        if (newPassword.length() < 6 || newPassword.length() > 12) {
            return 3; // 新密码长度不符合要求
        }

        // 修改密码
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        db.update("USERS", values, "username = ?", new String[]{username});

        return 0; // 密码已重置
    }




    public boolean loginUser(String username, String password){
        Cursor cursor = db.query("USERS", null, "username = ? and password = ?", new String[]{username, password}, null, null, null);
        if(cursor.getCount() > 0){
            // 如果找到了匹配的记录，表示登录成功
            return true;
        }else{
            // 如果没有找到匹配的记录，表示登录失败
            return false;
        }
    }




    /**
     * 判断当前SONGS表中是否有数据*/
    public boolean isSONGS_Null(){
        if(db != null){
            Cursor cursor = db.query("SONGS",null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                return false;//不为空
            }
            cursor.close();
        }
        return true;//空
    }
}

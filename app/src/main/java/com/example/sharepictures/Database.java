package com.example.sharepictures;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    private static final String dbname="mydb";//创建一个数据库，里面保存用户信息以及图片信息

    private Context mContext;

//    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
//
//        super(context, name, factory, version);
//
//        mContext=context;
//
//    }

    public Database(Context context){
        super(context,"mydb",null,1);
        mContext=context;

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建一个用户表，里面有id为主键，密码不能为空，还用用户头像
        db.execSQL("create table if not exists users"+
                "(id text primary key," +
                "password text not null,"+
                "touxiang blob)");

        //创建一个图片表，存储图片
        db.execSQL("create table if not exists picturestable"+
                "(idnum int primary key,"+
                "pictures blob)");

        Toast.makeText(mContext,"数据库和表创建成功", Toast.LENGTH_SHORT).show();
    }

    //在原来的软件上更新会从这里开始，不卸载在线更新（但在这里，没有设计对数据库中的表进行更新，所以下面这个方法直接为空）
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

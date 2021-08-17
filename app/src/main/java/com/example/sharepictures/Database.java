package com.example.sharepictures;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public static final String CREATE_FORM="create table userData ("
            + "id text, "
            +"password text)";//创建表的语句
    private Context mContext;

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);

        mContext=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FORM);//执行创建表的语句
//        Toast.makeText(mContext,"表创建成功", Toast.LENGTH_SHORT).show();
    }

    //在原来的软件上更新会从这里开始，不卸载在线更新（但在这里，没有设计对数据库中的表进行更新，所以下面这个方法直接为空）
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

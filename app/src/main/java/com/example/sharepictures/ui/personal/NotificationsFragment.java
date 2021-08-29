package com.example.sharepictures.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.sharepictures.Database;
import com.example.sharepictures.LoginActivity;
import com.example.sharepictures.R;
import com.example.sharepictures.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {//这是个人信息

    private FragmentNotificationsBinding binding;
    private Database dbbase;
    private SQLiteDatabase db;
    private String username;
    private String password;
    private byte[] imagedata;
    private Bitmap imagebm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        String prefsName=getResources()
                .getString(R.string.shared_preferences_file_name);//读取SharedPreferences的文件名
        String accountKey = getResources()//用户的账号的标识
                .getString(R.string.login_account_name);
        String passwordKey=getResources().getString(R.string.login_password);//密码
        SharedPreferences spf =this.getActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        username = spf.getString(accountKey,"");//读取SharedPreferences里的用户名
        password =spf.getString(passwordKey,"");//
        binding.textPerson.setText(username);//将用户名显示在个人信息对应的界面
        dbbase=new Database(getActivity());
        db=dbbase.getWritableDatabase();

        Cursor cursor = db.query("users",null,"id=?",
                new String[]{username},
                null,null,null,null); // 根据接收到的id进行数据库查询
        if (cursor.moveToFirst()) {//移动光标到数据第一行

            imagedata = cursor.getBlob(2);
            imagebm = BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length);
            binding.ivTouxiang.setImageBitmap(imagebm);

        }

        binding.loginoutbutton.setOnClickListener(new View.OnClickListener(){//退出登陆

            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                System.exit(0);//将活动销毁，只剩下一个登陆界面的活动
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
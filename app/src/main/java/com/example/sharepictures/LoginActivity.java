package com.example.sharepictures;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sharepictures.ui.home.HomeFragment;
import com.example.sharepictures.ui.personal.NotificationsFragment;

import java.security.PublicKey;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Boolean bPwdSwitch = false;//判断密码是否可见,这里一开始默认密码不可见
    private EditText etPwd;//输入密码
    private EditText etAccount;//输入用户的账户名
    private CheckBox cbRememberPwd;//是否记住密码
    private Database userdb;//创建一个数据库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);//设置当前界面的样式

        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);//是否切换明文状态的格式
        etPwd = findViewById(R.id.et_pwd);//密码的形式
        etAccount = findViewById(R.id.et_account);//用户的账户名
        cbRememberPwd = findViewById(R.id.cb_remember_pwd);//记住密码的形式
        Button btLogin = findViewById(R.id.bt_login);//登陆按钮的形式
        Button btSignup=findViewById(R.id.bt_signup);//注册账户的形式
        userdb=new Database(this,"Userss.db",null,1);//数据库的初始化

        //当前登陆活动设置一个监听事件
        btLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//点击登陆按钮之后，实现页面的跳转

                String userName=etAccount.getText().toString();//获取文本框的数据
                String passWord=etPwd.getText().toString();

                //当登陆成功了，强制保存到SharePreferences里面
                String spFileName = getResources()//获取当前活动的文件名
                        .getString(R.string.shared_preferences_file_name);
                String accountKey = getResources()//用户的账号
                        .getString(R.string.login_account_name);
                String passwordKey =  getResources()//登陆密码
                        .getString(R.string.login_password);
                SharedPreferences spFile = getSharedPreferences(
                        spFileName,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spFile.edit();
                //将账户名和密码为一对，写入安卓自带的SharedPreferences文件里面
                editor.putString(accountKey, userName);
                editor.putString(passwordKey, passWord);
                editor.apply();

                if (login(userName,passWord)){//登陆成功


                    Intent intent=new Intent(LoginActivity.this, BottomNavigationActivity.class);
                    startActivity(intent);
                    finish();//释放资源
                }
                else{
                    Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();//失败，弹出登陆失败
                }

            }
        });

        //为注册按钮添加一个监听事件
        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击注册按钮之后，跳转到注册界面
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });



        String spFileName = getResources()//获取当前活动的文件名
                .getString(R.string.shared_preferences_file_name);
        String accountKey = getResources()//用户的账号
                .getString(R.string.login_account_name);
        String passwordKey =  getResources()//登陆密码
                .getString(R.string.login_password);
        String rememberPasswordKey = getResources()//记住密码
                .getString(R.string.login_remember_password);

        //在输入账户密码的时候，加载SharedPreferences中存储的用户账号信息
        SharedPreferences spFile = getSharedPreferences(
                spFileName,
                MODE_PRIVATE);
        String account = spFile.getString(accountKey, null);
        String password = spFile.getString(passwordKey, null);

        Boolean rememberPassword = spFile.getBoolean(
                rememberPasswordKey,
                false);//false是控制是否记住密码图标，这里表示不记住


        //以下两个if是显示在对应的文本框，但是是否记住密码这个控件失效了
        if (account != null && !TextUtils.isEmpty(account)) {//读取到的账户名不能为空并且账户名框内不能为空
            etAccount.setText(account);
        }

        if (password != null && !TextUtils.isEmpty(password)) {//读取到的密码不能为空并且密码框内不能为空
            etPwd.setText(password);
        }

        cbRememberPwd.setChecked(rememberPassword);//这里显示不记住密码


        ivPwdSwitch.setOnClickListener(new View.OnClickListener() {//在切换密码是否可见的图标处，添加一个监听事件
//ivPwdSwitch对象调用方法，括号里面是new一个接口当参数传入
            @Override
            public void onClick(View view) {
                bPwdSwitch = !bPwdSwitch;
                if (bPwdSwitch) {
                    ivPwdSwitch.setImageResource(
                            R.drawable.ic_baseline_visibility_24);
                    etPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//输入一个对用户可见的密码
                } else {
                    ivPwdSwitch.setImageResource(
                            R.drawable.ic_baseline_visibility_off_24);
                    etPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT);//输入一个密码或者是一个普通文本
                    etPwd.setTypeface(Typeface.DEFAULT);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {

        String spFileName = getResources()
                .getString(R.string.shared_preferences_file_name);//当前活动的文件名
        String accountKey = getResources()
                .getString(R.string.login_account_name);//用户的账号
        String passwordKey =  getResources()
                .getString(R.string.login_password);//登陆密码
        String rememberPasswordKey = getResources()
                .getString(R.string.login_remember_password);//记住密码

        SharedPreferences spFile = getSharedPreferences(
                spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spFile.edit();
        //通过getSharedPreferences方法获得SharedPreferences对象，
        // 并通过SharedPreferences.Editor进行编辑操作。

        if (cbRememberPwd.isChecked()) {//当记住密码为真
            String password = etPwd.getText().toString();//密码
            String account = etAccount.getText().toString();//账户

            //将账户名和密码为一对，写入安卓自带的SharedPreferences文件里面
            editor.putString(accountKey, account);
            editor.putString(passwordKey, password);
            editor.putBoolean(rememberPasswordKey, true);
            editor.apply();
        } else {
            //如果没有勾选记住密码，则将账户名和密码清除
            editor.remove(accountKey);
            editor.remove(passwordKey);
            editor.remove(rememberPasswordKey);
            editor.apply();
        }

    }

    public boolean login(String username,String password) {//验证此账号密码是否正确
        SQLiteDatabase db = userdb.getWritableDatabase();
        String sql = "select * from userData where id=? and password=?";//将登录时填的账号和密码在数据库里面进行查询，如果存在该数据，则返回true，否则返回false
        Cursor cursor = db.rawQuery(sql, new String[] {username, password});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        else{
            return false;
        }

    }


}
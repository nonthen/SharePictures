package com.example.sharepictures;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Boolean bPwdSwitch = false;//判断密码是否可见,这里一开始默认密码不可见
    private EditText etPwd;//输入密码
    private EditText etAccount;//输入用户的账户名
    private CheckBox cbRememberPwd;//是否记住密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);//是否切换明文状态的格式
        etPwd = findViewById(R.id.et_pwd);//密码的形式
        etAccount = findViewById(R.id.et_account);//用户的账户名
        cbRememberPwd = findViewById(R.id.cb_remember_pwd);//记住密码的形式
        Button btLogin = findViewById(R.id.bt_login);//登陆按钮的形式
        btLogin.setOnClickListener(this);//当前登陆活动设置一个监听事件,在点击登陆按钮的时候，将账户密码存储或者删掉

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
                false);

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
}
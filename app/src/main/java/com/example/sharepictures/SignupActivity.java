package com.example.sharepictures;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private Boolean bPwdSwitch1 = false;//判断第一个输入密码是否可见,这里默认密码不可见
    private Boolean bPwdSwitch2 = false;//判断第二个确认密码是否可见,这里默认密码不可见
    private EditText etPwd1;//输入密码
    private EditText etPwd2;//输入确认密码
    private EditText etAccount;//输入用户的账户名
    private Database userdb;//创建一个数据库


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupactivity);//设置当前界面的样式

        final ImageView ivPwdSwitch1 = findViewById(R.id.imageView1);//是否切换明文状态的格式
        final ImageView ivPwdSwitch2 = findViewById(R.id.imageView2);//是否切换明文状态的格式
        etAccount = findViewById(R.id.NumberSignup);//用户的账户名
        etPwd1=findViewById(R.id.Password1);
        etPwd2=findViewById(R.id.Password2);
        Button btSignup=findViewById(R.id.signupbutton);//注册账户的形式
        userdb=new Database(this,"Userss.db",null,1);//数据库的初始化



        //这是第一个控制密码是否可见
        ivPwdSwitch1.setOnClickListener(new View.OnClickListener() {//在切换密码是否可见的图标处，添加一个监听事件
            //ivPwdSwitch对象调用方法，括号里面是new一个接口当参数传入
            @Override
            public void onClick(View view) {
                bPwdSwitch1 = !bPwdSwitch1;
                if (bPwdSwitch1) {
                    ivPwdSwitch1.setImageResource(
                            R.drawable.ic_baseline_visibility_24);
                    etPwd1.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//输入一个对用户可见的密码
                } else {
                    ivPwdSwitch1.setImageResource(
                            R.drawable.ic_baseline_visibility_off_24);
                    etPwd1.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT);//输入一个密码或者是一个普通文本
                    etPwd1.setTypeface(Typeface.DEFAULT);
                }

            }

        });

        //这是第二个控制密码是否可见
        ivPwdSwitch2.setOnClickListener(new View.OnClickListener() {//在切换密码是否可见的图标处，添加一个监听事件
            //ivPwdSwitch对象调用方法，括号里面是new一个接口当参数传入
            @Override
            public void onClick(View view) {
                bPwdSwitch2 = !bPwdSwitch2;
                if (bPwdSwitch2) {
                    ivPwdSwitch2.setImageResource(
                            R.drawable.ic_baseline_visibility_24);
                    etPwd2.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//输入一个对用户可见的密码
                } else {
                    ivPwdSwitch2.setImageResource(
                            R.drawable.ic_baseline_visibility_off_24);
                    etPwd2.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT);//输入一个密码或者是一个普通文本
                    etPwd2.setTypeface(Typeface.DEFAULT);
                }
            }

        });




        btSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//点击注册按钮，判断是否成功，若是成功就跳转到登陆页面

                String account=etAccount.getText().toString().trim();
                String password1=etPwd1.getText().toString().trim();
                String password2=etPwd2.getText().toString().trim();


                if (CheckIsDataAlreadyInDBorNot(account)) {
                    Toast.makeText(getApplicationContext(),"该用户名已被注册，注册失败",Toast.LENGTH_SHORT).show();
                }
                else {

                    if (register(account, password1,password2)) {
                        Toast.makeText(getApplicationContext(), "注册成功，即将返回登录界面", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }
        });


    }

    //向数据库插入数据
    public boolean register(String username,String password1,String password2){

        if (username.equals("")){
            Toast.makeText(getApplicationContext(),"账号名不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(TextUtils.isEmpty(password1)|| TextUtils.isEmpty(password2)){//判断两个密码框是否为空
            Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!password1.equals(password2)){//两个密码不一致，弹窗提示重新输入密码
            Toast toast =Toast.makeText(getApplicationContext(),
                    "密码不一致，请重新确认密码",
                    Toast.LENGTH_LONG);
            toast.show();//弹窗显示
            return false;
        }
        else{
            SQLiteDatabase db= userdb.getWritableDatabase();
            //contenvalues只能存储基本类型的数据，像string，int之类的
            ContentValues values=new ContentValues();
            values.put("id",username);
            values.put("password",password1);
            db.insert("userData",null,values);
            db.close();
            return true;
        }
    }
    //检验用户名是否已存在
    public boolean CheckIsDataAlreadyInDBorNot(String value) {
        SQLiteDatabase db = userdb.getWritableDatabase();
        String Query = "Select * from userData where id =?";
        Cursor cursor = db.rawQuery(Query, new String[]{value});
        if (cursor.getCount() > 0) {//用户存在
            cursor.close();
            return true;
        }
        else{
            cursor.close();
            return false;
        }

    }

}

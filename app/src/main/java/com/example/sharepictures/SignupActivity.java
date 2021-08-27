package com.example.sharepictures;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sharepictures.ui.personal.NotificationsFragment;

import java.io.ByteArrayOutputStream;

public class SignupActivity extends AppCompatActivity {

    private Boolean bPwdSwitch1 = false;//判断第一个输入密码是否可见,这里默认密码不可见
    private Boolean bPwdSwitch2 = false;//判断第二个确认密码是否可见,这里默认密码不可见
    private EditText etPwd1;//输入密码
    private EditText etPwd2;//输入确认密码
    private EditText etAccount;//输入用户的账户名
    private Database userdb;//创建一个数据库
    private Button btSignup;
    private ImageButton chooseImage;//创建一个用户头像
    private byte[] image;//设置头像
    private Bitmap bm;//选择头像的照片

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupactivity);//设置当前界面的样式

        final ImageView ivPwdSwitch1 = findViewById(R.id.imageView1);//是否切换明文状态的格式
        final ImageView ivPwdSwitch2 = findViewById(R.id.imageView2);//是否切换明文状态的格式
        etAccount = findViewById(R.id.NumberSignup);//用户的账户名
        etPwd1=findViewById(R.id.Password1);
        etPwd2=findViewById(R.id.Password2);
        btSignup=findViewById(R.id.signupbutton);//注册账户的形式
        chooseImage =findViewById(R.id.chooseimage);//选择头像的形式
//        userdb=new Database(this,"Userss.db",null,1);//数据库的初始化
        userdb = new Database(this);

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

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//添加点击事件
                //检测是否进行了授权
                if (ContextCompat.checkSelfPermission(SignupActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(SignupActivity.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    //打开系统相册
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);//请求标识为1
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

                    if (register(account, password1,password2,image)) {
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
    public boolean register(String username,String password1,String password2,byte[] image){

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
            values.put("touxiang",image);
            db.insert("users",null,values);
            db.close();
            return true;
        }
    }
    //检验用户名是否已存在
    public boolean CheckIsDataAlreadyInDBorNot(String value) {
        SQLiteDatabase db = userdb.getWritableDatabase();
        String Query = "Select * from users where id =?";
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //requestCode是用来标识请求的来源(这里是图片点击事件，标识为1）， resultCode是用来标识返回的数据来自哪一个activity
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();//选择照片
            String[] filePathColumns = {MediaStore.Images.Media.DATA};//获取图片路径

            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();//正确指向第一个位置
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }

    }

    private void showImage(String imagePath) {

        bm = BitmapFactory.decodeFile(imagePath);//通过BitmapFactory.decodeFile(imagePath)方法来加载图片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//字符串输出流
        //三个参数分别是压缩后的图像的格式（png），图像显示的质量（0—100），100表示最高质量，图像处理的输出流（baos）。
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        image = baos.toByteArray();//接收读取到的字符，即图片的路径
        chooseImage.setImageBitmap(bm);//设置头像

    }




}

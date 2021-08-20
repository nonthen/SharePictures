package com.example.sharepictures;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sharepictures.databinding.ActivityBottomNavigationBinding;
import com.example.sharepictures.ui.personal.NotificationsFragment;

public class BottomNavigationActivity extends AppCompatActivity {

//    private String UserName;//用户名
//    Intent intent = getIntent();//获取用户的用户名信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityBottomNavigationBinding binding = ActivityBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//        PostUserName();//目的是用户一登陆，能够及时将用户名设置到个人信息界面

//        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_upload, R.id.navigation_personal)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);//主页


    }

//    public void PostUserName(){//将当前用户的账户名返回到个人信息界面
//        UserName=intent.getStringExtra("userName");//此时账号由登陆的Activity传递到另一个Activity
//
//        //下面对数据进行传递，由Activity传递到Frament
//        NotificationsFragment PersonFragment =new NotificationsFragment();//实例化个人信息界面的Fragment
//        Bundle bundle=new Bundle();//实例化Bundle对象
//        bundle.putString("Account",UserName);//将用户名数据存入到Bundle中
//        PersonFragment.setArguments(bundle);//调用Fragment的setArguments方法，传入Bundle对象
//
//    }

}
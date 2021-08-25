package com.example.sharepictures.ui.personal;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;

import com.example.sharepictures.BottomNavigationActivity;
import com.example.sharepictures.Database;
import com.example.sharepictures.LoginActivity;
import com.example.sharepictures.R;
import com.example.sharepictures.SignupActivity;
import com.example.sharepictures.databinding.FragmentNotificationsBinding;

import java.io.ByteArrayOutputStream;

public class NotificationsFragment extends Fragment {//这是个人信息

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private Database dbbase;
    private byte[] image;
    private Bitmap bm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        notificationsViewModel =
//                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        String prefsName=getResources()
                .getString(R.string.shared_preferences_file_name);//读取SharedPreferences的文件名
        String accountKey = getResources()//用户的账号的标识
                .getString(R.string.login_account_name);
        String passwordKey=getResources().getString(R.string.login_password);//密码
        SharedPreferences spf =this.getActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        String username = spf.getString(accountKey,"");//读取SharedPreferences里的用户名
        String password =spf.getString(passwordKey,"");//
        binding.textPerson.setText(username);//将用户名显示在个人信息对应的界面

        dbbase = new Database(getActivity());
        final SQLiteDatabase db = dbbase.getReadableDatabase();

        binding.ivTouxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//在图片处添加点击事件

                //检测是否进行了授权
                if (ContextCompat.checkSelfPermission(NotificationsFragment.this.getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(NotificationsFragment.this.getActivity(), new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    //打开系统相册
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);//请求标识为1

                    ContentValues values=new ContentValues();//储存个人信息
                    values.put("id",username);
                    values.put("password",password);
                    values.put("touxiang",image);//图片
//                    db.insert("user",null,values);//将数据更新到表格中
                    db.update("users",values,"id=?",new String[]{username});
                    db.close();

//                    binding.ivTouxiang.setImageBitmap(bm);
                    onResume();//选择完图片，设置当前用户的头像
//                    Toast.makeText(getContext(), "头像设置成功", Toast.LENGTH_SHORT).show();

                }

            }
        });



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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //requestCode是用来标识请求的来源(这里是图片点击事件，标识为1）， resultCode是用来标识返回的数据来自哪一个activity
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();//选择照片
            String[] filePathColumns = {MediaStore.Images.Media.DATA};//获取图片路径

            Cursor c = NotificationsFragment.this.getActivity().getContentResolver().query(selectedImage, filePathColumns, null, null, null);
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
        binding.ivTouxiang.setImageBitmap(bm);//设置图片

    }

    @Override
    public void onResume() {//刷新当前界面
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
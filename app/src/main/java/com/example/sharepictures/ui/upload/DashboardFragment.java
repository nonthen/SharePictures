package com.example.sharepictures.ui.upload;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sharepictures.Database;
import com.example.sharepictures.LoginActivity;
import com.example.sharepictures.databinding.FragmentDashboardBinding;

import java.io.ByteArrayOutputStream;

public class DashboardFragment extends Fragment {//发布照片

    private Database dbbase;
    private SQLiteDatabase db;
    private byte[] imagedata;
    private Bitmap imagebm;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbbase=new Database(getActivity());
        db=dbbase.getWritableDatabase();

        binding.imageButton.setOnClickListener(new View.OnClickListener() {//选择上传的图片
            @Override
            public void onClick(View view) {
                //检测是否进行了授权
                if (ContextCompat.checkSelfPermission(DashboardFragment.this.getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DashboardFragment.this.getActivity(), new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    //打开系统相册
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);//请求标识为1
                }
            }
        });


        binding.fabu.setOnClickListener(new View.OnClickListener() {//发布照片按钮
            @Override
            public void onClick(View view) {

                ContentValues values=new ContentValues();//储存图片信息

                values.put("idnum",binding.PictureId.getText().toString());
                values.put("details",binding.PicturesDetails.getText().toString());
                values.put("likes",0);
                values.put("pictures",imagedata);

                db.insert("picturestable",null,values);
                Toast.makeText(getContext(), "发布成功，请重新刷新界面", Toast.LENGTH_SHORT).show();

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

            Cursor c = DashboardFragment.this.getActivity().getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();//正确指向第一个位置
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }

    }

    private void showImage(String imagePath) {

        imagebm = BitmapFactory.decodeFile(imagePath);//通过BitmapFactory.decodeFile(imagePath)方法来加载图片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//字符串输出流
        //三个参数分别是压缩后的图像的格式（png），图像显示的质量（0—100），100表示最高质量，图像处理的输出流（baos）。
        imagebm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imagedata = baos.toByteArray();//接收读取到的字符，即图片的路径
        binding.imageButton.setImageBitmap(imagebm);//设置头像

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
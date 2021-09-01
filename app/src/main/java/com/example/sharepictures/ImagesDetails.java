package com.example.sharepictures;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sharepictures.ui.home.HomeFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagesDetails extends AppCompatActivity {//图片信息详情

    private int likeSwitch;//判断用户是否喜欢该图片,这里一开始默认不喜欢
    private Database dbbase;
    private SQLiteDatabase db;
    private byte[] imagedata;
    private Bitmap imagebm;
    private Intent intent;
    private ContentValues values;
    private Cursor cursor;
    private File appDir;//存储图片的文件夹

    private ImageView picture;//图片
    private TextView pictureId;//图片id
    private TextView Imagesdetails;//图片信息
    private ImageView like;//喜欢状态的切换
    private ImageView download;//下载
    private ImageView share;//分享
    private ImageView back;//返回首页

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagesdetails);//设置当前界面的样式

        picture=findViewById(R.id.imageView);
        pictureId=findViewById(R.id.setId);
        Imagesdetails=findViewById(R.id.Imagesdetails);
        like=findViewById(R.id.like);
        download=findViewById(R.id.saveimage);
        share=findViewById(R.id.share);
        back=findViewById(R.id.back);

        dbbase=new Database(this);
        db=dbbase.getWritableDatabase();
        intent = getIntent();
        cursor = db.query("picturestable",null,
                "idnum=?",new String[]{intent.getStringExtra("id")},
                null,null,null,null); // 根据接收到的id进行数据库查询

        if (cursor.moveToFirst()){//移动光标到数据第一行
//            while (!cursor.isAfterLast()){//判断当前是否为数据中的最后一行

                pictureId.setText(cursor.getString(0));
                Imagesdetails.setText(cursor.getString(1));
                likeSwitch=cursor.getInt(2);
                if (likeSwitch==1){//喜欢
                    like.setImageResource(
                            R.drawable.love_red);
                }else {
                    like.setImageResource(
                            R.drawable.ic_baseline_favorite_border_24);
                }
                imagedata = cursor.getBlob(3);
                imagebm = BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length);
                picture.setImageBitmap(imagebm);
//                cursor.moveToNext();//移动到下一行数据
//            }
        }

        like.setOnClickListener(new View.OnClickListener(){//是否喜欢

            @Override
            public void onClick(View view) {

                //当点击时发生改变
                if(likeSwitch==0){
                    likeSwitch=1;//变成喜欢
                    like.setImageResource(
                            R.drawable.love_red);
                }
                else{
                    likeSwitch=0;//变成不喜欢
                    like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                }

                values=ChangeLike(likeSwitch);
//                Toast.makeText(ImagesDetails.this,String.valueOf(likeSwitch),Toast.LENGTH_SHORT).show();
                db.update("picturestable",values,"idnum=?",new String[] {intent.getStringExtra("id")});

            }
        });

        back.setOnClickListener(new View.OnClickListener() {//返回首页
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(ImagesDetails.this, BottomNavigationActivity.class);
                startActivity(intent);
            }
        });


        download.setOnClickListener(new View.OnClickListener() {//单击下载按钮保存图片
            @Override
            public void onClick(View view) {
                saveImageToGallery(ImagesDetails.this,imagebm);

            }
        });

        share.setOnClickListener(new View.OnClickListener() {//分享图片
            @Override
            public void onClick(View view) {

                Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(
                        ImagesDetails.this.getContentResolver(),imagebm, null, null));

                shareImage(ImagesDetails.this,imageUri,"分享到……");

            }
        });


    }

    public ContentValues ChangeLike(int i){

        ContentValues valuestemp=new ContentValues();
        valuestemp.put("idnum",intent.getStringExtra("id"));
        valuestemp.put("details",Imagesdetails.getText().toString());
        valuestemp.put("likes",i);
        valuestemp.put("pictures",imagedata);
        return valuestemp;
    }

    public void saveImageToGallery(Context context, Bitmap bmp) {//保存图片

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (ImagesDetails.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ImagesDetails.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                } else {
                    //这里就是权限打开之后自己要操作的逻辑
//                    appDir = new File(Environment.getExternalStorageDirectory(),//这种写法不被推荐使用
//                            "SharePictures");//文件名
                    appDir=new File(context.getExternalFilesDir(null).getPath()+"SharePictures");
                    appDir.mkdir();
                }
            }
        }

        String fileName = System.currentTimeMillis() + ".jpg";
//        File file = new File(appDir, fileName);
        File file=new File(appDir+"/"+fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {

            Toast.makeText(context, "保存失败1", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        } catch (IOException e) {

            Toast.makeText(context, "保存失败2",Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }


        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
            Toast.makeText(context, "保存成功",Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {

            Toast.makeText(context, "保存失败3",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // 最后通知图库更新
//        第一种方法
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(file);
//        intent.setData(uri);
//        context.sendBroadcast(intent);
//        Toast.makeText(context, "保存成功",Toast.LENGTH_SHORT).show();

//        第二种方法
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(file.getPath()))));
    }


    public static void shareImage(Context context, Uri uri,String title) {//title为目标Activity提供一个标题
        //分享图片
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);//分享图片的路径
        shareIntent.setType("image/*");//分享内容为图片类型
        context.startActivity(Intent.createChooser(shareIntent, title));
    }

}

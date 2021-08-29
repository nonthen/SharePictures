package com.example.sharepictures;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sharepictures.ui.home.HomeFragment;

public class ImagesDetails extends AppCompatActivity {//图片信息详情

    private int likeSwitch;//判断用户是否喜欢该图片,这里一开始默认不喜欢
    private Database dbbase;
    private SQLiteDatabase db;
    private byte[] imagedata;
    private Bitmap imagebm;
    private Intent intent;
    private ContentValues values;
    private Cursor cursor;

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
//                    like.setImageResource(
//                            R.drawable.love_white);
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


        download.setOnClickListener(new View.OnClickListener() {//将图片保存下来
            @Override
            public void onClick(View view) {


            }
        });

        share.setOnClickListener(new View.OnClickListener() {//分享图片
            @Override
            public void onClick(View view) {

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


}

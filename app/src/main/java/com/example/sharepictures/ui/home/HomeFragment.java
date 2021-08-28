package com.example.sharepictures.ui.home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sharepictures.Database;
import com.example.sharepictures.R;
import com.example.sharepictures.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {//这是主页

    private FragmentHomeBinding binding;
    private Database dbbase;
    private SQLiteDatabase db;
    private byte[] imagedata;
    private Bitmap imagebm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbbase=new Database(getActivity());
        db=dbbase.getWritableDatabase();

        Map<String, Object> item;  // 列表项内容用Map存储
        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>(); // 列表信息
        Cursor cursor = db.query("picturestable",null,
                null,null,null,
                null,null,null); // 数据库查询

        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                item = new HashMap<String, Object>();  // 为列表项赋值
                item.put("idnum",cursor.getString(0));
                item.put("details",cursor.getString(1));
                imagedata = cursor.getBlob(2);
                imagebm = BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length);
                item.put("pictures",imagebm);
                cursor.moveToNext();
                data.add(item); // 加入到列表中
            }
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), data, R.layout.activity_my_pictufabu, new String[] { "pictures", "idnum", "details" },
                new int[] { R.id.item_image, R.id.userid, R.id.itemdetails });
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView iv = (ImageView)view;
                    iv.setImageBitmap( (Bitmap)data );
                    return true;
                }else{
                    return false;
                }
            }
        });
        binding.listView.setAdapter(simpleAdapter);



        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
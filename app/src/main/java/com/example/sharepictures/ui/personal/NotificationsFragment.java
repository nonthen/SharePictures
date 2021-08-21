package com.example.sharepictures.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;

import com.example.sharepictures.BottomNavigationActivity;
import com.example.sharepictures.LoginActivity;
import com.example.sharepictures.R;
import com.example.sharepictures.SignupActivity;
import com.example.sharepictures.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {//这是个人信息

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;

    private String UserName;

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
        SharedPreferences spf =this.getActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        String username = spf.getString(accountKey,"");//读取SharedPreferences里的用户名
        binding.textPerson.setText(username);//将用户名显示在个人信息对应的界面

//        notificationsViewModel.getName().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String name) {
//                binding.textPerson.setText(name);//将当前用户名显示在个人信息界面上
//
//            }
//        });

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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
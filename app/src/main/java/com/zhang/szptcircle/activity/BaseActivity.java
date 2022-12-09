package com.zhang.szptcircle.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.SkinAppCompatDelegateImpl;


public abstract class BaseActivity extends AppCompatActivity {
    public Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //传入layout布局
        setContentView(initLayout());
        //初始化控件对象
        initView();
        //封装监听事件，调用接口，方法等
        initData();
    }


    protected abstract int initLayout();


    protected abstract void initView();



    protected abstract void initData();


    //消息提示框
    public void showToast(String msg) {
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        //显示消息框时去除包名
        toast.setText(msg);
        toast.show();
    }


    public void showToastSync(String msg) {
        //在线程中创建消息循环
        Looper.prepare();
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
        Looper.loop();
    }

    //封装页面跳转
    public void navigateTo(Class cls) {
        Intent in = new Intent(mContext, cls);
        startActivity(in);
    }

    //设置特殊的flags来控制intent的处理事件
    public void navigateToWithFlag(Class cls, int flags) {
        Intent in = new Intent(mContext, cls);
        in.setFlags(flags);
        startActivity(in);
    }

    //把token存入sp_szptcircle.xml
    protected void saveStringToSp(String key, String val) {
        SharedPreferences sp = getSharedPreferences("sp_szptcircle", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, val);
        editor.commit();
    }



    //从sp_szptcircle.xml获取token
    protected String getStringFromSp(String key) {
        SharedPreferences sp = getSharedPreferences("sp_szptcircle", MODE_PRIVATE);
        //返回key对应的value
        return sp.getString(key, "");
    }


    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return SkinAppCompatDelegateImpl.get(this, this);
    }
}

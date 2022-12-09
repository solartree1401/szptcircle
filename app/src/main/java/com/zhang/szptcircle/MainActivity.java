package com.zhang.szptcircle;

import android.view.View;
import android.widget.Button;
import com.zhang.szptcircle.activity.BaseActivity;
import com.zhang.szptcircle.activity.HomeActivity;
import com.zhang.szptcircle.activity.LoginActivity;
import com.zhang.szptcircle.activity.RegisterActivity;
import com.zhang.szptcircle.util.StringUtils;

public class MainActivity extends BaseActivity {

    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }

    @Override
    protected void initData() {
        //假如token不为空，没有注销登录
        if (!StringUtils.isEmpty(getStringFromSp("token"))) {
            //跳过此页面，直接进入首页
            navigateTo(HomeActivity.class);
            finish();
        }
        //监听登录按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到登录页面
                navigateTo(LoginActivity.class);
            }
        });
        //监听注册按钮点击事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到注册页面
                navigateTo(RegisterActivity.class);
            }
        });
    }
}
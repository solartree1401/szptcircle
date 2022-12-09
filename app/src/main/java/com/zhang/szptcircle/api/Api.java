package com.zhang.szptcircle.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.zhang.szptcircle.activity.LoginActivity;
import com.zhang.szptcircle.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class Api {
    private static OkHttpClient client;
    //声明请求url
    private static String requestUrl;
    //声明mParams存储(请求参数),key=value
    private static HashMap<String, Object> mParams;
    //创建api对象
    public static Api api = new Api();

    //Api空参构造
    public Api() {

    }

    //设置Api各项参数
    public static Api config(String url, HashMap<String, Object> params) {
        //构建者模式构建client请求对象并设置Api参数
        client = new OkHttpClient.Builder()
                .build();

        //基本路径+特有路径
        requestUrl = ApiConfig.BASE_URl + url;

        mParams = params;
        return api;
    }


    //使用post请求接口，带上token
    public void postRequest(Context context, final SzptCallback callback) {

        //读取sp_szptcircle.xml文件
        SharedPreferences sp = context.getSharedPreferences("sp_szptcircle", MODE_PRIVATE);
        //获取token值，默认是空串
        String token = sp.getString("token", "");


        //把请求参数转为json对象
        JSONObject jsonObject = new JSONObject(mParams);
        //转为json字符串
        String jsonStr = jsonObject.toString();


        //把json字符串添加到请求体里面
        RequestBody requestBodyJson =
                RequestBody.create(MediaType.parse("application/json;charset=utf-8")
                        , jsonStr);
        //第三步创建Request
        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("contentType", "application/json;charset=UTF-8")
                //把token添加到请求头
                .addHeader("token", token)
                //请求体
                .post(requestBodyJson)
                .build();
        //第四步创建call回调对象
        final Call call = client.newCall(request);
        //第五步发起请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure", e.getMessage());
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取相应体
                final String result = response.body().string();
                //处理完业务逻辑 echo结果给客户端
                callback.onSuccess(result);
            }
        });
    }


    //发起get请求
    public void getRequest(Context context, final SzptCallback callback) {
        //读取sp_szptcircle.xml文件
        SharedPreferences sp = context.getSharedPreferences("sp_szptcircle", MODE_PRIVATE);
        //获取token
        String token = sp.getString("token", "");
        //拼接地址和需要携带的参数
        String url = getAppendUrl(requestUrl, mParams);
        //构建请求对象
        Request request = new Request.Builder()
                //请求url
                .url(url)
                //头部添加token
                .addHeader("token", token)
                //请求方法get
                .get()
                //构建
                .build();
        //异步回调
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //打印失败信息
                Log.e("onFailure", e.getMessage());
                //回调失败
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //响应体结果
                final String result = response.body().string();
                try {
                    //将结果转换为 json对象
                    JSONObject jsonObject = new JSONObject(result);
                    //获取对象里面的状态码
                    String code = jsonObject.getString("code");
                    if (code.equals("401")) {
                        //401状态码代表未授权，跳转回登录页面
                      Intent in = new Intent(context, LoginActivity.class);
                        context.startActivity(in);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();//json异常
                }
                //处理完业务逻辑 echo结果给客户端
                callback.onSuccess(result);
            }
        });
    }

    //添加get请求的
    private String getAppendUrl(String url, Map<String, Object> map) {
        if (map != null && !map.isEmpty()) {
            //构造一个空的字符串缓冲区
            StringBuffer buffer = new StringBuffer();
            //把HashMap类型的数据转换成集合类型,然后是获得map的迭代器，
            Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
            //用作遍历map中的每一个键值对
            while (iterator.hasNext()) {

                Entry<String, Object> entry = iterator.next();
                if (StringUtils.isEmpty(buffer.toString())) {
                    //第一个参宿和前加?号
                    buffer.append("?");
                } else {
                    //后面的都用&拼接
                    buffer.append("&");
                }
                buffer.append(entry.getKey()).append("=").append(entry.getValue());
            }
            url += buffer.toString();
        }
        //返回拼接后的结果
        return url;
    }
}


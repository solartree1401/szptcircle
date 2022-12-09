package com.zhang.szptcircle.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zhang.szptcircle.R;

public class MoocFragment extends Fragment {
    private WebView webView;
    private String url="http://i.chaoxing.com/base?t=1669099079434";


    public static MoocFragment newInstance() {
        MoocFragment fragment = new MoocFragment();
        return fragment;
    }

    public MoocFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_mooc, container, false);
        webView = (WebView) view.findViewById(R.id.wv_mooc);

        initWebView();
        return view;
    }

    private void initWebView() {
        webView.setWebViewClient(new WebViewClient() {

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                //系统默认会打开系统浏览器去打开网页，为了要显示在自己的webview中必须设置这个属性
//                view.loadUrl(url);
//
//                return super.shouldOverrideUrlLoading(view, url);
//            }

            //解决Webview 重定向和 net::ERR_UNKNOWN_URL_SCHEME
@Override
public boolean shouldOverrideUrlLoading(WebView view, String url) {
    if (url == null) return false;
    if (url.startsWith("http:") || url.startsWith("https:")) {
        view.loadUrl(url);
        return false;
    } else {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            MoocFragment.this.startActivity(intent);
        } catch (Exception e) {
//                    ToastUtils.showShort("暂无应用打开此链接");
        }
        return true;
    }
}




            //加载开始时调用

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            // 加载结束时调用

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }


        });

        webView.setWebChromeClient(new WebChromeClient() {
            //加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.e("WebView>>>>>>>", newProgress + "");
                super.onProgressChanged(view, newProgress);
            }


            @Override
            public void onRequestFocus(WebView view) {
                super.onRequestFocus(view);
            }

            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

        });

        webView.loadUrl(url);
        // 得到setting
        WebSettings webSettings=webView.getSettings();
        //设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        webView.getSettings().setSupportZoom(true);

//        webView.getSettings().setBuiltInZoomControls(true);
        //支持的语言类型
        webSettings.setDefaultTextEncodingName("UTF-8");
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        webView.canGoBack();

        webSettings.setDomStorageEnabled(true);
        //支持缩放
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);

//自动适应屏幕

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setBlockNetworkImage(false);


        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK ) {
                        //这里处理返回键事件
                        if (webView.canGoBack()){
                            webView.goBack();
                            return true;
                        }
                    }
                }
                return false;
            }
        });


        //加入文件下载功能
        webView.setDownloadListener(new MyWebViewDownLoadListener());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除记录
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();

        webView.destroy();
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Log.i("tag", "url="+url);
            Log.i("tag", "userAgent="+userAgent);
            Log.i("tag", "contentDisposition="+contentDisposition);
            Log.i("tag", "mimetype="+mimetype);
            Log.i("tag", "contentLength="+contentLength);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }


}
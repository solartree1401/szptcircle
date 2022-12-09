package com.zhang.szptcircle.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhang.szptcircle.R;
import com.zhang.szptcircle.activity.LanguageTranslationActivity;
import com.zhang.szptcircle.activity.SzptMapActivity;
import com.zhang.szptcircle.activity.TextRecognitionActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private Banner banner;
    private Button btnOcr;
    private Button btnMap;
    private Button btnTran;
    private List<Integer> image=new ArrayList<>();
    private List<String> title=new ArrayList<>();
    private void initData() {
        image.add(R.mipmap.lb1);
        image.add(R.mipmap.lb2);
        image.add(R.mipmap.lb3);
        image.add(R.mipmap.lb4);
        image.add(R.mipmap.lb5);
        image.add(R.mipmap.lb6);
        title.add("学思楼");
        title.add("音乐厅");
        title.add("运动场");
        title.add("校园正门");
        title.add("溪湖");
        title.add("步云路");
        btnOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TextRecognitionActivity.class);
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SzptMapActivity.class);
                startActivity(intent);
            }
        });
        btnTran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LanguageTranslationActivity.class);
                startActivity(intent);
            }
        });

    }
    private void initView() {

        banner.setIndicatorGravity(BannerConfig.CENTER);

        banner.setImageLoader(new MyImageLoader());

        banner.setImages(image);

        banner.setBannerAnimation(Transformer.Default);

        banner.isAutoPlay(true);

        banner.setBannerTitles(title);

        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);

        banner.setDelayTime(3000);

        banner.setOnBannerListener(this::OnBannerClick);

        banner.start();
    }


    public void OnBannerClick(int position) {
//        Toast.makeText(getActivity(), "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();
    }


    private class MyImageLoader extends ImageLoader {

        public void displayImage(Context context, Object path, ImageView imageView) {

            Glide.with(context).load(path).into(imageView);

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, container, false);
        banner = view.findViewById(R.id.banner);
        btnOcr = view.findViewById(R.id.btn_ocr);
        btnMap = view.findViewById(R.id.btn_map);
btnTran = view.findViewById(R.id.btn_tranicon);
        initData();
        initView();
        return view;
    }


    public HomeFragment() {

    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
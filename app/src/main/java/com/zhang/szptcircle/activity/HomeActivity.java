package com.zhang.szptcircle.activity;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.zhang.szptcircle.R;
import com.zhang.szptcircle.fragment.HomeFragment;
import com.zhang.szptcircle.adapter.MyPagerAdapter;
import com.zhang.szptcircle.entity.TabEntity;
import com.zhang.szptcircle.fragment.MoocFragment;
import com.zhang.szptcircle.fragment.MyFragment;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {

    //底部导航标题集
    private String[] mTitles = {"首页", "学习通","我的"};
    //底部导航未选中图标集
    private int[] mIconUnselectIds = {
            R.mipmap.home_unselect,
            R.mipmap.chaoxing_unselect,R.mipmap.my_unselect};
    //底部导航选中图标集
    private int[] mIconSelectIds = {
            R.mipmap.home_selected,
            R.mipmap.chaoxing_selected,R.mipmap.my_selected};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    //ViewPager(视图滑动切换工具)
    private ViewPager viewPager;
    //FlycoTabLayout实现底部导航
    private CommonTabLayout commonTabLayout;

    @Override
    protected int initLayout() {
        return R.layout.activity_home;
    }


    @Override
    protected void initView() {
        //根据id来找到View
        viewPager = findViewById(R.id.viewpager);
        commonTabLayout = findViewById(R.id.commonTabLayout);
    }

    @Override
    protected void initData() {
        //放入三个Fragment对象分别对应三个页面的Fragment
        mFragments.add(HomeFragment.newInstance());
        mFragments.add(MoocFragment.newInstance());
        mFragments.add(MyFragment.newInstance());



        for (int i = 0; i < mTitles.length; i++) {
            //添加底部导航到mTabEntities
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        //把数据设置进去
        commonTabLayout.setTabData(mTabEntities);
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            //给viewPager设置当前Item,通过下标设置fragments
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        viewPager.setOffscreenPageLimit(mFragments.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

//切换ViewPage时候执行这个回调，把Tab跟着切换，滑动时实现联动
            @Override
            public void onPageSelected(int position) {
                commonTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //把集合Fragment渲染到页面viewPager中
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mTitles, mFragments));

    }
}
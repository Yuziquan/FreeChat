package com.wuchangi.freechat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by WuchangI on 2018/11/20.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter
{
    private List<Fragment> mFragmentList;

    public MyFragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList)
    {
        super(fragmentManager);
        mFragmentList = fragmentList;
    }


    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }
}

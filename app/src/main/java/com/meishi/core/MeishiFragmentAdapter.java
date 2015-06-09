package com.meishi.core;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.meishi.mymeishi.R;

/**
 * Created by Aaron on 2015/6/7.
 */
public class MeishiFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private Context context;

    public MeishiFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MapFragment.newInstance(position);
            case 1:
                return OrderFragment.newInstance(position);
        }
        throw new RuntimeException("no more than 2 fragments");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.map_tab_name);
            case 1:
                return context.getString(R.string.order_tab_name);
        }
        throw new RuntimeException("no more than 2 fragments");
    }
}

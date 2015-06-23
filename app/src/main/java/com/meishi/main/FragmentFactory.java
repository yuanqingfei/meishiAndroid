package com.meishi.main;

import android.app.Fragment;

import com.meishi.R;
import com.meishi.meishi.MeishiListFragment;
import com.meishi.meishi.MeishiMapFragment;

public class FragmentFactory {
    public static Fragment getFragmentForMain(int index) {
        Fragment fragment = null;
        switch (index) {
            case R.id.rb_meishi:
                fragment = new MeishiFragment();
                break;
            case R.id.rb_my:
                fragment = new MineFragment();
                break;
        }
        return fragment;
    }

    public static Fragment getFragmentForMeishi(int index) {
        Fragment fragment = null;
        switch (index) {
            case R.id.rb_meishi_map:
                fragment = new MeishiMapFragment();
                break;
            case R.id.rb_meishi_list:
                fragment = new MeishiListFragment();
                break;
        }
        return fragment;
    }
}

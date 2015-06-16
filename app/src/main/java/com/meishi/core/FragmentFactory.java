package com.meishi.core;

import android.app.Fragment;

import com.meishi.R;
import com.meishi.core.map.MapFragment;
import com.meishi.core.order.OrderFragment;


public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index) {
        Fragment fragment = null;
        switch (index) {
            case R.id.rb_map:
                fragment = new MapFragment();
                break;
            case R.id.rb_my:
                fragment = new OrderFragment();
                break;
        }
        return fragment;
    }
}

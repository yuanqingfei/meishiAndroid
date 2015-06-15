package com.meishi.core;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Aaron on 2015/6/8.
 */
public class MeishiViewPager extends ViewPager {

    public MeishiViewPager(Context context) {
        super(context);
    }

    public MeishiViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
}

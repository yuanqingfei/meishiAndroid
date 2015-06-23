package com.meishi.main;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;

import com.meishi.R;

/**
 * Created by Aaron on 2015/6/23.
 */
public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);

        fragmentManager = getFragmentManager();
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        radioGroup.setOnCheckedChangeListener(this);

        findViewById(R.id.rb_meishi).performClick();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = FragmentFactory.getFragmentForMain(checkedId);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

}

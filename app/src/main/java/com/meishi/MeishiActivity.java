package com.meishi;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.meishi.core.MeishiFragmentAdapter;
import com.meishi.login.LoginActivity;
import com.meishi.login.LogoutFragment;
import com.meishi.support.SlidingTabLayout;


/**
 * Created by Aaron on 2015/6/7.
 */
public class MeishiActivity extends AppCompatActivity {

    private static final String TAG = MeishiActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MeishiFragmentAdapter(getSupportFragmentManager(),
                MeishiActivity.this));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                OrderFragment fragment = (OrderFragment) viewPager.getAdapter().instantiateItem(viewPager, position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Give the TabLayout the ViewPager
        SlidingTabLayout tabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabLayout.setDistributeEvenly(true);
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent_material_dark);
            }
        });
        tabLayout.setViewPager(viewPager);

        // set tab if specified
        int position  = getIntent().getIntExtra("position", 0);
        Log.i(TAG, new Integer(position).toString());
        viewPager.setCurrentItem(position);

        // Set a ToolBar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setCustomView(R.layout.action_bar_layout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meishi_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchViewAction = (SearchView) MenuItemCompat
                .getActionView(searchMenuItem);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager
                .getSearchableInfo(getComponentName());
        searchViewAction.setSearchableInfo(searchableInfo);
        searchViewAction.setIconifiedByDefault(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.login:
                login();
                return true;
            case R.id.logout:
                logout();
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        FragmentManager fm = getSupportFragmentManager();
        LogoutFragment alertDialog = LogoutFragment.newInstance(getString(R.string.logout_title));
        alertDialog.show(fm, "fragment_alert");
    }

    private void login() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, LoginActivity.class));
        startActivity(intent);
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

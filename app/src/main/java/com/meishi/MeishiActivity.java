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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.meishi.core.MeishiFragmentAdapter;
import com.meishi.logon.LoginActivity;
import com.meishi.logon.LogoutFragment;
import com.meishi.register.RegisterActivity;
import com.meishi.support.SlidingTabLayout;


/**
 * Created by Aaron on 2015/6/7.
 */
public class MeishiActivity extends AppCompatActivity implements LogoutFragment.CustomAlertListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MeishiFragmentAdapter(getSupportFragmentManager(),
                MeishiActivity.this));

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
            case R.id.register:
                registerUser();
                return true;
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
        intent.setComponent(new ComponentName(MeishiActivity.this, LoginActivity.class));
        startActivity(intent);
    }

    private void registerUser() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(MeishiActivity.this, RegisterActivity.class));
        startActivity(intent);
    }


    @Override
    public void onOKButton() {
        Toast.makeText(this, "Pressed OK!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelButton() {
        Toast.makeText(this, "Pressed Cancel!", Toast.LENGTH_SHORT).show();
    }


}

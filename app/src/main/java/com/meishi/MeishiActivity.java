package com.meishi;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.meishi.core.MeishiFragmentAdapter;
import com.meishi.logon.LoginActivity;
import com.meishi.logon.LogoutFragment;
import com.meishi.mymeishi.R;


/**
 * Created by Aaron on 2015/6/7.
 */
public class MeishiActivity extends AppCompatActivity implements LogoutFragment.CustomAlertListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_acitivity);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MeishiFragmentAdapter(getSupportFragmentManager(),
                MeishiActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Set a ToolBar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Add search view
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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
            case R.id.search:
                onSearchRequested();
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
        Toast.makeText(this, "register", Toast.LENGTH_SHORT).show();
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

package com.meishi;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.meishi.core.FragmentFactory;
import com.meishi.login.LoginActivity;
import com.meishi.login.LogoutFragment;
import com.meishi.support.Constants;


/**
 * Created by Aaron on 2015/6/7.
 */
public class MeishiActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    private FragmentManager fragmentManager;

    private static final String TAG = MeishiActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);

        fragmentManager = getFragmentManager();
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        radioGroup.setOnCheckedChangeListener(this);

        // set tab if specified
        int position  = getIntent().getIntExtra(Constants.POSITION_BUNDILE_ID, 0);
        Log.i(TAG, new Integer(position).toString());
        if(position == 0){
            findViewById(R.id.rb_map).performClick();
        }
        if(position == 1){
            findViewById(R.id.rb_my).performClick();
        }

        //actionbar with search
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setCustomView(R.layout.action_bar_layout);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = FragmentFactory.getInstanceByIndex(checkedId);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meishi_menu, menu);

//        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
//        SearchView searchViewAction = (SearchView) getActionView(searchMenuItem);
//
//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchableInfo searchableInfo = searchManager
//                .getSearchableInfo(getComponentName());
//        searchViewAction.setSearchableInfo(searchableInfo);
//        searchViewAction.setIconifiedByDefault(false);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        FragmentManager fm = getFragmentManager();
        LogoutFragment alertDialog = LogoutFragment.newInstance(getString(R.string.logout_title));
        alertDialog.show(fm, "fragment_alert");
    }

    private void login() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, LoginActivity.class));
        startActivity(intent);
    }


//    private long exitTime = 0;
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}

package com.meishi.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.meishi.MeishiApplication;
import com.meishi.R;
import com.meishi.login.LoginActivity;
import com.meishi.model.Customer;

/**
 * Created by Aaron on 2015/6/23.
 */
public class MyProfileActivity extends Activity {

    private TextView myName;

    private TextView myAccount;

    private TextView myBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        myName = (TextView)findViewById(R.id.myNameValueView);
        myAccount = (TextView)findViewById(R.id.myAccountValueView);
        myBalance = (TextView)findViewById(R.id.myBalanceValueView);

        MeishiApplication app = (MeishiApplication) this.getApplication();
        Customer client = app.getCustomer();
        if(client != null){
            myName.setText(client.getName());
            myAccount.setText(client.getIdentity());
            myBalance.setText(String.valueOf(client.getAccountBalance()));
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}

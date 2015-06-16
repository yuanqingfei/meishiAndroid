package com.meishi.register;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.meishi.R;
import com.meishi.model.Customer;
import com.meishi.rest.PostCustomerTask;
import com.meishi.support.Constants;

public class RegisterActivity extends Activity implements OnGetGeoCoderResultListener {

    private static final String TAG = RegisterActivity.class.getName();

    private GeoCoder mSearch = null;

    private Button submitButton;
    private EditText identity;
    private EditText password;
    private EditText password2;
    private EditText name;
    private EditText address;

    private Customer customer;

    private PostCustomerTask postTask;

    private AlertDialog alertDialog;

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // create location converter for future
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);


        identity = (EditText) findViewById(R.id.identityValue);
        password = (EditText) findViewById(R.id.passwordValue);
        password2 = (EditText) findViewById(R.id.passwordValue2);
        name = (EditText) findViewById(R.id.nameValue);
        address = (EditText) findViewById(R.id.addressValue);

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                customer = new Customer();
                customer.setIdentity(identity.getText().toString());
                customer.setPassword(password.getText().toString());
                customer.setName(name.getText().toString());
                customer.setTelephoneNumber(identity.getText().toString());
                String addressValue = address.getText().toString();
                customer.setAddress(addressValue);
                mSearch.geocode(new GeoCodeOption().city(Constants.CITY).address(addressValue));
            }
        });

        // add attached alert diaglog
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("账户创建");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }


    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，您提供的地址无法定位，请输入更具体地址。", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        customer.setLocation(new double[]{result.getLocation().longitude, result.getLocation().latitude});

        postTask = new PostCustomerTask(this, customer.getIdentity());
        postTask.execute(new Customer[]{customer});
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        Toast.makeText(this, result.getAddress(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        mSearch.destroy();
        super.onDestroy();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }


}

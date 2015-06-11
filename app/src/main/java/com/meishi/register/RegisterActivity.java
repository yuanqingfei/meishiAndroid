package com.meishi.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class RegisterActivity extends AppCompatActivity implements OnGetGeoCoderResultListener {

    private static final String TAG = RegisterActivity.class.getName();

    private GeoCoder mSearch = null;

    private Button submitButton;
    private EditText email;
    private EditText password;
    private EditText name;
    private EditText idNumber;
    private EditText cellPhone;
    private EditText address;

    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // create location converter for future
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);


        email = (EditText)findViewById(R.id.emailValue);
        password = (EditText)findViewById(R.id.passwordValue);
        name = (EditText)findViewById(R.id.nameValue);
        idNumber = (EditText)findViewById(R.id.personalIdValue);
        cellPhone = (EditText)findViewById(R.id.cellPhoneValue);
        address = (EditText)findViewById(R.id.addressValue);

        submitButton = (Button)findViewById(R.id.submit);
        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "into callback");
                customer = new Customer();
                customer.setIdentity(email.getText().toString());
                customer.setPassword(password.getText().toString());
                customer.setName(name.getText().toString());
                customer.setTelephoneNumber(cellPhone.getText().toString());
                customer.setAddress(address.getText().toString());
                mSearch.geocode(new GeoCodeOption().city("上海").address("老沪闵路1296弄39号101室"));

                Log.i(TAG, "start revert");
//                mSearch.geocode(new GeoCodeOption().city("上海").address("老沪闵路1296弄"));
                Log.i(TAG, "end revert");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();

        customer.setLocation(new double[]{result.getLocation().longitude, result.getLocation().latitude});

        Log.i(TAG, "longitude: " + new Double(customer.getLocation()[0]).toString());
        Log.i(TAG, "latitude: " + new Double(customer.getLocation()[1]).toString());
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
    }
}

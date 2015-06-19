package com.meishi.core.cook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.meishi.MeishiApplication;
import com.meishi.R;
import com.meishi.model.Cook;
import com.meishi.model.Customer;
import com.meishi.model.Dish;
import com.meishi.model.OrderRequest;
import com.meishi.rest.GetDishTask;
import com.meishi.rest.PostOrderTask;
import com.meishi.support.Constants;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Aaron on 2015/6/17.
 */
public class CookActivity extends Activity implements OnGetGeoCoderResultListener {
    private static final String TAG = CookActivity.class.getSimpleName();

    private List<Dish> dishes;

    private String finalAddress;

    private String dishName;

    private GeoCoder mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        // prepare data
        Cook cook = (Cook) getIntent().getSerializableExtra(Constants.COOK_BUNDLE_ID);
        List<String> dishIds = cook.getDishIds();
        GetDishTask getDishTask = new GetDishTask(this);
        getDishTask.execute(dishIds.toArray(new String[dishIds.size()]));
        try {
            dishes = getDishTask.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (TimeoutException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        // update view
        setContentView(R.layout.activity_cook);
        TextView cookNameView = (TextView) findViewById(R.id.cook_name);
        cookNameView.setText(cook.getName());
        LinearLayout dishLayout = (LinearLayout) findViewById(R.id.dish_layout);

        for (final Dish dish : dishes) {
            dishName = dish.getName();

            TextView dishNameView = new TextView(this);
            dishNameView.setText(dish.getName());
            dishLayout.addView(dishNameView);

            Button dishButton = new Button(this);
            dishButton.setText("下单");
            dishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(CookActivity.this).create();
                    alertDialog.setTitle("送货地址");

                    final EditText addressEdit = new EditText(CookActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    addressEdit.setLayoutParams(lp);
                    alertDialog.setView(addressEdit);

                    final Customer customer = ((MeishiApplication) getApplication()).getCustomer();
                    alertDialog.setMessage("使用注册地址：" + customer.getAddress() + " 或者提供一个地址");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (addressEdit.getText() != null && addressEdit.getText().length() > 0) {
                                        finalAddress = addressEdit.getText().toString();
                                        mSearch.geocode(new GeoCodeOption().city(Constants.CITY).address(finalAddress));
                                    } else {
                                        post(null);
                                    }
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }
            });
            dishLayout.addView(dishButton);
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，您提供的地址无法定位，请输入更具体地址。", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        post(result);
    }

    private void post(GeoCodeResult result){
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setClientId(((MeishiApplication) getApplication()).getCustomerId());
        orderRequest.setDishName(dishName);
        if(result != null) {
            orderRequest.setAddress(finalAddress + ":" + result.getLocation().longitude + "," + result.getLocation().latitude);
        }

        PostOrderTask task = new PostOrderTask(CookActivity.this);
        task.execute(orderRequest);
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

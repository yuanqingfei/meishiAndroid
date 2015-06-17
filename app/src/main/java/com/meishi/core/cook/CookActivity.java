package com.meishi.core.cook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishi.MeishiApplication;
import com.meishi.R;
import com.meishi.login.LoginActivity;
import com.meishi.model.Cook;
import com.meishi.model.Dish;
import com.meishi.rest.GetDishTask;
import com.meishi.rest.OrderRequest;
import com.meishi.rest.PostOrderTask;
import com.meishi.support.Constants;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Aaron on 2015/6/17.
 */
public class CookActivity extends Activity {
    private static final String TAG = CookActivity.class.getSimpleName();

    private Cook cook;

    private List<Dish> dishes;

    private TextView cookNameView;

    private TextView dishNameView;

    private LinearLayout dishLayout;

    private Button dishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // prepare data
        cook = (Cook) getIntent().getSerializableExtra(Constants.COOK_BUNDLE_ID);
        List<String> dishIds = cook.getDishIds();
        GetDishTask getDishTask = new GetDishTask(this);
        getDishTask.execute(dishIds.toArray(new String[dishIds.size()]));
        try {
            dishes = getDishTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }

        // update view
        setContentView(R.layout.activity_cook);
        cookNameView = (TextView) findViewById(R.id.cook_name);
        cookNameView.setText(cook.getName());
        dishLayout = (LinearLayout) findViewById(R.id.dish_layout);

        for (final Dish dish : dishes) {
            dishNameView = new TextView(this);
            dishNameView.setText(dish.getName());
            dishLayout.addView(dishNameView);
            dishButton = new Button(this);
            dishButton.setText("下单");

            dishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostOrderTask task = new PostOrderTask(CookActivity.this);
                    String clientId = ((MeishiApplication)getApplication()).getCustomerId();
                    if(clientId == null){
                        Intent intent = new Intent(CookActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        OrderRequest orderRequest = new OrderRequest();
                        orderRequest.setClientId(clientId);
                        orderRequest.setDish(dish);
                        orderRequest.setLocation(cook.getLocation());
                        task.execute(orderRequest);
                    }
                }
            });
        }
    }
}

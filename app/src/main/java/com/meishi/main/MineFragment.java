package com.meishi.main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishi.MeishiApplication;
import com.meishi.R;
import com.meishi.login.LoginActivity;
import com.meishi.mine.AboutActivity;
import com.meishi.mine.MyProfileActivity;
import com.meishi.model.Customer;
import com.meishi.mine.MyOrderActivity;

/**
 * Created by Aaron on 2015/6/23.
 */
public class MineFragment extends Fragment {

    private TextView myProfileView;

    private TextView myOrderView;

    private LinearLayout selfLayout;

    private TextView aboutUsView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment_layout, container, false);

        myProfileView = (TextView)view.findViewById(R.id.my_profile);
        myOrderView = (TextView)view.findViewById(R.id.my_order);
        aboutUsView = (TextView) view.findViewById(R.id.about_us);
        selfLayout = (LinearLayout)view.findViewById(R.id.showSelf);

        myProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(intent);
            }
        });

        myOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyOrderActivity.class);
                startActivity(intent);
            }
        });

        aboutUsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        Customer customer = ((MeishiApplication)getActivity().getApplication()).getCustomer();
        if(customer == null || customer.getIdentity() == null){
            Button registerButton = new Button(getActivity());
            registerButton.setText("注册/登录");
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            selfLayout.addView(registerButton);
            selfLayout.setGravity(Gravity.CENTER);
        } else {
            TextView textView = new TextView(getActivity());
            textView.setText("欢迎你, " + customer.getName());
            selfLayout.addView(textView);
            selfLayout.setGravity(Gravity.CENTER);
        }

        return view;
    }

}

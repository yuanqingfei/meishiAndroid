package com.meishi.rest;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by Aaron on 2015/6/12.
 */
public class SimpleAsync implements AsyncInterface {
    private Activity activity;
    private ProgressDialog progressDialog;

    public SimpleAsync(Activity activity) {
        this.activity = activity;
    }

    public void showLoadingProgressDialog() {
        this.showProgressDialog("请等待...");
    }

    public void showProgressDialog(CharSequence message) {
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(activity);
            this.progressDialog.setIndeterminate(true);
        }

        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (this.progressDialog != null && !activity.isDestroyed()) {
            this.progressDialog.dismiss();
        }
    }
}

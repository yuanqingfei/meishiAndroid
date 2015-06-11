package com.meishi.rest;

import android.app.ListActivity;
import android.app.ProgressDialog;

/**
 * Created by Aaron on 2015/6/11.
 */
public abstract class AbstractAsyncListActivity extends ListActivity implements AsyncActivity {

    protected static final String TAG = AbstractAsyncActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    private boolean destroyed = false;

    // ***************************************
    // Activity methods
    // ***************************************
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.destroyed = true;
    }

    // ***************************************
    // Public methods
    // ***************************************
    public void showLoadingProgressDialog() {
        this.showProgressDialog("Loading. Please wait...");
    }

    public void showProgressDialog(CharSequence message) {
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setIndeterminate(true);
        }

        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (this.progressDialog != null && !this.destroyed) {
            this.progressDialog.dismiss();
        }
    }

}

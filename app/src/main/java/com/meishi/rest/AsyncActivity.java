package com.meishi.rest;

/**
 * Created by Aaron on 2015/6/11.
 */
public interface AsyncActivity {

    public void showLoadingProgressDialog();

    public void showProgressDialog(CharSequence message);

    public void dismissProgressDialog();

}

package com.meishi.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.meishi.MeishiApplication;
import com.meishi.R;

/**
 * Created by Aaron on 2015/6/8.
 */
public class LogoutFragment extends DialogFragment {

    public LogoutFragment() {
        // Empty constructor required for DialogFragment
    }

    public static LogoutFragment newInstance(String title) {
        LogoutFragment frag = new LogoutFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(getString(R.string.logout_content));
        alertDialogBuilder.setPositiveButton(getString(R.string.logout_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // reset current client id to null;
                ((MeishiApplication) getActivity().getApplication()).setCustomerId(null);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.logout_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
    }
}

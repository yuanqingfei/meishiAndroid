package com.meishi.logon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.meishi.mymeishi.R;

/**
 * Created by Aaron on 2015/6/8.
 */
public class LogoutFragment extends DialogFragment {
    public interface CustomAlertListener {
        void onOKButton();
        void onCancelButton();
    }

    private CustomAlertListener listener;

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
        listener = (CustomAlertListener) getActivity();
        alertDialogBuilder.setPositiveButton(getString(R.string.logout_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onOKButton();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.logout_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onCancelButton();
                dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
    }
}

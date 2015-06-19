package com.meishi.login;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.meishi.R;
import com.meishi.register.RegisterActivity;
import com.meishi.rest.GetCustomerTask;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // UI references.
    private AutoCompleteTextView identityView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mRegisterView;

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Set up the login form.
        identityView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mRegisterView = findViewById(R.id.registerView);
        mRegisterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        identityView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String identity = identityView.getText().toString();
        String password = mPasswordView.getText().toString();

        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
        }

        // Check for a valid identity address.
        if (TextUtils.isEmpty(identity)) {
            identityView.setError(getString(R.string.error_field_required));
            focusView = identityView;
        } else if (!isIdentityValid(identity)) {
            identityView.setError(getString(R.string.error_invalid_email));
            focusView = identityView;
        }

        GetCustomerTask mAuthTask = new GetCustomerTask(this, mPasswordView, identity, password);
        mAuthTask.execute();

    }

    private boolean isIdentityValid(String identity) {
        return identity.length() >= 3;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 3;
    }


}


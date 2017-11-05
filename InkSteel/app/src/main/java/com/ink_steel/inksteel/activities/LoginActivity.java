package com.ink_steel.inksteel.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.fragments.LoginFragment;
import com.ink_steel.inksteel.helpers.Listeners.OnLoginActivityButtonClickListener;
import com.ink_steel.inksteel.helpers.PermissionUtil;

import static com.ink_steel.inksteel.fragments.ProfileFragment.IS_SIGN_OUT;

public class LoginActivity extends AppCompatActivity implements DatabaseManager.UserManagerListener,
        OnLoginActivityButtonClickListener {

    public static final String IS_NEW_USER = "isNewUser";
    private DatabaseManager mUserManager;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View container = findViewById(R.id.activity_login_container);
        mSnackbar = Snackbar.make(container, "Loading...", Snackbar.LENGTH_INDEFINITE);
        Intent intent = getIntent();
        if (intent.hasExtra(IS_SIGN_OUT) && !intent.getBooleanExtra(IS_SIGN_OUT, false)) {
            mSnackbar.show();
        }

        mUserManager = DatabaseManager.getInstance();
        mUserManager.checkIfSignedIn(this);
        LoginFragment fragment = new LoginFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_placeholder, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onUserLogInError(String error) {
        showAlert(error);
        mSnackbar.dismiss();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    private void showAlert(String error) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertBuilder.setMessage(error)
                .setCancelable(false)
                .setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ad = alertBuilder.create();
        ad.setTitle(getString(R.string.warning_msg));
        ad.setIcon(R.drawable.warning_msg);
        ad.show();
    }

    @Override
    public void onUserSignUpError(String error) {
        showAlert(error);
        mSnackbar.dismiss();
    }

    @Override
    public void onUserInfoLoaded() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        if (mUserManager.getCurrentUser().getName().isEmpty()) {
            intent.putExtra(IS_NEW_USER, true);
        } else {
            intent.putExtra(IS_NEW_USER, false);
        }
        mSnackbar.dismiss();
        startActivity(intent);
    }

    @Override
    public void onButtonClick(ButtonType buttonType, String email, String password) {
        switch (buttonType) {
            case LOGIN:
                mUserManager.loginUser(this, email, password);
                break;
            case REGISTER:
                mUserManager.signUpUser(this, email, password);
                break;
        }
        mSnackbar.dismiss();
    }

    public enum ButtonType {
        LOGIN, REGISTER
    }
}
package com.ink_steel.inksteel.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.fragments.LoginFragment;
import com.ink_steel.inksteel.fragments.RegisterFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements DatabaseManager.OnUserLoginListener,
        LoginFragment.OnLoginButtonSelectedListener, RegisterFragment.OnRegisterButtonSelectedListener {

    public static final String IS_NEW_USER = "isNewUser";
    @BindView(R.id.activity_login_container)
    View mContainer;
    private DatabaseManager.UserManager mDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mDatabaseManager = DatabaseManager.getUserManager();
        Log.d("posts", "opened login activity");

        LoginFragment fragment = new LoginFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_login_fragment_placeholder, fragment)
                .commit();
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
    public void onUserLoggedIn(boolean isNewUser) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra(IS_NEW_USER, isNewUser);
        startActivity(intent);
    }

    @Override
    public void onError(String errorMessage) {
        showAlert(errorMessage);
    }

    @Override
    public void onLoginButtonSelected(String email, String password) {
        mDatabaseManager.loginUser(this, email, password);
    }

    @Override
    public void onRegisterButtonSelected(String email, String password) {
        mDatabaseManager.registerUser(this, email, password);
    }
}
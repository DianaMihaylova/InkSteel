package com.ink_steel.inksteel.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.FirebaseManager;
import com.ink_steel.inksteel.data.UserManager;
import com.ink_steel.inksteel.helpers.IOnFragmentButtonListener;
import com.ink_steel.inksteel.fragments.LoginFragment;
import com.ink_steel.inksteel.helpers.ConstantUtils;

public class LoginActivity extends AppCompatActivity implements IOnFragmentButtonListener,
        UserManager.UserManagerListener {

    public static final String IS_NEW_USER = "isNewUser";
    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserManager = UserManager.getInstance();
        mUserManager.checkIfSignedIn(this);

        LoginFragment fragment = new LoginFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_placeholder, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFragmentButtonListener(int which, String email, String password) {
        switch (which) {
            case ConstantUtils.LOGIN_BUTTON:
                mUserManager.loginUser(this, email, password);
                break;
            case ConstantUtils.REGISTER_BUTTON:
                mUserManager.signUpUser(this, email, password);
                break;
        }
    }

    @Override
    public void onUserLogInError(String error) {
        // handle error
        showAlert(error);
    }

    private void showAlert(String error) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertBuilder.setMessage(error)
                .setCancelable(false)
                .setPositiveButton("BACK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ad = alertBuilder.create();
        ad.setTitle("WARNING MESSAGE");
        ad.setIcon(R.drawable.warning_msg);
        ad.show();
    }

    @Override
    public void onUserSignUpError(String error) {
        // handle error
    }

    @Override
    public void onUserInfoLoaded() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        if (mUserManager.getCurrentUser().getName().isEmpty())
            intent.putExtra(IS_NEW_USER, true);
        else
            intent.putExtra(IS_NEW_USER, false);
        startActivity(intent);
    }
}
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
import com.ink_steel.inksteel.fragments.IOnFragmentButtonListener;
import com.ink_steel.inksteel.fragments.LoginFragment;
import com.ink_steel.inksteel.helpers.ConstantUtils;
import com.ink_steel.inksteel.model.CurrentUser;

public class LoginActivity extends AppCompatActivity implements IOnFragmentButtonListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        LoginFragment fragment = new LoginFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_placeholder, fragment)
                .addToBackStack(null)
                .commit();

        if (CurrentUser.getInstance() != null) {
            goToFeed();
        }
    }

    private void goToFeed() {
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i);
    }

    @Override
    public void onFragmentButtonListener(int which, String email, String password) {
        switch (which) {
            case ConstantUtils.LOGIN_BUTTON:
                loginUser(email, password, false);
                break;
            case ConstantUtils.REGISTER_BUTTON:
                registerUser(email, password);
                break;
        }
    }

    private void loginUser(String email, String password, final boolean isNewUser) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Sign in " + ConstantUtils.USER_EMAIL,
                            Toast.LENGTH_SHORT).show();
                    CurrentUser.getInstance();
                    Intent i = new Intent(LoginActivity.this, UserInfoActivity.class);
                    i.putExtra("isNewUser", isNewUser);
                    startActivity(i);
                } else {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertBuilder.setMessage("Wrong e-mail or password! Try again.")
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
            }
        });
    }

    private void registerUser(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "User registered!\n" +
                                    ConstantUtils.USER_EMAIL, Toast.LENGTH_SHORT).show();
                            loginUser(email, password, true);
                        } else {
                            Toast.makeText(LoginActivity.this, "User NOT registered!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
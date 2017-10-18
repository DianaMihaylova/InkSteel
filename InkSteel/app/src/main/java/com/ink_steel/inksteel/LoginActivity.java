package com.ink_steel.inksteel;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ink_steel.inksteel.fragments.IOnFragmentButtonListener;
import com.ink_steel.inksteel.fragments.LoginFragment;


public class LoginActivity extends AppCompatActivity implements IOnFragmentButtonListener {

    public static final int LOGIN_BUTTON = 1;
    public static final int REGISTER_BUTTON = 2;
    public static final String LOGIN_EMAIL = "login_email";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        LoginFragment fragment = new LoginFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder, fragment);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Already SIGNED-IN\n" + currentUser.getEmail(),
                    Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString(LOGIN_EMAIL, currentUser.getEmail());
            fragment.setArguments(bundle);
        }

        fragmentTransaction.commit();
        goToFeed(currentUser);
    }

    private void goToFeed(FirebaseUser user) {
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i);
    }

    @Override
    public void onFragmentButtonListener(int which, String email, String password) {
        switch (which) {
            case LOGIN_BUTTON:
                loginUser(email, password);
                break;
            case REGISTER_BUTTON:
                registerUser(email, password);
                break;
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Sign in " + mAuth.getCurrentUser()
                            .getEmail(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, UserInfoActivity.class);
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
        Toast.makeText(this, "REGISTER", Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "User registered!\n" +
                                            currentUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            loginUser(email, password);
                        } else {
                            Toast.makeText(LoginActivity.this, "User NOT registered!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
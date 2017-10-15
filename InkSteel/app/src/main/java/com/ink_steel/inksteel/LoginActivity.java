package com.ink_steel.inksteel;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ink_steel.inksteel.fragments.IOnFragmentButtonListener;
import com.ink_steel.inksteel.fragments.LoginFragment;
import com.ink_steel.inksteel.fragments.RegisterFragment;

public class LoginActivity extends AppCompatActivity implements IOnFragmentButtonListener {

    public static final int LOGIN_BUTTON = 1;
    public static final int REGISTER_BUTTON = 3;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginFragment fragment = new LoginFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_placeholder, fragment);
        fragmentTransaction.commit();
        // yes
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            Toast.makeText(this, "Already SIGNED-IN\n" + currentUser.getEmail(),
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentButtonListener(int which, String email, String password) {
        switch (which) {
            case LOGIN_BUTTON:
                Toast.makeText(this, "LOGIN", Toast.LENGTH_SHORT).show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Sign in " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Not sign in!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case REGISTER_BUTTON:
                registerUser(email, password);
                break;
        }
    }

    private void registerUser(String email, String password) {
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
                        } else {
                            Toast.makeText(LoginActivity.this, "User NOT registered!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ink_steel.inksteel.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginFragment extends Fragment {
    @BindView(R.id.fragment_login_email_et)
    EditText mEmail;
    @BindView(R.id.fragment_login_password_et)
    EditText mPassword;
    @BindView(R.id.fragment_login_login_btn)
    Button mLoginButton;
    @BindView(R.id.fragment_login_register_btn)
    Button mRegisterButton;
    private OnLoginButtonSelectedListener mLoginButtonSelectedListener;

    public LoginFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnLoginButtonSelectedListener) {
            mLoginButtonSelectedListener = (OnLoginButtonSelectedListener) context;
        } else {
            Log.e(LoginFragment.class.getSimpleName(), "Activity not implementing " +
                    "OnLoginButtonSelectedListener interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });

        return view;
    }

    private void goToRegister() {
        RegisterFragment fragment = new RegisterFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_login_fragment_placeholder, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void loginUser() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if (areFieldsValid(email, password)) {
            mLoginButtonSelectedListener.onLoginButtonSelected(email, password);
        }
    }

    private boolean areFieldsValid(String email, String password) {
        if (email.isEmpty()) {
            mEmail.setError(getString(R.string.invalid_email));
            return false;
        }
        if (password.isEmpty() || mPassword.length() < 6) {
            mPassword.setError(getString(R.string.invalid_pass));
            return false;
        }
        return true;
    }

    public interface OnLoginButtonSelectedListener {
        void onLoginButtonSelected(String email, String password);
    }
}
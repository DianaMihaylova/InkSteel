package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.LoginActivity;
import com.ink_steel.inksteel.helpers.ConstantUtils;

public class LoginFragment extends Fragment {

    private EditText mEmail, mPass;
    private LoginActivity mLoginActivity;
    private String mUserEmail;
    private String mPassword;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginActivity = (LoginActivity) getActivity();

        mEmail = (EditText) view.findViewById(R.id.login_email);
        mPass = (EditText) view.findViewById(R.id.login_pass);
        Button logBtn = (Button) view.findViewById(R.id.login_login_btn);
        Button regBtn = (Button) view.findViewById(R.id.login_register_btn);

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserEmail = mEmail.getText().toString();
                mPassword = mPass.getText().toString();
                if (areFieldsValid()) {
                    mLoginActivity.onFragmentButtonListener(ConstantUtils.LOGIN_BUTTON,
                            mUserEmail, mPassword);
                }
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegisterFragment fragment = new RegisterFragment();

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_placeholder, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private boolean areFieldsValid() {
        if (mUserEmail.isEmpty() || mUserEmail == null) {
            mEmail.setError("Invalid email!");
            return false;
        }
        if (mPassword.isEmpty() || mPassword == null) {
            mPass.setError("Invalid password!");
            return false;
        }
        return true;
    }
}
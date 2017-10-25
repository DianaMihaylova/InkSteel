package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.LoginActivity;
import com.ink_steel.inksteel.helpers.ConstantUtils;

public class RegisterFragment extends Fragment {

    private EditText mUserEmailEt, mUserPasswordEt, mConfirmPasswordEt;
    private LoginActivity mLoginActivity;
    private String mUserEmail;
    private String mPassword;
    private String mConfirmPassword;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mLoginActivity = (LoginActivity) getActivity();

        mUserEmailEt = (EditText) view.findViewById(R.id.login_email);
        mUserPasswordEt = (EditText) view.findViewById(R.id.login_pass);
        mConfirmPasswordEt = (EditText) view.findViewById(R.id.login_conf_pass);
        Button regBtn = (Button) view.findViewById(R.id.login_register_btn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserEmail = mUserEmailEt.getText().toString();
                mPassword = mUserPasswordEt.getText().toString();
                mConfirmPassword = mConfirmPasswordEt.getText().toString();
                if (areFieldsValid()) {
                    mLoginActivity.onFragmentButtonListener(
                            ConstantUtils.REGISTER_BUTTON, mUserEmail, mPassword);
                }
            }
        });

        return view;
    }

    private boolean areFieldsValid() {
        if (mUserEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mUserEmail).matches()) {
            mUserEmailEt.setError("Invalid email!");
            return false;
        }
        if (mPassword.length() < 6) {
            mUserPasswordEt.setError("Password too short! Minimum length - 6 characters.");
            return false;
        }
        if (!mPassword.equals(mConfirmPassword)) {
            mConfirmPasswordEt.setError("Passwords don't match!");
            return false;
        }
        return true;
    }
}

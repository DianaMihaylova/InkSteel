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

public class RegisterFragment extends Fragment {

    private EditText email, pass, confirmPass;
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

        email = (EditText) view.findViewById(R.id.login_email);
        pass = (EditText) view.findViewById(R.id.login_pass);
        confirmPass = (EditText) view.findViewById(R.id.login_conf_pass);
        Button regBtn = (Button) view.findViewById(R.id.login_register_btn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserEmail = email.getText().toString();
                mPassword = pass.getText().toString();
                mConfirmPassword = confirmPass.getText().toString();
                if (isValidFields()) {
                    mLoginActivity.onFragmentButtonListener(
                            ConstantUtils.REGISTER_BUTTON, mUserEmail, mPassword);
                }
            }
        });

        return view;
    }

    private boolean isValidFields() {
        if (mUserEmail.isEmpty()) {
            email.setError("Invalid email!");
            return false;
        }
        if (mPassword.isEmpty()) {
            pass.setError("Invalid pass!");
            return false;
        }
        if (!mPassword.equals(mConfirmPassword)) {
            confirmPass.setError("Passwords don't match!");
            return false;
        }
        return true;
    }
}

package com.ink_steel.inksteel.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.LoginActivity;
import com.ink_steel.inksteel.helpers.Listeners.OnLoginActivityButtonClickListener;

import static com.ink_steel.inksteel.activities.LoginActivity.ButtonType.REGISTER;

public class RegisterFragment extends Fragment {

    private EditText mUserEmailEt, mUserPasswordEt, mConfirmPasswordEt;
    private LoginActivity mLoginActivity;
    private String mUserEmail;
    private String mPassword;
    private String mConfirmPassword;
    private OnLoginActivityButtonClickListener mListener;

    public RegisterFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof LoginActivity) {
            mListener = (LoginActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mLoginActivity = (LoginActivity) getActivity();

        mUserEmailEt = view.findViewById(R.id.login_email);
        mUserPasswordEt = view.findViewById(R.id.login_pass);
        mConfirmPasswordEt = view.findViewById(R.id.login_conf_pass);
        Button regBtn = view.findViewById(R.id.login_register_btn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return view;
    }

    private void registerUser() {
        if (mListener == null) {
            Activity activity = getActivity();
            if (activity instanceof LoginActivity) {
                mListener = (OnLoginActivityButtonClickListener) activity;
            } else {
                activity.finish();
            }
        }

        mUserEmail = mUserEmailEt.getText().toString();
        mPassword = mUserPasswordEt.getText().toString();
        mConfirmPassword = mConfirmPasswordEt.getText().toString();
        if (areFieldsValid()) {
            mLoginActivity.onButtonClick(REGISTER, mUserEmail, mPassword);
        }
    }

    private boolean areFieldsValid() {
        if (mUserEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mUserEmail).matches()) {
            mUserEmailEt.setError(getString(R.string.invalid_email));
            return false;
        }
        if (mPassword.length() < 6) {
            mUserPasswordEt.setError(getString(R.string.pass_short));
            return false;
        }
        if (!mPassword.equals(mConfirmPassword)) {
            mConfirmPasswordEt.setError(getString(R.string.pass_not_match));
            return false;
        }
        return true;
    }
}
package com.ink_steel.inksteel.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.LoginActivity;

import com.ink_steel.inksteel.helpers.Listeners.OnLoginActivityButtonClickListener;

import static com.ink_steel.inksteel.activities.LoginActivity.ButtonType.LOGIN;

public class LoginFragment extends Fragment {

    private EditText mEmail, mPass;
    private String mUserEmail;
    private String mPassword;
    private OnLoginActivityButtonClickListener mListener;

    public LoginFragment() {
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

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mEmail = view.findViewById(R.id.login_email);
        mPass = view.findViewById(R.id.login_pass);
        Button logBtn = view.findViewById(R.id.login_login_btn);
        Button regBtn = view.findViewById(R.id.login_register_btn);

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
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
                .replace(R.id.fragment_placeholder, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loginUser() {
        if (mListener == null) {
            Activity activity = getActivity();
            if (activity instanceof LoginActivity) {
                mListener = (OnLoginActivityButtonClickListener) activity;
            } else {
                activity.finish();
            }
        }
        mUserEmail = mEmail.getText().toString();
        mPassword = mPass.getText().toString();
        if (areFieldsValid()) {
            mListener.onButtonClick(LOGIN, mUserEmail, mPassword);
        }
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
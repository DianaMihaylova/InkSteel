package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

    private EditText email, pass;
    private LoginActivity mLoginActivity;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginActivity = (LoginActivity) getActivity();

        email = (EditText) view.findViewById(R.id.login_email);
        pass = (EditText) view.findViewById(R.id.login_pass);
        Button logBtn = (Button) view.findViewById(R.id.login_login_btn);
        Button regBtn = (Button) view.findViewById(R.id.login_register_btn);

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String password = pass.getText().toString();
                mLoginActivity.onFragmentButtonListener(ConstantUtils.LOGIN_BUTTON,
                        userEmail, password);
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
}
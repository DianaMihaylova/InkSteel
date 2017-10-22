package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ink_steel.inksteel.activities.LoginActivity;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.ConstantUtils;

public class RegisterFragment extends Fragment {

    private EditText email, pass, confirmPass;
    private Button regBtn;
    private LoginActivity mLoginActivity;

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
        regBtn = (Button) view.findViewById(R.id.login_register_btn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String password = pass.getText().toString();
                String confirmPassword = confirmPass.getText().toString();
                boolean valid = true;
                if (userEmail.isEmpty()) {
                    email.setError("Invalid email!");
                    valid = false;
                }
                if (password.isEmpty()) {
                    pass.setError("Invalid pass!");
                    valid = false;
                }
                if (!password.equals(confirmPassword)) {
                    confirmPass.setError("Passwords don't match!");
                    valid = false;
                }
                if (valid) {
                    mLoginActivity.onFragmentButtonListener(ConstantUtils.REGISTER_BUTTON,
                            userEmail, password);
                }
            }
        });
        return view;
    }
}

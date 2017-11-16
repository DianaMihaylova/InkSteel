package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
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


public class RegisterFragment extends Fragment {

    @BindView(R.id.fragment_register_email_et)
    EditText mEmail;
    @BindView(R.id.fragment_register_password_et)
    EditText mPassword;
    @BindView(R.id.fragment_register_password_confirm_et)
    EditText mConfirmPassword;
    @BindView(R.id.fragment_register_btn)
    Button mRegisterButton;
    private OnRegisterButtonSelectedListener mRegisterButtonSelectedListener;

    public RegisterFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnRegisterButtonSelectedListener) {
            mRegisterButtonSelectedListener = (OnRegisterButtonSelectedListener) context;
        } else {
            Log.e(LoginFragment.class.getSimpleName(), "Activity not implementing " +
                    "OnLoginButtonSelectedListener interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return view;
    }

    private void registerUser() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        if (password.equals(confirmPassword)) {
            mRegisterButtonSelectedListener.onRegisterButtonSelected(email, password);
        } else {
            mConfirmPassword.setError(getString(R.string.pass_not_match));
        }
    }

    public interface OnRegisterButtonSelectedListener {
        void onRegisterButtonSelected(String email, String password);
    }
}
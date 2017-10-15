package com.ink_steel.inksteel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ink_steel.inksteel.R;

public class LoginFragment extends Fragment {

    private EditText email, pass;
    private Button logBtn, regBtn;
    private TextView regTv;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = (EditText) view.findViewById(R.id.login_email);
        pass = (EditText) view.findViewById(R.id.login_pass);
        logBtn = (Button) view.findViewById(R.id.login_login_btn);
        regBtn = (Button) view.findViewById(R.id.login_register_btn);
        regTv = (TextView) view.findViewById(R.id.login_reg_tv);
        return view;
    }

}

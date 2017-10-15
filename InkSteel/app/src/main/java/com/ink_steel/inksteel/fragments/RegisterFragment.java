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

public class RegisterFragment extends Fragment {

    private EditText email, pass, confirmPass;
    private Button regBtn;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        email = (EditText) view.findViewById(R.id.login_email);
        pass = (EditText) view.findViewById(R.id.login_pass);
        confirmPass = (EditText) view.findViewById(R.id.login_conf_pass);
        regBtn = (Button) view.findViewById(R.id.login_login_btn);
        return view;
    }

}

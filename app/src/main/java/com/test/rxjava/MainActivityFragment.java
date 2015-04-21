package com.test.rxjava;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class MainActivityFragment extends Fragment{

    public MainActivityFragment() {
    }

    boolean validEmail;
    boolean validPassword;

    EditText email;
    EditText password;
    Button login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        email = (EditText)v.findViewById(R.id.edit_email1);
        password = (EditText)v.findViewById(R.id.edit_password1);
        login = (Button)v.findViewById(R.id.btn_login1);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                validEmail = !TextUtils.isEmpty(charSequence);
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                Log.e(this.toString(), "After email changed");
                login.setEnabled(validEmail && validPassword);
            }
        });


        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                validPassword = !TextUtils.isEmpty(charSequence);
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                Log.e(this.toString(), "After password changed");
                login.setEnabled(validEmail && validPassword);
            }
        });

        return v;
    }

}

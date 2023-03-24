package com.example.mobilewithaccelerometer.classes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilewithaccelerometer.R;
import com.example.mobilewithaccelerometer.strings.CommunicationString;

import org.json.JSONException;

import java.io.IOException;

public class Register extends PrepareConnection {

    private EditText login;
    private EditText password;
    private EditText passwordConfirm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = (EditText) findViewById(R.id.editTextLogin);
        password = (EditText) findViewById(R.id.editTextTextPassword);
        passwordConfirm = (EditText) findViewById(R.id.editTextTextPasswordConfirm);
    }

    public void register(View view) throws JSONException, IOException {
        String loginString = login.getText().toString();
        String passwordString = password.getText().toString();
        String passwordConfirmString = passwordConfirm.getText().toString();

        if (loginString.isEmpty() || passwordString.isEmpty() || passwordConfirmString.isEmpty()) {
            Toast.makeText(getApplicationContext(), CommunicationString.COMPLETE_YOUR_LOGIN_DETAILS, Toast.LENGTH_LONG).show();
        } else {
            if (passwordString.equals(passwordConfirmString)) {
                createNewAccount(loginString, passwordString);
            } else {
                Toast.makeText(getApplicationContext(), CommunicationString.THE_PASSWORDS_ARE_DIFFERENCE, Toast.LENGTH_LONG).show();
            }
        }
    }
}
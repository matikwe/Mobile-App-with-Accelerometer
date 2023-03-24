package com.example.mobilewithaccelerometer.classes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.mobilewithaccelerometer.R;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends PrepareConnection {

    private EditText login;
    private EditText password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (EditText) findViewById(R.id.editTextTextLogin);
        password = (EditText) findViewById(R.id.editTextPassword);
    }

    public void login(View view) throws JSONException, IOException {
        verifyLoginDetails(login.getText().toString(), password.getText().toString());
    }

    public void register(View view) {
        openActivityWithIntent(Register.class);
    }
}
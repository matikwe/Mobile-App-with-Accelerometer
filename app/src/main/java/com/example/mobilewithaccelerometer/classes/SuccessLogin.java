package com.example.mobilewithaccelerometer.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mobilewithaccelerometer.R;
import com.example.mobilewithaccelerometer.strings.CommunicationString;
import com.example.mobilewithaccelerometer.strings.UtilityStrings;

public class SuccessLogin extends PrepareConnection implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isJumping = false;
    private int jumpCount = 0;
    private TextView textView;
    private String currentUserId;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_login);
        initValue();
    }

    @SuppressLint("SetTextI18n")
    private void initValue() {
        Intent intent = getIntent();
        currentUserId = intent.getStringExtra(UtilityStrings.CURRENT_USER_ID);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        textView = findViewById(R.id.textView);
        textView.setText("User z id: " + currentUserId + "\nSkocz by zacząć!");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float z = sensorEvent.values[2];
        if (z > 9.8 && !isJumping) {
            isJumping = true;
        } else if (z < 0 && isJumping) {
            isJumping = false;
            jumpCount++;
            textView.setText("Jump count: " + jumpCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
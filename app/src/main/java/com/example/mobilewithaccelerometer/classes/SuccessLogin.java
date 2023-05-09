package com.example.mobilewithaccelerometer.classes;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilewithaccelerometer.R;
import com.example.mobilewithaccelerometer.strings.CommunicationString;
import com.example.mobilewithaccelerometer.strings.UtilityStrings;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class SuccessLogin extends PrepareConnection implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isJumping = false;
    private int jumpCount = 0;
    private int jumpCountToSave = 0;
    private int totalJumps;
    private int totalJumpCount;
    private int dailyJumps;
    private int yesterdayMissingJumps;
    private Long currentUserId;
    private long breakTime = 0;
    private TextView textViewCurrentJumps;
    private TextView textViewDaily;
    private TextView textTotalJumps;

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
        String responseString = intent.getStringExtra(UtilityStrings.RESPONSE_WITH_ID_AND_JUMP);
        Map<String, String> response = castStringToMap(responseString);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        textViewCurrentJumps = findViewById(R.id.textView);
        textViewDaily = findViewById(R.id.textViewDaily);
        textTotalJumps = findViewById(R.id.editTextTotalJumps);
        yesterdayMissingJumps = Integer.parseInt(Objects.requireNonNull(response.get(UtilityStrings.YESTERDAY_MISSING_JUMPS)));
        totalJumps = Integer.parseInt(Objects.requireNonNull(response.get(UtilityStrings.TOTAL_JUMPS))) + yesterdayMissingJumps;
        currentUserId = Long.valueOf(Objects.requireNonNull(response.get(UtilityStrings.ID)));
        dailyJumps = addOrGetDailyJumps(0);
        totalJumpCount = getTotalJumpCount(0);
        jumpCount = totalJumpCount;
        jumpCountToSave = jumpCount;
        setViewDaily();
        createNotificationChannel();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float z = sensorEvent.values[2];

        if (z > 9.8 && !isJumping) {
            isJumping = true;
        } else if (z < 0 && isJumping) {
            isJumping = false;
            if (dailyJumps != totalJumps) {
                long currentTime = System.currentTimeMillis();
                if (currentTime >= breakTime) {
                    if (jumpCountToSave < jumpCount) {
                        jumpCountToSave = jumpCount;
                    }
                    jumpCount--;
                    textViewCurrentJumps.setText(jumpCount + " !");
                    if (jumpCount == 0) {
                        totalJumpCount = getTotalJumpCount(1);
                        addOrGetDailyJumps(jumpCountToSave);
                        Toast.makeText(getApplicationContext(), CommunicationString.CONGRATULATIONS, Toast.LENGTH_LONG).show();
                        jumpCount = totalJumpCount;
                        jumpCountToSave = 0;
                        triggerNotification();
                        triggerMediaPlayer();
                    }
                    dailyJumps++;
                    setViewDaily();
                } else {
                    Toast.makeText(getApplicationContext(), CommunicationString.YOU_HAVE_A_BREAK, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), CommunicationString.INCREASE_NUMBER_OF_DAILY_JUMPS, Toast.LENGTH_LONG).show();
            }
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

    @SuppressLint("SetTextI18n")
    public void editTotalJumps(View view) throws IOException {
        String totalJumpsString = textTotalJumps.getText().toString();
        if (totalJumpsString.isEmpty()) {
            Toast.makeText(getApplicationContext(), CommunicationString.EMPTY_TOTAL_JUMPS, Toast.LENGTH_LONG).show();
        } else {
            int totalJumpsInt = Integer.parseInt(totalJumpsString);
            if (totalJumps > 0) {
                if (dailyJumps < totalJumpsInt) {
                    editTotalJumps(currentUserId, totalJumpsInt);
                    totalJumps = totalJumpsInt + yesterdayMissingJumps;
                    totalJumpCount = getTotalJumpCount(0);
                    jumpCount = totalJumpCount;
                    setViewDaily();
                } else {
                    Toast.makeText(getApplicationContext(), CommunicationString.EDIT_TOTAL_JUMPS_ERROR, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), CommunicationString.INCORRECT_TOTAL_JUMPS, Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setViewDaily() {
        textViewDaily.setText(CommunicationString.DAILY_JUMPS + dailyJumps + "/" + totalJumps);
    }

    private int getTotalJumpCount(int delay) {
        int minValue = 5;
        int maxValue = 10;
        int random = Math.abs(minValue + (int) (Math.random() * ((maxValue - minValue) + 1)));
        if (random + dailyJumps >= totalJumps) {
            return totalJumps - dailyJumps - delay;
        }
        return random == 0 ? 1 : random;
    }

    private int addOrGetDailyJumps(int countJumps) {
        try {
            return addJumps(currentUserId, countJumps);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNotificationChannel() {
        CharSequence name = "ReminderChannel";
        String description = "test";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(UtilityStrings.CHANNEL_ID, name, importance);
        notificationChannel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void triggerNotification() {
        Intent intent = new Intent(SuccessLogin.this, ReminderBroadcast.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SuccessLogin.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long timeAlButtonClick = System.currentTimeMillis();
        long OneHoursInMillis = 1000 * 10 /* 60*/;
        breakTime = timeAlButtonClick + OneHoursInMillis;
        alarmManager.set(AlarmManager.RTC_WAKEUP, breakTime, pendingIntent);
    }

    private void triggerMediaPlayer() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.loud_applause);
        mediaPlayer.start();
    }
}
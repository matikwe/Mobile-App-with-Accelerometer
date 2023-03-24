package com.example.mobilewithaccelerometer.classes;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilewithaccelerometer.R;
import com.example.mobilewithaccelerometer.strings.CommunicationString;
import com.example.mobilewithaccelerometer.strings.UtilityStrings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PrepareConnection extends AppCompatActivity {

    private final static String address = "http://192.168.0.176:8080/api/v1/";
    private HttpURLConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    protected void verifyLoginDetails(String login, String password) throws JSONException, IOException {
        URL url = new URL(address + "user/login");
        connection = createConnection(url);
        String json = getJson(login, password);
        sendDataToServer(json);
        if (connection.getResponseCode() == 200) {
            String response = connectedWitStatus200();
            openActivityWithIntent(SuccessLogin.class, UtilityStrings.CURRENT_USER_ID, response);
        } else {
            Toast.makeText(getApplicationContext(), CommunicationString.INCORRECT_LOGIN_DETAILS, Toast.LENGTH_LONG).show();
        }
    }

    void createNewAccount(String login, String password) throws IOException, JSONException {
        URL url = new URL(address + "user/register");
        connection = createConnection(url);
        String json = getJson(login, password);
        sendDataToServer(json);
        if (connection.getResponseCode() == 200) {
            String response = connectedWitStatus200();
            openActivityWithIntent(SuccessLogin.class, UtilityStrings.CURRENT_USER_ID, response);
        } else {
            Toast.makeText(getApplicationContext(), "ERROR TODO", Toast.LENGTH_LONG).show();
        }
    }

    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(UtilityStrings.POST);
        connection.setRequestProperty(UtilityStrings.CONTENT_TYPE, UtilityStrings.APPLICATION_JSON);
        connection.setDoOutput(true);

        return connection;
    }

    private void sendDataToServer(String json) {
        try (OutputStream os = connection.getOutputStream()) {
            os.write(json.getBytes());
            os.flush();
        } catch (IOException e) {
            System.out.println(CommunicationString.FAILED_SEND_DATA + e.getMessage());
        }
    }

    private String getJson(String login, String password) throws JSONException {
        return new JSONObject()
                .put(UtilityStrings.LOGIN, login)
                .put(UtilityStrings.PASSWORD, password)
                .toString();
    }

    private String connectedWitStatus200() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line = "";
        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    }

    protected void openActivityWithIntent(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    protected void openActivityWithIntent(Class<?> cls, String name, String value) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(name, value);
        startActivity(intent);
    }
}

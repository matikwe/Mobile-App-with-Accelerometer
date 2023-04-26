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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class PrepareConnection extends AppCompatActivity implements JsonHelper {

    //private final static String ip = "192.168.0.176";
    private final static String ip = "192.168.219.109";
    private final static String address = "http://" + ip + ":8080/api/v1/";
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
        connection = createConnection(url, UtilityStrings.POST);
        String json = getJsonWithCredential(login, password);
        sendDataToServer(json);
        if (connection.getResponseCode() == 200) {
            String response = connectedWitStatus200();
            openActivityWithIntent(SuccessLogin.class, UtilityStrings.RESPONSE_WITH_ID_AND_JUMP, response);
        } else {
            Toast.makeText(getApplicationContext(), CommunicationString.INCORRECT_LOGIN_DETAILS, Toast.LENGTH_LONG).show();
        }
    }

    protected void createNewAccount(String login, String password, Long totalJumps) throws IOException, JSONException {
        URL url = new URL(address + "user/register");
        connection = createConnection(url, UtilityStrings.POST);
        String json = getJsonWithJump(login, password, totalJumps);
        sendDataToServer(json);
        if (connection.getResponseCode() == 200) {
            String response = connectedWitStatus200();
            openActivityWithIntent(SuccessLogin.class, UtilityStrings.RESPONSE_WITH_ID_AND_JUMP, response);
        } else {
            Toast.makeText(getApplicationContext(), CommunicationString.INCORRECT_PASSWORD, Toast.LENGTH_LONG).show();
        }
    }

    protected int addJumps(Long userId, int countJumps) throws IOException, JSONException {
        URL url = new URL(address + "jump");
        connection = createConnection(url, UtilityStrings.POST);
        String json = getJsonToAddJumps(userId, countJumps);
        sendDataToServer(json);
        String response = connectedWitStatus200();

        return Integer.parseInt(response);
    }

    protected void editTotalJumps(Long userId, int totalJumps) throws IOException {
        URL url = new URL(address + "user/editTotalJumps/" + userId + "?totalJumps=" + totalJumps);
        connection = createConnection(url, UtilityStrings.PUT);
        sendDataToServer("");
        connectedWitStatus200();
    }

    private HttpURLConnection createConnection(URL url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
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


    private String connectedWitStatus200() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line = "";
        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    }

    protected Map<String, String> castStringToMap(String value) {

        return Arrays.stream(value.replace("{", "").replace("}", "").split(","))
                .map(arrayData -> arrayData.split(":"))
                .collect(Collectors.toMap(d -> ((String) d[0]).trim().replace("\"", ""), d -> (String) d[1].trim().replace("\"", "")));
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

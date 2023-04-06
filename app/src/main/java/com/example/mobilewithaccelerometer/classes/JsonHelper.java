package com.example.mobilewithaccelerometer.classes;

import com.example.mobilewithaccelerometer.strings.UtilityStrings;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonHelper {
    default String getJsonWithCredential(String login, String password) throws JSONException {
        return new JSONObject()
                .put(UtilityStrings.LOGIN, login)
                .put(UtilityStrings.PASSWORD, password)
                .toString();
    }

    default String getJsonWithJump(String login, String password, Long totalJumps) throws JSONException {
        return new JSONObject()
                .put(UtilityStrings.LOGIN, login)
                .put(UtilityStrings.PASSWORD, password)
                .put(UtilityStrings.TOTAL_JUMPS, totalJumps)
                .toString();
    }

    default String getJsonToAddJumps(Long userId, int countJumps) throws JSONException {
        return new JSONObject()
                .put(UtilityStrings.COUNT_JUMPS, countJumps)
                .put(UtilityStrings.USER_ID, userId)
                .toString();
    }
}

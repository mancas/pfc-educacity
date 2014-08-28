package com.mancas.utils;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParse {
    public static final String REGISTER_CODE_TAG = "code";
    public static final String REGISTER_ERRORS_TAG = "errors";
    public static final String REGISTER_CHILDREN_TAG = "children";
    public static final String REGISTER_EMAIL_TAG = "email";
    public static final String REGISTER_PASSWORD_TAG = "password";
    public static final String REGISTER_ID_TAG = "id";
    public static final String ACCESS_TOKEN_TAG = "access_token";
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 400;

    /**
     * Checks the response from REST server after a registration request
     * @param register the response from server
     * @return a validation model of Register
     */
    public static RegisterModel checkRegister(String register)
    {
        RegisterModel errors = new RegisterModel();
        try {
            JSONObject json = new JSONObject(register);
            int code = json.getInt(REGISTER_CODE_TAG);
            switch (code) {
            case SUCCESS_CODE:
                errors.setId(json.getInt(REGISTER_ID_TAG));
                break;
            case ERROR_CODE:
                JSONObject node = json.getJSONObject(REGISTER_ERRORS_TAG);
                node = node.getJSONObject(REGISTER_CHILDREN_TAG);
                Log.d("JSON CHILDREN", node.toString());
                String email = node.getString(REGISTER_EMAIL_TAG);
                String password = node.getString(REGISTER_PASSWORD_TAG);
                if (isJSONObject(email)) {
                    //We can assume here that email property has errors
                    errors.setEmail(true);
                }

                if (isJSONObject(password)) {
                    errors.setPassword(true);
                }
                break;
            }
        } catch (JSONException e) {
            Log.d("JSON", e.getMessage());
            errors.setOther(true);
        }
        return errors;
    }

    /**
     * Check if a string is a JSONArray or not
     * @param json string to check if is a JSONArray or not
     * @return true if the json argument is a JSONArray, or false if not
     */
    private static boolean isArray(String json)
    {
        if (json.isEmpty()) {
            return false;
        }

        if (json.startsWith("[") && json.endsWith("]")) {
            return true;
        }

        return false;
    }

    /**
     * Check if a string is a JSONObject or not
     * @param json string to check if is an JSONObject or not
     * @return true if the json argument is a JSONObject, or false if not
     */
    private static boolean isJSONObject(String json)
    {
        if (json.isEmpty()) {
            return false;
        }

        if (json.startsWith("{") && json.endsWith("}")) {
            return true;
        }

        return false;
    }
}

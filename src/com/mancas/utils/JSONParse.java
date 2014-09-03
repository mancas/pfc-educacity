package com.mancas.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mancas.models.LoginModel;
import com.mancas.models.RegisterModel;

public class JSONParse {
    public static final String REGISTER_CODE_TAG = "code";
    public static final String REGISTER_ERRORS_TAG = "errors";
    public static final String REGISTER_CHILDREN_TAG = "children";
    public static final String REGISTER_EMAIL_TAG = "email";
    public static final String REGISTER_PASSWORD_TAG = "password";
    public static final String REGISTER_ID_TAG = "id";
    public static final String ACCESS_TOKEN_TAG = "access_token";
    public static final String REFRESH_TOKEN_TAG = "refresh_token";
    public static final String CLIENT_ID_TAG = "client_id";
    public static final String CLIENT_SECRET_TAG = "client_secret";
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 400;

    /**
     * Checks the response from REST server after a registration request
     * @param register the response from server
     * @return a validation model of Register
     */
    public static RegisterModel checkRegister(String register)
    {
        RegisterModel model = new RegisterModel();
        try {
            JSONObject json = new JSONObject(register);
            int code = json.getInt(REGISTER_CODE_TAG);
            switch (code) {
            case SUCCESS_CODE:
                model.setClientId(json.getString(CLIENT_ID_TAG));
                model.setClientSecret(json.getString(CLIENT_SECRET_TAG));
                break;
            case ERROR_CODE:
                JSONObject node = json.getJSONObject(REGISTER_ERRORS_TAG);
                node = node.getJSONObject(REGISTER_CHILDREN_TAG);
                Log.d("JSON CHILDREN", node.toString());
                String email = node.getString(REGISTER_EMAIL_TAG);
                String password = node.getString(REGISTER_PASSWORD_TAG);
                if (isJSONObject(email)) {
                    //We can assume here that email property has errors
                    model.setEmail(true);
                }

                if (isJSONObject(password)) {
                    model.setPassword(true);
                }
                break;
            }
        } catch (JSONException e) {
            Log.d("JSON", e.getMessage());
            model.setOther(true);
        }
        return model;
    }

    /**
     * Checks if the edition of the profile has gone alright
     * @param response the server response
     * @return true if everything went ok, or false if not
     */
    public static boolean checkEditProfileStatus(String response)
    {
        JSONObject json;
        try {
            json = new JSONObject(response);
            int code = json.getInt(REGISTER_CODE_TAG);
            if (code == 200) {
                return true;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
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

    /**
     * Retrieves from server the access token and the refresh token for this user
     * @param response the response from the server
     * @param model the current register model
     * @return the current register model updated
     */
    public static RegisterModel checkLoginFromRegister(String response,
            RegisterModel model) {
        try {
            JSONObject json = new JSONObject(response);
            String accessToken = json.getString(ACCESS_TOKEN_TAG);
            String refreshToken = json.getString(REFRESH_TOKEN_TAG);
            model.setAccessToken(accessToken);
            model.setRefreshToken(refreshToken);
        } catch (JSONException e) {
            model.setOther(true);
        }

        return model;
    }

    /**
     * Checks if the current login attempt has errors or not
     * @param response the response from the server
     * @param model the Login model associated to this attempt of login
     * @return the current model updated
     */
    public static LoginModel checkLoginErrors(String response, LoginModel model) {
        try {
            JSONObject json = new JSONObject(response);
            String clientID = json.getString(CLIENT_ID_TAG);
            String clientSecret = json.getString(CLIENT_SECRET_TAG);
            model.setClientId(clientID);
            model.setClientSecret(clientSecret);
        } catch (JSONException e) {
            //We can assume here the response has errors
            model.setError(true);
        }

        return model;
    }

    /**
     * Parse the response from the server that contains the access and the refresh tokens
     * @param response the response from the server
     * @param model the Login model associated to this attempt of login
     * @return the current model updated
     */
    public static LoginModel parseLogin(String response, LoginModel model) {
        try {
            JSONObject json = new JSONObject(response);
            String accessToken = json.getString(ACCESS_TOKEN_TAG);
            String refreshToken = json.getString(REFRESH_TOKEN_TAG);
            model.setAccessToken(accessToken);
            model.setRefreshToken(refreshToken);
        } catch (JSONException e) {
            //We can assume here the response has errors
            Log.d("JSON", e.getMessage());
            model.setError(true);
        }

        return model;
    }
}

package com.mancas.models;

/**
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class LoginModel {
    private boolean mError;
    private String mAccessToken;
    private String mRefreshToken;
    private String mClientId;
    private String mClientSecret;
    private int mId;

    public LoginModel() {
        mError = false;
        mId = 1;
    }
    
    /**
     * Gets ID from the new user
     * @return the new user ID
     */
    public int getId()
    {
        return mId;
    }

    /**
     * Sets new user ID
     * @param id new user ID
     */
    public void setId(int id)
    {
        mId = id;
    }

    /**
     * Gets error
     * @return true if there is an error, false if not
     */
    public boolean getError()
    {
        return mError;
    }

    /**
     * Sets error
     * @param other true if there is an error
     */
    public void setError(boolean error)
    {
        mError = error;
    }

    /**
     * Gets the access token
     * @return the access token of the user
     */
    public String getAccessToken()
    {
        return mAccessToken;
    }

    /**
     * Sets the access token
     * @param token the new access token for the new user
     */
    public void setAccessToken(String token)
    {
        mAccessToken = token;
    }

    /**
     * Gets the refresh token
     * @return the refresh token of the user
     */
    public String getRefreshToken()
    {
        return mRefreshToken;
    }

    /**
     * Sets the refresh token
     * @param token the new refresh token for the new user
     */
    public void setRefreshToken(String token)
    {
        mRefreshToken = token;
    }

    /**
     * Gets the client ID
     * @return the client ID to perform the login in the server
     */
    public String getClientId()
    {
        return mClientId;
    }

    /**
     * Gets the client secret
     * @return the client secret to perform the login in the server
     */
    public String getClientSecret()
    {
        return mClientSecret;
    }

    /**
     * Sets the client ID token
     * @param token the client ID token
     */
    public void setClientId(String token)
    {
        mClientId = token;
    }

    /**
     * Sets the client secret token
     * @param token the client secret token
     */
    public void setClientSecret(String token)
    {
        mClientSecret = token;
    }

    /**
     * Returns if the current registration process contains errors
     * @return true if has errors, or false if not
     */
    public boolean hasErrors()
    {
        return mError;
    }
}

package com.mancas.utils;

/**
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class RegisterModel {
    private boolean mEmail;
    private boolean mPassword;
    private int mId;
    private boolean mOther;

    public RegisterModel() {
        mEmail = false;
        mPassword = false;
        mOther = false;
    }

    /**
     * Gets error for email field
     * @return true if the email field has error, or false if not
     */
    public boolean getEmail()
    {
        return mEmail;
    }

    /**
     * Gets error for password field
     * @return true if the password field has error, or false if not
     */
    public boolean getPassword()
    {
        return mPassword;
    }

    /**
     * Sets error for email field
     * @param email email field has error
     */
    public void setEmail(boolean email)
    {
        mEmail = email;
    }

    /**
     * Sets error for password field
     * @param password password field has error
     */
    public void setPassword(boolean password)
    {
        mPassword = password;
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
     * Gets other type error
     * @return true if there is an other type error, false if not
     */
    public boolean getOther()
    {
        return mOther;
    }

    /**
     * Sets other type error
     * @param other true if there is an other type error
     */
    public void setOther(boolean other)
    {
        mOther = other;
    }

    /**
     * Returns if the current registration process contains errors
     * @return true if has errors, or false if not
     */
    public boolean hasErrors()
    {
        return (mEmail || mPassword || mOther);
    }
}

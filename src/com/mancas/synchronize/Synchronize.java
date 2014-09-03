package com.mancas.synchronize;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.sax.StartElementListener;
import android.util.Log;

import com.mancas.database.Account.AccountEntry;
import com.mancas.database.DBHelper;
import com.mancas.database.DBHelper.DBHelperCallback;
import com.mancas.database.Image.ImageEntry;
import com.mancas.educacity.LoginActivity;
import com.mancas.educacity.R;
import com.mancas.models.LoginModel;
import com.mancas.utils.AppUtils;
import com.mancas.utils.HTTPRequestHelper;
import com.mancas.utils.HTTPRequestHelper.HTTPResponseCallback;
import com.mancas.utils.JSONParse;

/**
 * Class to manage user data synchronized with the REST server
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class Synchronize implements HTTPResponseCallback
{
    /**
     * The current instance of this class because this class is a singleton
     */
    private static volatile Synchronize instance = null;
    /**
     * Progress dialog to show the percentage of completion
     */
    private ProgressDialog mProgressDialog;
    /**
     * The context of the application
     */
    private Context mContext;
    /**
     * If there some errors then the user will be inform
     */
    private Boolean mError = false;
    /**
     * Tag for reference the url when uploading a file
     */
    public static final String UPLOAD_IMAGE_URL = "http://rest.educacity-sevilla.com/security/synchronize/file";
    /**
     * Tag for reference the url when updating profile
     */
    public static final String UPLOAD_PROFILE_URL = "http://rest.educacity-sevilla.com/security/synchronize/profile";
    private static final String NAME_KEY = "name";
    private static final String PUBLIC_KEY = "public_profile";
    private static String mCurrentAccessToken;
    private static String mCurrentRefreshToken;
    
    private boolean mResponseProfile = false;
    private DBHelperCallback mPreviousCallback;

    /**
     * Gets the current instance of this class if exists
     * @param context the context of the application
     * @return the current instance of {@link Synchronize}
     */
    public static synchronized Synchronize getInstance(Context context)
    {
        if (instance == null) {
            instance = new Synchronize(context);
        } else {
            instance.setContext(context);
            instance.configureDialog();
        }

        return instance;
    }

    private Synchronize(Context context)
    {
        mProgressDialog = new ProgressDialog(context);
        configureDialog();
        mContext = context;
    }

    public void configureDialog() {
        mProgressDialog.setTitle(R.string.synchronize);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(0);
    }

    public void setContext(Context context)
    {
        mContext = context;
    }

    public void synchronizeData()
    {
        new GetUserDataAsync().execute();
    }
    
    public class GetUserDataAsync extends AsyncTask<Void, Void, Void> implements DBHelperCallback, HTTPResponseCallback
    {
        private DBHelper mHelper;
        private LoginModel mModel = new LoginModel();
        @Override
        protected Void doInBackground(Void... params) {
            mHelper = DBHelper.getInstance(mContext, this);
            String select = AccountEntry._ID + "=?";
            String[] args = {String.valueOf(AppUtils.getAccountID(mContext))};
            Cursor profile = mHelper.select(AccountEntry.TABLE_NAME_WITH_PREFIX,
                    AccountEntry.TABLE_PROJECTION, select, args, null, null, null);

            if (profile != null && profile.getCount() > 0) {
                profile.moveToFirst();
                mCurrentAccessToken = profile.getString(profile.getColumnIndex(AccountEntry.COLUMN_ACCESS_TOKEN));
                mCurrentRefreshToken = profile.getString(profile.getColumnIndex(AccountEntry.COLUMN_REFRESH_TOKEN));
                mModel.setAccessToken(mCurrentAccessToken);
                mModel.setRefreshToken(mCurrentRefreshToken);
                mModel.setClientId(profile.getString(profile.getColumnIndex(AccountEntry.COLUMN_CLIENT_ID)));
                mModel.setClientSecret(profile.getString(profile.getColumnIndex(AccountEntry.COLUMN_CLIENT_SECRET)));
            }
            HTTPRequestHelper helper = new HTTPRequestHelper(null, this);
            JSONObject extras = new JSONObject();
            try {
                extras.put("refresh_token", mModel.getRefreshToken());
                extras.put(LoginActivity.CLIENT_ID, mModel.getClientId());
                extras.put(LoginActivity.CLIENT_SECRET, mModel.getClientSecret());
                extras.put(LoginActivity.GRANT_TYPE, "refresh_token");
            } catch (JSONException e) {
                this.cancel(true);
                return null;
            }
            helper.setParams(extras);
            helper.performPost(LoginActivity.TOKEN_URL);
            if (!mModel.hasErrors()) {
                mCurrentAccessToken = mModel.getAccessToken();
                mCurrentRefreshToken = mModel.getRefreshToken();
                //we need to update db
                ContentValues values = new ContentValues();
                values.put(AccountEntry.COLUMN_ACCESS_TOKEN, mModel.getAccessToken());
                values.put(AccountEntry.COLUMN_REFRESH_TOKEN, mModel.getRefreshToken());
                mHelper.update(AccountEntry.TABLE_NAME_WITH_PREFIX, values, select, args);
            } else {
                this.cancel(true);
            }
            return null;
        }
        
        @Override
        protected void onCancelled()
        {
            performLogin();
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            processData();
        }
        
        @Override
        public void onDatabaseOpen(SQLiteDatabase database) {
        }
        @Override
        public void onSelectReady(Cursor data) {
        }
        @Override
        public void onInsertReady(long id) {
        }
        @Override
        public void onUpdateReady(int rows) {
        }

        @Override
        public void onResponseReady(String response) {
            if (!response.isEmpty()) {
                mModel = JSONParse.parseLogin(response, mModel);
            }
        }
    }

    private boolean uploadImage(String path)
    {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(path); 
        if (!sourceFile.isFile()) {
            return false;
        }
        try { // open a URL connection to the Servlet
             FileInputStream fileInputStream = new FileInputStream(sourceFile);
             URL url = new URL(UPLOAD_IMAGE_URL + "?access_token=" + mCurrentAccessToken);
             conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
             conn.setDoInput(true); // Allow Inputs
             conn.setDoOutput(true); // Allow Outputs
             conn.setUseCaches(false); // Don't use a Cached Copy
             conn.setRequestMethod("POST");
             Log.d("SYNC", mCurrentAccessToken);
             conn.setRequestProperty("Connection", "Keep-Alive");
             conn.setRequestProperty("ENCTYPE", "multipart/form-data");
             conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
             conn.setRequestProperty("uploaded_file", path); 
             dos = new DataOutputStream(conn.getOutputStream());

             dos.writeBytes(twoHyphens + boundary + lineEnd); 
             dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ path + "\"" + lineEnd);
             dos.writeBytes(lineEnd);

             bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

             bufferSize = Math.min(bytesAvailable, maxBufferSize);
             buffer = new byte[bufferSize];

             // read file and write it into form...
             bytesRead = fileInputStream.read(buffer, 0, bufferSize);
               
             while (bytesRead > 0) {
               dos.write(buffer, 0, bufferSize);
               bytesAvailable = fileInputStream.available();
               bufferSize = Math.min(bytesAvailable, maxBufferSize);
               bytesRead = fileInputStream.read(buffer, 0, bufferSize);
              }

             // send multipart form data necesssary after file data...
             dos.writeBytes(lineEnd);
             dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

             // Responses from the server (code and message)
             int serverResponseCode = conn.getResponseCode();
             String serverResponseMessage = conn.getResponseMessage();
             Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
             //close the streams //
             fileInputStream.close();
             dos.flush();
             dos.close();
             if(serverResponseCode == 200){
                 return true;
             }
        } catch (MalformedURLException ex) {  
            Log.d("uploadFIle", ex.getMessage());
            return false;
        } catch (Exception e) {
            Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            return false;
        }

        return false;
    }

    public void performLogin() {
        Intent i = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(i);
    }

    public void processData() {
        new UploadNotSync().execute();
    }

    /**
     * Updates the profile data in the server
     * @param name the new name for the user
     * @param profilePublic specific if the user has public profile or not
     * @return true if everything went ok, or false if not
     */
    private boolean uploadProfile(String name, int profilePublic)
    {
        JSONObject httpParams = new JSONObject();
        try {
            httpParams.put("access_token", mCurrentAccessToken);
            httpParams.put(NAME_KEY, name);
            httpParams.put(PUBLIC_KEY, profilePublic);
        } catch (JSONException e) {
            Log.d("uploadProfile", e.getMessage());
            return false;
        }
        HTTPRequestHelper helper = new HTTPRequestHelper(httpParams, this);
        helper.performPost(UPLOAD_PROFILE_URL + "?access_token=" + mCurrentAccessToken);

        return mResponseProfile;
    }

    public class UploadNotSync extends AsyncTask<Void, Void, Void>
        implements DBHelperCallback {
        private DBHelper mDBHelper;
        private Map<Integer, String> mImages = new HashMap<Integer, String>();

        @Override
        protected void onPreExecute()
        {
            mProgressDialog.show();
            mError = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("SYNC", "here");
            mPreviousCallback = DBHelper.getCallback();
            mDBHelper = DBHelper.getInstance(mContext, this);
            String select = ImageEntry.COLUMN_SYNC + "=?";
            String[] args = {String.valueOf(0)};
            Cursor data = mDBHelper.select(ImageEntry.TABLE_NAME_WITH_PREFIX,
                    ImageEntry.TABLE_PROJECTION, select, args, null, null, null);

            if (data != null) {
                while (data.moveToNext()) {
                    String path = data.getString(data.getColumnIndex(ImageEntry.COLUMN_PATH));
                    Integer id = data.getInt(data.getColumnIndex(ImageEntry._ID));
                    mImages.put(id, path);
                }
                mProgressDialog.setMax(mImages.size());
                upload();
            }
            mProgressDialog.setProgress(0);
            mProgressDialog.setMax(1);

            select = AccountEntry.COLUMN_SYNC + "=?";

            Cursor profile = mDBHelper.select(AccountEntry.TABLE_NAME_WITH_PREFIX,
                    AccountEntry.TABLE_PROJECTION, select, args, null, null, null);
            if (profile != null && profile.getCount() > 0) {
                profile.moveToFirst();
                Integer id = profile.getInt(profile.getColumnIndex(AccountEntry._ID));
                String name = profile.getString(profile.getColumnIndex(AccountEntry.COLUMN_NAME));
                int profilePublic = profile.getInt(profile.getColumnIndex(AccountEntry.COLUMN_PUBLIC));
                boolean response = uploadProfile(name, profilePublic);
                if (response) {
                    ContentValues values = new ContentValues();
                    values.put(AccountEntry.COLUMN_SYNC, true);
                    String[] whereArgs = {String.valueOf(id)};

                    mDBHelper.update(AccountEntry.TABLE_NAME_WITH_PREFIX, values, AccountEntry._ID + "=?", whereArgs);
                    mProgressDialog.setProgress(1);
                } else {
                    mError = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            mProgressDialog.dismiss();
            showErrorIfNeeded();
            mDBHelper.restoreCallback(mPreviousCallback);
        }

        @Override
        public void onDatabaseOpen(SQLiteDatabase database) {
        }

        @Override
        public void onSelectReady(Cursor data) {
        }

        @Override
        public void onInsertReady(long id) {
        }

        @Override
        public void onUpdateReady(int rows) {
        }

        private void upload()
        {
            for (Map.Entry<Integer, String> entry: mImages.entrySet()) {
                Integer id = entry.getKey();
                String path = entry.getValue();
                boolean response = uploadImage(path);
                if (response) {
                    ContentValues values = new ContentValues();
                    values.put(ImageEntry.COLUMN_SYNC, true);
                    String[] args = {String.valueOf(id)};
                    mDBHelper.update(ImageEntry.TABLE_NAME_WITH_PREFIX, values, ImageEntry._ID + "=?", args);
                } else {
                    mError = true;
                }
                mProgressDialog.setProgress(mProgressDialog.getProgress() + 1);
            }
        }
    }

    public void showErrorIfNeeded() {
        if (mError) {
            AppUtils.showToast(mContext, mContext.getResources().getString(R.string.error_synchronize));
        } else {
            AppUtils.showToast(mContext, mContext.getResources().getString(R.string.success_synchronized));
        }
    }

    @Override
    public void onResponseReady(String response) {
        Log.d("RESPONSE", response);
        mResponseProfile = JSONParse.checkEditProfileStatus(response);
    }
}

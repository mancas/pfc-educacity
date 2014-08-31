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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.mancas.database.Account.AccountEntry;
import com.mancas.database.DBHelper;
import com.mancas.database.DBHelper.DBHelperCallback;
import com.mancas.database.Image.ImageEntry;
import com.mancas.educacity.R;
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
    public static final String UPLOAD_IMAGE_URL = "";
    /**
     * Tag for reference the url when updating profile
     */
    public static final String UPLOAD_PROFILE_URL = "";
    private static final String NAME_KEY = "name";
    
    private boolean mResponseProfile = false;

    /**
     * Gets the current instance of this class if exists
     * @param context the context of the application
     * @return the current instance of {@link Synchronize}
     */
    public static synchronized Synchronize getInstance(Context context)
    {
        if (instance == null) {
            instance = new Synchronize(context);
        }

        return instance;
    }

    private Synchronize(Context context)
    {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle(R.string.synchronize);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(0);
        mContext = context;
    }

    public void synchronizeData()
    {
        new UploadNotSync().execute();
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
             URL url = new URL(UPLOAD_IMAGE_URL);
             conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
             conn.setDoInput(true); // Allow Inputs
             conn.setDoOutput(true); // Allow Outputs
             conn.setUseCaches(false); // Don't use a Cached Copy
             conn.setRequestMethod("POST");
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

    private boolean uploadProfile(String name)
    {
        JSONObject httpParams = new JSONObject();
        try {
            httpParams.put(NAME_KEY, name);
        } catch (JSONException e) {
            Log.d("uploadProfile", e.getMessage());
            return false;
        }
        HTTPRequestHelper helper = new HTTPRequestHelper(httpParams, this);
        helper.performPost(UPLOAD_PROFILE_URL);

        return mResponseProfile;
    }

    public class UploadNotSync extends AsyncTask<Void, Void, Void>
        implements DBHelperCallback {
        private DBHelper mDBHelper;
        private Map<Integer, String> mImages = new HashMap<Integer, String>();

        @Override
        protected void onPreExecute()
        {
            mError = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mDBHelper = DBHelper.getInstance(mContext, this);
            String select = ImageEntry.COLUMN_SYNC + "=?";
            String[] args = {String.valueOf(false)};
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
            if (profile != null) {
                profile.moveToFirst();
                Integer id = profile.getInt(profile.getColumnIndex(AccountEntry._ID));
                String name = profile.getString(profile.getColumnIndex(AccountEntry.COLUMN_NAME));
                boolean response = uploadProfile(name);
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
        }
    }

    @Override
    public void onResponseReady(String response) {
        mResponseProfile = JSONParse.checkEditProfileStatus(response);
    }
}

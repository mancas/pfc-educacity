package com.mancas.educacity;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.mancas.database.Account.AccountEntry;
import com.mancas.database.DBHelper;
import com.mancas.database.DBHelper.DBHelperCallback;
import com.mancas.database.DBHelper.DBOpenHelper;
import com.mancas.dialogs.NoNetworkDialog;
import com.mancas.models.LoginModel;
import com.mancas.models.RegisterModel;
import com.mancas.utils.AppUtils;
import com.mancas.utils.HTTPRequestHelper;
import com.mancas.utils.JSONParse;
import com.mancas.utils.HTTPRequestHelper.HTTPResponseCallback;
import com.mancas.utils.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class LoginActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    /**
     * Value for email at the time of login attempt
     */
    private String mEmail;
    /**
     * Value for password at the time of login attempt
     */
    private String mPassword;

    // UI references.
    /**
     * Email view where the user write his email
     */
    private EditText mEmailView;
    /**
     * Password view where the user write his password
     */
    private EditText mPasswordView;
    /**
     * Container of the login form
     */
    private View mLoginFormView;
    /**
     * View to show the login progress
     */
    private View mLoginStatusView;
    /**
     * Text view to inform the user about the login progress
     */
    private TextView mLoginStatusMessageView;
    /**
     * Tag for handle a login attempt when the user has registered
     */
    public static final int REGISTER_REQUEST = 1;
    /**
     * Tag for handle fragment load when the user has logged into the system
     */
    public static final int LOGIN_REQUEST = 2;
    /**
     * URL used to register users
     */
    public static final String TOKEN_URL = "http://rest.educacity-sevilla.com/oauth/v2/token";
    /**
     * URL used to login users
     */
    public static final String LOGIN_URL = "http://rest.educacity-sevilla.com/oauth/v2/login_app";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String GRANT_TYPE = "grant_type";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        setupActionBar();

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id,
                            KeyEvent keyEvent) {
                        if (id == R.id.login || id == EditorInfo.IME_NULL) {
                            attemptLogin();
                            return true;
                        }
                        return false;
                    }
                });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin();
                    }
                });
        findViewById(R.id.new_account_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivityForResult(register, REGISTER_REQUEST);
                    }
                });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (getCurrentFocus() != null) {
            InputMethodManager inputManager = 
                    (InputMethodManager) getApplicationContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE); 
            inputManager.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 6) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Utils.checkEmailAddress(mEmail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            AppUtils.showProgress(getApplicationContext(), mLoginStatusView, mLoginFormView, true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("LOGIN", requestCode + "");
        if (requestCode == REGISTER_REQUEST) {
            Bundle extras = data.getExtras();
            if (!extras.getString(JSONParse.ACCESS_TOKEN_TAG).isEmpty()) {
                setResult(LoginActivity.LOGIN_REQUEST, null);
                finish();
            }
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> implements HTTPResponseCallback {
        private boolean isCheckingLogin = true;
        private LoginModel mModel = new LoginModel();
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean connection = AppUtils.checkNetworkConnection(getApplicationContext());
            if (!connection) {
                NoNetworkDialog dialog = new NoNetworkDialog();
                dialog.show(getFragmentManager(), "No Network");
                this.cancel(true);
                return null;
            }
            HTTPRequestHelper httpHelper = new HTTPRequestHelper(null, this);
            JSONObject loginParams = new JSONObject();
            try {
                loginParams.put(LoginActivity.USERNAME, mEmail);
                loginParams.put(LoginActivity.PASSWORD, mPassword);
            } catch (JSONException e) {
                return null;
            }
            
            httpHelper.setParams(loginParams);
            httpHelper.performPost(LOGIN_URL);
            if (mModel.hasErrors()) {
                this.cancel(true);
            }
            isCheckingLogin = false;
            try {
                loginParams.put(LoginActivity.USERNAME, mEmail);
                loginParams.put(LoginActivity.PASSWORD, mPassword);
                loginParams.put(LoginActivity.CLIENT_ID, mModel.getClientId());
                loginParams.put(LoginActivity.CLIENT_SECRET, mModel.getClientSecret());
                loginParams.put(LoginActivity.GRANT_TYPE, "password");
            } catch (JSONException e) {
                return null;
            }
            httpHelper.setParams(loginParams);
            httpHelper.performPost(TOKEN_URL);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                displayErrorsIfNeeded(mModel);
            } else {
                displayServerError();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            AppUtils.showProgress(getApplicationContext(), mLoginStatusView, mLoginFormView, false);
            displayServerError();
        }

        @Override
        public void onResponseReady(String response) {
            if (!response.isEmpty()) {
                if (isCheckingLogin) {
                    mModel = JSONParse.checkLoginErrors(response, mModel);
                } else {
                    mModel = JSONParse.parseLogin(response, mModel);
                }
            }
        }
    }

    public void displayServerError() {
        AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.error_network_response));
    }

    public void displayErrorsIfNeeded(LoginModel model) {
        if (!model.hasErrors()) {
            new UserLoginInAppTask().execute(model);
        } else {
            AppUtils.showProgress(getApplicationContext(), mLoginStatusView, mLoginFormView, false);
            mPasswordView
                .setError(getString(R.string.rest_login_fail));
            mPasswordView.requestFocus();
        }
    }

    /**
     * Represents an asynchronous task used to register
     * a new the user.
     */
    public class UserLoginInAppTask extends AsyncTask<LoginModel, Void, Boolean> implements DBHelperCallback {
        private DBHelper helper;
        private LoginModel mModel;
        @Override
        protected Boolean doInBackground(LoginModel... params) {
            mModel = params[0];
            helper = DBHelper.getInstance(getApplicationContext(), this);
            DBOpenHelper opener = helper.getDBOpenHelper();
            helper.setDataBase(opener.getWritableDatabase());
            if (AppUtils.getAccountID(getApplicationContext()) != -1) {
                //The account exists, we need to refresh tokens
                ContentValues values = new ContentValues();
                values.put(AccountEntry.COLUMN_ACCESS_TOKEN, mModel.getAccessToken());
                values.put(AccountEntry.COLUMN_REFRESH_TOKEN, mModel.getRefreshToken());
                String[] args = {String.valueOf(AppUtils.getAccountID(getApplicationContext()))};
                helper.update(AccountEntry.TABLE_NAME_WITH_PREFIX, values, AccountEntry.DEFAULT_TABLE_SELECTION, args);
                return true;
            }
            try {
                long image_id = helper.createNewAccount(-1, mEmail, "", "",
                        mModel.getAccessToken(), mModel.getRefreshToken(), mModel.getClientId(), mModel.getClientSecret());
                if (image_id == -1) {
                    this.cancel(true);
                    return false;
                }
                String[] args = {String.valueOf(image_id)};
                Cursor account = helper.select(AccountEntry.TABLE_NAME_WITH_PREFIX, AccountEntry.TABLE_PROJECTION,
                        AccountEntry.COLUMN_IMAGE + "=?", args, null, null, null);
                if (account != null && account.getCount() > 0) {
                    account.moveToFirst();
                    mModel.setId(account.getInt(account.getColumnIndex(AccountEntry._ID)));
                }
                AppUtils.setAccountID(getApplicationContext(), mModel.getId());
                AppUtils.setAccountImageID(getApplicationContext(), image_id);
            } catch (InterruptedException e) {
                this.cancel(false);
                return false;
            } catch (ExecutionException e) {
                this.cancel(false);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Intent intent = new Intent();
                Bundle extras = new Bundle();
                extras.putString(JSONParse.ACCESS_TOKEN_TAG, mModel.getAccessToken());
                intent.putExtras(extras);
                setResult(LoginActivity.LOGIN_REQUEST, intent);
                finish();
            } else {
                this.cancel(true);
            }
        }

        @Override
        protected void onCancelled() {
            AppUtils.showProgress(getApplicationContext(), mLoginStatusView, mLoginFormView, false);
            AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.db_insert_error));
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
    }
}

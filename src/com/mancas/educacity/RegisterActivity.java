package com.mancas.educacity;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mancas.database.DBHelper;
import com.mancas.database.DBHelper.DBHelperCallback;
import com.mancas.database.DBHelper.DBOpenHelper;
import com.mancas.utils.AppUtils;
import com.mancas.utils.Utils;

public class RegisterActivity extends Activity
{
    /**
     * Value for email at the time of register attempt
     */
    private String mEmail;
    /**
     * Value for password at the time of register attempt
     */
    private String mPassword;
    /**
     * Value for confirm password at the time of register attempt
     */
    private String mPasswordConfirm;

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
     * Password view where the user write his password confirmation
     */
    private EditText mPasswordConfirmView;
    /**
     * Container of the register form
     */
    private View mRegisterFormView;
    /**
     * View to show the register progress
     */
    private View mRegisterStatusView;
    /**
     * Text view to inform the user about the register progress
     */
    private TextView mRegisterStatusMessageView;
    /**
     * Keep track of the register task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mRegisterTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //setupActionBar();

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm);
        mPasswordConfirmView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id,
                            KeyEvent keyEvent) {
                        if (id == R.id.register || id == EditorInfo.IME_NULL) {
                            attemptRegister();
                            return true;
                        }
                        return false;
                    }
                });

        mRegisterFormView = findViewById(R.id.register_form);
        mRegisterStatusView = findViewById(R.id.register_status);
        mRegisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);

        findViewById(R.id.new_account_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attemptRegister();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
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

    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual register attempt is made.
     */
    public void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        // Store values at the time of the register attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        mPasswordConfirm = mPasswordConfirmView.getText().toString();

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

        if (TextUtils.isEmpty(mPasswordConfirm)) {
            mPasswordConfirmView.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirmView;
            cancel = true;
        } else if (mPasswordConfirm.length() < 6) {
            mPasswordConfirmView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordConfirmView;
            cancel = true;
        } else if (!TextUtils.equals(mPassword, mPasswordConfirm)) {
            mPasswordConfirmView.setError(getString(R.string.error_not_equals_passwords));
            focusView = mPasswordConfirmView;
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
            mRegisterStatusMessageView.setText(R.string.register_progress_signing_in);
            hideKeyboardIfNeeded();
            AppUtils.showProgress(getApplicationContext(), mRegisterStatusView, mRegisterFormView, true);
            mRegisterTask = new UserRegisterTask();
            mRegisterTask.execute((Void) null);
        }
    }

    /**
     * Hide the keyboard if needed, before show the progress screen
     */
    private void hideKeyboardIfNeeded() {
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
     * Represents an asynchronous task used to register
     * a new the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> implements DBHelperCallback {
        private DBHelper helper;
        @Override
        protected Boolean doInBackground(Void... params) {
            //We must open the db here because of the establishDB() method
            //is another async task and doesn't work
            helper = DBHelper.getInstance(getApplicationContext(), this);
            DBOpenHelper opener = helper.getDBOpenHelper();
            helper.setDataBase(opener.getWritableDatabase());
            try {
                long id = helper.createNewAccount(mEmail, "", "");
                AppUtils.setAccountID(getApplicationContext(), id);
                long image_id = helper.getProfileImageId();
                AppUtils.setAccountImageID(getApplicationContext(), image_id);
            } catch (InterruptedException e) {
                return false;
            } catch (ExecutionException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
            AppUtils.showProgress(getApplicationContext(), mRegisterStatusView, mRegisterFormView, false);

            if (success) {
                setResult(LoginActivity.REGISTER_REQUEST, null);
                finish();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.db_insert_error, Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            AppUtils.showProgress(getApplicationContext(), mRegisterStatusView, mRegisterFormView, false);
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

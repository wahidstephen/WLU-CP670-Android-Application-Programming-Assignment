package com.example.androidassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    /** Called when the user touches the button */
    public void login(View view) {
        // Do something in response to button click
        saveUserData();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    protected static final String ACTIVITY_NAME = "LoginActivity";

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(ACTIVITY_NAME, "In onCreate()");
        setContentView(R.layout.activity_login);
        loadUserData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void loadUserData() {
        String preference_file_name = getString(R.string.preference_name);
        SharedPreferences mPrefs = getSharedPreferences(
                preference_file_name, MODE_PRIVATE);

        String email_key = getString(R.string.preference_key_profile_email);
        String new_email_value = mPrefs.getString(email_key, "wahi8260@mylaurier.ca");

        ((EditText) findViewById(R.id.editTextTextEmailAddress)).setText(new_email_value);
    }

    private void saveUserData() {
        String preference_file_name = getString(R.string.preference_name);
        SharedPreferences mPrefs = getSharedPreferences(preference_file_name, MODE_PRIVATE);

        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.clear();

        // Save email information
        String email_key  = getString(R.string.preference_key_profile_email);
        String new_email_entered = (String) ((EditText) findViewById(R.id.editTextTextEmailAddress))
                .getText().toString();
        mEditor.putString(email_key, new_email_entered);

        // Commit all the changes into the shared preference
        mEditor.commit();
    }
}
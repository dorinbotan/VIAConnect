package com.example.dorin.viaconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.example.dorin.viaconnect.Utils.StringParser;
import com.example.dorin.viaconnect.WebClient.WebClient;

import java.io.File;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;

    private EditText loginEditText;
    private EditText passwordEditText;

    private WebClient webClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO remove me
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        setup();

        handleIntent(getIntent());
    }

    // Setup the layout
    private void setup() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);

        loginEditText = (EditText) findViewById(R.id.loginEditText);
        loginEditText.setRawInputType(Configuration.KEYBOARDHIDDEN_YES);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                    try {
                        connectButtonPressed(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                return false;
            }
        });

        if (isOnline())
            webClient = (WebClient) getApplicationContext();
    }

    // TODO: handle login properly
    // Handle a file being "shared" intent by sending it to the print server
    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_SEND.equals(action)) {
            Uri fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            fileUri = Uri.parse(StringParser.getRealPathFromUri(getApplicationContext(), fileUri));
            File file = new File(fileUri.getEncodedPath());
            String filePath = file.getAbsolutePath();
            String fileName = filePath.substring(filePath.lastIndexOf('/'));

            if (webClient.isLoggedIn()) {
                mProgressDialog.show();
                webClient.sendPrintJob(fileName, intent.getType(), new File(fileUri.getEncodedPath()), this);
            }
        }
    }

    // Check input and move to next activity
    public void connectButtonPressed(View view) {
        if (validateInput() && isOnline()) {
            mProgressDialog.show();
            webClient.logIn(loginEditText.getText().toString(), passwordEditText.getText().toString(), this);
        }
    }

    public void startPrintActivity() {
        startActivity(new Intent(this, PrintActivity.class));
    }

    public void showError() {
        passwordEditText.setError("Wrong input");
        passwordEditText.requestFocus();

        mProgressDialog.hide();
    }

    // Check internet connection
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean validateInput() {
        boolean toReturn = true;

        if (!validateLogin()) {
            loginEditText.setError("Wrong input");
            loginEditText.requestFocus();
            toReturn = false;
        }

        if (!validatePassword()) {
            passwordEditText.setError("Wrong input");
            passwordEditText.requestFocus();
            toReturn = false;
        }

        return toReturn;
    }

    private boolean validateLogin() {
        return loginEditText.getText().length() == 6;
    }

    private boolean validatePassword() {
        return passwordEditText.getText().length() > 3;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mProgressDialog.cancel();
    }
}
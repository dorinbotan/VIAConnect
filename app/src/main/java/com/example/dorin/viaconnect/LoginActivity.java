package com.example.dorin.viaconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dorin.viaconnect.WebClient.WebClient;

import java.io.File;
import java.io.IOException;

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
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        setup();
        webClient.initiate();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null)
            if (type.startsWith("image/"))
                try {
                    handleSendImage(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                return false;
            }
        });

        webClient = (WebClient) getApplicationContext();
    }

    private void handleSendImage(Intent intent) throws IOException {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (imageUri != null) {
            imageUri = Uri.parse(getRealPathFromUri(this, imageUri));
            File imageFile = new File(imageUri.getEncodedPath());
            String imagePath = imageFile.getAbsolutePath();
            String imageName = imagePath.substring(imagePath.lastIndexOf('/'));

            if (webClient.print.isLoggedIn()) {
                webClient.print.sendJob(imageName, "image/*", new File(imageUri.getEncodedPath()));
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                ImageView image = (ImageView) findViewById(R.id.logoImage);
                image.setImageBitmap(bitmap);
            }
        }
    }

    private static String getRealPathFromUri(Activity activity, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void connectButtonPressed(View view) throws IOException {
        hideKeyboard();

        if (checkInput()) {
            mProgressDialog.show();

            new Thread(new Runnable() {
                public void run() {
                    boolean result = webClient.logIn(loginEditText.getText().toString(), passwordEditText.getText().toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.hide();
                        }
                    });

                    if (result)
                        switchActivity();
                    else
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showError();
                            }
                        });
                }
            }).start();
        }
    }

    public void switchActivity() {
        Intent intent = new Intent(this, PrintActivity.class);
        startActivity(intent);
    }

    public void showError() {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG);
    }

    private boolean checkInput() {
        return checkLogin() && checkPassword();
    }

    private boolean checkLogin() {
        return loginEditText.getText().length() == 6;
    }

    private boolean checkPassword() {
        return passwordEditText.getText().length() > 3;
    }

    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mProgressDialog.cancel();
    }
}
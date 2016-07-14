package com.example.dorin.viaconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void connectButtonPressed(View view) {
        TextView signature = (TextView) findViewById(R.id.signatureTextView);
        EditText login = (EditText) findViewById(R.id.loginEditText);
        EditText password = (EditText) findViewById(R.id.passwordEditText);

        shade();
    }

    private void shade() {
        hideSoftKeyboard();

        ImageView shade = (ImageView) findViewById(R.id.shadeImageView);
        ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
        shade.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }
}

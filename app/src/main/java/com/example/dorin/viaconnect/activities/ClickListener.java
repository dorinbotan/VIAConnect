package com.example.dorin.viaconnect.activities;

import android.view.View;

import java.io.IOException;

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position) throws IOException;
}
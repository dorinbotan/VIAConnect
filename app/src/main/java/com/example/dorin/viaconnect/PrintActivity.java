package com.example.dorin.viaconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.dorin.viaconnect.WebClient.MediaType;

public class PrintActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
        });
    }

    public void performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        // Use ACTION_OPEN_DOCUMENT for persistent access
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");
        String[] mimetypes = MediaType.getAllTypes();
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        startActivityForResult(intent, READ_REQUEST_CODE);
    }
}
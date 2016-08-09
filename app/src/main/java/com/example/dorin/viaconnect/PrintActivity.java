package com.example.dorin.viaconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.dorin.viaconnect.WebClient.Print.MediaType;
import com.example.dorin.viaconnect.WebClient.Print.PrintJob;
import com.example.dorin.viaconnect.WebClient.WebClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PrintActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;

    private ShakeListener mShakeListener;

    private ListView printJobList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> names;
    private FloatingActionButton fab;

    private WebClient webClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setup();

        webClient.getPrintJobs(this);
    }

    private void setup() {
        names = new ArrayList<>();

        printJobList = (ListView) findViewById(R.id.printJobListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        printJobList.setAdapter(adapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
        });

        webClient = (WebClient) getApplicationContext();

        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                vibe.vibrate(100);
                try {
                    buttonClicked(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().

            if (intent != null) {
                Uri fileUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                fileUri = Uri.parse(StringParser.getRealPathFromUri(getApplicationContext(), fileUri));
                File file = new File(fileUri.getEncodedPath());
                String filePath = file.getAbsolutePath();
                String fileName = filePath.substring(filePath.lastIndexOf('/'));

                if (webClient.isLoggedIn())
                    webClient.sendPrintJob(fileName, intent.getType(), new File(fileUri.getEncodedPath()));
            }
        }
    }

    public void buttonClicked(View view) throws IOException {
        webClient.getPrintJobs(this);

        Button button = (Button) findViewById(R.id.button);
    }

    public void updateListView(ArrayList<PrintJob> printJobs) {
        names.clear();

        if (printJobs.size() != 0)
            for (PrintJob p : printJobs)
                names.add(p.name + "\n" + p.dateTime);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        mShakeListener.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mShakeListener.pause();
        super.onPause();
    }
}
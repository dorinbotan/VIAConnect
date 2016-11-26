package com.example.dorin.viaconnect;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.dorin.viaconnect.utils.StringParser;
import com.example.dorin.viaconnect.webClient.print.MediaType;
import com.example.dorin.viaconnect.webClient.print.PrintJob;
import com.example.dorin.viaconnect.webClient.WebClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PrintActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;

    private ArrayList<PrintJob> printJobs = new ArrayList<>();
    private PrintListViewAdapter adapter;

    private WebClient webClient;

    private SwipeRefreshLayout swipeContainer;
    private FloatingActionButton fab;

    private PrintActivity v = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        setup();

        webClient.getPrintJobs(this);
    }

    // Setup the layout
    private void setup() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO ugly here
                // TODO make async
                webClient.getPrintJobs(v);
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccent1,
                R.color.colorAccent2,
                R.color.colorAccent3,
                R.color.colorAccent4,
                R.color.colorAccent5,
                R.color.colorAccent6);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
        });

        webClient = (WebClient) getApplicationContext();

        adapter = new PrintListViewAdapter(this, R.layout.printjob_layout, printJobs);
        ListView listView = (ListView) findViewById(R.id.printJobListView);
        listView.setAdapter(adapter);
    }

    // Upload file to printing server
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case READ_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri fileUri = intent.getData();

                    ContentResolver contentResolver = getApplicationContext().getContentResolver();
                    String fileType = contentResolver.getType(fileUri);

                    fileUri = Uri.parse(StringParser.getRealPathFromUri(getApplicationContext(), fileUri));

                    if (webClient.isLoggedIn())
                        webClient.sendPrintJob(fileType, new File(fileUri.getEncodedPath()));
                }
                break;
        }
    }

    // Open file explorer window and get a file
    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = MediaType.getAllTypes();
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    // Callback methods
    public void updateListView(ArrayList<PrintJob> printJobs) {
        this.printJobs.clear();
        this.printJobs.addAll(printJobs);

        adapter.notifyDataSetChanged();
    }
}
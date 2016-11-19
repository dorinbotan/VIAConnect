package com.example.dorin.viaconnect;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.dorin.viaconnect.Utils.StringParser;
import com.example.dorin.viaconnect.WebClient.Print.MediaType;
import com.example.dorin.viaconnect.WebClient.Print.Print;
import com.example.dorin.viaconnect.WebClient.Print.PrintJob;
import com.example.dorin.viaconnect.WebClient.WebClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PrintActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;

    private ArrayList<PrintJob> printJobs;

    private SwipeMenuListView printJobList;
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

    // Setup the layout
    private void setup() {
        webClient = (WebClient) getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        names = new ArrayList<>();

        printJobList = (SwipeMenuListView) findViewById(R.id.printJobListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, names);
        printJobList.setAdapter(adapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
        });

        setupSwipeMenu();
    }

    // Setup the ListView
    private void setupSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent1)));
                openItem.setWidth(150);
                openItem.setTitle("Print");
                openItem.setTitleSize(15);
                openItem.setTitleColor(Color.BLACK);
                menu.addMenuItem(openItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent6)));
                deleteItem.setWidth(150);
                deleteItem.setTitle("Delete");
                deleteItem.setTitleSize(15);
                deleteItem.setTitleColor(Color.BLACK);
                menu.addMenuItem(deleteItem);
            }
        };

        printJobList.setMenuCreator(creator);

        printJobList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // Print
                        webClient.printJob(printJobs.get(position).jid, Print.PID_CAMPUS_HORSENS,
                                1, 1, 1, Print.DUPLEX_NONE, false);
                        break;
                    case 1:
                        // Delete
                        webClient.deletePrintJob(printJobs.get(position).jid);
                        break;
                }

                printJobs.remove(position);
                adapter.notifyDataSetChanged();

                try {
                    buttonClicked(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        printJobList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Refresh the ListView
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                try {
                    buttonClicked(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void buttonClicked(View view) throws IOException {
        webClient.getPrintJobs(this);
    }

    public void updateListView(ArrayList<PrintJob> printJobs) {
        this.printJobs = printJobs;

        names.clear();

        if (printJobs.size() != 0)
            for (PrintJob p : printJobs)
                names.add(p.name + "\nDate:  " + p.dateTime);

        adapter.notifyDataSetChanged();
    }
}
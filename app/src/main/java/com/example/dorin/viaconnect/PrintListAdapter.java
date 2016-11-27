package com.example.dorin.viaconnect;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dorin.viaconnect.webClient.print.PrintJob;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PrintListAdapter extends RecyclerView.Adapter<PrintListAdapter.MyViewHolder> {

    private List<PrintJob> printJobList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // TODO replace name with printjob_title/name
        public TextView name, time, status;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            status = (TextView) view.findViewById(R.id.status);
            time = (TextView) view.findViewById(R.id.time);
        }
    }

    public PrintListAdapter(List<PrintJob> printJobList) {
        this.printJobList = printJobList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.printjob_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PrintJob printJob = printJobList.get(position);
        holder.name.setText(printJob.name);
        holder.status.setText(printJob.status);
        holder.time.setText(getTime(printJob.dateTime));
    }

    @Override
    public int getItemCount() {
        return printJobList.size();
    }

    int i = 1;

    private String getTime(String dateTime) {
        Log.e("Date", dateTime);

        Date date = new Date(16, 1, 1);
        Log.e("DateTime", date.toString());

        Calendar c = Calendar.getInstance();
        Log.e("Current time", c.getTime().toString());

        i += 5;
        return i + " m" ;
    }
}

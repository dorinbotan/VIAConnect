package com.example.dorin.viaconnect;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dorin.viaconnect.webClient.print.PrintJob;

import java.util.List;

public class PrintListViewAdapter extends ArrayAdapter<PrintJob> {
    Context context;

    public PrintListViewAdapter(Context context, int resourceId, List<PrintJob> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        TextView name;
        TextView status;
        TextView time;
    }

    public View getView(int postition, View convertView, ViewGroup parent) {
        ViewHolder holder;
        PrintJob printJob = getItem(postition);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.printjob_layout, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.name.setText(printJob.name);
        holder.status.setText(printJob.status);
        holder.time.setText(getTime(printJob.dateTime));

        return convertView;
    }

    private String getTime(String dateTime) {
        return "15 m";
    }
}
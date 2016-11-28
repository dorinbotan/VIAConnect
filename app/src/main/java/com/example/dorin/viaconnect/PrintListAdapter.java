package com.example.dorin.viaconnect;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dorin.viaconnect.webClient.print.PrintJob;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private String getTime(String dateTime) {
        String now = new SimpleDateFormat("dd-MM-yy HH:mm").format(Calendar.getInstance().getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");

        try {
            Date date1 = simpleDateFormat.parse(dateTime);
            Date date2 = simpleDateFormat.parse(now);

            long different = date2.getTime() - date1.getTime();

            int minutesInMilli = 60000;
            int hoursInMilli = minutesInMilli * 60;
            int daysInMilli = hoursInMilli * 60;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;

            if(elapsedDays > 0)
                return elapsedDays + " d";
            else if(elapsedHours > 0)
                return elapsedHours + " h";
            else if(elapsedMinutes > 0)
                return elapsedMinutes + " m";
            else
                return "now";
        } catch (ParseException e) {
            Log.e("Error:", "Unable to parse dateTime");
            return "unknown";
        }
    }
}

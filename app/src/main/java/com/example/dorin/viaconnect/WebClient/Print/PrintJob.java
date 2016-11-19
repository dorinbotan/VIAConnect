package com.example.dorin.viaconnect.WebClient.Print;

public class PrintJob {
    public String name;
    public String dateTime;
    public String jid;

    public PrintJob(String name, String dateTime, String jid) {
        this.name = name;
        this.dateTime = dateTime;
        this.jid = jid;
    }
}
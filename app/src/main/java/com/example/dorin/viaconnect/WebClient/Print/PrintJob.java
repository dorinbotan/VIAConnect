package com.example.dorin.viaconnect.WebClient.Print;

public class PrintJob {
    public final static String[] states = { "Processing", "Awaiting release", "Printed" };

    public String name;
    public String dateTime;
    public String jid;
    public String pages;
    public String status;

    public PrintJob(String name, String dateTime, String jid, String pages, String status) {
        this.name = name;
        this.dateTime = dateTime;
        this.jid = jid;
        this.pages = pages;
        this.status = status;
    }
}
package com.example.dorin.viaconnect.webClient.print;

public class PrintJob {
    public final static String[] STATES = { "Processing", "Awaiting release", "Printed" };

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

    public String toString() {
        return name + " " + dateTime + " " + pages + " " + jid + " " + status;
    }
}

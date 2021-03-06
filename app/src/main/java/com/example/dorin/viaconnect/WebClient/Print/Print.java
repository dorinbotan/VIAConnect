package com.example.dorin.viaconnect.webClient.print;

import android.util.Log;

import com.example.dorin.viaconnect.webClient.okhttp.MultipartBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Print {
    private final int CODE_OK = 200;
    private final int CODE_FOUND = 302;
    public final static String DUPLEX_NONE = "1";
    public final static String DUPLEX_LONG_SIDE = "2";
    public final static String DUPLEX_SHORT_SIDE = "3";

    public final static String PID_CLOTHING_MECHANICS_IN_MEJLGADE = "Eqc=";
    public final static String PID_CFUIHE_HP_P3015 = "EaY=";
    public final static String PID_CAMPUS_AARHUS_C = "EKg=";
    public final static String PID_CAMPUS_AARHUS_C_PLOTTER_P819 = "EqY=";
    public final static String PID_CAMPUS_AARHUS_C_PLOTTER_P998 = "F68=";
    public final static String PID_CAMPUS_AARHUS_N = "Eas=";
    public final static String PID_CAMPUS_HERNING = "Ea0=";
    public final static String PID_CAMPUS_HERNING_PLOTTER_P758 = "Eqk=";
    public final static String PID_CAMPUS_HOLSTEBRO = "Eac=";
    public final static String PID_CAMPUS_HOLSTEBRO_PLOTTER_P848 = "Eqo=";
    public final static String PID_CAMPUS_HORSENS = "E68=";
    public final static String PID_CAMPUS_HORSENS_PLOTTER_P301 = "Eqw=";
    public final static String PID_CAMPUS_HORSENS_PLOTTER_P302 = "Eq0=";
    public final static String PID_CAMPUS_HORSENS_PLOTTER_P883_BLACK = "Eqs=";
    public final static String PID_CAMPUS_HORSENS_PLOTTER_P883_COLOR = "Eqg=";
    public final static String PID_CAMPUS_RANDERS = "E64=";
    public final static String PID_CAMPUS_SILKEBORG = "E6w=";
    public final static String PID_CAMPUS_VIBORG = "Eag=";
    public final static String PID_IKAST = "Eao=";
    public final static String PID_TEACHER_TRAINING_IN_NORRE_NISSUM = "EKk=";
    public final static String PID_TEACHER_TRAINING_IN_NORRE_SKIVE = "Fqk=";
    public final static String PID_MULTIPLATFORM_STORYTELLING_AND_PRODUCTION = "Ea8=";
    public final static String PID_TEACEHR_TRAINING_IN_GRENA = "Fqg=";
    public final static String PID_THIRSTED_BLACK = "EK8=";
    public final static String PID_THIRSTED_COLOR = "EKw=";
    public final static String PID_VIA_PRINT = "HA==";

    private OkHttpClient client;

    public Print(OkHttpClient client) {
        this.client = client;
    }

    // Check if user is logged in to print.via.dk (session not expired)
    public boolean isLoggedIn() throws IOException {
        // GET main page
        Request request = new Request.Builder()
                .url("https://print.via.dk/index.cfm")
                .build();

        Response response = client.newCall(request).execute();

        // If successful, user has access to it
        return response.code() == CODE_OK;
    }

    // Log in to print.via.dk
    public boolean logIn(String login, String password) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("LoginAction", "login")
                .add("LoginString", "SfuY7UX5jqNo8YzxUeo=")
                .add("Username", login)
                .add("Password", password)
                .build();

        // TODO log in to english version (?lang=en)
        Request request = new Request.Builder()
                .url("https://print.via.dk/login.cfm")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        // If successful, user is redirected to main page
        return response.code() == CODE_FOUND;
    }

    // Log out from print.via.dk
    public void logOut() throws IOException {
        Request request = new Request.Builder()
                .url("https://print.via.dk/login.cfm?message=Logout")
                .build();

        // Close connection with the server
        client.newCall(request).execute().close();
    }

    // Upload a file for printing
    public boolean sendJob(String mediaType, File file) throws IOException {
        // Place file in multipart/form-data message
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"type\""),
                        RequestBody.create(null, "file"))
                .addFormDataPart(
                        "FileToPrint", file.getName(), RequestBody.create(MediaType.parse(mediaType), file))
                .build();

        Request request = new Request.Builder()
                .url("https://print.via.dk/webprint.cfm")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        // If successful, user is redirected to main page
        return response.code() == CODE_FOUND;
    }

    public void printJob(String JID, String PID, int numberOfCopies, int pageFrom,
                         int pageTo, int duplex, boolean bw) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("JID", JID)
                .add("PID", PID)
                .add("NumberOfCopies", numberOfCopies + "")
                .add("PageFrom", pageFrom + "")
                .add("PageTo", pageTo + "")
                .add("Duplex", duplex + "")
                .add("PrintBW", bw + "")
                .add("method", "printjob")
                .build();

        Request request = new Request.Builder()
                .url("https://print.via.dk/afunctions.cfm")
                .post(formBody)
                .build();

        client.newCall(request).execute();
    }

    public void printJob(PrintJob printJob, String PID) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("JID", printJob.jid)
                .add("PID", PID)
                .add("NumberOfCopies", "1")
                .add("PageFrom", "1")
                .add("PageTo", printJob.pages)
                .add("Duplex", DUPLEX_NONE)
                .add("PrintBW", "True")
                .add("method", "printjob")
                .build();

        Log.e("JID", printJob.toString());

        Request request = new Request.Builder()
                .url("https://print.via.dk/afunctions.cfm")
                .post(formBody)
                .build();

        client.newCall(request).execute();
    }

    // Remove file from printing list
    public void deleteJob(String JID) throws IOException {
        Request request = new Request.Builder()
                .url("https://print.via.dk/index.cfm?action=deletejob&jid=" + JID)
                .build();

        Log.e("DELETE:", JID);

        client.newCall(request).execute();
    }

    // Get all print jobs available for printing
    public ArrayList<PrintJob> getPrintJobs() throws IOException {
        Request request = new Request.Builder()
                .url("https://print.via.dk/index.cfm?Message=JobAdded")
                .build();

        Response response = client.newCall(request).execute();

        return getPrintJobValues(response.body().string());
    }

    // TODO find the bug
    private ArrayList<PrintJob> getPrintJobValues(String builder) {
        ArrayList<PrintJob> toReturn = new ArrayList<>();

        Document html = Jsoup.parse(builder);
        Elements elements = html.getElementsByAttribute("onmouseover");

        for(int i = 0; i < elements.size(); i++) {
            String dateTime = elements.get(i).getElementsByAttributeValue("style", " width: 110px;").text();
            String name = elements.get(i).getElementsByIndexEquals(2).text();
            String pages = elements.get(i).getElementsByIndexEquals(3).text();
            String status = elements.get(i).getElementsByIndexEquals(4).text();
            String jid = "---";
            if(status.equalsIgnoreCase("Awaiting release"))
                jid = elements.get(i).getElementsByTag("a").get(2).toString().substring(44, 52);

            toReturn.add(new PrintJob(name, dateTime, jid, pages, status));
        }

        return toReturn;
    }
}
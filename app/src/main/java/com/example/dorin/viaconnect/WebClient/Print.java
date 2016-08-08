package com.example.dorin.viaconnect.WebClient;

import com.example.dorin.viaconnect.WebClient.okhttp.MultipartBody;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Print {
    public final int CODE_OK = 200;
    public final int CODE_FOUND = 302;
    public final int DUPLEX_NONE = 1;
    public final int DUPLEX_LONG_SIDE = 2;
    public final int DUPLEX_SHORT_SIDE = 3;
    // TODO add all printers
    public final String PID_CAMPUS_AARHUS_C = "EKg=";
    public final String PID_CAMPUS_AARHUS_N = "Eas=";
    public final String PID_CAMPUS_HERNING = "Ea0=";
    public final String PID_CAMPUS_HOLSTEBRO = "Eac=";
    public final String PID_CAMPUS_HORSENS = "E68=";
    public final String PID_CAMPUS_RANDERS = "E64=";
    public final String PID_CAMPUS_SILKEBORG = "E6w=";
    public final String PID_CAMPUS_VIBORG = "Eag=";

    private OkHttpClient client;

    public Print(OkHttpClient client) {
        this.client = client;
    }

    // Check if user is logged in to print.via.dk (check if cookies expired)
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
    public boolean sendJob(String fileName, String mediaType, File file) throws IOException {
        // Place file in multipart/form-data message
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"type\""),
                        RequestBody.create(null, "file"))
                .addFormDataPart(
                        "FileToPrint", fileName, RequestBody.create(MediaType.parse(mediaType), file))
                .build();

        Request request = new Request.Builder()
                .url("https://print.via.dk/webprint.cfm")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        // If successful, user is redirected to main page
        return response.code() == CODE_FOUND;
    }

    // TODO fix parameters
    // Send an uploaded file for printing
    public void printJob(String JID, String PID, String numberOfCopies, String pageFrom,
                         String pageTo, String duplex, boolean bw) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("JID", JID)
                .add("PID", PID)
                .add("NumberOfCopies", numberOfCopies)
                .add("PageFrom", pageFrom)
                .add("PageTo", pageTo)
                .add("Duplex", duplex)
                .add("PrintBW", bw + "")
                .add("method", "printjob")
                .build();

        Request request = new Request.Builder()
                .url("https://print.via.dk/afunctions.cfm")
                .post(formBody)
                .build();

        client.newCall(request).execute().close();
    }

    // Remove file from printing list
    public void deleteJob(String JID) throws IOException {
        Request request = new Request.Builder()
                .url("https://print.via.dk/index.cfm?action=deletejob&jid=" + JID)
                .build();

        client.newCall(request).execute();
    }

    // TODO finish (use regex here)
    private String[] getJID() throws IOException {
        Request request = new Request.Builder()
                .url("https://print.via.dk/index.cfm?Message=JobAdded")
                .build();

        Response response = client.newCall(request).execute();

        String builder = response.body().string();
        String start = "<input name=\"JID\" type=\"hidden\" value=\"";
        String end = "\">\n" +
                "<input name=\"PID\" type=\"hidden\" value=\"";
        String part = builder.substring(builder.indexOf(start) + start.length());
        String question = part.substring(0, part.indexOf(end));

        return new String[]{question};
    }
}
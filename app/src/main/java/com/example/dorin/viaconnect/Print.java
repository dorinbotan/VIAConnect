package com.example.dorin.viaconnect;

import android.os.Environment;

import com.example.dorin.viaconnect.okhttp.MultipartBody;

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

    public boolean isLoggedIn() throws IOException {
        Request requess = new Request.Builder()
                .url("https://print.via.dk/index.cfm")
                .build();

        Response response = client.newCall(requess).execute();

        return response.code() == CODE_OK;
    }

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

        return response.code() == CODE_FOUND;
    }

    public boolean send() throws IOException {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"type\""),
                        RequestBody.create(null, "file"))
                .addFormDataPart(
                        "FileToPrint", "img.png", RequestBody.create(MediaType.parse("image/png"),
                                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/img.png")))
                .build();

        Request request = new Request.Builder()
                .url("https://print.via.dk/webprint.cfm")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        return true;
    }

    public void print(String JID, String PID, String numberOfCopies, String pageFrom,
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

    // TODO use regex here
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

    public void logOut() throws IOException {
        Request request = new Request.Builder()
                .url("https://print.via.dk/login.cfm?message=Logout")
                .build();

        client.newCall(request).execute();
    }
}
package com.example.dorin.viaconnect.WebClient.Shop;

import com.example.dorin.viaconnect.StringParser;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Shop {
    private final int CODE_OK = 200;
    private final int CODE_FOUND = 302;

    private OkHttpClient client;

    public Shop(OkHttpClient client) {
        this.client = client;
    }

    public boolean logIn(String login, String password) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("imLoginName", login)
                .add("amDomainPassword", password)
                .add("dologin", "1")
                .add("action", "mainpage")
                .build();

        Request request = new Request.Builder()
                .url("https://shop.via.dk/")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        return response.code() == CODE_FOUND;
    }

    // Log out from shop.via.dk
    public void logOut() throws IOException {
        Request request = new Request.Builder()
                .url("https://shop.via.dk/?action=logout")
                .build();

        // Close connection with the server
        client.newCall(request).execute().close();
    }

    public String getBalance() throws IOException {
        Request request = new Request.Builder()
                .url("https://shop.via.dk/?embedded=1&action=payment&menuitemguid={AA34756C-9CBB-48CB-BA32-EE8D82C10F19}")
                .build();

        Response response = client.newCall(request).execute();

        String builder = response.body().string();
        String start = "<p>Konto saldo: ";
        String end = "</p>";
        String balance = StringParser.getStringsBetweenTags(builder, start, end).get(0);

        return balance;
    }

    public String getName() throws IOException {
        Request request = new Request.Builder()
                .url("https://shop.via.dk/?embedded=1&action=payment&menuitemguid={AA34756C-9CBB-48CB-BA32-EE8D82C10F19}")
                .build();

        Response response = client.newCall(request).execute();

        String builder = response.body().string();
        String start = "<p>Konto navn: Kontantkort (Primært) - ";
        String end = "</p>";
        String balance = StringParser.getStringsBetweenTags(builder, start, end).get(0).substring(19);

        return balance;
    }
}
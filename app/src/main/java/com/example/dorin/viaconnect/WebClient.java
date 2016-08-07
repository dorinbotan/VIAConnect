package com.example.dorin.viaconnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class WebClient {
    private OkHttpClient client;
    private Print print;

    public WebClient() {
        client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, HashMap<String, Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        HashMap<String, Cookie> cookieMap = cookieStore.get(url.host());
                        if (cookieMap == null)
                            cookieMap = new HashMap<>();

                        for (Cookie cookie : cookies)
                            cookieMap.put(cookie.name(), cookie);

                        cookieStore.put(url.host(), cookieMap);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        HashMap<String, Cookie> cookieMap = cookieStore.get(url.host());
                        List<Cookie> toReturn = new ArrayList<>();

                        if (cookieMap != null)
                            for (String key : cookieMap.keySet())
                                toReturn.add(cookieMap.get(key));

                        return toReturn;
                    }
                })
                .followRedirects(false)
                .followSslRedirects(false)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        print = new Print(client);
    }
}

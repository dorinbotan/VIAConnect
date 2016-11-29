package com.example.dorin.viaconnect.webClient;

import android.app.Application;

import com.example.dorin.viaconnect.LoginActivity;
import com.example.dorin.viaconnect.activities.PrintActivity;
import com.example.dorin.viaconnect.webClient.print.Print;
import com.example.dorin.viaconnect.webClient.print.PrintJob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class WebClient extends Application {
    private OkHttpClient client;
    public Print print;

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

    public boolean isLoggedIn() {
        try {
            return print.isLoggedIn();
        } catch (IOException e) {
            return false;
        }
    }

    public void logIn(final String login, final String password, final LoginActivity activity) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (print.logIn(login, password))
                        activity.startPrintActivity();
                    else
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.showError();
                            }
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void logOut() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    print.logOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Send print job and notify LoginActivity when done
    public void sendPrintJob(final String mediaType, final File file,
                             final LoginActivity activity) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (print.sendJob(mediaType, file))
                        activity.startPrintActivity();
                    else
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.showError();
                            }
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendPrintJob(final String mediaType, final File file) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    print.sendJob(mediaType, file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void deletePrintJob(String JID) {
        try {
            print.deleteJob(JID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printJob(final PrintJob printJob, final String PID) throws IOException {
        new Thread(new Runnable() {
            public void run() {
                try {
                    print.printJob(printJob, PID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void printJob(final String JID, final String PID, final int numberOfCopies, final int pageFrom,
                         final int pageTo, final int duplex, final boolean bw) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    print.printJob(JID, PID, numberOfCopies, pageFrom, pageTo, duplex, bw);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Get the list of files available for printing
    public void getPrintJobs(final PrintActivity activity) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final ArrayList<PrintJob> printJobs = print.getPrintJobs();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.updateListView(printJobs);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
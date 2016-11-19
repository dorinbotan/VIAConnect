package com.example.dorin.viaconnect.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class StringParser {
    public static ArrayList<String> getStringsBetweenTags(String builder, String start, String end) {
        ArrayList<String> toReturn = new ArrayList<>();

        int startIndex = builder.indexOf(start);
        int endIndex = builder.indexOf(end);
        while (startIndex != -1 && endIndex != -1) {
            builder = builder.substring(startIndex + start.length());
            String question = builder.substring(0, builder.indexOf(end));
            toReturn.add(question);

            startIndex = builder.indexOf(start);
            endIndex = builder.indexOf(end);
        }

        return toReturn;
    }

    @Nullable
    public static String getRealPathFromUri(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Log.i("URI", uri + "");
        String result = uri + "";
        // DocumentProvider
        //  if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if (isKitKat && (result.contains("media.documents"))) {
            String[] ary = result.split("/");
            int length = ary.length;
            String imgary = ary[length - 1];
            final String[] dat = imgary.split("%3A");
            final String type = dat[0];
            Uri contentUri = null;
            if ("image".equals(type))
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    dat[1]
            };
            return getDataColumn(context, contentUri, selection, selectionArgs);
        } else if ("content".equalsIgnoreCase(uri.getScheme()))
            return getDataColumn(context, uri, null, null);
        else if ("file".equalsIgnoreCase(uri.getScheme()))
            return uri.getPath();

        return null;
    }

    @Nullable
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}

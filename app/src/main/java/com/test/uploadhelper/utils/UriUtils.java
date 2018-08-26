package com.test.uploadhelper.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.test.uploadhelper.BuildConfig;

/**
 * @author wangyd
 * @date 2018/5/23
 * @description description
 */
public class UriUtils {

    private static final String TAG = UriUtils.class.getSimpleName();

    public static String getPathFromUri(Context context, Uri uri) {
        String path;
        //使用第三方应用打开
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "file: " + path);
            }
            return path;
        }
        //4.4以后
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            path = getPath(context, uri);

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "getPath: " + path);
            }
        }
        //4.4以下
        else {
            path = getDataColumn(context, uri, null, null);

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "getDataColumn: " + path);
            }
        }
        return path;
    }


    /**
     * 4.4以上获取文件绝对路径
     */
    private static String getPath(final Context context, final Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // DocumentProvider
            if (isExternalStorageDocument(uri)) {
                // ExternalStorageProvider
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);

            } else if (isMediaDocument(uri)) {
                // MediaProvider
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // MediaStore (and general)
            return getDataColumn(context, uri, null, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // File
            return uri.getPath();
        }
        return null;
    }

    /**
     * 4.4以下
     * Get the value of the data column for this Uri.
     * This is useful for MediaStore Uris, and other file-based ContentProviders.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String res = null;
        Cursor cursor = null;
        String[] proj = {MediaStore.Images.Media.DATA};

        try {
            cursor = context.getContentResolver().query(uri, proj, selection, selectionArgs, null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                res = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return res;
    }

    /**
     * Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}

package com.test.uploadhelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.test.uploadhelper.CusApplication;
import com.test.uploadhelper.common.service.SoapService;

/**
 * Created by wangyd on 2018/5/27.
 * 本地存储
 */
public class SharedPrefHelper {

    private static final String NAME = "SPH_NAME";
    private static SharedPreferences sp;

    private static SharedPrefHelper instance;

    private SharedPrefHelper() {
        sp = CusApplication.getApplication().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefHelper getInstance() {
        if (instance == null) {
            synchronized (SharedPrefHelper.class) {
                if (instance == null) {
                    instance = new SharedPrefHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 服务器地址
     */
    public String getServerUrl() {
        return sp.getString("server_url", SoapService.URL);
    }

    public void setServerUrl(String url) {
        sp.edit().putString("server_url", url).commit();
    }

    /**
     * 下载地址
     */
    public String getDownloadUrl() {
        return sp.getString("download_url", SoapService.DOWN_URL);
    }

    public void setDownloadUrl(String url) {
        sp.edit().putString("download_url", url).commit();
    }

    /*抄表人ID*/
    public String getID() {
        return sp.getString("user_id", "");
    }

    public void setID(String id) {
        sp.edit().putString("user_id", id).commit();
    }

    /*Meters*/
    public String getMetersPath() {
        return sp.getString("path_Meters", "");
    }

    public void setMetersPath(String path) {
        sp.edit().putString("path_Meters", path).commit();
    }

    /*NoteBookGroup*/
    public String getNoteBookGroupPath() {
        return sp.getString("path_NoteBookGroup", "");
    }

    public void setNoteBookGroupPath(String path) {
        sp.edit().putString("path_NoteBookGroup", path).commit();
    }

    /*Meters下载位置*/
    public String getMetersDownloadPath() {
        return sp.getString("path_d_Meters", "");
    }

    public void setMetersDownloadPath(String path) {
        sp.edit().putString("path_d_Meters", path).commit();
    }

    /*NoteBookGroup*/
    public String getNoteBookGroupDownloadPath() {
        return sp.getString("path_d_NoteBookGroup", "");
    }

    public void setNoteBookGroupDownloadPath(String path) {
        sp.edit().putString("path_d_NoteBookGroup", path).commit();
    }
}

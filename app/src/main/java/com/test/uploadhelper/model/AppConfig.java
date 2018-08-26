package com.test.uploadhelper.model;

import android.support.annotation.Keep;

/**
 * Created by wangyd on 2018/8/26.
 */
@Keep
public class AppConfig {

    private String userId;
    private String pathMeters;
    private String pathNotebookGroup;
    private String pathDownloadMeters;
    private String pathDownloadNotebookGroup;
    private String serverUrl;
    private String downloadUrl;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPathMeters() {
        return pathMeters;
    }

    public void setPathMeters(String pathMeters) {
        this.pathMeters = pathMeters;
    }

    public String getPathNotebookGroup() {
        return pathNotebookGroup;
    }

    public void setPathNotebookGroup(String pathNotebookGroup) {
        this.pathNotebookGroup = pathNotebookGroup;
    }

    public String getPathDownloadMeters() {
        return pathDownloadMeters;
    }

    public void setPathDownloadMeters(String pathDownloadMeters) {
        this.pathDownloadMeters = pathDownloadMeters;
    }

    public String getPathDownloadNotebookGroup() {
        return pathDownloadNotebookGroup;
    }

    public void setPathDownloadNotebookGroup(String pathDownloadNotebookGroup) {
        this.pathDownloadNotebookGroup = pathDownloadNotebookGroup;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}

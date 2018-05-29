package com.test.uploadhelper.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author wangyd
 * @date 2018/5/23
 * @description description
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final Integer RC_PERM_STORAGE = 200;
    public static final String[] PERMS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressBar();
    }

    public void onGetNecessaryPermissions() {

    }

    public void requestNecessaryPermission() {
        if (hasPermission()) {
            onGetNecessaryPermissions();
        } else {
            EasyPermissions.requestPermissions(this, "上传需要获取访问SD卡权限，请允许", RC_PERM_STORAGE, PERMS_STORAGE);
        }
    }

    public boolean hasPermission() {
        return EasyPermissions.hasPermissions(this, PERMS_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void showProgressBar() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("加载中...");
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        } else {
            mProgressDialog.setMessage("加载中...");
        }
        mProgressDialog.show();
    }

    public void hideProgressBar() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void setProgressBar(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        } else {
            mProgressDialog.setMessage(msg);
        }
        mProgressDialog.show();
    }
}

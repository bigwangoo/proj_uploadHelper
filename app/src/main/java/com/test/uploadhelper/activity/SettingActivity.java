package com.test.uploadhelper.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.test.uploadhelper.R;
import com.test.uploadhelper.common.service.SoapService;
import com.test.uploadhelper.utils.SharedPrefHelper;
import com.test.uploadhelper.utils.UriUtils;

/**
 * Created by wangyd on 2018/5/27.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SettingActivity.class.getSimpleName();

    public static final Integer RC_FILE_SELECT_METERS = 100;
    public static final Integer RC_FILE_SELECT_NOTE = 101;
    public static final Integer RC_FILE_SELECT_METERS_D = 102;
    public static final Integer RC_FILE_SELECT_NOTE_D = 103;

    private SharedPrefHelper spHelper;
    private TextView tvID, tvGroup, tvMeters, tvGroupDownload, tvMetersDownload, tvServer, tvDownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        spHelper = SharedPrefHelper.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvRight = findViewById(R.id.tvRight);
        tvTitle.setText("设置Config");
        tvRight.setText("重置");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingActivity.this).
                        setMessage("确定要重置配置吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetSetting();
                                setData();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create().show();
            }
        });

        tvID = findViewById(R.id.tvID);
        tvGroup = findViewById(R.id.tvGroup);
        tvMeters = findViewById(R.id.tvMeters);
        tvGroupDownload = findViewById(R.id.tvGroupDownload);
        tvMetersDownload = findViewById(R.id.tvMetersDownload);
        tvMetersDownload = findViewById(R.id.tvMetersDownload);
        tvServer = findViewById(R.id.tvServer);
        tvDownload = findViewById(R.id.tvDownload);

        findViewById(R.id.llID).setOnClickListener(this);
        findViewById(R.id.llGroup).setOnClickListener(this);
        findViewById(R.id.llMeters).setOnClickListener(this);
        findViewById(R.id.llGroupDownload).setOnClickListener(this);
        findViewById(R.id.llMetersDownload).setOnClickListener(this);
        findViewById(R.id.tvServer).setOnClickListener(this);
        findViewById(R.id.tvDownload).setOnClickListener(this);

        requestNecessaryPermission();
        setData();
    }

    private void resetSetting() {
        spHelper.setID("");
        spHelper.setNoteBookGroupPath("");
        spHelper.setMetersPath("");
        spHelper.setNoteBookGroupDownloadPath("");
        spHelper.setMetersDownloadPath("");
        spHelper.setServerUrl("");
        spHelper.setDownloadUrl("");
    }

    private void setData() {
        tvID.setText(spHelper.getID());
        tvGroup.setText(spHelper.getNoteBookGroupPath());
        tvMeters.setText(spHelper.getMetersPath());
        tvGroupDownload.setText(spHelper.getNoteBookGroupDownloadPath());
        tvMetersDownload.setText(spHelper.getMetersDownloadPath());
        tvServer.setText(spHelper.getServerUrl());
        tvDownload.setText(spHelper.getDownloadUrl());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llID:
                onIdClick();
                break;
            case R.id.llGroup:
                onFilePickClick(RC_FILE_SELECT_NOTE);
                break;
            case R.id.llMeters:
                onFilePickClick(RC_FILE_SELECT_METERS);
                break;
            case R.id.llGroupDownload:
                onFileDownloadPickClick(RC_FILE_SELECT_NOTE_D);
                break;
            case R.id.llMetersDownload:
                onFileDownloadPickClick(RC_FILE_SELECT_METERS_D);
                break;
            case R.id.tvServer:
                onServerUrlClick();
                break;
            case R.id.tvDownload:
                onDownloadUrlClick();
                break;
            default:
                break;
        }
    }

    private void onIdClick() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_input, null);
        final EditText edtInput = (EditText) view.findViewById(R.id.edtInput);

        new AlertDialog.Builder(this)
                .setTitle("设置抄表人ID")
                .setView(view)
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String trim = edtInput.getText().toString().trim();
                        spHelper.setID(trim);
                        tvID.setText(trim);
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    private void onServerUrlClick() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_input, null);
        final EditText edtInput = (EditText) view.findViewById(R.id.edtInput);
        edtInput.setText(spHelper.getServerUrl());

        new AlertDialog.Builder(this)
                .setTitle("设置接口服务地址")
                .setView(view)
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String trim = edtInput.getText().toString().trim();
                        spHelper.setServerUrl(trim);
                        tvServer.setText(trim);
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    private void onDownloadUrlClick() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_input, null);
        final EditText edtInput = (EditText) view.findViewById(R.id.edtInput);
        edtInput.setText(spHelper.getDownloadUrl());

        new AlertDialog.Builder(this)
                .setTitle("设置下载服务地址")
                .setView(view)
                .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String trim = edtInput.getText().toString().trim();
                        spHelper.setDownloadUrl(trim);
                        tvDownload.setText(trim);
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    private void onFilePickClick(int requestCode) {
        if (!hasPermission()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    private void onFileDownloadPickClick(int requestCode) {
        if (!hasPermission()) {
            return;
        }
        startActivityForResult(new Intent(this, FileDirPickActivity.class), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_FILE_SELECT_NOTE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }
            Log.e(TAG, "onActivityResult: " + uri.toString());
            String path = UriUtils.getPathFromUri(this, uri);
            spHelper.setNoteBookGroupPath(path);
            tvGroup.setText(path);

        } else if (requestCode == RC_FILE_SELECT_METERS && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }
            Log.e(TAG, "onActivityResult: " + uri.toString());
            String path = UriUtils.getPathFromUri(this, uri);
            spHelper.setMetersPath(path);
            tvMeters.setText(path);

        } else if (requestCode == RC_FILE_SELECT_NOTE_D && resultCode == Activity.RESULT_OK) {
            String path = data.getStringExtra(FileDirPickActivity.FILE_RESULT);
            spHelper.setNoteBookGroupDownloadPath(path);
            tvGroupDownload.setText(path);

        } else if (requestCode == RC_FILE_SELECT_METERS_D && resultCode == Activity.RESULT_OK) {
            String path = data.getStringExtra(FileDirPickActivity.FILE_RESULT);
            spHelper.setMetersDownloadPath(path);
            tvMetersDownload.setText(path);
        }
    }
}
